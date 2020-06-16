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

import xyz.cofe.collection.BasicEventList;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.ecolls.ReadWriteLockSupport;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Выполянее запрос и пишет данные в соответ приемник
 * @author Kamnev Georgiy
 */
public class QueryExecutor extends Thread implements ReadWriteLockSupport
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(QueryExecutor.class.getName());

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
        logger.entering(QueryExecutor.class.getName(), method, params);
    }
    
    private static void logExiting( String method){
        logger.exiting(QueryExecutor.class.getName(), method);
    }
    
    private static void logExiting( String method, Object result){
        logger.exiting(QueryExecutor.class.getName(), method, result);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="PropertyChangeSupport">
    protected final PropertyChangeSupport psupport;
    
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
    
    //<editor-fold defaultstate="collapsed" desc="event/listeners">
    //<editor-fold defaultstate="collapsed" desc="QueryExecutorEvent">
    public static class QueryExecutorEvent {
        public QueryExecutorEvent( QueryExecutor qe){
            this.queryExecutor = qe;
        }
        
        protected QueryExecutor queryExecutor;
        
        public QueryExecutor getQueryExecutor() {
            return queryExecutor;
        }
    }
    //</editor-fold>
    
    public static class ErrorCatched extends QueryExecutorEvent {
        public ErrorCatched( QueryExecutor qe, Throwable error) {
            super(qe);
            this.error = error;
        }
        
        protected Throwable error;

        public Throwable getError() {
            return error;
        }
    }
    
    public static class ResultSetAccepted extends QueryExecutorEvent {
        public ResultSetAccepted( QueryExecutor qe, ResultSet rs) {
            super(qe);
            this.resultSet = rs;
        }
        
        protected ResultSet resultSet;

        public ResultSet getResultSet() {
            return resultSet;
        }
    }
    
    public static class Started extends QueryExecutorEvent {
        public Started( QueryExecutor qe) {
            super(qe);
        }
    }

    public static class Finished extends QueryExecutorEvent {
        public Finished( QueryExecutor qe) {
            super(qe);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="listeners">
    public interface QueryExecutorListener {
        void queryExecutorEvent( QueryExecutorEvent ev );
    }

    protected final ListenersHelper<QueryExecutorListener,QueryExecutorEvent> listeners;
    
    public boolean hasQueryExecutorListener(QueryExecutorListener listener) {
        return listeners.hasListener(listener);
    }
    
    public Set<QueryExecutorListener> getQueryExecutorListeners() {
        return listeners.getListeners();
    }
    
    public AutoCloseable addQueryExecutorListener( QueryExecutorListener listener) {
        return listeners.addListener(listener);
    }
    
    public AutoCloseable addQueryExecutorListener( QueryExecutorListener listener, boolean weakLink) {
        return listeners.addListener(listener, weakLink);
    }
    
    public void removeQueryExecutorListener(QueryExecutorListener listener) {
        listeners.removeListener(listener);
    }
    
    public void fireQueryExecutorEvent(QueryExecutorEvent event) {
        listeners.fireEvent(event);
    }
    //</editor-fold>
    //</editor-fold>

    protected final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public ReadWriteLock getReadWriteLock(){ return readWriteLock; }

    /**
     * Конструктор
     * @param conn соединение с базой данных
     * @param query запрос
     */
    public QueryExecutor( Connection conn, String query ){
        if( conn==null )throw new IllegalArgumentException("conn == null");
        if( query==null )throw new IllegalArgumentException("query == null");
        
        listeners = new ListenersHelper<>( (QueryExecutorListener ls, QueryExecutorEvent ev) -> {
                if(ls!=null && ev!=null )ls.queryExecutorEvent(ev);
            }
        );
        
        psupport = new PropertyChangeSupport(this);
        
        this.connection = conn;
        this.query = query;
        
        setDaemon(true);
        setName("QueryExecutor");
    }
    
    //<editor-fold defaultstate="collapsed" desc="connection : Connection">
    protected Connection connection;
    
    /**
     * Указывает соединение с БД
     * @return Соединение с БД
     */
    public Connection getConnection() {
        return readLock(()->connection);
    }
    
    /**
     * Указывает соединение с БД
     * @param connection Соединение с БД
     */
    public void setConnection( Connection connection) {
        Object[] old_cur = new Object[2];
        writeLock(()->{
            old_cur[0] = this.connection;
            this.connection = connection;
            old_cur[1] = this.connection;
        });
        firePropertyChange("connection", old_cur[0], old_cur[1]);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="query : String">
    protected volatile String query;
    
    /**
     * Указывает запрос к БД
     * @return запрос к БД
     */
    public String getQuery() {
        return readLock( ()->query );
    }
    
    /**
     * Указывает запрос к БД
     * @param query запрос к БД
     */
    public void setQuery( String query) {
        Object[] old_cur = new Object[2];
        writeLock(()->{
            old_cur[0] = this.query;
            this.query = query;
            old_cur[1] = this.query;
        });
        firePropertyChange("query", old_cur[0], old_cur[1]);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="params">
    protected List params;
    
    /**
     * Указывает параметры запроса
     * @return параметры запроса
     */
    public List getParams(){
        synchronized(this){
            if( params!=null )return params;
            params = new BasicEventList(new ArrayList(), readWriteLock);
            return params;
        }
    }
    
    /**
     * Указывает параметры запроса. 
     * <p>
     * Актульано при автоматическом создании запроса
     * @param params параметры запроса
     * @see #getStatement() 
     */
    public void setParams( List params){
        synchronized(this){
            this.params = params;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="statement : Statement">
    protected Statement statement;
    
    /**
     * Указывает объект запроса.
     * 
     * <p>
     * Создается автоматически (если не создан) ври выполнеии run().
     * и если надо, то добавляются параметры запроса
     * @return объект заапроса
     */
    public Statement getStatement() {
        synchronized(this){ return statement; }
    }
    
    /**
     * Указывает объект запроса.
     * @param statement запрос
     */
    public void setStatement( Statement statement) {
        Object[] old_cur = new Object[2];
        synchronized(this){
            old_cur[0] = this.statement;
            this.statement = statement;
            old_cur[1] = this.statement;
        }
        firePropertyChange("statement", old_cur[0], old_cur[1]);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="closeStatement : boolean = true">
    protected boolean closeStatement = true;
    
    /**
     * Указывает при завершении запроса - закрывать Statement
     * @return true (по умолчанию) - при завершении закрывать statement
     */
    public boolean isCloseStatement() {
        synchronized(this) { return closeStatement; }
    }
    
    /**
     * Указывает при завершении запроса - закрывать Statement
     * @param closeStatement true (по умолчанию) - при завершении закрывать statement
     */
    public void setCloseStatement(boolean closeStatement) {
        Object[] old_cur = new Object[2];
        synchronized(this){
            old_cur[0] = this.closeStatement;
            this.closeStatement = closeStatement;
            old_cur[1] = this.closeStatement;
        }
        firePropertyChange("closeStatement", old_cur[0], old_cur[1]);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="closeResultSet : boolean">
    protected boolean closeResultSet = true;
    
    /**
     * Указывает закрывать resultSet при завершении чтения из него
     * @return true (по умолчанию) - закрывать resultSet
     */
    public boolean isCloseResultSet() {
        synchronized(this){
            return closeResultSet;
        }
    }
    
    /**
     * Указывает закрывать resultSet при завершении чтения из него
     * @param closeResultSet true (по умолчанию) - закрывать resultSet
     */
    public void setCloseResultSet(boolean closeResultSet) {
        Object[] old_cur = new Object[2];
        synchronized(this){
            old_cur[0] = this.closeResultSet;
            this.closeResultSet = closeResultSet;
            old_cur[1] = this.closeResultSet;
        }
        firePropertyChange("closeResultSet", old_cur[0], old_cur[1]);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="resultSetType : ResultSetType">
    /**
     * Направление выборки / Изменяемая или нет выборка
     */
    public static enum ResultSetType {
        /**
         * Выборка только вперед
         */
        ForwardOnly,
        
        /**
         * Выборка может вперед и назад, но не чувствитеьлная к изменениям в ResultSet
         */
        ScrollInsensitive,
        
        /**
         * Выборка может вперед и назад, чувствитеьлная к изменениям в ResultSet
         */
        ScrollSensitive;
        int value(){
            switch(this){
                case ForwardOnly: return ResultSet.TYPE_FORWARD_ONLY;
                case ScrollInsensitive: return ResultSet.TYPE_SCROLL_INSENSITIVE;
                case ScrollSensitive: return ResultSet.TYPE_SCROLL_SENSITIVE;
            }
            return ResultSet.TYPE_FORWARD_ONLY;
        }
    }
    
    protected ResultSetType resultSetType;
    
    /**
     * Указывает тип выборки
     * @return тип выборки
     */
    public ResultSetType getResultSetType() {
        synchronized(this){ return resultSetType; }
    }
    
    /**
     * Указывает тип выборки
     * @param resultSetType тип выборки
     */
    public void setResultSetType(ResultSetType resultSetType) {
        Object[] old_cur = new Object[2];
        synchronized(this){
            old_cur[0] = this.resultSetType;
            this.resultSetType = resultSetType;
            old_cur[1] = this.resultSetType;
        }
        firePropertyChange("rsType", old_cur[0], old_cur[1]);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="concurrency : Concurrency">
    /**
     * Тип данных в выборке read-only / read-write
     */
    public static enum Concurrency {
        /**
         * Данные в ResultSet только для чтения
         */
        ReadOnly,
        
        /**
         * Данные в ResultSet для чтения/записи
         */
        Updatable;
        int value(){
            switch(this){
                case ReadOnly: return ResultSet.CONCUR_READ_ONLY;
                case Updatable: return ResultSet.CONCUR_UPDATABLE;
            }
            return ResultSet.CONCUR_READ_ONLY;
        }
    }
    
    protected Concurrency concurrency;
    
    /**
     * Указывает тип данных read-only/read-write в ResultSet
     * @return тип данных read-only/read-write
     */
    public Concurrency getConcurrency() {
        synchronized(this){
            return concurrency;
        }
    }
    
    /**
     * Указывает тип данных read-only/read-write в ResultSet
     * @param concurrency тип данных read-only/read-write
     */
    public void setConcurrency(Concurrency concurrency) {
        Object[] old_cur = new Object[2];
        synchronized(this){
            old_cur[0] = this.concurrency;
            this.concurrency = concurrency;
            old_cur[1] = this.concurrency;
        }
        firePropertyChange("concurrency", old_cur[0], old_cur[1]);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="holdability : Holdability">
    /**
     * Доступность данных в ResultSet
     */
    public static enum Holdability {
        /**
         * Данные будут доступны после commit
         */
        HoldCursorOverCommit,
        
        /**
         * Данные не будут доступны после закрытия транзакции (commit)
         */
        CloseCursorsAtCommit;
        int value(){
            switch(this){
                case HoldCursorOverCommit: return ResultSet.HOLD_CURSORS_OVER_COMMIT;
                case CloseCursorsAtCommit: return ResultSet.CLOSE_CURSORS_AT_COMMIT;
            }
            return ResultSet.HOLD_CURSORS_OVER_COMMIT;
        }
    }
    
    protected Holdability holdability;
    
    /**
     * Указывает будут или нет доступны данные после закрытия транзакции
     * @return доступность данных
     */
    public Holdability getHoldability() {
        synchronized(this){
            return holdability;
        }
    }
    
    /**
     * Указывает будут или нет доступны данные после закрытия транзакции
     * @param holdability доступность данных
     */
    public void setHoldability(Holdability holdability) {
        Object[] old_cur = new Object[2];
        synchronized(this){
            old_cur[0] = this.holdability;
            this.holdability = holdability;
            old_cur[1] = this.holdability;
        }
        firePropertyChange("holdability", old_cur[0], old_cur[1]);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="messageReader : MessageReader">        
    protected MessageReader messageReader;
    
    /**
     * Указыавает куда передавать текстовые сообщения
     * @return приемник текстовых сообщений
     */
    public MessageReader getMessageReader() {
        synchronized(this){
            return messageReader;
        }
    }
    
    /**
     * Указыавает куда передавать текстовые сообщения
     * @param messageReader приемник текстовых сообщений
     */
    protected void setMessageReader( MessageReader messageReader) {
        Object[] old_cur = new Object[2];
        synchronized(this){
            old_cur[0] = this.messageReader;
            this.messageReader = messageReader;
            old_cur[1] = this.messageReader;
        }
        firePropertyChange("messageReader", old_cur[0], old_cur[1]);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="fetchGeneratedKeys : Boolean">
    protected Boolean fetchGeneratedKeys = false;
    
    /**
     * Указывает возвращаеть (или нет) сгенерированные ключи
     * @return true - возвращает сгенерированные ключи
     */
    public Boolean getFetchGeneratedKeys() {
        synchronized(this){
            return fetchGeneratedKeys;
        }
    }
    
    /**
     * Указывает возвращаеть (или нет) сгенерированные ключи
     * @param fetchGeneratedKeys true - возвращает сгенерированные ключи
     */
    public void setFetchGeneratedKeys( Boolean fetchGeneratedKeys) {
        synchronized(this){
            this.fetchGeneratedKeys = fetchGeneratedKeys;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="fetchGenKeysFirst : Boolean">
    protected boolean fetchGenKeysFirst = true;
    
    /**
     * Указывает производить выборку сгененированных ключей перед выборкой данных. <p>
     * Значение актуально для mssql драйвера.
     * @return true (по умолчанию) - сначала выборка ключей, потом данные / <br>
     * false - сначала данные, потом ключи
     */
    public boolean isFetchGenKeysFirst() {
        synchronized(this){ return fetchGenKeysFirst; }
    }
    
    /**
     * Указывает производить выборку сгененированных ключей перед выборкой данных
     * @param fetchGenKeysFirst true (по умолчанию) - сначала выборка ключей, потом данные / <br>
     * false - сначала данные, потом ключи
     * @see #isFetchGenKeysFirst() 
     */
    public void setFetchGenKeysFirst(boolean fetchGenKeysFirst) {
        synchronized(this){ this.fetchGenKeysFirst = fetchGenKeysFirst; }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="skipGenKeysException : boolean = false">
    protected boolean skipGenKeysException = false;
    
    /**
     * Пропускать или нет ошибки при чтении сгенерированных ключей. <p>
     * Значение актуально для mssql драйвера.
     * @return false (по умолчанию) - генерировать ошибки / <br>
     * false - пропускать ошибки
     */
    public boolean isSkipGenKeysException() {
        synchronized(this){ return skipGenKeysException; }
    }
    
    /**
     * Пропускать или нет ошибки при чтении сгенерированных ключей. <p>
     * Значение актуально для mssql драйвера.
     * @param skipGenKeysException false (по умолчанию) - генерировать ошибки / <br>
     * false - пропускать ошибки
     * @see #isFetchGenKeysFirst() 
     */
    public void setSkipGenKeysException(boolean skipGenKeysException) {
        synchronized(this){ this.skipGenKeysException = skipGenKeysException; }
    }
    //</editor-fold>
    
    protected MessageReader createMessageReader( Statement st, Consumer<SQLWarning> consumer){
        if( st == null )throw new IllegalArgumentException("st == null");
        if( consumer == null )throw new IllegalArgumentException("consumer == null");
        return new MessageReader(st, consumer);
    }
    
    //<editor-fold defaultstate="collapsed" desc="resultWriter">
    protected QueryWriter resultWriter;
    
    /**
     * Указывает куда передавать данные
     * @return приемник данных
     */
    public QueryWriter getResultWriter() {
        synchronized(this){
            return resultWriter;
        }
    }
    
    /**
     * Указывает куда передавать данные
     * @param resultWriter приемник данных
     */
    public void setResultWriter(QueryWriter resultWriter) {
        Object[] old_cur = new Object[2];
        synchronized(this){
            old_cur[0] = this.resultWriter;
            this.resultWriter = resultWriter;
            old_cur[1] = this.resultWriter;
        }
        firePropertyChange("resultWriter", old_cur[0], old_cur[1]);
    }
    //</editor-fold>

    /**
     * Метод выполнения
     */
    public static enum ExecMethod {
        Execute,
        ExecuteUpdate,
        ExecuteQuery
    }
    
    protected volatile ExecMethod execMethod = ExecMethod.Execute;
    
    /**
     * Указывает метод выполнения запроса
     * @return метод выполнения запроса
     */
    public synchronized ExecMethod getExecMethod(){
        if( execMethod==null )execMethod = ExecMethod.Execute;
        return execMethod;
    }
    
    /**
     * Указывает метод выполнения запроса
     * @param execMethod метод
     */
    public synchronized void setExecMethod(ExecMethod execMethod){
        this.execMethod = execMethod;
    }
    
    //<editor-fold defaultstate="collapsed" desc="run() - main cycle">
    @Override
    public void run() {
        Connection cn = null;
        String sqlq = null;
        boolean clStmnt = true;
        boolean clRsSet = true;
        boolean startConsumerBeforeExecute = true;            
        
        ResultSetType rstype = null;
        Concurrency concur = null;
        Holdability hld = null;
        //Reciver<SQLWarning> messageConsumer = null;
        //Reciver<DataTable> genKeyCons = null;
        List params = null;
        final QueryWriter qrWriter;
        Boolean fetchGeneratedKeys = null;
        boolean fetchGenKeysFirst = false;
        boolean skipGenKeysException = false;
        ExecMethod emethod = ExecMethod.Execute;
        
        synchronized(this){
            cn = getConnection();
            sqlq = getQuery();
            clStmnt = isCloseStatement();
            clRsSet = isCloseResultSet();
            rstype = getResultSetType();
            concur = getConcurrency();
            hld = getHoldability();
            //messageConsumer = getMessageConsumer();
            params = this.params;
            //genKeyCons = getGeneratedKeysConsumer();
            qrWriter = getResultWriter();
            fetchGeneratedKeys = getFetchGeneratedKeys();
            fetchGenKeysFirst = isFetchGenKeysFirst();
            skipGenKeysException = isSkipGenKeysException();
            emethod = getExecMethod();
        }
        
        try {
            fireQueryExecutorEvent(new Started(this));
            qrWriter.begin();

            if( cn==null ){
                logWarning("connection not set");
                fireQueryExecutorEvent(new Finished(this));
                qrWriter.end();
                return;
            }

            if( sqlq==null ){
                logWarning("query not set");
                fireQueryExecutorEvent(new Finished(this));
                qrWriter.end();
                return;
            }

            Statement st = getStatement();
            //<editor-fold defaultstate="collapsed" desc="create statement">
            if( st==null ){
                try {
                    logFiner("create Statement");

                    if( params==null || params.isEmpty() ){
                        if( rstype!=null && concur!=null && hld!=null ){
                            st = cn.createStatement(rstype.value(), concur.value(), hld.value());
                            //fetchGeneratedKeys = true;
                        }else if( rstype!=null && concur!=null && hld==null ){
                            st = cn.createStatement(rstype.value(), concur.value());
                            //fetchGeneratedKeys = true;
                        }else{
                            st = cn.createStatement();
                            //fetchGeneratedKeys = true;
                        }
                    }else{
                        if( rstype!=null && concur!=null && hld!=null ){
                            st = cn.prepareStatement(sqlq,rstype.value(),concur.value(),hld.value());
                            //fetchGeneratedKeys = true;
                        }else if( rstype!=null && concur!=null && hld==null ){
                            st = cn.prepareStatement(sqlq,rstype.value(),concur.value());
                            //fetchGeneratedKeys = true;
                        }else{
                            if( fetchGeneratedKeys!=null ){
                                if( fetchGeneratedKeys ){
                                    st = cn.prepareStatement(sqlq, Statement.RETURN_GENERATED_KEYS);
                                }else{
                                    st = cn.prepareStatement(sqlq, Statement.NO_GENERATED_KEYS);
                                }
                            }else{
                                st = cn.prepareStatement(sqlq, Statement.RETURN_GENERATED_KEYS);
                                if( fetchGeneratedKeys==null )fetchGeneratedKeys = true;
                            }
                        }

                        PreparedStatement pst = (PreparedStatement)st;
                        for( int pi=0; pi<params.size(); pi++ ){
                            pst.setObject(pi+1, params.get(pi));
                        }
                    }

                    setStatement(st);
                } catch ( SQLException ex) {
                    Logger.getLogger(QueryExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    fireQueryExecutorEvent(new ErrorCatched(this, ex));
                    fireQueryExecutorEvent(new Finished(this));
                    return;
                }
            }
            //</editor-fold>
            
            MessageReader mreader = null;

            try {
                //<editor-fold defaultstate="collapsed" desc="start message reader async">
                if( startConsumerBeforeExecute ){
                    logFiner( "create MessageReader thread" );
                    mreader = createMessageReader(st, new Consumer<SQLWarning>() {
                        @Override
                        public void accept( SQLWarning obj) {
                            qrWriter.writeMessage(obj);
                        }
                    });
                    String selfName = getName();
                    String readerName =
                        (selfName!=null
                        ? selfName
                        : QueryExecutor.class.getSimpleName()
                        ) + "#" + MessageReader.class.getSimpleName();
                    mreader.setName(readerName);
                    setMessageReader(mreader);

                    logFiner( "start MessageReader \"{0}\"",readerName );
                    mreader.start();
                }
                //</editor-fold>
                
                ResultSet execQueryRS = null;
                Integer execUpdateCnt = null;

                //<editor-fold defaultstate="collapsed" desc="execute statement">
                logFiner("execute {0}",sqlq);
                if( st instanceof PreparedStatement ){
                    switch(emethod){
                        case Execute: 
                            ((PreparedStatement)st).execute();
                            break;
                        case ExecuteQuery: 
                            execQueryRS = ((PreparedStatement)st).executeQuery();
                            break;
                        case ExecuteUpdate: 
                            execUpdateCnt = ((PreparedStatement)st).executeUpdate();
                            break;
                    }
                    //((PreparedStatement)st).execute();
                }else{
                    int autoGenKeys = Statement.NO_GENERATED_KEYS;
                    
                    if( fetchGeneratedKeys!=null ){
                        autoGenKeys = fetchGeneratedKeys ? 
                            Statement.RETURN_GENERATED_KEYS :
                            Statement.NO_GENERATED_KEYS;
                    }else{
                        autoGenKeys = Statement.NO_GENERATED_KEYS;
                        fetchGeneratedKeys = false;
                    }
                    
                    switch(emethod){
                        case Execute: 
                            st.execute(sqlq,autoGenKeys);
                            break;
                        case ExecuteQuery: 
                            execQueryRS = st.executeQuery(sqlq);
                            break;
                        case ExecuteUpdate: 
                            execUpdateCnt = st.executeUpdate(sqlq,autoGenKeys);
                            break;
                    }
                }
                //</editor-fold>
                
                //<editor-fold defaultstate="collapsed" desc="write generated keys">
                try{
                    if( Objects.equals(fetchGeneratedKeys,true) && fetchGenKeysFirst ){
                        ResultSet rsGenKeys = st.getGeneratedKeys();
                        if( rsGenKeys!=null ){
                            qrWriter.writeGeneratedKeys(rsGenKeys);
                        }
                    }
                }catch( SQLException ex){
                    if( !skipGenKeysException ){
                        Logger.getLogger(QueryExecutor.class.getName()).log(Level.FINE, null, ex);
                        fireQueryExecutorEvent(new ErrorCatched(this, ex));
                        qrWriter.writeError(ex);
                    }else{
                        Logger.getLogger(QueryExecutor.class.getName()).log(Level.FINER, null, ex);
                    }
                }
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="start message reader async">
                if( !startConsumerBeforeExecute ){
                    logFiner( "create MessageReader thread" );
                    mreader = createMessageReader(st, new Consumer<SQLWarning>() {
                        @Override
                        public void accept( SQLWarning obj) {
                            qrWriter.writeMessage(obj);
                        }
                    });
                    String selfName = getName();
                    String readerName =
                        (selfName!=null
                        ? selfName
                        : QueryExecutor.class.getSimpleName()
                        ) + "#" + MessageReader.class.getSimpleName();
                    mreader.setName(readerName);
                    setMessageReader(mreader);

                    logFiner( "start MessageReader \"{0}\"",readerName );
                    mreader.start();
                }
                //</editor-fold>
                
                int resultSetIndex = -1;
                if( emethod== ExecMethod.Execute ){
                    while( true ){
                        if( Thread.currentThread().isInterrupted() ){
                            logFine("break by interrupt");
                            break;
                        }

                        int rowCount = st.getUpdateCount();
                        if( rowCount!=-1 )qrWriter.writeUpdateCount(rowCount);

                        if( rowCount>0 ){ //  это счетчик обновлений
                            st.getMoreResults();
                            logFiner("get next resultSet by update counter");
                            continue;
                        }

                        if( rowCount==0 ){ // команда DDL или 0 обновлений
                            st.getMoreResults();
                            logFiner("get next resultSet by DDL/0 row count");
                            continue;
                        }

                        ResultSet rs = st.getResultSet();
                        if( rs==null ){
                            logFiner("break - no resultSet");
                            break;
                        }
                        resultSetIndex++;

                        logFiner("fire ResultSetAccepted");
                        fireQueryExecutorEvent(new ResultSetAccepted(this, rs));

                        qrWriter.writeResultSet(rs, resultSetIndex);

                        if( clRsSet && rs!=null ){
                            logFiner("close resultSet");
                            rs.close();
                        }

                        if( Thread.interrupted() ){
                            logFine("break by interrupt");
                            break;
                        }

                        int moreRS = clRsSet ? Statement.CLOSE_CURRENT_RESULT : Statement.KEEP_CURRENT_RESULT;
                        logFiner("getMoreResults({0}) resultSet", 
                            clRsSet ? "CLOSE_CURRENT_RESULT" : "KEEP_CURRENT_RESULT"
                        );
                        st.getMoreResults(moreRS);
                    }
                }
                
                if( execQueryRS!=null ){
                    resultSetIndex++;
                    
                    logFiner("fire ResultSetAccepted");
                    fireQueryExecutorEvent(new ResultSetAccepted(this, execQueryRS));

                    qrWriter.writeResultSet(execQueryRS, resultSetIndex);

//                    if( execQueryRS!=null && !execQueryRS.isClosed() && clRsSet ){
//                        execQueryRS.close();
//                    }
                }
                
                if( execUpdateCnt!=null ){
                    if( execUpdateCnt!=-1 )qrWriter.writeUpdateCount(execUpdateCnt);
                }
                
                //<editor-fold defaultstate="collapsed" desc="write generated keys">
                try{
                    if( Objects.equals(fetchGeneratedKeys,true) && !fetchGenKeysFirst  ){
                        ResultSet rsGenKeys = null;
                        rsGenKeys = st.getGeneratedKeys();
                        if( rsGenKeys!=null ){
                            qrWriter.writeGeneratedKeys(rsGenKeys);
                        }
                    }
                }catch( SQLException ex){
                    if( !skipGenKeysException ){
                        Logger.getLogger(QueryExecutor.class.getName()).log(Level.FINE, null, ex);
                        fireQueryExecutorEvent(new ErrorCatched(this, ex));
                        qrWriter.writeError(ex);
                    }else{
                        Logger.getLogger(QueryExecutor.class.getName()).log(Level.FINER, null, ex);
                    }
                }
                //</editor-fold>
                
                if( execQueryRS!=null && !execQueryRS.isClosed() && clRsSet ){
                    execQueryRS.close();
                }
                
//                if( st!=null && !st.isClosed() && clStmnt ){
//                    st.close();
//                }
            } catch ( SQLException ex) {
                Logger.getLogger(QueryExecutor.class.getName()).log(Level.SEVERE, null, ex);
                fireQueryExecutorEvent(new ErrorCatched(this, ex));
                qrWriter.writeError(ex);
            }

            //<editor-fold defaultstate="collapsed" desc="close statement">
            if( st!=null && clStmnt ){
                try {
                    logFine("close statement {0}",st.toString());
                    st.close();
                } catch ( SQLException ex) {
                    Logger.getLogger(QueryExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    fireQueryExecutorEvent(new ErrorCatched(this, ex));
                    qrWriter.writeError(ex);
                }
            }
            //</editor-fold>
        }
        finally{
            fireQueryExecutorEvent(new Finished(this));
            qrWriter.end();
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="terminate()">
    /**
     * Остановка выполнения. <p>
     * Нельзя вызывать только в самом же треде.
     * @param withReader true - Остановить также MessageReader
     * @param timeout макс. время (мс) за которое должна произойти остановка
     * @param sleep время (мс) паузы, при значении &lt; 0 - передает управление другому потоку Thread.yield()
     */
    public void terminate( boolean withReader, long timeout, long sleep ){
        terminate(timeout, sleep);
        
        if( withReader ){            
            MessageReader mr = getMessageReader();
            if( mr!=null ){
                mr.terminate(timeout, sleep);
            }
        }
    }
    
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
    //</editor-fold>
}
