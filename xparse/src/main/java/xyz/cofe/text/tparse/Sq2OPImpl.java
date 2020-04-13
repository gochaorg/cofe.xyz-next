package xyz.cofe.text.tparse;

import xyz.cofe.fn.Fn2;

import java.util.List;
import java.util.Optional;

public class Sq2OPImpl<P extends Pointer<?,?,P>, T1 extends Tok<P>, T2 extends Tok<P>> implements Sq2OP<P, T1, T2> {
    private final GR<P,T1> first;
    private final GR<P,T2> second;

    public Sq2OPImpl(GR<P,T1> first, GR<P,T2> second){
        if( first==null )throw new IllegalArgumentException("first==null");
        if( second==null )throw new IllegalArgumentException("second==null");
        this.first = first;
        this.second = second;
    }

    @Override
    public <U extends Tok<P>> GR<P, U> map(Fn2<T1, T2, U> map) {
        if( map==null )throw new IllegalArgumentException("map == null");
        return new GR<P, U>() {
            private final SqNOPImpl<P> sq = new SqNOPImpl<P>(first,second);

            @Override
            public Optional<U> apply(P ptr) {
                if( ptr==null )throw new IllegalArgumentException("ptr==null");
                Optional<List<? extends Tok<P>>> m = sq.match(ptr);
                if( m.isPresent() ){
                    List<? extends Tok<P>> toks = m.get();
                    if( toks.size()<2 )throw new IllegalStateException("bug");

                    T1 t0 = (T1)toks.get(0);
                    T2 t1 = (T2)toks.get(1);

                    return Optional.of( map.apply(t0,t1) );
                }
                return Optional.empty();
            }
        };
    }

    @Override
    public <U extends Tok<P>> Sq3OP<P, T1, T2, U> next(GR<P, U> third) {
        if(third==null)throw new IllegalArgumentException("third==null");
        return new Sq3OPImpl<P,T1,T2,U>(first,second,third);
    }
}
