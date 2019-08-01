package xyz.cofe.iter;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Итератор выдающий последовательность значений из итераторов
 * @param <TValue>
 */
public class JoinIterable<TValue> implements Eterable<TValue>
{
    private Iterable<TValue>[] src = null;
    private Iterable<Iterable<TValue>> srcItr = null;

    public JoinIterable(Iterable<TValue> ... src)
    {
        this.src = src;
    }

    public JoinIterable(Iterable<Iterable<TValue>> src)
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

        return new JoinIterator<TValue>(list);
    }
}
