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

import java.awt.geom.Rectangle2D;

/**
 * Функции работы с прямоугольниками
 * @author user
 */
public class RectangleFn {
    /**
     * Уменьшает размер прямоугольника слева
     * @param rect прямоугольник
     * @param padding на сколько уменьшить
     * @return новый прямоугольник
     */
    public static Rectangle2D paddingLeft( Rectangle2D rect, double padding ){
        if( rect==null )throw new IllegalArgumentException("rect == null");
        if( padding==0 )return rect;
        if( padding>0 && padding>rect.getWidth() ){
            padding = rect.getWidth();
        }
        return new Rectangle2D.Double(
            rect.getMinX()+padding,
            rect.getMinY(),
            rect.getMaxX()-rect.getMinX()-padding,
            rect.getMaxY()-rect.getMinY());
    }

    /**
     * Уменьшает размер прямоугольника справа
     * @param rect прямоугольник
     * @param padding на сколько уменьшить
     * @return новый прямоугольник
     */
    public static Rectangle2D paddingRight( Rectangle2D rect, double padding ){
        if( rect==null )throw new IllegalArgumentException("rect == null");
        if( padding==0 )return rect;
        if( padding>0 && padding>rect.getWidth() ){
            padding = rect.getWidth();
        }
        return new Rectangle2D.Double(
            rect.getMinX(),
            rect.getMinY(),
            rect.getMaxX()-rect.getMinX()-padding,
            rect.getMaxY()-rect.getMinY());
    }

    /**
     * Уменьшает размер прямоугольника сверху
     * @param rect прямоугольник
     * @param padding на сколько уменьшить
     * @return новый прямоугольник
     */
    public static Rectangle2D paddingTop( Rectangle2D rect, double padding ){
        if( rect==null )throw new IllegalArgumentException("rect == null");
        if( padding==0 )return rect;
        if( padding>0 && padding>rect.getHeight() ) padding = rect.getHeight();
        return new Rectangle2D.Double(
            rect.getMinX(),
            rect.getMinY()+padding,
            rect.getWidth(),
            rect.getHeight()-padding);
    }

    /**
     * Уменьшает размер прямоугольника снизу
     * @param rect прямоугольник
     * @param padding на сколько уменьшить
     * @return новый прямоугольник
     */
    public static Rectangle2D paddingBottom( Rectangle2D rect, double padding ){
        if( rect==null )throw new IllegalArgumentException("rect == null");
        if( padding==0 )return rect;
        if( padding>0 && padding>rect.getHeight() ) padding = rect.getHeight();
        return new Rectangle2D.Double(
            rect.getMinX(),
            rect.getMinY(),
            rect.getWidth(),
            rect.getHeight()-padding);
    }

    /**
     * Перемещает прямоугольник
     * @param rect исходный прямоугольник
     * @param x перемещение по x
     * @param y перемещение по y
     * @return новый прямоугольник
     */
    public static Rectangle2D move( Rectangle2D rect, double x, double y){
        if( rect==null )throw new IllegalArgumentException("rect == null");
        if( x==0 && y==0 )return rect;
        return new Rectangle2D.Double(
            rect.getMinX()+x,
            rect.getMinY()+y,
            rect.getMaxX()-rect.getMinX(),
            rect.getMaxY()-rect.getMinY());
    }

    /**
     * Устанавливает размер прямоугольника
     * @param rect исходный прямоугольник
     * @param width ширина
     * @param height высота
     * @return новый прямоугольник
     */
    public static Rectangle2D size( Rectangle2D rect, double width, double height){
        if( rect==null )throw new IllegalArgumentException("rect == null");
        return new Rectangle2D.Double(
            rect.getMinX(),
            rect.getMinY(),
            width+rect.getMinX(),
            height+rect.getMinY());
    }

    /**
     * Обединяет прямоугольники создавая один прямоугольник, включающий указанные,
     * но пропускает прямоугольники нулевой толшины.
     * @param rects прямоугольники
     * @return прямоугольник или null
     */
    public static Rectangle2D union( Rectangle2D ... rects ){
        if( rects==null || rects.length==0 )return null;
        Double xmin = null;
        Double xmax = null;
        Double ymin = null;
        Double ymax = null;
        for( Rectangle2D r : rects ){
            if( r==null )continue;
            if( r.getWidth()<=0 )continue;
            if( r.getHeight()<=0 )continue;
            xmin = xmin==null ? r.getMinX() : Math.min(xmin, r.getMinX());
            xmax = xmax==null ? r.getMaxX() : Math.max(xmax, r.getMaxX());
            ymin = ymin==null ? r.getMinY() : Math.min(ymin, r.getMinY());
            ymax = ymax==null ? r.getMaxY() : Math.max(ymax, r.getMaxY());
        }
        if( xmin==null )return null;
        if( ymin==null )return null;
        if( xmax==null )return null;
        if( ymax==null )return null;
        return new Rectangle2D.Double(
            Math.min(xmin,xmax),
            Math.min(ymin,ymax),
            Math.max(xmin,xmax)-Math.min(xmin,xmax),
            Math.max(ymin,ymax)-Math.min(ymin,ymax)
        );
    }
}
