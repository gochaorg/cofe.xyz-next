package xyz.cofe.iter;

import java.util.Iterator;

/**
 * Возвращает пустую последовательность объектов
 */
public class EmptyIterable<T> implements Eterable<T>, Iterator<T> {
    public Iterator<T> iterator() {
        return this;
    }

    public boolean hasNext() {
        return false;
    }

    public T next() {
        return null;
    }

    public void remove() {
    }
}
