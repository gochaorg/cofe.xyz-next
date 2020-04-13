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


import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableModel;

/**
 * "Расширенная" колонка, переопределяет поведение исходной колоноки TableModel
 * @author gocha
 */
public class ExtendColumn extends Column {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(ExtendColumn.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(ExtendColumn.class.getName()).log(Level.FINER, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(ExtendColumn.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(ExtendColumn.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(ExtendColumn.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(ExtendColumn.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="sourceColumn">
    protected int sourceColumn = -1;

    public int getSourceColumn() {
        return sourceColumn;
    }

    public void setSourceColumn(int sourceColumn) {
        Object old = this.sourceColumn;
        this.sourceColumn = sourceColumn;
        firePropertyChange(SOURCE_COLUMN, old, sourceColumn);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="rowIndex">
    protected int rowIndex = -1;

    /**
     * Возвращает индекст строки
     * @return индекс строки
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * Указывает индекс строки
     * @param rowIndex индекс строки
     */
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }// </editor-fold>

    /**
     * Создает колонку на базе модели таблицы
     * @param tm модель
     * @param columnIndex индекс колонки в исходной модели
     * @param writeable колонка поддерживает запись значений в исходную таблицу
     * @return колонка
     */
    public static ExtendColumn createFrom(TableModel tm,int columnIndex,boolean writeable){
        final ExtendColumn dc = new ExtendColumn();
        dc.setSourceColumn(columnIndex);
        dc.setName(tm.getColumnName(columnIndex));
        dc.setType(tm.getColumnClass(columnIndex));
        dc.setReader( from -> from );
        if( writeable ){
            final TableModel ftm = tm;
            final int srcColIdx = columnIndex;
            dc.setWriter( (Cell cell) -> {
                int ri = dc.getRowIndex();
                ftm.setValueAt(cell.newValue, ri, srcColIdx);
                return true;
            });
        }
        return dc;
    }
}
