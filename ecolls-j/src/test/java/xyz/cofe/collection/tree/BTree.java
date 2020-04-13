package xyz.cofe.collection.tree;

import java.util.Comparator;

/**
 * Бинарное дерево
 * @param <A> Тип значение узла дерева
 */
public interface BTree<A> {
    Comparator<A> getSort();
    A getValue();
    BTree<A> getLeft();
    BTree<A> getRight();
    BTree<A> getParent();
}
