package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Tuple2;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class CCachePagedData extends CachePagedData {
    protected static class State implements CachePagedState {
        private DirtyPagedData cachePages;
        private ResizablePages persistentPages;
        private int[] cache2prst;
        private Map<Integer, Integer> prst2cache;
        private volatile boolean closed = false;

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
        public int[] cache2prst() {
            return cache2prst;
        }

        @Override
        public void cache2prst(int[] map) {
            cache2prst = map;
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

        @Override
        public synchronized void close() {
            if( !closed ){
                if( persistentPages instanceof AutoCloseable ){
                    try {
                        ((AutoCloseable) persistentPages).close();
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
                persistentPages = null;

                if( cachePages instanceof AutoCloseable ){
                    try {
                        ((AutoCloseable) cachePages).close();
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
                cachePages = null;

                cache2prst = null;
                prst2cache = null;
            }
            closed = true;
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

    public void close(){
        synchronized (closeSync) {
            state.close();
        }
    }

    private final ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private <R> R lock(ReadWriteLock lock, boolean write, Supplier<R> code){
        if( write ){
            try {
                lock.writeLock().lock();
                return code.get();
            } finally {
                lock.writeLock().unlock();
            }
        }else{
            try {
                lock.readLock().lock();
                return code.get();
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    private <R> R cacheLock( int cache_page, boolean write, Supplier<R> code ){
        return code.get();
    }
    private <R> R persistLock( int persist_page, boolean write, Supplier<R> code ){
        return code.get();
    }

    @Override
    public UsedPagesInfo memoryInfo() {
        return lock( cacheLock, false, super::memoryInfo);
    }

    @Override
    protected int persist2cache(int persistPage) {
        return lock( cacheLock, false, ()->super.persist2cache(persistPage) );
    }

    @Override
    protected int cache2persist(int cachePage) {
        return lock( cacheLock, false, ()->super.cache2persist(cachePage) );
    }

    @Override
    protected boolean dirty(int cachePage) {
        return lock( cacheLock, false, ()->super.dirty(cachePage) );
    }

    @Override
    protected boolean clean(int cachePage) {
        return lock( cacheLock, false, ()->super.clean(cachePage) );
    }

    @Override
    protected int flush(int cachePage) {
        return lock( cacheLock, false, ()->super.flush(cachePage) );
    }

    @Override
    public void flush() {
        lock( cacheLock, false, ()->{
            super.flush();
            return null;
        });
    }

    @Override
    protected int unmap(int cachePage) {
        return lock( cacheLock, false, ()->super.unmap(cachePage) );
    }

    @Override
    protected Tuple2<List<Integer>, List<Integer>> cleanDirtyPages() {
        return lock( cacheLock, false, super::cleanDirtyPages);
    }

    @Override
    protected int unmapCandidate(List<Integer> pages, boolean clean) {
        return lock( cacheLock, false, ()->super.unmapCandidate(pages, clean) );
    }

    @Override
    protected int allocCachePage() {
        return lock( cacheLock, false, super::allocCachePage);
    }

    @Override
    protected byte[] map(int cachePage, int persistPage) {
        return lock( cacheLock, false, ()->super.map(cachePage, persistPage) );
    }

    @Override
    public byte[] readPage(int page) {
        return lock( cacheLock, false, ()->super.readPage(page) );
    }

    @Override
    public void writePage(int page, byte[] data) {
        lock( cacheLock, false, ()->{
            super.writePage(page, data);
            return null;
        });
    }

    //region изменение размера кеша resizeCachePages, resizePages, extendPages, reducePages
    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> extendPages(int pages) {
        return lock( cacheLock, true, ()->super.extendPages(pages) );
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> reducePages(int pages) {
        return lock( cacheLock, true, ()->super.reducePages(pages) );
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> resizeCachePages(int pages) {
        return lock( cacheLock, true, ()->super.resizeCachePages(pages) );
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> resizePages(int pages) {
        return lock( cacheLock, true, ()->super.resizePages(pages) );
    }
    //endregion
}
