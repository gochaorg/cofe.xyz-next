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
import java.io.Reader;
import java.io.Writer;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Синхронный текстовый буфер для потоковых операций: Reader / Writer. <br><br>
 *
 * Операции чтения (read()) выставляют флаг ожидания waitData
 * и будет ждать появления данных в буфере, переодично опрашивая содержимое буфера. <br>
 *
 * Данные буфера чиаются с начала, и прочитанные данные удаляются из буфера.
 * <br>
 * <br>
 *
 * Операции записи (write()) временно блокирует буфер для записи данных в конец буфера.
 * @author nt.gocha@gmail.com
 */
public class TextBuffer
    extends Reader
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TextBuffer.class.getName());
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

    protected StringBuilder buffer;
    protected final Random rnd = new Random();
    protected final Object bufferLock;

    public TextBuffer( StringBuilder buffer ){
        if( buffer==null )throw new IllegalArgumentException("buffer==null");
        this.buffer = buffer;
        this.bufferLock = this;
    }

    public TextBuffer( StringBuilder buffer,Object bufferLock ){
        if( buffer==null )throw new IllegalArgumentException("buffer==null");
        this.buffer = buffer;
        this.bufferLock = bufferLock==null ? this : bufferLock;
    }

    public TextBuffer(){
        this.buffer = new StringBuilder();
        this.bufferLock = this;
    }

    /**
     * Буфер
     * @return буфер
     */
    public StringBuilder getBuffer() {
        return buffer;
    }

    /**
     * Объект для блокировки чтения/записи содержимого буфера
     * @return объект блокировки
     */
    public Object getBufferLock(){ return bufferLock; }

    /**
     * Синхронно читает содержимое буфера
     * @return содержимое буфера
     */
    public String getBufferText(){
        synchronized( bufferLock ){
            if( buffer!=null )return buffer.toString();
            return null;
        }
    }

    protected final AtomicBoolean waitData = new AtomicBoolean(false);

    /**
     * Возвращает флаг ожидания данных в буфере, т.е. в данный момент выполняется операция read()
     * @return флаг ожидания данных
     */
    public boolean isWaitData(){ return waitData.get(); }

    /**
     * Синхронно читает данные из буфера
     * @param cbuf приемник
     * @param off смещение
     * @param len длинна
     * @return сколько прочитано
     * @throws IOException ошибка чтения
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException
    {
        if( cbuf==null )throw new IllegalArgumentException("cbuff is null");
        if( off<0 )throw new IllegalArgumentException("off<0");
        if( len<=0 )throw new IllegalArgumentException("len<=0");
        if( (len-off) > cbuf.length )throw new IllegalArgumentException("off out of range");

        StringBuilder sbuf = buffer;
        if( sbuf==null ){
            // throw new IOException("reader is closed");
            return -1;
        }
        if( len<=0 )return -1;

        try{
            waitData.set(true);
            while( true ){
                synchronized(bufferLock){
                    int buffLen = sbuf.length();
                    if( buffLen>0 ){
                        int rd = buffLen;
                        if( rd>len )rd = len;

                        String rdstr = sbuf.substring(0, rd);
                        sbuf.delete(0, rd);

                        for( int ci=0; ci<rdstr.length(); ci++ )
                            cbuf[ci+off] = rdstr.charAt(ci);

                        return rdstr.length();
                    }
                }

                try {
                    Thread.sleep(10+rnd.nextInt(50));
                } catch (InterruptedException ex) {
                    return -1;
                }
            }
        }finally{
            waitData.set(false);
        }
    }

    /**
     * Закрывает буфер
     * @throws IOException Ошибка IO
     */
    @Override
    public void close() throws IOException {
        synchronized(bufferLock){
            buffer = null;
        }
    }

    /**
     * Синхронно записывает данные в конец буфера
     * @param txt данные
     */
    public void write( String txt ){
        if( txt==null || txt.length()<1 )return;

        synchronized(bufferLock){
            if( buffer==null )return;
            buffer.append(txt);
        }
    }

    public Writer createWriter(){
        return new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                TextBuffer.this.write(new String(cbuf, off, len));
            }

            @Override
            public void flush() throws IOException {
            }

            @Override
            public void close() throws IOException {
                TextBuffer.this.close();
            }
        };
    }

    protected volatile  Writer writer;

    public Writer getWriter(){
        if( writer!=null )return writer;
        synchronized(this){
            if( writer==null ){
                writer = createWriter();
            }
            return writer;
        }
    }
}
