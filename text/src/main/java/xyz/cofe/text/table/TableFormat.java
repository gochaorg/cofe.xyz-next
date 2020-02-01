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

package xyz.cofe.text.table;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Табличное форматирование текста.
 *
 * <br><br>
 *
 *     Таблица представляет из себя 4 логических блока данных:
 *     <ol>
 *         <li>
 *             Заголовок - одна или несколько строк текста, содержащие названия колонок.
 *             Заголовок может быть 0 или 1 у таблицы. <br>
 *
 *             Для формирования заголовка см функцию {@link #formatHeader(String...)}
 *         </li>
 *         <li>
 *             Первая строка данных - может быть 0 или 1 строка, влияет на форматирование
 *             см функцию {@link #formatFirstRow(String...)}
 *         </li>
 *         <li>
 *             Средняя строка данных - может быть 0 и более строк.
 *             см функцию {@link #formatMiddleRow(String...)}
 *         </li>
 *         <li>
 *             Последняя строка данных - может быть 0 и 1 строка.
 *             см функцию {@link #formatLastRow(String...)}
 *         </li>
 *     </ol>
 *
 *     Кажда строка (включая зоголовок см
 *     {@link #getHeader()}, {@link #getFirstRow()},
 *     {@link #getMiddleRow()}, {@link #getLastRow()}
 *     ) содержит набор ячеек ({@link CellFormat}) <br><br>
 *
 *     Для каждой типа строки - ячейки в строке могут содержать свое собственное форматирование:
 *     Форматирование первой ячейки в строке {@link #getFirstCell()},
 *     средней {@link #getMiddleCell()}
 *     и последней {@link #getLastCell()}.
 *     <br><br>
 *
 *     Каждую ячейку можно отформатировать по след значениям:
 *     Ширина, Выравнивание по горизонтали/вертикали, перенос текста, ограничение на длину текста.
 *     <br><br>
 *
 *     Для каждого вида форматирования: таблицы, строки, ячейки есть возможность указать бордюр {@link Border}.
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TableFormat {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(TableFormat.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(TableFormat.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(TableFormat.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(TableFormat.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(TableFormat.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(TableFormat.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(TableFormat.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /**
     * Конструктор по умолчанию
     */
    public TableFormat(){
    }

    /**
     * Конструктор копирования
     * @param src образец
     */
    public TableFormat(TableFormat src){
        if( src!=null ){
            border = src.border!=null ? src.border.clone() : border;

            headerVisible = src.headerVisible;

            defaultCell = src.defaultCell!=null ?
                src.defaultCell.clone() : defaultCell;

            firstCell = src.firstCell!=null ?
                src.firstCell.clone() : firstCell;

            lastCell = src.lastCell!=null ?
                src.lastCell.clone() : lastCell;

            middleCell = src.middleCell!=null ?
                src.middleCell.clone() : middleCell;

            if( src.cellFormatMap!=null ){
                this.cellFormatMap = src.cellFormatMap.clone();
            }

            header = src.header!=null ? src.header.clone() : header;
            if( header!=null ){
                if( defaultCell!=null )header.setDefaultCell(defaultCell);
                if( firstCell!=null )header.setFirstCell(firstCell);
                if( lastCell!=null )header.setLastCell(lastCell);
                if( middleCell!=null )header.setMiddleCell(middleCell);
                if( cellFormatMap!=null )header.setCellFormatMap(cellFormatMap);
            }

            firstRow = src.firstRow!=null ? src.firstRow.clone() : firstRow;
            if( firstRow!=null ){
                if( defaultCell!=null )firstRow.setDefaultCell(defaultCell);
                if( firstCell!=null )firstRow.setFirstCell(firstCell);
                if( lastCell!=null )firstRow.setLastCell(lastCell);
                if( middleCell!=null )firstRow.setMiddleCell(middleCell);
                if( cellFormatMap!=null )firstRow.setCellFormatMap(cellFormatMap);
            }

            middleRow = src.middleRow!=null ? src.middleRow.clone() : middleRow;
            if( middleRow!=null ){
                if( defaultCell!=null )middleRow.setDefaultCell(defaultCell);
                if( firstCell!=null )middleRow.setFirstCell(firstCell);
                if( lastCell!=null )middleRow.setLastCell(lastCell);
                if( middleCell!=null )middleRow.setMiddleCell(middleCell);
                if( cellFormatMap!=null )middleRow.setCellFormatMap(cellFormatMap);
            }

            lastRow = src.lastRow!=null ? src.lastRow.clone() : lastRow;
            if( lastRow!=null ){
                if( defaultCell!=null )lastRow.setDefaultCell(defaultCell);
                if( firstCell!=null )lastRow.setFirstCell(firstCell);
                if( lastCell!=null )lastRow.setLastCell(lastCell);
                if( middleCell!=null )lastRow.setMiddleCell(middleCell);
                if( cellFormatMap!=null )lastRow.setCellFormatMap(cellFormatMap);
            }
        }
    }

    /**
     * Клонирование
     * @return клон
     */
    @Override
    public TableFormat clone(){
        return new TableFormat(this);
    }

    //<editor-fold defaultstate="collapsed" desc="headerVisible">
    protected boolean headerVisible = true;

    public boolean isHeaderVisible() {
        return headerVisible;
    }

    public void setHeaderVisible(boolean headerVisible) {
        this.headerVisible = headerVisible;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="border">
    private Border border = Borders.empty().clone();

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border border) {
        this.border = border;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="header">
    private RowFormat header = null;

    public RowFormat getHeader() {
        if( header!=null )return header;
        header = RowFormats.singleRow().clone();
        header.setCellFormatMap(getCellFormatMap());
        header.setDefaultCell(getDefaultCell());
        header.setFirstCell(getFirstCell());
        header.setMiddleCell(getMiddleCell());
        header.setLastCell(getLastCell());
        header.getBorder().setLeftBottomText("\u255E");
        header.getBorder().setRightBottomText("\u2561");
        header.getBorder().setBottomText("\u2550");
        header.getVerticalSplitter().setBottomText("\u256A");
        return header;
    }

    public void setHeader(RowFormat header) {
        this.header = header;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="firstRow">
    private RowFormat firstRow = null;

    public RowFormat getFirstRow() {
        if( firstRow!=null )return firstRow;
        firstRow = RowFormats.singleRow().clone();
        firstRow.setCellFormatMap(getCellFormatMap());
        firstRow.setDefaultCell(getDefaultCell());
        firstRow.setFirstCell(getFirstCell());
        firstRow.setMiddleCell(getMiddleCell());
        firstRow.setLastCell(getLastCell());
        firstRow.getBorder().setTopHeight(0);
        firstRow.getBorder().setLeftBottomText("\u251C");
        firstRow.getBorder().setRightBottomText("\u2524");
        firstRow.getVerticalSplitter().setBottomText("\u253C");
        return firstRow;
    }

    public void setFirstRow(RowFormat firstRow) {
        this.firstRow = firstRow;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="middleRow">
    private RowFormat middleRow = null;

    public RowFormat getMiddleRow() {
        if( middleRow!=null )return middleRow;
        middleRow = RowFormats.singleRow().clone();
        middleRow.setCellFormatMap(getCellFormatMap());
        middleRow.setDefaultCell(getDefaultCell());
        middleRow.setFirstCell(getFirstCell());
        middleRow.setMiddleCell(getMiddleCell());
        middleRow.setLastCell(getLastCell());
        middleRow.getBorder().setTopHeight(0);
        middleRow.getBorder().setLeftBottomText("\u251C");
        middleRow.getBorder().setRightBottomText("\u2524");
        middleRow.getVerticalSplitter().setBottomText("\u253C");
        return middleRow;
    }

    public void setMiddleRow(RowFormat middleRow) {
        this.middleRow = middleRow;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="lastRow">
    private RowFormat lastRow = null;

    public RowFormat getLastRow() {
        if( lastRow!=null )return lastRow;
        lastRow = RowFormats.singleRow().clone();
        lastRow.setCellFormatMap(getCellFormatMap());
        lastRow.setDefaultCell(getDefaultCell());
        lastRow.setFirstCell(getFirstCell());
        lastRow.setMiddleCell(getMiddleCell());
        lastRow.setLastCell(getLastCell());
        lastRow.getBorder().setTopHeight(0);
        return lastRow;
    }

    public void setLastRow(RowFormat lastRow) {
        this.lastRow = lastRow;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="defaultCell">
    private CellFormat defaultCell = CellFormats.def().clone();

    public CellFormat getDefaultCell() {
        if( defaultCell!=null )return defaultCell;
        defaultCell = CellFormats.def().clone();
        return defaultCell;
    }

    public void setDefaultCell(CellFormat defaultCell) {
        this.defaultCell = defaultCell;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="firstCell">
    private CellFormat firstCell = null;

    public CellFormat getFirstCell() {
        if( firstCell!=null )return firstCell;
        firstCell = getDefaultCell();
        return firstCell;
    }

    public void setFirstCell(CellFormat firstCell) {
        this.firstCell = firstCell;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="lastCell">
    private CellFormat lastCell = null;

    public CellFormat getLastCell() {
        if( lastCell!=null )return lastCell;
        lastCell = getDefaultCell();
        return lastCell;
    }

    public void setLastCell(CellFormat lastCell) {
        this.lastCell = lastCell;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="middleCell">
    private CellFormat middleCell = null;

    public CellFormat getMiddleCell() {
        if( middleCell!=null )return middleCell;
        middleCell = getDefaultCell();
        return middleCell;
    }

    public void setMiddleCell(CellFormat middleCell) {
        this.middleCell = middleCell;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cellFormatMap">
    private CellFormatMap cellFormatMap = null;

    public CellFormatMap getCellFormatMap(){
        if( cellFormatMap!=null )return cellFormatMap;
        cellFormatMap = new CellFormatMap();
        return cellFormatMap;
    }

    public void setCellFormatMap(CellFormatMap cellFormatMap) {
        this.cellFormatMap = cellFormatMap;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="formatHeader()">
    public List<String> formatHeader( String ... labels ){
        return formatHeader(labels, null);
    }

    public List<String> formatHeader( String[] labels, String[] columnNames ){
        if( !isHeaderVisible() ){
            return new ArrayList<String>();
        }

        if( labels==null )throw new IllegalArgumentException( "labels==null" );

        List<String> headerLines = getHeader().format(labels,columnNames);

        Border brd = getBorder();
        if( brd==null )return headerLines;
        if( brd.isEmpty() )return headerLines;

        TextCell headerTextCell = new TextCell(headerLines);
        Bounds headerBounds = Bounds.max(headerTextCell);

        List<String> out = new ArrayList<String>();

        // верхний слой
        if( brd.getTopHeight()>0 ){
            out.addAll( TextCell.horizontalJoin(
                brd.getLeftTopCell(),
                brd.getTopCell(headerBounds),
                brd.getRightTopCell()
            ) );
        }

        // средний слой
        out.addAll( TextCell.horizontalJoin(
            brd.getLeftCell(headerBounds),
            headerTextCell,
            brd.getRightCell(headerBounds)
        ) );

//        // нижний  слой
//        if( brd.getBottomWidth()>0 ){
//            out.addAll( TextCell.joinAsList(
//                brd.getLeftBottomCell(),
//                brd.getBottomCell(headerBounds),
//                brd.getRightBottomCell()
//            ));
//        }

        return out;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="formatFirstRow()">
    public List<String> formatFirstRow( String ... data ){
        return formatFirstRow(data, null);
    }

    public List<String> formatFirstRow( String[] data, String[] columnNames ){
        if( data==null )throw new IllegalArgumentException( "labels==null" );

        List<String> dataLines = getFirstRow().format(data,columnNames);

        Border brd = getBorder();
        if( brd==null )return dataLines;
        if( brd.isEmpty() )return dataLines;

        TextCell dataTextCell = new TextCell(dataLines);
        Bounds dataBounds = Bounds.max(dataTextCell);

        List<String> out = new ArrayList<String>();

//        // верхний слой
//        if( brd.getTopWidth()>0 ){
//            out.addAll( TextCell.joinAsList(
//                brd.getLeftTopCell(),
//                brd.getTopCell(headerBounds),
//                brd.getRightTopCell()
//            ) );
//        }

        // средний слой
        out.addAll( TextCell.horizontalJoin(
            brd.getLeftCell(dataBounds),
            dataTextCell,
            brd.getRightCell(dataBounds)
        ) );

//        // нижний  слой
//        if( brd.getBottomWidth()>0 ){
//            out.addAll( TextCell.joinAsList(
//                brd.getLeftBottomCell(),
//                brd.getBottomCell(headerBounds),
//                brd.getRightBottomCell()
//            ));
//        }

        return out;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="formatMiddleRow()">
    public List<String> formatMiddleRow( String ... data ){
        return formatMiddleRow(data,null);
    }

    public List<String> formatMiddleRow( String[] data, String[] columnNames ){
        if( data==null )throw new IllegalArgumentException( "labels==null" );

        List<String> dataLines = getMiddleRow().format(data,columnNames);

        Border brd = getBorder();
        if( brd==null )return dataLines;
        if( brd.isEmpty() )return dataLines;

        TextCell dataTextCell = new TextCell(dataLines);
        Bounds dataBounds = Bounds.max(dataTextCell);

        List<String> out = new ArrayList<String>();

//        // верхний слой
//        if( brd.getTopWidth()>0 ){
//            out.addAll( TextCell.joinAsList(
//                brd.getLeftTopCell(),
//                brd.getTopCell(headerBounds),
//                brd.getRightTopCell()
//            ) );
//        }

        // средний слой
        out.addAll( TextCell.horizontalJoin(
            brd.getLeftCell(dataBounds),
            dataTextCell,
            brd.getRightCell(dataBounds)
        ) );

//        // нижний  слой
//        if( brd.getBottomWidth()>0 ){
//            out.addAll( TextCell.joinAsList(
//                brd.getLeftBottomCell(),
//                brd.getBottomCell(headerBounds),
//                brd.getRightBottomCell()
//            ));
//        }

        return out;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="formatLastRow()">
    public List<String> formatLastRow( String ... data ){
        return formatLastRow(data, null);
    }

    public List<String> formatLastRow( String[] data, String[] columnNames ){
        if( data==null )throw new IllegalArgumentException( "labels==null" );

        List<String> dataLines = getLastRow().format(data,columnNames);

        Border brd = getBorder();
        if( brd==null )return dataLines;
        if( brd.isEmpty() )return dataLines;

        TextCell dataTextCell = new TextCell(dataLines);
        Bounds dataBounds = Bounds.max(dataTextCell);

        List<String> out = new ArrayList<String>();

//        // верхний слой
//        if( brd.getTopWidth()>0 ){
//            out.addAll( TextCell.joinAsList(
//                brd.getLeftTopCell(),
//                brd.getTopCell(headerBounds),
//                brd.getRightTopCell()
//            ) );
//        }

        // средний слой
        out.addAll( TextCell.horizontalJoin(
            brd.getLeftCell(dataBounds),
            dataTextCell,
            brd.getRightCell(dataBounds)
        ) );

        // нижний  слой
        if( brd.getBottomHeigth()>0 ){
            out.addAll( TextCell.horizontalJoin(
                brd.getLeftBottomCell(),
                brd.getBottomCell(dataBounds),
                brd.getRightBottomCell()
            ));
        }

        return out;
    }
    //</editor-fold>
}
