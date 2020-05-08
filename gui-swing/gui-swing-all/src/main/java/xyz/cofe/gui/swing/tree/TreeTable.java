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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.KeyStroke;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import xyz.cofe.collection.TreeEvent;
import xyz.cofe.ecolls.Closeables;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.gui.swing.GuiUtil;
import xyz.cofe.gui.swing.cell.CellContext;
import xyz.cofe.gui.swing.cell.CellFormat;
import xyz.cofe.gui.swing.cell.TCRenderer;
import xyz.cofe.gui.swing.tmodel.Column;
import xyz.cofe.gui.swing.tmodel.Columns;
import xyz.cofe.gui.swing.table.Table;
import xyz.cofe.j2d.RectangleFn;

// TODO: При первом отображении проверять: rootVisible==false && rootExpand exists  = то => expand

/**
 * Компонент TreeTable.
 * Компонент Java Swing, для работы с деревом в виде таблицы. <p>
 *
 * Основные фичи
 * <ul>
 * <li> Компонент сам хранит внутри себя структуру дерева
 * <li> Дерево может динамически изменяться (добавление/удаление/перемещение узлов),
 * компонент берет на себя заботу, по отслеживанию изменения структуры дерева.
 * <li>Вершинами дерева, могут являться любые объекты (включая null) и между ними можно задать функции:
 * <ul>
 * <li> follow ( node ) : nodes - функцию доступа к дочерним узлам (например file.list())
 * <li> naming( node ) : String - функцию именования вершины
 * <li>followable( node ) : boolean - проверка наличия дочерних узлов
 * <li> и д.р.
 * </ul>
 * <li> Дерево может быть не однородно по типу данных вершин,
 * так например вполне допускается что вершина File может содержать дочерние вершины как Map/String/... и т.д.
 * <li> Дерево может содержать данные которые повторяются, null ссылки и кольца
 * <li> Любой узел может содержать свою версию функций: <i>follow, naming, followable, ...</i>
 * <li> Узлы дерева полученные через <i>follow</i> кэшируются и могут быть освобождены/перечитаны спустя некоторое время
 * <li> Генериуется различные события как раскрытие узла, или добавление узла в дерево и т.д.
 * </ul>
 * @author nt.gocha@gmail.com
 */
public class TreeTable
    extends Table
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTable.class.getName());

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
        logger.entering(TreeTable.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(TreeTable.class.getName(), method, result);
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

    public TreeTable(){
        setDefaultRenderer(TreeTableNodeBasic.class, getNodeRender());
        setDefaultRenderer(TreeTableNode.class, getNodeRender());

        setDefaultEditor(TreeTableNodeValue.class, getNodeEditor());

        //TreeTableNodeRender ttnr = new TreeTableNodeRender();
        //setDefaultRenderer(TreeTableNodeValue.class, ttnr);

        setDefaultRenderer(TreeTableNodeValue.class, new TCRenderer());

        setModel(getTreeTableModel());
    }

    //<editor-fold defaultstate="collapsed" desc="columnScrollModel : ColumnScrollModel">
    private ColumnScrollModel columnScrollModel;

    /**
     * Возвращает модель скроллирования колонок
     * @return модель скроллинга колонок
     */
    public ColumnScrollModel getColumnScrollModel(){
        if( columnScrollModel!=null )return columnScrollModel;
        columnScrollModel = new ColumnScrollModel();
        columnScrollModel.setTable(this);
        return columnScrollModel;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="nodeRender : TCellNodeRender">
    protected TCellNodeRender nodeRender;

    /**
     * Возвращает рендер ячеек
     * @return рендер ячеек
     */
    public TCellNodeRender getNodeRender(){
        if(nodeRender!=null)return nodeRender;

        nodeRender = new TCellNodeRender(
            new TreeNodeCellRender(
                new CellFormat().font(getFont()),
                getColumnScrollModel()
            ),
            null
        );
        nodeRender.setColumnScrollModel(getColumnScrollModel());
        return nodeRender;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="nodeEditor">
    protected TreeTableNodeValueEditor nodeEditor;

    /**
     * Возвращает редактор ячеек
     * @return редактор ячейки
     */
    public TreeTableNodeValueEditor getNodeEditor(){
        if( nodeEditor!=null ){ return nodeEditor; }
        synchronized(this){
            if( nodeEditor!=null ){ return nodeEditor; }
            nodeEditor = new TreeTableNodeValueEditorDef();
            return nodeEditor;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="inTreeIconRect(e)">
    /**
     * Проверяет что мышь указывает на иконку (плюс/минус) узла дерева
     * @param e событие мыши
     * @return true - мышь наведена на иконку развернуть/свернуть узел дерева
     */
    protected boolean inTreeIconRect( MouseEvent e ){
        if( mouseOver &&
            mouseAtRow>=0 &&
            mouseAtColumn>=0 &&
            (getCellRenderer(mouseAtRow, mouseAtColumn) instanceof TCellNodeRender)
        ){
            Rectangle2D rect = getCellContentLayout(mouseAtRow, mouseAtColumn);
            if( rect!=null ){
                CellContext cc = getNodeRender().getTreeNodeCellRender().getTreeIconContext();

                Rectangle cellRect = getCellRect(mouseAtRow, mouseAtColumn, true);

                Rectangle2D irect = cc!=null ? cc.getBounds() : null;
                if( irect!=null && cellRect!=null ){
                    irect = RectangleFn.move(irect, cellRect.x, cellRect.y);
                }
                boolean treeIcoMatch = irect!=null ? irect.contains(e.getX(), e.getY()) : false;
                return treeIcoMatch;
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="mousePressed(e)">
    @Override
    protected boolean mousePressed(MouseEvent e) {
        boolean captured = super.mousePressed(e);

        Object omod = getModel();
        TreeTableModel ttmodel = null;
        if( omod instanceof TreeTableModel ){
            ttmodel = (TreeTableModel)omod;
        }

        // capture click on tree icon
        if( ttmodel!=null && !captured && inTreeIconRect(e) && e.getButton()==MouseEvent.BUTTON1 ){
            TreeTableNode node = ttmodel.getNodeOf(mouseAtRow);
            if( node!=null ){
                if( node.isExpanded() ){
                    node.collapse();
                }else{
                    node.expand();
                }
                repaint();
                return true;
            }
        }

        // capture click on scroll
        if( getColumnScrollModel().startDrag(e.getX(), e.getY())){
            return true;
        }

        return captured;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="mouseMove(e)">
    @Override
    protected boolean mouseMove(MouseEvent e) {
        boolean captured = super.mouseMove(e);
        return captured;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="mouseDragged(e)">
    @Override
    protected boolean mouseDragged(MouseEvent e) {
        if( e!=null && getColumnScrollModel().isScrollDragged() ){
            ColumnScroll cs = getColumnScrollModel().getScrolledColumn();
            if( cs==null )return super.mouseDragged(e);

            double xcur = e.getX();
            double xstart = getColumnScrollModel().getScrollDragStartX();
            double xdelta = getColumnScrollModel().getScrollXKofStarted()!=0 ?
                (xcur - xstart) / getColumnScrollModel().getScrollXKofStarted() : 0;

            double scrollXNew = getColumnScrollModel().getScrollXStarted() + xdelta;
            if( scrollXNew<0 )scrollXNew = 0;

            double colwidth = getColumnScrollModel().getScrollColumWidthStarted();
            double scrollwidth = getColumnScrollModel().getScrollWidthStarted();
            double scrollXMax = scrollwidth - colwidth;
            //scrollXMax = -1;
            if( scrollXMax>0 && scrollXNew>scrollXMax ) scrollXNew = scrollXMax;

            cs.setScrollX(scrollXNew);

            repaint();
            return true;
        }
        return super.mouseDragged(e);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="mouseReleased(e)">
    @Override
    protected boolean mouseReleased(MouseEvent e) {
        getColumnScrollModel().setScrollDragged(false);
        return super.mouseReleased(e);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="mouseExit(e)">
    @Override
    protected boolean mouseExit(MouseEvent e) {
        getColumnScrollModel().setScrollDragged(false);
        return super.mouseExit(e);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="paint(g)">
    @Override
    public void paint(Graphics g) {
        //nodeRenderMinX = null;
        //nodeRenderMaxX = null;
        getColumnScrollModel().resetNodeRenderBounds();

        super.paint(g);

        getColumnScrollModel().recalcScrollWidths();
        getColumnScrollModel().paintScrollers((Graphics2D)g, this);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="root : TreeTableNodeBasic">
    protected TreeTableNodeBasic root;

    /**
     * Возвращает корень дерева элементов
     * @return корень дерева элементов
     */
    public TreeTableNodeBasic getRoot(){
        synchronized( this ){
            if( root!=null )return root;
            Object old = this.root;

            rootCloseListeners.close();

            this.root = new TreeTableNodeBasic("root");

            if( this.root!=null ){
                rootCloseListeners.append(
                    this.root.addTreeListener( true, rootListeners::fireEvent));

                if( this.root instanceof SetTreeTable ){
                    ((SetTreeTable)this.root).setTreeTable(this);
                }
            }

            this.root.setExpanded(true);

            TreeTableNodeBasic ttnbA = new TreeTableNodeBasic("default node a");
            ttnbA.setExpanded(true);

            TreeTableNodeBasic ttnbB = new TreeTableNodeBasic("B");

            TreeTableNodeBasic ttnbC = new TreeTableNodeBasic("C");
            TreeTableNodeBasic ttnbD = new TreeTableNodeBasic("D");
            TreeTableNodeBasic ttnbE = new TreeTableNodeBasic("E");
            TreeTableNodeBasic ttnbF = new TreeTableNodeBasic("F");
            TreeTableNodeBasic ttnbG = new TreeTableNodeBasic("G");

            this.root.appends(ttnbA,ttnbB);

            ttnbA.appends(ttnbC,ttnbD,ttnbE);
            ttnbE.appends(ttnbF,ttnbG);

            firePropertyChange("root", old, this.root);
            return root;
        }
    }

    /**
     * Указывает корень дерева элементов
     * @param newroot корень дерева
     */
    public void setRoot(TreeTableNodeBasic newroot){
        if (newroot== null) {
            throw new IllegalArgumentException("newroot==null");
        }

        Object old = null;
        synchronized(this){
            rootCloseListeners.close();

            old = this.root;

            if( this.root instanceof CompareAndSetTreeTable ){
                ((CompareAndSetTreeTable)this.root).compareAndSetTreeTable(this, null);
            }

            this.root = newroot;

            if( this.root!=null ){
                rootCloseListeners.append(
                this.root.addTreeListener( true, rootListeners::fireEvent));
                if( this.root instanceof SetTreeTable ){
                    ((SetTreeTable)this.root).setTreeTable(this);
                }
            }

            if( treeTableModel!=null )treeTableModel.setRoot(root);
        }
        firePropertyChange("root", old, this.root);
    }
    //</editor-fold>

    private final Closeables rootCloseListeners = new Closeables();
//    private final TreeEvent.Listener<TreeTableNodeBasic> treeNodeListener = event -> {
//            if( event==null )return;
//            if( event instanceof NodeGetSource ){
//                if( ((NodeGetSource)event).getSource() instanceof TreeTableNodeBasic ||
//                    ((NodeGetSource)event).getSource()==null
//                ){
//                    TreeEvent<TreeTableNodeBasic> ev = event;
//                    rootListeners.fireEvent(ev);
//                }
//            }
//    };

    //<editor-fold defaultstate="collapsed" desc="rootListeners">
    private final
    ListenersHelper<
            TreeEvent.Listener<TreeTableNodeBasic>,
            TreeEvent<TreeTableNodeBasic>
            > rootListeners = new ListenersHelper<>(
        (ls,ev) -> {

                if( ls!=null ){
                    ls.treeEvent(ev);
                }
        }
    );

    /**
     * Проверят надичие подписчки на корень дерева
     * @param listener подписчик
     * @return true подписчка оформлена
     */
    public boolean hasRootListener(TreeEvent.Listener<TreeTableNodeBasic> listener) {
        return rootListeners.hasListener(listener);
    }

    /**
     * Возвращает подписчиков на корневой элемент дерева
     * @return подписчики
     */
    public Set<TreeEvent.Listener<TreeTableNodeBasic>> getRootListeners() {
        return rootListeners.getListeners();
    }

    /**
     * Добавляет подписчика на корневой элемент дерева
     * @param listener подписчик
     * @return отписка от уведомлений
     */
    public AutoCloseable addRootListener(TreeEvent.Listener<TreeTableNodeBasic> listener) {
        return rootListeners.addListener(listener);
    }

    /**
     * Добавляет подписчика на корневой элемент дерева
     * @param listener подписчик
     * @param weakLink true - добавить подписчика как weak ссылку / false - как обычную
     * @return отписка от уведомлений
     */
    public AutoCloseable addRootListener(TreeEvent.Listener<TreeTableNodeBasic> listener, boolean weakLink) {
        return rootListeners.addListener(listener, weakLink);
    }

    /**
     * Удалить подписчика с коревого элемента дерева
     * @param listener подписчик
     */
    public void removeRootListener(TreeEvent.Listener<TreeTableNodeBasic> listener) {
        rootListeners.removeListener(listener);
    }

    /**
     * Удаление всех подписчиков корневого элемента дерева
     */
    public void removeRootAllListeners() {
        rootListeners.removeAllListeners();
    }

    protected void fireRootEvent(TreeEvent<TreeTableNodeBasic> event) {
        rootListeners.fireEvent(event);
    }

    /**
     * Возвращает очередь сообщений коревого элемента
     * @return очередь сообщений
     */
    public Queue<TreeEvent<TreeTableNodeBasic>> getRootEventQueue() {
        return rootListeners.getEventQueue();
    }

    /**
     * Добавляет сообщение в очередь
     * @param ev сообщение/уведомление
     */
    public void addRootEvent(TreeEvent<TreeTableNodeBasic> ev) {
        rootListeners.addEvent(ev);
    }

    /**
     * Рассылка всех сообщений из очереди
     */
    public void fireRootEvents() {
        rootListeners.runEventQueue();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="treeTableModel">
    protected TreeTableModel treeTableModel;

    /**
     * Возвращает модель дерево-таблицы
     * @return модель данных
     */
    public TreeTableModel getTreeTableModel(){
        synchronized(this){
            if( treeTableModel!=null )return treeTableModel;

            treeTableModel = new TreeTableModel();
            treeTableModel.setRoot(getRoot());

            return treeTableModel;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getNodeOf() getRowOf()">
    /**
     * Возвращает узел для указанной строки
     * @param row row - строка
     * @return узел или null
     */
    public TreeTableNode getNodeOf( int row ){
        if( row<0 )return null;
        synchronized(this){
            return getTreeTableModel().getNodeOf(row);
        }
    }

    /**
     * Возвращает индекст строки для указанного узла дерева
     * @param node узел дерева
     * @return индекс строки или -1
     */
    public int getRowOf( TreeTableNode node ){
        if( node==null )return -1;
        synchronized( this ){
            return getTreeTableModel().getRowOf(node);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rootVisible : boolean">
    /**
     * Отображть или нет корневой элемент
     * @return true - отображать корневой элемент / false - отображать его дочерние элементы
     */
    public boolean isRootVisible(){
        synchronized( this ){
            return getTreeTableModel().getDirectModel().isRootVisible();
        }
    }

    /**
     * Отображть или нет корневой элемент
     * @param rootVisible true - отображать корневой элемент / false - отображать его дочерние элементы
     */
    public void setRootVisible( boolean rootVisible ){
        synchronized(this){
            getTreeTableModel().getDirectModel().setRootVisible(rootVisible);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="columnsWidths : int[]">
    /**
     * Возвращает ширину колонок
     * @return ширина колонок
     */
    public int[] getColumnsWidths(){
        TableColumnModel tcm = getColumnModel();
        int cc = tcm.getColumnCount();
        int[] w = new int[cc];
        for( int ci=0; ci<cc; ci++ ){
            TableColumn tc = tcm.getColumn(ci);
            w[ci] = tc.getWidth();
        }
        return w;
    }

    /**
     * Указывает ширину колонок
     * @param pref true - установить предпочитаемую ширину (setPreferredWidth) /
     * false - просто установить (setWidth)
     * @param w ширина колонок
     * @see TableColumn#setPreferredWidth(int)
     * @see TableColumn#setWidth(int)
     */
    public void setColumnsWidths( boolean pref, int[] w ){
        if( w==null )return;

        TableColumnModel tcm = getColumnModel();
        int cc = Math.min(w.length, tcm.getColumnCount() );
        for( int ci=0; ci<cc; ci++ ){
            TableColumn tc = tcm.getColumn(ci);
            if( pref ){
                tc.setPreferredWidth(w[ci]);
            }else{
                tc.setWidth(w[ci]);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getDataTreeColumns()">
    /**
     * Возвращает колонки модели
     * @return колонки модели
     */
    public Columns getDataTreeColumns(){
        //return getDirectModel().getColumns();
        return getTreeTableModel().getColumns();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getDataTreeColumnAtX( x )">
    /**
     * Возвращает колонку модели соответ координате
     * @param x горизонтальная координата
     * @return колонка или null
     */
    public Column getDataTreeColumnAtX( int x ){
        int ci = getColumnModel().getColumnIndexAtX(x);
        if( ci<0 )return null;

        TableColumn tc = getColumnModel().getColumn(ci);
        if( tc==null )return null;

        int mi = tc.getModelIndex();
        if( mi<0 )return null;

        int cc = getDataTreeColumns().size();
        if( mi>=cc )return null;

        return getDataTreeColumns().get(mi);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getNodeAt( x, y )">
    /**
     * Возвращает узел дерева соответ координате
     * @param x координаты
     * @param y координаты
     * @return Узел дерева или null
     */
    public TreeTableNode getNodeAt( int x, int y ){
        int row = this.rowAtPoint(new Point(x, y));
        if( row<0 )return null;

        TreeTableNode ttn = getNodeOf(row);
        return ttn;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="nodeColumnName : String">
    /**
     * Возвращает имя колоноки TreeTableNodeColumn из модели данных
     * @return имя колонки
     */
    public String getNodeColumnName(){
        for( Column col : getDataTreeColumns() ){
            if( col==null )continue;
            if( col instanceof TreeTableNodeColumn ){
                TreeTableNodeColumn ttnc = (TreeTableNodeColumn)col;
                return ttnc.getName();
            }
        }
        return null;
    }

    /**
     * Указывает имя колоноки TreeTableNodeColumn из модели данных
     * @param name имя колонки
     */
    public void setNodeColumnName( String name ){
        if (name== null) {
            throw new IllegalArgumentException("name==null");
        }
        for( Column col : getDataTreeColumns() ){
            if( col==null )continue;
            if( col instanceof TreeTableNodeColumn ){
                TreeTableNodeColumn ttnc = (TreeTableNodeColumn)col;
                ttnc.setName(name);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="nodeColumnHeaderValue : Object">
    /**
     * Возвращает отображаемое значение в заголовке колонки TreeTableNodeColumn
     * @return отображаемый заголовок колонки TreeTableNodeColumn
     */
    public Object getNodeColumnHeaderValue(){
        int cc = getColumnModel().getColumnCount();
        for( int ci=0; ci<cc; ci++ ){
            TableColumn tc = getColumnModel().getColumn(ci);
            if( tc==null )continue;

            int mi = tc.getModelIndex();
            if( mi<0 || mi >= getDataTreeColumns().size() )continue;

            Column dc = getDataTreeColumns().get(mi);
            if( dc instanceof TreeTableNodeColumn ){
                return tc.getHeaderValue();
            }
        }
        return null;
    }

    /**
     * Указывает отображаемое значение в заголовке колонки TreeTableNodeColumn
     * @param value отображаемый заголовок колонки TreeTableNodeColumn
     */
    public void setNodeColumnHeaderValue( Object value ){
        int cc = getColumnModel().getColumnCount();
        for( int ci=0; ci<cc; ci++ ){
            TableColumn tc = getColumnModel().getColumn(ci);
            if( tc==null )continue;

            int mi = tc.getModelIndex();
            if( mi<0 || mi >= getDataTreeColumns().size() )continue;

            Column dc = getDataTreeColumns().get(mi);
            if( dc instanceof TreeTableNodeColumn ){
                tc.setHeaderValue(value);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="toggleNodeFolding">
    protected KeyStroke toggleNodeFolding;

    /**
     * Возвращает комбинацию клавиш соответ сворачиванию/разворачиванию узла дерева.
     * @return Комбинация клавиш, по умолчанию кнопка + на доп клавиатуре
     */
    public KeyStroke getToggleNodeFolding() {
        if( toggleNodeFolding!=null ){
            return toggleNodeFolding;
        }
        toggleNodeFolding = KeyStroke.getKeyStroke("pressed ADD");
        return toggleNodeFolding;
    }

    /**
     * Указывает комбинацию клавиш соответ сворачиванию/разворачиванию узла дерева.
     * @param toggleNodeFolding Комбинация клавиш
     */
    public void setToggleNodeFolding(KeyStroke toggleNodeFolding) {
        this.toggleNodeFolding = toggleNodeFolding;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getSelectedNodes()">
    /**
     * Возвращает выбранные узлы
     * @return выбранные узлы или пустой список
     */
    public List<TreeTableNode> getSelectedNodes(){
        List<TreeTableNode> lnodes = new ArrayList<>();
        for( int row : getSelectedRows() ){
            TreeTableNode ttn = getNodeOf(row);
            if( ttn!=null ){
                lnodes.add(ttn);
            }
        }
        return lnodes;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setSelectedRows() - Устанавливает выбранные строки">
    /**
     * Устанавливает выбранные строки
     * @param rows строки таблицы
     */
    public void setSelectedRows(int[] rows){
        getSelectionModel().clearSelection();
        for( int row : rows ){
            if( row<0 || row>=getRowCount() )continue;
            getSelectionModel().addSelectionInterval(row, row);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setSelectedNodes()">
    /**
     * Устанавливает выбранные узлы
     * @param nodes узлы
     * @return выбранные узлы
     */
    public List<TreeTableNode> setSelectedNodes( Iterable<TreeTableNode> nodes ){
        List<TreeTableNode> selected = new ArrayList<>();

        getSelectionModel().clearSelection();
        if( nodes==null ){
            return selected;
        }

        for( TreeTableNode ttnode : nodes ){
            if( ttnode==null )continue;
            int row = getRowOf(ttnode);
            // int row = getSortModel().getRowOf(ttnode);
            if( row>=0 && row<getRowCount() ){
                getSelectionModel().addSelectionInterval(row, row);
                selected.add(ttnode);
            }
        }

        return selected;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="focusedNode">
    /**
     * Возвращает узел содержащий фокус ввода
     * @return узел дерева или null
     */
    public TreeTableNode getFocusedNode(){
        int row = getFocusedRow();
        if( row<0 )return null;
        return getNodeOf(row);
    }

    /**
     * Указывает узел содержащий фокус ввода
     * @param node узел дерева или null
     */
    public void setFocusedNode( TreeTableNode node ){
        getSelectionModel().clearSelection();
        if( node==null ){
            return;
        }
        int row = getRowOf(node);
        if( row>=0 ){
            setFocusedCell(row, 0);
            //setFo
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setFocusedCell(row,col)">
    /**
     * Установка фокуса на определенную ячейку таблицы
     * @param row строка
     * @param col ячейка
     */
    public void setFocusedCell(int row,int col){
        // System.out.println("setFocusedCell("+row+","+col+")");

        int[] selrows = getSelectedRows();

        boolean addRowSel = true;
        for( int selrow : selrows ){
            if( selrow==row ){
                addRowSel = false;
                // System.out.println("is selected row "+selrow);
            }
        }

        boolean addColSel = true;
        int[] selcols = getColumnModel().getSelectedColumns();
        for( int selcol : selcols ){
            if( selcol==col ){
                addColSel = false;
                // System.out.println("is selected col "+selcol);
            }
        }

        if( addRowSel ){
            getSelectionModel().addSelectionInterval(row, row);
            // System.out.println("addSelectionInterval("+row+","+row+")");
        }

        if( addColSel ){
            getColumnModel().getSelectionModel().addSelectionInterval(col, col);
            // System.out.println("column addSelectionInterval("+row+","+row+")");
        }

        getSelectionModel().setLeadSelectionIndex(row);
        getColumnModel().getSelectionModel().setLeadSelectionIndex(col);
    }
    //</editor-fold>

    @Override
    protected void processKeyEvent(KeyEvent e) {
        TreeTableNode node = getFocusedNode();
        if( node!=null && GuiUtil.match(e, getToggleNodeFolding())){
            if( node.isExpanded() ){
                node.collapse();
            }else{
                node.expand();
            }
            repaint();
            return;
        }
        super.processKeyEvent(e);
    }

    /**
     * Добавляет обработчик срабатываемый при смене фокуса
     * @param reciver обработчик принимающий сфокусированный узел дерева
     * @return Отписка от уведомлений
     */
    public AutoCloseable onFocusedNodeChanged( final Consumer<TreeTableNode> reciver ){
        if( reciver==null )throw new IllegalArgumentException("reciver == null");
        return onFocusedRowChanged(rowEvent -> {
                int row = rowEvent.getCurrentRow();
                TreeTableNode node = getNodeOf(row);
                reciver.accept(node);
        });
    }
}
