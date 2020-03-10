package xyz.cofe.text.tparse;

import xyz.cofe.fn.Fn2;

import java.util.List;
import java.util.function.Function;

public interface RptOP<P extends Pointer<?,?,P>, T extends Tok<P>> {
    int min();
    RptOP<P,T> min(int n);

    int max();
    RptOP<P,T> max(int n);

    boolean greedly();
    RptOP<P,T> greedly(boolean b);

    <U extends Tok<P>> GR<P,U> map(Function<List<T>,U> map);
}
