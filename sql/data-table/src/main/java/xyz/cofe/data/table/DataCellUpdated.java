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

package xyz.cofe.data.table;

import xyz.cofe.data.events.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kamnev Georgiy
 */
public class DataCellUpdated implements DataEvent {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(DataCellUpdated.class.getName());

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
        logger.entering(DataCellUpdated.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(DataCellUpdated.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(DataCellUpdated.class.getName(), method, result);
    }
    //</editor-fold>
    
    public DataCellUpdated(){
    }
    
    //<editor-fold defaultstate="collapsed" desc="table">
    private DataTable table;
    
    public synchronized DataTable getTable() {
        return table;
    }
    
    public synchronized void setTable(DataTable table) {
        this.table = table;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="row">
    private DataRow row;
    
    public synchronized DataRow getRow() {
        return row;
    }
    
    public synchronized void setRow(DataRow nrow) {
        this.row = nrow;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="column">
    private int column;
    
    public synchronized int getColumn() {
        return column;
    }
    
    public synchronized void setColumn(int column) {
        this.column = column;
    }
    //</editor-fold>    
    //<editor-fold defaultstate="collapsed" desc="oldValue">
    private Object oldValue;
    
    public synchronized Object getOldValue() {
        return oldValue;
    }
    
    public synchronized void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="currentValue">
    private Object currentValue;
    
    public synchronized Object getCurrentValue() {
        return currentValue;
    }
    
    public synchronized void setCurrentValue(Object currentValue) {
        this.currentValue = currentValue;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="originValue">
    private Object originValue;
    
    public synchronized Object getOriginValue() {
        return originValue;
    }
    
    public synchronized void setOriginValue(Object originValue) {
        this.originValue = originValue;
    }
    //</editor-fold>

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DataCellUpdated{")
            .append("table=").append(table)
            .append(", row=").append(row)
            .append(", column=").append(column)
            .append(", originValue=").append(originValue)
            .append(", oldValue=").append(oldValue)
            .append(", currentValue=").append(currentValue)
            .append("}");
        return sb.toString();
    }
}
