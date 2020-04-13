/*
 * The MIT License
 *
 * Copyright 2016 user.
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

package xyz.cofe.cbuffer;

import java.io.IOError;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.io.fn.IOFun;

/**
 *
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class RAFBuffer
    implements ContentBuffer
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(RAFBuffer.class.getName());
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

    protected final Lock lock;

    public RAFBuffer(){
        lock = new ReentrantLock();
    }

    public RAFBuffer( Lock lock ){
        this.lock = lock==null ? new ReentrantLock() : lock;
    }

    public RAFBuffer( RAFBuffer src ){
        this(src, new ReentrantLock());
    }

    public RAFBuffer( RAFBuffer src, Lock lock ){
        this.lock = lock==null ? new ReentrantLock() : lock;
        if( src!=null ){
            try{
                src.lock.lock();

                if( src.cloneRafContent ){
                    RandomAccessFile traf = src.createTempRAF();
                    this.raf = traf;

                    RandomAccessFile rafSrc = src.raf;
                    RandomAccessFile rafDest = traf;

                    if( rafSrc!=null && rafDest!=null ){
                        ContentBufferInputStream cin = null;
                        cin = new ContentBufferInputStream(src);

                        ContentBufferOutputStream cout = null;
                        cout = new ContentBufferOutputStream(this);

                        try{
                            IOFun.copy(cin, cout);
                        }catch( IOException err ){ throw new IOError(err); }

                        try{
                            cout.close();
                        }catch( IOException err ){ throw new IOError(err); }

                        try{
                            cin.close();
                        }catch( IOException err ){ throw new IOError(err); }
                    } else {
                        this.raf = src.raf;
                    }
                }

                this.throwNotSetRaf = src.throwNotSetRaf;
                this.flushOnClose = src.flushOnClose;
                this.cloneRafContent = src.cloneRafContent;
                this.closeRaf = src.closeRaf;
                this.deleteTempOnExit = src.deleteTempOnExit;
                this.releaseRaf = src.releaseRaf;
            }finally{
                src.lock.unlock();
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="cloneRafContent">
    protected boolean cloneRafContent = true;

    /**
     * Указывает клонировать во временный файл содержимое буфера
     * @return true (по умолчанию) - клонировать
     */
    public boolean isCloneRafContent() {
        try{
            lock.lock();
            return cloneRafContent;
        }finally{
            lock.unlock();
        }
    }

    /**
     * Указывает клонировать во временный файл содержимое буфера
     * @param cloneRafContent true (по умолчанию) - клонировать
     */
    public void setCloneRafContent(boolean cloneRafContent) {
        try{
            lock.lock();
            this.cloneRafContent = cloneRafContent;
        }finally{
            lock.unlock();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="lock">
    public Lock getLock(){ return lock; }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="raf">
    protected RandomAccessFile raf;

    public RandomAccessFile getRaf() {
        try{
            lock.lock();
            return raf;
        }finally{
            lock.unlock();
        }
    }

    public void setRaf(RandomAccessFile raf) {
        try{
            lock.lock();
            this.raf = raf;
        }finally{
            lock.unlock();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="throwNotSetRaf">
    protected boolean throwNotSetRaf = true;

    public boolean isThrowNotSetRaf() {
        try{
            lock.lock();
            return throwNotSetRaf;
        }finally{
            lock.unlock();
        }
    }

    public void setThrowNotSetRaf(boolean throwNotSetRaf) {
        try{
            lock.lock();
            this.throwNotSetRaf = throwNotSetRaf;
        }finally{
            lock.unlock();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getSize()">
    @Override
    public long getSize() {
        try{
            lock.lock();
            if( raf==null ){
                if( throwNotSetRaf ){
                    throw new IllegalStateException("property raf not set");
                }
                return 0;
            }
            try {
                return raf.length();
            } catch (IOException ex) {
                throw new IOError(ex);
            }
        }finally{
            lock.unlock();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setSize()">
    @Override
    public void setSize(long size) {
        try{
            lock.lock();
            if( raf==null ){
                if( throwNotSetRaf ){
                    throw new IllegalStateException("property raf not set");
                }
                return;
            }
            try {
                raf.setLength(0);
            } catch (IOException ex) {
                throw new IOError(ex);
            }
        }finally{
            lock.unlock();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="set()">
    @Override
    public void set(long offset, byte[] data, int dataOffset, int dataLen) {
        if( offset<0 )throw new IllegalArgumentException( "offset("+offset+")<0" );
        if( data==null )throw new IllegalArgumentException( "data==null" );
        if( dataOffset<0 )throw new IllegalArgumentException( "dataOffset("+dataOffset+")<0" );
        if( dataOffset>=data.length )
            throw new IllegalArgumentException(
                "dataOffset("+dataOffset+")>=data.length("+(data.length)+")"
            );
        if( dataLen==0 )return;
        if( dataLen<0 )throw new IllegalArgumentException(
            "dataLen("+dataLen+")<0"
        );
        if( (dataLen+dataOffset)>(data.length) )
            throw new IllegalArgumentException(
                "dataLen("+dataLen+")+dataOffset("+dataOffset+")>data.length("+data.length+")"
            );

        try{
            lock.lock();
            if( raf==null ){
                if( throwNotSetRaf ){
                    throw new IllegalStateException("property raf not set");
                }
                return;
            }
            try {
                raf.seek(offset);
                raf.write(data, dataOffset, dataLen);
            } catch (IOException ex) {
                throw new IOError(ex);
            }
        }finally{
            lock.unlock();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="get()">
    @Override
    public byte[] get(long offset, int dataLen) {
        if( offset<0 )throw new IllegalArgumentException("offset("+offset+")<0");
        if( dataLen<0 )throw new IllegalArgumentException("dataLen("+dataLen+")<0");
        if( dataLen==0 )return new byte[]{};

        try{
            lock.lock();
            if( raf==null ){
                if( throwNotSetRaf ){
                    throw new IllegalStateException("property raf not set");
                }
                return new byte[]{};
            }

            long size = getSize();

            // остаток
            long residual = size - offset;

            if( residual<=0 )return new byte[]{};
            if( residual<dataLen ){
                try {
                    int readSize = (int)residual;
                    raf.seek(offset);

                    byte[] data = new byte[readSize];

                    int ptr = 0;

                    while( true ){
                        int readed = raf.read(data, ptr, data.length - ptr);
                        if( readed<=0 )break;
                        ptr += readed;
                        if( ptr>=readSize )break;
                    }

                    return data;
                } catch (IOException ex) {
                    throw new IOError(ex);
                }
            }else{
                try {
                    int readSize = (int)dataLen;
                    raf.seek(offset);

                    byte[] data = new byte[readSize];

                    int ptr = 0;

                    while( true ){
                        int readed = raf.read(data, ptr, data.length - ptr);
                        if( readed<=0 )break;
                        ptr += readed;
                        if( ptr>=readSize )break;
                    }

                    return data;
                } catch (IOException ex) {
                    throw new IOError(ex);
                }
            }
        }finally{
            lock.unlock();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="clear()">
    @Override
    public void clear() {
        setSize(0);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="deleteTempOnExit">
    protected boolean deleteTempOnExit = true;

    /**
     * Указывает удалять временно клонированный файл при выходе
     * @return true (по умолчанию) - удалять клонированный файл
     */
    public boolean isDeleteTempOnExit() {
        try{
            lock.lock();
            return deleteTempOnExit;
        }finally{
            lock.unlock();
        }
    }

    /**
     * Указывает удалять временно клонированный файл при выходе
     * @param deleteTempOnExit true (по умолчанию) - удалять клонированный файл
     */
    public void setDeleteTempOnExit(boolean deleteTempOnExit) {
        try{
            lock.lock();
            this.deleteTempOnExit = deleteTempOnExit;
        }finally{
            lock.unlock();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="createTempRAF()">
    protected java.io.File createTempFile(){
        try {
            java.io.File tempFile = java.io.File.createTempFile("rafBuffer", ".tmp");
            return tempFile;
        } catch (IOException ex) {
            throw new IOError(ex);
        }
    }

    protected RandomAccessFile createTempRAF(){
        if( raf==null ){
            if( throwNotSetRaf ){
                throw new IllegalStateException("property raf not set");
            }
            return null;
        }

        java.io.File tempFile = null;
        try {
            tempFile = createTempFile();
            if( deleteTempOnExit )tempFile.deleteOnExit();

            RandomAccessFile traf = new RandomAccessFile(tempFile, "rw");
            return traf;
        } catch (IOException ex) {
            throw new IOError(ex);
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="clone()">
    @Override
    public RAFBuffer clone() {
        try{
            lock.lock();
            if( raf==null ){
                if( throwNotSetRaf ){
                    throw new IllegalStateException("property raf not set");
                }

                return new RAFBuffer(this);
            }

            return new RAFBuffer(this);
        }finally{
            lock.unlock();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="flush()">
    @Override
    public void flush() {
        if( raf==null ){
            if( throwNotSetRaf ){
                throw new IllegalStateException("property raf not set");
            }
            return;
        }

        try {
            raf.getFD().sync();
        } catch (IOException ex) {
            throw new IOError(ex);
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="flushOnClose">
    protected boolean flushOnClose = true;

    /**
     * Указывает сбрасывать содержимое памяти на диск при закрытии
     * @return true (по умолчанию) - сбрасывать.
     */
    public boolean isFlushOnClose() {
        try{
            lock.lock();
            return flushOnClose;
        }finally{
            lock.unlock();
        }
    }

    /**
     * Указывает сбрасывать содержимое памяти на диск при закрытии
     * @param flushOnClose true (по умолчанию) - сбрасывать.
     */
    public void setFlushOnClose(boolean flushOnClose) {
        try{
            lock.lock();
            this.flushOnClose = flushOnClose;
        }finally{
            lock.unlock();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="closeRaf">
    protected boolean closeRaf = true;

    /**
     * Указывает закрывать RandomAccessFile при вызове close.
     * @return true (по умолчанию) - закрывать.
     */
    public boolean isCloseRaf() {
        try{
            lock.lock();
            return closeRaf;
        }finally{
            lock.unlock();
        }
    }

    /**
     * Указывает закрывать RandomAccessFile при вызове close.
     * @param closeRaf true (по умолчанию) - закрывать.
     */
    public void setCloseRaf(boolean closeRaf) {
        try{
            lock.lock();
            this.closeRaf = closeRaf;
        }finally{
            lock.unlock();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="releaseRaf">
    protected boolean releaseRaf = true;

    /**
     * Указывает освобаждать ссылку (устанавливать свойство raf в null) на RandomAccessFile
     * @return true (по умолчанию) - освобождать.
     */
    public boolean isReleaseRaf() {
        try{
            lock.lock();
            return releaseRaf;
        }finally{
            lock.unlock();
        }
    }

    /**
     * Указывает освобаждать ссылку (устанавливать свойство raf в null) на RandomAccessFile
     * @param releaseRaf true (по умолчанию) - освобождать.
     */
    public void setReleaseRaf(boolean releaseRaf) {
        try{
            lock.lock();
            this.releaseRaf = releaseRaf;
        }finally{
            lock.unlock();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="close()">
    @Override
    public void close() {
        try{
            lock.lock();
            if( raf==null )return;
            if( flushOnClose ){
                flush();
            }
            try {
                if( closeRaf )raf.close();
            } catch (IOException ex) {
                throw new IOError(ex);
            }
            if( releaseRaf )raf = null;
        }finally{
            lock.unlock();
        }
    }
//</editor-fold>
}
