/*
 * The MIT License
 *
 * Copyright 2017 user.
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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Орисовка списка рендеров
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class StackRender
    extends ArrayList<CellRender>
    implements CellRender
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(StackRender.class.getName());
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
        logger.entering(StackRender.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(StackRender.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(StackRender.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор по умолчанию
     */
    public StackRender(){
    }

    /**
     * Конструктор копирования
     * @param sample образец копирования
     */
    public StackRender(StackRender sample){
        if( sample!=null ){
            for( CellRender cr : sample ){
                add( cr!=null ? cr.clone() : null );
            }
        }
    }

    /**
     * Клонирование
     * @return клон
     */
    @Override
    public StackRender clone(){
        return new StackRender(this);
    }

    /**
     * Добавление рендер в конец списка
     * @param cr рендер
     * @return self ссылка
     */
    public StackRender append( CellRender cr ){
        if( cr==null )throw new IllegalArgumentException("cr==null");
        add(cr);
        return this;
    }

    @Override
    public Rectangle2D cellRectangle(Graphics2D gs, CellContext context) {
        Rectangle2D rect = null;
        for( CellRender cr : this ){
            if( cr==null )continue;
            Rectangle2D r = cr.cellRectangle(gs, context);

            // Расширение области - способом наложения поверх
            if( rect==null ){
                if( r!=null )rect = r;
            }else{
                if( r!=null ){
                    double x1 = Math.min(rect.getMinX(), r.getMinX());
                    double y1 = Math.min(rect.getMinY(), r.getMinY());
                    double x2 = Math.max(rect.getMaxX(), r.getMaxX());
                    double y2 = Math.max(rect.getMaxY(), r.getMaxY());
                    rect = new Rectangle(
                        (int)Math.min(x1,x2),
                        (int)Math.min(y1,y2),
                        (int)Math.abs(x1-x2),
                        (int)Math.abs(y1-y2)
                    );
                }
            }
        }
        return rect;
    }

    @Override
    public void cellRender(Graphics2D gs, CellContext context) {
        //Rectangle2D rect = null;
        for( CellRender cr : this ){
            if( cr==null )continue;
            cr.cellRender(gs, context);
        }
    }
}
