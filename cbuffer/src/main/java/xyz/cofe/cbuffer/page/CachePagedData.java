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
public class CachePagedData implements ResizablePages, Flushable {
    /**
     * Кеш страниц (быстрая)
     */
    protected DirtyPagedData cachePages;

    /**
     * Основная|Постоянная память (медленная)
     */
    protected ResizablePages persistentPages;

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
    @SuppressWarnings({"SpellCheckingInspection"})
    protected int[] cache2prst;

    /**
     * отображение страниц основной памяти на кеш
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    protected Map<Integer,Integer> prst2cache;

    /**
     * Конструктор
     * @param cachePages Кеш память (быстрая)
     * @param persistentPages Постоянная (медленная)
     */
    public CachePagedData( DirtyPagedData cachePages, ResizablePages persistentPages ){
        if( cachePages==null )throw new IllegalArgumentException( "cachePages==null" );
        if( persistentPages ==null )throw new IllegalArgumentException( "hardPages==null" );
        if( cachePages.memoryInfo().pageSize()!= persistentPages.memoryInfo().pageSize() ){
            throw new IllegalArgumentException( "different page size between cachePages and hardPages" );
        }
        this.cachePages = cachePages;
        this.persistentPages = persistentPages;

        int pc = cachePages.memoryInfo().pageCount();

        cache2prst = new int[pc];
        Arrays.fill(cache2prst,-1);

        prst2cache = new HashMap<>();
    }

    protected boolean isClosed(){
        if( cachePages==null )return true;
        if( persistentPages==null )return true;
        if( cache2prst==null )return true;
        if( prst2cache==null )return true;
        return false;
    }

    @Override
    public UsedPagesInfo memoryInfo() {
        if( isClosed() )throw new IllegalStateException("closed");
        return persistentPages.memoryInfo();
    }

    /**
     * Возвращает индекс кешированной страницы для постоянной страницы
     * @param persistPage постоянная страница, допустимые значения: от 0 и более
     * @return значение: от 0 и более - целевая страница кеша, или -1 (и меньше) - страница не спроецирована
     */
    protected int persist2cache( int persistPage ){
        if( persistPage<0 )throw new IllegalArgumentException( "persistPage<0" );
        Integer c_idx = prst2cache.get(persistPage);
        return c_idx!=null ? c_idx : -1;
    }

    /**
     * Возвращает индекс постоянной страницы для кешированной страницы
     * @param cachePage кешированная страница, допустимые значения: от 0 и более
     * @return значение: от 0 и более - постоянная страница (индекс), или -1 (и меньше) - страница не спроецирована (свободная ячейка кеша)
     */
    protected int cache2persist( int cachePage ){
        arg_cachePage_range(cachePage);
        return cache2prst[cachePage];
    }

    /**
     * Проверка, что страница указано в допустимых диапазонах, иначе генерирует IllegalArgumentException
     * @param cachePage кешированная страница, допустимые значения: от 0 и более
     */
    private void arg_cachePage_range(int cachePage ){
        if( cachePage<0 )throw new IllegalArgumentException( "cachePage out of range: cachePage<0" );
        if( cachePage>=cache2prst.length )throw new IllegalArgumentException( "cachePage out of range: cachePage>=cache2prst.length" );
    }

    /**
     * Проверка, что страница является "грязной"
     * @param cachePage индекс страницы кеша
     * @return true - содержит не сохраненные изменения
     */
    protected boolean dirty( int cachePage ){
        arg_cachePage_range(cachePage);
        return cachePages.dirty(cachePage);
    }

    /**
     * Проверка, что страница является "чистой"
     * @param cachePage индекс страницы кеша
     * @return true - данные страницы сохранены в persistent
     */
    protected boolean clean( int cachePage ){
        return !dirty(cachePage);
    }

    /**
     * Сохранение страницы кеша
     * @param cachePage индекс страницы кеша
     * @return индекс persistentPage или значение меньше 0
     */
    protected int flush( int cachePage ){
        arg_cachePage_range(cachePage);
        if( clean(cachePage) )return -1;

        int persistPage = cache2persist(cachePage);
        if( persistPage<0 )return persistPage;

        persistentPages.writePage( persistPage, cachePages.readPage(cachePage) );
        cachePages.flushPage(cachePage);

        return persistPage;
    }

    @Override
    public void flush() {
        if( isClosed() )throw new IllegalStateException("closed");
        for( int cache_page=0; cache_page<cache2prst.length; cache_page++ ){
            flush(cache_page);
        }
    }

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
        if( dirty(cachePage) )flush(cachePage);

        int persistPage = cache2persist(cachePage);
        if( persistPage<0 )return persistPage;

        prst2cache.remove(persistPage);
        cache2prst[cachePage] = -1;

        return persistPage;
    }

    /**
     * Получение списка чистых/грязных страниц
     * @return чистые/грязные страницы (cachePage)
     */
    protected Tuple2<List<Integer>,List<Integer>> cleanDirtyPages(){
        List<Integer> cleanPages = new ArrayList<>(cache2prst.length);
        List<Integer> dirtyPages = new ArrayList<>(cache2prst.length);
        for (int p : cache2prst) {
            if (p < 0) {
                cleanPages.add(p);
            } else {
                dirtyPages.add(p);
            }
        }
        return Tuple2.of(cleanPages,dirtyPages);
    }

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
     * @return индекс страницы
     */
    protected int allocCachePage(){
        int cachePagesCount = cachePages.memoryInfo().pageCount();
        if( cachePagesCount<1 )throw new IllegalStateException("cachePages pages not exists, call resizeCachePages");

        // В кеше есть еще не размеченная область cache2prst[x] < 0
        for( int i=0; i<cache2prst.length; i++ ){
            int x = cache2prst[i];
            if( x<0 )return i;
        }

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

    /**
     * Проецирование страницы из постоянной памяти на кеш
     * @param cachePage страница кеш-памяти
     * @param persistPage страница постоянной памяти
     * @return содержание спроецированной страницы
     */
    protected byte[] map( int cachePage, int persistPage ){
        if( isClosed() )throw new IllegalStateException("closed");
        arg_cachePage_range(cachePage);

        int prst_p_cnt = persistentPages.memoryInfo().pageCount();
        int cche_p_cnt = cachePages.memoryInfo().pageCount();

        if( cachePage<0 || cachePage>=cche_p_cnt ){
            throw new IllegalArgumentException("cachePage<0 || cachePage>=cche_p_cnt; cachePage="+cachePage+" cche_p_cnt="+cche_p_cnt);
        }
        if( persistPage<0 || persistPage>=prst_p_cnt ){
            throw new IllegalArgumentException("persistPage<0 || persistPage>=prst_p_cnt; persistPage="+persistPage+" prst_p_cnt="+prst_p_cnt);
        }

        int mapped_prst_page = cache2persist(cachePage);
        if( mapped_prst_page>=0 )unmap(mapped_prst_page);

        byte[] buff = persistentPages.readPage(persistPage);
        cachePages.writePage(cachePage,buff);
        cachePages.flushPage(cachePage);

        cache2prst[cachePage] = persistPage;
        prst2cache.put(persistPage, cachePage);

        return buff;
    }

    @Override
    public byte[] readPage(int page) {
        if( isClosed() )throw new IllegalStateException("closed");
        if( page<0 )throw new IllegalArgumentException( "page<0" );

        int cidx = persist2cache(page);
        if( cidx>=0 ){
            // Страница спроецирована
            return cachePages.readPage(cidx);
        }else{
            // Страница не спроецирована
            // необходимо - выделить свободную страницу в кеше
            //   если не достигнут предел по кешу
            //     тогда занять свободную страницу
            //     иначе выгрузить редко используемую страницу и занять ее
            int cache_page = allocCachePage();
            if( cache_page<0 )throw new IllegalStateException("can't allocate page in cache");

            return map(cache_page, page);
        }
    }

    @Override
    public void writePage(int page, byte[] data) {
        if( isClosed() )throw new IllegalStateException("closed");

        int page_size = cachePages.memoryInfo().pageSize();
        if( data.length>page_size )throw new IllegalArgumentException("data.length(="+data.length+") > page_size(="+page_size+")");

        int cidx = persist2cache(page);
        if( cidx>=0 ){
            // Страница спроецирована
            // изменить страницу в кеше
            // отметить страницу как dirty (auto)
            persistentPages.writePage(cidx, data);
        }else{
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
                cachePages.writePage(cache_page, buff);
            }else{
                cachePages.writePage(cache_page, data);
            }
        }
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> extendPages(int pages) {
        if( isClosed() )throw new IllegalStateException("closed");
        if( pages<0 )throw new IllegalArgumentException( "pages<0" );
        if( pages==0 ){
            return Tuple2.of(
                persistentPages.memoryInfo(),
                persistentPages.memoryInfo()
            );
        }

        return persistentPages.extendPages(pages);
    }

    @Override
    public Tuple2<UsedPagesInfo, UsedPagesInfo> reducePages(int pages) {
        if( isClosed() )throw new IllegalStateException("closed");
        if( pages<0 )throw new IllegalArgumentException( "pages<0" );
        if( pages==0 ){
            return Tuple2.of(
                persistentPages.memoryInfo(),
                persistentPages.memoryInfo()
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

        int curr_pages = persistentPages.memoryInfo().pageCount();
        int next_pages = curr_pages - pages;
        if( next_pages<0 ){
            throw new IllegalArgumentException("reduce pages to big");
        }

        for( int c_page=0; c_page<cache2prst.length; c_page++ ){
            int p_page = cache2persist(c_page);
            if( p_page>=next_pages ){
                unmap(c_page);
            }
        }

        return persistentPages.reducePages(pages);
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

        int curr_pages = cache2prst.length;
        int diff = pages-curr_pages;
        if( diff==0 ){
            return Tuple2.of(cachePages.memoryInfo(), cachePages.memoryInfo());
        }else if( diff>0 ){
            Tuple2<UsedPagesInfo, UsedPagesInfo> res = cachePages.resizePages(pages);
            int before_pages = cache2prst.length;
            cache2prst = Arrays.copyOf(cache2prst, pages);
            for( int cache_page=before_pages;cache_page<pages;cache_page++ ){
                cache2prst[cache_page] = -1;
            }
            return res;
        }else{
            for( int cache_page=0; cache_page<cache2prst.length; cache_page++ ){
                if( cache_page>=pages ){
                    unmap(cache_page);
                }
            }
            cache2prst = Arrays.copyOf(cache2prst, pages);
            return cachePages.resizePages(pages);
        }
    }
}
