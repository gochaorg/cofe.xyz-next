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


import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.io.fn.IOFun;

/**
 * Вспомогательные функции для ContentBuffer
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class ContentBufferFuncs {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ContentBufferFuncs.class.getName());
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

    /**
     * Записывает поток в буффер
     * @param stream Поток из которого происходит чтение
     * @param cbuf Буффер в который производиться запись
     * @return Кол-во скопированных байтов
     */
    public long readStreamInto( InputStream stream, ContentBuffer cbuf )
    {
        return readStreamInto(stream, 0, -1, cbuf, 0, -1, 1024 * 8, null);
    }

    /**
     * Записывает поток в буффер
     * @param stream Поток из которого происходит чтение
     * @param skip Сколько байт пропустить в начале
     * @param maxRead Сколько прочесть максимально байт (0 и меньше - копирование до конца)
     * @param cbuf Буффер в который производиться запись
     * @param bufferBeginIndex С какой позиции в буфере начать запись (0 - начало)
     * @param bufferEndIndexExclusive По какую позицию (искл.) закончить запись (-1 - до конца буфера, пусть растет)
     * @param blockSize Размер блока чтения за раз
     * @param progress Прогресс чтения/записи - кол-во скопированых байтов, возможно null
     * @return Кол-во скопированных байтов
     */
    public long readStreamInto(
        final InputStream stream,
        final long skip,
        final long maxRead,
        final ContentBuffer cbuf,
        long bufferBeginIndex,
        long bufferEndIndexExclusive,
        final int blockSize,
        final Consumer<Long> progress
    )
    {
        if( stream==null )throw new IllegalArgumentException( "stream==null" );
        if( cbuf==null )throw new IllegalArgumentException( "cbuf==null" );
        if( blockSize<1 )throw new IllegalArgumentException( "blockSize<1" );

        if( bufferBeginIndex>=0 && bufferEndIndexExclusive>=0 && bufferBeginIndex>bufferEndIndexExclusive ){
            long t = bufferBeginIndex;
            bufferBeginIndex = bufferEndIndexExclusive;
            bufferEndIndexExclusive = t;
        }

        final long fbuffBeginIndex = bufferBeginIndex;
        final long fbuffEndIndexExc = bufferEndIndexExclusive;

        Object osync = (cbuf instanceof SyncContentBuffer)
            ? ((SyncContentBuffer)cbuf).getSyncObject()
            : null;

        Supplier fn = new Supplier() {
            @Override
            public Object get() {
                ContentBufferOutputStream cout = null;
                if( fbuffBeginIndex<0 ){
                    if( fbuffEndIndexExc<0 ){
                        cout = new ContentBufferOutputStream(cbuf);
                    }else{
                        cout = new ContentBufferOutputStream(cbuf,0,fbuffEndIndexExc);
                    }
                }else{
                    if( fbuffEndIndexExc<0 ){
                        cout = new ContentBufferOutputStream(cbuf,fbuffBeginIndex);
                    }else{
                        cout = new ContentBufferOutputStream(cbuf,fbuffBeginIndex,fbuffEndIndexExc);
                    }
                }

                long total = 0;
                try{
                    if( skip>0 ){
                        byte[] dblock = new byte[blockSize];
                        long skipped = 0;
                        while( skipped<skip ){
                            // остаток
                            int toread = 0;
                            long residue = skip - skipped;
                            if( residue>blockSize ){
                                toread = blockSize;
                            }else{
                                toread = (int)residue;
                            }
                            int readed = stream.read(dblock,0,toread);
                            if( readed<0 )break;
                            skipped += readed;
                        }
                    }

                    total = IOFun.copy(stream, cout, maxRead, blockSize, progress);
                }
                catch(IOException ex){
                    throw new IOError(ex);
                }

                try {
                    cout.close();
                } catch (IOException ex) {
                    logException(ex);
                }
                return total;
            }
        };

        if( osync!=null ){
            synchronized(osync){
                return (Long)fn.get();
            }
        }else{
            return (Long)fn.get();
        }
    }
}
