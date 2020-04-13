/*
 * The MIT License
 *
 * Copyright 2017 Kamnev Georgiy (nt.gocha@gmail.com).
 *
 * Данная лицензия разрешает, безвозмездно, лицам, получившим копию данного программного
 * обеспечения и сопутствующей документации (в дальнейшем именуемыми "Программное Обеспечение"),
 * использовать Программное Обеспечение без ограничений, включая неограниченное право на
 * использование, копирование, изменение, объединение, публикацию, распространение, сублицензирование
 * и/или продажу копий Программного Обеспечения, также как и лицам, которым предоставляется
 * данное Программное Обеспечение, при соблюдении следующих условий:
 *
 * Вышеупомянутый копирайт и данные условия должны быть включены во все копии
 * или значимые части данного Программного Обеспечения.
 *
 * ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ ПРЕДОСТАВЛЯЕТСЯ «КАК ЕСТЬ», БЕЗ ЛЮБОГО ВИДА ГАРАНТИЙ,
 * ЯВНО ВЫРАЖЕННЫХ ИЛИ ПОДРАЗУМЕВАЕМЫХ, ВКЛЮЧАЯ, НО НЕ ОГРАНИЧИВАЯСЬ ГАРАНТИЯМИ ТОВАРНОЙ ПРИГОДНОСТИ,
 * СООТВЕТСТВИЯ ПО ЕГО КОНКРЕТНОМУ НАЗНАЧЕНИЮ И НЕНАРУШЕНИЯ ПРАВ. НИ В КАКОМ СЛУЧАЕ АВТОРЫ
 * ИЛИ ПРАВООБЛАДАТЕЛИ НЕ НЕСУТ ОТВЕТСТВЕННОСТИ ПО ИСКАМ О ВОЗМЕЩЕНИИ УЩЕРБА, УБЫТКОВ
 * ИЛИ ДРУГИХ ТРЕБОВАНИЙ ПО ДЕЙСТВУЮЩИМ КОНТРАКТАМ, ДЕЛИКТАМ ИЛИ ИНОМУ, ВОЗНИКШИМ ИЗ, ИМЕЮЩИМ
 * ПРИЧИНОЙ ИЛИ СВЯЗАННЫМ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ ИЛИ ИСПОЛЬЗОВАНИЕМ ПРОГРАММНОГО ОБЕСПЕЧЕНИЯ
 * ИЛИ ИНЫМИ ДЕЙСТВИЯМИ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ.
 */

package xyz.cofe.gui.swing.tree;


import java.io.Closeable;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import xyz.cofe.collection.Tree;
import xyz.cofe.collection.TreeEvent;
import xyz.cofe.ecolls.Closeables;
import xyz.cofe.gui.swing.table.Column;
import xyz.cofe.gui.swing.table.Columns;
import xyz.cofe.gui.swing.table.EventSupport;
import xyz.cofe.gui.swing.table.GetReaderForRow;
import xyz.cofe.gui.swing.table.IsRowEditable;

/**
 * Модель дерева, которое отображет все узлы вне зависимости от их состояния раскрытия
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 * @see TreeTableNodeBasic#setExpanded(boolean)
 */
public class TreeTableDirectModel implements TreeTableModelInterface
{
    private static boolean eq( Object a, Object b ){
        if( a==null && b==null )return true;
        if( a==null && b!=null )return false;
        if( a!=null && b==null )return false;
        return a.equals(b);
    }

    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTableDirectModel.class.getName());

    private static Level logLevel(){
        return logger.getLevel() ;
    }

    private static boolean isLogSevere(){
        Level level = logLevel();
        return level==null
            ? true
            : level.intValue() <= Level.SEVERE.intValue();
    }

    private static boolean isLogWarning(){
        Level level = logLevel();
        return level==null
            ? true
            : level.intValue() <= Level.WARNING.intValue();
    }

    private static boolean isLogInfo(){
        Level level = logLevel();
        return level==null
            ? true
            : level.intValue() <= Level.INFO.intValue();
    }

    private static boolean isLogFine(){
        Level level = logLevel();
        return level==null
            ? true
            : level.intValue() <= Level.FINE.intValue();
    }

    private static boolean isLogFiner(){
        Level level = logLevel();
        return level==null
            ? false
            : level.intValue() <= Level.FINER.intValue();
    }

    private static boolean isLogFinest(){
        Level level = logLevel();
        return level==null
            ? false
            : level.intValue() <= Level.FINEST.intValue();
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

    private static void logEntering(String method,Object ... params){
        logger.entering(TreeTableDirectModel.class.getName(),method,params);
    }

    private static void logExiting(String method,Object result){
        logger.exiting(TreeTableDirectModel.class.getName(),method,result);
    }

    private static void logExiting(String method){
        logger.exiting(TreeTableDirectModel.class.getName(),method);
    }
    //</editor-fold>

    protected final EventSupport evsupport;

    /**
     * Конструктор по умолчанию
     */
    public TreeTableDirectModel(){
        evsupport = new EventSupport(this);
    }

    //<editor-fold defaultstate="collapsed" desc="root : TreeTableNode">
    protected TreeTableNode root;

    @Override
    public synchronized TreeTableNode getRoot()
    {
        if( root!=null )return root;
        root = new TreeTableNodeBasic();
        listenRoot();
        return root;
    }

    @Override
    public synchronized void setRoot(TreeTableNode root)
    {
        this.root = root;
        listenRoot();
        evsupport.fireAllChanged();
    }
    //</editor-fold>

    /**
     * Уведомляет подписчиков о изменении всех строк таблицы
     */
    public void fireAllChanged() {
        evsupport.fireAllChanged();
    }

    /**
     * Уведомляет подписчиков о изменении колонок таблицы
     */
    public void fireColumnsChanged() {
        evsupport.fireColumnsChanged();
    }

    /**
     * Уведомляет подписчиков о измении строки таблицы
     * @param row индекс строки
     */
    public void fireRowUpdated(int row) {
        evsupport.fireRowUpdated(row);
    }

    /**
     * Уведомляет подписчиков о изменении строк
     * @param rowIndexFrom индекс начала диапазона строк
     * @param toIndexInclude индекс конца (включительно) диапазона строк
     */
    public void fireRowsUpdated(int rowIndexFrom, int toIndexInclude) {
        evsupport.fireRowsUpdated(rowIndexFrom, toIndexInclude);
    }

    /**
     * Уведомляет подписчиков о изменении ячейки таблицы
     * @param rowIndex строка
     * @param columnIndex колонка
     */
    public void fireCellChanged(int rowIndex, int columnIndex) {
        evsupport.fireCellChanged(rowIndex, columnIndex);
    }

    /**
     * Уведомляет подписчиков о добавлении строк в таблицу
     * @param rowIndexFrom индекс начала диапазона строк
     * @param toIndexInclude индекс конца (включительно) диапазона строк
     */
    public void fireRowsInserted(int rowIndexFrom, int toIndexInclude) {
        evsupport.fireRowsInserted(rowIndexFrom, toIndexInclude);
    }

    /**
     * Уведомляет подписчиков о удалении строк из таблицы
     * @param rowIndexFrom индекс начала диапазона строк
     * @param toIndexInclude индекс конца (включительно) диапазона строк
     */
    public void fireRowsDeleted(int rowIndexFrom, int toIndexInclude) {
        evsupport.fireRowsDeleted(rowIndexFrom, toIndexInclude);
    }

    /**
     * Уведомляет подписчиков о событии таблицы
     * @param e событие
     */
    public void fireTableModelEvent(TableModelEvent e) {
        evsupport.fireTableModelEvent(e);
    }

//    protected synchronized void onTreeNodeBulkInserted(
//        TreeNodeBulkInserted ev,
//        TreeTableNode parent,
//        Integer insertIndex,
//        List insertedItems
//    )
//    {
//        if( ev==null )throw new IllegalArgumentException("ev == null");
//        if( parent==null )throw new IllegalArgumentException("parent == null");
//        if( insertIndex==null )throw new IllegalArgumentException("insertIndex == null");
//        if( insertedItems==null )throw new IllegalArgumentException("insertedItems == null");
//        if( insertIndex<0 )throw new IllegalArgumentException("insertIndex("+insertIndex+") < 0");
//        if( insertedItems.isEmpty() )return;
//
//        int insChildNCount = 0;
//        int chi = -1;
//        TreeTableNode firstInsertChild = null;
//
//        for( Object ch : insertedItems ){
//            chi++;
//            if( ch==null )throw new IllegalArgumentException("insert child["+chi+"] == null");
//            if( !(ch instanceof TreeTableNode) ){
//                throw new IllegalArgumentException(
//                    "insert child["+chi+"] not instanceof TreeTableNode");
//            }
//            if( firstInsertChild==null )firstInsertChild = (TreeTableNode)ch;
//            insChildNCount += ((TreeTableNode)ch).getNodesCount();
//        }
//
//        // parent.getRootOffset() - можно вычислить от предыдущего значения parent.getRootOffset(),
//        // при условии что от предыдущего состояния RootSCN не изменился
//        int parentOff = parent.getRootOffset();
//        if( !isRootVisible() )parentOff--;
//
//        // firstInsertChild.getRootOffset() - можно вычислить от parent.getRootOffset() + gap до firstInsertChild
//        int firstChildOff = firstInsertChild.getRootOffset();
//        if( !isRootVisible() ){
//            firstChildOff--;
//        }
//
//        if( parentOff>=0 ){
//            logFiner("onTreeNodeBulkInserted() fireRowUpdated parentOff={0}",parentOff);
//            evsupport.fireRowUpdated(parentOff);
//        }
//
//        int firstRow = firstChildOff;
//        int lastRow = firstChildOff + insChildNCount - 1;
//        evsupport.fireRowsInserted(firstRow, lastRow);
//    }

    /**
     * Вызывается при добавлении дочерних узлов в поддерево.
     * Генерирует соответ событие fireRowsInserted
     * @param evtna уведомлении о добавлении
     * @param parent поддерво в которое добавлен дочерний узел
     * @param child дочерний узел
     * @param childIndex индекс дочернего узла
     */
    protected synchronized void onTreeNodeAdded(
        TreeEvent.Added evtna,
        TreeTableNode parent,
        TreeTableNode child,
        Integer childIndex )
    {
        int childNCount = child.getNodesCount();
        int childOff = child.getRootOffset();
        if( !isRootVisible() ){
            childOff--;
        }

        int parentOff = parent.getRootOffset();
        if( !isRootVisible() )parentOff--;

        if( parentOff>=0 ){
            logFiner("onTreeNodeAdded() fireRowUpdated parentOff={0}",parentOff);
            evsupport.fireRowUpdated(parentOff);
        }

        logFiner(
            "onTreeNodeAdded() fireRowsInserted"
                + " rootVisible={0},"
                + ""
                + "child.ncnt={3}, fire inserted: {4} to {5}",
            isRootVisible(),
            null, //parent.getRootOffset(),
            null, //child.getRootOffset(),
            childNCount,
            childOff,
            childOff + childNCount - 1
        );

        evsupport.fireRowsInserted(childOff, childOff + childNCount - 1);
    }

    /**
     * Описывает удаленные дочерние узлы
     */
    protected static class RemovingNodeData {
        public int childRootOffset;
        public int childNodesCount;
        public int fromIndex;
        public int toIndex;
        public boolean rootVisible;
        public int parentRootOffset;

        public void fireEvent( EventSupport evsup, TreeTableNode child ){
            logFiner( "onTreeNodeRemoved() fireRowsDeleted( {0}, {1} ) roff={2} cnt={3} data={4}",
                fromIndex,
                toIndex,
                childRootOffset,
                childNodesCount,
                child.getData()
            );
            evsup.fireRowsDeleted(fromIndex, toIndex);

            if( rootVisible ){
                int poff = parentRootOffset;
                if( poff>=0 ){
                    logFiner( "onTreeNodeRemoved() fireRowsUpdated( {0}, {1} ) roff={2}",
                        parentRootOffset,parentRootOffset,
                        childRootOffset
                    );
                    evsup.fireRowsUpdated(parentRootOffset, parentRootOffset);
                }
            }else{
                int poff = parentRootOffset-1;
                if( poff>=0 ){
                    logFiner( "onTreeNodeRemoved() fireRowsUpdated( {0}, {1} ) roff={2}",
                        poff,poff,
                        childRootOffset
                    );
                    evsup.fireRowsUpdated(poff, poff);
                }
            }

        }
    }

    protected WeakHashMap<TreeTableNode,RemovingNodeData> removingChild
        = new WeakHashMap<>();

    /**
     * Вызывается при удалении дочерних узлов из поддерева
     * @param ev уведомление о удалении
     * @param parent поддерево из которого удален узел
     * @param child удаленный узел
     * @param childIndex индекс удаленного узла
     */
    protected synchronized void onTreeNodeRemoving(
        TreeEvent.Removed ev,
        TreeTableNode parent,
        TreeTableNode child,
        Integer childIndex
    ){
        int nchild = child.getNodesCount();
        int childoff = child.getRootOffset();

        int from = childoff;
        if( !isRootVisible() )from--;

        int to = from + nchild - 1;

        RemovingNodeData rndata = new RemovingNodeData();
        rndata.childNodesCount = nchild;
        rndata.childRootOffset = childoff;
        rndata.fromIndex = from;
        rndata.toIndex = to;
        rndata.rootVisible = isRootVisible();
        rndata.parentRootOffset = parent.getRootOffset();

        removingChild.put(child, rndata);
    }

    /**
     * Вызывается при удалении дочерних узлов из поддерева
     * @param ev уведомление о удалении
     * @param parent поддерево из которого удален узел
     * @param child удаленный узел
     * @param childIndex индекс удаленного узла
     */
    protected synchronized void onTreeNodeRemoved(
        TreeEvent.Removed ev,
        TreeTableNode parent,
        TreeTableNode child,
        Integer childIndex
    ){
        RemovingNodeData rndata = removingChild.get(child);
        if( rndata!=null )rndata.fireEvent(evsupport,child);
    }

    //<editor-fold defaultstate="collapsed" desc="listenRoot()">
    protected final Closeables rootListeners = new Closeables();

    /**
     * Добавляет подписчика на новый/текущий корневой узел,
     * чтоб отслеживать добавление удаление узлов в дочерних узлах дерева
     */
    protected synchronized void listenRoot(){
        rootListeners.close();
        if( root==null )return;

        rootListeners.append(
        root.listen( TreeEvent.Inserted.class, ev -> {
            Tree tnchild = ((TreeEvent.Inserted)ev).getChild();
            Tree tnprnt = ((TreeEvent.Inserted)ev).getParent();

            if( !(tnchild instanceof TreeTableNode) )return;
            if( !(tnprnt instanceof TreeTableNode) )return;

            TreeTableNode child = (TreeTableNode)tnchild;
            TreeTableNode parent = (TreeTableNode)tnprnt;

            onTreeNodeAdded((TreeEvent.Inserted)ev, parent, child, ((TreeEvent.Inserted)ev).getIndex());
        }));

//        Closeable cl =
//            //root.onTreeNodeEvent(TreeNodeAdded.class, ev -> {
//            root.onTreeNodeEvent(TreeNodeAdded.class, new Reciver() {
//                @Override
//                public void recive(Object ev) {
//                    if( !(ev instanceof TreeNodeAdded ) )return;
//                    TreeNodeAdded tnaev = (TreeNodeAdded)ev;
//                    TreeNode tnchild = tnaev.getChild();
//                    TreeNode tnprnt = tnaev.getParent();
//
//                    if( !(tnchild instanceof TreeTableNode) )return;
//                    if( !(tnprnt instanceof TreeTableNode) )return;
//
//                    TreeTableNode child = (TreeTableNode)tnchild;
//                    TreeTableNode parent = (TreeTableNode)tnprnt;
//
//                    onTreeNodeAdded(tnaev, parent, child, tnaev.getChildIndex());
//                }
//            } );
//        rootListeners.add( cl );



////        cl =
//            root.onTreeNodeEvent(TreeNodeBulkInserted.class, new Reciver() {
//                @Override
//                public void recive(Object ev) {
//                    if( !(ev instanceof TreeNodeBulkInserted ) )return;
//                    TreeNodeBulkInserted bev = (TreeNodeBulkInserted)ev;
//
//                    int insIdx = bev.getInsertIndex();
//                    List insItms = bev.getItems();
//                    TreeNode tnParent = bev.getParent();
//
//                    if( !(tnParent instanceof TreeTableNode) )
//                        throw new IllegalStateException("inserted parent not TreeTableNode");
//
//                    TreeTableNode parent = (TreeTableNode)tnParent;
//
//                    onTreeNodeBulkInserted(bev,parent,insIdx,insItms);
//                }
//            } );
////        rootListeners.add( cl );

        rootListeners.append(
        root.listen( TreeEvent.Deleted.class, _ev -> {
            TreeEvent.Deleted ev = (TreeEvent.Deleted)_ev;

            Tree tnchild = ((TreeEvent.Deleted)ev).getChild();
            Tree tnprnt = ((TreeEvent.Deleted)ev).getParent();

            if( !(tnchild instanceof TreeTableNode) )return;
            if( !(tnprnt instanceof TreeTableNode) )return;

            TreeTableNode child = (TreeTableNode)tnchild;
            TreeTableNode parent = (TreeTableNode)tnprnt;

            onTreeNodeRemoving(ev, parent, child, ev.getIndex());
            onTreeNodeRemoved(ev, parent, child, ev.getIndex());

        }));

//        cl = root.onTreeNodeEvent(TreeNodeRemoving.class, new Reciver() {
//            @Override
//            public void recive(Object ev) {
//                if( !(ev instanceof TreeNodeRemoving ) )return;
//
//                TreeNodeRemoving tev = (TreeNodeRemoving)ev;
//                TreeNode tnchild = tev.getChild();
//                TreeNode tnprnt = tev.getParent();
//
//                if( !(tnchild instanceof TreeTableNode) )return;
//                if( !(tnprnt instanceof TreeTableNode) )return;
//
//                TreeTableNode child = (TreeTableNode)tnchild;
//                TreeTableNode parent = (TreeTableNode)tnprnt;
//
//                onTreeNodeRemoving(tev, parent, child, tev.getChildIndex());
//            }} );
//        rootListeners.add( cl );
//
//        cl = root.onTreeNodeEvent(TreeNodeRemoved.class, new Reciver() {
//            @Override
//            public void recive(Object ev) {
//                if( !(ev instanceof TreeNodeRemoved ) )return;
//
//                TreeNodeRemoved tev = (TreeNodeRemoved)ev;
//                TreeNode tnchild = tev.getChild();
//                TreeNode tnprnt = tev.getParent();
//
//                if( !(tnchild instanceof TreeTableNode) )return;
//                if( !(tnprnt instanceof TreeTableNode) )return;
//
//                TreeTableNode child = (TreeTableNode)tnchild;
//                TreeTableNode parent = (TreeTableNode)tnprnt;
//
//                onTreeNodeRemoved(tev, parent, child, tev.getChildIndex());
//            }} );
//        rootListeners.add( cl );

//        cl = rootListeners.add(
//            root.onTreeNodeEvent(TreeTableDataChagned.class, ev -> {
//            } )
//        );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rootVisible : boolean">
    protected boolean rootVisible = false;
    @Override
    public synchronized boolean isRootVisible() { return rootVisible; }

    /**
     * Указывает отображать или нет корень дерева в модели таблицы
     * @param rootVisible true - отображать в модели таблицы / false - отобрадать его дочерние узлы
     */
    public synchronized void setRootVisible(boolean rootVisible) {
        this.rootVisible = rootVisible;
        fireAllChanged();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getNodeOf(row)">
    @Override
    public synchronized TreeTableNode getNodeOf( int row ){
        if( root==null )return null;
        if( !rootVisible ){
            int trow = row + 1;
            TreeTableNode ttn = (TreeTableNode)root.deepOffset(trow);
            return ttn;
        }
        return (TreeTableNode)root.deepOffset(row);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getRowOf(node)">
    @Override
    public synchronized int getRowOf( TreeTableNode node ){
        if( node == null )return -1;
        if( root == null )return -1;

        int roff = node.getRootOffset();

        Object o = root.deepOffset(roff);
        if( o==null )return -1;

        if( !eq(o, node) )return -1;

        if( eq(root, node) ){
            if( rootVisible )return 0;
            return roff - 1;
        }

        return rootVisible ? roff : roff - 1;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="columns">
    protected Columns columns;

    /**
     * Возвращает колонки модели таблицы
     * @return колонки
     */
    public synchronized Columns getColumns(){
        if( columns!=null )return columns;
        columns = new Columns();
        columns.add(new TreeTableNodeColumn() );
        //columns.onChanged( (idx,oldc,newc) -> {
        columns.onChanged( (idx, oldc, newc) -> evsupport.fireAllChanged() );
        return columns;
    }
    //</editor-fold>

    @Override
    public synchronized int getRowCount()
    {
        if( root==null )return 0;

        int rc = root.getNodesCount();
        if( rc<0 )return 0;

        if( !rootVisible ){
            if( rc<=1 )return 0;
            return rc-1;
        }

        return rc;
    }

    @Override
    public synchronized int getColumnCount()
    {
        return getColumns().size();
    }

    @Override
    public synchronized String getColumnName(int columnIndex)
    {
        String name = null;
        if( columnIndex>=0 && columnIndex<getColumnCount() ){
            Column col = getColumns().get(columnIndex);
            name = col!=null ? col.getName() : null;
        }
        return name==null ? "?" : name;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        Class type = null;
        if( columnIndex>=0 && columnIndex<getColumnCount() ){
            Column col = getColumns().get(columnIndex);
            type = col!=null ? col.getType() : null;
        }
        return type==null ? String.class : type;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        TreeTableNode node = getNodeOf(rowIndex);
        if( node==null )return false;

        Column col = null;
        Class type = null;

        if( columnIndex>=0 && columnIndex<getColumnCount() ){
            col = getColumns().get(columnIndex);
            type = col!=null ? col.getType() : null;
        }

        if( col instanceof IsRowEditable ){
            boolean editable = ((IsRowEditable)col).isRowEditable(node);
            return editable;
        }

        Function conv = col==null ? null : col.getWriter();

        if( conv!=null ){
            return true;
        }

        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        TreeTableNode node = getNodeOf(rowIndex);
        if( node==null )return null;

        Column col = null;
        Class type = null;

        if( columnIndex>=0 && columnIndex<getColumnCount() ){
            col = getColumns().get(columnIndex);
            type = col!=null ? col.getType() : null;
        }

        Function conv = null;
        if( col!=null ){
            conv =
                (col instanceof GetReaderForRow)
                    ? ((GetReaderForRow)col).getReader(rowIndex)
                    : col.getReader();
        }

        //col==null ? null : col.getReader();

        if( type==null || conv==null )return node.toString();

        return conv.apply(node);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        TreeTableNode node = getNodeOf(rowIndex);
        if( node==null )return;

        Column col = null;
        Class type = null;

        if( columnIndex>=0 && columnIndex<getColumnCount() ){
            col = getColumns().get(columnIndex);
            type = col!=null ? col.getType() : null;
        }

        Function<Column.Cell,Boolean> conv = col==null ? null : col.getWriter();

        if( conv!=null ){
            if( conv.apply(new Column.Cell(node, aValue)) ){
                fireCellChanged(rowIndex, columnIndex);
            }
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l)
    {
        evsupport.addTableModelListener(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l)
    {
        evsupport.removeTableModelListener(l);
    }
}
