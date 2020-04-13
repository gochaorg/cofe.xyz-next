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

package xyz.cofe.gui.swing.cell;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Рендер ячейки таблицы (JTable) с использованием рендера LabelRender
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 * @see LabelRender
 */
public class TCRenderer extends JComponent implements TableCellRenderer {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TCRenderer.class.getName());
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
        logger.entering(TCRenderer.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TCRenderer.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TCRenderer.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор по умолчанию
     */
    public TCRenderer(){
    }

    /**
     * Конструктор
     * @param lrender рендер значения
     */
    public TCRenderer( LabelRender lrender ){
        this.labelRender = lrender;
    }

    //<editor-fold defaultstate="collapsed" desc="cellContext : TableCellContext">
    protected TableCellContext cellContext;
    /**
     * Возвращает контекст значения для отображения ячейки
     * @return контекст отображения
     */
    public synchronized TableCellContext getCellContext(){
        if( cellContext!=null )return cellContext;
        cellContext = new TableCellContext();
        return cellContext;
    }
    /**
     * Указывает контекст значения для отображения ячейки
     * @param tcc контекст отображения
     */
    public void setCellContext(TableCellContext tcc){
        synchronized(this){
            cellContext = tcc;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="labelRender : LabelRender">
    protected LabelRender labelRender;
    /**
     * Возвращает рендер значения ячейки
     * @return рендер значения
     * @see DefaultLabelRender
     */
    public synchronized LabelRender getLabelRender(){
        if( labelRender!=null )return labelRender;
        labelRender = new DefaultLabelRender();
        return labelRender;
    }
    /**
     * Указывает рендер значения ячейки
     * @param lRender рендер значения
     */
    public void setLabelRender(LabelRender lRender){
        synchronized(this){
            labelRender = lRender;
        }
    }
    //</editor-fold>

    /**
     * Вычисление размеров ячейки
     * @param table таблица
     * @param value значение
     * @param selected значение выбранно пользователем
     * @param focused значение(ячейка) содержит фокус ввода
     * @param row строка ячейки таблицы
     * @param column колонка ячейки таблицы
     * @return размеры
     */
    protected Rectangle2D computeCellContextBound(JTable table, Object value, boolean selected, boolean focused, int row,int column){
        int w = getWidth();
        int h = getHeight();

        if( table!=null ){
            TableColumnModel tcm = table.getColumnModel();
            if( tcm!=null ){
                TableColumn tc = tcm.getColumn(column);
                if( tc!=null ){
                    w = tc.getWidth();
                }
            }
            h = table.getRowHeight(row);
        }

        return new Rectangle2D.Double(0, 0, w, h);
    }

    @Override
    public synchronized Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected, boolean hasFocus, int row, int column)
    {
        getCellContext().setBounds(computeCellContextBound(table, value, isSelected, hasFocus, row, column));
        getCellContext().setTable(table);
        getCellContext().setFocus(hasFocus);
        getCellContext().setSelected(isSelected);
        getCellContext().setRow(row);
        getCellContext().setColumn(column);
        getCellContext().setValue(value);

        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if( g instanceof Graphics2D ){
            Graphics2D gs = (Graphics2D)g;
            getLabelRender().cellRender(gs, getCellContext());
            return;
        }
        super.paintComponent(g);
    }

    /**
     * Вычисление размеров отображаемых даынных
     * @param gs контекст отображения
     * @return размер занимаемых данных
     */
    public Rectangle2D computeRect(Graphics2D gs){
        if( gs==null )return null;
        return getLabelRender().cellRectangle(gs, getCellContext());
    }
}
