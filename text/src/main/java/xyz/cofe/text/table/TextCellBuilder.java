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
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.text.Align;
import xyz.cofe.text.Text;

/**
 * Форматирование текстовой колонки
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TextCellBuilder {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(TextCellBuilder.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(TextCellBuilder.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(TextCellBuilder.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(TextCellBuilder.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(TextCellBuilder.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(TextCellBuilder.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(TextCellBuilder.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public TextCellBuilder(){
    }

    public TextCellBuilder(TextCellBuilder src){
        if( src!=null ){
            width = src.width;
            horzAlign = src.horzAlign;
            trimSpaces = src.trimSpaces;
            evalNewLine = src.evalNewLine;
            fillAlignText = src.fillAlignText;
            blankLineCount = src.blankLineCount;
            multiLine = src.multiLine;
            threeDots = src.threeDots;
        }
    }

    @Override
    public TextCellBuilder clone(){
        return new TextCellBuilder(this);
    }

    //<editor-fold defaultstate="collapsed" desc="width">
    protected Integer width = null;

    /**
     * Указывает ширину колонки
     * @return ширина колонки
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * Указывает ширину колонки
     * @param width ширина колонки
     */
    public void setWidth(Integer width) {
        this.width = width;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="horzAlign">
    private Align horzAlign = null;

    /**
     * Указывает Выравнивание по горизонтали
     * @return Выравнивание по горизонтали
     */
    public Align getHorzAlign() {
        return horzAlign;
    }

    /**
     * Указывает Выравнивание по горизонтали
     * @param horzAlign Выравнивание по горизонтали
     */
    public void setHorzAlign(Align horzAlign) {
        this.horzAlign = horzAlign;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="trimSpaces">
    private Boolean trimSpaces = null;

    /**
     * Указывает Убрать пробельные символы с концов строки
     * @return Убрать пробельные символы
     */
    public Boolean isTrimSpaces() {
        return trimSpaces;
    }

    /**
     * Указывает Убрать пробельные символы с концов строки
     * @param trimSpaces Убрать пробельные символы
     */
    public void setTrimSpaces(Boolean trimSpaces) {
        this.trimSpaces = trimSpaces;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="evalNewLine">
    private Boolean evalNewLine = null;

    /**
     * Указывает Интерпретировать переводы строк - вставлять пустые строки
     * @return true - Интерпретировать
     */
    public Boolean isEvalNewLine() {
        return evalNewLine;
    }

    /**
     * Указывает Интерпретировать переводы строк - вставлять пустые строки
     * @param evalNewLine true - Интерпретировать
     */
    public void setEvalNewLine(Boolean evalNewLine) {
        this.evalNewLine = evalNewLine;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fillAlignText">
    private String fillAlignText = null;

    /**
     * Возвращает строку которая используется в качестве выравнивания текста.
     * @return Строка/текст заполнитель при выравнивании
     */
    public String getFillAlignText(){
//        if( fillAlignText==null )fillAlignText = " ";
//        if( fillAlignText.length()<1 )fillAlignText = " ";
        return fillAlignText;
    }

    /**
     * Устанавливает строку которая используется в качестве выравнивания текста.
     * @param txt Строка/текст заполнитель при выравнивании
     */
    public void setFillAlignText(String txt){
        this.fillAlignText = txt;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="blankLineCount">
    private Integer blankLineCount = null;

    /**
     * Указывает Кол-во вставляемых пустых строк, при замене перевода строк, 0 или больше
     * @return кол-во пустых строк
     */
    public Integer getBlankLineCount() {
        return blankLineCount;
    }

    /**
     * Указывает Кол-во вставляемых пустых строк, при замене перевода строк, 0 или больше
     * @param blankLineCount кол-во пустых строк
     */
    public void setBlankLineCount(Integer blankLineCount) {
//        if (blankLineCount<0) throw new IllegalArgumentException("blankLineCount<0");
        this.blankLineCount = blankLineCount;
    }

    /**
     * Возвращает пустую строку испоьлзуему в качестве заменителя перевода строк.
     * @return Пустая строка
     */
    protected String getBlankLine(){ return ""; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="multiLine">
    private Boolean multiLine = null;

    public Boolean isMultiLine() {
        return multiLine;
    }

    public void setMultiLine(Boolean multiLine) {
        this.multiLine = multiLine;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="threeDots">
    protected String threeDots = null;

    /**
     * Возвращает символ многоточние. <br>
     * по умолчанию используется unicode: \u2026 - \u005cu2026
     * @return символ многоточние
     */
    public String getThreeDots() {
        return threeDots;
    }

    /**
     * Указывает символ многоточние. <br>
     * по умолчанию используется unicode: \u2026 - \u005cu2026
     * @param threeDots символ многоточние
     */
    public void setThreeDots(String threeDots) {
        this.threeDots = threeDots;
    }
    //</editor-fold>

    protected String[] initConvertToLines(String text){
        return new String[]{ text };
    }

    protected String[] trimSpaces(String[] lines){
//        String txt = Text.join(lines, "\n");
//        return Text.splitNewLines(txt.trim());
        String[] res = new String[lines.length];
        for( int i=0; i<lines.length; i++ ){
            res[i] = lines[i].trim();
        }
        return res;
    }

    protected String[] evalNewLine(String[] srcLines){
        ArrayList<String> res = new ArrayList<String>();
        for( String line : srcLines ){
            String[] lines = Text.splitNewLines(line);
            if( lines.length==1 ){
                res.add(line);
            }else if( lines.length>1 ){
                int idx = -1;
                for( String sline : lines ){
                    idx++;
                    if( idx>0 ){
                        Integer bc = getBlankLineCount();
                        if( bc==null || bc<1 )bc = 1;
                        for( int j=0; j<bc; j++ ){
                            res.add( getBlankLine() );
                        }
                    }
                    res.add(sline);
                }
            }
        }
        return res.toArray(new String[]{});
    }

    protected String[] wordWrap(String[] srcLines){
        Integer w = getWidth();
        if( w==null || w<1 )return srcLines;

        ArrayList<String> res = new ArrayList<String>();
        for( String line : srcLines ){
            String[] lines = Text.wordWrap(line, w);
            res.addAll(Arrays.asList(lines));
        }
        return res.toArray(new String[]{});
    }

    protected String[] horizAlign(String[] srcLines){
        Integer w = getWidth();
        if( w==null || w<1 )return srcLines;

        Align align = getHorzAlign();
        if( align==null )align = Align.Begin;

        String fill = getFillAlignText();
        if( fill==null || fill.length()<1 )fill = " ";
        String[] lines = Text.align(srcLines, align, fill, w);
        return lines;
    }

    protected String[] buildMultiLineData(String text){
        if( text==null )throw new IllegalArgumentException( "text==null" );

        String[] lines = initConvertToLines(text);

        Boolean evnewline = isEvalNewLine();
        if( evnewline!=null && evnewline ){
            lines = evalNewLine(lines);
        }else{
            lines = Text.splitNewLines(text);
        }

        Integer w = getWidth();
        if( w!=null && w>0 )lines = wordWrap(lines);

        Boolean trimcaps = isTrimSpaces();
        if( trimcaps!=null && trimcaps )lines = trimSpaces(lines);

        if( w!=null && w>0 )lines = horizAlign(lines);

        return lines;
    }

    protected String[] buildSingleLineData(String text){
        Integer w = getWidth();
        if( w==null ){
            return new String[]{text};
        }
        if( w<=0 ){
            return new String[]{};
        }

        String tdots = getThreeDots();
        tdots = tdots==null ? "\u2026" : tdots;

        if( tdots.length()>1 ) tdots = tdots.substring(0, 1);
        if( tdots.length()<1 ) tdots = "\u2026";

        if( w<text.length() ){
            if( w==1 ){
                return new String[]{ tdots }; // многоточие
            }

            Align a = getHorzAlign();
            if( a==null )a = Align.Begin;
            if( a==Align.Begin ){
                text = text.substring(0, w-1) + tdots;
            }else if( a==Align.Center ){
                int diffw = text.length() - w;
                int leftw = diffw / 2;

                text = text.substring(leftw);

                if( text.length()>w ){
                    text = text.substring(text.length()-w, text.length());
                }

                if( text.length()>2 ){
                    text = tdots + text.substring(1, text.length()-2) + tdots;
                }else if( text.length()==2 ){
                    text = tdots + tdots;
                }else if( text.length()==1 ){
                    text = tdots;
                }
            }else if( a==Align.End ){
                String rtext = text.substring(text.length()-w, text.length());
                text = tdots + rtext.substring(1);
            }

            return new String[]{text};
        }else if( w>text.length() ){
            Align a = getHorzAlign();
            if( a==null )a = Align.Begin;
            String fill = getFillAlignText();
            String aligned = Text.align(text, a, fill, w);
            return new String[]{ aligned };
        }else{
            return new String[]{ text };
        }
    }

    protected String[] buildCellData( String text ){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        Boolean ml = isMultiLine();
        if( ml!=null && ml ){
            return buildMultiLineData(text);
        }else{
            return buildSingleLineData(text);
        }
    }

    public TextCell build(String text){
        if( text==null )throw new IllegalArgumentException( "text==null" );
        String[] lines = buildCellData(text);
        return new TextCell(lines,text);
    }
}
