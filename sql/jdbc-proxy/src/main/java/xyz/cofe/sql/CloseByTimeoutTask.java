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

import xyz.cofe.sql.proxy.InvokeActivityStat;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Закрывает соединения/statement которые не используется больше определенного времени idleTimeout
 * @author Kamnev Georgiy
 */
public class CloseByTimeoutTask extends TimerTask {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(CloseByTimeoutTask.class.getName());

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
        logger.entering(CloseByTimeoutTask.class.getName(), method, params);
    }
    
    private static void logExiting( String method){
        logger.exiting(CloseByTimeoutTask.class.getName(), method);
    }
    
    private static void logExiting( String method, Object result){
        logger.exiting(CloseByTimeoutTask.class.getName(), method, result);
    }
    //</editor-fold>
    
    public CloseByTimeoutTask( ConnectPool cpool){
        if( cpool==null )throw new IllegalArgumentException("cpool == null");
        this.connectPool = cpool;
    }
    
    protected ConnectPool connectPool;

    @Override
    public void run() {
        logFinest("run()");
        
        ConnectPool cp = connectPool;
        if( cp==null )return;
        
        cleanupConnections(cp);
        cleanupStatements(cp);
    }
    
    private void cleanupStatements( ConnectPool cp){
        int alreadyClosed = 0;
        int closedNow = 0;
        
        Set<Statement> stmnts = cp.getStatements(false);
        if( stmnts==null || stmnts.isEmpty() )return;
        
        for( Statement st : stmnts ){
            if( st==null )continue;
            
            //String cname = cp.nameOf(conn);
            String stname = "unnamed stamement";
            
            ConnectOptions copts = cp.optionsOf(st);
            if( copts==null ){
                logFinest("skip, no options for {0}", stname);
                continue;
            }
            
            long idleTimeout = copts.getStatementIdleTimeout();
            if( idleTimeout<=0 ){
                logFinest("skip, idle={0} for {1}", idleTimeout, stname);
                continue;
            }
            
            InvokeActivityStat stat = cp.activityStatOf(st);
            Long lastAct = stat.getLastActivity();
            if( lastAct==null ){
                logFinest("skip, no last activity for {0}", stname);
                continue;
            }
            
            long curTime = System.currentTimeMillis();
            long tdiff = curTime - lastAct;
            
            if( tdiff<idleTimeout ){
                logFinest("skip, tdiff({0})<idle({1}) for {2}", tdiff, idleTimeout, stname);
                continue;
            }
            
            try {
                if( st.isClosed() ){
                    alreadyClosed++;
                    logFinest("skip, already closed for {0}", stname);
                }else{
                    logFine("close statement {0}", stname);
                    st.close();
                    closedNow++;

                    //ConnectPoolEvent.Disconnected ev = new ConnectPoolEvent.Disconnected(cp);
                    //ev.setConnection(st);
                    //ev.setSourceConnection(cp.sourceOf(st));
                    //ev.setProxy(cp.isProxy(st));

                    //cp.fireDataEvent(ev);
                }
            } catch ( SQLException ex) {
                Logger.getLogger(CloseByTimeoutTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if( (alreadyClosed+closedNow)>0 ){
            cp.removeClosedStatements();
        }
    }
    
    private void cleanupConnections( ConnectPool cp){
        int alreadyClosed = 0;
        int closedNow = 0;
        
        Set<Connection> conns = cp.getSourceConnections();
        if( conns==null || conns.isEmpty() )return;
        
        for( Connection conn : conns ){
            if( conn==null )continue;
            
            String cname = cp.nameOf(conn);
            
            ConnectOptions copts = cp.optionsOf(conn);
            if( copts==null ){
                logFinest("skip, no options for {0}", cname);
                continue;
            }
            
            long idleTimeout = copts.getIdleTimeout();
            if( idleTimeout<=0 ){
                logFinest("skip, idle={0} for {1}", idleTimeout, cname);
                continue;
            }
            
            InvokeActivityStat stat = cp.activityStatOf(conn);
            Long lastAct = stat.getLastActivity();
            if( lastAct==null ){
                logFinest("skip, no last activity for {0}", cname);
                continue;
            }
            
            long curTime = System.currentTimeMillis();
            long tdiff = curTime - lastAct;
            
            if( tdiff<idleTimeout ){
                logFinest("skip, tdiff({0})<idle({1}) for {2}", tdiff, idleTimeout, cname);
                continue;
            }
            
            try {
                if( conn.isClosed() ){
                    alreadyClosed++;
                    logFinest("skip, already closed for {0}", cname);
                }else{
                    logFine("close connection {0}", cname);
                    conn.close();
                    closedNow++;

                    ConnectPoolEvent.Disconnected ev = new ConnectPoolEvent.Disconnected(cp);
                    ev.setConnection(conn);
                    ev.setSourceConnection(cp.sourceOf(conn));
                    ev.setProxy(cp.isProxy(conn));

                    cp.fireDataEvent(ev);
                }
            } catch ( SQLException ex) {
                Logger.getLogger(CloseByTimeoutTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if( (alreadyClosed+closedNow)>0 ){
            cp.removeClosedConnections();
        }
    }
}
