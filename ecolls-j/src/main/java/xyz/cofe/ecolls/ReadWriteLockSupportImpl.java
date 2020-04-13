package xyz.cofe.ecolls;

import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * Поддержка выполнения ReadWriteLockSupport
 */
public class ReadWriteLockSupportImpl {
    /**
     * Выполняет код
     * @param inst экземпляр ReadWriteLockSupport, может быть null
     * @param lock блокировка, может быть null
     * @param syncBlock код
     * @param <T> тип результата
     * @return результат выполнения кода
     */
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

    /**
     * Выполняет код
     * @param inst экземпляр ReadWriteLockSupport,  может быть null
     * @param lock блокировка, может быть null
     * @param syncBlock код
     */
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
