package xyz.cofe.collection;

/**
 * Событие изменения колллекции
 * @param <C> тип коллекции
 * @param <E> тип элемента коллекции
 */
public interface CollectionEvent<C,E> {
    /**
     * Ссылка на коллекцию
     * @return коллеция
     */
    C getSource();
}
