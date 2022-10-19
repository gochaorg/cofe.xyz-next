package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Fn1;
import xyz.cofe.fn.Tuple2;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CacheMap {
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

    public CacheMap(){
    }

    public final List<CachePage> cachePages = new ArrayList<>();

    //#region FlushRequest
    /**
     * Запрос на сохранение страницы
     */
    public static class FlushRequest {
        public final int cachedPageIndex;
        public final int persistentPageIndex;
        public final CachePage cachePage;

        public FlushRequest(int persistentPageIndex, CachePage cachePage) {
            this.cachedPageIndex = cachePage.cachePageIndex;
            this.persistentPageIndex = persistentPageIndex;
            this.cachePage = cachePage;
        }
    }
    //#endregion

    //#region size()/resize()
    /**
     * Получение кол-ва страниц выделенных под кеш
     * @return кол-во страниц
     */
    public int size(){
        return cachePages.size();
    }

    private final PageListener popupEvent = listeners::fire;

    private final PageListener mapPersistent2cache = event -> {
//        if( event instanceof CachePage.UnTarget ){
//        }
    };

    /**
     * Изменение кол-ва страниц выделенных под кеш
     * @param newSize кол-во страниц
     * @param flushing запрос на сохранение страницы
     */
    public void resize(int newSize, Consumer<FlushRequest> flushing){
        if(newSize<0) throw new IllegalArgumentException("newSize<0");
        if(flushing==null) throw new IllegalArgumentException("flushing==null");

        if( cachePages.size()==newSize )return;
        if( cachePages.size()<newSize ){
            var extend = newSize - cachePages.size();
            var fromIdx = cachePages.size();
            for( int i=0;i<extend;i++ ){
                var cachePage = new CachePage(fromIdx+i);
                cachePage.addListener(popupEvent);
                cachePage.addListener(mapPersistent2cache);
                cachePages.add(cachePage);
            }
        }else{
            var reduceSize = cachePages.size() - newSize;
            var reduceList = new ArrayList<Tuple2<Integer,CachePage>>(reduceSize);
            for( int i=cachePages.size()-1; i>=0; i-- ){
                reduceList.add(Tuple2.of(i,cachePages.get(i)));
            }

            for (var cp:reduceList) {
                if( cp.b().isDirty() && cp.b().getTarget().isPresent() ){
                    flushing.accept(new FlushRequest(cp.a(), cp.b()));
                }
                cachePages.remove(cp.a().intValue());
                cp.b().removeListener(popupEvent);
                cp.b().removeListener(mapPersistent2cache);
            }
        }
    }
    //#endregion
    //#region find()
    public class Find {
        private void log(String message){
            System.out.println("[TH:"+Thread.currentThread().getId()+"] "+message);
        }

        public final Predicate<CachePage> what;

        public Find(Predicate<CachePage> what) {
            this.what = what;
        }

        public <R> Optional<R> forWriteOnce(Fn1<CachePage,R> update){
            synchronized (CacheMap.this) {
                for (var cp : cachePages) {
                    if (what.test(cp)) {
                        return Optional.of(update.apply(cp));
                    }
                }
                return Optional.empty();
            }
        }

        public <R> Optional<R> forReadOnce(Fn1<CachePage,R> reading){
            synchronized (CacheMap.this) {
                for (var cp : cachePages) {
                    if (what.test(cp)) {
                        return Optional.of(reading.apply(cp));
                    }
                }
                return Optional.empty();
            }
        }

        public List<CachePage> go() {
            synchronized (CacheMap.this) {
                var pages = new ArrayList<CachePage>();
                for (var cp : cachePages) {
                    if (what.test(cp)) {
                        pages.add(cp);
                    }
                }
                return pages;
            }
        }
    }

    public synchronized Find find(Predicate<CachePage> what){
        if( what==null )throw new IllegalArgumentException("what==null");
        return new Find(what);
    }
    //#endregion
    //#region findPersistentPageForRead
    public synchronized <R> Optional<R> findPersistentPageForRead(int persistentPage, Fn1<CachePage,R> process) {
        if( process==null )throw new IllegalArgumentException("process==null");
        for( var cp: cachePages ){
            if( Objects.equals(cp.target, (Integer)persistentPage) ){
                return Optional.of(process.apply(cp));
            }
        }
        return Optional.empty();
    }
    //#endregion
    //#region findPersistentPageForWrite
    public synchronized  <R> Optional<R> findPersistentPageForWrite(int persistentPage, Fn1<CachePage,R> process) {
        if( process==null )throw new IllegalArgumentException("process==null");
        for( var cp:cachePages ){
            if(Objects.equals(cp.target, (Integer)persistentPage)){
                return Optional.of(process.apply(cp));
            }
        }
        return Optional.empty();
    }
    //#endregion

    //#region allocation
    public static class AllocatedUnmapped implements PageEvent {
        public final CachePage page;

        public AllocatedUnmapped(CachePage page) {
            this.page = page;
        }

        @Override
        public String toString() {
            return "AllocatedUnmapped{" +
                "page=" + page +
                '}';
        }
    }

    public static class AllocatedDirty implements PageEvent {
        public final CachePage page;

        public AllocatedDirty(CachePage page) {
            this.page = page;
        }

        @Override
        public String toString() {
            return "AllocatedDirty{" +
                "page=" + page +
                '}';
        }
    }

    public static class AllocatedCleaned implements PageEvent {
        public final CachePage page;

        public AllocatedCleaned(CachePage page) {
            this.page = page;
        }

        @Override
        public String toString() {
            return "AllocatedCleaned{" +
                "page=" + page +
                '}';
        }
    }

    /**
     * Выделение свободной страницы
     * 1 первая не размеченная страница
     * 2 любая грязная страница
     * 3 любая страница
     * @param consumer получтель страницы
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public synchronized void allocate(Consumer<CachePage> consumer, Consumer<FlushRequest> flushing){
        if(consumer==null)throw new IllegalArgumentException("consumer==null");
        if(flushing==null)throw new IllegalArgumentException("flushing==null");
        synchronized (this) {
            // 1 первая не размеченная страница
            var unmapped = findUnmapped();
            if (!unmapped.isEmpty()) {
                var en = unmapped.iterator().next();
                fire(new AllocatedUnmapped(en));
                consumer.accept(en);
                return;
            }

            // 2 любая грязная страница
            var dirtyPages = findDirty();
            if (!dirtyPages.isEmpty()) {
                if (dirtyPages.size() == 1) {
                    var cp = dirtyPages.get(0);
                    fire(new AllocatedDirty(cp));
                    flushing.accept(new FlushRequest(cp.getTarget().get(), cp));
                    consumer.accept(cp);
                } else {
                    var cp = dirtyPages.get(ThreadLocalRandom.current().nextInt(dirtyPages.size()));
                    fire(new AllocatedDirty(cp));
                    flushing.accept(new FlushRequest(cp.getTarget().get(), cp));
                    consumer.accept(cp);
                }
                return;
            }

            // 3 любая страница
            if (cachePages.size() > 0) {
                var cp = cachePages.get(ThreadLocalRandom.current().nextInt(cachePages.size()));
                if (cp.getTarget().isPresent() && cp.isDirty()) {
                    flushing.accept(new FlushRequest(cp.getTarget().get(), cp));
                }
                fire(new AllocatedCleaned(cp));
                consumer.accept(cp);
            }
        }
    }

    private synchronized List<CachePage> findUnmapped(){
        return find(cp -> cp.getTarget().isEmpty()).go();
    }

    private synchronized List<CachePage> findDirty(){
        return find(cp -> cp.getTarget().isPresent() && cp.isDirty()).go();
    }
    //#endregion

    public synchronized void flush( Consumer<FlushRequest> flushing ){
        if(flushing==null)throw new IllegalArgumentException("flushing==null");
        var dirtyPages = findDirty();
        dirtyPages.forEach( cp -> {
            if( cp.isDirty() && cp.getTarget().isPresent() ){
                flushing.accept(new FlushRequest(cp.getTarget().get(), cp));
            }
        });
    }

    public synchronized List<CachePage> dirtyPages(){
        var list = new ArrayList<CachePage>();
            for( var cp : cachePages ){
                if( cp.isDirty() && cp.getTarget().isPresent() ){
                    list.add(cp);
                }
            }
        return list;
    }

    //#region read/write lock
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public <R> R readLock(Supplier<R> code){
        if( code==null )throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.readLock().lock();
            return code.get();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
    public void readLock(Runnable code){
        if( code==null )throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.readLock().lock();
            code.run();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
    public <R> R writeLock(Supplier<R> code){
        if( code==null )throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.writeLock().lock();
            return code.get();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
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
