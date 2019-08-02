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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class RowFormat {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(RowFormat.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(RowFormat.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(RowFormat.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(RowFormat.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(RowFormat.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(RowFormat.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(RowFormat.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public RowFormat(){
    }

    public RowFormat( RowFormat src ){
        if( src!=null ){
            border = src.border!=null ? src.border.clone() : border;

            verticalSplitter = src.verticalSplitter!=null ?
                src.verticalSplitter.clone() : verticalSplitter;

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
        }
    }

    @Override
    public RowFormat clone(){
        return new RowFormat(this);
    }

    //<editor-fold defaultstate="collapsed" desc="border">
    private Border border = Borders.singleLine().clone();

    public Border getBorder() {
        if( border==null )border = Borders.singleLine().clone();
        return border;
    }

    public void setBorder(Border border) {
        this.border = border;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="verticalSplitter">
    private VerticalSplitter verticalSplitter = new VerticalSplitter();

    public VerticalSplitter getVerticalSplitter() {
        if( verticalSplitter!=null )return verticalSplitter;
        verticalSplitter = new VerticalSplitter();
        return verticalSplitter;
    }

    public void setVerticalSplitter(VerticalSplitter verticalSplitter) {
        this.verticalSplitter = verticalSplitter;
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
    private CellFormat firstCell = CellFormats.def().clone();

    public CellFormat getFirstCell() {
        if( firstCell!=null )return firstCell;
        firstCell = CellFormats.def().clone();
        return firstCell;
    }

    public void setFirstCell(CellFormat firstCell) {
        this.firstCell = firstCell;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="lastCell">
    private CellFormat lastCell = CellFormats.def().clone();

    public CellFormat getLastCell() {
        if( lastCell!=null )return lastCell;
        lastCell = CellFormats.def().clone();
        return lastCell;
    }

    public void setLastCell(CellFormat lastCell) {
        this.lastCell = lastCell;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="middleCell">
    private CellFormat middleCell = CellFormats.def().clone();

    public CellFormat getMiddleCell() {
        if( middleCell!=null )return middleCell;
        middleCell = CellFormats.def().clone();
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

    public List<String> format( String ... data ){
        if( data==null )throw new IllegalArgumentException( "data==null" );
        final String[] fdata = data;
        Function<Integer,CellFormat> c = new Function<Integer, CellFormat>() {
            @Override
            public CellFormat apply(Integer from) {
                if( from==null )return getDefaultCell();

                int colIdx = (int)from;
                CellFormat cf = getCellFormatMap().get(colIdx);
                if( cf!=null )return cf;

                if( fdata.length>1 ){
                    if( colIdx==0 )return getFirstCell();
                    if( colIdx==fdata.length-1 )return getLastCell();
                    return getMiddleCell();
                }

                return getDefaultCell();
            }
        };
        return format(data, c);
    }

    public List<String> format( String[] data, String[] columnNames ){
        if( data==null )throw new IllegalArgumentException( "data==null" );

        // Если элемент null - то заменяет на пустую строку
        for( int i=0; i<data.length; i++ ){
            if( data[i]==null )data[i] = "";
        }

        if( columnNames!=null ){
            // Добавляет недостающие данные
            if( data.length < columnNames.length ){
                int addCo = (columnNames.length - data.length);
                for( int i=0; i < addCo; i++ ){
                    data = Arrays.copyOf(data, data.length+1);
                    data[data.length-1] = "";
                }
            }
        }

        final String[] fdata = data;
        final String[] colNames = columnNames;

        Function<Integer,CellFormat> c = new Function<Integer, CellFormat>() {
            @Override
            public CellFormat apply(Integer from) {
                if( from==null )return getDefaultCell();

                CellFormat cf = getDefaultCell();
                int colIdx = (int)from;

                if( fdata.length>1 ){
                    if( colIdx==0 ){
                        cf = getFirstCell();
                    }else
                    if( colIdx==fdata.length-1 ){
                        cf = getLastCell();
                    }else{
                        cf = getMiddleCell();
                    }
                }

                String colName =
                    colNames==null
                        ?   Integer.toString(colIdx)
                        :   ( colIdx>=0 && colIdx<colNames.length
                        ? ( colNames[colIdx]!=null
                        ? colNames[colIdx]
                        : Integer.toString(colIdx)
                    )
                        : Integer.toString(colIdx)
                    );


                CellFormat[] cfByIndex = getCellFormatMap().getByIndex(colIdx);
                if( cfByIndex!=null ){
                    for( CellFormat cfi : cfByIndex ){
                        cf = cf.merge(cfi);
                    }
                }

                CellFormat[] cfByName = getCellFormatMap().getByName(colName);
                if( cfByName!=null ){
                    for( CellFormat cfn : cfByName ){
                        cf = cf.merge(cfn);
                    }
                }

                return cf;
            }
        };
        return format(data, c);
    }

    public List<String> format( String[] data, Map<Integer,CellFormat> cellFormatter ){
        final Map<Integer,CellFormat> fc = cellFormatter;
        Function<Integer,CellFormat> c = new Function<Integer, CellFormat>() {
            @Override
            public CellFormat apply(Integer from) {
                return fc.get(from);
            }
        };
        return format(data, c);
    }

    public List<String> format( String[] data, Function<Integer,CellFormat> cellFormatter ){
        if( data==null )throw new IllegalArgumentException( "data==null" );
        if( cellFormatter==null )throw new IllegalArgumentException( "cellFormatter==null" );

        // Отформатированные данные (ячейки), без обрамления
        TextCell[] dataCells = new TextCell[data.length];

        // Оформление ячеек
        CellFormat[] cellFormats = new CellFormat[data.length];

        for( int i=0; i<dataCells.length; i++ ){
            CellFormat cf = cellFormatter.apply(i);

            TextCell tc = cf.getCellBuilder().build(data[i]);
            dataCells[i] = tc;

            cellFormats[i] = cf;
        }

        // Габариты отфарматированных данных
        Bounds dataBounds = Bounds.get(dataCells);

        // Отформатированные данные с обрамлением
        TextCell[] borderCells = new TextCell[dataCells.length];
        for( int i=0; i<data.length; i++ ){
            borderCells[i] = cellFormats[i].build(dataCells[i].getTextLines(), dataBounds.getHeight());
        }

        // Ячейки с обрамлением и разделителями
        ArrayList<TextCell> rowCells = new ArrayList<TextCell>();
        Map<TextCell,Boolean> isRowSplitter = new HashMap<TextCell, Boolean>();
        int splitterWidth = getVerticalSplitter().getWidth();

        if( borderCells.length>1 && splitterWidth>0 ){
            Bounds borderCellsBounds = Bounds.get(borderCells);
            TextCell splitter = TextCell.createBlock(
                getVerticalSplitter().getText(),
                splitterWidth,
                borderCellsBounds.getHeight()
            );
            rowCells.add( borderCells[0] );
            isRowSplitter.put(borderCells[0], Boolean.FALSE);

            for( int i=1; i<borderCells.length; i++ ){
                TextCell borderCell = borderCells[i];
                rowCells.add( splitter );
                isRowSplitter.put(splitter, Boolean.TRUE);

                rowCells.add( borderCell );
                isRowSplitter.put(borderCell, Boolean.FALSE);
            }
        }else{
            rowCells.addAll( Arrays.asList(borderCells) );
            for( TextCell tc : borderCells )isRowSplitter.put(tc, Boolean.FALSE);
        }

        TextCell rowContent = TextCell.joinAsTextCell(rowCells);

        // Внешнее обрамление
        Border outBorder = getBorder();
        Bounds rowContentBounds = Bounds.get(rowContent);

        // Верхний ряд
        ArrayList<TextCell> outTopCells = new ArrayList<TextCell>();
        if( outBorder.getTopHeight()>0 ){
            if( outBorder.getLeftWidth()>0 ){
                outTopCells.add(outBorder.getLeftTopCell());
            }
            for( TextCell tc : rowCells ){
                Boolean isSplt = isRowSplitter.get(tc);
                if( splitterWidth>0 && isSplt ){
                    TextCell splt = TextCell.createBlock(
                        getVerticalSplitter().getTopText(),
                        splitterWidth, outBorder.getTopHeight());
                    outTopCells.add(splt);
                }else{
                    Bounds b = Bounds.get(tc);
                    TextCell horzLine = outBorder.getTopCell(b);
                    outTopCells.add( horzLine );
                }
            }
            if( outBorder.getRightWidth()>0 ){
                outTopCells.add(outBorder.getRightTopCell());
            }
        }

        // Средний ряд
        ArrayList<TextCell> outInnCells = new ArrayList<TextCell>();
        if( rowContentBounds.getHeight()>0 ){
            if( outBorder.getLeftWidth()>0 ){
                TextCell b = outBorder.getLeftCell(rowContentBounds);
                outInnCells.add(b);
            }
            outInnCells.add(rowContent);
            if( outBorder.getRightWidth()>0 ){
                TextCell b = outBorder.getRightCell(rowContentBounds);
                outInnCells.add(b);
            }
        }

        // Нижний ряд
        ArrayList<TextCell> outBottomCells = new ArrayList<TextCell>();
        if( outBorder.getBottomHeigth()>0 ){
            if( outBorder.getLeftWidth()>0 ){
                outBottomCells.add(outBorder.getLeftBottomCell());
            }
            for( TextCell tc : rowCells ){
                if( splitterWidth>0 && isRowSplitter.get(tc) ){
                    TextCell splt = TextCell.createBlock(
                        getVerticalSplitter().getBottomText(),
                        splitterWidth, outBorder.getBottomHeigth());
                    outBottomCells.add(splt);
                }else{
                    Bounds b = Bounds.get(tc);
                    TextCell horzLine = outBorder.getBottomCell(b);
                    outBottomCells.add( horzLine );
                }
            }
            if( outBorder.getRightWidth()>0 ){
                outBottomCells.add(outBorder.getRightBottomCell());
            }
        }

        List<String> outList = TextCell.joinAsList(outTopCells);
        outList.addAll( TextCell.joinAsList(outInnCells) );
        outList.addAll( TextCell.joinAsList(outBottomCells) );

        return outList;
    }
}
