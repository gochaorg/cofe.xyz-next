package xyz.cofe.collection;

import xyz.cofe.fn.Triple;

import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;

public class UpTreeImpl {
    protected static final WeakHashMap<UpTree,UpTree> childParentRef = new WeakHashMap<>();
    public static UpTree getParent( UpTree child ){
        if( child == null )throw new IllegalArgumentException( "child == null" );
        return childParentRef.get(child);
    }
    public static void setParent(UpTree child, UpTree parent){
        if( child == null )throw new IllegalArgumentException( "child == null" );
        if( parent==null ){
            childParentRef.remove(child);
        }else {
            childParentRef.put(child, parent);
        }
    }
    public static boolean compareAndSetParent(UpTree child, UpTree parent, UpTree newparent){
        if( child == null )throw new IllegalArgumentException( "child == null" );
        synchronized ( childParentRef ) {
            UpTree p = getParent(child);
            if( Objects.equals(p, parent) ){
                setParent(child, newparent);
                return true;
            }
            return false;
        }
    }

    public static <A extends UpTree<A>> int sibIndex(UpTree sib) {
        if( sib==null )throw new IllegalArgumentException("sib == null");
        UpTree prnt = getParent(sib);
        if( prnt==null )return -1;
        List<Tree> lst = TreeImpl.nodesOf(prnt);
        return lst.indexOf(sib);
    }

    @SuppressWarnings("unchecked")
    public static <A extends UpTree<A>> A sibling(A node, int sib) {
        if(node==null)throw new IllegalArgumentException("node == null");
        if(sib==0)return node;

        UpTree prnt = getParent(node);
        if(prnt==null)return null;

        List<Tree> lst = TreeImpl.nodesOf(prnt);
        if(lst==null)return null;

        int idx = lst.indexOf(node);
        if( idx<0 )return null;

        int tidx = idx+sib;
        if(tidx<0)return null;
        if(tidx>=lst.size())return null;
        return (A)lst.get(tidx);
    }

    public static <A extends UpTree<A>> void postInsert(UpTree<A> ut, A node, List<Triple<Integer, A, A>> added) {
        if( ut==null ) throw new IllegalArgumentException("ut==null");
        if( node!=null ){
            node.setParent((A)ut);
        }
        if( added!=null && added.size()==1 ){
            ut.treeNotify(new TreeEvent.Inserted<A>(ut, node, added.get(0).a()));
        }else {
            ut.treeNotify(new TreeEvent.Added<A>(ut, node));
        }
    }
}
