/*
 * The MIT License
 *
 * Copyright 2017 user.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package xyz.cofe.sql.cpool;

import xyz.cofe.collection.BasicEventMap;
import xyz.cofe.collection.EventMap;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.scn.LongScn;
import xyz.cofe.scn.ScnEvent;
import xyz.cofe.scn.ScnListener;

import javax.sql.DataSource;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Описание соединения с СУБД
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class SimpleDataSource 
    implements DataSource, LongScn<SimpleDataSource, SimpleDataSource>
{
    //region log Функции
    private transient static final Logger logger = Logger.getLogger(SimpleDataSource.class.getName());
    private transient static final Level logLevel = logger.getLevel();
    
    @SuppressWarnings("SimplifiableConditionalExpression")
    private transient static final boolean isLogSevere =
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.SEVERE.intValue();

    @SuppressWarnings("SimplifiableConditionalExpression")
    private transient static final boolean isLogWarning =
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.WARNING.intValue();

    @SuppressWarnings("SimplifiableConditionalExpression")
    private transient static final boolean isLogInfo =
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.INFO.intValue();

    @SuppressWarnings("SimplifiableConditionalExpression")
    private transient static final boolean isLogFine =
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.FINE.intValue();

    @SuppressWarnings("SimplifiableConditionalExpression")
    private transient static final boolean isLogFiner =
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.FINER.intValue();

    @SuppressWarnings("SimplifiableConditionalExpression")
    private transient static final boolean isLogFinest =
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.FINEST.intValue();

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
        logger.entering(SimpleDataSource.class.getName(), method, params);
    }
    
    private static void logExiting( String method){
        logger.exiting(SimpleDataSource.class.getName(), method);
    }
    
    private static void logExiting( String method, Object result){
        logger.exiting(SimpleDataSource.class.getName(), method, result);
    }
    //endregion

    //region scnListenerHelper
    private final ListenersHelper<ScnListener<SimpleDataSource, Long, SimpleDataSource>, ScnEvent<SimpleDataSource, Long, SimpleDataSource>> lh =
        new ListenersHelper<>(ScnListener::scnEvent);

    /**
     * Возвращает помощника издателя для поддержи событий
     * @return помошник издателя
     */
    @Override
    public ListenersHelper<ScnListener<SimpleDataSource, Long, SimpleDataSource>, ScnEvent<SimpleDataSource, Long, SimpleDataSource>> scnListenerHelper(){
        return lh;
    }
    //endregion

    protected final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public ReadWriteLock getReadWriteLock(){
        return readWriteLock;
    }

    //region PropertyChangeSupport
    protected transient final PropertyChangeSupport psupport;
    
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
    //endregion

    public SimpleDataSource(){
        psupport = new PropertyChangeSupport(this);
    }
    
    public SimpleDataSource( String url){
        psupport = new PropertyChangeSupport(this);
        this.url = url;
    }
    
    public SimpleDataSource( String url, String username, String password){
        psupport = new PropertyChangeSupport(this);
        this.url = url;
        setUsername(username);
        setPassword(password);
    }
    
    public SimpleDataSource( String url, Properties props){
        psupport = new PropertyChangeSupport(this);
        this.url = url;
        if( props!=null ){
            properties().clear();
            for( String k : props.stringPropertyNames() ){
                String v = props.getProperty(k);
                if( k!=null && v!=null ){
                    properties().put(k, v);
                }
            }
            //properties().putAll(props);
        }
    }
    
    public SimpleDataSource( SimpleDataSource sample ){
        psupport = new PropertyChangeSupport(this);
        if( sample!=null ){
            synchronized(sample){
                this.url = sample.url;
                properties().putAll(sample.properties());
                this.loginTimeout = sample.loginTimeout;
                this.logWriter = sample.logWriter;
            }
        }
    }
    
    public void assign( SimpleDataSource ds ){
        if( ds!=null ){
            synchronized(ds){
                synchronized(this){
                    this.url = ds.url;
                    properties().clear();
                    properties().putAll(ds.properties());
                    this.loginTimeout = ds.loginTimeout;
                    this.logWriter = ds.logWriter;
                }
            }
        }
    }
    
    @Override
    public SimpleDataSource clone(){
        return new SimpleDataSource(this);
    }
    
    //<editor-fold defaultstate="collapsed" desc="url">
    protected String url;
    
//    @UiBean(
//        shortDescription = "jdbc url ..."
//        ,htmlDescription =
//            "<h2>mysql</h2>"
//                + "<code>"
//                + "jdbc:"
//                + "(mysql|mariadb):"
//                + "[replication:|failover:|sequential:|aurora:]//"
//                + "<i>hostDescription</i>[,<i>hostDescription</i>...]/"
//                + "[database][?<i>key1</i>=<i>value1</i>[&<i>key2</i>=<i>value2</i>]]"
//                + "</code>"
//            + "<h3>HostDescription</h3>"
//                + "<code><i>host</i>[:<i>portnumber</i>] "
//                + "or address=(host=<i>host</i>)[(port=<i>portnumber</i>)][(type=(master|slave))]</code>"
//    )
    public String getUrl() {
        synchronized(this){
            return url;
        }
    }
    
    @SuppressWarnings("rawtypes")
    public void setUrl( final String url) {
        final AtomicReference
            old = new AtomicReference(),
            cur = new AtomicReference();
        scn( new Runnable() {
            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                synchronized(this){
                    old.set( SimpleDataSource.this.url );
                    SimpleDataSource.this.url = url;
                    cur.set( SimpleDataSource.this.url );
                }
                nextscn();
            }
        } );
        firePropertyChange("url", old, cur);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="properties">
    protected EventMap<String, String> properties;
    
    /**
     * Указывает свойства/параметры JDBC соединения
     * @return свойства параметры
     */
    public EventMap<String, String> properties(){
        synchronized(this){
            if( properties!=null )return properties;
            properties = new BasicEventMap<>(new LinkedHashMap<String, String>(), this.readWriteLock);
            properties.onChanged( (key,old,cur) -> {
                if( key!=null ){
                    scn( new Runnable(){
                        public void run(){
                            switch( key ){
                                case "user":
                                    firePropertyChange("username", old, cur);
                                    firePropertyChange("property.user", old, cur);
                                    break;
                                case "password":
                                    firePropertyChange("password", old, cur);
                                    firePropertyChange("property.password", old, cur);
                                    break;
                                default:
                                    firePropertyChange("property."+key, old, cur);
                            }

                            nextscn();
                        }
                    });
                }
            });
            return properties;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="username">
    /**
     * Указывает имя пользователя соединения
     * @return имя пользователя БД
     */
    public String getUsername(){
        return properties().get("user");
    }
    
    /**
     * Указывает имя пользователя соединения
     * @param name имя пользователя БД
     */
    public void setUsername( String name){
        if( name==null ){
            properties().remove("user");
        }else{
            properties().put("user",name);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="password">
    /**
     * Возвращает пароль для соединения с СУБД
     * @return Пароль или null
     */
    public String getPassword(){
        return properties().get("password");
    }
    
    /**
     * Устанавливает пароль для соединения с СУБД
     * @param pswd Пароль или null
     */
    public void setPassword( String pswd ){
        if( pswd==null ){
            properties().remove("password");
        }else{
            properties().put("password", pswd);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="loginTimeout">
    protected int loginTimeout = 60;

    /**
     * Указывает таймаут (в секундах) на подключение к базе.
     * 
     * <p>
     * По умолчанию 60 секунд
     * @param seconds таймаут в секундах
     * @throws SQLException Данная ошибка не генерируется этим методом
     */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException{
        Object old,cur;
        synchronized(this){
            old = loginTimeout;
            loginTimeout = seconds;
            cur = loginTimeout;
        }
        firePropertyChange("loginTimeout", old, cur);
    }
    
    /**
     * Указывает таймаут (в секундах) на подключение к базе.
     * 
     * <p>
     * По умолчанию 60 секунд
     * @return таймаут в секундах
     * @throws SQLException Данная ошибка не генерируется этим методом
     */
    @Override
    public int getLoginTimeout() throws SQLException{
        synchronized(this){
            return loginTimeout;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="save/restore DefLoginTimeout">
    private transient final static Stack<Integer> savedDefLoginTimeout = new Stack<>();
    
    private static void saveDefLoginTimeout(){
        synchronized(savedDefLoginTimeout){
            savedDefLoginTimeout.push(DriverManager.getLoginTimeout());
        }
    }
    private static void restoreDefLoginTimeout(){
        synchronized(savedDefLoginTimeout){
            if( !savedDefLoginTimeout.empty() ){
                int v = savedDefLoginTimeout.pop();
                DriverManager.setLoginTimeout(v);
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="logWriter">
    protected transient PrintWriter logWriter;
    
    //@UiBean(forceHidden = true)
    @Override
    public PrintWriter getLogWriter() throws SQLException{
        synchronized(this){
            return logWriter;
        }
    }
    
    @Override
    public void setLogWriter( PrintWriter out) throws SQLException{
        Object old,cur;
        synchronized(this){
            old = logWriter;
            logWriter = out;
            cur = logWriter;
        }
        firePropertyChange("logWriter", old, cur);
    }
    //</editor-fold>
    
    @SuppressWarnings("SameParameterValue")
    private void logs( Level lvl, String message){
        if( message==null )return;
        synchronized(this){
            try{
                if( logWriter != null ){
                    logWriter.println(message);
                }
            }catch( Throwable err){
                logException(err);
            }
            
            if( lvl!=null ){
                logger.log(lvl, message);
            }
        }
    }
    
    private void logs( Throwable ex, String message){
        synchronized(this){
            try{
                if( logWriter != null ){
                    if( message!=null )logWriter.println(message);
                    if( ex!=null )logWriter.println(ex);
                }
            }catch( Throwable err){
                logException(err);
            }
            
            if( ex!=null )logger.log(Level.SEVERE, message, ex);
        }
    }
    
    private void logConnecting(){
        logs( Level.FINE, "connecting "+url );
    }

    private void logConnected( Connection conn){
        logs( Level.FINE, "connected "+url );
    }

    private void logConnectFail( SQLException e){
        logs( e, "connect fail "+url );
    }

    //@UiBean(forceHidden = true)
    @Override
    public Connection getConnection() throws SQLException{
        synchronized(this){
            if( url==null )throw new IllegalStateException("url not set");
            try{
                saveDefLoginTimeout();
                DriverManager.setLoginTimeout(loginTimeout);
                
                logConnecting();
                Properties props = new Properties();
                props.putAll(properties());
                Connection conn = DriverManager.getConnection(url, props);
                
                logConnected(conn);
                return conn;
            }catch( SQLException e){
                logConnectFail(e);
                throw e;
            }finally{
                restoreDefLoginTimeout();
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="getConnection(username,password)">
    @Override
    public Connection getConnection( String username, String password) throws SQLException{
        synchronized(this){
            if( url==null )throw new IllegalStateException("url not set");
            if( username==null )throw new IllegalArgumentException("username == null");
            if( password==null )throw new IllegalArgumentException("password == null");
            try{
                saveDefLoginTimeout();
                DriverManager.setLoginTimeout(loginTimeout);
                
                logConnecting();
                //Connection conn = DriverManager.getConnection(url, username, password);
                Properties props = new Properties();
                props.putAll(properties());
                props.put("user", username);
                props.put("password", password);
                
                Connection conn = DriverManager.getConnection(url,props);
                
                logConnected(conn);
                return conn;
            }catch( SQLException e){
                logConnectFail(e);
                throw e;
            }finally{
                restoreDefLoginTimeout();
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getParentLogger()">
    //@UiBean(forceHidden = true)
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException{
        return logger.getParent();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="unwrap()">
    @Override
    public <T> T unwrap( Class<T> iface) throws SQLException{
        if( iface==null )return null;
        if( DataSource.class.equals(iface) ){
            return (T)this;
        }
        return null;
    }
    
    @Override
    public boolean isWrapperFor( Class<?> iface) throws SQLException
    {
        if( iface==null )return false;
        if( DataSource.class.equals(iface) ){
            return true;
        }
        return false;
    }
    //</editor-fold>
}
