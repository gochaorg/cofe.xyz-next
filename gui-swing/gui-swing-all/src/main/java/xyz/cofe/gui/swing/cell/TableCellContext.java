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

package xyz.cofe.gui.swing.cell;

import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import xyz.cofe.j2d.RectangleFn;

/**
 * Контекст ячейки таблицы
 * @param <T> тип self ссылки
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TableCellContext<T extends TableCellContext<?>> implements CellContext, TableContext
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TableCellContext.class.getName());
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
        logger.entering(TableCellContext.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TableCellContext.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TableCellContext.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор
     */
    public TableCellContext() {
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public TableCellContext(TableCellContext sample) {
        if( sample!=null ){
            this.bounds = sample.bounds;
            this.value = sample.value;
            this.table = sample.table;
            this.row = sample.row;
            this.column = sample.column;
            this.selected = sample.selected;
            this.focus = sample.focus;
        }
    }

    /**
     * Конструктор
     * @param value отображаемое значение
     * @param bounds границы отображения
     */
    public TableCellContext(Object value, Rectangle2D bounds) {
        this.value = value;
        this.bounds = bounds;
    }

    @Override
    public TableCellContext clone(){
        return new TableCellContext(this);
    }

    //<editor-fold defaultstate="collapsed" desc="bounds : Rectangle2D">
    protected Rectangle2D bounds;

    /**
     * Указывает расположение контекста/рамка в которую производится отображение
     * @return Рамка
     */
    @Override
    public Rectangle2D getBounds() {
        return bounds;
    }

    /**
     * Указывает расположение контекста/рамка в которую производится отображение
     * @param bounds Рамка
     */
    @Override
    public void setBounds(Rectangle2D bounds) {
        this.bounds = bounds;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="padLeft/padRight/padTop/padBottom()">
    /**
     * Уменьшает размер прямоугольника (bounds) слева
     * @param pad на сколько уменьшить
     * @return self ссылка
     */
    @Override
    public TableCellContext padLeft( double pad ){
        Rectangle2D rect = bounds;
        if( rect!=null ){
            bounds = RectangleFn.paddingLeft(rect, pad);
        }
        return this;
    }

    /**
     * Уменьшает размер прямоугольника (bounds) сверху
     * @param pad на сколько уменьшить
     * @return self ссылка
     */
    @Override
    public TableCellContext padTop( double pad ){
        Rectangle2D rect = bounds;
        if( rect!=null ){
            bounds = RectangleFn.paddingTop(rect, pad);
        }
        return this;
    }

    /**
     * Уменьшает размер прямоугольника (bounds) справа
     * @param pad на сколько уменьшить
     * @return self ссылка
     */
    @Override
    public TableCellContext padRight( double pad ){
        Rectangle2D rect = bounds;
        if( rect!=null ){
            bounds = RectangleFn.paddingRight(rect, pad);
        }
        return this;
    }

    /**
     * Уменьшает размер прямоугольника (bounds) снизу
     * @param pad на сколько уменьшить
     * @return self ссылка
     */
    @Override
    public TableCellContext padBottom( double pad ){
        Rectangle2D rect = bounds;
        if( rect!=null ){
            bounds = RectangleFn.paddingBottom(rect, pad);
        }
        return this;
    }
    //</editor-fold>

    /**
     * Смещает прямоугольник контекста
     * @param x на сколько по x
     * @param y на сколько по y
     * @return self ссылка
     */
    @Override
    public T move(double x, double y) {
        Rectangle2D rect = bounds;
        if( rect!=null ){
            bounds = RectangleFn.move(rect, x, y);
        }
        return (T)this;
    }

    /**
     * Устанавливает размер прямоугольника
     * @param width ширина
     * @param height высота
     * @return self ссылка
     */
    @Override
    public T size( double width, double height ){
        Rectangle2D rect = bounds;
        if( rect!=null ){
            bounds = RectangleFn.size(rect, width, height);
        }
        return (T)this;
    }

    //<editor-fold defaultstate="collapsed" desc="value">
    protected Object value;

    @Override
    public Object getValue() {
        return value;
    }

    /**
     * Указывает отображаемое значение
     * @param value отображаемое значение
     */
    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Указывает отображаемое значение
     * @param value отображаемое значение
     * @return self ссылка
     */
    @Override
    public T value(Object value){
        this.value = value;
        return (T)this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="table : JTable">
    protected JTable table;

    /**
     * Возвращает таблицу
     * @return таблица
     */
    @Override
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

    /**
     * Указывает таблицу
     * @param table таблица
     * @return self ссылка
     */
    public T table(JTable table) {
        this.table = table;
        return (T)this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="row : int">
    protected int row;

    /**
     * Возвращает отображаемую строку
     * @return строка
     */
    @Override
    public int getRow() {
        return row;
    }

    /**
     * Указывает отображаемую строку
     * @param row строка
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Указывает отображаемую строку
     * @param row строка
     * @return self ссылка
     */
    public T row(int row) {
        this.row = row;
        return (T)this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="column : int">
    protected int column;

    /**
     * Возвращает отображаемую колонку
     * @return колонка
     */
    @Override
    public int getColumn() {
        return column;
    }

    /**
     * Указывает отображаемую колонку
     * @param column колонка
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * Указывает отображаемую колонку
     * @param column колонка
     * @return self ссылка
     */
    public T column(int column) {
        this.column = column;
        return (T)this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="selected : boolean">
    protected boolean selected;

    /**
     * Возвращает выделена ли ячейка
     * @return true - ячейка выделена/выбрана
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /**
     * Указывает выделена ли ячейка
     * @param selected true - ячейка выделена/выбрана
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Указывает выделена ли ячейка
     * @param sel true - ячейка выделена/выбрана
     * @return self ссылка
     */
    public T selected(boolean sel){
        this.selected = sel;
        return (T)this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="focus : boolean">
    protected boolean focus;

    /**
     * Указывает содержет ли фокус ячейка
     * @return true - содержит фокус
     */
    @Override
    public boolean isFocus() {
        return focus;
    }

    /**
     * Указывает содержет ли фокус ячейка
     * @param focus true - содержит фокус
     */
    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    /**
     * Указывает содержет ли фокус ячейка
     * @param focus true - содержит фокус
     * @return self ссылка
     */
    public T focus(boolean focus){
        this.focus = focus;
        return (T)this;
    }
    //</editor-fold>
}
