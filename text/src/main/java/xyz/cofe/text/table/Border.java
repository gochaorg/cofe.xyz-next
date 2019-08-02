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
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.text.Text;

/**
 * Стиль бордюра вокруг ячейки
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class Border {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(Border.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(Border.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(Border.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(Border.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(Border.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(Border.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(Border.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /**
     * Конструктор по умолчанию
     */
    public Border(){
    }

    /**
     * Конструктор копирования
     * @param src образец для копирования
     */
    public Border(Border src){
        if( src!=null ){
            leftText = src.leftText;
            rightText = src.rightText;
            topText = src.topText;
            bottomText = src.bottomText;

            leftTopText = src.leftTopText;
            rightTopText = src.rightTopText;

            leftBottomText = src.leftBottomText;
            rightBottomText = src.rightBottomText;

            leftWidth = src.leftWidth;
            rightWidth = src.rightWidth;
            topHeight = src.topHeight;
            bottomHeigth = src.bottomHeigth;
        }
    }

    /**
     * Создает клон
     * @return клон
     */
    @Override
    public synchronized Border clone(){
        return new Border(this);
    }

    /**
     * Возвращает true если рамка не задана - "пустая"
     * @return true - рамка не задана - "пустая"
     */
    public synchronized boolean isEmpty(){
        return
            getLeftWidth()==0
                && getRightWidth()==0
                && getTopHeight()==0
                && getBottomHeigth()==0;
    }

    //<editor-fold defaultstate="collapsed" desc="leftTopCell">
    private transient TextCell leftTopCell = null;

    /**
     * Левая верхняя ячейка текста. <br>
     * При изменении текста / ширины, она пересоздается
     * @return левая-верх ячейка
     */
    public synchronized TextCell getLeftTopCell(){
        if( leftTopCell!=null )return leftTopCell;
        leftTopCell = TextCell.createBlock(leftTopText, getLeftWidth(), getTopHeight());
        return leftTopCell;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rightTopCell">
    private transient TextCell rightTopCell = null;

    /**
     * Правая верхняя ячейка текста. <br>
     * При изменении текста / ширины, она пересоздается
     * @return правая-верх ячейка
     */
    public synchronized TextCell getRightTopCell(){
        if( rightTopCell!=null )return rightTopCell;
        rightTopCell = TextCell.createBlock(rightTopText, getRightWidth(), getTopHeight());
        return rightTopCell;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="leftBottomCell">
    private transient TextCell leftBottomCell = null;

    /**
     * Левая нижняя ячейка текста. <br>
     * При изменении текста / ширины, она пересоздается
     * @return левая-ниж ячейка
     */
    public synchronized TextCell getLeftBottomCell(){
        if( leftBottomCell!=null )return leftBottomCell;
        leftBottomCell = TextCell.createBlock(leftBottomText, getLeftWidth(), getBottomHeigth());
        return leftBottomCell;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rightBottomCell">
    private transient TextCell rightBottomCell = null;

    /**
     * Правая нижняя ячейка текста. <br>
     * При изменении текста / ширины, она пересоздается
     * @return правая-ниж ячейка
     */
    public synchronized TextCell getRightBottomCell(){
        if( rightBottomCell!=null )return rightBottomCell;
        rightBottomCell = TextCell.createBlock(rightBottomText, getRightWidth(), getBottomHeigth());
        return rightBottomCell;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="topCell">
    private transient TextCell topCell = null;
    private transient Integer topCellWidth = null;

    /**
     * Верхняя ячейка текста. <br>
     * При изменении текста / ширины, она пересоздается
     * @param width ширина ячейки
     * @return верх ячейка
     */
    public synchronized TextCell getTopCell(int width){
        if( topCellWidth!=null && topCellWidth==width && topCell!=null )
            return topCell;

        topCell = TextCell.createBlock(topText, width, getTopHeight());
        topCellWidth = width;

        return topCell;
    }

    /**
     * Верхняя ячейка текста. <br>
     * При изменении текста / ширины, она пересоздается
     * @param b ширина ячейки
     * @return верх ячейка
     */
    public synchronized TextCell getTopCell(Bounds b){
        int w = b.getWidth();
        return getTopCell(w);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bottomCell">
    private transient TextCell bottomCell = null;
    private transient Integer bottomCellWidth = null;

    /**
     * Нижняя ячейка текста. <br>
     * При изменении текста / ширины, она пересоздается
     * @param width ширина ячейки
     * @return ниж ячейка
     */
    public synchronized TextCell getBottomCell(int width){
        if( bottomCellWidth!=null && bottomCellWidth==width && bottomCell!=null ){
            return bottomCell;
        }
        bottomCell = TextCell.createBlock(bottomText, width, getBottomHeigth());
        bottomCellWidth = width;
        return bottomCell;
    }

    /**
     * Нижняя ячейка текста. <br>
     * При изменении текста / ширины, она пересоздается
     * @param b ширина ячейки
     * @return ниж ячейка
     */
    public synchronized TextCell getBottomCell(Bounds b){
        return getBottomCell(b.getWidth());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="leftCell">
    private transient TextCell leftCell = null;
    private transient Integer leftCellHeight = null;

    /**
     * Левая ячейка текста. <br>
     * При изменении текста / высота, она пересоздается
     * @param height высота ячейки
     * @return левая ячейка
     */
    public synchronized TextCell getLeftCell(int height){
        if( leftCellHeight!=null && leftCellHeight==height && leftCell!=null ){
            return leftCell;
        }
        leftCell = TextCell.createBlock(leftText, getLeftWidth(), height);
        leftCellHeight = height;
        return leftCell;
    }

    /**
     * Левая ячейка текста. <br>
     * При изменении текста / высота, она пересоздается
     * @param b высота ячейки
     * @return левая ячейка
     */
    public synchronized TextCell getLeftCell(Bounds b){
        return getLeftCell(b.getHeight());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rightCell">
    private transient TextCell rightCell = null;
    private transient Integer rightCellHeight = null;

    /**
     * Правая ячейка текста. <br>
     * При изменении текста / высота, она пересоздается
     * @param height высота ячейки
     * @return правая ячейка
     */
    public synchronized TextCell getRightCell(int height){
        if( rightCellHeight!=null && rightCellHeight==height && rightCell!=null ){
            return rightCell;
        }
        rightCell = TextCell.createBlock(rightText, getRightWidth(), height);
        rightCellHeight = height;
        return rightCell;
    }

    /**
     * Правая ячейка текста. <br>
     * При изменении текста / высота, она пересоздается
     * @param b высота ячейки
     * @return правая ячейка
     */
    public synchronized TextCell getRightCell(Bounds b){
        return getRightCell(b.getHeight());
    }
    //</editor-fold>

    public static ArrayList<String> format( TextCell cell, Border border ){
        if( cell==null )throw new IllegalArgumentException( "cell==null" );
        if( border==null )throw new IllegalArgumentException( "border==null" );

        return format( cell.getTextLines(), border );
    }

    public static ArrayList<String> format( Iterable<String> lines, Border border ){
        if( lines==null )throw new IllegalArgumentException( "lines==null" );
        if( border==null )throw new IllegalArgumentException( "border==null" );
        if( lines instanceof Collection ){
            Collection<String> c = (Collection<String>)lines;
            return format(c.toArray(new String[]{}), border);
        }else{
            ArrayList<String> l = new ArrayList<String>();
            for( String s : lines ){
                l.add(s);
            }
            return format(l.toArray(new String[]{}), border);
        }
    }

    public static ArrayList<String> format( String[] lines, Border border ){
        if( lines==null )throw new IllegalArgumentException( "lines==null" );
        if( border==null )throw new IllegalArgumentException( "border==null" );
        ArrayList<String> res = new ArrayList<String>();

        Bounds b = Bounds.get(lines);

        if( border.getTopHeight()>0 ){
            TextCell tLT = border.getLeftTopCell();
            TextCell tT = border.getTopCell(b);
            TextCell tRT = border.getRightTopCell();
            res.addAll( TextCell.joinAsList(tLT, tT, tRT ) );
        }

        if( lines.length>0 ){
            TextCell tL = border.getLeftCell(lines.length);
            TextCell tR = border.getRightCell(lines.length);

            String[] lL = tL.getTextLines();
            String[] lR = tR.getTextLines();

            StringBuilder sb = new StringBuilder();
            for( int y=0; y<lines.length; y++ ){
                sb.setLength(0);
                if( y<lL.length ){
                    sb.append(lL[y]);
                }else{
                    sb.append(Text.repeat(" ", border.getLeftWidth()));
                }
                sb.append(lines[y]);
                if( y<lR.length ){
                    sb.append(lR[y]);
                }else{
                    sb.append(Text.repeat(" ", border.getRightWidth()));
                }
                res.add( sb.toString() );
            }
        }

        if( border.getBottomHeigth()>0 ){
            TextCell tLB = border.getLeftBottomCell();
            TextCell tB = border.getBottomCell(b);
            TextCell tRB = border.getRightBottomCell();
            res.addAll( TextCell.joinAsList(tLB, tB, tRB ) );
        }

        return res;
    }

    //<editor-fold defaultstate="collapsed" desc="leftText">
    private String leftText = null;

    /**
     * Возвращает текст заполнитель левой части бордюра
     * @return текст заполнитель
     */
    public synchronized String getLeftText() {
        return leftText;
    }

    /**
     * Устанавливает текст заполнитель левой части бордюра
     * @param leftText текст заполнитель
     */
    public synchronized void setLeftText(String leftText) {
        this.leftText = leftText;
        this.leftCell = null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rightText">
    private String rightText = null;

    /**
     * Возвращает текст заполнитель правой части бордюра
     * @return текст заполнитель
     */
    public synchronized String getRightText() {
        return rightText;
    }

    /**
     * Устанавливает текст заполнитель правой части бордюра
     * @param rightText текст заполнитель
     */
    public synchronized void setRightText(String rightText) {
        this.rightText = rightText;
        rightCell = null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bottomText">
    private String bottomText = null;

    /**
     * Возвращает текст заполнитель нижней части бордюра
     * @return текст заполнитель
     */
    public synchronized String getBottomText() {
        return bottomText;
    }

    /**
     * Устанавливает текст заполнитель нижней части бордюра
     * @param bottomText текст заполнитель
     */
    public synchronized void setBottomText(String bottomText) {
        this.bottomText = bottomText;
        bottomCell = null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="topText">
    private String topText = null;

    /**
     * Возвращает текст заполнитель верхней части бордюра
     * @return текст заполнитель
     */
    public synchronized String getTopText() {
        return topText;
    }

    /**
     * Устанавливает текст заполнитель верхней части бордюра
     * @param topText текст заполнитель
     */
    public synchronized void setTopText(String topText) {
        this.topText = topText;
        this.topCell = null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="leftTopText">
    private String leftTopText = null;

    /**
     * Возвращает текст заполнитель левого-верхнего угла бордюра
     * @return текст заполнитель
     */
    public synchronized String getLeftTopText() {
        return leftTopText;
    }

    /**
     * Устанавливает текст заполнитель левого-верхнего угла бордюра
     * @param leftTopText текст заполнитель
     */
    public synchronized void setLeftTopText(String leftTopText) {
        this.leftTopText = leftTopText;
        leftTopCell = null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rightTopText">
    private String rightTopText = null;

    /**
     * Возвращает текст заполнитель правого-верхнего угла бордюра
     * @return текст заполнитель
     */
    public synchronized String getRightTopText() {
        return rightTopText;
    }

    /**
     * Устанавливает текст заполнитель нижнего-правого угла бордюра
     * @param rightTopText текст заполнитель
     */
    public synchronized void setRightTopText(String rightTopText) {
        this.rightTopText = rightTopText;
        rightTopCell = null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="leftBottomText">
    private String leftBottomText = null;

    /**
     * Возвращает текст заполнитель левого-нижнего угла бордюра
     * @return текст заполнитель
     */
    public synchronized String getLeftBottomText() {
        return leftBottomText;
    }

    /**
     * Устанавливает текст заполнитель левого-нижнего угла бордюра
     * @param leftBottomText текст заполнитель
     */
    public synchronized void setLeftBottomText(String leftBottomText) {
        this.leftBottomText = leftBottomText;
        leftBottomCell = null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rightBottomText">
    private String rightBottomText = null;

    /**
     * Возвращает текст заполнитель нижнего-правого угла бордюра
     * @return текст заполнитель
     */
    public synchronized String getRightBottomText() {
        return rightBottomText;
    }

    /**
     * Устанавливает текст заполнитель нижнего-правого угла бордюра
     * @param rightBottomText текст заполнитель
     */
    public synchronized void setRightBottomText(String rightBottomText) {
        this.rightBottomText = rightBottomText;
        rightBottomCell = null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="leftWidth">
    private int leftWidth = 0;

    /**
     * Возвращает шириную левой части бордюра
     * @return ширина
     */
    public synchronized int getLeftWidth() {
        return leftWidth;
    }

    /**
     * Устанавливает шириную левой части бордюра
     * @param leftWidth ширина
     */
    public synchronized void setLeftWidth(int leftWidth) {
        this.leftWidth = leftWidth;
        leftTopCell = null;
        leftBottomCell = null;
        leftCell = null;
    }

    /**
     *Устанавливает шириную левой части бордюра
     * @param leftWidth ширина
     * @return ссылка на себя (this)
     */
    public synchronized Border leftWidth(int leftWidth) {
        setLeftWidth(leftWidth);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rightWidth">
    private int rightWidth = 0;

    /**
     * Возвращает шириную правой части бордюра
     * @return ширина
     */
    public synchronized int getRightWidth() {
        return rightWidth;
    }

    /**
     * Устанавливает шириную правой части бордюра
     * @param rightWidth ширина
     */
    public synchronized void setRightWidth(int rightWidth) {
        this.rightWidth = rightWidth;
        rightTopCell = null;
        rightBottomCell = null;
        rightCell = null;
    }

    /**
     * Устанавливает шириную правой части бордюра
     * @param rightWidth ширина
     * @return ссылка на себя (this)
     */
    public synchronized Border rightWidth(int rightWidth) {
        setRightWidth(rightWidth);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="topHeight">
    private int topHeight = 0;

    /**
     * Возвращает высоту верхней части бордюра
     * @return высота
     */
    public synchronized int getTopHeight() {
        return topHeight;
    }

    /**
     * Устанавливает высоту верхней части бордюра
     * @param topHeight высота
     */
    public synchronized void setTopHeight(int topHeight) {
        this.topHeight = topHeight;
        leftTopCell = null;
        rightTopCell = null;
        topCell = null;
    }

    /**
     * Устанавливает высоту верхней части бордюра
     * @param topHeight высота
     * @return ссылка на себя (this)
     */
    public synchronized Border topHeight(int topHeight) {
        setTopHeight(topHeight);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bottomHeigth">
    private int bottomHeigth = 0;

    /**
     * Возвращает высоту нижней части бордюра
     * @return высота
     */
    public synchronized int getBottomHeigth() {
        return bottomHeigth;
    }

    /**
     * Устанавливает высоту нижней части бордюра
     * @param bottomHeigth высота
     */
    public synchronized void setBottomHeigth(int bottomHeigth) {
        this.bottomHeigth = bottomHeigth;
        leftBottomCell = null;
        rightBottomCell = null;
        bottomCell = null;
    }

    /**
     * Устанавливает высоту нижней части бордюра
     * @param bottomHeigth высота
     * @return ссылка на себя (this)
     */
    public synchronized Border bottomHeigth(int bottomHeigth) {
        setBottomHeigth(bottomHeigth);
        return this;
    }
    //</editor-fold>
}
