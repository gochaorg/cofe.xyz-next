package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Fn1;
import xyz.cofe.fn.Tuple2;

import java.util.*;
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
        readWriteLock = new ReentrantReadWriteLock();
    }

    private final List<CachePage> cachePages = new ArrayList<>();

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

    //#region readLock/writeLock
    private final ReadWriteLock readWriteLock;
    public ReadWriteLock getReadWriteLock(){ return readWriteLock; }

    public <R> R readLock(Supplier<R> code){
        if(code==null)throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.readLock().lock();
            return code.get();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void readLock(Runnable code){
        if(code==null)throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.readLock().lock();
            code.run();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public <R> R writeLock(Supplier<R> code){
        if(code==null)throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.writeLock().lock();
            return code.get();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void writeLock(Runnable code){
        if(code==null)throw new IllegalArgumentException("code==null");
        try {
            readWriteLock.writeLock().lock();
            code.run();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
    //#endregion
    //#region size()/resize()
    /**
     * Получение кол-ва страниц выделенных под кеш
     * @return кол-во страниц
     */
    public int size(){
        return readLock(cachePages::size);
    }

    private final PageListener popupEvent = listeners::fire;

    /**
     * Изменение кол-ва страниц выделенных под кеш
     * @param newSize кол-во страниц
     * @param flushing запрос на сохранение страницы
     */
    public void resize(int newSize, Consumer<FlushRequest> flushing){
        if(newSize<0) throw new IllegalArgumentException("newSize<0");
        if(flushing==null) throw new IllegalArgumentException("flushing==null");

        writeLock(()->{
            if( cachePages.size()==newSize )return;
            if( cachePages.size()<newSize ){
                var extend = newSize - cachePages.size();
                var fromIdx = cachePages.size();
                for( int i=0;i<extend;i++ ){
                    var cachePage = new CachePage(fromIdx+i);
                    cachePage.addListener(popupEvent);
                    cachePages.add(cachePage);
                }
            }else{
                var reduceSize = cachePages.size() - newSize;
                var reduceList = new ArrayList<Tuple2<Integer,CachePage>>(reduceSize);
                for( int i=cachePages.size()-1; i>=0; i-- ){
                    reduceList.add(Tuple2.of(i,cachePages.get(i)));
                }
                try {
                    // lock pages
                    for (var cp : reduceList) {
                        cp.b().getReadWriteLock().writeLock().lock();
                    }

                    for (var cp:reduceList) {
                        if( cp.b().isDirty() && cp.b().getTarget().isPresent() ){
                            flushing.accept(new FlushRequest(cp.a(), cp.b()));
                        }
                    }

                    for(var cp:reduceList) {
                        cachePages.remove(cp.a().intValue());
                        cp.b().removeListener(popupEvent);
                    }
                } finally {
                    // un lock pages
                    for (var cp : reduceList) {
                        cp.b().getReadWriteLock().writeLock().unlock();
                    }
                }
            }
        });
    }
    //#endregion
    //#region find()
    public class Find {
        public final Predicate<CachePage> what;

        public Find(Predicate<CachePage> what) {
            this.what = what;
        }

        public <R> Optional<R> forWriteOnce(Fn1<CachePage,R> update){
            return readLock(()->{
                for( var cp : cachePages ) {
                    var res = cp.writeLock(()->{
                        if( what.test(cp) ){
                            return Optional.of(update.apply(cp));
                        }else{
                            return Optional.<R>empty();
                        }
                    });
                    if( res.isPresent() )return res;
                }
                return Optional.empty();
            });
        }

        public <R> Optional<R> forReadOnce(Fn1<CachePage,R> reading){
            return readLock(()->{
                for( var cp : cachePages ) {
                    var res = cp.readLock(()->{
                        if( what.test(cp) ){
                            return Optional.of(reading.apply(cp));
                        }else{
                            return Optional.<R>empty();
                        }
                    });
                    if( res.isPresent() )return res;
                }
                return Optional.empty();
            });
        }

        public Found go( Fn1<CachePage,LockMethod> locking ) {
            if(locking==null)throw new IllegalArgumentException("locking==null");
            var pages = new HashMap<CachePage,Runnable>();
            var found = new Found(pages);

            readLock(()->{
                for( var cp : cachePages ) {
                    if( what.test(cp) ){
                        locking.apply(cp).lock(cp.getReadWriteLock()).ifPresent( lck -> {
                            if( what.test(cp) ){
                                var unlockCalled = new AtomicBoolean(false);
                                Runnable release = ()->{
                                    synchronized (unlockCalled) {
                                        if (!unlockCalled.get()) {
                                            lck.unlock();
                                            unlockCalled.set(true);
                                        }
                                    }
                                };
                                pages.put(cp,release);
                            }
                        });
                    }
                }
            });

            return found;
        }
    }

    public static class Found {
        public final Map<CachePage,Runnable> pages;
        public Found(Map<CachePage,Runnable> pages) {
            this.pages = pages;
        }

        public void release(){
            pages.forEach((p,r)->r.run());
        }

        public List<CachePage> list(){
            return new ArrayList<>(pages.keySet());
        }
    }

    public interface LockMethod {
        public Optional<Lock> lock(ReadWriteLock lock);
        public static LockMethod readLock(){
            return (lck) -> {
                lck.readLock().lock();
                return Optional.of(lck.readLock());
            };
        }
        public static LockMethod writeLock(){
            return (lck) -> {
                lck.writeLock().lock();
                return Optional.of(lck.writeLock());
            };
        }
        public static LockMethod tryReadLock(){
            return lck -> {
                if( lck.readLock().tryLock() ){
                    return Optional.of(lck.readLock());
                }
                return Optional.empty();
            };
        }
        public static LockMethod tryReadLock(long time, TimeUnit timeUnit){
            return lck -> {
                try {
                    if (lck.readLock().tryLock(time, timeUnit)) {
                        return Optional.of(lck.readLock());
                    }
                    return Optional.empty();
                } catch (InterruptedException ex){
                    return Optional.empty();
                }
            };
        }
        public static LockMethod tryWriteLock(){
            return lck -> {
                if( lck.writeLock().tryLock() ){
                    return Optional.of(lck.writeLock());
                }
                return Optional.empty();
            };
        }
        public static LockMethod tryWriteLock(long time, TimeUnit timeUnit){
            return lck -> {
                try {
                    if( lck.writeLock().tryLock(time, timeUnit) ){
                        return Optional.of(lck.writeLock());
                    }
                    return Optional.empty();
                } catch (InterruptedException ex){
                    return Optional.empty();
                }
            };
        }
    }

    public Find find(Predicate<CachePage> what){
        if( what==null )throw new IllegalArgumentException("what==null");
        return new Find(what);
    }
    //#endregion
    //#region findPersistentPageForRead
    public static class FindPersistentPageForRead<R> implements PageEvent {
        public final int persistentPage;
        public final CachePage page;
        public final R result;

        public FindPersistentPageForRead(int persistentPage, CachePage page, R result) {
            this.persistentPage = persistentPage;
            this.page = page;
            this.result = result;
        }
    }

    public <R> Optional<R> findPersistentPageForRead(int persistentPage, Fn1<CachePage,R> process) {
        if( process==null )throw new IllegalArgumentException("process==null");
        return find(cp->cp.getTarget().map(t->t==persistentPage).orElse(false))
            .forReadOnce( cp -> {
                var r = process.apply(cp);
                fire(new FindPersistentPageForRead<R>(persistentPage, cp, r));
                return r;
            });
    }
    //#endregion
    //#region findPersistentPageForWrite
    public static class findPersistentPageForWrite<R> implements PageEvent {
        public final int persistentPage;
        public final CachePage page;
        public final R result;

        public findPersistentPageForWrite(int persistentPage, CachePage page, R result) {
            this.persistentPage = persistentPage;
            this.page = page;
            this.result = result;
        }
    }

    public <R> Optional<R> findPersistentPageForWrite(int persistentPage, Fn1<CachePage,R> process) {
        if( process==null )throw new IllegalArgumentException("process==null");
        return find(cp->cp.getTarget().map(t->t==persistentPage).orElse(false))
            .forWriteOnce(cp -> {
                var r = process.apply(cp);
                fire(new findPersistentPageForWrite<R>(persistentPage, cp, r));
                return r;
            });
    }
    //#endregion

    //#region allocation
    public static class AllocatedUnmapped implements PageEvent {
        public final CachePage page;

        public AllocatedUnmapped(CachePage page) {
            this.page = page;
        }
    }

    public static class AllocatedDirty implements PageEvent {
        public final CachePage page;

        public AllocatedDirty(CachePage page) {
            this.page = page;
        }
    }

    public static class AllocatedCleaned implements PageEvent {
        public final CachePage page;

        public AllocatedCleaned(CachePage page) {
            this.page = page;
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
    public void allocate(Consumer<CachePage> consumer, Consumer<FlushRequest> flushing){
        if(consumer==null)throw new IllegalArgumentException("consumer==null");
        if(flushing==null)throw new IllegalArgumentException("flushing==null");
        readLock(()->{
            // 1 первая не размеченная страница
            var unmapped = findUnmapped();
            if( !unmapped.pages.isEmpty() ){
                var en = unmapped.pages.entrySet().iterator().next();
                fire(new AllocatedUnmapped(en.getKey()));
                consumer.accept(en.getKey());
                unmapped.release();
                return;
            }

            // 2 любая грязная страница
            var dirtyPages = findDirty();
            if( !dirtyPages.pages.isEmpty() ){
                var lst = dirtyPages.list();
                if( lst.size()==1 ){
                    var cp = lst.get(0);
                    fire(new AllocatedDirty(cp));
                    flushing.accept(new FlushRequest(cp.getTarget().get(), cp));
                    consumer.accept(cp);
                    dirtyPages.release();
                }else{
                    var cp = lst.get(ThreadLocalRandom.current().nextInt(lst.size()));
                    fire(new AllocatedDirty(cp));
                    flushing.accept(new FlushRequest(cp.getTarget().get(), cp));
                    consumer.accept(cp);
                    dirtyPages.release();
                }
                return;
            }

            // 3 любая страница
            if( cachePages.size()>0 ){
                var cp = cachePages.get(ThreadLocalRandom.current().nextInt(cachePages.size()));
                cp.writeLock(()->{
                    if( cp.getTarget().isPresent() && cp.isDirty() ){
                        flushing.accept(new FlushRequest(cp.getTarget().get(), cp));
                    }
                    fire(new AllocatedCleaned(cp));
                    consumer.accept(cp);
                });
            }
        });
    }

    private Found findUnmapped(){
        return find(cp -> cp.getTarget().isEmpty()).go(cp->LockMethod.writeLock());
    }

    private Found findDirty(){
        return find(cp -> cp.getTarget().isPresent() && cp.isDirty()).go(cp->LockMethod.writeLock());
    }
    //#endregion

    public void flush( Consumer<FlushRequest> flushing ){
        if(flushing==null)throw new IllegalArgumentException("flushing==null");
        readLock(()->{
            var dirtyPages = findDirty();
            try {
                dirtyPages.list().forEach( cp -> {
                    if( cp.isDirty() && cp.getTarget().isPresent() ){
                        flushing.accept(new FlushRequest(cp.getTarget().get(), cp));
                    }
                });
            } finally {
                dirtyPages.release();
            }
        });
    }

    public List<CachePage> dirtyPages(){
        var list = new ArrayList<CachePage>();
        readLock(()->{
            for( var cp : cachePages ){
                if( cp.isDirty() && cp.getTarget().isPresent() ){
                    list.add(cp);
                }
            }
        });
        return list;
    }
}
