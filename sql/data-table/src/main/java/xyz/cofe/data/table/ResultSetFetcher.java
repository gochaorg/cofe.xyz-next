/*
 * The MIT License
 *
 * Copyright 2017 user.
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

package xyz.cofe.data.table;

import xyz.cofe.data.events.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Выборка данных из Jdbc в DataTable
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class ResultSetFetcher 
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ResultSetFetcher.class.getName());
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

    private static void logFine( String message, Object... args){
        logger.log(Level.FINE, message, args);
    }
    
    private static void logFiner( String message, Object... args){
        logger.log(Level.FINER, message, args);
    }
    
    private static void logFinest( String message, Object... args){
        logger.log(Level.FINEST, message, args);
    }
    
    private static void logInfo( String message, Object... args){
        logger.log(Level.INFO, message, args);
    }

    private static void logWarning( String message, Object... args){
        logger.log(Level.WARNING, message, args);
    }
    
    private static void logSevere( String message, Object... args){
        logger.log(Level.SEVERE, message, args);
    }

    private static void logException( Throwable ex){
        logger.log(Level.SEVERE, null, ex);
    }

    private static void logEntering( String method, Object... params){
        logger.entering(ResultSetFetcher.class.getName(), method, params);
    }
    
    private static void logExiting( String method){
        logger.exiting(ResultSetFetcher.class.getName(), method);
    }
    
    private static void logExiting( String method, Object result){
        logger.exiting(ResultSetFetcher.class.getName(), method, result);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="PropertyChangeSupport">
    protected final PropertyChangeSupport psupport; //= new PropertyChangeSupport
    
    public void addPropertyChangeListener( PropertyChangeListener listener) {
        psupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener( PropertyChangeListener listener) {
        psupport.removePropertyChangeListener(listener);
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return psupport.getPropertyChangeListeners();
    }
    
    public void addPropertyChangeListener( String propertyName, PropertyChangeListener listener) {
        psupport.addPropertyChangeListener(propertyName, listener);
    }
    
    public void removePropertyChangeListener( String propertyName, PropertyChangeListener listener) {
        psupport.removePropertyChangeListener(propertyName, listener);
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners( String propertyName) {
        return psupport.getPropertyChangeListeners(propertyName);
    }
    
    public void firePropertyChange( String propertyName, Object oldValue, Object newValue) {
        psupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    public boolean hasListeners( String propertyName) {
        return psupport.hasListeners(propertyName);
    }
    //</editor-fold>
    
    public ResultSetFetcher(){
        psupport = new PropertyChangeSupport(true);
    }
    
    public ResultSetFetcher( 
        ResultSet rs,
        boolean withClose, 
        DataTable table,
        ClassLoader cl,
        boolean evInAwt
    ){
        psupport = evInAwt 
            ?   new SwingPropertyChangeSupport(this,false)
            :   new PropertyChangeSupport(true);
        
        setClassLoader(cl);
        setResultSet(rs);
        setWithClose(withClose);
        setDataTable(table);
    }
    
    //<editor-fold defaultstate="collapsed" desc="resultSet">
    protected ResultSet resultSet;
    
    public ResultSet getResultSet() {
        synchronized(this){
            return resultSet;
        }
    }
    
    public void setResultSet( ResultSet resultSet) {
        synchronized(this){
            this.resultSet = resultSet;
            this.columns = null;
            if( resultSet!=null ){
                try {
                    if( !resultSet.isClosed() ){
                        getColumns(); // cache columns
                    }
                } catch ( SQLException ex) {
                    Logger.getLogger(ResultSetFetcher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="classLoader">
    private ClassLoader classLoader = null;
    private ClassLoader classLoader(){
        synchronized(this){
            if( classLoader!=null )return classLoader;
            ClassLoader cl = null;
            if( cl==null )cl = Thread.currentThread().getContextClassLoader();
            if( cl==null )cl = ResultSetFetcher.class.getClassLoader();
            classLoader = cl;
            return cl;
        }
    }
    public ClassLoader getClassLoader(){
        synchronized(this) { return classLoader; }
    }
    public void setClassLoader( ClassLoader cl ){
        synchronized(this){
            classLoader = cl;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="columnCount">
    //private Integer columnCount = null;
    public int getColumnCount(){
        synchronized(this){
            return getColumns().size();
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="columns : List<JdbcColumn>">
    private List<JdbcColumn> columns = null;
    
    public List<JdbcColumn> getColumns(){
        synchronized(this){
            if( columns!=null )return columns;
            if( resultSet==null )throw new IllegalStateException("resultSet not set");
            try {
                ResultSetMetaData md = resultSet.getMetaData();
                JdbcColumn[] dcs = new JdbcColumn[md.getColumnCount()];
                for( int ci=0; ci<md.getColumnCount(); ci++ ){
                    int sci = ci+1;
                    dcs[ci] = JdbcColumn.createFrom(md, sci, classLoader());
                }
                columns = Arrays.asList(dcs);
                return columns;
            } catch ( SQLException | ClassNotFoundException ex) {
                Logger.getLogger(ResultSetFetcher.class.getName()).log(Level.SEVERE, null, ex);
                throw new IllegalStateException(ex.getMessage(),ex);
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="hasNext()">
    public boolean hasNext(){
        synchronized(this){
            if( resultSet==null )return false;
            try {
                boolean closed = resultSet.isClosed();
                if( closed ){
                    fetchFinished();
                    return false;
                }
                
                boolean fetchedAll = resultSet.isAfterLast();
                if( fetchedAll ){
                    fetchFinished();
                    return false;
                }
            } catch ( SQLException ex) {
                Logger.getLogger(ResultSetFetcher.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        }
    }
    //</editor-fold>
    
    protected Function<SQLException,Object> sqlExceptionHook = null;

    public synchronized Function<SQLException,Object> getSqlExceptionHook() {
        return sqlExceptionHook;
    }

    public void setSqlExceptionHook(Function<SQLException,Object> sqlExceptionHook) {
        synchronized(this){
        this.sqlExceptionHook = sqlExceptionHook;
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="next()">
    public Object[] next(){
        synchronized(this){
            if( resultSet==null ){
                //throw new IllegalStateException("resultSet not set");
                return null;
            }
            try {
                boolean fetched = resultSet.next();
                if( !fetched ){
                    fetchFinished();
                    return null;
                }
                
                int cc = getColumnCount();
                if( cc<=0 )return new Object[0];
                
                Object[] res = new Object[cc];
                for( int ci=0; ci<cc; ci++ ){
                    Object v = resultSet.getObject(ci+1);
                    res[ci] = v;
                }
                
                return res;
            } catch ( SQLException ex) {
                Logger.getLogger(ResultSetFetcher.class.getName()).log(Level.SEVERE, null, ex);
                if( sqlExceptionHook!=null ){
                    sqlExceptionHook.apply(ex);
                    fetchFinished();
                    return null;
                }else{
                    throw new IllegalStateException(ex.getMessage(),ex);
                }
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="dataTable : DataTable">
    protected DataTable dataTable;
    
    public DataTable getDataTable() {
        synchronized(this){
            if( dataTable!=null )return dataTable; 
            dataTable = new DataTable();
            dataTableRebuilded = false;
            return dataTable; 
        }
    }
    
    public void setDataTable( DataTable dataTable) {
        Object old,cur;
        synchronized(this){
            old = this.dataTable;
            this.dataTable = dataTable;
            dataTableRebuilded = false;
            cur = this.dataTable;
        }
        firePropertyChange("dataTable", old, cur);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rebuildTable()">
    public DataTable rebuildTable(){
        synchronized(this){
            rebuildStart = System.currentTimeMillis();
            
            DataTable dt = getDataTable();
            if( dt==null ){
                throw new IllegalStateException("dataTable not set");
            }
            
            rebuildColumns = 0;
            
            dt.drop();
            for( JdbcColumn col : getColumns() ){
                dt.addColumn(col);
                rebuildColumns++;
            }
            dataTableRebuilded = true;
            
            rebuildFinish = System.currentTimeMillis();
            
            return dt;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dataTableRebuilded : boolean">
    private boolean dataTableRebuilded = false;
    
    public boolean isDataTableRebuilded(){
        synchronized(this){
            return dataTableRebuilded;
        }
    }
    //</editor-fold>
    
    protected long fetchStart = 0;
    protected long fetchLast = 0;
    protected long fetchCount = 0;
    
    protected long sumFetchTimeNS = 0;
    protected long minFetchTimeNS = Long.MAX_VALUE;
    protected long maxFetchTimeNS = Long.MIN_VALUE;

    protected long sumInsertTimeNS = 0;
    protected long minInsertTimeNS = Long.MAX_VALUE;
    protected long maxInsertTimeNS = Long.MIN_VALUE;
    
    protected long rebuildStart = 0;
    protected long rebuildFinish = 0;
    protected long rebuildColumns = 0;
    
    protected Map<String, Number> counters;
    public Map<String, Number> getCounters(){
        synchronized(this){
            if( counters==null )counters = new LinkedHashMap<>();
            long fetchTime = Math.abs(fetchLast - fetchStart);
            //long fetchCount = 
            counters.put("fetchStart", fetchStart);
            counters.put("fetchLast", fetchLast);
            counters.put("fetchTime", fetchTime);
            counters.put("fetchCount", fetchCount);
            counters.put("fetchRowTimeAvg", fetchCount>0 ? ((double)fetchTime/(double)fetchCount) : Double.NaN);
            
            counters.put("sumFetchTimeNS", sumFetchTimeNS);
            counters.put("minFetchTimeNS", minFetchTimeNS);
            counters.put("maxFetchTimeNS", maxFetchTimeNS);
            
            counters.put("sumInsertTimeNS", sumInsertTimeNS);
            counters.put("minInsertTimeNS", minInsertTimeNS);
            counters.put("maxInsertTimeNS", maxInsertTimeNS);
            
            counters.put("rebuildStart", rebuildStart);
            counters.put("rebuildFinish", rebuildFinish);
            counters.put("rebuildTime", Math.abs(rebuildStart - rebuildFinish));
            counters.put("rebuildColumns", rebuildColumns);
            return counters;
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="fetch()">
    public boolean fetch(){
        synchronized(this){
            long t0 = -1;
            long t0ns = -1;
            long t1 = -1;
            long t1ns = -1;
            long t2 = -1;
            long t2ns = -1;
            
            try{
                if( dataTable==null )throw new IllegalStateException("dataTable == null");
                if( !dataTableRebuilded ){
                    rebuildTable();
                }

                t0 = System.currentTimeMillis();
                t0ns = System.nanoTime();
                if( !hasNext() )return false;

                Object[] vals = next();
                
                int rownum = -1;
                if( resultSet!=null ){
                    try {
                        rownum = resultSet.getRow();
                    } catch ( SQLException ex) {
                        Logger.getLogger(ResultSetFetcher.class.getName()).log(Level.SEVERE, null, ex);
                        if( sqlExceptionHook!=null ){
                            sqlExceptionHook.apply(ex);
                        }
                        return false;
                    }
                }
                
                if( vals==null )return false;                
                t1 = System.currentTimeMillis();
                t1ns = System.nanoTime();

                DataTableInserting ins = dataTable.insert(vals).fixed(true);
                ins.go();
                t2 = System.currentTimeMillis();
                t2ns = System.nanoTime();

                DataRow dr = ins.getDataRow();
                fetchCount++;
                
                return true;
            }finally{
                if( fetchStart<=0 && t0>0 )fetchStart = t0;                
                if( t2>0 )fetchLast = t2;
                
                if( t0ns>0 && t1ns>0 && t2ns>0 ){
                    long fetchTimeNS = Math.abs(t1ns - t0ns);
                    sumFetchTimeNS += fetchTimeNS;
                    minFetchTimeNS = Math.min(fetchTimeNS, minFetchTimeNS);
                    maxFetchTimeNS = Math.max(fetchTimeNS, maxFetchTimeNS);
                    
                    long insertTimeNS = Math.abs(t2ns - t1ns);
                    sumInsertTimeNS += insertTimeNS;
                    minInsertTimeNS = Math.min(fetchTimeNS, minInsertTimeNS);
                    maxInsertTimeNS = Math.max(fetchTimeNS, maxInsertTimeNS);
                }
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="withClose : boolean">
    protected boolean withClose = false;
    
    /**
     * Указывает закрывать или нет ResultSet при завершении чтения.
     * <p>
     * Значение актуально если releaseREsultSet = true
     * @return true - закрывать ResultSet
     * @see #fetchFinished() 
     */
    public boolean isWithClose() {
        synchronized(this){
            return withClose;
        }
    }
    
    /**
     * Указывает закрывать или нет ResultSet при завершении чтения.
     * <p>
     * Значение актуально если releaseREsultSet = true
     * @param withClose true - закрывать ResultSet
     * @see #fetchFinished() 
     */
    public void setWithClose(boolean withClose) {
        Object old,cur;
        synchronized(this){
            old = this.withClose;
            this.withClose = withClose;
            cur = this.withClose;
        }
        firePropertyChange("withClose", old, cur);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="releaseResultSet : boolean">
    protected boolean releaseResultSet = true;
    
    /**
     * Указывает удалять или нет ссылку на ResultSet
     * @return true - удалять ссылку на ResultSet
     */
    public boolean isReleaseResultSet() {
        synchronized(this){
            return releaseResultSet;
        }
    }
    
    /**
     * Указывает удалять или нет ссылку на ResultSet
     * @param releaseResultSet  true - удалять ссылку на ResultSet
     */
    public void setReleaseResultSet(boolean releaseResultSet) {
        Object old,cur;
        synchronized(this){
            old = this.releaseResultSet;
            this.releaseResultSet = releaseResultSet;
            cur = this.releaseResultSet;
        }
        firePropertyChange("closeResultSet", old, cur);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="fetchLast()">
    /**
     * Вызывается при завершении чтения данных.
     * 
     * <ul>
     * <li>Если releaseResultSet = true, то
     *  <ul>
     * <li> ссылка на resultSet удаляется </li>
     * <li> Если withClose = true, то 
     * <ul>
     * <li>resultSet - закрывается (если еще не закрыт)</li>
     * </ul>
     * </li>
     * 
     * </ul>
     * </li>
     * </ul>
     */
    protected void fetchFinished(){
        logFine("fetchFinished");
        boolean fireClosed = false;
        synchronized(this){
            if( releaseResultSet && resultSet!=null ){
                if( withClose ){
                    try {
                        if( !resultSet.isClosed() ){
                            resultSet.close();
                        }
                    } catch ( SQLException ex) {
                        Logger.getLogger(ResultSetFetcher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                resultSet = null;
                fireClosed = true;
            }
        }
        if( fireClosed ){
            firePropertyChange("closed", false, true);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="closed : boolean">
    public boolean isClosed(){
        synchronized(this){
            return resultSet == null;
        }
    }
    //</editor-fold>
}
