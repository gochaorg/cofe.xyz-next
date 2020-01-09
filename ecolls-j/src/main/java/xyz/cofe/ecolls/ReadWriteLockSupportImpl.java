package xyz.cofe.ecolls;

import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

public class ReadWriteLockSupportImpl {
    public static <T> T lock( ReadWriteLockSupport inst, Lock lock, Supplier<T> syncBlock){
        if( lock!=null ){
            try {
                lock.lock();
                return syncBlock.get();
            } finally {
                lock.unlock();
            }
        }
        return syncBlock.get();
    }
    public static void lock(ReadWriteLockSupport inst, Lock lock, Runnable syncBlock){
        if( lock!=null ){
            try {
                lock.lock();
                syncBlock.run();
                return;
            } finally {
                lock.unlock();
            }
        }
        syncBlock.run();
    }
}
