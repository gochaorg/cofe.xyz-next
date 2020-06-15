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

package xyz.cofe.sql.cpool.proxy;

import xyz.cofe.fn.Fn4;
import xyz.cofe.sql.cpool.ConnectPool;
import xyz.cofe.sql.cpool.ConnectPoolEvent;
import xyz.cofe.text.Text;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Трекер соединения с базой данных
 * @author Kamnev Georgiy
 */
public class ConnectionTracker extends MethodCallAdapter
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ConnectionTracker.class.getName());

    private static Level logLevel(){ return Logger.getLogger(ConnectionTracker.class.getName()).getLevel(); }
    
    private static boolean isLogSevere(){ 
        Level logLevel = Logger.getLogger(ConnectionTracker.class.getName()).getLevel();
        return logLevel==null ? true : logLevel.intValue() <= Level.SEVERE.intValue();
    }
    
    private static boolean isLogWarning(){
        Level logLevel = Logger.getLogger(ConnectionTracker.class.getName()).getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.WARNING.intValue();
    }
    
    private static boolean isLogInfo(){ 
        Level logLevel = Logger.getLogger(ConnectionTracker.class.getName()).getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.INFO.intValue();
    }
    
    private static boolean isLogFine(){
        Level logLevel = Logger.getLogger(ConnectionTracker.class.getName()).getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINE.intValue();
    }
    
    private static boolean isLogFiner(){
        Level logLevel = Logger.getLogger(ConnectionTracker.class.getName()).getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINER.intValue();
    }
    
    private static boolean isLogFinest(){ 
        Level logLevel = Logger.getLogger(ConnectionTracker.class.getName()).getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINEST.intValue();
    }

    private static void logFine(String message,Object ... args){
        Logger.getLogger(ConnectionTracker.class.getName()).log(Level.FINE, message, args);
    }
    
    private static void logFiner(String message,Object ... args){
        Logger.getLogger(ConnectionTracker.class.getName()).log(Level.FINER, message, args);
    }
    
    private static void logFinest(String message,Object ... args){
        Logger.getLogger(ConnectionTracker.class.getName()).log(Level.FINEST, message, args);
    }
    
    private static void logInfo(String message,Object ... args){
        Logger.getLogger(ConnectionTracker.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(ConnectionTracker.class.getName()).log(Level.WARNING, message, args);
    }
    
    private static void logSevere(String message,Object ... args){
        Logger.getLogger(ConnectionTracker.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(ConnectionTracker.class.getName()).log(Level.SEVERE, null, ex);
    }

    private static void logEntering(String method,Object ... params){
        Logger.getLogger(ConnectionTracker.class.getName()).entering(ConnectionTracker.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        Logger.getLogger(ConnectionTracker.class.getName()).exiting(ConnectionTracker.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        Logger.getLogger(ConnectionTracker.class.getName()).exiting(ConnectionTracker.class.getName(), method, result);
    }
    //</editor-fold>
    
    protected final WeakReference<ConnectPool> wcpool;
    protected final WeakReference<Connection> wconn;
    
    public ConnectionTracker(ConnectPool cpool, Connection conn){
        if( cpool==null )throw new IllegalArgumentException("cpool == null");
        if( conn==null )throw new IllegalArgumentException("conn == null");
        
        wconn  = new WeakReference<>(conn);
        wcpool = new WeakReference<>(cpool);
    }
    
    //<editor-fold defaultstate="collapsed" desc="collect activity">
    //<editor-fold defaultstate="collapsed" desc="collectName : Func4">
    protected volatile Fn4<Object,Object,Method,Object[],String> collectName = null;
    /**
     * Указывает функцию определяющую имя метрики для метода
     * @return функция сопоставления имени метода и названия метрики
     */
    public Fn4<Object,Object,Method,Object[],String> getCollectName(){
        synchronized(this){
            return collectName;
        }
    }
    /**
     * Указывает функцию определяющую имя метрики для метода
     * @param fn функция сопоставления метода и название метрики, 
     * если функция вернет null, то метрика не будет собираться для указанного метода <br>
     * fn( proxy, source, method, arguments ) : metricName
     */
    public void setCollectName( Fn4<Object,Object,Method,Object[],String> fn ){
        synchronized(this){
            collectName = fn;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="isCollectActivity()">
    /**
     * Возвращает собирать или нет статистику вызова методов
     * @return true - собирать статистику
     */
    public boolean isCollectActivity(){
        synchronized(this){
            return collectName!=null && wconn.get()!=null && wcpool.get()!=null;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="collectActivity()">
    /**
     * Собирает статистику вызова метода
     * @param proxy прокси объект (содинение)
     * @param source исходный объект
     * @param meth метод
     * @param args параметры метода
     * @see #setCollectName
     */
    protected void collectActivity(Object proxy, Object source, Method meth, Object[] args){
        synchronized(this){
            ConnectPool cp = wcpool.get();
            Connection conn = wconn.get();
            Fn4<Object,Object,Method,Object[],String> fn = collectName;
            if( cp==null || conn==null || fn==null )return;
            
            String cname = fn.apply(proxy, source, meth, args);
            if( cname==null )return;
            
            InvokeActivityStat iastat = cp.activityStatOf(conn);
            if( iastat==null )return;
            
            iastat.collect(cname);
            logger.finer("collected activity src="+source+" method="+meth);
        }
    }
    //</editor-fold>
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="trackClose : boolean">
    protected boolean trackClose = false;
    
    /**
     * Отслеживать вызов метода close().
     * 
     * <p>
     * при закрытии соединения посылает сообщение ConnectPoolEvent.Disconnected в ConnectPool
     * @return true - отслеживать вызов close
     */
    public boolean isTrackClose() {
        synchronized(this){ return trackClose; }
    }
    
    /**
     * Отслеживать вызов метода close()
     * @param trackClose true - отслеживать вызов close
     * @see #isTrackClose() 
     */
    public void setTrackClose(boolean trackClose) {
        synchronized(this){ this.trackClose = trackClose; }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="trackStatements : boolean">
    protected boolean trackStatements = false;
    
    /**
     * Создавать proxy для Statement
     * @return true - создавать proxy для Statement
     */
    public boolean isTrackStatements() {
        synchronized(this){ return trackStatements; }
    }
    
    /**
     * Создавать proxy для Statement
     * @param trackStatements true - создавать proxy для Statement
     */
    public void setTrackStatements(boolean trackStatements) {
        synchronized(this){ this.trackStatements = trackStatements; }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="shared : boolean">
    protected boolean shared = false;
    /**
     * Соединение является общим (shared) для клиентов.
     * 
     * <p>
     * Если соединение является общим, то при вызове close прокси соединения, 
     * вызывается очистка связанных объектов (statements/resultset)
     * @return true - является общим
     */
    public synchronized boolean isShared() {
        return shared;
    }
    /**
     * Соединение является общим (shared) для клиентов
     * @param shared true - является общим
     * @see #isShared() 
     */
    public synchronized void setShared(boolean shared) {
        this.shared = shared;
    }
    //</editor-fold>
    
    protected volatile Boolean isClosed = null;
    
    @Override
    public void beginCall(Object proxy, Object source, Method meth, Object[] args) {
        synchronized(this){
            if( isCollectActivity() ){
                collectActivity(proxy, source, meth, args);
            }
        }
    }

    /**
     * Отслеживает вызов close() и создает событие ConnectPoolEvent.Disconnected. <br>
     * Отслеживает вызов createStatement, prepareCall, prepareStatement и создает прокси
     * @param proxy прокси соединения с бд
     * @param source исходный объект соединения с бд
     * @param meth метод
     * @param args аргументы вызова
     * @param callResult результат вызова
     * @param err исключение если оно было
     * @return результат перехвата
     */
    @Override
    public HookResult endCall(Object proxy, Object source, Method meth, Object[] args, Object callResult, Throwable err) {
        HookResult rrc = super.endCall(proxy, source, meth, args, callResult, err);
        synchronized(this){
            ConnectPool cp = wcpool.get();
            Connection conn = wconn.get();
            
            if( meth!=null && meth.getName().equals("close") && err==null && trackClose && !shared ){
                if( cp!=null && conn!=null ){
                    ConnectPoolEvent.Disconnected ev = new ConnectPoolEvent.Disconnected(cp);
                            
                    ev.setConnection(proxy instanceof Connection ? (Connection)proxy : null);
                    ev.setSourceConnection(conn);
                    ev.setProxy( proxy instanceof Connection ? cp.isProxy((Connection)proxy)  : false );
                    
                    cp.fireDataEvent(ev);
                }
            }
            
            // rewrite createStatement/prepareCall/prepareStatement
            
            Object cres = rrc!=null ? rrc.getResult() : callResult;
            if( cres instanceof Statement 
            && trackStatements 
            && cp!=null && !cp.isProxy(cres) 
            && conn!=null
            && meth!=null && Text.in(meth.getName(), "createStatement", "prepareCall", "prepareStatement")
            ){
                GenericProxy.Builder bldr = null;
                switch( meth.getName() ){
                    case "createStatement": {
                        if( cres instanceof Statement ){
                            bldr = GenericProxy.builder((Statement)cres, Statement.class);
                        }
                    } break;
                    case "prepareStatement": {
                        if( cres instanceof PreparedStatement ){
                            bldr = GenericProxy.builder((PreparedStatement)cres, PreparedStatement.class);
                        }
                    } break;
                    case "prepareCall": {
                        if( cres instanceof CallableStatement ){
                            bldr = GenericProxy.builder((CallableStatement)cres, CallableStatement.class);
                        }
                    } break;
                }
                
                if( bldr!=null ){
                    StatementTracker stt = new StatementTracker(cp, conn, (Statement)cres);
                    stt.setCollectName(
                        InvokeActivityStat.exclude(
                            InvokeActivityStat.simpleMethodName(),
                            "toString","hashCode", "equals"
                        )
                    );
                    bldr.add(stt);
                    
                    Object rewritedStmnt = bldr.create();
                    HookResult srrc = new HookResult(rewritedStmnt);
                    logFine("create proxy for statement with tracker");
                    
                    ConnectPoolEvent.StatementCreated stEv = new ConnectPoolEvent.StatementCreated(cp);
                    stEv.setArguments(args);
                    stEv.setMethod(meth);
                    stEv.setStatement((Statement)rewritedStmnt);
                    stEv.setSourceStatement((Statement)cres);
                    stEv.setProxy(true);
                    cp.fireDataEvent(stEv);
                    
                    cp.registerStatement(conn, (Statement)cres);
                    
                    return srrc;
                }
            }
        }        
        return rrc;
    }

    @Override
    public boolean isRewriteCall(Object proxy, Object source, Method method, Object[] args) {
        // rewrite close()
        if( method!=null && 
            method.getName().equals("close") && 
            /*( (args!=null && args.length==0) ||
               args==null
            )*/
            shared 
        ){
            return true;
        }

        // rewrite isClosed()
        if( method!=null && method.getName().equals("isClosed") 
            /* &&  ( (args!=null && args.length==0) ||
                   args==null
                ) */
            &&  shared 
        ){
            return true;
        }
        return super.isRewriteCall(proxy, source, method, args);
    }

    @Override
    public synchronized Object rewriteCall(Object proxy, Object source, Method method, Object[] args) {
        // rewrite close()
        if( method!=null && 
            method.getName().equals("close") && 
            /* ( (args!=null && args.length==0) ||
               args==null
            ) && */
            shared 
        ){
            isClosed = true;
            ConnectPool cp = wcpool!=null ? wcpool.get() : null;
            if( cp!=null && proxy instanceof Connection ){
                cp.cleanup((Connection)proxy);
            }
                
            Logger.getLogger(ConnectionTracker.class.getName()).fine("catch close() for shared connection");
            return null;
        }
        
        // rewrite isClosed()
        if( method!=null && method.getName().equals("isClosed") 
            /*&&  ( (args!=null && args.length==0) ||
                   args==null
                ) */
            &&  shared 
        ){
            if( isClosed!=null && isClosed==true ){
                return true;
            }
            
            try {
                Object val = method.invoke(source, args);
                if( val instanceof Boolean ){
                    if( ((Boolean)val) == true ){
                        isClosed = true;
                    }
                }
                return val;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ConnectionTracker.class.getName()).log(Level.SEVERE, null, ex);
                throw new Error("ConntectionTracker error invoke "+method, ex);
            }
        }
        
        return super.rewriteCall(proxy, source, method, args);
    }
}
