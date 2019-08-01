package xyz.cofe.iter;

import org.junit.Test;

import java.util.*;
import java.util.function.Function;

public class TreeIteratorTest {
    public static class ParentChildren<A> {
        public final A parent;
        public final List<A> children;
        public ParentChildren(A parent, List<A> children){
            this.parent = parent;
            this.children = children;
        }
    }
    public static class Tree<A> extends HashMap<A,ParentChildren<A>> {
        public Tree<A> add( A parent, A ... children ){
            put( parent, new ParentChildren<>(parent, Arrays.asList(children)));
            return this;
        }
    }
    public Tree<String> validTree(){
        Tree<String> tree = new Tree<>();
        tree.add("a", "a/a", "a/b","a/c");
        tree.add("a/b", "a/b/a", "a/b/b","a/b/c");
        tree.add("a/b/b", "a/b/b/a", "a/b/b/b","a/b/b/c");
        tree.add("a/c", "a/c/a", "a/c/b");
        return tree;
    }
    public static <A> Function<A,Iterable<A>> follow(Tree<A> tree){
        return (a) ->{
            ParentChildren<A> pc = tree.get(a);
            List<A> lst = pc != null ? pc.children : null;
            return lst;
        };
    }

    @Test
    public void test01(){
        System.out.println("follow default");
        for( TreeStep<String> ts : TreeIterator.<String>of("a", follow(validTree())) ){
            System.out.println("visit "+ts.getNode());
        }

        System.out.println("follow poll first, push last");
        for( TreeStep<String> ts : TreeIterator.<String>of(
            "a", follow(validTree()), TreeIterator.pollFirst(), TreeIterator.pushLast()) ){
            System.out.println("visit "+ts.getNode());
        }

        System.out.println("follow poll last, push last");
        for( TreeStep<String> ts : TreeIterator.<String>of(
            "a", follow(validTree()), TreeIterator.pollLast(), TreeIterator.pushLast()) ){
            System.out.println("visit "+ts.getNode());
        }

        System.out.println("follow poll first, push first");
        for( TreeStep<String> ts : TreeIterator.<String>of(
            "a", follow(validTree()), TreeIterator.pollFirst(), TreeIterator.pushFirst()) ){
            System.out.println("visit "+ts.getNode());
        }

        System.out.println("follow poll last, push first");
        for( TreeStep<String> ts : TreeIterator.<String>of(
            "a", follow(validTree()), TreeIterator.pollLast(), TreeIterator.pushFirst()) ){
            System.out.println("visit "+ts.getNode());
        }
    }

    public Tree<String> invalidTree(){
        Tree<String> tree = new Tree<>();
        tree.add("a", "a/a", "a/b","a/c");
        tree.add("a/b", "a/b/a", "a/b/b","a/b/c");
        tree.add("a/b/b", "a", "b");
        tree.add("a/c", "a/c/a", "a/c/b");
        return tree;
    }

    @Test
    public void test02(){
        for( TreeStep<String> ts : TreeIterator.<String>of("a", follow(validTree())) ){
            System.out.println("visit "+ts.getNode());
        }
    }
}
