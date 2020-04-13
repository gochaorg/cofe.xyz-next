package xyz.cofe.ecolls;

import java.util.concurrent.locks.Lock;

/**
 * Получение блокировки чтения
 */
public interface GetReadLock {
    /**
     * Возвращает блокировку чтения
     * @return блокировка чтения
     */
    Lock getReadLock();
}
