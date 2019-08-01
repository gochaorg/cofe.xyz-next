package xyz.cofe.iter;

import java.util.Iterator;
import java.util.function.Function;

public class MapIterable<FromType,ToType> implements Eterable<ToType>
{
    private Iterable<FromType> src = null;
    private Function<FromType,ToType> convertor = null;

    public MapIterable(Iterable<FromType> src, Function<FromType,ToType> convertor)
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

    public Iterator<ToType> iterator()
    {
        return new MapIterator<FromType,ToType>(src.iterator(), convertor);
    }
}
