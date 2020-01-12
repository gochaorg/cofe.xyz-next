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
 *
 * @author user
 */
public class LineFactor {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(LineFactor.class.getName());
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
        logger.entering(LineFactor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(LineFactor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(LineFactor.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * @param a Коэфицент А
     * @param b Коэфицент B
     * @param c Коэфицент C
     */
    public LineFactor(double a, double b, double c)
    {
        this.A = a;
        this.B = b;
        this.C = c;
    }

    /**
     * @param x1 Координата x первой точки
     * @param y1 Координата y первой точки
     * @param x2 Координата x второй точки
     * @param y2 Координата y второй точки
     */
    public LineFactor(double x1, double y1, double x2, double y2)
    {
        A = y1 - y2;
        B = x2 - x1;
        C = - A * x1 - B * y1;
    }

    /**
     * @param d1 Координаты первой точки
     * @param d2 Координаты второй точки
     */
    public LineFactor(Point2D d1, Point2D d2)
    {
        this(d1.getX(), d1.getY(), d2.getX(), d2.getY());
    }

    /**
     * @param line Линия
     */
    public LineFactor(Line line)
    {
        //if( line==null )throw new IllegalAccessException("line==null");
        this(line.getBegin(),line.getEnd());
    }

    private double A;
    /**
     * Коэфицент А, если A = 0 - то прямая горизонтальна
     * @return значение коэфициента
     */
    public double getA(){ return A; }

    private double B;
    /**
     * Коэфицент B, если B = 0 - то прямая вертикальна
     * @return значение коэфициента
     */
    public double getB(){ return B; }

    private double C;
    /**
     * Коэфицент C
     * @return значение коэфициента
     */
    public double getC(){ return C; }

    /**
     * Возвращает координату x прямой по известной координаты y
     * @param y Известная координата y
     * @return Искомая координата x
     * @throws MathException Если не можеть быть вычесленно, если известная координата не лежит на прямой
     */
    public double getX(double y)
    {
        if (A == 0)
        {
            throw new MathException("Известная координата не лежит на прямой");
        }
        return -(B * y + C) / A;
    }

    /**
     * Возвращает координату y прямой по известной координаты x
     * @param x Известная координата x
     * @return Искомая координата y
     * @throws MathException Если не можеть быть вычесленно, если известная координата не лежит на прямой
     */
    public double getY(double x)
    {
        if (B == 0)
        {
            throw new MathException("Известная координата не лежит на прямой");
        }
        return -(A * x + C) / B;
    }

    /**
     * Создает нормаль к прямой через указанную точку
     * @param x Координата x
     * @param y Координата y
     * @return Нормаль
     */
    public LineFactor makeNormal(double x, double y)
    {
        double x2 = x + A;
        double y2 = y + B;

        return new LineFactor(x, y, x2, y2);
    }

    /**
     * Создает нормаль к прямой через указанную точку
     * @param dot Точка
     * @return Нормаль
     */
    public LineFactor makeNormal(Point2D dot)
    {
        return LineFactor.this.makeNormal(dot.getX(), dot.getY());
    }

    /**
     * Проверяет паралельность прямой
     * @param line Прямая
     * @return Паралельна
     */
    public boolean IsParallel(LineFactor line)
    {
        double f1 = B == 0 ? 0 : A / B;
        double f2 = line.B == 0 ? 0 : line.A / line.B;

        return f1 == f2;
    }

    /**
     * Вычисляет пересение прямой
     * @param line Прямая
     * @return Точка пересечения
     */
    public Point2D intersection(LineFactor line)
    {
        if (IsParallel(line))
        {
            throw new MathException("Прямые паралельны");
        }

        double a1 = A;
        double b1 = B;
        double c1 = C;
        double a2 = line.A;
        double b2 = line.B;
        double c2 = line.C;

        double x = (b1 * c2 - b2 * c1) / (a1 * b2 - a2 * b1);
        double y = (c1 * a2 - c2 * a1) / (a1 * b2 - a2 * b1);

        return new Point2D.Double(x, y);
    }

    /**
     * Возвращает растояние между точкой и прямой
     * @param x Координата x
     * @param y Координата y
     * @return Растояние
     */
    public double distance(double x, double y)
    {
        double v = value(x, y);
        v = v < 0 ? -v : v;
        return v / Math.sqrt(A * A + B * B);
    }

    /**
     * Вычисляет значение точки (Ax+By+C) относительно уровнения приямой
     * @param x Координата x
     * @param y Координата y
     * @return Значение
     */
    public double value(double x, double y)
    {
        return A * x + B * y + C;
    }

    /**
     * Получение точки на линии, через приблеженную точку
     * @param d Приближение точка
     * @return Точка на линии
     * Если не возможно
     */
    public Point2D crossDot(Point2D d)
    {
        LineFactor normal = makeNormal(d);
        return intersection(normal);
    }
}
