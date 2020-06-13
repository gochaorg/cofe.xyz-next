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

import xyz.cofe.fn.Fn0;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Строка данных таблицы, с поддержкой отката изменений. <br>
 * <ul>
 * <li>
 * Строка может пренадлежать таблице или нет (свойство table).
 * <li>
 * Строка хранит счетчик изменений <b>changeCount</b>
 * <li>
 * Строка хранит в себе два набора:      <br>
 * <b>origin</b> - оригинальные данные   <br>
 * <b>data</b> - текущие данные   
 * <li>
 * Оба набора данных задаются в конструкторе, счетчик измений устанавливается в 0.
 * <li> Изменения вносятся в текущий набор, 
 * при каждом изменении увеличивается счетчик <i>changeCount</i>.
 * <li> Отличия текущих данных от оргинальных, можно получить методом: getChangedValues()
 * <li> метод fixChanges() - сохраняет внесенные изменения в оригинальные данные, счетчик сбрасывает в 0
 * <li> метод cancelChanges() - откатывает измененияЮ счетчик сбрасывает в 0
 * </ul>
 * 
 * В зависимости от состояния таблицы, строка может находится в следующих состояниях:
 * <ul>
 * <li> Detached - Строка не присоединена к таблице
 * <li> Fixed - Строка присоединена к таблице и счетчик изменений 0
 * <li> Updated - Строка присоединена к таблице и счетчик изменений более 0 (содержит измененные данные)
 * <li> Inserted - Строка присоединена к таблице, и помечена для добавления
 * <li> Deleted - Строка присоединена к таблице, и помечена для удаления
 * </ul>
 * @author Kamnev Georgiy
 */
public class DataRow implements Closeable, Serializable
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private transient static final Logger logger = Logger.getLogger(DataRow.class.getName());

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
        logger.entering(DataRow.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(DataRow.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(DataRow.class.getName(), method, result);
    }
    //</editor-fold>
    
    //private static final transient AtomicLong 
    
    /**
     * Счетчик изменений
     */
    protected int changes = 0;
    
    /**
     * Номера (от 0) измененных колонок
     */
    protected final Set<Integer> changed = new LinkedHashSet<>();
    
    //<editor-fold defaultstate="collapsed" desc="Конструкторы">
    //<editor-fold defaultstate="collapsed" desc="DataRow(tbl,... data)">
    /**
     * Создает строку таблицы
     * @param table таблица
     * @param data данные строки
     */
    public DataRow( DataTable table, Object ... data ){
        if( table==null )throw new IllegalArgumentException("table == null");
        if( data==null )throw new IllegalArgumentException("data == null");
        
        this.table = table;
        
        boolean earlyConstraint = false;
        
        if( earlyConstraint ){
            this.data = new Object[0];        
            int ccnt = table.getColumnsCount();
            for( int i=0; i<ccnt; i++ ){
                set(i, data[i], false);
            }
        }else{        
            int ccnt = table.getColumnsCount();
            this.data = Arrays.copyOf(data, ccnt);
        }
        
        fixChanges(false);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="deserialize DataRow()">
    // TODO disable direct access
    /**
     * Конструктор десиариализации
     * @param dataTable Таблица к которой относится строка
     * @param data Текущие данные
     * @param origin Оригинальные данные
     * @param changes Кол-во изменений
     */
    public DataRow( DataTable dataTable, Object[] data, Object[] origin, int changes ){
        if( dataTable==null )throw new IllegalArgumentException("dataTable == null");
        if( data==null )throw new IllegalArgumentException("data == null");
        if( origin==null )throw new IllegalArgumentException("origin == null");
        if( changes<0 )throw new IllegalArgumentException("changes<0");
        
        this.table = dataTable;
        this.data = data;
        this.origin = origin;
        this.changes = changes;
        
        int cmax = Math.max(origin.length, data.length);
        for( int ci=0; ci<cmax; ci++ ){
            Object v1 = ci < data.length ? data[ci] : null;
            Object v2 = ci < origin.length ? origin[ci] : null;
            if( !Objects.equals(v1, v2) ){
                changed.add(ci);
            }
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="copy DataRow()">
    /**
     * Конструктор копирования
     * @param sample Образец для копирования
     */
    public DataRow( DataRow sample ){
        if( sample==null )throw new IllegalArgumentException("sample == null");
        
        synchronized(sample){
            this.table = sample.table;
            this.data = sample.data!=null ? Arrays.copyOf(sample.data, sample.data.length) : null;
            this.origin = sample.origin!=null ? Arrays.copyOf(sample.origin, sample.origin.length) : null;
            this.changes = sample.changes;
            
            changed.clear();
            changed.addAll(sample.changed);
        }
    }
    //</editor-fold>
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="data : Object[]">
    protected Object[] data;
    
    /**
     * Возвращает рабочие данные строки
     * @return данные
     */
    public Object[] getData(){ 
        synchronized(this){
            if( table!=null ){
                synchronized(table){
                    //List<DataColumn> dcList = table.getColumns();
                    //Object[] res =
                    //    data==null
                    //    ? new Object[dcList.size()]
                    //    : Arrays.copyOf(data, dcList.size());
                    //return res;
                    int ccnt = table.getColumnsCount();
                    Object[] res =
                        data==null
                        ? new Object[ccnt]
                        : Arrays.copyOf(data, ccnt);
                    return res;
                }
            }
            return data!=null ? Arrays.copyOf(data, data.length) : null;
        } 
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="origin : Object[]">
    protected Object[] origin;
    
    /**
     * Возвращает зафиксированные данные
     * @return зафиксированные данные
     */
    public Object[] getOrigin(){ 
        synchronized(this){
            if( table!=null ){
                synchronized(table){
                    //List<DataColumn> dcList = table.getColumns();
                    //Object[] res = origin==null 
                    //    ? new Object[dcList.size()] 
                    //    : Arrays.copyOf(origin, dcList.size());
                    //return res;
                    int ccnt = table.getColumnsCount();
                    Object[] res = origin==null 
                        ? new Object[ccnt] 
                        : Arrays.copyOf(origin, ccnt);
                    return res;
                }
            }
            return origin!=null ? Arrays.copyOf(origin, origin.length) : null;
        } 
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="changedColumn : Integer[]">
    /**
     * Возвращает индексы измененных колонок
     * @return индексы
     */
    public Integer[] getChangedColumn(){
        synchronized(this){
            return changed.toArray(new Integer[]{});
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="changed : boolean">
    /**
     * Возвращает факт наличия не зафиксированных данных
     * @return true - если незафиксированные данные
     */
    public boolean isChanged(){ synchronized(this){ return changes>0; } }
    //</editor-fold>    
    //<editor-fold defaultstate="collapsed" desc="changeCount : int">
    /**
     * Возвращает кол-во изменений с последней фиксации
     * @return кол-во изменений
     */
    public int getChangeCount(){ synchronized(this){ return changes; } }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="class ChangedValue">
    /**
     * Описывает изенеия строки
     */
    public static class ChangedValue {
        private int column;
        
        /**
         * Возвращает индекс колонки
         * @return колонка
         */
        public int getColumn(){ return column; }
        
        private Object from;
        
        /**
         * Возвращает оригинальные данные (на момент фиксации)
         * @return оригинальные данные
         */
        public Object getFrom(){ return from; }
        
        private Object to;
        
        /**
         * Возвращает измененное значение
         * @return иземенное значение
         */
        public Object getTo(){ return to; }
        
        /**
         * Конструктор
         * @param column колонка
         * @param from зафиксированное значение
         * @param to измененное значение
         */
        public ChangedValue( int column, Object from, Object to ){
            this.column = column;
            this.from = from;
            this.to = to;
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="changedValues : List<ChangedValue>">
    /**
     * Возвращает список изменений
     * @return список изменений
     */
    public List<ChangedValue> getChangedValues(){
        synchronized(this){
            ArrayList<ChangedValue> res = new ArrayList<>(10);
            for( Integer col : changed ){
                if( col==null )throw new IllegalStateException("changed column is null");
                Object from =
                    (origin==null || col<0 || col>=origin.length) ? null : origin[col];
                Object to =
                    (data==null || col<0 || col>=data.length) ? null : data[col];
                res.add(new ChangedValue(col, from, to));
            }
            return res;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="cancelChanges()">
    /**
     * Откатывает изменения
     */
    public void cancelChanges(){
        DataTable mtbl = null;
        synchronized(this){
            if( origin==null )throw new IllegalStateException("origin == null");
            cancelChanges(true);
        }
        if( mtbl!=null ){
            mtbl.fireEventQueue();
        }
    }
    /**
     * Откатывает изменения и добавляет событие в очередь
     * @param addEvents добавить событие в очередь таблицы
     */
    public void cancelChanges(boolean addEvents){
        DataTable tbl = null;
        synchronized(this){
            if( origin==null )throw new IllegalStateException("origin == null");
            data = Arrays.copyOf(origin, origin.length);
            changed.clear();
            changes = 0;
            tbl = table;
        }
        if( tbl!=null && addEvents ){
            tbl.addDataEvent(new DataRowChangesCanceled(tbl, this));
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="fixChanges()">
    /**
     * Фиксировать изменения
     */
    public void fixChanges(){
        DataTable tbl = null;
        synchronized(this){
            tbl = table;
            fixChanges(true);
        }
        if( tbl!=null ){
            tbl.fireEventQueue();
        }
    }
    /**
     * Фиксировать изменения
     * @param addEvents добавить событие в очередь таблицы
     */
    public void fixChanges(boolean addEvents){
        DataTable tbl = null;
        synchronized(this){
            origin = data!=null ? Arrays.copyOf(data, data.length) : null;
            changed.clear();
            changes = 0;
            tbl = table;
        }
        if( tbl!=null && addEvents ){
            tbl.addDataEvent(new DataRowChangesFixed(tbl, this));
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="close()">
    /**
     * Закрывает строку и освобождает ссылки
     * @throws IOException Ошибка клонирования
     */
    @Override
    public void close() throws IOException {
        synchronized(this){
            this.table = null;
            this.data = null;
            this.origin = null;
            changed.clear();
            changes = 0;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="get(cidx):Object">
    /**
     * Возвращает данные из указанной колонки
     * @param column колонка
     * @return данные
     */
    public Object get(int column) {
        if( column<0 )throw new IllegalArgumentException("column("+column+") < 0");
        synchronized(this){
            if( table==null )throw new IllegalStateException("row detached from table");
            if( data==null )return null;
            if( column>=data.length )return null;
            return data[column];
        }
    }
    //</editor-fold>    
    //<editor-fold defaultstate="collapsed" desc="set(cidx,value)">
    /**
     * Указывает значение колонки
     * @param column колонка
     * @param value значение
     */
    public void set(int column, Object value){
        DataTable dt = null;
        
        synchronized(this){
            dt = table;
            set(column,value,true);
        }
        
        if( dt != null ){
            dt.fireEventQueue();
        }
    }
    /**
     * Указывает значение колонки
     * @param column колонка
     * @param value значение
     * @param addEvents true - добавляет событие в очередь таблицы
     */
    public void set(int column, Object value, boolean addEvents){
        if( column<0 )throw new IllegalArgumentException("column("+column+") < 0");
        
        DataTable mtable;
        List<DataEvent> evs = new ArrayList<>();
        DataRowState s0 = null;
        DataRowState s1 = null;
        
        synchronized(this){
            if( table==null )throw new IllegalStateException("row detached from table");
            synchronized( table ){
                mtable = table;
                s0 = getState();
                
                /*List<DataColumn> dcList = table.getColumns();
                if( column>=dcList.size() ){
                    throw new IllegalStateException(
                        "column("+column+") >= columns.size("+
                            dcList.size()+
                            ") in table");
                }
                */
                
                DataColumn dcol = table.getColumn(column);
                if( dcol==null )throw new IllegalStateException("column("+column+") is null");
                
                boolean allowNull = true;
                boolean allowSubType = true;
                Fn0 fgen = null;
                Class dtype = dcol.getDataType();
                
                if( dcol instanceof DataColumn ){
                    DataColumn mc = (DataColumn)dcol;
                    
                    allowNull = mc.isAllowNull();
                    allowSubType = mc.isAllowSubTypes();
                    fgen = mc.getGenerator();
                }
                
                if( !allowNull && value==null ){
                    boolean except = false;
                    if( fgen!=null ){
                        Object nval = fgen.apply();
                        if( nval==null ){
                            except = true;
                        }else{
                            value = nval;
                        }
                    }else{
                        except = true;
                    }
                    if( except ){
                        throw new IllegalArgumentException("null value not allowed");
                    }
                }
                
                if( value!=null && dtype!=null ){
                    if( allowSubType ){
                        boolean assignable = dtype.isAssignableFrom(value.getClass());
                        if( !assignable ){
                            throw new ClassCastException("value("+value+") not assignable from "+dtype);
                        }
                    }else{
                        boolean assignable = dtype.equals(value.getClass());
                        if( !assignable ){
                            throw new ClassCastException("value type("+value.getClass()+") not equals to "+dtype);
                        }
                    }
                }
                
                if( changes==0 ){
                    origin = Arrays.copyOf(data, data.length);
                }
                
                if( column>=data.length ){
                    data = Arrays.copyOf(data, column+1);
                }
                
                Object oldVal = data[column];
                Object orgVal = origin!=null && column>=0 && column<origin.length ? origin[column] : null;
                
                data[column] = value;
                changes++;
                changed.add(column);
                
                if( addEvents ){
                    DataCellUpdated dcUpdEv = new DataCellUpdated();
                    dcUpdEv.setTable(table);
                    dcUpdEv.setRow(this);
                    dcUpdEv.setColumn(column);
                    dcUpdEv.setOldValue(oldVal);
                    dcUpdEv.setCurrentValue(value);
                    dcUpdEv.setOriginValue(orgVal);
                    evs.add(dcUpdEv);
                }
            }
            
            s1 = getState();
            
            if( !Objects.equals(s0, s1) ){
                evs.add(new DataRowStateChanged(mtable, this, s0, s1));
            }

            if( mtable!=null && !evs.isEmpty() && addEvents ){
                for( DataEvent e : evs ){
                    if( e!=null )mtable.addDataEvent(e);
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="table : DataTable">
    protected volatile transient DataTable table;
    
    /**
     * Указывает таблицу строки
     * @return таблица
     */
    public DataTable getTable() {
        synchronized(this){
            return table;
        }
    }
    
    /**
     * Указывает таблицу сторки
     * @param table таблица
     */
    public void setTable( DataTable table ){
        synchronized(this){
            this.table = table;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="detached : boolean">
    /**
     * Указывает присоединена ли строка к таблице
     * @return true - есть ссылка на таблицу
     */
    public boolean isDetached(){
        synchronized(this){
            return table==null;
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="deleted : boolean">
    /**
     * Возвращает true если строка отмечена как удаленная
     * @return true - отмечена под удаление
     */
    public boolean isDeleted(){
        synchronized(this){
            if( table==null )return false;
            return table.isDeleted(this);
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="inserted : boolean">
    /**
     * Возвращает true если строка отмечена как новая (inserted)
     * @return true - строка добавлена но не фиксирована
     */
    public boolean isInserted(){
        synchronized(this){
            if( table==null )return false;
            return table.isInserted(this);
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="updated : boolean">
    /**
     * Возвращает true если строка быда обновлена, но не фиксированна
     * @return true - строка была обновлена, но не фиксированна
     */
    public boolean isUpdated(){
        synchronized(this){
            if( table==null )return false;
            return table.isUpdated(this);
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="state : DataRowState">
    /**
     * Возвращает состояние строки
     * @return состояние строки
     */
    public DataRowState getState(){
        synchronized(this){
            if( table==null )return DataRowState.Detached;
            return table.stateOf(this);
        }
    }
    //</editor-fold>
}
