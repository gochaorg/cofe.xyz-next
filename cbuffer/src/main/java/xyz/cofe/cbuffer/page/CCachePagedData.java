package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Tuple2;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Кеш страниц, с разделением на быструю и медленную память
 * с поддержкой многопоточности
 */
public class CCachePagedData extends CachePagedDataBase<CCachePagedData.State, UsedPagesInfo, DirtyPagedDataSafe>
implements PageLock
{
    public static class State implements CachePagedState<UsedPagesInfo, DirtyPagedDataSafe> {
        protected DirtyPagedDataSafe cachePages;
        protected ResizablePages<UsedPagesInfo> persistentPages;
        protected volatile int[] cache2prst;
        protected Map<Integer, Integer> prst2cache;
        protected volatile boolean closed = false;
        protected volatile ReadWriteLock[] cachePageLocks = new ReadWriteLock[0];
        protected final AtomicLong statCacheHit = new AtomicLong(0);
        protected final AtomicLong statCacheMiss = new AtomicLong(0);

        @Override
        public void statCacheHitMiss(boolean hit) {
            AtomicLong a = hit ? statCacheHit : statCacheMiss;
            a.incrementAndGet();
        }

        @Override
        public Tuple2<Long, Long> statCacheHitMiss() {
            return Tuple2.of(statCacheHit.get(), statCacheMiss.get());
        }

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
        public DirtyPagedDataSafe cachePages() {
            return cachePages;
        }

        @Override
        public void cachePages(DirtyPagedDataSafe pages) {
            cachePages = pages;
        }

        @Override
        public ResizablePages<UsedPagesInfo> persistentPages() {
            return persistentPages;
        }

        @Override
        public void persistentPages(ResizablePages<UsedPagesInfo> pages) {
            persistentPages = pages;
        }

        @Override
        public <R> R cache2prst_read(Function<IntArrayReadOnly, R> code) {
            if (code == null) throw new IllegalArgumentException("code==null");
            return code.apply(IntArrayReadOnly.of(cache2prst));
        }

        // глобальная мутация
        @Override
        public void cache2prst_write(Consumer<IntArrayMutable> code) {
            if (code == null) throw new IllegalArgumentException("code==null");
            code.accept(IntArrayMutable.of(cache2prst));
        }

        // глобальная мутация
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

                ReadWriteLock[] locks = new ReadWriteLock[res.length];
                for( int i=0; i<this.cachePageLocks.length; i++ ){
                    locks[i] = new ReentrantReadWriteLock();
                }
                this.cachePageLocks = locks;
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

    protected CCachePagedData(State state) {
        super(state);
    }

    public CCachePagedData(DirtyPagedDataSafe cachePages, ResizablePages<UsedPagesInfo> persistentPages) {
        super(cachePages, persistentPages, new State());
    }

    protected final Object closeSync = new Object();

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

    //region persist2cache()
    @Override
    protected int persist2cache(int persistPage) {
        return state.globalCacheLock(false, () -> super.persist2cache(persistPage));
    }

    protected int persist2cache_mut(int persistPage) {
        return state.globalCacheLock(true, () -> super.persist2cache(persistPage));
    }
    //endregion
    //region cache2persist()
    @Override
    protected int cache2persist(int cachePage) {
        return state.globalCacheLock(false, () -> super.cache2persist(cachePage));
    }

    @Override
    protected int cache2persist_mut(int cachePage) {
        return state.globalCacheLock(true, () -> super.cache2persist_mut(cachePage));
    }
    //endregion
    //region dirty()
    @Override
    protected boolean dirty(int cachePage) {
        return state.globalCacheLock(false,
            ()->state.cachePageReadLock(
                cachePage,()->super.dirty(cachePage)));
    }

    @Override
    protected boolean dirty_mut(int cachePage) {
        return state.globalCacheLock(true,
            ()->state.cachePageWriteLock(
                cachePage,()->super.dirty(cachePage)));
    }
    //endregion
    //region flush()
    @Override
    protected int flush(int cachePage) {
        return state.globalCacheLock(false,
            ()->state.cachePageReadLock(
                cachePage, ()->super.flush(cachePage)));
    }

    @Override
    protected int flush_mut(int cachePage) {
        return state.globalCacheLock(true,
            ()->state.cachePageWriteLock(
                cachePage, ()->super.flush_mut(cachePage)));
    }

    private static class LockAll {
        private final List<Lock> locked;
        public LockAll(List<Lock> locked){
            this.locked = locked;
        }
        public void release(){
            for( Lock lock : locked ){
                lock.unlock();
            }
            locked.clear();
        }
    }
    private LockAll lockAll(){
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

        return new LockAll(locked);
    }

    @Override
    public void flush() {
        state.globalCacheLock(false, () -> {
            LockAll lockAll = lockAll();
            super.flush();
            lockAll.release();
            return null;
        });
    }
    //endregion

    @Override
    protected int unmap(int cachePage) {
        if( cachePage<0 )throw new IllegalArgumentException( "cachePage<0" );
        return state.globalCacheLock(true,
            ()->state.cachePageWriteLock(cachePage,
                ()->super.unmap(cachePage)));
    }

    @Override
    protected Tuple2<List<Integer>, List<Integer>> cleanDirtyPages() {
        return state.globalCacheLock(true, super::cleanDirtyPages);
    }

    @Override
    protected int unmapCandidate(List<Integer> pages, boolean clean) {
        return state.globalCacheLock(true, () -> super.unmapCandidate(pages, clean));
    }

    @Override
    protected int allocCachePage() {
        return state.globalCacheLock(true, ()->{
            //noinspection UnnecessaryLocalVariable
            int cache_page = super.allocCachePage();
            return cache_page;
        });
    }

    @Override
    protected byte[] map(int cachePage, int persistPage) {
        if( cachePage<0 )throw new IllegalArgumentException( "cachePage<0" );
        return state.globalCacheLock(true, () -> super.map(cachePage, persistPage));
    }

    //region readPage()
    @Override
    public byte[] readPage(int page) {
        if( isClosed() )throw new IllegalStateException("closed");
        if( page<0 )throw new IllegalArgumentException( "page<0" );

        boolean readLock_unlock = true;
        try {
            // глобальная блокировка.
            state.cacheLock.readLock().lock();

            int cidx = persist2cache(page);
            if( cidx>=0 ){
                return readPage_mapped(cidx, page);
            }else{
                // Глобальная блокировка.
                //   Повышаем до exclusive.
                state.cacheLock.readLock().unlock();
                readLock_unlock = false;

                try {
                    state.cacheLock.writeLock().lock();
                    // Тут имеем global exclusive -
                    // возможность перераспределить новый участок памяти
                    // повторная проверка, возможно уже страница была спроецирована
                    cidx = persist2cache_mut(page);
                    if( cidx>=0 ){
                        return readPage_mapped(cidx, page);
                    }

                    return readPage_alloc(page);
                } finally {
                    state.cacheLock.writeLock().unlock();
                }
            }
        } finally {
            if( readLock_unlock )state.cacheLock.readLock().unlock();
        }
    }

    @Override
    protected byte[] readPage_mapped(int cidx, int page) {
        return state.cachePageReadLock( cidx, ()->super.readPage_mapped(cidx, page) );
    }

    @Override
    protected byte[] readPage_alloc(int page) {
        return super.readPage_alloc(page);
    }
    //endregion
    //region writePage()
    @Override
    public void writePage(int page, byte[] data) {
        if( isClosed() )throw new IllegalStateException("closed");
        if( page<0 )throw new IllegalArgumentException( "page<0" );

        boolean readLock_unlock = true;
        try {
            // глобальная блокировка.
            state.cacheLock.readLock().lock();

            int page_size = state.cachePages().memoryInfo().pageSize();
            if( data.length>page_size )throw new IllegalArgumentException("data.length(="+data.length+") > page_size(="+page_size+")");

            int cidx = persist2cache(page);
            if( cidx>=0 ){
                writePage_mapped(cidx, page, data);
            }else{
                // Глобальная блокировка.
                //   Повышаем до exclusive.
                state.cacheLock.readLock().unlock();
                readLock_unlock = false;

                try {
                    state.cacheLock.writeLock().lock();
                    // Тут имеем global exclusive -
                    // возможность перераспределить новый участок памяти
                    // повторная проверка, возможно уже страница была спроецирована
                    cidx = persist2cache_mut(page);
                    if( cidx>=0 ){
                        writePage_mapped(cidx, page, data);
                    }else {
                        writePage_alloc(page, data);
                    }
                } finally {
                    state.cacheLock.writeLock().unlock();
                }
            }
        } finally {
            if( readLock_unlock )state.cacheLock.readLock().unlock();
        }
    }

    @Override
    protected void writePage_mapped(int cidx, int page, byte[] data) {
        state.cachePageWriteLock(cidx, ()->{
            super.writePage_mapped(cidx, page, data);
            return null;
        });
    }

    @Override
    protected void writePage_alloc(int page, byte[] data) {
        super.writePage_alloc(page, data);
    }
    //endregion

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

    protected List<Lock> lockPersistPages( int from, int toExc, Function<ReadWriteLock,Lock> lockType ){
        return state.globalCacheLock(true,()->{
            List<Lock> lcks = new ArrayList<>();

            Set<Integer> cache_pages = state.prst2cache_read( map -> {
                Set<Integer> cache_pages_1 = new TreeSet<>();
                int p_from = Math.max(Math.min(from,toExc),0);
                int p_to = Math.max(Math.max(from,toExc),0);
                for( int p_i=p_from; p_i<p_to; p_i++ ){
                    Integer c_idx = map.get(p_i);
                    if( c_idx!=null ){
                        cache_pages_1.add(c_idx);
                    }
                }
                return cache_pages_1;
            });

            for( Integer cache_page : cache_pages ){
                Optional<ReadWriteLock> rwLock = state.cachePageRWLock(cache_page);
                if( rwLock.isPresent() ){
                    Lock lock = lockType.apply(rwLock.get());
                    lock.lock();
                    lcks.add(lock);
                }
            }

            return lcks;
        });
    }

    @Override
    public void writePageLock(int from, int toExc, Runnable code) {
        if( code==null )throw new IllegalArgumentException( "code==null" );
        List<Lock> locks = lockPersistPages(from,toExc, ReadWriteLock::writeLock);
        try {
            code.run();
        } finally {
            for( Lock lock : locks ){
                lock.unlock();
            }
        }
    }

    @Override
    public void readPageLock(int from, int toExc, Runnable code) {
        if( code==null )throw new IllegalArgumentException( "code==null" );
        List<Lock> locks = lockPersistPages(from,toExc, ReadWriteLock::readLock);
        try {
            code.run();
        } finally {
            for( Lock lock : locks ){
                lock.unlock();
            }
        }
    }
}
