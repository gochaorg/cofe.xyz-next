package xyz.cofe.cbuffer.page;

import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class CachePage {
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

    public CachePage(int cachePageIndex){
        this.cachePageIndex = cachePageIndex;
    }

    public final int cachePageIndex;

    public volatile Integer target;
    public synchronized Optional<Integer> getTarget(){
        var t = target;
        return t!=null ? Optional.of(t) : Optional.empty();
    }

    public interface CachePageEvent extends PageEvent {
        CachePage page();
    }

    //#region AssignTarget
    public static class AssignTarget implements CachePageEvent {
        public final CachePage page;
        public CachePage page(){ return page; }

        public AssignTarget(CachePage page) {
            this.page = page;
        }

        @Override
        public String toString() {
            return "AssignTarget{" +
                "page=" + page +
                '}';
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public synchronized Optional<Integer> assignTarget(int target){
        var old = getTarget();
        this.target = target;
        fire(new AssignTarget(this));
        return old;
    }
    //#endregion
    //#region UnTarget
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class UnTarget implements CachePageEvent {
        public final CachePage page;
        public CachePage page(){ return page; }

        public final Optional<Integer> persistentPageIndex;

        public UnTarget(CachePage page, Optional<Integer> persistentPageIndex) {
            this.page = page;
            this.persistentPageIndex = persistentPageIndex;
        }

        @Override
        public String toString() {
            return "UnTarget{" +
                "page=" + page +
                ", persistentPageIndex=" + persistentPageIndex +
                '}';
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public synchronized Optional<Integer> unTarget(){
        var old = getTarget();
        this.target = null;
        this.dirty = false;
        fire(new UnTarget(this, old));
        return old;
    }
    //#endregion
    //#region MarkMapped
    public static class MarkMapped implements CachePageEvent {
        public final CachePage page;
        public CachePage page(){ return page; }

        public MarkMapped(CachePage page) {
            this.page = page;
        }

        @Override
        public String toString() {
            return "MarkMapped{" +
                "page=" + page +
                '}';
        }
    }

    public void markMapped() {
        dirty = false;
        fire(new MarkMapped(this));
    }
    //#endregion
    //#region MarkReads
    public static class MarkReads implements CachePageEvent {
        public final CachePage page;
        public CachePage page(){ return page; }

        public MarkReads(CachePage page) {
            this.page = page;
        }

        @Override
        public String toString() {
            return "MarkReads{" +
                "page=" + page +
                '}';
        }
    }

    public void markReads() {
        fire(new MarkReads(this));
    }
    //#endregion
    //#region MarkWrote
    public static class MarkWrote implements CachePageEvent {
        public final CachePage page;
        public CachePage page(){ return page; }

        public MarkWrote(CachePage page) {
            this.page = page;
        }

        @Override
        public String toString() {
            return "MarkWrote{" +
                "page=" + page +
                '}';
        }
    }

    public void markWrote() {
        dirty = true;
        fire(new MarkWrote(this));
    }
    //#endregion
    //#region MarkFlushed
    public static class MarkFlushed implements CachePageEvent {
        public final CachePage page;
        public CachePage page(){ return page; }

        public MarkFlushed(CachePage page) {
            this.page = page;
        }

        @Override
        public String toString() {
            return "MarkFlushed{" +
                "page=" + page +
                '}';
        }
    }

    public void markFlushed() {
        dirty = false;
        fire(new MarkFlushed(this));
    }
    //#endregion MarkFlushed

    boolean hasTarget(){
        return
            //readLock(()->
            target!=null;
            //);
    }

    private volatile boolean dirty;
    public boolean isDirty(){
        return dirty;
    }

    //#region data size
    private volatile Integer dataSize;
    public Optional<Integer> getDataSize(){
        return //readLock(()->
            dataSize!=null ? Optional.of(dataSize) : Optional.empty()
        //)
        ;
    }
    public void resetDataSize(){
        dataSize = null;
    }
    public static class SetDataSize implements CachePageEvent {
        public final CachePage page;
        public CachePage page(){ return page; }

        public SetDataSize(CachePage page) {
            this.page = page;
        }

        @Override
        public String toString() {
            return "SetDataSize{" +
                "page=" + page +
                '}';
        }
    }
    public void setDataSize(int dataSize){
        this.dataSize = dataSize;
        fire(new SetDataSize(this));
    }
    //#endregion

    @Override
    public String toString() {
        return "CachePage { idx="+cachePageIndex+", target="+target+", dirty="+dirty+", dataSize="+dataSize+" }";
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
