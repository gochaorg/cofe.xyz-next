/*
 * The MIT License
 *
 * Copyright 2018 user.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package xyz.cofe.gui.swing.cell;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import xyz.cofe.collection.LRUCache;
import xyz.cofe.fn.Pair;
import xyz.cofe.gui.swing.color.ColorModificator;
import xyz.cofe.gui.swing.color.NColorModificator;
import xyz.cofe.gui.swing.str.FlatBlock;
import xyz.cofe.text.Text;

/**
 * Рендер строки/текста
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class FlatTextRender implements CellRender
{
    /**
     * Конструктор
     */
    public FlatTextRender(){
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public FlatTextRender(FlatTextRender sample){
        if( sample!=null ){
            this.font = sample.font;
            this.aliasing = sample.aliasing;
            this.textAlign = sample.textAlign;
            this.valign = sample.valign;
            this.halign = sample.halign;
            this.baseColor = sample.baseColor;

            this.focusModificator = sample.focusModificator!=null ?
                sample.focusModificator.clone() : null;
            this.selectModificator = sample.selectModificator!=null ?
                sample.selectModificator.clone() : null;

            for( NColorModificator cm : sample.getRowModificators() ){
                if( cm!=null ){
                    getRowModificators().add( cm.clone() );
                }
            }
            for( NColorModificator cm : sample.getColumnModificators() ){
                if( cm!=null ){
                    getColumnModificators().add( cm.clone() );
                }
            }

            this.paddingLeft = sample.paddingLeft;
            this.paddingTop = sample.paddingTop;
            this.paddingRight = sample.paddingRight;
            this.paddingBottom = sample.paddingBottom;
        }
    }

    @Override
    public FlatTextRender clone(){
        return new FlatTextRender(this);
    }

    //<editor-fold defaultstate="collapsed" desc="font : Font">
    protected Font font;

    /**
     * Указывает шрифт
     * @return шрифт
     */
    public Font getFont() { return font; }

    /**
     * Указывает шрифт
     * @param font шрифт
     */
    public void setFont(Font font) { this.font = font; }

    /**
     * Указывает шрифт
     * @param fnt шрифт
     * @return self ссылка
     */
    public FlatTextRender font(Font fnt){
        setFont(fnt);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="aliasing : TextAliasing">
    protected TextAliasing aliasing = TextAliasing.ON;

    /**
     * Указывает тип отображения шрифта (anti-aliasing) текста
     * @return тип отображения шрифта
     */
    public TextAliasing getAliasing() { return aliasing; }

    /**
     * Указывает тип отображения шрифта (anti-aliasing) текста
     * @param aliasing тип отображения шрифта
     */
    public void setAliasing(TextAliasing aliasing) { this.aliasing = aliasing; }

    /**
     * Указывает тип отображения шрифта (anti-aliasing) текста
     * @param v тип отображения шрифта
     * @return self ссылка
     */
    public FlatTextRender aliasing(TextAliasing v){
        setAliasing(v);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="textAlign : double">
    protected double textAlign = 0.0;

    /**
     * Указывает выравнивание текста (0..1) в пределах контекста
     * @return выравнивание текста (0..1)
     */
    public double getTextAlign() { return textAlign; }
    /**
     * Указывает выравнивание текста (0..1) в пределах контекста
     * @param textAlign выравнивание текста (0..1)
     */
    public void setTextAlign(double textAlign) { this.textAlign = textAlign; }
    /**
     * Указывает выравнивание текста (0..1) в пределах контекста
     * @param v выравнивание текста (0..1)
     * @return self ссылка
     */
    public FlatTextRender textAlign(double v){
        setTextAlign(v);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="valign : double">
    protected double valign = 0.0;
    /**
     * Указывает выравнивание относительно контекста
     * @return 0-по верхнему краю; ... 0.5-по центру; ... 1-по нижнему.
     */
    public double getValign() {
        return valign;
    }
    /**
     * Указывает выравнивание относительно контекста
     * @param valign 0-по верхнему краю; ... 0.5-по центру; ... 1-по нижнему.
     */
    public void setValign(double valign) {
        this.valign = valign;
    }
    /**
     * Указывает выравнивание относительно контекста
     * @param v 0-по верхнему краю; ... 0.5-по центру; ... 1-по нижнему.
     * @return self ссылка
     */
    public FlatTextRender valign(double v){
        setValign(v);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="halign : double">
    protected Double halign = 0.0;
    /**
     * Указывает выравнивание относительно контекста
     * @return 0-по левому краю; ... 0.5-по центру; ... 1-по правому.
     */
    public Double getHalign() {
        return halign;
    }
    /**
     * Указывает выравнивание относительно контекста
     * @param halign 0-по левому краю; ... 0.5-по центру; ... 1-по правому.
     */
    public void setHalign(Double halign) {
        this.halign = halign;
    }
    /**
     * Указывает выравнивание относительно контекста
     * @param v 0-по левому краю; ... 0.5-по центру; ... 1-по правому.
     * @return self ссылка
     */
    public FlatTextRender halign(double v){
        setHalign(v);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="baseColor : Color">
    protected Color baseColor;
    /**
     * Указывает базовый цвет текста
     * @return базовый цвет
     */
    public Color getBaseColor() { return baseColor; }
    /**
     * Указывает базовый цвет текста
     * @param baseColor базовый цвет
     */
    public void setBaseColor(Color baseColor) { this.baseColor = baseColor; }
    /**
     * Указывает базовый цвет текста
     * @param v базовый цвет
     * @return self ссылка
     */
    public FlatTextRender baseColor(Color v){
        setBaseColor(v);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="color : Color">
    protected Color color;
    /**
     * Возвращает цвет текста
     * @return цвет текста, если не null - то отменяет модификации цвета
     */
    public Color getColor() { return color; }
    /**
     * Указывает цвет текста <br>
     * цвет текста, если не null - то отменяет модификации цвета
     * @param color цвет текста
     */
    public void setColor(Color color) { this.color = color; }
    /**
     * Указывает цвет текста <br>
     * цвет текста, если не null - то отменяет модификации цвета
     * @param v цвет текста
     * @return self ссылка
     */
    public FlatTextRender color(Color v){
        setColor(v);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rowModificators : List<NColorModificator>">
    protected List<NColorModificator> rowModificators = new ArrayList<>();

    /**
     * Возвращает модификатор цвета для строк
     * @return выборочный модификатор
     */
    public List<NColorModificator> getRowModificators() {
        if( rowModificators==null )rowModificators = new ArrayList<>();
        return rowModificators;
    }

    /**
     * Указывает модификатор цвета для строк
     * @param mods выборочный модификатор
     */
    public void setRowModificators( List<NColorModificator> mods ){
        rowModificators = mods;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="columnModificators : List<NColorModificator>">
    protected List<NColorModificator> columnModificators = new ArrayList<>();

    /**
     * Возвращает модификатор цвета для колонок
     * @return выборочный модификатор
     */
    public List<NColorModificator> getColumnModificators() {
        if( columnModificators==null )columnModificators = new ArrayList<>();
        return columnModificators;
    }

    /**
     * Указывает модификатор цвета для колонок
     * @param mods выборочный модификатор
     */
    public void setColumnModificators(List<NColorModificator> mods) {
        columnModificators = mods;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="selectModificator : ColorModificator">
    protected ColorModificator selectModificator;
    /**
     * Возвращает модификатор цвета для выбранных пользователем ячеек
     * @return модификатор
     */
    public ColorModificator getSelectModificator() {
        return selectModificator;
    }
    /**
     * Указывает модификатор цвета для выбранных пользователем ячеек
     * @param selectModificator модификатор
     */
    public void setSelectModificator(ColorModificator selectModificator) {
        this.selectModificator = selectModificator;
    }
    /**
     * Указывает модификатор цвета для выбранных пользователем ячеек
     * @param cm модификатор
     * @return self ссылка
     */
    public FlatTextRender selectModificator(ColorModificator cm){
        setSelectModificator(cm);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="focusModificator : ColorModificator">
    protected ColorModificator focusModificator;
    /**
     * Возвращает модификатор цвета для ячеек содержащих фокус ввода
     * @return модификатор цвета
     */
    public ColorModificator getFocusModificator() {
        return focusModificator;
    }
    /**
     * Указывает модификатор цвета для ячеек содержащих фокус ввода
     * @param focusModificator модификатор цвета
     */
    public void setFocusModificator(ColorModificator focusModificator) {
        this.focusModificator = focusModificator;
    }
    /**
     * Указывает модификатор цвета для ячеек содержащих фокус ввода
     * @param cm модификатор цвета
     * @return self ссылка
     */
    public FlatTextRender focusModificator(ColorModificator cm){
        setFocusModificator(cm);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="paddingLeft : double">
    protected double paddingLeft = 0;
    /**
     * Указывает дополнение пустого места слева
     * @return дополнение слева
     */
    public double getPaddingLeft() { return paddingLeft; }
    /**
     * Указывает дополнение пустого места слева
     * @param paddingLeft дополнение слева
     */
    public void setPaddingLeft(double paddingLeft) { this.paddingLeft = paddingLeft; }
    /**
     * Указывает дополнение пустого места слева
     * @param pad дополнение слева
     * @return self ссылка
     */
    public FlatTextRender paddingLeft(double pad){
        this.paddingLeft = pad;
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="paddingRight : double">
    /**
     * Указывает дополнение пустого места справа
     */
    protected double paddingRight = 0;
    /**
     * Указывает дополнение пустого места справа
     * @return дополнение справа
     */
    public double getPaddingRight() { return paddingRight; }
    /**
     * Указывает дополнение пустого места справа
     * @param paddingRight дополнение справа
     */
    public void setPaddingRight(double paddingRight) { this.paddingRight = paddingRight; }
    /**
     * Указывает дополнение пустого места справа
     * @param pad дополнение справа
     * @return self ссылка
     */
    public FlatTextRender paddingRight(double pad){
        this.paddingLeft = pad;
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="paddingTop : double">
    protected double paddingTop = 0;
    /**
     * Указывает дополнение пустого места сверху
     * @return дополнение сверху
     */
    public double getPaddingTop() { return paddingTop; }
    /**
     * Указывает дополнение пустого места сверху
     * @param paddingTop дополнение сверху
     */
    public void setPaddingTop(double paddingTop) { this.paddingTop = paddingTop; }
    /**
     * Указывает дополнение пустого места сверху
     * @param pad дополнение сверху
     * @return self ссылка
     */
    public FlatTextRender paddingTop(double pad){
        this.paddingTop = pad;
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="paddingBottom : double">
    protected double paddingBottom = 0;
    /**
     * Указывает дополнение пустого места снизу
     * @return дополнение снизу
     */
    public double getPaddingBottom() { return paddingBottom; }
    /**
     * Указывает дополнение пустого места снизу
     * @param paddingBottom дополнение снизу
     */
    public void setPaddingBottom(double paddingBottom) { this.paddingBottom = paddingBottom; }
    /**
     * Указывает дополнение пустого места снизу
     * @param pad дополнение снизу
     * @return self ссылка
     */
    public FlatTextRender paddingBottom(double pad){
        this.paddingBottom = pad;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="maxLinesCount : int = -1 - max text lines">
    /**
     * Указывает максимальное отображаемое кол-во линий текста
     */
    protected int maxLinesCount = -1;

    /**
     * Указывает максимальное отображаемое кол-во линий текста
     * @return кол-во или -1/0 - без ограничения
     */
    public int getMaxLinesCount() {
        return maxLinesCount;
    }

    /**
     * Указывает максимальное отображаемое кол-во линий текста
     * @param maxLinesCount кол-во или -1/0 - без ограничения
     */
    public void setMaxLinesCount(int maxLinesCount) {
        this.maxLinesCount = maxLinesCount;
    }

    /**
     * Указывает максимальное отображаемое кол-во линий текста
     * @param maxLinesCount кол-во или -1/0 - без ограничения
     * @return self ссылка
     */
    public FlatTextRender maxLinesCount(int maxLinesCount){
        this.maxLinesCount = maxLinesCount;
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="maxLineLength : int = -1 - max line len">
    /**
     * Указывает максимальное отображаемое кол-во символов в строке
     */
    protected int maxLineLength = -1;

    /**
     * Указывает максимальное отображаемое кол-во символов в строке
     * @return кол-во или -1/0 - без ограничения
     */
    public int getMaxLineLength() {
        return maxLineLength;
    }

    /**
     * Указывает максимальное отображаемое кол-во символов в строке
     * @param maxLineLength кол-во или -1/0 - без ограничения
     */
    public void setMaxLineLength(int maxLineLength) {
        this.maxLineLength = maxLineLength;
    }

    /**
     * Указывает максимальное отображаемое кол-во символов в строке
     * @param maxLineLength кол-во или -1/0 - без ограничения
     * @return self ссылка
     */
    public FlatTextRender maxLineLength(int maxLineLength){
        this.maxLineLength = maxLineLength;
        return this;
    }
    //</editor-fold>

    @Override
    public Rectangle2D cellRectangle(Graphics2D gs, CellContext context) {
        if( gs==null )throw new IllegalArgumentException("gs == null");
        if( context==null )throw new IllegalArgumentException("context == null");

//        TimeLaps tlap = this.timeLaps;
//        if( tlap!=null )tlap.begin("cellRectangle");

        FlatBlock tb = prepare(gs, context);

        Pair<Double,Double> pos = evalPosition(context, tb);
        Rectangle2D.Double rect = tb.getBounds();
        double x = pos.a()+paddingLeft;
        double y = pos.b()+paddingTop;

        rect = new Rectangle2D.Double(x-paddingLeft, y-paddingTop,
            rect.getWidth()+paddingLeft+paddingRight,
            rect.getHeight()+paddingTop+paddingBottom);

//        if( tlap!=null )tlap.end("cellRectangle");
        return rect;
    }

    @Override
    public void cellRender(Graphics2D gs, CellContext context) {
        if( gs==null )throw new IllegalArgumentException("gs == null");
        if( context==null )throw new IllegalArgumentException("context == null");

        // TimeLaps tlap = this.timeLaps;
        // if( tlap!=null )tlap.begin("cellRender");

//        if( tlap!=null )tlap.begin("prepare");
        FlatBlock tb = prepare(gs, context);
//        if( tlap!=null )tlap.end("prepare");

        Object taSave = gs.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        Paint paintSave = gs.getPaint();

        //<editor-fold defaultstate="collapsed" desc="text aliasing">
        TextAliasing ta = getAliasing();
        if( ta!=null ){
            switch(ta){
                case DEFAULT:
                    gs.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
                    break;
                case GASP:
                    gs.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
                    break;
                case LCD_HBGR:
                    gs.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);
                    break;
                case LCD_HRGB:
                    gs.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    break;
                case LCD_VBGR:
                    gs.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR);
                    break;
                case LCD_VRGB:
                    gs.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
                    break;
                case OFF:
                    gs.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                    break;
                case ON:
                    gs.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    break;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="baseColor table context">
        if(baseColor!=null){
            Color col = baseColor;

            if( context instanceof TableContext ){
                TableContext tcc = (TableContext)context;
                int column = tcc.getColumn();
                int row = tcc.getRow();

                for( NColorModificator cm : getRowModificators() ){
                    if( cm==null )continue;
                    int cycle = cm.getCycle();
                    int ph = cm.getPhase();
                    if( cycle<1 )continue;
                    if( cycle==1 ){
                        col = cm.apply(col);
                        continue;
                    }
                    if( (row%cycle)==ph ){
                        col = cm.apply(col);
                    }
                }
                for( NColorModificator cm : getColumnModificators() ){
                    if( cm==null )continue;
                    int cycle = cm.getCycle();
                    int ph = cm.getPhase();
                    if( cycle<1 )continue;
                    if( cycle==1 ){
                        col = cm.apply(col);
                        continue;
                    }
                    if( (column%cycle)==ph ){
                        col = cm.apply(col);
                    }
                }

                if( selectModificator!=null && tcc.isSelected() ){
                    col = selectModificator.apply(col);
                }
                if( focusModificator!=null && tcc.isFocus() ){
                    col = focusModificator.apply(col);
                }
            }

            gs.setPaint(col);
        }
        if( color!=null ){
            gs.setPaint(color);
        }
        //</editor-fold>

//        if( tlap!=null )tlap.begin("evalPosition");
        Pair<Double,Double> pos = evalPosition(context, tb);
//        if( tlap!=null )tlap.end("evalPosition");

        double x = pos.a()+paddingLeft;
        double y = pos.b()+paddingTop;

//        if( tlap!=null )tlap.begin("render");
        tb.render(gs,x,y, context.getBounds());
//        if( tlap!=null )tlap.end("render");

        gs.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, taSave);
        gs.setPaint(paintSave);

//        if( tlap!=null )tlap.end("cellRender");
    }

    private Map<String,FlatBlock> cache;
    /**
     * Возвращает кэш расчитанных текстовых блок
     * @return кэш текстовых блоков
     */
    public Map<String,FlatBlock> getCache(){
        return cache;
    }
    /**
     * Указывает кэш расчитанных текстовых блок
     * @param cache кэш расчитанных текстовых блок
     */
    public void setCache(Map<String,FlatBlock> cache){
        this.cache = cache;
    }

//    private TimeLaps timeLaps = new TimeLaps();
//    public TimeLaps getTimeLaps(){
//        return timeLaps;
//    }
//    public void setTimeLaps(TimeLaps tlap){
//        this.timeLaps = tlap;
//    }

    private int minCachedTextLength = 150;
    public int getMinCachedTextLength() { return minCachedTextLength; }
    public void setMinCachedTextLength(int minCachedTextLength) { this.minCachedTextLength = minCachedTextLength; }

    private Checksum crc = new Adler32();
    public Checksum getCrc() { return crc; }
    public void setCrc(Checksum crc) { this.crc = crc; }

    private FlatBlock prepare(Graphics2D gs, CellContext context) {
//        TimeLaps tlap = this.timeLaps;
//        if( tlap!=null )tlap.begin("get ctx");

        Font font = this.font != null ? this.font : gs.getFont();
        FontRenderContext fctx = gs.getFontRenderContext();

        Object value = context.getValue();
        String txt = value==null ? "" : value.toString();

//        if( tlap!=null )tlap.end("get ctx");

        if( maxLinesCount>0 ){
//            if( tlap!=null )tlap.begin("split lines");

            String[] lines = Text.splitNewLines(txt);
            if( lines.length > maxLinesCount ){
                txt = Text.join(lines, "\n", 0, maxLinesCount)+"...";
            }

//            if( tlap!=null )tlap.end("split lines");
        }

        Map<String,FlatBlock> cache = this.cache;
        String hash = null;
        if( cache!=null && txt.length()>=minCachedTextLength ){
//            if( tlap!=null )tlap.begin("hash");
            hash = FlatBlock.hash(txt, true, textAlign, font, crc);
//            if( tlap!=null )tlap.end("hash");

//            if( tlap!=null )tlap.begin("cache read");
            FlatBlock cached = cache.get(hash);
//            if( tlap!=null )tlap.end("cache read");

            if( cached!=null )return cached;
        }

//        if( tlap!=null )tlap.begin("FlatBlock");
        FlatBlock tb = null;
        tb = new FlatBlock(txt, true, textAlign, font, fctx, hash);
//        if( tlap!=null )tlap.end("FlatBlock");

        if( cache!=null && txt.length()>=minCachedTextLength ){
//            if( tlap!=null )tlap.begin("cache write");
            cache.put(hash, tb);
//            if( tlap!=null )tlap.end("cache write");
        }

        return tb;
    }
    private Pair<Double,Double> evalPosition( CellContext context, FlatBlock tb ){
//        TimeLaps tlap = this.timeLaps;
//        if( tlap!=null )tlap.begin("evalPosition");

        double x = 0;
        double y = 0;

        Rectangle2D ctxRect = context.getBounds();
        if( ctxRect!=null ){
            x = ctxRect.getMinX();
            y = ctxRect.getMinY();
        }

        Rectangle2D txtRect = tb.getBounds();
        if( halign!=0 && ctxRect!=null && txtRect!=null ){
            double wdiff = ctxRect.getWidth() - txtRect.getWidth()
                - paddingLeft - paddingRight;
            x += wdiff * halign;
        }

        if( valign!=0 && ctxRect!=null && txtRect!=null ){
            double hdiff = ctxRect.getHeight() - txtRect.getHeight()
                - paddingTop - paddingBottom ;
            y += hdiff * valign;
        }

//        if( tlap!=null )tlap.end("evalPosition");
        return Pair.of(x,y);
    }

    /**
     * Кэш расчитаных текстовых блоков
     */
    public static class TLRUCache extends LRUCache<String,FlatBlock> {
        protected long lifetimeLimit = 1000L * 15L;

        /**
         * Конструктор
         * @param cacheSizeMax максимальное кол-во записей
         * @param lifetimeLimit макс время (мс) жизни записи в кэше
         */
        public TLRUCache(int cacheSizeMax, long lifetimeLimit) {
            super(cacheSizeMax);
            this.lifetimeLimit = lifetimeLimit;
        }

        /**
         * Возвращает макс время (мс) жизни записи в кэше
         * @return макс время (мс) жизни записи
         */
        public long getLifetimeLimit() {
            return lifetimeLimit;
        }

        /**
         * Указывает макс время (мс) жизни записи в кэше
         * @param lifetimeLimit макс время (мс) жизни записи
         */
        public void setLifetimeLimit(long lifetimeLimit) {
            this.lifetimeLimit = lifetimeLimit;
        }

        protected final WeakHashMap<String,Long> cachedTime = new WeakHashMap<>();

        @Override
        public FlatBlock get(Object key) {
            if( key!=null ){
                Long t = cachedTime.get(key);
                Long tdiff = t!=null ? Math.abs(System.currentTimeMillis() - t) : null;
                if( tdiff!=null && tdiff<lifetimeLimit && lifetimeLimit>0 ){
                    return super.get(key);
                }else{
                    remove(key);
                    cachedTime.remove(key);
                    return null;
                }
            }
            return super.get(key);
        }

        @Override
        public FlatBlock put(String key, FlatBlock value) {
            if( key!=null && value!=null ){
                cachedTime.put(key, System.currentTimeMillis());
            }
            return super.put(key, value);
        }
    }
}
