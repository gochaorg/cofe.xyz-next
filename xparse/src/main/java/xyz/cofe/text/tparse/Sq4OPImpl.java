package xyz.cofe.text.tparse;

import xyz.cofe.fn.Fn2;
import xyz.cofe.fn.Fn4;

import java.util.List;
import java.util.Optional;

public class Sq4OPImpl<
        P extends Pointer<?,?,P>,
        T1 extends Tok<P>,
        T2 extends Tok<P>,
        T3 extends Tok<P>,
        T4 extends Tok<P>
        > implements Sq4OP<P, T1, T2, T3, T4> {
    private final GR<P,T1> first;
    private final GR<P,T2> second;
    private final GR<P,T3> third;
    private final GR<P,T4> fourth;

    public Sq4OPImpl(GR<P,T1> first, GR<P,T2> second, GR<P,T3> third, GR<P,T4> fourth){
        if( first==null )throw new IllegalArgumentException("first==null");
        if( second==null )throw new IllegalArgumentException("second==null");
        if( third==null )throw new IllegalArgumentException("third==null");
        if( fourth==null )throw new IllegalArgumentException("fourth==null");
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    @Override
    public <U extends Tok<P>> GR<P, U> map(Fn4<T1, T2, T3, T4, U> map) {
        if( map==null )throw new IllegalArgumentException("map == null");
        return new GR<P, U>() {
            private String name;

            @Override
            public GR<P, U> name( String name ){
                this.name = name;
                return this;
            }
            @Override public String name(){ return name; }

            @Override
            public String toString(){
                if( name!=null )return name;
                return super.toString();
            }

            private final SqNOPImpl<P> sq = new SqNOPImpl<P>(first,second,third,fourth);

            @Override
            public Optional<U> apply(P ptr) {
                if( ptr==null )throw new IllegalArgumentException("ptr==null");
                Optional<List<? extends Tok<P>>> m = sq.name(name).match(ptr);
                if( m.isPresent() ){
                    List<? extends Tok<P>> toks = m.get();
                    if( toks.size()<4 )throw new ImplementError("tokens count < 4");

                    T1 t0 = (T1)toks.get(0);
                    T2 t1 = (T2)toks.get(1);
                    T3 t2 = (T3)toks.get(2);
                    T4 t3 = (T4)toks.get(3);

                    U t = map.apply(t0,t1,t2,t3);
                    if( t==null )throw new MapResultError("return null");
                    if( t.end().position() != t3.end().position() )
                        throw new MapResultError("pointer order");

                    return Optional.of( t );
                }
                return Optional.empty();
            }
        };
    }

    @Override
    public <U extends Tok<P>> Sq5OP<P, T1, T2, T3, T4, U> next(GR<P, U> fifth) {
        if( fifth==null )throw new IllegalArgumentException("fifth==null");
        return new Sq5OPImpl<>(first,second,third,fourth,fifth);
    }
}
