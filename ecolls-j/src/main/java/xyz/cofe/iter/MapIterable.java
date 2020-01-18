package xyz.cofe.iter;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Итератор отобрадения одного типа данных на другой
 * @param <FromType> Исходный тип
 * @param <ToType> Целевой тип
 */
public class MapIterable<FromType,ToType> implements Eterable<ToType>
{
    private Iterable<FromType> src = null;
    private Function<FromType,ToType> convertor = null;

    /**
     * Конструктор
     * @param src исходные данные
     * @param convertor функция конвертировния
     */
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
