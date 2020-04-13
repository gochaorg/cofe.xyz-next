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
package xyz.cofe.gui.swing.table;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.gui.swing.GuiUtil;
import xyz.cofe.gui.swing.SwingListener;

/**
 * Таблица с дополнительными функциями.
 *
 * <ul>
 * <li> Изменение высоты строк через UI
 * </ul>
 * @author user
 */
public class Table extends JTable {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(Table.class.getName());
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
        logger.entering(Table.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(Table.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(Table.class.getName(), method, result);
    }
    //</editor-fold>

    public Table(){
        setFillsViewportHeight(true);

        tableFocusListener = new TableFocusListener(this, true){
            @Override
            protected void onFocusedRowChanged(JTable table, int oldRow, int curRow) {
                focusedRowChanged(oldRow, curRow);
            }
        };

        autoResizeKeyStrokes = new KeyStroke[]{ KeyStroke.getKeyStroke("control pressed E") };
        SwingListener.onKeyPressed(this, (ke) -> {
            if( autoResizeKeyStrokes==null )return;
            if( GuiUtil.match(ke, autoResizeKeyStrokes) ){
                autoResizeCell(ke);
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="listeners">
    protected final ListenersHelper<TableListener, TableEvent> listeners
        = new ListenersHelper<>(
        ( TableListener ls, TableEvent ev )->{
            if( ls!=null ) ls.tableEvent(ev);
        }
    );

    public boolean hasTableListener(TableListener listener) {
        return listeners.hasListener(listener);
    }

    public Set<TableListener> getTableListeners() {
        return listeners.getListeners();
    }

    public AutoCloseable addTableListener(TableListener listener) {
        return listeners.addListener(listener);
    }

    public AutoCloseable addTableListener(TableListener listener, boolean weakLink) {
        return listeners.addListener(listener,weakLink);
    }

    public void removeTableListener(TableListener listener) {
        listeners.removeListener(listener);
    }

    public void removeAllTableListeners() {
        listeners.removeAllListeners();
    }

    public void fireTableEvent(TableEvent event) {
        listeners.fireEvent(event);
    }

    public void addTableEvent(TableEvent ev) {
        listeners.addEvent(ev);
    }

    public void fireTableEvents() {
        listeners.runEventQueue();
    }
    //</editor-fold>

    protected final TableFocusListener tableFocusListener;
    protected void focusedRowChanged( int oldRow, int curRow ){
        fireTableEvent(new TableEvent.FocusedRowChanged(this, oldRow, curRow));
    }
    public AutoCloseable onFocusedRowChanged( final Consumer<TableEvent.FocusedRowChanged> consumer ){
        if (consumer== null) {
            throw new IllegalArgumentException("consumer==null");
        }
        return addTableListener( (TableEvent ev) -> {
                if( ev instanceof TableEvent.FocusedRowChanged ){
                    consumer.accept((TableEvent.FocusedRowChanged)ev);
                }
            });
    }

    //<editor-fold defaultstate="collapsed" desc="auto resize">
    //<editor-fold defaultstate="collapsed" desc="autoResizeKeyStrokes : KeyStroke[]">
    protected KeyStroke[] autoResizeKeyStrokes;
    public synchronized KeyStroke[] getAutoResizeKeyStrokes(){
        return autoResizeKeyStrokes;
    }
    public synchronized void setAutoResizeKeyStrokes(KeyStroke[] kss){
        autoResizeKeyStrokes = kss;
    }
    //</editor-fold>

    private WeakReference<Graphics2D> memgsInst;
    public synchronized Graphics2D getMemGraphics2D(){
        Graphics2D g = memgsInst!=null ? memgsInst.get() : null;
        if( g!=null )return g;

        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        g = bi.createGraphics();

        memgsInst = new WeakReference<>(g);
        return g;
    }

    private void autoResizeCell(KeyEvent ke){
        int frow = getFocusedRow();
        if( frow>=0 ){
            if( autoRowHeight(frow, true, true) ){
                ke.consume();
                autoCellSize(frow, getFocusedColumn(), true, false, true, false, true);
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="getCellContentLayout()">
    /**
     * Получение размера контекта ячейки.
     *
     * <p>
     * Вычисление размера работает для TCRender
     * @param row строка
     * @param col колонка
     * @param selected ячейка "выбрана"
     * @param focused ячейка содержит фокус
     * @return размер контента ячейки или null
     */
    public Rectangle2D getCellContentLayout(int row, int col, boolean selected, boolean focused){
        if( row<0 || col<0 )return null;

        int colCnt = getColumnCount();
        int rowCnt = getRowCount();

        if( row>=rowCnt )return null;
        if( col>=colCnt )return null;

        Object cellRender = getCellRenderer(row, col);
        if( cellRender==null )return null;
        if( !(cellRender instanceof xyz.cofe.gui.swing.cell.TCRenderer) )return null;

        xyz.cofe.gui.swing.cell.TCRenderer tcr =
            (xyz.cofe.gui.swing.cell.TCRenderer)cellRender;

        TableColumn tc = getColumnModel().getColumn(col);
        if(tc==null)return null;

        int modCi = tc.getModelIndex();

        TableModel tm = getModel();
        if( tm==null )return null;

        if( modCi<0 || modCi>=tm.getColumnCount() )return null;

        Object val = tm.getValueAt(row, modCi);
        tcr.getTableCellRendererComponent(this, val, selected, focused, row, col);

        Rectangle2D rect = tcr.computeRect(getMemGraphics2D());
        return rect;
    }

    /**
     * Получение размера контекта ячейки.
     *
     * <p>
     * Вычисление размера работает для TCRender
     * @param row строка
     * @param col колонка
     * @return размер контента ячейки или null
     */
    public Rectangle2D getCellContentLayout(int row, int col){
        if( row<0 || col<0 )return null;

        int colCnt = getColumnCount();
        int rowCnt = getRowCount();

        if( row>=rowCnt )return null;
        if( col>=colCnt )return null;

        int[] selrows = getSelectedRows();
        boolean rowSelected = false;
        for( int selrow : selrows ){
            if( row==selrow ){
                rowSelected = true;
                break;
            }
        }

        int frow = getFocusedRow();
        boolean hasFcs = row == frow;

        return getCellContentLayout(row, col, rowSelected, hasFcs);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="getRowMaxContentLayout()">
    /**
     * Получение размера (высота) контекта строки.
     *
     * <p>
     * Вычисление размера работает для TCRender
     * @param row строка
     * @return размер контента или null
     */
    public Rectangle2D getRowMaxContentLayout( int row ){
        if( row<0 )return null;

        int colCnt = getColumnCount();
        int rowCnt = getRowCount();

        if( row>=rowCnt )return null;

        int[] selrows = getSelectedRows();
        boolean rowSelected = false;
        for( int selrow : selrows ){
            if( row==selrow ){
                rowSelected = true;
                break;
            }
        }

        int frow = getFocusedRow();
        boolean hasFcs = row == frow;

        Double minX = null;
        Double minY = null;
        Double maxX = null;
        Double maxY = null;

        for( int ci=0; ci<colCnt; ci++ ){
            Rectangle2D rect = getCellContentLayout(row,ci,rowSelected,hasFcs);

            if( rect!=null ){
                if( minX==null || minX>rect.getMinX() )minX = rect.getMinX();
                if( minY==null || minY>rect.getMinY() )minY = rect.getMinY();
                if( maxX==null || maxX<rect.getMaxX() )maxX = rect.getMaxX();
                if( maxY==null || maxY<rect.getMaxY() )maxY = rect.getMaxY();
            }
        }

        if( minX==null || minY==null || maxX==null || maxY==null )return null;

        return new Rectangle2D.Double(
            Math.min(minX, maxX),
            Math.min(minY, maxY),
            Math.abs(maxX - minX),
            Math.abs(maxY - minY)
        );
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="autoRowHeight()">
    /**
     * Подгоняет высоту строки под содержание
     * @param row строка
     * @param extend автоматически расширять
     * @param shrink автоматически сужать
     * @return true - было изменение размера
     */
    public boolean autoRowHeight(int row, boolean extend,boolean shrink){
        Rectangle2D maxCntLayout = getRowMaxContentLayout(row);
        if( maxCntLayout==null )return false;

        double cntHeight = maxCntLayout.getHeight();
        int curHeight = getRowHeight(row);

        if( cntHeight>0 && cntHeight<Double.MAX_VALUE ){
            if( curHeight<cntHeight && extend ){
                setRowHeight(row, (int)cntHeight );
            }else if( curHeight>cntHeight && shrink ){
                setRowHeight(row, (int)cntHeight );
            }
            return true;
        }

        return false;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="autoCellSize()">
    /**
     * Подгоняет высоту и ширину ячейки под содержание
     * @param row строка
     * @param col колонка
     * @param extendHeight автоматически расширять высоту
     * @param shrinkHeight автоматически сужать высоту
     * @param extendWidth автоматически расширять ширину
     * @param shrinkWidth автоматически сужать ширину
     * @param preferredWidth true - устанавливать ширину ячейки методом TableColumn.setPreferredWidth /
     * false - устанавливать ширину ячейки методом TableColumn.setWidth
     * @return true - было изменение размера
     */
    public boolean autoCellSize(
        int row, int col,
        boolean extendHeight,
        boolean shrinkHeight,
        boolean extendWidth,
        boolean shrinkWidth,
        boolean preferredWidth
    ){
        Rectangle2D cntLayout = getCellContentLayout(row, col);
        if( cntLayout==null )return false;

        if( col<0 )return false;

        int colCnt = getColumnCount();
        if( col>=colCnt )return false;

        if( row<0 )return false;
        int rowCnt = getRowCount();
        if( row>=rowCnt )return false;

        TableColumn tc = getColumnModel().getColumn(col);
        if( tc==null ){
            return false;
        }

        double curHeight = getRowHeight(row);
        double curWidth = tc.getWidth();

        double trgHeight = cntLayout.getHeight();
        if( trgHeight<1 )trgHeight = 1;

        double trgWidth = cntLayout.getWidth();
        if( trgWidth<1 )trgWidth = 1;

        boolean heightChanged = false;
        boolean widthChanged = false;

        if( curHeight<trgHeight && extendHeight ){
            setRowHeight(row, (int)trgHeight);
            heightChanged = true;
        }else if( curHeight>trgWidth && shrinkHeight ){
            setRowHeight(row, (int)trgHeight);
            heightChanged = true;
        }

        if( curWidth<trgWidth && extendWidth ){
            if( preferredWidth ){
                tc.setPreferredWidth((int)trgWidth);
                widthChanged = true;
            }else{
                tc.setWidth((int)trgWidth);
                widthChanged = true;
            }
        }else if( curWidth>trgWidth && shrinkWidth ){
            if( preferredWidth ){
                tc.setPreferredWidth((int)trgWidth);
                widthChanged = true;
            }else{
                tc.setWidth((int)trgWidth);
                widthChanged = true;
            }
        }

        return heightChanged || widthChanged;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getColumnWidth()">
    /**
     * Возвращает ширину колонки
     * @param col колонка
     * @return ширина
     */
    public int getColumnWidth(int col){
        if( col<0 )throw new IllegalArgumentException("col < 0");

        int colCnt = getColumnCount();
        if( col>=colCnt )throw new IllegalArgumentException("col >= columnCount(="+colCnt+")");

        TableColumn tc = getColumnModel().getColumn(col);
        if( tc==null ){
            throw new IllegalStateException("can't get TableColumn for col="+col);
        }

        return tc.getWidth();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="table cell resizing">
    protected volatile TableCellResizer resizer;
    public TableCellResizer getTableCellResizer(){
        if( resizer!=null )return resizer;
        synchronized(this){
            if( resizer!=null )return resizer;
            resizer = new TableCellResizer();
            resizer.setTable(this);
//            resizer.setResizeHeight(true);
            return resizer;
        }
    }

    protected int mouseAtRow = -1;
    protected int mouseAtColumn = -1;
    protected boolean mouseOver = false;
    protected Rectangle2D resizeCaptureRect;

    protected int resizeMouseButton = MouseEvent.BUTTON1;
    protected Boolean resizeMouseAlt = false;
    protected Boolean resizeMouseShift = false;
    protected Boolean resizeMouseControl = false;
    protected Boolean resizeMouseMeta = false;

    protected void changeMouseAtCell( MouseEvent e, int row, int col ){
        mouseAtRow = row;
        mouseAtColumn = col;
        resizeCaptureRect = getTableCellResizer().cellCaptureZone(mouseAtRow, mouseAtColumn);
        repaint();
    }

    protected boolean mouseEnter( MouseEvent e ){
        mouseOver = true;
        resizeCaptureRect = null;
        repaint();
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        return false;
    }
    protected boolean mouseExit( MouseEvent e ){
        mouseOver = false;
        resizeCaptureRect = null;
        getTableCellResizer().stop();
        repaint();
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        return false;
    }
    protected boolean mouseMove( MouseEvent e ){
        int mrow = rowAtPoint(e.getPoint());
        int mcol = columnAtPoint(e.getPoint());
        if( mrow!=mouseAtRow || mcol!=mouseAtColumn ){
            changeMouseAtCell(e, mrow, mcol);
        }

        if( resizeCaptureRect==null ){
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }else{
            if( resizeCaptureRect.contains(e.getPoint()) ){
                setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
            }else{
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }

        return false;
    }
    protected boolean mouseDragged(MouseEvent e){
        if( getTableCellResizer().isStarted() ){
            getTableCellResizer().drag(e);
            return true;
        }
        return false;
    }
    //    protected boolean mouseWheel(MouseEvent e){
//        if( getTableCellResizer().isStarted() ){
//            getTableCellResizer().drag(e);
//            return true;
//        }
//        return false;
//    }
    protected boolean isResizeStartEvent(MouseEvent e){
        if( e==null )return false;
        if( e.getID()!=MouseEvent.MOUSE_PRESSED )return false;
        if( e.getButton()!=resizeMouseButton )return false;
        if( resizeMouseAlt!=null && !Objects.equals(e.isAltDown(),resizeMouseAlt) )return false;
        if( resizeMouseControl!=null && !Objects.equals(e.isControlDown(),resizeMouseControl) )return false;
        if( resizeMouseShift!=null && !Objects.equals(e.isShiftDown(),resizeMouseShift) )return false;
        if( resizeMouseMeta!=null && !Objects.equals(e.isMetaDown(),resizeMouseMeta) )return false;
        return true;
    }
    protected boolean isResizeStopEvent(MouseEvent e){
        if( e==null )return false;
        if( e.getID()==MouseEvent.MOUSE_RELEASED )return true;
        if( e.getID()==MouseEvent.MOUSE_EXITED )return true;
        return false;
    }
    protected boolean mousePressed(MouseEvent e){
        if( isResizeStartEvent(e) ){
            if( getTableCellResizer().isCaptureZone(e) ){
                getTableCellResizer().start(e);
                return true;
            }
        }
        return false;
    }
    protected boolean mouseReleased(MouseEvent e){
        if( isResizeStopEvent(e) && getTableCellResizer().isStarted() ){
            getTableCellResizer().stop();
            return true;
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="processMouseMotionEvent()">
    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
        switch(e.getID()){
            case MouseEvent.MOUSE_MOVED:
                if( mouseMove(e) )return;
                break;
            case MouseEvent.MOUSE_DRAGGED:
                if( mouseDragged(e) )return;
                break;
            //case MouseEvent.MOUSE_WHEEL:
            //System.out.println("MOUSE_WHEEL");
            //if( mouseWheel(e) )return;
            //break;
        }
        super.processMouseMotionEvent(e);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="processMouseEvent()">
    @Override
    protected void processMouseEvent(MouseEvent e) {
        switch(e.getID()){
            case MouseEvent.MOUSE_ENTERED:
                if( mouseEnter(e) )return;
                break;
            case MouseEvent.MOUSE_EXITED:
                if( mouseExit(e) )return;
                break;
            case MouseEvent.MOUSE_PRESSED:
                if( mousePressed(e) )return;
                break;
            case MouseEvent.MOUSE_RELEASED:
                if( mouseReleased(e) )return;
                break;
            //case MouseEvent.MOUSE_WHEEL:
            //System.out.println("MOUSE_WHEEL(2)");
            //if( mouseWheel(e) )return;
            //break;
        }
        super.processMouseEvent(e);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="paint()">
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //<editor-fold defaultstate="collapsed" desc="table Resizer">
        if( g instanceof Graphics2D ){
            Graphics2D gs = (Graphics2D)g;
            if( mouseOver && mouseAtRow>=0 && mouseAtColumn>=0 ){
                Rectangle2D rect = getTableCellResizer().cellCaptureZone(mouseAtRow, mouseAtColumn);
                if( rect!=null ){
                    getTableCellResizer().paintCaptureZone(gs, rect);
                }
            }
        }
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="focusedRow : int">
    /**
     * Возращает строку содержащую фокус
     * @return строка
     */
    public int getFocusedRow(){
        return getSelectionModel().getLeadSelectionIndex();
    }

    /**
     * Указывает строку содержащую фокус
     * @param row строка
     * @param clearSelection очистить список выбранных строк
     * @param addSelection добавить строку в писок выделенных
     */
    public void setFocusedRow(int row,boolean clearSelection,boolean addSelection){
        if( row<0 )return;

        int rc = getRowCount();
        if( row>=rc )return;

        if( clearSelection ){
            getSelectionModel().clearSelection();
            getColumnModel().getSelectionModel().clearSelection();
        }
        if( addSelection ){
            getSelectionModel().addSelectionInterval(row, row);
            getColumnModel().getSelectionModel().addSelectionInterval(0, 0);
        }

        getColumnModel().getSelectionModel().setLeadSelectionIndex(0);
        getSelectionModel().setLeadSelectionIndex(row);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="focusedColumn : int">
    /**
     * Возращает колонку содержащую фокус
     * @return колонка с фокусом
     */
    public int getFocusedColumn(){
        return getColumnModel().getSelectionModel().getLeadSelectionIndex();
    }

    /**
     * Устанавливает колонку содержащую фокус
     * @param col колонка с фокусом
     */
    public void setFocusedColumn(int col){
        int colcnt = getColumnCount();
        if( col<0 )return;
        if( col>=colcnt )return;

        getColumnModel().getSelectionModel().setLeadSelectionIndex(col);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="visible area">
    /**
     * Видимая область
     */
    public static class VisibleArea {
        protected Integer minRow;
        protected Integer minColumn;
        protected Integer maxRow;
        protected Integer maxColumn;
        protected Rectangle visibleRect;

        /**
         * Возвращает область видимости в пикселях
         * @return область видимости в пикселях
         */
        public Rectangle getVisibleRect() {
            return visibleRect;
        }

        /**
         * Указывает область видимости в пикселях
         * @param visibleRect область видимости в пикселях
         */
        public void setVisibleRect(Rectangle visibleRect) {
            this.visibleRect = visibleRect;
        }

        /**
         * Добавляет видимую ячейку
         * @param row строка
         * @param col колонка
         * @param crect расположение ячейки
         */
        public void addCell( int row, int col, Rectangle crect ){
            minRow = minRow==null ? row : Math.min(minRow, row);
            maxRow = maxRow==null ? row : Math.max(maxRow, row);
            minColumn = minColumn==null ? row : Math.min(minColumn, col);
            maxColumn = maxColumn==null ? row : Math.max(maxColumn, row);
        }

        public Integer getMinRow() { return minRow; }
        public Integer getMinColumn() { return minColumn; }
        public Integer getMaxRow() { return maxRow; }
        public Integer getMaxColumn() { return maxColumn; }

        public boolean hasCells(){
            return minRow!=null && minColumn!=null && maxRow!=null && maxColumn!=null;
        }
    }

    /**
     * Возвращает область видимости
     * @return Область видимости
     */
    public VisibleArea getVisibleArea(){
        VisibleArea va = new VisibleArea();

        Rectangle vrect = getVisibleRect();
        va.setVisibleRect(vrect);

        int rc = getRowCount();
        if( rc<1 )return va;

        // поиск центральной строки
        int centralRow = findCentralRow(vrect);
        if( centralRow<0 )return va;
        if( centralRow>=rc )return va;

        // перемещение вверх от центра
        for( int ri=centralRow; ri>=0; ri-- ){
            for( int ci=0; ci<getColumnCount(); ci++ ){
                Rectangle cellRect = getCellRect(ri, ci, true);
                if( cellRect.intersects(vrect) ){
                    va.addCell(ri, ci, cellRect);
                }else{
                    ri = -100;
                }
            }
        }

        // перемещение вниз от центра
        for( int ri=centralRow; ri<rc; ri++ ){
            for( int ci=0; ci<getColumnCount(); ci++ ){
                Rectangle cellRect = getCellRect(ri, ci, true);
                if( cellRect.intersects(vrect) ){
                    va.addCell(ri, ci, cellRect);
                }else{
                    ri = rc+100;
                }
            }
        }

        /*for( int ri=0; ri<getRowCount(); ri++ ){
        for( int ci=0; ci<getColumnCount(); ci++ ){
        Rectangle cellRect = getCellRect(ri, ci, true);
        }
        }*/
        return va;
    }

    private int findCentralRow( Rectangle rect ){
        if( rect==null )return -1;

        int rc = getRowCount();
        if( rc<1 )return -1;
        if( rc==1 )return 0;

        Point2D.Double cPt = new Point2D.Double(rect.getCenterX(), rect.getCenterY());

        int row = rc / 2;
        int step = rc / 4;

        while( true ){
            if( step<1 )return row;
            Rectangle cellRect = getCellRect(row, 0, true);
            double ydiff = cPt.getY() - cellRect.getCenterY();
            if( ydiff<0 ){
                int nrow = row - step;
                if( nrow==row )return row;
                if( nrow<0 )return 0;
                row = nrow;
                step = step / 2;
            }else if( ydiff>0 ){
                int nrow = row + step;
                if( nrow==row )return row;
                if( nrow >= rc ){
                    return nrow-1;
                }
                row = nrow;
                step = step / 2;
            }else{
                return row;
            }
        }
    }
    //</editor-fold>
}
