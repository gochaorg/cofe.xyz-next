/*
 * The MIT License
 *
 * Copyright 2017 Kamnev Georgiy (nt.gocha@gmail.com).
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

package xyz.cofe.sql.proxy;

import xyz.cofe.fn.Fn4;
import xyz.cofe.sql.ConnectPool;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Отслеживает вызов методов Statement и ведет статистику вызовов методов
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class StatementTracker extends MethodCallAdapter
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(StatementTracker.class.getName());

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
        logger.entering(StatementTracker.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(StatementTracker.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(StatementTracker.class.getName(), method, result);
    }
    //</editor-fold>

    protected final WeakReference<ConnectPool> wcpool;
    protected final WeakReference<Connection> wconn;
    protected final WeakReference<Statement> wst;

    public StatementTracker(ConnectPool cpool, Connection conn, Statement st){
        if( cpool==null )throw new IllegalArgumentException("cpool == null");
        if( conn==null )throw new IllegalArgumentException("conn == null");
        if( st==null )throw new IllegalArgumentException("st == null");
        
        wconn  = new WeakReference<>(conn);
        wcpool = new WeakReference<>(cpool);
        wst = new WeakReference<>(st);
    }
    
    //<editor-fold defaultstate="collapsed" desc="collect activity">
    //<editor-fold defaultstate="collapsed" desc="collectName : Func4">
    protected Fn4<Object,Object,Method,Object[],String> collectName = null;
    
    /**
     * Указывает функцию сопостовления метода и имя метрики
     * @return функц именования метрики
     */
    public Fn4<Object,Object,Method,Object[],String> getCollectName(){
        synchronized(this){
            return collectName;
        }
    }
    
    /**
     * Указывает функцию сопостовления метода и имя метрики
     * @param fn функц именования метрики
     */
    public void setCollectName( Fn4<Object,Object,Method,Object[],String> fn ){
        synchronized(this){
            collectName = fn;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="connectionCollectName">
    protected String connectionCollectName = "@statment";
    
    /**
     * Указывает имя обновляемой метрики для соединения
     * @return имя метрики - по умолчанию @statment
     */
    public String getConnectionCollectName() {
        synchronized(this){ return connectionCollectName; }
    }
    
    /**
     * Указывает имя обновляемой метрики для соединения
     * @param connectionCollectName имя метрики - по умолчанию @statment
     */
    public void setConnectionCollectName(String connectionCollectName) {
        synchronized(this){ this.connectionCollectName = connectionCollectName; }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="isCollectActivity()">
    /**
     * Возвращает есть ли необходимость собирать статистику
     * @return true - требуетсясобирать статистику
     */
    public boolean isCollectActivity(){
        synchronized(this){
            return (collectName!=null || connectionCollectName!=null ) && wconn.get()!=null && wcpool.get()!=null;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="collectActivity()">
    /**
     * Сбор статистики вызова метода
     * @param proxy прокси объекта
     * @param source исходный объект
     * @param meth метод
     * @param args аргументы
     */
    protected void collectActivity(Object proxy, Object source, Method meth, Object[] args){
        synchronized(this){
            ConnectPool cp = wcpool.get();
            Connection conn = wconn.get();
            Fn4<Object,Object,Method,Object[],String> fn = collectName;
            
            if( source instanceof Statement && fn!=null && cp!=null ){
                Statement st = (Statement)source;
                
                String cname = fn.apply(proxy, source, meth, args);
                if( cname!=null ){
                    InvokeActivityStat iastat = cp.activityStatOf(st);
                    if( iastat!=null ){
                        iastat.collect(cname);
                        logFinest("collected stat for statement");
                    }
                }
            }
            
            if( cp!=null && conn!=null && connectionCollectName!=null ){
                if( fn!=null ){
                    String cname = fn.apply(proxy, source, meth, args);
                    if( cname!=null ){
                        InvokeActivityStat iastat = cp.activityStatOf(conn);
                        if( iastat!=null ){
                            iastat.collect(connectionCollectName);
                            logFinest("collected stat for connection");
                        }
                    }
                }else{
                    InvokeActivityStat iastat = cp.activityStatOf(conn);
                    if( iastat!=null ){
                        iastat.collect(connectionCollectName);
                        logFinest("collected stat for connection");
                    }
                }
            }
        }
    }
    //</editor-fold>
    //</editor-fold>

    @Override
    public void beginCall(Object proxy, Object source, Method meth, Object[] args) {
        synchronized(this){
            if( isCollectActivity() ){
                collectActivity(proxy, source, meth, args);
            }
        }
        return;
    }
}
