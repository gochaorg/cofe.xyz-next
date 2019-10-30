/*
 * The MIT License
 *
 * Copyright 2016 Kamnev Georgiy (nt.gocha@gmail.com).
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


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Буфер обвертка
 * @param <T> Тип обворачиваемого буфера
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class WrapperContentBuffer<T extends ContentBuffer>
    implements ContentBuffer
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(WrapperContentBuffer.class.getName());
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

    protected T buffer;

    public WrapperContentBuffer( T cbuffer ){
        if( cbuffer==null )throw new IllegalArgumentException( "cbuffer==null" );
        this.buffer = cbuffer;
        this.lock = new ReentrantLock();
    }

    public WrapperContentBuffer( T cbuffer, Lock lock ){
        if( cbuffer==null )throw new IllegalArgumentException( "cbuffer==null" );
        this.buffer = cbuffer;
        this.lock = lock==null ? new ReentrantLock() : lock;
    }

    public T getWrappedBuffer(){
        try {
            lock.lock();
            return buffer;
        }
        finally {
            lock.unlock();
        }
    }

    public void setWrappedBuffer( T cbuffer ){
        try {
            lock.lock();
            if( buffer==null )throw new IllegalArgumentException( "buffer==null" );
            this.buffer = cbuffer;
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public long getSize() {
        try {
            lock.lock();
            return buffer.getSize();
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void setSize(long size) {
        try {
            lock.lock();
            buffer.setSize(size);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void set(long offset, byte[] data, int dataOffset, int dataLen) {
        try {
            lock.lock();
            buffer.set(offset, data, dataOffset, dataLen);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public byte[] get(long offset, int dataLen) {
        try {
            lock.lock();
            return buffer.get(offset, dataLen);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        try {
            lock.lock();
            buffer.clear();
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void flush() {
        try {
            lock.lock();
            buffer.flush();
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        try {
            lock.lock();
            buffer.close();
        }
        finally {
            lock.unlock();
        }
    }
}
