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

/**
 * Элемент стека трейса исключения
 * @author nt.gocha@gmail.com
 */
public class ErrStackElement {
    /**
     * Конструктор по умолчанию
     */
    public ErrStackElement(){
    }
    /**
     * Конструктор копирования
     * @param se образец для копирования
     */
    public ErrStackElement( StackTraceElement se){
        if( se!=null ){
            className = se.getClassName();
            methodName = se.getMethodName();
            
            fileName = se.getFileName();
            lineNumber = se.getLineNumber();
            
            nativeMethod = se.isNativeMethod();
        }
    }
    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public ErrStackElement( ErrStackElement sample){
        if( sample!=null ){
            className = sample.className;
            methodName = sample.methodName;
            fileName = sample.fileName;
            lineNumber = sample.lineNumber;
            nativeMethod = sample.nativeMethod;
        }
    }
    /**
     * Клонирование
     * @return клон
     */
    @Override
    public ErrStackElement clone(){
        return new ErrStackElement(this);
    }
    
    //<editor-fold defaultstate="collapsed" desc="className : String">
    protected String className;
    
    /**
     * Указывает имя класса
     * @return имя класса
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * Указывает имя класса
     * @param className имя класса
     */
    public void setClassName( String className) {
        this.className = className;
    }
    
    /**
     * Указывает имя класса
     * @param className имя класса
     * @return self ссылка
     */
    public ErrStackElement className( String className) {
        this.className = className;
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="methodName : String">
    protected String methodName;
    
    /**
     * Указывает имя метода
     * @return имя метода
     */
    public String getMethodName() {
        return methodName;
    }
    
    /**
     * Указывает имя метода
     * @param methodName имя метода
     */
    public void setMethodName( String methodName) {
        this.methodName = methodName;
    }
    
    /**
     * Указывает имя метода
     * @param methodName имя метода
     * @return self ссылка
     */
    public ErrStackElement methodName( String methodName) {
        this.methodName = methodName;
        return this;
    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="fileName : String">
    protected String fileName;
    
    /**
     * Указывает имя файла
     * @return имя файла
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Указывает имя файла
     * @param fileName имя файла
     */
    public void setFileName( String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Указывает имя файла
     * @param fileName имя файла
     * @return self ссылка
     */
    public ErrStackElement fileName( String fileName) {
        this.fileName = fileName;
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="lineNumber : int">
    protected int lineNumber;
    
    /**
     * Указывает номер строки
     * @return номер строки
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /**
     * Указывает номер строки
     * @param lineNumber номер строки
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    /**
     * Указывает номер строки
     * @param lineNumber номер строки
     * @return self ссылка
     */
    public ErrStackElement lineNumber( int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="nativeMethod : boolean">
    protected boolean nativeMethod;
    
    /**
     * Указыает нативный метод
     * @return true - нативный / false - java метод
     */
    public boolean isNativeMethod() {
        return nativeMethod;
    }
    
    /**
     * Указыает нативный метод
     * @param nativeMethod true - нативный / false - java метод
     */
    public void setNativeMethod(boolean nativeMethod) {
        this.nativeMethod = nativeMethod;
    }
    
    /**
     * Указыает нативный метод
     * @param nativeMethod true - нативный / false - java метод
     * @return self ссылка
     */
    public ErrStackElement nativeMethod( boolean nativeMethod) {
        this.nativeMethod = nativeMethod;
        return this;
    }
    //</editor-fold>
}
