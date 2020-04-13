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

package xyz.cofe.gui.swing.properties;

import java.beans.PropertyEditor;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Настройки свойства
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class PropertySettings {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(PropertySettings.class.getName());
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
        logger.entering(PropertySettings.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(PropertySettings.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(PropertySettings.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор
     */
    public PropertySettings(){
        readWriteLock = new ReentrantReadWriteLock();
    }

    /**
     * Констркутор копирования
     * @param sample образец для копирования
     */
    public PropertySettings(PropertySettings sample){
        readWriteLock = new ReentrantReadWriteLock();
        if( sample!=null ){
            displayName = sample.displayName;
            constrained = sample.constrained;
            expert = sample.expert;
            hidden = sample.hidden;
            name = sample.name;
            preferred = sample.preferred;
            shortDescription = sample.shortDescription;
            readOnly = sample.readOnly;
            editorName = sample.editorName;
        }
    }

    @Override
    public PropertySettings clone(){
        return new PropertySettings(this);
    }

    /**
     * Блокировки на чтение/запись
     */
    protected transient final ReadWriteLock readWriteLock;

    /**
     * Выволнение операции чтения с использованием блокировки
     * @param <T> Тип результата
     * @param reader Функция чтения
     * @return результат
     */
    protected <T> T readLock(Supplier<T> reader){
        if( reader==null )throw new IllegalArgumentException("reader == null");
        try{
            readWriteLock.readLock().lock();
            T result = reader.get();
            return result;
        }finally{
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * Выволнение операции записи с использованием блокировки
     * @param <T> Тип результата
     * @param writer Функция записи
     * @return результат
     */
    protected <T> T writeLock(Supplier<T> writer){
        if(writer==null) throw new IllegalArgumentException("writer == null");
        try{
            readWriteLock.writeLock().lock();
            T result = writer.get();
            return result;
        }finally{
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Выволнение операции записи с использованием блокировки
     * @param writer Функция записи
     * @return результат
     */
    protected void writeLock(Runnable writer){
        if(writer==null) throw new IllegalArgumentException("writer == null");
        try{
            readWriteLock.writeLock().lock();
            writer.run();
        }finally{
            readWriteLock.writeLock().unlock();
        }
    }

    //<editor-fold defaultstate="collapsed" desc="displayName">
    protected volatile String displayName;

    /**
     * Указывает отображаемое имя свойства
     * @return отображаемое имя свойства
     */
    public String getDisplayName() {
        return readLock(()->displayName);
    }

    /**
     * Указывает отображаемое имя свойства
     * @param displayName отображаемое имя свойства
     */
    public void setDisplayName(final String displayName) {
        writeLock(() -> {
                PropertySettings.this.displayName = displayName;
        });

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="constrained">
    protected volatile Boolean constrained;

    /**
     * Указыает содержит ли свойство ограничения
     * @return true - содержит ограничения на возможные значения
     */
    public Boolean getConstrained() {
        return readLock(() -> constrained);
    }

    /**
     * Указыает содержит ли свойство ограничения
     * @param constrained содержит ограничения на возможные значения
     */
    public void setConstrained(final Boolean constrained) {
        writeLock(()->{
            PropertySettings.this.constrained = constrained;
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="expert">
    protected volatile Boolean expert;

    /**
     * Указывает что свойство рекомендуется редактировать экспертами
     * @return свойство для экспертов
     */
    public Boolean getExpert() {
        return readLock(()->expert);
    }

    /**
     * Указывает что свойство рекомендуется редактировать экспертами
     * @param expert свойство для экспертов
     */
    public void setExpert(final Boolean expert) {
        writeLock(()->{
            PropertySettings.this.expert = expert;
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hidden">
    protected volatile Boolean hidden;

    /**
     * Указывает что свойство скрыто для редактирования
     * @return свойство скрыто для редактирования
     */
    public Boolean getHidden() {
        return readLock( ()-> this.hidden );
    }

    /**
     * Указывает что свойство скрыто для редактирования
     * @param hidden свойство скрыто для редактирования
     */
    public void setHidden(final Boolean hidden) {
        writeLock( ()->{
            PropertySettings.this.hidden = hidden;
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="name">
    protected volatile String name;

    /**
     * Указывает имя свойства
     * @return имя свойства
     */
    public String getName() {
        return readLock( ()->name );
    }

    /**
     * Указывает имя свойства
     * @param name имя свойства
     */
    protected void setName(final String name) {
        writeLock( ()->{
            PropertySettings.this.name = name;
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="preferred">
    protected volatile Boolean preferred;

    /**
     * Указыает что свойство класса "предпочитаемое" для редактирования
     * @return свойство указыает на основной функционал
     */
    public Boolean getPreferred() {
        return readLock( ()->preferred );
    }

    /**
     * Указыает что свойство класса "предпочитаемое" для редактирования
     * @param preferred свойство указыает на основной функционал
     */
    public void setPreferred(final Boolean preferred) {
        writeLock( ()->{
            PropertySettings.this.preferred = preferred;
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="shortDescription">
    protected volatile String shortDescription;

    /**
     * Указыавет краткое текстовое описание свойства
     * @return краткое текстовое описание
     */
    public String getShortDescription() {
        return readLock( ()->shortDescription );
    }

    /**
     * Указыавет краткое текстовое описание свойства
     * @param shortDescription краткое текстовое описание
     */
    public void setShortDescription(final String shortDescription) {
        writeLock( ()->{
            PropertySettings.this.shortDescription = shortDescription;
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="htmlDescription">
    protected volatile String htmlDescription;

    /**
     * Указывает html описание свойства
     * @return html описание свойства
     */
    public String getHtmlDescription() {
        return readLock(()->htmlDescription);
    }

    /**
     * Указывает html описание свойства
     * @param htmlDescription html описание свойства
     */
    public void setHtmlDescription(final String htmlDescription) {
        writeLock( ()->{
            PropertySettings.this.htmlDescription = htmlDescription;
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readOnly">
    protected volatile Boolean readOnly;

    /**
     * Указыает что свойство доступно только для чтения
     * @return true - только для чтения
     */
    public Boolean getReadOnly() {
        return readLock( ()->readOnly );
    }

    /**
     * Указыает что свойство доступно только для чтения
     * @param readOnly true - только для чтения
     */
    public void setReadOnly(final Boolean readOnly) {
        writeLock( () -> {
                PropertySettings.this.readOnly = readOnly;
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="editorName">
    protected volatile String editorName;

    /**
     * Указывает имя редактора свойства
     * @return имя редактора
     */
    public String getEditorName() {
        return readLock( ()->editorName );
    }

    /**
     * Указывает имя редактор
     * @param editorName имя редактора
     */
    public void setEditorName(final String editorName) {
        writeLock( () -> {
            PropertySettings.this.editorName = editorName;
        });
    }
    //</editor-fold>

    /**
     * Применяет настройки к свойству
     * @param p свойство
     * @param pdb бд настроек/редакторов, возможно null
     */
    public void applyTo( final Property p, final PropertyDB pdb ){
        if (p== null) {
            throw new IllegalArgumentException("p==null");
        }

        readLock( () -> {
                if( displayName!=null )p.setDisplayName(displayName);
                if( constrained!=null )p.setConstrained(constrained);
                if( expert!=null )p.setExpert(expert);
                if( hidden!=null )p.setHidden(hidden);
                if( name!=null )p.setName(name);
                if( preferred!=null )p.setPreferred(preferred);
                if( shortDescription!=null )p.setShortDescription(shortDescription);
                if( htmlDescription!=null )p.setHtmlDescription(htmlDescription);

                if( readOnly!=null )p.setReadOnly(readOnly);
                if( editorName!=null && pdb!=null ){
                    PropertyEditor pe = pdb.getNamedEditors().get(editorName);
                    if( pe!=null )pdb.assignEditor(p, pe);
                }
                return null;
        });
    }

    @Override
    public int hashCode() {
        return readLock( () -> {
                int hash = 5;
                hash = 89 * hash + (PropertySettings.this.displayName != null ? PropertySettings.this.displayName.hashCode() : 0);
                hash = 89 * hash + (PropertySettings.this.constrained != null ? PropertySettings.this.constrained.hashCode() : 0);
                hash = 89 * hash + (PropertySettings.this.expert != null ? PropertySettings.this.expert.hashCode() : 0);
                hash = 89 * hash + (PropertySettings.this.hidden != null ? PropertySettings.this.hidden.hashCode() : 0);
                hash = 89 * hash + (PropertySettings.this.name != null ? PropertySettings.this.name.hashCode() : 0);
                hash = 89 * hash + (PropertySettings.this.preferred != null ? PropertySettings.this.preferred.hashCode() : 0);
                hash = 89 * hash + (PropertySettings.this.shortDescription != null ? PropertySettings.this.shortDescription.hashCode() : 0);
                hash = 89 * hash + (PropertySettings.this.readOnly != null ? PropertySettings.this.readOnly.hashCode() : 0);
                hash = 89 * hash + (PropertySettings.this.editorName != null ? PropertySettings.this.editorName.hashCode() : 0);
                return hash;
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final PropertySettings other = (PropertySettings) obj;
        return readLock(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                if ((PropertySettings.this.displayName == null) ? (other.displayName != null) : !PropertySettings.this.displayName.equals(other.displayName)) {
                    return false;
                }
                if ((PropertySettings.this.name == null) ? (other.name != null) : !PropertySettings.this.name.equals(other.name)) {
                    return false;
                }
                if ((PropertySettings.this.shortDescription == null) ? (other.shortDescription != null) : !PropertySettings.this.shortDescription.equals(other.shortDescription)) {
                    return false;
                }
                if ((PropertySettings.this.htmlDescription == null) ? (other.htmlDescription != null) : !PropertySettings.this.htmlDescription.equals(other.htmlDescription)) {
                    return false;
                }
                if ((PropertySettings.this.editorName == null) ? (other.editorName != null) : !PropertySettings.this.editorName.equals(other.editorName)) {
                    return false;
                }
                if (PropertySettings.this.constrained != other.constrained && (PropertySettings.this.constrained == null || !PropertySettings.this.constrained.equals(other.constrained))) {
                    return false;
                }
                if (PropertySettings.this.expert != other.expert && (PropertySettings.this.expert == null || !PropertySettings.this.expert.equals(other.expert))) {
                    return false;
                }
                if (PropertySettings.this.hidden != other.hidden && (PropertySettings.this.hidden == null || !PropertySettings.this.hidden.equals(other.hidden))) {
                    return false;
                }
                if (PropertySettings.this.preferred != other.preferred && (PropertySettings.this.preferred == null || !PropertySettings.this.preferred.equals(other.preferred))) {
                    return false;
                }
                if (PropertySettings.this.readOnly != other.readOnly && (PropertySettings.this.readOnly == null || !PropertySettings.this.readOnly.equals(other.readOnly))) {
                    return false;
                }
                return true;
            }
        });
    }
}
