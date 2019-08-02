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
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Создает stream для записи
 * @author user
 */
public class ContentBufferOutputStream extends OutputStream
{
    protected Lock lock = null;
    protected ContentBuffer contentBuffer = null;
    protected long beginIndex = -1;
    protected long endIndexExclusive = -1;
    protected long pointer = 0;

    public ContentBufferOutputStream(
        ContentBuffer buffer,
        long beginIndex,
        long endIndexExclusive,
        Lock lock )
    {
        if( buffer==null )throw new IllegalArgumentException("buffer == null");
        if( beginIndex<0 )beginIndex = 0;

        this.contentBuffer = buffer;
        this.beginIndex = beginIndex;
        this.endIndexExclusive = endIndexExclusive;
        this.pointer = beginIndex;
        this.lock = lock;
    }

    public ContentBufferOutputStream( ContentBuffer buffer, long beginIndex, long endIndexExclusive ){
        this(buffer, beginIndex, endIndexExclusive, new ReentrantLock());
    }

    public ContentBufferOutputStream( ContentBuffer buffer, long beginIndex ){
        this(buffer, beginIndex, -1);
    }

    public ContentBufferOutputStream( ContentBuffer buffer ){
        this(buffer, 0, -1);
    }

    @Override
    public void write(int b) throws IOException {
        if( lock!=null ){
            lock.lock();
            try{
                write0(b);
            }
            finally{
                lock.unlock();
            }
        }else{
            write0(b);
        }
    }

    private void write0(int b) throws IOException {
        byte[] buff = new byte[1];
        buff[0] = (byte)(b + (int)Byte.MIN_VALUE);
        if( contentBuffer==null )return;
        if( endIndexExclusive>=0 && pointer>=endIndexExclusive )return;
        contentBuffer.set(pointer, buff, 0, 1);
        pointer++;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if( lock!=null ){
            lock.lock();
            try{
                write0(b,off,len);
            }
            finally{
                lock.unlock();
            }
        }else{
            write0(b,off,len);
        }
    }

    private void write0(byte[] b, int off, int len) throws IOException {
        if( contentBuffer==null )return;

        if( b==null )throw new IllegalArgumentException("b==null");
        if( off<0 )throw new IllegalArgumentException("off < 0");
        if( len<0 )throw new IllegalArgumentException("len < 0");
        if( len==0 )return;
        if( off>=b.length )throw new IllegalArgumentException("off >= b.length");
        if( (off + len)>b.length )throw new IllegalArgumentException("off + len > b.length");

        if( endIndexExclusive>=0 && pointer>=endIndexExclusive )return;
        contentBuffer.set(pointer, b, off, len);
        pointer+=len;
    }


    @Override
    public void write(byte[] b) throws IOException {
        if( b==null )throw new IllegalArgumentException("b==null");
        if( contentBuffer==null )return;
        write(b, 0, b.length);
    }

    @Override
    public void close() throws IOException {
        if( lock!=null ){
            lock.lock();
            try{
                close0();
            }
            finally{
                lock.unlock();
            }
        }else{
            close0();
        }
    }

    private void close0() throws IOException {
        if( contentBuffer!=null ){
            contentBuffer.flush();
            contentBuffer = null;
        }
    }

    @Override
    public void flush() throws IOException {
        if( lock!=null ){
            lock.lock();
            try{
                flush0();
            }
            finally{
                lock.unlock();
            }
        }else{
            flush0();
        }
    }

    private void flush0() throws IOException {
        if( contentBuffer!=null ){
            contentBuffer.flush();
        }
    }
}
