package xyz.cofe.iter;

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Итератор - фильтр
 * @param <T> тип элементов
 */
public class FilterIterator<T> implements Iterator<T>
{
    /**
     * Итератор
     */
    protected Iterator<T> iterator = null;
    /**
     * Предикат
     */
    protected Predicate<T> predicate = null;
    /**
     * Текущий элемент
     */
    protected T current = null;
    /**
     * Есть еще?
     */
    protected boolean hasNext = false;

    /**
     * Конструктор
     * @param predicate Предикат
     * @param iterator Итератор
     */
    public FilterIterator(Predicate<T> predicate, Iterator<T> iterator)
    {
        if (iterator == null) {
            throw new IllegalArgumentException("iterator == null");
        }
        if (predicate == null) {
            throw new IllegalArgumentException("predicate  == null");
        }

        this.iterator = iterator;
        this.predicate = predicate;

        findNext();
    }

    /**
     * Поиск следующего элемента
     */
    protected void findNext()
    {
        T obj = null;
        while (iterator.hasNext()) {
            obj = iterator.next();
            if (predicate.test(obj)) {
                current = obj;
                hasNext = true;
                return;
            }
        }
        hasNext = false;
        current = null;
        return;
    }

    /**
     * Есть элементы?
     * @return Есть элементы?
     */
    public boolean hasNext()
    {
        return hasNext;
    }

    /**
     * Получить элемент и перейти к следующему
     * @return элемент
     */
    public T next()
    {
        if (!hasNext) {
            return null;
        }

        T result = current;
        findNext();
        return result;
    }

    /**
     * Удалить проччитанный из коллекции (Не поддерживаеться)
     */
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

