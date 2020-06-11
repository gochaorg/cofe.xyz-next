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

package xyz.cofe.data.store;

import xyz.cofe.data.DataRow;
import xyz.cofe.data.DataTable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Специальные колонки
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public abstract class CSVSpecialColumn {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(CSVSpecialColumn.class.getName());
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
        logger.entering(CSVSpecialColumn.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(CSVSpecialColumn.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(CSVSpecialColumn.class.getName(), method, result);
    }
    //</editor-fold>
    
    /**
     * Возвращает имя колонки
     * @return имя колонки
     */
    public abstract String getName();
    
    /**
     * Возвращает специальное значение строки
     * @param dr Строка для которой запрашивается спец значение
     * @return Строка
     */
    public abstract String asString( DataRow dr );
    
    /**
     * Парсинг спец значения
     * @param dt Таблица для которой производится парсинг
     * @param value Текстовое значение
     */
    public abstract void parse( DataTable dt, String value );
    
    /**
     * Пишет состояние строки
     */
    public static class RowStateColumn extends CSVSpecialColumn {
        public RowStateColumn(){
        }
        
        public RowStateColumn(String name){
            this.name = name;
        }
        
        //<editor-fold defaultstate="collapsed" desc="name : String">
        protected String name = "#RowState";
        
        @Override
        public String getName() {
            if( name==null )return "#RowState";
            return name;
        }
        
        public void setName( String name ){
            this.name = name;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="asString">
        @Override
        public String asString( DataRow dr) {
            if( dr == null )return "";
            if( dr.isInserted() ){
                return "inserted";
            }else if( dr.isDeleted() ){
                return "deleted";
            }else if( dr.isUpdated() ){
                return "updated";
            }
            return "";
        }
        //</editor-fold>

        @Override
        public void parse( DataTable dt, String value) {
        }
    }
}
