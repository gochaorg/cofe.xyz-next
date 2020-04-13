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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import xyz.cofe.gui.swing.bean.UiBean;
import xyz.cofe.gui.swing.properties.Property;
import xyz.cofe.gui.swing.tree.TreeTableNodeValue;
import xyz.cofe.gui.swing.tree.TreeTableNodeValueEditor;

/**
 * Абстрактный редактор
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public abstract class CustomEditor
    extends TreeTableNodeValueEditor.BaseEditor
    implements PropertyEditor
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(CustomEditor.class.getName());
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
        logger.entering(CustomEditor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(CustomEditor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(CustomEditor.class.getName(), method, result);
    }
    //</editor-fold>

    protected final PropertyEditorSupport propertyEditorSupport;

    public CustomEditor(){
        propertyEditorSupport = new PropertyEditorSupport(this);
    }

    public CustomEditor(CustomEditor sample){
        propertyEditorSupport = new PropertyEditorSupport(this);
        if( sample!=null ){
            setTags(sample.getTags());
        }
    }

    @Override
    abstract public CustomEditor clone();

    //<editor-fold defaultstate="collapsed" desc="tags">
    @Override
    public String[] getTags() {
        return propertyEditorSupport.getTags();
    }

    public void setTags(String[] tags) {
        propertyEditorSupport.setTags(tags);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="add/remove PropertyChangeListener">
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyEditorSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyEditorSupport.removePropertyChangeListener(listener);
    }

    public void firePropertyChanged(String name, Object oldValue, Object newValue) {
        propertyEditorSupport.firePropertyChanged(name, oldValue, newValue);
    }
    //</editor-fold>

    @Override
    public Object getCellEditorValue() {
        return getValue();
    }

    protected void parseEditOptions(String opts) {
    }

    @Override
    public void startEditing(Object value, Object context) {
        if( context instanceof TreeTableNodeValue ){
            TreeTableNodeValue ttnv = (TreeTableNodeValue)context;
            if( ttnv.getNode().getData() instanceof Property ){
                Property prop = (Property)ttnv.getNode().getData();
                UiBean uib = prop.getUiBean();
                if( uib!=null ){
                    parseEditOptions(uib.editorOpts());
                }else{
                    parseEditOptions("");
                }
            }else{
                parseEditOptions("");
            }
        }else{
            parseEditOptions("");
        }
        setValue(value);
        super.startEditing(value, context);
    }

    /**
     * Создание графического компонента - редактора
     * @return компонент редактор
     */
    protected abstract JComponent createComponent();

    @Override
    public JComponent getComponent() {
        if( this.component!=null )return this.component;
        this.component = createComponent();
        return this.component;
    }

    @Override
    public boolean isPaintable() {
        return false;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Component getCustomEditor() {
        return getComponent();
    }

    @Override
    public boolean supportsCustomEditor() {
        JComponent cmp = getComponent();
        return cmp!=null;
    }
}
