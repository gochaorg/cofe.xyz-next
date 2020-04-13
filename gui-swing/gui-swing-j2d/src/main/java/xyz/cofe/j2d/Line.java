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
 * Линия - отрезок
 * @author user
 */
public class Line {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(Line.class.getName());
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
        logger.entering(Line.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(Line.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(Line.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * @param begin Начальная точка
     * @param dEnd Конечная точка
     */
    public Line(Point2D begin, Point2D dEnd)
    {
        if(begin==null)throw new IllegalArgumentException("begin==null");
        if(dEnd==null)throw new IllegalArgumentException("dEnd==null");

        this.Begin = begin;
        this.End = dEnd;
    }

    private Point2D Begin;
    /**
     * Начальная точка
     * @return Начальная точка
     */
    public Point2D getBegin() { return Begin; }

    private Point2D End;
    /**
     *  Конечная точка
     * @return Конечная точка
     */
    public Point2D getEnd(){return End; }

    /**
     * Вычисление поподаия точки в линию
     * @param d Точка
     * @param r Величина погрешности (&gt;=0)
     * @return True - Есть попаданиеs
     */
    public boolean hitTest(Point2D d, double r)
    {
        if(d==null)
            throw new IllegalArgumentException("d==null");

        if (r < 0)
            throw new IllegalArgumentException("r<0");

        double minx = End.getX() < Begin.getX() ? End.getX() : Begin.getX();
        double miny = End.getY() < Begin.getY() ? End.getY() : Begin.getY();
        double maxx = End.getX() > Begin.getX() ? End.getX() : Begin.getX();
        double maxy = End.getY() > Begin.getY() ? End.getY() : Begin.getY();

        minx -= r;
        miny -= r;
        maxx += r;
        maxy += r;

        if (d.getX() < minx || d.getX() > maxx || d.getY() < miny || d.getY() > maxy)
        {
            return false;
        }

        LineFactor lf = new LineFactor(Begin.getX(), Begin.getY(), End.getX(), End.getY());
        double _r = lf.distance(d.getX(), d.getY());
        _r = _r<0 ? -_r : _r;
        if (_r <= r)
        {
            return true;
        }

        return false;
    }

    /**
     * Коэфицент прямой
     * @return Коэфициены прямой
     */
    public LineFactor getLineFactor()
    {
        return new LineFactor(this);
    }

    /**
     *  Возвращает точку пересечения отрезков
     *  @param line Отрезок
     *  @return Точка или null если нет пересечения, или отрезки паралельны
     */
    public Point2D intersection(Line line)
    {
        try
        {
            Point2D cross = this.getLineFactor().intersection(line.getLineFactor());

            boolean dotOnThisLine = false;
            boolean dotOnArgmLine = false;

            // Проверка пренадлежности точки отрезку
            double minx = 0, miny = 0, maxx = 0, maxy = 0;

            minx = this.Begin.getX() > this.End.getX() ? this.End.getX() : this.Begin.getX();
            miny = this.Begin.getY() > this.End.getY() ? this.End.getY() : this.Begin.getY();

            maxx = this.Begin.getX() > this.End.getX() ? this.Begin.getX() : this.End.getX();
            maxy = this.Begin.getY() > this.End.getY() ? this.Begin.getY() : this.End.getY();

            dotOnThisLine =
                cross.getX() >= minx && cross.getX() <= maxx &&
                    cross.getY() >= miny && cross.getY() <= maxy;

            minx = line.Begin.getX() > line.End.getX() ? line.End.getX() : line.Begin.getX();
            miny = line.Begin.getY() > line.End.getY() ? line.End.getY() : line.Begin.getY();

            maxx = line.Begin.getX() > line.End.getX() ? line.Begin.getX() : line.End.getX();
            maxy = line.Begin.getY() > line.End.getY() ? line.Begin.getY() : line.End.getY();

            dotOnArgmLine =
                cross.getX() >= minx && cross.getX() <= maxx &&
                    cross.getY() >= miny && cross.getY() <= maxy;

            if (dotOnArgmLine && dotOnThisLine)
                return cross;

            return null;
        }
        catch (MathException ex)
        {
            return null;
        }
    }
}
