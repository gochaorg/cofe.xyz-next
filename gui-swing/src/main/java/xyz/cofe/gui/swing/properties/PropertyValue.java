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

import xyz.cofe.gui.swing.tree.FormattedValue;
import xyz.cofe.gui.swing.tree.TreeTableNodeGetFormat;
import xyz.cofe.gui.swing.tree.TreeTableNodeValueEditor;

/**
 * Значения свойства
 * @author nt.gocha@gmail.com
 */
public class PropertyValue
    extends FormattedValue
    implements TreeTableNodeGetFormat
{
    /**
     * Конструктор
     */
    public PropertyValue(){
    }

    /**
     * Конструктор
     * @param prop Свойство
     * @param value Значение свойства
     * @param err Ошибка (если есть) при чтении свойства или null
     */
    public PropertyValue( Property prop, Object value, Throwable err ){
        this.property = prop;
        this.value = value;
        this.error = err;
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public PropertyValue( PropertyValue sample ){
        if( sample!=null ){
            property = sample.property!=null ? sample.property.clone() : null;
            value = sample.value;
            error = sample.error;
            format = sample.format!=null ? sample.format.clone() : null;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="property - Свойство">
    protected Property property;

    /**
     * Возвращает свойство
     * @return свойство
     */
    public Property getProperty() {
        return property;
    }

    /**
     * Указывает свойство
     * @param property свойство
     */
    public void setProperty(Property property) {
        this.property = property;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="error">
    protected Throwable error;

    /**
     * Возвращает ошибка чтения свойства
     * @return ошибка или null
     */
    public Throwable getError() {
        return error;
    }

    /**
     * Указывает ошибку чтения свойства
     * @param error ошибка или null
     */
    public void setError(Throwable error) {
        this.error = error;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="editor">
    protected TreeTableNodeValueEditor.Editor editor;

    /**
     * Возвращает редактор свойства
     * @return редактор свойства
     */
    public TreeTableNodeValueEditor.Editor getEditor() {
        return editor;
    }

    /**
     * Указвает редактор свойства
     * @param editor редактор
     */
    public void setEditor(TreeTableNodeValueEditor.Editor editor) {
        this.editor = editor;
    }
    //</editor-fold>
}
