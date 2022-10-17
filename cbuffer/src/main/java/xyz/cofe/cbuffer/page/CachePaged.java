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

    private void flushCachePage(int cachePage, int persistentPageIndex){
        persistent.writePage(persistentPageIndex,cache.readPage(cachePage));
        fire(new FlushCachePage(cachePage,persistentPageIndex));
    }

    @Override
    public UsedPagesInfo memoryInfo() {
        return persistent.memoryInfo();
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

    @Override
    public byte[] readPage(int page) {
        return readPersistentLock(page,()->{
            // 1 найти в кеше -> вернуть из кеша
            var fromCache = cacheMap.findPersistentPageForRead(page,cp->{
                fire(new CacheHit(page,true));
                cp.markReads();
                return persistent.readPage(cp.getTarget().get());
            });
            if( fromCache.isPresent() )return fromCache.get();

            fire(new CacheMiss(page,true));

            // 2 загрузить в кеш -> вернуть из кеша
            var result = new AtomicReference<byte[]>(null);
            cacheMap.allocate(
                cp -> {
                    cp.writeLock(()->{
                        cp.unTarget();

                        var data = persistent.readPage(page);
                        fire(new PageLoaded(page,data));

                        cache.writePage(cp.cachePageIndex, data);
                        fire(new CacheWrote(page,cp.cachePageIndex, data));

                        cp.setDataSize(data.length);
                        cp.assignTarget(page);
                        cp.markMapped();
                        result.set(data);
                    });
                },
                fr -> {
                    fr.cachePage.writeLock(()->{
                        flushCachePage(fr.cachedPageIndex, fr.persistentPageIndex);
                        fr.cachePage.markFlushed();
                    });
                }
            );

            var bytes = result.get();
            if( bytes==null )throw new PageError("data not loaded, not allocated");

            return bytes;
        });
    }

    @Override
    public void writePage(int page, byte[] data2write) {
        System.out.println("writePage page="+page+" writePersistentLock");
        writePersistentLock(page,()->{
            // 1 найти в кеше -> записать в кеш
            if(cacheMap.findPersistentPageForWrite(page,cachePage -> {
                synchronized (persistent) {
                    synchronized (cache) {
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
                }
            }).orElse(false))return;

            fire(new CacheMiss(page,false));

            // 2 загрузить в кеш -> записать в кеш
            var allocated = new AtomicBoolean(false);
            cacheMap.allocate(
                cp -> {
                    cp.writeLock(()->{
                        synchronized (persistent) {
                            synchronized (cache) {
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
                        }
                    });
                },
                fr -> {
                    fr.cachePage.writeLock(()->{
                        flushCachePage(fr.cachedPageIndex, fr.persistentPageIndex);
                        fr.cachePage.markFlushed();
                    });
                }
            );

            if( !allocated.get() ){
                throw new PageError("page not allocated in cache");
            }
        });
    }

    @Override
    public void updatePage(int page, Fn1<byte[], byte[]> update) {
        if (update == null) throw new IllegalArgumentException("update==null");
        writePersistentLock(page,()-> {
            if (cacheMap.findPersistentPageForWrite(page, cp -> {
                synchronized (cp) {
                    return cp.writeLock(() -> {
                        //synchronized (cp) {
                            fire(new CacheHit(page, false));

                            var cacheData = cache.readPage(cp.cachePageIndex);
                            cp.markReads();

                            var newData = update.apply(cacheData);
                            cache.writePage(cp.cachePageIndex, newData);
                            fire(new CacheWrote(page, cp.cachePageIndex, newData));
                            cp.markWrote();

                            return true;
                        //}
                    });
                }
            }).orElse(false)) {
                return;
            }

            fire(new CacheMiss(page, false));

            var allocated = new AtomicBoolean(false);
            cacheMap.allocate(
                cp -> {
                    synchronized (cp) {
                        cp.writeLock(() -> {
                            //synchronized (cp) {
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
                            //};
                        });
                    }
                },
                fr -> {
                    fr.cachePage.writeLock(()->{
                        flushCachePage(fr.cachedPageIndex, fr.persistentPageIndex);
                        fr.cachePage.markFlushed();
                    });
                }
            );
            if (!allocated.get()) {
                throw new PageError("page not allocated in cache");
            }
        });
    }

    public void flush(){
        cacheMap.flush(ev -> {
            flushCachePage(ev.cachedPageIndex, ev.persistentPageIndex);
            ev.cachePage.markFlushed();
        });
    }

    //#region persistentPageLocks
    public synchronized  <R> R readPersistentLock(int page, Supplier<R> code) {
        return readPersistentLock1(page,code);
    }
    public synchronized void writePersistentLock(int page, Runnable code) {
        writePersistentLock1(page,code);
    }

    public <R> R readPersistentLock2(int page, Supplier<R> code) {
        synchronized (this){
            return code.get();
        }
    }
    public void writePersistentLock2(int page, Runnable code) {
        synchronized (this){
            code.run();
        }
    }


    private final Object[] plocks = new Object[] {
        new Object(), new Object(), new Object(), new Object(),
        new Object(), new Object(), new Object(), new Object(),
        new Object(), new Object(), new Object(), new Object(),
        new Object(), new Object(), new Object(), new Object(),
    };

    public <R> R readPersistentLock1(int page, Supplier<R> code) {
        final var obj = plocks[page % plocks.length];
        synchronized (obj){
            //System.out.println("lock "+page+" "+obj+" "+Thread.currentThread().getId());
            return code.get();//
        }
    }
    public void writePersistentLock1(int page, Runnable code) {
        final var obj = plocks[page % plocks.length];
        synchronized (obj){
            //System.out.println("lock "+page+" "+obj+" "+Thread.currentThread().getId());
            code.run();
        }
    }

    private final Object syn1 = new Object();
    private final Object syn2 = new Object();

    public <R> R readPersistentLock0(int page, Supplier<R> code) {
        var syn = page%2 == 0 ? syn1 : syn2;
        if( page%2==0 ){
            synchronized (syn) {
                return code.get();
            }
        }else{
            synchronized (syn) {
                return code.get();
            }
        }
    }
    public void writePersistentLock0(int page, Runnable code) {
        var syn = page%2 == 0 ? syn1 : syn2;
        if( page%2==0 ){
            synchronized (syn) {
                code.run();
            }
        }else{
            synchronized (syn) {
                code.run();
            }
        }
    }
    //#endregion
}
