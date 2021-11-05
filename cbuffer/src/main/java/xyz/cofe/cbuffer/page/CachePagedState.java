package xyz.cofe.cbuffer.page;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Состояние объекта {@link CachePagedData}
 * Предполагается наличие 2ух реализаций,
 * для Non Thread safe и Thread safe
 */
public interface CachePagedState {
    /**
     * Кеш страниц (быстрая)
     */
    DirtyPagedData cachePages();
    void cachePages( DirtyPagedData pages );

    /**
     * Основная|Постоянная память (медленная)
     */
    ResizablePages persistentPages();
    void persistentPages( ResizablePages pages );

    /**
     * Отображение кеша страниц на основную память.
     *
     * <p>
     * Семантика:
     * <code>{@link #cache2prst}[ cache_page_index ] = hard_pages_index</code>
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
    int[] cache2prst();
    void cache2prst(int[] map);
    default <R> R cache2prst_read(Function<IntArrayReadOnly,R> code){
        if( code==null )throw new IllegalArgumentException( "code==null" );
        return code.apply(IntArrayReadOnly.of(cache2prst()));
    }
    default <R> R cache2prst_write(Function<IntArrayMutable,R> code){
        if( code==null )throw new IllegalArgumentException( "code==null" );
        int[] buff = cache2prst();
        R res = code.apply(IntArrayMutable.of(buff));
        cache2prst(buff);
        return res;
    }
    default void cache2prst_write0(Consumer<IntArrayMutable> code){
        if( code==null )throw new IllegalArgumentException( "code==null" );
        cache2prst_write(arr -> {
            code.accept(arr);
            return null;
        });
    }
    default void cache2prst_replace(Function<IntArrayReadOnly,int[]> code){
        if( code==null )throw new IllegalArgumentException( "code==null" );
        int[] arr = cache2prst();
        IntArrayReadOnly i_arr = IntArrayReadOnly.of(arr);
        int[] r_arr = code.apply(i_arr);
        cache2prst(r_arr);
    }

    /**
     * отображение страниц основной памяти на кеш
     */
    Map<Integer,Integer> prst2cache();
    void prst2cache(Map<Integer,Integer> map);

    boolean isClosed();
    void close();

    /**
     * Создание состояния - Non Thread safe
     * @return состояние
     */
    public static CachePagedState nonSafe(){
        return new NonThreadSafe();
    }

    public static class NonThreadSafe implements CachePagedState {
        private DirtyPagedData cachePages;
        private ResizablePages persistentPages;
        private int[] cache2prst;
        private Map<Integer,Integer> prst2cache;

        @Override
        public DirtyPagedData cachePages() {
            if( closed )throw new IllegalStateException("CachePagedData closed");
            return cachePages;
        }

        @Override
        public void cachePages(DirtyPagedData pages) {
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
        public int[] cache2prst() {
            if( closed )throw new IllegalStateException("CachePagedData closed");
            return cache2prst;
        }

        @Override
        public void cache2prst(int[] map) {
            if( closed )throw new IllegalStateException("CachePagedData closed");
            this.cache2prst = map;
        }

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

        private boolean closed = false;

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
