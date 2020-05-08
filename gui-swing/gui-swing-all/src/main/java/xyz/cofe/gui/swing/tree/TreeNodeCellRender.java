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

package xyz.cofe.gui.swing.tree;


import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import xyz.cofe.fn.Fn1;
import xyz.cofe.gui.swing.cell.CellContext;
import xyz.cofe.gui.swing.cell.CellFormat;
import xyz.cofe.gui.swing.cell.DefaultLabelRender;
import xyz.cofe.gui.swing.cell.TableCellContext;
import xyz.cofe.j2d.RectangleFn;

/**
 * Рендер ячейки для отображения узла дерева
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TreeNodeCellRender extends DefaultLabelRender {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeNodeCellRender.class.getName());
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
        logger.entering(TreeNodeCellRender.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TreeNodeCellRender.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TreeNodeCellRender.class.getName(), method, result);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="minus plus icons default">
    protected static Icon minusIconDefault;
    protected static Icon plusIconDefault;
    protected static Icon leafIconDefault;

    static {
        URL uMinus = TreeNodeCellRender.class.getResource("/xyz/cofe/gui/swing/table/node-minus-v1-12x12.png");
        minusIconDefault = new ImageIcon(uMinus);

        URL uPlus = TreeNodeCellRender.class.getResource("/xyz/cofe/gui/swing/table/node-plus-v1-12x12.png");
        plusIconDefault = new ImageIcon(uPlus);

        URL uLeaf = TreeNodeCellRender.class.getResource("/xyz/cofe/gui/swing/table/node-leaf-v1-6x6.png");
        leafIconDefault = new ImageIcon(uLeaf);
    }
    //</editor-fold>

    /**
     * Конструктор
     */
    public TreeNodeCellRender(){
        this((CellFormat)null);
    }

    /**
     * Конструктор
     * @param cellFormat формат ячейки
     */
    public TreeNodeCellRender(CellFormat cellFormat){
        super(cellFormat);
        minusIcon = minusIconDefault;
        plusIcon = plusIconDefault;
        leafIcon = leafIconDefault;
        double icow = 0;
        for( Icon ico : new Icon[]{minusIcon, plusIcon, leafIcon} ){
            if(ico==null)continue;
            icow = Math.max( icow, (double)ico.getIconWidth() );
        }
        prefferedIconWidth = icow;
    }

    /**
     * Конструктор
     * @param cellFormat формат ячейки
     * @param csm информауия/модель о скроллировании данных в колонке
     */
    public TreeNodeCellRender(CellFormat cellFormat, ColumnScrollModel csm){
        super(cellFormat);
        columnScrollModel = csm;
        minusIcon = minusIconDefault;
        plusIcon = plusIconDefault;
        leafIcon = leafIconDefault;
        double icow = 0;
        for( Icon ico : new Icon[]{minusIcon, plusIcon, leafIcon} ){
            if(ico==null)continue;
            icow = Math.max( icow, (double)ico.getIconWidth() );
        }
        prefferedIconWidth = icow;
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public TreeNodeCellRender(TreeNodeCellRender sample){
        super(sample);
        if( sample!=null ){
            minusIcon = sample.minusIcon;
            plusIcon = sample.plusIcon;
            leafIcon = sample.leafIcon;
            prefferedIconWidth = sample.prefferedIconWidth;
            oneLevelOffset = sample.oneLevelOffset;
            columnScrollModel = sample.columnScrollModel;
        }
    }

    @Override
    public synchronized TreeNodeCellRender clone() {
        return new TreeNodeCellRender(this);
    }

    //<editor-fold defaultstate="collapsed" desc="treeIconContext : CellContext">
    protected CellContext treeIconContext;
    /**
     * Возвращает контекст для отображения иконки
     * @return контекст иконки
     */
    public CellContext getTreeIconContext(){
        return treeIconContext;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="minusIcon : Icon">
    protected Icon minusIcon;
    /**
     * Возвращает иконку для отображения развернутого узла
     * @return иконка "минус"
     */
    public Icon getMinusIcon() { return minusIcon; }
    /**
     * Указывает иконку для отображения развернутого узла
     * @param minusIcon иконка "минус"
     */
    public void setMinusIcon(Icon minusIcon) { this.minusIcon = minusIcon; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="plusIcon : Icon">
    protected Icon plusIcon;
    /**
     * Возвращает иконку для отображения свернутого узла
     * @return иконка "плюс"
     */
    public Icon getPlusIcon() { return plusIcon; }
    /**
     * Указывает иконку для отображения свернутого узла
     * @param plusIcon иконка "плюс"
     */
    public void setPlusIcon(Icon plusIcon) { this.plusIcon = plusIcon; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="leafIcon : Icon">
    protected Icon leafIcon;
    /**
     * Возвращает иконку для отображения листа (конечнго узла) дерева
     * @return иконка "листа"
     */
    public Icon getLeafIcon() { return leafIcon; }
    /**
     * Указывает иконку для отображения листа (конечнго узла) дерева
     * @param plusIcon иконка "листа"
     */
    public void setLeafIcon(Icon plusIcon) { this.leafIcon = plusIcon; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="prefferedIconWidth : double">
    protected double prefferedIconWidth;
    /**
     * Возвращает предпочитаемую ширину иконок
     * @return ширина иконок (предпочитаемая)
     */
    public double getPrefferedIconWidth() { return prefferedIconWidth; }
    /**
     * Указывает предпочитаемую ширину иконок
     * @param prefferedIconWidth ширина иконок (предпочитаемая)
     */
    public void setPrefferedIconWidth(double prefferedIconWidth) { this.prefferedIconWidth = prefferedIconWidth; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="oneLevelOffset : double">
    protected double oneLevelOffset = 16;
    /**
     * Возвращает кол-во пикселей для отступа на один уровенеь
     * @return отступ на один уровенеь
     */
    public double getOneLevelOffset() { return oneLevelOffset; }
    /**
     * Указывает кол-во пикселей для отступа на один уровенеь
     * @param oneLevelOffset отступ на один уровенеь
     */
    public void setOneLevelOffset(double oneLevelOffset) { this.oneLevelOffset = oneLevelOffset; }
    //</editor-fold>
    protected double treeIconPadLeft = 1.0;
    protected double treeIconPadRight = 3.0;
    protected double treeIconPadTop = 3.0;
    protected double treeIconPadBottom = 0.0;

    //<editor-fold defaultstate="collapsed" desc="scrollX : double">
    private double scrollX = 0.0;
    public double getScrollX() { return scrollX; }
    public void setScrollX(double scrollX) { this.scrollX = scrollX; }
    //</editor-fold>

    protected Icon preparedTreeIcon;

    protected void prepareTreeTableNode(Graphics2D gs, TreeNodeContext ctx, CellFormat cf, TreeTableNode node){
        // Вычисление смещения относительно уровня вложенности
        int lvl = ctx.getVisibleTreeLevel();
        double lvlStepOffset = oneLevelOffset;
        double lvlOffset = lvlStepOffset * lvl;

        if( getIcoContext()!=null ){
            getIcoContext().move(lvlOffset, 0);
            getIcoContext().move(-scrollX, 0);
        }

        if( getTextContext()!=null ){
            getTextContext().move(lvlOffset, 0);
            getTextContext().move(-scrollX, 0);
        }

        if( node instanceof TreeTableNodeGetText ){
            String text = ((TreeTableNodeGetText)node).treeTableNodeGetText();
            if( text!=null ){
                //ctx = ctx.value(text);
                getTextContext().setValue(text);
            }else{
                Object data = node.getData();
                if( data!=null ){
                    getTextContext().setValue(data);
                }else{
                    getTextContext().setValue("");
                    getIcoContext().setValue(nullIcon);
                }
            }
        }

        // Иконка узла древа
        Icon treeIco = null;

        boolean hasChildren = false;

        if( node.count()>0 ){
            hasChildren = true;
        }else if( node instanceof TreeTableNodeBasic ){
            TreeTableNodeBasic tnode = (TreeTableNodeBasic)node;
            if( tnode instanceof TreeTableNodeExpander ){
                hasChildren = true;
            }else{
                Fn1<Object,Boolean> extractable = tnode.getPreferredDataFollowable();
                Object data = tnode.getData();
                if( extractable!=null ){
                    boolean canExtract = extractable.apply(data);
                    if( canExtract )hasChildren = true;
                }else{
                    Object extracter = tnode.getPreferredDataFollower();
                    Object extractFinished = tnode.getFollowFinished();
                    if( extracter!=null && extractFinished==null ){
                        hasChildren = true;
                    }
                }
            }
        }

        if( hasChildren ){
            if( node.isExpanded() ){
                treeIco = getMinusIcon();
            }else{
                treeIco = getPlusIcon();
            }
        }else{
            //treeIco = getLeafIcon();
        }

        if( getBorderContext()!=null /*&& treeIco!=null*/ ){
            treeIconContext = getBorderContext().clone();
            //treeIconContext.size(treeIco.getIconWidth(), treeIco.getIconHeight());
            treeIconContext.size(
                getPrefferedIconWidth() + treeIconPadLeft + treeIconPadRight
                , treeIco != null
                    ? treeIco.getIconHeight() + treeIconPadTop + treeIconPadBottom
                    : getPrefferedIconWidth() + treeIconPadTop + treeIconPadBottom
            );
            treeIconContext.move(lvlOffset, 0);
            treeIconContext.move(-scrollX, 0);

            preparedTreeIcon = treeIco;

            if( getIcoContext()!=null ){
                getIcoContext().move(treeIconContext.getBounds().getWidth(), 0);
            }

            if( getTextContext()!=null ){
                getTextContext().move(treeIconContext.getBounds().getWidth(), 0);
            }
        }
    }

    @Override
    public synchronized boolean prepare(Graphics2D gs, CellContext context, CellFormat cf) {
        // Подготовка рендера узла дерева
        // Иконка узла дерева
        preparedTreeIcon = null;

        // Расположение соответ иконки
        treeIconContext = null;

        TreeTableNodeFormat fmt = null;
        if( context instanceof TreeNodeContext ){
            TreeNodeContext ctx = (TreeNodeContext)context;
            TreeTableNode node = ctx.getNode();
            if( node instanceof TreeTableNodeGetFormat ){
                fmt = ((TreeTableNodeGetFormat)node).getTreeTableNodeFormat();
            }// else if( node instanceof TreeTableNodeGetFormatOf)
        }

        if( fmt!=null ){
            cf = cf!=null ? cf.clone() : getFormat().clone();
            if( fmt.getIcons().size()>0 ){
                cf.icon(fmt.getIcons().get(0));
                cf.iconPadRight(3.0);
            }

            //if( fmt.getIconWidthMin()!=null ){
            //    cf.iconPlaceholder(fmt.getIconWidthMin(), fmt.getIconWidthMin());
            //}

            if( fmt.getFontFamily()!=null ){
                if( fmt.getFontSize()!=null ){
                    cf.font(fmt.getFontFamily(), fmt.getFontSize(), false, false);
                }else{
                    cf.font(fmt.getFontFamily(), 11, false, false);
                }
            }
            if( fmt.getBold()!=null ){ cf.bold(fmt.getBold()); }
            if( fmt.getItalic()!=null ) cf.italic(fmt.getItalic());
            if( fmt.getBackground()!=null ) cf.backgroundColor(fmt.getBackground());
            if( fmt.getForeground()!=null ) cf.color(fmt.getForeground());
        }

        boolean res = super.prepare(gs, context, cf);

        if( context instanceof TreeNodeContext ){
            TreeNodeContext ctx = (TreeNodeContext)context;
            TreeTableNode node = ctx.getNode();
            if( node!=null ){
                prepareTreeTableNode(gs, ctx, cf, node);
            }
        }

        return res;
    }

    @Override
    public synchronized void cellRender(Graphics2D gs, CellContext context) {
        if( !prepare(gs, context, getFormat().clone()) )return;

        //////////////////////////////// рендер ////////////////////////////
        // рендер фона
        backgroundRender(gs);

        // рендер текста
        textRender(gs);

        // рендер иконки
        imageRender(gs);

        // рендер иконки дерева
        if( gs!=null && context!=null ){
            Rectangle2D treeIcoRect = null;

            if( treeIconContext!=null && preparedTreeIcon!=null ){
                treeIcoRect = treeIconContext.getBounds();
                preparedTreeIcon.paintIcon(null, gs,
                    (int)(treeIcoRect.getMinX()+treeIconPadLeft),
                    (int)(treeIcoRect.getMinY()+treeIconPadTop)
                );
            }

            Rectangle2D textRect = null;
            if( isTextVisible() && getTextContext()!=null ){
                //Rectangle2D curTextRect = getTextRender()
                textRect = getTextRender().cellRectangle(gs, getTextContext());
            }

            Rectangle2D imgRect = null;
            if( isImageVisible() && getIcoContext()!=null ){
                imgRect = getImageRender().cellRectangle(gs, getIcoContext() );
            }

            Rectangle2D rect = RectangleFn.union(textRect, imgRect, treeIcoRect);
            if( rect!=null ){
                onRenderedContentBounds(rect, context);
            }
        }

        // рендер рамки
        borderRender(gs);
    }

    @Override
    public synchronized Rectangle2D cellRectangle(Graphics2D gs, CellContext context) {
        Rectangle2D srect = super.cellRectangle(gs, context);
        if( srect==null )return null;
        if( treeIconContext==null )return srect;
        return RectangleFn.union(srect, treeIconContext.getBounds());
    }

    protected ColumnScrollModel columnScrollModel;

    /**
     * Возвращает модель скролирования колонок
     * @return модель скролирования колонок
     */
    public ColumnScrollModel getColumnScrollModel() {
        return columnScrollModel;
    }

    /**
     * Указывает модель скролирования колонок
     * @param columnScrollModel модель скролирования колонок
     */
    public void setColumnScrollModel(ColumnScrollModel columnScrollModel) {
        this.columnScrollModel = columnScrollModel;
    }

    /**
     * Вызывается при отображении значения, обновляет модель сролирования колонок
     * @param rect граница отображаемого контента
     * @param cctx контекст отображения
     * @see #getColumnScrollModel()
     */
    protected void onRenderedContentBounds(Rectangle2D rect, CellContext cctx){
        ColumnScrollModel csm = getColumnScrollModel();

        if( cctx instanceof TableCellContext && rect!=null && csm!=null ){
            TableCellContext tctx = (TableCellContext)cctx;
            int col = tctx.getColumn();
            if( col>=0 ){
                ColumnScroll cscrl = csm.getColumnScroll(col);
                if( cscrl!=null ){
                    cscrl.updateNodeRenderBounds(rect);
                }
            }
        }
    }
}
