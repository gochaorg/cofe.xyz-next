package xyz.cofe.collection;

/**
 * Возвращает индекс связанный с событием изменения коллекции
 * @param <K> тип индекса
 */
public interface ItemIndex<K> {
    /**
     * Возвращает индекс элемента в коллекции
     * @return индекс элемента
     */
    K getIndex();
}
