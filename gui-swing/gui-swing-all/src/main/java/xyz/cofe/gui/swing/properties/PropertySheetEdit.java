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

import java.util.ArrayList;
import java.util.List;

/**
 * Событие редакатирования объекта
 * @author user
 */
public class PropertySheetEdit extends PropertySheetEvent {
    public PropertySheetEdit() {
    }

    public PropertySheetEdit(PropertySheet propertySheet) {
        super(propertySheet);
    }

    /**
     * Редактируемый бин
     */
    protected volatile Object bean;

    /**
     * Возвращает редактируемый бин
     * @return редактируемый бин
     */
    public Object getBean() {
        synchronized(this){
            return bean;
        }
    }

    /**
     * Указывает редактируемый бин
     * @param bean редактируемый бин
     */
    public void setBean(Object bean) {
        synchronized(this){
            this.bean = bean;
        }
    }

    protected volatile List<Property> properties;

    /**
     * Указывает редактируемые свойства
     * @return редактируемые свойства
     */
    public List<Property> getProperties() {
        synchronized(this){
            if( properties!=null )return properties;
            properties = new ArrayList<>();
            return properties;
        }
    }

    /**
     * Указывает редактируемые свойства
     * @param properties редактируемые свойства
     */
    public void setProperties(List<Property> properties) {
        synchronized(this){
            this.properties = properties;
        }
    }
}
