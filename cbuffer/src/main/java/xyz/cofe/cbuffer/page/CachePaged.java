package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Fn1;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * Кеш страниц памяти
 *
 * <h2>Страничная организация памяти</h2>
 *
 * <ul>
 *     <li>
 * Отображает страницы кеш памяти на страницы постоянной памяти,
 * по мере необходимости загружает страницы из постоянной в кеш память
 * и выгружает страницы из кеша в постоянную память.
 *     </li>
 *     <li>
 * Отображением страниц кеша на постоянную занимается {@link CacheMap}
 *     </li>
 *     <li>
 * Размер страницы - постоянное значение в байтах
 *     </li>
 *     <li>
 * Каждая страница имеет свой номер, нумерация начинаеться с 0
 *     </li>
 * </ul>
 *
 *
 *
 *
 * <h2>Отображение страниц</h2>
 *
 * <ul>
 *     <li>
 *        В постоянной памяти страницы расположены последовательно
 *     </li>
 *     <li>
 *        В кеш памяти страницы содержат копии страниц постоянной памяти
 *     </li>
 *     <li>
 *        В кеш памяти страницы могут распологаться в любом порядке
 *     </li>
 *     <li>
 *         Страницы кеш памяти соержат дополнительные свойства
 *         <ul>
 *            <li>признак наличия отображения</li>
 *            <li>номер страницы в постоянной памяти</li>
 *            <li>факт изменения кеш страницы и изменения еще не записаны в постоянную память</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * <h2>Блокировки</h2><br/>
 *
 * Есть несколько типов блокировок
 *
 * <ul>
 *     <li>ReadWrite {@link ReentrantReadWriteLock}
 *     </li>
 *     <li>
 *         Блокировки на несколько уровней
 *         <ul>
 *             <li>
 *                 На уровне {@link CachePaged}:
 *                 {@link #readLock(Supplier)} )} {@link #writeLock(Runnable)}
 *             </li>
 *             <li>
 *                 На уровне отдельной страницы постоянной памяти
 *                 {@link #readPersistentLock(int, Supplier)}
 *                 {@link #writePersistentLock(int, Runnable)}
 *             </li>
 *             <li>
 *                 На уровне распределения/отображения постоянных/кеш страниц - {@link CacheMap}
 *                 {@link CacheMap#readLock(Supplier)}
 *                 {@link CacheMap#writeLock(Runnable)}
 *             </li>
 *             <li>
 *                 На уровне страницы кеша {@link CachePage}
 *                 {@link CachePage#readLock(Supplier)}
 *                 {@link CachePage#writeLock(Runnable)}
 *             </li>
 *         </ul>
 *     </li>
 * </ul>
 * @param <CACHEPAGES> Страницы кеша
 * @param <PERSISTPAGES> Страницы постоянной памяти
 */
public class CachePaged<CACHEPAGES extends Paged & ResizablePages, PERSISTPAGES extends Paged & ResizablePages> implements Paged, ResizablePages {
    //#region listeners
    private final PageListener.PageListenerSupport listeners = new PageListener.PageListenerSupport();

    /**
     * Добавление подписчика на события
     * @param listener подписчик
     */
    public void addListener(PageListener listener) {
        listeners.addListener(listener);
    }

    /**
     * Удаление подписчика
     * @param listener подписчик
     */
    public void removeListener(PageListener listener) {
        listeners.removeListener(listener);
    }

    /**
     * Проверка наличия подписчика
     * @param listener подписчик
     * @return true - подписчик
     */
    public boolean hasListener(PageListener listener) {
        return listeners.hasListener(listener);
    }

    /**
     * Уведомить подписчиков о событии
     * @param event событие
     */
    public void fire(PageEvent event) {
        listeners.fire(event);
    }
    //#endregion

    /**
     * Конструктор
     * @param cache кеш страниц временных данных
     * @param persistent странцы постоянной памяти
     */
    public CachePaged(CACHEPAGES cache, PERSISTPAGES persistent){
        if( cache==null )throw new IllegalArgumentException("cache==null");
        if( persistent==null )throw new IllegalArgumentException("persistent==null");

        if( cache.memoryInfo().pageCount()<1 )throw new IllegalArgumentException("cache.memoryInfo().pageCount()<1");
        if( persistent.memoryInfo().pageCount()<cache.memoryInfo().pageCount() )throw new IllegalArgumentException("persistent.memoryInfo().pageCount()<cache.memoryInfo().pageCount()");

        if( cache.memoryInfo().pageSize()!=persistent.memoryInfo().pageSize() )
            throw new PageError("pageSize different between cache and persistent");

        this.cache = cache;
        this.persistent = persistent;
        this.cacheMap = new CacheMap();
        cacheMap.resize(cache.memoryInfo().pageCount(),req -> flushCachePage(req.cachedPageIndex, req.persistentPageIndex));
        cacheMap.addListener(listeners::fire);
    }

    /**
     * Изменение размера кеша
     * @param pages кол-во страниц памяти
     */
    public void resizeCachePages(int pages){
        if(pages<1)throw new IllegalArgumentException("pages<1");
        writeLock(()->{
            if(pages > persistent.memoryInfo().pageCount())throw new IllegalArgumentException("pages > persistent.memoryInfo().pageCount()");
            cacheMap.writeLock(()->{
                cacheMap.resize(pages, ev -> {
                    flushCachePage(ev.cachedPageIndex, ev.persistentPageIndex);
                });
                cache.resizePages(pages);
            });
        });
    }

    /**
     * Изменение размера постоянной памяти
     * @param pages целевое кол-во страниц
     * @return как изменилось память
     */
    public ResizedPages resizePages(int pages){
        if(pages<1)throw new IllegalArgumentException("pages<1");
        return writeLock(()->{
            return cacheMap.writeLock(()->{
                var before = memoryInfo().clone();
                if( cacheMap.size()>pages ){
                    cacheMap.resize(pages, ev -> {
                        flushCachePage(ev.cachedPageIndex, ev.persistentPageIndex);
                    });
                    cache.resizePages(pages);
                }
                persistent.resizePages(pages);
                var after = memoryInfo().clone();
                return new ResizedPages(before,after);
            });
        });
    }

    private final CacheMap cacheMap;

    /**
     * Распределение страниц памяти
     * @return Распределение страниц памяти
     */
    public CacheMap getCacheMap(){ return cacheMap; }

    /**
     * Размер кеша
     * @return кол-во страниц
     */
    public int getCacheSize(){
        return cacheMap.size();
    }

    private final CACHEPAGES cache;

    /**
     * Кеш страниц паямти
     * @return Кеш страниц паямти
     */
    public CACHEPAGES getCache(){ return cache; }

    private final PERSISTPAGES persistent;

    /**
     * Страницы постоянной памяти
     * @return Страницы постоянной памяти
     */
    public PERSISTPAGES getPersistent(){ return persistent; }

    private void flushCachePage(int cachePage, int persistentPageIndex){
        persistent.writePage(persistentPageIndex,cache.readPage(cachePage));
        fire(new FlushCachePage(cachePage,persistentPageIndex));
    }

    /**
     * Информация о памяти (постоянной)
     * @return информация памяти
     */
    @Override
    public UsedPagesInfo memoryInfo() {
        return persistent.memoryInfo();
    }

    //#region events
    /**
     * Событие записи страницы из кеша в постоянную память
     */
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

    /**
     * Промах поиска страницы в кеше
     */
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

    /**
     * Успешный поиск страницы в памяти
     */
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

    /**
     * Странца из постоянной памяти загружена в кеш
     */
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

    /**
     * Измененна страница кеша
     */
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

    /**
     * Чтение страницы
     * @param page индекс страницы, от 0 и более
     * @return данные страницы
     */
    @Override
    public byte[] readPage(int page) {
        return readLock(()->{
            return readPersistentLock(page,()->{
                // 1 найти в кеше -> вернуть из кеша
                var fromCache = cacheMap.findPersistentPageForRead(page, cp -> {
                    return cp.readLock(()->{
                        fire(new CacheHit(page, true));
                        cp.markReads();
                        return cache.readPage(cp.cachePageIndex);
                    });
                });
                if (fromCache.isPresent()) return fromCache.get();

                fire(new CacheMiss(page, true));

                // 2 загрузить в кеш -> вернуть из кеша
                var result = new AtomicReference<byte[]>(null);
                cacheMap.allocate(
                    cp -> {
                        cp.writeLock(()->{
                            cp.unTarget();

                            var data = persistent.readPage(page);
                            fire(new PageLoaded(page, data));

                            cache.writePage(cp.cachePageIndex, data);
                            fire(new CacheWrote(page, cp.cachePageIndex, data));

                            cp.setDataSize(data.length);
                            cp.assignTarget(page);
                            cp.markMapped();
                            result.set(data);
                        });
                    },
                    fr -> {
                        fr.cachePage.readLock(()->{
                            flushCachePage(fr.cachedPageIndex, fr.persistentPageIndex);
                            fr.cachePage.markFlushed();
                        });
                    }
                );

                var bytes = result.get();
                if (bytes == null) throw new PageError("data not loaded, not allocated");

                return bytes;
            });
        });
    }

    /**
     * Запись страницы
     * @param page индекс страницы, от 0 и более
     * @param data2write массив байтов, размер не должен превышать {@link UsedPagesInfo#pageSize()}
     */
    @Override
    public void writePage(int page, byte[] data2write) {
        readLock(()->{
            writePersistentLock(page,()->{
                // 1 найти в кеше -> записать в кеш
                if (cacheMap.findPersistentPageForWrite(page, cachePage -> {
                    return cachePage.writeLock(()->{
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
                    });
                }).orElse(false)) return;

                fire(new CacheMiss(page, false));

                // 2 загрузить в кеш -> записать в кеш
                var allocated = new AtomicBoolean(false);
                cacheMap.allocate(
                    cp -> {
                        cp.writeLock(()->{
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
                        });
                    },
                    fr -> {
                        fr.cachePage.readLock(()->{
                            flushCachePage(fr.cachedPageIndex, fr.persistentPageIndex);
                            fr.cachePage.markFlushed();
                        });
                    }
                );

                if (!allocated.get()) {
                    throw new PageError("page not allocated in cache");
                }
            });
        });
    }

    /**
     * Атомарное изменение страницы
     * @param page индекс страницы
     * @param update функция обновления
     */
    @Override
    public void updatePage(int page, Fn1<byte[], byte[]> update) {
        if (update == null) throw new IllegalArgumentException("update==null");
        readLock(()->{
            writePersistentLock(page,()->{
                if (cacheMap.findPersistentPageForWrite(page, cp -> {
                    return cp.writeLock(()->{
                        fire(new CacheHit(page, false));

                        var cacheData = cache.readPage(cp.cachePageIndex);
                        cp.markReads();

                        var newData = update.apply(cacheData);
                        cache.writePage(cp.cachePageIndex, newData);
                        fire(new CacheWrote(page, cp.cachePageIndex, newData));
                        cp.markWrote();

                        return true;
                    });
                }).orElse(false)) {
                    return;
                }

                fire(new CacheMiss(page, false));

                var allocated = new AtomicBoolean(false);
                cacheMap.allocate(
                    cp -> {
                        cp.writeLock(()->{
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
                        });
                    },
                    fr -> {
                        fr.cachePage.readLock(()->{
                            flushCachePage(fr.cachedPageIndex, fr.persistentPageIndex);
                            fr.cachePage.markFlushed();
                        });
                    }
                );
                if (!allocated.get()) {
                    throw new PageError("page not allocated in cache");
                }
            });
        });
    }

    /**
     * Запись всех измененных страниц кеша в постоянную память
     */
    public void flush(){
        writeLock(()->{
            cacheMap.flush(ev -> {
                flushCachePage(ev.cachedPageIndex, ev.persistentPageIndex);
                ev.cachePage.markFlushed();
            });
        });
    }

    //#region persistentPageLocks
    private final Map<Integer,ReadWriteLock> persistentPageLocks = new HashMap<>();

    private List<ReadWriteLock> persistentLocks(int ... pages){
        var lockSet = new HashSet<ReadWriteLock>();
        synchronized (persistentPageLocks){
            for( var p : pages ){
                int persistentPageLocksMax = 64;
                lockSet.add(
                persistentPageLocks.computeIfAbsent(
                    p % persistentPageLocksMax,
                    x -> new ReentrantReadWriteLock()
                ));
            }
        }
        return new ArrayList<>(lockSet);
    }
    private List<ReadWriteLock> persistentLocks(int fromPage,int toPageExc){
        var lockSet = new HashSet<ReadWriteLock>();
        synchronized (persistentPageLocks){
            for( var p=Math.min(fromPage,toPageExc); p<Math.max(fromPage,toPageExc);p++ ){
                int persistentPageLocksMax = 64;
                lockSet.add(
                persistentPageLocks.computeIfAbsent(
                    p % persistentPageLocksMax,
                    x -> new ReentrantReadWriteLock()
                ));
            }
        }
        return new ArrayList<>(lockSet);
    }

    /**
     * Блокировка страницы постоянной памяти для чтения
     * @param page индекс страницы
     * @param code функция выполняемая в период блокировки
     * @return результат выполнения функции
     * @param <R> выполнения функции
     */
    public <R> R readPersistentLock(int page, Supplier<R> code) {
        if( code==null )throw new IllegalArgumentException("code==null");
        var locks = persistentLocks(page);
        try {
            locks.forEach(lck->lck.readLock().lock());
            return code.get();
        } finally {
            locks.forEach(lck->lck.readLock().unlock());
        }
    }

    /**
     * Блокировка страницы постоянной памяти для чтения
     * @param pages индекс страницы
     * @param code функция выполняемая в период блокировки
     * @return результат выполнения функции
     * @param <R> выполнения функции
     */
    public <R> R readPersistentLock(int[] pages, Supplier<R> code) {
        if( code==null )throw new IllegalArgumentException("code==null");
        var locks = persistentLocks(pages);
        try {
            locks.forEach(lck->lck.readLock().lock());
            return code.get();
        } finally {
            locks.forEach(lck->lck.readLock().unlock());
        }
    }

    /**
     * Блокировка страниц постоянной памяти для чтения
     * @param fromPage индекс страницы
     * @param toPageExc индекс страницы
     * @param code функция выполняемая в период блокировки
     * @return результат выполнения функции
     * @param <R> выполнения функции
     */
    public <R> R readPersistentLock(int fromPage,int toPageExc, Supplier<R> code) {
        if( code==null )throw new IllegalArgumentException("code==null");
        var locks = persistentLocks(fromPage, toPageExc);
        try {
            locks.forEach(lck->lck.readLock().lock());
            return code.get();
        } finally {
            locks.forEach(lck->lck.readLock().unlock());
        }
    }

    /**
     * Блокировка страницы постоянной памяти для записи
     * @param page индекс страницы
     * @param code функция выполняемая в период блокировки
     */
    public void writePersistentLock(int page, Runnable code) {
        if( code==null )throw new IllegalArgumentException("code==null");
        var locks = persistentLocks(page);
        try {
            locks.forEach(lck->lck.writeLock().lock());
            code.run();
        } finally {
            locks.forEach(lck->lck.writeLock().unlock());
        }
    }

    /**
     * Блокировка страницы постоянной памяти для записи
     * @param page индекс страницы
     * @param code функция выполняемая в период блокировки
     * @return результат выполнения функции
     * @param <R> результат выполнения функции
     */
    public <R> R writePersistentLock(int page, Supplier<R> code) {
        if( code==null )throw new IllegalArgumentException("code==null");
        var locks = persistentLocks(page);
        try {
            locks.forEach(lck->lck.writeLock().lock());
            return code.get();
        } finally {
            locks.forEach(lck->lck.writeLock().unlock());
        }
    }

    /**
     * Блокировка страницы постоянной памяти для записи
     * @param pages индекс страницы
     * @param code функция выполняемая в период блокировки
     * @return результат выполнения функции
     */
    public void writePersistentLock(int[] pages, Runnable code) {
        if( code==null )throw new IllegalArgumentException("code==null");
        var locks = persistentLocks(pages);
        try {
            locks.forEach(lck->lck.writeLock().lock());
            code.run();
        } finally {
            locks.forEach(lck->lck.writeLock().unlock());
        }
    }

    /**
     * Блокировка страницы постоянной памяти для записи
     * @param fromPage индекс страницы
     * @param toPageExc индекс страницы
     * @param code функция выполняемая в период блокировки
     * @return результат выполнения функции
     */
    public void writePersistentLock(int fromPage,int toPageExc, Runnable code) {
        if( code==null )throw new IllegalArgumentException("code==null");
        var locks = persistentLocks(fromPage, toPageExc);
        try {
            locks.forEach(lck->lck.writeLock().lock());
            code.run();
        } finally {
            locks.forEach(lck->lck.writeLock().unlock());
        }
    }

    /**
     * Блокировка страницы постоянной памяти для записи
     * @param pages индекс страницы
     * @param code функция выполняемая в период блокировки
     * @return результат выполнения функции
     * @param <R> результат выполнения функции
     */
    public <R> R writePersistentLock(int[] pages, Supplier<R> code) {
        if( code==null )throw new IllegalArgumentException("code==null");
        var locks = persistentLocks(pages);
        try {
            locks.forEach(lck->lck.writeLock().lock());
            return code.get();
        } finally {
            locks.forEach(lck->lck.writeLock().unlock());
        }
    }
    //#endregion
    //#region read/write lock
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * Блокировка для чтения
     * @param code функция выполняемая в период блокировки
     * @return результат выполнения функции
     * @param <R> результат выполнения функции
     */
    public <R> R readLock(Supplier<R> code){
        if( code==null )throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.readLock().lock();
            return code.get();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Блокировка для чтения
     * @param code функция выполняемая в период блокировки
     */
    public void readLock(Runnable code){
        if( code==null )throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.readLock().lock();
            code.run();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Блокировка для записи
     * @param code функция выполняемая в период блокировки
     * @return результат выполнения функции
     * @param <R> результат выполнения функции
     */
    public <R> R writeLock(Supplier<R> code){
        if( code==null )throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.writeLock().lock();
            return code.get();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Блокировка для записи
     * @param code функция выполняемая в период блокировки
     */
    public void writeLock(Runnable code){
        if( code==null )throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.writeLock().lock();
            code.run();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
    //#endregion
}
