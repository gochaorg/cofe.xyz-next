package xyz.cofe.ecolls;

import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockPropImpl {
    public static final WeakHashMap<Object, ReadWriteLock> rwLocks = new WeakHashMap<>();
    public static ReadWriteLock getRWLockOf(Object inst){
        if( inst==null )return null;
        synchronized (rwLocks){
            return rwLocks.computeIfAbsent(inst,x->new ReentrantReadWriteLock());
        }
    }
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

    public static Lock getReadLock(Object inst){
        if( inst==null )return null;
        return getRWLockOf(inst).readLock();
    }

    public static Lock getWriteLock(Object inst){
        if( inst==null )return null;
        return getRWLockOf(inst).writeLock();
    }
}
