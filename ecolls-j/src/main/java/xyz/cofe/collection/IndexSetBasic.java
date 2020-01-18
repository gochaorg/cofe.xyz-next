/*
 * The MIT License
 *
 * Copyright 2017 Kamnev Georgiy (nt.gocha@gmail.com).
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

package xyz.cofe.collection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import xyz.cofe.ecolls.Pair;
import xyz.cofe.collection.SortInsert;
import xyz.cofe.collection.SortInsertProfiling;
import xyz.cofe.ecolls.QuadConsumer;
import xyz.cofe.ecolls.ReadWriteLockSupport;
import xyz.cofe.scn.LongScn;

/**
 * Список содежащий уникальные элементы, отсортированные в порядке возрастания
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 * @param <A>  Тип элементов в множестве
 */
@SuppressWarnings("WeakerAccess")
public class IndexSetBasic<A extends Comparable<A>>
    implements
        IndexSet<A>,
        ReadWriteLockSupport,
        LongScn<IndexSetBasic<A>,Void>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(IndexSetBasic.class.getName());
    private static final Level logLevel = logger.getLevel();

    private static final boolean isLogSevere =
        logLevel==null || logLevel.intValue()<=Level.SEVERE.intValue();

    private static final boolean isLogWarning =
        logLevel==null || logLevel.intValue()<=Level.WARNING.intValue();

    private static final boolean isLogInfo =
        logLevel==null || logLevel.intValue()<=Level.INFO.intValue();

    private static final boolean isLogFine =
        logLevel==null || logLevel.intValue()<=Level.FINE.intValue();

    private static final boolean isLogFiner =
        logLevel==null || logLevel.intValue()<=Level.FINER.intValue();

    private static final boolean isLogFinest =
        logLevel==null || logLevel.intValue()<=Level.FINEST.intValue();

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
        logger.entering(IndexSetBasic.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(IndexSetBasic.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(IndexSetBasic.class.getName(), method, result);
    }
    //</editor-fold>

    protected final List<A> list;

    /**
     * Конструктор
     */
    public IndexSetBasic(){
        list = createList();
    }

    //<editor-fold defaultstate="collapsed" desc="createList()">

    /**
     * Создание списка который будет хранить значения
     * @return
     */
    protected List createList(){
        return new ArrayList();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="size()">

    /**
     * Возвращает кол-во элементов
     * @return кол-во элементов
     */
    @Override
    public int size() {
        return readLock(list::size);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="get(idx):a">

    /**
     * Возвращает элемент по его индексу
     * @param idx индекс
     * @return элемент
     */
    @Override
    public A get(int idx) {
        return readLock(()->list.get(idx));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="exists(a):boolean">

    /**
     * Проверка наличие элемента
     * @param a элемент
     * @return true - элемент присуствует
     */
    @Override
    public boolean exists(A a) {
        return readLock( ()->indexOf(a) >= 0 );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="indexOf()">

    /**
     * Поиск индекса элемента
     * @param a элемент
     * @return индекс или -1
     */
    protected int findIdexOf(A a){
        if( a==null )return -1;
        return readLock( ()->{
            if( list.isEmpty() )return -1;
            int listSize = list.size();

            // один элемент в списке
            if( listSize==1 ){
                A ca = list.get(0);
                if( ca==null )return -1;
                if( a.compareTo(ca)==0 )return 0;
                return -1;
            }

            // За пределами минимального значения
            if( minValue!=null ){
                int cmin = a.compareTo(minValue);
                if( cmin==0 )return 0;
                if( cmin<0 )return -1;
            }

            // За пределами максимального значения
            if( maxValue!=null ){
                int cmax = a.compareTo(maxValue);
                if( cmax==0 )return listSize-1;
                if( cmax>0 )return -1;
            }

            // В списке всего два элемента
            // Притом сравниваемое значение не соот ни мин, ни макс. значению
            if( listSize==2 ){
                return -1;
            }

            int half = listSize / 2;

            int fLeftIdx = findIdexOfInRange(a, 0, half);
            if( fLeftIdx>=0 )return fLeftIdx;

            int fRightIdx = findIdexOfInRange(a, half, listSize);
            return fRightIdx;
        });
    }

    protected int indexOfScanRange(){ return 30; }

    protected int findIdexOfInRange(A a, int begin, int endex){
        if( begin>endex ){
            int t = begin;
            begin = endex;
            endex = t;
        }

        int rdiff = endex - begin; //rdiff всегда >= 0
        if( rdiff<=0 )return -1; // диапазон поиска пуст

        // поиск среди одного элемента
        if( rdiff==1 ){
            A b = list.get(begin);
            int cmp = a.compareTo(b);
            if( cmp==0 )return begin;
            return -1;
        }

        // Поиск среди 2 .. 30 элементов - скан
        if( rdiff>=2 && rdiff<30 ){
            for( int i=0; i<rdiff; i++ ){
                int tidx = begin+i;
                A b = list.get(tidx);
                int cmp = a.compareTo(b);
                if( cmp==0 )return tidx;
            }
            return -1;
        }

        // Получаем центральный элемент в заданном диапазоне
        int half = rdiff / 2;
        int centerIdx = begin + half;

        A center = list.get(centerIdx);
        int cmp2Center = a.compareTo(center);

        // Совпал с центром
        if( cmp2Center==0 ){
            return centerIdx;
        }

        // Левее центра
        if( cmp2Center<0 ){
            return findIdexOfInRange(a, begin, centerIdx);
        }

        // Правее центра
        return findIdexOfInRange(a, centerIdx, endex);
    }

    /**
     * Поиск индекс элемента
     * @param a элемент
     * @return индекс или -1
     */
    @Override
    public int indexOf(A a) {
        return findIdexOf(a);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="each()">

    /**
     * Обход всех элементов
     * @param iter потребитель
     */
    @Override
    public void each(Consumer<A> iter) {
        if( iter==null )throw new IllegalArgumentException("iter == null");
        readLock( ()->{
            for( A a : list ){
                iter.accept(a);
            }
        });
    }

//    TODO Разработа с учетом java 12, миниально 9
//    public Stream<Pair<A,Integer>> stream(){
//        synchronized( sync ){
//            if( size()<1 )return Stream.empty();
//            if( size()==1 )return Stream.of( Pair.of(get(0),0));
//            return Stream.iterate(
//                Pair.of(get(0),0),
//                x -> x!=null && x.b()!=null && x.b() <= (size()-1),
//                x -> x!=null && x.b()!=null && x.b() <  (size()-1) ? Pair.of( get(x.b()+1), x.b()+1 ) : null
//            );
//        }
//    }

    // TODO поддержка java 8

    /**
     * Создание стрима
     * @return стрим элементов
     */
    public Stream<Pair<A,Integer>> stream(){
        return readLock( ()->{
            if( size()<1 )return Stream.empty();
            if( size()==1 )return Stream.of( Pair.of(get(0),0));
            return Stream.iterate(
                Pair.of(get(0),0),
                //x -> x!=null && x.b()!=null && x.b() <= (size()-1),
                x -> x!=null && x.b()!=null && x.b() <  (size()-1) ? Pair.of( get(x.b()+1), x.b()+1 ) : null
            ).limit( size() );
        });
    }

    /**
     * Обход элементов с получением индекса
     * @param begin С какого индекса начать
     * @param endEx По какой исключительно закнчить
     * @param consumer Функция fn(index,item):any принимающая значения
     */
    @Override
    public void eachByIndex( int begin, int endEx, BiConsumer<Integer,A> consumer ){
        if( begin<0 )throw new IllegalArgumentException("begin < 0");
        if( endEx<0 )throw new IllegalArgumentException("endEx < 0");
        if( consumer==null )throw new IllegalArgumentException("consumer == null");
        if( begin==endEx )return;
        readLock( ()->{
            int order = begin>endEx ? -1 : 1;
            int fbegin = begin>endEx ? endEx : begin;
            int fendex = begin>endEx ? begin : endEx;

            int cnt = fendex - fbegin;
            if( cnt<1 )return;

            int size = list.size();
            if( fendex>size )fendex = size;

            if( order<0 ){
                for( int i=fendex-1; i>=fbegin; i-- ){
                    A a = get(i);
                    consumer.accept(i, a);
                }
            }else{
                for( int i=fbegin; i<fendex; i++ ){
                    A a = get(i);
                    consumer.accept(i, a);
                }
            }
        });
    }

    @Override
    public void eachByValue(
            A begin, boolean incBegin,
            A end, boolean incEnd,
            QuadConsumer<Integer,A,Integer,Integer> consumer )
    {
        if( begin==null )throw new IllegalArgumentException("begin == null");
        if( end==null )throw new IllegalArgumentException("end == null");
        if( consumer==null )throw new IllegalArgumentException("consumer == null");

        int cmpBE = begin.compareTo(end);
        if( cmpBE==0 ){
            // выборка за исключением begin | end
            if( !incBegin || !incEnd )return;

            // выборка одного элемента
            readLock(()->{
                int idx = indexOf(begin);
                if( idx>=0 ){
                    consumer.accept(idx, begin, 0, 1);
                }
                return;
            });
        }

        readLock(()->{
            if( cmpBE<0 ){
                // выборка слева на право (в сторону увеличения)
                Pair<Integer,A> from = tailEntry(begin, !incBegin, 0, size());
                if( from==null )return;

                Pair<Integer,A> to = headEntry(end, !incEnd, 0, size());
                if( to==null ){
                    // Найден всего один
                    consumer.accept(from.a(), from.b(), 0, 1);
                    return;
                }

                int cnt = to.a() - from.a() + 1;
                if( cnt>2 ){
                    // найдено более 2 элементов
                    consumer.accept(from.a(), from.b(), 0, cnt);
                    for( int i=from.a()+1; i<to.a(); i++ ){
                        consumer.accept(i, get(i), i-from.a(), cnt);
                    }
                    consumer.accept(to.a(), to.b(), cnt-1, cnt);
                }else{
                    // найдено 2 элемента
                    consumer.accept(from.a(), from.b(), 0, 2);
                    consumer.accept(to.a(), to.b(), 1, 2);
                }
            }else{
                // выборка справа на лево (в сторону уменьшения)
                Pair<Integer,A> from = tailEntry(end, !incEnd, 0, size());
                if( from==null )return;

                Pair<Integer,A> to = headEntry(begin, !incBegin, from.a(), size());
                if( to==null ){
                    // Найден всего один
                    consumer.accept(from.a(), from.b(), 0, 1);
                    return;
                }

                int fromIdx = from.a();
                int toIdx = to.a();
                int cnt = toIdx - fromIdx + 1;
                if( cnt>2 ){
                    // найдено более 2 элементов
                    int qidx = 0;
                    consumer.accept(to.a(), to.b(), qidx, cnt);
                    qidx++;
                    for( int i=toIdx-1; i>fromIdx; i-- ){
                        consumer.accept(i, get(i), qidx, cnt);
                        qidx++;
                    }
                    consumer.accept(from.a(), from.b(), qidx, cnt);
                }else{
                    // найдено 2 элемента
                    consumer.accept(to.a(), to.b(), 0, 2);
                    consumer.accept(from.a(), from.b(), 1, 2);
                }
            }
        });
    }

    @Override
    public void eachByValue(A begin, boolean incBegin, A end, boolean incEnd, final BiConsumer<Integer, A> consumer) {
        if( begin==null )throw new IllegalArgumentException("begin == null");
        if( end==null )throw new IllegalArgumentException("end == null");
        if( consumer==null )throw new IllegalArgumentException("consumer == null");

        QuadConsumer<Integer,A,Integer,Integer> consumer4 =
                (arg1, arg2, arg3, arg4) -> consumer.accept(arg1, arg2);

        eachByValue(begin, incBegin, end, incEnd, consumer4);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="comparator()">
    private volatile Comparator<A> comparator_instance;
    protected Comparator<A> comparator(){
        return readLock(()->{
            if( comparator_instance!=null )return comparator_instance;
            comparator_instance = new Comparator<A>() {
                @Override
                public int compare(A a1, A a2) {
                    if( a1==null && a2==null )return 0;
                    if( a1!=null && a2==null )return -1;
                    if( a1==null && a2!=null )return 1;
                    return a1.compareTo(a2);
                }
            };
            return comparator_instance;
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="sortInsert()">
    private volatile SortInsert<List,A> sortInsert_instance;
    protected SortInsert<List,A> sortInsert(){
        return readLock(()->{
            if( sortInsert_instance!=null )return sortInsert_instance;
            sortInsert_instance = (SortInsert)SortInsertProfiling.createForList();
            return sortInsert_instance;
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="add()">
    @Override
    public int add(A a) {
        if( a==null )throw new IllegalArgumentException("a == null");
        return writeLock(()->{
            int idx = add0(a);
            updateMinMax();
            return idx;
        });
    }
    //</editor-fold>

    @Override
    public IndexSet<A> append(A ... items) {
        if( items==null )throw new IllegalArgumentException("items == null");
        writeLock(()->{
            for( A a : items ){
                add(a);
            }
        });
        return this;
    }

    //<editor-fold defaultstate="collapsed" desc="min/max">
    //<editor-fold defaultstate="collapsed" desc="updateMinMax()">
    private void updateMinMax(){
        if( !list.isEmpty() ){
            minValue = list.get(0);
            maxValue = list.get(list.size()-1);
        }else{
            minValue = null;
            maxValue = null;
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="minMax()">
    @Override
    public Pair<A, A> minMax() {
        return readLock(()->{
            if( minValue==null || maxValue==null )return null;
            return Pair.of( minValue, maxValue );
        });
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="min/max()">
    protected A minValue;
    protected A maxValue;

    @Override
    public A min() {
        return readLock(()->{
            return minValue;
        });
    }

    @Override
    public A max() {
        return readLock(()->{
            return maxValue;
        });
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getTimeCounters()">
    private final Map<String, Long> timeOp = new LinkedHashMap<>();

//    @Override
//    public Map<String, Long> getTimeCounters() {
//        timeOp.put("indexOf ns", indexOfTimeNS);
//        timeOp.put("sortInsert ns", sortInsertTimeNS);
//
//        Object si = sortInsert();
//        if( si instanceof SortInsertProfiling ){
//            SortInsertProfiling s = (SortInsertProfiling)si;
//            timeOp.put("sortInsert maxDepth", (long)s.maxDepth);
//            timeOp.put("sortInsert insert ns", (long)s.insertTotalNS);
//            timeOp.put("sortInsert sort ns", ((long)s.sortInsertTimeNS) - (long)s.insertTotalNS);
//        }
//
//        timeOp.put("tailEntry ns", tailEntryFinished - tailEntryStarted );
//        timeOp.put("tailEntry maxDepth", (long)tailEntryLastMaxDepth );
//        timeOp.put("tailEntry getCall", (long)tailEntryGetCall );
//        timeOp.put("tailEntry scanSize", (long)tailEntryScanSize );
//
//        timeOp.put("headEntry ns", headEntryFinished - headEntryStarted );
//        timeOp.put("headEntry maxDepth", (long)headEntryLastMaxDepth );
//        timeOp.put("headEntry getCall", (long)headEntryGetCall );
//        timeOp.put("headEntry scanSize", (long)headEntryScanSize );
//
//        return timeOp;
//    }

    private long indexOfTimeNS;
    private long sortInsertTimeNS;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="add()">
    private int add0(A a){
        long t0,t1;
        t0 = System.nanoTime();

        int eidx = indexOf(a);
        t1 = System.nanoTime();
        indexOfTimeNS = t1 - t0;

        if( eidx>=0 )return eidx;

        SortInsert si = sortInsert();
        t0 = System.nanoTime();
        int idx = si.sortInsert(list, a, comparator(), 0, list.size());
        nextscn();
        t1 = System.nanoTime();
        sortInsertTimeNS = t1 - t0;

        return idx;
    }

    @Override
    public void add(Iterable<A> adds, BiConsumer<Integer, A> added) {
        if( adds==null )throw new IllegalArgumentException("adds == null");
        writeLock(()->{
            for( A a : adds ){
                if( a!=null ){
                    int idx = add0(a);
                    if( added!=null ){
                        added.accept(idx,a);
                    }
                }
            }
            updateMinMax();
        });
    }

    @Override
    public void add(IndexSet<A> adds, final BiConsumer<Integer, A> added) {
        if( adds==null )throw new IllegalArgumentException("adds == null");
        writeLock(()->{
            adds.each( (A a) -> {
                        if (a != null) {
                            int idx = add0(a);
                            if (added != null) {
                                added.accept(idx, a);
                            }
                        }
                    }
                );
            updateMinMax();
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="remove()">
    @Override
    public int remove(A a) {
        if( a==null )return -1;
        return writeLock(()->{
            int idx = remove0(a);
            updateMinMax();
            return idx;
        });
    }

    private int remove0(A a){
        if( a==null )return -1;

        int idx = indexOf(a);
        if( idx<0 )return -1;

        list.remove(idx);
        nextscn();

        return idx;
    }

    @Override
    public void remove(Iterable<A> removes, BiConsumer<Integer, A> removed) {
        if( removes==null )throw new IllegalArgumentException("removes == null");
        writeLock(()->{
            for( A a : removes ){
                int idx = remove0(a);
                if( idx>=0 && removed!=null ){
                    removed.accept(idx,a);
                }
            }
            updateMinMax();
        });
    }

    @Override
    public void remove(IndexSet<A> removes, final BiConsumer<Integer, A> removed) {
        if( removes==null )throw new IllegalArgumentException("removes == null");
        writeLock(()->{
            //for( A a : removes ){
            removes.each( (A a) -> {
                    int idx = remove0(a);
                    if( idx>=0 && removed!=null ){
                        removed.accept(idx,a);
                    }
                });
            updateMinMax();
        });
    }

    @Override
    public A removeByIndex(int idx) {
        if( idx<0 )throw new IndexOutOfBoundsException("idx < 0");
        return writeLock(()->{
            if( idx>=size() )throw new IndexOutOfBoundsException("idx("+idx+") > size("+size()+")");
            A res = list.remove(idx);
            nextscn();
            updateMinMax();
            return res;
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="clear()">
    @Override
    public void clear(){
        writeLock(()->{
            if( list!=null ){
                list.clear();
                nextscn();
                updateMinMax();
            }
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="tailEntry()">    
    /**
     * Последняя максимальная глубина поиска
     */
    protected int tailEntryLastMaxDepth = 0;

    /**
     * Текущаяя глубина поиска tailEntry
     */
    protected int tailEntryCurrentDepth = 0;

    /**
     * Время (нс) начало поиска (enter in try/finally &amp;&amp; tailEntryCurrentDeep = 1)
     */
    protected long tailEntryStarted  = 0;

    /**
     * Время (нс) конца поиска (exit in try/finally &amp;&amp; tailEntryCurrentDeep = 1)
     */
    protected long tailEntryFinished = 0;

    /**
     * Размер сканированных данных
     */
    protected int tailEntryScanSize = 0;

    /**
     * Кол-во чтений (get(idx))
     */
    protected int tailEntryGetCall = 0;

    @Override
    public Pair<Integer, A> tailEntry(A a, boolean strong, int beginIndex, int endExIndex) {
        if( a==null )throw new IllegalArgumentException("a === null");
        final int f1_begin = beginIndex>endExIndex ? endExIndex : beginIndex;
        final int f1_endEx = beginIndex>endExIndex ? beginIndex : endExIndex;
//        if( begin>endEx ){
//            int t = begin;
//            begin = endEx;
//            endEx = t;
//        }
        return readLock(()->{
            try{
                tailEntryCurrentDepth++;
                if( tailEntryCurrentDepth==1 ){
                    tailEntryLastMaxDepth = 1;
                    tailEntryStarted = System.nanoTime();
                    tailEntryScanSize = 0;
                    tailEntryGetCall = 0;
                }else if( tailEntryCurrentDepth>1 ){
                    if( tailEntryLastMaxDepth<tailEntryCurrentDepth ){
                        tailEntryLastMaxDepth=tailEntryCurrentDepth;
                    }
                }

                final int endEx = f1_endEx>size() ? size() : f1_endEx;
                final int begin = f1_begin<0 ? 0 : f1_begin;

//                if( endEx>size() ){ endEx = size(); }
//                if( begin<0 ){ begin = 0; }

                int searchAreaSize = endEx - begin;

                if( searchAreaSize<=0 )return null;

                if( searchAreaSize==1 ){
                    A b = get(begin);
                    tailEntryGetCall++;
                    int cmp = b.compareTo(a);
                    if( (strong && cmp>0) || (!strong && cmp>=0) ){
                        return Pair.of(begin,b);
                    }else{
                        return null;
                    }
                }else if( searchAreaSize<indexOfScanRange() && indexOfScanRange()>=2 ){
                    for(int bi=begin;bi<endEx;bi++){
                        tailEntryScanSize++;
                        A b = get(bi);
                        tailEntryGetCall++;
                        int cmp = b.compareTo(a);
                        if( (strong && cmp>0) || (!strong && cmp>=0) ){
                            return Pair.of(bi,b);
                        }
                    }
                    return null;
                }else{
                    int halfOffset = (endEx - begin) / 2;

                    A bgnEl = get( begin );
                    A ctrEl = get( begin+halfOffset );
                    A endEl = get( endEx-1 );
                    tailEntryGetCall+=3;

                    int cb = bgnEl.compareTo(a);
                    boolean fb = (strong && cb > 0) || (!strong && cb >= 0);

                    int cc = ctrEl.compareTo(a);
                    boolean fc = (strong && cc > 0) || (!strong && cc >= 0);

                    int ce = endEl.compareTo(a);
                    boolean fe = (strong && ce > 0) || (!strong && ce >= 0);

                    //    Возможны след. комбинации
                    //
                    //    |b|   |c|   |e|
                    //    |-|---|-|---|-|
                    //    |x|xxx|x|xxx|x| искомые данные представлены во всем наборе
                    //    | | xx|x|xxx|x| искомые данные частично в левой, и польностью в правой
                    //    | |   | | xx|x| искомые данные частично в правой
                    //    | |   | |   |x| искомые данные частично в правой
                    //    | |   | |   | | нет искомых данных
                    //    |x|xxx|x|x  | | не возможная ситуация - повреждение данных
                    //    |x|xxx| |   | | не возможная ситуация - повреждение данных
                    //    |x|xxx| |  x|x| не возможная ситуация - повреждение данных

                    if( fb==true && (fc==false || fe==false)  ){
                        throw new IllegalStateException("data corrupt, state="+fb+" "+fc+" "+fe);
                    }

                    if( fb==false && fc==false && fe==false )return null;
                    if( fb==true ){
                        return Pair.of(begin,bgnEl);
                    }

                    // далее fb = false
                    if( fc==false ){
                        // искомые данные частично в правой
                        return tailEntry( a, strong, begin+halfOffset, endEx );
                    }else {
                        // искомые данные частично в левой
                        return tailEntry( a, strong, begin, begin+halfOffset );
                    }
                }
            }finally{
                if( tailEntryCurrentDepth==1 ){
                    tailEntryFinished = System.nanoTime();
                }
                tailEntryCurrentDepth--;
            }
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="headEntry()">
    /**
     * Последняя максимальная глубина поиска
     */
    protected int headEntryLastMaxDepth = 0;

    /**
     * Текущаяя глубина поиска headEntry
     */
    protected int headEntryCurrentDepth = 0;

    /**
     * Время (нс) начало поиска (enter in try/finally &amp;&amp; headEntryCurrentDeep = 1)
     */
    protected long headEntryStarted  = 0;

    /**
     * Время (нс) конца поиска (exit in try/finally amp;&amp; headEntryCurrentDeep = 1)
     */
    protected long headEntryFinished = 0;

    /**
     * Размер сканированных данных
     */
    protected int headEntryScanSize = 0;

    /**
     * Кол-во чтений (get(idx))
     */
    protected int headEntryGetCall = 0;

    @Override
    public Pair<Integer, A> headEntry(A a, boolean strong, int beginIndex, int endExIndex) {
        if( a==null )throw new IllegalArgumentException("a === null");
        final int f1_begin = beginIndex>endExIndex ? endExIndex : beginIndex;
        final int f1_endEx = beginIndex>endExIndex ? beginIndex : endExIndex;
        return readLock(()->{
            try{
                headEntryCurrentDepth++;
                if( headEntryCurrentDepth==1 ){
                    headEntryLastMaxDepth = 1;
                    headEntryStarted = System.nanoTime();
                    headEntryScanSize = 0;
                    headEntryGetCall = 0;
                }else if( headEntryCurrentDepth>1 ){
                    if( headEntryLastMaxDepth<headEntryCurrentDepth ){
                        headEntryLastMaxDepth=headEntryCurrentDepth;
                    }
                }

//                if( begin>endEx ){
//                    int t = begin;
//                    begin = endEx;
//                    endEx = t;
//                }

                boolean recalcSearchAreaSize = false;

                int endEx = f1_endEx;
                int begin = f1_begin;

                if( endEx>size() ){ endEx = size(); recalcSearchAreaSize = true; }
                if( begin<0 ){ begin = 0; recalcSearchAreaSize = true; }

                int searchAreaSize = endEx - begin;
                if( searchAreaSize<=0 )return null;

                if( searchAreaSize==1 ){
                    A b = get(begin);
                    headEntryGetCall++;
                    int cmp = b.compareTo(a);
                    if( (strong && cmp<0) || (!strong && cmp<=0) ){
                        return Pair.of(begin,b);
                    }else{
                        return null;
                    }
                }else if( searchAreaSize<indexOfScanRange() && indexOfScanRange()>=2 ){
                    //for(int bi=begin;bi<endEx;bi++){
                    for(int bi=endEx-1;bi>=begin;bi--){
                        headEntryScanSize++;
                        A b = get(bi);
                        headEntryGetCall++;
                        int cmp = b.compareTo(a);
                        if( (strong && cmp<0) || (!strong && cmp<=0) ){
                            return Pair.of(bi,b);
                        }
                    }
                    return null;
                }else{
                    int halfOffset = (endEx - begin) / 2;

                    A bgnEl = get( begin );
                    A ctrEl = get( begin+halfOffset );
                    A endEl = get( endEx-1 );
                    headEntryGetCall+=3;

                    int cb = bgnEl.compareTo(a);
                    boolean fb = (strong && cb < 0) || (!strong && cb <= 0);

                    int cc = ctrEl.compareTo(a);
                    boolean fc = (strong && cc < 0) || (!strong && cc <= 0);

                    int ce = endEl.compareTo(a);
                    boolean fe = (strong && ce < 0) || (!strong && ce <= 0);

                    //    Возможны след. комбинации
                    //
                    //    |b|   |c|   |e|
                    //    |-|---|-|---|-|
                    //    |x|xxx|x|xxx|x| искомые данные представлены во всем наборе
                    //    |x|xxx|x|xx | | искомые данные частично в правой, и польностью в левой
                    //    |x|xxx|x|   | | искомые данные частично в правой
                    //    |x|xx | |   | | искомые данные частично в левой
                    //    | |   | |   | | нет искомых данных
                    //    | | xx|x|xxx|x| не возможная ситуация - повреждение данных
                    //    | |   |x|xxx|x| не возможная ситуация - повреждение данных
                    //    | |   | |  x|x| не возможная ситуация - повреждение данных

                    if( fe==true && (fc==false || fb==false)  ){
                        throw new IllegalStateException("data corrupt, state="+fb+" "+fc+" "+fe);
                    }

                    if( fb==false && fc==false && fe==false )return null;
                    if( fe==true )return Pair.of(endEx-1,endEl);

                    // далее fe = false
                    if( fc==false ){
                        if( fb==false )return null;

                        // искомые данные частично в левой
                        return headEntry( a, strong, begin, begin+halfOffset );
                    }else {
                        // искомые данные частично в правой
                        return headEntry( a, strong, begin+halfOffset, endEx );
                    }
                }
            } finally {
                if( headEntryCurrentDepth==1 ){
                    headEntryFinished = System.nanoTime();
                }
                headEntryCurrentDepth--;
            }
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="un impl">    
    //@Override
    private Pair<Integer, Integer> update(A a, A b, boolean allowMerge, boolean generateError) {
        if( a==null )throw new IllegalArgumentException("a == null");
        if( b==null )throw new IllegalArgumentException("b == null");

        return writeLock(()->{
            int ia = indexOf(a);
            if( ia<0 )return null;

            int cmpAB = a.compareTo(b);
            if( cmpAB==0 )return Pair.of(ia,ia);

            if( cmpAB<0 ){
                // b должно стоять левее a
                // ищем конец головы
                Pair<Integer,A> headEndEn = headEntry(b, false, 0, ia);
            }else{
                // b должно стоять правее a
                // ищем начало хвоста
                Pair<Integer,A> tailBeginEn = tailEntry(b, false, ia, size());
            }

            throw new UnsupportedOperationException("Not supported yet.");
        });
    }
    //</editor-fold>
}
