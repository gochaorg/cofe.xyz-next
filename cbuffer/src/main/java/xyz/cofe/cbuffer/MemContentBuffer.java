/*
 * The MIT License
 *
 * Copyright 2015 Kamnev Georgiy (nt.gocha@gmail.com).
 *
 * Данная лицензия разрешает, безвозмездно, лицам, получившим копию данного программного
 * обеспечения и сопутствующей документации (в дальнейшем именуемыми "Программное Обеспечение"),
 * использовать Программное Обеспечение без ограничений, включая неограниченное право на
 * использование, копирование, изменение, объединение, публикацию, распространение, сублицензирование
 * и/или продажу копий Программного Обеспечения, также как и лицам, которым предоставляется
 * данное Программное Обеспечение, при соблюдении следующих условий:
 *
 * Вышеупомянутый копирайт и данные условия должны быть включены во все копии
 * или значимые части данного Программного Обеспечения.
 *
 * ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ ПРЕДОСТАВЛЯЕТСЯ «КАК ЕСТЬ», БЕЗ ЛЮБОГО ВИДА ГАРАНТИЙ,
 * ЯВНО ВЫРАЖЕННЫХ ИЛИ ПОДРАЗУМЕВАЕМЫХ, ВКЛЮЧАЯ, НО НЕ ОГРАНИЧИВАЯСЬ ГАРАНТИЯМИ ТОВАРНОЙ ПРИГОДНОСТИ,
 * СООТВЕТСТВИЯ ПО ЕГО КОНКРЕТНОМУ НАЗНАЧЕНИЮ И НЕНАРУШЕНИЯ ПРАВ. НИ В КАКОМ СЛУЧАЕ АВТОРЫ
 * ИЛИ ПРАВООБЛАДАТЕЛИ НЕ НЕСУТ ОТВЕТСТВЕННОСТИ ПО ИСКАМ О ВОЗМЕЩЕНИИ УЩЕРБА, УБЫТКОВ
 * ИЛИ ДРУГИХ ТРЕБОВАНИЙ ПО ДЕЙСТВУЮЩИМ КОНТРАКТАМ, ДЕЛИКТАМ ИЛИ ИНОМУ, ВОЗНИКШИМ ИЗ, ИМЕЮЩИМ
 * ПРИЧИНОЙ ИЛИ СВЯЗАННЫМ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ ИЛИ ИСПОЛЬЗОВАНИЕМ ПРОГРАММНОГО ОБЕСПЕЧЕНИЯ
 * ИЛИ ИНЫМИ ДЕЙСТВИЯМИ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ.
 */

package xyz.cofe.cbuffer;


import xyz.cofe.ecolls.ListenersHelper;

import java.io.Closeable;
import java.util.Arrays;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Буфер в памяти
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class MemContentBuffer
    implements ContentBuffer
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(MemContentBuffer.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(MemContentBuffer.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(MemContentBuffer.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(MemContentBuffer.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(MemContentBuffer.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(MemContentBuffer.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(MemContentBuffer.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public MemContentBuffer(){
    }

    public MemContentBuffer(byte[] sourceData){
        if( sourceData==null )throw new IllegalArgumentException( "sourceData==null" );
        this.data = sourceData;
        this.dataSize = data.length;
    }

    protected volatile byte[] data = new byte[]{};
    protected volatile int dataSize = 0;
    protected volatile int extendSize = 1024*1024;

    public int getExtendSize(){ return extendSize; }
    public void setExtendSize(int size){
        if( size<1 )throw new IllegalArgumentException("size<1");
        extendSize = size;
    }

    @Override
    public long getSize() {
        return dataSize;
    }

    @Override
    public synchronized void setSize(long size) {
        if( size<0 )throw new IllegalArgumentException("size<0");
        if( size>Integer.MAX_VALUE )throw new IllegalArgumentException("size>Integer.MAX_VALUE");
        if( data==null ){
            data = new byte[(int)size];
            dataSize = (int)size;
            fireEvent(new SizeChangedEvent(this, 0, dataSize));
        }else{
            if( dataSize > size ){
                long old = dataSize;
                //data = Arrays.copyOf(data, (int)size);
                dataSize = (int)size;
                fireEvent(new SizeChangedEvent(this, old, dataSize));
            }else if( dataSize < size ){
                long old = dataSize;
                if( data.length < size ){
                    long extRest = size % extendSize;
                    long extCnt = (size/extendSize) + (extRest>0 ? 1 : 0);
                    long targetSize = extCnt * extendSize;
                    if( targetSize>Integer.MAX_VALUE ){
                        throw new IllegalStateException("size>Integer.MAX_VALUE");
                    }
                    data = Arrays.copyOf(data,(int)targetSize);
                }
                dataSize = (int)size;
                fireEvent(new SizeChangedEvent(this, old, dataSize));
            }
        }
    }

    @Override
    public void set(long offset, byte[] data, int dataOffset, int dataLen) {
        if( data==null )throw new IllegalArgumentException( "data==null" );
        if( dataOffset<0 )throw new IllegalArgumentException( "dataOffset<0" );
        if( dataLen<0 )throw new IllegalArgumentException( "dataLen<0" );
        if( dataLen==0 )return;
        if( offset<0 )throw new IllegalArgumentException( "offset<0" );
        if( offset>Integer.MAX_VALUE )throw new IllegalArgumentException( "offset>Integer.MAX_VALUE" );

        int targetSize = (int)offset + dataLen;
        if( targetSize>dataSize ){
            setSize(targetSize);
        }

        System.arraycopy(data, dataOffset, this.data, (int)offset, dataLen);
    }

    @Override
    public byte[] get(long offset, int dataLen) {
        if( offset>Integer.MAX_VALUE )return new byte[]{};

        if( this.data==null )return new byte[]{};
        if( (int)offset >= dataSize )return new byte[]{};
        if( dataSize<1 )return new byte[]{};

        int minIdx = (int)offset;
        int targetEnd = (int)offset + ((int)dataLen);
        if( targetEnd > dataSize )targetEnd = dataSize;

        return Arrays.copyOfRange(this.data, minIdx, targetEnd);
    }

    @Override
    public void clear() {
        setSize(0);
    }

    @Override
    public ContentBuffer clone() {
        return new MemContentBuffer(data==null ? new byte[]{} : Arrays.copyOf(data, dataSize));
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    //<editor-fold defaultstate="collapsed" desc="listeners">
    protected final ListenersHelper<ContentBufferListener,ContentBufferEvent>
        listeners = new ListenersHelper<ContentBufferListener, ContentBufferEvent>(
        ( ls, ev ) -> {
            if( ls!=null ){
                ls.contentBufferEvent(ev);
            }
        }
    );

    public boolean hasListener(ContentBufferListener listener) {
        return listeners.hasListener(listener);
    }

    public Set<ContentBufferListener> getListeners() {
        return listeners.getListeners();
    }

    public AutoCloseable addListener(ContentBufferListener listener) {
        return listeners.addListener(listener);
    }

    public AutoCloseable addListener(ContentBufferListener listener, boolean weakLink) {
        return listeners.addListener(listener, weakLink);
    }

    public void removeListener(ContentBufferListener listener) {
        listeners.removeListener(listener);
    }

    public void fireEvent(ContentBufferEvent event) {
        listeners.fireEvent(event);
    }
    //</editor-fold>
}
