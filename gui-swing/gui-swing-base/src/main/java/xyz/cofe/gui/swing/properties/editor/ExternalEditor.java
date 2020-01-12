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
package xyz.cofe.gui.swing.properties.editor;

import java.awt.Component;

/**
 * Внешний редактор
 * @author user
 */
public interface ExternalEditor {
    /**
     * Указывает контекст редактора
     * @param contextComponent Графический компонент с которым связан редактор
     */
    public void setContextComponent( Component contextComponent );

    /**
     * Указывает контекст редактора
     * @return Графический компонент с которым связан редактор
     */
    public Component getContextComponent();

    /**
     * Указывает куда передавать события
     * @param consumer Куда передовать события редактора
     */
    public void setConsumer( ExternalEditorConsumer consumer );

    /**
     * Указывает куда передавать события
     * @return Куда передовать события редактора
     */
    public ExternalEditorConsumer getConsumer();

    /**
     * Открывает редактор
     * @param value Редактируемое значение
     */
    public void open( Object value );

    /**
     * Возвращает признак что в данный момент редактор открыт
     * @return true - редактор открыт
     */
    public boolean isOpen();

    /**
     * Закрытие редактора
     */
    public void close();

    /**
     * Установка нового значения редактора
     * @param value редактируемое значение
     */
    public void setValue( Object value );

    /**
     * Получение редактируемого значения
     * @return текущее редактируемое значение
     */
    public Object getValue();
}
