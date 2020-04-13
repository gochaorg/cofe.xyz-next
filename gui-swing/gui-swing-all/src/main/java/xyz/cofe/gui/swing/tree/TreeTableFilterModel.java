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

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import xyz.cofe.ecolls.Closeables;
import xyz.cofe.gui.swing.table.FilterRowTM;
import xyz.cofe.gui.swing.table.RowData;
import xyz.cofe.iter.TreeStep;

/**
 * Модель таблицы-дерева с поддержкой фильтрации узлов
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TreeTableFilterModel extends FilterRowTM implements TreeTableModelInterface
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTableFilterModel.class.getName());
    private static final Level logLevel(){ return Logger.getLogger(TreeTableFilterModel.class.getName()).getLevel(); }

    private static final boolean isLogSevere(){
        Level logLevel = logLevel();
        return logLevel==null
            ? true
            : logLevel.intValue() <= Level.SEVERE.intValue();
    }

    private static final boolean isLogWarning(){
        Level logLevel = logLevel();
        return logLevel==null
            ? true
            : logLevel.intValue() <= Level.WARNING.intValue(); }

    private static final boolean isLogInfo(){
        Level logLevel = logLevel();
        return logLevel==null
            ? true
            : logLevel.intValue() <= Level.INFO.intValue(); }

    private static final boolean isLogFine(){
        Level logLevel = logLevel();
        return logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINE.intValue(); }

    private static final boolean isLogFiner(){
        Level logLevel = logLevel();
        return logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINER.intValue(); }

    private static final boolean isLogFinest(){
        Level logLevel = logLevel();
        return logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINEST.intValue(); }

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
        logger.entering(TreeTableFilterModel.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TreeTableFilterModel.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TreeTableFilterModel.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор
     * @param dm нтерфейс доступа к таблице-дереву
     */
    public TreeTableFilterModel( TreeTableModelInterface dm ){
        if (dm== null) {
            throw new IllegalArgumentException("dm==null");
        }

        setRowFilter( filter );

        setSourceListen(true);
        setTableModel(dm);

        TreeTableNode root = dm.getRoot();
        if( root!=null ){
            setRoot(root);
        }
    }

    protected final Predicate<RowData> filter = rowdata -> {
            Object onode = rowdata.getValue(0);
            if( onode instanceof TreeTableNode ){
                TreeTableNode node = (TreeTableNode)onode;
                boolean visible = isVisible(node);

//                if( isLo
                logFinest("filter.validate visible={0} ro={1} data={2}",
                    visible, node.getRootOffset(), node.getData());

                return visible;
            }
            return true;
        };

    /**
     * Возвращает виден-ли узел
     * @param node узел
     * @return true - узел должен быть виден
     */
    public synchronized boolean isVisible( TreeTableNode node ){
        if( node==null )return true;

        List npath = node.path();
        if( npath==null )return true;

        int level = npath.size();
        if( level==0 )return true;

        // root node
        if( level==1 )return true;

        // sub root
        if( !isRootVisible() && level==2 )return true;

        npath.remove(npath.size()-1);

        int ito = isRootVisible() ? 0 : 1;
        for( int i=npath.size()-1; i>=ito; i-- ){
            Object o_n = npath.get(i);
            if( o_n instanceof TreeTableNode ){
                TreeTableNode n = (TreeTableNode)o_n;
                boolean exp = n.isExpanded();
                if( !exp ){
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public synchronized TreeTableNode getNodeOf( int row ){
        int directRow = mapRowToInside(row);
        if( directRow<0 )return null;

        return ((TreeTableModelInterface)getTableModel()).getNodeOf(directRow);
    }
    //
    @Override
    public synchronized int getRowOf( TreeTableNode node ){
        if( node==null )return -1;
        if( root == null )return -1;

        int sortrow = ((TreeTableModelInterface)getTableModel()).getRowOf(node);
        if( sortrow<0 )return -1;

        int frow = mapRowToOutside(sortrow);
        return frow;
    }

    @Override
    public synchronized boolean isRootVisible(){
        return ((TreeTableModelInterface)getTableModel()).isRootVisible();
    }

    //<editor-fold defaultstate="collapsed" desc="root : TreeTableNode - древо таблицы">
    /**
     * Древо таблицы
     */
    protected TreeTableNode root;

    /**
     * Возвращает древо таблицы
     * @return древо таблицы
     */
    @Override
    public synchronized TreeTableNode getRoot() {
        if( root!=null )return root;
        root = new TreeTableNodeBasic();
        return root;
    }

    /**
     * Устанавливает древо таблицы
     * @param root древо
     */
    @Override
    public synchronized void setRoot(TreeTableNode root) {
        ((TreeTableModelInterface)getTableModel()).setRoot(root);

        TreeTableNode old = this.getRoot();
        this.root = root;
        TreeTableNode newv = this.getRoot();

        logFine("setRoot old={0} current={1}",old,newv);

        listenRoot();
        applyFilter();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="listenRoot()">
    protected final Closeables rootListeners = new Closeables();

    /**
     * Добавляет подписчика на событие корневого элемента дерева
     */
    protected synchronized void listenRoot(){
        logFine("listenRoot");

        logFinest("rootListeners.closeAll");
        rootListeners.close();

        if( root==null )return;

        rootListeners.append(
            root.listen( TreeTableNodeCollapsed.class, _ev -> {
                TreeTableNodeCollapsed ev = (TreeTableNodeCollapsed)_ev;
                onTreeNodeCollapsed(ev);
            }),
            root.listen( TreeTableNodeExpanded.class, _ev -> {
                TreeTableNodeExpanded ev = (TreeTableNodeExpanded)_ev;
                onTreeNodeExpanded(ev);
            })
        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="onTreeNodeExpanded()">
    private void onTreeNodeExpanded(TreeTableNodeExpanded ev) {
        TreeTableNode ettn1 = ev==null ? null : ev.getSource();
        TreeTableNodeBasic ettn = (TreeTableNodeBasic)ettn1;
        Object ettnData = ettn==null ? null : ettn.getData();

        logFine("onTreeNodeExpanded ev.source.data={0}", ettnData );

        if( source==null )return;

        TreeSet<Integer> includeRows = new TreeSet<>();

        for( TreeTableNode twnode : ev.getSource().walk().go()){
            TreeTableNode node = ((TreeTableNode)twnode);

            int si = ((TreeTableModelInterface)getTableModel()).getRowOf(node);
            if( si<0 )continue;

            Integer di = source.indexOf(si);
            if( di==null || di<0 ){
                if( isVisible(node) ){
                    // необходимо отобразить строку
                    di = source.add(si);
                    includeRows.add(di);
                }
            }else{
                continue;
            }
        }

        if( includeRows.isEmpty() )return;
        if( includeRows.size()==1 ){
            int di = includeRows.first();
            fireRowsInserted(di, di);
            return;
        }

        List<int[]> ranges = new ArrayList<>();

        int rangeStart = -1;
        int rangeEnd = -1;
        int diPrev = -1;
        int i = -1;
        for( int di : includeRows ){
            i++;
            if( i==0 ){
                rangeStart = di;
            }else{
                int d = Math.abs(diPrev - di);
                if( d>1 ){
                    rangeEnd = diPrev;
                    ranges.add(new int[]{ rangeStart, rangeEnd });
                }
                rangeStart = di;
            }
            diPrev = di;
        }
        ranges.add(new int[]{ rangeStart, diPrev });

        for( int ri=0; ri<ranges.size(); ri++ ){
            int[] range = ranges.get(ri);
            int rStart = range[0];
            int rEnd = range[1];
            int rFrom = Math.min(rStart, rEnd);
            int rTo = Math.max(rStart, rEnd);
            fireRowsInserted(rFrom, rTo);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="onTreeNodeCollapsed()">
    private void onTreeNodeCollapsed(TreeTableNodeCollapsed ev) {
        TreeTableNode<?> ettn1 = ev==null ? null : ev.getSource();

        TreeTableNodeBasic ettn = (TreeTableNodeBasic)ettn1;
        Object ettnData = ettn==null ? null : ettn.getData();

        logFine("onTreeNodeCollapsed ev.source.data={0}", ettnData );

        if( source==null )return;

        TreeSet<Integer> removedRows = new TreeSet<>();
        TreeSet<Integer> forRemoveFrontRows = new TreeSet<>();

        for( TreeStep twnode : ev.getSource().walk().tree()){
            TreeStep ts = (((TreeStep)twnode));
            TreeTableNode node = (TreeTableNode) ts.getNode();

            int backIndex = ((TreeTableModelInterface)getTableModel()).getRowOf(node);
            if( backIndex<0 )continue;

            Integer frontIndex = source.indexOf(backIndex);
            if( frontIndex==null )continue;
            if( frontIndex<0 )continue;

//            System.out.println("ts level="+ts.getLevel()+" path="+
//                ts.nodes().map( x -> ((TreeTableNode)x).getData() ).toList()
//            );
            int lvl = Math.abs(ts.getLevel());
            if( lvl>0 ){
                // точно не видим
//                System.out.println("mark 1 removeByIndex="+frontIndex);
                //source.removeByIndex(frontIndex);
                forRemoveFrontRows.add(frontIndex);
                //removedRows.add(frontIndex);
                continue;
            }

            if( !isVisible(node) ){
//                System.out.println("mark 2 removeByIndex="+frontIndex);
                //source.removeByIndex(frontIndex);
                forRemoveFrontRows.add(frontIndex);
            }
        }

        forRemoveFrontRows.descendingSet().forEach( frontRowIdx -> {
//            System.out.println("remove front="+frontRowIdx);
            source.removeByIndex(frontRowIdx);
            removedRows.add(frontRowIdx);
        });

        if( removedRows.isEmpty() )return;
        if( removedRows.size()==1 ){
            int di = removedRows.first();
            fireRowsDeleted(di, di);
            return;
        }

        List<int[]> ranges = new ArrayList<>();

        int rangeStart = -1;
        int rangeEnd = -1;
        int diPrev = -1;
        int i = -1;
        for( int di : removedRows ){
            i++;
            if( i==0 ){
                rangeStart = di;
            }else{
                int d = Math.abs(diPrev - di);
                if( d>1 ){
                    rangeEnd = diPrev;
                    ranges.add(new int[]{ rangeStart, rangeEnd });
                    rangeStart = di;
                }
            }
            diPrev = di;
        }
        ranges.add(new int[]{ rangeStart, diPrev });

        for( int ri=ranges.size()-1; ri>=0; ri-- ){
            int[] range = ranges.get(ri);
            int rStart = range[0];
            int rEnd = range[1];
            int rFrom = Math.min(rStart, rEnd);
            int rTo = Math.max(rStart, rEnd);
            fireRowsDeleted(rFrom, rTo);
        }
    }
    //</editor-fold>
}
