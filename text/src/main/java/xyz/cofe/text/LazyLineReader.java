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


import xyz.cofe.iter.Eterable;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Отложенный /ленивый построчный итератор
 * @author nt.gocha@gmail.com
 */
public class LazyLineReader
    implements Iterable<String>, Closeable
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(LazyLineReader.class.getName());
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

    protected Supplier<Reader> lazyReader;
    protected int bufferSize = 1024 * 4;

    protected final Lock lock;

    public LazyLineReader( Supplier<Reader> reader ){
        this( reader, 1024*4, null );
    }

    public LazyLineReader( Supplier<Reader> reader, int bufferSize, Lock lock ){
        if( reader==null )throw new IllegalArgumentException( "reader==null" );
        if( bufferSize<1 )bufferSize = 1024 * 4;

        this.lazyReader = reader;
        this.bufferSize = bufferSize;
        this.lock = lock==null ? new ReentrantLock() : lock;
    }

//    private final WeakHashMap<LineReaderIterator,Object> lr_itr_map = new WeakHashMap();

    @Override
    public Iterator<String> iterator() {
        try{
            lock.lock();
            LineReaderIterator lr_itr = null;

            if( lazyReader!=null ){
                Reader rd = lazyReader.get();
                if( rd==null )return Eterable.<String>empty().iterator();

                lr_itr = new LineReaderIterator(rd, bufferSize, closeReaderFun, lock);
//                lr_itr_map.put(lr_itr, this);
                return lr_itr;
            }else{
                return Eterable.<String>empty().iterator();
            }
        }finally{
            lock.unlock();
        }
    }

    private Consumer<Reader> closeReaderFun = new Consumer<Reader>() {
        @Override
        public void accept( Reader rd ) {
            try{
                lock.lock();
                try {
                    rd.close();
                } catch( IOException ex ) {
                    Logger.getLogger(LazyLineReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }finally{
                lock.unlock();
            }
        }
    };

    @Override
    public void close() throws IOException {
        try{
            lock.lock();

//            for( Map.Entry<LineReaderIterator,Object> en : lr_itr_map.entrySet() ){
//                LineReaderIterator lri = en.getKey();
//                if( lri==null )continue;
//
//                lri.reader = null;
//                lri.close();
//            }

//            lr_itr_map.clear();

            if( lazyReader!=null ){
                lazyReader = null;
            }
        }finally{
            lock.unlock();
        }
    }
}
