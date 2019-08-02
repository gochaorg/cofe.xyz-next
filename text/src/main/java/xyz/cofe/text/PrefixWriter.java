/*
 * The MIT License
 *
 * Copyright 2016 Kamnev Georgiy <nt.gocha@gmail.com>.
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

package xyz.cofe.text;

import java.io.IOException;
import java.io.Writer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Пишет префикс в начале каждой строки
 * @author nt.gocha@gmail.com
 */
public class PrefixWriter extends EndLineReWriter
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(PrefixWriter.class.getName());
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
    //</editor-fold>

    public PrefixWriter(Writer writer) {
        super(writer);
    }

    public PrefixWriter(Writer writer, String endl) {
        super(writer, endl);
    }

    public PrefixWriter(Writer writer, EndLine endl) {
        super(writer, endl);
    }

    protected Supplier<String> linePrefix;

    /**
     * Указывает функцию префиса строки
     * @param fn функция префикса строки
     */
    public synchronized void setLinePrefixFn( Supplier<String> fn ){
        linePrefix = fn;

        assignLinePrefixFn();
    }

    /**
     * Возвращает функцию префикса строки
     * @return функцию префикса строки
     */
    public synchronized Supplier<String> getLinePrefixFn(){
        return linePrefix;
    }

    /**
     * Возвращает префикс строки
     * @return префикс строки
     */
    public synchronized String getLinePrefix(){
        Supplier<String> lPrefix = linePrefix;
        if( lPrefix==null )return null;

        try{
            String prefix = lPrefix.get();
            return prefix;
        }catch( Throwable err ){
            logException(err);
        }

        return null;
    }

    /**
     * Указывает префикс строки
     * @param prefix префикс строки
     */
    public synchronized void setLinePrefix( final String prefix ){
        if( prefix==null ){
            linePrefix = null;
            return;
        }

        linePrefix = () -> prefix;

        assignLinePrefixFn();
    }

    private synchronized void assignLinePrefixFn(){
        if( linePrefix==null ){
            setLineConvertor(null);
        }

        Supplier<String> prefixValue = new Supplier<String>() {
            @Override
            public String get() {
                return getLinePrefix();
            }
        };

        Function<String,String> lineConv = Text.Convertors.wrap(prefixValue, null);
        setLineConvertor(lineConv);
    }
}
