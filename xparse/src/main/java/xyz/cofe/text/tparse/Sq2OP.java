package xyz.cofe.text.tparse;

import xyz.cofe.fn.Fn2;

public interface Sq2OP<P extends Pointer<?,?,P>, T1 extends Tok<P>, T2 extends Tok<P>> {
    <U extends Tok<P>> GR<P,U> map(Fn2<T1,T2,U> map);
    <U extends Tok<P>> Sq3OP<P,T1,T2,U> next(GR<P,U> after );
}
