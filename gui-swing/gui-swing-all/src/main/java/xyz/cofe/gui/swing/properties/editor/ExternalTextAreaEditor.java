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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import xyz.cofe.ecolls.Closeables;
import xyz.cofe.gui.swing.GuiUtil;
import xyz.cofe.gui.swing.SwingListener;

/**
 * Внешний текстовый редактор - открывает редактор в отдельном окне
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class ExternalTextAreaEditor implements ExternalEditor {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ExternalTextAreaEditor.class.getName());
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
        logger.entering(ExternalTextAreaEditor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(ExternalTextAreaEditor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(ExternalTextAreaEditor.class.getName(), method, result);
    }
    //</editor-fold>

    private JFrame frame = null;
    private JScrollPane scroll = null;
    private JTextArea textArea = null;

    private ExternalEditorConsumer consumer = null;
    private Component context;

    public ExternalTextAreaEditor(){
    }

    protected void buildUI(){
        frame = new JFrame("text editor");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        SwingListener.onWindowClosing(frame, obj -> {
            if( consumer!=null ){
                consumer.closed(ExternalTextAreaEditor.this);
            }
        });

        frame.getContentPane().setLayout(new BorderLayout());

        textArea = new JTextArea();
        scroll = new JScrollPane(textArea);
        frame.getContentPane().add(scroll);

        JPanel buttons = new JPanel(new FlowLayout());
        frame.getContentPane().add(buttons,BorderLayout.SOUTH);

        JButton okButton = new JButton("Ok");
        JButton cancelButton = new JButton("Cancel");

        buttons.add(cancelButton);
        buttons.add(okButton);

        SwingListener.onActionPerformed(okButton, obj -> {
            if( consumer!=null ){
                consumer.updated(ExternalTextAreaEditor.this, getValue());
                if( frame!=null ){
                    frame.setVisible(false);
                    frame.dispose();
                    consumer.closed(ExternalTextAreaEditor.this);
                }
            }
        });
        SwingListener.onActionPerformed(cancelButton, obj -> {
            if( consumer!=null ){
                //consumer.updated(ExternalTextAreaEditor.this, getValue());
                if( frame!=null ){
                    frame.setVisible(false);

                    frame.dispose();
                    consumer.canceled(ExternalTextAreaEditor.this);
                    //consumer.closed(ExternalTextAreaEditor.this);
                }
            }
        });

        listenChanges(true);
    }

    protected Closeables textChangedListeners = new Closeables();

    protected void listenChanges(boolean listen){
        textChangedListeners.close();
        if( listen ){
            Closeable cl =
                SwingListener.onFocusLost(textArea, obj -> {
                    if( consumer!=null ){
                        consumer.updated(ExternalTextAreaEditor.this, getValue());
                    }
                });

            textChangedListeners.add(cl);
        }
    }

    @Override
    public void setContextComponent(Component contextComponent) {
        this.context = contextComponent;
    }

    @Override
    public Component getContextComponent() {
        return this.context;
    }

    @Override
    public void setConsumer(ExternalEditorConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public ExternalEditorConsumer getConsumer() {
        return this.consumer;
    }

    @Override
    public void open(Object value) {
        if( frame==null || !frame.isVisible() ){
            buildUI();
        }

        if( context!=null && context.isVisible() ){
            Point loc = context.getLocationOnScreen();
            Dimension sz = context.getSize();

            Rectangle screenRect = GuiUtil.getScreenRectangle();

            Point start = new Point(loc.x, loc.y + sz.height );
            int bottomFree = screenRect.height - start.y;
            if( bottomFree<frame.getHeight() ){
                start = new Point( loc.x, loc.y - frame.getHeight() );
            }

            frame.setLocation(start);
        }

        listenChanges(false);
        setValue(value);
        listenChanges(true);

        frame.setVisible(true);
        frame.toFront();
        //frame.requestFocusInWindow();
        if( textArea!=null )textArea.requestFocus();
    }

    @Override
    public boolean isOpen() {
        return frame!=null && frame.isVisible();
    }

    @Override
    public void close() {
        if( frame!=null ){
            frame.setVisible(false);
            frame.dispose();
            frame = null;
            listenChanges(false);
        }
    }

    @Override
    public void setValue(Object value) {
        listenChanges(false);
        if( textArea!=null ){
            textArea.setText(value != null ? value.toString() : "");
        }
        listenChanges(true);
    }

    @Override
    public Object getValue() {
        if( textArea!=null )return textArea.getText();
        return null;
    }
}
