package xyz.cofe.iter;

import xyz.cofe.ecolls.Pair;

import java.util.Iterator;

public class BiProductIterable<A,B> implements Eterable<Pair<A,B>> {
    protected Iterator<A> _1;
    protected Iterable<B> _2;

    public static <A,B> BiProductIterable<A,B> of(Iterator<A> _1,Iterable<B> _2){
        if( _1==null )throw new IllegalArgumentException("_1 == null");
        if( _2==null )throw new IllegalArgumentException("_2 == null");
        BiProductIterable<A,B> it = new BiProductIterable<>();
        it._1 = _1;
        it._2 = _2;
        return it;
    }

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
