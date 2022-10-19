package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Fn1;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class CachePaged implements Paged {
    //#region listeners
    private final PageListener.PageListenerSupport listeners = new PageListener.PageListenerSupport();

    public void addListener(PageListener listener) {
        listeners.addListener(listener);
    }

    public void removeListener(PageListener listener) {
        listeners.removeListener(listener);
    }

    public boolean hasListener(PageListener listener) {
        return listeners.hasListener(listener);
    }

    public void fire(PageEvent event) {
        listeners.fire(event);
    }
    //#endregion

    public CachePaged(Paged cache, Paged persistent){
        if( cache==null )throw new IllegalArgumentException("cache==null");
        if( persistent==null )throw new IllegalArgumentException("persistent==null");

        if( cache.memoryInfo().pageSize()!=persistent.memoryInfo().pageSize() )
            throw new PageError("pageSize different between cache and persistent");

        this.cache = cache;
        this.persistent = persistent;
        this.cacheMap = new CacheMap();
        cacheMap.resize(cache.memoryInfo().pageCount(),req -> flushCachePage(req.cachedPageIndex, req.persistentPageIndex));
        cacheMap.addListener(listeners::fire);
    }

    private final CacheMap cacheMap;
    public CacheMap getCacheMap(){ return cacheMap; }

    public int getCacheSize(){
        return cacheMap.size();
    }

    private final Paged cache;
    private final Paged persistent;

    private void flushCachePage(int cachePage, int persistentPageIndex){
        persistent.writePage(persistentPageIndex,cache.readPage(cachePage));
        fire(new FlushCachePage(cachePage,persistentPageIndex));
    }

    @Override
    public UsedPagesInfo memoryInfo() {
        return persistent.memoryInfo();
    }

    //#region events
    public static class FlushCachePage implements PageEvent {
        public final int cachePage;
        public final int persistentPageIndex;

        public FlushCachePage(int cachePage, int persistentPageIndex) {
            this.cachePage = cachePage;
            this.persistentPageIndex = persistentPageIndex;
        }

        @Override
        public String toString() {
            return "FlushCachePage{" +
                "cachePage=" + cachePage +
                ", persistentPageIndex=" + persistentPageIndex +
                '}';
        }
    }

    public static class CacheMiss implements PageEvent {
        public final int persistentPageIndex;
        public final boolean read;

        public CacheMiss(int persistentPageIndex,boolean read) {
            this.persistentPageIndex = persistentPageIndex;
            this.read = read;
        }

        @Override
        public String toString() {
            return "CacheMiss{" +
                "persistentPageIndex=" + persistentPageIndex +
                ", read=" + read +
                '}';
        }
    }

    public static class CacheHit implements PageEvent {
        public final int persistentPageIndex;
        public final boolean read;

        public CacheHit(int persistentPageIndex,boolean read) {
            this.persistentPageIndex = persistentPageIndex;
            this.read = read;
        }

        @Override
        public String toString() {
            return "CacheHit{" +
                "persistentPageIndex=" + persistentPageIndex +
                ", read=" + read +
                '}';
        }
    }

    public static class PageLoaded implements PageEvent {
        public final int persistentPageIndex;
        public final byte[] data;

        public PageLoaded(int persistentPageIndex, byte[] data) {
            this.persistentPageIndex = persistentPageIndex;
            this.data = data;
        }

        @Override
        public String toString() {
            return "PageLoaded{" +
                "persistentPageIndex=" + persistentPageIndex+
                '}';
        }
    }

    public static class CacheWrote implements PageEvent {
        public final int persistentPageIndex;
        public final int cachePageIndex;
        public final byte[] data;

        public CacheWrote(int persistentPageIndex, int cachePageIndex, byte[] data) {
            this.persistentPageIndex = persistentPageIndex;
            this.cachePageIndex = cachePageIndex;
            this.data = data;
        }

        @Override
        public String toString() {
            return "CacheWrote{" +
                "persistentPageIndex=" + persistentPageIndex +
                ", cachePageIndex=" + cachePageIndex+
                '}';
        }
    }
    //#endregion

    @Override
    public synchronized byte[] readPage(int page) {
        synchronized (this) {
            synchronized (cacheMap) {
                synchronized (cache) {
                    synchronized (persistent) {
                        // 1 найти в кеше -> вернуть из кеша
                        var fromCache = cacheMap.findPersistentPageForRead(page, cp -> {
                            fire(new CacheHit(page, true));
                            cp.markReads();
                            return cache.readPage(cp.cachePageIndex);
                        });
                        if (fromCache.isPresent()) return fromCache.get();

                        fire(new CacheMiss(page, true));

                        // 2 загрузить в кеш -> вернуть из кеша
                        var result = new AtomicReference<byte[]>(null);
                        cacheMap.allocate(
                            cp -> {
                                cp.unTarget();

                                var data = persistent.readPage(page);
                                fire(new PageLoaded(page, data));

                                cache.writePage(cp.cachePageIndex, data);
                                fire(new CacheWrote(page, cp.cachePageIndex, data));

                                cp.setDataSize(data.length);
                                cp.assignTarget(page);
                                cp.markMapped();
                                result.set(data);
                            },
                            fr -> {
                                flushCachePage(fr.cachedPageIndex, fr.persistentPageIndex);
                                fr.cachePage.markFlushed();
                            }
                        );

                        var bytes = result.get();
                        if (bytes == null) throw new PageError("data not loaded, not allocated");

                        return bytes;
                    }
                }
            }
        }
    }

    @Override
    public synchronized void writePage(int page, byte[] data2write) {
        synchronized (this) {
            synchronized (cacheMap) {
                synchronized (cache) {
                    synchronized (persistent) {
                        // 1 найти в кеше -> записать в кеш
                        if (cacheMap.findPersistentPageForWrite(page, cachePage -> {
                            synchronized (cachePage) {
                                fire(new CacheHit(page, false));
                                var dataToWrite = data2write;
                                if (cachePage.getDataSize().isPresent()) {
                                    var max = cachePage.getDataSize().get();
                                    if (data2write.length > max) {
                                        dataToWrite = Arrays.copyOf(data2write, max);
                                    }
                                }
                                cache.writePage(cachePage.cachePageIndex, dataToWrite);
                                fire(new CacheWrote(page, cachePage.cachePageIndex, dataToWrite));

                                cachePage.markWrote();
                                return true;
                            }
                        }).orElse(false)) return;

                        fire(new CacheMiss(page, false));

                        // 2 загрузить в кеш -> записать в кеш
                        var allocated = new AtomicBoolean(false);
                        cacheMap.allocate(
                            cp -> {
                                synchronized (cp) {
                                    cp.unTarget();

                                    var data2persist = persistent.readPage(page);
                                    fire(new PageLoaded(page, data2persist));

                                    var data2write2 = data2write;
                                    if (data2write2.length < data2persist.length) {
                                        System.arraycopy(data2write, 0, data2persist, 0, data2write.length);
                                        data2write2 = data2persist;
                                    } else if (data2write2.length > data2persist.length) {
                                        throw new PageError("destination out of rage, persistent size = " + data2persist.length + " write size = " + data2write.length);
                                    }

                                    cache.writePage(cp.cachePageIndex, data2write2);
                                    fire(new CacheWrote(page, cp.cachePageIndex, data2write2));

                                    cp.setDataSize(data2write2.length);
                                    cp.assignTarget(page);
                                    cp.markMapped();

                                    cp.markWrote();
                                    allocated.set(true);
                                }
                            },
                            fr -> {
                                flushCachePage(fr.cachedPageIndex, fr.persistentPageIndex);
                                fr.cachePage.markFlushed();
                            }
                        );

                        if (!allocated.get()) {
                            throw new PageError("page not allocated in cache");
                        }
                    }
                }
            }
        }
    }

    @Override
    public synchronized void updatePage(int page, Fn1<byte[], byte[]> update) {
        if (update == null) throw new IllegalArgumentException("update==null");
        synchronized (this) {
            synchronized (cacheMap) {
                synchronized (cache) {
                    synchronized (persistent) {
                        if (cacheMap.findPersistentPageForWrite(page, cp -> {
                            synchronized (cp) {
                                fire(new CacheHit(page, false));

                                var cacheData = cache.readPage(cp.cachePageIndex);
                                cp.markReads();

                                var newData = update.apply(cacheData);
                                cache.writePage(cp.cachePageIndex, newData);
                                fire(new CacheWrote(page, cp.cachePageIndex, newData));
                                cp.markWrote();

                                return true;
                            }
                        }).orElse(false)) {
                            return;
                        }

                        fire(new CacheMiss(page, false));

                        var allocated = new AtomicBoolean(false);
                        cacheMap.allocate(
                            cp -> {
                                synchronized (cp) {
                                    cp.unTarget();

                                    var persistData = persistent.readPage(page);
                                    fire(new PageLoaded(page, persistData));

                                    var updatedData = update.apply(persistData);

                                    cache.writePage(cp.cachePageIndex, updatedData);
                                    fire(new CacheWrote(page, cp.cachePageIndex, updatedData));

                                    cp.setDataSize(updatedData.length);
                                    cp.assignTarget(page);
                                    cp.markMapped();

                                    cp.markWrote();
                                    allocated.set(true);
                                }
                            },
                            fr -> {
                                flushCachePage(fr.cachedPageIndex, fr.persistentPageIndex);
                                fr.cachePage.markFlushed();
                            }
                        );
                        if (!allocated.get()) {
                            throw new PageError("page not allocated in cache");
                        }
                    }
                }
            }
        }
    }

    public synchronized void flush(){
        synchronized (cacheMap) {
            cacheMap.flush(ev -> {
                flushCachePage(ev.cachedPageIndex, ev.persistentPageIndex);
                ev.cachePage.markFlushed();
            });
        }
    }
}