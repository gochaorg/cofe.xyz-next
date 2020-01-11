package xyz.cofe.gui.swing.table;

import xyz.cofe.ecolls.ReadWriteLockSupport;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Колонка таблицы
 * @author gocha
 */
public class Column
    implements GetReaderForRow, ReadWriteLockSupport
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(Column.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(Column.class.getName()).log(Level.FINER, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(Column.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(Column.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(Column.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(Column.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    protected final ReadWriteLock sync;

    @Override
    public Lock getReadLock(){
        return sync!=null ? sync.readLock() : null;
    }

    @Override
    public Lock getWriteLock(){
        return sync!=null ? sync.writeLock() : null;
    }

    /**
     * Конструктор
     */
    public Column(){
        sync = new ReentrantReadWriteLock();
    }

    /**
     * Конструктор
     * @param sync объект для синхронизации
     */
    public Column( ReadWriteLock sync ){
        this.sync = sync==null ? new ReentrantReadWriteLock() : sync;
    }

    /**
     * Конструктор копирования
     * @param src образец для копирования
     */
    public Column( Column src ){
        sync = new ReentrantReadWriteLock();
        if( src!=null ){
            src.readLock(()->{
                this.name = src.name;
                this.type = src.type;
                this.reader = src.reader;
                this.writer = src.writer;
            });
        }
    }

    /**
     * Конструктор
     * @param sync объект для синхронизации, если null - то используется this
     * @param src образец для копирования
     */
    public Column( ReadWriteLock sync, Column src ){
        this.sync = sync==null ? new ReentrantReadWriteLock() : sync;
        if( src!=null ){
            src.readLock(()->{
                this.name = src.name;
                this.type = src.type;
                this.reader = src.reader;
                this.writer = src.writer;
            });
        }
    }

    @Override
    public Column clone(){
        return new Column(this);
    }

    /**
     * Клонирование с указаным объектом синхронизации
     * @param sync объект для синхронизации
     * @return клон
     */
    public Column cloneWith( ReadWriteLock sync ){
        return new Column(sync, this);
    }

    // <editor-fold defaultstate="collapsed" desc="name">
    /**
     * имя колонки
     */
    protected volatile String name = "column";
    public static final String NAME = "name";

    /**
     * Имя колонки
     * @return имя колонки
     */
    public String getName() {
        return readLock( ()->name );
    }

    /**
     * Имя колонки
     * @param name имя колонки
     */
    public void setName(String name) {
        Object old =
        writeLock( ()->{
            Object oldz = this.name;
            this.name = name;
            return oldz;
        });
        firePropertyChange(NAME, old, this.name);
    }

    /**
     * Имя колонки
     * @param name имя колонки
     * @return this ссылка
     */
    public Column name( String name ){
        setName(name);
        return this;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="reader">
    protected volatile Function<Object,Object> reader = null;
    public static final String READER = "reader";

    /**
     * Чтение значения ячейки. <br>
     * Вызывается так: <code>column.getReader().convert( Элемент списка )</code> - должен вернуть значение элемента для соответ. колонки.
     * @return чтение значения ячейки
     */
    public Function<Object, Object> getReader() {
        return readLock( ()-> reader );
    }

    /**
     * Чтение значения ячейки в зависимости от номера строки
     */
    protected volatile Function<Integer,Function<Object,Object>> rowReader;

    /**
     * Чтение значения ячейки. <br>
     * Вызывается так: <code>column.getReader( row ).convert( Элемент списка )</code> - должен вернуть значение элемента для соответ. колонки.
     * <br><br>
     *
     * В реализации по умолчанию - исполюуется метод getReader(), если функция rowReader равна нулю или вернула null.
     * @return чтение значения ячейки
     * @see #getReader()
     */
    @Override
    public Function<Object, Object> getReader(int row) {
        return readLock( ()->{
            Function<Integer,? extends Function> rr = rowReader;
            if( rr==null )return getReader();

            Function conv = rr.apply(row);
            if( conv==null )return getReader();

            return conv;
        });
    }

    /**
     * Чтение значения ячейки. <br>
     * Вызывается так: <code>column.getReader().convert( Элемент списка )</code> - должен вернуть значение элемента для соответ. колонки.
     * @param reader чтение значения ячейки
     */
    public void setReader(Function<Object, Object> reader) {
        Object old =
        writeLock( ()->{
            Object oldz = this.reader;
            this.reader = reader;
            return oldz;
        });
        firePropertyChange(READER, old, this.reader);
    }

    /**
     * Чтение значения ячейки. <br>
     * Вызывается так: <code>column.getReader().convert( Элемент списка )</code> -
     * должен вернуть значение элемента для соответ. колонки.
     * @param reader чтение значения ячейки
     * @return this ссылка
     */
    public Column reader(Function<Object, Object> reader) {
        setReader(reader);
        return this;
    }

    /**
     * Чтение значения ячейки. <br>
     * Вызывается так: <code>column.getRowReader( row ).convert( Элемент списка )</code> -
     * должен вернуть значение элемента для соответ. колонки.
     * @param rowReader Функция возвращ. функцию чтения
     * @return this ссылка
     */
    public Column rowReader(Function<Integer,Function<Object,Object>> rowReader){
        writeLock( ()->{
            this.rowReader = rowReader;
        });
        return this;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="writer">
    /**
     * Записываемое значение
     */
    public static class Cell{
        /**
         * Элемент списка
         */
        public Object object = null;

        /**
         * Записываемое значение
         */
        public Object newValue = null;

        /**
         * Конструктор по умолчанию
         */
        public Cell(){
        }

        public Cell(Object object,Object newValue){
            this.object = object;
            this.newValue = newValue;
        }
    }

    public static final String WRITER = "writer";

    /**
     * Функция записи
     */
    protected volatile Function<Cell,Boolean> writer = null;

    /**
     * Запись значения ячейки. <br>
     * Вызывается так: <font style="font-family:monospaced"> getWriter().convert( new Cell( Элемент списка, Новое значение ячейки) ) </font>
     * @return true - записано, false - не записано
     */
    public Function<Cell, Boolean> getWriter() {
        return readLock( ()->writer );
    }

    /**
     * Запись значения ячейки. <br>
     * Вызывается так: <font style="font-family:monospaced"> getWriter().convert( new Cell( Элемент списка, Новое значение ячейки) ) </font>
     * @param writer Функция записи
     */
    public void setWriter(Function<Cell, Boolean> writer) {
        Object old =
        writeLock( ()->{
            Object oldz = this.writer;
            this.writer = writer;
            return oldz;
        } );
        firePropertyChange(WRITER, old, this.writer);
    }

    /**
     * Запись значения ячейки. <br>
     * Вызывается так: <font style="font-family:monospaced"> getWriter().convert( new Cell( Элемент списка, Новое значение ячейки) ) </font>
     * @param writer  Функция записи
     * @return this ссылка
     */
    public Column writer(Function<Cell, Boolean> writer){
        setWriter(writer);
        return this;
    }
    // </editor-fold>

    public static final String SOURCE_COLUMN="sourceColumn";

    // <editor-fold defaultstate="collapsed" desc="type">
    public static final String TYPE="type";
    protected volatile Class type = String.class;

    /**
     * Тип данных колонки
     * @return Тип данных колонки
     */
    public Class getType() {
        return readLock( ()->type );
    }

    /**
     * Тип данных колонки
     * @param type Тип данных колонки
     */
    public void setType(Class type) {
        Object old =
        writeLock(()->{
            Object oldz = this.type;
            this.type = type;
            return oldz;
        });
        firePropertyChange(TYPE, old, type);
    }

    /**
     * Тип данных колонки
     * @param type Тип данных колонки
     * @return this ссылка
     */
    public Column type(Class type){
        setType(type);
        return this;
    }
    // </editor-fold>

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
        synchronized(sync){
            if( propertyChangeSupport!=null )return propertyChangeSupport;
            propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
        }
        return propertyChangeSupport;
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
    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertySupport().addPropertyChangeListener( listener );
    }

    /**
     * Удаляет подписчика
     * @param listener Подписчик
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertySupport().removePropertyChangeListener( listener );
    }
    // </editor-fold>
}

