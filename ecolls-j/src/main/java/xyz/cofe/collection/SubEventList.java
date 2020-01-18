package xyz.cofe.collection;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Список представление части исходного списка
 * @param <E> Тип элемента списка
 */
public class SubEventList<E> extends AbstractList<E> implements List<E> {
    /**
     * Список
     */
    protected EventList<E> list = null;

    /**
     * Кол-во элементов
     */
    protected int size = 0;

    /**
     * Смещение относительно списка
     */
    protected int offset = 0;

    /**
     * Конструктор
     * @param list список
     * @param beginIndex начальный индекс включительно
     * @param endIndexExclusive конечный индекс исключительно
     */
    public SubEventList(EventList<E> list,int beginIndex,int endIndexExclusive){
        if( list==null )throw new IllegalArgumentException( "list==null" );
        if( beginIndex<0 )throw new IllegalArgumentException( "beginIndex<0" );
        if( endIndexExclusive>list.size() )throw new IllegalArgumentException( "endIndexExclusive>list.size()" );
        if( beginIndex>endIndexExclusive )throw new IllegalArgumentException( "beginIndex>endIndexExclusive" );
        this.list = list;
        offset = beginIndex;
        size = endIndexExclusive - beginIndex;
    }

    /**
     * Возвращает кол-во элементов
     * @return Кол-во элементов
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Проверяет наличие элементов
     * @return true - список пустой
     */
    @Override
    public boolean isEmpty() {
        return size==0;
    }

    /**
     * Проверяет наличие элемента в списке
     * @param o элемент
     * @return true - содержиться в списке
     */
    @Override
    public boolean contains(Object o) {
        for( int i=0; i<size; i++ ){
            int ti = i+offset;
            int ss = list.size();
            if( ti>=ss )break;
            Object srcO = list.get(ti);
            if( o==null && srcO==null )return true;
            if( o!=null && srcO!=null ){
                if( o.equals(srcO) )return true;
            }
        }
        return false;
    }

    /**
     * Получение массива элементов
     * @return массив
     */
    @Override
    public Object[] toArray() {
        Object[] a = new Object[]{};
        for( int i=0; i<size; i++ ){
            int ti = i+offset;
            int ss = list.size();
            if( ti>=ss )break;
            Object srcO = list.get(ti);
            a = Arrays.copyOf(a, a.length+1);
            a[a.length-1] = srcO;
        }
        return a;
    }

    /**
     * Получение массива элементов
     * @param a тип массива
     * @param <T> тип элементов массива
     * @return массив
     */
    @Override
    public <T> T[] toArray(T[] a) {
        for( int i=0; i<size; i++ ){
            int ti = i+offset;
            int ss = list.size();
            if( ti>=ss )break;
            Object srcO = list.get(ti);
            a = Arrays.copyOf(a, a.length+1);
            a[a.length-1] = (T)srcO;
        }
        return a;
    }

    /**
     * Добавление элементов в список
     * @param e элемент
     * @return true - элемент добавлен
     */
    @Override
    public boolean add(E e) {
        int pos = offset+size;
        int ss = list.size();
        if( pos>=ss ){
            boolean succ = list.add(e);
            if( succ ){
                size++;
            }
            return succ;
        }else{
            list.add(pos, e);
            size++;
            return true;
        }
    }

    /**
     * Проверяет надичие всех указанных элементов в списке
     * @param c проверяемые элементы
     * @return true - все указанные элементы содержится
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for( Object o : c ){
            if( !contains(o) )return false;
        }
        return true;
    }

    /**
     * Добавляет указанные элементы
     * @param c элементы
     * @return факт добавления
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean added = false;
        for( E e : c ){
            boolean r = add(e);
            added = r ? r : added;
        }
        return added;
    }

    /**
     * Удаление всех элементо
     */
    @Override
    public void clear() {
        if( size<=0 )return;
        for( int i=size-1; i>=0; i-- ){
            int ti = i + offset;
            int s = list.size();
            if( ti<0 || ti>=s )continue;
            list.remove(ti);
        }
    }

    /**
     * Получение объекта по его индексу
     * @param index индекс элемента
     * @return Элемент
     */
    @Override
    public E get(int index) {
        if( index<0 )return null;
        if( index>=size )return null;
        int ti = offset+index;
        int s = list.size();
        if( ti<0 || ti>=s )return null;
        return list.get(ti);
    }

    /**
     * Записиывает элемент в определенную позицию
     * @param index индекс
     * @param e элемент
     * @return предыдущее значение
     */
    @Override
    public E set(int index, E e) {
        if( index<0 )return null;
        if( index>=size )return null;
        int ti = offset+index;
        int s = list.size();
        if( ti<0 || ti>=s )return null;
        return list.set(ti,e);
    }

    /**
     * Вставляет элемент в определнное место списка
     * @param index индекс
     * @param e элемент
     */
    @Override
    public void add(int index, E e) {
        if( index<0 )index = 0;
        if( index>size )index = size;
        int pos = offset+index;
        int ss = list.size();
        if( pos>=ss ){
            boolean succ = list.add(e);
            if( succ ){
                size++;
            }
        }else{
            list.add(pos, e);
            size++;
        }
    }

    /*
     * Удялет элемент из списка
     * @param index индекс элемента
     * @return удаленный элемент
     */
    @Override
    public E remove(int index) {
        if( index<0 )return null;
        if( index>=size )return null;
        if( size<=0 )return null;
        int ti = offset+index;
        int s = list.size();
        if( ti<0 || ti>=s )return null;
        E r = list.remove(ti);
        size--;
        return r;
    }

    /**
     * Поиск первого индекса элемента в коллекции
     * @param o элемент
     * @return индекс или -1, если не найден
     */
    @Override
    public int indexOf(Object o) {
        for( int i=0; i<size; i++ ){
            int ti = i+offset;
            int ss = list.size();
            if( ti>=ss )break;
            Object srcO = list.get(ti);
            if( o==null && srcO==null )return i;
            if( o!=null && srcO!=null ){
                if( o.equals(srcO) )return i;
            }
        }
        return -1;
    }

    /**
     * Поиск последнего индекса элемента в коллекции
     * @param o элемент
     * @return индекс или -1, если не найден
     */
    @Override
    public int lastIndexOf(Object o) {
        for( int i=size-1; i>=0; i-- ){
            int ti = i+offset;
            int ss = list.size();
            if( ti>=ss )continue;
            Object srcO = list.get(ti);
            if( o==null && srcO==null )return i;
            if( o!=null && srcO!=null ){
                if( o.equals(srcO) )return i;
            }
        }
        return -1;
    }
}
