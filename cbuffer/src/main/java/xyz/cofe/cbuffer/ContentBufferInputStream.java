/*
 * The MIT License
 *
 * Copyright 2015 user.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Чтение из буфера
 * @author user
 */
public class ContentBufferInputStream extends InputStream
{
    protected Lock lock = null;
    protected ContentBuffer contentBuffer = null;
    protected long beginIndex = -1;
    protected long endIndexExclusive = -1;
    protected long pointer = 0;

    /**
     * Конструктор
     * @param buffer буффер
     * @param beginIndex начальный индекс
     * @param endIndexExclusive конечный индекс или -1
     * @param lock блокировка или null
     */
    public ContentBufferInputStream( ContentBuffer buffer, long beginIndex, long endIndexExclusive, Lock lock ){
        if( buffer==null )throw new IllegalArgumentException("buffer == null");
        if( beginIndex<0 )beginIndex = 0;

        this.contentBuffer = buffer;
        this.beginIndex = beginIndex;
        this.endIndexExclusive = endIndexExclusive;
        this.pointer = beginIndex;
        this.lock = lock;
    }

    /**
     * Конструктор
     * @param buffer буффер
     * @param beginIndex начальный индекс
     * @param endIndexExclusive конечный индекс или -1
     */
    public ContentBufferInputStream( ContentBuffer buffer, long beginIndex, long endIndexExclusive ){
        this(buffer, beginIndex, endIndexExclusive, new ReentrantLock());
    }

    /**
     * Конструктор
     * @param buffer буффер
     * @param beginIndex начальный индекс
     */
    public ContentBufferInputStream( ContentBuffer buffer, long beginIndex ){
        this(buffer, beginIndex, -1);
    }

    /**
     * Конструктор
     * @param buffer буффер
     */
    public ContentBufferInputStream( ContentBuffer buffer ){
        this(buffer, 0, -1);
    }

    @Override
    public int read() throws IOException {
        if( lock!=null ){
            lock.lock();
            try{
                return read0();
            }
            finally{
                lock.unlock();
            }
        }else{
            return read0();
        }
    }

    private int read0() throws IOException {
        if( contentBuffer==null )return -1;
        if( endIndexExclusive>=0 && pointer>=endIndexExclusive )return -1;

        byte[] buff = contentBuffer.get(pointer, 1);
        if( buff == null || buff.length==0 )return -1;
        pointer++;

        return (int)buff[0] + (-((int)Byte.MIN_VALUE));
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if( lock!=null ){
            lock.lock();
            try{
                int readed = read0(b,off,len);
                return readed;
            }
            finally{
                lock.unlock();
            }
        }else{
            int readed = read(b,off,len);
            return readed;
        }
    }

    private int read0(byte[] b, int off, int len) throws IOException {
        if( b==null )throw new IllegalArgumentException("b==null");
        if( off<0 )throw new IllegalArgumentException("off < 0");
        if( len<0 )throw new IllegalArgumentException("len < 0");
        if( len==0 )return 0;
        if( off>=b.length )throw new IllegalArgumentException("off >= b.length");
        if( (off + len)>b.length )throw new IllegalArgumentException("off + len > b.length");

        if( endIndexExclusive>=0 ){
            if( pointer>=endIndexExclusive )return -1;
            if( pointer+len>=endIndexExclusive ){
                long diff = (pointer+len)+endIndexExclusive;
                if( diff>Integer.MAX_VALUE ){
                    len = (int)Integer.MAX_VALUE;
                }else{
                    len = (int)diff;
                }
            }
        }

        byte[] bb = contentBuffer.get(pointer, len);
        if( bb==null || bb.length==0 )return -1;

        int readSize = Math.min(bb.length, b.length);
        System.arraycopy(bb, 0, b, off, readSize);
        pointer += readSize;

        return bb.length;
    }

    @Override
    public int read(byte[] b) throws IOException {
        if( b==null )throw new IllegalArgumentException("b==null");
        return read(b, 0, b.length);
    }

    @Override
    public void close() throws IOException {
        if( lock!=null ){
            lock.lock();
            try{
                contentBuffer = null;
            }
            finally{
                lock.unlock();
            }
            lock = null;
        }else{
            contentBuffer = null;
        }
    }

    @Override
    public long skip(long n) throws IOException {
        if( lock!=null ){
            lock.lock();
            try{
                return skip0(n);
            }
            finally{
                lock.unlock();
            }
        }else{
            return skip0(n);
        }
    }

    private long skip0(long n) throws IOException {
        if( n<=0 )return 0;
        if( contentBuffer==null )return 0;
        if( pointer>=endIndexExclusive )return 0;
        if( n>Integer.MAX_VALUE )n = Integer.MAX_VALUE;
        pointer += n;
        return n;
    }
}
