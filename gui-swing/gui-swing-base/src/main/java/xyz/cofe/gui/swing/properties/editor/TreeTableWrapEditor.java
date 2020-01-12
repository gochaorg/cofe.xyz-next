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
import java.beans.PropertyEditor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import xyz.cofe.gui.swing.tree.TreeTableNodeValueEditor;

/**
 * Редактор - делегат
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TreeTableWrapEditor
    implements TreeTableNodeValueEditor.Editor
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTableWrapEditor.class.getName());
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
        logger.entering(TreeTableWrapEditor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TreeTableWrapEditor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TreeTableWrapEditor.class.getName(), method, result);
    }
    //</editor-fold>

    protected PropertyEditor pe;
    protected JTextField textField;

    public TreeTableWrapEditor( PropertyEditor pe ){
        if (pe== null) {
            throw new IllegalArgumentException("pe==null");
        }
        this.pe = pe;

        if( !pe.supportsCustomEditor() ){
            textField = new JTextField();
        }
    }

    @Override
    public JComponent getComponent() {
        if( textField!=null )return textField;
        Component cmpt =pe.getCustomEditor();
        if( cmpt instanceof JComponent ){
            return (JComponent)cmpt;
        }else{
            JPanel p = new JPanel(new BorderLayout());
            p.add( cmpt );
            return p;
        }
    }

    @Override
    public void startEditing(Object value, Object context) {
        pe.setValue(value);
        if( textField!=null ){
            textField.setText(pe.getAsText());
        }
    }

    @Override
    public boolean stopCellEditing() {
        return true;
    }

    @Override
    public void cancelCellEditing() {
    }

    @Override
    public Object getCellEditorValue() {
        if( textField!=null ){
            pe.setAsText(textField.getText());
            return pe.getValue();
        }
        return pe.getValue();
    }

    //<editor-fold defaultstate="collapsed" desc="cellEditable">
    protected boolean cellEditable = true;

    public void setCellEditable( boolean v ){
        cellEditable = true;
    }

    @Override
    public boolean isCellEditable() {
        return cellEditable;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="shouldSelectCell">
    protected boolean shouldSelectCell = true;

    @Override
    public boolean isShouldSelectCell() {
        return shouldSelectCell;
    }

    public void setShouldSelectCell(boolean shouldSelectCell) {
        this.shouldSelectCell = shouldSelectCell;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="listeners">
    protected final TreeTableNodeValueEditor.CellEditorListenerSupport listeners = new TreeTableNodeValueEditor.CellEditorListenerSupport();

    @Override
    public void clearAllListeners() {
        listeners.clearAllListeners();
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        listeners.addCellEditorListener(l);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        listeners.removeCellEditorListener(l);
    }

    @Override
    public void fireEditingCanceled(Object src) {
        listeners.fireEditingCanceled(src);
    }

    @Override
    public void fireEditingStopped(Object src) {
        listeners.fireEditingStopped(src);
    }
    //</editor-fold>
}
