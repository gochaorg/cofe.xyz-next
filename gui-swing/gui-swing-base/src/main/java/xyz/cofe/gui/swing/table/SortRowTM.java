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

package xyz.cofe.gui.swing.table;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import xyz.cofe.collection.SortInsert;
import xyz.cofe.collection.SortInsertDefault;
import xyz.cofe.fn.Fn1;
import xyz.cofe.gui.swing.table.impl.SortRowTMImpl;
import xyz.cofe.text.out.Output;

/**
 * Модель таблицы для сортировки исходной модели
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class SortRowTM extends WrapTM
{
//    private int fitnessOnInsertEach(){
//        Integer v = fitnessOnInsertEach;
//        if( v!=null )return v;
//        synchronized( SortRowTM.class ){
//        }
//    }

    // TODO Дописывать все

    private static boolean eq( Object a, Object b ){
        if( a==null && b==null )return true;
        if( a==null && b!=null )return false;
        if( a!=null && b==null )return false;
        return a.equals(b);
    }

    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(SortRowTM.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(SortRowTM.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(SortRowTM.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(SortRowTM.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(SortRowTM.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(SortRowTM.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(SortRowTM.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    protected TreeMap<Integer,Integer> row2srcMap = null;
    protected TreeMap<Integer,Integer> src2rowMap = null;

    protected RowData rowData = new RowData();

    //<editor-fold defaultstate="collapsed" desc="rowComparator">
    private Comparator<RowData> rowComparator = null;

    /**
     * Возвращает функцию сравнения используемую для сортировки
     * @return функция сравнения
     */
    public synchronized Comparator<RowData> getRowComparator() {
        return rowComparator;
    }

    /**
     * Указыавет функцию сравнения используемую для сортировки
     * @param rowComparator функция сравнения
     */
    public synchronized void setRowComparator(Comparator<RowData> rowComparator) {
        this.rowComparator = rowComparator;
        applySort();
        fireAllChanged();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getRowCount()">
    @Override
    public synchronized int getRowCount() {
        if( row2srcMap==null )return super.getRowCount();
        return row2srcMap.size();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getColumnCount()">
    @Override
    public synchronized int getColumnCount() {
        return super.getColumnCount();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="shiftOnInsertIndexes()">
    /**
     * Сдвиг индексов при вставке исходных
     * @param from  исходный индекс - начало
     * @param toInc исходный индекс - конец
     */
    protected synchronized void shiftOnInsertIndexes( int from, int toInc ){
        if( row2srcMap==null )return;

        logFine( "shiftOnInsertIndexes( {0}, {1} )",from,toInc );

        int icnt = toInc - from + 1;
        if( icnt<=0 )return;

        for( Map.Entry<Integer,Integer> me : row2srcMap.entrySet() ){
            Integer si = me.getValue();
            if( si>=from ){
                me.setValue(si+icnt);
//                log
            }
        }

        if( src2rowMap == null )src2rowMap = new TreeMap<Integer,Integer>();
        src2rowMap.clear();
        for( Map.Entry<Integer,Integer> me : row2srcMap.entrySet() ){
            src2rowMap.put(me.getValue(), me.getKey());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="insertRow()">
    /**
     * Вставка внутренней строки
     * @param di внешний индекс
     * @param si внутренний (исходный) индекс
     */
    protected synchronized void insertRow( int di, int si ){
        if( row2srcMap==null )return;

        logFine( "insertRow( {0}, {1} )",di,si );

        TreeMap<Integer,Integer> nrs = new TreeMap<Integer,Integer>();

        // обновление di2 = di1 + 1 | di1 >= di
        for( Integer di1 : row2srcMap.descendingKeySet() ){
            if( di1>=di ){
                Integer di2 = di1 + 1;
                Integer si1 = row2srcMap.get(di1);
                nrs.put(di2, si1);
            }else{
                Integer si1 = row2srcMap.get(di1);
                nrs.put(di1, si1);
            }
        }
        nrs.put(di, si);
        row2srcMap = nrs;

        if( src2rowMap==null )src2rowMap = new TreeMap<Integer,Integer>();
        src2rowMap.clear();

        //row2srcMap.forEach( (di1, si1) -> { src2rowMap.put(si1, di1); } );
        for( Map.Entry<Integer,Integer> enR2S : row2srcMap.entrySet() ){
            Integer di1 = enR2S.getKey();
            Integer si1 = enR2S.getValue();
            src2rowMap.put(si1, di1);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="shiftOnDeleteIndexes()">
    /**
     * Сдвиг индексов при удалении исходных
     * @param from  исходный индекс - начало
     * @param toInc исходный индекс - конец
     * @param deletedOutterIndexes удаление исходящих индексов
     */
    protected synchronized void shiftOnDeleteIndexes( int from, int toInc, Consumer<Integer> deletedOutterIndexes ){
        if( row2srcMap==null )return;

        logFine( "shiftOnDeleteIndexes( {0}, {1} )",from,toInc );

        int icnt = toInc - from + 1;
        if( icnt<=0 )return;

        LinkedHashSet<Integer> removedDI = new LinkedHashSet<Integer>();

        for( Map.Entry<Integer,Integer> me : row2srcMap.entrySet() ){
            Integer di = me.getKey();
            Integer si = me.getValue();
            if( si < from )continue;
            if( si <= toInc ){
                removedDI.add(di);
            }else{
                Integer si2 = si - icnt;
                me.setValue(si2);
            }
        }

        //removedDI.forEach( di -> {
        for( Integer di : removedDI ){
            row2srcMap.remove(di);
            if( deletedOutterIndexes!=null )
                deletedOutterIndexes.accept( di );
        }// );

        // reindex row2srcMap
        AtomicInteger ni = new AtomicInteger(0);
        TreeMap<Integer,Integer> nrow2srcMap = new TreeMap<Integer,Integer>();
        for( Map.Entry<Integer,Integer> enR2S : row2srcMap.entrySet() ){
            //row2srcMap.forEach( (di,si) -> {
            Integer di = enR2S.getKey();
            Integer si = enR2S.getValue();
            nrow2srcMap.put(ni.get(), si);
            ni.incrementAndGet();
        }//);
        row2srcMap = nrow2srcMap;

        if( src2rowMap == null )src2rowMap = new TreeMap<Integer,Integer>();
        src2rowMap.clear();
        for( Map.Entry<Integer,Integer> me : row2srcMap.entrySet() ){
            src2rowMap.put(me.getValue(), me.getKey());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="checkDestIndexesFast()">
    /**
     * Быстрая проверка ключей rows2srcMap, на предмет заполнения от 0 до N
     */
    protected synchronized void checkDestIndexesFast(){
        if( row2srcMap==null )return;

        int size = row2srcMap.size();
        if( size<1 )return;

        int fk = row2srcMap.firstKey();
        if( fk!=0 )throw new Error("checkDestIndexesFast fail, fk="+fk);

        int lk = row2srcMap.lastKey();
        if( lk!=(size-1) )
            throw new Error("checkDestIndexesFast fail, lk="+lk+" size="+size);
    }
    //</editor-fold>

    /**
     * Проверка валидности индексов. <br>
     * Индексы должны:
     * <ul>
     * <li>row2srcMap - ключи начинаться 0</li>
     * <li>row2srcMap - ключи закачиваться row2srcMap.size()-1</li>
     * <li>row2srcMap - ключи не иметь пропусков</li>
     * <li>row2srcMap - значения ссылаться на src2rowMap</li>
     * <li>row2srcMap - значения ссылаться на src2rowMap, те ссылаться на изначальные ключи row2srcMap</li>
     * <li>row2srcMap.size() = src2rowMap.size()</li>
     * </ul>
     * @return true - индексы валидны
     */
    protected synchronized boolean isIndexesValid(){
        if( row2srcMap==null ){
            if( src2rowMap!=null ){
                logWarning("isIndexesValid row2srcMap==null && src2rowMap!=null");
                return false;
            }
        }
        if( src2rowMap==null ){
            if( row2srcMap!=null ){
                logWarning("isIndexesValid row2srcMap!=null && src2rowMap==null");
                return false;
            }
        }
        if( src2rowMap==null && row2srcMap==null )
            return true;

        if( row2srcMap.size()!=src2rowMap.size() ){
            logWarning("isIndexesValid row2srcMap.size() != src2rowMap.size()");
            return false;
        }

        if( row2srcMap.size()<1 )return true;

        Integer rs_fk = row2srcMap.firstKey();
        Integer rs_lk = row2srcMap.lastKey();

        if( rs_fk==null ){
            logWarning("row2srcMap.firstKey() == null");
            return false;
        }

        if( rs_lk==null ){
            logWarning("row2srcMap.lastKey() == null");
            return false;
        }

        if( rs_fk!=0 ){
            logWarning("row2srcMap.lastKey() != 0");
            return false;
        }

        if( rs_lk!=(row2srcMap.size()-1) ){
            logWarning("row2srcMap.lastKey() != (row2srcMap.size()-1)");
            StringWriter sw = new StringWriter();
            dump( new Output(sw,true) );
            logWarning("dump:\n{0}", sw.toString());
            return false;
        }

        int idx = -1;
        for( Map.Entry<Integer,Integer> rs_e : row2srcMap.entrySet() ){
            idx++;
            Integer rs_di = rs_e.getKey();
            Integer rs_si = rs_e.getValue();
            if( rs_di==null || rs_di!=idx ){
                logWarning("row2srcMap key={0} waitKey={1} key null or not equ wait", rs_di, idx);
                StringWriter sw = new StringWriter();
                dump( new Output(sw,true) );
                logWarning("dump:\n{0}", sw.toString());
                return false;
            }
            if( rs_si==null ){
                logWarning("row2srcMap key={0} value={1} value null", rs_di, rs_si);
                StringWriter sw = new StringWriter();
                dump( new Output(sw,true) );
                logWarning("dump:\n{0}", sw.toString());
                return false;
            }

            if( !src2rowMap.containsKey(rs_si) ){
                logWarning("src2rowMap key={0} not found", rs_si );
                StringWriter sw = new StringWriter();
                dump( new Output(sw,true) );
                logWarning("dump:\n{0}", sw.toString());
                return false;
            }

            Integer sr_di = src2rowMap.get(rs_si);
            if( sr_di==null ){
                logWarning("src2rowMap key={0} value={1} value null", rs_si, sr_di );
                StringWriter sw = new StringWriter();
                dump( new Output(sw,true) );
                logWarning("dump:\n{0}", sw.toString());
                return false;
            }

            if( !eq(sr_di, rs_di) ){
                logWarning("src2rowMap key={0} value={1} reverse fail (rs {2} -> {3} sr {3} -> {4})",
                    rs_si, sr_di, rs_di, rs_si, sr_di
                );
                StringWriter sw = new StringWriter();
                dump( new Output(sw,true) );
                logWarning("dump:\n{0}", sw.toString());
                return false;
            }
        }

        return true;
    }

    /**
     * Создание дампа индексов, отображает таблицы соответствия собественных индексов (строк) на исходные и обратно
     */
    private synchronized void dump(){
        Output out = new Output(System.out);
        dump(out);
    }

    /**
     * Создание дампа индексов, отображает таблицы соответствия собественных индексов (строк) на исходные и обратно
     * @param xout поток для вывода дампа
     */
    private synchronized void dump( Output xout){
        if( xout==null )xout = new Output(System.out);
        Output out = xout;
        out.setLinePrefix("dump|");

        if( row2srcMap!=null && src2rowMap!=null ){
            out.template("row2srcMap(${rsc}) src2rowMap(${src})")
                .bind("rsc", row2srcMap.size())
                .bind("src", src2rowMap.size())
                .println();

            for( Map.Entry<Integer,Integer> enR2S : row2srcMap.entrySet() ){
                Integer di1 = enR2S.getKey();
                Integer si1 = enR2S.getValue();
                //row2srcMap.forEach( (di1,si1) -> {
                Integer si2 = src2rowMap.containsKey(si1) ? si1 : null;
                Integer di2 = si2!=null ? src2rowMap.get(si2) : null;
                out.template("d${di1:3} -> s${si1:3} | d${di2:3} <- s${si2:3} | ${ok}")
                    .bind("di1", di1)
                    .bind("di2", di2)
                    .bind("si1", si1)
                    .bind("si2", si2)
                    .bind("ok", si2!=null && di2!=null &&
                        si1!=null && di1!=null &&
                        si1 == si2 && di1 == di2 )
                    .println();
            }//);
        }else{
            out.println("row2srcMap = "+row2srcMap);
            out.println("src2rowMap = "+src2rowMap);
        }

        out.flush();
    }

    //<editor-fold defaultstate="collapsed" desc="map rows">
    //<editor-fold defaultstate="collapsed" desc="mapColumnToInside(column):int">
    @Override
    public synchronized int mapColumnToInside(int columnIndex) {
        return super.mapColumnToInside(columnIndex);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="mapRowToInside(column):int">
    @Override
    public synchronized int mapRowToInside(int rowIndex) {
        if( row2srcMap!=null ){
            if( row2srcMap.containsKey(rowIndex) )
                return row2srcMap.get(rowIndex);
            return -1;
        }
        return super.mapRowToInside(rowIndex);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="mapRowToOutside(row):int">
    @Override
    public synchronized int mapRowToOutside(int rowIndex) {
        if( src2rowMap==null )return super.mapRowToOutside(rowIndex);
        if( !src2rowMap.containsKey(rowIndex) )return -1;
        return src2rowMap.get(rowIndex);
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setTableModel(model)">
    /**
     * Указывает исходную модель таблицы
     * @param tableModel исходная модель
     * @see #applySort()
     */
    @Override
    public synchronized void setTableModel(TableModel tableModel) {
        super.setTableModel(tableModel);
        rowData.setTableModel(tableModel);
        applySort();
        fireAllChanged();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="applySort()">
    /**
     * Применяет сортировку исходных данных, сортируя все строки исходной таблицы
     * @see #setTableModel(TableModel)
     */
    public synchronized void applySort(){
        if(row2srcMap!=null)row2srcMap.clear();
        row2srcMap = null;

        if( src2rowMap!=null )src2rowMap.clear();
        src2rowMap = null;

        if( rowComparator==null )return;
        if( tableModel==null )return;

        int rowCo = tableModel.getRowCount();

        List<Integer> indexes = new ArrayList<Integer>();
        for( int i=0; i<rowCo; i++ ){
            indexes.add(i);
        }

//        Comparator<Integer> srcRowCmp = new Comparator<Integer>() {
//            @Override
//            public int compare(Integer row1, Integer row2) {
//                RowData rowA = new RowData();
//                rowA.setTableModel(tableModel);
//                rowA.setRowIndex(row1);
//
//                RowData rowB = new RowData();
//                rowB.setTableModel(tableModel);
//                rowB.setRowIndex(row2);
//
//                int res = rowComparator.compare(rowA, rowB);
//                return res;
//            }
//        };

        Comparator<Integer> srcRowCmp = createComparator(tableModel, rowComparator);

        Collections.sort(indexes, srcRowCmp);

        src2rowMap = new TreeMap<Integer, Integer>();
        row2srcMap = new TreeMap<Integer, Integer>();
        for( int outRowIndex = 0; outRowIndex<indexes.size(); outRowIndex++ ){
            int innerRowIndex = indexes.get(outRowIndex);
            row2srcMap.put(outRowIndex, innerRowIndex);
            src2rowMap.put(innerRowIndex, outRowIndex);
        }
    }
    //</editor-fold>

    /**
     * Создает функцию сортировки индексов строк
     * @param tm модель таблицы, содержащаяя исходные строки
     * @param cmpr функция срвнения строк
     * @return функция сравнения индексов
     */
    protected Comparator<Integer> createComparator( TableModel tm, Comparator<RowData> cmpr ){
        if (  tm== null) { throw new IllegalArgumentException("tm==null"); }
        if (cmpr== null) { throw new IllegalArgumentException("cmpr==null"); }

        return createComparator(tm, tm, cmpr);
    }

    /**
     * Создает функцию сортировки индексов строк
     * @param tm1 модель таблицы 1
     * @param tm2 модель таблицы 1
     * @param cmpr функция сравнения
     * @return функция сравнения
     */
    private Comparator<Integer> createComparator( final TableModel tm1, final TableModel tm2, final Comparator<RowData> cmpr ){
        if (tm1 == null) {throw new IllegalArgumentException("tm1==null");}
        if (tm2 == null) {throw new IllegalArgumentException("tm2==null");}
        if (cmpr== null) {throw new IllegalArgumentException("cmpr==null");}

        //return (Integer r1, Integer r2) -> {
        return new Comparator<Integer>() {
            @Override
            public int compare(Integer r1, Integer r2) {

                RowData rd1 = new RowData();
                rd1.setTableModel(tm1);
                rd1.setRowIndex(r1);

                RowData rd2 = new RowData();
                rd2.setTableModel(tm2);
                rd2.setRowIndex(r2);

                return cmpr.compare(rd1, rd2);
            }};
    }

    /**
     * Функция проверки упорядонночести
     * @param skip Функция пропуска
     * @return 0 - упорядочены значения, в порядке возрастания, меньше нуля - мера беспорядка.
     */
    protected synchronized int fitness(Fn1<Integer,Boolean> skip){
        if( rowComparator==null )return 0;
        if( row2srcMap==null )return 0;
        if( row2srcMap.size()<2 )return 0;
        int i = -1;

        RowData prevrd = new RowData();
        prevrd.setTableModel(this);

        RowData currrd = new RowData();
        currrd.setTableModel(this);

        int fit=0;

        for( Integer di : row2srcMap.keySet() ){
            if( skip!=null && skip.apply(di) )continue;

            i++;
            currrd.setRowIndex(di);
            if( i>0 ){
                int cmp = rowComparator.compare(prevrd, currrd);
                if( cmp>0 ){
                    fit--;

                    Integer di1 = prevrd.getRowIndex();
                    Integer di2 = currrd.getRowIndex();

                    Object objd1 = prevrd.getValue(0);
                    Object objd2 = currrd.getValue(0);

                    //System.out.println("cmp="+cmp+" fit="+fit+" "+di1+" ("+objd1+") "+di2+" ("+objd2+")");
                }
            }
            prevrd.setRowIndex(di);
        }

        return fit;
    }

    @Override
    protected synchronized List<TableModelEvent> onTableChanged(TableModelEvent e) {
        // полная сортировка
        applySort();

        // уведомляем о измении всей таблицы
        List<TableModelEvent> lev = new ArrayList<TableModelEvent>();
        lev.add(new TableModelEvent(this));
//        lev.add(new TableModelEvent(this,0,Integer.MAX_VALUE,TableModelEvent.ALL_COLUMNS,TableModelEvent.UPDATE));
        return lev;
    }

    private long onRowUpdatedCall = 0;

    @Override
    protected synchronized List<TableModelEvent> onRowUpdated(TableModelEvent e, final int firstRow, final int lastRow) {
        onRowUpdatedCall++;

        // полная сортировка
        // applySort();

        // уведомляем о измении таблицы
        List<TableModelEvent> lev = new ArrayList<TableModelEvent>();
        // lev.add(new TableModelEvent(this));

//        checkDestIndexesFast();
        if( !isIndexesValid() ){
            applySort();
            // уведомляем о измении всей таблицы
            lev.add(new TableModelEvent(this));
            return lev;
        }

        //int fit = fitness( skipidx -> skipidx>= firstRow && skipidx<=lastRow );
        int eachNUpdated = SortRowTMImpl.getFitnessOnUpdateEach();
        if( eachNUpdated>0 ){
            boolean check = false;
            if( eachNUpdated==1 ){
                check = true;
            }else{
                check = (onRowUpdatedCall % eachNUpdated) == 0;
            }
            if( check ){
                int fit = fitness( new Fn1<Integer,Boolean>() {
                    @Override
                    public Boolean apply(Integer skipidx) {
                        return skipidx>= firstRow && skipidx<=lastRow;
                    }
                } );

                if( fit!=0 || tableModel==null ){
                    applySort();

                    // уведомляем о измении всей таблицы
                    lev.add(new TableModelEvent(this));
                    return lev;
                }
            }
        }

        // Удаление измененных
        lev.addAll(
            onRowDeleted(e, firstRow, lastRow)
        );

        // Вставка измененных
        lev.addAll(
            onRowInserted(firstRow, lastRow, false, false)
        );

        return lev;
    }

    @Override
    protected synchronized List<TableModelEvent> onRowDeleted(TableModelEvent e, int firstRow, int lastRow) {
        // полная сортировка
        // applySort();

        List<TableModelEvent> lev = new ArrayList<TableModelEvent>();

        if( !isIndexesValid() ){
            applySort();
            // уведомляем о измении всей таблицы
            lev.add(new TableModelEvent(this));
            return lev;
        }

        final TreeSet<Integer> deleted = new TreeSet<Integer>();

        //shiftOnDeleteIndexes(firstRow, lastRow, di -> deleted.add(di) );
        shiftOnDeleteIndexes(firstRow, lastRow, new Consumer<Integer>() {
            @Override
            public void accept(Integer di) {
                deleted.add( di );
            }
        } );

        for( Integer di : deleted.descendingSet() ){
            // fireRowsDeleted(di, di)
            lev.add(new TableModelEvent(this, di, di, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
        }

        // уведомляем о измении всей таблицы
        // lev.add(new TableModelEvent(this));
        return lev;
    }

    private long onRowInsertedCall = 0;

    @Override
    protected synchronized List<TableModelEvent> onRowInserted(TableModelEvent e, int firstRow, int lastRow) {
        // уведомления
        // List<TableModelEvent> lev = new ArrayList<TableModelEvent>();

        /* checkDestIndexesFast();
        int fit = fitness();

        if( fit!=0 || tableModel==null ){
            applySort();

            // уведомляем о измении всей таблицы
            lev.add(new TableModelEvent(this));
            return lev;
        }*/

        boolean checkFitness = false;

        onRowInsertedCall++;
        int eachNInserted = SortRowTMImpl.getFitnessOnInsertEach();
        if( eachNInserted>0 ){
            if( eachNInserted==1 ){
                checkFitness = true;
            }else{
                checkFitness = (onRowInsertedCall % eachNInserted) == 0;
            }
        }

        return onRowInserted(firstRow, lastRow, true, checkFitness);
    }

    protected synchronized List<TableModelEvent> onRowInserted(
        int firstRow,
        int lastRow,
        boolean checkDestIndexes,
        boolean checkFitness
    ) {
        // полная сортировка
        // applySort();

        // уведомления
        List<TableModelEvent> lev = new ArrayList<TableModelEvent>();

        if( !isIndexesValid() ){
            applySort();
            // уведомляем о измении всей таблицы
            lev.add(new TableModelEvent(this));
            return lev;
        }

        if( checkDestIndexes ) checkDestIndexesFast();
        if( checkFitness ) {
            int fit = fitness(null);
            if( fit!=0 || tableModel==null ){
                applySort();

                // уведомляем о измении всей таблицы
                lev.add(new TableModelEvent(this));
                return lev;
            }
        }

        // обновление индексов при вставке
        shiftOnInsertIndexes(firstRow, lastRow);

        if( rowComparator==null ){
            for( int si=Math.min(firstRow,lastRow); si<=Math.max(firstRow,lastRow); si++ ){
                int lastdi = row2srcMap.lastKey();
                int di = lastdi+1;
                row2srcMap.put(di, si);
                src2rowMap.put(si, di);
                lev.add(new TableModelEvent(this, di, di, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
            }
            return lev;
        }

//        RowData rd1 = new RowData();
//        rd1.setTableModel(this);

        RowData rd2 = new RowData();
        rd2.setTableModel(tableModel);

        SortInsert ins = new SortInsertDefault() {
            @Override
            public void insert(Object container, int position, Object item) {
                if( !(item instanceof RowData) )
                    throw new Error( "can't insert not RowData" );

                RowData rd = (RowData)item;
                TableModel tm = rd.getTableModel();
                if( !eq(tm, tableModel) ){
                    throw new Error( "can't insert RowData (tableModel is not source)" );
                }

                int si = rd.getRowIndex();
                int di = position;

                insertRow(di,si);
            }

            @Override
            public Object get(Object container, int position) {
                RowData rd = new RowData();
                rd.setTableModel(SortRowTM.this);
                rd.setRowIndex(position);
                return rd;
            }
        };

        for( int si=firstRow; si<=lastRow; si++ ){
            rd2.setRowIndex(si);
            rd2.setTableModel(tableModel);

            int rowco = row2srcMap.size();
            int insertedPos = ins.sortInsert(this, rd2, rowComparator, 0, rowco);

            lev.add(new TableModelEvent(this,
                insertedPos, insertedPos,
                TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
        }

        return lev;
    }
}
