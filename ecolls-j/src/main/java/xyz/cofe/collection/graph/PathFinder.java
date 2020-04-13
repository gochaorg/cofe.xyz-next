/*
 * The MIT License
 *
 * Copyright 2014 Kamnev Georgiy (nt.gocha@gmail.com).
 *
 * Данная лицензия разрешает, безвозмездно, лицам, получившим копию данного программного
 * обеспечения и сопутствующей документации (в дальнейшем именуемыми "Программное Обеспечение"),
 * использовать Программное Обеспечение без ограничений, включая неограниченное право на
 * использование, копирование, изменение, объединение, публикацию, распространение, сублицензирование
 * и/или продажу копий Программного Обеспечения, также как и лицам, которым предоставляется
 * данное Программное Обеспечение, при соблюдении следующих условий:
 *
 * Вышеупомянутый копирайт и данные условия должны быть включены во все копии
 * или значимые части данного Программного Обеспечения.
 *
 * ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ ПРЕДОСТАВЛЯЕТСЯ «КАК ЕСТЬ», БЕЗ ЛЮБОГО ВИДА ГАРАНТИЙ,
 * ЯВНО ВЫРАЖЕННЫХ ИЛИ ПОДРАЗУМЕВАЕМЫХ, ВКЛЮЧАЯ, НО НЕ ОГРАНИЧИВАЯСЬ ГАРАНТИЯМИ ТОВАРНОЙ ПРИГОДНОСТИ,
 * СООТВЕТСТВИЯ ПО ЕГО КОНКРЕТНОМУ НАЗНАЧЕНИЮ И НЕНАРУШЕНИЯ ПРАВ. НИ В КАКОМ СЛУЧАЕ АВТОРЫ
 * ИЛИ ПРАВООБЛАДАТЕЛИ НЕ НЕСУТ ОТВЕТСТВЕННОСТИ ПО ИСКАМ О ВОЗМЕЩЕНИИ УЩЕРБА, УБЫТКОВ
 * ИЛИ ДРУГИХ ТРЕБОВАНИЙ ПО ДЕЙСТВУЮЩИМ КОНТРАКТАМ, ДЕЛИКТАМ ИЛИ ИНОМУ, ВОЗНИКШИМ ИЗ, ИМЕЮЩИМ
 * ПРИЧИНОЙ ИЛИ СВЯЗАННЫМ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ ИЛИ ИСПОЛЬЗОВАНИЕМ ПРОГРАММНОГО ОБЕСПЕЧЕНИЯ
 * ИЛИ ИНЫМИ ДЕЙСТВИЯМИ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ.
 */
package xyz.cofe.collection.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Поиск путей в графе. <p>
 * Производит обход графа формируя возможные пути.
 * Обход производится по крайчащим путям.
 * Конечная вершина поиска определяется пользователем данного класса. <p>
 * За один вызов next() выдает один возможный путь из указанной точки.
 * @author gocha
 * @param <N> Тип вершины
 * @param <E> Тип ребра
 */
public class PathFinder<N,E> implements Iterator<Path<N,E>>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(PathFinder.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }

    private static boolean isLogSevere(){
        Level logLevel = logger.getLevel();
        return logLevel==null ? true : logLevel.intValue() <= Level.SEVERE.intValue();
    }

    private static boolean isLogWarning(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.WARNING.intValue();
    }

    private static boolean isLogInfo(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.INFO.intValue();
    }

    private static boolean isLogFine(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINE.intValue();
    }

    private static boolean isLogFiner(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINER.intValue();
    }

    private static boolean isLogFinest(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINEST.intValue();
    }

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

    private static void logEntering(String method,Object ... params){
        logger.entering(PathFinder.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(PathFinder.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(PathFinder.class.getName(), method, result);
    }
    //</editor-fold>


    /**
     * Конструктор
     * @param graph Одно направленный граф
     * @param start Начальная вершина пути
     * @param direction Направление движения
     * @param getWeight Получение веса ребра, вес должен быть положительным или равен нулю
     */
    public PathFinder(
            SingleDirectedGraph<N,E> graph,
            N start,
            Path.Direction direction,
            Function<Edge<N,E>,Double> getWeight
    ){
        if( getWeight==null )throw new IllegalArgumentException( "getWeight==null" );
        if( start==null )throw new IllegalArgumentException( "start==null" );
        if( direction==null )throw new IllegalArgumentException( "direction==null" );
        if( graph==null )throw new IllegalArgumentException( "graph==null" );

        this.graph = graph;
        this.direction = direction;
        this.paths = createPathsList();
        this.comparator = createComparatorFrom(getWeight);
        //this.validator = createValidtor();

        Iterable<Edge<N,E>> next = getNextEdges(start);
        for( Edge<N,E> e : next ){
            Path<N,E> bp = createPath(/*direction*/);

            //bp = bp.add(e);
            bp = bp.start(start);
            bp = bp.join(GraphTools.secondNode(e, direction), e.getEdge());
            paths.add(bp);
        }
        Collections.sort(paths, comparator);
    }

    /**
     * Полчение веса пути
     * @param path Путь
     * @param getWeight Получение веса ребра
     * @return Вес пути
     */
    protected double getIntWeightOf( Path<N,E> path, Function<Edge<N,E>,Double> getWeight ){
        double w = 0;
        for( Edge<N,E> e : path.fetch(0, path.nodeCount()) ){
            double we = getWeight.apply(e);
            w += we;
        }
        return w;
    }

    /**
     * Создание Comparator для пути
     * @param getWeight Получение веса ребра
     * @return Comparator
     */
    protected Comparator<Path<N,E>> createComparatorFrom( Function<Edge<N,E>,Double> getWeight ){
        final Function<Edge<N,E>,Double> fgetWeight = getWeight;
        return new Comparator<Path<N, E>>() {
            @Override
            public int compare(Path<N, E> p1, Path<N, E> p2) {
                double w1 = getIntWeightOf(p1, fgetWeight);
                double w2 = getIntWeightOf(p2, fgetWeight);
                return w1 == w2 ? 0 : ( w1 < w2 ? -1 : 1 );
            }
        };
    }

    /**
     * Конструктор
     * @param graph Одно направленный граф
     * @param start Начальная вершина пути
     * @param direction Направление движения
     * @param comparator Сравнение длины путей
     */
    public PathFinder(
            SingleDirectedGraph<N,E> graph,
            N start,
            Path.Direction direction,
            Comparator<Path<N,E>> comparator
    ){
        if (graph== null) {
            throw new IllegalArgumentException("graph==null");
        }
        if (start== null) {
            throw new IllegalArgumentException("start==null");
        }
        if (direction== null) {
            throw new IllegalArgumentException("direction==null");
        }
        if (comparator== null) {
            throw new IllegalArgumentException("comparator==null");
        }

        this.graph = graph;
        this.direction = direction;
        this.paths = createPathsList();
        this.comparator = comparator;
        //this.validator = createValidtor();

        Iterable<Edge<N,E>> next = getNextEdges(start);
        for( Edge<N,E> e : next ){
            Path<N,E> bp = createPath(/*direction*/);
            bp = bp.start(start);
            bp = bp.join(GraphTools.secondNode(e, direction),e.getEdge());
            paths.add(bp);
        }
        Collections.sort(paths, comparator);
    }

    /**
     * Создает список путей
     * @return Список путей
     */
    protected List<Path<N,E>> createPathsList(){ return new ArrayList<Path<N, E>>(); }

    /**
     * Создаает путь
     * @return Путь
     */
    protected Path<N,E> createPath(/*Path.Direction d*/){
        BasicPath<N,E> p = new BasicPath<N, E>();
        //p = p.direction(d);
        return p;
    }

    /**
     * Добавляет ребро в конец пути
     * @param path Путь
     * @param e Ребро
     * @return Новый путь
     */
    protected Path<N,E> append(Path<N,E> path,Edge<N,E> e){
        N n = GraphTools.secondNode(e, direction);
        Path npath = path.join( n, e.getEdge());
        return npath;
    }

    /**
     * Граф в котором производится поиск
     */
    protected SingleDirectedGraph<N,E> graph = null;

    /**
     * Направление движения
     */
    protected Path.Direction direction = null;

    /**
     * Список путей используемых в поиске.
     * Используются их конечные вершины.
     */
    protected List<Path<N,E>> paths = null;
    protected Comparator<Path<N,E>> comparator = null;
    //protected Predicate<Edge<N,E>> validator = null;

    /*
     * Создает предикат проверки циклов в пути.
     * @return true - Проверяемое ребро AB <b>не</b> существует в пути; <p>
     * false - Проверяемое ребро AB <b>существует</b> в пути;
    protected Predicate<Edge<N,E>> createValidtor(){
        Predicate<Edge<N,E>> p = new Predicate<Edge<N,E>>() {
            @Override
            public boolean validate(Edge<N,E> value) {
                if( paths==null )return false;
                if( value==null )return false;
                for( Path<N,E> p : paths ){
                    //if( p.has(value.getNodeA(), value.getNodeB()) )return false;

                    N na = GraphTools.secondNode(value, direction);
                    if( na==null )return false;

                    if( p.count(na)>0 )return false;
                }
                return true;
            }
        };
        return p;
    }
     */

    /**
     * Извлекает исходящие ребра/дуги из вершины n в соот. движению.
     * @param n Вершина
     * @return Ребра/дуги направления движения.
     */
    protected Iterable<Edge<N,E>> getNextEdges(N n)
    {
        Iterable<Edge<N,E>> itr =
                direction.equals(Path.Direction.AB)
                        ? graph.edgesOfNodeA(n)
                        : graph.edgesOfNodeB(n);

        /*if( validator!=null ){
            itr = Iterators.predicate(itr, validator);
        }*/
        return itr;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator
     */
    @Override
    public boolean hasNext() {
        if( paths==null || paths.isEmpty() )return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator
     */
    @Override
    public Path<N, E> next() {
        if( paths==null || paths.isEmpty() )return null;

        Path<N, E> p = paths.remove((int)0);

        if( p.nodeCount()>0 ){
            N last = p.node(-1);
            if( !p.hasCycles() ){
                Iterable<Edge<N,E>> next = getNextEdges(last);

                for( Edge<N,E> e : next ){
                    paths.add( append(p, e) );
                }
            }else{
                logFiner( "cycle detected" );
            }
        }

        Collections.sort(paths, comparator);

        return p;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator
     */
    @Override
    public void remove() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }
}
