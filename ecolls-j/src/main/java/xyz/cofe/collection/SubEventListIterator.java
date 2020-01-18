package xyz.cofe.collection;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Итератор по списку
 * @param <E> Тип элемента коллеции
 */
public class SubEventListIterator<E> implements ListIterator<E>, AutoCloseable
{
    /**
     * Список
     */
    protected EventList<E> elist = null;

    /**
     * Позиция
     */
    protected int position = 0;

    /**
     * Предыдущаяя позиция
     */
    protected int lastPosition = -1;

    /**
     * Итератор закрыт
     */
    protected volatile boolean closed = false;

    /**
     * Конструктор
     * @param elist список
     */
    public SubEventListIterator(EventList<E> elist){
        if( elist==null )throw new IllegalArgumentException( "elist==null" );
        this.elist = elist;
    }

    /**
     * Конструктор
     * @param elist список
     * @param initialIndex начальный индекс
     */
    public SubEventListIterator(EventList<E> elist, int initialIndex){
        if( elist==null )throw new IllegalArgumentException( "elist==null" );
        this.elist = elist;
        this.position = initialIndex;
    }

    /**
     * Закрытие итератора
     * @throws Exception исключения не будет
     */
    @Override
    public void close() throws Exception {
        closed = true;
    }

    /**
     * Флаг - есть следующий элемент
     * @return true - есть следующий элемент
     */
    @Override
    public boolean hasNext() {
        if( closed )return false;
        if( position<0 )return false;

        EventList lst = elist;
        if( lst==null )return false;
        if( position>=lst.size() )return false;

        return true;
    }

    /**
     * Возвращает следующий элемент
     * @return следующий элемент
     * @throws NoSuchElementException достигнут конец
     * @throws IllegalStateException итератор закрыт
     */
    @Override
    public E next() {
        if( closed )throw new IllegalStateException("closed");
        if( position<0 )throw new NoSuchElementException();

        EventList<E> lst = elist;
        if( lst==null )throw new NoSuchElementException();
        if( position>=lst.size() )throw new NoSuchElementException();

        E e = lst.get(position);
        lastPosition = position;
        position++;

        return e;
    }

    /**
     * Проверяет наличие предыдущего значения
     * @return true - есть предыдущее значение
     */
    @Override
    public boolean hasPrevious() {
        if( closed )return false;
        if( position<1 )return false;

        EventList lst = elist;
        if( lst==null )return false;
        if( lst.size()<1 )return false;
        if( position > lst.size() )return false;

        return true;
    }

    /**
     * Возвращает предыдущее значение
     * @return предыдущее значение или null
     * @throws NoSuchElementException достигнут конец
     * @throws IllegalStateException итератор закрыт
     */
    @Override
    public E previous() {
        if( closed )throw new IllegalStateException("closed");
        if( position<1 )throw new NoSuchElementException();

        EventList<E> lst = elist;
        if( lst==null )throw new IllegalStateException("closed");
        if( lst.size()<1 )throw new NoSuchElementException();
        if( position > lst.size() )throw new NoSuchElementException();

        position--;
        E e = lst.get(position);
        lastPosition = position;

        return e;
    }

    /**
     * Возвращает индекс следующего элемента
     * @return индекс следующего элемента
     */
    @Override
    public int nextIndex() {
        return position;
    }

    /**
     * Возвращает индекс предыдущего элемента
     * @return индекс предыдущего элемента
     */
    @Override
    public int previousIndex() {
        return position-1;
    }

    /**
     * Удаляет значение
     */
    @Override
    public void remove() {
        if( closed )throw new IllegalStateException("closed");

        EventList<E> lst = elist;
        if( lst==null )throw new IllegalStateException("closed");

        if( lastPosition<0 )throw new IllegalStateException("need before call next()/previous()");
        lst.remove(lastPosition);
        position = lastPosition;
        lastPosition = -1;
    }

    /**
     * Устанавливает значение
     * @param e значение
     */
    @Override
    public void set(E e) {
        if( closed )throw new IllegalStateException("closed");

        EventList<E> lst = elist;
        if( lst==null )throw new IllegalStateException("closed");

        if( lastPosition<0 )throw new IllegalStateException("need before call next()/previous()");
        lst.set(lastPosition, e);
    }

    /**
     * Добавляет значение
     * @param e значение
     */
    @Override
    public void add(E e) {
        if( closed )throw new IllegalStateException("closed");

        EventList<E> lst = elist;
        if( lst==null )throw new IllegalStateException("closed");

        lst.add(position,e);
        position++;
        lastPosition = -1;
    }
}
