package xyz.cofe.ecolls;

import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Поддержка реализации {@link ReadWriteLockProperty}
 */
public class ReadWriteLockPropImpl {
    /**
     * Карта объектов ReadWriteLock
     */
    public static final WeakHashMap<Object, ReadWriteLock> rwLocks = new WeakHashMap<>();

    /**
     * Получение экземпляра ReadWriteLock для указанного объекта
     * @param inst объект
     * @return ReadWriteLock экземпляр
     */
    public static ReadWriteLock getRWLockOf(Object inst){
        if( inst==null )return null;
        synchronized (rwLocks){
            return rwLocks.computeIfAbsent(inst,x->new ReentrantReadWriteLock());
        }
    }

    /**
     * Указание экземпляра ReadWriteLock
     * @param inst объект
     * @param rwLock ReadWriteLock блокировки
     */
    public static void setRWLock(Object inst,ReadWriteLock rwLock){
        if( inst==null )return;
        synchronized (rwLocks){
            if( rwLock==null ) {
                rwLocks.remove(inst);
            }else {
                rwLocks.put(inst, rwLock);
            }
        }
    }

    /**
     * Получение блокировки чтения
     * @param inst объект
     * @return блокировка чтения
     */
    public static Lock getReadLock(Object inst){
        if( inst==null )return null;
        return getRWLockOf(inst).readLock();
    }

    /**
     * Получение блокировки записи
     * @param inst объект
     * @return блокировка записи
     */
    public static Lock getWriteLock(Object inst){
        if( inst==null )return null;
        return getRWLockOf(inst).writeLock();
    }
}
