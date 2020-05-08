package xyz.cofe.gui.swing.tmodel;

import xyz.cofe.collection.*;
import xyz.cofe.ecolls.Closeables;

import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Модель таблицы основанная на списке объектов
 * @author nt.gocha@gmail.com
 * @param <E> Тип элемента списка
 */
public class ListTM<E> implements TableModel {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(ListTM.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(ListTM.class.getName()).log(Level.FINER, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(ListTM.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(ListTM.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(ListTM.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(ListTM.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    protected final Lock lock;

    /**
     * Конструктор
     */
    public ListTM(){
        this.lock = new ReentrantLock();
        if( list!=null ){
            // list.addEventListListener(listAdapter);
            addSourceListeners();
        }
        if( columns!=null )columns.addCollectionListener(columnsAdapter);
    }

    // <editor-fold defaultstate="collapsed" desc="propertyChangeSupport">
    /**
     * Поддержка PropertyChangeEvent
     */
    private volatile transient java.beans.PropertyChangeSupport propertyChangeSupport = null;

    /**
     * Поддержка PropertyChangeEvent
     * @return Поддержка PropertyChangeEvent
     */
    protected java.beans.PropertyChangeSupport propertySupport(){
        if( propertyChangeSupport!=null )return propertyChangeSupport;
        synchronized(this){
            if( propertyChangeSupport!=null )return propertyChangeSupport;

            propertyChangeSupport = //new java.beans.PropertyChangeSupport(this);
                new SwingPropertyChangeSupport(this);

            return propertyChangeSupport;
        }
    }

    /**
     * Уведомляет подписчиков о измении свойства
     * @param property Свойство
     * @param oldValue Старое значение
     * @param newValue Новое значение
     */
    protected void firePropertyChange(String property,Object oldValue, Object newValue){
        propertySupport().firePropertyChange(property, oldValue, newValue);
    }

    /**
     * Добавляет подписчика
     * @param listener Подписчик
     */
    public void addPropertyChangeListener( PropertyChangeListener listener )
    {
        propertySupport().addPropertyChangeListener( listener );
    }

    /**
     * Удаляет подписчика
     * @param listener Подписчик
     */
    public void removePropertyChangeListener( PropertyChangeListener listener )
    {
        propertySupport().removePropertyChangeListener( listener );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="evSupport">
    protected EventSupport evSupport = new EventSupport(this);
    public EventSupport getEventSupport(){ return evSupport; }

    /**
     * Удаляет подписчика, чтоб он не получал сообщения
     * @param l подписчик
     */
    @Override
    public void removeTableModelListener(TableModelListener l) {
        synchronized(this){ evSupport.removeTableModelListener(l); }
    }

    /**
     * Возвращает список подписчиков
     * @return список подписчиков
     */
    public Collection<TableModelListener> getListenersCollection() {
        synchronized(this){ return evSupport.getListenersCollection(); }
    }

    /**
     * Возвращает список подписчиков
     * @return список подписчиков
     */
    public TableModelListener[] getListeners() {
        synchronized(this){ return evSupport.getListeners(); }
    }

    /**
     * Рассылка уведомления подписчикам
     * @param e уведомление
     */
    public void fireTableModelEvent(TableModelEvent e) {
        synchronized(this){ evSupport.fireTableModelEvent(e); }
    }

    /**
     * Рассылка уведомления подписчикам о обновлении строк
     * @param rowIndexFrom начало диапазона строк
     * @param toIndexInclude конец (включительно) диапазона строк
     */
    public void fireRowsUpdated(int rowIndexFrom, int toIndexInclude) {
        synchronized(this){ evSupport.fireRowsUpdated(rowIndexFrom, toIndexInclude); }
    }

    /**
     * Рассылка уведомления подписчикам о добавлении строк в таблицу
     * @param rowIndexFrom начало диапазона строк
     * @param toIndexInclude конец (включительно) диапазона строк
     */
    public void fireRowsInserted(int rowIndexFrom, int toIndexInclude) {
        synchronized(this){ evSupport.fireRowsInserted(rowIndexFrom, toIndexInclude); }
    }

    /**
     * Рассылка уведомления подписчикам о удалении строк из таблицы
     * @param rowIndexFrom начало диапазона строк
     * @param toIndexInclude конец (включительно) диапазона строк
     */
    public void fireRowsDeleted(int rowIndexFrom, int toIndexInclude) {
        synchronized(this){ evSupport.fireRowsDeleted(rowIndexFrom, toIndexInclude); }
    }

    /**
     * Рассылка уведомления подписчикам о измении строки
     * @param row индекс строки
     */
    public void fireRowUpdated(int row) {
        synchronized(this){ evSupport.fireRowUpdated(row); }
    }

    /**
     * Рассылка уведомления подписчикам
     */
    public void fireColumnsChanged() {
        synchronized(this){ evSupport.fireColumnsChanged(); }
    }

    /**
     * Рассылка уведомления подписчикам о измении ячейки
     * @param rowIndex строка таблицы
     * @param columnIndex колонка таблицы
     */
    public void fireCellChanged(int rowIndex, int columnIndex) {
        synchronized(this){  evSupport.fireCellChanged(rowIndex, columnIndex); }
    }

    /**
     * Рассылка уведомления подписчикам о измении всех данных
     */
    public void fireAllChanged() {
        synchronized(this){ evSupport.fireAllChanged(); }
    }

    /**
     * Добавление подписчика на уведомлении о измении модели таблицы
     * @param l подписчик
     */
    @Override
    public void addTableModelListener(TableModelListener l) {
        synchronized(this){ evSupport.addTableModelListener(l); }
    }
    // </editor-fold>

    protected final Closeables listListeners = new Closeables();

    protected void addSourceListeners(){
        if( list==null )return;

        listListeners.append(
            list.onInserted( (idx,oldv,newv) -> onInserted(newv, idx)),
            list.onUpdated( (idx,oldv,newv) -> onUpdated(oldv, newv, idx)),
            list.onDeleted( (idx, oldv, newv) -> onDeleted(oldv, idx) )
        );
    }

    /**
     * Подписчик событий установлен на оригинал
     * @return true - в текущий момент подписчик на оригинал установлен
     */
    public boolean isSourceListen(){
        try{
            lock.lock();
            if( list==null ){
                return false;
            }
            //return list.containsEventListListener(listAdapter);
            return listListeners.getCloseables().length>0;
        }finally{
            lock.unlock();
        }
    }

    /**
     * Установить/сбросить подписчика на события оригинальной TableModel
     * @param listen true - установить/false - сбросить
     */
    public void setSourceListen( boolean listen ){
        boolean old = isSourceListen();

        if( listen!=old ){
            try{
                lock.lock();
                if( listen ){
                    addSourceListeners();
                }else{
                    listListeners.close();
                }
            }finally{
                lock.unlock();
            }

            boolean now = isSourceListen();
            if( now!=old ){
                firePropertyChange("sourceListen", old, now);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="list">
    protected EventList<E> list = new BasicEventList<>();

    /**
     * Возвращает исходный список объектов
     * @return список объектов
     */
    public EventList<E> getList() {
        try{
            lock.lock();
            if( list==null ){
                list = new BasicEventList<>();
                addSourceListeners();
            }
            return list;
        }finally{
            lock.unlock();
        }
    }

    /**
     * Указывает исходный список объектов
     * @param newList список объектов
     */
    public void setList(EventList<E> newList){
        Object oldl = null;
        Object newl = null;

        try{
            lock.lock();
            listListeners.close();

            oldl = this.list;
            newl = newList;
            this.list = newList;

            if( this.list!=null ){
                addSourceListeners();
            }
        }finally{
            lock.unlock();
        }

        firePropertyChange("list", oldl, newl);
        fireAllChanged();
    }
    // </editor-fold>

    /**
     * Вызывается когда в исходный список добавляется элемент,
     * генериует событие вставки строки в таблицу
     * @param e Елемент исходного списка
     * @param position индекс списка в которой добавлен исходный элемент
     */
    protected void onInserted(E e, Integer position){
        if( position==null ){
            throw new IllegalArgumentException("position == null");
        }
        fireRowsInserted(position,position);
    }

    /**
     * Вызывается когда в исходном списоке обновляется элемент,
     * генерирует событие обновления строки таблицы
     * @param oldv предыдущий элемент списка
     * @param newv текущий элемент списка
     * @param position индекс списка
     */
    protected void onUpdated(E oldv, E newv, Integer position){
        if( position==null ){
            throw new IllegalArgumentException("position == null");
        }
        fireRowsUpdated(position,position);
    }

    /**
     * Вызывается когда из исходного списка удален элемент,
     * генериует собтиые удаления строки из таблицы
     * @param e удаленный элемент
     * @param position индекс удаленного элемента
     */
    protected void onDeleted(E e, Integer position){
        if( position==null ){
            throw new IllegalArgumentException("position == null");
        }
        fireRowsDeleted(position,position);
    }

    protected final CollectionListener columnsAdapter = new CollectionListener() {
        @Override
        public void collectionEvent( CollectionEvent event ){
            if( event instanceof RemovedEvent && ((RemovedEvent) event).getOldItem() instanceof Column ){
                ((Column)((AddedEvent) event).getNewItem()).removePropertyChangeListener(columnPropertiesListener);
            }
            if( event instanceof AddedEvent && ((AddedEvent) event).getNewItem() instanceof Column ){
                ((Column)((AddedEvent) event).getNewItem()).addPropertyChangeListener(columnPropertiesListener);
            }
        }
    };

    protected PropertyChangeListener columnPropertiesListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if( Column.NAME.equals( evt.getPropertyName() ) ){
                fireColumnsChanged();
            }else if( Column.TYPE.equals(evt.getPropertyName()) ){
                fireColumnsChanged();
            }else if( Column.SOURCE_COLUMN.equals(evt.getPropertyName()) ){
                fireAllChanged();
            }else if( Column.READER.equals(evt.getPropertyName()) ){
                fireAllChanged();
            }
        }
    };

    /**
     * Возвращает элемент списка по индексу строки
     * @param index индекс строки таблицы
     * @return элемент списка
     */
    public E getItemByIndex(int index){
        try{
            lock.lock();

            if( list==null )return null;
            if( index<0 )return null;
            if( index>=list.size() )return null;

            return list.get(index);
        }finally{
            lock.unlock();
        }
    }

    /**
     * Возвращает индекст строки таблицы соответ элементу
     * @param item искомый элемент
     * @return индекс строки или null
     */
    public int getIndexOfItem(E item){
        int res = -1;
        try{
            lock.lock();
            if( list==null )return -1;
            res = list.indexOf(item);
        }finally{
            lock.unlock();
        }
        return res;
    }

    /**
     * Возвращает индексы строк соответствующие фильтру элементов исходного списка
     * @param pred фильтр
     * @return индексы строк
     */
    public List<Integer> getIndexesOfItem( Predicate<E> pred){
        if( pred==null ) throw new IllegalArgumentException( "pred==null" );
        List<Integer> res = new ArrayList<Integer>();
        int idx = -1;

        try{
            lock.lock();
            if( list==null )return res;
            for( E o : list ){
                idx++;
                if( pred.test(o) )
                    res.add( idx );
            }
        }finally{
            lock.unlock();
        }

        return res;
    }

    // <editor-fold defaultstate="collapsed" desc="columns">
    protected Columns columns = new Columns();

    /**
     * Возвращает колонки таблицы
     * @return колонки таблицы
     */
    public Columns getColumns() {
        return columns;
    }
    // </editor-fold>

    @Override
    public int getRowCount() {
        try{
            lock.lock();
            if( list==null )return 0;
            return list.size();
        }finally{
            lock.unlock();
        }
    }

    @Override
    public int getColumnCount() {
        if( columns==null )return 0;
        return columns.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        if( columns==null || columnIndex<0 || columnIndex>=columns.size() )return "?";
        return columns.get(columnIndex).getName();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if( columns==null || columnIndex<0 || columnIndex>=columns.size() )
            return Object.class;

        return columns.get(columnIndex).getType();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        try{
            lock.lock();
            if( list==null )return false;
            if( columns==null )return false;

            if( rowIndex<0 )return false;
            if( rowIndex>=list.size() )return false;

            if( columnIndex<0 )return false;
            if( columnIndex>=columns.size() )return false;

            Column col = columns.get(columnIndex);
            if( col.getWriter()!=null ){
                Object v = list.get(rowIndex);
                if( col instanceof IsRowEditable ){
                    return ((IsRowEditable)col).isRowEditable(v);
                }
                return true;
            }else{
                return false;
            }
        }finally{
            lock.unlock();
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Column c = null;
        Object v = null;

        try{
            lock.lock();

            if( list==null )return null;
            if( columns==null )return null;

            if( rowIndex<0 )return null;
            if( rowIndex>=list.size() )return null;

            if( columnIndex<0 )return null;
            if( columnIndex>=columns.size() )return null;

            v = list.get(rowIndex);
            c = columns.get(columnIndex);
        }finally{
            lock.unlock();
        }

        if( c==null )return null;

        Function conv =
            (c instanceof GetReaderForRow)
                ? ((GetReaderForRow)c).getReader(rowIndex)
                : c.getReader();

        if( conv==null )return null;

        return conv.apply(v);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Column c = null;
        Object v = null;

        try{
            lock.lock();
            if( list==null )return;
            if( columns==null )return;

            if( rowIndex<0 )return;
            if( rowIndex>=list.size() )return;

            if( columnIndex<0 )return;
            if( columnIndex>=columns.size() )return;

            v = list.get(rowIndex);
            c = columns.get(columnIndex);
        }finally{
            lock.unlock();
        }

        if( c==null )return;

        Function<Column.Cell,Boolean> cellWriter = c.getWriter();
        if( cellWriter==null )return;

        Boolean succ = cellWriter.apply(new Column.Cell(v,aValue));
        if( succ )fireRowsUpdated(rowIndex, rowIndex);
    }
}
