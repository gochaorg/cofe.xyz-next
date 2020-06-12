package xyz.cofe.collection;

/**
 * Уведомление о предстоящем удалении элемента
 * @param <C> тип коллекции
 * @param <E> тип элемента коллекции
 */
public interface RemovingEvent<C, E> extends CollectionEvent<C, E> {
    /**
     * Ссылка на элемент коллекции, который будет удален
     * @return элемент коллеции
     */
    E getOldItem();

    /**
     * Создание уведомления
     * @param coll    коллекция
     * @param element удаленный значение/элемент
     * @param <C>     Тип коллеции
     * @param <E>     Тип элемента коллеции
     * @return уведомление
     */
    static <C, E> RemovingEvent<C, E> create( C coll, E element) {
        return new RemovingEvent<C, E>() {
            @Override
            public C getSource() {
                return coll;
            }

            @Override
            public E getOldItem() {
                return element;
            }

            @Override
            public String toString() {
                return "RemovingEvent{ item="+element+", colllection="+(coll != null ? coll.hashCode() : null)+" }";
            }
        };
    }
}
