package xyz.cofe.collection.tree;

import java.util.Comparator;

/**
 * Мутируемое бинарное дерево
 * @param <A> Тип значения узла
 */
public class MBTree<A> implements BTree<A> {
    public MBTree(Comparator<A> sort ){
        if( sort==null )throw new IllegalArgumentException("sort == null");
        this.sort = sort;
    }

    private final Comparator<A> sort;
    @Override public Comparator<A> getSort() { return sort; }

    private A value;
    @Override public A getValue() { return value; }
    public void setValue(A a){ this.value = a; }

    private BTree<A> left;
    @Override public BTree<A> getLeft() { return left; }
    public void setLeft(BTree<A> node){ this.left = node; }

    private BTree<A> right;
    @Override public BTree<A> getRight() { return right; }
//    public void setRight(BTree<A>)

    @Override public BTree<A> getParent() {
        return null;
    }
}
