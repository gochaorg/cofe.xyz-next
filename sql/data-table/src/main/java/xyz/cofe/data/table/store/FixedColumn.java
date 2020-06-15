/*
 * The MIT License
 *
 * Copyright 2014 user.
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
package xyz.cofe.data.table.store;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Описывает фиксированную колонку
 * @author user
 */
public class FixedColumn {
    //<editor-fold defaultstate="collapsed" desc="PropertyChangeSupport">
    protected final PropertyChangeSupport psupp = new SwingPropertyChangeSupport(this);
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        psupp.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        psupp.removePropertyChangeListener(listener);
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return psupp.getPropertyChangeListeners();
    }
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        psupp.addPropertyChangeListener(propertyName, listener);
    }
    
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        psupp.removePropertyChangeListener(propertyName, listener);
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return psupp.getPropertyChangeListeners(propertyName);
    }
    
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        psupp.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    public void firePropertyChange(PropertyChangeEvent event) {
        psupp.firePropertyChange(event);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="begin - Начало колонки данных в строке">
    /**
     * Начало колонки данных в строке
     */
    protected int begin = 0;

    /**
     * Указывает начало колонки в строке (от нуля)
     * @return начало колонки в строке (от нуля)
     */
    public int getBegin() {
        return begin;
    }

    /**
     * Указывает начало колонки в строке (от нуля)
     * @param begin начало колонки в строке (от нуля)
     */
    public void setBegin(int begin) {
        Object old = this.getBegin();
        this.begin = begin;
        firePropertyChange("begin", old, getBegin());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="length - Ширина колонки">
    /**
     * Ширина колонки
     */
    protected int length = 0;

    /**
     * Указывает ширину колонки в символах
     * @return Ширина колонки в символах
     */
    public int getLength() {
        return length;
    }

    /**
     * Указывает ширину колонки в символах
     * @param length Ширина колонки в символах
     */
    public void setLength(int length) {
        Object old = this.getLength();
        this.length = length;
        firePropertyChange("length", old, getLength());
    }
    //</editor-fold>

    /**
     * Конструктор
     * @param begin Начало колонки в строке от нуля
     * @param length Длинна колонки в символах
     */
    public FixedColumn( int begin, int length ){
        this.begin = begin;
        this.length = length;
    }

    /**
     * Конструктор по умолчанию
     */
    public FixedColumn(){
    }

    /**
     * Конструктор копирования
     * @param src образец
     */
    public FixedColumn( FixedColumn src ){
        if( src!=null ){
            this.begin = src.begin;
            this.length = src.length;
        }
    }
    
    /**
     * Клонирование объекта
     * @return клон
     */
    @Override
    public FixedColumn clone(){
        return new FixedColumn(this);
    }
}
