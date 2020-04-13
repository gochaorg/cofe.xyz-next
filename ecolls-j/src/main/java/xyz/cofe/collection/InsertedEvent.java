package xyz.cofe.collection;

/**
 * Добавление элемента в коллецию
 * @param <C> Тип коллеции
 * @param <K> Тип ключа коллекции, в случаи с List - это Integer, в случаи Map&lt;K,V&gt; это K
 * @param <E> Тип элемента коллеции
 */
public interface InsertedEvent<C,K,E> extends AddedEvent<C,E>, ItemIndex<K> {
    /**
     * Создание уведомления
     * @param coll коллекция
     * @param key ключ добавленного значения
     * @param element текущее значение/элемент
     * @param <C> Тип коллеции
     * @param <K> Тип ключа коллекции, в случаи с List - это Integer, в случаи Map&lt;K,V&gt; это K
     * @param <E> Тип элемента коллеции
     * @return уведомление
     */
    static <C,K,E> InsertedEvent<C,K,E> create( C coll, K key, E element ){
        return new InsertedEvent<C, K, E>() {
            @Override
            public C getSource() {
                return coll;
            }

            @Override
            public E getNewItem() {
                return element;
            }

            @Override
            public K getIndex() {
                return key;
            }

            @Override
            public String toString() {
                return "InsertedEvent{ idx="+key+", item="+element+", colllection="+(coll!=null ? coll.hashCode() : null)+" }";
            }
        };
    }
}
