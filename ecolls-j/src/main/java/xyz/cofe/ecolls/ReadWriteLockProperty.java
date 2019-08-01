package xyz.cofe.ecolls;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public interface ReadWriteLockProperty extends GetReadLock, GetWriteLock {
    default Lock getReadLock() {
        return ReadWriteLockPropImpl.getReadLock(this);
    }

    default Lock getWriteLock() {
        return ReadWriteLockPropImpl.getWriteLock(this);
    }

    default ReadWriteLock getReadWriteLock() {
        return ReadWriteLockPropImpl.getRWLockOf(this);
    }

    default void setReadWriteLock(ReadWriteLock rwLock) {
        ReadWriteLockPropImpl.setRWLock(this, rwLock);
    }
}
