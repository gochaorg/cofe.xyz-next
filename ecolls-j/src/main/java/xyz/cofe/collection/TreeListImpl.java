package xyz.cofe.collection;

import xyz.cofe.fn.TripleConsumer;

import java.util.Collection;
import java.util.List;

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

    public static <A> A node( List<? extends A> nodeList, int index ){
        if( nodeList==null )throw new IllegalArgumentException("nodeList==null");
        return nodeList.get(index);
    }

    public static <A> void set( List<? extends A> nodeList, int index, Iterable<A> values, TripleConsumer<Integer,A,A> changes ){
        if( nodeList==null )throw new IllegalArgumentException("nodeList==null");
        if( values==null )throw new IllegalArgumentException("values==null");
    }
}
