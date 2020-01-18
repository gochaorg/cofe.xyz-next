package xyz.cofe.ecolls;

import java.util.concurrent.locks.Lock;

/**
 * Возвращает блокировку записи
 */
public interface GetWriteLock {
    /**
     * Возвращает блокировку записи
     * @return блокировка записи
     */
    Lock getWriteLock();
}
