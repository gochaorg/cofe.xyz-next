package xyz.cofe.gui.swing.str;

import xyz.cofe.text.Text;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;


/**
 * Прямоугольный "блок" простых строк для рендера многострочного текста
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class FlatBlock {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(FlatBlock.class.getName());
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
        logger.entering(FlatBlock.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(FlatBlock.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(FlatBlock.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор
     * @param text исходный текст
     * @param multiLine исходный текст разбить на несколько строк
     * @param align горизонтальное выравнивание (0...1)
     * @param font шрифт
     * @param fctx контекст отображения
     * @param computedHash вычисленный хеш строки, шрифта и прочих параметров влияющих на отображение текста
     * @see #hash(String, boolean, double, Font, Checksum)
     */
    public FlatBlock(String text, boolean multiLine, double align, Font font, FontRenderContext fctx, String computedHash){
        if( text==null )throw new IllegalArgumentException("text == null");
        if( font==null )throw new IllegalArgumentException("font == null");
        if( fctx==null )throw new IllegalArgumentException("fctx == null");

        lines = new ArrayList<>();

        double x = 0;
        double y = 0;

        String[] strlines = multiLine ? Text.splitNewLines(text) : new String[]{ text };
        for( String strline : strlines ){
            FlatString fstr = new FlatString(strline, font, fctx, this);

            y += fstr.getAscent();
            fstr.setX(x);
            fstr.setY(y);
            y += fstr.getDescent() + fstr.getLeading();

            lines.add( fstr );
        }

        align(align);

        hash = computedHash!=null ? computedHash : hash(text, multiLine, align, font, null);
    }

    private String hash;

    /**
     * Возвращает вычисленный хеш строки, шрифта и прочих параметров влияющих на отображение текста
     * @return хэш
     * @see #hash(String, boolean, double, Font, Checksum)
     */
    public String getHash(){ return hash; }

    /**
     * Вычисляет хэш для строки, шрифта и прочих входных параметров
     * @param text исходный текст, используется в хэше
     * @param multiLine разбить исходный текст на несколько строк, используется в хэше
     * @param align горизонтальное выравнивание (0...1), используется в хэше
     * @param font шрифт для отображения, используется в хэше
     * @param crc алгоритм вычисления хэша
     * @return хэш
     */
    public static String hash( String text, boolean multiLine, double align, Font font, Checksum crc){
        if (text== null) {
            throw new IllegalArgumentException("text==null");
        }
        if (font== null) {
            throw new IllegalArgumentException("font==null");
        }

        if( crc==null )crc = new CRC32();

        if( text.length()<250 ){
            return (multiLine?"1":"0")+
                "|"+align+
                "|"+font.getName()+"|"+font.getSize()+"|"+font.getStyle()+
                "|"+text;
        }else{
            long crcval = -1;
            crc.reset();
            byte[] bytes = text.getBytes();
            crc.update(bytes,0,bytes.length);
            crcval = crc.getValue();

            return (multiLine?"1":"0")+
                "|"+align+
                "|"+font.getName()+"|"+font.getSize()+"|"+font.getStyle()+
                "|"+crcval;
        }
    }

    private final ArrayList<FlatString> lines;

    //<editor-fold defaultstate="collapsed" desc="bounds : Rectangle2D.Double">
    protected Rectangle2D.Double bounds;

    /**
     * Возвращает границы отображаемого блока
     * @return границы отображаемого блока
     */
    public Rectangle2D.Double getBounds(){
        if( bounds!=null )return bounds;
        if( lines.isEmpty() ){
            bounds = new Rectangle2D.Double(0, 0, 0, 0);
            return bounds;
        }

        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = Double.MIN_VALUE;
        double maxy = Double.MIN_VALUE;

        for( FlatString fstr : lines ){
            minx = Math.min(fstr.getMinX(), minx);
            miny = Math.min(fstr.getMinY(), miny);
            maxx = Math.max(fstr.getMaxX(), maxx);
            maxy = Math.max(fstr.getMaxY(), maxy);
        }

        bounds = new Rectangle2D.Double(minx, miny, maxx - minx, maxy - miny);
        return bounds;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getMaxLineWidth()">
    private double getMaxLineWidth(){
        double w = 0;
        for( FlatString fstr : lines ){
            if( fstr==null )continue;
            double w2 = fstr.getWidth();
            w = Math.max(w2, w);
        }
        return w;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="render()">
    /**
     * Отображение текстового блока
     * @param gs контекст отображения
     */
    public void render( Graphics2D gs ){
        if( gs==null )throw new IllegalArgumentException("gs == null");
        for( FlatString fstr : lines ){
            if( fstr==null )continue;
            fstr.render(gs);
        }
    }

    /**
     * Отображение текстового блока
     * @param gs контекст отображения
     * @param x координаты
     * @param y координаты
     * @param bounds граница отображения,
     * используется нижная граница (maxY) для отсечеия хвостовой части
     */
    public void render( Graphics2D gs, double x, double y, Rectangle2D bounds ){
        if( gs==null )throw new IllegalArgumentException("gs == null");
        AffineTransform at = (AffineTransform)gs.getTransform().clone();
        gs.translate(x, y);

        Double yMax = bounds!=null ? bounds.getMaxY() - y : null;

        for( FlatString fstr : lines ){
            if( fstr==null )continue;
            if( yMax!=null && fstr.getY()>yMax )continue;

            fstr.render(gs);
        }

        gs.setTransform(at);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="align()">
    private void align(double xalign){
        double wmax = getMaxLineWidth();
        for( FlatString fstr : lines ){
            double wstr = fstr.getWidth();
            //double wtot = rect.getWidth();
            double wtot = wmax;
            double wdiff = wtot - wstr;
            fstr.setX(wdiff*xalign);
        }
    }
    //</editor-fold>
}
