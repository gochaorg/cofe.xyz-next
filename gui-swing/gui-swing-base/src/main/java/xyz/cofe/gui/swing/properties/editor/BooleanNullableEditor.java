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

import java.awt.Component;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import xyz.cofe.gui.swing.SwingListener;
import xyz.cofe.gui.swing.properties.Icons;
import xyz.cofe.gui.swing.properties.PropertyDB;
import xyz.cofe.gui.swing.properties.PropertyDBService;
import xyz.cofe.text.Text;

/**
 * Редактор для Boolean значений
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class BooleanNullableEditor extends CustomEditor implements PropertyDBService
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(BooleanNullableEditor.class.getName());
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
        logger.entering(BooleanNullableEditor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(BooleanNullableEditor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(BooleanNullableEditor.class.getName(), method, result);
    }
    //</editor-fold>

    public BooleanNullableEditor(){
        prepareIcons();

        combo = new JComboBox(new Object[]{"true", "false", "null"});

        ListCellRenderer cr = new BasicComboBoxRenderer(){
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if( "true".equals(value) ){
                    setIcon(checkedIcon);
                }else if( "false".equals(value) ){
                    setIcon(unCheckedIcon);
                }else if( "null".equals(value) ){
                    setIcon(nullIcon);
                }else{
                    setIcon(null);
                }

                return this;
            }
        };

        combo.setRenderer(cr);

        listenComboBoxChanges();
    }

    public BooleanNullableEditor(BooleanNullableEditor sample){
        prepareIcons();

        combo = new JComboBox(new Object[]{"true", "false", "null"});

        ListCellRenderer cr = new BasicComboBoxRenderer(){
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if( "true".equals(value) ){
                    setIcon(checkedIcon);
                }else if( "false".equals(value) ){
                    setIcon(unCheckedIcon);
                }else if( "null".equals(value) ){
                    setIcon(nullIcon);
                }else{
                    setIcon(null);
                }

                return this;
            }
        };

        combo.setRenderer(cr);

        if( sample!=null ){
            checkedIcon = sample.checkedIcon;
            unCheckedIcon = sample.unCheckedIcon;
            nullIcon = sample.nullIcon;
        }

        listenComboBoxChanges();
    }

    protected void listenComboBoxChanges(){
        SwingListener.onActionPerformed(combo,e->fireEditingStopped(BooleanNullableEditor.this));
    }

    @Override
    public BooleanNullableEditor clone() {
        return new BooleanNullableEditor(this);
    }

    @Override
    public void register(PropertyDB pdb) {
        if( pdb!=null ){
            pdb.registerTypeEditor(Boolean.class, this, 1.0);
        }
    }

    private ImageIcon checkedIcon = null;
    private ImageIcon unCheckedIcon = null;
    private Icon nullIcon = null;
    private JComboBox combo = null;

    private void prepareIcons(){
        URL checkIconUrl = BooleanNullableEditor.class.getResource(
            "/xyz/cofe/gui/swing/properties/editor/checked-2.png");

        if( checkIconUrl!=null ){
            checkedIcon = new ImageIcon(checkIconUrl);
        }

        URL uncheckIconUrl = BooleanNullableEditor.class.getResource(
            "/xyz/cofe/gui/swing/properties/editor/unchecked-2.png");

        if( uncheckIconUrl!=null ){
            unCheckedIcon = new ImageIcon(uncheckIconUrl);
        }

        nullIcon = Icons.getNullIcon();
    }

    @Override
    protected JComponent createComponent() {
        return combo;
    }

    protected Boolean value;

    @Override
    public void setValue(Object value) {
        if( value instanceof Boolean ){
            this.value = (Boolean)value;

            Boolean v = (Boolean)value;
            combo.setSelectedIndex( v ? 0 : 1 );
        }else if( value==null ){
            this.value = null;

            combo.setSelectedIndex(2);
        }
    }

    @Override
    public void startEditing(Object value, Object context) {
        setValue(value);
    }

    public Boolean getBooleanValue(){
        int sel = combo.getSelectedIndex();
        if( sel==0 )return Boolean.TRUE;
        if( sel==1 )return Boolean.FALSE;
        return null;
    }

    @Override
    public Object getValue() {
        return getBooleanValue();
    }

    @Override
    public String getJavaInitializationString() {
        if( getBooleanValue() != null ){
            return getBooleanValue() ? "true" : "false";
        }else{
            return "null";
        }
    }

    @Override
    public String getAsText() {
        if( getBooleanValue() != null ){
            return getBooleanValue() ? "true" : "false";
        }else{
            return "null";
        }
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if( text==null ){
            combo.setSelectedIndex(2);
        }else{
            if( text.equalsIgnoreCase("null") ){
                combo.setSelectedIndex(2);
            }else{
                if( Text.in(text.trim().toLowerCase(), "true", "1", "yes", "on" ) ){
                    combo.setSelectedIndex(0);
                }else{
                    combo.setSelectedIndex(1);
                }
            }
        }
    }
}
