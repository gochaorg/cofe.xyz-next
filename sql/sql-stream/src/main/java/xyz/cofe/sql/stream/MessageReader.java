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

package xyz.cofe.sql.stream;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Чтение сообщений из Statement warnings
 * @author Kamnev Georgiy
 */
public class MessageReader extends Thread {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(MessageReader.class.getName());

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
        logger.entering(MessageReader.class.getName(), method, params);
    }
    
    private static void logExiting( String method){
        logger.exiting(MessageReader.class.getName(), method);
    }
    
    private static void logExiting( String method, Object result){
        logger.exiting(MessageReader.class.getName(), method, result);
    }
    //</editor-fold>
    
    /**
     * Конструктор
     * @param st SQL Statement из которого будут прочитываться сообщения
     * @param consumer Приемник сообщений
     * @param sleep задержка между приемами сообщений
     */
    public MessageReader( Statement st, Consumer<SQLWarning> consumer, int sleep){
        setName("QueryExecutorMessageReader");
        setDaemon(true);
        this.statement = st;
        this.consumer = consumer;
        this.sleep = sleep;
        sync = new Object();
    }

    /**
     * Конструктор
     * @param st SQL Statement из которого будут прочитываться сообщения
     * @param consumer Приемник сообщений
     */
    public MessageReader( Statement st, Consumer<SQLWarning> consumer){
        this(st, consumer, 100);
    }

    protected final Object sync;

    public Object getSync(){ return sync; }

    //<editor-fold defaultstate="collapsed" desc="statement">
    protected Statement statement;

    public Statement getStatement(){
        synchronized(sync){
            return statement;
        }
    }

    public void setStatement( Statement st){
        synchronized(sync){
            statement = st;
        }
    }

    /**
     * Удаление ссылки на statement
     */
    public void releaseStatement(){
        synchronized(sync){
            if( statement!=null ){
                statement = null;
                logFine("released statement");
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="consumer">
    protected Consumer<SQLWarning> consumer;

    public Consumer<SQLWarning> getConsumer(){
        synchronized(sync){
            return consumer;
        }
    }

    public void setConsumer( Consumer<SQLWarning> ncons ){
        synchronized(sync){
            this.consumer = ncons;
        }
    }
    //</editor-fold>

    protected final Set<SQLWarning> readed = new LinkedHashSet<>();

    //<editor-fold defaultstate="collapsed" desc="sqlWarnLevel">
    protected Level sqlWarnLevel = Level.FINE;

    /**
     * Указывает уровень логирования сообщений
     * @return уровень логирования - по умолчанию FINE
     */
    public Level getSqlWarnLevel() {
        synchronized( sync ){ return sqlWarnLevel; }
    }

    /**
     * Указывает уровень логирования сообщений
     * @param sqlWarnLevel уровень логирования - по умолчанию FINE
     */
    public void setSqlWarnLevel( Level sqlWarnLevel) {
        synchronized(sync) { this.sqlWarnLevel = sqlWarnLevel; }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="sleep">
    protected long sleep = 100;

    /**
     * Указывает задержку для ожидания поступления нового сообщения
     * @return задержка в мс
     */
    public long getSleep() {
        synchronized(sync){ return sleep; }
    }

    /**
     * Указывает задержку для ожидания поступления нового сообщения
     * @param sleep задержка в мс
     */
    public void setSleep(long sleep) {
        synchronized( sync ){ this.sleep = sleep; }
    }
    //</editor-fold>
    
    protected long startLag = 0;
    protected long waitForOpenTimeout = 2000;
    protected long waitForOpen = 0;

    //<editor-fold defaultstate="collapsed" desc="run()">
    @Override
    public void run() {
        logFine("start reading");
        
        long startlag = 0; // Лаг на открытие statement
        long wait_for_open_timeout = 2000; // таймаут ожидания открытия statement
        
        Statement st = null;
        synchronized(sync){
            st = statement;
            wait_for_open_timeout = waitForOpenTimeout;
            startlag = startLag;
        }
        
        //<editor-fold defaultstate="collapsed" desc="Задержка перед началом">
        if( startlag>0 ){
            try {
                logFiner("start lag, sleep={0}",startlag);
                Thread.sleep(startlag);
            } catch ( InterruptedException ex) {
                Logger.getLogger(MessageReader.class.getName()).log(Level.SEVERE, null, ex);
                logFine("interrupted");
                releaseStatement();
                return;
            }
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Ожидание открытия">
        if( wait_for_open_timeout>0 && st!=null ){
            logFiner("wait for open, timeout={0}",wait_for_open_timeout);
            long slp = 5;
            long t0 = System.currentTimeMillis();
            while( true ){
                long t1 = System.currentTimeMillis();
                long tdiff = Math.abs(t1 - t0);
                waitForOpen = tdiff;
                if( tdiff>wait_for_open_timeout ){
                    logFine("wait open timeout({0}) < {1}", tdiff, wait_for_open_timeout);
                    break;
                }
                
                try {
                    if( st.isClosed() ){
                        try {
                            Thread.sleep(slp);
                        } catch ( InterruptedException ex) {
                            logFine("interrupted");
                            releaseStatement();
                            return;
                        }
                    }else{
                        logFiner("statement openned per {0} ms",tdiff);
                        break;
                    }
                } catch ( SQLException ex) {
                    Logger.getLogger(MessageReader.class.getName()).log(Level.FINER, "wait for open", ex);
                    continue;
                }
            }
        }
        //</editor-fold>
        
        long started = System.currentTimeMillis();
        
        while( true ){
            if( Thread.currentThread().isInterrupted() ){
                logFine("break by interrupt");
                break;
            }

            Consumer<SQLWarning> cons = null;
            long slp = 100;
            Level sqlwl = Level.FINE;
            
            synchronized(sync){
                st = statement;
                cons = consumer;
                slp = sleep;
                sqlwl = sqlWarnLevel != null ? sqlWarnLevel : sqlwl;
            }
            
            if( st==null ){
                logFine("break by statement = null");
                break;
            }
            
            if( cons==null ){
                logFine("break by consumer = null");
                break;
            }
            
            try {
                SQLWarning warn = st.getWarnings();
                if( warn==null ){
                    //<editor-fold defaultstate="collapsed" desc="Пауза">
                    if( slp>0 ){
                        try {
                            Thread.sleep(slp);
                        } catch ( InterruptedException ex) {
                            logFine("break by interrupt");
                            break;
                        }
                    }else if( slp<0 ){
                        Thread.yield();
                    }
                    continue;
                    //</editor-fold>
                }
                
                //<editor-fold defaultstate="collapsed" desc="Передача сообщения">
                synchronized(readed){
                    while(true){
                        if( readed.contains(warn) ){
                            SQLWarning wnext = warn.getNextWarning();
                            if( wnext!=null ){
                                warn = wnext;
                                continue;
                            }else{
                                break;
                            }
                        }else{
                            try{
                                cons.accept(warn);
                            }catch( Throwable err ){
                                Logger.getLogger(QueryExecutor.class.getName()).log(
                                    Level.SEVERE,
                                    "consumer throw error "+
                                        err.getClass().getSimpleName()+": "+
                                        err.getMessage(),
                                    err);
                            }
                            readed.add(warn);
                            
                            SQLWarning wnext = warn.getNextWarning();
                            if( wnext!=null ){
                                warn = wnext;
                                continue;
                            }else{
                                break;
                            }
                        }
                    }
                }
                //</editor-fold>
            } catch ( SQLException ex) {
                //<editor-fold defaultstate="collapsed" desc="Лаг от начала">
                /*
                long lag = Math.abs(System.currentTimeMillis() - started);
                if( startlagmax>0 && lag<=startlagmax ){
                logFiner( "startLagMax {2} {3} lag {0} max {1}",lag,startlagmax, ex.getMessage(), ex.getErrorCode() );
                if( slp>0 ){
                try {
                logFiner("sleep {0}", slp);
                Thread.sleep(slp);
                } catch (InterruptedException intrex) {
                logFine("break by interrupt");
                break;
                }
                }else if( slp<0 ){
                logFiner("yield");
                Thread.yield();
                }
                continue;
                }
                */
                //</editor-fold>
                
                Logger.getLogger(QueryExecutor.class.getName()).log(sqlwl, null, ex);
                logFiner("breay by SQLException "+ex.getErrorCode()+" "+ex.getMessage());
                break;
            }
            
            //<editor-fold defaultstate="collapsed" desc="Пауза">
            if( slp>0 ){
                try {
                    logFinest("sleep {0}", slp);
                    Thread.sleep(slp);
                } catch ( InterruptedException ex) {
                    logFine("break by interrupt");
                    break;
                }
            }else if( slp<0 ){
                logFiner("yield");
                Thread.yield();
            }
            //</editor-fold>
        }
        
        logFiner("clear readed messages");
        readed.clear();
        
        releaseStatement();
        logFine("read finished");
    }
    //</editor-fold>

    /**
    * Остановка выполнения. <p>
    * Нельзя вызывать только в самом же треде.
    * @param timeout макс. время (мс) за которое должна произойти остановка
    * @param sleep время (мс) паузы, при значении &lt; 0 - передает управление другому потоку Thread.yield()
    */
   public void terminate( long timeout, long sleep ){
       if( !isAlive() )return;

       long curid = Thread.currentThread().getId();
       long thisid = getId();

       if( curid==thisid ){
           throw new IllegalThreadStateException("can't call from self thread");
       }

       long t0 = System.currentTimeMillis();
       while( isAlive() ){
           interrupt();
           long t1 = System.currentTimeMillis();
           long tdiff = Math.abs(t1 - t0);
           if( timeout>0 && tdiff>=timeout ){
               stop();
           }
           if( sleep>0 ){
               try {
                   Thread.sleep(sleep);
               } catch ( InterruptedException ex) {
                   Logger.getLogger(QueryExecutor.class.getName()).log(Level.SEVERE, null, ex);
                   stop();
               }
           }else if( sleep<0 ){
               Thread.yield();
           }
       }
   }
}
