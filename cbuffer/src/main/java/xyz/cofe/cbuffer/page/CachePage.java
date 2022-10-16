package xyz.cofe.cbuffer.page;

import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class CachePage {
    public CachePage(){
        readWriteLock = new ReentrantReadWriteLock();
    }

    protected volatile Integer target;
    public Optional<Integer> getTarget(){
        //return readLock(()->{
            var t = target;
            return t!=null ? Optional.of(t) : Optional.empty();
        //});
    }

    public Optional<Integer> assignTarget(int target){
        //return writeLock(()->{
            var old = getTarget();
            this.target = target;
            return old;
        //});
    }

    public Optional<Integer> unTarget(){
        //return writeLock(()-> {
            var old = getTarget();
            this.target = null;
            this.dirty = false;
            return old;
        //});
    }

    public void markMapped() {
        //writeLock(()-> {
            dirty = false;
        //});
    }

    public void markReads() {
        //writeLock(()->{});
    }

    public void markWrote() {
        //writeLock(()->{
            dirty = true;
        //});
    }
    public void markFlushed() {
        //writeLock(()->{
            dirty = false;
        //});
    }

    boolean hasTarget(){
        return
            //readLock(()->
            target!=null;
            //);
    }

    private volatile boolean dirty;
    public boolean isDirty(){
        //return readLock(()->{
            return dirty;
        //});
    }

    private volatile Integer dataSize;
    public Optional<Integer> getDataSize(){
        return //readLock(()->
            dataSize!=null ? Optional.of(dataSize) : Optional.empty()
        //)
        ;
    }
    public void resetDataSize(){
        //writeLock(()->{
            dataSize = null;
        //});
    }
    public void setDataSize(int dataSize){
        //writeLock(()->{
            this.dataSize = dataSize;
        //});
    }

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
}
