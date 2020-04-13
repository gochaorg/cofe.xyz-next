/*
 * The MIT License
 *
 * Copyright 2016 Strelok.
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
package xyz.cofe.gui.swing.border;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Обычный линейный бордюр
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class LineBorder implements javax.swing.border.Border
{
    //<editor-fold defaultstate="collapsed" desc="top">
    //<editor-fold defaultstate="collapsed" desc="topWidth">
    protected double topWidth = 0;

    /**
     * Возвращает толщину верхней линии
     * @return толщина
     */
    public double getTopWidth() {
        return topWidth;
    }

    /**
     * Указывает толщину верхней линии
     * @param topWidth толщина
     */
    public void setTopWidth(double topWidth) {
        this.topWidth = topWidth;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="topMargin">
    protected double topMargin = 0;

    /**
     * Возвращает внешний отступ от верхнего края
     * @return отступ
     */
    public double getTopMargin() {
        return topMargin;
    }

    /**
     * Указывает отступ от верхнего края
     * @param topMargin отступ
     */
    public void setTopMargin(double topMargin) {
        this.topMargin = topMargin;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="topPadding">
    protected double topPadding = 0;

    /**
     * Возвращает внутренний от верхнего края
     * @return отступ
     */
    public double getTopPadding() {
        return topPadding;
    }

    /**
     * Указывает внутренний от верхнего края
     * @param topPadding отступ
     */
    public void setTopPadding(double topPadding) {
        this.topPadding = topPadding;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="topColor">
    protected Color topColor = Color.black;

    /**
     * Возвращает цвет верхней линии
     * @return цвет верхней линии
     */
    public Color getTopColor() {
        return topColor;
    }

    /**
     * Указывает цвет верхней линии
     * @param topColor цвет верхней линии
     */
    public void setTopColor(Color topColor) {
        this.topColor = topColor;
    }
    //</editor-fold>

    /**
     * Указывает верхнюю линию
     * @param width толщину верхней линии
     * @param padding внутренний отступ от верхнего края
     * @param margin отступ от верхнего края
     * @param color цвет верхней линии
     * @return self ссылка
     */
    public LineBorder top( int width, int padding, int margin, Color color ){
        setTopWidth(width);
        setTopPadding(padding);
        setTopMargin(margin);
        setTopColor(color);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bottom">
    //<editor-fold defaultstate="collapsed" desc="bottomWidth">
    protected double bottomWidth = 0;

    /**
     * Возвращает толщину нижней линии
     * @return толщину нижней линии
     */
    public double getBottomWidth() {
        return bottomWidth;
    }

    /**
     * Указывает толщину нижней линии
     * @param bottomWidth толщина
     */
    public void setBottomWidth(double bottomWidth) {
        this.bottomWidth = bottomWidth;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bottomMargin">
    protected double bottomMargin = 0;

    /**
     * Возвращает внешний отступ от нижнего края
     * @return отступ
     */
    public double getBottomMargin() {
        return bottomMargin;
    }

    /**
     * Указывает внешний отступ от нижнего края
     * @param bottomMargin отступ
     */
    public void setBottomMargin(double bottomMargin) {
        this.bottomMargin = bottomMargin;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bottomPadding">
    protected double bottomPadding = 0;

    /**
     * Возвращает внутренний отступ от нижнего края
     * @return отступ
     */
    public double getBottomPadding() {
        return bottomPadding;
    }

    /**
     * Указывает внутренний отступ от нижнего края
     * @param bottomPadding отступ
     */
    public void setBottomPadding(double bottomPadding) {
        this.bottomPadding = bottomPadding;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bottomColor">
    protected Color bottomColor = Color.black;

    /**
     * Возвращает цвет нижней линии
     * @return цвет
     */
    public Color getBottomColor() {
        return bottomColor;
    }

    /**
     * Указывает цвет нижней линии
     * @param bottomColor цвет
     */
    public void setBottomColor(Color bottomColor) {
        this.bottomColor = bottomColor;
    }
    //</editor-fold>

    /**
     * Указывает нижниюю линию
     * @param width толщина линии
     * @param padding внутренний отступ от нижнего края
     * @param margin вшнешний отступ от нижнего края
     * @param color цвет нижней линии
     * @return self ссылка
     */
    public LineBorder bottom( int width, int padding, int margin, Color color ){
        setBottomWidth(width);
        setBottomPadding(padding);
        setBottomMargin(margin);
        setBottomColor(color);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="left">
    //<editor-fold defaultstate="collapsed" desc="leftWidth">
    protected double leftWidth = 0;

    /**
     * Указывает толщину левой линии
     * @return толщина
     */
    public double getLeftWidth() {
        return leftWidth;
    }

    /**
     * Указывает толщину левой линии
     * @param leftWidth толщина
     */
    public void setLeftWidth(double leftWidth) {
        this.leftWidth = leftWidth;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="leftMargin">
    protected double leftMargin = 0;

    /**
     * Возвращает внешний отступ от левого края
     * @return внешний отступ
     */
    public double getLeftMargin() {
        return leftMargin;
    }

    /**
     * Указывает внешний отступ от левого края
     * @param leftMargin внешний отступ
     */
    public void setLeftMargin(double leftMargin) {
        this.leftMargin = leftMargin;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="leftPadding">
    protected double leftPadding = 0;

    /**
     * Возвращает внутренний отступ от левого края
     * @return внутренний отступ
     */
    public double getLeftPadding() {
        return leftPadding;
    }

    /**
     * Указывает внутренний отступ от левого края
     * @param leftPadding внутренний отступ
     */
    public void setLeftPadding(double leftPadding) {
        this.leftPadding = leftPadding;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="leftColor">
    protected Color leftColor = Color.black;

    /**
     * Возвращает цвет левой линии
     * @return цвет
     */
    public Color getLeftColor() {
        return leftColor;
    }

    /**
     * Указывает цвет левой линии
     * @param leftColor цвет
     */
    public void setLeftColor(Color leftColor) {
        this.leftColor = leftColor;
    }
    //</editor-fold>

    /**
     * Указывает левуюю линию
     * @param width толщина
     * @param padding внутрениий отступ
     * @param margin внешний отступ
     * @param color цвет
     * @return self ссылка
     */
    public LineBorder left( int width, int padding, int margin, Color color ){
        setLeftWidth(width);
        setLeftPadding(padding);
        setLeftMargin(margin);
        setLeftColor(color);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="right">
    //<editor-fold defaultstate="collapsed" desc="rightWidth">
    protected double rightWidth = 0;

    public double getRightWidth() {
        return rightWidth;
    }

    public void setRightWidth(double rightWidth) {
        this.rightWidth = rightWidth;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rightMargin">
    protected double rightMargin = 0;

    /**
     * Возвращает внешний отступ правой линии от края
     * @return внешний отступ
     */
    public double getRightMargin() {
        return rightMargin;
    }

    /**
     * Указываеь внешний отступ правой линии от края
     * @param rightMargin внешний отступ
     */
    public void setRightMargin(double rightMargin) {
        this.rightMargin = rightMargin;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rightPadding">
    protected double rightPadding = 0;

    /**
     * Возвращает внутренний отступ правой линии от края
     * @return отступ
     */
    public double getRightPadding() {
        return rightPadding;
    }

    /**
     * Указывает внутренний отступ правой линии от края
     * @param rightPadding отступ
     */
    public void setRightPadding(double rightPadding) {
        this.rightPadding = rightPadding;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rightColor">
    protected Color rightColor = Color.black;

    /**
     * Возвращает цвет правой линии
     * @return цвет
     */
    public Color getRightColor() {
        return rightColor;
    }

    /**
     * Указыавет правую линию - цвет
     * @param rightColor цвет
     */
    public void setRightColor(Color rightColor) {
        this.rightColor = rightColor;
    }
    //</editor-fold>

    /**
     * Указыавет правую линию
     * @param width толщина
     * @param padding внутренний отступ
     * @param margin внешний отступ
     * @param color цвет
     * @return self ссылка
     */
    public LineBorder right( int width, int padding, int margin, Color color ){
        setRightWidth(width);
        setRightPadding(padding);
        setRightMargin(margin);
        setRightColor(color);
        return this;
    }
    //</editor-fold>

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if( !(g instanceof Graphics2D) )return;
        if( c==null )return;

        Graphics2D gs = (Graphics2D)g;

        // insets
        double t = (topMargin>=0 ? topMargin : 0) + (topPadding>=0 ? topPadding : 0) + (topWidth >= 0 ? topWidth : 0);
        double b = (bottomMargin>=0 ? bottomMargin : 0) + (bottomPadding>=0 ? bottomPadding : 0) + (bottomWidth >= 0 ? bottomWidth : 0);
        double l = (leftMargin>=0 ? leftMargin : 0) + (leftPadding>=0 ? leftPadding : 0) + (leftWidth >= 0 ? leftWidth : 0);
        double r = (rightMargin>=0 ? rightMargin : 0) + (rightPadding>=0 ? rightPadding : 0) + (rightWidth >= 0 ? rightWidth : 0);

        Line2D topLine = null;
        Line2D bottomLine = null;
        Line2D leftLine = null;
        Line2D rightLine = null;

        Point2D leftTopPnt  = new Point2D.Double(x+l/2.0, y+t/2.0);
        Point2D rightTopPnt = new Point2D.Double(x+width-r/2.0, y+t/2.0);
        Point2D leftBottomPnt  = new Point2D.Double(x+l/2.0, y+height-b/2.0);
        Point2D rightBottomPnt = new Point2D.Double(x+width-r/2.0, y+height-b/2.0);

        boolean topLineSet = topWidth>0 && topColor!=null;
        boolean bottomLineSet = bottomWidth>0 && bottomColor!=null;
        boolean leftLineSet = leftWidth>0 && leftColor!=null;
        boolean rightLineSet = rightWidth>0 && rightColor!=null;

        if( topLineSet )topLine = new Line2D.Double(leftTopPnt, rightTopPnt);
        if( bottomLineSet )bottomLine = new Line2D.Double(leftBottomPnt, rightBottomPnt);
        if( leftLineSet )leftLine = new Line2D.Double(leftTopPnt, leftBottomPnt);
        if( rightLineSet )rightLine = new Line2D.Double(rightTopPnt, rightBottomPnt);

        if( topLineSet ){
            gs.setStroke(new BasicStroke((float)topWidth));
            gs.setColor(topColor);
            gs.draw(topLine);
        }
        if( bottomLineSet ){
            gs.setStroke(new BasicStroke((float)bottomWidth));
            gs.setColor(bottomColor);
            gs.draw(bottomLine);
        }
        if( leftLineSet ){
            gs.setStroke(new BasicStroke((float)leftWidth));
            gs.setColor(leftColor);
            gs.draw(leftLine);
        }
        if( rightLineSet ){
            gs.setStroke(new BasicStroke((float)rightWidth));
            gs.setColor(rightColor);
            gs.draw(rightLine);
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        double t = (topMargin>=0 ? topMargin : 0) + (topPadding>=0 ? topPadding : 0) + (topWidth >= 0 ? topWidth : 0);
        double b = (bottomMargin>=0 ? bottomMargin : 0) + (bottomPadding>=0 ? bottomPadding : 0) + (bottomWidth >= 0 ? bottomWidth : 0);
        double l = (leftMargin>=0 ? leftMargin : 0) + (leftPadding>=0 ? leftPadding : 0) + (leftWidth >= 0 ? leftWidth : 0);
        double r = (rightMargin>=0 ? rightMargin : 0) + (rightPadding>=0 ? rightPadding : 0) + (rightWidth >= 0 ? rightWidth : 0);
        return new Insets((int)t, (int)l, (int)b, (int)r);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
