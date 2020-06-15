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

package xyz.cofe.sql.stream;


import xyz.cofe.xml.FormatXMLWriter;
import xyz.cofe.xml.stream.path.XEventPath;

import javax.xml.stream.XMLStreamException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Отметка SCN записи в выборке SCN данных для синхронизации последовательности данных
 * @author nt.gocha@gmail.com
 */
public class ScnMark {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ScnMark.class.getName());
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

    private static void logFine( String message, Object... args){
        logger.log(Level.FINE, message, args);
    }
    
    private static void logFiner( String message, Object... args){
        logger.log(Level.FINER, message, args);
    }
    
    private static void logFinest( String message, Object... args){
        logger.log(Level.FINEST, message, args);
    }
    
    private static void logInfo( String message, Object... args){
        logger.log(Level.INFO, message, args);
    }

    private static void logWarning( String message, Object... args){
        logger.log(Level.WARNING, message, args);
    }
    
    private static void logSevere( String message, Object... args){
        logger.log(Level.SEVERE, message, args);
    }

    private static void logException( Throwable ex){
        logger.log(Level.SEVERE, null, ex);
    }

    private static void logEntering( String method, Object... params){
        logger.entering(ScnMark.class.getName(), method, params);
    }
    
    private static void logExiting( String method){
        logger.exiting(ScnMark.class.getName(), method);
    }
    
    private static void logExiting( String method, Object result){
        logger.exiting(ScnMark.class.getName(), method, result);
    }
    //</editor-fold>
    
    /**
     * Конструктор
     */
    public ScnMark(){        
    }

    /**
     * Конструктор
     * @param scn номер SCN
     * @param time время (System.currentTimeMillis)
     * @param threadId идентификатор трэда
     * @see System#currentTimeMillis()
     */
    public ScnMark( long scn, long time, long threadId){
        this.scn = scn;
        this.time = time;
        this.threadId = threadId;
    }
    
    /**
     * Конструктор копирования
     * @param sample образец копирования
     */
    public ScnMark( ScnMark sample ){
        if( sample!=null ){
            this.scn = sample.scn;
            this.time = sample.time;
            this.threadId = sample.threadId;
        }
    }
    
    /**
     * Клонирование
     * @return клон
     */
    @Override
    public ScnMark clone(){
        return new ScnMark(this);
    }
    
    /**
     * Попытка чтения отметки SCN из XML атрибутов (ti, scn, th)
     * @param path XML элемент
     * @return отметка SCN или null
     */
    public static ScnMark tryRead( XEventPath path){
        if( path==null )return null;
        
        Long ti = path.readAttributeAsLong("ti", null);
        Long scn = path.readAttributeAsLong("scn", null);
        Long th = path.readAttributeAsLong("th", null);
        if( ti==null || scn==null || th==null )return null;
        
        return new ScnMark(scn, ti, th);
    }

    protected long scn;

    /**
     * Возвращает номер SCN
     * @return номер SCN
     */
    public long getScn() {
        return scn;
    }

    /**
     * Указывает номер SCN
     * @param scn номер SCN 
     */
    public void setScn(long scn) { this.scn = scn; }
    
    /**
     * Указывает номер SCN
     * @param scn номер SCN 
     * @return self ссылка
     */
    public ScnMark scn( long scn) {
        this.scn = scn;
        return this;
    }
    
    protected long time;
    /**
     * Указывает время наступления события
     * @return время
     * @see System#currentTimeMillis()
     */
    public long getTime() { return time; }
    /**
     * Указывает время наступления события
     * @param time время
     * @see System#currentTimeMillis()
     */
    public void setTime(long time) { this.time = time; }
    /**
     * Указывает время наступления события
     * @param time время
     * @return self ссылка
     * @see System#currentTimeMillis()
     */
    public ScnMark time( long time) { this.time = time; return this; }
    
    protected long threadId;
    /**
     * Указывает идентификатор трэда ОС
     * @return идентификатор трэда ОС
     */
    public long getThreadId() { return threadId; }
    /**
     * Указывает идентификатор трэда ОС
     * @param threadId идентификатор трэда ОС
     */
    public void setThreadId(long threadId) { this.threadId = threadId; }
    /**
     * Указывает идентификатор трэда ОС
     * @param threadId идентификатор трэда ОС
     * @return self ссылка
     */
    public ScnMark threadId( long threadId) { this.threadId = threadId; return this; }
    
    /**
     * Добавляет атрибуты (scn, ti, th)
     * @param out XML поток
     * @throws XMLStreamException Ошибка I/O
     */
    public void write( FormatXMLWriter out) throws XMLStreamException{
        if( out==null )throw new IllegalArgumentException("out==null");
        
        out.writeAttribute("scn", Long.toString(scn));
        out.writeAttribute("ti", Long.toString(time));
        out.writeAttribute("th", Long.toString(threadId));
    }
}
