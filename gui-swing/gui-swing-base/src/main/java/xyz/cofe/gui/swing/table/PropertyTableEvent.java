/*
 * The MIT License
 *
 * Copyright 2017 Kamnev Georgiy <nt.gocha@gmail.com>.
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

import java.io.Closeable;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.gui.swing.properties.Property;

/**
 * События PropertyTable
 * @author nt.gocha@gmail.com
 */
public class PropertyTableEvent {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(PropertyTableEvent.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }
    private static boolean isLogSevere(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.SEVERE.intValue();
    }
    private static boolean isLogWarning(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.WARNING.intValue();
    }
    private static boolean isLogInfo(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.INFO.intValue();
    }
    private static boolean isLogFine(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.FINE.intValue();
    }
    private static boolean isLogFiner(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINER.intValue();
    }
    private static boolean isLogFinest(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINEST.intValue();
    }

    private static void logEntering(String method,Object ... args){
        logger.entering(PropertyTableEvent.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(PropertyTableEvent.class.getName(), method, result);
    }

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

    //<editor-fold defaultstate="collapsed" desc="propertyTable">
    protected PropertyTable propertyTable;

    public PropertyTable getPropertyTable() {
        return propertyTable;
    }

    public void setPropertyTable(PropertyTable propertyTable) {
        this.propertyTable = propertyTable;
    }
    //</editor-fold>

    public PropertyTableEvent(){
    }

    public PropertyTableEvent(PropertyTable ptable){
        this.propertyTable = ptable;
    }

    //<editor-fold defaultstate="collapsed" desc="ElementCacheCreated">
    public static class ElementCacheCreated extends PropertyTableEvent {
        public ElementCacheCreated() {
        }

        public ElementCacheCreated(PropertyTable ptable) {
            super(ptable);
        }

        //<editor-fold defaultstate="collapsed" desc="cache">
        protected List cache;

        public List getCache() {
            return cache;
        }

        public void setCache(List cache) {
            this.cache = cache;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="index">
        protected int index;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="row">
        protected int row;

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="element">
        protected Object element;

        public Object getElement() {
            return element;
        }

        public void setElement(Object element) {
            this.element = element;
        }
        //</editor-fold>
    }

    public static AutoCloseable onElementCacheCreated( PropertyTable pt, final Consumer<ElementCacheCreated> consumer ){
        if( pt==null )throw new IllegalArgumentException("pt == null");
        if( consumer==null )throw new IllegalArgumentException("consumer == null");

        return pt.addPropertyTableListener( event -> {
                if( event instanceof ElementCacheCreated ){
                    consumer.accept((ElementCacheCreated)event);
                }
        });
    }
    //</editor-fold>

    public static class ElementCacheRemoved extends PropertyTableEvent {
        public ElementCacheRemoved() {
        }

        public ElementCacheRemoved(PropertyTable ptable) {
            super(ptable);
        }

        //<editor-fold defaultstate="collapsed" desc="cache">
        protected List cache;

        public List getCache() {
            return cache;
        }

        public void setCache(List cache) {
            this.cache = cache;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="index">
        protected int index;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="row">
        protected int row;

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="element">
        protected Object element;

        public Object getElement() {
            return element;
        }

        public void setElement(Object element) {
            this.element = element;
        }
        //</editor-fold>
    }

    //<editor-fold defaultstate="collapsed" desc="PropertyWrited">
    public static class PropertyWrited extends PropertyTableEvent {

        public PropertyWrited() {
        }

        public PropertyWrited(PropertyTable ptable) {
            super(ptable);
        }

        public PropertyWrited(PropertyTable ptable, PropertyColumn pcolumn, Property p, Object bean, Object val) {
            super(ptable);
            this.propertyColumn = pcolumn;
            this.property = p;
            this.bean = bean;
            this.value = val;
        }

        public PropertyWrited(PropertyTable ptable, PropertyColumn.PropertyWrited ev){
            super(ptable);
            if( ev!=null ){
                this.propertyColumn = ev.getPropertyColumn();
                this.property = ev.getProperty();
                this.bean = ev.getBean();
                this.value = ev.value;
            }
        }

        //<editor-fold defaultstate="collapsed" desc="propertyColumn">
        protected PropertyColumn propertyColumn;

        public PropertyColumn getPropertyColumn() {
            return propertyColumn;
        }

        public void setPropertyColumn(PropertyColumn propertyColumn) {
            this.propertyColumn = propertyColumn;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="property">
        protected Property property;

        public Property getProperty() {
            return property;
        }

        public void setProperty(Property property) {
            this.property = property;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="bean">
        protected Object bean;

        public Object getBean() {
            return bean;
        }

        public void setBean(Object bean) {
            this.bean = bean;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="value">
        protected Object value;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
        //</editor-fold>
    }

    public static AutoCloseable onPropertyWrited( PropertyTable pt, final Consumer<PropertyWrited> consumer ){
        if( pt==null )throw new IllegalArgumentException("pt == null");
        if( consumer==null )throw new IllegalArgumentException("consumer == null");

        return pt.addPropertyTableListener( event -> {
            if( event instanceof PropertyWrited ){
                consumer.accept((PropertyWrited)event);
            }
        });
    }
    //</editor-fold>
}
