package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CCachePagedData extends BaseCachePagedData<CCachePagedData.State> {
    public static class State implements CachePagedState {
        protected DirtyPagedData cachePages;
        protected ResizablePages persistentPages;
        protected int[] cache2prst;
        protected Map<Integer, Integer> prst2cache;
        protected volatile boolean closed = false;

        protected volatile ReadWriteLock[] cachePageLocks = new ReadWriteLock[0];

        public Optional<ReadWriteLock> cachePageRWLock(int cache_page) {
            if (cache_page < 0) throw new IllegalArgumentException("cache_page<0");

            ReadWriteLock[] cp = cachePageLocks;
            if (cp == null) return Optional.empty();
            if (cache_page >= cp.length) {
                throw new IllegalArgumentException("cache_page>=" + cp.length + "; out of range");
            }
            ReadWriteLock rwLock = cp[cache_page];
            if (rwLock != null) return Optional.of(rwLock);
            return Optional.empty();
        }

        // В теории может потребоваться блокировка write
        // Зависит от cacheLock
        public <R> R cachePageReadLock( int cache_page, Supplier<R> code ){
            if( code==null )throw new IllegalArgumentException( "code==null" );
            Optional<ReadWriteLock> rwOpt = cachePageRWLock(cache_page);
            if( rwOpt.isPresent() ){
                ReadWriteLock rwLock = rwOpt.get();
                try {
                    rwLock.readLock().lock();
                    return code.get();
                } finally {
                    rwLock.readLock().unlock();
                }
            }else{
                return code.get();
            }
        }

        public <R> R cachePageWriteLock( int cache_page, Supplier<R> code ){
            if( code==null )throw new IllegalArgumentException( "code==null" );
            Optional<ReadWriteLock> rwOpt = cachePageRWLock(cache_page);
            if( rwOpt.isPresent() ){
                ReadWriteLock rwLock = rwOpt.get();
                try {
                    rwLock.writeLock().lock();
                    return code.get();
                } finally {
                    rwLock.writeLock().unlock();
                }
            }else{
                return code.get();
            }
        }

        @Override
        public DirtyPagedData cachePages() {
            return cachePages;
        }

        @Override
        public void cachePages(DirtyPagedData pages) {
            cachePages = pages;
        }

        @Override
        public ResizablePages persistentPages() {
            return persistentPages;
        }

        @Override
        public void persistentPages(ResizablePages pages) {
            persistentPages = pages;
        }

        @Override
        public <R> R cache2prst_read(Function<IntArrayReadOnly, R> code) {
            if (code == null) throw new IllegalArgumentException("code==null");
            return code.apply(IntArrayReadOnly.of(cache2prst));
        }

        @Override
        public void cache2prst_write(Consumer<IntArrayMutable> code) {
            if (code == null) throw new IllegalArgumentException("code==null");
            code.accept(IntArrayMutable.of(cache2prst));
        }

        @Override
        public void cache2prst_replace(Function<IntArrayReadOnly, int[]> code) {
            if (code == null) throw new IllegalArgumentException("code==null");

            ReadWriteLock[] cachePageLocks = this.cachePageLocks;
            List<Lock> writeLocks = new ArrayList<>();
            if (cachePageLocks != null) {
                for (ReadWriteLock rwLock : cachePageLocks) {
                    if (rwLock == null) continue;
                    Lock lock = rwLock.writeLock();
                    lock.lock();
                    writeLocks.add(lock);
                }
            }

            try {
                int[] res = code.apply(IntArrayReadOnly.of(cache2prst == null ? new int[0] : cache2prst));
                if (res == null) throw new IllegalStateException("cache2prst_replace(code), code - return null");
                cache2prst = res;
            } finally {
                for (Lock lock : writeLocks) {
                    lock.unlock();
                }
            }
        }

        @Override
        public Map<Integer, Integer> prst2cache() {
            return prst2cache;
        }

        @Override
        public void prst2cache(Map<Integer, Integer> map) {
            prst2cache = map;
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        private volatile int closeCall = 0;

        @Override
        public synchronized void close() {
            try {
                closeCall++;
                int closeCall_v = closeCall;
                if (closeCall_v > 1) throw new IllegalStateException("illegal cycle call close()");

                if (!closed) {
                    ReadWriteLock[] cachePageLocks = this.cachePageLocks;
                    List<Lock> writeLocks = new ArrayList<>();
                    if (cachePageLocks != null) {
                        for (ReadWriteLock rwLock : cachePageLocks) {
                            if (rwLock == null) continue;
                            Lock lock = rwLock.writeLock();
                            lock.lock();
                            writeLocks.add(lock);
                        }
                    }
                    /////////////
                    try {
                        if (persistentPages instanceof AutoCloseable) {
                            try {
                                ((AutoCloseable) persistentPages).close();
                            } catch (Exception e) {
                                throw new Error(e);
                            }
                        }
                        persistentPages = null;

                        if (cachePages instanceof AutoCloseable) {
                            try {
                                ((AutoCloseable) cachePages).close();
                            } catch (Exception e) {
                                throw new Error(e);
                            }
                        }
                        cachePages = null;

                        cache2prst = null;
                        prst2cache = null;
                    } finally {
                        for (Lock lock : writeLocks) {
                            lock.unlock();
                        }
                    }
                }
                closed = true;
            } finally {
                closeCall--;
            }
        }

        public final ReadWriteLock cacheLock = new ReentrantReadWriteLock();

        public <R> R globalCacheLock(boolean write, Supplier<R> code) {
            if (write) {
                try {
                    cacheLock.writeLock().lock();
                    return code.get();
                } finally {
                    cacheLock.writeLock().unlock();
                }
            } else {
                try {
                    cacheLock.readLock().lock();
                    return code.get();
                } finally {
                    cacheLock.readLock().unlock();
                }
            }
        }
    }

    protected CCachePagedData(CachePagedState state) {
        super(new State());
    }

    public CCachePagedData(DirtyPagedData cachePages, ResizablePages persistentPages) {
        super(cachePages, persistentPages, new State());
    }

    private final Object closeSync = new Object();

    @Override
    protected boolean isClosed() {
        synchronized (closeSync) {
            return super.isClosed();
        }
    }

    public void close() {
        synchronized (closeSync) {
            state.close();
        }
    }

    @Override
    public UsedPagesInfo memoryInfo() {
        return state.globalCacheLock(false, super::memoryInfo);
    }

    @Override
    protected int persist2cache(int persistPage) {
        return state.globalCacheLock(false, () -> super.persist2cache(persistPage));
    }

    @Override
    protected int cache2persist(int cachePage) {
        return state.globalCacheLock(false, () -> super.cache2persist(cachePage));
    }

    @Override
    protected boolean dirty(int cachePage) {
        return state.globalCacheLock(false,
            ()->state.cachePageReadLock(
                cachePage,()->super.dirty(cachePage)));
    }

    @Override
    protected boolean clean(int cachePage) {
        return state.globalCacheLock(false,
            ()->state.cachePageReadLock(
                cachePage, ()->super.clean(cachePage)));
    }

    @Override
    protected int flush(int cachePage) {
        return state.globalCacheLock(false,
            ()->state.cachePageReadLock(
                cachePage, ()->super.flush(cachePage)));
    }

    @Override
    public void flush() {
        state.globalCacheLock(false, () -> {
            List<Lock> locked = state.cache2prst_read( arr -> {
                List<Lock> locks = new ArrayList<>();
                for( int cache_page=0; cache_page<arr.length(); cache_page++ ){
                    Optional<ReadWriteLock> rwLock = state.cachePageRWLock(cache_page);
                    if( rwLock.isPresent() ){
                        Lock lock = rwLock.get().readLock();
                        lock.lock();
                        locks.add(lock);
                    }
                }
                return locks;
            });

            super.flush();

            for( Lock lock : locked ){
                lock.unlock();
            }
            return null;
        });
    }

    @Override
    protected int unmap(int cachePage) {
        if( cachePage<0 )throw new IllegalArgumentException( "cachePage<0" );
        return state.globalCacheLock(false,
            ()->state.cachePageWriteLock(cachePage,
                ()->super.unmap(cachePage)));
    }

    @Override
    protected Tuple2<List<Integer>, List<Integer>> cleanDirtyPages() {
        return state.globalCacheLock(false, super::cleanDirtyPages);
    }

    @Override
    protected int unmapCandidate(List<Integer> pages, boolean clean) {
        return state.globalCacheLock(false, () -> super.unmapCandidate(pages, clean));
    }

    @Override
    protected int allocCachePage() {
        return state.globalCacheLock(false, super::allocCachePage);
    }

    @Override
    protected byte[] map(int cachePage, int persistPage) {
        if( cachePage<0 )throw new IllegalArgumentException( "cachePage<0" );
        state.cachePageRWLock(cachePage);
        return state.globalCacheLock(false, () -> super.map(cachePage, persistPage));
    }

    @Override
    public byte[] readPage(int page) {
        return state.globalCacheLock(false, () -> super.readPage(page));
    }

    @Override
    public void writePage(int page, byte[] data) {
        state.globalCacheLock(false, () -> {
            super.writePage(page, data);
            return null;
        });
    }

    //region изменение размера кеша resizeCachePages, resizePages, extendPages, reducePages - глобальное exclusive cacheLock
    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> extendPages(int pages) {
        return state.globalCacheLock(true, () -> super.extendPages(pages));
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> reducePages(int pages) {
        return state.globalCacheLock(true, () -> super.reducePages(pages));
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> resizeCachePages(int pages) {
        return state.globalCacheLock(true, () -> super.resizeCachePages(pages));
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> resizePages(int pages) {
        return state.globalCacheLock(true, () -> super.resizePages(pages));
    }
    //endregion
}
