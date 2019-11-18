package xyz.cofe.gui.swing.tree;

import xyz.cofe.ecolls.Closeables;
import xyz.cofe.gui.swing.table.Table;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Модель горизонтального скроллинга содержимого в внутри каждой колонки таблицы
 * @author nt.gocha@gmail.com
 */
public class ColumnScrollModel {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ColumnScrollModel.class.getName());
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
        logger.entering(ColumnScrollModel.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(ColumnScrollModel.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(ColumnScrollModel.class.getName(), method, result);
    }
    //</editor-fold>

    public ColumnScrollModel(){
    }

    protected final Closeables tableListeners = new Closeables();

    //<editor-fold defaultstate="collapsed" desc="table : Table">
    protected Table table;
    /**
     * Указывает таблицу
     * @return таблица
     */
    public synchronized Table getTable() { return table; }
    /**
     * Указывает таблицу
     * @param table таблица
     */
    public synchronized void setTable(Table table) {
        tableListeners.close();
        columns = null;

        this.table = table;
        if( table!=null ){
            listen(table);
        }
    }
    /**
     * Добавляет подписчика на модель колонок таблицы
     * @param table таблица
     */
    protected void listen(Table table){
        table.getColumnModel().addColumnModelListener(columnsListener);
        final WeakReference<Table> wref = new WeakReference<>(table);
        tableListeners.add(new Runnable() {
            @Override
            public void run() {
                Table table = wref.get();
                if( table==null )return;
                table.getColumnModel().removeColumnModelListener(columnsListener);
            }
        });
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="columnsListener">
    /**
     * Пожписчик на модель колонок таблицы, слушает сообщения о добавлении/удалении/перемещении колоноки
     */
    protected TableColumnModelListener columnsListener = new TableColumnModelListener() {
        @Override
        public void columnAdded( TableColumnModelEvent e) {
            int fromIdx = e.getFromIndex();
            int toIdx = e.getToIndex();
            onColumnAdded(fromIdx, toIdx);
        }

        @Override
        public void columnRemoved(TableColumnModelEvent e) {
            int fromIdx = e.getFromIndex();
            int toIdx = e.getToIndex();
            onColumnRemoved(fromIdx, toIdx);
        }

        @Override
        public void columnMoved(TableColumnModelEvent e) {
            int fromIdx = e.getFromIndex();
            int toIdx = e.getToIndex();
            onColumnMoved(fromIdx, toIdx);
        }

        @Override
        public void columnMarginChanged( ChangeEvent e) {
        }

        @Override
        public void columnSelectionChanged( ListSelectionEvent e) {
        }
    };

    /**
     * Вызывается при добавлении колоноки, добавляет соответ ColumnScroll
     * @param from начало диапазона добавленных колонок
     * @param to конец(вкл) диапазона добавленных колонок
     */
    protected synchronized void onColumnAdded(int from, int to){
        for( int ci=Math.max(from, to); ci>=Math.min(from, to); ci-- ){
            List<ColumnScroll> l = getColumns();
            if( ci<l.size() && ci>=0 ){
                ColumnScroll c = createColumnScroll();
                l.add(ci, c);
            }
        }
    }

    /**
     * Вызывается при удалени колоноки, удаляет соответ ColumnScroll
     * @param from начало диапазона удаленных колонок
     * @param to конец(вкл) диапазона удаленных колонок
     */
    protected synchronized void onColumnRemoved(int from, int to){
        for( int ci=Math.max(from, to); ci>=Math.min(from, to); ci-- ){
            List<ColumnScroll> l = getColumns();
            if( ci<l.size() && ci>=0 ){
                l.remove(ci);
            }
        }
    }

    /**
     * Вызывает при смене местами колонок, меняет местами соответ ColumnScroll
     * @param from колонка
     * @param to колонка
     */
    protected synchronized void onColumnMoved(int from, int to){
        if( table==null )return;
        if( from==to )return;

        ColumnScroll c1 = getColumnScroll(from);
        ColumnScroll c2 = getColumnScroll(to);

        List<ColumnScroll> ls = getColumns();
        ls.set(from, c2);
        ls.set(to,c1);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="createColumnScroll()">
    /**
     * Создает ColumnScroll
     * @return созданный ColumnScroll
     */
    protected ColumnScroll createColumnScroll(){
        return new ColumnScroll();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="columns : List<ColumnScroll>">
    private List<ColumnScroll> columns;
    /**
     * Возвращает ColumnScroll для колонок таблицы
     * @return ColumnScroll для колонок
     */
    public synchronized List<ColumnScroll> getColumns(){
        if( columns!=null )return columns;
        columns = new CopyOnWriteArrayList<>();
        return columns;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="getColumnScroll(column) : ColumnScroll">
    /**
     * Возвращает информацию о скроллинге ячейки.
     * По необходимости создает соответ объект ColumnScroll.
     * @param column колонка
     * @return скроллинг ячейки или null
     */
    public ColumnScroll getColumnScroll(int column){
        if( column<0 )return null;
        if( table==null )return null;
        if( column >= table.getColumnCount() )return null;

        List<ColumnScroll> ls = getColumns();
        while(true){
            if( column>=ls.size() ){
                ls.add(createColumnScroll());
            }else{
                break;
            }
        }

        return ls.get(column);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="resetNodeRenderBounds()">
    /**
     * Сброс минимальной и максимальной координат
     */
    public void resetNodeRenderBounds(){
        for( ColumnScroll cs : getColumns() ){
            if( cs!=null )cs.resetNodeRenderBounds();
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="recalcScrollWidths()">
    /**
     * Пересчет ширины скролирования scrollWidth
     */
    public void recalcScrollWidths(){
        //logInfo("recalcScrollWidths");
        Table tbl = getTable();
        if( tbl==null )return;
        for( int ci=0; ci<tbl.getColumnCount(); ci++ ){
            double colWidth = tbl.getColumnWidth(ci);
            ColumnScroll cscr = getColumnScroll(ci);
            if( cscr!=null ){
                cscr.recalcScrollWidth(colWidth);
            }
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="paintScrollers()">
    /**
     * Отображение скроллинга
     * @param gs интф рендера
     * @param table таблица
     */
    public void paintScrollers( Graphics2D gs, Table table){
        if(gs==null)return;
        if(table==null)return;

        for( int ci=0; ci<table.getColumnCount(); ci++ ){
            ColumnScroll cscr = getColumnScroll(ci);
            if( cscr!=null ){
                cscr.paintScroller(gs, table, ci);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="scrollDragged : boolean">
    private boolean scrollDragged = false;
    /**
     * Возвращает флаг начала смещения скроллинга
     * @return true - в осуществляется скроллинг содержимого
     */
    public boolean isScrollDragged() { return scrollDragged; }
    /**
     * Указывает флаг начала смещения скроллинга
     * @param scrollDragged true - в осуществляется скроллинг содержимого
     */
    public void setScrollDragged(boolean scrollDragged) { this.scrollDragged = scrollDragged; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="scrollDragStartX : double">
    private double scrollDragStartX = 0;
    /**
     * Возвращает начальные координаты/размеры перед началом операции скроллирования
     * @return начальные данные перед скроллированием
     */
    public double getScrollDragStartX() { return scrollDragStartX; }
    /**
     * Указывает начальные координаты/размеры перед началом операции скроллирования
     * @param scrollDragStartX начальные данные перед скроллированием
     */
    public void setScrollDragStartX(double scrollDragStartX) { this.scrollDragStartX = scrollDragStartX; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="scrollDragStartY : double">
    private double scrollDragStartY = 0;
    /**
     * Возвращает начальные координаты/размеры перед началом операции скроллирования
     * @return начальные данные перед скроллированием
     */
    public double getScrollDragStartY() { return scrollDragStartY; }
    /**
     * Указывает начальные координаты/размеры перед началом операции скроллирования
     * @param scrollDragStartY  начальные данные перед скроллированием
     */
    public void setScrollDragStartY(double scrollDragStartY) { this.scrollDragStartY = scrollDragStartY; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="scrollXStarted : double">
    private double scrollXStarted = 0;
    /**
     * Возвращает начальные координаты/размеры перед началом операции скроллирования
     * @return начальные данные перед скроллированием
     */
    public double getScrollXStarted() { return scrollXStarted; }
    /**
     * Указывает начальные координаты/размеры перед началом операции скроллирования
     * @param scrollXStarted начальные данные перед скроллированием
     */
    public void setScrollXStarted(double scrollXStarted) { this.scrollXStarted = scrollXStarted; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="scrollXKofStarted : double">
    private double scrollXKofStarted = 0;
    /**
     * Возвращает начальные координаты/размеры перед началом операции скроллирования
     * @return начальные данные перед скроллированием
     */
    public double getScrollXKofStarted() { return scrollXKofStarted; }
    /**
     * Указывает начальные координаты/размеры перед началом операции скроллирования
     * @param scrollXKofStarted  начальные данные перед скроллированием
     */
    public void setScrollXKofStarted(double scrollXKofStarted) { this.scrollXKofStarted = scrollXKofStarted; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="scrollWidthStarted : double">
    private double scrollWidthStarted = 0;
    /**
     * Возвращает начальные координаты/размеры перед началом операции скроллирования
     * @return начальные данные перед скроллированием
     */
    public double getScrollWidthStarted() { return scrollWidthStarted; }
    /**
     * Указывает начальные координаты/размеры перед началом операции скроллирования
     * @param scrollWidthStarted начальные данные перед скроллированием
     */
    public void setScrollWidthStarted(double scrollWidthStarted) { this.scrollWidthStarted = scrollWidthStarted; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="scrollColumWidthStarted : double">
    private double scrollColumWidthStarted = 0;
    /**
     * Возвращает начальные координаты/размеры перед началом операции скроллирования
     * @return начальные данные перед скроллированием
     */
    public double getScrollColumWidthStarted() { return scrollColumWidthStarted; }
    /**
     * Указывает начальные координаты/размеры перед началом операции скроллирования
     * @param scrollColumWidthStarted начальные данные перед скроллированием
     */
    public void setScrollColumWidthStarted(double scrollColumWidthStarted) { this.scrollColumWidthStarted = scrollColumWidthStarted; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getScrollerDragRect(column) : Rectangle2D">
    /**
     * Возвращает координаты отображения ползунка скроллирования для указанной колонки
     * @param column колонка (0....)
     * @return Координаты
     */
    public Rectangle2D getScrollerDragRect(int column){
        if( column<0 )return null;
        if( table==null )return null;

        ColumnScroll cs = getColumnScroll(column);
        if( cs==null )return null;

        return cs.getScrollerDragRect(table, column);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="getScrollerRect(column) : Rectangle2D">
    /**
     * Возвращает координаты отображения полосы скроллирования для указанной колонки
     * @param column колонка (0....)
     * @return координаты
     */
    public Rectangle2D getScrollerRect(int column){
        if( column<0 )return null;
        if( table==null )return null;

        ColumnScroll cs = getColumnScroll(column);
        if( cs==null )return null;

        return cs.getScrollerRect(table, column);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="scrolledColumn : ColumnScroll">
    private ColumnScroll scrolledColumn;
    /**
     * Возвращает ссылку на информацию о скрлленге колонки
     * @return скроллируемая колонка
     */
    public ColumnScroll getScrolledColumn() { return scrolledColumn; }
    /**
     * Указывает ссылку на информацию о скрлленге колонки
     * @param scrolledColumn скроллируемая колонка
     */
    public void setScrolledColumn(ColumnScroll scrolledColumn) { this.scrolledColumn = scrolledColumn; }
    //</editor-fold>

    /**
     * Отмечает начало скроллирования
     * @param x координаты мыши
     * @param y координаты мыши
     * @return true - скроллирование начато / false - не начато, например координаты не соответствуют ползунку
     */
    public boolean startDrag(int x, int y){
        setScrollDragged(false);
        if( table==null )return false;

        int col = table.getColumnModel().getColumnIndexAtX(x);
        if( col<0 )return false;

        ColumnScroll cscrl = getColumnScroll(col);
        if( cscrl==null )return false;

        Rectangle2D dragRect = getScrollerDragRect(col);
        if( dragRect==null )return false;
        if( !dragRect.contains(x,y) )return false;

        setScrolledColumn(cscrl);
        setScrollDragStartX(x);
        setScrollDragStartY(y);
        setScrollXStarted(cscrl.getScrollX());
        setScrollWidthStarted(cscrl.getScrollWidth());
        setScrollColumWidthStarted(table.getColumnWidth(col));

        double scrollWidthStarted = getScrollWidthStarted();
        double scrollColumWidthStarted = getScrollColumWidthStarted();
        if( scrollWidthStarted>0 ){
            setScrollXKofStarted( scrollColumWidthStarted / scrollWidthStarted );
            setScrollDragged(true);
            return true;
        }

        return false;
    }
}
