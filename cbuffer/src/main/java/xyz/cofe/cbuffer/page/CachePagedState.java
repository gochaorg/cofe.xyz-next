package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Tuple2;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Состояние объекта {@link CachePagedDataBase}
 * Предполагается наличие 2ух реализаций,
 * для Non Thread safe и Thread safe
 */
public interface CachePagedState<M extends UsedPagesInfo> {
    /**
     * Кеш страниц (быстрая)
     */
    DirtyPagedDataBase<M> cachePages();
    void cachePages( DirtyPagedDataBase<M> pages );

    /**
     * Основная|Постоянная память (медленная)
     */
    ResizablePages<M> persistentPages();
    void persistentPages( ResizablePages<M> pages );

    /**
     * Отображение кеша страниц на основную память.
     *
     * <p>
     * Семантика:
     * <code>cache2prst[ cache_page_index ] = hard_pages_index</code>
     *
     * <p>
     * Значение (hard_pages_index):
     * <ul>
     *     <li> <b>-1 или меньше</b> - нет отображения</li>
     *     <li>
     *         <b>0 или больше</b> - отображение есть
     *         <p>
     *             страница может быть чистой или грязной:
     *             {@link DirtyPagedData#dirty(int)} - где аргумент <i>hard_pages_index</i>
     *     </li>
     * </ul>
     */
    <R> R cache2prst_read(Function<IntArrayReadOnly,R> code);
    void cache2prst_write(Consumer<IntArrayMutable> code);
    void cache2prst_replace(Function<IntArrayReadOnly,int[]> code);

    /**
     * отображение страниц основной памяти на кеш
     */
    Map<Integer,Integer> prst2cache();
    default <R> R prst2cache_read(Function<MapReadonly<Integer,Integer>,R> code){
        if( code==null )throw new IllegalArgumentException( "code==null" );
        Map<Integer,Integer> map = prst2cache();
        return code.apply(MapReadonly.of(map));
    }

    void prst2cache(Map<Integer,Integer> map);
    default <R> R prst2cache_write(Function<MapMutable<Integer,Integer>,R> code){
        Map<Integer,Integer> map = prst2cache();
        R r = code.apply(MapMutable.of(map));
        prst2cache(map);
        return r;
    }

    boolean isClosed();
    void close();

    void statCacheHitMiss( boolean hit );
    Tuple2<Long,Long> statCacheHitMiss();

    /**
     * Создание состояния - Non Thread safe
     * @return состояние
     */
    public static NonThreadSafe nonSafe(){
        return new NonThreadSafe();
    }

    public static class NonThreadSafe implements CachePagedState<UsedPagesInfo> {
        private DirtyPagedDataBase<UsedPagesInfo> cachePages;
        private ResizablePages<UsedPagesInfo> persistentPages;
        private int[] cache2prst = new int[0];
        private Map<Integer,Integer> prst2cache = new HashMap<>();
        private boolean closed = false;

        private long stateCacheHit = 0;
        private long stateCacheMiss = 0;

        @Override
        public void statCacheHitMiss(boolean hit) {
            if( hit ){
                stateCacheHit++;
            }else {
                stateCacheMiss++;
            }
        }

        @Override
        public Tuple2<Long, Long> statCacheHitMiss() {
            return Tuple2.of(stateCacheHit,stateCacheMiss);
        }

        @Override
        public DirtyPagedDataBase<UsedPagesInfo> cachePages() {
            if( closed )throw new IllegalStateException("CachePagedData closed");
            return cachePages;
        }

        @Override
        public void cachePages(DirtyPagedDataBase<UsedPagesInfo> pages) {
            if( closed )throw new IllegalStateException("CachePagedData closed");
            this.cachePages = pages;
        }

        @Override
        public ResizablePages persistentPages() {
            if( closed )throw new IllegalStateException("CachePagedData closed");
            return persistentPages;
        }

        @Override
        public void persistentPages(ResizablePages pages) {
            if( closed )throw new IllegalStateException("CachePagedData closed");
            this.persistentPages = pages;
        }

        @Override
        public <R> R cache2prst_read(Function<IntArrayReadOnly, R> code) {
            if( code==null )throw new IllegalArgumentException( "code==null" );
            return code.apply(IntArrayReadOnly.of(cache2prst));
        }

        @Override
        public void cache2prst_write(Consumer<IntArrayMutable> code) {
            if( code==null )throw new IllegalArgumentException( "code==null" );
            code.accept(IntArrayMutable.of(cache2prst));
        }

        @Override
        public void cache2prst_replace(Function<IntArrayReadOnly, int[]> code) {
            if( code==null )throw new IllegalArgumentException( "code==null" );
            int[] res = code.apply(IntArrayReadOnly.of(cache2prst==null ? new int[0] : cache2prst));
            if( res==null )throw new IllegalStateException("cache2prst_replace(code), code - return null");
            cache2prst = res;
        }

        /////////////////////////////

        @Override
        public Map<Integer, Integer> prst2cache() {
            if( closed )throw new IllegalStateException("CachePagedData closed");
            return prst2cache;
        }

        @Override
        public void prst2cache(Map<Integer, Integer> map) {
            if( closed )throw new IllegalStateException("CachePagedData closed");
            prst2cache = map;
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() {
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
}
