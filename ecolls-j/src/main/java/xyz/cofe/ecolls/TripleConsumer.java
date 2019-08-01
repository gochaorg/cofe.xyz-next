package xyz.cofe.ecolls;

public interface TripleConsumer<A, B, C> {
    void accept(A a, B b, C c);
}
