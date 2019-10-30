package xyz.cofe.ecolls;

import java.util.function.BiFunction;

public interface Fn2<A,B,Z> extends BiFunction<A,B,Z> {
    Z apply(A a, B b);
}
