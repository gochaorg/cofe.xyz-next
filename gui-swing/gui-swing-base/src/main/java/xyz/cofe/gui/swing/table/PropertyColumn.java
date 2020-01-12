/*
 * The MIT License
 *
 * Copyright 2016 Kamnev Georgiy <nt.gocha@gmail.com>.
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

package xyz.cofe.gui.swing.table;

import java.awt.Color;
import java.beans.PropertyEditor;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import xyz.cofe.collection.ClassMap;
import xyz.cofe.ecolls.Fn1;
import xyz.cofe.ecolls.Fn2;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.gui.swing.properties.GetPropertyType;
import xyz.cofe.gui.swing.properties.Icons;
import xyz.cofe.gui.swing.properties.Property;
import xyz.cofe.gui.swing.properties.PropertyDB;
import xyz.cofe.gui.swing.properties.PropertyValue;
import xyz.cofe.gui.swing.properties.editor.TreeTableWrapEditor;
import xyz.cofe.gui.swing.tree.TreeTableNodeFormat;
import xyz.cofe.gui.swing.tree.TreeTableNodeFormatBasic;
import xyz.cofe.gui.swing.tree.TreeTableNodeGetFormatOf;
import xyz.cofe.gui.swing.tree.TreeTableNodeValueEditor;

/**
 * Колонка PropertyColumn для отображения свойства объекта.
 * Для рендеринга/редактирования испольуется PropertyValue.
 * @author nt.gocha@gmail.com
 */
public class PropertyColumn extends Column implements IsRowEditable
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(PropertyColumn.class.getName());
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
        logger.entering(PropertyColumn.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(PropertyColumn.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(PropertyColumn.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор
     */
    public PropertyColumn() {
        init();
    }

    /**
     * Конструктор
     * @param name имя свойства
     */
    public PropertyColumn( String name ) {
        init();
        this.setName(name);
    }

    /**
     * Конструктор
     * @param name имя свойства
     * @param forceReadonly свойство доступно только для чтения
     */
    public PropertyColumn( String name, boolean forceReadonly ) {
        init();
        this.setName(name);
        this.setForceReadOnly(forceReadonly);
    }

    /**
     * Конструктор копирования
     * @param sync объект для синхронизации
     * @param sample образец для копирования
     */
    public PropertyColumn( ReadWriteLock sync, PropertyColumn sample ) {
        super(sync, sample);

        if( sample!=null ){
            if( sample.valueFormat!=null ){
                getValueFormat().putAll(sample.getValueFormat());
            }

            if( sample.classProperties!=null ){
                getClassProperties().putAll(sample.classProperties);
            }

            setForceReadOnly(sample.forceReadOnly);

            nullIcon = sample.nullIcon;

            if( sample.nullValueFormat!=null ){
                nullValueFormat = sample.nullValueFormat.clone();
            }

            this.pdb = sample.pdb;

            this.propertyName = sample.propertyName;
            this.propertyTable = sample.propertyTable;
        }

        init();
    }

    //<editor-fold defaultstate="collapsed" desc="events & listeners">
    //<editor-fold defaultstate="collapsed" desc="Event">
    /**
     * Описывает событие колонки
     */
    public static class Event {
        /**
         * Конструктор по умолчанию
         */
        public Event(){
        }

        /**
         * Конструктор
         * @param propertyColumn ссылка на колонку
         */
        public Event(PropertyColumn propertyColumn){
            this.propertyColumn = propertyColumn;
        }

        //<editor-fold defaultstate="collapsed" desc="propertyColumn">
        private PropertyColumn propertyColumn;

        /**
         * Возвращает колонку к которой относится событие
         * @return колонка
         */
        public PropertyColumn getPropertyColumn() {
            return propertyColumn;
        }

        /**
         * Указывает колонку к которой относится событие
         * @param propertyColumn колонка
         */
        public void setPropertyColumn(PropertyColumn propertyColumn) {
            this.propertyColumn = propertyColumn;
        }
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="PropertyWrited">
    /**
     * Событие записи значения в свойство
     */
    public static class PropertyWrited extends Event {
        /**
         * Конструктор
         */
        public PropertyWrited() {
        }

        /**
         * Конструктор
         * @param propertyColumn колонка
         */
        public PropertyWrited(PropertyColumn propertyColumn) {
            super(propertyColumn);
        }

        /**
         * Конструктор
         * @param propertyColumn колонка
         * @param property свойство
         * @param bean объект владелец свойства
         * @param value записанное значение
         */
        public PropertyWrited(PropertyColumn propertyColumn, Property property, Object bean, Object value) {
            super(propertyColumn);
            this.property = property;
            this.bean = bean;
            this.value = value;
        }

        //<editor-fold defaultstate="collapsed" desc="property">
        protected Property property;

        /**
         * Свойство обновленного объекта
         * @return свойство
         */
        public Property getProperty() {
            return property;
        }

        /**
         * Свойство обновленного объекта
         * @param property свойство
         */
        public void setProperty(Property property) {
            this.property = property;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="bean">
        protected Object bean;

        /**
         * Объект владелец обновленного свойства
         * @return владелец свойства
         */
        public Object getBean() {
            return bean;
        }

        /**
         * Объект владелец обновленного свойства
         * @param bean владелец свойства
         */
        public void setBean(Object bean) {
            this.bean = bean;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="value">
        protected Object value;

        /**
         * Записанное значение
         * @return значение
         */
        public Object getValue() {
            return value;
        }

        /**
         * Записанное значение
         * @param value значение
         */
        public void setValue(Object value) {
            this.value = value;
        }
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="listeners">
    /**
     * Подписчик на события колонки
     */
    public interface Listener {
        void propertyColumnEvent( Event ev );
    }

    private final ListenersHelper<Listener,Event> listeners =
        new ListenersHelper<>( (ls,ev) -> {
                ls.propertyColumnEvent(ev);
        });

    /**
     * Проверка на наличии подписки
     * @param listener подписчик
     * @return есть подписка
     */
    public boolean hasListener(Listener listener) {
        return listeners.hasListener(listener);
    }

    /**
     * Возвращает подписчиков
     * @return подписчики
     */
    public Set<Listener> getListeners() {
        return listeners.getListeners();
    }

    /**
     * Добавляет подписчика
     * @param listener подписчик
     * @return отписка от уведомлений
     */
    public AutoCloseable addListener(Listener listener) {
        return listeners.addListener(listener);
    }

    /**
     * Добавляет подписчика
     * @param listener подписчик
     * @param weakLink добавить как weak ссылку
     * @return отписка от уведомлений
     */
    public AutoCloseable addListener(Listener listener, boolean weakLink) {
        return listeners.addListener(listener, weakLink);
    }

    /**
     * Отписка от уведомлений
     * @param listener подписчик
     */
    public void removeListener(Listener listener) {
        listeners.removeListener(listener);
    }

    /**
     * Рассылка увдомления подписчикам
     * @param event уведомление
     */
    public void fireEvent(Event event) {
        listeners.fireEvent(event);
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="forceReadOnly : Boolean">
    protected Boolean forceReadOnly = null;

    /**
     * Указывает свойство доступно только для чтения
     * @return true - свойство достпно только для чтения
     */
    public Boolean getForceReadOnly() {
        synchronized(sync){
            return forceReadOnly;
        }
    }

    /**
     * Указывает что свойство достпно только для чтения
     * @param forceReadOnly только для чтения
     */
    public void setForceReadOnly(Boolean forceReadOnly) {
        synchronized(sync){
            this.forceReadOnly = forceReadOnly;
        }
    }

    /**
     * Указывает что свойство достпно только для чтения
     * @param forceReadOnly только для чтения
     * @return self ссылка
     */
    public PropertyColumn forceReadonly(Boolean forceReadOnly){
        setForceReadOnly(forceReadOnly);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="pdb">
    protected transient volatile PropertyDB pdb;

    /**
     * Указывает "базу" свойств
     * @return "база" свойств
     */
    public PropertyDB getPropertyDB(){
        if( pdb!=null )return pdb;

        Runnable fev = null;
        try{
            synchronized(sync){
                if( pdb!=null )return pdb;
                pdb = new PropertyDB();
                fev = new Runnable() {
                    @Override
                    public void run() {
                        firePropertyChange("propertyDB", null, pdb);
                    }
                };
                /*fev = () -> {
                    firePropertyChange("propertyDB", null, pdb);
                };*/
                return pdb;
            }
        }
        finally{
            if( fev!=null ){
                fev.run();
            }
        }
    }

    /**
     * Указывает "базу" свойств
     * @param newPdb "база" свойств
     */
    public void setPropertyDB(final PropertyDB newPdb){
        Runnable fev = null;
        try{
            synchronized(sync){
                final Object old = this.pdb;
                this.pdb = newPdb;
                /*fev = () -> {
                    firePropertyChange("propertyDB", old, newPdb);
                };*/
                fev = new Runnable() {
                    @Override
                    public void run() {
                        firePropertyChange("propertyDB", old, newPdb);
                    }
                };
            }
        }
        finally{
            if( fev!=null ){
                fev.run();
            }
        }
    }

    /**
     * Указывает "базу" свойств
     * @param newPdb "база" свойств
     * @return self ссылка
     */
    public PropertyColumn propertyDB(PropertyDB newPdb){
        setPropertyDB(newPdb);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="propertyTable">
    protected PropertyTable propertyTable;

    /**
     * Указыавет таблицу редактирования свойств
     * @return таблица реадктор свойств
     */
    public PropertyTable getPropertyTable() {
        synchronized(sync){
            return propertyTable;
        }
    }

    /**
     * Указыавет таблицу редактирования свойств
     * @param propertyTable таблица реадктор свойств
     */
    public void setPropertyTable(PropertyTable propertyTable) {
        Object old = null;
        synchronized(sync){
            old = this.getPropertyTable();
            this.propertyTable = propertyTable;
        }
        firePropertyChange("propertyTable", old, getPropertyTable());
    }

    /**
     * Указыавет таблицу редактирования свойств
     * @param propertyTable таблица реадктор свойств
     * @return self ссылка
     */
    public PropertyColumn propertyTable(PropertyTable propertyTable){
        setPropertyTable(propertyTable);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="columns">
    /**
     * Возвращает перечень колонок таблицы
     * @return колонки таблицы или null
     */
    public Columns getColumns(){
        synchronized(sync){
            PropertyTable pt = getPropertyTable();
            if( pt==null )return null;
            return pt.getColumns();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="modelIndex">
    /**
     * Возвращает индекс колонки в модели таблицы
     * @return индекс клонки или -1
     */
    public int getModelIndex(){
        synchronized(sync){
            Columns cols = getColumns();
            if( cols==null ){
                return -1;
            }
            return cols.indexOf(this);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="nullValueFormat">
    //<editor-fold defaultstate="collapsed" desc="nullIcon">
    protected Icon nullIcon = null;
    protected Icon getNullIcon(){
        if( nullIcon!=null )return nullIcon;

        nullIcon = Icons.getNullIcon();
        return nullIcon;
    }
    protected void setNullIcon( Icon ico ){
        nullIcon = ico;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="get/set nullValueFormat">
    protected volatile TreeTableNodeFormat nullValueFormat;

    /**
     * ВОзвращает настройки форматирования для null значения
     * @return настройки форматирования
     */
    public TreeTableNodeFormat getNullValueFormat() {
        TreeTableNodeFormat fmt = nullValueFormat;
        if( fmt!=null )return fmt;

        synchronized( this ){
            fmt = nullValueFormat;
            if( fmt!=null )return fmt;

            fmt = new TreeTableNodeFormatBasic();
            fmt.setBold(true);
            fmt.setItalic(true);
            fmt.setForeground(Color.gray);

            Icon ico = getNullIcon();

            if( ico!=null ){
                fmt.getIcons().add(ico);
            }

            nullValueFormat = fmt;
            return nullValueFormat;
        }
    }

    /**
     * Указывает настройки форматирования для null значения
     * @param nullValueFormat настройки форматирования
     */
    public void setNullValueFormat(TreeTableNodeFormat nullValueFormat) {
        this.nullValueFormat = nullValueFormat;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="init()">
    private void init(){
        synchronized(this){
            logFine("init()");

            setType(PropertyValue.class);

            setReader(bean -> read(bean));
            setWriter(from -> {
                    if( from==null )return false;
                    if( from.newValue instanceof PropertyValue ){
                        return write(from.object, (PropertyValue)from.newValue);
                    }
                    return false;
                }
            );
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="valueFormat - форматирование данных">
    /**
     * valueFormat : ClassMap&lt;Форматирование, Объект, Узел&gt; - функция: <br>
     * fn ( Объект, Узел ) =&gt; Форматирование объекта.
     */
    protected ClassMap<Fn2<Object,Object,TreeTableNodeFormat>> valueFormat
        = new ClassMap<>();

    /**
     * Форматирование ClassMap&lt;Форматирование, Объект, Узел&gt; - функция: <br>
     * fn ( Объект, Узел ) =&gt; Форматирование объекта.
     * @return картка форматирования различных типов объектов
     */
    public ClassMap<Fn2<Object,Object,TreeTableNodeFormat>> getValueFormat(){
        if( valueFormat!=null )return valueFormat;
        valueFormat = new ClassMap<>();
        return valueFormat;
    }

    /**
     * Указывает форматирование для указанных типов
     * @param <T> тип данных узла
     * @param cls тип данных узла
     * @param reader функция форматирования (bean, значение) =&gt; формат
     * @return self ссылка
     */
    public <T> PropertyColumn addValueFormat( Class<T> cls, Fn2<Object,T,TreeTableNodeFormat> reader ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        if (reader== null) {
            throw new IllegalArgumentException("reader==null");
        }

        getValueFormat().put(
            cls,
            (Fn2<Object,Object,TreeTableNodeFormat>)reader
        );

        return this;
    }

    /**
     * Указывает форматирование для указанных типов
     * @param <T> тип данных узла
     * @param cls тип данных узла
     * @param reader функция форматирования (значение) =&gt; формат
     * @return self ссылка
     */
    public <T> PropertyColumn addValueFormat( Class<T> cls, final Fn1<T,TreeTableNodeFormat> reader ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        if (reader== null) {
            throw new IllegalArgumentException("reader==null");
        }

        getValueFormat().put(
            cls,
            //(Func2<TreeTableNodeFormat,Object,Object>)reader
            new Fn2<Object, Object,TreeTableNodeFormat>() {
                @Override
                public TreeTableNodeFormat apply(Object bean, Object data) {
                    return reader.apply((T)data);
                }
            }
        );

        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setName()">
    @Override
    public void setName(String name) {
        synchronized(this){
            logFine("setName({0})",name);
            classProperties.clear();
            mapProperties.clear();
            super.setName(name);
        }
    }
    @Override
    public PropertyColumn name(String name){
        setName(name);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="writer">
//    @Override
//    public Convertor<Cell, Boolean> getWriter() {
//        return super.getWriter();
//    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="isRowEditable(row)">
    @Override
    public boolean isRowEditable(Object row) {
        synchronized(this){
            logFine("isRowEditable( {0} )", row);

            if( forceReadOnly!=null && forceReadOnly ){
                logFine( "read only forced" );
                return false;
            }

            if( row==null )return false;

            Property property = getClassProperty(row.getClass(), getName(), row);
            if( property!=null ){
                boolean readOnly = property.isReadOnly();

                logFiner("isRowEditable( {0} ) = {1}",row, !readOnly);
                return !readOnly;
            }

            if( row instanceof Map ){
                property = getMapProperty((Map)row, getName());
                if( property!=null ){
                    boolean readOnly = property.isReadOnly();
                    logFiner("isRowEditable( {0} ) = {1}",row, !readOnly);
                    return !readOnly;
                }
            }

            return false;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="clone()">
    @Override
    public PropertyColumn clone() {
        synchronized(this){
            logFine( "clone()" );
            return new PropertyColumn(null,this);
        }
    }

    @Override
    public Column cloneWith(ReadWriteLock sync) {
        logFine( "cloneWith()" );
        return new PropertyColumn(sync, this);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="propertyName">
    protected String propertyName = null;

    /**
     * Указывает имя свойства
     * @return имя свойства
     */
    public String getPropertyName() {
        synchronized(sync){
            return propertyName;
        }
    }

    /**
     * Указывает имя свойства
     * @param propertyName имя свойства
     */
    public void setPropertyName(String propertyName) {
        Object old = null;
        synchronized(sync){
            old = this.getPropertyName();
            this.propertyName = propertyName;
        }
        firePropertyChange("propertyName", old, getPropertyName());
    }

    /**
     * Указывает имя свойства
     * @param propertyName имя свойства
     * @return self ссылка
     */
    public PropertyColumn propertyName(String propertyName){
        setPropertyName(propertyName);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="read/write">
    /**
     * Осуществляет чтение значения свойства
     * @param bean владелец свойства
     * @return значение свойства
     */
    public PropertyValue read(Object bean){
        synchronized(this){
            String name = getName();
            if( name==null || name.length()<1 )name = "?";

            PropertyValue pv = null;
            if( bean==null ){
                pv = readAsDummy(bean,name);
            }else{
                String pname = getPropertyName();
                pname = pname!=null ? pname : name;
                pv = readFromBean(bean, pname);
            }

            if( pv.getValue()!=null ){
                boolean valueFormatted = false;

                Fn2<Object,Object,TreeTableNodeFormat> fn = getValueFormat().fetch(pv.getValue().getClass());
                if( fn!=null ){
                    TreeTableNodeFormat fmt = fn.apply(bean,pv.getValue());
                    if(fmt!=null){
                        pv.setFormat(fmt);
                        valueFormatted = true;
                    }
                }

                if( !valueFormatted ){
                    Set<TreeTableNodeGetFormatOf> formatters = getPropertyDB().getFormattersOf(pv.getValue().getClass());
                    if( formatters!=null && formatters.size()>0 ){
                        for( TreeTableNodeGetFormatOf gfmt : formatters ){
                            if( gfmt==null )continue;
                            TreeTableNodeFormat fmt = gfmt.getTreeTableNodeFormatOf(pv.getValue());
                            if( fmt!=null ){
                                pv.setFormat(fmt);
                                break;
                            }
                        }
                    }
                }
            }else{
                pv.setFormat(getNullValueFormat());
            }

            PropertyEditor pe = getPropertyDB().getPropertyEditorOf(pv);
            if( pe!=null ){
                if( pe instanceof TreeTableNodeValueEditor.Editor ){
                    pv.setEditor( (TreeTableNodeValueEditor.Editor)pe );
                }else{
                    if( pe.supportsCustomEditor() ){
                        pv.setEditor( new TreeTableWrapEditor(pe) );
                    }
                }
            }

            return pv;
        }
    }

    /**
     * Осуществляет запись значения в свойство
     * @param bean Владелец свойства
     * @param pvalue записываемое значение
     * @return true - запись произведена успешна
     */
    public boolean write(Object bean, PropertyValue pvalue){
        Property prop = null;
        Object newvalue = null;
        synchronized(this){
            logFine("write( {0}, {1} )", bean, pvalue);

            if( pvalue==null ){
                logFinest("exit from write");
                return false;
            }

            prop = pvalue.getProperty();
            if( prop==null ){
                logFiner("property from pvalue not found");
                return false;
            }

            if( prop.isReadOnly() ){
                logFiner("property is read only");
                return false;
            }

            newvalue = pvalue.getValue();
            logFiner("assign value = {0}",newvalue);

            if( prop.isNotNull() && newvalue==null ){
                logFiner( "is null - not allowed (isNotNull())" );
                return false;
            }

            try{
                prop.setBean(bean);
                prop.write(newvalue);
                logFiner( "write property ({0}) value ({1}) success",prop.getName(), newvalue );
            }catch(Throwable err){
                logException(err);
                System.err.println(err.toString());
                return false;
            }
        }

        fireEvent(new PropertyWrited(this, prop, bean, newvalue));
        return true;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dummy">
    private PropertyValue readAsDummy(final Object bean,final String name){
        synchronized(this){
            logFine("readAsDummy( {0}, {1} )",bean,name);

            Property prop = new Property(name, Void.class, () -> {
                    logFiner( "read dummy ( {0}, {1} ) = null",bean,name );
                    return null;
            }, arg -> {
                    logFiner( "write dummy ( {0}, {1} ) = {2}",bean,name,arg );
                    return null;
            });

            prop.setBean(bean);
            prop.setName(name);

            prop.setReadOnly(true);
            PropertyValue pvalue = new PropertyValue(prop, null, null);
            return pvalue;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="classProperties">
    private ClassMap<Property> classProperties = new ClassMap<Property>();

    /**
     * Карта для чтения/записи свойства различных классов
     * @return карта для различных типов экземпляров
     */
    public ClassMap<Property> getClassProperties(){
        synchronized(sync){
            return classProperties;
        }
    }

    /**
     * Указывает как читать свойство для конкртеного типа экземпляра класса
     * @param beanClass тип экземпляра представлющего строку таблицы
     * @param prop свойство для чтения/записи
     * @return self ссылка
     */
    public PropertyColumn classProperty( Class beanClass, Property prop ){
        if( beanClass!=null && prop!=null ){
            getClassProperties().put(beanClass, prop);
        }
        return this;
    }

    /**
     * Указывает как читать/писать свойство для конкртеного типа экземпляра класса
     * @param <BEAN> Тип экзепляра
     * @param <PROP> Тип свойства
     * @param beanClass Тип экземпляра
     * @param name Имя свойства
     * @param propType Тип свойства
     * @param propReader Функция чтения свойства
     * @param propWrite Функция записи свойства, может быть null ссылкой
     * @return self ссылка
     */
    public <BEAN,PROP> PropertyColumn classProperty(
        final Class<BEAN> beanClass,
        final String name,
        final Class<PROP> propType,
        final Fn1<BEAN,PROP> propReader,
        final Fn2<BEAN,PROP,Object> propWrite
    ){
        if( beanClass==null )throw new IllegalArgumentException( "beanClass==null" );
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( propType==null )throw new IllegalArgumentException( "propType==null" );
        if( propReader==null )throw new IllegalArgumentException( "propReader==null" );

        final Property[] cprop = new Property[1];

        Fn1 fwr = null;
        Supplier frd = () -> {
            Property pr = cprop[0];

            Object bean = null;
            if( pr!=null )bean = pr.getBean();

            if( bean!=null ){
                if( beanClass.isAssignableFrom( bean.getClass() ) ){
                    return propReader.apply( (BEAN) bean );
                }else{
                    return propReader.apply(null);
                }
            }else{
                return propReader.apply(null);
            }
        };

        if( propWrite!=null ){
            //fwr = (val) -> {
            fwr = val -> {
                Property pr = cprop[0];

                Object bean = null;
                if( pr!=null )bean = pr.getBean();

                if( bean!=null ){
                    if( beanClass.isAssignableFrom( bean.getClass() ) ){
                        if( val==null ){
                            return propWrite.apply((BEAN)bean, null);
                        }else if( propType.isAssignableFrom( val.getClass() ) ){
                            return propWrite.apply((BEAN)bean, (PROP)val);
                        }
                    }else{
                        return propWrite.apply(null, null);
                    }
                }

                return propWrite.apply(null, null);
            };
        }

        Property prop = new Property(name, propType, frd, fwr );
        cprop[0] = prop;

        getClassProperties().put(beanClass, prop);

        return this;
    }

    /**
     * Возвращает свойство для конкретного экземпляра класса
     * @param beanClass Тип экземпляра
     * @param propertyName имя свойства
     * @param bean сам экземпляр владелец свойства
     * @return свойство или null
     */
    private Property getClassProperty( Class beanClass, String propertyName, Object bean ){
        synchronized(sync){
            if( beanClass==null )throw new IllegalArgumentException( "beanClass==null" );
            if( propertyName==null )throw new IllegalArgumentException( "propertyName==null" );

            logFine("getClassProperty( {0}, {1}, {2} )",beanClass,propertyName,bean);

            Property cachedProperty = classProperties.fetch(beanClass);
            if( cachedProperty!=null ){
                cachedProperty.setBean(bean);
                logFine("getClassProperty( {0}, {1}, {2} ) read cached {3}",beanClass,propertyName,bean,cachedProperty);
                return cachedProperty;
            }

            Set<Property> sprops = Property.propertiesOf(beanClass, bean, true, propertyName);
            if( sprops==null || sprops.isEmpty() ){
                logFine("getClassProperty( {0}, {1}, {2} ) property not found",beanClass,propertyName,bean);
                return null;
            }

            Property firstProperty = null;
            for( Property p : sprops ){
                firstProperty = p;
                break;
            }

            if( firstProperty==null ){
                logFine("getClassProperty( {0}, {1}, {2} ) property not found",beanClass,propertyName,bean);
                return null;
            }

            classProperties.put(beanClass, firstProperty);
            logFiner("getClassProperty( {0}, {1}, {2} ) cache property ",beanClass,propertyName,bean, firstProperty);

            return firstProperty;
        }
    }

    private PropertyValue readFromBean( final Object bean, final String name ){
        synchronized(sync){
            logFine("readFromBean( {0}, {1} )",bean,name);

            Property property = getClassProperty(bean.getClass(), name, bean);
            if( property==null ){
                if( bean instanceof Map ){
                    logFiner("readFromBean( {0}, {1} ) property not found, try read bean as map",bean,name);
                    return readFromMap((Map)bean, name);
                }

                logFiner("readFromBean( {0}, {1} ) property not found, read as dummy",bean,name);
                return readAsDummy(bean,name);
            }

            try{
                property.setBean(bean);
                Object val = property.read();
                PropertyValue pvalue = new PropertyValue(property, val, null);

                logFiner("readFromBean property={0} value={2} readOnly={1}",
                    property.getName(),
                    property.isReadOnly(),
                    val
                );

                return pvalue;
            }catch( Throwable err ){
                return new PropertyValue(property, null, err);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="mapProperties">
    private Map<Object,Property> mapProperties = new WeakHashMap<Object, Property>();

    private Property getMapProperty( final Map map, final String name ){
        synchronized(this){
            if( map==null )return null;

            Property p = mapProperties.get(map);
            if( p==null && name==null )return null;

            if( name!=null && p==null ){
                Object value = map.get(name);

                Class t = map instanceof GetPropertyType ? ((GetPropertyType)map).getPropertyType(name) : null;
                t = t==null ? (value==null ? String.class : value.getClass()) : t;

                logFiner("create property {0} : {1} = {2}", name, t, value);

                final Property[] createdProp = new Property[]{ null };

                p = new Property(name, t, () -> {
                        Object val = map.get(name);
                        logFiner("readed map property {0} : {1} = {2}",
                            createdProp[0]==null ? name : createdProp[0].getName(),
                            val==null ? Void.class : val.getClass(),
                            val);
                        return val;
                }, arg -> {
                        logFiner("write map property {0} : {1} = {2}",
                            createdProp[0]==null ? name : createdProp[0].getName(),
                            arg==null ? Void.class : arg.getClass(),
                            arg);

                        map.put(name, arg);
                        return null;
                });

                createdProp[0] = p;

                mapProperties.put(map, p);
                return p;
            }

            return p;
        }
    }

    private PropertyValue readFromMap( final Map map, final String name ){
        synchronized(this){
            logFine("readFromMap( {0}, {1} )",map,name);

            Property property = getMapProperty(map,name);
            Object value = null;

            if( property==null )return readAsDummy(map, name);

            property.setBean(map);
            value = property.read();

            logFiner("return PropertyValue( {0}, {1}, null )",property, value);
            PropertyValue pvalue = new PropertyValue(property, value, null);
            return pvalue;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="tableColumns">
    /**
     * Возвращает видимые колонки таблицы
     * @return колонки таблицы
     */
    public TableColumn[] getTableColumns(){
        synchronized(sync){
            PropertyTable pt = getPropertyTable();
            if( pt==null ){
                return new TableColumn[0];
            }

            int modelIdx = getModelIndex();
            if( modelIdx<0 )return new TableColumn[0];

            TableColumnModel tcm = pt.getTable().getColumnModel();
            if( tcm==null )return new TableColumn[0];

            List<TableColumn> ltc = new ArrayList<>();

            int cc = tcm.getColumnCount();
            for( int tci=0; tci<cc; tci++ ){
                TableColumn tc = tcm.getColumn(tci);
                if( tc.getModelIndex()==modelIdx ){
                    ltc.add(tc);
                }
            }

            return ltc.toArray(new TableColumn[0]);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="visible">
    /**
     * Указывает отображать или нет колонку в таблице
     * @return true - отображать колонку
     */
    public boolean isVisible(){
        synchronized(sync){
            return getTableColumns().length>0;
        }
    }

    /**
     * Указывает отображать или нет колонку в таблице
     * @param visible true - отображать колонку
     */
    public void setVisible(boolean visible){
        PropertyTable pt = null;

        synchronized(sync){
            pt = getPropertyTable();
            if( pt==null )return;

            boolean cv = isVisible();
            if( cv==visible )return;
        }

        if( visible ){
            final JTable jt = pt.getTable();
            if( jt==null )return;

            final TableColumn tc = createTableColumn();
            if( tc==null )return;

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    jt.addColumn(tc);
                }};

            if( SwingUtilities.isEventDispatchThread() ){
                r.run();
            }else{
                SwingUtilities.invokeLater(r);
            }
        }else{
            final TableColumn[] tcs = getTableColumns();

            final JTable jt = pt.getTable();
            if( jt==null )return;

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    for( TableColumn tc : tcs ){
                        jt.removeColumn(tc);
                    }
                }};

            if( SwingUtilities.isEventDispatchThread() ){
                r.run();
            }else{
                SwingUtilities.invokeLater(r);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="createTableColumn()">
    /**
     * Создает колонку для отображения ее в таблице
     * @return колонка
     */
    public TableColumn createTableColumn(){
        synchronized(sync){
            PropertyTable pt = getPropertyTable();
            if( pt==null )return null;

            TableCellRenderer tcRender = pt.getPropertyRender();
            if( tcRender==null )return null;

            TreeTableNodeValueEditor tcEditor = pt.getPropertyEditor();
            if( tcEditor!=null )return null;

            int mi = getModelIndex();
            if( mi<0 )return null;

            TableColumn tc = new TableColumn(mi);

            String name = getName();
            if( name==null )name = "column#"+mi;

            TableCellRenderer hcr = pt.getHeaderRender();
            if( hcr!=null ){
                tc.setHeaderRenderer(hcr);
            }

            tc.setHeaderValue(name);

            tc.setCellRenderer(tcRender);
            tc.setCellEditor(tcEditor);

            return tc;
        }
    }
//</editor-fold>
}
