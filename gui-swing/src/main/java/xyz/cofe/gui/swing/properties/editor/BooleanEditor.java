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

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import xyz.cofe.gui.swing.properties.PropertyDB;
import xyz.cofe.gui.swing.properties.PropertyDBService;
import xyz.cofe.text.Text;

/**
 * Редактор для boolean значений
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class BooleanEditor extends CustomEditor implements PropertyDBService
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(BooleanEditor.class.getName());
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
        logger.entering(BooleanEditor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(BooleanEditor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(BooleanEditor.class.getName(), method, result);
    }
    //</editor-fold>

    public BooleanEditor(){
        prepareIcons();
        label = new JLabel();
    }

    public BooleanEditor(BooleanEditor sample){
        prepareIcons();
        label = new JLabel();
        if( sample!=null ){
            checkedIcon = sample.checkedIcon;
            unCheckedIcon = sample.unCheckedIcon;
        }
    }

    @Override
    public BooleanEditor clone() {
        return new BooleanEditor(this);
    }

    @Override
    public void register(PropertyDB pdb) {
        if( pdb!=null ){
            pdb.registerTypeEditor(boolean.class, this, 1.0);
        }
    }

    private ImageIcon checkedIcon = null;
    private ImageIcon unCheckedIcon = null;
    private JLabel label;

    private void prepareIcons(){
        URL checkIconUrl = BooleanEditor.class.getResource(
            "/xyz/cofe/gui/swing/properties/editors/checked-2.png");

        if( checkIconUrl!=null ){
            checkedIcon = new ImageIcon(checkIconUrl);
        }

        URL uncheckIconUrl = BooleanEditor.class.getResource(
            "/xyz/cofe/gui/swing/properties/editors/unchecked-2.png");

        if( uncheckIconUrl!=null ){
            unCheckedIcon = new ImageIcon(uncheckIconUrl);
        }
    }

    @Override
    protected JComponent createComponent() {
        return label;
    }

    protected Boolean value;

    @Override
    public void setValue(Object value) {
        if( value instanceof Boolean ){
            this.value = (Boolean)value;

            label.setText( ((Boolean)value) ? "true" : "false" );
            label.setIcon( ((Boolean)value) ? checkedIcon : unCheckedIcon );
        }else if( value==null ){
            this.value = null;

            label.setText("null");
            label.setIcon( unCheckedIcon );
        }
    }

    @Override
    public void startEditing(Object value, Object context) {
        setValue(value);

        if( value!=null && (value instanceof Boolean) ){
            boolean val = (boolean)((Boolean)value);
            setValue(!val);

            SwingUtilities.invokeLater( new Runnable() {
                @Override
                public void run() {
                    fireEditingStopped(this);
                }});
        }
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getJavaInitializationString() {
        if( value != null ){
            return value ? "true" : "false";
        }else{
            return "null";
        }
    }

    @Override
    public String getAsText() {
        if( value != null ){
            return value ? "true" : "false";
        }else{
            return "null";
        }
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if( text==null ){
            this.value = null;
        }else{
            if( text.equalsIgnoreCase("null") ){
                this.value = null;
            }else{
                this.value = Text.in(text.trim().toLowerCase(), "true", "1", "yes", "on" );
            }
        }
    }
}
