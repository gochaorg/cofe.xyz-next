package xyz.cofe.ecolls;

import java.util.concurrent.locks.Lock;

public interface GetWriteLock {
    Lock getWriteLock();
}
