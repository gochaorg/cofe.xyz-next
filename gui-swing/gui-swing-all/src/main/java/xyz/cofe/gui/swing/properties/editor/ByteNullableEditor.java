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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import xyz.cofe.gui.swing.properties.PropertyDB;
import xyz.cofe.gui.swing.properties.PropertyDBService;
import xyz.cofe.gui.swing.text.ValidatedTextField;

/**
 * Редактор Byte значений
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class ByteNullableEditor extends TextFieldEditor implements PropertyDBService
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ByteNullableEditor.class.getName());
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
        logger.entering(ByteNullableEditor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(ByteNullableEditor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(ByteNullableEditor.class.getName(), method, result);
    }
    //</editor-fold>

    public ByteNullableEditor() {
        super(true);
    }

    @Override
    public void register(PropertyDB pdb) {
        if( pdb==null )return;
        pdb.registerTypeEditor(Byte.class, this, 1.0);
    }

    @Override
    protected JTextComponent getTextField() {
        if( textField!=null )return textField;

        final ValidatedTextField tf = new ValidatedTextField();
        tf.setFilter((String str) -> {
            try{
                if( isAllowNull() && (str==null || str.length()==0) )return true;

                Byte.parseByte(str);
                return true;
            }catch(NumberFormatException ex){
                tf.setBalloonText("format exception: "+ex.getLocalizedMessage());
            }
            return false;
        });
        tf.setPlaceholder("Enter number (Byte)");
        tf.setBalloonText("");
        tf.setBorder(new EmptyBorder(0, 0, 0, 0));

        textField = tf;
        return textField;
    }

    @Override
    protected Object getTextFieldValue() {
        String txt = getTextField().getText();

        try{
            Byte b = Byte.parseByte(txt);
            return b;
        }catch( NumberFormatException ex ){
            JOptionPane.showMessageDialog(
                null, ex.getLocalizedMessage(), "NumberFormatException", JOptionPane.ERROR_MESSAGE);
            return (Byte)((byte)0);
        }
    }

    @Override
    protected void setTextFieldValue(Object value) {
        if( value instanceof Byte ){
            super.setTextFieldValue(Byte.toString((Byte)value));
            return;
        }
        super.setTextFieldValue(value);
    }
}
