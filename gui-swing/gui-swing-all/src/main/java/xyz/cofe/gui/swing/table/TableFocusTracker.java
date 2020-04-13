/*
 * The MIT License
 *
 * Copyright 2016 Kamnev Georgiy (nt.gocha@gmail.com).
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

package xyz.cofe.gui.swing.table;


import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import xyz.cofe.ecolls.Closeables;
import xyz.cofe.fn.Fn2;
import xyz.cofe.fn.Fn4;
import xyz.cofe.fn.Fn6;
import xyz.cofe.gui.swing.GuiUtil;

/**
 * Отслеживание перемещение фокуса в таблице при помощи таймера Swing
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TableFocusTracker
    implements Closeable
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TableFocusTracker.class.getName());

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
        logger.entering(TableFocusTracker.class.getName(),method,params);
    }

    private static void logExiting(String method,Object result){
        logger.exiting(TableFocusTracker.class.getName(),method,result);
    }

    private static void logExiting(String method){
        logger.exiting(TableFocusTracker.class.getName(),method);
    }
    //</editor-fold>

    /**
     * Конструктор
     * @param table таблица
     * @param delay задержка между очередной проверкой
     * @param weak true - хранить ссылку на таблицу как weak ссылку / false - как обычную ссылку
     */
    public TableFocusTracker( final JTable table, int delay, boolean weak ){
        if( table==null )throw new IllegalArgumentException( "table==null" );
        if( delay<1 )throw new IllegalArgumentException( "delay<1" );

        this.table      = weak ? null : table;
        this.tableRef   = weak ? new WeakReference<JTable>(table) : null;

        //this.timer = new Timer(delay, (e) -> { checkChanges(); } );
        this.timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkChanges();
            }
        } );

        lastFocusedRow = getFocusedRow();
        lastFocusedColumn = getFocusedColumn();

        final ComponentAdapter cmptAdapter =
            new ComponentAdapter() {
                @Override
                public void componentHidden(ComponentEvent e) {
                    logFiner("componentHidden");
                    onTableHide();
                }

                @Override
                public void componentShown(ComponentEvent e) {
                    logFiner("componentShown");
                    onTableShow();
                }
            };

        table.addComponentListener( cmptAdapter );

        final WeakReference<JTable> wref = new WeakReference(table);
        listeners.add( new Runnable() {
            @Override
            public void run() {
                JTable t = wref.get();
                if( t!=null ){
                    t.removeComponentListener(cmptAdapter);
                }
                //return null;
            }} );

        final Closeables wndListeners = new Closeables();

        final JTable ftable = table;
        final AncestorListener ancLsnr = new AncestorListener() {
            JTable iftable = ftable;

            @Override
            public void ancestorAdded(AncestorEvent event) {
                logFiner("ancestorAdded");
                wndListeners.close();
                onTableShow();

                final Window wnd = GuiUtil.getWindowOfComponent(iftable);
                if( wnd!=null ){

                    final WindowAdapter wclose = new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            logFiner("windowClosed");
                            onTableHide();

                            Window wnd = e.getWindow();
                            if( wnd!=null ){
                                logFiner("removeWindowListener by self");
                                wnd.removeWindowListener(this);
                            }
                        }

                        @Override
                        public void windowClosing(WindowEvent e) {
                            logFiner("windowClosing");
                            onTableHide();

                            Window wnd = e.getWindow();
                            if( wnd!=null ){
                                logFiner("removeWindowListener by self");
                                wnd.removeWindowListener(this);
                            }
                        }
                    };

                    final WeakReference<Window> wwnd = new WeakReference<Window>(wnd);
                    wndListeners.add( new Runnable(){
                        @Override public void run(){
                            Window rwnd = wwnd.get();
                            if( rwnd!=null ){
                                logFiner("removeWindowListener by runnable");
                                wnd.removeWindowListener(wclose);
                                wwnd.clear();
                            }
                        }});
                }
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                logFiner("ancestorRemoved");
                onTableHide();
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
                logFiner("ancestorMoved");
            }
        };

        table.addAncestorListener( ancLsnr );
        listeners.add( new Runnable() {
            @Override
            public void run() {
                JTable t = wref.get();
                if( t!=null ){
                    t.removeAncestorListener(ancLsnr);
                }
                //return null;
            }} );

        listeners.add(wndListeners);
    }

    protected void onTableHide(){
        stop();
    }

    protected void onTableShow(){
        start();
        if( !isRunning() ){
            try {
                close();
            } catch( IOException ex ) {
                Logger.getLogger(TableFocusTracker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    protected Closeables listeners = new Closeables();

    @Override
    public void close() throws IOException {
        synchronized(this){
            stop();
            listeners.close();
            if( timer!=null ){
                timer = null;
            }
            if( table!=null ){
                table = null;
            }
            if( tableRef!=null ){
                tableRef.clear();
                tableRef = null;
            }
            cellReader = null;
            equalsComparator = null;
            lastFocusedItem = null;
            rowChanged = null;
            cellChanged = null;
            itemChanged = null;
        }
    }

    /**
     * Создание трекера
     * @param tbl таблица
     * @param weak true - использовать weak ссылку на таблицу
     * @return трекер
     */
    public static TableFocusTracker tracking( JTable tbl, boolean weak ){
        return new TableFocusTracker(tbl, 100, weak);
    }

    /**
     * Указывает задержку между проверкой изменения фокуса
     * @param delay задержка, минимальное значение 1 мс
     * @return self ссылка
     */
    public TableFocusTracker timerDelay( int delay ){
        if( delay<1 )throw new IllegalArgumentException( "delay<1" );
        synchronized(this){
            Timer t = getTimer();
            if( t!=null ){
                t.setDelay(delay);
            }
        }
        return this;
    }

    //<editor-fold defaultstate="collapsed" desc="start()">
    /**
     * Запускает таймер
     * @return self ссылка
     */
    public TableFocusTracker start(){
        synchronized(this){
            if( timer!=null )timer.start();
        }
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="stop()">
    /**
     * Останавливает таймер
     * @return self ссылка
     */
    public TableFocusTracker stop(){
        synchronized(this){
            if( timer!=null )timer.stop();
        }
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="isRunning()">
    /**
     * Возвращает работает ли таймер/трекер
     * @return true - таймер запущен
     */
    public boolean isRunning(){
        synchronized(this){
            if( timer==null )return false;
            return timer.isRunning();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getTimer()">
    protected Timer timer;
    /**
     * Возвращает таймер
     * @return таймер
     */
    public Timer getTimer(){
        synchronized(this){
            return timer;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getTable()">
    protected JTable table;
    protected WeakReference<JTable> tableRef;

    /**
     * Вовращает ссылку на таблицу
     * @return таблица
     */
    public JTable getTable(){
        synchronized(this){
            if( table==null ){
                if( tableRef!=null ){
                    return tableRef.get();
                }
            }
            return table;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getFocusedRow()">
    /**
     * Возвращает строку содержащуюю фокус
     * @return строка с фокусом или -1
     */
    public int getFocusedRow(){
        synchronized(this){
            JTable tbl = getTable();
            if( tbl==null )return -1;
            return tbl.getSelectionModel().getLeadSelectionIndex();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getFocusedColumn()">
    /**
     * Возвращает колонку содержащуюю фокус
     * @return колонка с фокусом или -1
     */
    public int getFocusedColumn(){
        synchronized(this){
            JTable tbl = getTable();
            if( tbl==null )return -1;
            return tbl.getColumnModel().getSelectionModel().getLeadSelectionIndex();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cellReader">
    protected Fn2<Integer,Integer,Object> cellReader;

    /**
     * Возвращает функцию чтения значения в ячейке
     * @return функция (x,y) =&gt; value
     */
    public Fn2<Integer, Integer,Object> getCellReader()
    {
        synchronized(this){ return cellReader; }
    }

    /**
     * Указывает функцию чтения значения в ячейке
     * @param cellReader функция (x,y) =&gt; value
     */
    public void setCellReader(Fn2<Integer, Integer, Object> cellReader)
    {
        synchronized(this){
            this.cellReader = cellReader;
            this.compareObject = cellReader != null;
        }
    }

    /**
     * Указывает функцию чтения значения в ячейке
     * @param cellReader функция (x,y) =&gt; value
     * @return self ссылка
     */
    public TableFocusTracker cellReader(Fn2<Integer, Integer, Object> cellReader){
        setCellReader(cellReader);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getItem(row,col)">
    /**
     * Возвращает значение в ячейке используя функцию cellReader
     * @param row строка
     * @param col столбец
     * @return значение или null
     * @see #cellReader(Fn2)
     */
    public Object getItem( int row, int col ){
        synchronized(this){
            if( cellReader!=null )
                return cellReader.apply(row, col);
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="compareRow : boolean">
    protected boolean compareRow = true;

    /**
     * Возвращает сравнивать ли старый и текущий номер строки
     * @return true (по умолчанию) сравнивать
     */
    public boolean isCompareRow() {
        synchronized(this){ return compareRow; }
    }

    /**
     * Указывает сравнивать ли старый и текущий номер строки
     * @param compareRow true (по умолчанию) сравнивать
     */
    public void setCompareRow(boolean compareRow) {
        synchronized(this){ this.compareRow = compareRow; }
    }

    /**
     * Указывает сравнивать ли старый и текущий номер строки
     * @param compareRow true (по умолчанию) сравнивать
     * @return self ссылка
     */
    public TableFocusTracker compareRow( boolean compareRow ){
        setCompareRow(compareRow);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="compareColumn : boolean">
    protected boolean compareColumn = false;

    /**
     * Возвращает сравнивать ли старый и текущий номер колонки
     * @return true (по умолчанию) сравнивать
     */
    public boolean isCompareColumn() {
        synchronized(this){ return compareColumn; }
    }

    /**
     * Указывает сравнивать ли старый и текущий номер колонки
     * @param compareColumn true (по умолчанию) сравнивать
     */
    public void setCompareColumn(boolean compareColumn) {
        synchronized(this){ this.compareColumn = compareColumn; }
    }

    /**
     * Указывает сравнивать ли старый и текущий номер колонки
     * @param compareColumn true (по умолчанию) сравнивать
     * @return self ссылка
     */
    public TableFocusTracker compareColumn( boolean compareColumn ){
        setCompareColumn(compareRow);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="compareObject : boolean">
    protected boolean compareObject = false;

    /**
     * Сравнивает или нет значения в ячейке
     * @return true - сравнивать, <br>
     * по умолчанию значние false
     */
    public boolean isCompareObject() {
        synchronized(this){ return compareObject; }
    }

    /**
     * Сравнивает или нет значения в ячейке
     * @param compareObject true - сравнивать
     */
    public void setCompareObject(boolean compareObject) {
        synchronized(this){ this.compareObject = compareObject; }
    }

    /**
     * Сравнивает или нет значения в ячейке
     * @param compareObject true - сравнивать
     * @return self ссылка
     */
    public TableFocusTracker compareObject( boolean compareObject ){
        setCompareObject(compareObject);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="equalsComparator">
    protected Fn2<Object,Object,Boolean> equalsComparator;

    /**
     * Функция сравнения на равенство значений
     * @return функция равенства значений
     */
    public Fn2<Object, Object,Boolean> getEqualsComparator()
    {
        synchronized(this){ return equalsComparator; }
    }

    /**
     * Функция сравнения на равенство значений
     * @param equalsComparator функция равенства значений
     */
    public void setEqualsComparator(Fn2<Object, Object, Boolean> equalsComparator)
    {
        synchronized(this){
            this.equalsComparator = equalsComparator;
            this.compareRow = this.equalsComparator != null;
        }
    }

    /**
     * Функция сравнения на равенство значений
     * @param equalsComparator функция равенства значений
     * @return self ссылка
     */
    public TableFocusTracker equalsComparator( Fn2<Object, Object, Boolean> equalsComparator ){
        setEqualsComparator(equalsComparator);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="compareEquals(i0, i1)">
    /**
     * Сравнение объектов на равенство.
     * Если не указана функция сравнения, то используется обычная функция equals
     * @param i0 первый объект
     * @param i1 второй объект
     * @return true - равенсто установленно
     * @see Object#equals(Object)
     * @see #equalsComparator
     */
    protected boolean compareEquals( Object i0, Object i1 ){
        if( i0==null || i1==null ){
            return i0 == i1;
        }
        if( equalsComparator!=null ){
            return equalsComparator.apply(i0, i1);
        }
        return i0.equals(i1);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="lastFocusedItem">
    protected Object lastFocusedItem;

    /**
     * Возвращает последнее сфокусированное значение
     * @return послденее значение с фокусом
     */
    public Object getLastFocusedItem() {
        return lastFocusedItem;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="lastFocusedRow">
    protected int lastFocusedRow;

    /**
     * Возвращает последнюю сфокусированную строку
     * @return последняя строка с фокусом
     */
    public int getLastFocusedRow() {
        return lastFocusedRow;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="lastFocusedColumn">
    protected int lastFocusedColumn;

    /**
     * Возвращает последнюю сфокусированную колонку
     * @return последняя колонка с фокусом
     */
    public int getLastFocusedColumn() {
        return lastFocusedColumn;
    }
    //</editor-fold>

    /**
     * Проверка наличия изменений, вызывается таймером
     */
    protected void checkChanges(){
        int frow;
        int fcol;
        Object fitm;

        int lrow;
        int lcol;
        Object litm;

        boolean rowChanged;
        boolean cllChanged;
        boolean itmChanged;
        boolean changed;

        synchronized(this){
            frow = getFocusedRow();
            fcol = getFocusedColumn();
            fitm = getItem(frow, fcol);

            lrow = lastFocusedRow;
            lcol = lastFocusedColumn;
            litm = lastFocusedItem;

            lastFocusedRow = frow;
            lastFocusedColumn = fcol;
            lastFocusedItem = fitm;

            rowChanged = frow != lrow && compareRow;
            cllChanged = fcol != lcol && compareColumn;
            itmChanged = compareObject && !compareEquals(litm, fitm);

            changed = rowChanged || cllChanged || itmChanged ;
        }

        if( changed ){
            changed( litm, lrow, lcol, fitm, frow, fcol );
        }

        if( rowChanged )TableFocusTracker.this.rowChanged(litm, lrow, lcol, fitm, frow, fcol);
        if( cllChanged )TableFocusTracker.this.cellChanged(litm, lrow, lcol, fitm, frow, fcol);
        if( itmChanged )TableFocusTracker.this.itemChanged(litm, lrow, lcol, fitm, frow, fcol);
    }

    /**
     * Вызывается при наличии изменений
     * @param lastItm последнее значение содержащее фокус
     * @param lastRow последняя строка содержащаяя фокус
     * @param lastCol последняя колонка содержащаяя фокус
     * @param currentItem текущее значение содержащее фокус
     * @param currentRow текущее строка содержащаяя фокус
     * @param currentCol текущее колонка содержащаяя фокус
     */
    protected void changed(
        Object lastItm, int lastRow, int lastCol,
        Object currentItem, int currentRow, int currentCol
    ){
    }

    //<editor-fold defaultstate="collapsed" desc="rowChanged : Func2">
    protected volatile Fn2<Integer,Integer,Object> rowChanged;

    /**
     * Возвращает функцию которая срабатывает при изменеии строки
     * @return функция (lastRow,currentRow):Any
     */
    public Fn2<Integer, Integer, Object> getRowChanged()
    {
        return rowChanged;
    }

    /**
     * Указывает функцию которая срабатывает при изменеии строки
     * @param rowChanged функция (lastRow,currentRow):Any
     */
    public void setRowChanged(Fn2<Integer, Integer, Object> rowChanged)
    {
        this.rowChanged = rowChanged;
    }

    /**
     * Указывает функцию которая срабатывает при изменеии строки
     * @param rowChanged функция (lastRow,currentRow):Any
     * @return self ссылка
     */
    public TableFocusTracker rowChanged( Fn2<Integer, Integer, Object> rowChanged ){
        this.rowChanged = rowChanged;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rowChanged()">
    /**
     * Вызывается при измении строки
     * @param lastItm последнее значение содержащее фокус
     * @param lastRow последняя строка содержащаяя фокус
     * @param lastCol последняя колонка содержащаяя фокус
     * @param currentItem текущее значение содержащее фокус
     * @param currentRow текущее строка содержащаяя фокус
     * @param currentCol текущее колонка содержащаяя фокус
     */
    protected void rowChanged(
        Object lastItm, int lastRow, int lastCol,
        Object currentItem, int currentRow, int currentCol
    ){
        Fn2 f = rowChanged;
        if( f!=null )f.apply(lastRow, currentRow);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cellChanged : Func4">
    protected volatile Fn4<Integer,Integer,Integer,Integer,Object> cellChanged;

    /**
     * Возвращает функцию которая срабатывает при изменеии ячейки
     * @return функция (lastRow, lastCol, currentRow, currentCol)
     */
    public Fn4<Integer, Integer,Integer,Integer, Object> getCellChanged()
    {
        return cellChanged;
    }

    /**
     * Указывает функцию которая срабатывает при изменеии ячейки
     * @param cellChanged функция (lastRow, lastCol, currentRow, currentCol)
     */
    public void setCellChanged( Fn4<Integer, Integer,Integer,Integer, Object> cellChanged)
    {
        this.cellChanged = cellChanged;
    }

    /**
     * Указывает функцию которая срабатывает при изменеии ячейки
     * @param cellChanged функция (lastRow, lastCol, currentRow, currentCol)
     * @return self ссылка
     */
    public TableFocusTracker cellChanged( Fn4<Integer, Integer,Integer,Integer, Object> cellChanged ){
        this.cellChanged = cellChanged;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cellChanged()">
    /**
     * Вызывается при изменении ячейки
     * @param lastItm последнее значение содержащее фокус
     * @param lastRow последняя строка содержащаяя фокус
     * @param lastCol последняя колонка содержащаяя фокус
     * @param currentItem текущее значение содержащее фокус
     * @param currentRow текущее строка содержащаяя фокус
     * @param currentCol текущее колонка содержащаяя фокус
     */
    protected void cellChanged(
        Object lastItm, int lastRow, int lastCol,
        Object currentItem, int currentRow, int currentCol
    ){
        Fn4 f = cellChanged;
        if( f!=null )f.apply(lastRow, lastCol, currentRow, currentCol);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="itemChanged : Func2">
    protected volatile Fn6<Integer,Integer,Object,Integer,Integer,Object, Object> itemChanged;

    /**
     * Возвращает функцию вызываемую при изменении фокуса
     * @return fn( lastRow, lastColumn, lastItem, currentRow, currentColumn, currentItem ) =&gt; dummy
     */
    public Fn6<Integer,Integer,Object,Integer,Integer,Object,Object> getItemChanged()
    {
        return itemChanged;
    }

    /**
     * Указыает функцию вызываемую при изменении фокуса
     * @param rowChanged fn( lastRow, lastColumn, lastItem, currentRow, currentColumn, currentItem ) =&gt; dummy
     */
    public void setItemChanged(Fn6<Integer,Integer,Object,Integer,Integer,Object,Object> rowChanged)
    {
        this.itemChanged = rowChanged;
    }

    /**
     * Указыает функцию вызываемую при изменении фокуса
     * @param rowChanged fn( lastRow, lastColumn, lastItem, currentRow, currentColumn, currentItem ) =&gt; dummy
     * @return this ссылка
     */
    public TableFocusTracker itemChanged( Fn6<Integer,Integer,Object,Integer,Integer,Object,Object> rowChanged ){
        itemChanged = rowChanged;
        return this;
    }

    /**
     * Указывает функцию срабатываемую при измении значения ячейки
     * @param rowChanged функция fn( newValue )
     * @return self ссылка
     */
    public TableFocusTracker itemChanged( final Consumer<Object> rowChanged ){
        if( rowChanged==null )throw new IllegalArgumentException( "rowChanged==null" );
        //itemChanged = (oldRow,oldCol,old,curRow,curCol,current) -> { rowChanged.recive(current); return null; };
        itemChanged = new Fn6<Integer, Integer, Object, Integer, Integer, Object, Object>() {
            @Override
            public Object apply(Integer oldRow, Integer oldCol, Object old, Integer curRow, Integer curCol, Object current) {
                rowChanged.accept(current);
                return null;
            }
        };
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="itemChanged()">
    /**
     * Вызывается при измении значения ячейки
     * @param lastItm последнее значение содержащее фокус
     * @param lastRow последняя строка содержащаяя фокус
     * @param lastCol последняя колонка содержащаяя фокус
     * @param currentItem текущее значение содержащее фокус
     * @param currentRow текущее строка содержащаяя фокус
     * @param currentCol текущее колонка содержащаяя фокус
     */
    protected void itemChanged(
        Object lastItm, int lastRow, int lastCol,
        Object currentItem, int currentRow, int currentCol
    ){
        Fn6 f = itemChanged;
        if( f!=null )f.apply(lastRow,lastCol,lastItm, currentRow,currentCol,currentItem);
    }
    //</editor-fold>
}
