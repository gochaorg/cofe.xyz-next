package xyz.cofe.iter;

import java.util.Iterator;

/**
 * Возвращает последовательность с одним элементом
 */
public class SingleIterator<T> implements Iterator<T> {
    private T item = null;
    private boolean readed = false;

    /**
     * Конструктор
     * @param item элемент
     */
    public SingleIterator(T item) {
        this.item = item;
    }

    @Override
    public boolean hasNext() {
        return !readed;
    }

    @Override
    public T next() {
        if( !readed ){
            readed = true;
            return item;
        }
        return null;
    }

    @Override
    public void remove() {
    }
}
