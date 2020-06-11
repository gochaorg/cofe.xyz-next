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

import xyz.cofe.collection.Func1;
import xyz.cofe.common.CloseableSet;
import xyz.cofe.sql.JdbcColumn;

import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Вставка табличных данных
 * @author Kamnev Georgiy
 */
public class DataTableInserting {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(DataTableInserting.class.getName());

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
        logger.entering(DataTableInserting.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(DataTableInserting.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(DataTableInserting.class.getName(), method, result);
    }
    //</editor-fold>
        
    public DataTableInserting(){
    }
    
    public DataTableInserting(DataTable dt){
        this.table = dt;
        if( dt!=null ){
            //this.values = new Object[dt.getColumns().size()];
            this.values = new Object[dt.getColumnsCount()];
        }
    }
    
    public DataTableInserting(DataTable dt, Object[] values){
        this.table = dt;
        this.values = values;
    }
    
    protected final WeakHashMap<String,Integer> firstIndexCache = new WeakHashMap<>();
    
    //<editor-fold defaultstate="collapsed" desc="firstIndex(columnName)">
    /**
     * Поиск индекса колонки по названию. <p>
     * Поиск производится с уччетом настроек регистра (JdbcColumn/DataColumn)
     * @param columnName Имя колонки
     * @return индекс колонки или -1
     */
    public int firstIndex( String columnName ){
        if( columnName==null )throw new IllegalArgumentException("columnName == null");
        
        DataTable dt = table;
        if( dt==null )return -1;
        
        synchronized(firstIndexCache){
            Integer cachedRes = firstIndexCache.get(columnName);
            if( cachedRes!=null ){
                return cachedRes;
            }
            
            Integer res = null;
            try{
                //List<DataColumn> columns = dt.getColumns();
                //if( columns==null || columns.isEmpty()){
                //    res = -1;
                //    return res;
                //}
                int ccnt = dt.getColumnsCount();
                if( ccnt<1 ){
                    res = -1;
                    return res;
                }
                
                for( int ci=0; ci<ccnt; ci++ ){
                    DataColumn dc = dt.getColumn(ci);
                    if( dc==null )continue;
                    
                    if( dc instanceof JdbcColumn ){
                        JdbcColumn jc = (JdbcColumn)dc;
                        if( !jc.isCaseSensitive() ){
                            String colLabel = jc.getColumnLabel();
                            if( colLabel.equalsIgnoreCase(columnName) ){
                                res = ci;
                                return res;
                            }
                            
                            String name = jc.getName();
                            if( name.equalsIgnoreCase(columnName) ){
                                res = ci;
                                return ci;
                            }
                        }else{
                            String colLabel = jc.getColumnLabel();
                            if( colLabel.equals(columnName) ){
                                res = ci;
                                return ci;
                            }
                            
                            String name = jc.getName();
                            if( name.equals(columnName) ){
                                res = ci;
                                return ci;
                            }
                        }
                    }else{
                        String name = dc.getName();
                        if( name.equals(columnName) ){
                            res = ci;
                            return ci;
                        }
                    }
                }
                
                return -1;
            }
            finally {
                if( res!=null && res>=0 ){
                    firstIndexCache.put(columnName, res);
                }
            }
        }
    }
    //</editor-fold>
    
    private void initEmptyValues(){
        synchronized(this){
            if( this.values!=null )return;
            if( this.table==null )throw new IllegalStateException("table not set");
            this.values = new Object[this.table.getColumnsCount()];
        }
    }
    
    public DataTableInserting set( int columnIndex, Object value ){
        synchronized(this){
            initEmptyValues();
            if( columnIndex<0 ){
                throw new IllegalArgumentException("columnIndex("+columnIndex+") < 0");
            }
            if( columnIndex>=values.length ){
                throw new IllegalArgumentException("columnIndex("+columnIndex+") > columnCount("+values.length+")");
            }
            values[columnIndex] = value;
        }
        return this;
    }
    
    public DataTableInserting set( String columnName, Object value ){
        if( columnName==null )throw new IllegalArgumentException("columnName == null");
        synchronized(this){
            initEmptyValues();
            int columnIndex = firstIndex(columnName);
            if( columnIndex<0 )throw new IllegalArgumentException("columnName("+columnName+") not found");
            values[columnIndex] = value;
        }
        return this;
    }
    
    //<editor-fold defaultstate="collapsed" desc="table">
    private DataTable table;
    
    /**
     * Указывает таблицу в которую производится вставка
     * @return таблица
     */
    public DataTable getTable() {
        synchronized(this){
            return table;
        }
    }
    
    protected final CloseableSet tableListeners = new CloseableSet();
    
    /**
     * Указывает таблицу в которую производится вставка
     * @param table таблица
     */
    public void setTable(DataTable table) {
        synchronized(this){
            tableListeners.closeAll();
            this.table = table;
            if( this.table!=null ){
                this.values = new Object[this.table.getColumnsCount()];
            }else{
                this.values = null;
            }
            synchronized(firstIndexCache){
                firstIndexCache.clear();
            }
            if( table!=null ){
                table.addDataEventListener(new DataEventListener() {
                    @Override
                    public void dataEvent(DataEvent ev) {
                        if( ev instanceof DataColumnEvent ){
                            synchronized(firstIndexCache){
                                firstIndexCache.clear();
                            }
                        }
                    }
                }, true);
                //tableListeners.add( 
                //    table.getColumns().onChanged(dropColumnMaps, true)
                //);
            }
        }
    }
    
    /*protected final Func3 dropColumnMaps = new Func3() {
        @Override
        public Object apply(Object arg1, Object arg2, Object arg3) {
            synchronized(firstIndexCache){
                firstIndexCache.clear();
            }
            return null;
        }
    };*/
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="values">
    private Object[] values;
    
    /**
     * Указывает вставляемые данные
     * @return данные строки
     */
    public Object[] getValues() {
        return values;
    }
    
    /**
     * Указывает вставляемые данные
     * @param values данные строки
     */
    public void setValues(Object[] values) {
        this.values = values;
    }
    
    /**
     * Указывает вставляемые данные
     * @param values данные строки
     * @return self ссылка
     */
    public DataTableInserting values( Object ... values ){
        setValues(values);
        return this;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="fixed">
    private boolean fixed = false;
    
    /**
     * Указывает что данная строка будет отмечена как state = fixed
     * @return true - будет зафиксирована
     */
    public boolean isFixed() {
        return fixed;
    }
    
    /**
     * Указывает что данная строка будет зафиксирована -  отмечена как state = fixed
     * @param fixed true - будет зафиксирована
     */
    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
    
    /**
     * Указывает что данная строка будет зафиксирована -  отмечена как state = fixed
     * @param fix true - будет зафиксирована
     * @return self ссылка
     */
    public DataTableInserting fixed( boolean fix ){
        this.fixed = fix;
        return this;
    }
    
    /**
     * Указывает что данная строка будет зафиксирована
     * @return self ссылка
     */
    public DataTableInserting fixed(){
        return fixed(true);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="dataRow">
    private DataRow dataRow;
    
    public DataRow getDataRow() {
        return dataRow;
    }
    
    public void setDataRow(DataRow dataRow) {
        this.dataRow = dataRow;
    }
    //</editor-fold>
    
    /**
     * Производит вставку строки
     * @return Добавленная строка
     */
    public DataRow go(){
        final DataTable dt = table;
        if( dt==null )throw new IllegalStateException("table not set");
        
        final Object[] vals = values;
        if( vals==null )throw new IllegalStateException("values not set");
        
        /*dt.lockRun(new Runnable() {
            @Override
            public void run() {
                DataRow dr = new DataRow(dt, vals);
                DataTableInserting.this.dataRow = dr;

                dt.getWorkedRows().add(dr);

                if( fixed ){
                    dt.fixed(dr);
                }
            }
        });*/
        
        dt.lockRunInternal(new Func1<Object, DataTable.InternalRun>() {
            @Override
            public Object apply(DataTable.InternalRun irun) {
                irun.nextScn();                
                
                DataRow dr = new DataRow(dt, vals);
                DataTableInserting.this.dataRow = dr;

                irun.getWorkedRows().add(dr);

                if( fixed ){
                    dt.fixed(dr);
                }
                
                return null;
            }
        });
        
        return DataTableInserting.this.dataRow;
    }
    
    /**
     * Вставка строки
     * @param vals значения
     * @return self ссылка
     */
    public DataTableInserting insert( Object ... vals ){
        values = vals;
        go();
        return this;
    }
}
