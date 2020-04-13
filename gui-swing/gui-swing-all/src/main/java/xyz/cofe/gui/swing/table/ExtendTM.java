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

import xyz.cofe.collection.BasicEventList;
import xyz.cofe.collection.EventList;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableModel;

/**
 * Модель таблицы с расширенными колонками, используется для переопределения исходной модедли
 * @author gocha
 */
public class ExtendTM extends WrapTM {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(ExtendTM.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(ExtendTM.class.getName()).log(Level.FINER, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(ExtendTM.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(ExtendTM.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(ExtendTM.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(ExtendTM.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /**
     * Конструктор
     */
    public ExtendTM(){
        columns.onChanged( (idx,oldv,curv) -> {
            if( oldv!=null )oldv.removePropertyChangeListener(columnPropertyListener);
            if( curv!=null )curv.addPropertyChangeListener(columnPropertyListener);
            if( oldv!=null || curv!=null ){
                fireColumnsChanged();
            }
        });
    }

    private PropertyChangeListener columnPropertyListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if( evt!=null ){
                if( Column.NAME.equals(evt.getPropertyName())
                    || Column.TYPE.equals(evt.getPropertyName())
                ){
                    fireColumnsChanged();
                }else if( Column.SOURCE_COLUMN.equals(evt.getPropertyName()) ){
                    int rc = getColumnCount();
                    if( rc>0 ){
                        fireRowsUpdated(0, rc-1);
                    }
                }
            }
        }
    };

    private EventList<Column> columns = new BasicEventList<>();

    /**
     * Возвращает колонки таблицы
     * @return колонки
     */
    public EventList<Column> getColumns(){ return columns; }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if( columnIndex<0 )return null;
        if( columnIndex>=columns.size() )return null;
        return columns.get(columnIndex).getType();
    }

    @Override
    public String getColumnName(int columnIndex) {
        if( columnIndex<0 )return null;
        if( columnIndex>=columns.size() )return null;
        return columns.get(columnIndex).getName();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
//        return super.getValueAt(rowIndex, columnIndex);
        if( columnIndex<0 )return null;
        if( columnIndex>=columns.size() )return null;
        if( tableModel==null )return null;

        Column co = columns.get(columnIndex);
        int srcColumnIndex = columnIndex;

        if( co instanceof ExtendColumn )srcColumnIndex = ((ExtendColumn)co).getSourceColumn();
        Object srcVal = tableModel.getValueAt(rowIndex, srcColumnIndex);

        Function conv = (co instanceof GetReaderForRow)
            ? ((GetReaderForRow)co).getReader(rowIndex)
            : co.getReader();

        if( conv==null )return null;

        return conv.apply(srcVal);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if( columnIndex<0 )return false;
        if( columnIndex>=columns.size() )return false;
        return columns.get(columnIndex).getWriter()!=null;
    }

    @Override
    public void setTableModel(TableModel tableModel) {
        super.setTableModel(tableModel);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if( columnIndex<0 )return;
        if( columnIndex>=columns.size() )return;
        if( tableModel==null )return;

        Column co = columns.get(columnIndex);
        int srcColumnIndex = columnIndex;
        if( co instanceof ExtendColumn ){
            ExtendColumn dcol = (ExtendColumn)co;
            srcColumnIndex = dcol.getSourceColumn();
            dcol.setRowIndex(rowIndex);
        }
        Object srcVal = tableModel.getValueAt(rowIndex, srcColumnIndex);

        Function<Column.Cell,Boolean> writer = co.getWriter();
        if(writer==null)return;

        Boolean succ = writer.apply(new Column.Cell(srcVal,aValue));
        if( !succ )return;

//        fireCellChanged(rowIndex, columnIndex);
        fireRowUpdated(rowIndex);
    }
}
