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
package xyz.cofe.gui.swing.ptable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import xyz.cofe.collection.EventList;
import xyz.cofe.ecolls.Closeables;
import xyz.cofe.fn.Fn2;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.fn.Pair;
import xyz.cofe.gui.swing.al.BasicAction;
import xyz.cofe.gui.swing.cell.TCRenderer;
import xyz.cofe.gui.swing.properties.Icons;
import xyz.cofe.gui.swing.properties.PropertyDB;
import xyz.cofe.gui.swing.properties.PropertyValue;
import xyz.cofe.gui.swing.ptable.de.CSVExchanger;
import xyz.cofe.gui.swing.ptable.de.PropertyTableExchanger;
import xyz.cofe.gui.swing.table.*;
import xyz.cofe.gui.swing.tmodel.*;
import xyz.cofe.gui.swing.tree.TreeTableNodeFormat;
import xyz.cofe.gui.swing.tree.TreeTableNodeFormatBasic;
import xyz.cofe.gui.swing.tree.TreeTableNodeValueEditor;
import xyz.cofe.gui.swing.tree.TreeTableNodeValueEditorDef;

/**
 * Таблица свойств объектов
 * @author user
 */
public class PropertyTable
    extends javax.swing.JPanel
{
    //<editor-fold defaultstate="collapsed" desc="Listeners">
    protected final ListenersHelper<PropertyTableListener, PropertyTableEvent> propertyTableListeners
        = new ListenersHelper<PropertyTableListener,PropertyTableEvent>( //(ls,ev) ->
        (ls, ev) -> {
                {
                    ls.propertyTableEvent(ev);
                }
            }
    );

    public boolean hasPropertyTableListener(PropertyTableListener listener) {
        return propertyTableListeners.hasListener(listener);
    }

    public Set<PropertyTableListener> getPropertyTableListeners() {
        return propertyTableListeners.getListeners();
    }

    public AutoCloseable addPropertyTableListener(PropertyTableListener listener) {
        return propertyTableListeners.addListener(listener);
    }

    public AutoCloseable addPropertyTableListener(PropertyTableListener listener, boolean weakLink) {
        return propertyTableListeners.addListener(listener, weakLink);
    }

    public void removePropertyTableListener(PropertyTableListener listener) {
        propertyTableListeners.removeListener(listener);
    }

    public void firePropertyTableEvent(PropertyTableEvent event) {
        propertyTableListeners.fireEvent(event);
    }
    //</editor-fold>

    /**
     * Creates new form PropertyTable
     */
    public PropertyTable() {
        initComponents();

        //final PropertyTable self = this;

        setLayout(new BorderLayout());
        add( getTableScroll(), BorderLayout.CENTER );
        add( getToolBar(), BorderLayout.NORTH );

        setNotifyInAwtThread(true);
        setAwtInvokeAndWait(false);
        setAutoCreateTableColumn(true);

        revalidate();

        focusTracker = new TableFocusTracker(getTable(), 250, true);
        focusTracker.setRowChanged(//(lastRow, currentRow) -> {
            (lastRow, currentRow) -> {

                    Object lastObj = getByRow(lastRow);
                    Object currObj = getByRow(currentRow);
                    firePropertyChange("focused", lastObj, currObj);
                    return null;
                });

        addPropertyChangeListener("defaultItemBuilder",
            new PropertyChangeListener() { @Override public void propertyChange(PropertyChangeEvent evt) {
                checkCreateNewEnable();
            }});

        addPropertyChangeListener("allowInsert",
            new PropertyChangeListener() { @Override public void propertyChange(PropertyChangeEvent evt) {
                checkCreateNewEnable();
            }});

        addPropertyChangeListener("allowDelete",
            new PropertyChangeListener() { @Override public void propertyChange(PropertyChangeEvent evt) {
                checkDeleteEnable();
            }});

        addPropertyChangeListener(
            "allowPaste",
            new PropertyChangeListener() { @Override public void propertyChange(PropertyChangeEvent evt) {
                checkPasteEnable();
            }});

        addPropertyChangeListener( "allowCopy",
            new PropertyChangeListener() { @Override public void propertyChange(PropertyChangeEvent evt) {
                checkCopyEnable();
            }});

        addPropertyChangeListener("inOperator",
            new PropertyChangeListener() { @Override public void propertyChange(PropertyChangeEvent evt) {
                refresh();
            }} );

        checkCreateNewEnable();
        checkRefreshEnable();
        checkDeleteEnable();

        //<editor-fold defaultstate="collapsed" desc="init table header">
        JTableHeader tableHeader = getTable().getTableHeader();
        tableHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int tci = getTable().getColumnModel().getColumnIndexAtX(e.getX());
                if( tci<0 )return;

                TableColumn tc = getTable().getColumnModel().getColumn(tci);
                if( tc==null )return;

                int mi = tc.getModelIndex();
                if( mi<0 || !(mi<getColumns().size()) )return;

                onHeaderColumnClicked( e, tc, mi );
            }
        });

        TableCellRenderer tcr = tableHeader.getDefaultRenderer();
        headerRender = new CellHeaderRenderer(tcr){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                setIcon(null);

                Component cmpt = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                Pair<Integer,Boolean>[] columnSort = PropertyTable.this.columnSort;

                if( arrowUpIcon!=null && arrowDownIcon!=null && columnSort!=null ){
                    for( Pair<Integer,Boolean> p : columnSort ){
                        if( p.a()!=null && p.a().equals(column) ){
                            if( p.b()!=null && p.b() ){
                                setIcon(arrowDownIcon);
                            }else{
                                setIcon(arrowUpIcon);
                            }
                        }
                    }
                }

                return cmpt;
            }
        };

        tableHeader.setDefaultRenderer(headerRender);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="listener add/remove PropertyColumn">
        getColumns().onChanged((idx,oldcol,curcol) -> {
            if( oldcol instanceof PropertyColumn ){
                ((PropertyColumn)oldcol).removeListener(propertyColumnListener);
            }
            if( curcol instanceof PropertyColumn ){
                ((PropertyColumn)curcol).addListener(propertyColumnListener,true);
            }
        });
        //</editor-fold>
    }

    protected final PropertyColumn.Listener propertyColumnListener = new PropertyColumn.Listener() {
        @Override
        public void propertyColumnEvent(PropertyColumn.Event ev) {
            if( ev instanceof PropertyColumn.PropertyWrited ){
                firePropertyTableEvent(
                    new PropertyTableEvent.PropertyWrited(
                        PropertyTable.this, (PropertyColumn.PropertyWrited)ev));
            }
        }
    };

    //<editor-fold defaultstate="collapsed" desc="header onHeaderColumnClicked">
    private TableCellRenderer headerRender;

    public TableCellRenderer getHeaderRender(){ return headerRender; }

    private void onHeaderColumnClicked( MouseEvent e, TableColumn tc, int modelIndex ){
        if( e.getButton()==MouseEvent.BUTTON1 ){
            updateRowComparator(e, tc, modelIndex);
        }
    }
    //</editor-fold>

    private TableFocusTracker focusTracker;

    private ImageIcon arrowUpIcon = readIcon("/xyz/cofe/gui/swing/table/arrow-up.png");
    private ImageIcon arrowDownIcon = readIcon("/xyz/cofe/gui/swing/table/arrow-down.png");

    protected final Closeables csColumns = new Closeables();

    //<editor-fold defaultstate="collapsed" desc="pdb">
    protected PropertyDB pdb;
    public synchronized PropertyDB getPropertyDB(){
        if( pdb!=null )return pdb;
        pdb = new PropertyDB();
        return pdb;
    }
    public synchronized void setPropertyDB(PropertyDB newPdb){
        this.pdb = newPdb;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ui elements">
    //<editor-fold defaultstate="collapsed" desc="toolbar">
    protected volatile JToolBar toolbar;
    public JToolBar getToolBar(){
        if( toolbar!=null )return toolbar;
        synchronized(this){
            if( toolbar!=null )return toolbar;
            toolbar = new JToolBar();

            refreshButton = toolbar.add(getRefreshAction());

            copyToClipboardButton = toolbar.add(getCopyToClipboardAction());
            pasteFromClipboardButton = toolbar.add(getPasteFromClipboardAction());

            createNewButton = toolbar.add(getCreateNewAction());

            toolbar.add(new JSeparator());
            /*toolbar.add(
                new javax.swing.Box.Filler(
                    new java.awt.Dimension(0, 0),
                    new java.awt.Dimension(50, 0),
                    new java.awt.Dimension(32767, 32767)
                )
            );*/
            deleteButton = toolbar.add(getDeleteSelectedAction());

            toolbar.setFloatable(false);
            toolbar.addPropertyChangeListener( "visible",
                new PropertyChangeListener() { @Override public void propertyChange(PropertyChangeEvent evt) {
                    revalidate();
                }});

            toolbar.addContainerListener(new ContainerListener() {
                @Override
                public void componentAdded(ContainerEvent e) {
                    Component cmpt = e.getChild();
                }

                @Override
                public void componentRemoved(ContainerEvent e) {
                    Component cmpt = e.getChild();
                }
            });
            return toolbar;
        }
    }

    public boolean getToolBarVisible(){ return getToolBar().isVisible(); }
    public void setToolBarVisible(boolean v){
        getToolBar().setVisible(v);
        revalidate();
        repaint();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="table">
    protected volatile JTable table;
    public JTable getTable(){
        if( table!=null )return table;
        synchronized(this){
            if( table!=null )return table;
            table = new Table();
            table.setAutoCreateRowSorter(false);
            table.setAutoCreateColumnsFromModel(false);
            table.setModel(getSortRowTM());
            table.setDefaultEditor(PropertyValue.class, getPropertyEditor());
            table.setDefaultRenderer(PropertyValue.class, getPropertyRender());
            table.setBackground(Color.white);
            table.setFillsViewportHeight(true);
            //table.setOpaque(false);
            table.setShowVerticalLines(true);
            return table;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="tableScroll">
    protected volatile JScrollPane tableScroll;
    public JScrollPane getTableScroll(){
        if( tableScroll!=null )return tableScroll;
        synchronized(this){
            if( tableScroll!=null )return tableScroll;
            tableScroll = new JScrollPane(getTable());
            //tableScroll.setBackground(Color.white);
            //tableScroll.setOpaque(true);

            return tableScroll;
        }
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="nullValueFormat">
    //<editor-fold defaultstate="collapsed" desc="nullIcon">
    protected Icon nullIcon = null;
    protected Icon getNullIcon(){
        if( nullIcon!=null )return nullIcon;

        nullIcon = Icons.getNullIcon();
        return nullIcon;
    }
    protected void setNullIcon( Icon ico ){
        nullIcon = ico;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="get/set nullValueFormat">
    protected volatile TreeTableNodeFormat nullValueFormat;

    public TreeTableNodeFormat getNullValueFormat() {
        TreeTableNodeFormat fmt = nullValueFormat;
        if( fmt!=null )return fmt;

        synchronized( this ){
            fmt = nullValueFormat;
            if( fmt!=null )return fmt;

            fmt = new TreeTableNodeFormatBasic();
            fmt.setForeground(Color.gray);
            fmt.setItalic(true);
            fmt.setBold(true);

            Icon ico = getNullIcon();
            if( ico!=null ){
                fmt.getIcons().add(ico);
            }

            nullValueFormat = fmt;
            return nullValueFormat;
        }
    }

    public void setNullValueFormat(TreeTableNodeFormat nullValueFormat) {
        this.nullValueFormat = nullValueFormat;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="awt refresh">
    //<editor-fold defaultstate="collapsed" desc="notifyInAwtThread : boolean">
    public boolean isNotifyInAwtThread(){
        return getCachedTM().getEventSupport().isNotifyInAwtThread();
    }
    public void setNotifyInAwtThread(boolean v){
        getCachedTM().getEventSupport().setNotifyInAwtThread(v);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="awtInvokeAndWait : boolean">
    public boolean isAwtInvokeAndWait(){
        return getCachedTM().getEventSupport().isAwtInvokeAndWait();
    }
    public void setAwtInvokeAndWait(boolean v){
        getCachedTM().getEventSupport().setAwtInvokeAndWait(v);
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="data">
    //<editor-fold defaultstate="collapsed" desc="source : Iterable">
    public Iterable getSource(){
        return getCachedTM().getSource();
    }
    public void setSource( Iterable src ){
        getCachedTM().setSource(src);
        checkRefreshEnable();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cache : IndexEventList">
    public EventList getCache(){
        return getCachedTM().getList();
    }
    //</editor-fold>
    //</editor-fold>

    /**
     * Описание отображения клонок
     */
    public static class PTableColumnDesc implements Serializable
    {
        /**
         * Индекс в модели
         */
        public int modelIndex = -1;

        /**
         * Индекс в таблице
         */
        public int index = -1;

        /**
         * Ширина колонки
         */
        public int width;
        public int minWidth;
        public int maxWidth;
        public int preferredWidth;
        public String modelName;
        public boolean resizable;

        /**
         * Создает описание отображения колонок
         * @param pt Таблица
         * @param ci Индекс колонки
         * @param tc Представление колонки
         * @return описание
         */
        public static PTableColumnDesc create( PropertyTable pt, int ci, TableColumn tc ){
            if (pt== null) {
                throw new IllegalArgumentException("pt==null");
            }
            if (tc== null) {
                throw new IllegalArgumentException("tc==null");
            }

            PTableColumnDesc tcd = new PTableColumnDesc();
            tcd.index = ci;
            tcd.width = tc.getWidth();
            tcd.minWidth = tc.getMinWidth();
            tcd.maxWidth = tc.getMaxWidth();
            tcd.preferredWidth = tc.getPreferredWidth();
            tcd.modelIndex = tc.getModelIndex();
            if( tcd.modelIndex>=0 && tcd.modelIndex < pt.getColumns().size() ){
                Column col = pt.getColumns().get(tcd.modelIndex);
                if( col!=null ){
                    tcd.modelName = col.getName();
                }
            }
            tcd.resizable = tc.getResizable();

            return tcd;
        }

        /**
         * Применение сохраненых значений на колонку
         * @param pt Таблица
         * @param tc Колонка
         */
        public void widthWrite( PropertyTable pt, TableColumn tc ){
            if (pt== null) {
                throw new IllegalArgumentException("pt==null");
            }
            if (tc== null) {
                throw new IllegalArgumentException("tc==null");
            }

            tc.setWidth(width);
            //tc.setPreferredWidth(preferredWidth);
            tc.setPreferredWidth(width);
            tc.setResizable(resizable);
            tc.setMinWidth(minWidth);
            tc.setMaxWidth(maxWidth);
        }

        /**
         * Проверка на совпадение
         * @param pt Таблица
         * @param ci Индекс в таблице
         * @param tc Колонка таблицы
         * @return true - есть совпадение
         */
        public boolean match( PropertyTable pt, int ci, TableColumn tc ){
            if (pt== null) {
                throw new IllegalArgumentException("pt==null");
            }
            if (tc== null) {
                throw new IllegalArgumentException("tc==null");
            }

            boolean modelNameMatched = false;
            if( modelName!=null ){
                int mi = tc.getModelIndex();
                if( mi>=0 && mi<pt.getColumns().size() ){
                    Column col = pt.getColumns().get( mi );
                    if( col!=null ){
                        modelNameMatched = modelName.equals(col.getName());
                    }
                }
            }else{
                modelNameMatched = true;
            }

            boolean idxMatched = index == ci;

            return idxMatched && modelNameMatched;
        }

        /**
         * Применяет сохраненные значения ширины колонок
         * @param pt Таблица
         * @param columnsDesc Описания ширины
         */
        public static void applyWidth( PropertyTable pt, Iterable<PTableColumnDesc> columnsDesc ){
            if (pt== null) {
                throw new IllegalArgumentException("pt==null");
            }
            if (columnsDesc== null) {
                throw new IllegalArgumentException("columnsDesc==null");
            }

            TableColumnModel tcm = pt.getTable().getColumnModel();
            for( int ci=0; ci<tcm.getColumnCount(); ci++ ){
                TableColumn tc = tcm.getColumn(ci);
                for( PTableColumnDesc cdesc : columnsDesc ){
                    if( cdesc==null )continue;
                    if( cdesc.match(pt, ci, tc) ){
                        cdesc.widthWrite(pt, tc);
                    }
                }
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="columns">
    public List<PTableColumnDesc> getColumnsDesc(){
        ArrayList<PTableColumnDesc> cols = new ArrayList<>();

        JTable tbl = getTable();

        TableColumnModel tcm = tbl.getColumnModel();
        for( int ci=0; ci<tcm.getColumnCount(); ci++ ){
            TableColumn tc = tcm.getColumn(ci);
            cols.add( PTableColumnDesc.create(this, ci, tc) );
        }

        return cols;
    }

    //<editor-fold defaultstate="collapsed" desc="columns : Columns">
    public Columns getColumns(){
        return getCachedTM().getColumns();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="onColumn Inserted/Updated/Deleted">
    protected void onColumnInserted( int colIdx, Column col ){
        if( !isAutoCreateTableColumn() )return;

        final TableColumn tc = new TableColumn(colIdx);
        tc.setHeaderValue(col.getName());

        if( headerRender!=null ){
            tc.setHeaderRenderer(headerRender);
        }

        if( col instanceof PropertyColumn ){
            tc.setCellRenderer(getPropertyRender());
            tc.setCellEditor(getPropertyEditor());
        }

        Runnable run = new Runnable() {
            @Override
            public void run() {
                getTable().addColumn(tc);
            }};

        if( isNotifyInAwtThread() && !SwingUtilities.isEventDispatchThread() ){
            if( isAwtInvokeAndWait() ){
                try {
                    SwingUtilities.invokeAndWait(run);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PropertyTable.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(PropertyTable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                SwingUtilities.invokeLater(run);
            }
        }else{
            run.run();
        }
    }

    protected void onColumnDeleted( int colIdx, Column col ){
        if( !isAutoCreateTableColumn() )return;

        final LinkedHashSet<TableColumn> cols = new LinkedHashSet<TableColumn>();

        TableColumnModel tcm = getTable().getColumnModel();
        int colCount = tcm.getColumnCount();
        for( int tci=0; tci<colCount; tci++ ){
            TableColumn tc = tcm.getColumn(tci);
            if( tc==null )continue;
            if( tc.getModelIndex()==colIdx ){
                cols.add(tc);
            }
        }

        Runnable run = new Runnable() {
            @Override
            public void run() {
                for( TableColumn tc : cols ){
                    getTable().removeColumn(tc);
                }
            }};

        if( isNotifyInAwtThread() && !SwingUtilities.isEventDispatchThread() ){
            if( isAwtInvokeAndWait() ){
                try {
                    SwingUtilities.invokeAndWait(run);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PropertyTable.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(PropertyTable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                SwingUtilities.invokeLater(run);
            }
        }else{
            run.run();
        }
    }

    protected void onColumnUpdated( int colIdx, Column oldcol, Column newcol ){
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="autoCreateTableColumn">
    protected boolean autoCreateTableColumn = true;

    public boolean isAutoCreateTableColumn() {
        return autoCreateTableColumn;
    }

    public void setAutoCreateTableColumn(boolean autoCreateTableColumn) {
        this.autoCreateTableColumn = autoCreateTableColumn;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="render / editor">
    //<editor-fold defaultstate="collapsed" desc="propertyRender">
    protected volatile TableCellRenderer propertyRender;
    public TableCellRenderer getPropertyRender(){
        if( propertyRender!=null )return propertyRender;
        synchronized(this){
            if( propertyRender!=null )return propertyRender;
            //propertyRender = new TreeTableNodeRender();
            //propertyRender.setLastRowBorder(new LineBorder().bottom(1, 0, 1, Color.black));
            propertyRender = new TCRenderer();
            return propertyRender;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="propertyEditor">
    protected volatile TreeTableNodeValueEditor propertyEditor;
    public TreeTableNodeValueEditor getPropertyEditor(){
        if( propertyEditor!=null )return propertyEditor;
        synchronized(this){
            if( propertyEditor!=null )return propertyEditor;
            propertyEditor = new TreeTableNodeValueEditorDef();
            return propertyEditor;
        }
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="inOperator">
    protected Fn2<Object,Collection,Boolean> inOperator = null;

    public Fn2<Object, Collection,Boolean> getInOperator() {
        synchronized(this){
            return inOperator;
        }
    }

    public void setInOperator(Fn2<Object, Collection,Boolean> inOperator) {
        Object old = this.inOperator;
        synchronized(this){
            old = this.inOperator;
            this.inOperator = inOperator;
        }
        firePropertyChange("inOperator", old, inOperator);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="table models">
    //<editor-fold defaultstate="collapsed" desc="cachedTM">
    protected volatile CachedTM cachedTM;
    public CachedTM getCachedTM(){
        if( cachedTM!=null )return cachedTM;
        synchronized(this){
            if( cachedTM!=null )return cachedTM;
            cachedTM = new CachedTM(){
                @Override
                protected boolean contains(Collection col, Object obj) {
                    Fn2<Object,Collection,Boolean> inOperator = PropertyTable.this.inOperator;
                    if( inOperator!=null ){
                        Boolean v = inOperator.apply(obj, col);
                        if( v!=null ){
                            return (boolean)v;
                        }
                    }
                    return super.contains(col, obj);
                }
            };

            cachedTM.getColumns().onChanged(
                (Integer cidx, Column oldCol, Column newCol) -> {
                        if( oldCol!=null && newCol!=null ){
                            onColumnUpdated(cidx, oldCol, newCol);
                        }else if( oldCol==null && newCol!=null ){
                            onColumnInserted(cidx, newCol);
                        }else if( oldCol!=null && newCol==null ){
                            onColumnDeleted(cidx, oldCol);
                        }

                        if( newCol instanceof PropertyColumn ){
                            ((PropertyColumn)newCol).setPropertyDB(getPropertyDB());
                            ((PropertyColumn)newCol).setPropertyTable(PropertyTable.this);
                        }
                    }
                );

            return cachedTM;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="filterRowTM">
    protected volatile FilterRowTM filterRowTM;
    public FilterRowTM getFilterRowTM(){
        if( filterRowTM!=null )return filterRowTM;
        synchronized(this){
            if( filterRowTM!=null )return filterRowTM;
            filterRowTM = new FilterRowTM();
            //filterRowTM.setRowFilter( rd -> true );
            filterRowTM.setRowFilter( (RowData value) -> true );
            filterRowTM.setTableModel(getCachedTM());
            return filterRowTM;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="sortRowTM">
    protected volatile SortRowTM sortRowTM;
    public SortRowTM getSortRowTM(){
        if( sortRowTM!=null )return sortRowTM;
        synchronized(this){
            if( sortRowTM!=null )return sortRowTM;
            sortRowTM = new SortRowTM();
            sortRowTM.setRowComparator( defaultComparator );
            sortRowTM.setTableModel(getFilterRowTM());
            return sortRowTM;
        }
    }
    //</editor-fold>

    private static boolean eq( Object a, Object b ){
        if( a==null && b==null )return true;
        if( a==null && b!=null )return false;
        if( a!=null && b==null )return false;
        return a.equals(b);
    }

    //<editor-fold defaultstate="collapsed" desc="defaultComparator">
    private Comparator<RowData> defaultComparator = //(RowData rd1, RowData rd2) -> {
        new Comparator<RowData>() {
            @Override
            public int compare(RowData rd1, RowData rd2) {
                int listRI1 = -1;
                int listRI2 = -1;

                if( eq(rd1.getTableModel(), getSortRowTM()) )
                {
                    listRI1 = getSortRowTM().mapRowToInside(rd1.getRowIndex());
                    listRI1 = getFilterRowTM().mapRowToInside(listRI1);
                }else if( eq(rd1.getTableModel(), getFilterRowTM()) ){
                    listRI1 = getFilterRowTM().mapRowToInside(rd1.getRowIndex());
                }

                if( eq(rd2.getTableModel(), getSortRowTM()) )
                {
                    listRI2 = getSortRowTM().mapRowToInside(rd2.getRowIndex());
                    listRI2 = getFilterRowTM().mapRowToInside(listRI2);
                }else if( eq(rd2.getTableModel(), getFilterRowTM()) ){
                    listRI2 = getFilterRowTM().mapRowToInside(rd2.getRowIndex());
                }

                if( listRI2>=0 && listRI1>=0 ){
                    int res = listRI1==listRI2 ? 0 : (listRI1 < listRI2 ? -1 : 1);
                    return res;
                }

                return 0;
            }};
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="createRowComparator()">
    private Pair<Integer,Boolean>[] columnSort = new Pair[]{};

    private boolean isColumnInSort( Pair<Integer,Boolean>[] columnSort,  int modelIndex ){
        if( columnSort==null || columnSort.length<1 )return false;
        for( Pair<Integer,Boolean> p : columnSort ){
            if( p==null )continue;
            if( p.a()==null )continue;
            if( modelIndex==p.a() )return true;
        }
        return false;
    }

    private boolean isColumnReverseSort( Pair<Integer,Boolean>[] columnSort, int modelIndex ){
        if( columnSort==null || columnSort.length<1 )return false;
        for( Pair<Integer,Boolean> p : columnSort ){
            if( p==null )continue;
            if( p.a()==null )continue;
            if( modelIndex==p.a() ){
                return p.b();
            }
        }
        return false;
    }

    private Pair<Integer,Boolean>[] updateColumnSort(
        Pair<Integer,Boolean>[] columnSort,
        int modelIndex,
        boolean reverse )
    {
        if( columnSort==null )return null;
        columnSort = Arrays.copyOf(columnSort, columnSort.length);
        for( int ci=0; ci<columnSort.length; ci++ ){
            if( columnSort[ci]==null )continue;
            if( columnSort[ci].a()!=null && modelIndex==columnSort[ci].a() ){
                Pair<Integer,Boolean> p = Pair.of( modelIndex, reverse );
                columnSort[ci] = p;
            }
        }
        return columnSort;
    }

    private Pair<Integer,Boolean>[] appendColumnSort(
        Pair<Integer,Boolean>[] columnSort,
        int modelIndex,
        boolean reverse )
    {
        if( columnSort==null )columnSort = new Pair[]{};
        columnSort = Arrays.copyOf(columnSort, columnSort.length+1);
        columnSort[columnSort.length-1] = Pair.of( modelIndex, reverse );
        return columnSort;
    }

    private void updateRowComparator( MouseEvent e, TableColumn tc, int modelIndex ){
        if( e.isControlDown() && e.isShiftDown() ){
            resetSort();
            return;
        }

        if( e.isShiftDown()){
            if( isColumnInSort(columnSort, modelIndex) ){
                boolean reverse = isColumnReverseSort(columnSort, modelIndex);

                columnSort = updateColumnSort(columnSort, modelIndex, !isColumnReverseSort(columnSort, modelIndex));
                Comparator<RowData> rcmp = createColumnSort(columnSort);
                if( rcmp!=null ){
                    getSortRowTM().setRowComparator(rcmp);
                }

                return;
            }else{
                columnSort = appendColumnSort(columnSort, modelIndex, false);

                Comparator<RowData> rcmp = createColumnSort(columnSort);
                if( rcmp!=null ){
                    getSortRowTM().setRowComparator(rcmp);
                }

                return;
            }
        }

        if( columnSort==null || columnSort.length<1 ){
            columnSort = new Pair[]{ Pair.of(modelIndex, false) };
            Comparator<RowData> rcmp = createColumnSort(columnSort);
            if( rcmp!=null ){
                getSortRowTM().setRowComparator(rcmp);
            }
        }else{
            int ecol = columnSort[0].a();
            boolean reverse = columnSort[0].b();
            if( ecol==modelIndex ){
                columnSort = new Pair[]{ Pair.of(modelIndex, !reverse) };
                Comparator<RowData> rcmp = createColumnSort(columnSort);
                if( rcmp!=null ){
                    getSortRowTM().setRowComparator(rcmp);
                }
            }else{
                columnSort = new Pair[]{ Pair.of(modelIndex, false) };
                Comparator<RowData> rcmp = createColumnSort(columnSort);
                if( rcmp!=null ){
                    getSortRowTM().setRowComparator(rcmp);
                }
            }
        }

        //getTable().repaint();
    }

    private Comparator<RowData> createColumnSort( Pair<Integer,Boolean>[] columnSort ){
        Comparator<RowData> cmp = null;
        if( columnSort==null || columnSort.length<1 ){
            //cmp = (RowData rd1,RowData rd2) -> 0;
            cmp = new Comparator<RowData>() {
                @Override
                public int compare(RowData o1, RowData o2) {
                    return 0;
                }
            };
            return cmp;
        }

        final List<Comparator<RowData>> comparators = new ArrayList<Comparator<RowData>>();
        for( final Pair<Integer,Boolean> pcmp : columnSort ){
            /*cmp =  (RowData rd1,RowData rd2) -> {
                return (pcmp.B() ? -1 : 1) * compareCells(rd1, rd2, pcmp.A());
            };*/
            cmp =  new Comparator<RowData>() {
                @Override
                public int compare(RowData rd1, RowData rd2) {
                    return (pcmp.b() ? -1 : 1) * compareCells(rd1, rd2, pcmp.a());
                }
            };
            comparators.add(cmp);
        }

        Comparator<RowData>  rcmp = //(RowData rd1,RowData rd2) -> {
            new Comparator<RowData>() {
                @Override
                public int compare(RowData rd1, RowData rd2) {
                    int r = 0;
                    for( Comparator<RowData> c : comparators ){
                        r = c.compare(rd1, rd2);
                        if( r!=0 )return r;
                    }
                    return r;
                }};

        return rcmp;
    }

    private int compareCellValue( Object o1, Object o2 ){
        if( o1==null && o2==null )return 0;
        if( o1!=null && o2==null )return -1;
        if( o1==null && o2!=null )return 1;

        if( o1 instanceof Number && o2 instanceof Number ){
            Double d1 = ((Number)o1).doubleValue();
            Double d2 = ((Number)o2).doubleValue();
            return d1.compareTo(d2);
        }

        if( o1.getClass().isAssignableFrom(o2.getClass()) && o1 instanceof Comparable ){
            int cmp = ((Comparable)o1).compareTo(o2);
            return cmp;
        }else if( o2.getClass().isAssignableFrom(o1.getClass()) && o2 instanceof Comparable ){
            int cmp = -((Comparable)o2).compareTo(o1);
            return cmp;
        }

        return 0;
    }

    private int compareCells( RowData rd1, RowData rd2, int column ){
        int cc1 = rd1.getTableModel().getColumnCount();
        if( column<0 || column>=cc1 )return 0;

        int cc2 = rd2.getTableModel().getColumnCount();
        if( column<0 || column>=cc2 )return 0;

        Object val1 = rd1.getValue(column);
        Object val2 = rd2.getValue(column);

        if( val1 instanceof PropertyValue ){
            val1 = ((PropertyValue)val1).getValue();
        }
        if( val2 instanceof PropertyValue ){
            val2 = ((PropertyValue)val2).getValue();
        }

        return compareCellValue(val1, val2);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="resetSort()">
    public void resetSort(){
        columnSort = new Pair[]{};
        getSortRowTM().setRowComparator( defaultComparator );
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="selected">
    public List getSelected(){
        List selected = new LinkedList();

        int[] rows = getTable().getSelectedRows();
        for( int row : rows ){
            if( row<0 )continue;

            int frow = getSortRowTM().mapRowToInside(row);
            if( frow<0 )continue;

            int crow = getFilterRowTM().mapRowToInside(frow);
            if( crow<0 )continue;

            if( crow<getCachedTM().getList().size() ){
                Object r = getCachedTM().getList().get(crow);
                selected.add( r );
            }
        }

        return selected;
    }

    private boolean selectFirstMatched = false;

    public void setSelected( List selected ){
        setSelectedIterable(selected);
    }

    public void setSelectedIterable( final Iterable selected ){
        Runnable run = new Runnable() {
            @Override
            public void run() {
                getTable().getSelectionModel().clearSelection();
                if( selected==null )return;

                TreeSet<Integer> selListIndexes = new TreeSet<Integer>();

                for( Object sel : selected ){
                    if( Thread.interrupted() )break;

                    if( selectFirstMatched ){
                        int idx = getCache().indexOf(sel);
                        if( idx>=0 ){
                            selListIndexes.add( idx );
                        }
                    }else{
                        for( int li=0; li<getCache().size(); li++ ){
                            Object lio = getCache().get(li);
                            if( eq(sel, lio) ){
                                selListIndexes.add(li);
                            }
                        }
                    }
                }

                for( int selListIdx : selListIndexes ){
                    if( selListIdx<0 )continue;

                    int row = selListIdx;

                    row = getFilterRowTM().mapRowToOutside(row);
                    if( row<0 )continue;

                    row = getSortRowTM().mapRowToOutside(row);
                    if( row<0 )continue;

                    getTable().getSelectionModel().addSelectionInterval(row, row);
                }
            }};

        if( isNotifyInAwtThread() && !SwingUtilities.isEventDispatchThread() ){
            if( isAwtInvokeAndWait() ){
                try {
                    SwingUtilities.invokeAndWait(run);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PropertyTable.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(PropertyTable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                SwingUtilities.invokeLater(run);
            }
        }else{
            run.run();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="mapRow inside/outside">
    public int mapRowToInside( int row ){
        if( row<0 )return -1;

        row = getSortRowTM().mapRowToInside(row);
        if( row<0 )return -1;

        row = getFilterRowTM().mapRowToInside(row);
        if( row<0 )return -1;

        return row;
    }

    public int mapRowToOutside( int row ){
        if( row<0 )return -1;
        if( !(row<getCache().size()) )return -1;

        row = getFilterRowTM().mapRowToOutside(row);
        if( row<0 )return -1;

        row = getSortRowTM().mapRowToOutside(row);
        if( row<0 )return -1;

        return row;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getByRow()">
    public Object getByRow( int idx ){
        return getByRow(idx, null);
    }

    public Object getByRow( int idx, Object def ){
        if( idx<0 )return def;

        idx = mapRowToInside(idx);
        if( idx>=0 ){
            return getCache().get(idx);
        }

        return def;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="focused">
    public Object getFocused(){
        int idx = getTable().getSelectionModel().getLeadSelectionIndex();
        if( idx>=0 )return getByRow(idx);

        return null;
    }

    public void setFocused( final Object val ){
        Runnable run = new Runnable() {
            @Override
            public void run() {
                int row = getCache().indexOf(val);
                if( row<0 )return;

                row = mapRowToOutside(row);
                if( row<0 )return;

                /*if( getTable().getColumnSelectionAllowed() ){*/
                getTable().getColumnModel().getSelectionModel().addSelectionInterval(0, 0);
                getTable().getColumnModel().getSelectionModel().setLeadSelectionIndex(0);
                /*}*/

                getTable().getSelectionModel().addSelectionInterval(row, row);
                getTable().getSelectionModel().setLeadSelectionIndex(row);
            }};

        if( isNotifyInAwtThread() && !SwingUtilities.isEventDispatchThread() ){
            if( isAwtInvokeAndWait() ){
                try {
                    SwingUtilities.invokeAndWait(run);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PropertyTable.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(PropertyTable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                SwingUtilities.invokeLater(run);
            }
        }else{
            run.run();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="defaultItemBuilder">
    protected Supplier<Object> defaultItemBuilder;

    public Supplier<Object> getDefaultItemBuilder() {
        return defaultItemBuilder;
    }

    public void setDefaultItemBuilder(Supplier<Object> defaultItemBuilder) {
        Object old = this.defaultItemBuilder;
        this.defaultItemBuilder = defaultItemBuilder;
        firePropertyChange("defaultItemBuilder", old, defaultItemBuilder);
    }
    //</editor-fold>

    private static ImageIcon readIcon( String resource ){
        if( resource==null )return null;
        URL url = PropertyTable.class.getResource(resource);
        if( url!=null )return new ImageIcon(url);
        return null;
    }

    //<editor-fold defaultstate="collapsed" desc="refresh">
    //<editor-fold defaultstate="collapsed" desc="refresh()">
    public void refresh(){
        getCachedTM().fetch();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="refreshAction">
    protected BasicAction refreshAction
        = new BasicAction("Обновить").
        actionListener(new Runnable() {
            @Override
            public void run() { refresh(); }} ).
        shortDescription("Обновить").
        smallIcon(readIcon("/xyz/cofe/gui/swing/ico/refresh/refresh-icon-16.png"))
        ;

    public BasicAction getRefreshAction(){ return refreshAction; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="refreshButton">
    protected JButton refreshButton;
    public JButton getRefreshButton(){ return refreshButton; }
    public boolean getRefreshVisible(){ return refreshButton.isVisible(); }
    public void setRefreshVisible(boolean v){ refreshButton.setVisible(v); }
    //</editor-fold>

    protected void checkRefreshEnable(){
        Iterable itr = getSource();
        getRefreshAction().setEnabled(itr!=null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="insertEnable">
    protected boolean insertEnable = true;

    public boolean isInsertEnable() {
        return insertEnable;
    }

    public void setInsertEnable(boolean insertEnable) {
        boolean old = this.insertEnable;
        this.insertEnable = insertEnable;
        firePropertyChange("insertEnable", old, this.insertEnable);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="pasteEnable">
    protected boolean pasteEnable = true;

    public boolean isPasteEnable() {
        return pasteEnable;
    }

    public void setPasteEnable(boolean pasteEnable) {
        boolean old = this.pasteEnable;
        this.pasteEnable = pasteEnable;
        firePropertyChange("pasteEnable", old, this.pasteEnable);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="copyEnable">
    protected boolean copyEnable = true;

    public boolean isCopyEnable() {
        return copyEnable;
    }

    public void setCopyEnable(boolean copyEnable) {
        boolean old = this.copyEnable;
        this.copyEnable = copyEnable;
        firePropertyChange("copyEnable", old, this.copyEnable);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="delete selected">
    //<editor-fold defaultstate="collapsed" desc="deleteEnable">
    protected boolean deleteEnable = true;

    public boolean isDeleteEnable() {
        return deleteEnable;
    }

    public void setDeleteEnable(boolean deleteEnable) {
        Object old = this.deleteEnable;
        this.deleteEnable = deleteEnable;
        firePropertyChange("deleteEnable", old, isDeleteEnable());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="deleteAction">
    protected BasicAction deleteAction
        = new BasicAction("Удалить").
        actionListener( new Runnable() {
            @Override
            public void run() {
                deleteSelected(); }} ).
        shortDescription("Удалить элемент").
        smallIcon(readIcon("/xyz/cofe/gui/swing/table/node-minus-v3-12x12.png"))
        ;

    public BasicAction getDeleteSelectedAction(){
        return deleteAction;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="deleteButton">
    protected JButton deleteButton;
    public JButton getDeleteButton(){ return deleteButton; }
    //</editor-fold>

    public boolean getDeleteButtonVisible(){ return deleteButton.isVisible(); }
    public void setDeleteButtonVisible(boolean v){ deleteButton.setVisible(v); }

    //<editor-fold defaultstate="collapsed" desc="deleteSelected()">
    public void deleteSelected(){
        Runnable run = new Runnable() {
            @Override
            public void run() {
                Object[] sel = getSelected().toArray();
                if( sel==null || sel.length<1 )return;

                EventList eCacheList = getCache();

                final LinkedHashMap<Object,Integer> deletedElements = new LinkedHashMap<>();

                Closeables cs = new Closeables();
                cs.add( eCacheList.onDeleted(true, (idx, oldv, curv) -> {
                        if( idx instanceof Number && oldv!=null ){
                            deletedElements.put(oldv, ((Number)idx).intValue());
                        }
                }));

                if( eCacheList!=null ){
                    LinkedHashSet lhs = new LinkedHashSet();
                    for( Object e : sel ){
                        lhs.add(e);
                    }
                    eCacheList.removeAll(lhs);
                }

                cs.close();

                for( Map.Entry<Object,Integer> en : deletedElements.entrySet() ){
                    PropertyTableEvent.ElementCacheRemoved ev = new PropertyTableEvent.ElementCacheRemoved(PropertyTable.this);
                    ev.setCache(eCacheList);
                    ev.setIndex(en.getValue());
                    ev.setElement(en.getKey());

                    //getCachedTM().setSourceListen(false);
                    firePropertyTableEvent(ev);
                    //getCachedTM().setSourceListen(true);
                }
            }};

        if( isNotifyInAwtThread() && !SwingUtilities.isEventDispatchThread() ){
            if( isAwtInvokeAndWait() ){
                try {
                    SwingUtilities.invokeAndWait(run);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PropertyTable.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(PropertyTable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                SwingUtilities.invokeLater(run);
            }
        }else{
            run.run();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="checkDeleteEnable()">
    protected void checkDeleteEnable(){
        getDeleteSelectedAction().setEnabled(isDeleteEnable() );
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="createNew/append">
    protected JButton createNewButton;
    public JButton getCreateNewButton(){ return createNewButton; }
    public boolean getCreateNewVisible(){ return createNewButton.isVisible(); }
    public void setCreateNewVisible(boolean v){ createNewButton.setVisible(v); }

    protected BasicAction createNewAction
        = new BasicAction("Добавить").
        actionListener( new Runnable() {
            @Override
            public void run() {
                createNew(Integer.MAX_VALUE, true); }} ).
        shortDescription("Добавить элемент").
        smallIcon(readIcon("/xyz/cofe/gui/swing/table/node-plus-v3-12x12.png"))
        ;

    protected void checkCreateNewEnable(){
        boolean enable = false;

        if( getDefaultItemBuilder()!=null && isInsertEnable() ){
            enable = true;
        }

        BasicAction act = getCreateNewAction();
        if( act!=null ){
            act.setEnabled(enable);
        }
    }

    public BasicAction getCreateNewAction() {
        return createNewAction;
    }

    public void append( final int pos, final Object obj, final boolean setfocus ){
        if (obj== null) {
            throw new IllegalArgumentException("obj==null");
        }

        Runnable run = new Runnable() {
            @Override
            public void run() {
                int listIdx = -1;
                if( pos<0 ){
                    getCache().add(0,obj);
                    listIdx = 0;
                }else if( pos>=getCache().size() ){
                    getCache().add(obj);
                    listIdx = getCache().indexOf(obj);
                }else{
                    getCache().add(pos,obj);
                    listIdx = getCache().indexOf(obj);
                }

                int row = mapRowToOutside(listIdx);
                if( row<0 )return;

                if( setfocus ){
                    setFocused(obj);

                    Rectangle rect = getTable().getCellRect(row, 0, true);
                    if( rect!=null ){
                        getTable().scrollRectToVisible(rect);
                    }
                }

                PropertyTableEvent.ElementCacheCreated ev = new PropertyTableEvent.ElementCacheCreated(PropertyTable.this);
                ev.setCache(getCache());
                ev.setElement(obj);
                ev.setRow(row);
                ev.setIndex(listIdx);

                firePropertyTableEvent(ev);
            }};

        if( isNotifyInAwtThread() && !SwingUtilities.isEventDispatchThread() ){
            if( isAwtInvokeAndWait() ){
                try {
                    SwingUtilities.invokeAndWait(run);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PropertyTable.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(PropertyTable.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                SwingUtilities.invokeLater(run);
            }
        }else{
            run.run();
        }
    }

    public void createNew( int pos, boolean setfocus){
        Supplier fn = getDefaultItemBuilder();
        if( fn==null ){
            throw new IllegalStateException("defaultItemBuilder not set");
        }

        Object obj = fn.get();
        if( obj==null ){
            throw new IllegalStateException("defaultItemBuilder return null");
        }

        //getCachedTM().setSourceListen(false);
        append(pos, obj, setfocus);
        //getCachedTM().setSourceListen(true);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="csvExchanger">
    private PropertyTableExchanger exchanger;

    public PropertyTableExchanger getExchanger() {
        if( exchanger!=null )return exchanger;
        exchanger = new CSVExchanger();
        return exchanger;
    }

    public void setExchanger(PropertyTableExchanger exchanger) {
        Object old = getExchanger();
        this.exchanger = exchanger;
        firePropertyChange("exchanger", old, getExchanger());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="copy to clipboard">
    public void copyToClipboard(){
        List selected = getSelected();
        if( selected==null || selected.isEmpty() )return;

        PropertyTableExchanger csve = getExchanger();

        StringWriter sw = new StringWriter();
        csve.exportTable(sw, selected, this);

        Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection ss = new StringSelection(sw.toString());
        cp.setContents(ss, null);
    }

    protected BasicAction copyToClipboardAction = new BasicAction("Копировать")
        .actionListener( new Runnable() {
            @Override
            public void run() {
                copyToClipboard(); }})
        .shortDescription("Копировать выделенные объекты")
        .smallIcon(readIcon("/xyz/cofe/gui/swing/ico/copy/copy-16.png"));

    public BasicAction getCopyToClipboardAction(){
        return copyToClipboardAction;
    }

    protected JButton copyToClipboardButton;
    public JButton getCopyToClipboardButton(){ return copyToClipboardButton; }
    public boolean getCopyToClipboardVisible(){ return copyToClipboardButton.isVisible(); }
    public void setCopyToClipboardVisible(boolean v){ copyToClipboardButton.setVisible(v); }

    protected void checkCopyEnable(){
        boolean en = getExchanger()!=null && isCopyEnable();
        getCopyToClipboardAction().setEnabled(en);
    }

    protected JButton pasteFromClipboardButton;
    public JButton getPasteFromClipboardButton(){
        return pasteFromClipboardButton;
    }
    public boolean getPasteFromClipboardVisible(){ return pasteFromClipboardButton.isVisible(); }
    public void setPasteFromClipboardVisible(boolean v){ pasteFromClipboardButton.setVisible(v); }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="paste from clipboard">
    public void pasteFromClipboard(){
        Clipboard clipboard =  Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable trans = clipboard.getContents(null);

        String strClipBoard = null;

        if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                // cast to string
                strClipBoard = (String) trans
                    .getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException e2) {
                e2.printStackTrace();
                return;
            } catch (IOException e2) {
                e2.printStackTrace();
                return;
            }
        }

        if( strClipBoard==null )return;

        PropertyTableExchanger csve = getExchanger();
        if( csve != null ){
            Iterable items = csve.importTable(new StringReader(strClipBoard), this);
            for( Object item : items ){
                if( Thread.interrupted() )break;
                if( item==null )continue;

                getCache().add(item);
            }
        }
    }

    protected BasicAction pasteFromClipboardAction = new BasicAction("Вставить")
        .actionListener( new Runnable() {
            @Override
            public void run() {
                pasteFromClipboard(); }})
        .shortDescription("Вставить объекты")
        .smallIcon(readIcon("/xyz/cofe/gui/swing/ico/paste/paste-16.png"));

    public BasicAction getPasteFromClipboardAction(){
        return pasteFromClipboardAction;
    }

    protected void checkPasteEnable(){
        boolean en = getExchanger()!=null && isPasteEnable();
        getPasteFromClipboardAction().setEnabled(en);
    }
    //</editor-fold>

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>


    // Variables declaration - do not modify
    // End of variables declaration
}
