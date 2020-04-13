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

package xyz.cofe.gui.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import xyz.cofe.gui.swing.color.ColorModificator;

/**
 * Рендер ячейки таблице по умолчанию
 * @author nt.gocha@gmail.com
 */
public class DefTableCellRender extends DefaultTableCellRenderer
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(DefTableCellRender.class.getName());
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
        logger.entering(DefTableCellRender.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(DefTableCellRender.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(DefTableCellRender.class.getName(), method, result);
    }
    //</editor-fold>

    protected Color unselectedForeground;
    protected Color unselectedBackground;
    protected Color alternateRowColor;
    protected Color selectionForeground;
    protected Color selectionBackground;

    protected Color focusedBackground;
    protected Color focusedForeground;

    protected boolean hasFocus = false;
    protected boolean isSelected = false;
    protected int row = 0;
    protected int column = 0;

    /**
     * Конструктор
     * @param table таблица
     */
    public DefTableCellRender(JTable table) {
        if( table==null )throw new IllegalArgumentException( "table==null" );

        unselectedForeground = table.getForeground();
        unselectedForeground = unselectedForeground==null ? Color.black : unselectedForeground;

        unselectedBackground = table.getBackground();
        unselectedBackground = unselectedBackground==null ? Color.white : unselectedBackground;

        alternateRowColor = UIManager.getColor("Table.alternateRowColor");

        selectionForeground = table.getSelectionForeground();
        selectionForeground = selectionForeground==null ? Color.white : selectionForeground;

        selectionBackground = table.getSelectionBackground();
        selectionBackground = selectionBackground==null ? Color.blue : selectionBackground;

        focusedBackground = new ColorModificator().brighter(-0.5f).apply(selectionBackground);
        focusedForeground = new ColorModificator().brighter(0.5f).apply(selectionForeground);

        // .........................................................

        setOpaque(true);
    }

    /**
     * Отображаеть строку альтернативным цветом (чет/нечет строка)
     * @param row индекс строки
     * @return true - отображать альтернативным цветом
     */
    protected boolean isPaintAlternateRowColor(int row){
        return alternateRowColor != null && row % 2 != 0;
    }

    /**
     * Возвращает цвет текста для ячейки
     * @param isSelected ячейка выбрана пользователем
     * @param hasFocus ячейка содержит фокус ввода
     * @param row строка таблицы
     * @param column колонока таблицы
     * @return цвет
     */
    protected Color getForegroundColorFor(
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column
    ){
        Color fg = unselectedForeground;

        if( isSelected ){
            fg = selectionForeground;
        }

        if( hasFocus && focusedBackground!=null ){
            fg = focusedForeground;
        }

        return fg;
    }

    @Override
    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column
    ) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        this.hasFocus = hasFocus;
        this.isSelected = isSelected;
        this.row = row;
        this.column = column;

        Color bg = unselectedBackground;
        Color fg = getForegroundColorFor( isSelected, hasFocus, row, column );

        if( isPaintAlternateRowColor(row) ){
            bg = alternateRowColor;
        }

        if( isSelected ){
            bg = selectionBackground;
        }

        if( hasFocus && focusedBackground!=null ){
            bg = focusedBackground;
        }

        setBackground(bg);
        setForeground(fg);

        Border brd = getBorder();
        if( brd!=null ){
            Insets inst = brd.getBorderInsets(this);
            if( inst!=null ){
                brd = new EmptyBorder(inst);
                setBorder(brd);
            }
        }

//        setBorder(null);

        return this;
    }

    protected void paintBackground( Graphics g ){
        if( g instanceof Graphics2D ){
            Graphics2D gs = (Graphics2D)g;

            Color bg = getBackground();
            if( hasFocus && focusedBackground!=null )bg = focusedBackground;
            if( bg!=null && isOpaque() ){
                int w = getWidth();
                int h = getHeight();

                gs.setColor(bg);
                gs.fillRect(0, 0, w, h);
            }
        }
    }
}
