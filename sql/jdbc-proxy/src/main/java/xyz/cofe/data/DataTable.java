/*
 * The MIT License
 *
 * Copyright 2017 user.
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

package xyz.cofe.data;

import xyz.cofe.collection.*;
import xyz.cofe.ecolls.Closeables;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.fn.Fn0;
import xyz.cofe.fn.Fn1;
import xyz.cofe.fn.TripleConsumer;
import xyz.cofe.iter.Eterable;
import xyz.cofe.ecolls.ReadWriteLockSupport;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Таблица с данными.
 * 
 * <p>
 * Каждая строка таблицы может находится в нескольких состояниях: <br>
 * Fixed, Updated - содержится в коллекции рабочих строк (getWorkedRows()) <br>
 * Inserted - содержится в коллекции рабочих строк и коллекции новых строк (getWorkedRows())
 * @author Kamnev Georgiy
 * @see DataRow
 */
public class DataTable implements ReadWriteLockSupport
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private transient static final Logger logger = Logger.getLogger(DataTable.class.getName());

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
        logger.entering(DataTable.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(DataTable.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(DataTable.class.getName(), method, result);
    }
    //</editor-fold>
    
    /**
     * Конструктор по умолчанию
     */
    public DataTable(){
        initConstraints();
        initRowChangeTracking();
    }
    
    /**
     * Конструктор
     * @param columns Описания колонок таблицы
     */
    public DataTable( DataColumn[] columns ){
        this(columns, null);
    }    
    
    /**
     * Конструктор
     * @param columns Описания колонок таблицы
     * @param initialData Начальные данные
     */
    public DataTable( DataColumn[] columns, Iterable<Object[]> initialData ){
        if( columns==null )throw new IllegalArgumentException("columns==null");
        for( int ci=0; ci<columns.length; ci++ ){
            DataColumn mc = columns[ci];
            if( mc==null )throw new IllegalArgumentException("columns["+ci+"]==null");
            
            getColumnsEventList().add(mc);
        }
        
        if( initialData!=null ){
            int ri = -1;
            for( Object[] data : initialData ){
                ri++;
                if( data==null )throw new IllegalArgumentException("initialData["+ri+"]==null");
                
                DataRow mrow = new DataRow(this, data);
                getWorkedRows().add(mrow);
            }
        }
        
        initConstraints();
        initRowChangeTracking();
    }
    
    /**
     * Конструктор де сериализации
     * @param columns Набор колонок
     * @param rows Фиксированные/Модифицированные строки
     * @param inserted Добавленные, но не фиксированные строки
     * @param deleted Удаленные, но не фиксированные строки
     */
    public DataTable( DataColumn[] columns, Iterable<DataRow> rows, Iterable<DataRow> inserted, Iterable<DataRow> deleted){
        if( columns==null )throw new IllegalArgumentException("columns==null");
        
        for( int ci=0; ci<columns.length; ci++ ){
            DataColumn mc = columns[ci];
            if( mc==null )throw new IllegalArgumentException("columns["+ci+"]==null");
            
            getColumnsEventList().add(mc);
        }
        
        if( rows!=null ){
            for( DataRow mrow : rows ){
                if( mrow==null )continue;
                getWorkedRows().add(mrow);
            }
        }
        
        if( inserted!=null ){
            for( DataRow mrow : inserted ){
                if( mrow==null )continue;
                getInsertedRows().add(mrow);
            }
        }
        
        if( deleted!=null ){
            for( DataRow mrow : deleted ){
                if( mrow==null )continue;
                getDeletedRows().add(mrow);
            }
        }
        
        initConstraints();
        initRowChangeTracking();
    }

    private final ReentrantReadWriteLock sync = new ReentrantReadWriteLock();

    //<editor-fold defaultstate="collapsed" desc="read write lock">
    public ReentrantReadWriteLock getReadWriteLock(){ return sync; }
    @Override public Lock getReadLock(){ return sync.readLock(); }
    @Override public Lock getWriteLock(){ return sync.writeLock(); }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="data events support">
    protected transient final DataEventSupport listeners = new DataEventSupport();
    
    /**
     * Добавляет подписчика на события
     * @param ls Подписчик
     * @param weak true - добавить как weak ссылку
     * @return Отписка
     */
    public AutoCloseable addDataEventListener(DataEventListener ls, boolean weak) {
        return listeners.addDataEventListener(ls, weak);
    }
    
    /**
     * Добавляет подписчика на события
     * @param ls Подписчик
     * @return Отписка
     */
    public AutoCloseable addDataEventListener(DataEventListener ls) {
        return listeners.addDataEventListener(ls);
    }
    
    /**
     * Удаляет подписчика
     * @param ls подписчик
     */
    public void removeDataEventListener(DataEventListener ls) {
        listeners.removeDataEventListener(ls);
    }
    
    /**
     * Проверка наличия подписчика
     * @param ls подписчик
     * @return true подписан
     */
    public boolean hasDataEventListener(DataEventListener ls) {
        return listeners.hasDataEventListener(ls);
    }
    
    /**
     * Возвращает список подписчиков
     * @return подписчики
     */
    public DataEventListener[] getDataEventListeners() {
        return listeners.getDataEventListeners();
    }
    
    /**
     * Уведомляет подписчиков о событии
     * @param event событие
     */
    public void fireEvent( DataEvent event) {
        listeners.fireDataEvent(event);
        scn.incrementAndGet();
    }
    
    protected final LinkedBlockingQueue<DataEvent> eventQueue
        = new LinkedBlockingQueue<>();
    
    /**
     * Добавляет событие в очередь
     * @param ev событие
     */
    public void addDataEvent( DataEvent ev){
        if( ev!=null )eventQueue.add(ev);
        scn.incrementAndGet();        
    }
    
    /**
     * Рассылает события из очереди подписчикам
     */
    public void fireEventQueue(){
        int ll = eventLockLevel.get();
        if( ll>0 )return;
        while( true ){
            DataEvent e = eventQueue.poll();
            if( e==null )break;
            
            listeners.fireDataEvent(e);
        }
    }
    
    protected final transient AtomicInteger eventLockLevel = new AtomicInteger(0);

    /**
     * Выполенеие внутреннего кода
     */
    public interface InternalRun {
        public List<DataRow> getWorkedRows();
        public Set<DataRow> getInsertedRows();
        public Set<DataRow> getDeletedRows();
        public long nextScn();
    }
    
    /**
     * Создаение объекта для доступа к внутренним объектам
     * @return доступ к внутренним объектам
     */
    protected InternalRun createInternalRun(){
        return new InternalRun() {
            @Override
            public List<DataRow> getWorkedRows() {
                return DataTable.this.getWorkedRows();
            }

            @Override
            public Set<DataRow> getInsertedRows() {
                return DataTable.this.getInsertedRows();
            }

            @Override
            public Set<DataRow> getDeletedRows() {
                return DataTable.this.getDeletedRows();
            }

            @Override
            public long nextScn() {
                return DataTable.this.nextScn();
            }
        };
    }
    
    /**
     * Выполнение внутреннего кода
     * @param run внутренний код
     * @return Результат выполнения
     */
    protected Object lockRunInternal( final Fn1<InternalRun,Object> run ){
        if( run==null )throw new IllegalArgumentException("run == null");
        return writeLock(() -> run.apply(createInternalRun()));
    }
    
    /**
     * Подписка на события определенного типа
     * @param <T> Тип события
     * @param evnType Тип события
     * @param weakRef Добавить подписчика как weak ссылку
     * @param listener Подписчик
     * @return Отписка от события
     */
    public <T extends DataEvent> AutoCloseable listen( final Class<T> evnType, boolean weakRef, final Consumer<T> listener ){
        if( evnType==null )throw new IllegalArgumentException("evnType == null");
        if( listener==null )throw new IllegalArgumentException("listener == null");
        return addDataEventListener(new DataEventListener() {
            @Override
            public void dataEvent( DataEvent ev) {
                if( ev==null )return;
                if( evnType.isAssignableFrom(ev.getClass()) ){
                    listener.accept((T)ev);
                }
            }
        }, weakRef);
    }
    
    /**
     * Подписка на события определенного типа
     * @param <T> Тип события
     * @param evnType Тип события
     * @param listener Подписчик
     * @return Отписка от получения событий
     */
    public <T extends DataEvent> AutoCloseable listen( final Class<T> evnType, final Consumer<T> listener ){
        if( evnType==null )throw new IllegalArgumentException("evnType == null");
        if( listener==null )throw new IllegalArgumentException("listener == null");
        return listen(evnType, false, listener);
    }
    
    /**
     * Добавление на событие добавления колонки
     * @param weak Добавить подписчика как weak ссылку
     * @param listener Подписчик
     * @return Отписка от получения событий
     */
    public AutoCloseable onColumnAdded( boolean weak, Consumer<DataColumnAdded> listener ){
        return listen(DataColumnAdded.class, weak, listener);
    }
    
    /**
     * Добавление на событие добавления колонки
     * @param listener Подписчик
     * @return Отписка от получения событий
     */
    public AutoCloseable onColumnAdded( Consumer<DataColumnAdded> listener ){
        return listen(DataColumnAdded.class, listener);
    }
    
    /**
     * Добавление на событие - колонка удалена
     * @param weak Добавить подписчика как weak ссылку
     * @param listener Подписчик
     * @return Отписка от получения событий
     */
    public AutoCloseable onColumnRemoved( boolean weak, Consumer<DataColumnAdded> listener ){
        return listen(DataColumnAdded.class, weak, listener);
    }
    
    /**
     * Добавление на событие - колонка удалена
     * @param listener Подписчик
     * @return Отписка от получения событий
     */
    public AutoCloseable onColumnRemoved( Consumer<DataColumnRemoved> listener ){
        return listen(DataColumnRemoved.class, listener);
    }
    
    /**
     * Добавление на событие: строка почена под удаление
     * @param weak Добавить подписчика как weak ссылку
     * @param listener Подписчик
     * @return Отписка от получения событий
     */
    public AutoCloseable onRowDeleted( boolean weak, Consumer<DataRowDeleted> listener ){
        return listen(DataRowDeleted.class, weak, listener);
    }
    
    /**
     * Добавление на событие: строка почена под удаление
     * @param listener Подписчик
     * @return Отписка от получения событий
     */
    public AutoCloseable onRowDeleted( Consumer<DataRowDeleted> listener ){
        return listen(DataRowDeleted.class, listener);
    }
    
    /**
     * Добавление на событие отката удаляения строки
     * @param weak Добавить подписчика как weak ссылку
     * @param listener Подписчик
     * @return Отписка от получения событий
     */
    public AutoCloseable onRowUndeleted( boolean weak, Consumer<DataRowUndeleted> listener ){
        return listen(DataRowUndeleted.class, weak, listener);
    }
    
    /**
     * Добавление на событие отката удаляения строки
     * @param listener Подписчик
     * @return Отписка от получения событий
     */
    public AutoCloseable onRowUndeleted( Consumer<DataRowUndeleted> listener ){
        return listen(DataRowUndeleted.class, listener);
    }
    
    /**
     * Добавление на событие окончательного удаляения строки
     * @param weak Добавить подписчика как weak ссылку
     * @param listener Подписчик
     * @return Отписка от получения событий
     */
    public AutoCloseable onRowErased( boolean weak, Consumer<DataRowErased> listener ){
        return listen(DataRowErased.class, weak, listener);
    }
    
    /**
     * Добавление на событие окончательного удаляения строки
     * @param listener Подписчик
     * @return Отписка от получения событий
     */
    public AutoCloseable onRowErased( Consumer<DataRowErased> listener ){
        return listen(DataRowErased.class, listener);
    }
    
    /**
     * Добавление на событие полного удаления таблицы, включая структуры
     * @param weak Добавить подписчика как weak ссылку
     * @param listener Подписчик
     * @return Отписка от получения событий
     */
    public AutoCloseable onDataTableDropped( boolean weak, Consumer<DataTableDropped> listener ){
        return listen(DataTableDropped.class, listener);
    }
    
    /**
     * Добавление на событие полного удаления таблицы, включая структуры
     * @param listener Подписчик
     * @return Отписка от получения событий
     */
    public AutoCloseable onDataTableDropped( Consumer<DataTableDropped> listener ){
        return listen(DataTableDropped.class, listener);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Ограничения">
    //<editor-fold defaultstate="collapsed" desc="constraintsListeners">
    /**
     * Подписчики обсуживающие ограничения таблицы
     */
    private transient final Closeables constraintsListeners = new Closeables();
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="initConstraints()">
    /**
     * Инициализация ограничений
     */
    private void initConstraints(){
        listenForNotNullValue(constraintsListeners, getWorkedRows());
        listenForValueType(constraintsListeners, getWorkedRows());
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="listenForNotNullValue()">
    /**
     * Проверка что добавляемая/обновляемая строка содержит необходиммые данные
     * @param cset Отписка (может быть null)
     * @param elist Список строк
     */
    private void listenForNotNullValue( Closeables cset, EventList<DataRow> elist ){
        TripleConsumer<Integer,DataRow,DataRow> ls = (idx,old,cur) -> {
            if( cur!=null ){
                synchronized(cur){
                    DataTable.this.readLock( ()->{
                        RuntimeException err = checkNullableValue(cur,true);
                        if( err!=null )throw err;
                    });
                }
            }
        };

        if( elist instanceof PreEventList ){
            cset.add(((PreEventList<DataRow>) elist).onInserting(ls));
            cset.add(((PreEventList<DataRow>) elist).onUpdating(ls));
        }else{
            cset.add(elist.onInserted(ls));
            cset.add(elist.onUpdated(ls));
        }
    }
    
    /**
     * Осуществляет проверку, что строка содержит необходимые данные, 
     * то есть выполняется соответсвующее условие колонки isAllowNull().
     * @param dr строка
     * @param initNull Инициализировать null значения, если есть соот возможность
     * @return null - если условие isAllowNull() выполненно для всех колонок или описание ошибки
     */
    private RuntimeException checkNullableValue( DataRow dr, boolean initNull ){
        int ci = -1;
        for( DataColumn dc : getColumnsEventList() ){
            ci++;
            if( dc.isAllowNull() )continue;
            
            Object value = dr.get(ci);
            Fn0 fgen = dc.getGenerator();
            
            if( value==null ){
                if( fgen!=null ){
                    Object nval = fgen.apply();
                    if( nval==null ){
                        return new NullPointerException("column (index="+ci+" name="+dc.getName()+") not allow null value");
                    }else{
                        RuntimeException typeErr = checkValueType(nval, dc, ci);
                        if( typeErr!=null )return typeErr;
                        
                        value = nval;                        
                        dr.set(ci, value);
                    }
                }
                
                return new NullPointerException("column (index="+ci+" name="+dc.getName()+") not allow null value");
            }
        }
        return null;
    }
    //</editor-fold>
    
    /**
     * Проверка что добавляемая/обновляемая строка содержит данные необходимого типа
     * @param cset Отписка (может быть null)
     * @param elist Список строк
     */
    private void listenForValueType( Closeables cset, EventList<DataRow> elist ){
//        Closeable cl =
//            elist.onAdding(new Reciver<DataRow>() {
//                @Override
//                public void recive(DataRow dr) {
//                    if( dr==null )throw new IllegalStateException("null rows not allowed");
//                    synchronized(dr){
//                        RuntimeException err = checkValueType(dr);
//                        if( err!=null )throw err;
//                    }
//                }
//            });

        TripleConsumer<Integer,DataRow,DataRow> ls = (idx,old,cur) -> {
            if( cur!=null ){
                synchronized( cur ){
                    RuntimeException err = checkValueType(cur);
                    if( err != null ) throw err;
                }
            }
        };

        AutoCloseable cl1 = elist.onInserted(ls);
        AutoCloseable cl2 = elist.onUpdated(ls);

        if( cset!=null )cset.add(cl1);
        if( cset!=null )cset.add(cl2);
    }
    
    /**
     * Осуществляет проверку, что строка содержит тип данных соответ колонке, 
     * то есть выполняется соответсвующее условие колонки getDataType() и isAllowSubTypes().
     * @param dr строка
     * @return null - если условие getDataType() и isAllowSubTypes() выполненно для всех колонок или описание ошибки
     */
    private RuntimeException checkValueType( DataRow dr ){
        int ci = -1;
        for( DataColumn dc : getColumnsEventList() ){
            ci++;

            Object value = dr.get(ci);
            if( value==null ){
                continue;
            }
            
            RuntimeException err = checkValueType(value, dc, ci);
            if( err!=null )return err;
        }
        return null;
    }
    
    /**
     * Осуществляет проверку, что значение содержит тип данных соответ колонке, 
     * то есть выполняется соответсвующее условие колонки getDataType() и isAllowSubTypes().
     * @param value значение
     * @param dc колонка
     * @param ci индекс колонки
     * @return null - если условие getDataType() и isAllowSubTypes() выполненно для всех колонок или описание ошибки
     */
    private RuntimeException checkValueType( Object value, DataColumn dc, int ci ){
        Class dtype = dc.getDataType();
            
        boolean allowSubType = true;
        allowSubType = dc.isAllowSubTypes();

        if( value!=null && dtype!=null ){
            if( allowSubType ){
                boolean assignable = dtype.isAssignableFrom(value.getClass());
                if( !assignable ){
                    return new ClassCastException(
                        "column(ci="+ci+" name="+dc.getName()+")"
                        + " value type("+value.getClass()+") not assignable from "+dtype);
                }
            }else{
                boolean assignable = dtype.equals(value.getClass());
                if( !assignable ){
                    return new ClassCastException(
                        "column(ci="+ci+" name="+dc.getName()+")"+
                        " value type("+value.getClass()+") not equals to "+dtype
                    );
                }
            }
        }
        
        return null;
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="initRowChangeTracking()">
    /**
     * Слушает изменения рабочего набора и 
     * при добавлении новой строки в WorkedRows отмечает ее как новую (getInsertedRows().add()) 
     * 
     * <p>
     * при удалении из рабочего набора строки, удаляет ее так же из (getInsertedRows)
     */
    public class WorkedRowsOnDeletedTacking implements TripleConsumer<Integer, DataRow, DataRow> {
        @Override
        public void accept(Integer idx, DataRow oldrow, DataRow nullRow) {
            /*if( oldrow!=null ){
                getDeletedRows().add(oldrow);
            }*/

            if( oldrow!=null ){
                if( getInsertedRows().contains(oldrow) ){
                    // INSERTED => ERASE
                    getInsertedRows().remove(oldrow);
                    addDataEvent(new DataRowErased(DataTable.this, oldrow, idx));
                }else{
                    // FIXED => DELETED
                    getDeletedRows().add(oldrow);
                    addDataEvent(new DataRowDeleted(DataTable.this, oldrow, idx));
                }
            }

            addDataEvent(new DataRowDeleted(DataTable.this, oldrow, idx));
            fireEventQueue();
        }
    }
    
    /**
     * Слушает изменения рабочего набора и 
     * при добавлении новой строки в WorkedRows отмечает ее как новую (getInsertedRows().add()) 
     * 
     * <p>
     * при удалении из рабочего набора строки, удаляет ее так же из (getInsertedRows)
     */
    public class WorkedRowsOnUpdateInsertTracking implements TripleConsumer<Integer, DataRow, DataRow> {
        @Override
        public void accept(Integer idx, DataRow oldrow, DataRow newrow) {
            if( oldrow!=null ){
                if( getInsertedRows().contains(oldrow) ){
                    // INSERTED => ERASE
                    getInsertedRows().remove(oldrow);
                    addDataEvent(new DataRowErased(DataTable.this, oldrow, idx));
                }else{
                    // FIXED => DELETED
                    getDeletedRows().add(oldrow);
                    addDataEvent(new DataRowDeleted(DataTable.this, oldrow, idx));
                }
            }
            if( newrow!=null ){
                getInsertedRows().add(newrow);
                addDataEvent(new DataRowInserted(DataTable.this, newrow, idx));
            }
            fireEventQueue();
        }
    }
    
    /**
     * Инициализация отслеживания изменений.
     * 
     * <p>
     * Добавляет трекеры на рабочий набор строк, которые ставят/снимают отметки в наборе Inserted
     * 
     * @see #getWorkedRows() 
     * @see #getInsertedRows() 
     */
    private void initRowChangeTracking(){
        AutoCloseable cl =
            getWorkedRows().onDeleted(new WorkedRowsOnDeletedTacking());
        
        rowTrackingListeners.add(cl);
        
        TripleConsumer fUpdateInsertTracker = new WorkedRowsOnUpdateInsertTracking();
        
        cl = getWorkedRows().onInserted(fUpdateInsertTracker);
        rowTrackingListeners.add(cl);
        
        cl = getWorkedRows().onUpdated(fUpdateInsertTracker);
        rowTrackingListeners.add(cl);
    }
        
    private final Closeables rowTrackingListeners = new Closeables();
    
    /**
     * Указывает отслеживать изменения
     * @return true - отслеживание включенно
     */
    public boolean isTrackChanges(){
        synchronized(rowTrackingListeners){
            Object[] clarr = rowTrackingListeners.getCloseables();
            return clarr != null && clarr.length > 0;
        }
    }
    
    /**
     * Указывает отслеживать изменения
     * @param track true - Отслеживать
     */
    public void setTrackChanges(boolean track){
        synchronized( rowTrackingListeners ){
            rowTrackingListeners.close();
            if( track ){
                initRowChangeTracking();
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="scn:long">
    private final AtomicLong scn = new AtomicLong(0L);
    
    /**
     * Возвращает еткущий номер изенений
     * @return текущий номер изменений
     */
    public long getScn(){
        return scn.get();
    }
    
    /**
     * Указывает текущий номер изменений
     * @return текущий номер изменений
     */
    protected long nextScn(){
        return scn.incrementAndGet();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="columns">
    private volatile EventList<DataColumn> columns;
    
    /**
     * Структура таблицы - колонки
     * @return колонки таблицы
     */
    private EventList<DataColumn> getColumnsEventList(){
        if( columns!=null )return columns;
        synchronized(this){
            if( columns!=null )return columns;
            columns = new BasicEventList<>( new ArrayList<DataColumn>(), sync);
            initReadonlyColumns(null, columns);
            return columns;
        }
    }
    
    /**
     * Возвращает массив колонок
     * @return колонки таблицы
     */
    public DataColumn[] getColumns(){
        return readLock(() -> {
            ArrayList<DataColumn> l = new ArrayList<>();
            for( DataColumn dc : getColumnsEventList() ){
                l.add(dc.clone());
            }
            return l.toArray(new DataColumn[]{});
        });
    }
    
    /**
     * Возвращает кол-во колонок в таблице
     * @return кол-во колонок
     */
    public int getColumnsCount(){
        return getColumnsEventList().size();
    }
    
    /**
     * Возвращает колонку по ее индексу
     * @param cidx индекс
     * @return колонка
     */
    public DataColumn getColumn( int cidx ){
        if( cidx<0 )throw new IllegalArgumentException("cidx<0");
        EventList<DataColumn> dclist = getColumnsEventList();
        if( cidx>=dclist.size() ){
            throw new IllegalArgumentException("cidx>="+dclist.size());
        }
        return dclist.get(cidx);
    }
    
    /**
     * Добавляет колонку к таблице
     * @param dc колонка
     */
    public void addColumn( final DataColumn dc ){
        if( dc==null )throw new IllegalArgumentException("dc == null");
        writeLock( ()->getColumnsEventList().add(dc) );
    }
    
    /**
     * Удаляет колонку из таблицы
     * @param dc колонка
     */
    public void removeColumn( final DataColumn dc ){
        if( dc==null )throw new IllegalArgumentException("dc == null");
        writeLock( ()->getColumnsEventList().remove(dc) );
    }
    
    /**
     * Удаляет колонку по ее индексу
     * @param colIdx индекс колонки
     */
    public void removeColumnByIndex( final int colIdx ){
        writeLock( ()->getColumnsEventList().remove(colIdx) );
    }
    
    /**
     * Удаляет все колонки
     */
    public void dropColumns(){
        writeLock(() -> getColumnsEventList().clear());
    }
    
    /**
     * Ограничение на операции с колонками.
     * 
     * <p>
     * Операции с колонками можно производить когда таблица не содержит данных
     */
    public class ReadonlyColumnsConstraint implements TripleConsumer<Integer, DataColumn, DataColumn> {
        @Override
        public void accept( Integer arg1, DataColumn arg2, DataColumn arg3) {

            boolean hasData = getWorkedRows().size() > 0;
            boolean hasInsData = getInsertedRows().size() > 0;
            boolean hasDelData = getDeletedRows().size() > 0;

            if( hasData || hasDelData || hasInsData ){
                throw new IllegalStateException("can't modify columns< while table contains data");
            }
        }
    }
    
    /**
     * Добавление ограничения, что структуру таблицы можно менять, только когда она пустая
     * @param cset Подписанты
     * @param columns Структура
     */
    private void initReadonlyColumns( Closeables cset, EventList<DataColumn> columns ){
        if( columns==null )throw new IllegalArgumentException("columns == null");

        ReadonlyColumnsConstraint readonlyColumnsConstraint = new ReadonlyColumnsConstraint();

        AutoCloseable cl =
            columns.onChanged(readonlyColumnsConstraint);
        
        if( cset!=null )cset.add(cl);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="rowsCount">
    /**
     * Возвращает кол-во строк в рабочем наборе
     * @return кол-во строк в рабочем наборе
     */
    public int getRowsCount() {
        return readLock(() -> getWorkedRows().size());
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="row(idx):DataRow">
    /**
     * Возвращает строку по индексу из рабочего набора
     * @param row индекс строки
     * @return строка
     */
    public DataRow getRow(final int row) {
        //synchronized(this){
        //    if( row<0 )throw new IllegalArgumentException("row < 0");
        //    if( row>=getWorkedRows().size() )throw new IllegalArgumentException("row("+row+") >= rowsCount("+getWorkedRows().size()+")");
        //    return getWorkedRows().get(row);
        //}
        if( row<0 )throw new IllegalArgumentException("row < 0");
        return readLock(() -> {
            if( row>=getWorkedRows().size() )throw new IllegalArgumentException("row("+row+") >= getRowsCount("+getWorkedRows().size()+")");
            return getWorkedRows().get(row);
        });
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="indexOf(row):int">
    private final WeakHashMap<DataRow,Integer> rowIndexCache = new WeakHashMap<>();
    private transient boolean indexOf_lastIsInDeleted = false;
    
    /**
     * Возвращает индекст строки в таблице
     * @param mrow строка
     * @return индекс или -1
     */
    public int indexOf( final DataRow mrow ){
        return (int)(Integer)readLock( () -> {
            indexOf_lastIsInDeleted = false;
            if( mrow==null )return -1;

            Integer cachedRI = rowIndexCache.get(mrow);
            if( cachedRI!=null ){
                boolean cacheMiss = false;

                int wrSize = getWorkedRows().size();
                if( cachedRI<0 ){
                    logWarning("cached rowIndex({0}) < 0", cachedRI);
                    rowIndexCache.remove(mrow);
                    cacheMiss = true;
                }

                if( !cacheMiss && cachedRI>=wrSize ){
                    logFiner("cached rowIndex({0}) >= workedRows.size({1})", cachedRI, wrSize);
                    rowIndexCache.remove(mrow);
                    cacheMiss = true;
                }

                if( !cacheMiss ){
                    Object oRow = getWorkedRows().get(cachedRI);
                    if( !Objects.equals(oRow, mrow) ){
                        logFiner("cached row({0}) miss", cachedRI);
                        cacheMiss = true;
                    }
                }

                if( !cacheMiss ){
                    logFinest("return cached index={0} for {1}", cachedRI, mrow);
                    return cachedRI;
                }
            }

            if( getDeletedRows().contains(mrow) ){
                logFinest("return index={0} from deletedRows", -1);
                indexOf_lastIsInDeleted = true;
                return -2;
            }

            int idx = getWorkedRows().indexOf(mrow);
            if( idx>=0 ){
                rowIndexCache.put(mrow, idx);
                logFiner("cache rowIndex = {0}",idx );
                return idx;
            }

            return -1;
        });
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="workedRows : EventList<DataRow>">
    private EventList<DataRow> workedRows;
    
    /**
     * Рабочий набор строк, содержит:
     * <ul>
     * <li>не изменные строки (fixed)
     * <li>изменные строки (updated)
     * <li>добавленные строки (inserted)
     * </ul>
     * @return Рабочий набор строк
     */
    private EventList<DataRow> getWorkedRows(){
        Object sync = this;
        synchronized(sync){
            if( workedRows!=null )return workedRows;
            workedRows = new StdEventList<DataRow>( new ArrayList<DataRow>(), this.sync );
            listenForDisableNullRows( null, workedRows );
            return workedRows;
        }
    }
    
    /**
     * Итератор по рабочему набору строк
     * @return итератор
     */
    public Iterator<DataRow> getRowsIterator(){
        return (Iterator<DataRow>)readLock(() -> getWorkedRows().iterator());
    }
    
    /**
     * Итератор по рабочему набору строк
     * @return итератор
     */
    public Eterable<DataRow> getRowsIterable(){
        return getWorkedRows();
    }
    
    /**
     * Итератор по строкам
     * @param states Указывает по строкам с каким состоянием производить поиск
     * @return Итератор
     */
    public Eterable<DataRow> getRowsIterable(final DataRowState... states){
        return readLock( () -> {
            Predicate<DataRow> filter = dr -> {
                DataRowState st = dr.getState();
                for( DataRowState fst : states ){
                    if( Objects.equals(st, fst) )return true;
                }
                return false;
            };
            return DataTable.this.getRowsIterableAll().filter(filter);
        });
    }
    
    /**
     * Возвращает строки ввиде списка
     * @param states строки с указаным состоянием будут возвращены
     * @return состояние строк
     */
    public List<DataRow> rowsList(final DataRowState... states){
        return (List<DataRow>)readLock( () -> {
            Predicate<DataRow> filter = dr -> {
                DataRowState st = dr.getState();
                for( DataRowState fst : states ){
                    if( Objects.equals(st, fst) )return true;
                }
                return false;
            };

            ArrayList<DataRow> list = new ArrayList<>();
            for( DataRow dr : DataTable.this.getRowsIterableAll().filter(filter) ){
                list.add( dr );
            }
            return list;
        });
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="allRows : Iterable<DataRow>">
    private transient Eterable<DataRow> allRows;
    
    /**
     * Возвращает все строки включая удаленные
     * @return строки
     */
    public Eterable<DataRow> getRowsIterableAll(){
        synchronized(this){
            if( allRows!=null )return allRows;
            //allRows = Iterators.sequence(getWorkedRows(), getDeletedRows());
            allRows = Eterable.of(getWorkedRows()).union(getDeletedRows());
            return allRows;
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="listenForDisableNullRows()">
    /**
     * Запрещает вставлять null ссылки в качестве строк
     */
    public class NullRowsDisabler implements TripleConsumer<Integer, DataRow, DataRow> {
        @Override
        public void accept(Integer idx, DataRow old, DataRow cur) {
            if( cur==null )throw new IllegalArgumentException("can't insert null row");
        }
    }
    /**
     * Запрет вставки null ссылок в список строк
     * @param cset Отписка (может быть null)
     * @param elist Список строк
     */
    private void listenForDisableNullRows( Closeables cset, EventList<DataRow> elist ){
        NullRowsDisabler nldis = new NullRowsDisabler();

        if( elist instanceof PreEventList ){
            AutoCloseable cl = ((PreEventList<DataRow>) elist).onInserting(nldis);
            if( cset != null ) cset.add(cl);

            cl = ((PreEventList<DataRow>) elist).onUpdating(nldis);
            if( cset!=null )cset.add(cl);
        }else{
            AutoCloseable cl = elist.onInserted(nldis);
            if( cset != null ) cset.add(cl);

            cl = elist.onUpdated(nldis);
            if( cset!=null )cset.add(cl);

            System.err.println("workedRows must implement PreEventList");
        }
    }
    //</editor-fold>

    private static class SkipableListeners<ListenerType,EventType> extends ListenersHelper<ListenerType,EventType> {
        public SkipableListeners( BiConsumer<ListenerType,EventType> callListFunc ){
            super(callListFunc);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="deletedRows : EventSet<DataRow>">
    private EventSet<DataRow> deletedRows;
    
    /**
     * Набор удаленных строк
     * @return набор удаленных строк
     */
    private EventSet<DataRow> getDeletedRows(){
        synchronized(this){
            if( deletedRows!=null )return deletedRows;

            SkipableListeners<CollectionListener<EventSet<DataRow>, DataRow>, CollectionEvent<EventSet<DataRow>, DataRow>>
                sl = new SkipableListeners<>((ls,ev)->{
                    if( ls!=null ){
                        if( dropCallLevel.get()>0 )return;
                        ls.collectionEvent(ev);
                    }
                }
            ){
                @Override
                public void fireEvent( CollectionEvent<EventSet<DataRow>, DataRow> event ){
                    if( dropCallLevel.get()>0 )return;
                    super.fireEvent(event);
                }

                @Override
                public void addEvent( CollectionEvent<EventSet<DataRow>, DataRow> ev ){
                    if( dropCallLevel.get()>0 )return;
                    super.addEvent(ev);
                }

                @Override
                public void runEventQueue(){
                    if( dropCallLevel.get()>0 )return;
                    super.runEventQueue();
                }
            };

            deletedRows = new BasicEventSet<DataRow>(new LinkedHashSet<DataRow>(), this.getReadWriteLock()){
                @Override
                public ListenersHelper<CollectionListener<EventSet<DataRow>, DataRow>, CollectionEvent<EventSet<DataRow>, DataRow>> listenerHelper(){
                    return sl;
                }
            };
            return deletedRows;
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="insertedRows : EventSet<DataRow>">
    private EventSet<DataRow> insertedRows;
    
    /**
     * Набор добавленных строк
     * @return добавоенные строки
     */
    private EventSet<DataRow> getInsertedRows(){
        synchronized(this){
            if( insertedRows!=null )return insertedRows;
            insertedRows = new BasicEventSet<DataRow>(new LinkedHashSet<DataRow>(), this.getReadWriteLock());
            return insertedRows;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="fixed()">
    /**
     * Зафиксировать все строки. <br>
     * Строки отмесенные как удаленные, удаляются из этой таблицы.
     * Строки отмеченные как добавленные, переводятся в статус обычных.
     * Строки отмеченные как измененные - переводятся в статус обычных с текущим состоянием данных.
     */
    public void fixed(){
        writeLock( ()->{
            rowTrackingListeners.close();
            
            ArrayList<DataRow> allRowsList = new ArrayList<>();
            for( DataRow dr : getRowsIterableAll() ){
                if( dr==null )throw new Error("Ошибка реализации");
                allRowsList.add(dr);
            }
            
            for( DataRow dr : allRowsList ){
                fixed(dr,true);
            }
            
            initRowChangeTracking();
        });
        
        fireEventQueue();
    }
    
    /**
     * Фиксация изменений
     * @param row Строка которую требуется зафиксировать
     */
    public void fixed( DataRow row ){
        if( row==null )throw new IllegalArgumentException("row == null");
        fixed(row, true);
        fireEventQueue();
    }
    
    /**
     * Фиксация изменений
     * @param row Строка которую требуется зафиксировать
     * @param addEvents true - добавить события в очередь
     */
    public void fixed( DataRow row,boolean addEvents ){
        if( row==null )throw new IllegalArgumentException("row == null");
        
        //synchronized(this){
        writeLock(()->{
            int ri = indexOf(row);

            DataRowState state0 = null;
            DataRowState state1 = null;

            state0 = stateOf(row);
            row.fixChanges(addEvents);
            
            boolean rowErased = false;
            boolean rowFixed = false;
            
            if( getInsertedRows().remove(row) ){
                if( getWorkedRows().contains(row) ){
                    rowFixed = true;
                }else{
                    rowErased = true;
                }
            }else if( getDeletedRows().remove(row) ){
                rowErased = true;
            }else if( !getWorkedRows().contains(row) ){
                if( getInsertedRows().contains(row) ){
                    getInsertedRows().remove(row);
                    rowErased = true;
                }else if( getDeletedRows().contains(row) ){
                    getDeletedRows().remove(row);
                    rowErased = true;
                }
            }
            
            if( rowErased && rowFixed ){
                throw new Error( "Ошибка реализации" );
            }
            
            if( rowErased ){
                if( addEvents ){
                    DataRowErased e = new DataRowErased(this, row);
                    e.setRowIndex(ri);
                    addDataEvent(e);
                }

                try {
                    row.close();
                } catch (IOException ex) {
                    Logger.getLogger(DataTable.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if( addEvents ){
                    DataRowClosed e = new DataRowClosed(this, row);
                    addDataEvent(e);
                }
            }
            
            state1 = stateOf(row);
            
            if( !Objects.equals(state0, state1) && addEvents ){
                addDataEvent(new DataRowStateChanged(this, row, state0, state1));
            }
        });
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="rollback()">
    /**
     * Откат изменений таблицы
     */
    public void rollback(){
        rollback(true);
        fireEventQueue();
    }
    
    /**
     * Откат изменений таблицы
     * @param addEvents Добавлять события в очередь
     */
    public void rollback(boolean addEvents){
        //synchronized(this){
        writeLock(()->{
            rowTrackingListeners.close();
            
            ArrayList<DataRow> allRowsList = new ArrayList<>();
            for( DataRow dr : getRowsIterableAll() ){
                if( dr==null )throw new Error("Ошибка реализации");
                allRowsList.add(dr);
            }
            
            for( DataRow dr : allRowsList ){
                rollback(dr,addEvents);
            }
            
            initRowChangeTracking();
        });
    }
    
    /**
     * Откат изменений строки
     * @param row Строка
     */
    public void rollback(DataRow row){
        if( row==null )throw new IllegalArgumentException("row == null");
        rollback(row, true);
        fireEventQueue();
    }
    
    /**
     * Откат изменений строки
     * @param row Строка
     * @param addEvents Добавлять события в очередь
     */
    public void rollback(DataRow row, boolean addEvents){
        if( row==null )throw new IllegalArgumentException("row == null");
        writeLock(()->{
        //synchronized(this){
            DataRowState s0 = null;
            DataRowState s1 = null;
            
            s0 = row.getState();
            
            switch( stateOf(row) ){
                case Detached:
                    row.cancelChanges(addEvents);
                    break;
                case Inserted:
                {
                    rowTrackingListeners.close();
                    
                    int ri = indexOf(row);
                    row.cancelChanges(addEvents);
                    
                    boolean insRemoved = getInsertedRows().remove(row);
                    boolean wsRemoved = getWorkedRows().remove(row);
                    boolean delRemoved = getDeletedRows().remove(row);
                    
                    if( addEvents ){
                        DataRowErased e = new DataRowErased(this, row);
                        e.setRowIndex(ri);
                        addDataEvent(e);
                    }
                    
                    try{
                        row.close();
                        // TODO generate events
                        if( addEvents ){
                            DataRowClosed e = new DataRowClosed(this, row);
                            addDataEvent(e);
                        }
                    }catch(IOException e){
                        logException(e);
                    }
                    
                    logFiner(
                        "row {0} rollbacked from inserted to null, ins={1} ws={2} del={3}",
                        row, insRemoved, wsRemoved, delRemoved);
                    
                    initRowChangeTracking();
                } break;
                case Deleted:
                {
                    rowTrackingListeners.close();
                    
                    boolean insRemoved = getInsertedRows().remove(row);
                    boolean wsRemoved = getDeletedRows().remove(row);
                    
                    int eri = getWorkedRows().indexOf(row);
                    boolean delRemoved = getWorkedRows().add(row);
                    
                    row.cancelChanges(addEvents);
                    
                    int ri = -1;
                    if( eri<0 ){
                        ri = getWorkedRows().indexOf(row);
                    }else{
                        logWarning("deleted row exists({0}) in workedset", eri);
                    }
                    
                    // TODO generate events
                    if( addEvents ){
                        DataRowUndeleted ev = new DataRowUndeleted(this, row, ri);
                        addDataEvent(ev);
                    }
                    
                    logFiner(
                        "row {0} rollbacked from deleted to fixed, ins={1} ws={2} del={3}",
                        row, insRemoved, wsRemoved, delRemoved);
                    
                    initRowChangeTracking();
                } break;
                case Fixed:
                    break;
                case Updated:
                    row.cancelChanges(addEvents);
                    break;
            }
            
            s1 = row.getState();
            if( !Objects.equals(s0, s1) && addEvents ){
                addDataEvent(new DataRowStateChanged(this, row, s0, s1));
            }
        });
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="drop()">
    protected transient final AtomicInteger dropCallLevel = new AtomicInteger(0);
    
    /**
     * Удаление всех данны, включая изменения и удаление структуры
     */
    public void drop(){
        //synchronized(this){
        writeLock(()->{
            dropCallLevel.incrementAndGet();
            rowTrackingListeners.close();
            try{            
                for( DataRow mrow : getWorkedRows() ){
                    try {
                        mrow.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                for( DataRow mrow : getInsertedRows() ){
                    try {
                        mrow.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                for( DataRow mrow : getDeletedRows()){
                    try {
                        mrow.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DataTable.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                getColumnsEventList().clear();
            } finally {
                dropCallLevel.decrementAndGet();
                initRowChangeTracking();
            }
        });
        
        fireEvent(new DataTableDropped(this));
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="isDeleted(row):boolean">
    /**
     * Возвращает true если строка отмечена как удаленная
     * @param row строка
     * @return true - отмечена под удаление
     */
    public boolean isDeleted( DataRow row ){
        if( row==null )throw new IllegalArgumentException("mrow == null");
        return readLock(()-> getDeletedRows().contains(row));
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="isInserted(row):boolean">
    /**
     * Возвращает true если строка отмечена как новая (inserted)
     * @param row стока
     * @return true - строка добавлена но не фиксирована
     */
    public boolean isInserted( DataRow row ){
        if( row==null )throw new IllegalArgumentException("mrow == null");
        return readLock(()->getInsertedRows().contains(row));
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="isUpdated(row):boolean">
    /**
     * Возвращает true если строка быда обновлена, но не фиксированна
     * @param row строка 
     * @return true - строка была обновлена, но не фиксированна
     */
    public boolean isUpdated( DataRow row ){
        if( row==null )throw new IllegalArgumentException("row == null");
        return readLock( ()->row.isChanged() && !isDeleted(row) && !isInserted(row) );
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="stateOf(row)">
    /**
     * Возвращает состояние строки
     * @param row строка
     * @return состояние строки
     */
    public DataRowState stateOf( DataRow row ){
        if( row==null )throw new IllegalArgumentException("row == null");
        return readLock( ()->{
            int ri = indexOf(row);
            
            if( indexOf_lastIsInDeleted ){
                return DataRowState.Deleted;
            }
            
            if( ri<0 )return DataRowState.Detached;
            
            boolean ins = getInsertedRows().contains(row);
            if( ins )return DataRowState.Inserted;
            
            if( row.isChanged() ){
                return DataRowState.Updated;
            }
            
            return DataRowState.Fixed;
        });
    }
    //</editor-fold>
    
    /**
     * Производит вставку строки
     * @param values значния
     * @return интерфейс вставки
     */
    public DataTableInserting insert( Object ... values ){
        if( values==null || values.length==0 ){
            return new DataTableInserting(this);
        }
        return new DataTableInserting(this, values);
    }
    
    /**
     * Производит вставку строки
     * @param row строка
     */
    public void insert( final DataRow row ){
        if( row==null )throw new IllegalArgumentException("row == null");
        writeLock(() -> {
            nextScn();
            getWorkedRows().add(row);
        });
    }
    
    /**
     * Удаляет строку
     * @param row строка
     */
    public void delete( final DataRow row ){
        if( row==null )throw new IllegalArgumentException("row == null");
        writeLock(() -> {
            nextScn();
            getWorkedRows().remove(row);
        });
    }
}
