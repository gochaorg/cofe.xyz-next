package xyz.cofe.iter;

import java.util.Iterator;
import java.util.function.Function;

public class MapIterator<FromType, ToType> implements Iterator<ToType>
{
    private Iterator<FromType> src = null;
    private Function<FromType, ToType> convertor = null;

    public MapIterator(Iterator<FromType> src, Function<FromType, ToType> convertor)
    {
        if (src == null)
        {
            throw new IllegalArgumentException("src == null");
        }

        if (convertor == null)
        {
            throw new IllegalArgumentException("convertor == null");
        }

        this.src = src;
        this.convertor = convertor;
    }

    public boolean hasNext()
    {
        return src.hasNext();
    }

    public ToType next()
    {
        FromType obj = src.next();
        ToType dest = convertor.apply(obj);
        return dest;
    }

    public void remove()
    {
        src.remove();
    }
}
