/*
 * The MIT License
 *
 * Copyright 2017 Kamnev Georgiy <nt.gocha@gmail.com>.
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
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import xyz.cofe.ecolls.Closeables;
import xyz.cofe.gui.swing.table.Columns;
import xyz.cofe.gui.swing.table.EventSupport;
import xyz.cofe.gui.swing.table.FilterRowTM;
import xyz.cofe.gui.swing.table.TableModelEventDelegator;

/**
 * Модель таблицы - древо
 * @author nt.gocha@gmail.com
 */
public class TreeTableModel
    implements TableModel
{
    private static boolean eq( Object a, Object b ){
        if( a==null && b==null )return true;
        if( a==null && b!=null )return false;
        if( a!=null && b==null )return false;
        return a.equals(b);
    }

    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTableModel.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }
    private static boolean isLogSevere(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.SEVERE.intValue();
    }
    private static boolean isLogWarning(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.WARNING.intValue();
    }
    private static boolean isLogInfo(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.INFO.intValue();
    }
    private static boolean isLogFine(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.FINE.intValue();
    }
    private static boolean isLogFiner(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINER.intValue();
    }
    private static boolean isLogFinest(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINEST.intValue();
    }

    private static void logEntering(String method,Object ... args){
        logger.entering(TreeTableModel.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(TreeTableModel.class.getName(), method, result);
    }

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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="root : TreeTableNode - древо таблицы">
    /**
     * Древо таблицы
     */
    protected TreeTableNode<?> root;

    /**
     * Возвращает древо таблицы
     * @return древо таблицы
     */
    public synchronized TreeTableNode getRoot() {
        if( root!=null )return root;
        root = new TreeTableNodeBasic();
        return root;
    }

    /**
     * Устанавливает древо таблицы
     * @param root древо
     */
    public synchronized void setRoot(TreeTableNode root) {
        TreeTableNode old = this.getRoot();
        this.root = root;
        TreeTableNode newv = this.getRoot();

        listenRoot();

        getDirectModel().setRoot(newv);
        Object fm = getFilterModel();
        if( fm instanceof TreeTableFilterModel ){
            ((TreeTableFilterModel)fm).setRoot(newv);
        }
        fireAllChanged();

        /*
        if( !eq(old, newv) ){
            if( directModel!=null ){
                directModel.setRoot(newv);
            }else{
                fireAllChanged();
            }
        }
        */
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="directModel : TreeTableDirectModel">
    protected TreeTableDirectModel directModel;

    /**
     * Возвращает модель дерева-таблицы без фильтров
     * @return модель дерева-таблицы
     */
    public synchronized TreeTableDirectModel getDirectModel(){
        if( directModel!=null )return directModel;
        directModel = new TreeTableDirectModel();
        directModel.setRootVisible(false);
        directModel.setRoot(getRoot());
        return directModel;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="filterModel : FilterRowTM">
    protected FilterRowTM filterModel;

    /**
     * Возвращает модель дерева-таблицы с фильтрацией невидимых узлов
     * @return модель дерева-таблицы
     */
    public synchronized FilterRowTM getFilterModel(){
        if( filterModel!=null )return filterModel;
        filterModel = new TreeTableFilterModel(getDirectModel());
        return filterModel;
        /* filterModel = new FilterRowTM();
        filterModel.setTableModel(getDirectModel());
        filterModel.setRowFilter(
            new Predicate<RowData>() {
            @Override
            public boolean validate(RowData rdata) {

            int ri = rdata.getRowIndex();

            TreeTableNode ttn = getDirectModel().getNodeOf(ri);
            if( ttn==null )return true;

            boolean v = isExpandVisible(ttn,null);
            return v;
        }} );
        return filterModel; */
    }

    /*public boolean isExpandVisible(TreeTableNode ttn){
        return isExpandVisible(ttn, null);
    }*/

    /*public synchronized boolean isExpandVisible(TreeTableNode ttn, List<TreeTableNode> prefix){
        if( prefix==null )prefix = new LinkedList<>();

        List ln = ttn.getNodePath();
        if( ln==null )return true;

        ln.addAll(0, prefix);

        if( ln.size()>0 ){
            ln.remove(ln.size()-1);
        }

        int level = 0;

        for( Object objn : ln ){
            level++;

            TreeTableNode n = (TreeTableNode)objn;
            if( n==null )continue;

            if( level==0 )continue; // root always expnaded
            if( level==1 && !getDirectModel().isRootVisible() )continue;

            if( !n.isExpanded() ){
                return false;
            }
        }

        return true;
    }*/
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="sortRowModel : SortRowTM">
    /*protected SortRowTM sortRowModel;

    public synchronized SortRowTM getSortRowModel(){
        if( sortRowModel!=null )return sortRowModel;
        sortRowModel = new SortRowTM();
        sortRowModel.setTableModel(getFilterRowModel());
        sortRowModel.setRowComparator( //(row1,row2) -> {
            new Comparator<RowData>() {
            @Override
            public int compare(RowData row1, RowData row2) {

            int ri1 = row1.getRowIndex();
            int ri2 = row2.getRowIndex();

            TableModel tm1 = row1.getTableModel();
            TableModel tm2 = row2.getTableModel();

            TreeTableNode ttn1 = null;
            TreeTableNode ttn2 = null;

            if( tm1==getFilterRowModel() ){
                int dri1 = getFilterRowModel().mapRowToInside(ri1);
                if( dri1>=0 ){
                    ttn1 = getDirectModel().getNodeOf(dri1);
                }
            }else if( tm1==getSortRowModel() ){
                int fri1 = getSortRowModel().mapRowToInside(ri1);
                int dri1 = fri1 >=0 ? getFilterRowModel().mapRowToInside(fri1) : -1;
                if( dri1>=0 ){
                    ttn1 = getDirectModel().getNodeOf(dri1);
                }
            }else if( tm1==getDirectModel()){
                ttn1 = getDirectModel().getNodeOf(ri1);
            }

            if( tm2==getFilterRowModel() ){
                int dri2 = getFilterRowModel().mapRowToInside(ri2);
                if( dri2>=0 ){
                    ttn2 = getDirectModel().getNodeOf(dri2);
                }
            }else if( tm2==getSortRowModel() ){
                int fri2 = getSortRowModel().mapRowToInside(ri2);
                int dri2 = fri2 >=0 ? getFilterRowModel().mapRowToInside(fri2) : -1;
                if( dri2>=0 ){
                    ttn2 = getDirectModel().getNodeOf(dri2);
                }
            }else if( tm2==getDirectModel()){
                ttn2 = getDirectModel().getNodeOf(ri2);
            }

            if( ttn1==null || ttn2==null )return 0;

            int ro1 = ttn1.getRootOffset();
            int ro2 = ttn2.getRootOffset();
            int cmp = ro1==ro2 ? 0 : (ro1 < ro2 ? -1 : 1);

            return cmp;
        }} );
        return sortRowModel;
    }*/
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EventSupport">
    /**
     * Поддержка событий TableModel
     */
    protected final EventSupport evsupport;

    /**
     * Уведомляет что полностью изменилась таблица, включая колонки
     */
    public void fireAllChanged() {
        evsupport.fireAllChanged();
    }

    /**
     * Уведомляет что изменились колонки: кол-во, названия, тип
     */
    public void fireColumnsChanged() {
        evsupport.fireColumnsChanged();
    }

    /**
     * Уведомляет что изменилась строка
     * @param row Индекс строки
     */
    public void fireRowUpdated(int row) {
        evsupport.fireRowUpdated(row);
    }

    /**
     * Уведомляет что изменилась строки
     * @param rowIndexFrom Индекс строки с какой
     * @param toIndexInclude Индекс строки по какую включительно
     */
    public void fireRowsUpdated(int rowIndexFrom, int toIndexInclude) {
        evsupport.fireRowsUpdated(rowIndexFrom, toIndexInclude);
    }

    /**
     * Уведомляет что изменилась ячейка
     * @param rowIndex Строка
     * @param columnIndex Колонка
     */
    public void fireCellChanged(int rowIndex, int columnIndex) {
        evsupport.fireCellChanged(rowIndex, columnIndex);
    }

    /**
     * Уведомляет что добавлены новые строки
     * @param rowIndexFrom с какой строки
     * @param toIndexInclude по какую строку включительно
     */
    public void fireRowsInserted(int rowIndexFrom, int toIndexInclude) {
        evsupport.fireRowsInserted(rowIndexFrom, toIndexInclude);
    }

    /**
     * Уведомляет что удалены строки
     * @param rowIndexFrom с какой строки
     * @param toIndexInclude  по какую строку включительно
     */
    public void fireRowsDeleted(int rowIndexFrom, int toIndexInclude) {
        evsupport.fireRowsDeleted(rowIndexFrom, toIndexInclude);
    }

    /**
     * Уведомляет подписчиков о событии
     * @param e Событие
     */
    public void fireTableModelEvent(TableModelEvent e) {
        evsupport.fireTableModelEvent(e);
    }

    /**
     * Посылать уведомления в потоке AWT/Swing
     * @return true - в потоке awt
     */
    public boolean isNotifyInAwtThread() {
        return evsupport.isNotifyInAwtThread();
    }

    /**
     * Посылать уведомления в потоке AWT/Swing
     * @param notifyInAwtThread true - в потоке awt
     */
    public void setNotifyInAwtThread(boolean notifyInAwtThread) {
        evsupport.setNotifyInAwtThread(notifyInAwtThread);
    }

    /**
     * Дожидаться ответа на увемоление AWT/Swing потока
     * @return true - вызвать SwingUtilites.invokeAndWait / false - вызывать SwingUtilites.invokeLater
     */
    public boolean isAwtInvokeAndWait() {
        return evsupport.isAwtInvokeAndWait();
    }

    /**
     * Дожидаться ответа на увемоление AWT/Swing потока
     * @param awtInvokeAndWait true - вызвать SwingUtilites.invokeAndWait / false - вызывать SwingUtilites.invokeLater
     */
    public void setAwtInvokeAndWait(boolean awtInvokeAndWait) {
        evsupport.setAwtInvokeAndWait(awtInvokeAndWait);
    }

    /**
     * Возвращает коллекцию подписчиков
     * @return Подписчики
     */
    public Collection<TableModelListener> getListenersCollection() {
        return evsupport.getListenersCollection();
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        // getFilterRowModel().addTableModelListener(l);
        evsupport.addTableModelListener(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        // getFilterRowModel().removeTableModelListener(l);
        evsupport.removeTableModelListener(l);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getNodeOf( row )">
    /**
     * Возвращает узел для указанной строки
     * @param row row - строка
     * @return узел или null
     */
    public TreeTableNode getNodeOf( int row ){
        //int row2 = getSortRowModel().mapRowToInside(row);
        //if( row2<0 )return null;
        int directRow = getFilterModel().mapRowToInside(row);

        //int directRow = getFilterRowModel().mapRowToInside(row);
        //int directRow = getFilterRowModel().mapRowToInside(row2);
        if( directRow<0 )return null;

        return getDirectModel().getNodeOf(directRow);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getRowOf( node ):int">
    /**
     * Возвращает индекс строки для заданного узла дерева
     * @param ttnode узел дерева
     * @return индекс строки или -1
     */
    public int getRowOf( TreeTableNode ttnode ){
        if( ttnode==null )return -1;

        int directRow = getDirectModel().getRowOf(ttnode);
        if( directRow<0 )return -1;

        int filterRow = getFilterModel().mapRowToOutside(directRow);
        //if( filterRow<0 )return -1;

        //int sortRow = getSortRowModel().mapRowToOutside(filterRow);
        //return sortRow;
        if( filterRow<0 )return -1;

        return filterRow;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="columns : Columns">
    /**
     * Возвращает колонки таблице
     * @return колонки
     */
    public synchronized Columns getColumns(){
        return getDirectModel().getColumns();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="eventDelegator : TableModelEventDelegator">
    protected final TableModelEventDelegator evdelegator;

    public TableModelEventDelegator getEventDelegator(){
        return evdelegator;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="listenRoot()">
    protected final Closeables rootListeners = new Closeables();
    protected synchronized void listenRoot(){
        rootListeners.close();
        if( root==null )return;
    }
    //</editor-fold>

    /**
     * Конструктор
     */
    public TreeTableModel(){
        evsupport = new EventSupport(this);

        evdelegator = new TableModelEventDelegator();

        evdelegator.setMapColumnToOutside( innerColumn -> innerColumn );
        evdelegator.setMapRowToOutside( innerRow -> innerRow );
        evdelegator.setSourceModel(getFilterModel());
        evdelegator.setTargetModel(this);

        evdelegator.setSender( ev -> evsupport.fireTableModelEvent(ev) );
        evdelegator.start();
    }

    //<editor-fold defaultstate="collapsed" desc="table model methods">
    @Override
    public int getRowCount() {
        int rc = getFilterModel().getRowCount();
        //int rc = getSortRowModel().getRowCount();
        return rc;
    }

    @Override
    public int getColumnCount() {
        int cc = getFilterModel().getColumnCount();
        // int cc = getSortRowModel().getColumnCount();
        return cc;
    }

    @Override
    public String getColumnName(int columnIndex) {
        String colname = getFilterModel().getColumnName(columnIndex);
        // String colname = getSortRowModel().getColumnName(columnIndex);
        return colname;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Class c = getFilterModel().getColumnClass(columnIndex);
        // Class c = getSortRowModel().getColumnClass(columnIndex);
        return c;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        boolean ce = getFilterModel().isCellEditable(rowIndex,columnIndex);
        // boolean ce = getSortRowModel().isCellEditable(rowIndex,columnIndex);
        return ce;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object v = getFilterModel().getValueAt(rowIndex,columnIndex);
        // Object v = getSortRowModel().getValueAt(rowIndex,columnIndex);
        return v;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        getFilterModel().setValueAt(aValue, rowIndex, columnIndex);
        // getSortRowModel().setValueAt(aValue, rowIndex, columnIndex);
    }
    //</editor-fold>
}
