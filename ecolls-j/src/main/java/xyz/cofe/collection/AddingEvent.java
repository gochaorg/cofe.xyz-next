package xyz.cofe.collection;

/**
 * Уведомление о предстоящем добавлении элемента
 * @param <C> тип коллекции
 * @param <E> тип элемента коллекции
 */
public interface AddingEvent<C, E> extends CollectionEvent<C, E> {
    /**
     * Ссылка на элемент коллекции, который будет вставлен/добавлен
     * @return элемент коллеции
     */
    E getNewItem();

    /**
     * Создание уведомления
     * @param coll       коллекция
     * @param element    текущее значение/элемент
     * @param <C>        Тип коллеции
     * @param <E>        Тип элемента коллеции
     * @return уведомление
     */
    static <C, E> AddingEvent<C, E> create( C coll, E element) {
        return new AddingEvent<C, E>() {
            @Override
            public C getSource() {
                return coll;
            }

            @Override
            public E getNewItem() {
                return element;
            }

            @Override
            public String toString() {
                return "AddingEvent{ item="+element+", colllection="+(coll != null ? coll.hashCode() : null)+" }";
            }
        };
    }
}
