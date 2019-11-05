/*
 * The MIT License
 *
 * Copyright 2015 Kamnev Georgiy (nt.gocha@gmail.com).
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

package xyz.cofe.gui.swing.table;


import xyz.cofe.collection.*;
import xyz.cofe.ecolls.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Модель таблицы основанная на карте ключ/значение (Map)
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 * @param <K> Тип ключа карты
 * @param <V> Тип значения карты
 */
public class MapTM<K,V>
    implements TableModel
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(MapTM.class.getName());
    private static final Level logLevel = logger.getLevel();

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
    //</editor-fold>

    /**
     * Конструктор
     */
    public MapTM() {
    }

    // <editor-fold defaultstate="collapsed" desc="propertyChangeSupport">
    /**
     * Поддержка PropertyChangeEvent
     */
    private transient java.beans.PropertyChangeSupport propertyChangeSupport = null;
    /**
     * Поддержка PropertyChangeEvent
     * @return Поддержка PropertyChangeEvent
     */
    protected java.beans.PropertyChangeSupport propertySupport(){
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

    // <editor-fold defaultstate="collapsed" desc="evSupport">
    protected EventSupport evSupport = new EventSupport(this);

    /**
     * Удаляет подписчика, чтоб он не получал сообщения
     * @param l подписчик
     */
    @Override
    public void removeTableModelListener(TableModelListener l) {
        evSupport.removeTableModelListener(l);
    }

    /**
     * Возвращает список подписчиков
     * @return список подписчиков
     */
    public Collection<TableModelListener> getListenersCollection() {
        return evSupport.getListenersCollection();
    }

    /**
     * Возвращает список подписчиков
     * @return список подписчиков
     */
    public TableModelListener[] getListeners() {
        return evSupport.getListeners();
    }

    /**
     * Рассылка уведомления подписчикам
     * @param e уведомление
     */
    public void fireTableModelEvent(TableModelEvent e) {
        evSupport.fireTableModelEvent(e);
    }

    /**
     * Рассылка уведомления подписчикам о обновлении строк
     * @param rowIndexFrom начало диапазона строк
     * @param toIndexInclude конец (включительно) диапазона строк
     */
    public void fireRowsUpdated(int rowIndexFrom, int toIndexInclude) {
        evSupport.fireRowsUpdated(rowIndexFrom, toIndexInclude);
    }

    /**
     * Рассылка уведомления подписчикам о добавлении строк в таблицу
     * @param rowIndexFrom начало диапазона строк
     * @param toIndexInclude конец (включительно) диапазона строк
     */
    public void fireRowsInserted(int rowIndexFrom, int toIndexInclude) {
        evSupport.fireRowsInserted(rowIndexFrom, toIndexInclude);
    }

    /**
     * Рассылка уведомления подписчикам о удалении строк из таблицы
     * @param rowIndexFrom начало диапазона строк
     * @param toIndexInclude конец (включительно) диапазона строк
     */
    public void fireRowsDeleted(int rowIndexFrom, int toIndexInclude) {
        evSupport.fireRowsDeleted(rowIndexFrom, toIndexInclude);
    }

    /**
     * Рассылка уведомления подписчикам о измении строки
     * @param row индекс строки
     */
    public void fireRowUpdated(int row) {
        evSupport.fireRowUpdated(row);
    }

    /**
     * Рассылка уведомления подписчикам
     */
    public void fireColumnsChanged() {
        evSupport.fireColumnsChanged();
    }

    /**
     * Рассылка уведомления подписчикам о измении ячейки
     * @param rowIndex строка таблицы
     * @param columnIndex колонка таблицы
     */
    public void fireCellChanged(int rowIndex, int columnIndex) {
        evSupport.fireCellChanged(rowIndex, columnIndex);
    }

    /**
     * Рассылка уведомления подписчикам о измении всех данных
     */
    public void fireAllChanged() {
        evSupport.fireAllChanged();
    }

    /**
     * Добавление подписчика на уведомлении о измении модели таблицы
     * @param l подписчик
     */
    @Override
    public void addTableModelListener(TableModelListener l) {
        evSupport.addTableModelListener(l);
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="map">
    private EventMap<K,V> map = null;

    /**
     * Возвращает карту значений
     * @return карта значений
     */
    public EventMap<K,V> getMap() {
        return map;
    }

    /**
     * Указывает карту значений
     * @param map карта
     */
    public void setMap(EventMap<K,V> map) {
        int oldCount = -1;
        int newCount = -1;

        Object old = null;
        oldCount = getRowCount();

        old = this.map;

        if( this.map!=null ){
            this.map.removeCollectionListener(listener);
        }

        this.map = map;

        if( this.map!=null ){
            this.map.addCollectionListener(true, listener);
        }

        clearCache();

        newCount = getRowCount();

        firePropertyChange("map", old, this.map);

        if( oldCount<0 || newCount<0 ){
            fireAllChanged();
        }else{
            if( oldCount>0 )fireRowsDeleted(0, oldCount-1);
            if( newCount>0 )fireRowsInserted(0, newCount-1);
        }
    }
    //</editor-fold>

    private final CollectionListener<EventMap<K,V>,V> listener = new CollectionListener<>(){
        @Override
        public void collectionEvent( CollectionEvent<EventMap<K,V>, V> event ){
            if( event instanceof DeletedEvent ){
                DeletedEvent ev = (DeletedEvent)event;
                if( ev.getSource() instanceof EventMap ){
                    onMapEntryDeleted(
                        (EventMap) ev.getSource(),
                        (K)ev.getIndex(),
                        (V)ev.getOldItem()
                    );
                }
            }
            if( event instanceof UpdatedEvent ){
                UpdatedEvent ev = (UpdatedEvent)event;
                if( ev.getSource() instanceof EventMap ){
                    onMapEntryUpdated(
                        (EventMap) ev.getSource(),
                        (K)ev.getIndex(),
                        (V)ev.getNewItem(),
                        (V)ev.getOldItem()
                    );
                }
            }
            if( event instanceof InsertedEvent ){
                InsertedEvent ev = (InsertedEvent)event;
                if( ev.getSource() instanceof EventMap ){
                    onMapEntryInserted(
                        (EventMap) ev.getSource(),
                        (K)ev.getIndex(),
                        (V)ev.getNewItem()
                    );
                }
            }
        }

//        @Override
//        protected void updated(EventMap<K,V> map, V old, K key, V value) {
//            onMapEntryUpdated(map, key, value, old);
//        }
//
//        @Override
//        protected void deleted(EventMap<K,V> map, K key, V value) {
//            onMapEntryDeleted(map, key, value);
//        }
//
//        @Override
//        protected void inserted(EventMap<K,V> map, K key, V value) {
//            onMapEntryInserted(map, key, value);
//        }

    };

    // Очередь сообщений
    private final List<Runnable> eventQueue = new ArrayList<Runnable>();

    /**
     * Возвращает очередь сообщений
     * @return очередь сообщений
     */
    protected List<Runnable> getEventQueue(){
        return eventQueue;
    }

    /**
     * Добавляет сообщение в очередь
     * @param r сообщение
     */
    protected void addEventQueue(Runnable r){
        if( r!=null )getEventQueue().add(r);
    }

    /**
     * Рассылка уведомлений из очереди сообщений и освобождение очереди
     */
    protected void runEventQueue(){
        List<Runnable> l = getEventQueue();
        for( Runnable r : l.toArray(new Runnable[]{}) ){
            r.run();
        }
        l.clear();
    }

    /**
     * Возвращает что вызван метод TableModel
     * @return true - вызван один из методов TableModel
     */
    protected boolean isTMMethodCalled(){
        return
            isCellEditableCalled.get()
                || this.getValueAtCalled.get()
                || this.setValueAtCalled.get()
            ;
    }

    /**
     * Добавляет в кеш пару ключ/значение
     * @param p пара ключ/значение
     */
    protected void addCachePair( Pair<K,V> p ){
        getCachePairs().add(p);
    }

    /**
     * Возвращает размер кеша пар ключ/значение
     * @return размер кеша
     */
    protected int getCachePairsSize(){
        return getCachePairs().size();
    }

    /**
     * Вызывается при добавлении в карту пары ключ/значение,
     * генерирует событие добавление строки таблицы
     * @param map карта
     * @param key ключ
     * @param value значение
     */
    protected void onMapEntryInserted(EventMap<K,V> map, K key, V value){
        final int[] rowidx = new int[]{ -1 };

        Pair<K,V> p = Pair.of(key, value);
        addCachePair(p);

        rowidx[0] = getCachePairsSize() - 1;
        setKeyRow(key, rowidx[0]);

        Runnable ev = new Runnable() {
            @Override
            public void run() {
                fireRowsInserted(rowidx[0], rowidx[0]);
            }
        };

        if( generateEvents.get() )addEventQueue(ev);

        if( !isTMMethodCalled() ){
            runEventQueue();
        }
    }

    /**
     * Удаляет из кеша значение по индексу
     * @param idx индекс пары ключ/значение
     */
    protected void removeCachePairByIndex( int idx ){
        getCachePairs().remove(idx);
    }

    /**
     * Перестраивает кеш пар ключ/значение
     */
    protected void rebuildKeyRowCache(){
        key2rowCache.clear();
        int idx = -1;
        for( Pair<K,V> p : getCachePairs() ){
            idx++;
            key2rowCache.put(p.a(), idx);
        }
    }

    private final AtomicBoolean generateEvents = new AtomicBoolean(true);

    /**
     * Вызывается при удаление пары ключ/значения из карты,
     * генерирует событие удаления строки
     * @param map карта
     * @param key ключ
     * @param value значение
     */
    protected void onMapEntryDeleted(EventMap<K,V> map, K key, V value){
        final int rowidx = getKeyRow(key);
        if( rowidx>=0 ){
            int csize = getCachePairsSize();
            if( rowidx < csize ){
                removeCachePairByIndex(rowidx);
                rebuildKeyRowCache();

                Runnable ev = new Runnable() {
                    @Override
                    public void run() {
                        fireRowsDeleted(rowidx, rowidx);
                    }
                };

                if( generateEvents.get() )addEventQueue(ev);
            }
        }

        if( !isTMMethodCalled() ){
            runEventQueue();
        }
    }

    /**
     * Вызывается при обновления ключа в карте,
     * генериует событие обновления строки таблицы
     * @param map карта
     * @param key ключ
     * @param value текущее значение
     * @param oldValue предыдущее значение
     */
    protected void onMapEntryUpdated(EventMap<K,V> map, K key, V value, V oldValue){
        final int rowidx = getKeyRow(key);
        if( rowidx>=0 ){
            int csize = getCachePairsSize();
            if( rowidx < csize ){
                Pair<K,V> p = getCachePair(rowidx);
                if( p!=null ){
                    setCachePair(rowidx, Pair.of(key, value));

                    Runnable ev = new Runnable() {
                        @Override
                        public void run() {
                            fireRowsUpdated(rowidx, rowidx);
                        }
                    };

                    if( generateEvents.get() )addEventQueue(ev);
                }
            }
        }

        if( !isTMMethodCalled() ){
            runEventQueue();
        }
    }

    // Кэш пар ключ/значение
    private List<Pair<K,V>> cache = null;

    // Кэш ключ -> строка
    private WeakHashMap<K,Integer> key2rowCache = new WeakHashMap();

    // Кэш null ключ -> строка
    private int nullKeyRow = -1;

    // очистка кэша
    protected void clearCache(){
        clearCache0();
    }

    private void clearCache0(){
        if( cache!=null ){
            cache.clear();
            cache = null;
        }

        key2rowCache.clear();
        nullKeyRow = -1;
    }

    /**
     * Возвращает пару ключ/значение по индексу
     * @param index индекс
     * @return занчение или null
     */
    protected Pair<K,V> getCachePair( int index ){
        if( index<0 )return null;
        if( index>=getCachePairs().size() )return null;
        return getCachePairs().get(index);
    }

    /**
     * Устанавливает значение в кеше по индексу
     * @param index индекс
     * @param p пара ключ/значение
     */
    protected void setCachePair( int index, Pair<K,V> p ){
        if( index<0 )return;
        if( index>=getCachePairs().size() )return;
        getCachePairs().set(index, p);
    }

    /**
     * Установка соответствия ключ -&gt; строка
     * @param key ключ
     * @param row строка
     */
    protected void setKeyRow( K key, int row ){
        if( key==null ){
            nullKeyRow = row;
        }else{
            key2rowCache.put(key, row);
        }
    }

    /**
     * Полчение соответствия ключ -&gt; строка
     * @param key Ключ
     * @return Строка или -1
     */
    protected int getKeyRow( K key ){
        if( key==null )return nullKeyRow;
        Integer row = key2rowCache.get(key);
        return row==null ? -1 : row;
    }

    /**
     * Возвращает карту объект (возможно null) -&gt; строка
     * @return карта объект (возможно null) -&gt; строкаы
     */
    protected List<Pair<K,Integer>> getKeyRowMap(){
        List<Pair<K,Integer>> res = new ArrayList<>();
        if( nullKeyRow>=0 ){
            res.add(Pair.<K,Integer>of(null, nullKeyRow));
        }
        for( K k : key2rowCache.keySet() ){
            if( k!=null ){
                res.add(Pair.<K,Integer>of(k, key2rowCache.get(k)));
            }
        }
        return res;
    }

    /**
     * Перестройка кэша по карте
     */
    protected void rebuildCache(){
        clearCache0();

        List<Pair<K,V>> res = new ArrayList<Pair<K,V>>();
        cache = res;

        if( map==null ){
            return;
        }

        int rowidx = -1;
        for( Map.Entry<K,V> oEntry : map.entrySet() ){
            if( oEntry==null )continue;
            if( !(oEntry instanceof Map.Entry) )continue;

            rowidx++;

            Map.Entry<K,V> en = (Map.Entry)oEntry;

            K k = en.getKey();
            V v = en.getValue();

            Pair p = Pair.of(k, v);
            res.add(p);

            setKeyRow(k, rowidx);
        }
    }

    /**
     * Полчение списка кэшированных записей
     * @return кэш записей
     */
    protected List<Pair<K,V>> getCachePairs(){
        if( cache!=null ){
            return cache;
        }
        rebuildCache();
        return cache;
    }

    //<editor-fold defaultstate="collapsed" desc="keyType">
    private Class keyType;

    /**
     * Возвращает тип ключа карты
     * @return тип ключа
     */
    public Class getKeyType() {
        return keyType;
    }

    /**
     * Указывает тип ключа карты
     * @param keyType тип ключа
     */
    public void setKeyType(Class keyType) {
        Object old = this.keyType;
        this.keyType = keyType;
        firePropertyChange("keyType", old, keyType);
        fireColumnsChanged();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="keyName">
    private String keyName;

    /**
     * Возвращает имя колонки соответ ключу карты
     * @return имя колонки
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * Указывает имя колонки соответ ключу карты
     * @param keyName имя колонки
     */
    public void setKeyName(String keyName) {
        Object old = this.keyName;
        this.keyName = keyName;
        firePropertyChange("keyName", old, keyName);
        fireColumnsChanged();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="valueType">
    private Class valueType;

    /**
     * Возвращает тип данных значений карты
     * @return тип значения
     */
    public Class getValueType() {
        return valueType;
    }

    /**
     * Указывает тип данных значений карты
     * @param valueType тип значения
     */
    public void setValueType(Class valueType) {
        Object old = this.valueType;
        this.valueType = valueType;
        firePropertyChange("valueType", old, valueType);
        fireColumnsChanged();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="valueName">
    private String valueName;

    /**
     * Возвращает отображаемое имя колонки значения
     * @return имя колонки значений
     */
    public String getValueName() {
        return valueName;
    }

    /**
     * Указывает отображаемое имя колонки значения
     * @param valueName имя колонки значений
     */
    public void setValueName(String valueName) {
        Object old = this.valueName;
        this.valueName = valueName;
        firePropertyChange("valueName", old, valueName);
        fireColumnsChanged();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="keyReader">
    private Function<K,Object> keyReader;

    /**
     * Возвращает функцию чтения ключа карты
     * @return функция чтения key =&gt; render value
     */
    public Function<K,Object> getKeyReader() {
        return keyReader;
    }

    /**
     * Указывает функцию чтения ключа карты
     * @param keyReader функция чтения key =&gt; render value
     */
    public void setKeyReader(Function<K,Object> keyReader) {
        Object old = null;
        int co = -1;

        old = this.keyReader;
        this.keyReader = keyReader;
        co = getRowCount();

        firePropertyChange("keyReader", old, keyReader);
        if( co>0 ){
            fireRowsUpdated(0, co-1);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="keyWriter">
    private Function<Object,K> keyWriter;

    /**
     * Возвращает функцию конвертации редактируемого значения в ключ карты
     * @return функция записи editor value =&gt; key
     */
    public Function<Object,K> getKeyWriter() {
        return keyWriter;
    }

    /**
     * Указывает функцию конвертации редактируемого значения в ключ карты
     * @param keyWriter функция записи editor value =&gt; key
     */
    public void setKeyWriter(Function<Object,K> keyWriter) {
        Object old = this.keyWriter;
        this.keyWriter = keyWriter;
        firePropertyChange("keyWriter", old, keyWriter);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="valueReader">
    private Function<V,Object> valueReader;

    /**
     * Возвращает функцию чтения значения карты
     * @return fn( value ) =&gt; render value
     */
    public Function<V,Object> getValueReader() {
        return valueReader;
    }

    /**
     * Указывает функцию чтения значения карты
     * @param valueReader fn( value ) =&gt; render value
     */
    public void setValueReader(Function<V,Object> valueReader) {
        Object old = null;
        int co = -1;
        old = this.valueReader;
        this.valueReader = valueReader;
        co = getRowCount();
        firePropertyChange("valueReader", old, valueReader);
        if( co>0 ){
            fireRowsUpdated(0, co-1);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="valueWriter">
    private Function<Object,V> valueWriter;

    /**
     * Возвращает функцию записи/конвертации значения карты
     * @return fn( edit value ) =&gt; map value
     */
    public Function<Object,V> getValueWriter() {
        return valueWriter;
    }

    /**
     * Указывает функцию записи/конвертации значения карты
     * @param valueWriter fn( edit value ) =&gt; map value
     */
    public void setValueWriter(Function<Object,V> valueWriter) {
        Object old = this.valueWriter;
        this.valueWriter = valueWriter;
        firePropertyChange("valueWriter", old, valueWriter);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getRowCount()">
    @Override
    public int getRowCount() {
        return getCachePairsSize();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getColumnCount()">
    @Override
    public int getColumnCount() {
        return 2;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getColumnName()">
    @Override
    public String getColumnName(int columnIndex) {
        if( columnIndex==0 ){
            if( keyName!=null )return keyName;
            return "key";
        }
        if( columnIndex==1 ){
            if( valueName!=null )return valueName;
            return "value";
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getColumnClass()">
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if( columnIndex==0 ){
            if( keyType!=null )return keyType;
        }
        if( columnIndex==1 ){
            if( valueType!=null )return valueType;
        }
        return String.class;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="isCellEditable()">
    private final AtomicBoolean isCellEditableCalled = new AtomicBoolean(false);

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        try {
            isCellEditableCalled.set(true);

            if( columnIndex<0 )return false;
            if( columnIndex>1 )return false;
            if( rowIndex<0 )return false;
            if( rowIndex>=getRowCount() )return false;

            if( columnIndex==0 && keyWriter!=null )return true;
            if( columnIndex==1 && valueWriter!=null )return true;
        }
        finally {
            isCellEditableCalled.set(false);
            runEventQueue();
        }

        return false;
    }
    //</editor-fold>

    public Pair<K,V> getKeyValueForRow(int rowIndex){
        if( rowIndex<0 )return null;

        Pair<K,V> p = getCachePair(rowIndex);
        if( p==null )return null;

        return p;
    }

    //<editor-fold defaultstate="collapsed" desc="getValueAt()">
    private final AtomicBoolean getValueAtCalled = new AtomicBoolean(false);

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            getValueAtCalled.set(true);

            if( columnIndex<0 )return false;
            if( columnIndex>1 )return false;
            if( rowIndex<0 )return false;
            if( rowIndex>=getRowCount() )return false;

//            List<Pair> lp = getCachePairs();
//            if( rowIndex>=lp.size() )return null;

//            Pair p = lp.get(rowIndex);
            Pair<K,V> p = getCachePair(rowIndex);
            if( p==null )return null;

            if( columnIndex==0 ){
                if( keyReader!=null ){
                    return keyReader.apply(p.a());
                }
                return p.a();
            }

            if( columnIndex==1 ){
                if( valueReader!=null ){
                    return valueReader.apply(p.b());
                }
                return p.b();
            }
        }
        finally {
            getValueAtCalled.set(false);
            runEventQueue();
        }
        return null;
    }
    //</editor-fold>

    private boolean removeOldKey = true;

    //<editor-fold defaultstate="collapsed" desc="setValueAt()">
    private final AtomicBoolean setValueAtCalled = new AtomicBoolean(false);
    private final AtomicInteger setValueAt_updateRowIdx = new AtomicInteger(-1);

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            setValueAtCalled.set(true);
            setValueAt_updateRowIdx.set(-1);

            if( map==null )return;

            if( columnIndex<0 )return;
            if( columnIndex>1 )return;
            if( rowIndex<0 )return;
            if( rowIndex>=getRowCount() )return;

            if( columnIndex==0 && keyWriter!=null ){
                Pair<K,V> p = getCachePair(rowIndex);
                K newKey = keyWriter.apply(aValue);
                V v = p==null ? null : p.b();

                if( removeOldKey ){
                    final int row = getKeyRow(p.a());
                    setValueAt_updateRowIdx.set(row);

                    if( row>=0 )generateEvents.set(false);

                    map.remove( p.a() );
                    map.put(newKey, v);

                    if( row>=0 ){
                        Runnable r = new Runnable() {
                            public void run() {
                                fireRowUpdated(row);
                            }
                        };
                        addEventQueue(r);
                    }
                }else{
                    map.put(newKey, v);
                }
            }

            if( columnIndex==1 && valueWriter!=null ){
                V newValue = valueWriter.apply(aValue);

                Pair<K,V> p = getCachePair(rowIndex);
                K k = p==null ? null : p.a();

                map.put(k, newValue);
            }
        }
        finally {
            generateEvents.set(true);
            setValueAtCalled.set(false);

            runEventQueue();
        }
    }
    //</editor-fold>
}
