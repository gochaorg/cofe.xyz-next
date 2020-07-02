package xyz.cofe.collection;

import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.iter.Eterable;
import xyz.cofe.iter.EterableProxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;

/**
 * Базовая реализация tree
 * @param <SELF> SELF тип производный от BasicTree
 */
public abstract class BasicTree<SELF extends BasicTree<SELF>>
    implements Tree<SELF>, UpTree<SELF>, IndexTree<SELF>
{
    //region parent : SELF
    protected SELF parent;

    @Override
    public SELF getParent(){
        return parent;
    }

    @Override
    public void setParent( SELF parent ){
        this.parent = parent;
    }

    @Override
    public boolean compareAndSetParent( SELF parent, SELF newparent ){
        if( this.parent == parent ){
            this.parent = newparent;
            return true;
        }
        return false;
    }
    //endregion

    //region childre() : List<SELF>
    protected final List<SELF> childrenList = new ArrayList<>();
    protected List<SELF> children(){ return childrenList; };
    //endregion

    //region Уведомление о изменении списка дочерних узлов
    /**
     * Уведомление о изменении списка дочерних узлов
     * @param idx индекс дочернего узла
     * @param old старый узел
     * @param cur новый узел
     */
    protected void changes( int idx, SELF old, SELF cur ){
        if( cur!=null ){
            // inserted or updated
            //noinspection unchecked
            cur.setParent((SELF)this);
        }else if( old!=null ) {
            //noinspection unchecked
            old.compareAndSetParent( (SELF)this, null );
        }

        if( old!=null && cur!=null ){
            // updated
            treeNotify( new TreeEvent.Updated<SELF>(this, idx, old, cur) );
        }else if( old!=null ){
            // deleted
            treeNotify( new TreeEvent.Deleted<SELF>(this, old, idx) );
        }else if( cur!=null ){
            // inserted
            treeNotify( new TreeEvent.Inserted<SELF>(this, cur, idx) );
        }
    }

    /**
     * Помощь для работы с подписчиками
     */
    protected final ListenersHelper<TreeEvent.Listener<SELF>, TreeEvent<SELF>> listenersHelper =
        new ListenersHelper<>((ls,ev)->{
            if( ls!=null ){
                ls.treeEvent(ev);
            }});

    @Override
    public void treeNotify( TreeEvent<SELF> event ){
        if( event==null )throw new IllegalArgumentException("event==null");
        nodesCount = -1;

        listenersHelper.fireEvent(event);

        SELF prnt = getParent();
        if( prnt != null ){
            prnt.treeNotify(event);
        }
    }

    @Override
    public AutoCloseable addTreeListener( TreeEvent.Listener<SELF> ls ){
        if( ls==null )throw new IllegalArgumentException("ls==null");
        return listenersHelper.addListener(ls);
    }

    @Override
    public <EV extends TreeEvent<SELF>> AutoCloseable listen( Class<EV> eventClass, Consumer<EV> listener ){
        if( eventClass==null )throw new IllegalArgumentException("eventClass==null");
        if( listener==null )throw new IllegalArgumentException("listener==null");
        return addTreeListener(ev -> {
            if( ev==null )return;
            //noinspection rawtypes
            Class c = ev.getClass();
            if( eventClass.isAssignableFrom(c) ){
                //noinspection unchecked
                listener.accept((EV)ev);
            }
        });
    }

    @Override
    public AutoCloseable addTreeListener( boolean weak, TreeEvent.Listener<SELF> ls ){
        if( ls==null )throw new IllegalArgumentException("ls==null");
        return listenersHelper.addListener(ls,weak);
    }

    @Override
    public void removeTreeListener( TreeEvent.Listener<SELF> ls ){
        if( ls==null )throw new IllegalArgumentException("ls==null");
        listenersHelper.removeListener(ls);
    }

    @Override
    public Set<TreeEvent.Listener<SELF>> getTreeListeners(){
        return listenersHelper.getListeners();
    }

    @Override
    public void removeAllTreeListeners(){
        listenersHelper.removeAllListeners();
    }
    //endregion

    //region Чтение дочерних/соседних узлов
    /**
     * Возвращает кол-во дочерних элементов
     * @return кол-во элементов
     */
    @Override
    public int count(){ return children().size(); }

    protected int nodesCount = -1;

    /**
     * Возвращает кол-во узлов включая себя и всех вложенных
     * @return кол-во узлов (всегда больше 0)
     */
    public int getNodesCount(){
        if( nodesCount<0 ){
            nodesCount = 1;
            for( SELF n : nodes() ){
                nodesCount += n.getNodesCount();
            }
        }
        return nodesCount;
    }

    /**
     * Возвращает дочерний элемент по его индексу
     * @param idx индекс дочернего элемента
     * @return дочерний элемент
     */
    @Override
    public SELF get( int idx ){ return children().get(idx); }

    /**
     * Возвращает дочерние элементы
     * @return дочерние элементы
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Eterable<SELF> nodes(){
        List<SELF> lst = children();
        if( lst instanceof Eterable ){
            return (Eterable)lst;
        }
        return new EterableProxy(lst){
            @Override
            public List toList(){
                return lst;
            }
        };
    }

    /**
     * Возвращает индекс узла в списке дочерних узлов по отношению к родителю
     * @return индекс или -1
     */
    @Override
    public int getSibIndex(){
        SELF prnt = getParent();
        if( prnt==null )return -1;

        //noinspection SuspiciousMethodCalls
        return prnt.childrenList.indexOf(this);
    }

    /**
     * Переходит соседнему узлу
     * @param offset смещение от текущего
     * @return узел или null
     */
    @Override
    public SELF sibling( int offset ){
        if( offset==0 )//noinspection unchecked
            return (SELF) this;

        SELF prnt = getParent();
        if( prnt==null )return null;

        //noinspection SuspiciousMethodCalls
        int selfIdx = prnt.childrenList.indexOf(this);
        if( selfIdx<0 )return null;

        int tidx = selfIdx + offset;
        if( tidx<0 )return null;
        if( tidx>=prnt.childrenList.size() )return null;

        return prnt.childrenList.get(tidx);
    }

    /**
     * Возвращает предыдущий соседний узел
     * @return Узел или null в случаи достижения края
     */
    @Override
    public SELF getPreviousSibling(){
        return sibling(-1);
    }

    /**
     * Возвращает следующий соседний узел
     * @return Узел или null в случаи достижения края
     */
    @Override
    public SELF getNextSibling(){
        return sibling(1);
    }
    //endregion

    //region Модификация дочерних узлов
    /**
     * Добавляет дочерний узел
     * @param node узел
     */
    @Override
    public void append( SELF node ){
        if( node==null )throw new IllegalArgumentException("node==null");
        children().add(node);
        changes( children().size()-1, null, node );
    }

    /**
     * Добавляет дочерние узлы
     * @param nodes дочерние узлы
     */
    @SafeVarargs
    @Override
    public final void appends( SELF... nodes ){
        if( nodes==null )throw new IllegalArgumentException("node==null");
        TreeListImpl.append(children(),Arrays.asList(nodes),this::changes);
    }

    /**
     * Добавляет дочерние узлы
     * @param nodes дочерние узлы
     */
    @Override
    public void appends( Iterable<SELF> nodes ){
        if( nodes==null )throw new IllegalArgumentException("nodes==null");
        TreeListImpl.append(children(),nodes,this::changes);
    }

    /**
     * Добавляет дочерний узел
     * @param idx индекс в какую позицию будет добавлен узел
     * @param node узел
     */
    @Override
    public void insert( int idx, SELF node ){
        if( node==null )throw new IllegalArgumentException("node==null");
        children().add(idx, node);
        changes( idx, null, node );
    }

    /**
     * Добавляет дочерние узлы
     * @param idx индекс в какую позицию будет добавлены узлы
     * @param nodes дочерние узлы
     */
    @SafeVarargs
    @Override
    public final void inserts( int idx, SELF... nodes ){
        if( nodes==null )throw new IllegalArgumentException("nodes==null");
        TreeListImpl.insert(children(),idx,Arrays.asList(nodes),this::changes);
    }

    /**
     * Добавляет дочерние узлы
     * @param idx индекс в какую позицию будет добавлены узлы
     * @param nodes дочерние узлы
     */
    @Override
    public void inserts( int idx, Iterable<SELF> nodes ){
        if( nodes==null )throw new IllegalArgumentException("nodes==null");
        TreeListImpl.insert(children(),idx,nodes,this::changes);
    }

    /**
     * Указывает/заменяет узлы
     * @param idx индекс
     * @param node узел
     */
    @Override
    public void set( int idx, SELF node ){
        if( node==null )throw new IllegalArgumentException("node==null");
        List<SELF> lst = children();
        SELF old = lst.set(idx, node);
        changes(idx,old,node);
    }

    /**
     * Указывает/заменяет узлы
     * @param idx индекс
     * @param nodes узелы
     */
    @SafeVarargs
    @Override
    public final void sets( int idx, SELF... nodes ){
        if( nodes==null )throw new IllegalArgumentException("nodes==null");
        TreeListImpl.set(children(),idx,Arrays.asList(nodes),this::changes);
    }

    /**
     * Указывает/заменяет узлы
     * @param idx индекс
     * @param nodes узелы
     */
    @Override
    public void sets( int idx, Iterable<SELF> nodes ){
        if( nodes==null )throw new IllegalArgumentException("nodes==null");
        TreeListImpl.set(children(),idx,nodes,this::changes);
    }

    /**
     * Удаляет дочерний узел
     * @param index индексы узлов
     */
    @Override
    public void remove( int index ){
        TreeListImpl.deleteByIndex(children(),Eterable.of(index),this::changes);
    }

    /**
     * Удаляет дочерние узлы
     * @param indexes индексы узлов
     */
    @Override
    public void removes( int... indexes ){
        if( indexes==null )throw new IllegalArgumentException("indexes==null");
        ArrayList<Integer> idxs = new ArrayList<>();
        for( int i : indexes )idxs.add(i);
        TreeListImpl.deleteByIndex(children(),idxs,this::changes);
    }

    /**
     * Удаляет дочерние узлы
     * @param indexes индексы узлов
     */
    @Override
    public void removes( Iterable<Integer> indexes ){
        if( indexes==null )throw new IllegalArgumentException("indexes==null");
        TreeListImpl.deleteByIndex(children(),indexes,this::changes);
    }

    /**
     * Удаляет дочерние узлы
     * @param node дочерние узлы
     */
    @Override
    public void delete( SELF node ){
        if( node==null )throw new IllegalArgumentException("node==null");
        //noinspection ArraysAsListWithZeroOrOneArgument
        TreeListImpl.deleteByValue(children(),Arrays.asList(node),this::changes);
    }

    /**
     * Удаляет дочерние узлы
     * @param nodes дочерние узлы
     */
    @SafeVarargs
    @Override
    public final void deletes( SELF... nodes ){
        if( nodes==null )throw new IllegalArgumentException("nodes==null");
        TreeListImpl.deleteByValue(children(),Arrays.asList(nodes),this::changes);
    }

    /**
     * Удаляет дочерние узлы
     * @param nodes дочерние узлы
     */
    @Override
    public void deletes( Iterable<SELF> nodes ){
        if( nodes==null )throw new IllegalArgumentException("nodes==null");
        TreeListImpl.deleteByValue(children(),nodes,this::changes);
    }

    /**
     * Удаляет дочерние узлы
     */
    @Override
    public void clear(){
        TreeListImpl.clear(children(),this::changes);
    }
    //endregion
}
