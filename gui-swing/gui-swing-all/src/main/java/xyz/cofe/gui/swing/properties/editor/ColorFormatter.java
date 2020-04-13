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

package xyz.cofe.gui.swing.properties.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import xyz.cofe.gui.swing.properties.PropertyDB;
import xyz.cofe.gui.swing.properties.PropertyDBService;
import xyz.cofe.gui.swing.tree.TreeTableNodeFormat;
import xyz.cofe.gui.swing.tree.TreeTableNodeFormatBasic;
import xyz.cofe.gui.swing.tree.TreeTableNodeGetFormatOf;
import xyz.cofe.gui.swing.typeconv.impl.RGB;

/**
 * Отображение иконки цвета
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class ColorFormatter
    extends AbstractPropertyEditor
    implements PropertyDBService, TreeTableNodeGetFormatOf
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ColorFormatter.class.getName());
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
        logger.entering(ColorFormatter.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(ColorFormatter.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(ColorFormatter.class.getName(), method, result);
    }
    //</editor-fold>

    public ColorFormatter(){
    }

    public ColorFormatter(ColorFormatter sample){
    }

    @Override
    public ColorFormatter clone() {
        return new ColorFormatter(this);
    }

    @Override
    public void register(PropertyDB pdb) {
        if( pdb==null )return;

        pdb.registerTypeEditor(Color.class, this, 0.5);
    }

    public static class ColorIcon implements Icon {
        public final int width;
        public final int height;
        public final Color color;
        public final Color border;

        public ColorIcon( Color color, Color border, int w, int h ){
            this.width = w;
            this.height = h;
            this.color = color;
            this.border = border;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D gs = (Graphics2D)g;
            if( width>0 && height>0 ){
                if( color!=null ){
                    gs.setPaint(color);
                    gs.fillRect(x, y, width, height);
                }

                if( border!=null ){
                    gs.setPaint(border);
                    gs.drawRect(x, y, width, height);
                }
            }
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }

    @Override
    public TreeTableNodeFormat getTreeTableNodeFormatOf(Object value) {
        if( value==null )return null;
        if( !(value instanceof Color) )return null;

        Color color = (Color)value;

        TreeTableNodeFormatBasic fmt = new TreeTableNodeFormatBasic();
        fmt.getIcons().add(new ColorIcon(color, Color.black, 12, 12));
        fmt.setIconWidthMin(18);
        fmt.setConvertor(from -> {
            if( from instanceof Color ){
                return RGB.rgb((Color)from);
            }
            return null;
        });

        return fmt;
    }

    protected Color value;

    @Override
    public void setValue(Object value) {
        if( value instanceof Color ){
            this.value = (Color)value;
        }else if( value==null ){
            this.value = null;
        }
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getJavaInitializationString() {
        return RGB.rgb(value==null ? Color.black : value);
    }

    @Override
    public String getAsText() {
        return value==null ? "null" : RGB.rgb(value);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        value = RGB.rgb(text==null ? "#000000" : text);
    }
}
