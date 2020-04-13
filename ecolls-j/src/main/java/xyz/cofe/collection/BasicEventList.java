package xyz.cofe.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Список с поддержкой уведомлений
 * @param <E> тип элементов в списке
 */
public class BasicEventList<E> implements EventList<E> {
    /**
     * Конструктор по умолчанию
     */
    public BasicEventList() {
        target = new ArrayList<>();
    }

    /**
     * Конструктор
     * @param target целевой список над которым производятся манипуляции
     */
    public BasicEventList(List<E> target) {
        if( target == null ) throw new IllegalArgumentException("target == null");
        this.target = target;
    }

    /**
     * Конструктор
     * @param target Целевой список над которым производятся манипуляции
     * @param rwLock Ссылка на оригинальный список
     */
    public BasicEventList(List<E> target, ReadWriteLock rwLock) {
        if( target == null ) throw new IllegalArgumentException("target == null");
        this.target = target;
        this.readWriteLock = rwLock;
    }

    /**
     * Ссылка на оригинальный список
     */
    protected volatile List<E> target;

    @Override
    public List<E> target() {
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
