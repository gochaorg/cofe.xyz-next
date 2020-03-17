package xyz.cofe.iter;

import org.junit.Test;
import xyz.cofe.collection.IndexTree;
import xyz.cofe.collection.MutableTree;
import xyz.cofe.collection.Tree;
import xyz.cofe.collection.UpTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

public class TreeSplitTest {
    public static class Node extends MutableTree<Node> {
        public Node(String value) {
            this.value = value;
        }

        public Node() { }

        protected String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public Node generate(Node root, int deep, int sibs, AtomicInteger idseq){
        if( idseq==null )idseq = new AtomicInteger(0);
        if( root==null )root = new Node("n"+idseq.incrementAndGet());

        if( sibs>0 ){
            for( int i=0; i<sibs; i++ ){
                Node n = new Node("n"+idseq.incrementAndGet());
                root.append(n);
                if( deep>0 ){
                    generate(n, deep-1, sibs, idseq);
                }
            }
        }
        return root;
    }

    public void dumpNodes( Node n ){
        n.walk().tree().forEach( ts -> {
            System.out.println(
                "node: "+(ts.nodes().map(Node::getValue).reduce("",(r, t)->r+"/"+t))
            );
        });
    }

    public long nodesTextLen( Node root ){
        long s = 0;
        for( Node n : root.walk().go() ){
            s += n.getValue().length();
        }
        return s;
    }

    private static AtomicInteger splitCnt = new AtomicInteger(0);

    public static class TreeWalker implements Spliterator<Node> {
        public TreeWalker(List<Node> workset){
            this.workset = workset!=null ? workset : new ArrayList<>();
        }

        public TreeWalker(Node node){
            this.workset = new ArrayList<>();
            if( node!=null ){
                this.workset.add(node);
            }
        }

        protected List<Node> workset;

        @Override
        public boolean tryAdvance(Consumer<? super Node> action) {
            if( action==null )return false;
            if( workset.isEmpty() )return false;
            Node n = workset.remove(workset.size()-1);
            action.accept(n);
            workset.addAll( n.children() );
            return true;
        }

        @Override
        public TreeWalker trySplit() {
            if( workset.size()<2 )return null;
            int mid = workset.size() / 2;
            List<Node> left = workset.subList(0,mid);
            List<Node> right = workset.subList(mid,workset.size());
            workset = right;
            splitCnt.incrementAndGet();
            return new TreeWalker(left);
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return Spliterator.DISTINCT | Spliterator.NONNULL;
        }
    }

    public long nodesTextLen2( Node root ){
        return StreamSupport.stream(new TreeWalker(root),true)
            .map( n -> ((long) n.getValue().length()) )
            .reduce(0L,(a,b)->a+b);
    }

    @Test
    public void test01(){
        long t0 = System.nanoTime();
        Node n = generate(null, 6, 8, null);
        long t1 = System.nanoTime();

        System.out.println("nodes "+n.getNodesCount());
        long t2 = System.nanoTime();

        System.out.println("walk A "+nodesTextLen(n));
        long t3 = System.nanoTime();

        System.out.println("walk B "+nodesTextLen2(n));
        long t4 = System.nanoTime();

        System.out.println("generate time="+(t1-t0)/Math.pow(10,6)+"ms");
        System.out.println("walk a time="+(t3-t2)/Math.pow(10,6)+"ms");
        System.out.println("walk b time="+(t4-t3)/Math.pow(10,6)+"ms");
        System.out.println("split count="+splitCnt);
    }
}
