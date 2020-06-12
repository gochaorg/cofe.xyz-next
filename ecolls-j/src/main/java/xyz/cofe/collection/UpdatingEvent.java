package xyz.cofe.collection;

import xyz.cofe.ecolls.ImmediateEvent;

/**
 * Предстоящее обновление элемента коллеции
 * @param <C> Тип коллеции
 * @param <K> Тип ключа коллекции, в случаи с List - это Integer, в случаи Map&lt;K,V&gt; это K
 * @param <E> Тип элемента коллеции
 */
public interface UpdatingEvent<C,K,E> extends AddingEvent<C,E>, RemovingEvent<C,E>, ItemIndex<K>, ImmediateEvent {
    /**
     * Создание уведомления
     * @param coll коллекция
     * @param key ключ чье значени изменилось
     * @param element текущее значение/элемент
     * @param oldElement предыдущее значение/элемент
     * @param <C> Тип коллеции
     * @param <K> Тип ключа коллекции, в случаи с List - это Integer, в случаи Map&lt;K,V&gt; это K
     * @param <E> Тип элемента коллеции
     * @return уведомление
     */
    static <C,K,E> UpdatingEvent<C,K,E> create( C coll, K key, E element, E oldElement ){
        return new UpdatingEvent<C, K, E>() {
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
            public E getOldItem() {
                return oldElement;
            }

            @Override
            public String toString() {
                return "UpdatingEvent{ idx="+key+", item="+element+", colllection="+(coll!=null ? coll.hashCode() : null)+" }";
            }
        };
    }
}
