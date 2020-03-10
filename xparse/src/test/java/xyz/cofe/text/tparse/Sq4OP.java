package xyz.cofe.text.tparse;

import xyz.cofe.fn.Fn4;

public interface Sq4OP<
        P extends Pointer<?,?,P>,
        T1 extends Tok<P>,
        T2 extends Tok<P>,
        T3 extends Tok<P>,
        T4 extends Tok<P>
        > {
    <U extends Tok<P>> GR<P,U> map(Fn4<T1, T2, T3, T4, U> map);
    <U extends Tok<P>> Sq5OP<P,T1,T2,T3,T4,U> next(GR<P,U> then );
}
