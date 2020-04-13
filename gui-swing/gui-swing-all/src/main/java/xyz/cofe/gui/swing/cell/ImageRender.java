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

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Отрисовка иконок/изображения.
 *
 * <p>
 * Для корректного поведения необходимо установить свойство iconComponent
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class ImageRender implements CellRender {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ImageRender.class.getName());
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
        logger.entering(ImageRender.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(ImageRender.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(ImageRender.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор по умолчанию
     */
    public ImageRender(){
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public ImageRender(ImageRender sample){
        if( sample!=null ){
            this.iconComponent = sample.iconComponent;
            this.iconComponentRef = sample.iconComponentRef;

            this.paddingLeft = sample.paddingLeft;
            this.paddingTop = sample.paddingTop;
            this.paddingRight = sample.paddingRight;
            this.paddingBottom = sample.paddingBottom;

            this.halign = sample.halign;
            this.valign = sample.valign;
        }
    }

    /**
     * Конструктор
     * @param cmpt компонент для отрисовки иконок
     * @param weakRef true - использовать weak ссылку на компонент
     */
    public ImageRender(Component cmpt,boolean weakRef){
        setIconComponent(cmpt, weakRef);
    }

    @Override
    public synchronized ImageRender clone(){
        return new ImageRender(this);
    }

    //<editor-fold defaultstate="collapsed" desc="iconComponent : Component">
    protected transient Component iconComponent;
    protected transient WeakReference<Component> iconComponentRef;

    /**
     * Указывает на компонент для отрисовки иконок
     * @return компонент для отрисовки иконок
     */
    public synchronized Component getIconComponent(){
        if( iconComponent!=null )return iconComponent;
        return iconComponentRef!=null ?
            iconComponentRef.get() : null;
    }

    /**
     * Указывает на компонент для отрисовки иконок
     * @param component компонент для отрисовки иконок
     * @param weak true - использовать weak ссылку на компонент; false - hard ссылку
     */
    public synchronized void setIconComponent(Component component,boolean weak){
        this.iconComponent = weak ? null : component;
        this.iconComponentRef = weak && component!=null ? new WeakReference<>( component ) : null;
    }

    /**
     * Указывает на компонент для отрисовки иконок
     * @param component компонент для отрисовки иконок
     * @param weak true - использовать weak ссылку на компонент; false - hard ссылку
     * @return self ссылка
     */
    public synchronized ImageRender iconComponent(Component component,boolean weak){
        setIconComponent(component, weak);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="paddingLeft : double">
    protected double paddingLeft = 0;
    /**
     * Указывает дополнение пустого места слева
     * @return дополнение слева
     */
    public synchronized double getPaddingLeft() { return paddingLeft; }
    /**
     * Указывает дополнение пустого места слева
     * @param paddingLeft дополнение слева
     */
    public synchronized void setPaddingLeft(double paddingLeft) { this.paddingLeft = paddingLeft; }
    /**
     * Указывает дополнение пустого места слева
     * @param pad дополнение слева
     * @return self ссылка
     */
    public synchronized ImageRender paddingLeft(double pad){
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
    public synchronized double getPaddingRight() { return paddingRight; }
    /**
     * Указывает дополнение пустого места справа
     * @param paddingRight дополнение справа
     */
    public synchronized void setPaddingRight(double paddingRight) { this.paddingRight = paddingRight; }
    /**
     * Указывает дополнение пустого места справа
     * @param pad дополнение справа
     * @return self ссылка
     */
    public synchronized ImageRender paddingRight(double pad){
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
    public synchronized double getPaddingTop() { return paddingTop; }
    /**
     * Указывает дополнение пустого места сверху
     * @param paddingTop дополнение сверху
     */
    public synchronized void setPaddingTop(double paddingTop) { this.paddingTop = paddingTop; }
    /**
     * Указывает дополнение пустого места сверху
     * @param pad дополнение сверху
     * @return self ссылка
     */
    public synchronized ImageRender paddingTop(double pad){
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
    public synchronized double getPaddingBottom() { return paddingBottom; }
    /**
     * Указывает дополнение пустого места снизу
     * @param paddingBottom дополнение снизу
     */
    public synchronized void setPaddingBottom(double paddingBottom) { this.paddingBottom = paddingBottom; }
    /**
     * Указывает дополнение пустого места снизу
     * @param pad дополнение снизу
     * @return self ссылка
     */
    public synchronized ImageRender paddingBottom(double pad){
        this.paddingBottom = pad;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="valign : double">
    protected double valign = 0.0;
    /**
     * Указывает выравнивание относительно контекста
     * @return 0-по верхнему краю; ... 0.5-по центру; ... 1-по нижнему.
     */
    public synchronized double getValign() {
        return valign;
    }
    /**
     * Указывает выравнивание относительно контекста
     * @param valign 0-по верхнему краю; ... 0.5-по центру; ... 1-по нижнему.
     */
    public synchronized void setValign(double valign) {
        this.valign = valign;
    }
    /**
     * Указывает выравнивание относительно контекста
     * @param valign 0-по верхнему краю; ... 0.5-по центру; ... 1-по нижнему.
     * @return self ссылка
     */
    public synchronized ImageRender valign(double valign){
        setValign(valign);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="halign : double">
    protected double halign = 0.0;
    /**
     * Указывает выравнивание относительно контекста
     * @return 0-по левому краю; ... 0.5-по центру; ... 1-по правому.
     */
    public double getHalign() {
        return halign;
    }
    /**
     * Указывает выравнивание относительно контекста
     * @param halign 0-по левому краю; ... 0.5-по центру; ... 1-по правому.
     */
    public void setHalign(double halign) {
        this.halign = halign;
    }
    /**
     * Указывает выравнивание относительно контекста
     * @param halign 0-по левому краю; ... 0.5-по центру; ... 1-по правому.
     * @return self ссылка
     */
    public synchronized ImageRender halign(double halign){
        setHalign(halign);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="enableImageContext : boolean">
    protected volatile boolean enableImageContext = true;
    /**
     * Указывает отображать ли изображение, если значение поддерживает интерфейс ImageContext
     * @return true (по умолчанию) - отображать
     * @see ImageContext
     */
    public synchronized boolean isEnableImageContext() { return enableImageContext; }
    /**
     * Указывает отображать ли изображение, если значение поддерживает интерфейс ImageContext
     * @param enableImageContext true (по умолчанию) - отображать
     */
    public synchronized void setEnableImageContext(boolean enableImageContext) { this.enableImageContext = enableImageContext; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="enableIconContext : boolean">
    protected volatile boolean enableIconContext = true;
    /**
     * Указывает отображать ли изображение, если значение поддерживает интерфейс ImageContext
     * @return true (по умолчанию) - отображать
     * @see IconContext
     */
    public synchronized boolean isEnableIconContext() { return enableIconContext; }
    /**
     * Указывает отображать ли изображение, если значение поддерживает интерфейс IconContext
     * @param enableIconContext true (по умолчанию) - отображать
     */
    public synchronized void setEnableIconContext(boolean enableIconContext) { this.enableIconContext = enableIconContext; }
    //</editor-fold>

    @Override
    public synchronized Rectangle2D cellRectangle(Graphics2D gs, CellContext context) {
        Object value = context.getValue();
        if( value instanceof Image ){
            return rectOf(gs, (Image)value, context.getBounds());
        }else if( value instanceof Icon ){
            return rectOf(gs, (Icon)value, context.getBounds());
        }else if( context instanceof ImageContext && enableImageContext ){
            return rectOf(gs, ((ImageContext)context).getImage(), context.getBounds() );
        }else if( context instanceof IconContext && enableIconContext ){
            return rectOf(gs, ((IconContext)context).getIcon(), context.getBounds() );
        }

        return null;
    }

    @Override
    public synchronized void cellRender(Graphics2D gs, CellContext context) {
        if( gs==null )return;
        if( context==null )return;

        Object value = context.getValue();
        if( value instanceof Image ){
            render(gs, (Image)value, context.getBounds());
        }else if( value instanceof Icon ){
            render(gs, (Icon)value, context.getBounds(), context);
        }else if( context instanceof ImageContext && enableImageContext ){
            render(gs, ((ImageContext)context).getImage(), context.getBounds() );
        }else if( context instanceof IconContext && enableIconContext ){
            render(gs, ((IconContext)context).getIcon(), context.getBounds(), context );
        }
    }

    private synchronized void render( Graphics2D gs, Image im, Rectangle2D crect ){
        if( im==null || gs==null || crect==null )return;

        double x = crect.getMinX();
        double y = crect.getMinY();

        double iw = im.getWidth(null)+paddingLeft+paddingRight;
        double ih = im.getHeight(null)+paddingTop+paddingBottom;

        double xdiff = (int)crect.getWidth()  - iw;
        double ydiff = (int)crect.getHeight() - ih;

        if( halign!=0 )x += xdiff * halign;
        if( valign!=0 )y += ydiff * valign;

        gs.drawImage(
            im,
            (int)(x+paddingLeft), (int)(y+paddingTop),
            null
        );
    }

    private synchronized Rectangle2D rectOf( Graphics2D gs, Image im, Rectangle2D crect ){
        if( im==null || gs==null || crect==null )return new Rectangle2D.Double(0, 0, 0, 0);

        double iw = im.getWidth(null)+paddingLeft+paddingRight;
        double ih = im.getHeight(null)+paddingTop+paddingBottom;

        int x = (int)crect.getMinX();
        int y = (int)crect.getMinY();

        double xdiff = (int)crect.getWidth()  - iw;
        double ydiff = (int)crect.getHeight() - ih;

        if( halign!=0 )x += xdiff * halign;
        if( valign!=0 )y += ydiff * valign;

        return new Rectangle2D.Double(x, y, iw, ih);
    }

    private synchronized void render( Graphics2D gs, Icon im, Rectangle2D crect, CellContext cctx ){
        if( im==null || gs==null || crect==null )return;

        Component c = getIconComponent();
        if( c==null && cctx instanceof TableCellContext ){
            c = ((TableCellContext)cctx).getTable();
        }

        if( c!=null ){
            int x = (int)crect.getMinX()+(int)paddingLeft;
            int y = (int)crect.getMinY()+(int)paddingTop;

            int iw = im.getIconWidth()+(int)paddingLeft+(int)paddingRight;
            int ih = im.getIconHeight()+(int)paddingTop+(int)paddingBottom;

            double xdiff = (int)crect.getWidth()  - iw;
            double ydiff = (int)crect.getHeight() - ih;

            if( halign!=0 )x += xdiff * halign;
            if( valign!=0 )y += ydiff * valign;

            im.paintIcon(c, gs, x, y);
        }else if( im instanceof ImageIcon ){
            Image img = ((ImageIcon)im).getImage();
            if( img!=null ){
                render(gs, img, crect);
            }
        }
    }

    private synchronized Rectangle2D rectOf( Graphics2D gs, Icon im, Rectangle2D crect ){
        if( im==null || gs==null || crect==null )return new Rectangle2D.Double(0, 0, 0, 0);

//        Component c = getIconComponent();
//        if( c==null )return new Rectangle2D.Double(0, 0, 0, 0);

        int iw = im.getIconWidth()+(int)paddingLeft+(int)paddingRight;
        int ih = im.getIconHeight()+(int)paddingTop+(int)paddingBottom;

        int x = (int)crect.getMinX();
        int y = (int)crect.getMinY();

        double xdiff = (int)crect.getWidth()  - iw;
        double ydiff = (int)crect.getHeight() - ih;

        if( halign!=0 )x += xdiff * halign;
        if( valign!=0 )y += ydiff * valign;

        return new Rectangle2D.Double(x, y, iw, ih);
    }
}
