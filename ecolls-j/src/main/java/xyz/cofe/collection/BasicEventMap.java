package xyz.cofe.collection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Базовая реализация карты с поддержкой уведомлений
 * @param <K> Тип ключа
 * @param <V> Тип значения
 */
public class BasicEventMap<K,V> implements EventMap<K,V> {
    /**
     * Конструктор по умолчанию
     */
    public BasicEventMap(){
        target = new LinkedHashMap<>();
    }

    /**
     * Конструктор
     * @param target целевая карта
     */
    public BasicEventMap(Map<K, V> target){
        if( target == null )throw new IllegalArgumentException( "target == null" );
        this.target = target;
    }

    /**
     * Конструктор
     * @param target целевая карта
     * @param rwLock Блокировка чтения/записи
     */
    public BasicEventMap(Map<K, V> target, ReadWriteLock rwLock){
        if( target == null )throw new IllegalArgumentException( "target == null" );
        this.target = target;
        this.readWriteLock = rwLock;
    }

    /**
     * Ссылка на оригинальный набор
     */
    protected volatile Map<K,V> target;

    @Override
    public Map<K, V> target() {
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
