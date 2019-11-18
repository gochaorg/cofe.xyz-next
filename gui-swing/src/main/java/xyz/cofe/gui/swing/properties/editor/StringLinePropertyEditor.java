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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import xyz.cofe.gui.swing.SwingListener;
import xyz.cofe.gui.swing.properties.PropertyDB;
import xyz.cofe.gui.swing.properties.PropertyDBService;
import xyz.cofe.text.Text;

/**
 * редактор текст-а
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class StringLinePropertyEditor extends AbstractPropertyEditor implements PropertyDBService
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(StringLinePropertyEditor.class.getName());
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
        logger.entering(StringLinePropertyEditor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(StringLinePropertyEditor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(StringLinePropertyEditor.class.getName(), method, result);
    }
    //</editor-fold>

    protected JPanel panel;
    protected JTextField textField;
    protected JCheckBox nullCheckBox;

    public StringLinePropertyEditor(){
        panel = new JPanel(new BorderLayout());

        textField = new JTextField();
        textField.setBorder(new EmptyBorder(0, 0, 0, 0));

        nullCheckBox = new JCheckBox();
        nullCheckBox.setText("null");

        //Consumer cons = (e) -> { nullCheckBox.setSelected(false); };
        var cons = new Consumer<KeyEvent>() {
            @Override
            public void accept(KeyEvent obj)
            { nullCheckBox.setSelected(false); }};

        SwingListener.onKeyPressed(textField, cons);
        SwingListener.onKeyTyped(textField, cons);

        panel.add(textField, BorderLayout.CENTER);
        panel.add(nullCheckBox, BorderLayout.EAST);
    }

    public StringLinePropertyEditor(StringLinePropertyEditor sample){
        panel = new JPanel(new BorderLayout());

        textField = new JTextField();
        textField.setBorder(new EmptyBorder(0, 0, 0, 0));

        nullCheckBox = new JCheckBox();
        nullCheckBox.setText("null");

        //Consumer cons = (e) -> { nullCheckBox.setSelected(false); };

        Consumer<KeyEvent> cons = new Consumer<KeyEvent>() {
            @Override
            public void accept(KeyEvent obj)
            { nullCheckBox.setSelected(false); }};

        SwingListener.onKeyPressed(textField, cons);
        SwingListener.onKeyTyped(textField, cons);

        panel.add(textField, BorderLayout.CENTER);
        panel.add(nullCheckBox, BorderLayout.EAST);
    }

    @Override
    public StringLinePropertyEditor clone() {
        return new StringLinePropertyEditor(this);
    }

    @Override
    public void register(PropertyDB pdb) {
        if( pdb==null )return;
        pdb.registerTypeEditor(String.class, this, 0.5);
    }

    @Override
    public void setValue(Object value) {
        if( value==null ){
            textField.setText("");
            nullCheckBox.setSelected(true);
        }else{
            textField.setText(value.toString());
            nullCheckBox.setSelected(false);
        }
    }

    @Override
    public Object getValue() {
        if( nullCheckBox.isSelected() ){
            return null;
        }else{
            return getAsText();
        }
    }

    @Override
    public boolean isPaintable() {
        return false;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getJavaInitializationString() {
        if( nullCheckBox.isSelected() ){
            return "null";
        }
        return Text.encodeStringConstant( getAsText() );
    }

    @Override
    public String getAsText() { return textField.getText(); }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if( text==null )throw new IllegalArgumentException("text == null");
        textField.setText(text);
    }

    @Override
    public Component getCustomEditor() {
        return panel;
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }
}
