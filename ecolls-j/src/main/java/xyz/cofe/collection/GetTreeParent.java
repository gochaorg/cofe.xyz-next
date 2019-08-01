package xyz.cofe.collection;

public interface GetTreeParent<A extends Tree<A>> {
    Tree<? extends A> getParent();
}
