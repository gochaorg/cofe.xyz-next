package xyz.cofe.collection;

import xyz.cofe.ecolls.Triple;

import java.util.ArrayList;
import java.util.List;

/**
 * Узел дерева с поддержкой родительского узла
 * @param <A> тип узла дерева
 */
@SuppressWarnings("unchecked")
public interface UpTree<A extends UpTree<A>> extends Tree<A>, GetTreeParent<A>, TreeNotify<A> {
    default UpTree<? extends A> getParent(){ return UpTreeImpl.getParent(this); }
    default void setParent(UpTree<A> parent){
        UpTreeImpl.setParent(this, parent);
    }
    default boolean compareAndSetParent(UpTree parent, UpTree newparent){
        return UpTreeImpl.compareAndSetParent(this, parent, newparent);
    }

    @Override
    default void append(A node) {
        List<Triple<Integer,A,A>> added = TreeImpl.append(this,node);
        UpTreeImpl.postInsert(this,node, added);
    }

//    private void postInsert(A node, List<Triple<Integer, A, A>> added) {
//        if( node!=null ){
//            node.setParent(this);
//        }
//        if( added!=null && added.size()==1 ){
//            treeNotify(new TreeEvent.Inserted<A>(this, node, added.get(0).a()));
//        }else {
//            treeNotify(new TreeEvent.Added<A>(this, node));
//        }
//    }

    @Override
    default void appends(A... nodes) {
        TreeImpl.append(this,nodes, t -> {
            if( t!=null )t.c().setParent(this);
            treeNotify(new TreeEvent.Inserted<A>(this, t.c(), t.a()));
        });
    }

    @Override
    default void appends(Iterable<A> nodes) {
        TreeImpl.append(this,nodes, t -> {
            if( t.c()!=null )t.c().setParent(this);
            treeNotify(new TreeEvent.Inserted<A>(this, t.c(), t.a()));
        });
    }

    @Override
    default void insert(int idx, A node) {
        List<Triple<Integer,A,A>> added = TreeImpl.insert(this,idx, node);
        UpTreeImpl.postInsert(this, node, added);
    }

    @Override
    default void inserts(int idx, A... nodes) {
        TreeImpl.insert(this,idx, nodes, t-> {
            if( t.c()!=null )t.c().setParent(this);
            treeNotify(new TreeEvent.Inserted<A>(this, t.c(), t.a()));
        });
    }

    @Override
    default void inserts(int idx, Iterable<A> nodes) {
        TreeImpl.insert(this,idx, nodes, t-> {
            if( t.c()!=null )t.c().setParent(this);
            treeNotify(new TreeEvent.Inserted<A>(this, t.c(), t.a()));
        });
    }

    @Override
    default void set(int idx, A node) {
        List<Triple<Integer,A,A>> updates = TreeImpl.set(this,idx, node);
        if( node!=null )node.setParent(this);
        if( updates!=null ){
            updates.forEach( e -> treeNotify(new TreeEvent.Updated<A>(this, e.a(), e.b(), e.c())) );
        }
    }

    @Override
    default void sets(int idx, A... nodes) {
        TreeImpl.set(this,idx, nodes, t-> {
            if( t!=null )t.c().setParent(this);
            treeNotify( new TreeEvent.Updated<A>(this, t.a(), t.b(), t.c()) );
        });
    }

    @Override
    default void sets(int idx, Iterable<A> nodes) {
        TreeImpl.set(this,idx, nodes, t-> {
            if( t!=null )t.c().setParent(this);
            treeNotify( new TreeEvent.Updated<A>(this, t.a(), t.b(), t.c()) );
        });
    }

    default int level(){
        int level = 0;
        UpTree ptr = this;
        while( ptr!=null ){
            ptr = ptr.getParent();
            level++;
        }
        return level;
    }

    default List<A> path(){
        ArrayList<A> path = new ArrayList<>();
        A ptr = (A)this;
        while( ptr!=null ){
            path.add(0,ptr);
            ptr = (A)ptr.getParent();
        }
        return path;
    }

    @Override
    default void remove(int indexes) {
        TreeImpl.deleteByIndex(this, new int[]{indexes}, t-> {
            if( t!=null )t.b().compareAndSetParent(this,null);
            treeNotify(new TreeEvent.Removed<A>(this, t.b()));
        });
    }

    @Override
    default void removes(int... indexes) {
        TreeImpl.deleteByIndex(this, indexes,t-> {
            if( t!=null )t.b().compareAndSetParent(this,null);
            treeNotify(new TreeEvent.Removed<A>(this, t.b()));
        });
    }

    @Override
    default void removes(Iterable<Integer> indexes) {
        TreeImpl.deleteByIndex(this, indexes,t-> {
            if( t!=null )t.b().compareAndSetParent(this,null);
            treeNotify(new TreeEvent.Removed<A>(this, t.b()));
        });
    }

    @Override
    default void delete(A node) {
        deletes(node);
    }

    @Override
    default void deletes(A... nodes) {
        TreeImpl.deleteByValue(this, nodes, t-> {
            if( t!=null )t.b().compareAndSetParent(this,null);
            treeNotify(new TreeEvent.Removed<A>(this, t.b()));
        });
    }

    @Override
    default void deletes(Iterable<A> nodes) {
        TreeImpl.deleteByValue(this, nodes, t-> {
            if( t!=null )t.b().compareAndSetParent(this,null);
            treeNotify(new TreeEvent.Removed<A>(this, t.b()));
        });
    }

    default int getSibIndex(){
        return UpTreeImpl.sibIndex(this);
    }

    default A sibling(int offset){
        return UpTreeImpl.sibling((A)this,offset);
    }

    /**
     * Возвращает следующий соседний узел
     * @return Узел или null в случаи достижения края
     */
    default A getPreviousSibling(){
        return sibling(-1);
    }

    /**
     * Возвращает следующий соседний узел
     * @return Узел или null в случаи достижения края
     */
    default A getNextSibling(){
        return sibling(1);
    }
}
