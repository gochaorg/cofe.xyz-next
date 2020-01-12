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
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import xyz.cofe.gui.swing.SwingListener;
import xyz.cofe.gui.swing.properties.Icons;

/**
 * Редактор значений типа Enum
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class EnumEditor extends CustomEditor
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(EnumEditor.class.getName());
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
        logger.entering(EnumEditor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(EnumEditor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(EnumEditor.class.getName(), method, result);
    }
    //</editor-fold>

    public EnumEditor( Class cenum, boolean allowNull ){
        if (cenum== null) {
            throw new IllegalArgumentException("cenum==null");
        }

        if( !cenum.isEnum() ){
            throw new IllegalArgumentException("cenum is not enum");
        }

        this.classEnum = cenum;
        this.allowNull = allowNull;

        Object[] consts = cenum.getEnumConstants();
        if( allowNull ){
            consts = Arrays.copyOf(consts, consts.length+1);
            consts[consts.length-1] = null;
            nullIndex = consts.length-1;
        }

        ListCellRenderer cr = new BasicComboBoxRenderer(){
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                Object renderValue = value;

                if( value==null ){
                    renderValue = "null";
                }

                super.getListCellRendererComponent(list, renderValue, index, isSelected, cellHasFocus);

                if( value == null ){
                    setIcon(Icons.getNullIcon());
                }else{
                    setIcon(null);
                }

                return this;
            }
        };

        combo = new JComboBox( consts );
        combo.setRenderer(cr);

        SwingListener.onItemStateChanged(combo, (ItemEvent obj) -> {
            fireEditingStopped(EnumEditor.this);
        });
    }

    public EnumEditor( EnumEditor sample ){
        if (sample== null) {
            throw new IllegalArgumentException("sample==null");
        }

        this.classEnum = sample.classEnum;
        this.allowNull = sample.allowNull;

        Object[] consts = classEnum.getEnumConstants();
        if( allowNull ){
            consts = Arrays.copyOf(consts, consts.length+1);
            consts[consts.length-1] = null;
            nullIndex = consts.length-1;
        }

        ListCellRenderer cr = new BasicComboBoxRenderer(){
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                Object renderValue = value;

                if( value==null ){
                    renderValue = "null";
                }

                super.getListCellRendererComponent(list, renderValue, index, isSelected, cellHasFocus);

                if( value == null ){
                    setIcon(Icons.getNullIcon());
                }else{
                    setIcon(null);
                }

                return this;
            }
        };

        combo = new JComboBox( consts );
        combo.setRenderer(cr);

        SwingListener.onItemStateChanged(combo, (ItemEvent obj) -> {
            fireEditingStopped(EnumEditor.this);
        });
    }

    @Override
    public EnumEditor clone() {
        return new EnumEditor(this);
    }

    protected Class classEnum;
    protected boolean allowNull;
    protected int nullIndex;

    protected JComboBox combo = null;

    @Override
    protected JComponent createComponent() {
        return combo;
    }

    @Override
    public void setValue(Object value) {
        if( value==null ){
            if( !allowNull ){
                throw new IllegalArgumentException("value == null");
            }
            combo.setSelectedIndex(nullIndex);
            return;
        }

        combo.setSelectedItem(value);
    }

    @Override
    public Object getValue() {
        int selIdx = combo.getSelectedIndex();
        if( selIdx==nullIndex && allowNull )return null;

        return combo.getSelectedItem();
    }

    @Override
    public String getJavaInitializationString() {
        Object val = getValue();
        if( val==null )return "null";
        return val.toString();
    }

    @Override
    public String getAsText() {
        Object v = getValue();
        return v==null ? "null" : v.toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if( text==null || text.equals("null") ){
            if( allowNull ){
                combo.setSelectedIndex(nullIndex);
                return;
            }
            throw new IllegalArgumentException("text == null");
        }

        combo.setSelectedItem( Enum.valueOf(classEnum, text) );
    }
}
