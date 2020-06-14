package xyz.cofe.collection;

import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.fn.TripleConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Supplier;

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
     * Пописчики на события
     */
    private final ListenersHelper<
        CollectionListener<EventList<E>,E>,
        CollectionEvent<EventList<E>,E>
        > listenersHelper = new ListenersHelper<>( (ls,ev) -> {
            if( ls!=null ){
                ls.collectionEvent(ev);
            }
    } );

    /**
     * Возвращает подписчиков
     * @return подписчики
     */
    @Override
    public ListenersHelper<CollectionListener<EventList<E>, E>, CollectionEvent<EventList<E>, E>> listenerHelper(){
        return listenersHelper;
    }

    /**
     * Ссылка на оригинальный список
     */
    protected volatile List<E> target;

    /**
     * Возвращает целевой список, над которым происходят преобразования
     * @return целевой список
     */
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
