package xyz.cofe.ecolls;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Свойства readLock, writeLock, readWriteLock
 */
public interface ReadWriteLockProperty extends GetReadLock, GetWriteLock {
    default Lock getReadLock() {
        return ReadWriteLockPropImpl.getReadLock(this);
    }

    default Lock getWriteLock() {
        return ReadWriteLockPropImpl.getWriteLock(this);
    }

    /**
     * Возвращает объект ReadWriteLock
     * @return RW блокировки
     */
    default ReadWriteLock getReadWriteLock() {
        return ReadWriteLockPropImpl.getRWLockOf(this);
    }

    /**
     * Указывает блокировки RW
     * @param rwLock RW блокировки
     */
    default void setReadWriteLock(ReadWriteLock rwLock) {
        ReadWriteLockPropImpl.setRWLock(this, rwLock);
    }
}
