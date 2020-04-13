package xyz.cofe.gui.swing.table;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
  * Поддержка событий TableModel
  * @author nt.gocha@gmail.com
  */
public class EventSupport {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(EventSupport.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(EventSupport.class.getName()).log(Level.FINER, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(EventSupport.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(EventSupport.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(EventSupport.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(EventSupport.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public EventSupport( TableModel owner){
        if( owner==null )throw new IllegalArgumentException( "owner==null" );
        this.tableModel = owner;
    }

    // <editor-fold defaultstate="collapsed" desc="propertyChangeSupport">
    /**
     * объект поддержки свойств
     */
    private transient java.beans.PropertyChangeSupport propertyChangeSupport = null;

    /**
     * Возвращает объект поддержки свойств, если надо то создает его
     * @return объект поддержки свойств
     */
    public java.beans.PropertyChangeSupport propertySupport(){
        if( propertyChangeSupport!=null )return propertyChangeSupport;
        propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
        return propertyChangeSupport;
    }

    /**
     * Уведомляет подписчиков о измении свойства
     * @param property Свойство
     * @param oldValue Старое значение
     * @param newValue Новое значение
     */
    public void firePropertyChange(String property,Object oldValue, Object newValue){
        propertySupport().firePropertyChange(property, oldValue, newValue);
    }

    /**
     * Добавляет подписчика свойств
     * @param listener Подписчик
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertySupport().addPropertyChangeListener( listener );
    }

    /**
     * Удаляет подписчика свойств
     * @param listener подписчик
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertySupport().removePropertyChangeListener( listener );
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="owner">
    /**
     * Владелец от которого исходят сообщения
     */
    protected TableModel tableModel = null;

    /**
     * Указывает владельца от которого исходят сообщения
     * @return владелец
     */
    public TableModel getTableModel() {
        return tableModel;
    }

    /**
     * Указывает владельца от которого исходят сообщения
     * @param owner владелец
     */
    public void setTableModel(TableModel owner) {
        if( owner==null )throw new IllegalArgumentException( "owner==null" );
        Object old = this.tableModel;
        this.tableModel = owner;
        firePropertyChange("tableModel", old, this.tableModel);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="notifyInAwtThread : boolean">
    /**
     * Посылать уведомления в потоке AWT/Swing
     */
    private boolean notifyInAwtThread = true;

    /**
     * Посылать уведомления в потоке AWT/Swing
     * @return Посылать уведомления в потоке AWT/Swing
     */
    public boolean isNotifyInAwtThread() {
        synchronized(this){ return notifyInAwtThread; }
    }

    /**
     * Посылать уведомления в потоке AWT/Swing
     * @param notifyInAwtThread Посылать уведомления в потоке AWT/Swing
     */
    public void setNotifyInAwtThread(boolean notifyInAwtThread) {
        synchronized(this){ this.notifyInAwtThread = notifyInAwtThread; }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="awtInvokeAndWait : boolean">
    /**
     * Дожидаться ответа на увемоление AWT/Swing потока
     */
    private boolean awtInvokeAndWait = false;

    /**
     * Дожидаться ответа на увемоление AWT/Swing потока
     * @return true - вызвать SwingUtilites.invokeAndWait / false - вызывать SwingUtilites.invokeLater
     */
    public boolean isAwtInvokeAndWait()
    {
        synchronized(this){ return awtInvokeAndWait; }
    }

    /**
     * Дожидаться ответа на увемоление AWT/Swing потока
     * @param awtInvokeAndWait true - вызвать SwingUtilites.invokeAndWait / false - вызывать SwingUtilites.invokeLater
     */
    public void setAwtInvokeAndWait(boolean awtInvokeAndWait)
    {
        synchronized(this){ this.awtInvokeAndWait = awtInvokeAndWait; }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fireAllChanged()">
    private int fireAllChangedLevel = 0;

    /**
     * Уведомляет что полностью изменилась таблица, включая колонки
     */
    public void fireAllChanged(){
        synchronized(this){
            try{
                fireAllChangedLevel++;
                if( fireAllChangedLevel>1 )return;

                TableModelEvent e = new TableModelEvent(getTableModel());
                fireTableModelEvent(e);
            }finally{
                fireAllChangedLevel--;
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fireColumnsChanged()">
    private int fireColumnsChangedLevel = 0;

    /**
     * Уведомляет что изменились колонки: кол-во, названия, тип
     */
    public void fireColumnsChanged(){
        synchronized(this){
            try{
                fireColumnsChangedLevel++;
                if( fireColumnsChangedLevel>1 )return;

                TableModelEvent e = new TableModelEvent(getTableModel(),TableModelEvent.HEADER_ROW);
                fireTableModelEvent(e);
            }finally{
                fireColumnsChangedLevel--;
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fireRowUpdated()">
    private int fireRowUpdated = 0;

    /**
     * Уведомляет что изменилась строка
     * @param row Индекс строки
     */
    public void fireRowUpdated(int row){
        synchronized(this){
            try{
                fireRowUpdated++;
                if( fireRowUpdated>1 )return;

                TableModelEvent e = new TableModelEvent(getTableModel(),row);
                fireTableModelEvent(e);
            }finally{
                fireRowUpdated--;
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fireRowsUpdated()">
    private int fireRowUpdated2 = 0;

    /**
     * Уведомляет что изменилась строки
     * @param rowIndexFrom Индекс строки с какой
     * @param toIndexInclude Индекс строки по какую включительно
     */
    public void fireRowsUpdated(int rowIndexFrom,int toIndexInclude){
        synchronized(this){
            try{
                fireRowUpdated2++;
                if( fireRowUpdated2>1 )return;

                TableModelEvent e = new TableModelEvent(getTableModel(),rowIndexFrom, toIndexInclude);
                fireTableModelEvent(e);
            }finally{
                fireRowUpdated2--;
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fireCellChanged()">
    private int fireCellChanged = 0;

    /**
     * Уведомляет что изменилась ячейка
     * @param rowIndex Строка
     * @param columnIndex Колонка
     */
    public void fireCellChanged(int rowIndex,int columnIndex){
        synchronized(this){
            try{
                fireCellChanged++;
                if( fireCellChanged>1 )return;

                TableModelEvent e = new TableModelEvent(getTableModel(),rowIndex, rowIndex, columnIndex);
                fireTableModelEvent(e);
            }finally{
                fireCellChanged--;
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fireRowsInserted()">
    private int fireRowsInserted = 0;

    /**
     * Уведомляет что добавлены новые строки
     * @param rowIndexFrom с какой строки
     * @param toIndexInclude по какую строку включительно
     */
    public void fireRowsInserted(int rowIndexFrom,int toIndexInclude){
        synchronized(this){
            try{
                fireRowsInserted++;
                if( fireRowsInserted>1 )return;

                TableModelEvent e = new TableModelEvent(getTableModel(),rowIndexFrom, toIndexInclude, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
                fireTableModelEvent(e);
            }finally{
                fireRowsInserted--;
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fireRowsDeleted()">
    private int fireRowsDeleted = 0;

    /**
     * Уведомляет что удалены строки
     * @param rowIndexFrom с какой строки
     * @param toIndexInclude по какую строку включительно
     */
    public void fireRowsDeleted(int rowIndexFrom,int toIndexInclude){
        synchronized(this){
            try{
                fireRowsDeleted++;
                if( fireRowsDeleted>1 )return;

                TableModelEvent e = new TableModelEvent(
                    getTableModel(),
                    rowIndexFrom,
                    toIndexInclude,
                    TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
                fireTableModelEvent(e);
            }finally{
                fireRowsDeleted--;
            }
        }
    }
    //</editor-fold>

    private final Queue<Runnable> eventQueue = new ConcurrentLinkedQueue<>();
    private void processEventQueue(){
        while( true ){
            Runnable r = eventQueue.poll();
            if( r==null )break;

            r.run();
        }
    }

    private int fireTableModelEvent = 0;

    protected static class TableModelEventSender implements Runnable {
        protected Collection<TableModelListener> listeners;
        protected TableModelEvent event;

        public TableModelEventSender(Collection<TableModelListener> listeners, TableModelEvent event){
            if( listeners==null )throw new IllegalArgumentException("listeners == null");
            if( event==null )throw new IllegalArgumentException("event == null");
            this.listeners = listeners;
            this.event = event;
        }

        @Override
        public void run() {
            for( TableModelListener l : listeners ){
                if( l==null )continue;
                l.tableChanged(event);
            }
        }
    }

    /**
     * Уведомляет подписчиков о событии
     * @param e Событие
     */
    public void fireTableModelEvent(final TableModelEvent e){
        synchronized(this){
            try{
                fireTableModelEvent++;
                if( fireTableModelEvent>32 )return;

                if( e==null )return;

                /*Runnable swingRun = new Runnable() {
                    @Override
                    public void run() {
                    final Set<TableModelListener> lsns = new LinkedHashSet<TableModelListener>();
                    lsns.addAll(getListenersCollection());

                    for( TableModelListener l : lsns ){
                        if( l==null )continue;
                        l.tableChanged(e);
                    }
                    }
                };*/

                int evQueueSize = eventQueue.size();

                TableModelEventSender tmeSender = new TableModelEventSender(getListenersCollection(), e);
                eventQueue.add(tmeSender);

                //eventQueue.add(swingRun);

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        processEventQueue();
                    }};

                if( isNotifyInAwtThread() ){
                    if( SwingUtilities.isEventDispatchThread() ){
                        //r.run();
                        processEventQueue();
                    }else{
                        if( isAwtInvokeAndWait() ){
                            try {
                                SwingUtilities.invokeAndWait(r);
                            } catch( InterruptedException ex ) {
                                Logger.getLogger(EventSupport.class.getName()).log(Level.SEVERE, null, ex);
                                Thread.currentThread().interrupt();
                            } catch( InvocationTargetException ex ) {
                                Logger.getLogger(EventSupport.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }else{
                            if( evQueueSize<1 ){
                                SwingUtilities.invokeLater(r);
                            }
                        }
                    }
                }else{
                    processEventQueue();
                }
            }finally{
                fireTableModelEvent--;
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="listeners ...">
    /**
     * Подписчики
     */
    protected volatile Collection<TableModelListener> listeners = null;

    /**
     * Возвращает коллекцию подписчиков
     * @return Подписчики
     */
    public Collection<TableModelListener> getListenersCollection(){
        if( listeners!=null )return listeners;
        synchronized(this){
            if( listeners!=null )return listeners;
            listeners = createListenersCollections();
            return listeners;
        }
    }

    /**
     * Возвращает массив подписчиков
     * @return Подписчики
     */
    public TableModelListener[] getListeners(){
        Collection<TableModelListener> coll = getListenersCollection();
        return coll.toArray(new TableModelListener[]{});
    }

    /**
     * Создает коллецию подписчиков
     * @return Подписчики
     */
    public Collection<TableModelListener> createListenersCollections(){
        //return new LinkedHashSet<TableModelListener>();
        //return new ConcurrentSkipListSet<>();
        return new ConcurrentLinkedQueue<>();
    }

    private Collection<TableModelListener> lstnrs(){
        return getListenersCollection();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="addTableModelListener()">
    /**
     * Добавляет подписчика на события
     * @param l Подписчик
     */
    public void addTableModelListener(TableModelListener l) {
        synchronized(this){
            if( l==null )return;
            lstnrs().add(l);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="removeTableModelListener()">
    /**
     * Удаляет подписчика на события
     * @param l Подписчик
     */
    public void removeTableModelListener(TableModelListener l) {
        synchronized(this){
            if( l==null )return;
            lstnrs().remove(l);
        }
    }
    //</editor-fold>
}
