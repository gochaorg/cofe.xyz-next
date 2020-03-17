package xyz.cofe.collection;

import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.fn.Tuple2;
import xyz.cofe.iter.Eterable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Мутериуемое дерево
 * @param <SELF> Собыственный тип дерева (в дочерних класса должен содержать сам класс)
 */
public abstract class MutableTree<SELF extends MutableTree<SELF>>
    implements Tree<SELF>, GetTreeParent<SELF>, UpTree<SELF>
{
    /**
     * Кол-во дочерних узлов включая вложенные
     */
    protected Integer cacheCount;

    /**
     * Возвращает кол-во дочерних узлов включая вложенные
     * @return кол-во дочерних узлов
     */
    public int getNodesCount(){
        if( cacheCount!=null )return cacheCount;

        AtomicInteger sum = new AtomicInteger(0);
        nodes().forEach( n -> sum.addAndGet(n.getNodesCount()) );

        sum.incrementAndGet();
        cacheCount = sum.get();

        return cacheCount;
    }

    /**
     * Указывает кол-во дочерних узлов включая вложенные
     * @param count кол-во дочерних узлов
     */
    public void setNodesCount(Integer count){
        cacheCount = count;
    }

    /**
     * Подписчики на события дерева
     */
    protected final ListenersHelper<TreeEvent.Listener<SELF>, TreeEvent<SELF>> listeners
        = new ListenersHelper<>(TreeEvent.Listener::treeEvent);

    /**
     * Уведомляет о изменении дерева
     * @param event событие
     */
    @Override
    public void treeNotify(TreeEvent<SELF> event) {
        if( event==null )throw new IllegalArgumentException( "event==null" );
        cacheCount = null;

        listeners.fireEvent(event);

        SELF prnt = this.getParent();
        if( prnt != null ){
            prnt.treeNotify(event);
        }
    }

    /**
     * Добавляет подписчика на события изменения дерева
     * @param ls подписчик
     * @return отписка от уведомлений
     */
    @Override
    public AutoCloseable addTreeListener(TreeEvent.Listener<SELF> ls) {
        if( ls==null )throw new IllegalArgumentException( "ls==null" );
        return listeners.addListener(ls);
    }

    /**
     * Добавляет подписчика на события изменения дерева
     * @param eventClass класс событий
     * @param listener подписчик
     * @return отписка от уведомлений
     */
    @Override
    public <EV extends TreeEvent<SELF>> AutoCloseable listen(Class<EV> eventClass, Consumer<EV> listener) {
        if( eventClass==null )throw new IllegalArgumentException( "eventClass==null" );
        if( listener==null )throw new IllegalArgumentException( "listener==null" );
        return listeners.addListener( ev ->{
            if( ev != null && eventClass.isAssignableFrom(ev.getClass()) ){
                listener.accept((EV)ev);
            }
        });
    }

    /**
     * Добавляет подписчика на события изменения дерева
     * @param ls подписчик
     * @return отписка от уведомлений
     */
    @Override
    public AutoCloseable addTreeListener(boolean weak, TreeEvent.Listener<SELF> ls) {
        return null;
    }

    /**
     * Отписка от уведомлений
     * @param ls подписчик
     */
    @Override
    public void removeTreeListener(TreeEvent.Listener<SELF> ls) {

    }

    /**
     * Возвращает всех одписчиков
     * @return подписчики
     */
    @Override
    public Set<TreeEvent.Listener<SELF>> getTreeListeners() {
        return null;
    }

    /**
     * Удалеяет всех подписчиков
     */
    @Override
    public void removeAllTreeListeners() {

    }

    /**
     * Возвращает индекс узла в списке дочерних узлов по отношению к родителю
     * @return индекс или -1
     */
    @Override
    public int getSibIndex() {
        SELF prnt = getParent();
        if( prnt==null )return -1;
        List<SELF> lst = children();
        return lst.indexOf(this);
    }

    /**
     * Переходит соседнему узлу
     * @param offset смещение от текущего
     * @return узел или null
     */
    @Override
    public SELF sibling(int offset) {
        SELF prnt = getParent();
        if(prnt==null)return null;

        List<SELF> lst = prnt.children();
        if(lst==null)return null;

        int idx = lst.indexOf(this);
        if( idx<0 )return null;

        int tidx = idx+offset;
        if(tidx<0)return null;
        if(tidx>=lst.size())return null;
        return lst.get(tidx);
    }

    /**
     * Возвращает следующий соседний узел
     * @return Узел или null в случаи достижения края
     */
    @Override
    public SELF getPreviousSibling() {
        return sibling(-1);
    }

    /**
     * Возвращает следующий соседний узел
     * @return Узел или null в случаи достижения края
     */
    @Override
    public SELF getNextSibling() {
        return sibling(1);
    }

    protected SELF parent;

    /**
     * Возвращает родитеский узлер текущего узла
     * @return Родительский узел или null
     */
    @Override
    public SELF getParent() {
        return parent;
    }

    /**
     * Укзывает родитеский узлер текущего узла
     * @param parent родитеский узлер текущего узла
     */
    @Override
    public void setParent(SELF parent) {
        this.parent = parent;
    }

    /**
     * Устанавливает новый родительский узел, если текущий узел совпадает с указаным
     * @param parent проверяемый
     * @param newparent новое значение
     * @return true - значение установлено, false - проверяемый и фактический родительские узелы не совпадают
     */
    @Override
    public boolean compareAndSetParent(SELF parent, SELF newparent) {
        if( this.parent == parent ){
            setParent(newparent);
            return true;
        }
        return false;
    }

    protected List<SELF> children = new ArrayList<>();

    /**
     * Возвращает список дочерних узлов
     * @return список дочерних узлов
     */
    public List<SELF> children(){
        return children;
    }

    /**
     * Возвращает кол-во дочерних узлов
     * @return кол-во дочерних узлов
     */
    @Override
    public int count() {
        return children().size();
    }

    /**
     * Возвращает дочерний элемент по его индексу
     * @param idx индекс дочернего элемента
     * @return дочерний элемент
     */
    @Override
    public SELF get(int idx) {
        return children().get(idx);
    }

    /**
     * Возвращает дочерние элементы
     * @return дочерние элементы
     */
    @Override
    public Eterable<SELF> nodes() {
        return Eterable.of(children());
    }

    /**
     * Добавляет дочерний узел
     * @param node узел
     */
    @Override
    public void append(SELF node) {
        children().add(node);
        if( node!=null ){
            node.setParent((SELF)this);
        }
        treeNotify(new TreeEvent.Inserted<>(this,node,children().size()-1));
    }

    /**
     * Добавляет дочерние узлы
     * @param nodes дочерние узлы
     */
    @Override
    public void appends(SELF... nodes) {
        if( nodes==null )throw new IllegalArgumentException( "nodes==null" );
        for(SELF s:nodes){
            append(s);
        }
    }

    /**
     * Добавляет дочерние узлы
     * @param nodes дочерние узлы
     */
    @Override
    public void appends(Iterable<SELF> nodes) {
        if( nodes==null )throw new IllegalArgumentException( "nodes==null" );
        for(SELF s:nodes){
            append(s);
        }
    }

    /**
     * Добавляет дочерний узел
     * @param idx индекс в какую позицию будет добавлен узел
     * @param node узел
     */
    @Override
    public void insert(int idx, SELF node) {
        children().add(idx,node);
        treeNotify(new TreeEvent.Inserted(this,node,idx));
    }

    /**
     * Добавляет дочерние узлы
     * @param idx индекс в какую позицию будет добавлены узлы
     * @param nodes дочерние узлы
     */
    @Override
    public void inserts(int idx, SELF... nodes) {
        if( nodes==null )throw new IllegalArgumentException( "nodes==null" );
        for( int i=0;i<nodes.length;i++ ){
            insert(idx+i,nodes[i]);
        }
    }

    /**
     * Добавляет дочерние узлы
     * @param idx индекс в какую позицию будет добавлены узлы
     * @param nodes дочерние узлы
     */
    @Override
    public void inserts(int idx, Iterable<SELF> nodes) {
        if( nodes==null )throw new IllegalArgumentException( "nodes==null" );
        int i=-1;
        for( SELF n : nodes ){
            i++;
            insert(i+idx,n);
        }
    }

    /**
     * Указывает/заменяет узлы
     * @param idx индекс
     * @param node узел
     */
    @Override
    public void set(int idx, SELF node) {
        SELF old = children().set(idx,node);
        if( node!=null ){
            node.setParent((SELF)this);
        }
        if( old!=null ){
            old.compareAndSetParent((SELF)this, null);
        }
        treeNotify(new TreeEvent.Updated<>(this,idx,old,node));
    }

    /**
     * Указывает/заменяет узлы
     * @param idx индекс
     * @param nodes узелы
     */
    @Override
    public void sets(int idx, SELF... nodes) {
        if( nodes==null )throw new IllegalArgumentException( "nodes==null" );
        for( int i=0;i<nodes.length;i++ ){
            set(idx+i,nodes[i]);
        }
    }

    /**
     * Указывает/заменяет дочерние узлы
     * @param idx индекс
     * @param nodes узелы
     */
    @Override
    public void sets(int idx, Iterable<SELF> nodes) {
        if( nodes==null )throw new IllegalArgumentException( "nodes==null" );
        int i=-1;
        for( SELF n : nodes ){
            i++;
            set(i+idx,n);
        }
    }

    /**
     * Удаляет дочерний узел
     * @param index индексы узлов
     */
    @Override
    public void remove(int index) {
        SELF old = children().remove(index);
        if( old!=null ){
            old.compareAndSetParent((SELF)this, null);
        }
        treeNotify(new TreeEvent.Deleted<>(this,old,index));
    }

    /**
     * Удаляет дочерние узлы
     * @param indexes индексы узлов
     */
    @Override
    public void removes(int... indexes) {
        if( indexes==null )throw new IllegalArgumentException( "indexes==null" );
        List<Integer> lst = new ArrayList<>(indexes.length);
        for( int i:indexes )lst.add(i);
        removes(lst,true);
    }

    /**
     * Удаляет дочерние узлы
     * @param indexes индексы узлов
     * @param mutateIndexes true - модифицирует исходный список индексов / false - создает копию исходного списка
     */
    public void removes(List<Integer> indexes,boolean mutateIndexes) {
        if( indexes==null )throw new IllegalArgumentException( "indexes==null" );
        if( mutateIndexes ){
            Collections.sort(indexes);
        }else {
            indexes = new ArrayList<>(indexes);
            Collections.sort(indexes);
        }
        for( int i=indexes.size()-1; i>=0; i-- ){
            int idx = indexes.get(i);
            remove(idx);
        }
    }

    /**
     * Удаляет дочерние узлы
     * @param indexes индексы узлов
     */
    @Override
    public void removes(Iterable<Integer> indexes) {
        if( indexes==null )throw new IllegalArgumentException( "indexes==null" );
        ArrayList<Integer> lst = new ArrayList<>();
        indexes.forEach( i -> lst.add(i) );
        removes(lst,true);
    }

    /**
     * Удаляет дочерние узлы
     * @param node дочерние узлы
     */
    @Override
    public void delete(SELF node) {
        int idx = children().indexOf(node);
        if( children().remove(node) ) {
            if( node!=null ){
                node.compareAndSetParent((SELF)this, null);
            }
            treeNotify(new TreeEvent.Deleted<>(this, node, idx));
        }
    }

    /**
     * Удаляет дочерние узлы
     * @param nodes дочерние узлы
     */
    @Override
    public void deletes(SELF... nodes) {
        if( nodes==null )throw new IllegalArgumentException( "nodes==null" );
        for( SELF s : nodes ){
            delete(s);
        }
    }

    /**
     * Удаляет дочерние узлы
     * @param nodes дочерние узлы
     */
    @Override
    public void deletes(Iterable<SELF> nodes) {
        if( nodes==null )throw new IllegalArgumentException( "nodes==null" );
        for( SELF s : nodes ){
            delete(s);
        }
    }

    /**
     * Удаляет дочерние узлы
     */
    @Override
    public void clear() {
        List<Tuple2<SELF,Integer>> nodes = new ArrayList<>(children().size());
        for( int i=0; i<children().size(); i++ ){
            nodes.add(0,Tuple2.of(children().get(i),i));
        }
        children().clear();
        for( Tuple2<SELF,Integer> t : nodes ){
            if( t.a()!=null ){
                t.a().compareAndSetParent((SELF)this, null);
            }
        }
        nodes.forEach(x -> treeNotify(new TreeEvent.Deleted<>(this,x.a(),x.b())));
    }

    /**
     * Возвращает вложенность узла начиная от корня
     * @return уровень, 0 - корень
     */
    @Override
    public int level() {
        return UpTree.super.level();
    }

    /**
     * Возвращает путь от корня
     * @return путь
     */
    @Override
    public List<SELF> path() {
        return UpTree.super.path();
    }

    /**
     * Обход дочерних элементов
     * @return итератор по дочерним узлам
     */
    @Override
    public Walk<SELF> walk() {
        return UpTree.super.walk();
    }
}
