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

import xyz.cofe.collection.IndexSet;
import xyz.cofe.collection.IndexSetBasic;
import xyz.cofe.fn.Pair;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Модель таблицы с фильтрацией строк исходной таблицы
 * @author nt.gocha@gmail.com
 */
public class FilterRowTM
    extends WrapTM
    //implements GetRowToSourceMap, GetSourceToRowMap
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(FilterRowTM.class.getName());
    private static final Level logLevel(){ return Logger.getLogger(FilterRowTM.class.getName()).getLevel(); }

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
        logger.entering(FilterRowTM.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(FilterRowTM.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(FilterRowTM.class.getName(), method, result);
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="rowFilter">
    protected Predicate<RowData> rowFilter = null;
    private RowData rowData = new RowData();

    /**
     * Устанавливает фильтр строк
     * @param filter фильтр строк
     */
    public void setRowFilter(Predicate<RowData> filter) {
        Object old = this.rowFilter;
        this.rowFilter = filter;
        applyFilter();
        firePropertyChange("rowFilter", old, rowFilter);
    }

    /**
     * Возвращает фильтр строк
     * @return фильтр
     */
    public Predicate<RowData> getRowFilter() {
        return rowFilter;
    }
    // </editor-fold>

    protected IndexSet<Integer> source;

    /**
     * Возвращает индексированный набор (IndexSet) исходных строк
     * @return индексированный набор исходных строк
     */
    public synchronized IndexSet<Integer> getSourceIndexSet(){
        return source;
    }

    @Override
    public synchronized int getRowCount() {
        if( source==null && rowFilter!=null && this.tableModel!=null ){
            Runnable r = new Runnable(){
                @Override
                public void run() {
                    applyFilter();
                }
            };
            SwingUtilities.invokeLater(r);
        }

        if( source==null )return super.getRowCount();
        return source.size();
    }

    @Override
    public synchronized int getColumnCount() {
        if( source==null && rowFilter!=null && this.tableModel!=null ){
            Runnable r = new Runnable(){
                @Override
                public void run() {
                    applyFilter();
                }
            };
            SwingUtilities.invokeLater(r);
        }

        return super.getColumnCount();
    }

    @Override
    public synchronized int mapColumnToInside(int columnIndex) {
        if( source==null && rowFilter!=null && this.tableModel!=null ){
            Runnable r = new Runnable(){
                @Override
                public void run() {
                    applyFilter();
                }
            };
            SwingUtilities.invokeLater(r);
        }

        return super.mapColumnToInside(columnIndex);
    }

    @Override
    public synchronized int mapRowToInside(int rowIndex) {
        if( source==null && rowFilter!=null && this.tableModel!=null ){
            Runnable r = new Runnable(){
                @Override
                public void run() {
                    applyFilter();
                }
            };
            SwingUtilities.invokeLater(r);
        }

        if( source!=null ){
            int maxRow = source.size()-1;
            if( rowIndex>maxRow || rowIndex<0 )return -1;
            return source.get(rowIndex);
        }
        return super.mapRowToInside(rowIndex);
    }

    @Override
    public synchronized int mapRowToOutside(int rowIndex) {
        if( source==null && rowFilter!=null && this.tableModel!=null ){
            Runnable r = new Runnable(){
                @Override
                public void run() {
                    applyFilter();
                }
            };
            SwingUtilities.invokeLater(r);
        }

        if( source!=null ){
            int idx = source.indexOf(rowIndex);
            return idx >= 0 ? idx : -1;
        }
        return rowIndex;
    }

    /**
     * Фильтрует все строки исходной модели
     */
    public void applyFilter(){
        applyFilter( true );
    }

    /**
     * Фильтрует все строки исходной модели
     * @param fireAllChanged сгенерировать сообщение о измении всех строк таблицы
     */
    public synchronized void applyFilter(boolean fireAllChanged){
        logFine("applyFilter({0}), filter setted={1}", fireAllChanged, rowFilter!=null);

        if( source!=null )source.clear();
        source = null;

        if( rowFilter!=null && tableModel!=null ){
            source = buildSourceIndex();
        }

        if( fireAllChanged )fireAllChanged();
    }

    /**
     * Проверка на включение исходной строки в результирующий набор строк - т.е. фильтрация
     * @param sourceRow исходная строка
     * @return true - удовлетворяет фильтру
     */
    public synchronized boolean filterSource( int sourceRow ){
        if( rowFilter!=null && tableModel!=null ){
            rowData.setTableModel(tableModel);
            rowData.setRowIndex(sourceRow);
            return rowFilter.test(rowData);
        }else{
            return true;
        }
    }

    /**
     * Создает индексированный набор исходных строк
     * @return индексированный набор исходных строк
     */
    public synchronized IndexSetBasic<Integer> buildSourceIndex(){
        IndexSetBasic<Integer> source = new IndexSetBasic<>();
        if( rowFilter!=null && tableModel!=null ){
            rowData.setTableModel(tableModel);
            for( int srcRi=0; srcRi<tableModel.getRowCount(); srcRi++ ){
                rowData.setRowIndex(srcRi);
                if( rowFilter.test(rowData) ){
                    source.add(srcRi);
                }
            }
        }
        return source;
    }

    /**
     * Указывает исходную модель таблицы, фильтрует текущее содержание таблицы
     * @param tableModel исходная модель таблицы
     */
    @Override
    public synchronized void setTableModel(TableModel tableModel) {
        super.setTableModel(tableModel);
        rowData.setTableModel(tableModel);
        logFiner("setTableModel( {0} )", tableModel);
        applyFilter();
    }

    /**
     * Сдвиг индексов при вставке исходных
     * @param from  исходный индекс - начало
     * @param toInc исходный индекс - конец
     */
    protected synchronized void shiftOnInsertIndexes( int from, int toInc ){
        logFine("shiftOnInsertIndexes({0},{1})", from,toInc);

        if( source==null )return;

        final int icnt = toInc - from + 1;
        if( icnt<=0 )return;

        // требуется сдвинуть значения которые >= from на велечину icnt
        final LinkedHashMap<Integer,Integer> updates = new LinkedHashMap<>();

        Pair<Integer,Integer> tailStart = source.tailEntry(from, false, 0, source.size());
        if( tailStart==null )return;

        source.eachByIndex(source.size(), tailStart.a(), (Integer idx, Integer val) -> {
                updates.put(val, val+icnt);
        });

        for( Map.Entry<Integer,Integer> en : updates.entrySet() ){
            int k = en.getKey();
            int v = en.getValue();
            if( k!=v ){
                int oldIdx = source.remove(k);
                int newIdx = source.add(v);
                logFinest("shift [{0}]={1} => [{2}]={3}", oldIdx, k, newIdx, v);
            }
        }
    }

    /**
     * Сдвиг индексов при удалении исходных
     * @param from  исходный индекс - начало
     * @param toInc исходный индекс - конец
     * @param deletedOutterIndexes удаление исходящих индексов
     */
    protected synchronized void shiftOnDeleteIndexes( int from, int toInc, Consumer<Integer> deletedOutterIndexes ){
        logFine("shiftOnDeleteIndexes({0},{1})", from,toInc);
        if( source==null )return;

        final int icnt = toInc - from + 1;
        if( icnt<=0 )return;

        final LinkedHashSet<Integer> removedDI = new LinkedHashSet<>();

        source.eachByValue(toInc, true, from, true, (Integer idx, Integer val) -> {
            removedDI.add(idx);
        });

        for( Integer removeDI : removedDI ){
            Integer si = source.removeByIndex(removeDI);
            logFinest("removed [{0}]={1}", removeDI, si);
        }


        // требуется сдвинуть значения которые >= toInc на велечину icnt
        final LinkedHashMap<Integer,Integer> updates = new LinkedHashMap<>();

        logFinest("shiftOnDeleteIndexes debug1 source.size = {0}", source.size());

        Pair<Integer,Integer> tailStart = source.tailEntry(toInc, false, 0, source.size());
        if( tailStart!=null ){
            source.eachByIndex(source.size(), tailStart.a(), (Integer idx, Integer val) -> {
                updates.put(val, val-icnt);
            });

            /* bad code ?
            for( Map.Entry<Integer,Integer> en : updates.entrySet() ){
                int oldVal = en.getKey();
                int newVal = en.getValue();
                if( oldVal!=newVal ){
                    int oldIdx = source.remove(oldVal);
                    int newIdx = source.add(newVal);
                    logFinest("shift [{0}]={1} => [{2}]={3}", oldIdx, oldVal, newIdx, newVal);
                }
            }
            */

            for( Map.Entry<Integer,Integer> en : updates.entrySet() ){
                int oldVal = en.getKey();
                int newVal = en.getValue();
                if( oldVal!=newVal ){
                    int oldIdx = source.remove(oldVal);
                    logFinest("shift [{0}]={1} => delete", oldIdx, oldVal );
                }
            }

            for( Map.Entry<Integer,Integer> en : updates.entrySet() ){
                int oldVal = en.getKey();
                int newVal = en.getValue();
                if( oldVal!=newVal ){
                    int newIdx = source.add(newVal);
                    logFinest("shift [{0}]={1} => insert", newIdx, newVal);
                }
            }
        }

        logFinest("shiftOnDeleteIndexes debug2 source.size = {0}", source.size());

        if( deletedOutterIndexes!=null ){
            for( Integer di : removedDI ){
                deletedOutterIndexes.accept(di );
            }
        }
    }

    @Override
    protected synchronized List<TableModelEvent> onRowInserted(TableModelEvent e, int firstRow,int lastRow){
        List<TableModelEvent> evs = new ArrayList<>();

        logFine("onRowInserted({0},{1})", firstRow, lastRow);
        logFiner("source.size={0}", source==null ? "null" : source.size());

        if( source==null && rowFilter!=null ){
            applyFilter(false);
            evs.add(new TableModelEvent(this));
            return evs;
        }

        if( tableModel==null )return deletageTMEvent(e);
        if( source==null )return deletageTMEvent(e);
        if( lastRow<firstRow )return deletageTMEvent(e);

        shiftOnInsertIndexes(firstRow, lastRow);

        TreeSet<Integer> inserted = new TreeSet<>();
        //List<TableModelEvent> evs = new ArrayList<>();

        int icnt = lastRow - firstRow + 1;
        if( icnt>0 ){
            logFiner("source insert count {0}", icnt);
            for( int si=firstRow; si<=lastRow; si++ ){
                rowData.setRowIndex(si);
                if( rowFilter==null || rowFilter.test(rowData) ){
                    int di = source.add(si);
                    inserted.add(di);
                    logFinest("inserted [{0}]={1}", di, si);
                }
            }
        }

        int rangeStart = -1;
        int rangeEnd = -1;
        int lidx = -1;
        int i = -1;

        for( Integer idx : inserted ){
            i++;
            if( i==0 ){
                lidx = idx;
                rangeStart = idx;
                rangeEnd = idx;
                logFinest("first range [{0}..", rangeStart);
            }else{
                int didx = Math.abs(lidx - idx);
                if( didx>1 ){
                    rangeEnd = lidx;

                    TableModelEvent eins = new TableModelEvent(
                        this, rangeStart, rangeEnd, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);

                    logFinest("insert range [{0}..{1}] cnt={2}",
                        rangeStart, rangeEnd, Math.abs(rangeEnd-rangeStart)+1);

                    evs.add(eins);

                    rangeStart = idx;
                }
            }

            lidx = idx;
        }

        if( rangeStart<=lidx && rangeStart>=0 ){
            TableModelEvent eins = new TableModelEvent(
                this, rangeStart, rangeEnd, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);

            logFinest("insert range [{0}..{1}] cnt={2}",
                rangeStart, rangeEnd, Math.abs(rangeEnd-rangeStart)+1);

            evs.add(eins);
        }

        logFiner("after source.size={0}", source==null ? "null" : source.size());

        return evs;
    }

    /**
     * Производит фильтрацию строк исходной таблицы, вызывается когда произошло событие обновления строк исходной таблицы
     * @param sourceFirstRow Начало диапазона обновленных исходных строк
     * @param sourceLastRow Конец (включительно) диапазона обновленных исходных строк
     * @return События/уведомления о измении состава строк текущей таблицы
     */
    public synchronized List<TableModelEvent> processRowUpdated( int sourceFirstRow, int sourceLastRow ){
        List<TableModelEvent> evs = new LinkedList<>();

        if( tableModel==null )return null;
        if( sourceLastRow<sourceFirstRow) return null;

        // Список src который надо включить
        TreeSet<Integer> incList = new TreeSet<>();

        // Список src который надо исключить
        TreeSet<Integer> decList = new TreeSet<>();

        // Список src который надо обновить
        TreeSet<Integer> updList = new TreeSet<>();

        if( rowFilter!=null && source!=null ){
            for( int srcRi = sourceFirstRow; srcRi<=sourceLastRow; srcRi++ ){
                rowData.setRowIndex(srcRi);
                boolean inc = rowFilter.test(rowData);
                if( inc ){
                    if( !source.exists(srcRi) ){
                        incList.add(srcRi);
                    }else{
                        updList.add(srcRi);
                    }
                }else{
                    if( source.exists(srcRi) ){
                        decList.add(srcRi);
                    }
                }
            }
        }else{
            TableModelEvent e = new TableModelEvent(
                this, sourceFirstRow, sourceLastRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

            evs.add(e);
            return evs;
        }

        if( source!=null ){
            // Обработка списка update
            for( Integer srcRi : updList ){
                int di = source.indexOf(srcRi);
                logFinest("source.updated di={0} si={1}",di,srcRi);

                TableModelEvent e = new TableModelEvent(
                    this, di, di, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

                evs.add(e);
            }

            // Обработка списка delete
            for( Integer si : decList.descendingSet() ){
                Integer di = source.remove(si);

                TableModelEvent e = new TableModelEvent(
                    this, di, di, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
                evs.add(e);

                logFinest("source.removed di={0} si={1}",di,si);
                logFinest("processRowUpdated({0},{1}) - fire row delete {2} -> {3}", sourceFirstRow, sourceLastRow, di, si);
            }

            // Обработка списка insert
            for( Integer si : incList ) {
                int di = source.add(si);
                logFinest("source.inserted di={0} si={1}",di,si);

                TableModelEvent e = new TableModelEvent(
                    this, di, di, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
                evs.add(e);
            }

            return evs;
        }

        return null;
    }

    @Override
    protected synchronized List<TableModelEvent> onRowUpdated(TableModelEvent e, int firstRow,int lastRow){
        logFine("onRowUpdated({0},{1})", firstRow, lastRow);

        if( source==null && rowFilter!=null ){
            List<TableModelEvent> evs = new LinkedList<>();
            applyFilter(false);
            evs.add(new TableModelEvent(this));
            return evs;
        }

        List<TableModelEvent> evs = processRowUpdated(firstRow, lastRow);
        if( evs==null ){
            return deletageTMEvent(e);
        }

        return evs;
    }

    @Override
    protected synchronized List<TableModelEvent> onRowDeleted(TableModelEvent onDeleteEvent, int firstRow,int lastRow){
        logFine("onRowDeleted({0},{1})", firstRow, lastRow);
        logFiner("source.size={0}", source==null ? "null" : source.size());

        if( source==null ){
            applyFilter(true);
            return new LinkedList<>();
        }
        if( tableModel==null )return deletageTMEvent(onDeleteEvent);
        if( lastRow<firstRow) return deletageTMEvent(onDeleteEvent);

        return syncOnDelete(
            new SyncDeleteOpts().
                fireDeleted(true).
                fireInserted(false).
                fireUpdated(false).
                range(firstRow, lastRow)
        );
    }

    protected static class SyncOpts<T extends SyncOpts> {
        public boolean fireDeleted = true;
        public T fireDeleted(boolean v){
            this.fireDeleted = v;
            return (T)this;
        }
        public boolean fireUpdated = true;
        public T fireUpdated(boolean v){
            this.fireUpdated = v;
            return (T)this;
        }
        public boolean fireInserted = true;
        public T fireInserted(boolean v){
            this.fireInserted = v;
            return (T)this;
        }
    }

    protected static class SyncDeleteOpts extends SyncOpts<SyncDeleteOpts> {
        public int firstRow;
        public int lastRow;
        public SyncDeleteOpts range(int first,int last){
            this.firstRow = first;
            this.lastRow = last;
            return this;
        }
    }

    protected List<TableModelEvent> syncOnDelete(SyncDeleteOpts sopts){
        if( sopts==null )sopts = new SyncDeleteOpts();

        final List<TableModelEvent> evs = new LinkedList<>();

        // sync block
        IndexSet<Integer> newIdx = buildSourceIndex();
        IndexSet<Integer> oldIdx = new IndexSetBasic<>();
        if( source!=null )source.each( x -> oldIdx.add( x ) );

        if( sopts.fireDeleted && sopts.firstRow<=sopts.lastRow && sopts.firstRow>=0 ){
            while(true){
                int deleteIdx = -1;
                for( int i=oldIdx.size()-1; i>=0; i-- ){
                    int trgtIdx = oldIdx.get(i);
                    if( trgtIdx>=sopts.firstRow && trgtIdx<=sopts.lastRow ){
                        deleteIdx = i;
                        break;
                    }
                }
                if( deleteIdx>=0 ){
                    TableModelEvent e = new TableModelEvent(
                        this, deleteIdx, deleteIdx, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);

                    evs.add(e);

                    oldIdx.removeByIndex(deleteIdx);
                    continue;
                }
                break;
            }
        }

        if(sopts.fireUpdated){
            for( int i=0; i<Math.min(newIdx.size(),oldIdx.size()); i++ ){
                Integer oldSrcRi = oldIdx.get(i);
                Integer newSrcRi = newIdx.get(i);
                if( !Objects.equals(oldSrcRi, newSrcRi) ){
                    TableModelEvent e = new TableModelEvent(
                        this, i, i, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
                    evs.add(e);
                }
            }
        }

        //TreeSet<Integer> inserted = new TreeSet<>();

        for( int i=0; i<newIdx.size(); i++ ){
            Integer srcRi = newIdx.get(i);
            if( !oldIdx.exists(srcRi) ){
                //inserted.add(i);
                if( sopts.fireInserted ){
                    TableModelEvent e = new TableModelEvent(
                        this, i, i, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);

                    evs.add(e);
                }
            }
        }

        source = newIdx;

        return evs;
    }

    @Override
    protected List<TableModelEvent> onTableChanged(TableModelEvent e){
        logFine("onTableChanged({0},{1})");
        applyFilter();
        return null;
    }
}
