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

import xyz.cofe.data.DataEvent;
import xyz.cofe.text.Text;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * События связанные соединением бд
 * @author Kamnev Georgiy
 */
public abstract class ConnectPoolEvent implements DataEvent
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ConnectPoolEvent.class.getName());

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
        logger.entering(ConnectPoolEvent.class.getName(), method, params);
    }
    
    private static void logExiting( String method){
        logger.exiting(ConnectPoolEvent.class.getName(), method);
    }
    
    private static void logExiting( String method, Object result){
        logger.exiting(ConnectPoolEvent.class.getName(), method, result);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="connectPool">
    private ConnectPool connectPool;
    
    public ConnectPool getConnectPool() {
        return connectPool;
    }
    
    public void setConnectPool( ConnectPool connectPool) {
        this.connectPool = connectPool;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructor">
    public ConnectPoolEvent( ConnectPool connectPool) {
        this.connectPool = connectPool;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Connected">
    public static class Connected extends ConnectPoolEvent {
        public Connected( ConnectPool connectPool) {
            super(connectPool);
        }
        
        //<editor-fold defaultstate="collapsed" desc="connection">
        private Connection connection;
        
        public Connection getConnection() {
            return connection;
        }
        
        public void setConnection( Connection connection) {
            this.connection = connection;
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="sourceConnection">
        private Connection sourceConnection;
        
        public Connection getSourceConnection() {
            return sourceConnection;
        }
        
        public void setSourceConnection( Connection sourceConnection) {
            this.sourceConnection = sourceConnection;
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="dataSource">
        private DataSource dataSource;
        
        public DataSource getDataSource() {
            return dataSource;
        }
        
        public void setDataSource( DataSource dataSource) {
            this.dataSource = dataSource;
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="dataSourceName">
        private String dataSourceName;
        
        public String getDataSourceName() {
            return dataSourceName;
        }
        
        public void setDataSourceName( String dataSourceName) {
            this.dataSourceName = dataSourceName;
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="proxy">
        private boolean proxy = false;
        
        public boolean isProxy() {
            return proxy;
        }
        
        public void setProxy(boolean proxy) {
            this.proxy = proxy;
        }
        //</editor-fold>

        @Override
        public String toString() {
            return "Connected{" + 
                "connectPool=" + getConnectPool() +
                ", connection=" + connection + 
                ", sourceConnection=" + sourceConnection + 
                ", proxy=" + proxy + 
                ", dataSource="+ dataSource +
                ", dataSourceName="+ dataSourceName +
                '}';
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Disconnected">
    public static class Disconnected extends ConnectPoolEvent {
        public Disconnected( ConnectPool connectPool) {
            super(connectPool);
        }
        
        //<editor-fold defaultstate="collapsed" desc="connection">
        private Connection connection;
        
        public Connection getConnection() {
            return connection;
        }
        
        public void setConnection( Connection connection) {
            this.connection = connection;
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="sourceConnection">
        private Connection sourceConnection;
        
        public Connection getSourceConnection() {
            return sourceConnection;
        }
        
        public void setSourceConnection( Connection sourceConnection) {
            this.sourceConnection = sourceConnection;
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="proxy">
        private boolean proxy = false;
        
        public boolean isProxy() {
            return proxy;
        }
        
        public void setProxy(boolean proxy) {
            this.proxy = proxy;
        }
        //</editor-fold>
        
        @Override
        public String toString() {
            return "Disconnected{" +
                "connectPool=" + getConnectPool() +
                ", connection=" + connection +
                ", sourceConnection=" + sourceConnection +
                ", proxy=" + proxy +
                '}';
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="StatementCreated">
    public static class StatementCreated extends ConnectPoolEvent {
        public StatementCreated( ConnectPool connectPool) {
            super(connectPool);
        }
        
        //<editor-fold defaultstate="collapsed" desc="method">
        protected Method method;
        public Method getMethod() { synchronized(this){ return method; } }
        public void setMethod( Method method) { synchronized(this){ this.method = method; } }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="arguments">
        protected Object[] arguments = new Object[0];
        public Object[] getArguments() {
            synchronized(this){ return arguments; }
        }
        public void setArguments( Object[] arguments) {
            synchronized(this){ this.arguments = arguments; }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="statement">
        protected Statement statement;
        
        public Statement getStatement() {
            synchronized(this){ return statement; }
        }
        
        public void setStatement( Statement statement) {
            synchronized(this){ this.statement = statement; }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="sourceStatement">
        protected Statement sourceStatement;
        
        public Statement getSourceStatement() {
            synchronized(this){ return sourceStatement; }
        }
        
        public void setSourceStatement( Statement sourceStatement) {
            synchronized(this){ this.sourceStatement = sourceStatement; }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="proxy">
        protected boolean proxy;
        
        public boolean isProxy() {
            synchronized(this){ return proxy; }
        }
        
        public void setProxy(boolean proxy) {
            synchronized(this){ this.proxy = proxy; }
        }
        //</editor-fold>
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("StatementCreated");
            sb.append("{");
            
            if( proxy ){
                sb.append("proxy ");
            }
            
            ArrayList<String> params = new ArrayList<>();
            if( statement!=null ){
                params.add("type="+statement.getClass().getName());
            }
            if( method!=null ){
                params.add("method="+method.getName());
            }
            if( arguments!=null && arguments.length>0 ){
                StringBuilder sArgs = new StringBuilder();
                int ai = -1;
                for( Object arg : arguments ){
                    ai++;
                    if( ai>0 ){
                        sArgs.append(", ");
                    }
                    sArgs.append(arg==null ? "null" : arg.toString());
                }
                sb.append("args=[").append(sArgs.toString()).append("]");
            }
            sb.append(Text.join(params, ", "));
            
            sb.append("}");
            return sb.toString();
        }
    }
    //</editor-fold>
}
