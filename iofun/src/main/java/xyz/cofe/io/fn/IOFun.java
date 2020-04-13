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

package xyz.cofe.io.fn;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Функции обработки Stream
 * @author user
 */
public class IOFun {
    private static final Lock lock = new ReentrantLock();

    private static int defaultBlockSize = -1;
    private static int getDefaultBlockSize(){
        try{
            lock.lock();

            if( defaultBlockSize<0 ){
                defaultBlockSize = 1024 * 8;
            }
            return defaultBlockSize;
        }finally{
            lock.unlock();
        }
    }

    //<editor-fold defaultstate="collapsed" desc="copy()">
    /**
     * Копирует данные из входного потока в выходной поток. <br>
     * Процесс копирования можно прервать послав потоку (Thread) сигнал прерывания.
     * @param from Из какого потока копировать
     * @param to В какой поток копировать
     * @param maxSize Максимальное кол-во копируемых данных (0 и меньше - копирование до конца)
     * @param blockSize Размер блока копируемых данных (0 и меньше - по умолчанию)
     * @param progress Функция уведомления о прогрессе (возможно null)
     * аргумент функции - кол-во прочитаных байтов
     * @return Кол-во скопированных байтов
     * @throws java.io.IOException Если не может скопировать данные
     */
    public static long copy(
        InputStream from,
        OutputStream to,
        long maxSize,
        int blockSize,
        Consumer<Long> progress
    ) throws IOException
    {
        if (from == null) {
            throw new IllegalArgumentException("from == null");
        }
        if (to == null) {
            throw new IllegalArgumentException("to == null");
        }
        if( blockSize<1 )blockSize = getDefaultBlockSize();

        long total = 0;
        while(true)
        {
//            if( Thread.interrupted() )break;
            if( Thread.currentThread().isInterrupted() )break;

            int bs = blockSize;
            if( maxSize>0 ){
                long residue = maxSize - total;
                if( residue<=0 )break;

                if( residue > blockSize ){
                    bs = blockSize;
                }else{
                    bs = (int)residue;
                }
            }

            byte[] buff = new byte[bs];
            int rd = from.read(buff);

            if( rd<0 )break;
            to.write(buff, 0, rd);
            total += rd;

            if( progress!=null ){
                try{
                    progress.accept(total);
                }catch( Throwable err ){
                    Logger.getLogger(IOFun.class.getName())
                        .log( Level.WARNING, "error: "+err.getMessage(), err );
                }
            }
        }
        return total;
    }

    /**
     * Копирует данные из входного потока в выходной поток. <br>
     * Процесс копирования можно прервать послав потоку (Thread) сигнал прерывания.
     * @param from Из какого потока копировать
     * @param to В какой поток копировать
     * @param maxSize Максимальное кол-во копируемых данных (0 и меньше - копирование до конца)
     * @return Кол-во скопированных байтов
     * @throws java.io.IOException Если не может скопировать данные
     */
    public static long copy(
        InputStream from,
        OutputStream to,
        long maxSize
    ) throws IOException
    {
        return copy(from, to, maxSize, -1, null);
    }

    /**
     * Копирует данные из входного потока в выходной поток. <br>
     * Процесс копирования можно прервать послав потоку (Thread) сигнал прерывания.
     * @param from Из какого потока копировать
     * @param to В какой поток копировать
     * @return Кол-во скопированных байтов
     * @throws java.io.IOException Если не может скопировать данные
     */
    public static long copy(
        InputStream from,
        OutputStream to
    ) throws IOException
    {
        return copy(from, to, -1, -1, null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readBytes()">
    /**
     * Чтение байтового потока
     * @param from байтовый поток данных
     * @param maxSize Максимальное кол-во копируемых данных (0 и меньше - копирование до конца)
     * @param blockSize Размер блока копируемых данных (0 и меньше - по умолчанию)
     * @param progress Функция уведомления о прогрессе (возможно null),
     * аргумент функции - кол-во прочитаных байтов
     * @return Набор байтов
     * @throws IOException Ошибка ввода - вывода
     */
    public static byte[] readBytes(
        InputStream from,
        int maxSize,
        int blockSize,
        Consumer<Long> progress
    ) throws IOException {
        if( from==null )throw new IllegalArgumentException( "from==null" );

        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        copy(from, ba, maxSize, blockSize, progress);
        return ba.toByteArray();
    }

    /**
     * Чтение байтового потока
     * @param from байтовый поток данных
     * @param maxSize Максимальное кол-во копируемых данных (0 и меньше - копирование до конца)
     * @return Набор байтов
     * @throws IOException Ошибка ввода - вывода
     */
    public static byte[] readBytes(
        InputStream from,
        int maxSize
    ) throws IOException {
        if( from==null )throw new IllegalArgumentException( "from==null" );

        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        copy(from, ba, maxSize, -1, null);
        return ba.toByteArray();
    }

    /**
     * Чтение байтового потока
     * @param from байтовый поток данных
     * @return Набор байтов
     * @throws IOException Ошибка ввода - вывода
     */
    public static byte[] readBytes(
        InputStream from
    ) throws IOException {
        if( from==null )throw new IllegalArgumentException( "from==null" );

        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        copy(from, ba, -1, -1, null);
        return ba.toByteArray();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="writeBytes()">
    /**
     * Запись байт данных в поток
     * @param to Поток в который происходит запись
     * @param data Набор байтов которых необходимо записать
     * @param dataOffset Смещение в наборе данных
     * @param dataSize Кол-во байт необходимых для записи
     * @param blockSize Размер блока копируемых данных (0 и меньше - по умолчанию)
     * @param progress Функция уведомления копируемых данных (возможно null), <br>
     * первый аргумент - кол-во записанных байтов, <br>
     * второй аргумент - кол-во всего копируемых данных <br>
     * @throws IOException Ошибка ввода - вывода
     */
    public static void writeBytes(
        OutputStream to,
        byte[] data,
        int dataOffset,
        int dataSize,
        int blockSize,
        BiConsumer<Long,Long> progress
    ) throws IOException
    {
        if( to==null )throw new IllegalArgumentException( "to==null" );
        if( data==null )throw new IllegalArgumentException( "data==null" );
        if( dataOffset<0 )throw new IllegalArgumentException( "dataOffset("+dataOffset+")<0" );
        if( dataSize<0 )throw new IllegalArgumentException( "dataSize("+dataOffset+")<0" );
        if( blockSize<1 )blockSize = getDefaultBlockSize();

        if( dataSize==0 ){
            if( progress!=null ){
                progress.accept((long)0, (long)0);
            }
            return;
        }

        int total = 0;
        while( true ){
            int residue = dataSize - total;
            if( residue<=0 )break;

            int bs = blockSize;
            if( residue>blockSize ){
                bs = blockSize;
            }else{
                bs = (int)residue;
            }

            to.write(data, dataOffset + total, bs);
            total += bs;

            if( progress!=null ){
                try{
                    progress.accept((long)total, (long)dataSize);
                }catch( Throwable err ){
                    Logger.getLogger(IOFun.class.getName())
                        .log( Level.WARNING, "error: "+err.getMessage(), err );
                }
            }
        }

        to.flush();
    }

    /**
     * Запись байт данных в поток
     * @param to Поток в который происходит запись
     * @param data Набор байтов которых необходимо записать
     * @param dataOffset Смещение в наборе данных
     * @param dataSize Кол-во байт необходимых для записи
     * @throws IOException Ошибка ввода - вывода
     */
    public static void writeBytes(
        OutputStream to,
        byte[] data,
        int dataOffset,
        int dataSize
    ) throws IOException
    {
        writeBytes(to, data, dataOffset, dataSize, -1, null );
    }

    /**
     * Запись байт данных в поток
     * @param to Поток в который происходит запись
     * @param data Набор байтов которых необходимо записать
     * @throws IOException Ошибка ввода - вывода
     */
    public static void writeBytes(
        OutputStream to,
        byte[] data
    ) throws IOException
    {
        if( data==null )throw new IllegalArgumentException( "data==null" );
        writeBytes(to, data, 0, data.length, -1, null );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readText()">
    /**
     * Чтение текстовых данных
     * @param from Из какого потока копировать
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @param maxSize Максимальное кол-во копируемых данных (0 и меньше - копирование до конца)
     * @param blockSize Размер блока копируемых данных (0 и меньше - по умолчанию)
     * @param progress Функция уведомления о прогрессе (возможно null) <br>
     * аргумент функции - кол-во прочитаных байтов
     * @return Текстовые данные
     * @throws IOException Ошибка ввода - вывода
     */
    public static String readText(
        InputStream from,
        Charset cs,
        int maxSize,
        int blockSize,
        Consumer<Long> progress
    ) throws IOException {
        if( from==null )throw new IllegalArgumentException( "from==null" );
        if( cs==null )cs = Charset.defaultCharset();
        byte[] bytes = readBytes(from, maxSize, blockSize, progress);
        return new String(bytes, cs);
    }

    /**
     * Чтение текстовых данных
     * @param from Из какого потока копировать
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @param maxSize Максимальное кол-во копируемых данных (0 и меньше - копирование до конца)
     * @return Текстовые данные
     * @throws IOException Ошибка ввода - вывода
     */
    public static String readText(
        InputStream from,
        Charset cs,
        int maxSize
    ) throws IOException {
        if( from==null )throw new IllegalArgumentException( "from==null" );
        if( cs==null )cs = Charset.defaultCharset();
        byte[] bytes = readBytes(from, maxSize, -1, null);
        return new String(bytes, cs);
    }

    /**
     * Чтение текстовых данных
     * @param from Из какого потока копировать
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @return Текстовые данные
     * @throws IOException Ошибка ввода - вывода
     */
    public static String readText(
        InputStream from,
        String cs
    ) throws IOException {
        return readText(from,cs!=null ? Charset.forName(cs) : Charset.defaultCharset());
    }

    /**
     * Чтение текстовых данных
     * @param from Из какого потока копировать
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @return Текстовые данные
     * @throws IOException Ошибка ввода - вывода
     */
    public static String readText(
        InputStream from,
        Charset cs
    ) throws IOException {
        if( from==null )throw new IllegalArgumentException( "from==null" );
        if( cs==null )cs = Charset.defaultCharset();
        byte[] bytes = readBytes(from, -1, -1, null);
        return new String(bytes, cs);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="writeText()">
    /**
     * Запись текстовых данных в поток
     * @param to Поток в который происходит запись
     * @param string Текстовые данные
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @param blockSize Размер блока копируемых данных (0 и меньше - по умолчанию)
     * @param progress Функция уведомления копируемых данных (возможно null), <br>
     * первый аргумент - кол-во записанных байтов, <br>
     * второй аргумент - кол-во всего копируемых данных <br>
     * @throws IOException Ошибка ввода - вывода
     */
    public static void writeText(
        OutputStream to,
        String string,
        Charset cs,
        int blockSize,
        BiConsumer<Long,Long> progress
    ) throws IOException
    {
        if( to==null )throw new IllegalArgumentException( "to==null" );
        if( string==null )throw new IllegalArgumentException( "string==null" );
        if( blockSize<1 )blockSize = getDefaultBlockSize();
        if( cs==null )cs = Charset.defaultCharset();

        byte[] data = string.getBytes(cs);
        writeBytes(to, data, 0, data.length, blockSize, progress);
    }

    /**
     * Запись текстовых данных в поток
     * @param to Поток в который происходит запись
     * @param string Текстовые данные
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @throws IOException Ошибка ввода - вывода
     */
    public static void writeText(
        OutputStream to,
        String string,
        String cs
    ) throws IOException {
        writeText(to,string,cs!=null ? Charset.forName(cs) : Charset.defaultCharset());
    }

    /**
     * Запись текстовых данных в поток
     * @param to Поток в который происходит запись
     * @param string Текстовые данные
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @throws IOException Ошибка ввода - вывода
     */
    public static void writeText(
        OutputStream to,
        String string,
        Charset cs
    ) throws IOException
    {
        if( to==null )throw new IllegalArgumentException( "to==null" );
        if( string==null )throw new IllegalArgumentException( "string==null" );
        if( cs==null )cs = Charset.defaultCharset();

        byte[] data = string.getBytes(cs);
        writeBytes(to, data, 0, data.length, -1, null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readBytes( File .. )">
    /**
     * Чтение байт данных
     * @param file Файл из которого производится чтение
     * @param maxSize Максимальный объем читаемых данных
     * @param blockSize Размер блока копируемых данных (0 и меньше - по умолчанию)
     * @param progress Функция уведомления копируемых данных (возможно null), <br>
     * первый аргумент - кол-во записанных байтов, <br>
     * второй аргумент - кол-во всего копируемых данных <br>
     * @return Набор байтов
     * @throws IOException Ошибка ввода - вывода
     */
    public static byte[] readBytes(
        java.io.File file,
        int maxSize,
        int blockSize,
        BiConsumer<Long,Long> progress
    )
        throws IOException
    {
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if( blockSize<1 )blockSize = getDefaultBlockSize();

        final long fmaxsize = maxSize;
        long total = maxSize >= 0 ? maxSize : -1;
        final java.io.File fil = file;

        final BiConsumer<Long,Long> prgs = progress;

        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        FileInputStream fin = new FileInputStream(file);

        copy( fin, ba, total, blockSize, new Consumer<Long>() {
            @Override
            public void accept(Long readedTotal) {
                if( prgs!=null ){
                    try{
                        long fileSize = fil.length();
                        long total = fmaxsize >= 0 ? fmaxsize : fileSize;
                        prgs.accept(readedTotal, total);
                    }catch( Throwable err ){
                        Logger.getLogger(IOFun.class.getName())
                            .log( Level.WARNING, "error: "+err.getMessage(), err );
                    }
                }
            }
        });
        fin.close();

        return ba.toByteArray();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="writeBytes( File .. )">
    /**
     * Запись байт данных в файл
     * @param file Файл в который производится запись
     * @param data Набор байтов которых необходимо записать
     * @param dataOffset Смещение в наборе данных
     * @param dataSize Кол-во байт необходимых для записи
     * @param blockSize Размер блока копируемых данных (0 и меньше - по умолчанию)
     * @param progress Функция уведомления копируемых данных (возможно null), <br>
     * первый аргумент - кол-во записанных байтов, <br>
     * второй аргумент - кол-во всего копируемых данных
     * @throws IOException Ошибка ввода - вывода
     */
    public static void writeBytes(
        java.io.File file,
        byte[] data,
        int dataOffset,
        int dataSize,
        int blockSize,
        BiConsumer<Long,Long> progress
    ) throws IOException {
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if( data==null )throw new IllegalArgumentException( "data==null" );
        if( dataOffset<0 )throw new IllegalArgumentException( "dataOffset("+dataOffset+")<0" );
        if( dataSize<0 )throw new IllegalArgumentException( "dataSize("+dataOffset+")<0" );
        if( blockSize<1 )blockSize = getDefaultBlockSize();

        FileOutputStream fout = new FileOutputStream(file);
        writeBytes(fout, data, dataOffset, dataSize, blockSize, progress);
        fout.flush();
        fout.close();

        if( dataSize==0 ){
            if( progress!=null ){
                try{
                    progress.accept((long)0, (long)0);
                }catch( Throwable err ){
                    Logger.getLogger(IOFun.class.getName())
                        .log( Level.WARNING, "error: "+err.getMessage(), err );
                }
            }
        }
    }

    /**
     * Запись байт данных в файл
     * @param file Файл в который производится запись
     * @param data Набор байтов которых необходимо записать
     * @param dataOffset Смещение в наборе данных
     * @param dataSize Кол-во байт необходимых для записи
     * @throws IOException Ошибка ввода - вывода
     */
    public static void writeBytes(
        java.io.File file,
        byte[] data,
        int dataOffset,
        int dataSize
    ) throws IOException {
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if( data==null )throw new IllegalArgumentException( "data==null" );
        if( dataOffset<0 )throw new IllegalArgumentException( "dataOffset("+dataOffset+")<0" );
        if( dataSize<0 )throw new IllegalArgumentException( "dataSize("+dataOffset+")<0" );

        FileOutputStream fout = new FileOutputStream(file);
        writeBytes(fout, data, dataOffset, dataSize, -1, null);
        fout.flush();
        fout.close();
    }

    /**
     * Запись байт данных в файл
     * @param file Файл в который производится запись
     * @param data Набор байтов которых необходимо записать
     * @throws IOException Ошибка ввода - вывода
     */
    public static void writeBytes(
        java.io.File file,
        byte[] data
    ) throws IOException {
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if( data==null )throw new IllegalArgumentException( "data==null" );

        FileOutputStream fout = new FileOutputStream(file);
        writeBytes(fout, data, 0, data.length, -1, null);
        fout.flush();
        fout.close();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="appendBytes( File .. )">
    /**
     * Запись байт данных в конец файла
     * @param file Файл в который производится запись
     * @param data Набор байтов которых необходимо записать
     * @param dataOffset Смещение в наборе данных
     * @param dataSize Кол-во байт необходимых для записи
     * @param blockSize Размер блока копируемых данных (0 и меньше - по умолчанию)
     * @param progress Функция уведомления копируемых данных (возможно null), <br>
     * первый аргумент - кол-во записанных байтов, <br>
     * второй аргумент - кол-во всего копируемых данных
     * @throws IOException Ошибка ввода - вывода
     */
    public static void appendBytes(
        java.io.File file,
        byte[] data,
        int dataOffset,
        int dataSize,
        int blockSize,
        BiConsumer<Long,Long> progress
    ) throws IOException {
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if( data==null )throw new IllegalArgumentException( "data==null" );
        if( dataOffset<0 )throw new IllegalArgumentException( "dataOffset("+dataOffset+")<0" );
        if( dataSize<0 )throw new IllegalArgumentException( "dataSize("+dataOffset+")<0" );
        if( blockSize<1 )blockSize = getDefaultBlockSize();

        FileOutputStream fout = new FileOutputStream(file, true);
        writeBytes(fout, data, dataOffset, dataSize, blockSize, progress);
        fout.flush();
        fout.close();

        if( dataSize==0 ){
            if( progress!=null ){
                try{
                    progress.accept((long)0, (long)0);
                }catch( Throwable err ){
                    Logger.getLogger(IOFun.class.getName())
                        .log( Level.WARNING, "error: "+err.getMessage(), err );
                }
            }
        }
    }

    /**
     * Запись байт данных в конец файла
     * @param file Файл в который производится запись
     * @param data Набор байтов которых необходимо записать
     * @param dataOffset Смещение в наборе данных
     * @param dataSize Кол-во байт необходимых для записи
     * @throws IOException Ошибка ввода - вывода
     */
    public static void appendBytes(
        java.io.File file,
        byte[] data,
        int dataOffset,
        int dataSize
    ) throws IOException {
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if( data==null )throw new IllegalArgumentException( "data==null" );
        if( dataOffset<0 )throw new IllegalArgumentException( "dataOffset("+dataOffset+")<0" );
        if( dataSize<0 )throw new IllegalArgumentException( "dataSize("+dataOffset+")<0" );

        appendBytes(file, data, dataOffset, dataSize, -1, null);
    }

    /**
     * Запись байт данных в конец файла
     * @param file Файл в который производится запись
     * @param data Набор байтов которых необходимо записать
     * @throws IOException Ошибка ввода - вывода
     */
    public static void appendBytes(
        java.io.File file,
        byte[] data
    ) throws IOException {
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if( data==null )throw new IllegalArgumentException( "data==null" );

        appendBytes(file, data, 0, data.length, -1, null);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readText( File .. )">
    /**
     * Чтение текстовых данных
     * @param file Файл из которого производится чтение
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @param maxSize Максимальный объем читаемых данных
     * @param blockSize Размер блока копируемых данных (0 и меньше - по умолчанию)
     * @param progress Функция уведомления копируемых данных (возможно null), <br>
     * первый аргумент - кол-во записанных байтов, <br>
     * второй аргумент - кол-во всего копируемых данных <br>
     * @return Текстовые данные
     * @throws IOException Ошибка ввода - вывода
     */
    public static String readText(
        java.io.File file,
        Charset cs,
        int maxSize,
        int blockSize,
        BiConsumer<Long,Long> progress
    ) throws IOException {
        if( cs==null )cs = Charset.defaultCharset();
        if( file==null )throw new IllegalArgumentException( "file==null" );

        byte[] data = readBytes(file, maxSize, blockSize, progress);
        return new String(data, cs);
    }

    /**
     * Чтение текстовых данных
     * @param file Файл из которого производится чтение
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @param maxSize Максимальный объем читаемых данных
     * @return Текстовые данные
     * @throws IOException Ошибка ввода - вывода
     */
    public static String readText(
        java.io.File file,
        Charset cs,
        int maxSize
    ) throws IOException {
        if( cs==null )cs = Charset.defaultCharset();
        if( file==null )throw new IllegalArgumentException( "file==null" );

        byte[] data = readBytes(file, maxSize, -1, null);
        return new String(data, cs);
    }

    /**
     * Чтение текстовых данных
     * @param file Файл из которого производится чтение
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @return Текстовые данные
     * @throws IOException Ошибка ввода - вывода
     */
    public static String readText(
        java.io.File file,
        String cs
    ) throws IOException {
        return readText(file,cs!=null ? Charset.forName(cs) : Charset.defaultCharset());
    }

    /**
     * Чтение текстовых данных
     * @param file Файл из которого производится чтение
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @return Текстовые данные
     * @throws IOException Ошибка ввода - вывода
     */
    public static String readText(
        java.io.File file,
        Charset cs
    ) throws IOException {
        if( cs==null )cs = Charset.defaultCharset();
        if( file==null )throw new IllegalArgumentException( "file==null" );

        byte[] data = readBytes(file, -1, -1, null);
        return new String(data, cs);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="writeText( File .. )">
    /**
     * Запись текстовых данных в файл
     * @param file Файл в который производится запись
     * @param string Текстовые данные
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @param blockSize Размер блока копируемых данных (0 и меньше - по умолчанию)
     * @param progress Функция уведомления копируемых данных (возможно null), <br>
     * первый аргумент - кол-во записанных байтов, <br>
     * второй аргумент - кол-во всего копируемых данных
     * @throws IOException Ошибка ввода - вывода
     */
    public static void writeText(
        java.io.File file,
        String string,
        Charset cs,
        int blockSize,
        BiConsumer<Long,Long> progress
    ) throws IOException
    {
        if( cs==null )cs = Charset.defaultCharset();
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if( string==null )throw new IllegalArgumentException( "string==null" );

        FileOutputStream fout = new FileOutputStream(file);
        writeText(fout, string, cs, blockSize, progress);
        fout.flush();
        fout.close();
    }

    /**
     * Запись текстовых данных в файл
     * @param file Файл в который производится запись
     * @param string Текстовые данные
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @throws IOException Ошибка ввода - вывода
     */
    public static void writeText(
        java.io.File file,
        String string,
        String cs
    ) throws IOException
    {
        writeText(file, string, cs!=null ? Charset.forName(cs) : Charset.defaultCharset());
    }

    /**
     * Запись текстовых данных в файл
     * @param file Файл в который производится запись
     * @param string Текстовые данные
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @throws IOException Ошибка ввода - вывода
     */
    public static void writeText(
        java.io.File file,
        String string,
        Charset cs
    ) throws IOException
    {
        if( cs==null )cs = Charset.defaultCharset();
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if( string==null )throw new IllegalArgumentException( "string==null" );

        FileOutputStream fout = new FileOutputStream(file);
        writeText(fout, string, cs, -1, null);
        fout.flush();
        fout.close();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="appendText( File .. )">
    /**
     * Запись текстовых данных в конец файла
     * @param file Файл в который производится запись
     * @param string Текстовые данные
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @param blockSize Размер блока копируемых данных (0 и меньше - по умолчанию)
     * @param progress Функция уведомления копируемых данных (возможно null), <br>
     * первый аргумент - кол-во записанных байтов, <br>
     * второй аргумент - кол-во всего копируемых данных
     * @throws IOException Ошибка ввода - вывода
     */
    public static void appendText(
        java.io.File file,
        String string,
        Charset cs,
        int blockSize,
        BiConsumer<Long,Long> progress
    ) throws IOException
    {
        if( cs==null )cs = Charset.defaultCharset();
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if( string==null )throw new IllegalArgumentException( "string==null" );

        FileOutputStream fout = new FileOutputStream(file,true);
        writeText(fout, string, cs, blockSize, progress);
        fout.flush();
        fout.close();
    }

    /**
     * Запись текстовых данных в конец файла
     * @param file Файл в который производится запись
     * @param string Текстовые данные
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @throws IOException Ошибка ввода - вывода
     */
    public static void appendText(
        java.io.File file,
        String string,
        Charset cs
    ) throws IOException
    {
        if( cs==null )cs = Charset.defaultCharset();
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if( string==null )throw new IllegalArgumentException( "string==null" );

        FileOutputStream fout = new FileOutputStream(file,true);
        writeText(fout, string, cs, -1, null);
        fout.flush();
        fout.close();
    }

    /**
     * Запись текстовых данных в конец файла
     * @param file Файл в который производится запись
     * @param string Текстовые данные
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @throws IOException Ошибка ввода - вывода
     */
    public static void appendText(
        java.io.File file,
        String string,
        String cs
    ) throws IOException {
        appendText(file, string, cs!=null ? Charset.forName(cs) : Charset.defaultCharset());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readBytes(URL ..)">
    /**
     * Чтение байт данных
     * @param url Файл из которого производится чтение
     * @param maxSize  Максимальный объем читаемых данных
     * @param blockSize Размер блока копируемых данных (0 и меньше - по умолчанию)
     * @param progress Функция уведомления о прогрессе (возможно null) <br>
     * аргумент функции - кол-во прочитаных байтов
     * @return Набор байтов
     * @throws IOException Ошибка ввода - вывода
     */
    public static byte[] readBytes(
        java.net.URL url,
        int maxSize,
        int blockSize,
        Consumer<Long> progress
    ) throws IOException
    {
        if( url==null )throw new IllegalArgumentException( "url==null" );
        InputStream input = url.openStream();
        byte[] res = readBytes(input, maxSize, blockSize, progress);
        input.close();
        return res;
    }

    /**
     * Чтение байт данных
     * @param url Файл из которого производится чтение
     * @return Набор байтов
     * @throws IOException Ошибка ввода - вывода
     */
    public static byte[] readBytes(
        java.net.URL url
    ) throws IOException
    {
        return readBytes(url, -1, -1, null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readText(URL ..)">
    /**
     * Чтение текстовых данных
     * @param url Файл из которого производится чтение
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @param maxSize Максимальный объем читаемых данных
     * @param blockSize Размер блока копируемых данных (0 и меньше - по умолчанию)
     * @param progress Функция уведомления о прогрессе (возможно null) <br>
     * аргумент функции - кол-во прочитаных байтов
     * @return Текстовые данные
     * @throws IOException Ошибка ввода - вывода
     */
    public static String readText(
        java.net.URL url,
        Charset cs,
        int maxSize,
        int blockSize,
        Consumer<Long> progress
    ) throws IOException
    {
        if( cs==null )cs = Charset.defaultCharset();
        byte[] data = readBytes(url, maxSize, blockSize, progress);
        return new String(data, cs);
    }

    /**
     * Чтение текстовых данных
     * @param url Файл из которого производится чтение
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @return Текстовые данные
     * @throws IOException Ошибка ввода - вывода
     */
    public static String readText(
        java.net.URL url,
        Charset cs
    )  throws IOException {
        if( cs==null )cs = Charset.defaultCharset();
        byte[] data = readBytes(url, -1, -1, null);
        return new String(data, cs);
    }

    /**
     * Чтение текстовых данных
     * @param url Файл из которого производится чтение
     * @param cs Кодировка (возможно null, будет использоваться кодировка по умолчанию)
     * @return Текстовые данные
     * @throws IOException Ошибка ввода - вывода
     */
    public static String readText(
        java.net.URL url,
        String cs
    )  throws IOException {
        return readText(url, cs!=null ? Charset.forName(cs) : Charset.defaultCharset()) ;
    }
    //</editor-fold>
}
