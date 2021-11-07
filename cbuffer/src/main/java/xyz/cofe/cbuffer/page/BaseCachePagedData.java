package xyz.cofe.cbuffer.page;

import xyz.cofe.cbuffer.Flushable;
import xyz.cofe.fn.Tuple2;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Кеширование страниц.
 *
 * <h2>Состояния страниц</h2>
 * <table border="1">
 *     <tr style="vertical-align: top;">
 *         <td style="vertical-align: top; font-weight: bold">
 *             state
 *         </td>
 *         <td style="vertical-align: top; font-weight: bold">
 *             hard
 *         </td>
 *         <td style="vertical-align: top; font-weight: bold">
 *             cache
 *         </td>
 *         <td style="vertical-align: top; font-weight: bold">
 *             clean / dirty
 *         </td>
 *         <td style="vertical-align: top; font-weight: bold">
 *             read()
 *         </td>
 *         <td style="vertical-align: top; font-weight: bold">
 *             write()
 *         </td>
 *         <td style="vertical-align: top; font-weight: bold">
 *             flush()
 *         </td>
 *         <td style="vertical-align: top; font-weight: bold">
 *             reduce()
 *         </td>
 *     </tr>
 *
 *     <tr>
 *         <td style="color: gray"> none </td>
 *         <td style="color: gray"> ? </td>
 *         <td style="color: gray"> ? </td>
 *         <td style="color: gray"> ? </td>
 *         <td style="color: gray"> none </td>
 *         <td style="color: gray"> none </td>
 *         <td style="color: gray"> none </td>
 *         <td style="color: gray"> none </td>
 *     </tr>
 *
 *     <tr>
 *         <td style="color: gray"> bug </td>
 *         <td> ? </td>
 *         <td> cache </td>
 *         <td> any </td>
 *         <td style="color: gray"> bug </td>
 *         <td style="color: gray"> bug </td>
 *         <td style="color: gray"> bug </td>
 *         <td style="color: gray"> bug </td>
 *     </tr>
 *
 *     <tr>
 *         <td> # </td>
 *         <td> hard </td>
 *         <td style="color: gray"> none </td>
 *         <td style="color: gray"> none </td>
 *         <td> → map() </td>
 *         <td> → map() <br> → write() <br> → dirty</td>
 *         <td style="color: gray"> none </td>
 *         <td style="color: gray"> none </td>
 *     </tr>
 *
 *     <tr>
 *         <td> # </td>
 *         <td> hard </td>
 *         <td> cache </td>
 *         <td> clean </td>
 *         <td style="color: gray"> no change </td>
 *         <td> → dirty </td>
 *         <td style="color: gray"> no change </td>
 *         <td> unlink() </td>
 *     </tr>
 *
 *     <tr>
 *         <td> # </td>
 *         <td> hard </td>
 *         <td> cache </td>
 *         <td> dirty </td>
 *         <td style="color: gray"> no change </td>
 *         <td> → dirty </td>
 *         <td> → flush() <br> → clean </td>
 *         <td> store() <br> unlink() </td>
 *     </tr>
 *
 * </table>
 *
 */
public class BaseCachePagedData<S extends CachePagedState> implements ResizablePages, Flushable {
    protected final S state;

    /**
     * Конструктор
     * @param state внутреннее состояние
     */
    protected BaseCachePagedData(S state ){
        if( state==null )throw new IllegalArgumentException( "state==null" );
        this.state = state;
    }

    /**
     * Конструктор
     * @param cachePages Кеш память (быстрая)
     * @param persistentPages Постоянная (медленная)
     */
    protected BaseCachePagedData(DirtyPagedData cachePages, ResizablePages persistentPages, S state ){
        if( cachePages==null )throw new IllegalArgumentException( "cachePages==null" );
        if( persistentPages ==null )throw new IllegalArgumentException( "hardPages==null" );
        if( cachePages.memoryInfo().pageSize()!= persistentPages.memoryInfo().pageSize() ){
            throw new IllegalArgumentException( "different page size between cachePages and hardPages" );
        }
        if( state==null )throw new IllegalArgumentException( "state==null" );

        this.state = state;

        this.state.cachePages(cachePages);
        this.state.persistentPages(persistentPages);

        int pc = cachePages.memoryInfo().pageCount();

        int[] cache2prst = new int[pc];
        Arrays.fill(cache2prst,-1);
//        this.state.cache2prst(cache2prst);
        this.state.cache2prst_replace(arr -> cache2prst);

        this.state.prst2cache(new HashMap<>());
    }

    /**
     * Конструктор
     * @param cachePages Кеш память (быстрая)
     * @param persistentPages Постоянная (медленная)
     */
    public BaseCachePagedData(DirtyPagedData cachePages, ResizablePages persistentPages ){
        this( cachePages, persistentPages, (S)CachePagedState.nonSafe() );
    }

    protected boolean isClosed(){
        return state.isClosed();
    }

    @Override
    public UsedPagesInfo memoryInfo() {
        if( isClosed() )throw new IllegalStateException("closed");
        return state.persistentPages().memoryInfo();
    }

    // используется в readPage, writePage
    /**
     * Возвращает индекс кешированной страницы для постоянной страницы
     * @param persistPage постоянная страница, допустимые значения: от 0 и более
     * @return значение: от 0 и более - целевая страница кеша, или -1 (и меньше) - страница не спроецирована
     */
    protected int persist2cache( int persistPage ){
        if( persistPage<0 )throw new IllegalArgumentException( "persistPage<0" );
        Integer c_i = state.prst2cache_read( map -> map.get(persistPage));
        return c_i!=null ? c_i : -1;
    }

    // используется в flush, unmap, map, reducePages
    /**
     * Возвращает индекс постоянной страницы для кешированной страницы
     * @param cachePage кешированная страница, допустимые значения: от 0 и более
     * @return значение: от 0 и более - постоянная страница (индекс), или -1 (и меньше) - страница не спроецирована (свободная ячейка кеша)
     */
    protected int cache2persist( int cachePage ){
        arg_cachePage_range(cachePage);
        return state.cache2prst_read(arr -> arr.get(cachePage));
    }

    protected int cache2persist_mut( int cachePage ){
        arg_cachePage_range(cachePage);
        return state.cache2prst_read(arr -> arr.get(cachePage));
    }

    /**
     * Проверка, что страница указано в допустимых диапазонах, иначе генерирует IllegalArgumentException
     * @param cachePage кешированная страница, допустимые значения: от 0 и более
     */
    private void arg_cachePage_range(int cachePage ){
        if( cachePage<0 )throw new IllegalArgumentException( "cachePage out of range: cachePage<0" );

        int cnt = state.cache2prst_read(IntArrayReadOnly::length);
        if( cachePage>=cnt )throw new IllegalArgumentException( "cachePage out of range: cachePage>=cache2prst.length" );
    }

    // используется в unmap
    /**
     * Проверка, что страница является "грязной"
     * @param cachePage индекс страницы кеша
     * @return true - содержит не сохраненные изменения
     */
    protected boolean dirty( int cachePage ){
        arg_cachePage_range(cachePage);
        return state.cachePages().dirty(cachePage);
    }

    protected boolean dirty_mut( int cachePage ){
        arg_cachePage_range(cachePage);
        return state.cachePages().dirty(cachePage);
    }

    // используется в flush, unmap
    /**
     * Сохранение страницы кеша
     * @param cachePage индекс страницы кеша
     * @return индекс persistentPage или значение меньше 0
     */
    protected int flush( int cachePage ){
        return flush0(cachePage);
    }

    protected int flush_mut( int cachePage ){
        return flush0(cachePage);
    }

    private int flush0( int cachePage ){
        arg_cachePage_range(cachePage);
        if( !dirty(cachePage) )return -1;

        int persistPage = cache2persist(cachePage);
        if( persistPage<0 )return persistPage;

        state.persistentPages().writePage( persistPage, state.cachePages().readPage(cachePage) );
        state.cachePages().flushPage(cachePage);

        return persistPage;
    }

    @Override
    public void flush() {
        if( isClosed() )throw new IllegalStateException("closed");
        int cnt = state.cache2prst_read(IntArrayReadOnly::length);
        for( int cache_page=0; cache_page<cnt; cache_page++ ){
            flush(cache_page);
        }
    }

    // используется в allocCachePage, map, reducePages, resizeCachePages
    /**
     * выгрузка страницы
     * @param cachePage страница
     * @return индекс persistentPage или значение меньше 0
     */
    protected int unmap( int cachePage ){
        // проверить что страница:
        //   если dirty - сохранить
        // убрать связь cache2prst, prst2cache

        arg_cachePage_range(cachePage);
        if( dirty_mut(cachePage) )flush_mut(cachePage);

        int persistPage = cache2persist_mut(cachePage);
        if( persistPage<0 )return persistPage;

        state.prst2cache_write(map->map.remove(persistPage));
        state.cache2prst_write(arr->arr.set(cachePage,-1) );

        return persistPage;
    }

    // используется в allocCachePage
    /**
     * Получение списка чистых/грязных страниц
     * @return чистые/грязные страницы (cachePage)
     */
    protected Tuple2<List<Integer>,List<Integer>> cleanDirtyPages(){
        Tuple2<List<Integer>,List<Integer>> res =
            state.cache2prst_read( arr -> {
                List<Integer> cleanPages = new ArrayList<>(arr.length());
                List<Integer> dirtyPages = new ArrayList<>(arr.length());
                for( int i=0; i<arr.length(); i++ ){
                    int p = arr.get(i);
                    if (p < 0) {
                        cleanPages.add(i);
                    } else {
                        dirtyPages.add(i);
                    }
                }
                return Tuple2.of(cleanPages,dirtyPages);
            });
        return res;
    }

    // используется в allocCachePage
    /**
     * Выбор кандидата (страница кеша) на выгрузку
     * @param pages список страниц из которых выбор
     * @param clean true - это список чистых страниц, <br>
     *              false - это список грязных страниц
     * @return страница или -1 (и меньше),
     */
    protected int unmapCandidate(List<Integer> pages, boolean clean){
        if( pages.isEmpty() )return -1;
        return pages.get(ThreadLocalRandom.current().nextInt(pages.size()));
    }

    // Используется в readPage_alloc, writePage_alloc
    /**
     * Выделение свободной страницы
     *
     * <p>
     * Возможные сценарии
     * <ul>
     *     <li>В кеше есть еще не размеченная область cache2prst[x] < 0</li>
     *     <li>В кеше есть чистые страницы которые можно занять</li>
     *     <li>В кеше нет чистых страниц, необходимо выгружать страницу</li>
     * </ul>
     *
     * <p>
     *     Используется в {@link #readPage_alloc(int)}, {@link #writePage_alloc(int, byte[])}
     * @return индекс страницы
     */
    protected int allocCachePage(){
        int cachePagesCount = state.cachePages().memoryInfo().pageCount();
        if( cachePagesCount<1 )throw new IllegalStateException("cachePages pages not exists, call resizeCachePages");

        // В кеше есть еще не размеченная область cache2prst[x] < 0
        int unmappedCachePage = state.cache2prst_read(arr -> {
            for( int i=0; i<arr.length(); i++ ){
                int x = arr.get(i);
                if( x<0 )return i;
            }
            return -1;
        });
        if( unmappedCachePage>=0 )return unmappedCachePage;

        // В кеше есть чистые страницы которые можно занять
        Tuple2<List<Integer>,List<Integer>> cleanDirtyPages = cleanDirtyPages();
        List<Integer> cleanPages = cleanDirtyPages.a();
        List<Integer> dirtyPages = cleanDirtyPages.b();

        if( !cleanPages.isEmpty() ){
            // В кеше есть чистые страницы которые можно занять
            int p = unmapCandidate(cleanPages,true);
            if( p>=0 )return p;
        }

        if( dirtyPages.isEmpty() ){
            throw new IllegalStateException("all cache pages is busy, call resizeCachePages");
        }

        // В кеше нет чистых страниц, необходимо выгружать страницу
        int p = unmapCandidate(dirtyPages,false);
        if( p>=0 ){
            unmap(p);
            return p;
        }

        throw new IllegalStateException("all cache pages is busy, call resizeCachePages");
    }

    // Используется в readPage_alloc, writePage_alloc
    /**
     * Проецирование страницы из постоянной памяти на кеш
     * @param cachePage страница кеш-памяти
     * @param persistPage страница постоянной памяти
     * @return содержание спроецированной страницы
     */
    protected byte[] map( int cachePage, int persistPage ){
        if( isClosed() )throw new IllegalStateException("closed");
        arg_cachePage_range(cachePage);

        int prst_p_cnt = state.persistentPages().memoryInfo().pageCount();
        int cche_p_cnt = state.cachePages().memoryInfo().pageCount();

        if( cachePage<0 || cachePage>=cche_p_cnt ){
            throw new IllegalArgumentException("cachePage<0 || cachePage>=cche_p_cnt; cachePage="+cachePage+" cche_p_cnt="+cche_p_cnt);
        }
        if( persistPage<0 || persistPage>=prst_p_cnt ){
            throw new IllegalArgumentException("persistPage<0 || persistPage>=prst_p_cnt; persistPage="+persistPage+" prst_p_cnt="+prst_p_cnt);
        }

        int mapped_prst_page = cache2persist_mut(cachePage);
        if( mapped_prst_page>=0 )unmap(mapped_prst_page);

        byte[] buff = state.persistentPages().readPage(persistPage);
        state.cachePages().writePage(cachePage,buff);
        state.cachePages().flushPage(cachePage);

        state.cache2prst_write(arr -> {
            arr.set(cachePage,persistPage);
        });

        state.prst2cache_write(map -> map.put(persistPage,cachePage));

        return buff;
    }

    /**
     * Чтение страницы.
     *
     * <p>Варианты
     * <ul>
     *     <li> Страница спроецирована
     *     <li> Страница не спроецирована
     *     <ul>
     *         <li> необходимо - выделить свободную страницу в кеше
     *         <li> если не достигнут предел по кешу
     *         <ul>
     *             <li> тогда занять свободную страницу
     *             <li> иначе выгрузить редко используемую страницу и занять ее
     *         </ul>
     *     </ul>
     * </ul>
     * @param page индекс страницы, от 0 и более
     * @return массив байтов, по размеру равный {@link UsedPagesInfo#pageSize()} или меньше, если последняя страница
     */
    @Override
    public byte[] readPage(int page) {
        if( isClosed() )throw new IllegalStateException("closed");
        if( page<0 )throw new IllegalArgumentException( "page<0" );

        int cidx = persist2cache(page);
        if( cidx>=0 ){
            return readPage_mapped(cidx, page);
        }else{
            return readPage_alloc(page);
        }
    }

    protected byte[] readPage_mapped( int cidx, int page ){
        // Страница спроецирована
        return state.cachePages().readPage(cidx);
    }

    protected byte[] readPage_alloc( int page ){
        // Страница не спроецирована
        // необходимо - выделить свободную страницу в кеше
        //   если не достигнут предел по кешу
        //     тогда занять свободную страницу
        //     иначе выгрузить редко используемую страницу и занять ее
        int cache_page = allocCachePage();
        if( cache_page<0 )throw new IllegalStateException("can't allocate page in cache");

        return map(cache_page, page);
    }

    /**
     * Запись страницы
     *
     * <p>
     *     Возможные варианты
     *
     * <p>
     *     <b>Страница спроецирована ({@link #writePage_mapped(int, int, byte[])})</b>
     *     <ul>
     *     <li> изменить страницу в кеше
     *     <li> отметить страницу как dirty (auto)
     *     </ul>
     *
     * <p>
     *     <b>Страница не спроецирована ({@link #writePage_alloc(int, byte[])})</b>
     *
     *     <ul>
     *     <li>необходимо - выделить свободную страницу в кеше
     *       <ul>
     *       <li>если не достигнут предел по кешу
     *         <ul>
     *         <li>тогда занять свободную страницу
     *         <li>иначе выгрузить редко используемую страницу и занять ее
     *         </ul>
     *       </ul>
     *     <li>если data меньше pageSize
     *       <ul>
     *       <li>считать страницу из persist в cache
     *       </ul>
     *     <li>изменить страницу в кеше
     *     <li>отметить страницу как dirty (auto)
     *     </ul>
     *
     * @param page индекс страницы, от 0 и более
     * @param data массив байтов, размер не должен превышать {@link UsedPagesInfo#pageSize()}
     */
    @Override
    public void writePage(int page, byte[] data) {
        if( isClosed() )throw new IllegalStateException("closed");

        int page_size = state.cachePages().memoryInfo().pageSize();
        if( data.length>page_size )throw new IllegalArgumentException("data.length(="+data.length+") > page_size(="+page_size+")");

        int cidx = persist2cache(page);
        if( cidx>=0 ){
            writePage_mapped(cidx, page, data);
        }else{
            writePage_alloc(page, data);
        }
    }

    protected void writePage_mapped(int cidx, int page, byte[] data){
        // Страница спроецирована
        // изменить страницу в кеше
        // отметить страницу как dirty (auto)
        state.persistentPages().writePage(cidx, data);
    }

    protected void writePage_alloc(int page, byte[] data){
        // Страница не спроецирована
        // необходимо - выделить свободную страницу в кеше
        //   если не достигнут предел по кешу
        //     тогда занять свободную страницу
        //     иначе выгрузить редко используемую страницу и занять ее
        // если data меньше pageSize
        //   считать страницу из persist в cache
        // изменить страницу в кеше
        // отметить страницу как dirty (auto)
        int cache_page = allocCachePage();
        byte[] buff = map(cache_page, page);
        if( buff.length>data.length ){
            System.arraycopy(data,0, buff,0, data.length);
            state.cachePages().writePage(cache_page, buff);
        }else{
            state.cachePages().writePage(cache_page, data);
        }
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> extendPages(int pages) {
        if( isClosed() )throw new IllegalStateException("closed");
        if( pages<0 )throw new IllegalArgumentException( "pages<0" );
        if( pages==0 ){
            return Tuple2.of(
                state.persistentPages().memoryInfo(),
                state.persistentPages().memoryInfo()
            );
        }

        return state.persistentPages().extendPages(pages);
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> reducePages(int pages) {
        if( isClosed() )throw new IllegalStateException("closed");
        if( pages<0 )throw new IllegalArgumentException( "pages<0" );
        if( pages==0 ){
            return Tuple2.of(
                state.persistentPages().memoryInfo(),
                state.persistentPages().memoryInfo()
            );
        }

        // проверить что удаляемые страницы из кеша:
        //   если страницы спроецированы, тогда
        //     если clean - тогда спокойно удаляем
        //     если dirty - тогда:
        //       сохраняем страницу
        //   иначе - удаляем спокойно
        // удаление:
        //   cache2prst[_] = -1
        //   prst2cache[_] = -1

        int curr_pages = state.persistentPages().memoryInfo().pageCount();
        int next_pages = curr_pages - pages;
        if( next_pages<0 ){
            throw new IllegalArgumentException("reduce pages to big");
        }

        state.cache2prst_write(arr -> {
            for( int c_page=0; c_page<arr.length(); c_page++ ){
                int p_page = cache2persist_mut(c_page);
                if( p_page>=next_pages ){
                    unmap(c_page);
                }
            }
        });

        return state.persistentPages().reducePages(pages);
    }

    /**
     * Изменение размера кеша.
     *
     * @param pages сколько страниц отвести под кеш
     * @return Сколько было и сколько стало памяти
     */
    public Tuple2<UsedPagesInfo, UsedPagesInfo> resizeCachePages(int pages){
        if( isClosed() )throw new IllegalStateException("closed");
        if( pages<0 )throw new IllegalArgumentException( "pages<0" );

        //int[] c2p = state.cache2prst();
        int c2p_len = state.cache2prst_read(IntArrayReadOnly::length);

        int diff = pages-c2p_len;
        if( diff==0 ){
            return Tuple2.of(state.cachePages().memoryInfo(), state.cachePages().memoryInfo());
        }else if( diff>0 ){
            Tuple2<UsedPagesInfo, UsedPagesInfo> res = state.cachePages().resizePages(pages);

            state.cache2prst_replace( arr -> {
                int[] n_arr = arr.toArray();
                n_arr = Arrays.copyOf(n_arr, pages);
                for(int cache_page = c2p_len; cache_page<pages; cache_page++ ){
                    n_arr[cache_page] = -1;
                }
                return n_arr;
            });

            return res;
        }else{
            state.cache2prst_replace( arr -> {
                int[] n_arr = arr.toArray();
                for( int cache_page=0; cache_page<n_arr.length; cache_page++ ){
                    if( cache_page>=pages ){
                        unmap(cache_page);
                    }
                }
                n_arr = Arrays.copyOf(n_arr, pages);
                return n_arr;
            });

            return state.cachePages().resizePages(pages);
        }
    }
}
