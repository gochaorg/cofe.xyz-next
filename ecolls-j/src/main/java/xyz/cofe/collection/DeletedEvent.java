package xyz.cofe.collection;

/**
 * Удаление элемента из коллеции
 * @param <C> Тип коллеции
 * @param <K> Тип ключа коллекции, в случаи с List - это Integer, в случаи Map&lt;K,V&gt; это K
 * @param <E> Тип элемента коллеции
 */
public interface DeletedEvent<C, K, E> extends RemovedEvent<C, E>, ItemIndex<K> {
    /**
     * Создание уведомления
     * @param coll    коллекция
     * @param key     ключ удаленного значения
     * @param element удаленный значение/элемент
     * @param <C>     Тип коллеции
     * @param <K>     Тип ключа коллекции, в случаи с List - это Integer, в случаи Map&lt;K,V&gt; это K
     * @param <E>     Тип элемента коллеции
     * @return уведомление
     */
    static <C, K, E> DeletedEvent<C, K, E> create(C coll, K key, E element) {
        return new DeletedEvent<C, K, E>() {
            @Override
            public C getSource() {
                return coll;
            }

            @Override
            public E getOldItem() {
                return element;
            }

            @Override
            public K getIndex() {
                return key;
            }

            @Override
            public String toString() {
                return "DeletedEvent{ idx="+key+", item="+element+", colllection="+(coll != null ? coll.hashCode() : null)+" }";
            }
        };
    }
}
