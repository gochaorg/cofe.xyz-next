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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;

import xyz.cofe.fn.Fn2;
import xyz.cofe.gui.swing.color.ColorModificator;
import xyz.cofe.gui.swing.color.NColorModificator;

/**
 * Отображение метки
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class LabelRender implements CellRender
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(LabelRender.class.getName());
    private static final Level logLevel = logger.getLevel();

    private static final boolean isLogSevere =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.SEVERE.intValue();

    private static final boolean isLogWarning =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.WARNING.intValue();

    private static final boolean isLogInfo =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.INFO.intValue();

    private static final boolean isLogFine =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINE.intValue();

    private static final boolean isLogFiner =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINER.intValue();

    private static final boolean isLogFinest =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINEST.intValue();

    private static void logFine(String message,Object ... args){
        logger.log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        logger.log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        logger.log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        logger.log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        logger.log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        logger.log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        logger.log(Level.SEVERE, null, ex);
    }

    private static void logEntering(String method,Object ... params){
        logger.entering(LabelRender.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(LabelRender.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(LabelRender.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор по умолчанию
     */
    public LabelRender(){
        init();
    }

    /**
     * Конструктор
     * @param cellformat формат ячейки
     */
    public LabelRender(CellFormat cellformat){
        init();
        if( cellformat!=null )this.format = cellformat;
    }

    private synchronized void init(){
        textRender =
            new FlatTextRender().
                aliasing(TextAliasing.ON).baseColor(Color.black).
                focusModificator(new ColorModificator().bright(1.0f));

        backgroundRender =
            new FillRender().
                width(1).widthRelative(true).
                height(1).heightRelative(true).baseColor(Color.white).
                addRowModificator(
                    new NColorModificator(1, 2, new ColorModificator().brighter(-0.1f))
                ).
                selectModificator(new ColorModificator().sate(0.3f).hue(230)).
                focusModificator(new ColorModificator().saturation(0.0f).brighter(-0.5f))
        ;

        borderRender = new LineBorderRender();

        imageRender = new ImageRender();

        backgroundVisible = true;
        imageVisible = true;
        textVisible = true;
        borderVisible = true;
    }

    //<editor-fold defaultstate="collapsed" desc="backgroundVisible : true">
    private volatile boolean backgroundVisible;

    /**
     * Указывает закрашивать фон или нет
     * @return true - закрашивать фон
     */
    public synchronized boolean isBackgroundVisible() {
        return backgroundVisible;
    }

    /**
     * Указывает закрашивать фон или нет
     * @param backgroundVisible true - закрашивать фон
     */
    public synchronized void setBackgroundVisible(boolean backgroundVisible) {
        this.backgroundVisible = backgroundVisible;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="backgroundRender : FillRender">
    private volatile FillRender backgroundRender;
    /**
     * Указывет как закрашивать фон
     * @return закраска фона
     */
    public synchronized FillRender getBackgroundRender(){
        return backgroundRender;
    }
    /**
     * Указывет как закрашивать фон
     * @param render закраска фона
     */
    public synchronized void setBackgroundRender( FillRender render ){
        if( render==null )throw new IllegalArgumentException("render == null");
        this.backgroundRender = render;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="bgRowModificators : List<NColorModificator>">
    /**
     * Указывает чередование строк цвета закраски фона
     * @return чередование закраски фона
     */
    public synchronized List<NColorModificator> getBgRowModificators(){
        if( backgroundRender==null )return null;
        return backgroundRender.getRowModificators();
    }
    /**
     * Указывает чередование строк цвета закраски фона
     * @param mods чередование закраски фона
     */
    public synchronized void setBgRowModificators(List<NColorModificator> mods){
        if( backgroundRender==null )throw new IllegalStateException("backgroundRender == null");
        if( mods==null )throw new IllegalStateException("mods == null");
        backgroundRender.setRowModificators(mods);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="bgColumnModificators : List<NColorModificator>">
    /**
     * Указывает чередование колонок цвета закраски фона
     * @return чередование закраски фона
     */
    public synchronized List<NColorModificator> getBgColumnModificators(){
        if( backgroundRender==null )return null;
        return backgroundRender.getColumnModificators();
    }
    /**
     * Указывает чередование колонок цвета закраски фона
     * @param mods чередование закраски фона
     */
    public synchronized void setBgColumnModificators(List<NColorModificator> mods){
        if( backgroundRender==null )throw new IllegalStateException("backgroundRender == null");
        if( mods==null )throw new IllegalStateException("mods == null");
        backgroundRender.setColumnModificators(mods);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="bgSelectModificator : ColorModificator">
    /**
     * Указывает цвет фона для выбранной строки
     * @return цвет фона для выбранной строки
     */
    public synchronized ColorModificator getBgSelectModificator(){
        if( backgroundRender==null )return null;
        return backgroundRender.getSelectModificator();
    }
    /**
     * Указывает цвет фона для выбранной строки
     * @param mod цвет фона для выбранной строки
     */
    public synchronized void setBgSelectModificator(ColorModificator mod){
        if( backgroundRender==null )throw new IllegalStateException("backgroundRender == null");
        if( mod==null )throw new IllegalStateException("mod == null");
        backgroundRender.setSelectModificator(mod);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="bgFocusModificator : ColorModificator">
    /**
     * Указывает цвет фона для фокусированной ячейки
     * @return цвет фона для фокусированной ячейки
     */
    public synchronized ColorModificator getBgFocusModificator(){
        if( backgroundRender==null )return null;
        return backgroundRender.getFocusModificator();
    }
    /**
     * Указывает цвет фона для фокусированной ячейки
     * @param mod цвет фона для фокусированной ячейки
     */
    public synchronized void setBgFocusModificator(ColorModificator mod){
        if( backgroundRender==null )throw new IllegalStateException("backgroundRender == null");
        if( mod==null )throw new IllegalStateException("mod == null");
        backgroundRender.setFocusModificator(mod);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="bgBaseColor : Color">
    /**
     * Указывает базовый цвет фона
     * @return базовый цвет фона
     */
    public synchronized Color getBgBaseColor(){
        if( backgroundRender==null )return null;
        return backgroundRender.getBaseColor();
    }
    /**
     * Указывает базовый цвет фона
     * @param col базовый цвет фона
     */
    public synchronized void setBgBaseColor(Color col){
        if( backgroundRender==null )throw new IllegalStateException("backgroundRender == null");
        backgroundRender.setBaseColor(col);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="textRender : FlatTextRender">
    private volatile FlatTextRender textRender;
    /**
     * Возвращает рендер текста
     * @return рендер текста
     */
    public synchronized FlatTextRender getTextRender(){
        return textRender;
    }
    /**
     * Указывает рендер текста
     * @param render рендер текста
     */
    public synchronized void setTextRender( FlatTextRender render ){
        if( render==null )throw new IllegalArgumentException("render == null");
        this.textRender = render;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="textVisible : true">
    private volatile boolean textVisible;
    /**
     * Возвращаеть отображать ли текст
     * @return true - отображаеть текст
     */
    public synchronized boolean isTextVisible() {
        return textVisible;
    }
    /**
     * Указывает отображать ли текст
     * @param textVisible true - отображаеть текст
     */
    public synchronized void setTextVisible(boolean textVisible) {
        this.textVisible = textVisible;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="fgRowModificators : List<NColorModificator>">
    /**
     * Указывает чередование строк цвета текста
     * @return чередование цвета
     */
    public synchronized List<NColorModificator> getFgRowModificators(){
        if( textRender==null )return null;
        return textRender.getRowModificators();
    }
    /**
     * Указывает чередование строк цвета текста
     * @param mods чередование цвета
     */
    public synchronized void setFgRowModificators(List<NColorModificator> mods){
        if( textRender==null )throw new IllegalStateException("textRender == null");
        if( mods==null )throw new IllegalStateException("mods == null");
        textRender.setRowModificators(mods);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="fgColumnModificators : List<NColorModificator>">
    /**
     * Указывает чередование колонок цвета текста
     * @return чередование цвета
     */
    public synchronized List<NColorModificator> getFgColumnModificators(){
        if( textRender==null )return null;
        return textRender.getColumnModificators();
    }
    /**
     * Указывает чередование колонок цвета текста
     * @param mods чередование цвета
     */
    public synchronized void setFgColumnModificators(List<NColorModificator> mods){
        if( textRender==null )throw new IllegalStateException("textRender == null");
        if( mods==null )throw new IllegalStateException("mods == null");
        textRender.setColumnModificators(mods);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="fgSelectModificator : ColorModificator">
    /**
     * Указывает цвет текста выделенной ячейки
     * @return цвет текста
     */
    public synchronized ColorModificator getFgSelectModificator(){
        if( textRender==null )return null;
        return textRender.getSelectModificator();
    }
    /**
     * Указывает цвет текста выделенной ячейки
     * @param mod цвет текста
     */
    public synchronized void setFgSelectModificator(ColorModificator mod){
        if( textRender==null )throw new IllegalStateException("textRender == null");
        if( mod==null )throw new IllegalStateException("mod == null");
        textRender.setSelectModificator(mod);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="fgFocusModificator : ColorModificator">
    /**
     * Указывает цвет текста ячейки с фокусом
     * @return цвет текста
     */
    public synchronized ColorModificator getFgFocusModificator(){
        if( textRender==null )return null;
        return textRender.getFocusModificator();
    }
    /**
     * Указывает цвет текста ячейки с фокусом
     * @param mod цвет текста
     */
    public synchronized void setFgFocusModificator(ColorModificator mod){
        if( textRender==null )throw new IllegalStateException("textRender == null");
        if( mod==null )throw new IllegalStateException("mod == null");
        textRender.setFocusModificator(mod);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="fgBaseColor : Color">
    /**
     * Указывает базовый цвет текста
     * @return базовый цвет текста
     */
    public synchronized Color getFgBaseColor(){
        if( textRender==null )return null;
        return textRender.getBaseColor();
    }
    /**
     * Указывает базовый цвет текста
     * @param col базовый цвет текста
     */
    public synchronized void setFgBaseColor(Color col){
        if( textRender==null )throw new IllegalStateException("textRender == null");
        textRender.setBaseColor(col);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="imageRender : ImageRender">
    private volatile ImageRender imageRender;
    /**
     * Указывает рендер картики/иконки
     * @return рендер
     */
    public synchronized ImageRender getImageRender(){
        return imageRender;
    }
    /**
     * Указывает рендер картики/иконки
     * @param render рендер
     */
    public synchronized void setImageRender(ImageRender render){
        if( render==null )throw new IllegalArgumentException("render == null");
        this.imageRender = render;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="imageVisible : true">
    private volatile boolean imageVisible;
    /**
     * Указывает отображать ли иконку
     * @return true - отображать иконку
     */
    public synchronized boolean isImageVisible() {
        return imageVisible;
    }

    /**
     * Указывает отображать ли иконку
     * @param imageVisible true - отображать иконку
     */
    public synchronized void setImageVisible(boolean imageVisible) {
        this.imageVisible = imageVisible;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="iconComponent : Component">
    /**
     * Возвращает awt компонент используемый для рендера иконки
     * @return awt компонент для отображения икноки
     */
    public synchronized Component getIconComponent(){
        ImageRender ir = imageRender;
        return ir!=null ? ir.getIconComponent() : null;
    }
    /**
     * Указывает awt компонент используемый для рендера иконки
     * @param component awt компонент для отображения икноки
     * @param weak true - добавить как weak ссылку
     */
    public synchronized void setIconComponent(Component component,boolean weak){
        ImageRender ir = imageRender;
        if( ir!=null ){
            ir.setIconComponent(component, weak);
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="borderRender : LineBorderRender">
    private volatile LineBorderRender borderRender;
    /**
     * Возвращает рендер рамки
     * @return рендер рамки
     */
    public synchronized LineBorderRender getBorderRender(){
        return borderRender;
    }
    /**
     * Указывает рендер рамки
     * @param render рендер рамки
     */
    public synchronized void setBorderRender(LineBorderRender render){
        if( render==null )throw new IllegalArgumentException("render == null");
        this.borderRender = render;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="borderVisible : true">
    private boolean borderVisible;
    /**
     * Возвращает оторбажать или нет рамку
     * @return true - отображать рамку
     */
    public synchronized boolean isBorderVisible() {
        return borderVisible;
    }
    /**
     * Указывает отображать или нет рамку
     * @param imageVisible true - отображать рамку
     */
    public synchronized void setBorderVisible(boolean imageVisible) {
        this.borderVisible = imageVisible;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="format : CellFormat">
    private volatile CellFormat format;

    /**
     * Форматирование по умолчанию
     * @return Форматирование
     */
    public CellFormat getFormat(){
        if( format!=null )return format;
        synchronized(this){
            if( format!=null )return format;
            format = new CellFormat();
            return format;
        }
    }
    /**
     * Указывает форматирование по умолчанию
     * @param cf формат
     */
    public void setFormat(CellFormat cf){
        synchronized(this){
            format = cf;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="formatters">
    private volatile List<Fn2<CellContext,CellFormat,CellFormat>> formatters;

    /**
     * Список правил условного форматирования
     * @return список правил форматирования
     */
    public List<Fn2<CellContext,CellFormat,CellFormat>> getFormatters(){
        if( formatters!=null )return formatters;
        synchronized(this){
            if( formatters!=null )return formatters;
            formatters = new CopyOnWriteArrayList<>();
            return formatters;
        }
    }

    /**
     * Добавляет форматирование для определенного класса контекста
     * @param <CtxType> Тип контекста
     * @param cls Тип контекста
     * @param formatter Форматирование
     * @return Интерыейс отписки
     */
    public synchronized <CtxType extends CellContext> Closeable addFormatter(
        final Class<CtxType> cls,
        final Fn2<CtxType,CellFormat,CellFormat> formatter
    ){
        if( cls==null )throw new IllegalArgumentException("cls == null");
        if( formatter==null )throw new IllegalArgumentException("formatter == null");
        if( CellContext.class.equals(cls) ){
            getFormatters().add((Fn2)formatter);
            return new Closeable() {
                @Override
                public void close() throws IOException {
                    getFormatters().remove((Fn2)formatter);
                }
            };
        }else{
            final Fn2 ff = (Fn2<CellContext, CellFormat, CellFormat>) ( cc, cf )->{
                if( cc!=null && cls.isAssignableFrom(cc.getClass()) ){
                    CtxType cct = (CtxType)cc;
                    return formatter.apply(cct, cf);
                }
                return null;
            };
            getFormatters().add(ff);
            return new Closeable() {
                @Override
                public void close() throws IOException {
                    getFormatters().remove(ff);
                }
            };
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="clone()">
    @Override
    public synchronized LabelRender clone() {
        return new LabelRender(this);
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public LabelRender(LabelRender sample){
        if( sample!=null ){
            if( sample.textRender!=null ){
                this.textRender = sample.textRender.clone();
            }else{
                this.textRender =
                    new FlatTextRender().
                        aliasing(TextAliasing.ON).baseColor(Color.black).
                        focusModificator(new ColorModificator().bright(1.0f));
            }

            if( sample.backgroundRender!=null ){
                this.backgroundRender = sample.backgroundRender.clone();
            }else{
                backgroundRender =
                    new FillRender().
                        width(1).widthRelative(true).
                        height(1).heightRelative(true).baseColor(Color.white).
                        addRowModificator(
                            new NColorModificator(1, 2, new ColorModificator().brighter(-0.1f))
                        ).
                        selectModificator(new ColorModificator().sate(0.3f).hue(230)).
                        focusModificator(new ColorModificator().saturation(0.0f).brighter(-0.5f))
                ;
            }

            if( sample.borderRender!=null ){
                this.borderRender = sample.borderRender.clone();
            }else{
                this.borderRender = new LineBorderRender();
            }

            if( sample.imageRender!=null ){
                this.imageRender = sample.imageRender.clone();
            }else{
                this.imageRender = new ImageRender();
            }

            if( sample.format!=null ){
                format = sample.format.clone();
            }

            if( sample.formatters !=null ){
                getFormatters().addAll(sample.formatters);
            }

            backgroundVisible = sample.backgroundVisible;
        }else{
            init();
        }
    }
    //</editor-fold>

    private volatile CellContext context;
    private volatile CellContext bodyContext;
    /**
     * Возвращает контекст "тела" данных
     * @return контекст body
     */
    public CellContext getBodyContext(){ return bodyContext; }

    private volatile CellContext textContext;
    /**
     * Возвращает контекст для отображаения текста
     * @return контекст текста
     */
    public CellContext getTextContext(){ return textContext; }

    private volatile CellContext icoContext;
    /**
     * Возвращает контекст для отображаения текста
     * @return контекст отображения иконки
     */
    public CellContext getIcoContext(){ return icoContext; }

    private volatile CellContext backgroundContext;
    /**
     * Возвращает контекст для отображаения фона
     * @return контекст для фона
     */
    public CellContext getBackgroundContext(){ return backgroundContext; }

    private volatile CellContext borderContext;
    /**
     * Возвращает контекст для отображаения рамки
     * @return контекст для рамки
     */
    public CellContext getBorderContext(){ return borderContext; }

    /**
     * Подготовка перед отображением, подготавливает дочерние контексты
     * @param gs интерфейс графики
     * @param context контекст
     * @param cf форматирование
     * @return true - успешно подготовлены контекст
     * @see #getBackgroundContext()
     * @see #getBodyContext()
     * @see #getTextContext()
     * @see #getBorderContext()
     * @see #getIcoContext()
     */
    protected synchronized boolean prepare(Graphics2D gs, CellContext context, CellFormat cf){
        if( gs==null || context==null )return false;

        // Форматирования
        //CellFormat cf = getFormat().clone();
        if( cf==null )cf = getFormat().clone();

        // Сброс форматирования
        getTextRender().setColor(null);
        getTextRender().setMaxLineLength(-1);
        getTextRender().setMaxLinesCount(-1);
        getBackgroundRender().setColor(null);
        getBorderRender().reset();

        // Настройка форматирования
        for( Fn2<CellContext,CellFormat,CellFormat> ff : getFormatters() ){
            if( ff==null )continue;
            CellFormat ncf = ff.apply(context, cf);
            if( ncf!=null ){
                cf = ncf;
            }
        }

        if( cf!=null ){
            FlatTextRender tr = getTextRender();
            if( tr!=null ){
                tr.setTextAlign(cf.getTextAlign(0.0));
                tr.setHalign(cf.getHalign(0.0));
                tr.setValign(cf.getValign(0.0));
                tr.setPaddingLeft(cf.getTextPadLeft(0.0));
                tr.setPaddingTop(cf.getTextPadTop(0.0));
                tr.setPaddingRight(cf.getTextPadRight(0.0));
                tr.setPaddingBottom(cf.getTextPadBottom(0.0));
                tr.setColor(cf.getColor());
                tr.setFont(cf.getFont());
                if( cf.getMaxLineLength()!=null )tr.setMaxLineLength(cf.getMaxLineLength());
                if( cf.getMaxLinesCount()!=null )tr.setMaxLinesCount(cf.getMaxLinesCount());
            }

            FillRender fr = getBackgroundRender();
            if( fr!=null ){
                fr.setColor(cf.getBackgroundColor());
            }

            ImageRender ir = getImageRender();
            if( ir!=null ){
                //ir.setPaddingLeft(cf.getIconPadLeft());
                //ir.setPaddingRight(cf.getIconPadRight());
                //ir.setPaddingTop(cf.getIconPadTop());
                //ir.setPaddingBottom(cf.getIconPadBottom());
            }

            LineBorderRender lbr = getBorderRender();
            if( lbr!=null ){
                cf.applyBorder(lbr);
            }
        }

        ///////////////////// Расчет областей вывода //////////////////////////
        this.context = context;

        // Контекст содержимого
        bodyContext = context.clone();
        bodyContext = bodyContext.padLeft(cf.getPadLeft(0.0));
        bodyContext = bodyContext.padRight(cf.getPadRight(0.0));
        bodyContext = bodyContext.padTop(cf.getPadTop(0.0));
        bodyContext = bodyContext.padBottom(cf.getPadBottom(0.0));

        // Контекст текста
        textContext = bodyContext.clone();

        // Смещение контекста текста с учетом места под иконку
        Icon ico = cf.getIcon();
        Dimension icoPlacehld = cf.getIconPlaceholder();
        if( icoPlacehld!=null ){
            textContext.padLeft(icoPlacehld.getWidth());
        }else if( cf.isAutoIconPlaceholder() && ico!=null && icoPlacehld==null ){
            double pl = cf.getIconPadLeft(0d);
            double pr = cf.getIconPadRight(0d);
            double pt = cf.getIconPadTop(0d);
            double pb = cf.getIconPadBottom(0d);

            icoPlacehld = new Dimension(
                (int)(ico.getIconWidth()+pl+pr),
                (int)(ico.getIconHeight()+pt+pb)
            );
            textContext.padLeft(icoPlacehld.getWidth());
        }

        Rectangle2D textRect = getTextRender().cellRectangle(gs, textContext);

        icoContext = context.clone().value(ico);

        /* Rectangle2D imgSize = ico!=null ? getImageRender().cellRectangle(gs, icoContext) :
            new Rectangle2D.Double(); */

        getImageRender().setHalign(0);
        getImageRender().setValign(0);

        Rectangle2D icoBnd =
            icoPlacehld!=null ?
                new Rectangle2D.Double(
                    textRect.getMinX()-icoPlacehld.getWidth()+cf.getIconPadLeft(0d),
                    textRect.getMinY()+cf.getIconPadTop(0d),
                    icoPlacehld.getWidth(),
                    icoPlacehld.getHeight()
                )
                :   new Rectangle2D.Double(
                textRect.getMinX()-cf.getIconPadLeft(0d),
                textRect.getMinY()+cf.getIconPadTop(0d),
                0,
                0
            );

        icoContext.setBounds(icoBnd);

        backgroundContext = context;
        borderContext = context;

        return true;
    }

    @Override
    public synchronized Rectangle2D cellRectangle(Graphics2D gs, CellContext context) {
        if( !prepare(gs, context, getFormat().clone()) )return null;

        Rectangle2D textRect = null;
        if( textVisible && textContext!=null ){
            textRect = getTextRender().cellRectangle(gs, textContext);
        }

        Rectangle2D imgRect = null;
        if( imageVisible && icoContext!=null ){
            imgRect = getImageRender().cellRectangle(gs, icoContext);
        }

        Double minx = null;
        Double miny = null;
        Double maxx = null;
        Double maxy = null;

        for( Rectangle2D rect : new Rectangle2D[]{ textRect, imgRect } ){
            if( rect==null )continue;
            minx = minx==null ? rect.getMinX() : Math.min(minx, rect.getMinX());
            miny = miny==null ? rect.getMinY() : Math.min(miny, rect.getMinY());
            maxx = maxx==null ? rect.getMaxX() : Math.max(maxx, rect.getMaxX());
            maxy = maxy==null ? rect.getMaxY() : Math.max(maxy, rect.getMaxY());
        }

        if( minx!=null && miny!=null && maxx!=null && maxy!=null ){
            return new Rectangle2D.Double(
                Math.min(minx,maxx),
                Math.min(miny,maxy),

                Math.abs(maxx-minx),
                Math.abs(maxy-miny)
            );
        }

        return null;
    }

    @Override
    public synchronized void cellRender(Graphics2D gs, CellContext context) {
        if( !prepare(gs, context, getFormat().clone()) )return;

        //////////////////////////////// рендер ////////////////////////////
        // рендер фона
        backgroundRender(gs);

        // рендер текста
        textRender(gs);

        // рендер иконки
        imageRender(gs);

        // рендер рамки
        borderRender(gs);
    }

    protected synchronized void backgroundRender(Graphics2D gs){
        if( gs==null )return;

        // рендер фона
        if( backgroundVisible && backgroundContext!=null ){
            getBackgroundRender().cellRender(gs, backgroundContext);
        }
    }

    protected synchronized void textRender(Graphics2D gs){
        if( gs==null )return;

        // рендер фона
        if( textVisible && textContext!=null ){
            getTextRender().cellRender(gs, textContext);
        }
    }

    protected synchronized void imageRender(Graphics2D gs){
        if( gs==null )return;

        // рендер фона
        if( imageVisible && icoContext!=null ){
            getImageRender().cellRender(gs, icoContext);
        }
    }

    protected synchronized void borderRender(Graphics2D gs){
        if( gs==null )return;

        // рендер рамки
        if( borderVisible && borderContext!=null ){
            getBorderRender().cellRender(gs, borderContext);
        }
    }
}
