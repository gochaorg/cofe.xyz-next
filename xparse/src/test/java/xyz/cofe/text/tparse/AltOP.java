package xyz.cofe.text.tparse;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface AltOP<P extends Pointer<?,?,P>, T extends Tok<P>> {
    default <U extends Tok<P>> GR<P,U> map(Function<T,U> map) {
        if( map==null )throw new IllegalArgumentException("map==null");
        return new GR<P, U>() {
            @Override
            public Optional<U> apply(P ptr) {
                if(ptr==null)throw new IllegalArgumentException("ptr==null");
                return Optional.empty();
            }
        };
    }
    default GR<P,T> map() {
        return map(x->x);
    }
}
