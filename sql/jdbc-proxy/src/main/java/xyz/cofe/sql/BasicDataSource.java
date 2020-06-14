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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xyz.cofe.xml.FormatXMLWriter;
import xyz.cofe.xml.XmlUtil;
import xyz.cofe.xml.stream.path.PathMatch;
import xyz.cofe.xml.stream.path.XEventPath;
import xyz.cofe.xml.stream.path.XVisitorAdapter;
import xyz.cofe.xml.stream.path.XmlReader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Описание соединения с СУБД.
 * 
 * <p>
 * Формат XML:
 * <pre>
&lt;dataSource type="xyz.cofe.sql.BasicDataSource"&gt;
    <i style="color:#888888">&lt;!-- Ссылка на соединение с БД (JDBC connect string) --&gt;</i>
    &lt;url&gt;jdbc:mysql://127.0.0.1/database_name&lt;/url&gt;
    
    <i style="color:#888888">&lt;!-- время таймаута (сек) на соединения с базой данных --&gt;</i>
    &lt;loginTimeout&gt;60&lt;/loginTimeout&gt;
    &lt;networkTimeoutDaemon&gt;true&lt;/networkTimeoutDaemon&gt;
    &lt;networkTimeoutStatic&gt;true&lt;/networkTimeoutStatic&gt;
    &lt;networkTimeoutThreadName&gt;DataSource network timeout&lt;/networkTimeoutThreadName&gt;
    &lt;networkTimeoutThreads&gt;1&lt;/networkTimeoutThreads&gt;
    &lt;properties&gt;
        <i style="color:#888888">&lt;!-- логин --&gt;</i>
        &lt;property&gt;
            &lt;key&gt;user&lt;/key&gt;
            &lt;value&gt;login&lt;/value&gt;
        &lt;/property&gt;
        
        <i style="color:#888888">&lt;!-- пароль --&gt;</i>
        &lt;property&gt;
            &lt;key&gt;password&lt;/key&gt;
            &lt;value&gt;password&lt;/value&gt;
        &lt;/property&gt;
    &lt;/properties&gt;
    &lt;options&gt;
        &lt;property&gt;
            &lt;key&gt;proxyConnection&lt;/key&gt;
            &lt;value&gt;true&lt;/value&gt;
        &lt;/property&gt;
        &lt;property&gt;
            &lt;key&gt;collectActivityStat&lt;/key&gt;
            &lt;value&gt;true&lt;/value&gt;
        &lt;/property&gt;
        &lt;property&gt;
            &lt;key&gt;shared&lt;/key&gt;
            &lt;value&gt;true&lt;/value&gt;
        &lt;/property&gt;
        &lt;property&gt;
            &lt;key&gt;idleTimeout&lt;/key&gt;
            &lt;value&gt;5000&lt;/value&gt;
        &lt;/property&gt;
    &lt;/options&gt;
&lt;/dataSource&gt;
 * </pre>
 * @author Kamnev Georgiy
 */
public class BasicDataSource extends SimpleDataSource implements ConnectOptions
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private transient static final Logger logger = Logger.getLogger(BasicDataSource.class.getName());

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
        logger.entering(BasicDataSource.class.getName(), method, params);
    }
    
    private static void logExiting( String method){
        logger.exiting(BasicDataSource.class.getName(), method);
    }
    
    private static void logExiting( String method, Object result){
        logger.exiting(BasicDataSource.class.getName(), method, result);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="constructors">
    public BasicDataSource() {
    }
    
    public BasicDataSource( String url) {
        super(url);
    }
    
    public BasicDataSource( String url, String username, String password) {
        super(url, username, password);
    }
    
    public BasicDataSource( String url, Properties props) {
        super(url, props);
    }
    
    public BasicDataSource( BasicDataSource sample ){
        super(sample);
        if( sample!=null ){
            copts = new ConnectOptionsProperties(new LinkedHashMap<String, String>(),getReadWriteLock());
            copts.putAll(sample.getConnectOptions());
            
            autoCommit = sample.autoCommit;
            networkTimeout = sample.networkTimeout;
            networkTimeoutDaemon = sample.networkTimeoutDaemon;
            networkTimeoutStatic = sample.networkTimeoutStatic;
            networkTimeoutThreadName = sample.networkTimeoutThreadName;
            networkTimeoutThreads = sample.networkTimeoutThreads;
            schema = sample.schema;
            transactIsolation = sample.transactIsolation;
        }
    }
    
    @Override
    public BasicDataSource clone() {
        return new BasicDataSource(this);
    }
    
    @Override
    public void assign( SimpleDataSource ds ){
        super.assign(ds);
        if( ds!=null && ds instanceof BasicDataSource ){
            synchronized(this){
                synchronized(ds){
                    BasicDataSource sample = (BasicDataSource)ds;
                    if( copts==null ){
                        copts = new ConnectOptionsProperties();
                    }else{
                        copts.clear();
                    }
                    copts.putAll(sample.getConnectOptions());

                    autoCommit = sample.autoCommit;
                    networkTimeout = sample.networkTimeout;
                    networkTimeoutDaemon = sample.networkTimeoutDaemon;
                    networkTimeoutStatic = sample.networkTimeoutStatic;
                    networkTimeoutThreadName = sample.networkTimeoutThreadName;
                    networkTimeoutThreads = sample.networkTimeoutThreads;
                    schema = sample.schema;
                    transactIsolation = sample.transactIsolation;
                }
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="copts : ConnectOptionsProperties">
    private transient volatile ConnectOptionsProperties copts;
    
    /**
     * Указывает опции соединения
     * @return опции соединения
     */
    //@UiBean(forceHidden = true)
    public ConnectOptionsProperties getConnectOptions(){
        if( copts!=null )return copts;
        synchronized(this){
            if( copts!=null )return copts;
            copts = new ConnectOptionsProperties(new LinkedHashMap<String, String>(),this.readWriteLock);
            copts.setProxyConnection(true);
            copts.setCollectActivityStat(true);
            copts.onChanged( (String key, String old, String cur) -> {
                if( key!=null ){
                    switch(key){
                        case "proxyConnection":
                        case "collectActivityStat":
                        case "idleTimeout":
                        case "statementIdleTimeout":
                            firePropertyChange(key, old, cur);
                            firePropertyChange("connectOptions."+key, old, cur);
                            break;
                        default:
                            firePropertyChange("connectOptions."+key, old, cur);
                    }
                }
            });
            return copts;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="proxyConnection : boolean">
    /**
     * Создавать прокси соединение
     * @return true - создавать прокси
     */
    @Override
    public boolean isProxyConnection() {
        return getConnectOptions().isProxyConnection();
    }
    
    /**
     * Создавать прокси соединение
     * @param v true - создавать прокси
     */
    public void setProxyConnection(boolean v) {
        getConnectOptions().setProxyConnection(v);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="collectActivityStat : boolean">
    /**
     * Собирать статистику активности
     * @return собирать статистику активности
     */
    @Override
    public boolean isCollectActivityStat() {
        return getConnectOptions().isCollectActivityStat();
    }
    
    /**
     * Собирать статистику активности
     * @param v true - собирать статистику активности
     */
    public void setCollectActivityStat(boolean v) {
        getConnectOptions().setCollectActivityStat(v);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="idleTimeout : long">
    
    /**
     * Указывает время (мс) простоя соединения, после которого соединение будет закрыто.
     * 
     * <p>
     * значение -1 и меньше - не закрывать по истечению таймаута
     * @return Время простоя 
     */
    @Override
    public long getIdleTimeout() {
        return getConnectOptions().getIdleTimeout();
    }
    
    /**
     * Указывает время (мс) простоя соединения, после которого соединение будет закрыто.
     * 
     * <p>
     * значение -1 и меньше - не закрывать по истечению таймаута
     * @param ms время (мс) простоя соединения
     */
    public void setIdleTimeout(long ms) {
        getConnectOptions().setIdleTimeout(ms);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="statementIdleTimeout : long">
    /**
     * Указывает время (мс) простоя запроса,
     * после которого запрос будет закрыт.
     * 
     * <p>
     * значение -1 и меньше - не закрывать по истечению таймаута
     * @return Время (мс) простоя
     */
    @Override
    public long getStatementIdleTimeout() {
        return getConnectOptions().getStatementIdleTimeout();
    }
    
    /**
     * Указывает время (мс) простоя запроса,
     * после которого запрос будет закрыт.
     * 
     * <p>
     * значение -1 и меньше - не закрывать по истечению таймаута
     * @param ms Время (мс) простоя
     */
    public void setStatementIdleTimeout(long ms) {
        getConnectOptions().setStatementIdleTimeout(ms);
    }
    //</editor-fold>

    @Override
    public boolean isShared() {
        return getConnectOptions().isShared();
    }

    public void setShared(boolean v) {
        getConnectOptions().setShared(v);
    }
    
    //<editor-fold defaultstate="collapsed" desc="getConnection()">
//    @UiBean(forceHidden = true)
    @Override
    public Connection getConnection( String username, String password) throws SQLException{
        synchronized(this){
            Connection conn = super.getConnection(username, password);
            configureConnection(conn);
            return conn;
        }
    }
    
//    @UiBean(forceHidden = true)
    @Override
    public Connection getConnection() throws SQLException{
        synchronized(this){
            Connection conn = super.getConnection();
            configureConnection(conn);
            return conn;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="autoCommit : Boolean">
    protected Boolean autoCommit;
    
    /**
     * Указывает автоматическое подтверждение транзакций
     * @return true - автоматическое подтверждение транзакций
     */
    public Boolean getAutoCommit() {
        synchronized(this){ return autoCommit; }
    }
    
    /**
     * Указывает автоматическое подтверждение транзакций
     * @param autoCommit true - автоматическое подтверждение транзакций
     */
    public void setAutoCommit( Boolean autoCommit) {
        Object old,cur;
        synchronized(this){ 
            old = this.autoCommit;
            this.autoCommit = autoCommit; 
            cur = this.autoCommit;
        }
        firePropertyChange("autoCommit", old, cur);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="transactIsolation : TransactIsolation">
    
    /**
     * Режим изоляции транзакций
     */
    protected TransactIsolation transactIsolation;
    
    /**
     * Указывает режим изоляции транзакций
     * @return режим изоляции транзакций
     */
    public TransactIsolation getTransactIsolation() {
        synchronized(this){
            return transactIsolation;
        }
    }
    
    /**
     * Указывает режим изоляции транзакций
     * @param transactIsolation режим изоляции транзакций
     */
    public void setTransactIsolation(TransactIsolation transactIsolation) {
        Object old,cur;
        synchronized(this){
            old = this.transactIsolation;
            this.transactIsolation = transactIsolation;
            cur = this.transactIsolation;
        }
        firePropertyChange("transactIsolation", old, cur);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="schema : String">
    protected String schema;
    
    /**
     * Указывают текущую схему при установке соединения
     * @return имя схемы
     */
    public String getSchema() {
        synchronized(this){ return schema; }
    }
    
    /**
     * Указывают текущую схему при установке соединения
     * @param schema имя схемы
     */
    public void setSchema( String schema) {
        Object old,cur;
        synchronized(this){
            old = this.schema;
            this.schema = schema;
            cur = this.schema;
        }
        firePropertyChange("schema", old, cur);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="networkTimeout : Integer">
    protected Integer networkTimeout;
    
    /**
     * Устанавливает максимальный период, когда соединение или объекты, 
     * созданные из Connection, будут ждать, пока база данных ответит на любой запрос. 
     * 
     * <p>
     * Если какой-либо запрос остается без ответа, метод ожидания вернется 
     * с помощью SQLException, 
     * а Connection или объекты, созданные из Connection, 
     * будут отмечены как закрытые. 
     * Любое последующее использование объектов, за исключением методов close, isClosed или Connection.isValid, 
     * приведет к SQLException.
     * 
     * <p>
     * Примечание. Этот метод предназначен для решения редкого, 
     * но серьезного состояния, при котором сетевые разделы могут вызывать потоки, 
     * выдающие вызовы JDBC, для непрерывного зависания в сокетах, 
     * до OS TCP-TIMEOUT (обычно 10 минут). Этот метод связан с методом abort (), 
     * который предоставляет потоку администратора средство для освобождения таких потоков в случаях, 
     * когда соединение JDBC доступно для потока администратора. Метод setNetworkTimeout будет охватывать случаи, 
     * когда нет потока администратора или он не имеет доступа к соединению. 
     * Этот метод является серьезным в его эффектах и ​​
     * должен иметь достаточно высокое значение, поэтому он никогда не запускается 
     * до каких-либо более обычных тайм-аутов, таких как таймауты транзакций.
     * 
     * <p>
     * Реализации драйвера JDBC также могут поддерживать метод setNetworkTimeout, 
     * чтобы наложить ограничение на время отклика базы данных в средах, где нет сети.
     * 
     * <p>
     * Драйверы могут внутренне реализовывать некоторые или все свои вызовы 
     * API с несколькими внутренними передачами драйверов-баз данных, 
     * и для реализации этого варианта остается только, 
     * будет ли предел применяться всегда к ответу на вызов API или любому отдельному запросу во время вызова API.
     * 
     * <p>
     * Этот метод можно вызвать несколько раз, например, установить ограничение для области кода 
     * JDBC и сбросить значение по умолчанию при выходе из этой области. 
     * Вызов этого метода не влияет на уже выдающиеся запросы.
     * 
     * <p>
     * Значение тайм-аута Statement.setQueryTimeout () не зависит от значения таймаута, 
     * указанного в setNetworkTimeout. 
     * Если тайм-аут запроса истекает до истечения сетевого тайм-аута, выполнение заявки будет отменено. 
     * 
     * <p>
     * Если сеть все еще активна, результатом будет то, что и оператор, и соединение все еще могут использоваться. 
     * 
     * <p>
     * Однако, если истечение таймаута сети истекает до таймаута запроса 
     * или если тайм-аут инструкции выходит из строя из-за сетевых проблем, 
     * соединение будет отмечено как закрытое, любые ресурсы, 
     * удерживаемые соединением, будут освобождены, и соединение и инструкция будут непригодными.
     * 
     * <p>
     * Когда драйвер определяет, что значение тайм-аута setNetworkTimeout истекло, 
     * драйвер JDBC отмечает соединение закрытым и освобождает любые ресурсы, удерживаемые соединением.
     * 
     * <p>
     * Этот метод проверяет, есть ли объект SQLPermission, прежде чем позволить этому методу продолжить работу. 
     * Если существует SecurityManager и его метод checkPermission отклоняет вызов setNetworkTimeout, 
     * этот метод генерирует исключение java.lang.SecurityException.
     * @return таймут сети
     */
    public Integer getNetworkTimeout() {
        synchronized(this){
            return networkTimeout;
        }
    }
    
    /**
     * Указывает таймут сети.
     * @param networkTimeout таймаут
     * @see #getNetworkTimeout() 
     */
    public void setNetworkTimeout( Integer networkTimeout) {
        Object old,cur;
        synchronized(this){ 
            old = this.networkTimeout;
            this.networkTimeout = networkTimeout; 
            cur = this.networkTimeout;
        }
        firePropertyChange("networkTimeout", old, cur);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="networkTimeoutThreads">
    protected Integer networkTimeoutThreads = 1;
    
    /**
     * Указывает кол-во потоков/тредов для сервиса network-timeout
     * @return кол-во тредов, по умолч 1
     */
    public Integer getNetworkTimeoutThreads() {
        synchronized(this){ 
            return networkTimeoutThreads; 
        }
    }
    
    /**
     * Указывает кол-во потоков/тредов для сервиса network-timeout
     * @param networkTimeoutThreads  кол-во тредов, по умолч 1
     */
    public void setNetworkTimeoutThreads( Integer networkTimeoutThreads) {
        Object old,cur;
        synchronized(this){
            old = this.networkTimeoutThreads;
            this.networkTimeoutThreads = networkTimeoutThreads;
            cur = this.networkTimeoutThreads;
        }
        firePropertyChange("networkTimeoutThreads", old, cur);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="networkTimeoutDaemon">
    protected Boolean networkTimeoutDaemon = true;
    
    /**
     * Указывает как запускать сервис network timeout.
     * 
     * <p>
     * <b>true</b> (по умолчанию) - Сервис network timeout будет запущен как фоновый поток(потоки)
     * 
     * <p>
     * <b>false</b> - Сервис network timeout будет запущен как обычный поток (тред)
     * @return true - сервис network timeout в фоновом режиме
     */
    public Boolean getNetworkTimeoutDaemon() {
        synchronized(this){
            return networkTimeoutDaemon;
        }
    }
    
    /**
     * Указывает как запускать сервис network timeout.
     * @param networkTimeoutDaemon 
     * <b>true</b> (по умолчанию) - Сервис network timeout будет запущен как фоновый поток(потоки) <br>
     * <b>false</b> - Сервис network timeout будет запущен как обычный поток (тред)
     */
    public void setNetworkTimeoutDaemon( Boolean networkTimeoutDaemon) {
        Object old,cur;
        synchronized(this){
            old = this.networkTimeoutDaemon;
            this.networkTimeoutDaemon = networkTimeoutDaemon;
            cur = this.networkTimeoutDaemon;
        }
        firePropertyChange("networkTimeoutDaemon", old, cur);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="networkTimeoutThreadName">
    protected String networkTimeoutThreadName = "DataSource network timeout";
    
    public String getNetworkTimeoutThreadName() {
        synchronized(this){
            return networkTimeoutThreadName;
        }
    }
    
    public void setNetworkTimeoutThreadName( String networkTimeoutThreadName) {
        Object old,cur;
        synchronized(this){
            old = this.networkTimeoutThreadName;
            this.networkTimeoutThreadName = networkTimeoutThreadName;
            cur = this.networkTimeoutThreadName;
        }
        firePropertyChange("networkTimeoutThreadName", old, cur);
    }
    //</editor-fold>    
    //<editor-fold defaultstate="collapsed" desc="networkTimeoutService : ExecutorService">
    protected transient static volatile ExecutorService networkTimeoutService;
    public static ExecutorService getNetworkTimeoutService(){ return networkTimeoutService; }
    //</editor-fold>    
    //<editor-fold defaultstate="collapsed" desc="networkTimeoutStatic : true">    
    protected Boolean networkTimeoutStatic = true;
    
    /**
     * Использовать сервис проверки network timeout как статичный (синглетон) демон
     * <p>
     * Это поведение по умолчанию
     * @return true - использовать статичный (синглетон) демон network timeout
     */
    public Boolean getNetworkTimeoutStatic() {
        synchronized(this){
            return networkTimeoutStatic;
        }
    }
    
    /**
     * Использовать сервис проверки network timeout как статичный (синглетон) демон
     * @param networkTimeoutStatic true - использовать статичный (синглетон) демон network timeout
     */
    public void setNetworkTimeoutStatic( Boolean networkTimeoutStatic) {
        Object old,cur;
        synchronized(this){
            old = this.networkTimeoutStatic;
            this.networkTimeoutStatic = networkTimeoutStatic;
            cur = this.networkTimeoutStatic;
        }
        firePropertyChange("networkTimeoutStatic", old, cur);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="configureConnection">
    protected void configureConnection( Connection conn ){
        synchronized(this){
            if( conn==null )return;
            logFine("configure connection");
            
            //<editor-fold defaultstate="collapsed" desc="autoCommit">
            if( autoCommit!=null ){
                try {
                    logFiner("set autoCommit = {0}", autoCommit);
                    conn.setAutoCommit(autoCommit);
                } catch ( SQLException ex) {
                    Logger.getLogger(BasicDataSource.class.getName())
                        .log(Level.SEVERE,
                            "fail set autoCommit = "+autoCommit+": "+
                                ex.getErrorCode()+" "+ex.getMessage()
                            , ex);
                }
            }
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="transactIsolation">
            if( transactIsolation!=null ){
                try {
                    logFiner("set transactIsolation = {0}", transactIsolation);
                    conn.setTransactionIsolation(transactIsolation.value());
                } catch ( SQLException ex) {
                    Logger.getLogger(BasicDataSource.class.getName())
                        .log(Level.SEVERE, "fail set transactIsolation = "+transactIsolation+
                            ": "+ex.getErrorCode()+" "+ex.getMessage()
                            , ex);
                }
            }
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="schema">
            if( schema!=null ){
                try {
                    logFiner("set schema = {0}", schema);
                    conn.setSchema(schema);
                } catch ( SQLException ex) {
                    Logger.getLogger(BasicDataSource.class.getName())
                        .log(Level.SEVERE, "fail set schema = "+schema+
                            ": "+ex.getErrorCode()+" "+ex.getMessage()
                            , ex);
                }
            }
            //</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="networkTimeout">
            if( networkTimeout!=null && networkTimeout>0 ){
                logFiner("set networkTimeout = {0}", networkTimeout);
                
                int tcnt = networkTimeoutThreads!=null && networkTimeoutThreads>0 ? networkTimeoutThreads : 1;
                
                ThreadFactory tfact = null;
                if( networkTimeoutDaemon!=null && networkTimeoutDaemon ){
                    tfact = new ThreadFactory() {
                        private AtomicInteger threadNum = new AtomicInteger();
                        
                        @Override
                        public Thread newThread( Runnable r) {
                            Thread th = new Thread(r);
                            th.setDaemon(true);
                            String name = networkTimeoutThreadName;
                            if( name!=null ){
                                name += "#" + threadNum.incrementAndGet();
                                th.setName(name);
                            }
                            logFiner("created network timeout thread: id={0}, name={1}",th.getId(),th.getName());
                            return th;
                        }
                    };
                }
                
                ExecutorService execSrvc = null;
                
                if( (networkTimeoutStatic!=null && networkTimeoutStatic) || networkTimeoutStatic==null ){
                    synchronized( BasicDataSource.class ){
                        if( networkTimeoutService!=null ){
                            execSrvc = networkTimeoutService;
                            logFiner("use static network timeout service");
                        }else{
                            execSrvc = tfact!=null
                                ? Executors.newFixedThreadPool(tcnt, tfact)
                                : Executors.newFixedThreadPool(tcnt);
                            
                            networkTimeoutService = execSrvc;
                            logFiner("created static network timeout service");
                        }
                    }
                }else{
                    execSrvc = tfact!=null
                        ? Executors.newFixedThreadPool(tcnt, tfact)
                        : Executors.newFixedThreadPool(tcnt);
                    
                    logFiner("created network timeout service");
                }
                
                try {
                    conn.setNetworkTimeout(execSrvc, networkTimeout);
                } catch ( SQLException ex) {
                    Logger.getLogger(BasicDataSource.class.getName())
                        .log(Level.SEVERE, "fail set networkTimeout = "+networkTimeout+
                            ": "+ex.getErrorCode()+" "+ex.getMessage()
                            , ex);
                }
            }
            //</editor-fold>
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="write xml">
    /**
     * Имя корневого тега (dataSource)
     */
    public static final String XMLROOT="dataSource";
    
    public String asXml(){
        StringWriter sw = new StringWriter();
        writeXml(sw);
        return sw.toString();
    }
    
    public void write( javax.xml.stream.XMLStreamWriter xwr ){
        if( xwr==null )throw new IllegalArgumentException("xwr");
        write(new FormatXMLWriter(xwr));
    }
    
    public void write( FormatXMLWriter xwr ){
        if( xwr==null )throw new IllegalArgumentException("xwr");
        synchronized(this){
            try {
                xwr.writeStartElement("dataSource");
                writeAttributes(xwr);
                writeConnectProperties(xwr);
                writeConnectOptions(xwr);
                xwr.writeEndElement();
                xwr.flush();
            } catch ( XMLStreamException ex) {
                Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOError(ex);
            }
        }
    }
    
    public void write( Element el ){
        if( el==null )throw new IllegalArgumentException("el == null");
        synchronized(this){
            //try {
                //xwr.writeStartElement("dataSource");
                writeAttributes(el);
                writeConnectProperties(el);
                writeConnectOptions(el);
                //xwr.writeEndElement();
                //xwr.flush();
            //} catch (XMLStreamException ex) {
            //    Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            //    throw new IOError(ex);
            //}
        }
    }
    
    public void writeXml( Writer writer ){
        if( writer==null )throw new IllegalArgumentException("writer == null");
        try {
            FormatXMLWriter xwr = new FormatXMLWriter(writer);
            write(xwr);
            //xwr.flush();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    public void writeXml( OutputStream writer ){
        if( writer==null )throw new IllegalArgumentException("writer == null");
        try {
            FormatXMLWriter xwr = new FormatXMLWriter(writer);
            write(xwr);
            //xwr.flush();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    public void writeXml( OutputStream writer, Charset cs ){
        if( writer==null )throw new IllegalArgumentException("writer == null");
        if( cs==null )cs = defaultFileCharset;
        try {
            FormatXMLWriter xwr = new FormatXMLWriter(new OutputStreamWriter(writer, cs));
            write(xwr);
            //xwr.flush();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    public void writeXml( File writer, Charset cs ){
        if( writer==null )throw new IllegalArgumentException("writer == null");
        if( cs==null )cs = defaultFileCharset;
        try {
            FormatXMLWriter xwr = new FormatXMLWriter(writer,cs);
            write(xwr);
            xwr.flush();
            xwr.close();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    public void writeXml( File writer ){
        if( writer==null )throw new IllegalArgumentException("writer == null");
        Charset cs = defaultFileCharset;
        try {
            FormatXMLWriter xwr = new FormatXMLWriter(writer,cs);
            write(xwr);
            xwr.flush();
            xwr.close();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    public void writeXml( Path writer, Charset cs ){
        if( writer==null )throw new IllegalArgumentException("writer == null");
        if( cs==null )cs = defaultFileCharset;
        try {
            OutputStream out = Files.newOutputStream(writer);
            FormatXMLWriter xwr = new FormatXMLWriter(new OutputStreamWriter(out, cs));
            write(xwr);
            xwr.flush();
            xwr.close();
        } catch ( XMLStreamException | IOException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    public void writeXml( Path writer ){
        if( writer==null )throw new IllegalArgumentException("writer == null");
        Charset cs = defaultFileCharset;
        try {
            OutputStream out = Files.newOutputStream(writer);
            FormatXMLWriter xwr = new FormatXMLWriter(new OutputStreamWriter(out, cs));
            write(xwr);
            xwr.flush();
            xwr.close();
        } catch ( XMLStreamException | IOException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    public void writeXml( xyz.cofe.io.fs.File writer, Charset cs ){
        if( writer==null )throw new IllegalArgumentException("writer == null");
        if( cs==null )cs = defaultFileCharset;
        try {
            OutputStream out = writer.writeStream();
            FormatXMLWriter xwr = new FormatXMLWriter(new OutputStreamWriter(out, cs));
            write(xwr);
            xwr.flush();
            xwr.close();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    public void writeXml( xyz.cofe.io.fs.File writer ){
        if( writer==null )throw new IllegalArgumentException("writer == null");
        Charset cs = defaultFileCharset;
        try {
            OutputStream out = writer.writeStream();
            FormatXMLWriter xwr = new FormatXMLWriter(new OutputStreamWriter(out, cs));
            write(xwr);
            xwr.flush();
            xwr.close();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    private void writeSimpleTag( FormatXMLWriter xwr, String name, Object content ) throws XMLStreamException{
        if( content!=null && name!=null ){
            xwr.writeStartElement(name);
            xwr.writeCharacters(content.toString());
            xwr.writeEndElement();
        }
    }
    private Element writeSimpleTag( Element parent, String name, Object content ) {
        if( parent!=null && name!=null && content!=null ){
            Element child = parent.getOwnerDocument().createElement(name);
            child.setTextContent(content.toString());
            parent.appendChild(child);
            return child;
        }
        return null;
    }
    
    private void writeAttributes( FormatXMLWriter xwr ) throws XMLStreamException{
        xwr.writeAttribute("type", this.getClass().getName());
        
        writeSimpleTag(xwr, "url", url);
        writeSimpleTag(xwr, "loginTimeout",loginTimeout);
        
        writeSimpleTag(xwr, "autoCommit", autoCommit);
        writeSimpleTag(xwr,"schema", schema);
        writeSimpleTag(xwr,"transactIsolation", transactIsolation);
        
        writeSimpleTag(xwr,"networkTimeout", networkTimeout );
        writeSimpleTag(xwr,"networkTimeoutDaemon", networkTimeoutDaemon );
        writeSimpleTag(xwr,"networkTimeoutStatic", networkTimeoutStatic );
        writeSimpleTag(xwr,"networkTimeoutThreadName", networkTimeoutThreadName );
        writeSimpleTag(xwr,"networkTimeoutThreads", networkTimeoutThreads );
    }
    private void writeAttributes( Element parent ) {
        parent.setAttribute("type", this.getClass().getName());

        writeSimpleTag(parent, "url", url);
        writeSimpleTag(parent, "loginTimeout",loginTimeout);
        
        writeSimpleTag(parent, "autoCommit", autoCommit);
        writeSimpleTag(parent,"schema", schema);
        writeSimpleTag(parent,"transactIsolation", transactIsolation);
        
        writeSimpleTag(parent,"networkTimeout", networkTimeout );
        writeSimpleTag(parent,"networkTimeoutDaemon", networkTimeoutDaemon );
        writeSimpleTag(parent,"networkTimeoutStatic", networkTimeoutStatic );
        writeSimpleTag(parent,"networkTimeoutThreadName", networkTimeoutThreadName );
        writeSimpleTag(parent,"networkTimeoutThreads", networkTimeoutThreads );
    }
    
    private void writeConnectProperties( FormatXMLWriter xwr ) throws XMLStreamException{
        xwr.writeStartElement("properties");
        for( Map.Entry en : properties().entrySet() ){
            Object k = en.getKey();
            Object v = en.getValue();
            if( k==null || v==null )continue;
            xwr.writeStartElement("property");
            xwr.writeStartElement("key");
            xwr.writeCharacters(k.toString());
            xwr.writeEndElement();
            xwr.writeStartElement("value");
            xwr.writeCharacters(v.toString());
            xwr.writeEndElement();
            xwr.writeEndElement();
        }
        xwr.writeEndElement();
    }
    private void writeConnectProperties( Element parent ) {
        Element el = parent.getOwnerDocument().createElement("properties");
        parent.appendChild(el);
        
        for( Map.Entry en : properties().entrySet() ){
            Object k = en.getKey();
            Object v = en.getValue();
            if( k==null || v==null )continue;
            
            Element prop = el.getOwnerDocument().createElement("property");
            el.appendChild(prop);
            
            Element key = prop.getOwnerDocument().createElement("key");
            prop.appendChild(key);
            
            Element value = prop.getOwnerDocument().createElement("value");
            prop.appendChild(value);
            
            key.setTextContent(k.toString());
            value.setTextContent(v.toString());
        }
    }
    
    private void writeConnectOptions( FormatXMLWriter xwr ) throws XMLStreamException{
        if( copts==null || copts.isEmpty() )return;
        
        xwr.writeStartElement("options");
        for( Map.Entry en : copts.entrySet() ){
            Object k = en.getKey();
            Object v = en.getValue();
            if( k==null || v==null )continue;
            xwr.writeStartElement("property");
            xwr.writeStartElement("key");
            xwr.writeCharacters(k.toString());
            xwr.writeEndElement();
            xwr.writeStartElement("value");
            xwr.writeCharacters(v.toString());
            xwr.writeEndElement();
            xwr.writeEndElement();
        }
        xwr.writeEndElement();
    }
    
    private void writeConnectOptions( Element parent ) {
        if( copts==null || copts.isEmpty() )return;
        
        Element el = parent.getOwnerDocument().createElement("options");
        parent.appendChild(el);
        
        for( Map.Entry en : copts.entrySet() ){
            Object k = en.getKey();
            Object v = en.getValue();
            if( k==null || v==null )continue;
            
            Element prop = el.getOwnerDocument().createElement("property");
            el.appendChild(prop);
            
            Element key = el.getOwnerDocument().createElement("key");
            prop.appendChild(key);
            
            Element value = el.getOwnerDocument().createElement("value");
            prop.appendChild(value);
            
            key.setTextContent(k.toString());
            value.setTextContent(v.toString());
        }
    }
    //</editor-fold>    
    //<editor-fold defaultstate="collapsed" desc="read xml">
    //<editor-fold defaultstate="collapsed" desc="XmlVisitor">
    public static class XmlVisitor extends XVisitorAdapter {
        //<editor-fold defaultstate="collapsed" desc="Constructor">
        public XmlVisitor(){
            dataSource = null;
        }
        
        public XmlVisitor( BasicDataSource ds){
            dataSource = ds;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="classLoader">
        protected ClassLoader classLoader;
        
        public ClassLoader getClassLoader() {
            return classLoader;
        }
        
        public void setClassLoader( ClassLoader classLoader) {
            this.classLoader = classLoader;
        }
        //</editor-fold>
        
        protected ClassLoader classloader(){
            ClassLoader cl = getClassLoader();
            if( cl==null ) cl = Thread.currentThread().getContextClassLoader();
            if( cl==null ) cl = BasicDataSource.class.getClassLoader();
            return cl;
        }
        
        //<editor-fold defaultstate="collapsed" desc="dataSource">
        protected BasicDataSource dataSource;
        
        public BasicDataSource getDataSource() {
            if( dataSource==null ){
                dataSource = new BasicDataSource();
            }
            return dataSource;
        }
        
        public void setDataSource( BasicDataSource dataSource) {
            this.dataSource = dataSource;
        }
        //</editor-fold>
        
        protected boolean editDataSource = false;
        
        @PathMatch(enter = "dataSource")
        public void enterDS( XEventPath path ){
            editDataSource = true;
            String srcTypeName = path.readAttributeAsString("type", null);
            if( srcTypeName!=null ){
                try {
                    Class srcType = Class.forName(srcTypeName, true, classloader());
                    if( !(BasicDataSource.class.isAssignableFrom(srcType)) ){
                        logWarning("wrong dataSource type={0} is not child of {1}",srcType.getName(), BasicDataSource.class.getName());
                        return;
                    }
                    
                    boolean createDS = false;
                    if( dataSource!=null ){
                        if( !(srcType.equals(dataSource.getClass())) ){
                            createDS = true;
                        }
                    }else{
                        createDS = true;
                    }
                    if( !createDS )return;
                    
                    Object inst = srcType.newInstance();
                    if( inst instanceof BasicDataSource ){
                        dataSource = (BasicDataSource)inst;
                    }else{
                        logWarning("can't use {0} - not instanceof {1}", srcType.getName(), BasicDataSource.class.getName());
                    }
                } catch ( ClassNotFoundException ex) {
                    Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
                } catch ( InstantiationException ex) {
                    Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
                } catch ( IllegalAccessException ex) {
                    Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        @PathMatch(exit = "dataSource")
        public void exitDS( XEventPath path ){
            editDataSource = false;
        }
        
        @PathMatch(content = "dataSource/url")
        public void url( XEventPath path, String url ){
            if( !editDataSource )return;
            getDataSource().setUrl(url);
        }
        
        @PathMatch(content = "dataSource/loginTimeout")
        public void loginTimeout( XEventPath path, int seconds ){
            if( !editDataSource )return;
            try {
                getDataSource().setLoginTimeout(seconds);
            } catch ( SQLException ex) {
                Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, "fail set loginTimeout="+seconds, ex);
            }
        }
        
        @PathMatch(content = "dataSource/shared")
        public void shared( XEventPath path, boolean shared ){
            if( !editDataSource )return;
            getDataSource().setShared(shared);
        }
        
        @PathMatch(content = "dataSource/autoCommit")
        public void loginTimeout( XEventPath path, boolean autoCommit ){
            if( !editDataSource )return;
            getDataSource().setAutoCommit(autoCommit);
        }
        
        @PathMatch(content = "dataSource/schema")
        public void schema( XEventPath path, String schema ){
            if( !editDataSource )return;
            getDataSource().setSchema(schema);
        }
        
        @PathMatch(content = "dataSource/transactIsolation")
        public void transactIsolation( XEventPath path, String tisolation ){
            if( !editDataSource )return;
            getDataSource().setTransactIsolation(TransactIsolation.valueOf(tisolation));
        }
        
        @PathMatch(content = "dataSource/networkTimeout")
        public void networkTimeout( XEventPath path, int value ){
            if( !editDataSource )return;
            getDataSource().setNetworkTimeout(value);
        }
        
        @PathMatch(content = "dataSource/networkTimeoutDaemon")
        public void networkTimeoutDaemon( XEventPath path, boolean value ){
            if( !editDataSource )return;
            getDataSource().setNetworkTimeoutDaemon(value);
        }
        
        @PathMatch(content = "dataSource/networkTimeoutStatic")
        public void networkTimeoutStatic( XEventPath path, boolean value ){
            if( !editDataSource )return;
            getDataSource().setNetworkTimeoutStatic(value);
        }
        
        @PathMatch(content = "dataSource/networkTimeoutThreadName")
        public void networkTimeoutThreadName( XEventPath path, String value ){
            if( !editDataSource )return;
            getDataSource().setNetworkTimeoutThreadName(value);
        }
        
        @PathMatch(content = "dataSource/networkTimeoutThreads")
        public void networkTimeoutThreads( XEventPath path, int value ){
            if( !editDataSource  )return;
            getDataSource().setNetworkTimeoutThreads(value);
        }
        
        protected String propertyName;
        protected String propertyValue;
        
        @PathMatch(enter = "dataSource/properties/property")
        public void propertyEnter( XEventPath path ){
            if( !editDataSource )return;
            propertyName = null;
            propertyValue = null;
        }
        
        @PathMatch(exit = "dataSource/properties/property")
        public void propertyExit( XEventPath path ){
            if( !editDataSource )return;
            if( propertyName!=null && propertyValue!=null ){
                getDataSource().properties().put(propertyName, propertyValue);
            }
        }
        
        @PathMatch(content = "dataSource/properties/property/key")
        public void propertyKey( XEventPath path, String key ){
            if( !editDataSource )return;
            propertyName = key;
        }
        
        @PathMatch(content = "dataSource/properties/property/value")
        public void propertyValue( XEventPath path, String value ){
            if( !editDataSource )return;
            propertyValue = value;
        }
        
        protected String optName;
        protected String optValue;
        
        @PathMatch(enter = "dataSource/options/property")
        public void optionPropertyEnter( XEventPath path ){
            if( !editDataSource )return;
            optName = null;
            optValue = null;
        }
        
        @PathMatch(exit = "dataSource/options/property")
        public void optionPropertyExit( XEventPath path ){
            if( !editDataSource )return;
            if( optName!=null && optValue!=null ){
                getDataSource().getConnectOptions().put(optName, optValue);
            }
        }
        
        @PathMatch(content = "dataSource/options/property/key")
        public void optionKey( XEventPath path, String key ){
            if( !editDataSource || dataSource==null )return;
            optName = key;
        }
        
        @PathMatch(content = "dataSource/options/property/value")
        public void optionValue( XEventPath path, String value ){
            if( !editDataSource || dataSource==null )return;
            optValue = value;
        }
    }
    //</editor-fold>
    
    public static final Charset defaultFileCharset = Charset.forName("utf-8");
    
    public static BasicDataSource read( XMLStreamReader reader ){
        if( reader==null )throw new IllegalArgumentException("reader == null");
        //BasicDataSource ds = new BasicDataSource();
        XmlVisitor xv = new XmlVisitor();
        try {
            new XmlReader(reader, xv);
        } catch ( XMLStreamException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
        return xv.getDataSource();
    }
    
    public static BasicDataSource readXml( Reader reader ){
        if( reader==null )throw new IllegalArgumentException("reader == null");
        //BasicDataSource ds = new BasicDataSource();
        XmlVisitor xv = new XmlVisitor();
        try {
            new XmlReader(reader, xv);
        } catch ( XMLStreamException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
        return xv.getDataSource();
    }
    
    public static BasicDataSource readXml( URL reader ){
        if( reader==null )throw new IllegalArgumentException("reader == null");
        //BasicDataSource ds = new BasicDataSource();
        XmlVisitor xv = new XmlVisitor();
        try {
            new XmlReader(reader, defaultFileCharset, xv);
        } catch ( XMLStreamException | IOException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
        return xv.getDataSource();
    }
    
    public static BasicDataSource readXml( URL reader, Charset cs ){
        if( reader==null )throw new IllegalArgumentException("reader == null");
        if( cs==null )cs = defaultFileCharset;
        //BasicDataSource ds = new BasicDataSource();
        XmlVisitor xv = new XmlVisitor();
        try {
            new XmlReader(reader, cs, xv);
        } catch ( XMLStreamException | IOException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
        return xv.getDataSource();
    }
    
    public static BasicDataSource readXml( InputStream reader, Charset cs ){
        if( reader==null )throw new IllegalArgumentException("reader == null");
        if( cs==null )cs = defaultFileCharset;
        //BasicDataSource ds = new BasicDataSource();
        XmlVisitor xv = new XmlVisitor();
        try {
            new XmlReader(reader, cs, xv);
        } catch ( XMLStreamException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
        return xv.getDataSource();
    }
    
    public static BasicDataSource readXml( InputStream reader ){
        if( reader==null )throw new IllegalArgumentException("reader == null");
        //BasicDataSource ds = new BasicDataSource();
        XmlVisitor xv = new XmlVisitor();
        try {
            new XmlReader(reader, xv);
        } catch ( XMLStreamException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
        return xv.getDataSource();
    }
    
    public static BasicDataSource readXml( File reader ){
        if( reader==null )throw new IllegalArgumentException("reader == null");
        //BasicDataSource ds = new BasicDataSource();
        XmlVisitor xv = new XmlVisitor();
        try {
            new XmlReader(reader, xv);
        } catch ( XMLStreamException | IOException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
        return xv.getDataSource();
    }
    
    public static BasicDataSource readXml( File reader, Charset cs ){
        if( reader==null )throw new IllegalArgumentException("reader == null");
        if( cs==null )cs = defaultFileCharset;
        //BasicDataSource ds = new BasicDataSource();
        XmlVisitor xv = new XmlVisitor();
        try {
            new XmlReader(reader, cs, xv);
        } catch ( XMLStreamException | IOException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
        return xv.getDataSource();
    }
    
    public static BasicDataSource readXml( Path reader ){
        if( reader==null )throw new IllegalArgumentException("reader == null");
        //BasicDataSource ds = new BasicDataSource();
        XmlVisitor xv = new XmlVisitor();
        try {
            InputStream input = Files.newInputStream(reader);
            new XmlReader(input, xv);
            input.close();
        } catch ( XMLStreamException | IOException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
        return xv.getDataSource();
    }
    
    public static BasicDataSource readXml( Path reader, Charset cs ){
        if( reader==null )throw new IllegalArgumentException("reader == null");
        if( cs==null )cs = defaultFileCharset;
        //BasicDataSource ds = new BasicDataSource();
        XmlVisitor xv = new XmlVisitor();
        try {
            InputStream input = Files.newInputStream(reader);
            new XmlReader(input, cs, xv);
            input.close();
        } catch ( XMLStreamException | IOException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
        return xv.getDataSource();
    }
    
    public static BasicDataSource readXml( xyz.cofe.io.fs.File reader ){
        if( reader==null )throw new IllegalArgumentException("reader == null");
        //BasicDataSource ds = new BasicDataSource();
        XmlVisitor xv = new XmlVisitor();
        try {
            InputStream input = reader.readStream();
            new XmlReader(input, xv);
            input.close();
        } catch ( XMLStreamException | IOException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
        return xv.getDataSource();
    }
    
    public static BasicDataSource readXml( xyz.cofe.io.fs.File reader, Charset cs ){
        if( reader==null )throw new IllegalArgumentException("reader == null");
        if( cs==null )cs = defaultFileCharset;
        //BasicDataSource ds = new BasicDataSource();
        XmlVisitor xv = new XmlVisitor();
        try {
            InputStream input = reader.readStream();
            new XmlReader(input, cs, xv);
            input.close();
        } catch ( XMLStreamException | IOException ex) {
            Logger.getLogger(BasicDataSource.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
        return xv.getDataSource();
    }
    //</editor-fold>
    
    public void loadXml( Node xml ){
        if (xml== null) {
            throw new IllegalArgumentException("xml==null");
        }
        String xmlstr = XmlUtil.writeAsString(xml);
        synchronized(this){
            BasicDataSource readed = BasicDataSource.readXml(new StringReader(xmlstr));
            if( readed==null )throw new IllegalArgumentException("can't read xml - return null");
            
            assign(readed);
        }
    }
}
