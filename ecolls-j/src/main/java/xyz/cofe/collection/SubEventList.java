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
    protected EventList<E> list = null;
    protected int size = 0;
    protected int offset = 0;

    public SubEventList(EventList<E> list,int beginIndex,int endIndexExclusive){
        if( list==null )throw new IllegalArgumentException( "list==null" );
        if( beginIndex<0 )throw new IllegalArgumentException( "beginIndex<0" );
        if( endIndexExclusive>list.size() )throw new IllegalArgumentException( "endIndexExclusive>list.size()" );
        if( beginIndex>endIndexExclusive )throw new IllegalArgumentException( "beginIndex>endIndexExclusive" );
        this.list = list;
        offset = beginIndex;
        size = endIndexExclusive - beginIndex;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

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

    @Override
    public boolean containsAll(Collection<?> c) {
        for( Object o : c ){
            if( !contains(o) )return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean added = false;
        for( E e : c ){
            boolean r = add(e);
            added = r ? r : added;
        }
        return added;
    }

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

    @Override
    public E get(int index) {
        if( index<0 )return null;
        if( index>=size )return null;
        int ti = offset+index;
        int s = list.size();
        if( ti<0 || ti>=s )return null;
        return list.get(ti);
    }

    @Override
    public E set(int index, E e) {
        if( index<0 )return null;
        if( index>=size )return null;
        int ti = offset+index;
        int s = list.size();
        if( ti<0 || ti>=s )return null;
        return list.set(ti,e);
    }

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
