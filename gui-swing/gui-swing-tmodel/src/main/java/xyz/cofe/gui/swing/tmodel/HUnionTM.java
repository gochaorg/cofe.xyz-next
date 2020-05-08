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


import xyz.cofe.collection.BasicEventList;
import xyz.cofe.collection.EventList;
import xyz.cofe.fn.Pair;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Горизонтально объеденные две модели таблиц
 * @author nt.gocha@gmail.com
 */
public class HUnionTM implements TableModel {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(HUnionTM.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(HUnionTM.class.getName()).log(Level.FINER, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(HUnionTM.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(HUnionTM.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(HUnionTM.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(HUnionTM.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /**
     * Конструктор
     */
    public HUnionTM(){
    }

    private EventList<TableModel> tableModels = new BasicEventList<>();

    protected final TableModelListener tableModelListener = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
        }
    };

    protected void attachTableModelListener(TableModel tm){
        if( tm==null )return;
        tm.addTableModelListener(tableModelListener);
    }

    protected void detachTableModelListener(TableModel tm){
        if( tm==null )return;
        tm.removeTableModelListener(tableModelListener);
    }


    @Override
    public int getRowCount() {
        if( tableModels==null )return 0;
        if( tableModels.size()==0 )return 0;
        return tableModels.get(0).getRowCount();
    }

    @Override
    public int getColumnCount() {
        if( tableModels==null )return 0;
        if( tableModels.size()==0 )return 0;
        int co = 0;
        for( TableModel tm : tableModels ){
            if( tm!=null )co += tm.getColumnCount();
        }
        return co;
    }

    protected Pair<TableModel,Integer> getTMColumn( int columnIndex){
        if( tableModels==null )return null;
        if( tableModels.size()==0 )return null;
        if( columnIndex<0 )return null;

        int offset = 0;
        for( TableModel tm : tableModels ){
            int cc = tm.getColumnCount();
            if( (columnIndex < (offset + cc)) && (columnIndex < offset) ){
                return Pair.of(tm, columnIndex-offset);
            }
            offset += cc;
        }
        return null;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if( tableModels==null )return "?";
        if( tableModels.size()==0 )return "?";

        Pair<TableModel,Integer> pTC = getTMColumn(columnIndex);
        if( pTC==null )return "?";
        return pTC.a().getColumnName(pTC.b());
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if( tableModels==null )return null;
        if( tableModels.size()==0 )return null;

        Pair<TableModel,Integer> pTC = getTMColumn(columnIndex);
        if( pTC==null )return null;
        return pTC.a().getColumnClass(pTC.b());
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if( tableModels==null )return false;
        if( tableModels.size()==0 )return false;

        Pair<TableModel,Integer> pTC = getTMColumn(columnIndex);
        if( pTC==null )return false;
        return pTC.a().isCellEditable(rowIndex,pTC.b());
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if( tableModels==null )return null;
        if( tableModels.size()==0 )return null;

        Pair<TableModel,Integer> pTC = getTMColumn(columnIndex);
        if( pTC==null )return false;
        return pTC.a().getValueAt(rowIndex,pTC.b());
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if( tableModels==null )return;
        if( tableModels.size()==0 )return;

        Pair<TableModel,Integer> pTC = getTMColumn(columnIndex);
        if( pTC==null )return;
        pTC.a().setValueAt(aValue,rowIndex,pTC.b());
    }

    protected EventSupport eventSupport = new EventSupport(this);

    @Override
    public void removeTableModelListener(TableModelListener l) {
        eventSupport.removeTableModelListener(l);
    }

    public TableModelListener[] getListeners() {
        return eventSupport.getListeners();
    }

    public void fireTableModelEvent(TableModelEvent e) {
        eventSupport.fireTableModelEvent(e);
    }

    public void fireRowsUpdated(int rowIndexFrom, int toIndexInclude) {
        eventSupport.fireRowsUpdated(rowIndexFrom, toIndexInclude);
    }

    public void fireRowsInserted(int rowIndexFrom, int toIndexInclude) {
        eventSupport.fireRowsInserted(rowIndexFrom, toIndexInclude);
    }

    public void fireRowsDeleted(int rowIndexFrom, int toIndexInclude) {
        eventSupport.fireRowsDeleted(rowIndexFrom, toIndexInclude);
    }

    public void fireRowUpdated(int row) {
        eventSupport.fireRowUpdated(row);
    }

    public void fireColumnsChanged() {
        eventSupport.fireColumnsChanged();
    }

    public void fireCellChanged(int rowIndex, int columnIndex) {
        eventSupport.fireCellChanged(rowIndex, columnIndex);
    }

    public void fireAllChanged() {
        eventSupport.fireAllChanged();
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        eventSupport.addTableModelListener(l);
    }
}
