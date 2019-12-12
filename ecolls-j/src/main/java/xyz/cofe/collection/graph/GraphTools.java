/*
 * The MIT License
 *
 * Copyright 2016 nt.gocha@gmail.com.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package xyz.cofe.collection.graph;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.collection.NodesExtracter;
import xyz.cofe.ecolls.Pair;
import xyz.cofe.iter.EmptyIterable;
import xyz.cofe.iter.MapIterable;
import xyz.cofe.iter.TreeIterator;
import xyz.cofe.iter.TreeStep;

/**
 * Инстуремент для работы с графами
 * @author nt.gocha@gmail.com
 */
public class GraphTools {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(GraphTools.class.getName());
    private static final Level logLevel = logger.getLevel();
    private static final boolean isLogSevere =
            logLevel==null
                    ? true
                    : logLevel.intValue() <= Level.SEVERE.intValue();

    private static final boolean isLogWarning =
            logLevel==null
                    ? true
                    : logLevel.intValue() <= Level.WARNING.intValue();

    private static final boolean isLogInfo =
            logLevel==null
                    ? true
                    : logLevel.intValue() <= Level.INFO.intValue();

    private static final boolean isLogFine =
            logLevel==null
                    ? true
                    : logLevel.intValue() <= Level.FINE.intValue();

    private static final boolean isLogFiner =
            logLevel==null
                    ? true
                    : logLevel.intValue() <= Level.FINER.intValue();

    private static final boolean isLogFinest =
            logLevel==null
                    ? true
                    : logLevel.intValue() <= Level.FINEST.intValue();

    private static void logFine(String message,Object ... args){
        logger.log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        logger.log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        logger.log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        logger.log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        logger.log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        logger.log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        logger.log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /**
     * Возвращает вершину А из ребра
     * @param <N> Тип вершины
     * @param <E> Тип ребра/дуги
     * @return Вершина А
     */
    public static <N,E> Function<Edge<N,E>,N> nodeAOfEdge(){
        return new Function<Edge<N, E>, N>() {
            @Override
            public N apply(Edge<N, E> from) {
                if( from==null )return null;
                return from.getNodeA();
            }
        };
    }

    /**
     * Возвращает вершину Б из ребра
     * @param <N> Тип вершины
     * @param <E> Тип ребра/дуги
     * @return Вершина Б
     */
    public static <N,E> Function<Edge<N,E>,N> nodeBOfEdge(){
        return new Function<Edge<N, E>, N>() {
            @Override
            public N apply(Edge<N, E> from) {
                if( from==null )return null;
                return from.getNodeB();
            }
        };
    }

    /**
     * Создает извелечение смеженных узлов из узла
     * @param <N> Тип узла/вершины
     * @param <E> Тип ребра/дуги
     * @param g Граф
     * @param direct Направление
     * @return извлекатель
     */
    public static <N,E> NodesExtracter<N,N> nodeExtracter( final Graph<N,E> g, final Path.Direction direct ){
        if( g==null )throw new IllegalArgumentException( "g==null" );
        if( direct==null )throw new IllegalArgumentException( "direct==null" );

        Function<Edge<N,E>,N> conv = null;
        switch( direct ){
            case AB:
                conv = GraphTools.nodeBOfEdge();
                break;
            default:
                conv = GraphTools.nodeAOfEdge();
                break;
        }

        final Function<Edge<N,E>,N> cnv = conv;

        return new NodesExtracter<N, N>() {
            @Override
            public Iterable<N> extract(N from) {
                if( from==null )return new EmptyIterable<>();
                Iterable<Edge<N,E>> edges = null;
                switch( direct ){
                    case AB:
                        edges = g.edgesOfNodeA(from);
                        break;
                    default:
                        edges = g.edgesOfNodeB(from);
                }
                if( edges==null )return new EmptyIterable<>();
                //Iterable<N> res = Iterators.convert(edges, cnv);
                Iterable<N> res = new MapIterable<>(edges,cnv);
                return res;
            }
        };
    }

    /**
     * Получение максимального длинный пути в графе (макс кол-во ребр в пути).
     * @param <N> Тип узла/вершины
     * @param <E> Тип ребра/дуги
     * @param g Граф
     * @param start Начальная вершина
     * @param direct Направление
     * @return
     * -1 - цикл; <p>
     *  0 - начальная вершина, она же конечна;  <p>
     *  1 - одно ребро; 2 - два ребра ...
     */
    public static <N,E> int getMaxPathLength( Graph<N,E> g, N start, Path.Direction direct ){
        if( g==null )throw new IllegalArgumentException( "g==null" );
        if( start==null )throw new IllegalArgumentException( "start==null" );
        if( direct==null )throw new IllegalArgumentException( "direct==null" );

        NodesExtracter<N,N> walker = nodeExtracter(g, direct);
        Iterable<TreeStep<N>> twIter
                = TreeIterator.of(start, (NodesExtracter)walker);

        LinkedHashSet<N> visitedNodes = new LinkedHashSet<N>();
//        visitedNodes.add(start);

        int maxLevel = 0;
        for( TreeStep<N> tw : twIter ){
            N n = tw.getNode();
            if( visitedNodes.contains(n) )return -1;
            visitedNodes.add(n);

            int level = Math.abs(tw.getLevel());
            if( level > maxLevel ) maxLevel = level;
        }

        return maxLevel;
    }

    public static <N,E> N firstNode( Edge<N,E> edge, final Path.Direction d ){
        if( d==null )throw new IllegalArgumentException("d == null");
        if( edge==null )throw new IllegalArgumentException("edge == null");

        switch(d){
            case AB:
                return edge.getNodeA();
            case BA:
                return edge.getNodeB();
        }

        return null;
    }

    public static <N,E> N secondNode( Edge<N,E> edge, final Path.Direction d ){
        if( d==null )throw new IllegalArgumentException("d == null");
        if( edge==null )throw new IllegalArgumentException("edge == null");

        switch(d){
            case AB:
                return edge.getNodeB();
            case BA:
                return edge.getNodeA();
        }

        return null;
    }

    private static <N,E> NodesExtracter<N, Pair<N,E>> graphNodeExt(final Graph<N,E> gr, final boolean reverseOrder){
        NodesExtracter<N,Pair<N,E>> ne = new NodesExtracter<N, Pair<N, E>>() {
            @Override
            public Iterable<Pair<N, E>> extract(N from) {
                if( from==null )return null;
                ArrayList<Pair<N,E>> list = new ArrayList<>();
                if( !reverseOrder ){
                    for( Edge<N,E> edge : gr.edgesOfNodeA(from) ){
                        Pair<N,E> e = Pair.<N,E>of( edge.getNodeB(), edge.getEdge() );
                        list.add(e);
                    }
                }else{
                    for( Edge<N,E> edge : gr.edgesOfNodeB(from) ){
                        Pair<N,E> e = Pair.<N,E>of( edge.getNodeA(), edge.getEdge() );
                        list.add(e);
                    }
                }
                return list;
            }
        };

        return ne;
    }

    public static <N,E> GraphIterator<N,E> iterator( Graph<N,E> gr ){
        if( gr==null )throw new IllegalArgumentException("gr == null");
        GraphIterator<N,E> gitr = new GraphIterator<N,E>(
                gr.getNodes(),
                graphNodeExt(gr,false),
                new GraphIteratorPusher.AppendPusher<N,E>()
        );
        return gitr;
    }

    public static <N,E> GraphIterator<N,E> iterator( Graph<N,E> gr, boolean reverseOrder ){
        if( gr==null )throw new IllegalArgumentException("gr == null");
        GraphIterator<N,E> gitr = new GraphIterator<N,E>(
                gr.getNodes(),
                graphNodeExt(gr,reverseOrder),
                new GraphIteratorPusher.AppendPusher<N,E>()
        );
        return gitr;
    }
}
