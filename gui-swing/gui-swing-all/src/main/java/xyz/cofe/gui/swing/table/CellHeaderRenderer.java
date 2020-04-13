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

package xyz.cofe.gui.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.UIManager;

/**
 * Рендер ячейки заголовка таблицы
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class CellHeaderRenderer extends DefaultTableCellRenderer
    implements javax.swing.plaf.UIResource
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(CellHeaderRenderer.class.getName());
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
        logger.entering(CellHeaderRenderer.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(CellHeaderRenderer.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(CellHeaderRenderer.class.getName(), method, result);
    }
    //</editor-fold>

    private boolean horizontalTextPositionSet;
    private Icon sortArrow;
    private EmptyIcon emptyIcon = new EmptyIcon();
    private TableCellRenderer defaultRenderer;

    /**
     * Конструктор
     */
    public CellHeaderRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    /**
     * Конструктор
     * @param defaultRenderer рендер по умолчанию
     */
    public CellHeaderRenderer(TableCellRenderer defaultRenderer) {
        setHorizontalAlignment(JLabel.CENTER);
        this.defaultRenderer = defaultRenderer;
    }

    @Override
    public void setHorizontalTextPosition(int textPosition) {
        horizontalTextPositionSet = true;
        super.setHorizontalTextPosition(textPosition);
    }

    protected WeakReference<Component> rendererComponent;

    protected Component useDefaultCellRenderer(
        TableCellRenderer defrendrer,
        JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column
    ){
        Component cmpt = defrendrer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        rendererComponent = new WeakReference<>( cmpt );
        return cmpt;
    }

    @Override
    public synchronized Component getTableCellRendererComponent(JTable table, Object value,
                                                                boolean isSelected, boolean hasFocus, int row, int column
    ) {
        if( defaultRenderer!=null ){
            return useDefaultCellRenderer(defaultRenderer, table, value, isSelected, hasFocus, row, column);
        }
        return useInternalCellRenderer(table, value, isSelected, hasFocus, row, column);
    }

    protected Component useInternalCellRenderer(JTable table, Object value,
                                                boolean isSelected, boolean hasFocus, int row, int column
    ) {
        Icon sortIcon = null;

        boolean isPaintingForPrint = false;

        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                Color fgColor = null;
                Color bgColor = null;
                if (hasFocus) {
                    Color c1 = UIManager.getColor("TableHeader.foreground");
                    Color c2 = UIManager.getColor("Table.focusCellBackground");
                    fgColor = c1!=null ? c1 : c2;
                    //fgColor = DefaultLookup.getColor(this, ui, "TableHeader.focusCellForeground");

                    Color b1 = UIManager.getColor("TableHeader.background");
                    Color b2 = UIManager.getColor("Table.focusCellBackground");
                    bgColor = b1 != null ? b1 : b2;
                }
                if (fgColor == null) {
                    fgColor = header.getForeground();
                }
                if (bgColor == null) {
                    bgColor = header.getBackground();
                }
                setForeground(fgColor);
                setBackground(bgColor);

                setFont(header.getFont());

                isPaintingForPrint = header.isPaintingForPrint();
            }

            if (!isPaintingForPrint && table.getRowSorter() != null) {
                if (!horizontalTextPositionSet) {
                    // There is a row sorter, and the developer hasn't
                    // set a text position, change to leading.
                    setHorizontalTextPosition(JLabel.LEADING);
                }
                SortOrder sortOrder = getColumnSortOrder(table, column);
                if (sortOrder != null) {
                    switch(sortOrder) {
                        case ASCENDING:
                            sortIcon = UIManager.getIcon( "Table.ascendingSortIcon");
                            break;
                        case DESCENDING:
                            sortIcon = UIManager.getIcon( "Table.descendingSortIcon");
                            break;
                        case UNSORTED:
                            sortIcon = UIManager.getIcon( "Table.naturalSortIcon");
                            break;
                    }
                }
            }
        }

        setText(value == null ? "" : value.toString());
        setIcon(sortIcon);
        sortArrow = sortIcon;

        Border border = null;
        if (hasFocus) {
            border = UIManager.getBorder("Table.focusCellBorder");
        }
        if (border == null) {
            border = UIManager.getBorder("Table.cellBorder");
        }
        setBorder(border);

        return this;
    }

    public static SortOrder getColumnSortOrder(JTable table, int column) {
        SortOrder rv = null;
        if (table == null || table.getRowSorter() == null) {
            return rv;
        }
        java.util.List<? extends RowSorter.SortKey> sortKeys =
            table.getRowSorter().getSortKeys();
        if (sortKeys.size() > 0 && sortKeys.get(0).getColumn() ==
            table.convertColumnIndexToModel(column)) {
            rv = sortKeys.get(0).getSortOrder();
        }
        return rv;
    }

    @Override
    public void paintComponent(Graphics g) {
        boolean b = UIManager.getBoolean("TableHeader.rightAlignSortArrow");
        if (b && sortArrow != null) {
            //emptyIcon is used so that if the text in the header is right
            //aligned, or if the column is too narrow, then the text will
            //be sized appropriately to make room for the icon that is about
            //to be painted manually here.
            emptyIcon.width = sortArrow.getIconWidth();
            emptyIcon.height = sortArrow.getIconHeight();
            setIcon(emptyIcon);
            super.paintComponent(g);
            Point position = computeIconPosition(g);
            sortArrow.paintIcon(this, g, position.x, position.y);
        } else {
            super.paintComponent(g);
        }
    }

    private Point computeIconPosition(Graphics g) {
        FontMetrics fontMetrics = g.getFontMetrics();
        Rectangle viewR = new Rectangle();
        Rectangle textR = new Rectangle();
        Rectangle iconR = new Rectangle();
        Insets i = getInsets();
        viewR.x = i.left;
        viewR.y = i.top;
        viewR.width = getWidth() - (i.left + i.right);
        viewR.height = getHeight() - (i.top + i.bottom);
        SwingUtilities.layoutCompoundLabel(
            this,
            fontMetrics,
            getText(),
            sortArrow,
            getVerticalAlignment(),
            getHorizontalAlignment(),
            getVerticalTextPosition(),
            getHorizontalTextPosition(),
            viewR,
            iconR,
            textR,
            getIconTextGap());
        int x = getWidth() - i.right - sortArrow.getIconWidth();
        int y = iconR.y;
        return new Point(x, y);
    }

    private class EmptyIcon implements Icon, Serializable {
        int width = 0;
        int height = 0;
        public void paintIcon(Component c, Graphics g, int x, int y) {}
        public int getIconWidth() { return width; }
        public int getIconHeight() { return height; }
    }

    @Override
    public synchronized void setIconTextGap(int iconTextGap) {
        super.setIconTextGap(iconTextGap);
        Component cmpt = rendererComponent!=null ? rendererComponent.get() : null;
        if( cmpt instanceof JLabel ){
            ((JLabel)cmpt).setIconTextGap(iconTextGap);
        }
    }

    @Override
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        Component cmpt = rendererComponent!=null ? rendererComponent.get() : null;
        if( cmpt instanceof JLabel ){
            ((JLabel)cmpt).setIcon(icon);
        }
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        Component cmpt = rendererComponent!=null ? rendererComponent.get() : null;
        if( cmpt instanceof JLabel ){
            ((JLabel)cmpt).setText(text);
        }
    }
}
