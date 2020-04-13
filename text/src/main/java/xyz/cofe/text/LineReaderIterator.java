/*
 * The MIT License
 *
 * Copyright 2016 nt.gocha@gmail.com.
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


import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Построчный итератор
 * @author nt.gocha@gmail.com
 */
public class LineReaderIterator
    implements Iterator<String>, Closeable
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(LineReaderIterator.class.getName());
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

    protected Reader reader = null;
    protected char[] buff = new char[1024*8];
    protected String[] lines = null;

    protected final Lock lock;
    protected Consumer<Reader> closeReader = null;

    private long lineCounter = 0;

    public LineReaderIterator(Reader reader){
        if( reader==null ) throw new IllegalArgumentException( "reader==null" );
        this.reader = reader;
        this.lock = new ReentrantLock();
        lines = readLines();
    }

    public LineReaderIterator(Reader reader,int bufferSize,Consumer<Reader> closeReader, Lock lock){
        if( reader==null ) throw new IllegalArgumentException( "reader==null" );
        if( bufferSize<1 )throw new IllegalArgumentException("bufferSize<1");
        this.buff = new char[bufferSize];
        this.lock = lock==null ? new ReentrantLock() : lock;
        this.closeReader = closeReader;
        this.reader = reader;
        lines = readLines();
    }

    public Lock getLock(){ return lock; }

    public long getLineCounter(){
        try{
            lock.lock();
            return lineCounter;
        }finally{
            lock.unlock();
        }
    }

    protected String[] readLines(){
        try{
            lock.lock();
            StringBuilder sb = new StringBuilder();
            while( true ){
                if( reader==null )break;
                try {
                    int readed = reader.read(buff);
                    if( readed>0 ){
                        String l = new String(buff,0,readed);
                        if( l.contains("\n") || l.contains("\r") ){
                            sb.append(l);
                            return Text.splitNewLines(sb.toString());
                        }else{
                            sb.append(l);
                            continue;
                        }
                    }else if( readed==0 ){
                        continue;
                    }else{
                        if( closeReader!=null ){
                            closeReader.accept(reader);
                        }
                        reader = null;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(LineReaderIterator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return Text.splitNewLines(sb.toString());
        }finally{
            lock.unlock();
        }
    }

    @Override
    public boolean hasNext() {
        try{
            lock.lock();
            return lines!=null && lines.length>0;
        }finally{
            lock.unlock();
        }
    }

    @Override
    public String next() {
        try{
            lock.lock();
            if( lines==null ){
                try {
                    close();
                } catch (IOException ex) {
                    Logger.getLogger(LineReaderIterator.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            if( lines.length>1 ){
                String l = lines[0];
                lines = Arrays.copyOfRange(lines, 1, lines.length);
                lineCounter++;
                return l;
            }else if( lines.length==1 ){
                String[] lines2 = readLines();
                if( lines2==null ){
                    String r = lines[0];
                    lines = null;
                    lineCounter++;
                    return r;
                }else if( lines2.length==0 ){
                    String r = lines[0];
                    lines = null;
                    lineCounter++;
                    return r;
                }else if( lines2.length==1 ){
                    String l0 = lines[0];
                    String l1 = lines2[0];
                    String r = l0 + l1;
                    lines = null;
                    lineCounter++;
                    return r;
                }else{
                    String l0 = lines[0];
                    String l1 = lines2[0];
                    String r = l0 + l1;
                    lines = lines2;
                    lines = Arrays.copyOfRange(lines, 1, lines.length);
                    lineCounter++;
                    return r;
                }
            }else{
                try {
                    close();
                } catch (IOException ex) {
                    Logger.getLogger(LineReaderIterator.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        }finally{
            lock.unlock();
        }
    }

    @Override
    public void remove() {
//            throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() throws IOException {
        try{
            lock.lock();
            if( lines!=null ){
                lines = null;
            }
            if( reader!=null ){
                if( closeReader!=null ){
                    closeReader.accept(reader);
                }
                reader = null;
            }
        }finally{
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings( "FinalizeDeclaration" )
    protected void finalize() throws Throwable {
        try{
            close();
        }catch( Throwable er ){
            logException(er);
        }
        super.finalize();
    }
}
