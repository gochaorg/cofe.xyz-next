package xyz.cofe.iter;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Итератор выдающий последовательность значений из итераторов
 * @param <TValue> тип элементов
 */
public class UnionIterable<TValue> implements Eterable<TValue>
{
    private Iterable<TValue>[] src = null;
    private Iterable<Iterable<TValue>> srcItr = null;

    /**
     * Конструктор
     * @param src итераторы
     */
    public UnionIterable( Iterable<TValue> ... src)
    {
        this.src = src;
    }

    /**
     * Конструктор
     * @param src итераторы
     */
    public UnionIterable( Iterable<Iterable<TValue>> src)
    {
        if( src==null )throw new IllegalArgumentException("src==null");
        this.srcItr = src;
    }

    @Override
    public Iterator<TValue> iterator()
    {
        ArrayList<Iterator<TValue>> list = new ArrayList<Iterator<TValue>>();
        if( src!=null )
        {
            for(Iterable<TValue> _s : src)
            {
                if( _s!=null )
                {
                    Iterator<TValue> itr = _s.iterator();
                    if( itr!=null )
                    {
                        list.add(itr);
                    }
                }
            }
        }
        if( srcItr!=null ){
            for(Iterable<TValue> _s : srcItr)
            {
                if( _s!=null )
                {
                    Iterator<TValue> itr = _s.iterator();
                    if( itr!=null )
                    {
                        list.add(itr);
                    }
                }
            }
        }

        return new UnionIterator<TValue>(list);
    }
}
