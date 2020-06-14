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

package xyz.cofe.sql;

import xyz.cofe.collection.BasicEventMap;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Опции соединения
 * @author Kamnev Georgiy
 */
public class ConnectOptionsProperties extends BasicEventMap<String, String> implements ConnectOptions
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ConnectOptionsProperties.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }
    
    @SuppressWarnings("SimplifiableConditionalExpression")
    private static boolean isLogSevere(){
        Level logLevel = logger.getLevel();
        return logLevel==null ? true : logLevel.intValue() <= Level.SEVERE.intValue();
    }

    @SuppressWarnings("SimplifiableConditionalExpression")
    private static boolean isLogWarning(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.WARNING.intValue();
    }

    @SuppressWarnings("SimplifiableConditionalExpression")
    private static boolean isLogInfo(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.INFO.intValue();
    }

    @SuppressWarnings("SimplifiableConditionalExpression")
    private static boolean isLogFine(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINE.intValue();
    }

    @SuppressWarnings("SimplifiableConditionalExpression")
    private static boolean isLogFiner(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINER.intValue();
    }

    @SuppressWarnings("SimplifiableConditionalExpression")
    private static boolean isLogFinest(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINEST.intValue();
    }

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
        logger.entering(ConnectOptionsProperties.class.getName(), method, params);
    }
    
    private static void logExiting( String method){
        logger.exiting(ConnectOptionsProperties.class.getName(), method);
    }
    
    private static void logExiting( String method, Object result){
        logger.exiting(ConnectOptionsProperties.class.getName(), method, result);
    }
    //</editor-fold>

    public ConnectOptionsProperties() {
    }

    public ConnectOptionsProperties( Map<String, String> wrappedMap) {
        super(wrappedMap);
    }

    public ConnectOptionsProperties( Map<String, String> wrappedMap, ReadWriteLock lock) {
        super(wrappedMap, lock);
    }
    
    //<editor-fold defaultstate="collapsed" desc="proxyConnection">
    @Override
    public boolean isProxyConnection() {
        String v = get("proxyConnection");
        if( v==null )v = "false";
        return "true".equalsIgnoreCase(v);
    }
    
    public void setProxyConnection( boolean v ){
        put("proxyConnection", v ? "true" : "false" );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="collectActivityStat">
    @Override
    public boolean isCollectActivityStat() {
        String v = get("collectActivityStat");
        if( v==null )v = "false";
        return "true".equalsIgnoreCase(v);
    }
    
    public void setCollectActivityStat( boolean v ){
        put("collectActivityStat", v ? "true" : "false" );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="idleTimeout">
    /**
     * Время (мс) простоя до закрытия соединения
     * @return время простоя до закрытия соединения или -1
     */
    @Override
    public long getIdleTimeout() {
        String str = get("idleTimeout");
        if( str==null || str.trim().length()<1 )str = "-1";
        try{
            long num = Long.parseLong(str);
            return num;
        }catch( NumberFormatException e){
            return -1;
        }
    }
    
    /**
     * Время (мс) простоя до закрытия соединения
     * @param ms время простоя до закрытия соединения или -1
     */
    public void setIdleTimeout(long ms){
        put("idleTimeout", Long.toString(ms));
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="statementIdleTimeout">
    /**
     * Указывает время (мс) простоя запроса
     * @return Время простоя
     */
    @Override
    public long getStatementIdleTimeout(){
        String str = get("statementIdleTimeout");
        if( str==null || str.trim().length()<1 )str = "-1";
        try{
            long num = Long.parseLong(str);
            return num;
        }catch( NumberFormatException e){
            return -1;
        }
    }
    
    public void setStatementIdleTimeout(long ms){
        put("statementIdleTimeout", Long.toString(ms));
    }
    //</editor-fold>

    @Override
    public boolean isShared() {
        String v = get("shared");
        if( v==null )v = "false";
        return "true".equalsIgnoreCase(v);
    }
    
    public void setShared(boolean v){
        put("shared", v ? "true" : "false" );
    }
}
