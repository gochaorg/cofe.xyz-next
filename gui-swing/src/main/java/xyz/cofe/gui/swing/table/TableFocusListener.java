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

package xyz.cofe.gui.swing.table;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import xyz.cofe.ecolls.Closeables;

/**
 * Отслеживаение фокуса в таблице при помощи событий ListSelectionListener
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class TableFocusListener
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TableFocusListener.class.getName());
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
        logger.entering(TableFocusListener.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TableFocusListener.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TableFocusListener.class.getName(), method, result);
    }
    //</editor-fold>

    protected final Object sync;

    public TableFocusListener(){
        sync = this;
    }

    public TableFocusListener(JTable table,boolean start){
        sync = this;
        this.table = table;
        if( start )start();
    }

    protected JTable table;

    public JTable getTable() {
        synchronized( sync ){
            return table;
        }
    }

    public void setTable(JTable table) {
        synchronized( sync ){ this.table = table; }
    }

    protected final Closeables tableListeners = new Closeables();
    protected int focusedRow = -1;

    protected void listen(){
        tableListeners.close();
        if( this.table==null )return;

        final JTable table = this.table;
        final ListSelectionModel listSelModel = table.getSelectionModel();

        focusedRow = listSelModel.getLeadSelectionIndex();

        final ListSelectionListener listSelectListener =
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent lse) {
                    int curFocusedRow = listSelModel.getLeadSelectionIndex();
                    if( curFocusedRow!=focusedRow ){
                        int oldFocRow = focusedRow;
                        focusedRow = curFocusedRow;
                        onFocusedRowChanged(table, oldFocRow, focusedRow);
                    }
                }
            };

        table.getSelectionModel().addListSelectionListener(listSelectListener);

        Closeable cl = new Closeable() {
            @Override
            public void close() throws IOException {
                listSelModel.removeListSelectionListener(listSelectListener);
            }
        };

        tableListeners.add(cl);
    }

    protected void onFocusedRowChanged( JTable table, int oldRow, int curRow ){
    }

    public void start(){
        synchronized( sync ){
            if( table==null )throw new IllegalStateException("table == null");
            listen();
        }
    }

    public void stop(){
        synchronized( sync ){
            tableListeners.close();
        }
    }

    public boolean isRunning(){
        synchronized( sync ){
            Object[] listeners = tableListeners.getCloseables();
            return listeners!=null && listeners.length>0;
        }
    }
}
