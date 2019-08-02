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
import java.lang.ref.WeakReference;
import java.nio.channels.FileLock;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.io.fn.IOFun;

/**
 * Буфер с файловой блокировкой
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class FileLockBuffer extends RAFBuffer
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(FileLockBuffer.class.getName());
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

    public FileLockBuffer() {
    }

    public FileLockBuffer(Lock lock) {
        super(lock);
    }

    protected WeakReference<FileLockBuffer> original;

    public FileLockBuffer(FileLockBuffer src){
        if( src==null )throw new IllegalArgumentException( "src==null" );
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
                    this.original = null;
                }
            }

            this.throwNotSetRaf = src.throwNotSetRaf;
            this.flushOnClose = src.flushOnClose;
            this.cloneRafContent = src.cloneRafContent;
            this.closeRaf = src.closeRaf;
            this.deleteTempOnExit = src.deleteTempOnExit;
            this.releaseRaf = src.releaseRaf;

            if( src.cloneRafContent ){
                this.fileLock = null;
                this.original = null;
            }else{
                this.fileLock = src.fileLock;
                this.original = new WeakReference<FileLockBuffer>(src);
            }
        }finally{
            src.lock.unlock();
        }
    }

    @Override
    public FileLockBuffer clone() {
        return new FileLockBuffer(this);
    }

    @Override
    public void setRaf(RandomAccessFile raf) {
        try{
            lock.lock();
            super.setRaf(raf);
        }finally{
            lock.unlock();
        }
    }

    public boolean hasFileLock(){
        try {
            lock.lock();
            if( fileLock!=null ){
                if( fileLock.isValid() )return true;
            }
            for( FileLock fl : fileLocks ){
                if( fl==null )continue;
                if( fl.isValid() )return true;
            }
            return false;
        }
        finally {
            lock.unlock();
        }
    }

    protected FileLock fileLock;

    public FileLock getFileLock() {
        try{
            lock.lock();
            return fileLock;
        }finally{
            lock.unlock();
        }
    }

    protected final Set<FileLock> fileLocks = new LinkedHashSet<FileLock>();

    public Set<FileLock> getFileLocks(){
        try {
            lock.lock();
            Set<FileLock> fset = new LinkedHashSet<FileLock>();
            fset.addAll(fileLocks);
            return fset;
        }
        finally {
            lock.unlock();
        }
    }

    public FileLock fileLock(){
        try{
            lock.lock();

            if( raf==null ){
                if( throwNotSetRaf ){
                    throw new IllegalArgumentException("property raf not set");
                }
                return null;
            }

            if( original!=null ){
                FileLockBuffer flb = original.get();
                if( flb!=null ){
                    FileLock fl = flb.fileLock();
                    if( fl!=null ){
                        fileLocks.add(fl);
                    }
                    return fl;
                }else{
                    original = null;
                }
            }

            if( fileLock!=null ){
                if( fileLock.isValid() ){
                    fileLocks.add(fileLock);
                    return fileLock;
                }
            }

            try {
                fileLock = raf.getChannel().lock();
//                fileLock = raf.getChannel().lock(0,Long.MAX_VALUE,false);
                fileLocks.add(fileLock);
            } catch (IOException ex) {
                throw new IOError(ex);
            }

            return fileLock;
        }finally{
            lock.unlock();
        }
    }

    public FileLock fileUnlock(){
        try{
            lock.lock();

            FileLock res = null;

            if( original!=null ){
                FileLockBuffer flb = original.get();
                if( flb!=null ){
                    FileLock f = flb.fileUnlock();

                    if( f!=null ){
                        if( !f.isValid() ){
                            fileLocks.remove(f);
                            res = f;
                        }
                    }
                }else{
                    original = null;
                }
            }

            if( fileLock!=null && fileLock.isValid() ){
                try {
                    fileLock.release();
                    fileLocks.remove(fileLock);
                    res = fileLock;
                } catch (IOException ex) {
                    throw new IOError(ex);
                }
            }

            for( FileLock fl : fileLocks ){
                try {
                    if( fl==null )continue;
                    if( !fl.isValid() )continue;
                    fl.release();
                    res = fl;
                } catch (IOException ex) {
                    throw new IOError(ex);
                }
            }

            fileLocks.clear();
            return res;
        }finally{
            lock.unlock();
        }
    }

    @Override
    public void close() {
        fileUnlock();
        super.close();
    }
}
