package xyz.cofe.gui.swing.str;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

/**
 * Cтрока без форматирования подготовленная для рендера.
 * Предпологается что строки будет собраны в текстовый блок для отображения
 * @author gochaorg
 */
public class FlatString {
    /**
     * Инициализация своств text, font, width, height, ascent, descent, lineHeight, leading
     * @param text текст
     * @param font шрифт
     * @param ctx контекст
     * @param fblock Блок к которому пренадлежит строка
     */
    public FlatString( String text, Font font, FontRenderContext ctx, FlatBlock fblock ){
        if (text== null) {
            throw new IllegalArgumentException("text==null");
        }
        if (font== null) {
            throw new IllegalArgumentException("font==null");
        }
        if (ctx== null) {
            throw new IllegalArgumentException("ctx==null");
        }


        this.textBlock = fblock;
        this.text = text;
        this.font = font;

        width  = 0.0;
        height = 0.0;
        ascent = 0.0;
        descent = 0.0;
        lineHeight = 0.0;
        leading = 0.0;

        Rectangle2D rect = font.getStringBounds(text, ctx);
        if( rect==null ){
            return;
        }

        width = rect.getWidth();
        height = rect.getHeight();

        LineMetrics lm = font.getLineMetrics(text, ctx);

        ascent = lm.getAscent();
        descent = lm.getDescent();
        lineHeight = lm.getHeight();
        leading = lm.getLeading();
    }

    //<editor-fold defaultstate="collapsed" desc="textBlock : FlatBlock">
    protected transient FlatBlock textBlock;

    /**
     * Возвращает текстовый блок, к которому подготовлена строка
     * @return текстовый блок
     */
    public FlatBlock getTextBlock(){
        return textBlock;
    }

    /**
     * Указывает текстовый блок, к которому подготовлена строка
     * @param textBlock текстовый блок
     */
    private void setTextBlock(FlatBlock textBlock){
        this.textBlock = textBlock;
    }
    /*
    private synchronized void nextscn(){
        if( textBlock!=null ){
            textBlock.nextscn();
        }
    }
    */
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="text : String">
    /**
     * Отображаемый текст
     */
    protected String text;

    /**
     * Указывает отображаемый текст
     * @return отображаемый текст
     */
    public String getText() {
        return text;
    }

    /*
     * Указывает отображаемый текст
     * @param text отображаемый текст
     */
    private void setText(String text) {
        this.text = text;
        //nextscn();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="font : Font">
    protected transient Font font;

    /**
     * Возвращает шрифт используемый при рендере строки
     * @return шрифт
     */
    public Font getFont() {
        return font;
    }

    /**
     * Указывает шрифт используемый при рендере строки
     * @param font шрифт
     */
    private void setFont(Font font) {
        this.font = font;
        //nextscn();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="x : double">
    protected double x;

    /**
     * Возвращает координаты для рендера строки
     * @return координаты
     */
    public double getX() {
        return x;
    }

    /**
     * Указывает координаты для рендера строки
     * @param x координаты
     */
    public void setX(double x) {
        this.x = x;
        //nextscn();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="y : double">
    protected double y;

    /**
     * Возвращает координаты для рендера строки
     * @return координаты
     */
    public double getY() {
        return y;
    }

    /**
     * Указывает координаты для рендера строки
     * @param y координаты
     */
    public void setY(double y) {
        this.y = y;
        //nextscn();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="width : double">
    protected double width;

    /**
     * Возвращает ширину текстровой строки
     * @return ширина (пиксели)
     */
    public double getWidth() {
        return width;
    }

    /**
     * Указывает ширину текстровой строки
     * @param width ширина (пиксели)
     */
    private void setWidth(double width) {
        this.width = width;
        //nextscn();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="height : double">
    protected double height;

    /**
     * Возвращает высоту текстровой строки
     * @return высота (пиксели)
     */
    public double getHeight() {
        return height;
    }

    /**
     * Указыает высоту текстровой строки
     * @param height высота (пиксели)
     */
    private void setHeight(double height) {
        this.height = height;
        //nextscn();
    }
    //</editor-fold>

    /**
     * Возвращает координаты для рендера строки
     * @return координаты
     */
    public double getMinX(){ return x; }

    /**
     * Возвращает координаты для рендера строки
     * @return координаты
     */
    public double getMinY(){ return y; }

    /**
     * Возвращает координаты для рендера строки
     * @return координаты
     */
    public double getMaxX(){ return x+width; }

    /**
     * Возвращает координаты для рендера строки
     * @return координаты
     */
    public double getMaxY(){ return y+height; }

    //<editor-fold defaultstate="collapsed" desc="ascent : double">
    protected double ascent;

    /**
     * Возвращает высоту букв от базовой линии до верха
     * @return высота букв
     */
    public double getAscent() {
        return ascent;
    }

    /**
     * Указывает высоту букв от базовой линии до верха
     * @param ascent высота
     */
    private void setAscent(double ascent) {
        this.ascent = ascent;
        //nextscn();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="descent : double">
    protected double descent;

    /**
     * Возвращает высоту букв от базовой линии до низа букв с "хвостиками"
     * @return высота букв
     */
    public double getDescent() {
        return descent;
    }

    /**
     * Указывает высоту букв от базовой линии до низа букв с "хвостиками"
     * @param descent высота букв
     */
    private void setDescent(double descent) {
        this.descent = descent;
        //nextscn();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="lineHeight : double">
    protected double lineHeight;

    /**
     * Возвразает высоту строки
     * @return высота строки
     */
    public synchronized double getLineHeight() {
        return lineHeight;
    }

    /**
     * Указывает высоту строки
     * @param lineHeight высота строки
     */
    private void setLineHeight(double lineHeight) {
        this.lineHeight = lineHeight;
        //nextscn();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="leading : double">
    protected double leading;

    /**
     * Возвращает растояние между нижней границей и до след строки
     * @return растояние между строками
     */
    public double getLeading() {
        return leading;
    }

    /**
     * Указывает растояние между нижней границей и до след строки
     * @param leading расстояние
     */
    private void setLeading(double leading) {
        this.leading = leading;
        //nextscn();
    }
    //</editor-fold>

    /**
     * Отображение строки
     * @param gs контекст отображения
     */
    public void render( Graphics2D gs ){
        if( gs==null )throw new IllegalArgumentException("gs == null");

        Font saveFont = gs.getFont();
        Font renderFont = font!=null ? font : saveFont;

        if( renderFont!=null )gs.setFont(renderFont);
        gs.drawString(text, (float)x, (float)y);

        if( saveFont!=null )gs.setFont(saveFont);
    }

    /**
     * Отображение строки
     * @param gs контекст
     * @param x координаты отображения строки
     * @param y координаты отображения строки
     */
    public void render( Graphics2D gs, double x, double y ){
        if( gs==null )throw new IllegalArgumentException("gs == null");

        Font saveFont = gs.getFont();
        Font renderFont = font!=null ? font : saveFont;

        if( renderFont!=null )gs.setFont(renderFont);
        gs.drawString(text, (float)x, (float)y);

        if( saveFont!=null )gs.setFont(saveFont);
    }
}
