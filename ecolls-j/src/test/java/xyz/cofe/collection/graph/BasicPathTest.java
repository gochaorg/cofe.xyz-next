package xyz.cofe.collection.graph;

import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.io.PrintStream;
import java.util.List;

public class BasicPathTest {
    public BasicPathTest() {
    }

    public static final PrintStream out = System.out;

    @Test
    public void test01(){
        out.println("test01");
        out.println("=========");

        BasicPath<String,String> p = new BasicPath<>();
        assertTrue(p.nodeCount()==0);
        assertTrue(p.isEmpty());

        p = p.start("a");
        assertTrue(p.nodeCount()==1);
        assertTrue(!p.isEmpty());

        p = p.join("b", "ab");
        assertTrue(p.nodeCount()==2);

        List<String> edgs = p.edges(0, 1);
        assertTrue(edgs.size()==1);
        assertTrue("ab".equals(edgs.get(0)));

        p = p.join("c", "bc");
        assertTrue(p.nodeCount()==3);

        edgs = p.edges(1, 2);
        assertTrue(edgs.size()==1);
        assertTrue("bc".equals(edgs.get(0)));

        assertTrue(p.has("a"));
        assertTrue(p.has("b"));
        assertTrue(p.has("c"));
        assertTrue(!p.has("d"));

        assertTrue("a".equals(p.node(0)));
        assertTrue("b".equals(p.node(1)));
        assertTrue("c".equals(p.node(2)));
        assertTrue(p.node(3)==null);

        assertTrue("c".equals(p.node(-1)));
        assertTrue("b".equals(p.node(-2)));
        assertTrue("a".equals(p.node(-3)));
        assertTrue(p.node(-4)==null);

        BasicPath<String,String> pclone = p.clone();

        assertTrue(pclone!=null);
        assertTrue(pclone.nodeCount() == p.nodeCount());
        for( int ni=0; ni<p.nodeCount(); ni++ ){
            assertTrue(pclone.node(ni)!=null);
            assertTrue(pclone.node(ni).equals(p.node(ni)));
        }

        for( int ni=0; ni<p.nodeCount()-1; ni++ ){
            List<String> ed1 = p.edges(ni, ni+1);
            List<String> ed2 = pclone.edges(ni, ni+1);
            assertTrue(ed1!=null);
            assertTrue(ed2!=null);
            assertTrue(ed1.size() == ed2.size());
            for( int ei=0; ei<ed1.size(); ei++ ){
                assertTrue( ed1.get(ei)!=null );
                assertTrue( ed1.get(ei).equals(ed2.get(ei)) );
            }
        }

        p = p.clear();
        assertTrue(p.nodeCount()==0);
        assertTrue(p.isEmpty());

        p = p.join("a", null);
        p = p.join("b", "ab");
        p = p.join("c", "bc");
        p = p.join("a", "ca");

        int nc = p.nodeCount();
        assertTrue(nc == 4);
        assertTrue(p.hasCycles());
    }

    public void print( Path p ){
        if( p==null ){
            out.print("null");
            return;
        }

        int nc = p.nodeCount();
        if( nc==0 ){
            out.print("empty");
            return;
        }

        if( nc==1 ){
            out.print(p.node(0));
            return;
        }

        for( int ni=0; ni<nc-1; ni++ ){
            if( ni>0 ){
                out.print(" => ");
            }

            Object na = p.node(ni);
            Object nb = p.node(ni+1);
            Object e = p.edge(ni, ni+1);

            BasicPathTest.this.print(na, nb, e);
        }
    }
    public void print( Object na, Object nb, Object e ){
        out.print( na );
        out.print( "(" );
        out.print( e );
        out.print( ")" );
        out.print( nb );
    }
    public void print( Edge ed ){
        if( ed==null ){
            out.print("null");
            return;
        }

        Object na = ed.getNodeA();
        Object nb = ed.getNodeB();
        Object e = ed.getEdge();

        BasicPathTest.this.print(na, nb, e);
    }
    public void println( Path p ){
        BasicPathTest.this.print(p);
        out.println();
    }
    public void print( List<Edge<?,?>> edges ){
        if( edges==null ){
            out.print("null");
            return;
        }

        if( edges.size()==0 ){
            out.print("empty");
            return;
        }

        int edIdx = -1;
        for( Edge ed : edges ){
            edIdx++;
            if( edIdx>0 ){
                out.print(" => ");
            }
            BasicPathTest.this.print(ed);
        }
    }
    public void eprintln( List edges ){
        print(edges);
        out.println();
    }

    @Test
    public void fetchTest(){
        out.println("fetchTest");
        out.println("=====================");

        BasicPath<String,String> p = new BasicPath<>();
        p = p.start("a");
        p = p.join("b","ab");
        p = p.join("c","bc");
        p = p.join("d","cd");

        out.println("source path");
        println(p);

        List<Edge<String,String>> edges = p.fetch(0, p.nodeCount());
        out.println("fetch all");
        eprintln( edges );
        assertTrue(edges!=null);
        assertTrue(edges.size()==3);

        assertTrue(edges.get(0)!=null);
        assertTrue(edges.get(1)!=null);
        assertTrue(edges.get(2)!=null);

        assertTrue("a".equals(edges.get(0).getNodeA()));
        assertTrue("b".equals(edges.get(0).getNodeB()));

        assertTrue("b".equals(edges.get(1).getNodeA()));
        assertTrue("c".equals(edges.get(1).getNodeB()));

        assertTrue("c".equals(edges.get(2).getNodeA()));
        assertTrue("d".equals(edges.get(2).getNodeB()));

        out.println("reverse all");
        edges = p.fetch(p.nodeCount(), 0);
        eprintln(edges);

        assertTrue(edges!=null);
        assertTrue(edges.size()==3);

        assertTrue(edges.get(0)!=null);
        assertTrue(edges.get(1)!=null);
        assertTrue(edges.get(2)!=null);

        assertTrue("d".equals(edges.get(0).getNodeA()));
        assertTrue("c".equals(edges.get(0).getNodeB()));

        assertTrue("c".equals(edges.get(1).getNodeA()));
        assertTrue("b".equals(edges.get(1).getNodeB()));

        assertTrue("b".equals(edges.get(2).getNodeA()));
        assertTrue("a".equals(edges.get(2).getNodeB()));

        out.println("segment 1,2");
        edges = p.fetch(1, 2);
        eprintln(edges);

        assertTrue(edges!=null);
        assertTrue(edges.size()==1);

        assertTrue(edges.get(0)!=null);
        assertTrue("b".equals(edges.get(0).getNodeA()));
        assertTrue("c".equals(edges.get(0).getNodeB()));

        out.println("segment 2,1");
        edges = p.fetch(2,1);
        eprintln(edges);

        assertTrue(edges!=null);
        assertTrue(edges.size()==1);

        assertTrue(edges.get(0)!=null);
        assertTrue("c".equals(edges.get(0).getNodeA()));
        assertTrue("b".equals(edges.get(0).getNodeB()));

        out.println("segment -2,-1");
        edges = p.fetch(-2,-1);
        eprintln(edges);

        assertTrue(edges!=null);
        assertTrue(edges.size()==1);

        assertTrue(edges.get(0)!=null);
        assertTrue("c".equals(edges.get(0).getNodeA()));
        assertTrue("d".equals(edges.get(0).getNodeB()));
    }
}
