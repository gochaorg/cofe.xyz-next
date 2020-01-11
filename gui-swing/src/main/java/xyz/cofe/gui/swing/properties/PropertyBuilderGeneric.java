/*
 * The MIT License
 *
 * Copyright 2018 user.
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

import java.beans.PropertyEditor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.ecolls.Fn0;
import xyz.cofe.ecolls.Fn1;
import xyz.cofe.gui.swing.bean.UiBean;

/**
 * Конструирование свойств
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 * @param <T> Тип self ссылки
 */
public class PropertyBuilderGeneric<T extends PropertyBuilderGeneric<?>> {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(PropertyBuilderGeneric.class.getName());
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
        logger.entering(PropertyBuilderGeneric.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(PropertyBuilderGeneric.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(PropertyBuilderGeneric.class.getName(), method, result);
    }
    //</editor-fold>

    private String name;

    /**
     * Указывает имя свойства
     * @param name имя свойства
     * @return self ссылка
     */
    public T name( String name ){
        this.name = name;
        return (T)this;
    }

    private Object value;

    /**
     * Указывает значение свойства
     * @param value значение
     * @return self ссылка
     */
    public T value( Object value ){
        this.value = value;
        return (T)this;
    }

    private Class  type;

    /**
     * Указывает тип свойства
     * @param type тип
     * @return self ссылка
     */
    public T type( Class type ){
        this.type = type;
        return (T)this;
    }

    private Fn0 reader;
    /**
     * Указывает функцию чтения свойства
     * @param reader функция чтения
     * @return self ссылка
     */
    public T reader( Fn0 reader ){
        this.reader = reader;
        return (T)this;
    }

    private Fn1 writer;
    /**
     * Указывает функцию записи
     * @param writer функция записи
     * @return self ссылка
     */
    public T writer( Fn1 writer ){
        this.writer = writer;
        return (T)this;
    }

    private String category="";
    /**
     * Укащывает категорию свйоства
     * @param category имя категории
     * @return self ссылка
     */
    public T category( String category ){
        if( category==null )category = "";
        this.category = category;
        return (T)this;
    }

    private String shortDescription="";
    /**
     * Указывает короткое описание
     * @param shortDescription кртакое описание
     * @return self ссылка
     */
    public T shortDescription( String shortDescription ){
        if( shortDescription==null )shortDescription = "";
        this.shortDescription = shortDescription;
        return (T)this;
    }

    private String htmlDescription="";
    /**
     * Указыает html описание
     * @param htmlDescription html описание
     * @return self ссылка
     */
    public T htmlDescription( String htmlDescription ){
        if( htmlDescription==null )htmlDescription = "";
        this.htmlDescription = htmlDescription;
        return (T)this;
    }

    private String displayName="";
    /**
     * Указыает отображаемое имя
     * @param displayName отображаемое имя
     * @return self ссылка
     */
    public T displayName( String displayName ){
        if( displayName==null )displayName = "";
        this.displayName = displayName;
        return (T)this;
    }

    private Class propertyEditor=java.beans.PropertyEditor.class;
    /**
     * Указывает редактор свойства
     * @param propertyEditor редкатор свойства
     * @return self ссылка
     */
    public T propertyEditor( Class<? extends java.beans.PropertyEditor> propertyEditor ){
        if( propertyEditor==null )propertyEditor = java.beans.PropertyEditor.class;
        this.propertyEditor = propertyEditor;
        return (T)this;
    }

    private boolean forceReadOnly=false;
    /**
     * Указывает что свойство доступно только для чтения
     * @param forceReadOnly только для чтения
     * @return self ссылка
     */
    public T forceReadOnly( boolean forceReadOnly ){
        this.forceReadOnly = forceReadOnly;
        return (T)this;
    }

    private boolean forceReadOnlyDescent=false;
    /**
     * Указывает что свойство и все низсходящие объекты этого свойства доступны только для чтения
     * @param forceReadOnlyDescent только для чтения
     * @return self ссылка
     */
    public T forceReadOnlyDescent( boolean forceReadOnlyDescent ){
        this.forceReadOnlyDescent = forceReadOnlyDescent;
        return (T)this;
    }

    private boolean forceHidden=false;
    /**
     * Указывает что свойство скрыто в редакторе свойств
     * @param forceHidden свойство скрыто
     * @return self ссылка
     */
    public T forceHidden( boolean forceHidden ){
        this.forceHidden = forceHidden;
        return (T)this;
    }

    private String[] hiddenPeroperties=new String[]{};
    /**
     * Указывает перечень скрываемых свойств
     * @param hiddenPeroperties скрываемые свойства
     * @return self ссылка
     */
    public T hiddenPeroperties( String ... hiddenPeroperties ){
        ArrayList<String> props = new ArrayList<>();
        if( hiddenPeroperties!=null ){
            for( String p : hiddenPeroperties ){
                if( p!=null )props.add(p);
            }
        }
        this.hiddenPeroperties = props.toArray(new String[]{});
        return (T)this;
    }

    private boolean forceNotNull=false;
    /**
     * Указывает что свойство не допускает null значения
     * @param forceNotNull null значения не допускаются
     * @return self ссылка
     */
    public T forceNotNull( boolean forceNotNull ){
        this.forceNotNull = forceNotNull;
        return (T)this;
    }

    private String editorOpts="";
    /**
     * Указывает опции редактироавния
     * @param editorOpts опции редактироывания
     * @return self ссылка
     */
    public T editorOpts( String editorOpts ){
        this.editorOpts = editorOpts;
        return (T)this;
    }

    private Object bean = null;
    /**
     * Указывает владельца свойства
     * @param bean владелец свойства
     * @return self ссылка
     */
    public T bean( Object bean ){
        this.bean = bean;
        return (T)this;
    }

    private Boolean bound = null;
    /**
     * Указывает что изменение свойства генерирует событие PropertyChange
     * @param bound генерация события PropertyChange
     * @return self ссылка
     */
    public T bound( Boolean bound ){
        this.bound = bound;
        return (T)this;
    }

    private Boolean preferred = null;
    /**
     * Указывает что свойство часто изменяется
     * @param preferred свойство часто изменяется
     * @return self ссылка
     */
    public T preferred( Boolean preferred ){
        this.preferred = preferred;
        return (T)this;
    }

    private Boolean notNull = null;
    /**
     * Указывает что свойство не поддерживает null значения
     * @param notNull свойство не поддерживает null значения
     * @return self ссылка
     */
    public T notNull( Boolean notNull ){
        this.notNull = notNull;
        return (T)this;
    }

    private Boolean hidden = null;
    /**
     * Указыает что свойство является скрытым
     * @param hidden является скрытым
     * @return self ссылка
     */
    public T hidden( Boolean hidden ){
        this.hidden = hidden;
        return (T)this;
    }

    private Boolean expert = null;
    /**
     * Указывает что свойство желательно изменял эксперт
     * @param expert свойство для экспертов
     * @return self ссылка
     */
    public T expert( Boolean expert ){
        this.expert = expert;
        return (T)this;
    }

    private Boolean constrained = null;
    /**
     * Указывает что свойство может генерировать исключения при измении значения
     * @param constrained свойство имеет ограничения
     * @return self ссылка
     */
    public T constrained( Boolean constrained ){
        this.constrained = constrained;
        return (T)this;
    }

    /**
     * Создание оъекта свойства
     * @return свойство
     */
    public Property build(){
        if( name==null )throw new IllegalArgumentException("name not set");
        if( value==null && (reader==null || type==null) )throw new IllegalArgumentException("value/read+type not set");

        Class typ = null;
        Fn0 rd = null;
        if( reader!=null && type!=null ){
            rd = reader;
            typ = type;
        }else if( value!=null ){
            rd = ()->value;
            typ = value.getClass();
        }else{
            throw new IllegalArgumentException("value/read+type not set");
        }

        Fn1 wr = writer;

        Property prop = new Property(name, typ, rd, wr);

        UiBean uib = new UiBean() {
            @Override public String category() { return category; }
            @Override public String shortDescription() { return shortDescription; }
            @Override public String htmlDescription() { return htmlDescription; }
            @Override public String displayName() { return displayName; }
            @Override public Class<? extends PropertyEditor> propertyEditor() { return propertyEditor; }
            @Override public boolean forceReadOnly() { return forceReadOnly; }
            @Override public boolean forceReadOnlyDescent() { return forceReadOnlyDescent; }
            @Override public boolean forceHidden() { return forceHidden; }
            @Override public String[] hiddenPeroperties() { return hiddenPeroperties; }
            @Override public boolean forceNotNull() { return forceNotNull; }
            @Override public String editorOpts() { return editorOpts; }
            @Override public Class<? extends Annotation> annotationType() { return UiBean.class; }
        };

        prop.setUiBean(uib);
        prop.setBean(bean);

        if( bound!=null )prop.setBound(bound);
        if( constrained!=null )prop.setConstrained(constrained);
        if( expert!=null )prop.setExpert(expert);
        if( hidden!=null )prop.setHidden(hidden);
        if( notNull!=null )prop.setNotNull(notNull);
        if( preferred!=null )prop.setPreferred(preferred);
        //if( bound!=null )prop.setReadOnly(bound);
        //if( bound!=null )prop.setShortDescription("");
        //if( bound!=null )prop.set("");

        return prop;
    }
}
