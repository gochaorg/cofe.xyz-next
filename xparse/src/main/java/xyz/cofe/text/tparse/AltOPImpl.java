package xyz.cofe.text.tparse;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

public class AltOPImpl<P extends Pointer<?,?,P>, T extends Tok<P>> implements AltOP<P,T> {
    public AltOPImpl( GR<P,T> ... exps ){
        if( exps==null )throw new IllegalArgumentException("exps==null");
        this.exps = Arrays.asList(exps);
    }

    public AltOPImpl( Iterable<GR<P,T>> exps ){
        if( exps==null )throw new IllegalArgumentException("exps==null");
        this.exps = exps;
    }

    private final Iterable<GR<P,T>> exps;
    public Iterable<GR<P,T>> getExpressions(){ return exps; }

    @Override
    public <U extends Tok<P>> GR<P, U> map(Function<T, U> map) {
        if( map==null )throw new IllegalArgumentException("map==null");
        return new GR<P, U>() {
            @Override
            public Optional<U> apply(P ptr) {
                if(ptr==null)throw new IllegalArgumentException("ptr==null");

                Iterator<GR<P,T>> grIt = exps.iterator();
                //noinspection ConstantConditions
                if( grIt==null )return Optional.empty();

                Optional<T> found = Optional.empty();
                while( grIt.hasNext() ){
                    GR<P,T> gr = grIt.next();
                    if( gr==null )continue;

                    found = gr.apply(ptr);
                    if( found==null || !found.isPresent() ){
                        continue;
                    }

                    P p = found.get().end();
                    if( p==null )throw new IllegalStateException("token return null on end");

                    //noinspection rawtypes,unchecked
                    if( ((Pointer)ptr).compareTo(((P)p))>=0 ){
                        throw new IllegalStateException("bug of parser, end pointer as begin");
                    }

                    break;
                }

                if( found!=null && found.isPresent() ){
                    return Optional.of( map.apply( found.get() ) );
                }

                return Optional.empty();
            }
        };
    }
}
