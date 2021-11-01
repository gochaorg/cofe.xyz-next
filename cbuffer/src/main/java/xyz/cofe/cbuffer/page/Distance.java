package xyz.cofe.cbuffer.page;

public interface Distance<T,D extends Comparable<D>> {
    D distance(T a);
}
