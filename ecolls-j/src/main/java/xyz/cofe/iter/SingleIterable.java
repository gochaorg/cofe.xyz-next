package xyz.cofe.iter;

import java.util.Iterator;
import java.util.function.Supplier;

/**
 * Возвращает последовательность с одним элементом
 * @author gocha
 */
public class SingleIterable<T> implements Eterable<T> {
    private T item = null;
    private Supplier<T> lazyValue = null;

    public SingleIterable(T item) {
        this.item = item;
    }

    public SingleIterable(Supplier<T> item) {
        this.lazyValue = item;
    }

    @Override
    public Iterator<T> iterator() {
        if( lazyValue != null ){
            return new Iterator<T>() {
                boolean readed = false;

                @Override
                public boolean hasNext() {
                    return !readed;
                }

                @Override
                public T next() {
                    if( !readed ){
                        readed = true;
                        return lazyValue.get();
                    }
                    return null;
                }

                @Override
                public void remove() {
                }
            };
        }
        return new SingleIterator<T>(item);
    }
}
