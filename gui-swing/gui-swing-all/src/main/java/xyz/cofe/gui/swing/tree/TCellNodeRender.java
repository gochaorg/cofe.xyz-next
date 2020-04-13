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


import java.awt.Component;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import xyz.cofe.gui.swing.cell.CellContext;
import xyz.cofe.gui.swing.cell.CellRender;
import xyz.cofe.gui.swing.cell.TCRenderer;
import xyz.cofe.gui.swing.cell.TableCellContext;
import xyz.cofe.gui.swing.cell.TreeNodeCellRender;
import xyz.cofe.gui.swing.cell.TreeNodeContext;

/**
 * Рендер ячейки TableCell с поддержкой TreeTable
 * @author user
 */
public class TCellNodeRender extends TCRenderer {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TCellNodeRender.class.getName());
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
        logger.entering(TCellNodeRender.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TCellNodeRender.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TCellNodeRender.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор
     */
    public TCellNodeRender(){
        this.cellContext = createTreeNodeContext();
        this.labelRender = createTreeNodeCellRender();
        labelRender.getFormat().setFont(getFont());
    }

    /**
     * Конструктор
     * @param cellRender Рендер ячейки
     * @param ctx Контекст
     */
    public TCellNodeRender(TreeNodeCellRender cellRender, TreeNodeContext ctx){
        this.cellContext = ctx!=null ? ctx : createTreeNodeContext();
        this.labelRender = cellRender!=null ? cellRender : createTreeNodeCellRender();
    }

    //<editor-fold defaultstate="collapsed" desc="columnScrollModel : ColumnScrollModel">
    protected ColumnScrollModel columnScrollModel;
    /**
     * Возвращает модель горизонтального скроллирования колонки
     * @return модель сколлинга
     */
    public ColumnScrollModel getColumnScrollModel() { return columnScrollModel; }

    /**
     * Указывает модель горизонтального скроллирования колонки
     * @param columnScrollModel модель сколлинга
     */
    public void setColumnScrollModel(ColumnScrollModel columnScrollModel) { this.columnScrollModel = columnScrollModel; }
    //</editor-fold>

    protected TreeNodeCellRender createTreeNodeCellRender(){
        return new TreeNodeCellRender(){
            @Override
            protected void onRenderedContentBounds(Rectangle2D rect, CellContext cctx) {
                ColumnScrollModel csm = TCellNodeRender.this.getColumnScrollModel();

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
        };
    }

    /**
     * Создает контекст для отображения ячеек таблицы
     * @return контекст отображения
     * @see CellContext
     * @see CellRender
     */
    protected TreeNodeContext createTreeNodeContext(){
        return new TreeNodeContext();
    }

    /**
     * Возвращает рендер ячейки
     * @return рендрен ячеки
     */
    public synchronized TreeNodeCellRender getTreeNodeCellRender(){
        if( labelRender instanceof TreeNodeCellRender )return (TreeNodeCellRender)labelRender;
        this.labelRender = createTreeNodeCellRender();
        return (TreeNodeCellRender)labelRender;
    }

    /**
     * Указывает рендер ячейки
     * @return рендрен ячейки
     */
    @Override
    public synchronized TreeNodeContext getCellContext() {
        if( cellContext instanceof TreeNodeContext )return (TreeNodeContext)cellContext;
        cellContext = createTreeNodeContext();
        return (TreeNodeContext)cellContext;
    }

    /**
     * Подготавливает рендер для отображения табличных/дреовидных данных
     * @param table таблица
     * @param value занчение (TreeTableNode)
     * @param isSelected ячейка выбрана ползователем
     * @param hasFocus ячейка содержит фокус
     * @param row строка таблицы
     * @param column колонка таблицы
     * @return self ссылка
     * @see TreeTableNode
     * @see TreeTableNodeBasic
     */
    @Override
    public synchronized Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        ColumnScrollModel csm = getColumnScrollModel();
        if( csm!=null ){
            ColumnScroll cs = getColumnScrollModel().getColumnScroll(column);
            if( cs!=null ){
                double scrl = cs.getScrollX();
                getTreeNodeCellRender().setScrollX(scrl);
                //logInfo("set scroll {0}", scrl);
            }else{
                getTreeNodeCellRender().setScrollX(0);
                //logInfo("set scroll {0}", 0);
            }
        }

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if( value instanceof TreeTableNode ){
            TreeTableNode node = (TreeTableNode)value;
            getCellContext().setNode(node);
            getCellContext().setValue(node.getData());
        }

        return this;
    }
}
