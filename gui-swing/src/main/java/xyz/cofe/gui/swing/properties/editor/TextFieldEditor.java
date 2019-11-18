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

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import xyz.cofe.gui.swing.SwingListener;
import xyz.cofe.gui.swing.properties.SetPropertyEditorOpts;

/**
 * Редактор для String свойств
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TextFieldEditor
    extends CustomEditor
    implements SetPropertyEditorOpts
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TextFieldEditor.class.getName());
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
        logger.entering(TextFieldEditor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TextFieldEditor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TextFieldEditor.class.getName(), method, result);
    }
    //</editor-fold>

    private boolean allowNull;

    public TextFieldEditor( boolean allowNull ){
        this.allowNull = allowNull;
    }

    public TextFieldEditor( TextFieldEditor sample ){
        if (sample== null) {
            throw new IllegalArgumentException("sample==null");
        }
        this.allowNull = sample.allowNull;
    }

    @Override
    public CustomEditor clone() {
        return new TextFieldEditor(this);
    }

    @Override
    public void setPropertyEditorOpts(String opts) {
    }

    @Override
    public void startEditing(Object value, Object context) {
        super.startEditing(value, context);
    }

    //<editor-fold defaultstate="collapsed" desc="nullSelected : boolean">
    private JLabel nullButton;
    protected JLabel getNullButton(){
        if( nullButton!=null )return nullButton;

        nullButton = new JLabel(getNullIcon());
        nullButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        nullButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        SwingListener.onMouseClicked(nullButton, (MouseEvent me) -> {
            if( me.getButton()==MouseEvent.BUTTON1 ){
                setNullSelected(!isNullSelected());
            }
        });

        return nullButton;
    }

    public void setNullSelected( boolean selected ){
        boolean old = isNullSelected();
        if( nullButton!=null ){
            Icon icn1 = getNullSelectedIcon();
            Icon icn2 = getNullUnSelectedIcon();
            nullButton.setIcon(selected ? icn1 : icn2);
        }
        boolean cur = isNullSelected();
        firePropertyChanged("nullSelected", old, cur);
    }
    public boolean isNullSelected(){
        if( nullButton==null )return false;
        Icon icn = nullButton.getIcon();
        return icn == getNullSelectedIcon();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="textField : JTextComponent">
    protected JTextComponent textField;
    protected JTextComponent getTextField(){
        if( textField!=null )return textField;
        textField = new JTextField();
        textField.setBorder(new EmptyBorder(0,0,0,0));
        return textField;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="allowNull : boolean">
    public boolean isAllowNull(){
        //if( nullButton==null )return false;
        return getNullButton().isVisible();
    }
    public void setAllowNull(boolean allow){
        if( nullButton==null )return;

        boolean old = isAllowNull();

        nullButton.setVisible(allow);
        if( panel!=null ){
            panel.revalidate();
            panel.invalidate();
            panel.repaint();
        }

        boolean cur = isAllowNull();
        this.allowNull = cur;

        if( Objects.equals(old, cur) )firePropertyChanged("nullable", old, cur);
    }
    //</editor-fold>

    private JLabel extEditorButton;
    protected JLabel getOpenExternalEditor(){
        if( extEditorButton!=null )return extEditorButton;

        extEditorButton = new JLabel(getEditIcon());
        extEditorButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        extEditorButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        SwingListener.onMouseClicked(extEditorButton, (MouseEvent obj) -> {
            Object curVal = getValue();
            openEditor(curVal);
        });

        return extEditorButton;
    }

    protected void openEditor(Object currentValue){
        if( externalEditor!=null ){
            prestartValues.put(externalEditor, currentValue);
            externalEditor.open(currentValue);
        }
    }


    protected ExternalEditor externalEditor = null;

    public ExternalEditor getExternalEditor() {
        return externalEditor;
    }

    public void setExternalEditor(ExternalEditor externalEditor) {
        this.externalEditor = externalEditor;
        if( this.externalEditor!=null ){
            this.externalEditor.setConsumer(externalEditorConsumer);
        }

        if( panel!=null && extEditorButton!=null ){
            extEditorButton.setVisible(externalEditor!=null);
            panel.revalidate();
            panel.invalidate();
            panel.repaint();
        }
    }

    protected WeakHashMap<ExternalEditor,Object> prestartValues = new WeakHashMap<>();

    protected ExternalEditorConsumer externalEditorConsumer = new ExternalEditorConsumer() {
        @Override
        public void canceled(ExternalEditor ed) {
            if( ed==null )return;
            if( externalEditor!=ed )return;
            if( prestartValues.containsKey(ed) ){
                setValue(prestartValues.get(ed));
            }
            fireEditingCanceled(TextFieldEditor.this);
        }

        @Override
        public void closed(ExternalEditor ed) {
            if( ed==null )return;
            if( externalEditor!=ed )return;
            fireEditingStopped(TextFieldEditor.this);
        }

        @Override
        public void updated(ExternalEditor ed, Object value) {
            if( ed==null )return;
            if( externalEditor!=ed )return;
            setValue(value, false);
        }
    };

    //<editor-fold defaultstate="collapsed" desc="editorPanel">
    protected JPanel panel;
    protected JComponent getEditorPanel(){
        if( panel!=null )return panel;

        panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));

        GridBagConstraints gbc = null;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add( getTextField(), gbc );

        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 3, 0, 0);
        panel.add( getNullButton(), gbc );

        SwingListener.onKeyTyped(getTextField(), e->setNullSelected(false));
        SwingListener.onKeyPressed(getTextField(), ke -> {
            if( ke.getKeyCode()==KeyEvent.VK_ENTER ){
                fireEditingStopped(TextFieldEditor.this);
            }
        });

        if( allowNull ){
            getNullButton().setVisible(true);
        }else{
            getNullButton().setVisible(false);
        }

        gbc = new GridBagConstraints();
        gbc.gridx = 11;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 3, 0, 0);
        panel.add(getOpenExternalEditor(), gbc );

        getOpenExternalEditor().setVisible(externalEditor!=null);

        return panel;
    }

    @Override
    protected JComponent createComponent() {
        return getEditorPanel();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="get/setTextFieldValue">
    /**
     * Указывает редактируемое значение
     * @param value значение
     */
    protected void setTextFieldValue( Object value ){
        if( value!=null ){
            getTextField().setText(value.toString());
        }else{
            getTextField().setText("");
        }
    }

    protected Object getTextFieldValue(){
        return getTextField().getText();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="get/setValue">
    @Override
    public void setValue(Object value) {
        /*if( allowNull ){
            setNullSelected(value==null);
        }

        setTextFieldValue(value);*/
        setValue(value, true);
    }

    @Override
    public Object getValue() {
        if( allowNull && isNullSelected() ){
            return null;
        }
        return getTextFieldValue();
    }
    protected void setValue(Object value, boolean withExternal) {
        /*this.value = value!=null ? value.toString() : null;

        if( this.inlineLabel !=  null ){
            this.inlineLabel.setText(value != null ? value.toString() : null);
        }

        if( withExternal && externalEditor!=null && externalEditor.isOpen() ){
            externalEditor.setValue(value);
        }*/

        if( allowNull ){
            setNullSelected(value==null);
        }

        setTextFieldValue(value);

        if( withExternal && externalEditor!=null && externalEditor.isOpen() ){
            externalEditor.setValue(value);
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="getJavaInitializationString()">
    @Override
    public String getJavaInitializationString() {
        return null;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="get/set asText">
    @Override
    public String getAsText() {
        Object val = getValue();
        return val!=null ? val.toString() : null;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
    }
    //</editor-fold>
}
