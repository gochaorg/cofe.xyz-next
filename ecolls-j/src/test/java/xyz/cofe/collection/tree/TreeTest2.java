package xyz.cofe.collection.tree;

import org.junit.Assert;
import org.junit.Test;
import xyz.cofe.collection.*;
import xyz.cofe.iter.TreeStep;
import xyz.cofe.txt.Str;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class TreeTest2 {
    public static class TStr implements UpTree<TStr> {
        public TStr(){}
        public TStr(String str){
            setValue(str);
        }

        private String value;
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
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
        Builder<TStr,String> bld = new Builder<>(TStr::new, TStr::setValue)
            .val("a")
                .add("b", c -> c
                    .add("d")
                    .add("e"))
                .add("d")
                .add("c");

        TStr root = bld.build();
        printTree(root);
    }

    @Test
    public void test02(){
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

        TStr b = new TStr("b");

        root.appends(b);
        root.appends(new TStr("c"));

        b.appends(new TStr("ba"));
        b.appends(new TStr("bb"));
    }

    @Test
    public void test03(){
        Builder<TStr,String> bld = new Builder<>(TStr::new, TStr::setValue)
            .val("a")
            .add("b", c -> c
                .add("d")
                .add("e")
                .add("f"))
            .add("d")
            .add("c");

        TStr root = bld.build();
        printTree(root);

        TStr nE = root.walk().go().filter( n -> "e".equals(n.getValue()) ).first().get();

        System.out.println("e sidx="+nE.getSibIndex());
        Assert.assertTrue(nE.getSibIndex()==1);

        System.out.println("e prev="+nE.sibling(-1).getValue());
        System.out.println("e next="+nE.sibling(1).getValue());
    }
}
