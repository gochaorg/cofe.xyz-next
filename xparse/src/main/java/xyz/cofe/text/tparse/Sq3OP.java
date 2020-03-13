package xyz.cofe.text.tparse;

import xyz.cofe.fn.*;

public interface Sq3OP<
        P extends Pointer<?,?,P>,
        T1 extends Tok<P>,
        T2 extends Tok<P>,
        T3 extends Tok<P>
        > {
    <U extends Tok<P>> GR<P,U> map(Fn3<T1, T2, T3, U> map);
    <U extends Tok<P>> Sq4OP<P,T1,T2,T3,U> next(GR<P,U> then );
}
