package xyz.cofe.collection.tree;

import org.junit.Test;
import xyz.cofe.collection.Tree;
import xyz.cofe.iter.TreeStep;

public class TreeTest {
    public class TreeString implements Tree<TreeString> {
        protected String value;

        public TreeString(String val){
            this.value = val;
        }

        public String getValue() {
            return value;
        }
    }

    @Test
    public void test01(){
        TreeString tree = new TreeString("a");
        TreeString nb = new TreeString("b");
        TreeString nc = new TreeString("c");
        TreeString nd = new TreeString("d");
        TreeString ne = new TreeString("e");
        tree.appends(nb,nc);
        nc.appends(nd,ne);

        for( TreeStep<TreeString> ts : tree.walk().tree() ){
            if( ts.getLevel()>0 ){
                for(int i=0; i<ts.getLevel(); i++ ){
                    System.out.print("..");
                }
            }
            System.out.print(ts.getNode().getValue());
            System.out.println();
        }

        for( String val : tree.walk().tree().map(x->x.getNode().getValue()) ){
            System.out.print(val);
            System.out.println();
        }
    }
}
