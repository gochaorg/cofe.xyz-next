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

package xyz.cofe.gui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Событие мыши для ячейки таблицы
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class TableCellMouseEvent extends MouseEvent {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TableCellMouseEvent.class.getName());
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
        logger.entering(TableCellMouseEvent.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(TableCellMouseEvent.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(TableCellMouseEvent.class.getName(), method, result);
    }
    //</editor-fold>

    //<editor-fold desc="TableCellMouseEvent()">
    /**
     * Конструктор
     * @param source источник события
     * @param id идентификатор события
     * @param when время события
     * @param modifiers нажатия клавиш SHIFT/CONTROL/ALT...
     * @param x координаты
     * @param y координаты
     * @param clickCount кол-во нажатий на кнопку мыши
     * @param popupTrigger передать события вверх по дереву компонентов
     * @param button кнопка мыши
     */
    public TableCellMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount,
                               boolean popupTrigger, int button) {
        super(source, id, when, modifiers, x, y, clickCount, popupTrigger, button);
    }

    /**
     * Конструктор
     * @param source источник события
     * @param id идентификатор события
     * @param when время события
     * @param modifiers нажатия клавиш SHIFT/CONTROL/ALT...
     * @param x координаты
     * @param y координаты
     * @param clickCount кол-во нажатий на кнопку мыши
     * @param popupTrigger передать события вверх по дереву компонентов
     */
    public TableCellMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount,
                               boolean popupTrigger) {
        super(source, id, when, modifiers, x, y, clickCount, popupTrigger);
    }

    /**
     * Конструктор
     * @param source источник события
     * @param id идентификатор события
     * @param when время события
     * @param modifiers нажатия клавиш SHIFT/CONTROL/ALT...
     * @param x координаты
     * @param y координаты
     * @param xAbs абсолютные координаты
     * @param yAbs абсолютные координаты
     * @param clickCount кол-во нажатий на кнопку мыши
     * @param popupTrigger передать события вверх по дереву компонентов
     * @param button кнопка мыши
     */
    public TableCellMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int xAbs, int yAbs,
                               int clickCount, boolean popupTrigger, int button) {
        super(source, id, when, modifiers, x, y, xAbs, yAbs, clickCount, popupTrigger, button);
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public TableCellMouseEvent(MouseEvent sample ){
        super(sample.getComponent(), 
            sample.getID(), 
            sample.getWhen(), 
            sample.getModifiers(), 
            sample.getX(), sample.getY(), 
            sample.getXOnScreen(), sample.getYOnScreen(), 
            sample.getClickCount(), 
            sample.isPopupTrigger(), 
            sample.getButton()
        );
    }

    /**
     * Конструктор
     * @param sample образец для копирования
     * @param table таблица
     * @param row строка
     * @param column колонка
     */
    public TableCellMouseEvent(MouseEvent sample, JTable table, int row, int column ){
        super(sample.getComponent(), 
            sample.getID(), 
            sample.getWhen(), 
            sample.getModifiers(), 
            sample.getX(), sample.getY(), 
            sample.getXOnScreen(), sample.getYOnScreen(), 
            sample.getClickCount(), 
            sample.isPopupTrigger(), 
            sample.getButton()
        );
        this.table = table;
        this.row = row;
        this.column = column;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="table">
    protected JTable table;

    /**
     * Возвращает таблицу
     * @return таблица
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Указывает таблицу
     * @param table таблица
     */
    public void setTable(JTable table) {
        this.table = table;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="row">
    protected int row;

    /**
     * Возвращает строку таблицы
     * @return строка таблицы
     */
    public int getRow() {
        return row;
    }

    /**
     * Указывает строку таблицы
     * @param row строка таблицы
     */
    public void setRow(int row) {
        this.row = row;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="column">
    protected int column;

    /**
     * Возвращает колонку таблицы
     * @return колонка
     */
    public int getColumn() {
        return column;
    }

    /**
     * Указывает колонку таблицы
     * @param column клонка
     */
    public void setColumn(int column) {
        this.column = column;
    }
    //</editor-fold>
}
