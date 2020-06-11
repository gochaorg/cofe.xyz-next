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

import xyz.cofe.data.store.TableBuilder;
import xyz.cofe.fn.Fn1;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kamnev Georgiy
 */
public class DataTableBuilder implements TableBuilder {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(DataTableBuilder.class.getName());

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
        logger.entering(DataTableBuilder.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(DataTableBuilder.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(DataTableBuilder.class.getName(), method, result);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="classLoader">
    protected ClassLoader classLoader;

    @Override
    public synchronized ClassLoader getClassLoader() {
        return classLoader;
    }

    public synchronized void setClassLoader(ClassLoader cl){
        classLoader = cl;
    }
    //</editor-fold>
        
    //<editor-fold defaultstate="collapsed" desc="dataTable">
    protected DataTable dataTable;

    @Override
    public synchronized DataTable getDataTable() {
        if( dataTable!=null )return dataTable;
        dataTable = new DataTable();
        return dataTable;
    }

    public synchronized void setDataTable( DataTable mt ){
        dataTable = mt;
    }
    //</editor-fold>

    @Override
    public synchronized void begin() {
        DataTable tbl = getDataTable();
        tbl.drop();
        tbl.setTrackChanges(false);
    }

    @Override
    public synchronized void beginColumns() {
    }

    @Override
    public synchronized void addColumn( DataColumn col) {
        getDataTable().addColumn(col);
    }

    @Override
    public synchronized void endColumns() {
    }

    @Override
    public synchronized void unchangedRow(final DataRow row) {
        //getDataTable().getWorkedRows().add(row);
        getDataTable().lockRunInternal(new Fn1<DataTable.InternalRun, Object>() {
            @Override
            public Object apply(DataTable.InternalRun irun) {
                irun.getWorkedRows().add(row);
                return null;
            }
        });
    }

    @Override
    public synchronized void changedRow(final DataRow row) {
        //getDataTable().getWorkedRows().add(row);
        getDataTable().lockRunInternal(new Fn1<DataTable.InternalRun,Object>() {
            @Override
            public Object apply(DataTable.InternalRun irun) {
                irun.getWorkedRows().add(row);
                return null;
            }
        });
    }

    @Override
    public synchronized void insertedRow(final DataRow row) {
        //getDataTable().getWorkedRows().add(row);
        //getDataTable().getInsertedRows().add(row);
        
        getDataTable().lockRunInternal(new Fn1<DataTable.InternalRun, Object>() {
            @Override
            public Object apply(DataTable.InternalRun irun) {
                irun.getWorkedRows().add(row);
                irun.getInsertedRows().add(row);
                return null;
            }
        });
    }

    @Override
    public synchronized void deletedRow(final DataRow row) {
        //getDataTable().getDeletedRows().add(row);
        
        getDataTable().lockRunInternal(new Fn1<DataTable.InternalRun, Object>() {
            @Override
            public Object apply(DataTable.InternalRun irun) {
                irun.getDeletedRows().add(row);
                return null;
            }
        });
    }

    @Override
    public synchronized void end() {
        DataTable tbl = getDataTable();
        tbl.setTrackChanges(true);
    }
}
