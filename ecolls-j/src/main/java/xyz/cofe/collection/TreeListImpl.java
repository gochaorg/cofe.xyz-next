package xyz.cofe.collection;

import xyz.cofe.fn.Triple;
import xyz.cofe.fn.TripleConsumer;

import java.util.*;
import java.util.function.Consumer;

/**
 * Реализация Tree с использованием List
 */
public class TreeListImpl {
    /**
     * Возвращает кол-во узлов в списке
     * @param nodeList список узлов
     * @return кол-во узлов в списке
     */
    public static int nodesCount( Collection<? extends Object> nodeList ){
        if( nodeList==null )throw new IllegalArgumentException("nodeList==null");
        return nodeList.size();
    }

    /**
     * Возвращает n-ый дочерний узел
     * @param nodeList список узлов
     * @param index индекс
     * @param <A> тип узла
     * @return дочерний узел
     */
    public static <A> A node( List<? extends A> nodeList, int index ){
        if( nodeList==null )throw new IllegalArgumentException("nodeList==null");
        return nodeList.get(index);
    }

    /**
     * Замена дочернего узла
     * @param nodeList список узлов
     * @param index идекс узлов которые необходимо заменить
     * @param values дочерние узлы
     * @param changes (Возможно null) Список изменений (index, oldChildNode, newChildNode)
     * @param <A> Тип узла
     */
    public static <A> void set(
        List<A> nodeList,
        int index,
        Iterable<A> values,
        TripleConsumer<Integer,A,A> changes
    ){
        if( nodeList==null )throw new IllegalArgumentException("nodeList==null");
        if( values==null )throw new IllegalArgumentException("values==null");

        int idx = index;
        int ival = -1;
        for( A val : values ){
            ival++;
            if( val==null )throw new IllegalArgumentException("found null value["+ival+"]");

            if( idx>=0 && idx<nodeList.size() ){
                A old = nodeList.set(idx,val);
                if( changes!=null )changes.accept(idx,old,val);
            }else {
                throw new IllegalArgumentException("index (="+idx+") out of bound");
            }
            idx++;
        }
    }

    /**
     * Добавление дочернего узла в указанную позицию списка дочерних улов
     * @param nodeList список узлов
     * @param index индекс (0..) позиции вставки
     * @param values добавляемые дочерние узлы
     * @param changes (Возможно null) Список изменений (index, oldChildNode=null, newChildNode)
     * @param <A> Тип узла
     */
    public static <A> void insert(
        List<A> nodeList,
        int index,
        Iterable<A> values,
        TripleConsumer<Integer,A,A> changes
    ){
        if( nodeList==null )throw new IllegalArgumentException("nodeList==null");
        if( values==null )throw new IllegalArgumentException("values==null");

        int idx = index;
        int ival = -1;
        for( A a : nodeList ){
            ival++;
            if( a==null )throw new IllegalArgumentException("found null value["+ival+"]");

            nodeList.add(idx, a);
            if( changes!=null )changes.accept(idx,null,a);
            idx++;
        }
    }

    /**
     * Добавление дочернего узла в конец списка
     * @param nodeList список узлов
     * @param values добавляемые дочерние узлы
     * @param changes (Возможно null) Список изменений (index, oldChildNode=null, newChildNode)
     * @param <A> Тип узла
     */
    public static <A> void append(
        List<A> nodeList,
        Iterable<A> values,
        TripleConsumer<Integer,A,A> changes
    ){
        if( nodeList==null )throw new IllegalArgumentException("nodeList==null");
        if( values==null )throw new IllegalArgumentException("values==null");

        int ival = -1;
        for( A a : values ){
            ival++;
            if( a==null )throw new IllegalArgumentException("found null value["+ival+"]");

            nodeList.add(a);
            if( changes!=null )changes.accept(nodeList.size()-1,null,a);
        }
    }

    /**
     * Удаление дочернего узла(ов)
     * @param nodeList список узлов
     * @param index иднекс дочернего элемента(ов)
     * @param changes (Возможно null) Список изменений (index, oldChildNode, newChildNode=null)
     * @param <A> Тип узла
     */
    public static <A> void deleteByIndex(
        List<A> nodeList,
        Iterable<Integer> index,
        TripleConsumer<Integer,A,A> changes
    ){
        if( nodeList==null )throw new IllegalArgumentException("nodeList==null");
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
            A n = nodeList.remove((int)i);
            if( changes!=null && n!=null )changes.accept(i,n,null);
        }
    }

    /**
     * Удаление дочернего узла(ов)
     * @param nodeList список узлов
     * @param value дочерние удаляемые элементы
     * @param changes (Возможно null) Список изменений (index, oldChildNode, newChildNode=null)
     * @param <A> Тип узла
     */
    public static <A> void deleteByValue(List<A> nodeList, Iterable<A> value, TripleConsumer<Integer,A,A> changes){
        if( nodeList==null )throw new IllegalArgumentException("nodeList==null");
        if( value == null )throw new IllegalArgumentException( "value == null" );

        Set<A> removeSet = new LinkedHashSet<>();
        for( A a : value ){
            if( a!=null )removeSet.add(a);
        }
        List<Integer> removeIdx = new ArrayList<>();
        for( int i=0; i<nodeList.size(); i++ ){
            A n = nodeList.get(i);
            if( removeSet.contains(n) ){
                removeIdx.add(i);
            }
        }
        Collections.reverse(removeIdx);
        for( Integer i : removeIdx ){
            A n = nodeList.remove(i.intValue());
            if( changes!=null && n!=null )changes.accept(i,n,null);
        }
    }

    /**
     * Удаление всех дочерних узлов
     * @param nodeList список узлов
     * @param changes (Возможно null) Список изменений (index, oldChildNode, newChildNode=null)
     * @param <A> Тип узла
     */
    public static <A> void clear(List<A> nodeList, TripleConsumer<Integer,A,A> changes){
        if( nodeList==null )throw new IllegalArgumentException("nodeList==null");
        ArrayList<A> deletedNodes = new ArrayList<>(nodeList);
        nodeList.clear();
        if(changes!=null){
            int i = -1;
            for( A n : deletedNodes ){
                i++;
                changes.accept(i,n,null);
            }
        }
    }
}
