package xyz.cofe.cbuffer.page;

import xyz.cofe.collection.IndexSet;
import xyz.cofe.collection.IndexSetBasic;
import xyz.cofe.fn.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Базовая реализация страничной организации данных с кэшем
 */
@SuppressWarnings("WeakerAccess")
public class PageDataImpl extends PageDataAbstract implements PageData, PageSlowWrite {
    //region Запись в кэш
    //region fastData()
    /**
     * Событие записи в страницу кэша.
     * <b>Нумерация страниц - относительно кэша</b>
     */
    public final PageEvent<Pair<Integer,byte[]>> onFastDataWrited = new PageEvent<>();

    /**
     * Запись данных в fast страницы (кэш)
     *
     * <ul>
     * <li>Запись страницы в fast.
     * <li>Страница помечается как измененная {@link #dirty(int)}
     * </ul>
     * @param fastPageIndex индекс кэш страницы
     * @param bytes данные
     */
    @Override
    public void fastData( int fastPageIndex, byte[] bytes ){
        PageData.super.fastData(fastPageIndex,bytes);
        onFastDataWrited.notify(Pair.of(fastPageIndex,bytes));
    }
    //endregion
    //region fast data size
    /**
     * Событие установки размера кэш страницы.
     * <b>Нумерация страниц - относительно кэша</b>
     */
    public final PageEvent<Pair<Integer,Integer>> onFastDataSize = new PageEvent<>();

    /**
     * Размер кэш страницы
     */
    protected final Map<Integer,Integer> fastDataSize = new HashMap<>();

    /**
     * Установка размера кэш страницы
     * @param fastPageIndex индекс fast страницы
     * @param dataSize кол-во байт
     */
    @Override
    public void fastDataSize( int fastPageIndex, int dataSize ){
        if( fastPageIndex<0 ) throw new IllegalArgumentException("fastPageIndex<0");
        fastDataSize.put(fastPageIndex,dataSize);
        onFastDataSize.notify( Pair.of(fastPageIndex,dataSize) );
    }

    /**
     * Получение кол-ва данных кэш страницы
     * @param fastPageIndex индекс fast страницы
     * @return кол-во данных на странице
     */
    @Override
    public int fastDataSize( int fastPageIndex ){
        if( fastPageIndex<0 ) throw new IllegalArgumentException("fastPageIndex<0");
        return fastDataSize.getOrDefault(fastPageIndex, getPageSize());
    }
    //endregion
    //region dirty fast pages
    public final Map<Integer,Boolean> dirtyFastPages = new HashMap<>();

    @Override
    public boolean dirty( int fastPageIndex ){
        return dirtyFastPages.getOrDefault(fastPageIndex,false);
    }

    public final PageEvent<Pair<Integer,Boolean>> onDirty = new PageEvent<>();

    @Override
    public void dirty( int fastPageIndex, boolean dirty ){
        dirtyFastPages.put(fastPageIndex,dirty);
        onDirty.notify(Pair.of(fastPageIndex,dirty));
    }

    public int getDirtyPageCount(){
        return getDirtyPages().size();
    }

    public IndexSet<Integer> getDirtyPages(){
        IndexSet<Integer> pages = new IndexSetBasic<>();
        dirtyFastPages.forEach( (page,dirty)->{if(dirty){pages.add(page);}} );
        return pages;
    }
    //endregion
    //endregion

    //region Внутренние структуры данных
    //region fast2slow : Map<Integer,Integer> - отображение fast на slow
    /**
     * Таблица соответствия кэш страниц и страниц на диске <br>
     * т.е. отображение <b>fast &#x2192; на &#x2192; slow</b>
     */
    protected final Map<Integer,Integer> fast2slow = new HashMap<>();
    //endregion
    //region slow2fast : Map<Integer,Integer> - отображение slow на fast
    /**
     * Таблица соответствия страниц на диске и кэш страниц<br>
     * т.е. отображение <b>slow &#x2192; на &#x2192; fast</b>
     */
    protected final Map<Integer,Integer> slow2fast = new HashMap<>();
    //endregion
    //region fastToSlow(), slowToFast()
    /**
     * Возвращает отображение страницы fast = на =&gt; slow
     * @param fastPageIndex Индекс страницы в fast буфере
     * @return индес страницы в slow соответ fast или -1, если нет соответствия
     */
    @Override
    public int fastToSlow( int fastPageIndex ){
        return fast2slow.getOrDefault(fastPageIndex,-1);
    }

    /**
     * Возвращает отображение страницы slow = на =&gt; fast
     * @param slowPageIndex Индекс страницы в slow буфере
     * @return индес страницы в fast соответ slow или -1, если нет соответствия
     */
    @Override
    public int slowToFast( int slowPageIndex ){
        return slow2fast.getOrDefault(slowPageIndex,-1);
    }
    //endregion

    //region freepages : IndexSet<Integer> - Список свободных кэш страниц
    /**
     * Список свободных кэш страниц
     */
    protected final IndexSet<Integer> freepages = new IndexSetBasic<>();
    //endregion
    //region usedpages : IndexSet<Integer> - Список занятых кэш страниц
    /**
     * Список занятых кэш страниц
     */
    protected final IndexSet<Integer> usedpages = new IndexSetBasic<>();
    //endregion

    //region fastPageCount() - кол-во используемых кэш страниц
    /**
     * Возвращает кол-во используемых кэш страниц
     * @return кол-во используемых кэш страниц
     */
    @Override
    public int fastPageCount(){
        return fast2slow.size();
    }
    //endregion
    //region maxFastPageCount : int = 16 - максимальное кол-во кеш страниц
    /**
     * максимальное кол-во кеш страниц, по умолчанию 16
     */
    protected volatile int maxFastPageCount = 16;

    /**
     * Указыает максимальное кол-во кэшируемых страниц
     * @return максимальное кол-во кеш страниц, по умолчанию 16
     */
    public int getMaxFastPageCount(){
        //synchronized( this ){
            return maxFastPageCount;
        //}
    }

    public static final int MAX_FAST_PAGE_COUNT_Min = 1;

    /**
     * Указыает максимальное кол-во кэшируемых страниц
     * @param max максимальное кол-во кеш страниц, минимально допустимое - 1
     */
    public void setMaxFastPageCount(int max){
        if( max<MAX_FAST_PAGE_COUNT_Min )throw new IllegalArgumentException("max < "+MAX_FAST_PAGE_COUNT_Min);
        //synchronized( this ){
            maxFastPageCount = max;
        //}
    }
    //endregion
    //region maxFastPageIndex : int - максимальный используемый индекс кэш страницы.
    /**
     * Поиск максимального используемого индекса кэш страницы.
     * <br>
     * Ищет в {@link #fast2slow} максимальный индекс
     * @return максимальный индекс или -1
     */
    protected int getMaxFastPageIndex(){
        return fast2slow.keySet().stream().max((a,b)->a-b).orElse(-1);
    }
    //endregion
    //endregion

    //region allocate - выделение страниц в кэше
    //region alloc page events - события выделения страницы в кэше
    /**
     * Событие выделения страницы из списка свободных (ранее занятых) {@link #freepages}
     */
    public final PageEvent<Integer> onAllocFreePage = new PageEvent<>();

    /**
     * Событие выделения страницы из неразмеченной области (не распределенных)
     */
    public final PageEvent<Integer> onAllocNewPage = new PageEvent<>();

    /**
     * Событие выделения страницы из списка используемых страниц, с предварительным сохранением данных
     */
    public final PageEvent<Integer> onAllocExistsPage = new PageEvent<>();

    /**
     * Событие выделения страницы.
     * Происходит при срабатвании любого из перечисленных:
     * {@link #onAllocExistsPage}, {@link #onAllocFreePage}, {@link #onAllocNewPage}
     */
    public final PageEvent<Integer> onAlloc;
    {
        onAlloc = new PageEvent<>();
        onAlloc.listen(onAllocExistsPage);
        onAlloc.listen(onAllocFreePage);
        onAlloc.listen(onAllocNewPage);
    }
    //endregion
    //region allocatePage() : int - Выделение кэш страницы
    /**
     * Выделение кэш страницы. <br>
     * <b>Нумерация страниц - относительно кэша</b> <br>
     * @return индекс кэш страницы или -1,
     * если не выделена страница - возможно при условии, что getMaxFastPageCount() вернет 0 или меньше
     */
    protected int allocatePage(){
        // поиск среди свободных
        if( freepages.size()>0 ){
            Integer p = freepages.removeByIndex(0);
            onAllocFreePage.notify(p);
            return p;
        }

        int maxFastPgCnt = getMaxFastPageCount();

        // поиск среди не распределенных
        int unallocated = maxFastPgCnt - fastPageCount();
        if( unallocated>0 ){
            int maxPi = getMaxFastPageIndex();
            int p = maxPi>=0 ? maxPi+1 : 0;
            onAllocNewPage.notify(p);
            return p;
        }

        // поиск среди уже использованых
        int used = usedpages.size();
        if( used>0 ){
            int rndint = Math.abs(ThreadLocalRandom.current().nextInt());
            int trgt = rndint % used;
            unmap(trgt);

            onAllocExistsPage.notify(trgt);
            return trgt;
        }

        if( maxFastPgCnt>0 ){
            // выделение первой
            onAllocNewPage.notify(0);
            return 0;
        }

        return -1;
    }
    //endregion
    //endregion

    //region map/unmap операции отображения страниц
    /**
     * Событие удаления связи между кэш страницей и данными
     */
    public final PageEvent<PageMapEntry> onUnmapped = new PageEvent<>();

    /**
     * Событие сопоставления между кэш страницей и данными
     */
    public final PageEvent<PageMapEntry> onMapped = new PageEvent<>();

    /**
     * Загрузка страницы из данных (slow) в кэш (fast)
     * @param slowPageIndex страница
     * @return индекс кэш страницы (fast)
     */
    @Override
    public int map( int slowPageIndex ){
        if( slowPageIndex<0 ) throw new IllegalArgumentException("slowPageIndex<0");

        int trgt = allocatePage();
        if( trgt<0 )throw new IllegalStateException("can't allocate page");

        usedpages.add(trgt);
        freepages.remove(trgt);

        fast2slow.put(trgt,slowPageIndex);
        slow2fast.put(slowPageIndex,trgt);

        byte[] data = slowData(slowPageIndex);
        fastData(trgt,data);
        dirty(trgt,false);

        onMapped.notify(PageMapEntry.of(trgt,slowPageIndex));

        return trgt;
    }

    /**
     * Освобождение кэш страницы.
     *
     * <p>
     * Если страница помечена как грязная (т.е. измененная {@link #dirty(int)}),
     * то она будет сохранена - {@link #saveFastPage(int)}.
     *
     * <ul>
     *   <li> Удаляет связи из карт {@link #slow2fast}, {@link #fast2slow},
     *   <li> Изменяет статус свободных / занятых страниц: {@link #freepages}, {@link #usedpages}
     *   <li> Генерирует событие {@link #onUnmapped}
     * </ul>
     * @param fastPageIndex иднекс кэш страницы
     */
    public void unmap( int fastPageIndex ){
        if( dirty(fastPageIndex) ){
            saveFastPage(fastPageIndex);
        }

        int slowPage = fastToSlow(fastPageIndex);

        slow2fast.remove(slowPage, fastPageIndex);
        fast2slow.remove(fastPageIndex,slowPage);
        freepages.add(fastPageIndex);
        usedpages.remove(slowPage);

        onUnmapped.notify(PageMapEntry.of(fastPageIndex,slowPage));
    }
    //endregion

    //region saveFastPage() - Сохранение кэш страницы
    /**
     * Событие сохранения кэш страницы
     */
    public final PageEvent<Integer> onSavedFastPage = new PageEvent<>();

    /**
     * Сохранение кэш страницы
     * @param fastPageIndex индекс кэш страницы
     */
    public void saveFastPage(int fastPageIndex){
        int slowPage = fastToSlow(fastPageIndex);
        if( slowPage>=0 ){
            byte[] data = fastData(fastPageIndex);
            slowData(slowPage,data);
            dirty(fastPageIndex,false);
            onSavedFastPage.notify(fastPageIndex);
        }
    }
    //endregion

    //region чтение/запись данных
    /**
     * Событие чтения данных из страницы
     */
    public final PageEvent<PageDataReaded> onDataRead = new PageEvent<>();

    /**
     * Читает данные страницы
     *
     * @param page страница - <b>сквозная нумерация</b>
     * @return данные
     */
    @Override
    public byte[] data( int page ){
        byte[] bytes = PageData.super.data(page);
        onDataRead.send( ()->PageDataReaded.of(page,bytes) );
        return bytes;
    }

    /**
     * Событие записи данных в страницу
     */
    public final PageEvent<PageDataWrited> onDataWrited = new PageEvent<>();

    /**
     * Записывает данные в страницу
     *
     * @param page  страница - <b>сквозная нумерация</b>
     * @param bytes данные
     */
    @Override
    public void data( int page, byte[] bytes ){
        PageData.super.data(page,bytes);
        onDataWrited.send(()->PageDataWrited.of(page,bytes));
    }
    //endregion
}
