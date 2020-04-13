package xyz.cofe.collection;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Набор объектов с поддержкой уведомлений
 * @param <E> тип элементов в наборе
 */
public class BasicEventSet<E> implements EventSet<E> {
    /**
     * Конструктор по умолчанию
     */
    public BasicEventSet() {
        target = new LinkedHashSet<>();
    }

    /**
     * Конструктор
     * @param set Ссылка на оригинальный набор
     */
    public BasicEventSet(Set<E> set) {
        if( set == null ) throw new IllegalArgumentException("set == null");
        target = set;
    }

    /**
     * Конструктор
     * @param set    Ссылка на оригинальный набор
     * @param rwLock Блокировка чтения/записи
     */
    public BasicEventSet(Set<E> set, ReadWriteLock rwLock) {
        if( set == null ) throw new IllegalArgumentException("set == null");
        target = set;
        this.readWriteLock = rwLock;
    }

    /**
     * Ссылка на оригинальный набор
     */
    protected volatile Set<E> target;

    @Override
    public Set<E> target() {
        return target;
    }

    /**
     * Блокировка чтения/записи
     */
    protected volatile ReadWriteLock readWriteLock;

    @Override
    public Lock getReadLock() {
        ReadWriteLock rwLock = readWriteLock;
        return rwLock != null ? rwLock.readLock() : null;
    }

    @Override
    public Lock getWriteLock() {
        ReadWriteLock rwLock = readWriteLock;
        return rwLock != null ? rwLock.writeLock() : null;
    }
}
