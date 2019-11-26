package xyz.cofe.iter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Один шаг при обходе дерева
 * @param <A> дерево
 */
public class TreeStep<A> {
    protected final A node;
    protected final TreeStep<A> parent;

    public TreeStep(A node){
        this.node = node;
        this.parent = null;
    }
    public TreeStep(A node,TreeStep<A> parent){
        this.node = node;
        this.parent = parent;
    }

    public A getNode() {
        return node;
    }

    public TreeStep<A> getParent() {
        return parent;
    }

    public TreeStep<A> follow(A a){
        return new TreeStep<>(a,this);
    }

    /**
     * Возвращает уровень вложенности для узла
     * @return 0 - корень
     */
    public int getLevel(){
        TreeStep<A> ts = this;
        int level = 0;
        while( ts.parent!=null ){
            level++;
            ts = ts.parent;
        }
        return level;
    }

    public A[] nodePath(Class<A> nodeClass){
        if( nodeClass == null )throw new IllegalArgumentException( "nodeClass == null" );
        A[] arr = (A[])Array.newInstance(nodeClass,getLevel()+1);
        int idx = arr.length-1;
        TreeStep<A> ts = this;
        while( true ){
            arr[idx] = ts.node;
            ts = ts.parent;
            idx--;
            if(ts==null)break;
        }
        return arr;
    }

    public List<A> nodeList(){
        ArrayList<A> lst = new ArrayList<>();
        TreeStep<A> ts = this;
        while( true ){
            lst.add( 0, ts.node );
            ts = ts.parent;
            if(ts==null)break;
        }
        return lst;
    }

    public Eterable<A> nodes(){
        return Eterable.of(nodeList());
    }

    public void each(Consumer<A> visitor){
        if( visitor == null )throw new IllegalArgumentException( "visitor == null" );
        TreeStep<A> ts = this;
        while( true ){
            visitor.accept(ts.getNode());
            ts = ts.getParent();
            if(ts==null)break;
        }
    }

    public Map<A,Integer> frequency(){
        Map<A,Integer> f = new LinkedHashMap<>();
        each( x->{
            f.put(x,f.getOrDefault(x,0)+1);
        });
        return f;
    }

    public boolean hasCycles(){
        return frequency().values().stream().filter(x->x>1).count()>0;
    }
}
