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

package xyz.cofe.io.fs;

import java.io.IOError;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Файловый буфер с использоавнием канала файла
 * @author user
 */
public class FChannelBuffer extends xyz.cofe.cbuffer.SBCBuffer {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(FChannelBuffer.class.getName());
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

    private static void logEntering(String method,Object ... params){
        logger.entering(FChannelBuffer.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(FChannelBuffer.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(FChannelBuffer.class.getName(), method, result);
    }
    //</editor-fold>

    public FChannelBuffer(FileChannel channel) {
        super(channel);
    }

    public static FChannelBuffer open(Path path, OpenOption... options){
        try {
            FileChannel fc = FileChannel.open(path, options);
            return new FChannelBuffer(fc);
        } catch (IOException ex) {
            Logger.getLogger(FChannelBuffer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    public static FChannelBuffer open(Path path,  Set<? extends OpenOption> options, FileAttribute<?>... attrs){
        try {
            FileChannel fc = FileChannel.open(path, options, attrs);
            return new FChannelBuffer(fc);
        } catch (IOException ex) {
            Logger.getLogger(FChannelBuffer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    public static FChannelBuffer open( File file, OpenOption... options ){
        if( file==null )throw new IllegalArgumentException("file == null");
        return open(file.path, options);
    }

    public static FChannelBuffer open( File file, Set<? extends OpenOption> options, FileAttribute<?>... attrs ){
        if( file==null )throw new IllegalArgumentException("file == null");
        return open(file.path, options, attrs);
    }

    private FileChannel fc(){ return (FileChannel) channel; }

    public FileChannel getFileChannel(){ return fc(); }

    public synchronized FileLock lock(){
        try {
            return fc().lock();
        } catch (ClosedChannelException ex){
            throw new ClosedChannelError(ex);
        } catch (IOException ex) {
            Logger.getLogger(FChannelBuffer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    public synchronized FileLock lock(long position, long size, boolean shared){
        try {
            return fc().lock(position,size,shared);
        } catch (ClosedChannelException ex){
            throw new ClosedChannelError(ex);
        } catch (IOException ex) {
            Logger.getLogger(FChannelBuffer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    public synchronized FileLock tryLock(){
        try {
            return fc().tryLock();
        } catch (ClosedChannelException ex){
            throw new ClosedChannelError(ex);
        } catch (IOException ex) {
            Logger.getLogger(FChannelBuffer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    public synchronized FileLock tryLock(long position, long size, boolean shared){
        try {
            return fc().tryLock(position,size,shared);
        } catch (ClosedChannelException ex){
            throw new ClosedChannelError(ex);
        } catch (IOException ex) {
            Logger.getLogger(FChannelBuffer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    public synchronized void force(boolean metaData){
        try {
            fc().force(metaData);
        } catch (ClosedChannelException ex){
            throw new ClosedChannelError(ex);
        } catch (IOException ex) {
            Logger.getLogger(FChannelBuffer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    @Override
    public synchronized void flush() {
        force(true);
        super.flush(); //To change body of generated methods, choose Tools | Templates.
    }

    public synchronized MappedByteBuffer map(FileChannel.MapMode mode, long position, long size){
        try {
            return fc().map(mode, position, size);
        } catch (ClosedChannelException ex){
            throw new ClosedChannelError(ex);
        } catch (IOException ex) {
            Logger.getLogger(FChannelBuffer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    public synchronized long transferFrom(ReadableByteChannel src, long position, long count){
        try {
            return fc().transferFrom(src,position,count);
        } catch (ClosedChannelException ex){
            throw new ClosedChannelError(ex);
        } catch (IOException ex) {
            Logger.getLogger(FChannelBuffer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    public synchronized long transferTo(long position, long count, WritableByteChannel target){
        try {
            return fc().transferTo(position,count,target);
        } catch (ClosedChannelException ex){
            throw new ClosedChannelError(ex);
        } catch (IOException ex) {
            Logger.getLogger(FChannelBuffer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
}
