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

package xyz.cofe.gui.swing.table;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import xyz.cofe.gui.swing.color.ColorModificator;

/**
 * Функция изменения высоты строки таблицы
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TableCellResizer {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TableCellResizer.class.getName());
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
        logger.entering(TableCellResizer.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TableCellResizer.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TableCellResizer.class.getName(), method, result);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="table : JTable">
    protected JTable table;

    /**
     * Указывает таблицу
     * @return таблица
     */
    public JTable getTable() { return table; }

    /**
     * Указывает таблицу
     * @param table таблица
     */
    public void setTable(JTable table) { this.table = table; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="captureHAlign : double">
    protected double captureHAlign = 0;

    /**
     * Выравнивание области захвата по горизонтали.
     * @return выравнивание: 0 - по левой стороне, 0.5 - по центру, 1 - по правой стороне.
     */
    public double getCaptureHAlign() { return captureHAlign; }

    /**
     * Выравнивание области захвата по горизонтали.
     * @param captureHAlign выравнивание: 0 - по левой стороне, 0.5 - по центру, 1 - по правой стороне.
     */
    public void setCaptureHAlign(double captureHAlign) { this.captureHAlign = captureHAlign; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="captureWidth : double">
    protected double captureWidth = 0;

    /**
     * Часть размера (константа) области захвата по горизонтали.
     *
     * <p>
     * Реальный размер = Константа + Относительный
     * @return часть размера по горизонтали
     */
    public double getCaptureWidth() { return captureWidth; }

    /**
     * Часть размера (константа) области захвата по горизонтали.
     *
     * <p>
     * Реальный размер = Константа + Относительный
     * @param captureWidth часть размера по горизонтали
     */
    public void setCaptureWidth(double captureWidth) { this.captureWidth = captureWidth; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="captureWidthRelative : double">
    protected double captureWidthRelative = 1;

    /**
     * Часть размера (относительная) области захвата по горизонтали.
     *
     * <p>
     * Реальный размер = Константа + Относительный
     * @return часть размера по горизонтали
     */
    public double getCaptureWidthRelative() { return captureWidthRelative; }

    /**
     * Часть размера (относительная) области захвата по горизонтали.
     *
     * <p>
     * Реальный размер = Константа + Относительный
     * @param captureWidthRelative часть размера по горизонтали
     */
    public void setCaptureWidthRelative(double captureWidthRelative) {
        this.captureWidthRelative = captureWidthRelative;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="captureVAlign : double">
    protected double captureVAlign = 1;
    /**
     * Выравнивание области захвата по вертикали
     * @return выравнивание 0 - верз, 1 - низ
     */
    public double getCaptureVAlign() { return captureVAlign; }

    /**
     * Выравнивание области захвата по вертикали
     * @param captureVAlign выравнивание 0 - верз, 1 - низ
     */
    public void setCaptureVAlign(double captureVAlign) { this.captureVAlign = captureVAlign; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="captureHeight : double">
    protected double captureHeight = 4;

    /**
     * Указывает высоту области захвата
     * @return высота (4 по умолчанию)
     */
    public double getCaptureHeight() { return captureHeight; }

    /**
     * Указывает высоту области захвата
     * @param captureHeight высота в пикселях
     */
    public void setCaptureHeight(double captureHeight) { this.captureHeight = captureHeight; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="captureHeightRelative : double">
    protected double captureHeightRelative = 0;
    /**
     * Указывает относительную высоту захвата
     * @return относительная высота захвата, 0 по умолчанию
     */
    public double getCaptureHeightRelative() { return captureHeightRelative; }

    /**
     * Указывает относительную высоту захвата
     * @param captureHeightRelative относительная высота захвата
     */
    public void setCaptureHeightRelative(double captureHeightRelative) {
        this.captureHeightRelative = captureHeightRelative;
    }
    //</editor-fold>

    /**
     * Возвращает расположение области захвата
     * @param row строка таблицы
     * @param col колонка таблицы
     * @return область захвата или null
     */
    public Rectangle2D cellCaptureZone( int row, int col ){
        JTable tbl = table;
        if( tbl==null )return null;

        Rectangle cellRect = tbl.getCellRect(row, col, true);
        if( cellRect==null )return null;
        //System.out.println("cell("+row+","+col+") rect="+cellRect);

        double capw = cellRect.getWidth()*captureWidthRelative+captureWidth;
        double caph = cellRect.getHeight()*captureHeightRelative+captureHeight;

        double wdiff = cellRect.getWidth()-capw;
        double hdiff = cellRect.getHeight()-caph;

        double capx = cellRect.getMinX()+wdiff*captureHAlign;
        double capy = cellRect.getMinY()+hdiff*captureVAlign;

        Rectangle2D crect = new Rectangle2D.Double(capx, capy, capw, caph);
        //System.out.println("cap rect="+crect);
        return crect;
    }

    /**
     * Возвращает расположение области захвата
     * @param x координаты мыши
     * @param y координаты мыши
     * @return область захвата или null
     */
    public Rectangle2D captureZone( int x, int y ){
        JTable tbl = table;
        if( tbl==null )return null;
        if( x<0 )return null;
        if( y<0 )return null;

        Point pt = new Point(x, y);
        int row = tbl.rowAtPoint(pt);
        if( row<0 )return null;

        int col = tbl.columnAtPoint(pt);
        if( col<0 )return null;

        return cellCaptureZone(row, col);
    }

    /**
     * Возвращает расположение области захвата
     * @param me координаты мыши
     * @return область захвата или null
     */
    public Rectangle2D captureZone( MouseEvent me ){
        return me==null ? null : captureZone(me.getX(), me.getY());
    }

    /**
     * Проверят что мышь находится в области захвата
     * @param x координаты мыши
     * @param y координаты мыши
     * @return true - в области захвата
     */
    public boolean isCaptureZone( int x, int y ){
        Rectangle2D crect = captureZone(x, y);
        return crect!=null ? crect.contains(x, y) : false;
    }

    /**
     * Проверят что мышь находится в области захвата
     * @param me координаты мыши
     * @return true - в области захвата
     */
    public boolean isCaptureZone( MouseEvent me ){
        return me==null ? null : isCaptureZone(me.getX(), me.getY());
    }

//    /**
//     * Отображение области захвата
//     * @param gs контекст отображения
//     * @param ev События мыши
//     */
//    public void paintCaptureZone( Graphics2D gs, MouseEvent ev ){
//        if( gs==null )return;
//
//        Rectangle2D rect = captureZone(ev);
//        if( rect==null )return;
//    }

    /**
     * Отображение области захвата
     * @param gs контекст отображения
     * @param rect координаты рамки/области захвата
     */
    public void paintCaptureZone( Graphics2D gs, Rectangle2D rect ){
        if( gs==null || rect==null )return;

        Paint savePaint = gs.getPaint();
        Shape saveClip = gs.getClip();
        Stroke saveStoke = gs.getStroke();

        Color lightColor = new ColorModificator().alpha(0.5f).apply(Color.white);
        Color bodyColor = new ColorModificator().alpha(0.5f).brighter(-0.25f).apply(lightColor);
        Color darkColor = new ColorModificator().alpha(0.5f).brighter(-0.50f).apply(lightColor);

//        gs.setPaint(bodyColor);
//        gs.fill(rect);

        gs.setPaint(bodyColor);
        gs.draw(rect);
        if( rect.getHeight()>2 && rect.getWidth()>2 ){
            gs.setStroke(new BasicStroke(1));
            gs.setClip(rect);
            Color c1 = new ColorModificator().alpha(0.5f).apply(lightColor);
            Color c2 = new ColorModificator().alpha(0.5f).apply(darkColor);
            int ci = -1;
            double mm = Math.max(rect.getWidth(), rect.getHeight());
            for( double x=rect.getMinX()-rect.getHeight(); x<rect.getMaxX(); x+=2 ){
                ci++;
                Color c = ci%2>0 ? c2 : c1;
                gs.setPaint(c);
                gs.drawLine(
                    (int)x, (int)rect.getMaxY()+1,
                    (int)x+(int)mm, (int)rect.getMaxY()+1-(int)mm
                );
            }
            gs.setClip(saveClip);
        }

        gs.setStroke(new BasicStroke(1));

        Path2D pLight = new Path2D.Double();
        pLight.moveTo(rect.getMinX(), rect.getMaxY());
        pLight.lineTo(rect.getMinX(), rect.getMinY());
        pLight.lineTo(rect.getMaxX(), rect.getMinY());
        gs.setPaint(lightColor);
        gs.draw(pLight);

        Path2D pDark = new Path2D.Double();
        pDark.moveTo(rect.getMaxX(), rect.getMinY());
        pDark.lineTo(rect.getMaxX(), rect.getMaxY());
        pDark.lineTo(rect.getMinX(), rect.getMaxY());
        gs.setPaint(darkColor);
        gs.draw(pDark);

        gs.setClip(saveClip);
        gs.setPaint(savePaint);
        gs.setStroke(saveStoke);
    }

    protected int xStarted;
    protected int yStarted;
    protected int rowStarted = -1;
    protected int colStarted = -1;
    protected int rowHeightStarted;
    protected int colWidthStarted;

    //<editor-fold defaultstate="collapsed" desc="resizeHeight : boolean">
//    protected boolean resizeHeight;
//    public boolean isResizeHeight() { return resizeHeight; }
//    public void setResizeHeight(boolean resizeHeight) { this.resizeHeight = resizeHeight; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="resizeWidth : boolean">
//    protected boolean resizeWidth;
//    public boolean isResizeWidth() { return resizeWidth; }
//    public void setResizeWidth(boolean resizeWidth) { this.resizeWidth = resizeWidth; }
    //</editor-fold>

    /**
     * Начинает изменения высоты строки
     * @param me координаты мыши
     */
    public void start( MouseEvent me ){
        if( me==null )return;

        JTable tbl = table;
        if( tbl==null )return;

        rowStarted = tbl.rowAtPoint(me.getPoint());
        colStarted = tbl.columnAtPoint(me.getPoint());
        if( rowStarted<0 || colStarted<0 )return;

        xStarted = me.getX();
        yStarted = me.getY();

        rowHeightStarted = tbl.getRowHeight(rowStarted);
        colWidthStarted = tbl.getColumnModel().getColumn(colStarted).getWidth();
    }

    /**
     * Завершает изменения высоты строки
     */
    public void stop(){
        rowStarted = -1;
        colStarted = -1;
    }

    /**
     * Указывает нача-то ли изменение высоты строки
     * @return true - начатато
     */
    public boolean isStarted(){ return rowStarted>=0 && colStarted>=0; }

    /**
     * Принимает событие перемещения мыши для изменения строки
     * @param me координаты мыши
     */
    public void drag( MouseEvent me ){
        if( me==null )return;

        JTable tbl = table;
        if( tbl==null )return;
        if( rowStarted<0 )return;

        int ydiff = me.getY() - yStarted;
        int xdiff = me.getX() - xStarted;

        int trgtHeight = rowHeightStarted+ydiff;
        if( rowStarted>=0 && trgtHeight>=1 ){
            tbl.setRowHeight(rowStarted, trgtHeight);
        }
    }
}
