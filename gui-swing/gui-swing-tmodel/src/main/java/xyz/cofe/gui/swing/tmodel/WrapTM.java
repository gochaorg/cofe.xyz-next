/*
 * The MIT License
 *
 * Copyright 2014 Kamnev Georgiy (nt.gocha@gmail.com).
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
package xyz.cofe.gui.swing.tmodel;


import xyz.cofe.fn.Pair;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Обвертка над существующей таблицей
 * @author gocha
 */
public class WrapTM implements TableModel {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(WrapTM.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(WrapTM.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(WrapTM.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(WrapTM.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(WrapTM.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(WrapTM.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(WrapTM.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="propertyChangeSupport">
    /**
     * Поддержка PropertyChangeEvent
     */
    private transient java.beans.PropertyChangeSupport propertyChangeSupport = null;
    /**
     * Поддержка PropertyChangeEvent
     * @return Поддержка PropertyChangeEvent
     */
    protected java.beans.PropertyChangeSupport propertySupport(){
        if( propertyChangeSupport!=null )return propertyChangeSupport;
        propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
        return propertyChangeSupport;
    }

    /**
     * Уведомляет подписчиков о измении свойства
     * @param property Свойство
     * @param oldValue Старое значение
     * @param newValue Новое значение
     */
    protected void firePropertyChange(String property,Object oldValue, Object newValue){
        propertySupport().firePropertyChange(property, oldValue, newValue);
    }

    /**
     * Добавляет подписчика
     * @param listener Подписчик
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertySupport().addPropertyChangeListener( listener );
    }

    /**
     * Удаляет подписчика
     * @param listener Подписчик
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertySupport().removePropertyChangeListener( listener );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Поддержка уведомлений о измении таблцы">
    protected EventSupport evSupport = new EventSupport(this);

    @Override
    public void removeTableModelListener(TableModelListener l) {
        evSupport.removeTableModelListener(l);
    }

    public Collection<TableModelListener> getListenersCollection() {
        return evSupport.getListenersCollection();
    }

    public TableModelListener[] getListeners() {
        return evSupport.getListeners();
    }

    public void fireTableModelEvent(TableModelEvent e) {
        if( e!=null ){
            logFinest(
                "fireTableModelEvent column={0} type={1} first={2} last={3} src={4}",
                e.getColumn(), e.getType(), e.getFirstRow(), e.getLastRow(),
                e.getSource()
            );
        }
        evSupport.fireTableModelEvent(e);
    }

    public void fireRowsUpdated(int rowIndexFrom, int toIndexInclude) {
        logFiner("fireRowsUpdated from={0} to={1}", rowIndexFrom, toIndexInclude);
        evSupport.fireRowsUpdated(rowIndexFrom, toIndexInclude);
    }

    public void fireRowsInserted(int rowIndexFrom, int toIndexInclude) {
        logFiner("fireRowsInserted from={0} to={1}", rowIndexFrom, toIndexInclude);
        evSupport.fireRowsInserted(rowIndexFrom, toIndexInclude);
    }

    public void fireRowsDeleted(int rowIndexFrom, int toIndexInclude) {
        logFiner("fireRowsDeleted from={0} to={1}", rowIndexFrom, toIndexInclude);
        evSupport.fireRowsDeleted(rowIndexFrom, toIndexInclude);
    }

    public void fireRowUpdated(int row) {
        logFiner("fireRowUpdated {0}", row);
        evSupport.fireRowUpdated(row);
    }

    public void fireColumnsChanged() {
        logFiner("fireColumnsChanged");
        evSupport.fireColumnsChanged();
    }

    public void fireCellChanged(int rowIndex, int columnIndex) {
        logFiner("fireCellChanged row={0} col={1}",rowIndex,columnIndex);
        evSupport.fireCellChanged(rowIndex, columnIndex);
    }

    public void fireAllChanged() {
        logFiner("fireAllChanged");
        evSupport.fireAllChanged();
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        evSupport.addTableModelListener(l);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Делегироание методов к оригиналу tableModel">
    /* (non-Javadoc) @see TableModel */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if( tableModel==null )return null;
        int c = mapColumnToInside(columnIndex);
        if( c<0 )return null;
        return tableModel.getColumnClass(c);
    }

    /* (non-Javadoc) @see TableModel */
    @Override
    public int getColumnCount() {
        if( tableModel==null )return 0;
        return tableModel.getColumnCount();
    }

    /* (non-Javadoc) @see TableModel */
    @Override
    public String getColumnName(int columnIndex) {
        if( tableModel==null )return "?";
        int c = mapColumnToInside(columnIndex);
        if( c<0 )return "?";
        return tableModel.getColumnName(c);
    }

    /* (non-Javadoc) @see TableModel */
    @Override
    public int getRowCount() {
        if( tableModel==null )return 0;
        return tableModel.getRowCount();
    }

    /* (non-Javadoc) @see TableModel */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(tableModel==null)return null;
        int c = mapColumnToInside(columnIndex);
        if( c<0 )return null;
        int r = mapRowToInside(rowIndex);
        if( r<0 )return null;
        return tableModel.getValueAt(r, c);
    }

    /* (non-Javadoc) @see TableModel */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(tableModel==null)return false;
        int c = mapColumnToInside(columnIndex);
        if( c<0 )return false;
        int r = mapRowToInside(rowIndex);
        if( r<0 )return false;
        return tableModel.isCellEditable(r, c);
    }

    /* (non-Javadoc) @see TableModel */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (tableModel == null) return;
        int c = mapColumnToInside(columnIndex);
        if( c<0 )return;
        int r = mapRowToInside(rowIndex);
        if( r<0 )return;
        tableModel.setValueAt(aValue, r, c);
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Отображение столбцов/строк на <-> оригинал">
    /**
     * Карта отображения запрашиваемой колонки в исходную колонку.
     * По умолчанию возвращает запрашиваемое значение.
     * @param columnIndex индекс запрашиваемой колонки
     * @return исходная колонка или -1
     */
    public int mapColumnToInside(int columnIndex){
        return columnIndex;
    }

    /**
     * Карта отображения запрашиваемой строки в исходную строку.
     * По умолчанию возвращает запрашиваемое значение.
     * @param rowIndex индекс запрашиваемой строки
     * @return исходная строка или -1
     */
    public int mapRowToInside(int rowIndex){
        return rowIndex;
    }

    /**
     * Карта отображения внутреней колонки на -&gt; внешнюю. <br>
     * По умолчанию возвращает запрашиваемое значение.
     * @param columnIndex индекс запрашиваемой колонки
     * @return колонка (внешняя) или -1
     */
    public int mapColumnToOutside(int columnIndex){
        return columnIndex;
    }

    /**
     * Карта отображения внутреней строки на -&gt; внешнюю
     * По умолчанию возвращает запрашиваемое значение.
     * @param rowIndex индекс запрашиваемой строки
     * @return строка (внешняя) или -1
     */
    public int mapRowToOutside(int rowIndex){
        return rowIndex;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="свойство tableModel">
    /**
     * Оригинальная таблица
     */
    protected TableModel tableModel = null;

    /**
     * Подписчик событий установлен на оригинал
     */
    protected boolean tableModelListenerStarted = false;

    /**
     * Подписчик событий установлен на оригинал
     * @return true - в текущий момент подписчик на оригинал установлен
     */
    public boolean isSourceListen(){
        return tableModelListenerStarted;
    }

    /**
     * Установить/сбросить подписчика на события оригинальной TableModel
     * @param listen true - установить/false - сбросить
     */
    public void setSourceListen( boolean listen ){
        boolean old = tableModelListenerStarted;
        if( listen!=tableModelListenerStarted ){
            if( listen ){
                attachTMListener();
            }else{
                detachTMListener();
            }
            boolean now = tableModelListenerStarted;
            if( now!=old ){
                firePropertyChange("sourceListen", old, now);
            }
        }
    }

    /**
     * Снятие подписчика событий с оригинала
     */
    protected void detachTMListener(){
        if (this.tableModel != null) {
            this.tableModel.removeTableModelListener(tableModelListener);
            tableModelListenerStarted = false;
        }
    }

    /**
     * Установка подписчика событий на оригинал
     */
    protected void attachTMListener(){
        if (this.tableModel != null) {
            this.tableModel.addTableModelListener(tableModelListener);
            tableModelListenerStarted = true;
        }
    }

    /**
     * Указывает на оригинальную таблицу
     * @return Оригинал таблица
     */
    public TableModel getTableModel(){
        return tableModel;
    }

    /**
     * Указывает на оригинальную таблицу
     * @param tableModel Оригинал таблица
     */
    public void setTableModel(TableModel tableModel) {
        detachTMListener();

        Object old = this.tableModel;
        this.tableModel = tableModel;

        attachTMListener();
        firePropertyChange("tableModel", old, tableModel);
    }
    // </editor-fold>

    /**
     * Делегирует пришедшее событие из оригинальной таблицы к своим подписчикам
     * @param e Оригинальное событие
     * @return Соот. собственное событие
     */
    protected List<TableModelEvent> deletageTMEvent(TableModelEvent e){
        logFine("deletageTMEvent(e)");

        List<TableModelEvent> res = new ArrayList<TableModelEvent>();
        if( e==null )return res;

        int srcFirstRow = e.getFirstRow();
        int srcLastRow = e.getLastRow();
        int etype = e.getType();
        int srcColumn = e.getColumn();

        //boolean delegate = false;

        int outFirstRow = -1;
        int outLastRow = -1;
        int outType = -1;
        int outColumn = -1;

        // Глобальность события:
        //   1 - изменение таблицы целиком (изменения колонок/всех строк/...)
        //         => Передать событие TableModelEvent(source, HEADER_ROW);
        //   2 - изменение набора строк (изменения/добавление/удаление/...)
        //         => По пробовать отобразить и передать соот. событие
        //   3 - изменение отдельный ячеек (изменения)
        //         => По пробовать отобразить и передать соот. событие

        // global
        if( srcFirstRow==0 && srcLastRow==Integer.MAX_VALUE ){
            res.add( new TableModelEvent( WrapTM.this, TableModelEvent.HEADER_ROW ) );
            return res;
        }else if( srcFirstRow<0 ){
            res.add( new TableModelEvent( WrapTM.this, TableModelEvent.HEADER_ROW ) );
            return res;
        }

        // row modifications:
        if( srcFirstRow>=0 && srcLastRow>=srcFirstRow ){
            if( srcColumn>=0 ){
                outColumn = mapColumnToOutside(srcColumn);
                if( outColumn<0 )return null; // нет отображения: inner -> outter = 0
            }

            // строки (outter) которые изменились (insert/update/delete)
            TreeSet<Integer> modifiedRows = new TreeSet<Integer>();
            for( int irow=srcFirstRow; irow<=srcLastRow; irow++ ){
                int orow = mapRowToOutside(irow);
                if( orow<0 )continue;
                modifiedRows.add(orow);
            }

            if( modifiedRows.size()>0 ){
                List<Pair<Integer,Integer>> beginEnd = new ArrayList<Pair<Integer, Integer>>();
                int begin = -1;
                int end = -1;
                int nxt = -1;
                for( int orow : modifiedRows ){
                    if( begin<0 ){
                        begin = orow;
                        end = orow;
                        nxt = orow+1;
                    }else{
                        if( nxt==orow ){
                            nxt = orow+1;
                            end = orow;
                        }else{
                            beginEnd.add(Pair.of(begin, end));
                            begin = orow;
                            end = orow;
                            nxt = orow+1;
                        }
                    }
                }
                if( begin>0 && end>=begin )beginEnd.add(Pair.of(begin, end));

                if( etype==TableModelEvent.UPDATE ){
                    for( Pair<Integer,Integer> p : beginEnd ){
                        begin = p.a();
                        end = p.b();
                        res.add( new TableModelEvent( WrapTM.this, begin, end, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
                    }
                    return res;
                }else if( etype==TableModelEvent.INSERT ){
                    for( Pair<Integer,Integer> p : beginEnd ){
                        begin = p.a();
                        end = p.b();
                        res.add( new TableModelEvent( WrapTM.this, begin, end, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
                    }
                    return res;
                }else if( etype==TableModelEvent.DELETE ){
                    for( Pair<Integer,Integer> p : beginEnd ){
                        begin = p.a();
                        end = p.b();
                        res.add( 0, new TableModelEvent( WrapTM.this, begin, end, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
                    }
                    return res;
                }
            }
        }

        res.clear();
        res.add( new TableModelEvent( WrapTM.this, TableModelEvent.HEADER_ROW ) );
        return res;
    }

    /**
     * Подписчик на события оригинальной таблицы.
     * Делегирует события к своим подписчикам
     * @see #deletageTMEvent
     */
    protected final TableModelListener tableModelListener = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
            if( e==null )return;

            TableModel tm = e.getSource() instanceof TableModel ?
                (TableModel)e.getSource() :
                tableModel;

//            Возможны следущие комбинации
//            TableModelEvent(source);              //  The data, ie. all rows changed
//            = TableModelEvent(source, 0, Integer.MAX_VALUE, ALL_COLUMNS, UPDATE)
//            firstRow = 0, lastRow=Integer.MAX_VALUE, column=ALL_COLUMNS, type=UPDATE
//
//            TableModelEvent(source, HEADER_ROW);  //  Structure change, reallocate TableColumns
//            = TableModelEvent(source, HEADER_ROW, HEADER_ROW, ALL_COLUMNS, UPDATE)
//            HEADER_ROW=-1, ALL_COLUMNS=-1, UPDATE=0
//
//            TableModelEvent(source, 1);           //  Row 1 changed
//            = TableModelEvent(source, 1, 1, ALL_COLUMNS, UPDATE)
//
//            TableModelEvent(source, 3, 6);        //  Rows 3 to 6 inclusive changed
//            = TableModelEvent(source, 3, 6, ALL_COLUMNS, UPDATE)
//
//            TableModelEvent(source, 2, 2, 6);     //  Cell at (2, 6) changed
//            = TableModelEvent(source, 2, 2, 6, UPDATE)
//
//            TableModelEvent(source, 3, 6, ALL_COLUMNS, INSERT); // Rows (3, 6) were inserted
//            TableModelEvent(source, 3, 6, ALL_COLUMNS, DELETE); // Rows (3, 6) were deleted
//
//          Ситуации которые считаются не возможными
//            1. firstRow>=0, lastRow>=0, column>=0, INSERT (вставка ячейки)
//            2. firstRow>=0, lastRow>=0, column>=0, DELETE (удаление ячейки)

            int frow = e.getFirstRow();
            int lrow = e.getLastRow();
            int col = e.getColumn();
            int type = e.getType();

            // Категория
            // -1 - не определенная
            // 1 - изменение строки - вызов onRowUpdated
            // 2 - добавление строки - вызов onRowInserted
            // 3 - удаление строки - вызов onRowDeleted
            // 10 - изменение таблицы - вызов  onTableChanged
            int category = -1;

            List<TableModelEvent> ev = null;

            if( frow==0 && lrow==Integer.MAX_VALUE ){ category = 10; }
            else if( frow>=0 && lrow>=frow && type==TableModelEvent.INSERT ){ category = 2; }
            else if( frow>=0 && lrow>=frow && type==TableModelEvent.DELETE ){ category = 3; }
            else if( frow>=0 && lrow>=frow && type==TableModelEvent.UPDATE ){ category = 1; }

            switch( category ){
                case 1:
                    logFiner("onRowUpdated(e={0}, from={1} to={2})",e,frow,lrow);
                    ev = onRowUpdated( e, frow, lrow );
                    logFinest("onRowUpdated(e={0}, from={1} to={2})={3}",
                        e,frow,lrow,
                        ev!=null ? ev.size() : null
                    );
                    break;
                case 2:
                    logFiner("onRowInserted(e={0}, from={1} to={2})",e,frow,lrow);
                    ev = onRowInserted( e, frow, lrow );
                    logFinest("onRowInserted(e={0}, from={1} to={2})={3}",
                        e,frow,lrow,
                        ev!=null ? ev.size() : null
                    );
                    break;
                case 3:
                    logFiner("onRowDeleted(e={0}, from={1} to={2})",e,frow,lrow);
                    ev = onRowDeleted( e, frow, lrow );
                    logFinest("onRowDeleted(e={0}, from={1} to={2})={3}",
                        e,frow,lrow,
                        ev!=null ? ev.size() : null
                    );
                    break;
                default:
                    logFiner("onTableChanged(e={0}, from={1} to={2})",e,frow,lrow);
                    ev = onTableChanged( e );
                    logFinest("onTableChanged(e={0}, from={1} to={2})={3}",
                        e,frow,lrow,
                        ev!=null ? ev.size() : null
                    );
                    break;
            }

            if( ev!=null ){
                for( TableModelEvent te : ev ){
                    fireTableModelEvent(te);
                }
            }
        }
    };

    /**
     * Вызывается при событии добавления строк в оригинальную таблицу
     * @param e Исходное событие
     * @param firstRow Начальная строка
     * @param lastRow Конечная (включительно) строка
     * @return Событие обвертка или null
     */
    protected List<TableModelEvent> onRowInserted(TableModelEvent e, int firstRow,int lastRow){
        return deletageTMEvent(e);
    }

    /**
     * Вызывается при событии обновления строк в оригинальной таблицы
     * @param e Исходное событие
     * @param firstRow Начальная строка
     * @param lastRow Конечная (включительно) строка
     * @return Событие обвертка или null
     */
    protected List<TableModelEvent> onRowUpdated(TableModelEvent e, int firstRow,int lastRow){
        return deletageTMEvent(e);
    }

    /**
     * Вызывается при событии удаления строк из оригинальной таблицы
     * @param e Исходное событие
     * @param firstRow Начальная строка
     * @param lastRow Конечная (включительно) строка
     * @return Событие обвертка или null
     */
    protected List<TableModelEvent> onRowDeleted(TableModelEvent e, int firstRow,int lastRow){
        return deletageTMEvent(e);
    }

    /**
     * Вызывается при полном изменении оригинальной таблицы включая структуру
     * @param e Исходное событие
     * @return Событие обвертка или null
     */
    protected List<TableModelEvent> onTableChanged(TableModelEvent e){
        return deletageTMEvent(e);
    }
}
