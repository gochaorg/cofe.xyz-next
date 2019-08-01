package xyz.cofe.collection;

import xyz.cofe.collection.CollectionListener;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Подписка на события изменнения коллекции
 * @param <C> Тип коллекции
 * @param <E> Тип элемента коллекции
 */
public interface CollectionEventPublisher<C,E> {
    /**
     * Добавляет подписчика
     * @param weak Добавить как weak (true) ссылку или как обычную (false)
     * @param listener Подписчик
     * @return Интерфейс для отсоединения подписчика
     */
    default AutoCloseable addCollectionListener(boolean weak, CollectionListener<C,E> listener){
        return CollectionEventPublisherImpl.listenersHelperOf(this).addListener(listener,weak);
    }

    /**
     * Добавляет подписчика
     * @param listener Подписчик
     * @return Интерфейс для отсоединения подписчика
     */
    default AutoCloseable addCollectionListener(CollectionListener<C,E> listener){
        return CollectionEventPublisherImpl.listenersHelperOf(this).addListener(listener,false);
    }

    /**
     * Удаление подписчика из списка обработки
     * @param listener Подписчик
     */
    default void removeCollectionListener(CollectionListener<C,E> listener){
        CollectionEventPublisherImpl.listenersHelperOf(this).removeListener(listener);
    }

    /**
     * Проверка наличия подписчика в списке обработки
     * @param listener Подписчик
     * @return true - есть в списке обработки
     */
    default boolean hasCollectionListener(CollectionListener<C,E> listener){
        return CollectionEventPublisherImpl.listenersHelperOf(this).hasListener(listener);
    }

    /**
     * Получение списка подписчиков
     * @return подписчики
     */
    default Set<CollectionListener<C,E>> getCollectionListeners(){
        return CollectionEventPublisherImpl.listenersHelperOf(this).getListeners();
    }

    /**
     * Рассылка уведомления подписчикам
     * @param event уведомление
     */
    default void fireCollectionEvent(CollectionEvent<C,E> event){
        CollectionEventPublisherImpl.listenersHelperOf(this).fireEvent(event);
    }

    /**
     * Запустить выполнение кода в блоке, и не рассылать уведомления до завершения блока кода
     * @param eventBlock блок кода
     */
    default void withCollectionEventQueue(Runnable eventBlock){
        CollectionEventPublisherImpl.listenersHelperOf(this).withQueue(eventBlock);
    }

    /**
     * Запустить выполнение кода в блоке, и не рассылать уведомления до завершения блока кода
     * @param eventBlock блок кода
     * @param <T> тип возвращаемого занчения из блока
     * @return возвращаемое значение
     */
    default <T> T withCollectionEventQueue(Supplier<T> eventBlock){
        return CollectionEventPublisherImpl.listenersHelperOf(this).withQueue(eventBlock);
    }

    /**
     * Очередь событий
     * @return Очередь событий
     */
    default ConcurrentLinkedQueue<CollectionEvent<C,E>> getCollectionEventQueue(){
        return CollectionEventPublisherImpl.listenersHelperOf(this).getEventQueue();
    }

    /**
     * Добавляет событие в очередь
     * @param ev событие
     */
    default void addCollectionEvent( CollectionEvent<C,E> ev ){
        CollectionEventPublisherImpl.listenersHelperOf(this).addEvent(ev);
    }
}
