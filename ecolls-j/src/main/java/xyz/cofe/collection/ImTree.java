package xyz.cofe.collection;

import xyz.cofe.iter.Eterable;

public interface ImTree<A extends ImTree<? extends A>> {
    Eterable<A> nodes();
}
