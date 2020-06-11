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

package xyz.cofe.sql;

import java.io.Closeable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Timer;
import java.util.WeakHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import xyz.cofe.collection.Predicate;
import xyz.cofe.collection.list.EventList;
import xyz.cofe.collection.list.IndexEventList;
import xyz.cofe.collection.map.EventMap;
import xyz.cofe.collection.map.SyncEventMap;
import xyz.cofe.data.DataEvent;
import xyz.cofe.data.DataEventListener;
import xyz.cofe.data.DataEventSupport;
import xyz.cofe.sql.proxy.ConnectionTracker;
import xyz.cofe.sql.proxy.GenericProxy;
import xyz.cofe.sql.proxy.GetProxyTarget;
import xyz.cofe.sql.proxy.InvokeActivityStat;
import xyz.cofe.sql.proxy.StatementTracker;

/**
 * Пул соединений c СУБД. <p>
 * Задачи
 * <ul>
 * <li>Создание и регистрация соединений с СУБД
 * <li>Создание proxy для соединений и запросов
 * <li>Отслеживание активности соединений и запросов
 * <li>Закрытие объектов по истичении времени при остутсвии активности
 * </ul>
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class ConnectPool
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ConnectPool.class.getName());
    private static final Level logLevel = logger.getLevel();

    private static final boolean isLogSevere =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.SEVERE.intValue();

    private static final boolean isLogWarning =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.WARNING.intValue();

    private static final boolean isLogInfo =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.INFO.intValue();

    private static final boolean isLogFine =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINE.intValue();

    private static final boolean isLogFiner =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINER.intValue();

    private static final boolean isLogFinest =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINEST.intValue();

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
        logger.entering(ConnectPool.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(ConnectPool.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(ConnectPool.class.getName(), method, result);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dataEventSupport">
    protected transient final DataEventSupport eventSupport;

    public Closeable addDataEventListener(DataEventListener ls, boolean weak) {
        return eventSupport.addDataEventListener(ls, weak);
    }

    public Closeable addDataEventListener(DataEventListener ls) {
        return eventSupport.addDataEventListener(ls);
    }

    public void removeDataEventListener(DataEventListener ls) {
        eventSupport.removeDataEventListener(ls);
    }

    public boolean hasDataEventListener(DataEventListener ls) {
        return eventSupport.hasDataEventListener(ls);
    }

    public DataEventListener[] getDataEventListeners() {
        return eventSupport.getDataEventListeners();
    }

    public void fireDataEvent(DataEvent event) {
        eventSupport.fireDataEvent(event);
    }

    protected transient final LinkedBlockingQueue<DataEvent> eventQueue
        = new LinkedBlockingQueue<>();

    public void addDataEvent(DataEvent ev){
        if( ev!=null )eventQueue.add(ev);
        //scn.incrementAndGet();
    }

    public void fireEventQueue(){
        while( true ){
            DataEvent e = eventQueue.poll();
            if( e==null )break;

            fireDataEvent(e);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="id">
    private transient final static AtomicInteger idseq = new AtomicInteger();
    private transient final int id = idseq.incrementAndGet();

    private transient final static AtomicInteger connIdSeq = new AtomicInteger();

    /**
     * Возвращает идентификатор (в пределах экземпляра JVM) пула
     * @return идентификатор
     */
    public int getId(){ return id; }
    //</editor-fold>

    private static final WeakHashMap<ConnectPool,Date>
        instances = new WeakHashMap<ConnectPool, Date>();

    /**
     * Возвращает списиок доступных экземпляров
     * @return список экземпляров
     */
    public static List<ConnectPool> instances(){
        synchronized(instances){
            ArrayList<ConnectPool> inst = new ArrayList<>();
            for( ConnectPool cp : instances.keySet() ){
                if( cp!=null && !inst.contains(cp) ){
                    inst.add(cp);
                }
            }
            return inst;
        }
    }

    //private static final tim

    /**
     * Конструктор по умолчанию
     */
    public ConnectPool(){
        logFine("create ConnectionPool without arguments");
        synchronized(instances){
            instances.put(this, new Date());
        }

        logFiner( "create DataEventSupport()" );
        eventSupport = new DataEventSupport();

        logFiner( "create Timer thread" );
        //timer = new Timer("ConnectPool Monitor#"+id, true);

        long delay = 500;
        long period = 1000;
        logFiner( "schedule CloseByTimeoutTask delay={0} period={1}", delay, period );
        getTimer().schedule(new CloseByTimeoutTask(this), delay, period);

        logFiner( "load services from ServiceLoader.load(ConnectionPoolService.class)" );
        for( ConnectionPoolService srvc : ServiceLoader.load(ConnectionPoolService.class) ){
            if( srvc!=null ){
                try{
                    logFinest("init service: {0}", srvc.getClass().getName());
                    srvc.init(this);

                    logFinest("loaded service: {0}", srvc.getClass().getName());
                    getServices().add(srvc);
                } catch (Throwable err){
                    Logger.getLogger(ConnectPool.class.getName()).log(
                        Level.SEVERE,
                        "error init service "+srvc.getClass().getName()+
                            ", error: "+err.getClass().getSimpleName()+" : "+err.getMessage(),
                        err);
                }
            }
        }
    }

    /**
     * Конструктор
     * @param services сервисы расширения
     */
    public ConnectPool( ConnectionPoolService ... services ){
        logFine("create ConnectionPool with {0} services", services!=null ? services.length : "null");
        synchronized(instances){
            instances.put(this, new Date());
        }

        logFiner( "create DataEventSupport()" );
        eventSupport = new DataEventSupport();

        logFiner( "create Timer thread" );
        //timer = new Timer("ConnectPool Monitor#"+id, true);

        long delay = 500;
        long period = 1000;
        logFiner( "schedule CloseByTimeoutTask delay={0} period={1}", delay, period );
        getTimer().schedule(new CloseByTimeoutTask(this), delay, period);

        if( services!=null ){
            logFiner( "load services from arguments" );
            for( ConnectionPoolService srvc : services ){
                if( srvc!=null ){
                    try{
                        logFinest("init service: {0}", srvc.getClass().getName());
                        srvc.init(this);

                        logFinest("loaded service: {0}", srvc.getClass().getName());
                        getServices().add(srvc);
                    } catch (Throwable err){
                        Logger.getLogger(ConnectPool.class.getName()).log(
                            Level.SEVERE,
                            "error init service "+srvc.getClass().getName()+
                                ", error: "+err.getClass().getSimpleName()+" : "+err.getMessage(),
                            err);
                    }
                }
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="services : EventList<ConnectionPoolService>">
    protected final EventList<ConnectionPoolService> services = new IndexEventList<>();

    /**
     * Возвращает список сервисов (расширений) пула
     * @return список сервисов
     */
    public EventList<ConnectionPoolService> getServices(){ return services; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="timer">
    private volatile static Timer timer
        = new Timer("ConnectPool Monitor", true);

    /**
     * Возвращает таймер обслуживающий задачи утилизации/освобождения ресурсов
     * @return таймер
     */
    public Timer getTimer(){
        synchronized( ConnectPool.class ){
            if( timer!=null )return timer;
            timer = new Timer("ConnectPool Monitor", true);
            return timer;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="finalize()">
    @Override
    @SuppressWarnings("FinalizeDeclaration")
    protected void finalize() throws Throwable {
        logFine("finalize()");
        shutdown(false);
        super.finalize();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="closeAllConnections()">
    /**
     * Закрывает все объекты: statements, connections. Генерирует соответ сообщения.
     */
    public void closeAll(){
        logFine("closeAll");
        closeAllStatements(true);
        closeAllConnections(true);
        fireEventQueue();
    }

    /**
     * Закрытие всех запросов.
     * @param addEvents true - Добавить сообщения в очередь.
     */
    public void closeAllStatements(boolean addEvents){
        logFine("closeAllStatements()");
        synchronized(this){
            int closedCount = 0;
            int errCount = 0;

            for( Map.Entry<Statement,Connection> en : statements.entrySet() ){
                Statement st = en.getKey();
                if( st==null )continue;

                //String name = nameOf(cn);
                String name = "unnamed statement";
                try {
                    if( st.isClosed() ){
                        continue;
                    }

                    logFiner("close statement {0}", name);
                    st.close();
                    statements.remove(st);

                    closedCount++;
                } catch (SQLException ex) {
                    Logger.getLogger(ConnectPool.class.getName()).log(Level.SEVERE,
                        "close statement "+name+
                            " error: "+ex.getClass().getSimpleName()+
                            " ,message="+ex.getMessage()+
                            " ,error code="+ex.getErrorCode()
                        ,ex);

                    errCount++;
                }
            }

            logFiner("closeAllStatements() closed={0} errors={1}", closedCount, errCount);
        }
    }

    /**
     * Закрывает shared соединение с базой данных
     * @param conn shared соединение с базой данных
     * @throws SQLException Ошибка при работе с БД
     */
    public void closeShared(Connection conn) throws SQLException {
        if( conn==null )throw new IllegalArgumentException("conn == null");
        Connection sconn = null;
        ConnectPoolEvent.Disconnected ev = null;

        synchronized(this){
            sconn = sourceOf(conn);
            boolean shared = isShared(conn);
            if( shared==false ){
                throw new IllegalArgumentException("connection not shared");
            }

            sconn.close();

            ev = new ConnectPoolEvent.Disconnected(this);
            ev.setConnection(conn);
            ev.setSourceConnection(sconn);
            ev.setProxy( isProxy(conn) );

            sharedConnections.remove(sconn);
        }

        fireDataEvent(ev);
    }

    /**
     * Проверяет является ли соединение shared
     * @param conn соединение с бд
     * @return true - является shared соединением
     */
    public boolean isShared(Connection conn){
        if( conn==null )throw new IllegalArgumentException("conn == null");
        synchronized(this){
            Connection sconn = sourceOf(conn);
            String sharedName = sharedConnections.get(sconn);
            return sharedName!=null;
        }
    }

    /**
     * Закрытие всех соединений с СУБД
     * @param addEvents true - Добавить сообщения в очередь.
     */
    public void closeAllConnections(boolean addEvents){
        logFine("closeAllConnections()");
        synchronized(this){
            int closedCount = 0;
            int errCount = 0;

            for( Map.Entry<Connection,DataSource> en : connections.entrySet().toArray(new Map.Entry[]{}) ){
                Connection cn = en.getKey();
                if( cn==null )continue;

                String name = nameOf(cn);
                try {
                    if( cn.isClosed() ){
                        continue;
                    }

                    logFiner("close connection {0}", name);
                    cn.close();
                    connections.remove(cn);

                    closedCount++;
                } catch (SQLException ex) {
                    Logger.getLogger(ConnectPool.class.getName()).log(Level.SEVERE,
                        "close connection "+name+
                            " error: "+ex.getClass().getSimpleName()+
                            " ,message="+ex.getMessage()+
                            " ,error code="+ex.getErrorCode()
                        ,ex);

                    errCount++;
                }
            }

            logFiner("closeAllConnections() closed={0} errors={1}", closedCount, errCount);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="shutdown()">
    /**
     * Закрывает все соединения в пуле
     */
    public void shutdown(){
        shutdown(true);
        fireEventQueue();
    }

    /**
     * Закрытие всех запросов, соединений и остановка фоновых заданий.
     * @param addEvents  true - Добавить сообщения в очередь.
     */
    public void shutdown(boolean addEvents){
        logFine("shutdown()");
        synchronized(this){
            logFiner("timer.cancel()");
            synchronized( ConnectPool.class ){
                getTimer().cancel();
                if( timer!=null ){
                    timer = null;
                }
            }

            closeAllStatements(addEvents);
            closeAllConnections(addEvents);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="sources : EventMap<String,DataSource>">
    private EventMap<String,DataSource> sources;

    /**
     * Возвращает карту именнованных источников данных
     * @return источники данных
     */
    public EventMap<String,DataSource> getSources(){
        synchronized(this){
            if( sources!=null )return sources;
            sources = new SyncEventMap<>(new LinkedHashMap<String, DataSource>(), this);
            logFiner("create sources : SyncEventMap");
            return sources;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="registerSource(name,ds)">
    private boolean skipRegistered = true;

    /**
     * Указывает пропускать уже зарегистрированные соединения или перезаписывать существующие.
     *
     * @return true - пропускать (по умолчанию)
     * @see #registerSource(java.lang.String, javax.sql.DataSource)
     * @see #registerSource(java.lang.String, javax.sql.DataSource, xyz.cofe.sql.ConnectOptions)
     */
    public boolean isSkipRegistered() {
        synchronized(this){
            return skipRegistered;
        }
    }

    /**
     * Указывает пропускать уже зарегистрированные соединения или перезаписывать существующие.
     * @param skipRegistered true - пропускать (по умолчанию)
     * @see #registerSource(java.lang.String, javax.sql.DataSource)
     * @see #registerSource(java.lang.String, javax.sql.DataSource, xyz.cofe.sql.ConnectOptions)
     */
    public void setSkipRegistered(boolean skipRegistered) {
        Object old,cur;
        synchronized(this){
            old = this.skipRegistered;
            this.skipRegistered = skipRegistered;
            cur = this.skipRegistered;
            logFiner( "change skipRegistered from={0} to={1}", old,cur );
        }
    }

    /**
     * Регистрирует источник данных
     * @param name  Имя истоничка
     * @param ds Источник данных
     * @see #registerSource(java.lang.String, javax.sql.DataSource, xyz.cofe.sql.ConnectOptions)
     */
    public void registerSource( String name, DataSource ds ){
        registerSource(name, ds, null);
    }

    /**
     * Регистрирует источник данных. <p>
     * Если источник уже зарегистрирован и свойство skipRegistered = true, то новых источник не регистрируется. <br>
     * Если skipRegistered = false, то источник данных регистриуется (перетерая ранее зарегистрированный одноименный, если есть)
     * @param name Имя истоничка
     * @param ds Источник данных
     * @param copt Опции соединения или null
     */
    public void registerSource( String name, DataSource ds, ConnectOptions copt ){
        if( name==null )throw new IllegalArgumentException("name == null");
        if( ds==null )throw new IllegalArgumentException("ds == null");
        synchronized(this){
            Map<String,DataSource> m = getSources();
            if( m.containsKey(name) && skipRegistered ){
                return;
            }
            m.put(name, ds);
            logFine("register source: name=\"{0}\" ds=\"{1}\"", name, ds);

            if( copt!=null ){
                options.put(ds, copt);
                logFiner("register source options: name=\"{0}\" ds=\"{1}\" opts={2}", name, ds, copt);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="nameOf()">
    /**
     * Возвращает имя под которым зарегистрированн источник данных
     * @param ds источник данных
     * @return имена под которым зарегистрирован источник данных
     */
    public Set<String> namesOf( DataSource ds ){
        if( ds==null )throw new IllegalArgumentException("ds == null");
        LinkedHashSet<String> names = new LinkedHashSet<>();
        synchronized(this){
            for(Map.Entry<String,DataSource> en : getSources().entrySet() ){
                if( en==null )continue;
                if( en.getKey()==null )continue;
                if( Objects.equals(ds, en.getValue()) ){
                    names.add(en.getKey());
                }
            }
        }
        return names;
    }

    /**
     * Возвращает имя под которым зарегистрированн источник данных
     * @param ds источник данных
     * @return имя или null
     */
    public String nameOf( DataSource ds ){
        if( ds==null )throw new IllegalArgumentException("ds == null");
        synchronized(this){
            for(Map.Entry<String,DataSource> en : getSources().entrySet() ){
                if( en==null )continue;
                if( en.getKey()==null )continue;
                if( Objects.equals(ds, en.getValue()) ){
                    return en.getKey();
                }
            }
        }
        return null;
    }

    /**
     * Возвращает имя под которым зарегистрированно соединение с СУБД
     * @param conn соединение с СУБД
     * @return Имена под каторым зарегистрировано соединение
     */
    public Set<String> namesOf( Connection conn ){
        if( conn==null )throw new IllegalArgumentException("conn == null");
        LinkedHashSet<String> names = new LinkedHashSet<>();

        conn = ConnectPool.this.sourceOf(conn);

        String cachedName = connectionName.get(conn);
        if( cachedName!=null ){
            names.add(cachedName);
            return names;
        }

        DataSource ds = connections.get(conn);
        if( ds==null )return names;

        names.addAll(namesOf(ds));

        return names;
    }

    private final WeakHashMap<Connection,String> connectionName = new WeakHashMap<>();

    /**
     *  Возвращает имя под которым зарегистрированно соединение с СУБД
     * @param conn соединение с СУБД
     * @return Имя соединения или null
     */
    public String nameOf( Connection conn ){
        if( conn==null )throw new IllegalArgumentException("conn == null");

        String cachedName = null;
        synchronized(connectionName){

            cachedName = connectionName.get(conn);
            if( cachedName!=null )return cachedName;

            Connection sconn = ConnectPool.this.sourceOf(conn);
            if( sconn!=null ){
                cachedName = connectionName.get(conn);
                if( cachedName!=null )return cachedName;
            }

            Set<String> names = namesOf(conn);
            if( names==null || names.isEmpty() )return null;
            for( String n : names ){
                return n;
            }
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getConnections()/getConnectionsMap()">
    private WeakHashMap<Connection,String> sharedConnections = new WeakHashMap<>();

    /**
     * Отношение Соединение СУБД / Источник данных
     */
    private WeakHashMap<Connection,DataSource> connections = new WeakHashMap<>();

    /**
     * Отношение Соединение СУБД / Опции соединения
     */
    private WeakHashMap<Connection,ConnectOptions> connectionOptions = new WeakHashMap<>();

    /**
     * Отношение Соединение СУБД / Время создания соединения
     */
    private WeakHashMap<Connection,Long> connectionCreateTime = new WeakHashMap<>();

    /**
     * Удаляет ссылки и связанные объекты
     * @param conn Соединение
     */
    public void cleanup( Connection conn ){
        if( conn==null )return;
        synchronized( this ){
            LinkedHashSet<Statement> sts = new LinkedHashSet<>();
            for( Map.Entry<Statement,Connection> e : statements.entrySet() ){
                if( Objects.equals(e.getValue(), conn) && e.getKey()!=null ){
                    sts.add(e.getKey());
                }
            }
            for( Statement st : sts ){
                statements.remove(st);
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="getConnections()">

    /**
     * Возвращает соединения с СУБД
     * @param autoCreateProxy true - Автоматически создавать PROXY объект
     * @return Соединения с СУБД
     */
    public Set<Connection> getConnections(boolean autoCreateProxy){
        //boolean autoCreateProxy = true;

        LinkedHashSet<Connection> conn = new LinkedHashSet<>();
        synchronized(this){
            for( Map.Entry<Connection,DataSource> e : connections.entrySet() ){
                Connection c = e.getKey();
                if( c==null )continue;

                if( autoCreateProxy && !isProxy(c) ){
                    ConnectOptions co = connectionOptions.get(c);
                    co = co!=null ? co : getDefaultOptions();

                    if( co.isProxyConnection() ){
                        c = proxy(c, co);
                    }
                }

                conn.add(c);
            }
        }

        logFiner( "getConnections({0}) return {1} connections",autoCreateProxy,conn.size() );
        return conn;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getSourceConnections()">
    /**
     * Возвращает соединения с СУБД. <p>
     * Аналогично: <code>getConnections(false)</code>
     * @return соединения с СУБД
     */
    public Set<Connection> getSourceConnections(){
        return getConnections(false);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getConnections()">
    /**
     * Возвращает соединения с СУБД. <p>
     * Аналогично: <code>getConnections(true)</code>
     * @return соединения с СУБД
     */
    public Set<Connection> getConnections(){
        return getConnections(true);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getConnectionsMap(a)">
    /**
     * Возвращает карту источников данныи и их соединений
     * @param autoCreateProxy true - Автоматически создавать PROXY для соединений
     * @return Карта Источник данных =&gt; Соединения созданные этим источником данных
     */
    public Map<DataSource,Set<Connection>> getConnectionsMap(boolean autoCreateProxy){
        //boolean autoCreateProxy = true;
        int sumConn = 0;

        synchronized(this){
            LinkedHashMap<DataSource,Set<Connection>> map = new LinkedHashMap<>();
            for( Map.Entry<Connection,DataSource> e : connections.entrySet() ){
                Connection c = e.getKey();
                DataSource ds = e.getValue();
                if( ds==null )continue;

                Set<Connection> sc = map.get(ds);
                if( sc==null ){
                    sc = new LinkedHashSet<>();
                    map.put(ds, sc);
                }

                if( c!=null ){
                    if( autoCreateProxy && !isProxy(c) ){
                        ConnectOptions co = connectionOptions.get(c);
                        co = co!=null ? co : getDefaultOptions();

                        if( co.isProxyConnection() ){
                            c = proxy(c, co);
                        }
                    }

                    sc.add(c);
                }

                sumConn++;
            }

            logFiner( "getConnectionsMap({0}) return map.size()={1} conn.sum={2}", autoCreateProxy, map.size(), sumConn );
            return map;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getSourceConnectionsMap()">
    /**
     * Возвращает карту источников данныи и их соединений. <p>
     * Аналогично: <code>getConnectionsMap(false)</code>
     * @return Карта Источник данных =&gt; Соединения созданные этим источником данных
     */
    public Map<DataSource,Set<Connection>> getSourceConnectionsMap(){
        return getConnectionsMap(false);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getConnectionsMap()">
    /**
     * Возвращает карту источников данныи и их соединений. <p>
     * Аналогично: <code>getConnectionsMap(true)</code>
     * @return Карта Источник данных =&gt; Соединения созданные этим источником данных
     */
    public Map<DataSource,Set<Connection>> getConnectionsMap(){
        return getConnectionsMap(true);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getTotalConnectionsCount()">
    /**
     * Возвращает общее кол-во соединений с СУБД
     * @return Кол-во соединений с СУБД
     */
    public int getTotalConnectionsCount(){
        synchronized(this){
            return connections.size();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getOpenConnections(a)">
    /**
     * Возвращает открытиые соединения с СУБД
     * @param autoCreateProxy true - автоматически создавать PROXY
     * @return Открытые соединения с СУБД
     */
    public Set<Connection> getOpenConnections(boolean autoCreateProxy){
        Set<Connection> conns = findConnections(openConnection());
        if( !autoCreateProxy ){
            logFiner("getOpenConnections({0}) return {1} connections", autoCreateProxy, conns.size());
            return conns;
        }

        Set<Connection> res = new LinkedHashSet<>();
        for( Connection c : conns ){
            if( c==null )continue;

            if( autoCreateProxy && !isProxy(c) ){
                ConnectOptions co = connectionOptions.get(c);
                co = co!=null ? co : getDefaultOptions();

                if( co.isProxyConnection() ){
                    c = proxy(c, co);
                }
            }

            res.add(c);
        }

        logFiner("getOpenConnections({0}) return {1} connections", autoCreateProxy, res.size());
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="openConnection()">
    private Predicate<Connection> openConnection(){
        return new Predicate<Connection>() {
            @Override
            public boolean validate(Connection conn) {
                try {
                    if( conn==null )return false;
                    boolean closed = conn.isClosed();
                    return !closed;
                } catch (SQLException ex) {
                    Logger.getLogger(ConnectPool.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
            }
        };
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getOpenConnectionsCount()">
    /**
     * Возвращает кол-во открытых соединений
     * @return кол-во открытых соединений
     */
    public int getOpenConnectionsCount(){
        return findConnections(openConnection()).size();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="findConnections()">
    private Set<Connection> findConnections( Predicate<Connection> filter ){
        if( filter==null )throw new IllegalArgumentException("filter == null");
        Set<Connection> conns = new LinkedHashSet<>();
        synchronized(this){
            for( Map.Entry<Connection,DataSource> en : connections.entrySet() ){
                Connection cn = en.getKey();
                if( cn==null )continue;
                try{
                    if( filter.validate(cn) ){
                        conns.add(cn);
                    }
                }catch( Throwable ex ){
                    logException(ex);
                }
            }
        }
        return conns;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="proxy statement">
    protected final WeakHashMap<Statement,Connection> statements = new WeakHashMap<>();
    protected final WeakHashMap<Statement,Long> statementCreateTime = new WeakHashMap<>();

    /**
     * Создает строителя для Statment
     * @param meth Метод из Connection, который вызвал создание Statement
     * @param st Исходный Statement
     * @return Строитель
     * @see Connection#createStatement(int, int, int)
     * @see Connection#prepareStatement(java.lang.String)
     * @see Connection#prepareCall(java.lang.String)
     */
    public GenericProxy.Builder proxyBuilder( Method meth, Statement st ){
        if( meth==null )throw new IllegalArgumentException("meth == null");
        if( st==null )throw new IllegalArgumentException("st == null");

        GenericProxy.Builder bldr = null;

        switch( meth.getName() ){
            case "createStatement": {
                if( st instanceof Statement ){
                    bldr = GenericProxy.builder((Statement)st, Statement.class);
                }
            } break;
            case "prepareStatement": {
                if( st instanceof PreparedStatement ){
                    bldr = GenericProxy.builder((PreparedStatement)st, PreparedStatement.class);
                }
            } break;
            case "prepareCall": {
                if( st instanceof CallableStatement ){
                    bldr = GenericProxy.builder((CallableStatement)st, CallableStatement.class);
                }
            } break;
        }

        return bldr;
    }

    /**
     * Создает строителя для Statment на основе типа Statment
     * @param st Исходный Statement
     * @return Строитель
     * @see Statement
     * @see PreparedStatement
     * @see CallableStatement
     */
    public GenericProxy.Builder proxyBuilder( Statement st ){
        if( st==null )throw new IllegalArgumentException("st == null");
        GenericProxy.Builder bldr = null;

        if( st instanceof CallableStatement ){
            bldr = GenericProxy.builder((CallableStatement)st, CallableStatement.class);
        }else if( st instanceof PreparedStatement ){
            bldr = GenericProxy.builder((PreparedStatement)st, PreparedStatement.class);
        }else{
            bldr = GenericProxy.builder((Statement)st, Statement.class);
        }

        return bldr;
    }

    /**
     * Создает proxy для Statement и добавляет StatementTracker
     * @param meth Метод из Connection, который вызвал создание Statement
     * @param conn Содение с БД
     * @param st Statement
     * @return proxy
     * @see #proxyBuilder(java.lang.reflect.Method, java.sql.Statement)
     * @see Connection#createStatement(int, int, int)
     * @see Connection#prepareStatement(java.lang.String)
     * @see Connection#prepareCall(java.lang.String)
     */
    public Statement proxy( Method meth, Connection conn, Statement st ){
        if( meth==null )throw new IllegalArgumentException("meth == null");
        if( st==null )throw new IllegalArgumentException("st == null");
        if( conn==null )throw new IllegalArgumentException("conn == null");

        GenericProxy.Builder bldr = proxyBuilder(meth, st);

        StatementTracker stt = new StatementTracker(this, conn, (Statement)st);
        stt.setCollectName(
            InvokeActivityStat.exclude(
                InvokeActivityStat.simpleMethodName(),
                "toString","hashCode", "equals"
            )
        );

        bldr.add(stt);

        return (Statement)bldr.create();
    }

    /**
     * Создает proxy для Statement и добавляет StatementTracker
     * @param conn Содение с БД
     * @param st Statement
     * @return proxy
     * @see #proxyBuilder(java.sql.Statement)
     * @see StatementTracker
     * @see Connection#createStatement(int, int, int)
     * @see Connection#prepareStatement(java.lang.String)
     * @see Connection#prepareCall(java.lang.String)
     */
    public Statement proxy( Connection conn, Statement st ){
        if( st==null )throw new IllegalArgumentException("st == null");
        if( conn==null )throw new IllegalArgumentException("conn == null");

        GenericProxy.Builder bldr = proxyBuilder(st);

        StatementTracker stt = new StatementTracker(this, conn, (Statement)st);
        stt.setCollectName(
            InvokeActivityStat.exclude(
                InvokeActivityStat.simpleMethodName(),
                "toString","hashCode", "equals"
            )
        );

        bldr.add(stt);

        logFine("create proxy statement");
        return (Statement)(bldr.create());
    }

    /**
     * Создает proxy для PreparedStatement и добавляет StatementTracker
     * @param conn Содение с БД
     * @param st PreparedStatement
     * @return proxy
     * @see #proxyBuilder(java.sql.Statement)
     * @see StatementTracker
     */
    public PreparedStatement proxy( Connection conn, PreparedStatement st ){
        if( st==null )throw new IllegalArgumentException("st == null");
        if( conn==null )throw new IllegalArgumentException("conn == null");

        GenericProxy.Builder bldr = proxyBuilder(st);

        StatementTracker stt = new StatementTracker(this, conn, (Statement)st);
        stt.setCollectName(
            InvokeActivityStat.exclude(
                InvokeActivityStat.simpleMethodName(),
                "toString","hashCode", "equals"
            )
        );

        bldr.add(stt);

        logFine("create proxy (prepare)statement");
        return (PreparedStatement)(bldr.create());
    }

    /**
     * Создает proxy для PreparedStatement и добавляет StatementTracker
     * @param conn Содение с БД
     * @param st CallableStatement
     * @return proxy
     * @see #proxyBuilder(java.sql.Statement)
     * @see StatementTracker
     */
    public CallableStatement proxy( Connection conn, CallableStatement st ){
        if( st==null )throw new IllegalArgumentException("st == null");
        if( conn==null )throw new IllegalArgumentException("conn == null");

        GenericProxy.Builder bldr = proxyBuilder(st);

        StatementTracker stt = new StatementTracker(this, conn, (Statement)st);
        stt.setCollectName(
            InvokeActivityStat.exclude(
                InvokeActivityStat.simpleMethodName(),
                "toString","hashCode", "equals"
            )
        );

        bldr.add(stt);

        logFine("create proxy (call)statement");
        return (CallableStatement)(bldr.create());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getStatements()">
    /**
     * Возвращает текущий набор Statement (запросов)
     * @param autoCreateProxy true - автоматически создавать proxy и его возвращать, если еще не создан
     * @return текущий набор запросов
     */
    public Set<Statement> getStatements(boolean autoCreateProxy){
        LinkedHashSet<Statement> stmts = new LinkedHashSet<>();
        synchronized(statements){
            for( Map.Entry<Statement,Connection> en : statements.entrySet()){
                if( en==null )continue;

                Statement st = en.getKey();
                if( st==null )continue;

                Connection cn = en.getValue();
                if( isProxy(cn) )cn = sourceOf(cn);

                if( cn!=null && !isProxy(st) && autoCreateProxy ){
                    if( st instanceof CallableStatement ){
                        st = proxy(cn, (CallableStatement)st);
                    }else if( st instanceof PreparedStatement ){
                        st = proxy(cn, (PreparedStatement)st);
                    }else{
                        st = proxy(cn, (Statement)st);
                    }
                }

                stmts.add(st);
            }
        }
        return stmts;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getStatementsMap()">
    /**
     * Возвращает карту Соединение / Запрос
     * @param autoCreateProxy true - автоматически создавать proxy и его возвращать, если еще не создан
     * @return Карта текущих запросов
     */
    public Map<Connection,Set<Statement>> getStatementsMap( boolean autoCreateProxy ){
        LinkedHashMap<Connection,Set<Statement>> map = new LinkedHashMap<>();
        synchronized(statements){
            for( Map.Entry<Statement,Connection> en : statements.entrySet()){
                if( en==null )continue;

                Statement sst = en.getKey();
                Statement st = sst;
                if( st==null )continue;

                Connection scn = en.getValue();
                Connection cn = scn;
                if( isProxy(cn) )cn = sourceOf(cn);

                if( cn!=null && !isProxy(st) && autoCreateProxy ){
                    if( st instanceof CallableStatement ){
                        st = proxy(cn, (CallableStatement)st);
                    }else if( st instanceof PreparedStatement ){
                        st = proxy(cn, (PreparedStatement)st);
                    }else{
                        st = proxy(cn, (Statement)st);
                    }
                }

                if( scn!=null && st!=null ){
                    Set<Statement> ss = map.get(scn);
                    if( ss==null ){
                        ss = new LinkedHashSet<>();
                        map.put(scn, ss);
                    }

                    ss.add(st);
                }
            }
        }
        return map;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="registerStatement()">
    /**
     * Регистрация созданного запроса (Statement)
     * @param conn Соединение с БД
     * @param st Запрос
     */
    public void registerStatement( Connection conn, Statement st ){
        if( conn==null )throw new IllegalArgumentException("conn == null");
        if( st==null )throw new IllegalArgumentException("st == null");

        DataEvent de = null;
        synchronized(statements){
            Statement sst = isProxy(st) ? sourceOf(st) : st;
            if( sst!=null ){ st = sst; }

            Connection sconn = isProxy(conn) ? sourceOf(conn) : conn;
            if( sconn!=null ){ conn = sconn; }

            boolean newStatement = !statements.containsKey(st);
            statements.put(st, conn);

            if( newStatement ){
                logFiner("register new statement");
            }else{
                logFiner("already registered statement");
            }
        }

        if( de!=null ){
            addDataEvent(de);
            fireEventQueue();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="createTimeOf()">
    /**
     * Возвращает время создания соединения
     * @param conn соединение
     * @return время или -1
     */
    public long createTimeOf( Connection conn ){
        if( conn==null )return -1;
        conn = sourceOf(conn);
        synchronized(this){
            Long ct = connectionCreateTime.get(conn);
            if( ct!=null )return ct;
        }
        return -1;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="defaultOptions : ConnectOptions">
    private ConnectOptions defaultOptions;

    /**
     * Опции соединения с БД по умолчани.
     * @return опции
     */
    public ConnectOptions getDefaultOptions(){
        synchronized(this){
            if( defaultOptions!=null )return defaultOptions;
            ConnectOptionsProperties coProps = new ConnectOptionsProperties();
            defaultOptions = coProps;

            coProps.setProxyConnection(true);
            coProps.setCollectActivityStat(true);

            return defaultOptions;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="invokeStats : Map<Object,InvokeActivityStat>">
    private final WeakHashMap<Object,InvokeActivityStat> invokeStats = new WeakHashMap<>();
    public WeakHashMap<Object,InvokeActivityStat> getInvokeStats(){
        return invokeStats;
    }

    /**
     * Возвращает статистику активности соединения
     * @param conn соединение
     * @return статистика
     */
    public InvokeActivityStat activityStatOf( Connection conn ){
        if( conn==null )throw new IllegalArgumentException("conn == null");
        conn = sourceOf(conn);
        synchronized(this){
            InvokeActivityStat st = invokeStats.get(conn);
            if( st==null ){
                st = new InvokeActivityStat();
                invokeStats.put(conn, st);
            }
            return st;
        }
    }

    /**
     * Возвращает статистику активности запроса
     * @param stmt Запрос
     * @return статистика
     */
    public InvokeActivityStat activityStatOf( Statement stmt ){
        if( stmt==null )throw new IllegalArgumentException("stmt == null");
        stmt = sourceOf(stmt);
        synchronized(this){
            InvokeActivityStat st = invokeStats.get(stmt);
            if( st==null ){
                st = new InvokeActivityStat();
                invokeStats.put(stmt, st);
            }
            return st;
        }
    }

    /**
     * Возвращает статистику активности запроса
     * @param stmt Запрос
     * @return статистика
     */
    public InvokeActivityStat activityStatOf( PreparedStatement stmt ){
        if( stmt==null )throw new IllegalArgumentException("stmt == null");
        stmt = sourceOf(stmt);
        synchronized(this){
            InvokeActivityStat st = invokeStats.get(stmt);
            if( st==null ){
                st = new InvokeActivityStat();
                invokeStats.put(stmt, st);
            }
            return st;
        }
    }

    /**
     * Возвращает статистику активности запроса
     * @param stmt Запрос
     * @return статистика
     */
    public InvokeActivityStat activityStatOf( CallableStatement stmt ){
        if( stmt==null )throw new IllegalArgumentException("stmt == null");
        stmt = sourceOf(stmt);
        synchronized(this){
            InvokeActivityStat st = invokeStats.get(stmt);
            if( st==null ){
                st = new InvokeActivityStat();
                invokeStats.put(stmt, st);
            }
            return st;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="options">
    protected WeakHashMap<DataSource,ConnectOptions> options = new WeakHashMap<>();

    /**
     * Возвращает опции соединения с БД
     * @param ds Источник данных
     * @return Опции
     */
    public ConnectOptions optionsOf( DataSource ds ){
        if( ds==null )throw new IllegalArgumentException("ds == null");
        if( ds instanceof ConnectOptions ){
            logFiner("use options through interface for ds={0}", ds);
            return (ConnectOptions)ds;
        }

        ConnectOptions copt = options.get(ds);
        if( copt!=null )return copt;

        logFiner("use default options for ds={0}", ds);
        return getDefaultOptions();
    }

    /**
     * Возвращает опции соединения для указанного коннекта
     * @param conn коннект к БД
     * @return опции
     */
    public ConnectOptions optionsOf( Connection conn ){
        if( conn==null )return null;

        ConnectOptions co = connectionOptions.get(conn);
        if( co!=null )return co;

        co = connectionOptions.get(sourceOf(conn));
        return co;
    }

    /**
     * Возвращает опции соединения для указанного запроса
     * @param st запрос
     * @return опции
     */
    public ConnectOptions optionsOf( Statement st ){
        if( st==null )return null;

        Connection cn = statements.get(st);
        if( cn==null ){
            cn = statements.get(sourceOf(st));
        }

        if( cn==null )return null;

        return optionsOf(cn);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="sourceOf / isProxy">
    /**
     * Возвращает исходный объект для указанного proxy объекта
     * @param conn proxy объект
     * @return исходный объект
     */
    public Connection sourceOf( Connection conn ){
        if( conn==null )throw new IllegalArgumentException("conn == null");
        return sourceOf(Connection.class, conn);
    }

    /**
     * Возвращает исходный объект для указанного proxy объекта
     * @param conn proxy объект
     * @return исходный объект
     */
    public CallableStatement sourceOf( CallableStatement conn ){
        if( conn==null )throw new IllegalArgumentException("conn == null");
        return sourceOf(CallableStatement.class, conn);
    }

    /**
     * Возвращает исходный объект для указанного proxy объекта
     * @param conn proxy объект
     * @return исходный объект
     */
    public Statement sourceOf( Statement conn ){
        if( conn==null )throw new IllegalArgumentException("conn == null");
        return sourceOf(Statement.class, conn);
    }

    /**
     * Возвращает исходный объект для указанного proxy объекта
     * @param conn proxy объект
     * @return исходный объект
     */
    public PreparedStatement sourceOf( PreparedStatement conn ){
        if( conn==null )throw new IllegalArgumentException("conn == null");
        return sourceOf(PreparedStatement.class, conn);
    }

    /**
     * Возвращает исходный объект для указанного proxy объекта
     * @param <T> Интересующий тип объекта
     * @param cls Тип интересующего объекта
     * @param obj proxy объект
     * @return исходный объект
     */
    public <T> T sourceOf( Class<? extends T> cls, T obj ){
        if( obj==null )throw new IllegalArgumentException("conn == null");

        LinkedHashSet<T> visited = new LinkedHashSet<>();
        visited.add(obj);
        while( true ){
            if( !(obj instanceof GetProxyTarget) ){
                break;
            }

            Object trgt = ((GetProxyTarget)obj).getProxyTarget();
            if( trgt==null )return obj;

            boolean assign = cls.isAssignableFrom(trgt.getClass());
            if( !assign )return obj;

            T c = (T)trgt;

            if( visited.contains(c) ){
                break;
            }
            obj = c;
        }

        return obj;
    }

    /**
     * Проверяет что указанный объект является proxy
     * @param conn проверяемый объект
     * @return true - является Proxy
     */
    public boolean isProxy( Object conn ){
        if( conn==null )throw new IllegalArgumentException("conn == null");
        return conn instanceof GetProxyTarget;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="proxy(conn,statement,copts)">
    /*public Statement proxy( final Connection conn, final Statement st, ConnectOptions copts ){
        if( conn==null )throw new IllegalArgumentException("conn == null");
        if( copts==null )throw new IllegalArgumentException("copts == null");
        if( st instanceof GetProxyTarget )return st;

        logFine("create proxy for statement = {0}", st);

        GenericProxy.Builder<Statement> bldr = GenericProxy.builder(st, Statement.class);

        MethodCallListener connActivityTracker =
            InvokeActivityStat.createActivityTracker(
                activityStatOf(sourceOf(conn)),
                true,
                InvokeActivityStat.constName("@child")
            );

        bldr.add(connActivityTracker);
        logFiner("create activity stat collector for connection {0}", conn);

        MethodCallListener stActivityTracker =
            InvokeActivityStat.createActivityTracker(
                activityStatOf(st),
                true,
                null
            );

        bldr.add(stActivityTracker);
        logFiner("create activity stat collector for statement {0}", st);

        Statement proxy = bldr.create();
        return proxy;
    }*/
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="proxy()">
    /**
     * Создает (если еще не создан) proxy для соединения с БД
     * @param conn Соединение с БД
     * @param copts Опции
     * @return Proxy соединение
     */
    public Connection proxy( final Connection conn, final ConnectOptions copts ){
        if( conn==null )throw new IllegalArgumentException("conn == null");
        if( copts==null )throw new IllegalArgumentException("copts == null");
        if( conn instanceof GetProxyTarget )return conn;

        final WeakReference<Connection> wconn = new WeakReference<>(conn);
        final WeakReference<ConnectPool> wcpool = new WeakReference<>(this);

        logFine("create proxy for source connection = {0}", conn);
        GenericProxy.Builder<Connection> bldr = GenericProxy.builder(conn, Connection.class);
        bldr.loggerName("xyz.cofe.sql.proxy.connection."+nameOf(conn)+"");

        ConnectionTracker ct = new ConnectionTracker(this, conn);
        bldr.add(ct);
        logFiner("add conntection tracker");

        if( copts.isCollectActivityStat() ){
            ct.setCollectName(
                InvokeActivityStat.exclude(
                    InvokeActivityStat.simpleMethodName(),
                    "toString","hashCode", "equals"
                )
            );
            logFiner("tracking activity for connection");

            InvokeActivityStat iastat = activityStatOf(conn);
            iastat.collect("new");
        }

        ct.setShared(copts.isShared());
        logFiner("shared connection = {0}",ct.isShared());

        ct.setTrackClose(true);
        logFiner("tracking close() for connection");

        ct.setTrackStatements(true);
        logFiner("tracking statements for connection");

        Connection proxyConn = bldr.create();
        if( proxyConn!=null ){
            logFiner( "proxy connection created" );
        }

        synchronized(this){
            connectionOptions.put(proxyConn, copts);
        }

        return proxyConn;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="connect()">
    /**
     * Устанавливает соединение с БД
     * @param source имя соединения
     * @return соединение с БД
     * @throws java.sql.SQLException Ошибка SQL
     */
    public Connection connect( String source )
        throws SQLException
    {
        if( source==null )throw new IllegalArgumentException("source == null");
        return connect(source, null, null);
    }

    private String makeLoggerName( String str ){
        StringBuilder sb = new StringBuilder();
        for( int ci=0; ci<str.length(); ci++){
            char ch = str.charAt(ci);
            if( Character.isLetter(ch) ||
                Character.isDigit(ch)
            ){
                sb.append(ch);
            }else{
                sb.append("_");
            }
        }
        return sb.toString();
    }

    /**
     * Устанавливает соединение с БД
     * @param source имя соединения
     * @param username Имя пользователя БД
     * @param password Пароль пользователя БД
     * @return соединение с БД
     * @throws java.sql.SQLException Ошибка SQL
     */
    public Connection connect( String source, String username, String password )
        throws SQLException
    {
        if( source==null )throw new IllegalArgumentException("source == null");
        //if( username==null )throw new IllegalArgumentException("username == null");
        //if( password==null )throw new IllegalArgumentException("password == null");

        synchronized(this){
            if( username!=null ){
                logFine("connect( source=\"{0}\", username=\"{1}\", password=*** )", source, username);
            }else{
                logFine("connect( source=\"{0}\" )", source);
            }

            if( !getSources().containsKey(source) )
                throw new IllegalArgumentException(
                    "source \""+source+"\" not registered"
                );

            DataSource ds = getSources().get(source);
            if( ds==null ){
                throw new IllegalArgumentException(
                    "source \""+source+"\" not registered"
                );
            }

            ConnectOptions copts = optionsOf(ds);
            if( copts.isShared() ){
                for( Map.Entry<Connection,String> sharedConnEn : sharedConnections.entrySet() ){
                    if( sharedConnEn==null )continue;

                    Connection sconn = sharedConnEn.getKey();
                    String sname = sharedConnEn.getValue();
                    if( sconn==null )continue;
                    if( sname==null )continue;
                    if( sconn.isClosed() )continue;

                    if( sname.equals(source) ){
                        logger.log(Level.FINE, "already connected \"{0}\" as shared", sname);
                        return proxy(sconn, copts);
                    }
                }
            }

            Connection sourceConn = null;
            if( username!=null ){
                sourceConn = ds.getConnection(username,password);
            }else{
                sourceConn = ds.getConnection();
            }
            connectionCreateTime.put(sourceConn, System.currentTimeMillis());

            String dsName = source;
            String connName = dsName+"_"+connIdSeq.incrementAndGet();
            synchronized(connectionName){
                connectionName.put(sourceConn, connName);
            }

            logFiner( "created connection from source={0}", source );

            connections.put(sourceConn, ds);
            connectionOptions.put(sourceConn, copts);

            Connection proxyConn = null;
            if( copts!=null ){
                if( copts.isProxyConnection() ){
                    proxyConn = proxy(sourceConn, copts);
                }
                if( copts.isShared() ){
                    logger.log(Level.FINE, "use connection as shared (name={0})", source);
                    sharedConnections.put(sourceConn, source);
                }
            }

            Connection clientConn = proxyConn != null ? proxyConn : sourceConn;

            ConnectPoolEvent.Connected connEn = new ConnectPoolEvent.Connected(this);
            connEn.setConnection(clientConn);
            connEn.setSourceConnection(sourceConn);
            connEn.setProxy( proxyConn!=null );
            connEn.setDataSource(ds);
            connEn.setDataSourceName(source);

            addDataEvent(connEn);
            fireEventQueue();

            return clientConn;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="removeClosedConnections()">
    /**
     * Удаляет закрытые соединения из памяти
     */
    public void removeClosedConnections(){
        removeClosedConnections(true);
        fireEventQueue();
    }

    /**
     * Удаляет закрытые соединения их памяти
     * @param addEvents true - добавляет событие в очередь
     */
    public void removeClosedConnections(boolean addEvents){
        logFine("removeClosedConnections()");
        synchronized(this){
            int closeCount = 0;
            LinkedHashSet<Connection> closedConn = new LinkedHashSet<>();

            for( Map.Entry<Connection,DataSource> en : connections.entrySet() ){
                Connection cn = en.getKey();
                if( cn==null )continue;

                try {
                    if( cn.isClosed() ){
                        closedConn.add(cn);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ConnectPool.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            for( Connection cn : closedConn ){
                String name = nameOf(cn);
                logFiner("remove closed connection {0}", name==null ? "unnamed" : name);
                closeCount++;
                connections.remove(cn);
            }

            for( Map.Entry<Connection,String> en : sharedConnections.entrySet() ){
                Connection cn = en.getKey();
                if( cn==null )continue;

                try {
                    if( cn.isClosed() ){
                        logger.log(Level.FINER, "remove closed shared connection {0}", en.getValue());
                        sharedConnections.remove(en.getKey());
                        closeCount++;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ConnectPool.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            logFiner("removed {0} connections", closeCount);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="removeClosedStatements()">
    /**
     * Удаляет закрытые запросы из памяти
     */
    public void removeClosedStatements(){
        removeClosedStatements(true);
        fireEventQueue();
    }

    /**
     * Удаляет закрытые запросы из памяти
     * @param addEvents true - добавляет событие в очередь
     */
    public void removeClosedStatements(boolean addEvents){
        logFine("removeClosedStatements()");
        synchronized(this){
            int closeCount = 0;
            LinkedHashSet<Statement> closedStmnts = new LinkedHashSet<>();

            for( Map.Entry<Statement,Connection> en : statements.entrySet() ){
                Statement st = en.getKey();
                if( st==null )continue;

                try {
                    if( st.isClosed() ){
                        closedStmnts.add(st);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ConnectPool.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            for( Statement st : closedStmnts ){
                //String name = nameOf(cn);
                String name = "unnamed statement";
                logFiner("remove closed statement {0}", name==null ? "unnamed" : name);
                closeCount++;
                statements.remove(st);
            }

            logFiner("removed {0} statements", closeCount);
        }
    }
    //</editor-fold>
}
