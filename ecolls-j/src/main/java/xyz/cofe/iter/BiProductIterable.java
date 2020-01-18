package xyz.cofe.iter;

import xyz.cofe.ecolls.Pair;

import java.util.Iterator;

/**
 * Итератор - декартово произведение
 * @param <A> Тип первого значения в паре
 * @param <B> Тип воторого значения в паре
 */
public class BiProductIterable<A,B> implements Eterable<Pair<A,B>> {
    protected Iterator<A> _1;
    protected Iterable<B> _2;

    /**
     * Конструктор
     * @param _1 Итератор по первому списку
     * @param _2 Итератор по второму списку
     * @param <A> Тип первого списка
     * @param <B> Тип второго списка
     * @return Итератор по декртовому произведению
     */
    public static <A,B> BiProductIterable<A,B> of(Iterator<A> _1,Iterable<B> _2){
        if( _1==null )throw new IllegalArgumentException("_1 == null");
        if( _2==null )throw new IllegalArgumentException("_2 == null");
        BiProductIterable<A,B> it = new BiProductIterable<>();
        it._1 = _1;
        it._2 = _2;
        return it;
    }

    /**
     * Конструктор
     * @param _1 Итератор по первому списку
     * @param _2 Итератор по второму списку
     * @param <A> Тип первого списка
     * @param <B> Тип второго списка
     * @return Итератор по декртовому произведению
     */
    public static <A,B> BiProductIterable<A,B> of(Iterable<A> _1,Iterable<B> _2){
        if( _1==null )throw new IllegalArgumentException("_1 == null");
        if( _2==null )throw new IllegalArgumentException("_2 == null");
        BiProductIterable<A,B> it = new BiProductIterable<>();
        it._1 = _1.iterator();
        it._2 = _2;
        return it;
    }

    @Override
    public Iterator<Pair<A, B>> iterator() {
        return new BiProductIterator<A, B>(_1,_2);
    }
}
