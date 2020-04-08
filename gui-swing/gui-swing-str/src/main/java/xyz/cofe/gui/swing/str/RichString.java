package xyz.cofe.gui.swing.str;

import java.awt.*;
import java.awt.font.TextLayout;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Строка текста с атрибутами (расположение x/y и размеры) подготовленная для рендера
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class RichString {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(RichString.class.getName());
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
        logger.entering(RichString.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(RichString.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(RichString.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор
     * @param tl неизменное графическое изображение стилизованных символьных данных.
     * @param x координаты
     * @param y координаты
     */
    public RichString( TextLayout tl, double x, double y ){
        if( tl==null )throw new IllegalArgumentException("tl == null");

        this.layout = tl;
        this.x = x;
        this.y = y;
    }

    /**
     * Конструктор
     * @param tl неизменное графическое изображение стилизованных символьных данных.
     */
    public RichString( TextLayout tl ){
        if( tl==null )throw new IllegalArgumentException("tl == null");

        this.layout = tl;
        this.x = 0;
        this.y = 0;
    }

    //<editor-fold defaultstate="collapsed" desc="x : double">
    protected double x;

    /**
     * Возвращает координаты отображения
     * @return координаты отображения
     */
    public double getX() {
        return x;
    }

    /**
     * Указывает координаты отображения
     * @param x координаты отображения
     */
    public void setX(double x) {
        this.x = x;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="y : double">
    protected double y;

    /**
     * Возвращает координаты отображения
     * @return координаты отображения
     */
    public double getY() {
        return y;
    }

    /**
     * Указывает координаты отображения
     * @param y координаты отображения
     */
    public void setY(double y) {
        this.y = y;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="layout : TextLayout">
    protected TextLayout layout;

    public TextLayout getLayout() {
        return layout;
    }

    private void setLayout(TextLayout layout) {
        this.layout = layout;
    }
    //</editor-fold>

    /**
     * Возвращает ширину отображаемой строки
     * @return ширина
     */
    public double getWidth(){
        TextLayout tl = layout;
        if( tl==null )return 0;
        return tl.getBounds().getWidth();
    }

    /**
     * Возвращает высоту отображаемой строки
     * @return высота
     */
    public double getHeight(){
        TextLayout tl = layout;
        if( tl==null )return 0;
        return tl.getAscent() + tl.getDescent() + tl.getLeading();
    }

    /**
     * Возвращает координаты отображения
     * @return координаты отображения
     */
    public double getMinX(){ return x; }

    /**
     * Возвращает координаты отображения
     * @return координаты отображения
     */
    public double getMinY(){ return y; }

    /**
     * Возвращает координаты отображения
     * @return координаты отображения
     */
    public double getMaxX(){ return x + getWidth(); }

    /**
     * Возвращает координаты отображения
     * @return координаты отображения
     */
    public double getMaxY(){ return y + getHeight(); }

    /**
     * Отображает строку
     * @param gs контекст отображения
     */
    public void render( Graphics2D gs ){
        if (gs== null) throw new IllegalArgumentException("gs==null");

        TextLayout tl = layout;
        if( tl==null )return;

        double y = this.y;
        y += tl.getAscent();

        tl.draw(gs, (float)x, (float)y);
    }
}
