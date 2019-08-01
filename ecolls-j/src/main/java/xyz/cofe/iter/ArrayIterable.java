package xyz.cofe.iter;

import java.util.Iterator;

/**
 * Итератор по массиву
 */
public class ArrayIterable<T> implements Iterable<T> {
    private T[] arr;

    public ArrayIterable(T[] srcArray) {
        if( srcArray == null ){
            throw new IllegalArgumentException("srcArray == null");
        }
        this.arr = srcArray;
    }

    public Iterator<T> iterator() {
        return new ArrayIterator<T>(arr);
    }
}
