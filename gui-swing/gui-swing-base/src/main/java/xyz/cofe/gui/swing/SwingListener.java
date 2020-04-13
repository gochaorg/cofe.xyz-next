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

package xyz.cofe.gui.swing;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import javax.swing.tree.ExpandVetoException;
import java.awt.*;
import java.awt.event.*;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Упрощение создания подписчиков
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class SwingListener {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(SwingListener.class.getName());
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
        logger.entering(SwingListener.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(SwingListener.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(SwingListener.class.getName(), method, result);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="KeyListener">
    /**
     * Подписка на события нажатия кнопки
     * @param component компонент
     * @param consumer приемник события
     * @return отписка от уведомления
     */
    public static Closeable onKeyPressed( final Component component, final Consumer<KeyEvent> consumer ){
        if (component== null) {
            throw new IllegalArgumentException("component==null");
        }
        if (consumer== null) {
            throw new IllegalArgumentException("consumer==null");
        }
        
        final KeyAdapter ka = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addKeyListener(ka);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                component.removeKeyListener(ka);
            }};
        
        return cl;
    }

    /**
     * Подписка на события отпускания кнопки
     * @param component компонент
     * @param consumer приемник события
     * @return отписка от уведомления
     */
    public static Closeable onKeyReleased( final Component component, final Consumer<KeyEvent> consumer ){
        if (component== null) {
            throw new IllegalArgumentException("component==null");
        }
        if (consumer== null) {
            throw new IllegalArgumentException("consumer==null");
        }
        
        final KeyAdapter ka = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addKeyListener(ka);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                component.removeKeyListener(ka);
            }
        };
        
        return cl;
    }

    /**
     * Подписка на события нажатия кнопки
     * @param component компонент
     * @param consumer приемник события
     * @return отписка от уведомления
     */
    public static Closeable onKeyTyped( final Component component, final Consumer<KeyEvent> consumer ){
        if (component== null) {
            throw new IllegalArgumentException("component==null");
        }
        if (consumer== null) {
            throw new IllegalArgumentException("consumer==null");
        }
        
        final KeyAdapter ka = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addKeyListener(ka);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                component.removeKeyListener(ka);
            }};
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ActionListener">
    /**
     * Подписка на события нажатия кнопки
     * @param button  компонент
     * @param consumer приемник события
     * @return отписка от уведомления
     */
    public static Closeable onActionPerformed( final AbstractButton button, final Consumer<ActionEvent> consumer  ){
        if( button==null )throw new IllegalArgumentException( "button==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consumer.accept(e);
            }
        };
        
        button.addActionListener(al);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                button.removeActionListener(al);
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события нажатия кнопки
     * @param button  компонент
     * @param consumer приемник события
     * @return отписка от уведомления
     */
    public static Closeable onActionPerformed( final ButtonModel button, final Consumer<ActionEvent> consumer  ){
        if( button==null )throw new IllegalArgumentException( "button==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consumer.accept(e);
            }
        };
        
        button.addActionListener(al);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                button.removeActionListener(al);
            }};
        
        return cl;
    }

    /**
     * Подписка на события нажатия кнопки
     * @param button  компонент
     * @param consumer приемник события
     * @return отписка от уведомления
     */
    public static Closeable onActionPerformed( final JComboBox button, final Consumer<ActionEvent> consumer  ){
        if( button==null )throw new IllegalArgumentException( "button==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consumer.accept(e);
            }
        };
        
        button.addActionListener(al);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                button.removeActionListener(al);
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события нажатия кнопки
     * @param button  компонент
     * @param consumer приемник события
     * @return отписка от уведомления
     */
    public static Closeable onActionPerformed( final JFileChooser button, final Consumer<ActionEvent> consumer  ){
        if( button==null )throw new IllegalArgumentException( "button==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consumer.accept(e);
            }
        };
        
        button.addActionListener(al);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                button.removeActionListener(al);
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события нажатия кнопки
     * @param button  компонент
     * @param consumer приемник события
     * @return отписка от уведомления
     */
    public static Closeable onActionPerformed( final JTextField button, final Consumer<ActionEvent> consumer  ){
        if( button==null )throw new IllegalArgumentException( "button==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consumer.accept(e);
            }
        };
        
        button.addActionListener(al);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                button.removeActionListener(al);
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события срабатывания таймера
     * @param button компонент
     * @param consumer приемник события
     * @return отписка от уведомления
     */
    public static Closeable onActionPerformed( final Timer button, final Consumer<ActionEvent> consumer  ){
        if( button==null )throw new IllegalArgumentException( "button==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consumer.accept(e);
            }
        };
        
        button.addActionListener(al);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                button.removeActionListener(al);
            }};
        
        return cl;
    }

    /**
     * Подписка на события нажатия кнопки/combobox
     * @param button компонент
     * @param consumer приемник события
     * @return отписка от уведомления
     */
    public static Closeable onActionPerformed( final ComboBoxEditor button, final Consumer<ActionEvent> consumer  ){
        if( button==null )throw new IllegalArgumentException( "button==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consumer.accept(e);
            }
        };
        
        button.addActionListener(al);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                button.removeActionListener(al);
            }};
        
        return cl;
    }
    
    /**
     * Создание подписчика вызывающего метод объекта
     * @param reciver объект
     * @param method метод объекта
     * @param args аргументы метода, если метод содержит аргмент типа ActionEvent, то значение будет подставлено из события
     * @return подписчик
     */
    public static Consumer<ActionEvent> consumeActionEvent( final Object reciver, String method, final Object ... args ){
        if (reciver== null) {
            throw new IllegalArgumentException("reciver==null");
        }
        if (method== null) {
            throw new IllegalArgumentException("method==null");
        }
        
        Method[] methods = reciver.getClass().getMethods();
        Method meth = null;
        for( Method emeth : methods ){
            Method m = emeth;
            if( !m.getName().equals(method) )continue;
            meth = m;
            break;
        }
        
        if( meth==null ){
            throw new IllegalArgumentException("method \""+method+"\" not found");
        }
        
        int aeParamIdx = -1;
        
        Class[] paramTypes = meth.getParameterTypes();
        if( paramTypes.length>0 ){
            for( int pi=0; pi<paramTypes.length; pi++ ){
                Class param = paramTypes[pi];
                if( param.equals(ActionEvent.class) ){
                    aeParamIdx = pi;
                }
            }
        }
        
        final int aePIdx = aeParamIdx;
        final Method mth = meth;
        
        Consumer<ActionEvent> ra = new Consumer<ActionEvent>() {
            @Override
            public void accept(ActionEvent ae) {
                List params = new ArrayList();
                int ai = -1;
                boolean aeAdded = false;
                for( Object arg : args ){
                    ai++;
                    if( aePIdx==ai ){
                        params.add(ae);
                        aeAdded = true;
                    }
                    params.add(arg);
                }
                if( !aeAdded && aePIdx>=0 ){
                    params.add(ae);
                }
                
                try {
                    mth.invoke(reciver, params.toArray());
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(SwingListener.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(
                        null, 
                        ex.getMessage(), ex.getClass().getName(), JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        return ra;
    }
    
    /**
     * Создает подписчика вызывающего Runnable
     * @param reciver целевой подписчик
     * @return подписчик
     */
    public static Consumer<ActionEvent> consumeActionEvent( final Runnable reciver ){
        if( reciver==null )throw new IllegalArgumentException("reciver == null");
        return new Consumer<ActionEvent>() {
            @Override
            public void accept(ActionEvent obj) {
                reciver.run();
            }
        };
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ChangeListener">
    /**
     * Подписка на события изменения состояния
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onStateChanged( final AbstractButton component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ChangeListener ml = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addChangeListener(ml);
        
        Closeable cl = new Closeable() {
            AbstractButton cmpt = component;
            ChangeListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeChangeListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события изменения состояния
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onStateChanged( final AbstractSpinnerModel component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ChangeListener ml = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addChangeListener(ml);
        
        Closeable cl = new Closeable() {
            AbstractSpinnerModel cmpt = component;
            ChangeListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeChangeListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события изменения состояния
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onStateChanged( final BoundedRangeModel component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ChangeListener ml = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addChangeListener(ml);
        
        Closeable cl = new Closeable() {
            BoundedRangeModel cmpt = component;
            ChangeListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeChangeListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события изменения состояния
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onStateChanged( final ButtonModel component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ChangeListener ml = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addChangeListener(ml);
        
        Closeable cl = new Closeable() {
            ButtonModel cmpt = component;
            ChangeListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeChangeListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события изменения состояния
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onStateChanged( final SingleSelectionModel component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ChangeListener ml = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addChangeListener(ml);
        
        Closeable cl = new Closeable() {
            SingleSelectionModel cmpt = component;
            ChangeListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeChangeListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события изменения состояния
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onStateChanged( final SpinnerModel component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ChangeListener ml = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addChangeListener(ml);
        
        Closeable cl = new Closeable() {
            SpinnerModel cmpt = component;
            ChangeListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeChangeListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события изменения состояния
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onStateChanged( final DefaultBoundedRangeModel component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ChangeListener ml = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addChangeListener(ml);
        
        Closeable cl = new Closeable() {
            DefaultBoundedRangeModel cmpt = component;
            ChangeListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeChangeListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события изменения состояния
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onStateChanged( final JProgressBar component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ChangeListener ml = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addChangeListener(ml);
        
        Closeable cl = new Closeable() {
            JProgressBar cmpt = component;
            ChangeListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeChangeListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события изменения состояния
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onStateChanged( final JSlider component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ChangeListener ml = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addChangeListener(ml);
        
        Closeable cl = new Closeable() {
            JSlider cmpt = component;
            ChangeListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeChangeListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события изменения состояния
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onStateChanged( final JSpinner component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ChangeListener ml = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addChangeListener(ml);
        
        Closeable cl = new Closeable() {
            JSpinner cmpt = component;
            ChangeListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeChangeListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события изменения состояния
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onStateChanged( final JTabbedPane component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ChangeListener ml = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addChangeListener(ml);
        
        Closeable cl = new Closeable() {
            JTabbedPane cmpt = component;
            ChangeListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeChangeListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события изменения состояния
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onStateChanged( final JViewport component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ChangeListener ml = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addChangeListener(ml);
        
        Closeable cl = new Closeable() {
            JViewport cmpt = component;
            ChangeListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeChangeListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события изменения состояния
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onStateChanged( final MenuSelectionManager component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ChangeListener ml = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addChangeListener(ml);
        
        Closeable cl = new Closeable() {
            MenuSelectionManager cmpt = component;
            ChangeListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeChangeListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MouseListener">
    private static Consumer mouseEvent2TableCellMouseEvent(
        final Consumer<TableCellMouseEvent> consumer
    )
    {
        return new Consumer() {
            @Override
            public void accept(Object evt) {
                if( evt==null )return;
                if( !(evt instanceof MouseEvent) )return;
                
                MouseEvent mevt = (MouseEvent)evt;
                if( !(mevt.getComponent() instanceof JTable) )return;
                
                JTable tbl = (JTable)mevt.getComponent();
                int x = mevt.getX();
                int y = mevt.getY();
                Point p = new Point(x, y);
                
                int row = tbl.rowAtPoint(p);
                int col = tbl.columnAtPoint(p);
                
                TableCellMouseEvent tcme = new TableCellMouseEvent(mevt, tbl, row, col);
                consumer.accept(tcme);
            }
        };
    }

    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onCellMouseClicked(
        final JTable component, 
        final Consumer<TableCellMouseEvent> consumer  )
    {
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        return onMouseClicked(component, mouseEvent2TableCellMouseEvent(consumer));
    }

    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onCellMousePressed(
        final JTable component, 
        final Consumer<TableCellMouseEvent> consumer  )
    {
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        return onMousePressed(component, mouseEvent2TableCellMouseEvent(consumer));
    }

    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onCellMouseReleased(
        final JTable component, 
        final Consumer<TableCellMouseEvent> consumer  )
    {
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        return onMouseReleased(component, mouseEvent2TableCellMouseEvent(consumer));
    }

    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onCellMouseEntered(
        final JTable component, 
        final Consumer<TableCellMouseEvent> consumer  )
    {
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        return onMouseEntered(component, mouseEvent2TableCellMouseEvent(consumer));
    }

    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onCellMouseExited(
        final JTable component, 
        final Consumer<TableCellMouseEvent> consumer  )
    {
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        return onMouseExited(component, mouseEvent2TableCellMouseEvent(consumer));
    }

    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMouseClicked( final Component component, final Consumer<MouseEvent> consumer  ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MouseListener ml = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        
        component.addMouseListener(ml);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                component.removeMouseListener(ml);
            }};
        
        return cl;
    }

    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMousePressed( final Component component, final Consumer<MouseEvent> consumer  ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MouseListener ml = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
            }
        };
        
        component.addMouseListener(ml);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                component.removeMouseListener(ml);
            }};
        
        return cl;
    }

    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMouseReleased( final Component component, final Consumer<MouseEvent> consumer  ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MouseListener ml = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /*consumer.accept(e);*/
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
            }
        };
        
        component.addMouseListener(ml);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                component.removeMouseListener(ml);
            }};
        
        return cl;
    }

    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMouseEntered( final Component component, final Consumer<MouseEvent> consumer  ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MouseListener ml = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /*consumer.accept(e);*/
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
            }
        };
        
        component.addMouseListener(ml);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                component.removeMouseListener(ml);
            }};
        
        return cl;
    }

    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMouseExited( final Component component, final Consumer<MouseEvent> consumer  ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MouseListener ml = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /*consumer.accept(e);*/
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addMouseListener(ml);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                component.removeMouseListener(ml);
            }};
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MouseMotionListener">
    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onCellMouseDragged(
        final JTable component, 
        final Consumer<TableCellMouseEvent> consumer  )
    {
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        return onMouseDragged(component, mouseEvent2TableCellMouseEvent(consumer));
    }

    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onCellMouseMoved(
        final JTable component, 
        final Consumer<TableCellMouseEvent> consumer  )
    {
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        return onMouseMoved(component, mouseEvent2TableCellMouseEvent(consumer));
    }

    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMouseDragged( final Component component, final Consumer<MouseEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MouseMotionListener ml = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
            }
        };
        
        component.addMouseMotionListener(ml);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                component.removeMouseMotionListener(ml);
            }};
        
        return cl;
    }

    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMouseMoved( final Component component, final Consumer<MouseEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MouseMotionListener ml = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addMouseMotionListener(ml);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                component.removeMouseMotionListener(ml);
            }};
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MouseWheelListener">
    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onCellWheelMoved(
        final JTable component, 
        final Consumer<TableCellMouseEvent> consumer  )
    {
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        return onMouseWheelMoved(
            component, 
            mouseEvent2TableCellMouseEvent(consumer)
        );
    }

    /**
     * Подписка на события мыши
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMouseWheelMoved( final Component component, final Consumer<MouseWheelEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MouseWheelListener ml = new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addMouseWheelListener(ml);
        
        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                component.removeMouseWheelListener(ml);
            }};
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="DocumentListener">
    /**
     * Подписка на события изменения текста
     * @param textDocument компонент
     * @param runn подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onTextChanged( final javax.swing.text.Document textDocument, final Runnable runn ){
        if( textDocument==null )throw new IllegalArgumentException( "textDocument==null" );
        if( runn==null )throw new IllegalArgumentException( "runn==null" );
        
        final DocumentListener dl = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                runn.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                runn.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                runn.run();
            }
        };
        
        textDocument.addDocumentListener(dl);
        
        Closeable cl = new Closeable() {
            javax.swing.text.Document doc = textDocument;
            DocumentListener l = dl;
            @Override
            public void close() throws IOException {
                if( doc!=null && l!=null ){
                    doc.removeDocumentListener(l);
                    doc = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события изменения текста
     * @param textComponent компонент
     * @param runn подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onTextChanged( final JTextComponent textComponent, final Runnable runn ){
        if( textComponent==null )throw new IllegalArgumentException( "textComponent==null" );
        if( runn==null )throw new IllegalArgumentException( "runn==null" );
        
        return onTextChanged(textComponent.getDocument(), runn);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="FocusListener">
    /**
     * Подписка на события изменения фокуса ввода
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onFocusGained( final Component component, final Consumer<FocusEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final FocusListener ml = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void focusLost(FocusEvent e) {
            }
        };
        
        component.addFocusListener(ml);
        
        Closeable cl = new Closeable() {
            Component cmpt = component;
            FocusListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeFocusListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события изменения фокуса ввода
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onFocusLost( final Component component, final Consumer<FocusEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final FocusListener ml = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addFocusListener(ml);
        
        Closeable cl = new Closeable() {
            Component cmpt = component;
            FocusListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeFocusListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="AncestorListener">
    /**
     * Подписка на события изменения иерархии визуальных компонентов.
     * Они включают перемещение и когда компонент становится видимым или невидимым, 
     * любой setVisible () метод или будучи добавленным или удаленный из иерархии компонентов.
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onAncestorAdded( final JComponent component, final Consumer<AncestorEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final AncestorListener ml = new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                consumer.accept(event);
            }
            
            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }
            
            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        };
        
        component.addAncestorListener(ml);
        
        Closeable cl = new Closeable() {
            JComponent cmpt = component;
            AncestorListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeAncestorListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события изменения иерархии визуальных компонентов.
     * Они включают перемещение и когда компонент становится видимым или невидимым, 
     * любой setVisible () метод или будучи добавленным или удаленный из иерархии компонентов.
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onAncestorRemoved( final JComponent component, final Consumer<AncestorEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final AncestorListener ml = new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
            }
            
            @Override
            public void ancestorRemoved(AncestorEvent event) {
                consumer.accept(event);
            }
            
            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        };
        
        component.addAncestorListener(ml);
        
        Closeable cl = new Closeable() {
            JComponent cmpt = component;
            AncestorListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeAncestorListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события изменения иерархии визуальных компонентов.
     * Они включают перемещение и когда компонент становится видимым или невидимым, 
     * любой setVisible () метод или будучи добавленным или удаленный из иерархии компонентов.
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onAncestorMoved( final JComponent component, final Consumer<AncestorEvent> consumer )
    {
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final AncestorListener ml = new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
                consumer.accept(event);
            }            
        };
        
        component.addAncestorListener(ml);
        
        Closeable cl = new Closeable() {
            JComponent cmpt = component;
            AncestorListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeAncestorListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ContainerListener">
    /**
     * Подписка на события контейнера.
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onComponentAdded( final Container component, final Consumer<ContainerEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ContainerListener ml = new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent e) {
                consumer.accept(e);
            }

            @Override
            public void componentRemoved(ContainerEvent e) {
            }            
        };
        
        component.addContainerListener(ml);
        
        Closeable cl = new Closeable() {
            Container cmpt = component;
            ContainerListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeContainerListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }

    /**
     * Подписка на события контейнера.
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onComponentRemoved( final Container component, final Consumer<ContainerEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ContainerListener ml = new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent e) {                
            }

            @Override
            public void componentRemoved(ContainerEvent e) {
                consumer.accept(e);
            }            
        };
        
        component.addContainerListener(ml);
        
        Closeable cl = new Closeable() {
            Container cmpt = component;
            ContainerListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeContainerListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ComponentListener">
    /**
     * Подписка на события компонента.
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onComponentResized( final Component component, final Consumer<ComponentEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ComponentListener ml = new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void componentMoved(ComponentEvent e) {
            }
            
            @Override
            public void componentShown(ComponentEvent e) {
            }
            
            @Override
            public void componentHidden(ComponentEvent e) {
            }
        };
        
        component.addComponentListener(ml);
        
        Closeable cl = new Closeable() {
            Component cmpt = component;
            ComponentListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeComponentListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события компонента.
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onComponentMoved( final Component component, final Consumer<ComponentEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ComponentListener ml = new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
            }
            
            @Override
            public void componentMoved(ComponentEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void componentShown(ComponentEvent e) {
            }
            
            @Override
            public void componentHidden(ComponentEvent e) {
            }
        };
        
        component.addComponentListener(ml);
        
        Closeable cl = new Closeable() {
            Component cmpt = component;
            ComponentListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeComponentListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события компонента.
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onComponentShown( final Component component, final Consumer<ComponentEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ComponentListener ml = new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
            }
            
            @Override
            public void componentMoved(ComponentEvent e) {
            }
            
            @Override
            public void componentShown(ComponentEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void componentHidden(ComponentEvent e) {
            }
        };
        
        component.addComponentListener(ml);
        
        Closeable cl = new Closeable() {
            Component cmpt = component;
            ComponentListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeComponentListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события компонента.
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onComponentHidden( final Component component, final Consumer<ComponentEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ComponentListener ml = new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
            }
            
            @Override
            public void componentMoved(ComponentEvent e) {
            }
            
            @Override
            public void componentShown(ComponentEvent e) {
            }
            
            @Override
            public void componentHidden(ComponentEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addComponentListener(ml);
        
        Closeable cl = new Closeable() {
            Component cmpt = component;
            ComponentListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeComponentListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="HierarchyBoundsListener">
    /**
     * Подписка на события компонента связанные с иерархией визуальных компонент.
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onAncestorMoved( final Component component, final Consumer<HierarchyEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final HierarchyBoundsListener ml = new HierarchyBoundsListener() {
            @Override
            public void ancestorMoved(HierarchyEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void ancestorResized(HierarchyEvent e) {
            }
        };
        
        component.addHierarchyBoundsListener(ml);
        
        Closeable cl = new Closeable() {
            Component cmpt = component;
            HierarchyBoundsListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeHierarchyBoundsListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события компонента связанные с иерархией визуальных компонент.
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onAncestorResized( final Component component, final Consumer<HierarchyEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final HierarchyBoundsListener ml = new HierarchyBoundsListener() {
            @Override
            public void ancestorMoved(HierarchyEvent e) {
            }
            
            @Override
            public void ancestorResized(HierarchyEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addHierarchyBoundsListener(ml);
        
        Closeable cl = new Closeable() {
            Component cmpt = component;
            HierarchyBoundsListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeHierarchyBoundsListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="HierarchyListener">
    /**
     * Подписка на события компонента связанные с иерархией визуальных компонент.
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onHierarchyChanged( final Component component, final Consumer<HierarchyEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final HierarchyListener ml = new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addHierarchyListener(ml);
        
        Closeable cl = new Closeable() {
            Component cmpt = component;
            HierarchyListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeHierarchyListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="InputMethodListener">
    /**
     * Подписка на события изменения способоа ввода текста
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onInputMethodTextChanged( final Component component, final Consumer<InputMethodEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final InputMethodListener ml = new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                consumer.accept(event);
            }
            
            @Override
            public void caretPositionChanged(InputMethodEvent event) {
            }
        };
        
        component.addInputMethodListener(ml);
        
        Closeable cl = new Closeable() {
            Component cmpt = component;
            InputMethodListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeInputMethodListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события изменения позиции ввода текста - каретки
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onCaretPositionChanged( final Component component, final Consumer<InputMethodEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final InputMethodListener ml = new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
            }
            
            @Override
            public void caretPositionChanged(InputMethodEvent event) {
                consumer.accept(event);
            }
        };
        
        component.addInputMethodListener(ml);
        
        Closeable cl = new Closeable() {
            Component cmpt = component;
            InputMethodListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeInputMethodListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="WindowFocusListener">
    /**
     * Подписка на события изменения фокуса окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onWindowGainedFocus( final Window component, final Consumer<WindowEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final WindowFocusListener ml = new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void windowLostFocus(WindowEvent e) {
            }
        };
        
        component.addWindowFocusListener(ml);
        
        Closeable cl = new Closeable() {
            Window cmpt = component;
            WindowFocusListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeWindowFocusListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события изменения фокуса окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onWindowLostFocus( final Window component, final Consumer<WindowEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final WindowFocusListener ml = new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
            }
            
            @Override
            public void windowLostFocus(WindowEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addWindowFocusListener(ml);
        
        Closeable cl = new Closeable() {
            Window cmpt = component;
            WindowFocusListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeWindowFocusListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="WindowListener">
    /**
     * Подписка на события открытия окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onWindowOpened( final Window component, final Consumer<WindowEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final WindowListener ml = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
            }
            
            @Override
            public void windowClosed(WindowEvent e) {
            }
            
            @Override
            public void windowIconified(WindowEvent e) {
            }
            
            @Override
            public void windowDeiconified(WindowEvent e) {
            }
            
            @Override
            public void windowActivated(WindowEvent e) {
            }
            
            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        };
        
        component.addWindowListener(ml);
        
        Closeable cl = new Closeable() {
            Window cmpt = component;
            WindowListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeWindowListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события закрытия окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onWindowClosing( final Window component, final Consumer<WindowEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final WindowListener ml = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void windowClosed(WindowEvent e) {
            }
            
            @Override
            public void windowIconified(WindowEvent e) {
            }
            
            @Override
            public void windowDeiconified(WindowEvent e) {
            }
            
            @Override
            public void windowActivated(WindowEvent e) {
            }
            
            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        };
        
        component.addWindowListener(ml);
        
        Closeable cl = new Closeable() {
            Window cmpt = component;
            WindowListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeWindowListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события закрытия окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onWindowClosed( final Window component, final Consumer<WindowEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final WindowListener ml = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
            }
            
            @Override
            public void windowClosed(WindowEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void windowIconified(WindowEvent e) {
            }
            
            @Override
            public void windowDeiconified(WindowEvent e) {
            }
            
            @Override
            public void windowActivated(WindowEvent e) {
            }
            
            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        };
        
        component.addWindowListener(ml);
        
        Closeable cl = new Closeable() {
            Window cmpt = component;
            WindowListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeWindowListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события сворачивания окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onWindowIconified( final Window component, final Consumer<WindowEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final WindowListener ml = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
            }
            
            @Override
            public void windowClosed(WindowEvent e) {
            }
            
            @Override
            public void windowIconified(WindowEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void windowDeiconified(WindowEvent e) {
            }
            
            @Override
            public void windowActivated(WindowEvent e) {
            }
            
            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        };
        
        component.addWindowListener(ml);
        
        Closeable cl = new Closeable() {
            Window cmpt = component;
            WindowListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeWindowListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события восстановление из свернутого состояния окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onWindowDeiconified( final Window component, final Consumer<WindowEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final WindowListener ml = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
            }
            
            @Override
            public void windowClosed(WindowEvent e) {
            }
            
            @Override
            public void windowIconified(WindowEvent e) {
            }
            
            @Override
            public void windowDeiconified(WindowEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void windowActivated(WindowEvent e) {
            }
            
            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        };
        
        component.addWindowListener(ml);
        
        Closeable cl = new Closeable() {
            Window cmpt = component;
            WindowListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeWindowListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события изменения окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onWindowActivated( final Window component, final Consumer<WindowEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final WindowListener ml = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
            }
            
            @Override
            public void windowClosed(WindowEvent e) {
            }
            
            @Override
            public void windowIconified(WindowEvent e) {
            }
            
            @Override
            public void windowDeiconified(WindowEvent e) {
            }
            
            @Override
            public void windowActivated(WindowEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        };
        
        component.addWindowListener(ml);
        
        Closeable cl = new Closeable() {
            Window cmpt = component;
            WindowListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeWindowListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события изменения окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onWindowDeactivated( final Window component, final Consumer<WindowEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final WindowListener ml = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
            }
            
            @Override
            public void windowClosed(WindowEvent e) {
            }
            
            @Override
            public void windowIconified(WindowEvent e) {
            }
            
            @Override
            public void windowDeiconified(WindowEvent e) {
            }
            
            @Override
            public void windowActivated(WindowEvent e) {
            }
            
            @Override
            public void windowDeactivated(WindowEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addWindowListener(ml);
        
        Closeable cl = new Closeable() {
            Window cmpt = component;
            WindowListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeWindowListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="WindowStateListener">
    /**
     * Подписка на события изменения состояния окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onWindowStateChanged( final Window component, final Consumer<WindowEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final WindowStateListener ml = new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addWindowStateListener(ml);
        
        Closeable cl = new Closeable() {
            Window cmpt = component;
            WindowStateListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeWindowStateListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="MenuListener">
    /**
     * Подписка на события меню - выбор пункта меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMenuSelected( final JMenu component, final Consumer<MenuEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MenuListener ml = new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void menuDeselected(MenuEvent e) {
            }
            
            @Override
            public void menuCanceled(MenuEvent e) {
            }
        };
        
        component.addMenuListener(ml);
        
        Closeable cl = new Closeable() {
            JMenu cmpt = component;
            MenuListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeMenuListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события меню - отмена выбора пункта меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMenuDeselected( final JMenu component, final Consumer<MenuEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MenuListener ml = new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
            }
            
            @Override
            public void menuDeselected(MenuEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void menuCanceled(MenuEvent e) {
            }
        };
        
        component.addMenuListener(ml);
        
        Closeable cl = new Closeable() {
            JMenu cmpt = component;
            MenuListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeMenuListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события меню - отмена выбора пункта меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMenuCanceled( final JMenu component, final Consumer<MenuEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MenuListener ml = new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
            }
            
            @Override
            public void menuDeselected(MenuEvent e) {
            }
            
            @Override
            public void menuCanceled(MenuEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addMenuListener(ml);
        
        Closeable cl = new Closeable() {
            JMenu cmpt = component;
            MenuListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeMenuListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ItemListener">
    /**
     * Подписка на события изменения состояния кнопки
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onItemStateChanged( final AbstractButton component, final Consumer<ItemEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ItemListener ml = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addItemListener(ml);
        
        Closeable cl = new Closeable() {
            AbstractButton cmpt = component;
            ItemListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeItemListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события изменения состояния кнопки
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onItemStateChanged( final JComboBox component, final Consumer<ItemEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ItemListener ml = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addItemListener(ml);
        
        Closeable cl = new Closeable() {
            JComboBox cmpt = component;
            ItemListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeItemListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MenuDragMouseListener">
    /**
     * Подписка на события перемещения drag-and-drop пункта меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMenuDragMouseEntered( final JMenuItem component, final Consumer<MenuDragMouseEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MenuDragMouseListener ml = new MenuDragMouseListener() {
            @Override
            public void menuDragMouseEntered(MenuDragMouseEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void menuDragMouseExited(MenuDragMouseEvent e) {
            }
            
            @Override
            public void menuDragMouseDragged(MenuDragMouseEvent e) {
            }
            
            @Override
            public void menuDragMouseReleased(MenuDragMouseEvent e) {
            }
        };
        
        component.addMenuDragMouseListener(ml);
        
        Closeable cl = new Closeable() {
            JMenuItem cmpt = component;
            MenuDragMouseListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeMenuDragMouseListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события перемещения drag-and-drop пункта меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMenuDragMouseExited( final JMenuItem component, final Consumer<MenuDragMouseEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MenuDragMouseListener ml = new MenuDragMouseListener() {
            @Override
            public void menuDragMouseEntered(MenuDragMouseEvent e) {
            }
            
            @Override
            public void menuDragMouseExited(MenuDragMouseEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void menuDragMouseDragged(MenuDragMouseEvent e) {
            }
            
            @Override
            public void menuDragMouseReleased(MenuDragMouseEvent e) {
            }
        };
        
        component.addMenuDragMouseListener(ml);
        
        Closeable cl = new Closeable() {
            JMenuItem cmpt = component;
            MenuDragMouseListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeMenuDragMouseListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события перемещения drag-and-drop пункта меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMenuDragMouseDragged( final JMenuItem component, final Consumer<MenuDragMouseEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MenuDragMouseListener ml = new MenuDragMouseListener() {
            @Override
            public void menuDragMouseEntered(MenuDragMouseEvent e) {
            }
            
            @Override
            public void menuDragMouseExited(MenuDragMouseEvent e) {
            }
            
            @Override
            public void menuDragMouseDragged(MenuDragMouseEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void menuDragMouseReleased(MenuDragMouseEvent e) {
            }
        };
        
        component.addMenuDragMouseListener(ml);
        
        Closeable cl = new Closeable() {
            JMenuItem cmpt = component;
            MenuDragMouseListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeMenuDragMouseListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события перемещения drag-and-drop пункта меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMenuDragMouseReleased( final JMenuItem component, final Consumer<MenuDragMouseEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MenuDragMouseListener ml = new MenuDragMouseListener() {
            @Override
            public void menuDragMouseEntered(MenuDragMouseEvent e) {
            }
            
            @Override
            public void menuDragMouseExited(MenuDragMouseEvent e) {
            }
            
            @Override
            public void menuDragMouseDragged(MenuDragMouseEvent e) {
            }
            
            @Override
            public void menuDragMouseReleased(MenuDragMouseEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addMenuDragMouseListener(ml);
        
        Closeable cl = new Closeable() {
            JMenuItem cmpt = component;
            MenuDragMouseListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeMenuDragMouseListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="MenuKeyListener">
    /**
     * Подписка на события нажатия кнопок клавиатуры по отношеню к меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMenuKeyTyped( final JMenuItem component, final Consumer<MenuKeyEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MenuKeyListener ml = new MenuKeyListener() {
            @Override
            public void menuKeyTyped(MenuKeyEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void menuKeyPressed(MenuKeyEvent e) {
            }
            
            @Override
            public void menuKeyReleased(MenuKeyEvent e) {
            }
        };
        
        component.addMenuKeyListener(ml);
        
        Closeable cl = new Closeable() {
            JMenuItem cmpt = component;
            MenuKeyListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeMenuKeyListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события нажатия кнопок клавиатуры по отношеню к меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMenuKeyPressed( final JMenuItem component, final Consumer<MenuKeyEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MenuKeyListener ml = new MenuKeyListener() {
            @Override
            public void menuKeyTyped(MenuKeyEvent e) {
            }
            
            @Override
            public void menuKeyPressed(MenuKeyEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void menuKeyReleased(MenuKeyEvent e) {
            }
        };
        
        component.addMenuKeyListener(ml);
        
        Closeable cl = new Closeable() {
            JMenuItem cmpt = component;
            MenuKeyListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeMenuKeyListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события нажатия кнопок клавиатуры по отношеню к меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onMenuKeyReleased( final JMenuItem component, final Consumer<MenuKeyEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final MenuKeyListener ml = new MenuKeyListener() {
            @Override
            public void menuKeyTyped(MenuKeyEvent e) {
            }
            
            @Override
            public void menuKeyPressed(MenuKeyEvent e) {
            }
            
            @Override
            public void menuKeyReleased(MenuKeyEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addMenuKeyListener(ml);
        
        Closeable cl = new Closeable() {
            JMenuItem cmpt = component;
            MenuKeyListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeMenuKeyListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TreeExpansionListener">
    /**
     * Подписка на события разворачивания поддерева
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onTreeExpanded( final JTree component, final Consumer<TreeExpansionEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final TreeExpansionListener ml = new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                consumer.accept(event);
            }
            
            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
            }
        };
        
        component.addTreeExpansionListener(ml);
        
        Closeable cl = new Closeable() {
            JTree cmpt = component;
            TreeExpansionListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeTreeExpansionListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события сворачивания поддерева
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onTreeCollapsed( final JTree component, final Consumer<TreeExpansionEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final TreeExpansionListener ml = new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
            }
            
            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                consumer.accept(event);
            }
        };
        
        component.addTreeExpansionListener(ml);
        
        Closeable cl = new Closeable() {
            JTree cmpt = component;
            TreeExpansionListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeTreeExpansionListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TreeSelectionListener">
    /**
     * Подписка на события изменения узла дерева
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onValueChanged( final JTree component, final Consumer<TreeSelectionEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final TreeSelectionListener ml = new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addTreeSelectionListener(ml);
        
        Closeable cl = new Closeable() {
            JTree cmpt = component;
            TreeSelectionListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeTreeSelectionListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="TreeWillExpandListener">
    /**
     * Подписка на события раскрытися узла дерева
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onTreeWillExpand( final JTree component, final Consumer<TreeExpansionEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final TreeWillExpandListener ml = new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                consumer.accept(event);
            }
            
            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
            }
        };
        
        component.addTreeWillExpandListener(ml);
        
        Closeable cl = new Closeable() {
            JTree cmpt = component;
            TreeWillExpandListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeTreeWillExpandListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    
    /**
     * Подписка на события сворачивания узла дерева
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onTreeWillCollapse( final JTree component, final Consumer<TreeExpansionEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final TreeWillExpandListener ml = new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            }
            
            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                consumer.accept(event);
            }
        };
        
        component.addTreeWillExpandListener(ml);
        
        Closeable cl = new Closeable() {
            JTree cmpt = component;
            TreeWillExpandListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeTreeWillExpandListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="AdjustmentListener">
    /**
     * Подписка на события скороллирования
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onAdjustmentValueChanged( final JScrollBar component, final Consumer<AdjustmentEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final AdjustmentListener ml = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addAdjustmentListener(ml);
        
        Closeable cl = new Closeable() {
            JScrollBar cmpt = component;
            AdjustmentListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeAdjustmentListener(l);
                    cmpt = null;
                    l = null;
                }
            }};
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="PopupMenuListener">
    /**
     * Подписка на события всплывающего меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onPopupMenuWillBecomeVisible( final JPopupMenu component, final Consumer<PopupMenuEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final PopupMenuListener ml = new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        };
        
        component.addPopupMenuListener(ml);
        
        Closeable cl = new Closeable() {
            JPopupMenu cmpt = component;
            PopupMenuListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removePopupMenuListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    
    /**
     * Подписка на события всплывающего меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onPopupMenuWillBecomeInvisible( final JPopupMenu component, final Consumer<PopupMenuEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final PopupMenuListener ml = new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        };
        
        component.addPopupMenuListener(ml);
        
        Closeable cl = new Closeable() {
            JPopupMenu cmpt = component;
            PopupMenuListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removePopupMenuListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    
    /**
     * Подписка на события всплывающего меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onPopupMenuCanceled( final JPopupMenu component, final Consumer<PopupMenuEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final PopupMenuListener ml = new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addPopupMenuListener(ml);
        
        Closeable cl = new Closeable() {
            JPopupMenu cmpt = component;
            PopupMenuListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removePopupMenuListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }

    /**
     * Подписка на события всплывающего меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onPopupMenuWillBecomeVisible( final JComboBox component, final Consumer<PopupMenuEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final PopupMenuListener ml = new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        };
        
        component.addPopupMenuListener(ml);
        
        Closeable cl = new Closeable() {
            JComboBox cmpt = component;
            PopupMenuListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removePopupMenuListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    
    /**
     * Подписка на события всплывающего меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onPopupMenuWillBecomeInvisible( final JComboBox component, final Consumer<PopupMenuEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final PopupMenuListener ml = new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        };
        
        component.addPopupMenuListener(ml);
        
        Closeable cl = new Closeable() {
            JComboBox cmpt = component;
            PopupMenuListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removePopupMenuListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    
    /**
     * Подписка на события всплывающего меню
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onPopupMenuCanceled( final JComboBox component, final Consumer<PopupMenuEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final PopupMenuListener ml = new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addPopupMenuListener(ml);
        
        Closeable cl = new Closeable() {
            JComboBox cmpt = component;
            PopupMenuListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removePopupMenuListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ListSelectionListener">
    /**
     * Подписка на события изменения (выбора) из списка
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onValueChanged( final JList component, final Consumer<ListSelectionEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ListSelectionListener ml = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addListSelectionListener(ml);
        
        Closeable cl = new Closeable() {
            JList cmpt = component;
            ListSelectionListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeListSelectionListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="InternalFrameListener">
    /**
     * Подписка на события изменения внутреннего фрейма/окна - открытие окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onInternalFrameOpened( final JInternalFrame component, final Consumer<InternalFrameEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final InternalFrameListener ml = new InternalFrameListener() {
            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
            }
        };
        
        component.addInternalFrameListener(ml);
        
        Closeable cl = new Closeable() {
            JInternalFrame cmpt = component;
            InternalFrameListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeInternalFrameListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    
    /**
     * Подписка на события изменения внутреннего фрейма/окна - закрытие окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onInternalFrameClosing( final JInternalFrame component, final Consumer<InternalFrameEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final InternalFrameListener ml = new InternalFrameListener() {
            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
            }
        };
        
        component.addInternalFrameListener(ml);
        
        Closeable cl = new Closeable() {
            JInternalFrame cmpt = component;
            InternalFrameListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeInternalFrameListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    
    /**
     * Подписка на события изменения внутреннего фрейма/окна - закрытия окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onInternalFrameClosed( final JInternalFrame component, final Consumer<InternalFrameEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final InternalFrameListener ml = new InternalFrameListener() {
            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
            }
        };
        
        component.addInternalFrameListener(ml);
        
        Closeable cl = new Closeable() {
            JInternalFrame cmpt = component;
            InternalFrameListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeInternalFrameListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    
    /**
     * Подписка на события изменения внутреннего фрейма/окна - сворачивание окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onInternalFrameIconified( final JInternalFrame component, final Consumer<InternalFrameEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final InternalFrameListener ml = new InternalFrameListener() {
            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
            }
        };
        
        component.addInternalFrameListener(ml);
        
        Closeable cl = new Closeable() {
            JInternalFrame cmpt = component;
            InternalFrameListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeInternalFrameListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    
    /**
     * Подписка на события изменения внутреннего фрейма/окна - восстановление окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onInternalFrameDeiconified( final JInternalFrame component, final Consumer<InternalFrameEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final InternalFrameListener ml = new InternalFrameListener() {
            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
            }
        };
        
        component.addInternalFrameListener(ml);
        
        Closeable cl = new Closeable() {
            JInternalFrame cmpt = component;
            InternalFrameListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeInternalFrameListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    
    /**
     * Подписка на события изменения внутреннего фрейма/окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onInternalFrameActivated( final JInternalFrame component, final Consumer<InternalFrameEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final InternalFrameListener ml = new InternalFrameListener() {
            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
            }
        };
        
        component.addInternalFrameListener(ml);
        
        Closeable cl = new Closeable() {
            JInternalFrame cmpt = component;
            InternalFrameListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeInternalFrameListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    
    /**
     * Подписка на события изменения внутреннего фрейма/окна
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onInternalFrameDeactivated( final JInternalFrame component, final Consumer<InternalFrameEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final InternalFrameListener ml = new InternalFrameListener() {
            @Override
            public void internalFrameOpened(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
            }
            
            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addInternalFrameListener(ml);
        
        Closeable cl = new Closeable() {
            JInternalFrame cmpt = component;
            InternalFrameListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeInternalFrameListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="HyperlinkListener">
    /**
     * Подписка на события изменения гиппер ссылки
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onHyperlinkUpdate( final JEditorPane component, final Consumer<HyperlinkEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final HyperlinkListener ml = new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addHyperlinkListener(ml);
        
        Closeable cl = new Closeable() {
            JEditorPane cmpt = component;
            HyperlinkListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeHyperlinkListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ListDataListener">
    /**
     * Подписка на события добавления интервала в модели (строк/колонок/...)
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onIntervalAdded( final ListModel component, final Consumer<ListDataEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ListDataListener ml = new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void intervalRemoved(ListDataEvent e) {
            }
            
            @Override
            public void contentsChanged(ListDataEvent e) {
            }
        };
        
        component.addListDataListener(ml);
        
        Closeable cl = new Closeable() {
            ListModel cmpt = component;
            ListDataListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeListDataListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    
    /**
     * Подписка на события удаления интервала в модели (строк/колонок/...)
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onIntervalRemoved( final ListModel component, final Consumer<ListDataEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ListDataListener ml = new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
            }
            
            @Override
            public void intervalRemoved(ListDataEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void contentsChanged(ListDataEvent e) {
            }
        };
        
        component.addListDataListener(ml);
        
        Closeable cl = new Closeable() {
            ListModel cmpt = component;
            ListDataListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeListDataListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    
    /**
     * Подписка на события изменения интервала в модели (строк/колонок/...)
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onContentsChanged( final ListModel component, final Consumer<ListDataEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ListDataListener ml = new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
            }
            
            @Override
            public void intervalRemoved(ListDataEvent e) {
            }
            
            @Override
            public void contentsChanged(ListDataEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addListDataListener(ml);
        
        Closeable cl = new Closeable() {
            ListModel cmpt = component;
            ListDataListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeListDataListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ListSelectionListener">
    /**
     * Подписка на события изменения в модели выбранных строк/колонок/...
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onValueChanged( final ListSelectionModel component, final Consumer<ListSelectionEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final ListSelectionListener ml = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addListSelectionListener(ml);
        
        Closeable cl = new Closeable() {
            ListSelectionModel cmpt = component;
            ListSelectionListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeListSelectionListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CellEditorListener">
    /**
     * Подписка на события изменения табличного редактора ячеек - завершение редактирования
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onEditingStopped( final CellEditor component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final CellEditorListener ml = new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                consumer.accept(e);
            }
            
            @Override
            public void editingCanceled(ChangeEvent e) {
            }
        };
        
        component.addCellEditorListener(ml);
        
        Closeable cl = new Closeable() {
            CellEditor cmpt = component;
            CellEditorListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeCellEditorListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    
    /**
     * Подписка на события изменения табличного редактора ячеек - отмена изменений
     * @param component компонент
     * @param consumer подписчик
     * @return Отписка от уведомлений
     */
    public static Closeable onEditingCanceled( final CellEditor component, final Consumer<ChangeEvent> consumer ){
        if( component==null )throw new IllegalArgumentException( "component==null" );
        if( consumer==null )throw new IllegalArgumentException( "consumer==null" );
        
        final CellEditorListener ml = new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
            }
            
            @Override
            public void editingCanceled(ChangeEvent e) {
                consumer.accept(e);
            }
        };
        
        component.addCellEditorListener(ml);
        
        Closeable cl = new Closeable() {
            CellEditor cmpt = component;
            CellEditorListener l = ml;
            @Override
            public void close() throws IOException {
                if(cmpt!=null && l!=null ){
                    cmpt.removeCellEditorListener(l);
                    cmpt = null;
                    l = null;
                }
            }
        };
        
        return cl;
    }
    //</editor-fold>
}
