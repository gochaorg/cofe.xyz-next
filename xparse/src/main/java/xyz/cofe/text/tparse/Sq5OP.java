package xyz.cofe.text.tparse;

import xyz.cofe.fn.Fn5;

public interface Sq5OP<
        P extends Pointer<?,?,P>,
        T1 extends Tok<P>,
        T2 extends Tok<P>,
        T3 extends Tok<P>,
        T4 extends Tok<P>,
        T5 extends Tok<P>
        > {
    <U extends Tok<P>> GR<P,U> map(Fn5<T1, T2, T3, T4, T5, U> map);
}
