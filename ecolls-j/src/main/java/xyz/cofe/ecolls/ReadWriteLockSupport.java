package xyz.cofe.ecolls;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * Поддержка 2-х свойств, блокировки чтения и блокировки записи
 */
public interface ReadWriteLockSupport extends GetReadLock, GetWriteLock {
    /**
     * Возвращает блокировку чтения
     * @return блокировка чтения
     */
    default Lock getReadLock() { return getWriteLock(); }

    /**
     * Возвращает блокировку записи
     * @return блокировка записи
     */
    default Lock getWriteLock(){ return null; }

    /**
     * Выполнение кода с использованием блокировки чтения
     * @param syncBlock код
     * @param <T> тип результата выполнения
     * @return результат выполнения
     */
    default <T> T readLock(Supplier<T> syncBlock){
        Objects.requireNonNull(syncBlock);
        return ReadWriteLockSupportImpl.lock(this, getReadLock(), syncBlock);
    }

    /**
     * Выполнение кода с использованием блокировки чтения
     * @param syncBlock код
     */
    default void readLock(Runnable syncBlock){
        Objects.requireNonNull(syncBlock);
        ReadWriteLockSupportImpl.lock(this, getReadLock(), syncBlock);
    }

    /**
     * Выполнение кода с использованием блокировки записи
     * @param syncBlock код
     * @param <T> тип результата выполнения
     * @return результат выполнения
     */
    default <T> T writeLock(Supplier<T> syncBlock){
        Objects.requireNonNull(syncBlock);
        return ReadWriteLockSupportImpl.lock(this, getWriteLock(),syncBlock);
    }

    /**
     * Выполнение кода с использованием блокировки записи
     * @param syncBlock код
     */
    default void writeLock(Runnable syncBlock){
        Objects.requireNonNull(syncBlock);
        ReadWriteLockSupportImpl.lock(this,getWriteLock(),syncBlock);
    }
}
