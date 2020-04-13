package xyz.cofe.iter;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Итератор - фильтр
 * @param <T> тип элементов
 */
public class FilterIterable<T> implements Eterable<T>
{
    /**
     * Предикат
     */
    protected Predicate<T> predicate = null;
    /**
     * Итератор
     */
    protected Iterable<T> iterable = null;

    /**
     * Конструктор
     * @param predicate Предикат
     * @param iterable Итератор
     */
    public FilterIterable(Predicate<T> predicate, Iterable<T> iterable)
    {
        if (predicate == null) {
            throw new IllegalArgumentException("predicate == null");
        }
        this.predicate = predicate;

        if (iterable == null) {
            throw new IllegalArgumentException("iterable == null");
        }
        this.iterable = iterable;
    }

    /**
     * Возвращает итератор
     * @return итератор
     */
    public Iterator<T> iterator()
    {
        return new FilterIterator<T>(predicate, iterable.iterator());
    }
}

