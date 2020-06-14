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


import java.sql.SQLWarning;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Текстовое сообщение генерируемое SQL сервером (JDBC)
 * @author nt.gocha@gmail.com
 */
public class Message {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(Message.class.getName());
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
        logger.entering(Message.class.getName(), method, params);
    }
    
    private static void logExiting( String method){
        logger.exiting(Message.class.getName(), method);
    }
    
    private static void logExiting( String method, Object result){
        logger.exiting(Message.class.getName(), method, result);
    }
    //</editor-fold>
    
    /**
     * Конструктор
     */
    public Message(){
    }

    /**
     * Конструктор копирования
     * @param warning образец для копирования
     */
    public Message( SQLWarning warning){
        if( warning==null )throw new IllegalArgumentException("warning == null");
        message = warning.getMessage();
        localizedMessage = warning.getLocalizedMessage();
        code = warning.getErrorCode();
        state = warning.getSQLState();
        date = new Date();
        threadId = Thread.currentThread().getId();
    }
    
    /**
     * Конструктор копирования
     * @param sample образец
     */
    public Message( Message sample){
        if( sample != null ){
            message = sample.message;
            localizedMessage = sample.localizedMessage;
            code = sample.code;
            state = sample.state;
            date = sample.date;
            threadId = sample.threadId;
            scn = sample.scn;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="hashCode / equals">
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.message);
        hash = 47 * hash + Objects.hashCode(this.localizedMessage);
        hash = 47 * hash + this.code;
        hash = 47 * hash + Objects.hashCode(this.state);
        hash = 47 * hash + Objects.hashCode(this.date);
        hash = 47 * hash + Objects.hashCode(this.threadId);
        return hash;
    }
    
    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Message other = (Message) obj;
        if (this.code != other.code) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        if (!Objects.equals(this.localizedMessage, other.localizedMessage)) {
            return false;
        }
        if (!Objects.equals(this.state, other.state)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.threadId, other.threadId)) {
            return false;
        }
        return true;
    }
//</editor-fold>
    
    /**
     * Клонирование
     * @return клон
     */
    @Override
    public Message clone(){
        return new Message(this);
    }
    
    protected String message;
    /**
     * Возвращает текст сообщения
     * @return текст сообщения
     */
    public String getMessage() { return message; }
    /**
     * Указывает текст сообщения
     * @param message текст сообщения
     */
    public void setMessage( String message) { this.message = message; }
    /**
     * Указывает текст сообщения
     * @param message текст сообщения
     * @return self ссылка
     */
    public Message message( String message) { this.message = message; return this; }
    
    protected String localizedMessage;
    /**
     * Возвращает локализованное сообщение
     * @return локализованное сообщение
     */
    public String getLocalizedMessage() { return localizedMessage; }
    /**
     * Указывает локализованное сообщение
     * @param localizedMessage локализованное сообщение
     */
    public void setLocalizedMessage( String localizedMessage) { this.localizedMessage = localizedMessage; }
    /**
     * Указывает локализованное сообщение
     * @param localizedMessage локализованное сообщение
     * @return self ссылка
     */
    public Message localizedMessage( String localizedMessage) { this.localizedMessage = localizedMessage; return this; }
    
    protected int code;
    /**
     * Возвращает код сообщения
     * @return код сообщения
     */
    public int getCode() { return code; }
    /**
     * Указывает код сообщения
     * @param code код сообщения 
     */
    public void setCode(int code) { this.code = code; }
    /**
     * Указывает код сообщения
     * @param code код сообщения
     * @return self ссылка
     */
    public Message code( int code) { this.code = code; return this; }
    
    protected long scn;
    /**
     * Возвращает SCN номер изменения
     * @return SCN номер изменения
     */
    public long getScn() { return scn; }
    /**
     * Указывает SCN номер изменения
     * @param scn SCN номер изменения
     */
    public void setScn(long scn) { this.scn = scn; }
    /**
     * Указывает SCN номер изменения
     * @param scn SCN номер изменения
     * @return self ссылка
     */
    public Message scn( long scn) { this.scn = scn; return this; }
    
    protected String state;
    /**
     * Возвращает имя состояния сообщения
     * @return имя состояния сообщения
     */
    public String getState() { return state; }
    /**
     * Указывает имя состояния сообщения
     * @param state имя состояния сообщения
     */
    public void setState( String state) { this.state = state; }
    /**
     * Указывает имя состояния сообщения
     * @param state имя состояния сообщения
     * @return self ссылка
     */
    public Message state( String state) { this.state = state; return this; }
    
    protected Date date;
    /**
     * Возвращает дата/время сообещния
     * @return дата/время сообещния
     */
    public Date getDate() { return date; }
    /**
     * Указывает дата/время сообещния
     * @param date дата/время сообещния
     */
    public void setDate( Date date) { this.date = date; }
    /**
     * Указывает дата/время сообещния
     * @param date дата/время сообещния
     * @return self ссылка
     */
    public Message date( Date date) { this.date = date; return this; }
    
    protected Long threadId;
    /**
     * Возвращает идентификатор трэда ОС
     * @return идентификатор трэда ОС
     */
    public Long getThreadId() { return threadId; }
    /**
     * Указывает идентификатор трэда ОС
     * @param threadId идентификатор трэда ОС
     */
    public void setThreadId( Long threadId) { this.threadId = threadId; }
    /**
     * Указывает идентификатор трэда ОС
     * @param threadId идентификатор трэда ОС
     * @return self ссылка
     */
    public Message threadId( Long threadId) { this.threadId = threadId; return this; }
}
