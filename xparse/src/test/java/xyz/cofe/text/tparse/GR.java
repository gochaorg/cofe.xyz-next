package xyz.cofe.text.tparse;

import xyz.cofe.fn.Fn2;
import xyz.cofe.fn.Fn3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface GR<P extends Pointer<?,?,P>, T extends Tok<P>> extends Function<P, Optional<T>> {
    default <U extends Tok<P>> Sq2OP<P,T,U> next(GR<P,U> then) {
        if( then==null )throw new IllegalArgumentException("then==null");
        return new Sq2OPImpl<>(this,then);
    }

    default RptOP<P,T> repeat(){
        return new RptOPImpl<>(this,0,0,true);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default AltOP<P,T> another(GR<P,T> rule ) {
        if( rule==null )throw new IllegalArgumentException("rule == null");

        @SuppressWarnings("rawtypes") GR self = this;
        return new AltOPImpl<>( self, rule );
    }
}
