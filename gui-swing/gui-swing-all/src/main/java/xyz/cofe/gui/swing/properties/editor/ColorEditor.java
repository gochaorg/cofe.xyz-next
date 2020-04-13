/*
 * The MIT License
 *
 * Copyright 2017 Kamnev Georgiy <nt.gocha@gmail.com>.
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import xyz.cofe.gui.swing.SwingListener;
import xyz.cofe.gui.swing.border.LineBorder;
import xyz.cofe.gui.swing.properties.PropertyDB;
import xyz.cofe.gui.swing.properties.PropertyDBService;
import xyz.cofe.gui.swing.typeconv.impl.RGB;

/**
 * Редактор цвета
 * @author nt.gocha@gmail.com
 */
public class ColorEditor extends CustomEditor implements PropertyDBService
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ColorEditor.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }
    private static boolean isLogSevere(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.SEVERE.intValue();
    }
    private static boolean isLogWarning(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.WARNING.intValue();
    }
    private static boolean isLogInfo(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.INFO.intValue();
    }
    private static boolean isLogFine(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.FINE.intValue();
    }
    private static boolean isLogFiner(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINER.intValue();
    }
    private static boolean isLogFinest(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINEST.intValue();
    }

    private static void logEntering(String method,Object ... args){
        logger.entering(ColorEditor.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(ColorEditor.class.getName(), method, result);
    }

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
    //</editor-fold>

    @Override
    public void register(PropertyDB pdb) {
        if( pdb==null )return;
        pdb.registerTypeEditor(Color.class, this);
    }

    public ColorEditor(){
        buildUI();
    }

    public ColorEditor(ColorEditor sample){
        super(sample);
        buildUI();
    }

    protected void buildUI(){
        EmptyBorder ebrd = new EmptyBorder(0, 0, 0, 0);
        panel.setBorder(ebrd);
        panel.setLayout(new BorderLayout());

        panel.add( nameColor, BorderLayout.CENTER );

        //panel.add( selectColor, BorderLayout.EAST );

        JPanel buts = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buts.add( selectColor );
        buts.add(selectNull );
        buts.setBorder(ebrd);

        int lbrdW = 1;
        int lbrdP = 1;
        int lbrdM = 1;
        Color lbrdC = Color.black;
        LineBorder lbrd = new LineBorder()
            .bottom(lbrdW, 0, 2, lbrdC)
            .top(lbrdW, 0, 0, lbrdC)
            .right(lbrdW, lbrdP, lbrdM, lbrdC)
            .left(lbrdW, 1, 3, lbrdC);

        selectColor.setBorder(lbrd);
        selectNull.setBorder(lbrd);

        selectNull.setBackground(Color.gray);
        selectNull.setForeground(Color.white);
        selectNull.setOpaque(true);
        selectNull.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        selectColor.setBackground(Color.gray);
        selectColor.setForeground(Color.white);
        selectColor.setOpaque(true);
        selectColor.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        panel.add( buts, BorderLayout.EAST );

        selectColor.setText("..");
        SwingListener.onMouseClicked(selectColor, ae -> {
            //JColorChooser colorChooser = new JColorChooser(color == null ? Color.black : color);
            color = JColorChooser.showDialog(panel, "Select color", color == null ? Color.black : color);
            nameColor.setText(RGB.rgb(color));

            fireEditingStopped(this);
        });

        SwingListener.onMouseClicked(selectNull, ev -> {
            setValue(null);
            fireEditingStopped(this);
        });

        SwingListener.onMouseEntered(selectColor, ev -> {
            selectColor.setBackground(Color.black);
        });

        SwingListener.onMouseExited(selectColor,ev -> {
                selectColor.setBackground(Color.gray);
        });

        SwingListener.onMouseEntered(selectNull, ev -> {
                selectNull.setBackground(Color.black);
        });
        SwingListener.onMouseExited(selectNull, ev -> {
                selectNull.setBackground(Color.gray);
        });
    }

    @Override
    public ColorEditor clone() {
        return new ColorEditor(this);
    }

    protected final JPanel panel = new JPanel();
    protected final JLabel selectColor = new JLabel();
    protected final JLabel nameColor = new JLabel();
    protected final JLabel selectNull = new JLabel("null");

    protected Color color;

    @Override
    protected JComponent createComponent() {
        return panel;
    }

    @Override
    public void setValue(Object value) {
        if( value instanceof Color ){
            color = ((Color)value);
        }else{
            color = null;
        }

        nameColor.setText(color!=null ? RGB.rgb(color) : "null");
    }

    @Override
    public Object getValue() {
        //if( color==null ){
        //    return Color.black;
        //}
        return color;
    }

    @Override
    public String getJavaInitializationString() {
        return RGB.rgb(color==null ? color.black : color);
    }

    @Override
    public String getAsText() {
        return color==null ? "null" : RGB.rgb(color);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if( text==null || text.trim().equalsIgnoreCase("null") ){
            color = null;
        }else{
            color = RGB.rgb(text);
        }
    }
}
