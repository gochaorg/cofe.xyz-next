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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.text.Align;
import xyz.cofe.text.Text;

/**
 * Стиль форматирование ячейки
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class CellFormat {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(CellFormat.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(CellFormat.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(CellFormat.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(CellFormat.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(CellFormat.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(CellFormat.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(CellFormat.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    private final TextCellBuilder cellBuilder ; //= new TextCellBuilder();
    private Border border = null;

    /**
     * Конструктор по умолчанию
     */
    public CellFormat(){
        cellBuilder = new TextCellBuilder();
    }

    /**
     * Конструктор копирования
     * @param src образец для копирования
     */
    public CellFormat(CellFormat src){
        cellBuilder = src.cellBuilder!=null ? src.cellBuilder.clone() : new TextCellBuilder();

        if( src!=null ){
            border = src.border!=null ? src.border.clone() : null;
            vertAlign = src.vertAlign!=null ? src.vertAlign : null;
        }
    }

    /**
     * Клонирование объекта
     * @return Клон
     */
    @Override
    public CellFormat clone(){
        return new CellFormat(this);
    }

    /**
     * Построитель ячейки
     * @return ячейка с отформатированным текстом
     */
    public TextCellBuilder getCellBuilder() {
        return cellBuilder;
    }

    //<editor-fold defaultstate="collapsed" desc="border">
    /**
     * Возвращает бордюр вокруг ячейки
     * @return бордюр, может быть null
     */
    public Border getBorder() {
        return border;
    }

    /**
     * Указывает бордюр вокруг ячейки
     * @param border бордюр, может быть null
     */
    public void setBorder(Border border) {
        this.border = border;
    }

    /**
     * Указывает бордюр вокруг ячейки
     * @param border бордюр, может быть null
     * @return ссылку на себя (this)
     */
    public CellFormat border(Border border){
        this.border = border;
        return this;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="width">
    /**
     * Возвращает шириную ячейки / колонки, без учета бордюра
     * @return ширина, может быть null
     */
    public Integer getWidth() {
        return cellBuilder.getWidth();
    }

    /**
     * Указывает шириную ячейки / колонки, без учета бордюра
     * @param width ширина, может быть null
     */
    public void setWidth(Integer width) {
        cellBuilder.setWidth(width);
    }

    /**
     * Указывает шириную ячейки / колонки, без учета бордюра
     * @param width ширина, может быть null
     * @return ссылку на себя (this)
     */
    public CellFormat width(Integer width) {
        cellBuilder.setWidth(width);
        return this;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="horzAlign">
    /**
     * Возвращает горизонтальное выравнивание
     * @return  Выравнивание текста по горизонтали, может быть null
     */
    public Align getHorzAlign() {
        return cellBuilder.getHorzAlign();
    }

    /**
     * Указывает горизонтальное выравнивание
     * @param horzAlign Выравнивание текста по горизонтали, может быть null
     */
    public void setHorzAlign(Align horzAlign) {
        cellBuilder.setHorzAlign(horzAlign);
    }

    /**
     * Указывает горизонтальное выравнивание
     * @param horzAlign Выравнивание текста по горизонтали, может быть null
     * @return ссылку на себя (this)
     */
    public CellFormat align(Align horzAlign){
        cellBuilder.setHorzAlign(horzAlign);
        return this;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="trimCapsSapces">
    /**
     * Возвращает убрать пробельные символы с концов строки
     * @return Убрать концевые пробельные символы
     */
    public Boolean isTrimSpaces() {
        return cellBuilder.isTrimSpaces();
    }

    /**
     * Указывает убрать пробельные символы с концов строки
     * @param trimCapsSapces Убрать концевые пробельные символы
     */
    public void setTrimSpaces(Boolean trimCapsSapces) {
        cellBuilder.setTrimSpaces(trimCapsSapces);
    }

    /**
     * Указывает убрать пробельные символы с концов строки
     * @param trimCapsSapces Убрать концевые пробельные символы
     * @return ссылку на себя (this)
     */
    public CellFormat trimSpaces(Boolean trimCapsSapces) {
        cellBuilder.setTrimSpaces(trimCapsSapces);
        return this;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="evalNewLine">
    /**
     * Возвращает как интерпретировать переводы строк. <br>
     * @return true - вставлять пустые строки
     */
    public Boolean isEvalNewLine() {
        return cellBuilder.isEvalNewLine();
    }

    /**
     * Указывает Возвращает как интерпретировать переводы строк.
     * @param evalNewLine true - вставлять пустые строки
     */
    public void setEvalNewLine(Boolean evalNewLine) {
        cellBuilder.setEvalNewLine(evalNewLine);
    }

    /**
     * Указывает Возвращает как интерпретировать переводы строк.
     * @param evalNewLine true - вставлять пустые строки
     * @return ссылку на себя (this)
     */
    public CellFormat evalNewLine(Boolean evalNewLine) {
        cellBuilder.setEvalNewLine(evalNewLine);
        return this;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="horzAlignText">
    /**
     * Возвращает текст используемый при горизонтальном выравнивании
     * @return заполнитель при выравнивании
     */
    public String getHorzAlignText() {
        return cellBuilder.getFillAlignText();
    }

    /**
     * Указывает текст используемый при горизонтальном выравнивании
     * @param txt заполнитель при выравнивании
     */
    public void setHorzAlignText(String txt) {
        cellBuilder.setFillAlignText(txt);
    }

    /**
     * Указывает текст используемый при горизонтальном выравнивании
     * @param txt заполнитель при выравнивании
     * @return ссылку на себя (this)
     */
    public CellFormat alignText(String txt) {
        cellBuilder.setFillAlignText(txt);
        return this;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="blankLineCount">
    /**
     * Возвращает Кол-во вставляемых пустых строк, при замене перевода строк, 0 или больше
     * @return Кол-во вставляемых пустых строк
     */
    public Integer getBlankLineCount() {
        return cellBuilder.getBlankLineCount();
    }

    /**
     * Указывает Кол-во вставляемых пустых строк, при замене перевода строк, 0 или больше
     * @param blankLineCount Кол-во вставляемых пустых строк
     */
    public void setBlankLineCount(Integer blankLineCount) {
        cellBuilder.setBlankLineCount(blankLineCount);
    }

    /**
     * Указывает Кол-во вставляемых пустых строк, при замене перевода строк, 0 или больше
     * @param blankLineCount Кол-во вставляемых пустых строк
     * @return ссылку на себя (this)
     */
    public CellFormat blankLineCount(Integer blankLineCount) {
        cellBuilder.setBlankLineCount(blankLineCount);
        return this;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="multiLine">
    /**
     * Возвращает режим - многострочный / однострочный
     * @return
     * true - многострочный режим; <br>
     * false - однострочный
     */
    public Boolean isMultiLine() {
        return cellBuilder.isMultiLine();
    }

    /**
     * Указывает режим - многострочный / однострочный
     * @param multiLine
     * true - многострочный режим; <br>
     * false - однострочный
     */
    public void setMultiLine(Boolean multiLine) {
        cellBuilder.setMultiLine(multiLine);
    }

    /**
     * Указывает режим - многострочный / однострочный
     * @param multiLine
     * true - многострочный режим; <br>
     * false - однострочный
     * @return ссылку на себя (this)
     */
    public CellFormat multiLine(Boolean multiLine) {
        cellBuilder.setMultiLine(multiLine);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="vertAlign">
    private Align vertAlign = null;

    /**
     * Возвращает вертикальное выравнивание
     * @return  Выравнивание текста по вертикали, может быть null
     */
    public Align getVertAlign() {
        return vertAlign;
    }

    /**
     * Указывает вертикальное выравнивание
     * @param vertAlign Выравнивание текста по вертикали, может быть null
     */
    public void setVertAlign(Align vertAlign) {
        this.vertAlign = vertAlign;
    }

    /**
     * Указывает вертикальное выравнивание
     * @param vertAlign Выравнивание текста по вертикали, может быть null
     * @return ссылку на себя (this)
     */
    public CellFormat valign(Align vertAlign) {
        this.vertAlign = vertAlign;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="threeDots">
    /**
     * Возвращает символ многоточние. <br>
     * по умолчанию используется unicode: \u2026 - \u005cu2026
     * @return символ многоточние
     */
    public String getThreeDots() {
        return cellBuilder.getThreeDots();
    }

    /**
     * Указывает символ многоточние. <br>
     * по умолчанию используется unicode: \u2026 - \u005cu2026
     * @param threeDots символ многоточние
     */
    public void setThreeDots(String threeDots) {
        cellBuilder.setThreeDots(threeDots);
    }

    /**
     * Указывает символ многоточние. <br>
     * по умолчанию используется unicode: \u2026 - \u005cu2026
     * @param threeDots символ многоточние
     * @return ссылку на себя (this)
     */
    public CellFormat threeDots(String threeDots) {
        cellBuilder.setThreeDots(threeDots);
        return this;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="format(String[] text,int targetHeight)">
    /**
     * Форматирует текст с учетем указанных свойств на указанную велечину строк или более
     * @param text Исходный текст
     * @param targetHeight целевое кол-во строк по вертикали
     * @return Набор отформатированных строк
     */
    public List<String> format(String[] text,int targetHeight){
        if( text==null )throw new IllegalArgumentException( "text==null" );

        Border brd = getBorder();
        if( brd==null )brd = Borders.empty();

        Bounds b = Bounds.get(text);

        ArrayList<String> lines = new ArrayList<String>();
        lines.addAll( Arrays.asList(text) );

        if( lines.size()<targetHeight ){
            int diff = targetHeight - lines.size();

            Align valign = getVertAlign();
            if( valign==null )valign = Align.Begin;

            if( valign == Align.Begin ){
                for( int i=0; i<diff; i++ ){
                    lines.add( Text.repeat(" ",b.getWidth()) );
                }
            }else if( valign == Align.End ){
                for( int i=0; i<diff; i++ ){
                    lines.add( 0, Text.repeat(" ",b.getWidth()) );
                }
            }else{
                int topl = diff / 2;
                int bottoml = diff - topl;
                for( int i=0; i<topl; i++ ){
                    lines.add( 0, Text.repeat(" ",b.getWidth()) );
                }
                for( int i=0; i<bottoml; i++ ){
                    lines.add( Text.repeat(" ",b.getWidth()) );
                }
            }
        }

        return Border.format(lines, brd);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="build(String[] text,int targetHeight)">
    /**
     * Создает отформатированную ячейку с текстом
     * @param text Текст
     * @param targetHeight Целевой размер ячейки по вертикали
     * @return ячейка с текстом
     */
    public TextCell build(String[] text,int targetHeight){
        return new TextCell( format(text,targetHeight) );
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="merge( CellFormat cf )">
    /**
     * Создает новый стиль объединяя с указанным,
     * если у указанного стиля определенно свойство, то оно переопределяет текущее.
     * @param cf стиль с которым происходит объединение
     * @return новый объединенный стиль
     */
    public CellFormat merge( CellFormat cf ){
        if( cf==null )throw new IllegalArgumentException( "cf==null" );
        CellFormat f = this.clone();

        if( cf.isEvalNewLine() != null ) f.setEvalNewLine(cf.isEvalNewLine());
        if( cf.getBorder()!=null ) f.setBorder( cf.getBorder() );
        if( cf.getWidth()!=null ) f.setWidth( cf.getWidth() );
        if( cf.getHorzAlign()!=null ) f.setHorzAlign( cf.getHorzAlign() );
        if( cf.isTrimSpaces()!=null ){ f.setTrimSpaces(cf.isTrimSpaces()); }
        if( cf.getHorzAlignText()!=null ){ f.setHorzAlignText(cf.getHorzAlignText()); }
        if( cf.getBlankLineCount()!=null ){ f.setBlankLineCount(cf.getBlankLineCount()); }
        if( cf.isMultiLine()!=null ){ f.setMultiLine(cf.isMultiLine()); }
        if( cf.getVertAlign()!=null ){ f.setVertAlign(cf.getVertAlign()); }

        return f;
    }
//</editor-fold>
}
