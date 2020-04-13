package xyz.cofe.typeconv;

import xyz.cofe.collection.ClassSet;
import xyz.cofe.collection.graph.Edge;
import xyz.cofe.collection.graph.Path;
import xyz.cofe.collection.graph.PathFinder;
import xyz.cofe.collection.graph.SimpleSDGraph;
import xyz.cofe.fn.Pair;
import xyz.cofe.iter.Eterable;
import xyz.cofe.iter.EterableProxy;
import xyz.cofe.iter.SingleIterable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Граф преобразования данных из одног типа в другой
 */
public class TypeCastGraph
    extends SimpleSDGraph<Class, Function<Object,Object>>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TypeCastGraph.class.getName());

    private static Level logLevel(){
        return logger.getLevel() ;
    }

    private static boolean isLogSevere(){
        Level level = logLevel();
        return level==null || level.intValue()<=Level.SEVERE.intValue();
    }

    private static boolean isLogWarning(){
        Level level = logLevel();
        return level==null || level.intValue()<=Level.WARNING.intValue();
    }

    private static boolean isLogInfo(){
        Level level = logLevel();
        return level==null || level.intValue()<=Level.INFO.intValue();
    }

    private static boolean isLogFine(){
        Level level = logLevel();
        return level!=null && level.intValue()<=Level.FINE.intValue();
    }

    private static boolean isLogFiner(){
        Level level = logLevel();
        return level!=null && level.intValue()<=Level.FINER.intValue();
    }

    private static boolean isLogFinest(){
        Level level = logLevel();
        return level!=null && level.intValue()<=Level.FINEST.intValue();
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
        logger.entering(TypeCastGraph.class.getName(),method,params);
    }

    private static void logExiting(String method,Object result){
        logger.exiting(TypeCastGraph.class.getName(),method,result);
    }

    private static void logExiting(String method){
        logger.exiting(TypeCastGraph.class.getName(),method);
    }
    //</editor-fold>

    /**
     * Конструктор по умолчанию
     */
    public TypeCastGraph(){
    }

    /**
     * Конструктор копирования
     * @param src конструктор
     */
    public TypeCastGraph( TypeCastGraph src ){
        if( src!=null ){
            for( Class n : src.getNodes() ){
                add(n);
            }
            for( Edge<Class,Function<Object,Object>> e : src.getEdges() ){
                add(e);
            }
            classes.addAll(src.classes);
            this.findPathMinimum = src.findPathMinimum;
            this.edgeWeightConvertor = src.edgeWeightConvertor;
        }
    }

    /**
     * Клонирование
     * @return клон
     */
    @Override
    public TypeCastGraph clone(){
        return new TypeCastGraph(this);
    }

    /**
     * Множество классов - узлов графа для преобрвзования
     */
    protected ClassSet classes = new ClassSet();

    @Override
    protected void onNodeAdded(Class type) {
        super.onNodeAdded(type);
        if( type!=null ){
            classes.add(type);
        }
    }

    @Override
    protected void onNodeRemoved(Class type) {
        super.onNodeRemoved(type);
        if( type!=null ){
            classes.remove(type);
        }
    }

    /**
     * Получение начального узла преобразований
     * @param type Искомый тип
     * @param strongCompare true - жесткое сравнение типов; false - использование конструкции instanceof в сравнении
     * @param childToParent true - последовательность в порядке от дочерних классов, к родительскому классу <br>
     * false - обратная последовательность: в порядке от родительского класса к дочерним
     * @param incParent true - включать в поиск родитеслькие классы
     * @param incChildren true - включать в поиск дочерние классы
     * @return Перечень классов удовлетворяющих критерию поиска
     */
    public List<Class> findStartNode(
        Class type,
        boolean strongCompare,
        boolean childToParent,
        boolean incParent,
        boolean incChildren
    ){
        Eterable<Class> fromClasses = strongCompare ?
            new SingleIterable<>(type) :
            new EterableProxy<>(classes.getAssignableFrom(type, incParent, incChildren));

        List<Class> list = fromClasses.toList();
        Collections.sort(list, new ClassSet.ClassHeirarchyComparer(childToParent));
        return list;
    }

    /**
     * Создание конвертора ребро графа -&gt; вес
     * @return конвертор ребр графа в их веса
     */
    public Function<Edge<Class, Function<Object,Object>>, Double> createEdgeWeight(){
        return
            new Function<Edge<Class, Function<Object,Object>>, Double>(){
                @Override
                public Double apply(Edge<Class, Function<Object, Object>> from) {
                    Object edge = from.getEdge();
                    if( edge instanceof GetWeight )
                        return ((GetWeight)edge).getWeight();
                    return (double)1;
                }
            };
    }

    protected Function<Edge<Class, Function<Object,Object>>, Double> edgeWeightConvertor = null;

    /**
     * Получение конвертора ребра графа в его вес
     * @return конвертор ребр графа в веса
     */
    public Function<Edge<Class, Function<Object,Object>>, Double> getEdgeWeight(){
        if( edgeWeightConvertor!=null )return edgeWeightConvertor;
        edgeWeightConvertor = createEdgeWeight();
        return edgeWeightConvertor;
    }

    /**
     * Поиск пути цепочки преобразований.
     * Возвращает наименьший по длине путь преобразования типа
     * @param from класс начала пути преобразования
     * @param to конечный класс пути преобразования
     * @return путь или null
     */
    public Path<Class, Function<Object,Object>> findPath( Class from, Class to ){
        if( from==null )throw new IllegalArgumentException( "from==null" );
        if( to==null )throw new IllegalArgumentException( "to==null" );
        return findPath(from, to, null);
    }

    /**
     * Поиск пути цепочки преобразований.
     * Возвращает наименьший по длине путь преобразования типа
     * @param from класс начала пути преобразования
     * @param to конечный класс пути преобразования
     * @param filter Фильтр или null
     * @return путь или null
     */
    public Path<Class, Function<Object,Object>> findPath(
        Class from,
        Class to,
        Predicate<Path<Class, Function<Object,Object>>> filter
    ){
        if( from==null )throw new IllegalArgumentException( "from==null" );
        if( to==null )throw new IllegalArgumentException( "to==null" );

        PathFinder<Class, Function<Object,Object>> pfinder;
//        pfinder = new PathFinder<>(
//            this, from, Path.Direction.AB, (Edge<Class, Function<Object, Object>> from1) -> {
//                Object edge = from1.getEdge();
//                if( edge instanceof GetWeight )
//                    return ((GetWeight)edge).getWeight();
//                return (double)1;
//            });

        pfinder = new PathFinder<Class, Function<Object, Object>>(
            this,
            from,
            Path.Direction.AB,
            getEdgeWeight()
        );

        Path<Class, Function<Object,Object>> path = null;
        while( pfinder.hasNext() ){
            path = pfinder.next();
            if( path==null )break;
            Class lastnode = path.node(-1);
            if( lastnode!=null && lastnode.equals(to) ){
                if( filter!=null ){
                    if( filter.test(path) )return path;
                }else{
                    return path;
                }
            }
        }
        return null;
    }

    /**
     * Создание Caster для цепочки преобразований
     * @param path цепочка преобразований
     * @return caster
     */
    public SequenceCaster createSequenceCaster( Path<Class, Function<Object,Object>> path ){
        if( path==null )throw new IllegalArgumentException( "path==null" );
        SequenceCaster scaster = new SequenceCaster(path);
        return scaster;
    }

    private int findPathMinimum = 1;

    /**
     * Получение возможных путей преобразования
     * @param fromType Исходный тип данных
     * @param targetType Конечный тип данных
     * @return Цепочки преобразований
     */
    public List<Path<Class,Function<Object,Object>>> getCastPaths( Class fromType, Class targetType ){
        if( fromType==null )throw new IllegalArgumentException( "fromType==null" );
        if( targetType==null )throw new IllegalArgumentException( "targetType==null" );

        Class cv = fromType;
        List<Class> starts = findStartNode(cv, false, true, true, false);
        if( starts==null || starts.isEmpty() ){
            throw new ClassCastException( "can't cast "+cv+" to "+targetType+", can't find start class" );
        }

        List<Path<Class,Function<Object,Object>>> lvariants =
            new ArrayList<Path<Class,Function<Object,Object>>>();

        for( Class startCls : starts ){
            Path<Class,Function<Object,Object>> path = null;

            final List<Path<Class,Function<Object,Object>>> variants = new ArrayList<>();

            path = findPath(
                startCls,
                targetType,
                // java 8
                /*(pathFound) -> {
                    variants.add(pathFound);
                    if( variants.size()<findPathMinimum && findPathMinimum>=0 ){
                        return false;
                    }
                    //if( findPathMinimum<2 )return true;
                    return true;
                } */
                new Predicate<Path<Class, Function<Object, Object>>>() {
                    @Override
                    public boolean test(Path<Class, Function<Object, Object>> pathFound) {
                        variants.add(pathFound);
                        if( variants.size()<findPathMinimum && findPathMinimum>=0 ){
                            return false;
                        }
                        //if( findPathMinimum<2 )return true;
                        return true;
                    }
                }
            );

            /*if( path!=null ){
                lvariants.add(path);
            }*/

            lvariants.addAll(variants);
        }

//        starts.stream().map((startCls) -> {
//            Path<Class,Function<Object,Object>> path = null;
//            path = findPath(startCls, targetType);
//            return path;
//        }).filter((path) -> ( path!=null )).forEach((path) -> {
//            lvariants.add(path);
//        });

        return lvariants;
    }

    /**
     * Преборазования значения
     * @param <TARGET> Тип данных который хотим получить
     * @param value Исходное значение
     * @param targetType Целевой тип
     * @return Преобразованное значение
     * @throws ClassCastException если невозможно преобразование
     */
    public <TARGET> TARGET cast( Object value, Class<TARGET> targetType ){
        if( value==null )throw new IllegalArgumentException( "value==null" );
        if( targetType==null )throw new IllegalArgumentException( "targetType==null" );
        Class c = value.getClass();
        if( c.equals(targetType) )return (TARGET)value;
        return (TARGET)cast( value, targetType, null, null, null );
    }

    /**
     * Преборазования значения
     * @param value Исходное значение
     * @param targetType Целевой тип
     * @param newSeqCasters Созданные SequenceCaster
     * @param castedConvertor Function который удачно отработал
     * @param failedCastConvertor Function который не удачно отработал
     * @return Преобразованное значение
     * @throws ClassCastException если невозможно преобразование
     */
    public Object cast(
        Object value,
        Class targetType,
        Consumer<SequenceCaster> newSeqCasters,
        Consumer<Function<Object,Object>> castedConvertor,
        Consumer<Pair<Function<Object,Object>,Throwable>> failedCastConvertor
    ){
        if( value==null )throw new IllegalArgumentException( "value==null" );
        if( targetType==null )throw new IllegalArgumentException( "targetType==null" );

        Class cv = value.getClass();

        List<Path<Class,Function<Object,Object>>> lvariants = getCastPaths(cv, targetType);

        Set<Path<Class,Function<Object,Object>>> removeSet = new LinkedHashSet<>();
        for( Path<Class,Function<Object,Object>> p : lvariants ){
            if( p.nodeCount()<2 ){
                removeSet.add(p);
            }
        }
        lvariants.removeAll(removeSet);

        if( lvariants.isEmpty() ){
            throw new ClassCastException("can't cast "+cv+" to "+targetType
                +", no available casters"
            );
        }

        List<Throwable> castErrors = new ArrayList<Throwable>();
        List<SequenceCaster> scasters = new ArrayList<SequenceCaster>();

        for( Path<Class,Function<Object,Object>> path : lvariants ){
            //int psize = path.size();
            int ncount = path.nodeCount();
            //if( psize==1 ){
            if( ncount==1 ){
                Function<Object,Object> conv = path.edge(0,1);
                try{
                    Object res = conv.apply(value);
                    if( castedConvertor!=null )castedConvertor.accept(conv);
                    return res;
                }catch( Throwable ex ){
                    if( isLogFine() )logException(ex);
                    castErrors.add(ex);
                    if( failedCastConvertor!=null )
                        failedCastConvertor.accept(
                            Pair.of(conv,ex));
                }
            }else{
                SequenceCaster scaster = createSequenceCaster(path);
                scasters.add(scaster);
                if( newSeqCasters!=null )newSeqCasters.accept(scaster);
            }
        }

        for( SequenceCaster caster : scasters ){
            try{
                Object res = caster.apply(value);
                if( castedConvertor!=null )castedConvertor.accept(caster);
                return res;
            }catch( Throwable ex ){
                if( isLogFine() )logException(ex);
                castErrors.add(ex);
                if( failedCastConvertor!=null )
                    failedCastConvertor.accept(
                        Pair.of(caster,ex));
            }
        }

        int ci = -1;
        StringBuilder castErrMess = new StringBuilder();
        for( Throwable err : castErrors ){
            ci++;
            if( ci>0 )castErrMess.append("\n");
            castErrMess.append(err.getMessage());
        }

        throw new ClassCastException("can't cast "+cv+" to "+targetType
            +", cast failed:\n"+castErrMess.toString()
        );
    }
}
