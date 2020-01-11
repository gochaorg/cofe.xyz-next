package xyz.cofe.ecolls;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

public interface ReadWriteLockSupport extends GetReadLock, GetWriteLock {
    default Lock getReadLock() { return getWriteLock(); }
    default Lock getWriteLock(){ return null; }

//    private <T> T lock(Lock lock, Supplier<T> syncBlock){
//        if( lock!=null ){
//            try {
//                lock.lock();
//                return syncBlock.get();
//            } finally {
//                lock.unlock();
//            }
//        }
//        return syncBlock.get();
//    }
//    private void lock(Lock lock, Runnable syncBlock){
//        if( lock!=null ){
//            try {
//                lock.lock();
//                syncBlock.run();
//                return;
//            } finally {
//                lock.unlock();
//            }
//        }
//        syncBlock.run();
//    }
    default <T> T readLock(Supplier<T> syncBlock){
        Objects.requireNonNull(syncBlock);
        return ReadWriteLockSupportImpl.lock(this, getReadLock(), syncBlock);
    }
    default void readLock(Runnable syncBlock){
        Objects.requireNonNull(syncBlock);
        ReadWriteLockSupportImpl.lock(this, getReadLock(), syncBlock);
    }
    default <T> T writeLock(Supplier<T> syncBlock){
        Objects.requireNonNull(syncBlock);
        return ReadWriteLockSupportImpl.lock(this, getWriteLock(),syncBlock);
    }
    default void writeLock(Runnable syncBlock){
        Objects.requireNonNull(syncBlock);
        ReadWriteLockSupportImpl.lock(this,getWriteLock(),syncBlock);
    }
}
