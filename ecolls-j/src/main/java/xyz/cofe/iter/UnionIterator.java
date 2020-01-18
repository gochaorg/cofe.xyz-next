package xyz.cofe.iter;

import java.util.Iterator;

/**
 * Итератор выдающий последовательность значений из итераторов
 * @param <TValue> тип элементов
 */
public class UnionIterator<TValue> implements Iterator<TValue>
{
    private Iterable<Iterator<TValue>> src = null;
    private Iterator<Iterator<TValue>> srcitr = null;
    private boolean end = false;
    private TValue currentValue;
    private Iterator<TValue> currentSrc = null;

    /**
     * Конструктор
     * @param src итераторы
     */
    public UnionIterator( Iterable<Iterator<TValue>> src)
    {
        this.src = src;

        if (src == null)
        {
            end = true;
        }
        else
        {
            srcitr = this.src.iterator();
            find();
        }
    }

    private void find()
    {
        while (true)
        {
            if (currentSrc != null)
            {
                if (currentSrc.hasNext())
                {
                    currentValue = currentSrc.next();
                    break;
                }
                else
                {
                    if (!srcitr.hasNext())
                    {
                        end = true;
                        break;
                    }

                    currentSrc = srcitr.next();
                }
            }
            else
            {
                if (!srcitr.hasNext())
                {
                    end = true;
                    break;
                }

                currentSrc = srcitr.next();
            }
        }
    }

    public boolean hasNext()
    {
        return !end;
    }

    public TValue next()
    {
        if (end)
            return null;
        TValue res = currentValue;
        find();
        return res;
    }

    public void remove()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
