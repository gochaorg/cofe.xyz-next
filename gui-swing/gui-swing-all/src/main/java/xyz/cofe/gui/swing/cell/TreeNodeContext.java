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


import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import xyz.cofe.gui.swing.tree.TreeTableModel;
import xyz.cofe.gui.swing.tree.TreeTableNode;

/**
 * Контекст для отображения узла дерева
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TreeNodeContext extends TableCellContext<TreeNodeContext> {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeNodeContext.class.getName());
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
        logger.entering(TreeNodeContext.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TreeNodeContext.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TreeNodeContext.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор по умолчанию
     */
    public TreeNodeContext(){
    }

    /**
     * Конструктор копироавния
     * @param sample образец для копирования
     */
    public TreeNodeContext(TreeNodeContext sample){
        super(sample);
        if(sample!=null){
            this.node = sample.node;
        }
    }

    @Override
    public TreeNodeContext clone(){
        return new TreeNodeContext(this);
    }

    protected TreeTableNode node;
    /**
     * Указывает узел соответ значению
     * @return узел дерева
     */
    public TreeTableNode getNode() { return node; }

    /**
     * Указывает узел соответ значению
     * @param node узел дерева
     */
    public void setNode(TreeTableNode node) { this.node = node; }

    /**
     * Указывает виден ли корень дерева
     * @return true - корень дерева отображается
     */
    public boolean isRootVisible(){
        TreeTableModel ttm = getTreeTableModel();
        return ttm != null ? ttm.getDirectModel().isRootVisible() : true;
    }

    /**
     * Указывает модель дерева
     * @return модель дерева или null
     */
    public TreeTableModel getTreeTableModel(){
        JTable tbl = super.getTable();
        TreeTableModel ttm =
            tbl!=null && tbl.getModel()!=null ?
                (tbl.getModel() instanceof TreeTableModel) ?
                    ((TreeTableModel)tbl.getModel()) : null : null;
        return ttm;
    }

    /**
     * Указывает уровень узла дерева с учетом видимости корня дерева
     * @return уровень узла дерева (от нуля)
     */
    public int getVisibleTreeLevel(){
        TreeTableNode n = node;
        if( n==null )return 0;

        boolean rv = isRootVisible();
        int lvl = n.level();
        lvl--;
        lvl = lvl + (rv ? 0 : -1);
        //int lvl = n.getTreeLevel();

        return lvl;
    }
}
