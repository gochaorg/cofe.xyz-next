/*
 * The MIT License
 *
 * Copyright 2017 user.
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

import java.io.Closeable;
import java.io.IOError;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Буфер для работы через интерфейс SeekableByteChannel
 * @author Kamnev Georgiy
 */
public class SBCBuffer implements AutoCloseable, ContentBuffer
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(SBCBuffer.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }

    private static boolean isLogSevere(){
        Level logLevel = logger.getLevel();
        return logLevel==null ? true : logLevel.intValue() <= Level.SEVERE.intValue();
    }

    private static boolean isLogWarning(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.WARNING.intValue();
    }

    private static boolean isLogInfo(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.INFO.intValue();
    }

    private static boolean isLogFine(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINE.intValue();
    }

    private static boolean isLogFiner(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINER.intValue();
    }

    private static boolean isLogFinest(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINEST.intValue();
    }

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
        logger.entering(SBCBuffer.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(SBCBuffer.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(SBCBuffer.class.getName(), method, result);
    }
    //</editor-fold>

    protected SeekableByteChannel channel;
    protected boolean closeOnFinalize = false;

    public SBCBuffer(SeekableByteChannel channel){
        if( channel==null ){
            throw new IllegalArgumentException("channel == null");
        }
        this.channel = channel;
    }

    public SeekableByteChannel getChannel(){
        return channel;
    }

    @Override
    public synchronized void close() {
        if( channel!=null ){
            try {
                channel.close();
            } catch (IOException ex) {
                throw new IOError(ex);
            }
            channel = null;
        }
    }

    @Override
    @SuppressWarnings("FinalizeDeclaration")
    protected void finalize() throws Throwable {
        synchronized(this){
            if( closeOnFinalize ){
                close();
            }
        }
        super.finalize();
    }

    @Override
    public synchronized long getSize() {
        try {
            if( channel==null )throw new IllegalStateException("buffer is closed");
            return channel.size();
        } catch (IOException ex) {
            throw new IOError(ex);
        }
    }

    @Override
    public synchronized void setSize(long size) {
        if( channel==null )throw new IllegalStateException("buffer is closed");
        if( size<0 )throw new IllegalArgumentException("size < 0");
        long cursize = size;
        if( cursize>size ){
            try {
                channel.truncate(size);
            } catch (IOException ex) {
                throw new IOError(ex);
            }
        }else if( cursize<size ){
            try {
                if( size>0 ){
                    channel.position(size-1);
                    byte b0 = (byte)0;
                    byte[] barr =new byte[]{b0};
                    channel.write(ByteBuffer.wrap(barr));
                }
            } catch (IOException ex) {
                throw new IOError(ex);
            }
        }
    }

    @Override
    public synchronized void set(long offset, byte[] data, int dataOffset, int dataLen) {
        if( channel==null )throw new IllegalStateException("buffer is closed");
        if( offset<0 )throw new IllegalStateException("offset<0");
        if( data==null )throw new IllegalStateException("data == null");
        if( dataOffset<0 )throw new IllegalStateException("dataOffset<0");
        if( dataLen<0 )throw new IllegalStateException("dataLen<0");
        if( dataOffset+dataLen>data.length )
            throw new IllegalStateException(
                "dataOffset("+dataOffset+")+dataLen("+dataLen+")>data.length("+data.length+")"
            );
        try {
            channel.position(offset);
            channel.write(
                ByteBuffer.wrap(data, dataOffset, dataLen)
            );
        } catch (IOException ex) {
            throw new IOError(ex);
        }
    }

    @Override
    public synchronized byte[] get(long offset, int dataLen) {
        if( channel==null )throw new IllegalStateException("buffer is closed");
        if( dataLen<0 )throw new IllegalStateException("dataLen<0");
        if( offset<0 )throw new IllegalStateException("offset<0");
        if( dataLen==0 )return new byte[0];
        try {
            channel.position(offset);
            ByteBuffer bb = ByteBuffer.allocate(dataLen);
            int readed = channel.read(bb);
            if( readed<=0 )return new byte[0];
            bb.position(0);
            byte[] data = bb.array();
            if( readed<dataLen ){
                return Arrays.copyOf(data, readed);
            }
            return data;
        } catch (IOException ex) {
            throw new IOError(ex);
        }
    }

    @Override
    public synchronized void clear() {
        if( channel==null )throw new IllegalStateException("buffer is closed");
        synchronized(this){
            try {
                channel.truncate(0);
            } catch (IOException ex) {
                throw new IOError(ex);
            }
        }
    }

    @Override
    public synchronized ContentBuffer clone() {
        if( channel==null )throw new IllegalStateException("buffer is closed");
        throw new IllegalArgumentException("unsupported");
    }

    @Override
    public synchronized void flush() {
    }
}
