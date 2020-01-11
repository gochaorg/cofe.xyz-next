package xyz.cofe.collection.tree;

import org.junit.Assert;
import org.junit.Test;
import xyz.cofe.collection.*;
import xyz.cofe.iter.TreeStep;
import xyz.cofe.txt.Str;

public class IndexTreeTest {
    public static class TStr implements IndexTree<TStr> {
        public TStr(){}
        public TStr(String str){
            setValue(str);
        }

        private String value;
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        @Override
        public String toString(){
            return "TStr{" +
                "value='" + value + '\'' +
                '}';
        }
    }

    private void printTree(TStr root){
        System.out.println("tree:");
        for( TreeStep<TStr> ts : root.walk().tree() ){
            if( ts.getLevel()>0 ) System.out.print(Str.repeat("..",ts.getLevel()));
            System.out.print(ts.getNode().getValue());

            int ldiff = 5 - ts.getLevel();
            if( ldiff>0 ) System.out.print(Str.repeat("  ",ldiff));
            System.out.println(
                " path: "+ts.getNode().path().stream().map(m->m.getValue()).reduce((a,b) -> a+"/"+b).orElse("null"));
        }
    }

    @Test
    public void test01(){
        TStr root = new TStr("a");
        root.addTreeListener(new TreeEvent.Listener<TStr>() {
            @Override
            public void treeEvent(TreeEvent<TStr> event) {
                if( event instanceof UpdatedEvent ){
                    UpdatedEvent<TStr,Integer,TStr> ev = (UpdatedEvent) event;
                    System.out.println(
                        "update parent="+ev.getSource().getValue()+
                            " idx="+ev.getIndex()+
                            " old="+ev.getOldItem().getValue()+
                            " new="+ev.getNewItem().getValue()
                    );
                }else if( event instanceof InsertedEvent){
                    InsertedEvent<TStr,Integer,TStr> ev = (InsertedEvent) event;
                    System.out.println(
                        "inserted parent="+ev.getSource().getValue()+
                            " idx="+ev.getIndex()+
                            //" old="+ev.getOldItem().getValue()+
                            " new="+ev.getNewItem().getValue()
                    );
                }else if( event instanceof DeletedEvent ){
                    DeletedEvent<TStr,Integer,TStr> ev = (DeletedEvent) event;
                    System.out.println(
                        "deleted parent="+ev.getSource().getValue()+
                            " idx="+ev.getIndex()+
                            " old="+ev.getOldItem().getValue()
                            //+" new="+ev.getNewItem().getValue()
                    );
                }else if( event instanceof TreeEvent.Added ){
                    TreeEvent.Added<TStr> ev = (TreeEvent.Added)event;
                    System.out.println("added parent="+ev.getParent().getValue()+" child="+ev.getChild().getValue());
                }else {
                    System.out.println("event "+event);
                }
            }
        });

        TStr nB = new TStr("b");
        root.appends(nB);

        TStr nC = new TStr("c");
        root.appends(nC);

        printTree(root);
        System.out.println("root nodesCount="+root.getNodesCount());

        TStr nE = new TStr("e");
        nB.appends(nE);

        TStr nF = new TStr("f");
        nB.appends(nF);

        printTree(root);
        System.out.println("root nodesCount="+root.getNodesCount());
        Assert.assertTrue(root.getNodesCount()==5);

        System.out.println("root roffset="+root.getRootOffset());
        Assert.assertTrue(root.getRootOffset()==0);

        System.out.println("b roffset="+nB.getRootOffset());
        Assert.assertTrue(nB.getRootOffset()==1);

        System.out.println("b doff=1 "+nB.deepOffset(1));
        Assert.assertTrue(nB.deepOffset(1)!=null);
        Assert.assertTrue("e".equals(nB.deepOffset(1).getValue()) );

        System.out.println("b doff=-1 "+nB.deepOffset(-1));

        System.out.println("e roffset="+nE.getRootOffset());
        System.out.println("e doff=-1 "+nE.deepOffset(-1));
        System.out.println("e doff=-2 "+nE.deepOffset(-2));
        Assert.assertTrue("a".equals(nE.deepOffset(-2).getValue()) );

        System.out.println("e doff=-3 "+nE.deepOffset(-3));

        System.out.println("f roffset="+nF.getRootOffset());
        System.out.println("c roffset="+nC.getRootOffset());
        //Assert.assertTrue(root.getRootOffset()==0);
    }
}
