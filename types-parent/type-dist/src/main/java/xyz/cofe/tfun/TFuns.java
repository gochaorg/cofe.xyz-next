package xyz.cofe.tfun;

import xyz.cofe.iter.Eterable;
import xyz.cofe.typedist.TypeDistance;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Имутабельная коллекция функций
 */
public class TFuns implements Eterable<TFunction> {
    private final Eterable<TFunction> source;

    /**
     * Конструктор по умолчанию
     */
    public TFuns(){
        source = Eterable.empty();
    }

    /**
     * Конструктор
     * @param samples функции
     */
    public TFuns(Iterable<TFunction> samples){
        //noinspection unchecked,rawtypes,rawtypes
        source = samples!=null ? (samples instanceof Eterable ? (Eterable)samples : Eterable.of(samples) ) : Eterable.empty();
    }

    private volatile SortedMap<String,TFunction> funsByTypes;

    /**
     * Карта всех функций сортированная по входящим/исходящим типам
     * @return Карта функций
     */
    public SortedMap<String,TFunction> funsByTypes(){
        if( funsByTypes !=null )return funsByTypes;
        synchronized (this) {
            if( funsByTypes !=null )return funsByTypes;
            if (source != null) {
                TreeMap<String, TFunction> mapByTypes = new TreeMap<>();
                for (TFunction fun : source) {
                    if (fun != null) {
                        mapByTypes.put(keyByTypes(fun), fun);
                    }
                }
                funsByTypes = Collections.unmodifiableSortedMap(mapByTypes);
            } else {
                funsByTypes = Collections.unmodifiableSortedMap(Collections.emptyNavigableMap());
            }
            return funsByTypes;
        }
    }

    private volatile SortedMap<String,TFunction> funsByInput;

    /**
     * Карта функций сортированная по входящим типам
     * @return Карта функций
     */
    public SortedMap<String,TFunction> funsByInput(){
        if( funsByInput!=null )return funsByInput;
        synchronized (this) {
            if( funsByInput!=null )return funsByInput;
            if (source != null) {
                TreeMap<String, TFunction> mapByTypes = new TreeMap<>();
                for (TFunction fun : source) {
                    if (fun != null) {
                        mapByTypes.put(keyByInput(fun), fun);
                    }
                }
                funsByInput = Collections.unmodifiableSortedMap(mapByTypes);
            } else {
                funsByInput = Collections.unmodifiableSortedMap(Collections.emptyNavigableMap());
            }
            return funsByInput;
        }
    }

    private static String keyByTypes(TFunction fun){
        StringBuilder sb = new StringBuilder();
        for( Class<?> t : fun.types() ){
            if( sb.length()>0 )sb.append(",");
            sb.append(t.getName());
        }
        return sb.toString();
    }
    private static String keyByInput(TFunction fun){
        StringBuilder sb = new StringBuilder();
        for( Class<?> t : fun.input() ){
            if( sb.length()>0 )sb.append(",");
            sb.append(t.getName());
        }
        return sb.toString();
    }

    /**
     * Возвращает функции
     * @return функции
     */
    public Eterable<TFunction> functions(){
        return Eterable.of(funsByTypes().values());
    }

    /**
     * Итератор по функциям
     * @return итератор
     */
    @Override
    public Iterator<TFunction> iterator(){
        return funsByTypes().values().iterator();
    }

    /**
     * Клонирует и добавляет функции в клон
     * @param funs функции
     * @return клон с добавленными функциями
     */
    public TFuns add( TFunction ... funs ){
        if( funs==null )throw new IllegalArgumentException( "funs==null" );
        return new TFuns(source.union(Eterable.of(funs)));
    }

    /**
     * Клонирует и добавляет функции в клон
     * @param funs функции
     * @return клон с добавленными функциями
     */
    public TFuns add( Iterable<TFunction> funs ){
        if( funs==null )throw new IllegalArgumentException( "funs==null" );
        return new TFuns(source.union(Eterable.of(funs)));
    }

    /**
     * Клонирует и фильтрует функции в клон
     * @param funs фильтр
     * @return клон с отфильтрованными функциями
     */
    public TFuns filter( Predicate<TFunction> funs ){
        if( funs==null )throw new IllegalArgumentException( "funs==null" );
        List<TFunction> lst = source.filter(funs).toList();
        return new TFuns(lst);
    }

    /**
     * Возвращает первую функцию
     * @return функция
     */
    public Optional<TFunction> first(){
        return functions().first();
    }

    private volatile TFunction[] array;

    /**
     * Возвращает массив функций
     * @return массив функций
     */
    public TFunction[] toArray(){
        if( array!=null )return array;
        synchronized (this){
            if( array!=null )return array;
            List<TFunction> tfuns = functions().toList();
            array = tfuns.toArray(new TFunction[0]);
            return array;
        }
    }

    /**
     * Возвращает список функций
     * @return список функций
     */
    public List<TFunction> toList(){
        return Arrays.asList(toArray());
    }

    /**
     * Итерация по списку функций
     * @param funs список функций
     */
    public void each(Consumer<TFunction> funs){
        if( funs==null )throw new IllegalArgumentException( "funs==null" );
        functions().forEach(funs);
    }

    private <U> Stream<U> mapStream(Function<TFunction,U> mapFn){
        if( mapFn==null )throw new IllegalArgumentException( "mapFn==null" );
        return funsByTypes().values().stream().map(mapFn);
    }

    /**
     * Результат поиска функций совпадающей с требуемыми типами функций
     */
    public static class Found {
        public Found(TFunction fun, List<Integer> distances ){
            if( fun==null )throw new IllegalArgumentException( "fun==null" );
            if( distances==null )throw new IllegalArgumentException( "distances==null" );
            this.function = fun;
            this.distances = Collections.unmodifiableList(distances);
        }

        private TFunction function;

        /**
         * Возвращает искомую функцию
         * @return искомая функция
         */
        public TFunction function(){ return function; }

        private List<Integer> distances;

        /**
         * Дистанция между типами, <br>
         * возможно null значения - означает, что между типами нет ничего общего <br>
         * 0 - полное совпадение <br>
         * меньше 0 - фактический тип аргмента ко-вариантен к искомому <br>
         * больше 0 - фактический тип аргмента контр-вариантен к искомому
         * @return дистанция между фактическими типами аргументами функции и искомыми типами
         */
        public List<Integer> distances(){ return distances; }

        private Boolean callable;

        /**
         * Проверяет что данная функция может быть вызвана
         * @return true - функция вызываемая
         */
        public boolean callable(){
            if( callable!=null )return callable;
            if( distances().stream().anyMatch(v->v==null || v>0)){
                callable = false;
            }else {
                callable = true;
            }
            return callable;
        }

        /**
         * Возвращает сумму квадратов дистанций summ += (Math.abs(d)+1) * (Math.abs(d)+1))
         * @return дистанция или Long.MAX_VALUE когда данную функцию не возможно вызвать
         */
        public long sdistance(){
            if( !callable() )return Long.MAX_VALUE;
            long summ = 0;
            for( Integer d : distances() ){
                summ += (Math.abs(d)+1) * (Math.abs(d)+1);
            }
            return summ;
        }
    }

    /**
     * Выборка функций
     */
    public static class FindResult {
        private final List<Found> founds;
        public FindResult(List<Found> founds){
            if( founds==null )throw new IllegalArgumentException( "founds==null" );
            this.founds = Collections.unmodifiableList(founds);
        }

        /**
         * Возвращает найденые варианты
         * @return варианты
         */
        public List<Found> toList(){ return founds; }

        /**
         * Вычисляет минимальную дистацию
         * @return минимальная дистация или Long.MAX_VALUE
         */
        public long minSDistance(){
            return founds.stream().map(Found::sdistance).min(Long::compareTo).orElse(Long.MAX_VALUE);
        }

        /**
         * Возвращает кол-во найденых вариантов
         * @return кол-во найденых вариантов
         */
        public int count(){ return founds.size(); }

        /**
         * Возвращает предпочительные варианты с наименьшим расстоянием
         * @return предпочтительные варианты
         */
        public List<TFunction> preffered(){
            long min = minSDistance();
            return founds.stream().filter(f -> f.sdistance()==min).map(f -> f.function()).collect(Collectors.toList());
        }
    }

    /**
     * Поиск функций удовлетворяющих критерию
     */
    public static class Finder {
        protected TFuns source;
        protected Function<TFuns,Stream<Found>> foundMapper;

        public Finder(TFuns source, Function<TFuns,Stream<Found>> foundMapper){
            if( source==null )throw new IllegalArgumentException( "source==null" );
            if( foundMapper==null )throw new IllegalArgumentException( "foundMapper==null" );
            this.source = source;
            this.foundMapper = foundMapper;
        }

        /**
         * Получение выборки
         * @return выборка
         */
        public FindResult fetch(){
            return new FindResult(
                foundMapper
                    .apply(source)
                    .filter(Found::callable)
                    .sorted(Comparator.comparingLong(Found::sdistance))
                    .collect(Collectors.toList())
            );
        }
    }

    private Function<TFuns,Stream<Found>> foundMapper( Function<TFunction,Class<?>[]> fetchTypes, Class<?> ... args){
        return src -> {
            if( args.length==0 ){
                return src.filter( f -> f.input().length==0 ).mapStream( f -> {
                    return new Found(f,Collections.emptyList());
                });
            }else{
                return src.filter( f -> f.input().length==args.length ).mapStream( f -> {
                    List<Integer> dist = new ArrayList<>();

                    Class<?>[] fargs = fetchTypes.apply(f);
                    for( int i=0; i<fargs.length; i++ ){
                        Optional<TypeDistance> otdist = TypeDistance.distance(fargs[i], args[i]);
                        if( otdist.isPresent() ){
                            TypeDistance td = otdist.get();
                            Optional<Integer> od = td.dist();
                            if( od.isPresent() ){
                                dist.add(od.get());
                            }else {
                                dist.add(null);
                            }
                        }else{
                            dist.add(null);
                        }
                    }

                    return new Found(f,dist);
                });
            }
        };
    }

    /**
     * Поиск функции совпадающей с указанными аргументами.
     * Поиск просматривает все варианты и дает оценку на сколько совпадает
     * с функция с искомой
     * @param args типы аргментов искомой функции
     * @return Найденые функции
     */
    public FindResult findByArgs( Class<?> ... args ){
        if( args==null )throw new IllegalArgumentException( "args==null" );
        return new Finder(
            this,
            foundMapper(TFunction::input, args)
        ).fetch();
    }

    /**
     * Поиск функции совпадающей с указанными аргументами и типом результата.
     * Поиск просматривает все варианты и дает оценку на сколько совпадает
     * с функция с искомой
     * @param types типы аргментов искомой функции и тип результата
     * @return Найденые функции
     */
    public FindResult findByTypes( Class<?> ... types ){
        if( types==null )throw new IllegalArgumentException( "types==null" );
        return new Finder(
            this,
            foundMapper(TFunction::types, types)
        ).fetch();
    }
}
