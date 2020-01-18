package xyz.cofe.iter;

import java.util.Iterator;

/**
 * Итератор по массиву
 */
public class ArrayIterator<T> implements Iterator<T> {
    private T[] array;
    private int index = 0;

    /**
     * Конструктор
     * @param array исходный массив
     */
    public ArrayIterator(T[] array) {
        if( array == null ){
            throw new IllegalArgumentException("array == null");
        }
        this.array = array;
    }

    public boolean hasNext() {
        return index<array.length;
    }

    public T next() {
        if( !hasNext() ) return null;
        T res = array[index];
        index++;
        return res;
    }

    public void remove() {
    }
}
