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

package xyz.cofe.gui.swing.properties;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import xyz.cofe.fn.Pair;
import xyz.cofe.gui.swing.bean.UiBean;

/**
 * Описание свойства
 * @author nt.gocha@gmail.com
 */
public class Property
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(Property.class.getName());
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
        logger.entering(Property.class.getName(),method,params);
    }

    private static void logExiting(String method,Object result){
        logger.exiting(Property.class.getName(),method,result);
    }

    private static void logExiting(String method){
        logger.exiting(Property.class.getName(),method);
    }
    //</editor-fold>

    /**
     * Конструктор по умолчанию
     */
    public Property() {
    }

    /**
     * Конструктор
     * @param name Имя свойства
     * @param type Тип свойства
     * @param read Чтение
     * @param write Запись
     */
    public Property( String name, Class type, Supplier read, Function write) {
        this.name = name;
        this.propertyType = type;
        this.readFn = read;
        this.writeFn = write;
    }

    /**
     * Конструктор копирования
     * @param src образец для копирования
     */
    public Property( Property src ) {
        if( src==null )throw new IllegalArgumentException( "src==null" );
        synchronized( src ){
            this.propertyDescriptor = src.propertyDescriptor;
            this.bean = src.bean;
            this.bound = src.bound;
            this.constrained = src.constrained;
            this.displayName = src.displayName;
            this.expret = src.expret;
            this.hidden = src.hidden;
            this.name = src.name;
            this.notNull = src.notNull;
            this.preffered = src.preffered;
            this.propertyEditorClass = src.propertyEditorClass;
            this.propertyType = src.propertyType;
            this.readFn = src.readFn;
            this.readMethod = src.readMethod;
            this.readOnly = src.readOnly;
            this.shortDescription = src.shortDescription;
            this.uiBean = src.uiBean;
            this.writeFn = src.writeFn;
            this.writeMethod = src.writeMethod;
            this.htmlDescription = src.htmlDescription;
        }
    }

    protected final static WeakHashMap<PropertyDescriptor,Boolean> uiBeanApplied
        = new WeakHashMap<PropertyDescriptor, Boolean>();

    /**
     * Конструктор
     * @param bean исходный объект владелец свойства
     * @param src Описание свойства
     */
    public Property( Object bean, PropertyDescriptor src ) {
        if( src==null )throw new IllegalArgumentException( "src==null" );

        synchronized(src){
            this.bean = bean;
            this.propertyDescriptor = src;

            Method mread  = this.propertyDescriptor.getReadMethod();
            Method mwrite = this.propertyDescriptor.getWriteMethod();

            UiBean uib = mread!=null
                ? mread.getAnnotation(UiBean.class)
                : null;

            uib = uib==null && mwrite!=null
                ? mwrite.getAnnotation(UiBean.class)
                : uib;

            this.uiBean = uib;

            if( uib!=null ){
                if( uib.forceReadOnly() ){
                    this.readOnly = true;
                }

                if( uib.forceReadOnlyDescent() ){
                    this.readOnlyDescent = true;
                }

                if( uib.forceNotNull() ){
                    this.notNull = true;
                }

                if( uib.htmlDescription().length()>0 ){
                    this.htmlDescription = uib.htmlDescription();
                }

                synchronized( uiBeanApplied ){
                    Boolean applied = uiBeanApplied.get(src);
                    if( applied==null || !applied ){
                        uiBeanApplied.put(src, true);

                        String shortDesc = uib.shortDescription();
                        if( shortDesc!=null && shortDesc.length()>0 ){
                            String existsShortDesc = propertyDescriptor.getShortDescription();
                            if( !shortDesc.equals(existsShortDesc) ){
                                propertyDescriptor.setShortDescription(shortDesc);
                            }
                        }

                        String displayName = uib.displayName();
                        if( displayName!=null && displayName.length()>0 ){
                            String existsDisplayName = propertyDescriptor.getDisplayName();
                            if( !displayName.equals(existsDisplayName) ){
                                propertyDescriptor.setDisplayName(displayName);
                            }
                        }

                        Class propEdit = uib.propertyEditor();
                        if( propEdit!=null && !propEdit.equals(PropertyEditor.class) ){
                            propertyDescriptor.setPropertyEditorClass(propEdit);
                        }

                        if( uib.forceHidden() ){
                            propertyDescriptor.setHidden(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * Создание клона
     * @return клон
     */
    @Override
    public Property clone(){
        return new Property(this);
    }

    /**
     * Возвращает имя свойства
     * @return имя свойства
     */
    public String getPropertyName() {
        return getName();
    }

    //<editor-fold defaultstate="collapsed" desc="uiBean">
    protected volatile UiBean uiBean;

    /**
     * Возвращает настройки отображения/редактирования свойства
     * @return настройки или null
     */
    public synchronized UiBean getUiBean() {
        return uiBean;
    }

    /**
     * Указывает настройки отображения/редактирования свойства
     * @param uiBean настройки
     */
    public synchronized void setUiBean(UiBean uiBean) {
        this.uiBean = uiBean;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="propertyDescriptor">
    protected volatile PropertyDescriptor propertyDescriptor;

    /**
     * Возвращает описание свойства
     * @return описание свойства или null
     */
    public synchronized PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    /**
     * Указывает описание свойства
     * @param propertyDescriptor описапние
     */
    public synchronized void setPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor = propertyDescriptor;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bean">
    protected volatile Object bean;
    /**
     * Возвращает владельца свойства (объект)
     * @return владелец свойства (объект)
     */
    public synchronized Object getBean() {
        return bean;
    }
    /**
     * устанавливает владельца свойства (объект)
     * @param bean владелец свойства (объект)
     */
    public synchronized void setBean(Object bean) { this.bean = bean; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readFn : Func0">
    protected volatile Supplier readFn;

    /**
     * Возвращает функцию чтения свойства
     * @return функция чтения
     */
    public synchronized Supplier getReadFn()
    {
        return readFn;
    }

    /**
     * Указывает функцию чтения свойства
     * @param readFn функция чтения
     */
    public synchronized void setReadFn(Supplier readFn)
    {
        this.readFn = readFn;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="writeFn : Func1">
    protected volatile Function writeFn;

    /**
     * Указывает функцию записи значения в свойство
     * @return функция записи
     */
    public synchronized Function getWriteFn()
    {
        return writeFn;
    }

    /**
     * Указывает функцию записи значения в свойство
     * @param writeFn функция записи
     */
    public synchronized void setWriteFn(Function writeFn)
    {
        this.writeFn = writeFn;
    }
    //</editor-fold>

    private boolean readSetAccessible(){
        return true;
    }
    private boolean readKeepAccessible(){
        return true;
    }
    private static volatile WeakHashMap<Method,Boolean> accessibleIsSettedMap;
    private static WeakHashMap<Method,Boolean> accessibleIsSettedMap(){
        if( accessibleIsSettedMap!=null )return accessibleIsSettedMap;
        synchronized(Property.class){
            if( accessibleIsSettedMap!=null )return accessibleIsSettedMap;
            accessibleIsSettedMap = new WeakHashMap<>();
            return accessibleIsSettedMap;
        }
    }
    private boolean accessibleIsSetted( Method m ){
        Boolean v = accessibleIsSettedMap().get(m);
        return v!=null ? v : false;
    }
    private void accessibleSetted( Method m,boolean setted ){
        if( m!=null )accessibleIsSettedMap().put(m, setted);
    }

    //<editor-fold defaultstate="collapsed" desc="read / write">
    /**
     * Чтегие свойства
     * @return значение
     * @throws Error - ошибка чтения свойства
     */
    public synchronized Object read(){
        if( readFn!=null )return readFn.get();

        Object bn = bean;
        if( bn==null )throw new IllegalStateException("bean not set");

        Method mread = getReadMethod();
        if( mread==null )throw new IllegalStateException("can't read property "+getName()+" read method not set");

        try {
            if( readSetAccessible() && !accessibleIsSetted(mread) ){
                mread.setAccessible(true);
                if( readKeepAccessible() )accessibleSetted(mread,true);
            }
            Object val = mread.invoke(bn);
            return val;
        } catch (IllegalAccessException ex) {
            logSevere(
                "can't read property "+getPropertyName()+
                    " of "+bn.getClass()+
                    " (method "+mread+
                    ") "+ex.getMessage());
            throw new Error(ex);
        } catch (IllegalArgumentException ex) {
            logSevere(
                "can't read property "+getPropertyName()+
                    " of "+bn.getClass()+
                    " (method "+mread+
                    ") "+ex.getMessage());
            throw new Error(ex);
        } catch (InvocationTargetException ex) {
            logSevere(
                "can't read property "+getPropertyName()+
                    " of "+bn.getClass()+
                    " (method "+mread+
                    ") "+ex.getMessage());
            throw new Error(ex);
        }
    }

    /**
     * Запись свойства
     * @param val значение
     */
    public synchronized void write(Object val){
        if( writeFn!=null ){
            writeFn.apply(val);
            //return false;
            return;
        }

        //if( isReadOnly() )return false;
        if( isReadOnly() )return;

        Object bn = bean;
        if( bn==null )throw new IllegalStateException("bean not set");

        Method mwrite = getWriteMethod();
        if( mwrite==null )throw new IllegalStateException("can't write property "+getName()+" write method not set");

        try {
            mwrite.invoke(bn, val);
            //return true;
        } catch (IllegalAccessException ex) {
            throw new Error(ex);
        } catch (IllegalArgumentException ex) {
            throw new Error(ex);
        } catch (InvocationTargetException ex) {
            throw new Error(ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="addPropertyChangeListener()">
    /**
     * Добавляет подписчика на уведомления о изменении свойства
     * @param listener подписчик
     * @return отписка от уведомлений
     */
    public synchronized Closeable addPropertyChangeListener( final PropertyChangeListener listener ){
        final Object bin = bean;
        if( bin==null )throw new IllegalStateException("property 'bean' not set");

        Class cbean = bin.getClass();
        try {
            final Method mAdd = cbean.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
            final Method mRemove = cbean.getMethod("removePropertyChangeListener", PropertyChangeListener.class);

            mAdd.invoke(bin, listener);
            Closeable c = new Closeable() {
                protected boolean closeCalled = false;
                @Override
                public void close() throws IOException {
                    try {
                        if( closeCalled )return;
                        closeCalled = true;
                        mRemove.invoke(bin, listener);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            return c;
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new Closeable() {
            @Override
            public void close() throws IOException {
            }
        };
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readOnly : Boolean">
    protected volatile Boolean readOnly = null;

    /**
     * Свойство доступно только для записи или нет
     * @return true - только для записи
     */
    public synchronized boolean isReadOnly() {
        if( readOnly==null ){
            Method wmethod = getWriteMethod();

            boolean wmethodExists = wmethod!=null;
            boolean wfnExists = writeFn!=null;

            boolean ro = !(wmethodExists || wfnExists);
            return ro;
        }
        return readOnly;
    }
    /**
     * Свойство доступно только для записи или нет
     * @param readOnly true - только для записи
     */
    public synchronized void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readOnlyDescent : Boolean">
    protected volatile Boolean readOnlyDescent = null;

    /**
     * Дочерние свойства/объекты - так же должны быть только для чтения
     * @return true - дочрение так же readOnly; null - как есть
     */
    public synchronized Boolean getReadOnlyDescent() {
        return readOnlyDescent;
    }

    /**
     * Дочерние свойства/объекты - так же должны быть только для чтения
     * @param readOnlyDescent true - дочрение так же readOnly; null - как есть
     */
    public synchronized void setReadOnlyDescent(Boolean readOnlyDescent) {
        this.readOnlyDescent = readOnlyDescent;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="notNull">
    protected volatile boolean notNull = false;

    /**
     * Указывает требует ли свойство не null значения
     * @return true - не допускаются null значения
     */
    public synchronized boolean isNotNull()
    {
        Class type = getPropertyType();
        if( type!=null ){
            if( type.equals(byte.class) )return true;
            if( type.equals(short.class) )return true;
            if( type.equals(int.class) )return true;
            if( type.equals(long.class) )return true;
            if( type.equals(float.class) )return true;
            if( type.equals(double.class) )return true;
            if( type.equals(char.class) )return true;
            if( type.equals(boolean.class) )return true;
        }
        return notNull;
    }

    /**
     * Указывает требовать ли наличие значения
     * @param notNull true - не допускаются null значения
     */
    public synchronized void setNotNull(boolean notNull)
    {
        this.notNull = notNull;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="propertyType : Class">
    protected volatile Class propertyType;

    /**
     * Указывает тип данных свойства
     * @return тип данных
     */
    public synchronized Class<?> getPropertyType() {
        if( propertyDescriptor==null )return propertyType;
        return propertyDescriptor.getPropertyType();
    }

    /**
     * Указывает тип данных свойства
     * @param pt тип данных
     */
    public synchronized void setPropertyType(Class<?> pt){
        propertyType = pt;
        //if( propertyDescriptor!=null )return;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="get/set Value attributeNames">
    /**
     * Указывает атрибут свойства
     * @param attributeName имя атрибута
     * @param value значение
     * @see PropertyDescriptor#setValue(String, Object)
     */
    public synchronized void setValue(String attributeName, Object value) {
        if( propertyDescriptor==null )return;
        propertyDescriptor.setValue(attributeName, value);
    }

    /**
     * Возвращает атрибут свойства
     * @param attributeName имя атрибута
     * @return значение атрибута
     * @see PropertyDescriptor#getValue(String)
     */
    public synchronized Object getValue(String attributeName) {
        if( propertyDescriptor==null )return null;
        return propertyDescriptor.getValue(attributeName);
    }

    /**
     * Возвращает атрибуты свойства
     * @return атрибуты
     * @see PropertyDescriptor#attributeNames()
     */
    public synchronized Enumeration<String> attributeNames() {
        if( propertyDescriptor==null )return new Enumeration<String>()
        {
            @Override
            public boolean hasMoreElements()
            {
                return false;
            }

            @Override
            public String nextElement()
            {
                return null;
            }
        };
        return propertyDescriptor.attributeNames();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readMethod : Method">
    protected volatile Method readMethod;

    /**
     * Возвращает метод чтения значения свойства
     * @return метод или null
     */
    public synchronized Method getReadMethod() {
        if( propertyDescriptor==null )return readMethod;
        return propertyDescriptor.getReadMethod();
    }

    /**
     * Указывает метод чтения значения свойства
     * @param readMethod метод
     * @throws IntrospectionException см propertyDescriptor
     */
    public synchronized void setReadMethod(Method readMethod) throws IntrospectionException {
        this.readMethod = readMethod;
        if( propertyDescriptor==null )return;
        propertyDescriptor.setReadMethod(readMethod);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="writeMethod : Method">
    protected volatile Method writeMethod;

    /**
     * Указывает метод записи значения в свойство
     * @return метод или null
     */
    public synchronized Method getWriteMethod() {
        if( propertyDescriptor==null )return writeMethod;
        return propertyDescriptor.getWriteMethod();
    }

    /**
     * Указывает метод записи значения в свойство
     * @param writeMethod метод
     * @throws IntrospectionException см propertyDescriptor
     */
    public synchronized void setWriteMethod(Method writeMethod) throws IntrospectionException {
        this.writeMethod = writeMethod;
        if( propertyDescriptor!=null ){
            propertyDescriptor.setWriteMethod(writeMethod);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bound : boolean">
    protected volatile boolean bound;

    /**
     * Указывает что свойство генерирует событие PropertyChange
     * @return true - свойство генерирует событие / false - наверно не генерирует
     */
    public synchronized boolean isBound() {
        if( propertyDescriptor==null )return bound;
        return propertyDescriptor.isBound();
    }

    /**
     *  Указывает что свойство генерирует событие PropertyChange
     * @param bound true - свойство генерирует событие / false - наверно не генерирует
     */
    public synchronized void setBound(boolean bound) {
        this.bound = bound;
        if( propertyDescriptor!=null ){
            propertyDescriptor.setBound(bound);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="constrained : boolean">
    protected volatile boolean constrained;

    /**
     * Указывает что возможен запрет на изменение свойства
     * @return true - на свойстве надоложено ограничение
     * @see PropertyDescriptor#isConstrained()
     */
    public synchronized boolean isConstrained() {
        if( propertyDescriptor!=null ){
            return propertyDescriptor.isConstrained();
        }
        return constrained;
    }

    /**
     * Указывает что возможен запрет на изменение свойства
     * @param constrained true - на свойстве надоложено ограничение
     * @see PropertyDescriptor#setConstrained(boolean)
     */
    public synchronized void setConstrained(boolean constrained) {
        this.constrained = constrained;
        if( propertyDescriptor==null )return;
        propertyDescriptor.setConstrained(constrained);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="propertyEditorClass">
    protected volatile Class<?> propertyEditorClass;

    /**
     * Указывает редактор свойства
     * @param propertyEditorClass редактор свойства
     */
    public synchronized void setPropertyEditorClass(Class<?> propertyEditorClass) {
        this.propertyEditorClass = propertyEditorClass;

        if( propertyDescriptor==null )return;
        propertyDescriptor.setPropertyEditorClass(propertyEditorClass);
    }

    /**
     * Указывает редактор свойства
     * @return редактор свойства
     */
    public synchronized Class<?> getPropertyEditorClass() {
        if( propertyDescriptor==null )return propertyEditorClass;
        return propertyDescriptor.getPropertyEditorClass();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="createPropertyEditor(bean)">
    public PropertyEditor createPropertyEditor(Object bean) {
        if( propertyDescriptor==null )throw new IllegalArgumentException("propertyDescriptor is null");
        return propertyDescriptor.createPropertyEditor(bean);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="name">
    protected volatile String name;

    /**
     * Указывает имя свойства
     * @return имя свойства
     */
    public synchronized String getName() {
        if( propertyDescriptor==null )return name;
        return propertyDescriptor.getName();
    }

    /**
     * Указывает имя свойства
     * @param name имя свойства
     */
    public synchronized void setName(String name) {
        this.name = name;
        if( propertyDescriptor==null )return;
        propertyDescriptor.setName(name);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="displayName">
    protected volatile String displayName;

    /**
     * Указывает отображаемое имя свойства
     * @return отображаемое имя свойства
     */
    public synchronized String getDisplayName() {
        if( propertyDescriptor==null ){
            if( displayName!=null )return displayName;
            return getName();
        }else{
            return propertyDescriptor.getDisplayName();
        }
    }

    /**
     * Указывает отображаемое имя свойства
     * @param displayName отображаемое имя свойства
     */
    public synchronized void setDisplayName(String displayName) {
        this.displayName = displayName;
        if( propertyDescriptor==null )return;
        propertyDescriptor.setDisplayName(displayName);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="expret">
    protected volatile boolean expret = false;

    /**
     * Указывает что данное свойство доступно для экспертов
     * @return true - желательно чтоб правил его эксперт
     * @see PropertyDescriptor#isExpert()
     */
    public synchronized boolean isExpert() {
        if( propertyDescriptor==null )return expret;
        return propertyDescriptor.isExpert();
    }

    /**
     * Указывает что данное свойство доступно для экспертов
     * @param expert true - желательно чтоб правил его эксперт
     * @see PropertyDescriptor#setExpert(boolean)
     */
    public synchronized void setExpert(boolean expert) {
        this.expret = expert;

        if( propertyDescriptor==null )return;
        propertyDescriptor.setExpert(expert);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hidden">
    protected volatile boolean hidden = false;

    /**
     * Указывает что свойство должно быть скрытым и не отображаться в редакторе
     * @return true - свойство скрыто от редактирования
     */
    public synchronized boolean isHidden() {
        if( propertyDescriptor==null )return hidden;
        return propertyDescriptor.isHidden();
    }

    /**
     * Указывает что свойство должно быть скрытым и не отображаться в редакторе
     * @param hidden true - свойство скрыто от редактирования
     */
    public synchronized void setHidden(boolean hidden) {
        this.hidden = hidden;

        if( propertyDescriptor==null )return;
        propertyDescriptor.setHidden(hidden);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="preffered">
    protected volatile boolean preffered = false;

    /**
     * Указывает что свойство предпочитаемое для редактирования
     * @return свойство часто редактируемое
     * @see PropertyDescriptor#isPreferred()
     */
    public synchronized boolean isPreferred() {
        if( propertyDescriptor==null )return preffered;
        return propertyDescriptor.isPreferred();
    }

    /**
     * Указывает что свойство предпочитаемое для редактирования
     * @param preferred свойство часто редактируемое
     * @see PropertyDescriptor#setPreferred(boolean)
     */
    public synchronized void setPreferred(boolean preferred) {
        this.preffered = preferred;
        if( propertyDescriptor==null )return;
        propertyDescriptor.setPreferred(preferred);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="shortDescription">
    protected volatile String shortDescription;

    /**
     * Возвращает краткое описание свойства
     * @return краткое описание или null
     */
    public synchronized String getShortDescription() {
        if( propertyDescriptor==null )return shortDescription;
        return propertyDescriptor.getShortDescription();
    }

    /**
     * Указывает краткое описание свойства
     * @param text краткое описание
     */
    public synchronized void setShortDescription(String text) {
        this.shortDescription = text;
        if( propertyDescriptor==null )return;
        propertyDescriptor.setShortDescription(text);
    }
    //</editor-fold>

    protected volatile String htmlDescription;

    /**
     * Возвращает описание свойства в вормате html
     * @return описание или null
     */
    public synchronized String getHtmlDescription() {
        return htmlDescription;
    }

    /**
     * Указывает описание свойства в вормате html
     * @param htmlDescription описание или null
     */
    public synchronized void setHtmlDescription(String htmlDescription) {
        this.htmlDescription = htmlDescription;
    }

    //<editor-fold defaultstate="collapsed" desc="propertiesOf()">
    /**
     * Возвращает свойства для заданного объекта
     * @param cls тип объекта
     * @param bean сам объект
     * @return свойства
     */
    public static Set<Property> propertiesOf( Class cls, Object bean ){
        return propertiesOf(cls, bean, (Predicate<PropertyDescriptor>) null);
    }

    /**
     * Запрос к списку свойства
     */
    public static class PropertyQuery {
        protected Set<String> names = new LinkedHashSet<String>();

        /**
         * Возвращает перечень свойств
         * @return имена свойств
         */
        public Set<String> getNames() {
            return names;
        }

        /**
         * Указывает что нужно искать строго определенные свойства
         * @param names имена свойств
         * @return self ссылка
         */
        public PropertyQuery include( String ... names ){
            this.names.addAll(Arrays.asList(names));
            this.include = true;
            return this;
        }

        /**
         * Указывает что нужно искать все свойства, за исключением указанных
         * @param names исключаемые свойства
         * @return self ссылка
         */
        public PropertyQuery exclude( String ... names ){
            this.names.addAll(Arrays.asList(names));
            this.include = false;
            return this;
        }

        protected boolean include = false;

        protected boolean skipHidden = true;

        /**
         * Какие свойства пропускать
         */
        public class Skip {
            /**
             * Указывает что пропускать свойства отмеченные как скрытые
             * @param skip true - пропускать скрытые свойства
             * @return запрос к списку свойств
             */
            public PropertyQuery hidden( boolean skip ){
                skipHidden = skip;
                return PropertyQuery.this;
            }
        }

        /**
         * Указывает какие свойства пропускать
         * @return какие свойства пропускать
         */
        public Skip skip(){ return new Skip(); }

        /**
         * Создает предикат для фильтрации свойств
         * @return предикат
         */
        public Predicate<PropertyDescriptor> build(){
            final Set<String> nms = new LinkedHashSet<String>();
            nms.addAll(names);

            final boolean inc = include;

            final boolean shidden = skipHidden;

            return new Predicate<PropertyDescriptor>() {
                @Override
                public boolean test(PropertyDescriptor pd) {
                    if( pd==null )return false;
                    if( pd.getPropertyType()==null )return false;
                    if( shidden && pd.isHidden() )return false;
                    if( inc ){
                        for( String incName : nms ){
                            if( incName==null )continue;

                            String pname = pd.getName();
                            if( incName.equals(pname) )
                                return true;
                        }
                        return false;
                    }else{
                        for( String incName : nms ){
                            if( incName==null )continue;

                            String pname = pd.getName();
                            if( incName.equals(pname) )
                                return false;
                        }
                        return true;
                    }
                }
            };
        }
    }

    /**
     * Создает запрос для фильтрации свойств
     * @return запрос фильтрации свойств
     */
    public static PropertyQuery propertyQuery(){
        return new PropertyQuery();
    }

    /**
     * Получает свойства объекта
     * @param cls Тип объекта
     * @param bean Сам объект
     * @param include true - Включать только указанные / false - исключать указанные
     * @param names имена включаемых/исключаемых свойств
     * @return Свойства
     */
    public static Set<Property> propertiesOf( Class cls, Object bean, final boolean include, final String ... names ){
        Predicate<PropertyDescriptor> f = new Predicate<PropertyDescriptor>() {
            @Override
            public boolean test(PropertyDescriptor pd) {
                if( pd==null )return false;
                if( pd.getPropertyType()==null )return false;
                if( include ){
                    if( names==null || names.length==0 )return false;
                    for( String incName : names ){
                        if( incName==null )continue;

                        String pname = pd.getName();
                        if( incName.equals(pname) )
                            return true;
                    }
                    return false;
                }else{
                    if( names==null || names.length==0 )return true;
                    for( String incName : names ){
                        if( incName==null )continue;

                        String pname = pd.getName();
                        if( incName.equals(pname) )
                            return false;
                    }
                    return true;
                }
            }
        };

        return propertiesOf(cls, bean, f);
    }

    /**
     * Получение свойств объекта согласно указанному фильтру
     * @param cls Тип объекта
     * @param bean объект
     * @param filter фильтр
     * @return Свойства
     */
    public static Set<Property> propertiesOf( Class cls, Object bean, Predicate<PropertyDescriptor> filter ){
        return propertiesOf(cls, bean, filter, null);
    }

    /**
     * Получение свойств объекта согласно указанному фильтру
     * @param cls Тип объекта
     * @param bean объект
     * @param filter фильтр
     * @param propertyBuilder конвертирование свойств
     * @return Свойства
     */
    public static Set<Property> propertiesOf( Class cls,
                                              Object bean,
                                              Predicate<PropertyDescriptor> filter,
                                              Function<Pair<PropertyDescriptor,Object>,Property> propertyBuilder ){
        if( cls==null )throw new IllegalArgumentException("cls==null");
        LinkedHashSet<Property> props = new LinkedHashSet<Property>();

        LinkedHashSet<String> hiddenPropertyNames = new LinkedHashSet<String>();
        Object oUiBeanCls = cls.getAnnotation(UiBean.class);

        if( oUiBeanCls!=null ){
            UiBean uibCls = (UiBean)oUiBeanCls;
            hiddenPropertyNames.addAll(Arrays.asList(uibCls.hiddenPeroperties()));
        }

        try {
            BeanInfo bi = Introspector.getBeanInfo(cls);
            for( PropertyDescriptor pd : bi.getPropertyDescriptors() ){
                if( filter!=null ){
                    if( filter.test(pd) ){
                        props.add(new Property(bean, pd));
                    }
                    continue;
                }

                if( pd.getPropertyType()==null )continue;

                Property p = null;

                if( propertyBuilder!=null ){
                    p = propertyBuilder.apply(Pair.<PropertyDescriptor,Object>of(pd,bean));
                }

                if( p==null )p = new Property(bean, pd);

                /*if( p.isHidden() ){
                logFiner("skip property {0} of {1}", p.getPropertyName(), cls.getName());
                continue;
                }*/

                /*if( hiddenPropertyNames.contains(p.getPropertyName()) ){
                continue;
                }*/

                props.add(p);
            }
        } catch (IntrospectionException ex) {
            Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
        }

        return props;
    }
    //</editor-fold>
}
