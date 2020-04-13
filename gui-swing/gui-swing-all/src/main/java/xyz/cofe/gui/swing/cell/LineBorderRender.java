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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Рендер рамки
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class LineBorderRender implements CellRender
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(LineBorderRender.class.getName());
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
        logger.entering(LineBorderRender.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(LineBorderRender.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(LineBorderRender.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор по умолчанию
     */
    public LineBorderRender(){
    }

    /**
     * Конструктор
     * @param width ширина рамки
     * @param color цвет рамки
     */
    public LineBorderRender(double width, Color color){
        set().all(width, color).apply();
    }

    /**
     * Конструктор
     * @param width ширина рамки
     * @param color цвет рамки
     * @param dash узор рамки
     */
    public LineBorderRender(double width, Color color, float[] dash){
        set().all(width, color, dash).apply();
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public LineBorderRender(LineBorderRender sample){
        if( sample!=null ){
            this.leftColor = sample.leftColor;
            this.leftStroke = sample.leftStroke;
            //this.leftOffset = sample.leftOffset!=null ? (Point2D)sample.leftOffset.clone() : null;

            this.rightColor = sample.rightColor;
            this.rightStroke = sample.rightStroke;
            //this.rightOffset = sample.rightOffset!=null ? (Point2D)sample.rightOffset.clone() : null;

            this.topColor = sample.topColor;
            this.topStroke = sample.topStroke;
            //this.topOffset = sample.topOffset!=null ? (Point2D)sample.topOffset.clone() : null;

            this.bottomColor = sample.bottomColor;
            this.bottomStroke = sample.bottomStroke;
            //this.bottomOffset = sample.bottomOffset!=null ? (Point2D)sample.bottomOffset.clone() : null;

            this.inside = sample.inside;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="settings">
    /**
     * Утилита для настройки бордюра
     */
    public static class SideSettings implements Consumer<LineBorderRender>
    {
        public SideSettings(){
        }
        public SideSettings(boolean right,boolean top,boolean left, boolean bottom){
            leftSide = left;
            rightSide = right;
            topSide = top;
            bottomSide = bottom;
        }

        protected boolean leftSide;
        public boolean isLeftSide() { return leftSide; }
        public void setLeftSide(boolean leftSide) { this.leftSide = leftSide; }

        protected boolean rightSide;
        public boolean isRightSide() { return rightSide; }
        public void setRightSide(boolean rightSide) { this.rightSide = rightSide; }

        protected boolean topSide;
        public boolean isTopSide() { return topSide; }
        public void setTopSide(boolean topSide) { this.topSide = topSide; }

        protected boolean bottomSide;
        public boolean isBottomSide() { return bottomSide; }
        public void setBottomSide(boolean bottomSide) { this.bottomSide = bottomSide; }

        protected Color color;
        public Color getColor() { return color; }
        public void setColor(Color color) { this.color = color; }

        protected Double width;
        public Double getWidth() { return width; }
        public void setWidth(Double width) { this.width = width;  }

        protected float[] dash;
        public float[] getDash() { return dash; }
        public void setDash(float[] dash) { this.dash = dash; }

        protected float dashPhase;
        public float getDashPhase() { return dashPhase; }
        public void setDashPhase(float dashPhase) { this.dashPhase = dashPhase;}

        public void apply( LineBorderRender brd ){
            if( brd==null )return;
            if( color!=null ){
                if( leftSide )brd.setLeftColor(color);
                if( topSide )brd.setTopColor(color);
                if( rightSide )brd.setRightColor(color);
                if( bottomSide )brd.setBottomColor(color);
            }
            if( width!=null ){
                if( width>0 ){
                    BasicStroke str =
                        dash!=null && dash.length>0 ?
                            new BasicStroke(
                                width.floatValue(),
                                BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_BEVEL,
                                10f,
                                dash, dashPhase
                            ):
                            new BasicStroke(
                                width.floatValue(),
                                BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_BEVEL
                            );
                    if( leftSide )brd.setLeftStroke(str);
                    if( topSide )brd.setTopStroke(str);
                    if( rightSide )brd.setRightStroke(str);
                    if( bottomSide )brd.setBottomStroke(str);
                }else{
                    BasicStroke str = null;
                    if( leftSide )brd.setLeftStroke(str);
                    if( topSide )brd.setTopStroke(str);
                    if( rightSide )brd.setRightStroke(str);
                    if( bottomSide )brd.setBottomStroke(str);
                }
            }
        }

        @Override
        public void accept(LineBorderRender brd) {
            if( brd!=null )apply(brd);
        }
    }

    /**
     * Настройки
     */
    public static class Settings {
        protected List<Consumer<LineBorderRender>> settings;
        protected LineBorderRender border;

        public Settings(LineBorderRender brd){
            settings = new ArrayList<>();
            border = brd;
        }

        public LineBorderRender apply(){
            if( border!=null && settings!=null ){
                for( Consumer<LineBorderRender> set : settings ){
                    if( set!=null ){
                        set.accept(border);
                    }
                }
            }
            return border;
        }

        public Settings all( double width, Color color ){
            SideSettings ss = new SideSettings(true,true,true,true);
            ss.setWidth(width);
            ss.setColor(color);
            settings.add(ss);
            return this;
        }

        public Settings all( double width, Color color, float[] dash ){
            SideSettings ss = new SideSettings(true,true,true,true);
            ss.setWidth(width);
            ss.setColor(color);
            ss.setDash(dash);
            settings.add(ss);
            return this;
        }

        public Settings top( double width, Color color ){
            SideSettings ss = new SideSettings(false,true,false,false);
            ss.setWidth(width);
            ss.setColor(color);
            settings.add(ss);
            return this;
        }

        public Settings left( double width, Color color ){
            SideSettings ss = new SideSettings(false,false,true,false);
            ss.setWidth(width);
            ss.setColor(color);
            settings.add(ss);
            return this;
        }

        public Settings right( double width, Color color ){
            SideSettings ss = new SideSettings(true,false,false,false);
            ss.setWidth(width);
            ss.setColor(color);
            settings.add(ss);
            return this;
        }

        public Settings bottom( double width, Color color ){
            SideSettings ss = new SideSettings(false,false,false,true);
            ss.setWidth(width);
            ss.setColor(color);
            settings.add(ss);
            return this;
        }
    }

    /**
     * Указывает настройки отображения рамки
     * @return найстрока отображения рамки
     */
    public Settings set(){
        return new Settings(this);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="reset()">
    /**
     * Сброс настроек
     * @return self ссылка
     */
    public LineBorderRender reset(){
        left(null, 0);
        top(null, 0);
        right(null, 0);
        bottom(null, 0);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="leftColor : Color">
    protected Color leftColor;
    /**
     * Указывает цвет левой линии рамки
     * @return цвет рамки
     */
    public Color getLeftColor() { return leftColor; }
    /**
     * Указывает цвет левой линии рамки
     * @param leftColor цвет рамки
     */
    public void setLeftColor(Color leftColor) { this.leftColor = leftColor; }
    /**
     * Указывает цвет левой линии рамки
     * @param color цвет рамки
     * @return self ссылки
     */
    public LineBorderRender leftColor(Color color){
        this.leftColor = color;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="leftStroke : Stroke">
    protected Stroke leftStroke;
    /**
     * Указывает контур левой границы
     * @return контур
     */
    public Stroke getLeftStroke() { return leftStroke; }
    /**
     * Указывает контур левой границы
     * @param leftStroke контур
     */
    public void setLeftStroke(Stroke leftStroke) { this.leftStroke = leftStroke; }
    /**
     * Указывает контур левой границы
     * @param stroke контур
     * @return self ссылка
     */
    public LineBorderRender leftStroke(Stroke stroke){
        this.leftStroke = stroke;
        return this;
    }
    /**
     * Указывает контур левой границы
     * @param width ширина
     * @param dash узор
     * @return self ссылка
     */
    public LineBorderRender leftStroke(float width, float ... dash){
        this.leftStroke =
            dash!=null && dash.length>0 ?
                new BasicStroke(width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,10f,dash,0f):
                new BasicStroke(width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="left()">
    /**
     * Указывает контур левой границы
     * @param color цвет
     * @param width ширина
     * @param dash узор
     * @return self ссылка
     */
    public LineBorderRender left(Color color,float width,float ... dash){
        leftColor(color);
        leftStroke(width, dash);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rightColor : Color">
    protected Color rightColor;
    /**
     * Указывает цвет правой граници контура
     * @return цвет контура
     */
    public Color getRightColor() { return rightColor; }
    /**
     * Указывает цвет правой граници контура
     * @param rightColor цвет контура
     */
    public void setRightColor(Color rightColor) { this.rightColor = rightColor; }
    /**
     * Указывает цвет правой граници контура
     * @param color цвет контура
     * @return self ссылка
     */
    public LineBorderRender rightColor(Color color){
        this.rightColor = color;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rightStroke">
    protected Stroke rightStroke;
    /**
     * Указывает контур правой границы
     * @return контур
     */
    public Stroke getRightStroke() { return rightStroke; }
    /**
     * Указывает контур правой границы
     * @param stroke контур
     */
    public void setRightStroke(Stroke stroke) { this.rightStroke = stroke; }
    /**
     * Указывает контур правой границы
     * @param stroke контур
     * @return self ссылка
     */
    public LineBorderRender rightStroke(Stroke stroke){
        this.rightStroke = stroke;
        return this;
    }
    /**
     * Указывает контур правой границы
     * @param width ширина
     * @param dash узор
     * @return self ссылка
     */
    public LineBorderRender rightStroke(float width, float ... dash){
        this.rightStroke =
            dash!=null && dash.length>0 ?
                new BasicStroke(width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,10f,dash,0f):
                new BasicStroke(width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="right()">
    /**
     * Указывает контур правой границы
     * @param color цвет
     * @param width ширина
     * @param dash узор
     * @return self ссылка
     */
    public LineBorderRender right(Color color,float width,float ... dash){
        rightColor(color);
        rightStroke(width, dash);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="topColor : Color">
    protected Color topColor;
    /**
     * Указывает цвет контура верхней границы
     * @return цвет контура
     */
    public Color getTopColor() { return topColor; }
    /**
     * Указывает цвет контура верхней границы
     * @param topColor цвет контура
     */
    public void setTopColor(Color topColor) { this.topColor = topColor; }
    /**
     * Указывает цвет контура верхней границы
     * @param color цвет контура
     * @return self ссылка
     */
    public LineBorderRender topColor(Color color){
        this.topColor = color;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="topStroke : Stroke">
    protected Stroke topStroke;
    /**
     * Указывает контур верхней границы
     * @return контур
     */
    public Stroke getTopStroke() { return topStroke; }
    /**
     * Указывает контур верхней границы
     * @param stroke контур
     */
    public void setTopStroke(Stroke stroke) { this.topStroke = stroke; }
    /**
     * Указывает контур верхней границы
     * @param stroke контур
     * @return self ссылка
     */
    public LineBorderRender topStroke(Stroke stroke){
        this.topStroke = stroke;
        return this;
    }
    /**
     * Указывает контур верхней границы
     * @param width ширина
     * @param dash узор
     * @return self ссылка
     */
    public LineBorderRender topStroke(float width, float ... dash){
        this.topStroke =
            dash!=null && dash.length>0 ?
                new BasicStroke(width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,10f,dash,0f):
                new BasicStroke(width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="top()">
    /**
     * Указывает контур верхней границы
     * @param color цвет
     * @param width ширина
     * @param dash узор
     * @return self ссылка
     */
    public LineBorderRender top(Color color,float width,float ... dash){
        topColor(color);
        topStroke(width, dash);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bottomColor : Color">
    protected Color bottomColor;
    /**
     * Указывает цвет контура нижней границы
     * @return цвет контура
     */
    public Color getBottomColor() { return bottomColor; }
    /**
     * Указывает цвет контура нижней границы
     * @param bottomColor цвет контура
     */
    public void setBottomColor(Color bottomColor) { this.bottomColor = bottomColor; }
    /**
     * Указывает цвет контура нижней границы
     * @param color цвет контура
     * @return self ссылка
     */
    public LineBorderRender bottomColor(Color color){
        this.bottomColor = color;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bottomStroke : Stroke">
    protected Stroke bottomStroke;
    /**
     * Указывает контур нижней границы
     * @return контур
     */
    public Stroke getBottomStroke() { return bottomStroke; }
    /**
     * Указывает контур нижней границы
     * @param stroke контур
     */
    public void setBottomStroke(Stroke stroke) { this.bottomStroke = stroke; }
    /**
     * Указывает контур нижней границы
     * @param stroke контур
     * @return self ссылка
     */
    public LineBorderRender bottomStroke(Stroke stroke){
        this.bottomStroke = stroke;
        return this;
    }
    /**
     * Указывает контур нижней границы
     * @param width ширина
     * @param dash узор
     * @return self ссылка
     */
    public LineBorderRender bottomStroke(float width, float ... dash){
        this.bottomStroke =
            dash!=null && dash.length>0 ?
                new BasicStroke(width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,10f,dash,0f):
                new BasicStroke(width,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bottom()">
    /**
     * Указывает контур нижней границы
     * @param color цвет
     * @param width ширина
     * @param dash узор
     * @return self ссылка
     */
    public LineBorderRender bottom(Color color,float width,float ... dash){
        bottomColor(color);
        bottomStroke(width, dash);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="inside : double">
    protected double inside = 1;
    /**
     * Указывает центральное смещение (-1 .. 1) во внутрь контура
     * @return смещение -1 - наружу .. 0 - по центру .. 1 во внутрь
     */
    public double getInside() { return inside; }
    /**
     * Указывает центральное смещение (-1 .. 1) во внутрь контура
     * @param inside смещение -1 - наружу .. 0 - по центру .. 1 во внутрь
     */
    public void setInside(double inside) { this.inside = inside; }
    /**
     * Указывает центральное смещение (-1 .. 1) во внутрь контура
     * @param inside смещение -1 - наружу .. 0 - по центру .. 1 во внутрь
     * @return self ссылка
     */
    public LineBorderRender inside(double inside){
        this.inside = inside;
        return this;
    }
    //</editor-fold>

    @Override
    public LineBorderRender clone(){
        return new LineBorderRender(this);
    }

    @Override
    public Rectangle2D cellRectangle(Graphics2D gs, CellContext context) {
        if( gs==null || context==null )return null;
        return null;
    }

    @Override
    public void cellRender(Graphics2D gs, CellContext context) {
        if( gs==null || context==null )return;

        Rectangle2D rect = context.getBounds();
        if( rect==null )return;

        AffineTransform saveTransfrm = (AffineTransform)gs.getTransform().clone();
        Stroke saveStroke = gs.getStroke();
        Paint savePaint = gs.getPaint();

        if( topColor!=null && topStroke!=null ){
            double off = 0;
            if( topStroke instanceof BasicStroke ){
                off = (((BasicStroke)topStroke).getLineWidth() / 2.0) * inside;
            }

            Path2D path = new Path2D.Double();
            path.moveTo(rect.getMinX(), rect.getMinY()+off);
            path.lineTo(rect.getMaxX(), rect.getMinY()+off);
            gs.setPaint(topColor);
            gs.setStroke(topStroke);
            gs.draw(path);
        }

        if( rightColor!=null && rightStroke!=null ){
            double off = 0;
            if( rightStroke instanceof BasicStroke ){
                off = ((BasicStroke)rightStroke).getLineWidth() / 2.0;
            }

            Path2D path = new Path2D.Double();
            path.moveTo(rect.getMaxX()-off, rect.getMinY());
            path.lineTo(rect.getMaxX()-off, rect.getMaxY());
            gs.setPaint(rightColor);
            gs.setStroke(rightStroke);
            gs.draw(path);
        }

        if( bottomColor!=null && bottomStroke!=null ){
            double off = 0;
            if( bottomStroke instanceof BasicStroke ){
                off = ((BasicStroke)bottomStroke).getLineWidth() / 2.0;
            }

            Path2D path = new Path2D.Double();
            path.moveTo(rect.getMaxX(), rect.getMaxY()-off);
            path.lineTo(rect.getMinX(), rect.getMaxY()-off);
            gs.setPaint(bottomColor);
            gs.setStroke(bottomStroke);
            gs.draw(path);
        }

        if( leftColor!=null && leftStroke!=null ){
            double off = 0;
            if( leftStroke instanceof BasicStroke ){
                off = ((BasicStroke)leftStroke).getLineWidth() / 2.0;
            }

            Path2D path = new Path2D.Double();
            path.moveTo(rect.getMinX()+off, rect.getMaxY());
            path.lineTo(rect.getMinX()+off, rect.getMinY());
            gs.setPaint(leftColor);
            gs.setStroke(leftStroke);
            gs.draw(path);
        }

        gs.setTransform((AffineTransform)saveTransfrm.clone());
        gs.setPaint(savePaint);
        gs.setStroke(saveStroke);
    }
}
