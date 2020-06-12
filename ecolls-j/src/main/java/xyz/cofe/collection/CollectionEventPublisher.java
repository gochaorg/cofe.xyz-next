package xyz.cofe.collection;

import xyz.cofe.collection.CollectionListener;
import xyz.cofe.ecolls.ListenersHelper;

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
     * Возвращает помошника подписок
     * @return помошник
     */
    default ListenersHelper<
        CollectionListener<C, E>,
        CollectionEvent<C, E>
        > listenerHelper(){
        return CollectionEventPublisherImpl.listenersHelperOf(this);
    }

    /**
     * Добавляет подписчика
     * @param weak Добавить как weak (true) ссылку или как обычную (false)
     * @param listener Подписчик
     * @return Интерфейс для отсоединения подписчика
     */
    default AutoCloseable addCollectionListener(boolean weak, CollectionListener<C,E> listener){
        return listenerHelper().addListener(listener,weak);
    }

    /**
     * Добавляет подписчика
     * @param listener Подписчик
     * @return Интерфейс для отсоединения подписчика
     */
    default AutoCloseable addCollectionListener(CollectionListener<C,E> listener){
        return listenerHelper().addListener(listener,false);
    }

    /**
     * Удаление подписчика из списка обработки
     * @param listener Подписчик
     */
    default void removeCollectionListener(CollectionListener<C,E> listener){
        listenerHelper().removeListener(listener);
    }

    /**
     * Проверка наличия подписчика в списке обработки
     * @param listener Подписчик
     * @return true - есть в списке обработки
     */
    default boolean hasCollectionListener(CollectionListener<C,E> listener){
        return listenerHelper().hasListener(listener);
    }

    /**
     * Получение списка подписчиков
     * @return подписчики
     */
    default Set<CollectionListener<C,E>> getCollectionListeners(){
        return listenerHelper().getListeners();
    }

    /**
     * Рассылка уведомления подписчикам
     * @param event уведомление
     */
    default void fireCollectionEvent(CollectionEvent<C,E> event){
        listenerHelper().fireEvent(event);
    }

    /**
     * Запустить выполнение кода в блоке, и не рассылать уведомления до завершения блока кода
     * @param eventBlock блок кода
     */
    default void withCollectionEventQueue(Runnable eventBlock){
        listenerHelper().withQueue(eventBlock);
    }

    /**
     * Запустить выполнение кода в блоке, и не рассылать уведомления до завершения блока кода
     * @param eventBlock блок кода
     * @param <T> тип возвращаемого занчения из блока
     * @return возвращаемое значение
     */
    default <T> T withCollectionEventQueue(Supplier<T> eventBlock){
        return listenerHelper().withQueue(eventBlock);
    }

    /**
     * Очередь событий
     * @return Очередь событий
     */
    default ConcurrentLinkedQueue<CollectionEvent<C,E>> getCollectionEventQueue(){
        return listenerHelper().getEventQueue();
    }

    /**
     * Добавляет событие в очередь
     * @param ev событие
     */
    default void addCollectionEvent( CollectionEvent<C,E> ev ){
        listenerHelper().addEvent(ev);
    }
}
