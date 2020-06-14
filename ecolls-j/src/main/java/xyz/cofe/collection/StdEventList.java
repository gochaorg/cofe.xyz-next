package xyz.cofe.collection;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Список с поддержкой событий
 * {@link InsertingEvent}, {@link UpdatingEvent}, {@link DeletingEvent}
 * {@link InsertedEvent}, {@link UpdatingEvent}, {@link DeletedEvent}
 * @param <E> Тип элемента
 */
public class StdEventList<E> extends BasicEventList<E> implements PreEventList<E> {
    /**
     * Конструктор по умолчанию
     */
    public StdEventList(){
        super();
    }

    /**
     * Конструктор
     * @param target целевой список
     */
    public StdEventList( List<E> target ){
        super(target);
    }

    /**
     * Конструктор
     * @param target целевой список
     * @param rwLock блокировки чтения / записи
     */
    public StdEventList( List<E> target, ReadWriteLock rwLock ){
        super(target, rwLock);
    }
}
