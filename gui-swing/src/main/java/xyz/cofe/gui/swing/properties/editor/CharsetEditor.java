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

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import xyz.cofe.gui.swing.properties.PropertyDB;
import xyz.cofe.gui.swing.properties.PropertyDBService;

/**
 * Редактор для выбора кодировки
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class CharsetEditor extends ComboBoxEditor implements PropertyDBService
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(CharsetEditor.class.getName());
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
        logger.entering(CharsetEditor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(CharsetEditor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(CharsetEditor.class.getName(), method, result);
    }
    //</editor-fold>

    public CharsetEditor() {
        //super(false);
        String[] csarr = Charset.availableCharsets().keySet().toArray(new String[]{});
        setVartiants(csarr);
    }

    @Override
    public void register(PropertyDB pdb) {
        if( pdb==null )return;
        pdb.registerTypeEditor(Charset.class, this, 1.0);
    }

    protected boolean nullAsDefault = true;
    protected boolean emptyAsDefault = true;

    @Override
    public Object getValue() {
        Object val = super.getValue();
        if( val == null )return nullAsDefault ? Charset.defaultCharset() : null;

        String str = val.toString();
        if( str.trim().length()<1 )return emptyAsDefault ? Charset.defaultCharset() : null;

        try{
            Charset cs = Charset.forName(str);
            return cs;
        }catch( IllegalCharsetNameException | UnsupportedCharsetException ex ){
            logException(ex);
            JOptionPane.showMessageDialog(
                null, ex.getLocalizedMessage(), "Charset exception", JOptionPane.ERROR_MESSAGE);
            return Charset.defaultCharset();
        }
    }

    @Override
    public void setValue(Object value) {
        if( value instanceof Charset ){
            super.setValue( ((Charset)value).name() );
            return;
        }

        super.setValue( Charset.defaultCharset().name() );
    }

    @Override
    public void startEditing(Object value, Object context) {
        String[] csarr = Charset.availableCharsets().keySet().toArray(new String[]{});
        setVartiants(csarr);

        JComboBox cb = getComboBox();
        if( cb!=null ){
            cb.setSelectedItem( value!=null ? value.toString() : Charset.defaultCharset().toString() );
        }
    }
}
