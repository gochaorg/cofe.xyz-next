package xyz.cofe.collection;

/**
 * Подписчик на события коллекции
 * @param <C> тип коллекции
 * @param <E> тип элемента коллекции
 */
public interface CollectionListener<C,E> {
    /**
     * Уведомление о соыбитии коллекции
     * @param event событие
     */
    void collectionEvent(CollectionEvent<C,E> event);
}
