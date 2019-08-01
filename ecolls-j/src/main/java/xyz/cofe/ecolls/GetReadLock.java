package xyz.cofe.ecolls;

import java.util.concurrent.locks.Lock;

public interface GetReadLock {
    Lock getReadLock();
}
