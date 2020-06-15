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

import java.sql.SQLException;

/**
 * Ошибка работы с данными
 * @author nt.gocha@gmail.com
 */
public class Err {
    /**
     * Конструктор
     */
    public Err(){
    }
    /**
     * Конструктор копирования.
     * 
     * <p>
     * по умолчанию копируются 50 эл стека для 10 вложенных ошибок включительно
     * @param err образец для копирования
     */
    public Err( Throwable err){
        this(err,50,10);
    }
    /**
     * Конструктор копирования
     * @param err образец для копирования
     * @param stackTraceTop макс кол-во элементов стека
     * @param causeTop макс кол-во вложенных объектов
     */
    public Err( Throwable err, int stackTraceTop, int causeTop){
        if( err!=null ){
            errorClass = err.getClass().getName();
            message = err.getMessage();
            localizedMessage = err.getLocalizedMessage();
            
            if( err instanceof SQLException ){
                SQLException sqlerr = (SQLException)err;
                sqlErrorCode = sqlerr.getErrorCode();
            }

            if( stackTraceTop>0 ){
                StackTraceElement[] stackels = err.getStackTrace();
                if( stackels!=null ){
                    stack = new ErrStackElement[Math.min(stackTraceTop, stackels.length)];
                    for( int ei=0; ei<stack.length; ei++ ){
                        stack[ei] = new ErrStackElement(stackels[ei]);
                    }
                }
            }
            
            if( causeTop>0 ){
                Throwable ecause = err.getCause();
                if( ecause!=null ){
                    cause = new Err(ecause, 0, causeTop-1);
                }
            }
        }
    }
    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public Err( Err sample ){
        if( sample!=null ){
            message = sample.message;
            localizedMessage = sample.localizedMessage;
            errorClass = sample.errorClass;
            sqlErrorCode = sample.sqlErrorCode;
            
            if( sample.stack!=null ){
                stack = new ErrStackElement[sample.stack.length];
                for( int si=0; si<stack.length; si++ ){
                    stack[si] = sample.stack[si].clone();
                }
            }
            
            if( sample.cause!=null ){
                cause = sample.clone();
            }
        }
    }
    
    /**
     * Клонирование
     * @return клон
     */
    @Override
    public Err clone(){
        return new Err(this);
    }
    
    //<editor-fold defaultstate="collapsed" desc="cause">
    protected Err cause;
    
    /**
     * Возвращает вложенную ошибку
     * @return вложенная ошибка или null
     */
    public Err getCause() {
        return cause;
    }
    
    /**
     * Указывает вложенную ошибку
     * @param cause вложенная ошибка или null
     */
    public void setCause( Err cause) {
        this.cause = cause;
    }
    
    /**
     * Указывает вложенную ошибку
     * @param cause вложенная ошибка или null
     * @return self ссылка
     */
    public Err cause( Err cause) {
        this.cause = cause;
        return this;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="stack">
    protected ErrStackElement[] stack;
    /**
     * Возвращает стек трейсинга вызова
     * @return стек ошибок
     */
    public ErrStackElement[] getStack() { return stack; }
    /**
     * Указывает стек трейсинга вызова
     * @param stack стек трейсинга вызова
     */
    public void setStack(ErrStackElement[] stack) { this.stack = stack; }
    /**
     * Указывает стек трейсинга вызова
     * @param stack стек трейсинга вызова
     * @return self ссылка
     */
    public Err stack( ErrStackElement[] stack) { this.stack = stack; return this; }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="message/localizedMessage/errorClass/sqlErrorCode">
    protected String message;
    protected String localizedMessage;
    protected String errorClass;
    protected Integer sqlErrorCode;
    
    /**
     * Возвращает текст сообщения о ошибке
     * @return текст сообщения о ошибке
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Указывает текст сообщения о ошибке
     * @param message текст сообщения о ошибке
     */
    public void setMessage( String message) {
        this.message = message;
    }
    
    /**
     * Указывает текст сообщения о ошибке
     * @param message текст сообщения о ошибке
     * @return self ссылка
     */
    public Err message( String message) {
        this.message = message;
        return this;
    }
    
    /**
     * Возвращает текст сообщения о ошибке
     * @return текст сообщения о ошибке
     */
    public String getLocalizedMessage() {
        return localizedMessage!=null ? localizedMessage : getMessage();
    }
    
    /**
     * Указывает текст сообщения о ошибке
     * @param localizedMessage текст сообщения о ошибке
     */
    public void setLocalizedMessage( String localizedMessage) {
        this.localizedMessage = localizedMessage;
    }
    
    /**
     * Указывает текст сообщения о ошибке
     * @param localizedMessage текст сообщения о ошибке
     * @return self ссылка
     */
    public Err localizedMessage( String localizedMessage) {
        this.localizedMessage = localizedMessage;
        return this;
    }
    
    /**
     * Возвращает имя класса ошибки
     * @return имя класса ошибки
     */
    public String getErrorClass() {
        return errorClass;
    }
    
    /**
     * Указывает имя класса ошибки
     * @param errorClass имя класса ошибки
     */
    public void setErrorClass( String errorClass) {
        this.errorClass = errorClass;
    }
    
    /**
     * Указывает имя класса ошибки
     * @param errorClass имя класса ошибки
     * @return self ссылка
     */
    public Err errorClass( String errorClass) {
        this.errorClass = errorClass;
        return this;
    }
    
    /**
     * Возвращает SQL код ошибки
     * @return SQL код ошибки или null
     */
    public Integer getSqlErrorCode() {
        return sqlErrorCode;
    }
    
    /**
     * Указывает SQL код ошибки
     * @param sqlErrorCode SQL код ошибки или null
     */
    public void setSqlErrorCode( Integer sqlErrorCode) {
        this.sqlErrorCode = sqlErrorCode;
    }
    
    /**
     * Указывает SQL код ошибки
     * @param sqlErrorCode SQL код ошибки или null
     * @return self ссылка
     */
    public Err sqlErrorCode( Integer sqlErrorCode) {
        this.sqlErrorCode = sqlErrorCode;
        return this;
    }
    //</editor-fold>
}
