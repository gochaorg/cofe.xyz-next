package xyz.cofe.gui.swing.tree;

import xyz.cofe.gui.swing.table.Table;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Сколлинг внутри колонки
 * @author user
 */
public class ColumnScroll {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ColumnScroll.class.getName());
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
        logger.entering(ColumnScroll.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(ColumnScroll.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(ColumnScroll.class.getName(), method, result);
    }
    //</editor-fold>

    private Double nodeRenderMinX = null;

    /**
     * Минимальная координата контента ячейки
     * @return минимальная x контета
     */
    public Double getNodeRenderMinX() { return nodeRenderMinX; }

    /**
     * Минимальная координата контента ячейки
     * @param nodeRenderMinX минимальная x контета
     */
    public void setNodeRenderMinX(Double nodeRenderMinX) { this.nodeRenderMinX = nodeRenderMinX; }

    private Double nodeRenderMaxX = null;

    /**
     * Максимальная координата контента ячейки
     * @return максимальная x контета
     */
    public Double getNodeRenderMaxX() { return nodeRenderMaxX; }

    /**
     * Максимальная координата контента ячейки
     * @param nodeRenderMaxX максимальная x контета
     */
    public void setNodeRenderMaxX(Double nodeRenderMaxX) { this.nodeRenderMaxX = nodeRenderMaxX; }

    /**
     * Обновление минимальной/максимальной координат
     * @param rect координаты текущей ячейки
     */
    public void updateNodeRenderBounds( Rectangle2D rect){
        nodeRenderMinX =
            nodeRenderMinX==null ?
                nodeRenderMinX = rect.getMinX() :
                Math.min(nodeRenderMinX,rect.getMinX());

        nodeRenderMaxX =
            nodeRenderMaxX==null ?
                nodeRenderMaxX = rect.getMaxX() :
                Math.max(nodeRenderMaxX,rect.getMaxX());
    }

    /**
     * Сброс минимальной и максимальной координаты
     */
    public synchronized void resetNodeRenderBounds(){
        nodeRenderMaxX = null;
        nodeRenderMinX = null;
    }

    protected double scrollWidth = 0;

    /**
     * Ширина скроллируемого контента
     * @return ширина скроллинга
     */
    public double getScrollWidth() {
        return scrollWidth;
    }

    /**
     * Ширина скроллируемого контента
     * @param scrollWidth ширина скроллинга
     */
    public void setScrollWidth(double scrollWidth) {
        this.scrollWidth = scrollWidth;
    }

    private double scrollX = 0;
    /**
     * Возвращает величину скроллинга по горизонтали
     * @return величина скроллинга
     */
    public double getScrollX(){
        return scrollX;
    }
    /**
     * Устанавливает величину скроллинга по горизонтали
     * @param x величина скроллинга
     */
    public void setScrollX(double x){
        this.scrollX = x;
        //getNodeRender().getTreeNodeCellRender().setScrollX(x);
    }

    protected boolean scrollerVisible;
    /**
     * Отображать скроллинг
     * @return true - отображает скроллинг
     */
    public boolean isScrollerVisible() { return scrollerVisible; }
    /**
     * Отображать скроллинг
     * @param scrollerVisible true - отображает скроллинг
     */
    public void setScrollerVisible(boolean scrollerVisible) { this.scrollerVisible = scrollerVisible; }

    /**
     * Пересчет scrollWidth
     * @param columnWidth ширина колонки
     */
    public void recalcScrollWidth(double columnWidth){
        if( nodeRenderMaxX==null || nodeRenderMinX==null )return;

        //double nodesWidthMax = Math.abs(nodeRenderMaxX - nodeRenderMinX);
        double nodesWidthMax = nodeRenderMaxX+scrollX;
        double colWidth = columnWidth;

        if( colWidth<nodesWidthMax && colWidth>0 ){
            if( getScrollWidth() < nodesWidthMax ){
                setScrollWidth(nodesWidthMax);
            }
            setScrollerVisible(true);
        }else if( getScrollX()<=0 ){
            setScrollWidth(0);
            setScrollerVisible(false);
        }
    }

    protected double scrollerHeight = 20;

    /**
     * Возвращает высоту поллосы скроллинга
     * @return высота скроллинга
     */
    public double getScrollerHeight() {
        return scrollerHeight;
    }

    /**
     * Указывает высоту поллосы скроллинга
     * @param scrollerHeight высота скроллинга
     */
    public void setScrollerHeight(double scrollerHeight) {
        this.scrollerHeight = scrollerHeight;
    }

    /**
     * Границы колонки
     * @param table таблица
     * @param column колонка
     * @return границы или null
     */
    public static double[] getColumnWidthBounds( JTable table, int column){
        if( table==null )return null;
        if( column>=table.getColumnCount() || column<0 )return null;

        double x0 = 0;
        double x1 = 0;
        for( int ci=0; ci<=column; ci++ ){
            if( ci==column ){
                int cw = table.getColumnModel().getColumn(ci).getWidth();
                int mrg = table.getColumnModel().getColumnMargin();
                if( ci==0 ){
                    x0 = 0;
                    x1 = cw;
                    x1 += mrg;
                }else{
                    double xx1 = x1;
                    x1 += cw;
                    x1 += mrg;
                    x0 = xx1;
                }
            }else{
                int cw = table.getColumnModel().getColumn(ci).getWidth();
                int mrg = table.getColumnModel().getColumnMargin();
                if( ci==0 ){
                    x0 = 0;
                    x1 = cw;
                    x1 += mrg;
                }else{
                    double xx1 = x1;
                    x1 += cw;
                    x1 += mrg;
                    x0 = xx1;
                }
            }
        }

        return new double[]{ x0, x1 };
    }

    /**
     * Возвращает рамку в которой происходит скроллинг
     * @param table таблица
     * @param column колонка
     * @return рамка скроллинга или null
     */
    public Rectangle2D getScrollerRect( Table table, int column){
        if( table==null )return null;
        if( column>=table.getColumnCount() || column<0 )return null;

        double[] bounds = getColumnWidthBounds(table, column);
        if( bounds==null )return null;

        Rectangle r = table.getVisibleRect();
        return new Rectangle2D.Double(bounds[0], r.getMaxY()-scrollerHeight, bounds[1] - bounds[0], scrollerHeight);
    }

    /**
     * Возвращает рамку в которой происходит скроллинг
     * @param table таблица
     * @param column колонка
     * @return рамка скроллинга или null
     */
    public Rectangle2D getScrollerDragRect(Table table, int column){
        if( !scrollerVisible )return null;

        Rectangle2D srect = getScrollerRect(table, column);
        if( srect==null )return null;

        if( scrollWidth<=0 )return null;
        double colw = srect.getWidth();

        double k = colw / scrollWidth;
        double x1 = scrollX*k;

        return new Rectangle2D.Double(
            x1,
            srect.getMinY(),

            colw*k,
            srect.getHeight()
        );
    }

    /**
     * Отображает скроллинг
     * @param gs интф рендера
     * @param table таблица
     * @param column колонка
     */
    public void paintScroller(Graphics2D gs, Table table, int column){
        if( gs==null )return;
        if( table==null )return;

        if( isScrollerVisible() && getScrollWidth()>0 ){
            Rectangle2D scrollRect = getScrollerRect(table, column);
            if( scrollRect==null )return;

            Rectangle2D dragRect = getScrollerDragRect(table, column);
            if( dragRect==null )return;

            Composite cAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
            gs.setComposite(cAlpha);

            gs.setPaint(Color.lightGray);
            gs.fill(scrollRect);

            gs.setPaint(Color.gray);
            gs.fill(dragRect);

            gs.setComposite(AlphaComposite.Clear);
        }
    }
}
