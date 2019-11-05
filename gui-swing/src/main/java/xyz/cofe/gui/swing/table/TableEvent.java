/*
 * The MIT License
 *
 * Copyright 2018 user.
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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Событие таблицы
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TableEvent {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TableEvent.class.getName());
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
        logger.entering(TableEvent.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TableEvent.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TableEvent.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор
     * @param tbl таблица
     */
    public TableEvent(Table tbl){
        this.table = tbl;
    }

    //<editor-fold defaultstate="collapsed" desc="table : Table">
    protected Table table;

    /**
     * Возвращает ссылку на таблицу
     * @return таблица
     */
    public Table getTable() {
        return table;
    }
    //</editor-fold>

    /**
     * Событие изменнения фокуса
     */
    public static class FocusedRowChanged extends TableEvent {
        /**
         * Конструктор
         * @param tbl таблица
         * @param oldRow предыдущая строка содержащаяя фокус
         * @param curRow текущая строка содержащаяя фокус
         */
        public FocusedRowChanged(Table tbl,int oldRow, int curRow) {
            super(tbl);
            this.oldRow = oldRow;
            this.currentRow = curRow;
        }

        /**
         * Конструктор копирования
         * @param sample образец
         */
        public FocusedRowChanged(FocusedRowChanged sample) {
            super(sample.table);
            this.oldRow = sample.oldRow;
            this.currentRow = sample.currentRow;
        }

        protected int oldRow;
        protected int currentRow;

        /**
         * Возвращает предыдущую строку содержащую фокус
         * @return предыдущая строка содержащаяя фокус
         */
        public int getOldRow() {
            return oldRow;
        }

        /**
         * Возвращает текущуюю строку содержащую фокус
         * @return текущая строка содержащаяя фокус
         */
        public int getCurrentRow() {
            return currentRow;
        }
    }
}
