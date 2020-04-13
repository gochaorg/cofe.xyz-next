package xyz.cofe.gui.swing.str;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Текстовый блок из набора RichString строк.
 * Строки RichString формируются из AString
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class RichBlock extends ArrayList<RichString> {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(RichBlock.class.getName());
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
        logger.entering(RichBlock.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(RichBlock.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(RichBlock.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор
     * @param initialCapacity начальный размер
     */
    public RichBlock(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Конструктор
     */
    public RichBlock() {
    }

    /**
     * Конструктор
     * @param c набор отображаемых строк
     */
    public RichBlock( Collection<? extends RichString> c ) {
        super(c);
    }

    /**
     * Конструктор
     * @param left координаты
     * @param top координаты
     * @param halign вертикальное выравнивание (0...1)
     * @param text текст
     * @param splitNewLines разделять текст на набор строк
     * @param gs контекст отображения
     * @param textAntialiasingValue способ размытия текста
     */
    public RichBlock( double left, double top, double halign, AString text, boolean splitNewLines, Graphics2D gs, Object textAntialiasingValue ){
        if (text== null) throw new IllegalArgumentException("text==null");
        if (gs== null) throw new IllegalArgumentException("gs==null");

        List<AString> lines = null;
        if( splitNewLines ){
            lines = text.splitNewLines();
        }else{
            lines = new ArrayList<>();
            lines.add( text );
        }

        if( textAntialiasingValue!=null ){
            gs.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING, textAntialiasingValue
            );
        }

        FontRenderContext frc = gs.getFontRenderContext();

        for( AString astr : lines ){
            RichString rstr = astr.rstring(frc);
            this.add(rstr);
        }

        arrange().leftTop(left, top, halign);
    }

    /**
     * Возвращает максимальну ширину строк в текстовом блоке
     * @return максимальная ширина строк
     */
    public double getMaxItemWidth(){
        double w = 0;
        for( RichString rs : this ){
            double rw = rs.getWidth();
            if( w<rw ) w = rw;
        }
        return w;
    }

    /**
     * Отображение текстового блока
     * @param gs контекст отображения
     */
    public void render( Graphics2D gs ){
        if (gs== null) {
            throw new IllegalArgumentException("gs==null");
        }
        for( RichString rs : this ){
            if( rs==null )continue;
            rs.render(gs);
        }
    }

    /**
     * Выравнивание текст в блоке
     */
    public class Arrange {
        public Arrange topBottom( Double top ){
            int cnt = size();
            if( cnt<1 )return this;

            Double firstY = null;
            if( cnt>0 ){
                RichString rs = get(0);
                if( rs!=null ){
                    firstY = rs.getY();
                }
            }

            if( top==null && firstY==null )return this;

            double y = top!=null ? top : firstY;
            for( RichString it : RichBlock.this ){
                if( it==null )continue;
                it.setY((float)y);
                y += it.getHeight();
            }
            return this;
        }
        public Arrange halign( double align, Double left ){
            int cnt = size();

            if( cnt>0 && left!=null ){
                RichString rs = get(0);
                if( rs!=null ){
                    rs.setX((float)(double)left);
                }
            }

            if( cnt<2 )return this;

            double maxw = getMaxItemWidth();
            for( int ri=1; ri<cnt; ri++ ){
                RichString rs = get(ri);
                if( rs==null )continue;

                double w = rs.getWidth();
                double x = (maxw - w) * align + ( left!=null ? (double)left : rs.getX() );
                rs.setX(x);
            }

            return this;
        }
        public Arrange leftTop( double left, double top, double halign ){
            return topBottom(top).halign(halign, left);
        }
    }

    /**
     * Выравнивание текста в блоке
     * @return выравниватель текстового блока
     */
    public Arrange arrange(){
        return new Arrange();
    }

    /**
     * Возвращает размер текстового блока
     * @return размер текстового блока
     */
    public Rectangle2D getBounds(){
        Double xmin = null;
        Double ymin = null;
        Double xmax = null;
        Double ymax = null;

        for( RichString it : this ){
            if( it==null )continue;
            xmin = xmin!=null ? Math.min(xmin, it.getMinX()) : it.getMinX();
            ymin = ymin!=null ? Math.min(ymin, it.getMinY()) : it.getMinY();
            xmax = xmax!=null ? Math.max(xmax, it.getMaxX()) : it.getMaxX();
            ymax = ymax!=null ? Math.max(ymax, it.getMaxY()) : it.getMaxY();
        }

        if( xmin==null || ymin==null || xmax==null || ymax==null ){
            return new Rectangle2D.Double(0, 0, 0, 0);
        }

        return new Rectangle2D.Double(xmin, ymin, xmax - xmin, ymax - ymin);
    }
}
