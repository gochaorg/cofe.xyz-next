package xyz.cofe.collection;

import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.scn.ScnEvent;
import xyz.cofe.scn.ScnListener;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Набор объектов с поддержкой уведомлений
 * @param <E> тип элементов в наборе
 */
public class BasicEventSet<E> implements EventSet<E> {
    //region scnListenerHelper
    private final ListenersHelper<ScnListener<EventSet<E>, Long, CollectionEvent<EventSet<E>, E>>, ScnEvent<EventSet<E>, Long, CollectionEvent<EventSet<E>, E>>>
    lh = new ListenersHelper<>( ScnListener::scnEvent );

    /**
     * Возвращает помощника издателя для поддержи событий
     * @return помощник издателя
     */
    @Override
    public ListenersHelper<ScnListener<EventSet<E>, Long, CollectionEvent<EventSet<E>, E>>, ScnEvent<EventSet<E>, Long, CollectionEvent<EventSet<E>, E>>> scnListenerHelper(){
        return lh;
    }
    //endregion

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
     * Пописчики на события
     */
    private final ListenersHelper<CollectionListener<EventSet<E>, E>, CollectionEvent<EventSet<E>, E>>
        listenersHelper = new ListenersHelper<>( ( ls, ev) -> {
        if( ls!=null ){
            ls.collectionEvent(ev);
        }
    } );

    /**
     * Возвращает подписчиков
     * @return подписчики
     */
    @Override
    public ListenersHelper<CollectionListener<EventSet<E>, E>, CollectionEvent<EventSet<E>, E>> listenerHelper(){
        return listenersHelper;
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
