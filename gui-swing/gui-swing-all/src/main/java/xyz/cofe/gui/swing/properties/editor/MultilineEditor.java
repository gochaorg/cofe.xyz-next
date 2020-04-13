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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import xyz.cofe.gui.swing.SwingListener;

/**
 * Редактор с поддержкой нескольких строк
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class MultilineEditor extends CustomEditor
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(MultilineEditor.class.getName());
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
        logger.entering(MultilineEditor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(MultilineEditor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(MultilineEditor.class.getName(), method, result);
    }
    //</editor-fold>

    public MultilineEditor(){
        buildUI();
    }

    public MultilineEditor(MultilineEditor sample){
        buildUI();
    }

    protected JPanel inlineEditorPanel;
    protected JLabel inlineLabel;
    protected JButton openEditor;

    private void buildUI(){
        inlineEditorPanel = new JPanel();
        inlineEditorPanel.setBorder(new EmptyBorder(0,0,0,0));
        //inlineEditorPanel.setOpaque(false);
        inlineEditorPanel.setBackground(Color.white);
        inlineEditorPanel.setLayout(new BorderLayout());

        inlineLabel = new JLabel();
        inlineEditorPanel.add(inlineLabel);

        openEditor = new JButton("..");
        SwingListener.onActionPerformed(openEditor, obj -> {
            openEditor();
        });
        inlineEditorPanel.add( openEditor, BorderLayout.EAST);

        externalEditor = new ExternalTextAreaEditor();
        externalEditor.setContextComponent(inlineEditorPanel);
        externalEditor.setConsumer(externalEditorConsumer);
    }

    protected ExternalEditor externalEditor = null;
    protected ExternalEditorConsumer externalEditorConsumer = new ExternalEditorConsumer() {
        @Override
        public void canceled(ExternalEditor ed) {
            if( ed==null )return;
            if( externalEditor!=ed )return;
            if( prestartValues.containsKey(ed) ){
                setValue(prestartValues.get(ed));
            }
            fireEditingCanceled(MultilineEditor.this);
        }

        @Override
        public void closed(ExternalEditor ed) {
            if( ed==null )return;
            if( externalEditor!=ed )return;
            fireEditingStopped(MultilineEditor.this);
        }

        @Override
        public void updated(ExternalEditor ed, Object value) {
            if( ed==null )return;
            if( externalEditor!=ed )return;
            setValue(value, false);
        }
    };

    protected WeakHashMap<ExternalEditor,Object> prestartValues = new WeakHashMap<>();

    protected void openEditor(){
        if( externalEditor!=null ){
            prestartValues.put(externalEditor, value);
            externalEditor.open(value);
        }
    }

    @Override
    public MultilineEditor clone() {
        return new MultilineEditor(this);
    }

    @Override
    protected JComponent createComponent() {
        return inlineEditorPanel;
    }

    private String value = null;

    @Override
    public void setValue(Object value) {
        setValue(value, true);
    }

    protected void setValue(Object value, boolean withExternal) {
        this.value = value!=null ? value.toString() : null;

        if( this.inlineLabel !=  null ){
            this.inlineLabel.setText(value != null ? value.toString() : null);
        }

        if( withExternal && externalEditor!=null && externalEditor.isOpen() ){
            externalEditor.setValue(value);
        }
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public String getJavaInitializationString() {
        return this.value;
    }

    @Override
    public String getAsText() {
        return this.value;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        this.value = text;
        if( externalEditor!=null && externalEditor.isOpen() ){
            externalEditor.setValue(value);
        }
    }
}
