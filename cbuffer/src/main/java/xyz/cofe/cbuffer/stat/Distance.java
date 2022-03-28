package xyz.cofe.cbuffer.stat;

public interface Distance<T,D extends Comparable<D>> {
    D distance(T a);
}
