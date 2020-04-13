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

/**
 * Синхронная работа с буфером
 * @author user
 */
public class SyncContentBuffer implements ContentBuffer
{
    protected volatile ContentBuffer buffer = null;
    protected volatile boolean copyLock = false;

    /**
     * Объект для синхронизации
     */
    protected final Object sync;

    /**
     * Возвращает объект для синхронизации
     * @return объект синхронизации
     */
    public final Object getSyncObject(){ return sync; }

    /**
     * Конструктор
     * @param buffer буфер
     * @param sync объект синхронизации
     * @param copyLock
     * true -копировать ссылку на блокировку / false - создавать новую блокировку,
     * при клонировании
     */
    public SyncContentBuffer( ContentBuffer buffer, Object sync, boolean copyLock ){
        if( buffer==null )throw new IllegalArgumentException("buffer==null");
        this.sync = sync!=null ? sync : this;
        this.buffer = buffer;
        this.copyLock = copyLock;
    }

    public SyncContentBuffer( ContentBuffer buffer, Object sync ){
        this(buffer,sync,false);
    }

    public SyncContentBuffer( ContentBuffer buffer ){
        this(buffer,null,false);
    }

    @Override
    public long getSize() {
        if( buffer==null )return 0;
        synchronized(sync){
            return buffer.getSize();
        }
    }

    @Override
    public void setSize(long size) {
        if( buffer==null )return;
        synchronized(sync){
            buffer.setSize(size);
        }
    }

    @Override
    public void set(long offset, byte[] data, int dataOffset, int dataLen) {
        if( buffer==null )return;
        synchronized(sync){
            buffer.set(offset, data, dataOffset, dataLen);
        }
    }

    @Override
    public byte[] get(long offset, int dataLen) {
        if( buffer==null )return new byte[]{};
        synchronized(sync){
            return buffer.get(offset, dataLen);
        }
    }

    @Override
    public void clear() {
        if( buffer==null )return;
        synchronized(sync){
            buffer.clear();
        }
    }

    /* @Override
    public ContentBuffer clone() {
        if( buffer==null )return null;
        synchronized(sync){
            return new SyncContentBuffer(
                    buffer.clone(),
                    copyLock ? sync : null,
                    copyLock);
        }
    } */

    @Override
    public void flush() {
        if( buffer==null )return;
        synchronized(sync){
            buffer.flush();
        }
    }

    /**
     * Закрывает объект и удаляет ссылки
     */
    @Override
    public void close() {
        if( buffer==null )return;
        synchronized(sync){
            buffer.close();
            buffer = null;
        }
    }
}
