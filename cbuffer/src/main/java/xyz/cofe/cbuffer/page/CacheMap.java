package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Consumer2;
import xyz.cofe.fn.Fn1;
import xyz.cofe.fn.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CacheMap {

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

        public FlushRequest(int cachedPageIndex, int persistentPageIndex, CachePage cachePage) {
            this.cachedPageIndex = cachedPageIndex;
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
                for( int i=0;i<extend;i++ ){
                    cachePages.add(new CachePage());
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
                            flushing.accept(new FlushRequest(cp.a(), cp.b().getTarget().get(), cp.b()));
                        }
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

        public <R> Optional<R> forUpdate(Fn1<CachePage,R> update){
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

        public <R> Optional<R> forRead(Fn1<CachePage,R> reading){
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
    }

    public Find find(Predicate<CachePage> what){
        if( what==null )throw new IllegalArgumentException("what==null");
        return new Find(what);
    }
    //#endregion

    public static class AllocatedPage {
        public final int index;
        public final CachePage page;

        public AllocatedPage(int index, CachePage page) {
            this.index = index;
            this.page = page;
        }
    }

    /**
     * Выделение свободной страницы
     * @param consumer получтель страницы
     */
    public void allocate(Consumer<AllocatedPage> consumer){
        if(consumer==null)throw new IllegalArgumentException("consumer==null");

    }
}
