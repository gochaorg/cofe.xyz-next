package xyz.cofe.iter;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Создание итераторов по дереву
 * @param <A> Элемент дерева
 */
@SuppressWarnings("WeakerAccess")
public class TreeIterBuilderDefault<A> implements TreeIterBuilder<A>
{
    protected A root;
    protected Function<A,Iterable<A>> follow;
    protected Function<List<TreeStep<A>>, TreeStep<A>> poll;
    protected Consumer<TreeIterator.PushStep<A>> push;
    protected Predicate<TreeStep<A>> allow;

    public TreeIterBuilderDefault( A root, Function<A,Iterable<A>> follow ){
        if( root==null ) throw new IllegalArgumentException("root==null");
        if( follow==null ) throw new IllegalArgumentException("follow==null");
        this.root = root;
        this.follow = follow;
    }

    public TreeIterBuilderDefault( TreeIterBuilderDefault<A> sample ){
        if( sample==null ) throw new IllegalArgumentException("sample==null");
        this.root = sample.root;
        this.follow = sample.follow;
        this.poll = sample.poll;
        this.push = sample.push;
        this.allow = sample.allow;
    }

    public TreeIterBuilderDefault<A> clone(){
        return new TreeIterBuilderDefault<>(this);
    }

    @Override
    public Eterable<A> walk(){
        return go().map(TreeStep::getNode);
    }

    @Override
    public Eterable<TreeStep<A>> go(){
        return this::treeIterator;
    }

    public TreeIterator<A> treeIterator(){
        return new TreeIterator<A>(root,follow,poll,push,allow);
    }

    public TreeIterBuilder<A> poll(Function<List<TreeStep<A>>, TreeStep<A>> poll){
        TreeIterBuilderDefault<A> bldr = clone();
        bldr.poll = poll;
        return bldr;
    }

    public TreeIterBuilder<A> push(Consumer<TreeIterator.PushStep<A>> push){
        TreeIterBuilderDefault<A> bldr = clone();
        bldr.push = push;
        return bldr;
    }

    public TreeIterBuilder<A> filter(Predicate<TreeStep<A>> allow){
        TreeIterBuilderDefault<A> bldr = clone();
        bldr.allow = allow;
        return bldr;
    }

    public TreeIterBuilder<A> pushFirst(){
        TreeIterBuilderDefault<A> bldr = clone();
        bldr.push = TreeIterator.pushFirst();
        return bldr;
    }

    public TreeIterBuilder<A> pushLast(){
        TreeIterBuilderDefault<A> bldr = clone();
        bldr.push = TreeIterator.pushLast();
        return bldr;
    }

    public TreeIterBuilder<A> pushOrdered(){
        TreeIterBuilderDefault<A> bldr = clone();
        bldr.push = TreeIterator.pushOrdered();
        return bldr;
    }

    public TreeIterBuilder<A> pollFirst(){
        TreeIterBuilderDefault<A> bldr = clone();
        bldr.poll = TreeIterator.pollFirst();
        return bldr;
    }

    public TreeIterBuilder<A> pollLast(){
        TreeIterBuilderDefault<A> bldr = clone();
        bldr.poll = TreeIterator.pollLast();
        return bldr;
    }

    public TreeIterBuilder<A> checkCycles(){
        TreeIterBuilderDefault<A> bldr = clone();
        bldr.allow = TreeIterator.checkCycles();
        return bldr;
    }
}
