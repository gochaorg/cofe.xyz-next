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

package xyz.cofe.j2d;


import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Описывает полярные координаты
 * @author user
 */
public class Coord {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(Coord.class.getName());
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
        logger.entering(Coord.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(Coord.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(Coord.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор по умолчанию
     */
    public Coord()
    {
    }

    /**
     * Конструктор полярных координат
     * @param anlge Угол
     * @param length Расстояние
     */
    public Coord(double anlge,double length)
    {
        this.angle = anlge;
        this.length = length;
    }

    /**
     * Конструктор полярных координат из Декартовых
     * @param vector Декартовая координата
     */
    public Coord(Point2D vector)
    {
        if (vector == null) {
            throw new IllegalArgumentException("vector == null");
        }
        this.angle = angle(vector.getX(), vector.getY()); //vector.angle();
        this.length = distance(vector.getX(), vector.getY()); // vector.distance();
    }

    /**
     * Возвращает угол
     * @param x координата
     * @param y координата
     * @return угол
     */
    public static double angle(double x,double y){
        if( x==0 && y==0 )return 0;

        int p = 0;
        if( x>=0 && y>=0 )p = 0;
        if( x<0 && y>=0 )p = 1;
        if( x<0 && y<0 )p = 2;
        if( x>=0 && y<0 )p = 3;

        switch( p )
        {
            case 0:
                return x==0 ? Math.PI / 2 :  Math.atan( y / x );
            case 1:
                return Math.PI + Math.atan( y / x );
            case 2:
                return Math.PI + Math.atan( y / x );
            case 3:
                return x==0 ? Math.PI * 1.5 : Math.atan( y / x );
        }

        return 0;
    }

    /**
     * Возвращает расстояние от начала координат до точки
     * @param x координата
     * @param y координата
     * @return Расстояние
     */
    public static double distance(double x, double y)
    {
        return Math.sqrt(x*x + y*y);
    }

    private double angle = 0;
    private double length = 0;

    /**
     * Возвращает угол
     * @return Угол
     */
    public double getAngle()
    {
        return angle;
    }

    /**
     * Возвращает расстояние
     * @return Расстояние
     */
    public double getLength()
    {
        return length;
    }
}
