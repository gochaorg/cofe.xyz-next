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

package xyz.cofe.gui.swing.tree;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.ecolls.Pair;
import xyz.cofe.gui.swing.table.RowData;
import xyz.cofe.gui.swing.table.SortRowTM;

/**
 * Модель дерева-таблицы с поддержкой сортировки, в настоящий момент не используется
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TreeTableSortModel extends SortRowTM implements TreeTableModelInterface
{
    private static boolean eq( Object a, Object b ){
        if( a==null && b==null )return true;
        if( a==null && b!=null )return false;
        if( a!=null && b==null )return false;
        return a.equals(b);
    }

    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTableSortModel.class.getName());
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
        logger.entering(TreeTableSortModel.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TreeTableSortModel.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TreeTableSortModel.class.getName(), method, result);
    }
    //</editor-fold>

    public TreeTableSortModel( TreeTableModelInterface dm ){
        this( dm, null );
    }

    // FOR DEBUG
    public TreeTableSortModel( TreeTableModelInterface dm, final Consumer<Pair<RowData,RowData>> dumpDetail ){
        if (dm== null) {
            throw new IllegalArgumentException("dm==null");
        }

        // setRowComparator( (row1,row2) -> {
        setRowComparator( new Comparator<RowData>() {
            @Override
            public int compare(RowData row1, RowData row2) {

                int ri1 = row1.getRowIndex();
                int ri2 = row2.getRowIndex();

                Object onode1 = row1.getValue(0);
                Object onode2 = row2.getValue(0);

                if( onode1==null || onode2==null )return 0;

                TreeTableNode ttn1 = onode1 instanceof TreeTableNode ? ((TreeTableNode)onode1) : null;
                TreeTableNode ttn2 = onode2 instanceof TreeTableNode ? ((TreeTableNode)onode2) : null;

                if( ttn1==null || ttn2==null ){
                    if( dumpDetail!=null )dumpDetail.accept( Pair.of(row1, row2) );

                    throw new Error(
                        "sort fail"+
                            "\no1="+onode1+
                            "\no2="+onode2
                    );
                }

                int ro1 = ttn1.getRootOffset();
                int ro2 = ttn2.getRootOffset();
                int cmp = ro1==ro2 ? 0 : (ro1 < ro2 ? -1 : 1);

                return cmp;
            }} );

        setTableModel(dm);

        setSourceListen(true);
        applySort();
        fireAllChanged();
    }

    @Override
    public synchronized TreeTableNode getNodeOf( int row ){
        int directRow = mapRowToInside(row);
        if( directRow<0 )return null;

        return ((TreeTableModelInterface)getTableModel()).getNodeOf(directRow);
    }

    @Override
    public synchronized int getRowOf( TreeTableNode node ){
        if( node==null )return -1;

        int directrow = ((TreeTableModelInterface)getTableModel()).getRowOf(node);
        if( directrow<0 )return -1;

        int sortrow = mapRowToOutside(directrow);
        return sortrow;
    }

    @Override
    public boolean isRootVisible() {
        return ((TreeTableModelInterface)getTableModel()).isRootVisible();
    }

    @Override
    public TreeTableNode getRoot() {
        return ((TreeTableModelInterface)getTableModel()).getRoot();
    }

    @Override
    public void setRoot(TreeTableNode root) {
        ((TreeTableModelInterface)getTableModel()).setRoot(root);
    }
}
