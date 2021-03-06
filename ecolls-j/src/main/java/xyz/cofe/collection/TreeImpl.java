package xyz.cofe.collection;

import xyz.cofe.fn.Triple;

import java.util.*;
import java.util.function.Consumer;

public class TreeImpl {
    public static final WeakHashMap<Tree<?>, List<Tree<?>>> nodes = new WeakHashMap<>();

    @SuppressWarnings("unchecked")
    public static <A extends Tree<A>> List<A> nodesOf(Tree<A> tree) {
        if( tree == null ) throw new IllegalArgumentException("tree == null");
        List lst = nodes.computeIfAbsent(tree, (x)->new ArrayList());
        return lst;
    }

    /**
     * Получение кол-ва дочерних узлов
     * @param tree родительский узел
     * @return кол-во узлов
     */
    public static int nodesCount(Tree tree) {
        if( tree == null ) throw new IllegalArgumentException("tree == null");
        return nodesOf(tree).size();
    }

    /**
     * Получение дочернего элемента по индексу
     * @param tree родительский узел
     * @param index индекс
     * @param <A> Тип узла
     * @return Дочерний узел
     */
    public static <A extends Tree<A>> A node(Tree<A> tree, int index) {
        if( tree == null ) throw new IllegalArgumentException("tree == null");
        List<A> lst = nodesOf(tree);
        return nodesOf(tree).get(index);
    }

    /**
     * Замена дочернего узла
     * @param tree родительский узел
     * @param index идекс узлов которые необходимо заменить
     * @param values дочерние узлы
     * @param <A> Тип узла
     * @return  Список изменений (index, oldChildNode, newChildNode)
     */
    public static <A extends Tree<A>> List<Triple<Integer,A,A>> set(Tree<A> tree, int index, A ... values){
        List<Triple<Integer,A,A>> addList = new ArrayList<>();
        set(tree,index,values,addList::add);
        return addList;
    }

    /**
     * Замена дочернего узла
     * @param tree родительский узел
     * @param index идекс узлов которые необходимо заменить
     * @param values дочерние узлы
     * @param changes Список изменений (index, oldChildNode, newChildNode)
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void set(Tree<A> tree, int index, A[] values, Consumer<Triple<Integer,A,A>> changes){
        if( tree == null )throw new IllegalArgumentException( "tree == null" );
        if( values == null )throw new IllegalArgumentException( "values == null" );

        List<A> nodes = nodesOf(tree);
        int idx = index;
        int ival = -1;
        for( A val : values ){
            ival++;
            if( val==null )throw new IllegalArgumentException("found null value["+ival+"]");

            if( idx>=0 && idx<nodes.size() ){
                A old = nodes.set(idx,val);
                if( changes!=null )changes.accept(Triple.of(idx,old,val));
            }else {
                throw new IllegalArgumentException("index (="+idx+") out of bound");
            }
            idx++;
        }
    }

    /**
     * Замена дочернего узла
     * @param tree родительский узел
     * @param index идекс узлов которые необходимо заменить
     * @param values дочерние узлы
     * @param <A> Тип узла
     * @return  Список изменений (index, oldChildNode, newChildNode)
     */
    public static <A extends Tree<A>> List<Triple<Integer,A,A>> set(Tree<A> tree, int index, Iterable<A> values){
        List<Triple<Integer,A,A>> addList = new ArrayList<>();
        set(tree,index,values,addList::add);
        return addList;
    }

    /**
     * Замена дочернего узла
     * @param tree родительский узел
     * @param index идекс узлов которые необходимо заменить
     * @param values дочерние узлы
     * @param changes Список изменений (index, oldChildNode, newChildNode)
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void set(Tree<A> tree, int index, Iterable<A> values, Consumer<Triple<Integer,A,A>> changes){
        if( tree == null )throw new IllegalArgumentException( "tree == null" );
        if( values == null )throw new IllegalArgumentException( "values == null" );

        List<A> nodes = nodesOf(tree);
        int idx = index;
        int ival = -1;
        for( A val : values ){
            ival++;
            if( val==null )throw new IllegalArgumentException("found null value["+ival+"]");

            if( idx>=0 && idx<nodes.size() ){
                A old = nodes.set(idx,val);
                if( changes!=null )changes.accept(Triple.of(idx,old,val));
            }else {
                throw new IllegalArgumentException("index (="+idx+") out of bound");
            }
            idx++;
        }
    }

    /**
     * Добавление дочернего узла в указанную позицию списка дочерних улов
     * @param tree родительский узел
     * @param index индекс (0..) позиции вставки
     * @param nodes дочерние узлы
     * @param <A> Тип узла
     * @return список добавленных узлов - fn( index, null, insertedChildNode )
     */
    public static <A extends Tree<A>> List<Triple<Integer,A,A>> insert(Tree<A> tree, int index, A ... nodes){
        List<Triple<Integer,A,A>> addList = new ArrayList<>();
        insert(tree,index,nodes, addList::add);
        return addList;
    }

    /**
     * Добавление дочернего узла в указанную позицию списка дочерних улов
     * @param tree родительский узел
     * @param index индекс (0..) позиции вставки
     * @param nodes дочерние узлы
     * @param added список добавленных узлов - fn( index, null, insertedChildNode )
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void insert(Tree<A> tree, int index, A[] nodes, Consumer<Triple<Integer,A,A>> added){
        if( tree == null )throw new IllegalArgumentException( "tree == null" );
        if( nodes == null )throw new IllegalArgumentException( "nodes == null" );

        int idx = index;
        int ival = -1;
        for( A a : nodes ){
            ival++;
            if( a==null )throw new IllegalArgumentException("found null value["+ival+"]");

            nodesOf(tree).add(idx, a);
            if( added!=null )added.accept(Triple.of(idx,null,a));
            idx++;
        }
    }

    /**
     * Добавление дочернего узла в указанную позицию списка дочерних улов
     * @param tree родительский узел
     * @param nodes дочерние узлы
     * @param <A> Тип узла
     * @return  список добавленных узлов - fn( index, null, insertedChildNode )
     */
    public static <A extends Tree<A>> List<Triple<Integer,A,A>> insert(Tree<A> tree, int index, Iterable<A> nodes){
        List<Triple<Integer,A,A>> addList = new ArrayList<>();
        insert(tree,index,nodes,e -> addList.add(e));
        return addList;
    }

    /**
     * Добавление дочернего узла в указанную позицию списка дочерних улов
     * @param tree родительский узел
     * @param nodes дочерние узлы
     * @param index индекс (0..) позиции вставки
     * @param added список добавленных узлов - fn( index, null, insertedChildNode )
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void insert(Tree<A> tree, int index, Iterable<A> nodes, Consumer<Triple<Integer,A,A>> added){
        if( tree == null )throw new IllegalArgumentException( "tree == null" );
        if( nodes == null )throw new IllegalArgumentException( "nodes == null" );

        int idx = index;
        int ival = -1;
        for( A a : nodes ){
            ival++;
            if( a==null )throw new IllegalArgumentException("found null value["+ival+"]");

            nodesOf(tree).add(idx, a);
            if( added!=null )added.accept(Triple.of(idx,null,a));
            idx++;
        }
    }

    /**
     * Добавление дочернего узла в конец списка
     * @param tree родительский узел
     * @param nodes дочерние узлы
     * @param <A> Тип узла
     * @return  список добавленных узлов - fn( index, null, insertedChildNode )
     */
    public static <A extends Tree<A>> List<Triple<Integer,A,A>> append(A tree, A ... nodes){
        List<Triple<Integer,A,A>> addList = new ArrayList<>();
        append(tree,nodes, addList::add);
        return addList;
    }

    /**
     * Добавление дочернего узла в конец списка
     * @param tree родительский узел
     * @param nodes дочерние узлы
     * @param added список добавленных узлов - fn( index, null, insertedChildNode )
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void append(A tree, A[] nodes, Consumer<Triple<Integer,A,A>> added){
        if( tree == null )throw new IllegalArgumentException( "tree == null" );
        if( nodes == null )throw new IllegalArgumentException( "nodes == null" );

        int ival = -1;
        for( A a : nodes ){
            ival++;
            if( a==null )throw new IllegalArgumentException("found null value["+ival+"]");

            List children = nodesOf(tree);
            children.add(a);
            if( added!=null )added.accept(Triple.of(children.size()-1,null,a));
        }
    }

    /**
     * Добавление дочернего узла в конец списка
     * @param tree родительский узел
     * @param nodes дочерние узлы
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void append(A tree, Iterable<A> nodes){
        append(tree,nodes,null);
    }

    /**
     * Добавление дочернего узла в конец списка
     * @param tree родительский узел
     * @param nodes дочерние узлы
     * @param added список добавленных узлов - fn( index, null, insertedChildNode )
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void append(A tree, Iterable<A> nodes, Consumer<Triple<Integer,A,A>> added){
        if( tree == null )throw new IllegalArgumentException( "tree == null" );
        if( nodes == null )throw new IllegalArgumentException( "nodes == null" );

        int ival = -1;
        for( A a : nodes ){
            ival++;
            if( a==null )throw new IllegalArgumentException("found null value["+ival+"]");

            List children = nodesOf(tree);
            children.add(a);
            if( added!=null )added.accept(Triple.of(children.size()-1,null,a));
        }
    }

    /**
     * Удаление дочернего узла(ов)
     * @param tree родительский узел
     * @param index иднекс дочернего элемента(ов)
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void deleteByIndex(Tree<A> tree, int ... index){
        deleteByIndex(tree,index,null);
    }

    /**
     * Удаление дочернего узла(ов)
     * @param tree родительский узел
     * @param index иднекс дочернего элемента(ов)
     * @param deleted возможно null, уведомление о удаленных элементах: fn( index, deletedChildNode, null )
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void deleteByIndex(Tree<A> tree, int[] index, Consumer<Triple<Integer,A,A>> deleted){
        if( tree == null )throw new IllegalArgumentException( "tree == null" );
        if( index == null )throw new IllegalArgumentException( "index == null" );

        TreeSet<Integer> tset = new TreeSet<Integer>();
        for( int i : index ){
            tset.add(i);
        }
        for( Integer i : tset.descendingSet() ){
            A n = nodesOf(tree).remove((int)i);
            if( deleted!=null && n!=null )deleted.accept(Triple.of(i,n,null));
        }
    }

    /**
     * Удаление дочернего узла(ов)
     * @param tree родительский узел
     * @param index иднекс дочернего элемента(ов)
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void deleteByIndex(Tree<A> tree, Iterable<Integer> index){
        deleteByIndex(tree,index,null);
    }

    /**
     * Удаление дочернего узла(ов)
     * @param tree родительский узел
     * @param index иднекс дочернего элемента(ов)
     * @param deleted возможно null, уведомление о удаленных элементах: fn( index, deletedChildNode, null )
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void deleteByIndex(Tree<A> tree, Iterable<Integer> index, Consumer<Triple<Integer,A,A>> deleted){
        if( tree == null )throw new IllegalArgumentException( "tree == null" );
        if( index == null )throw new IllegalArgumentException( "index == null" );

        NavigableSet<Integer> set = null;
        if( index instanceof NavigableSet ){
            set = (NavigableSet<Integer>)index;
        }else {
            set = new TreeSet<>();
            for( Integer i : index ){
                if( i!=null )set.add(i);
            }
        }
        for( Integer i : set.descendingSet() ){
            A n = nodesOf(tree).remove((int)i);
            if( deleted!=null && n!=null )deleted.accept(Triple.of(i,n,null));
        }
    }

    /**
     * Удаление дочернего узла(ов)
     * @param tree родительский узел
     * @param value дочерние удаляемые элементы
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void deleteByValue(Tree<A> tree, A ... value){
        if( tree == null )throw new IllegalArgumentException( "tree == null" );
        if( value == null )throw new IllegalArgumentException( "value == null" );
        nodesOf(tree).removeAll(Arrays.asList(value));
    }

    /**
     * Удаление дочернего узла(ов)
     * @param tree родительский узел
     * @param value дочерние удаляемые элементы
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void deleteByValue(Tree<A> tree, Iterable<A> value){
        if( tree == null )throw new IllegalArgumentException( "tree == null" );
        if( value == null )throw new IllegalArgumentException( "value == null" );
        Set<A> removeSet = new LinkedHashSet<>();
        for( A a : value ){
            if( a!=null )removeSet.add(a);
        }
        nodesOf(tree).removeAll(removeSet);
    }

    /**
     * Удаление дочернего узла(ов)
     * @param tree родительский узел
     * @param value дочерние удаляемые элементы
     * @param deleted возможно null, уведомление о удаленных элементах: fn( index, deletedChildNode, null )
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void deleteByValue(Tree<A> tree, A[] value, Consumer<Triple<Integer,A,A>> deleted){
        if( tree == null )throw new IllegalArgumentException( "tree == null" );
        if( value == null )throw new IllegalArgumentException( "value == null" );
        deleteByValue(tree,Arrays.asList(value),deleted);
    }

    /**
     * Удаление дочернего узла(ов)
     * @param tree родительский узел
     * @param value дочерние удаляемые элементы
     * @param deleted возможно null, уведомление о удаленных элементах: fn( index, deletedChildNode, null )
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void deleteByValue(Tree<A> tree, Iterable<A> value, Consumer<Triple<Integer,A,A>> deleted){
        if( tree == null )throw new IllegalArgumentException( "tree == null" );
        if( value == null )throw new IllegalArgumentException( "value == null" );
        Set<A> removeSet = new LinkedHashSet<>();
        for( A a : value ){
            if( a!=null )removeSet.add(a);
        }
        List<Integer> removeIdx = new ArrayList<>();
        List<A> nodes = nodesOf(tree);
        for( int i=0; i<nodes.size(); i++ ){
            A n = nodes.get(i);
            if( removeSet.contains(n) ){
                removeIdx.add(i);
            }
        }
        Collections.reverse(removeIdx);
        for( Integer i : removeIdx ){
            A n = nodes.remove(i.intValue());
            if( deleted!=null && n!=null )deleted.accept(Triple.of(i,n,null));
        }
    }

    /**
     * Удаление всех дочерних узлов
     * @param tree родительский узел
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void clear(Tree<A> tree){
        if( tree == null )throw new IllegalArgumentException( "tree == null" );
        nodesOf(tree).clear();
    }

    /**
     * Удаление всех дочерних узлов
     * @param tree родительский узел
     * @param deleted возможно null, уведомление о удаленных элементах: fn( index, deletedChildNode, null )
     * @param <A> Тип узла
     */
    public static <A extends Tree<A>> void clear(Tree<A> tree, Consumer<Triple<Integer,A,A>> deleted){
        if( tree == null )throw new IllegalArgumentException( "tree == null" );
        List<A> nodes = nodesOf(tree);
        ArrayList<A> deletedNodes = new ArrayList<>(nodes);
        nodes.clear();
        if(deleted!=null){
            int i = -1;
            for( A n : deletedNodes ){
                i++;
                deleted.accept(Triple.of(i,n,null));
            }
        }
    }
}
