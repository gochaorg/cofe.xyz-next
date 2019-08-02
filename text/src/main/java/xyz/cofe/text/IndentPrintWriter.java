/*
 * The MIT License
 *
 * Copyright 2014 Kamnev Georgiy (nt.gocha@gmail.com).
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
package xyz.cofe.text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Поток вывода символов с поддержкой отступов и интерфеса печати
 * @author gocha
 */
public class IndentPrintWriter extends PrintWriter
{
    private IndentWriter indentWriter = null;
    private OutputStream toClose = null;

    /**
     * Конструктор по умолчанию
     */
    public IndentPrintWriter()
    {
        super(initWriter());
        indentWriter = lastInitedWriter;
        toClose = lastStreamforClose;
    }

    /**
     * Конструктор
     * @param writer целевой поток в который пишется текст
     */
    public IndentPrintWriter(Writer writer)
    {
        super(initWriter(writer));
        indentWriter = lastInitedWriter;
        toClose = lastStreamforClose;
    }

    /**
     * Конструктор
     * @param writer файл для записи
     */
    public IndentPrintWriter(File writer)
    {
        super(initWriter(writer,false,(String)null));
        indentWriter = lastInitedWriter;
        toClose = lastStreamforClose;
    }

    /**
     * Конструктор
     * @param writer файл для записи
     * @param cs кодировка
     */
    public IndentPrintWriter(File writer,Charset cs)
    {
        super(initWriter(writer,false,cs));
        indentWriter = lastInitedWriter;
        toClose = lastStreamforClose;
    }

    /**
     * Конструктор
     * @param writer файл для записи
     * @param cs кодировка
     * @param append добавлять в конец файла
     */
    public IndentPrintWriter(File writer,Charset cs,boolean append)
    {
        super(initWriter(writer,append,cs));
        indentWriter = lastInitedWriter;
        toClose = lastStreamforClose;
    }

    /**
     * Конструктор
     * @param writer файл для записи
     * @param charset кодировка
     */
    public IndentPrintWriter(File writer,String charset)
    {
        super(initWriter(writer,false,charset));
        indentWriter = lastInitedWriter;
        toClose = lastStreamforClose;
    }

    /**
     * Конструктор
     * @param writer файл для записи
     * @param charset кодировка
     * @param append добавлять в конец файла
     */
    public IndentPrintWriter(File writer,String charset,boolean append)
    {
        super(initWriter(writer,append,charset));
        indentWriter = lastInitedWriter;
        toClose = lastStreamforClose;
    }

    /**
     * Конструктор
     * @param outputStream целевой поток
     */
    public IndentPrintWriter(OutputStream outputStream)
    {
        super(initWriter(outputStream));
        indentWriter = lastInitedWriter;
        toClose = lastStreamforClose;
    }

    /**
     * Конструктор
     * @param outputStream целевой поток
     * @param cs кодировка
     */
    public IndentPrintWriter(OutputStream outputStream,Charset cs)
    {
        super(initWriter(outputStream,cs));
        indentWriter = lastInitedWriter;
        toClose = lastStreamforClose;
    }

    /**
     * Конструктор
     * @param outputStream целевой поток
     * @param cs кодировка
     */
    public IndentPrintWriter(OutputStream outputStream,String cs)
    {
        super(initWriter(outputStream,cs));
        indentWriter = lastInitedWriter;
        toClose = lastStreamforClose;
    }

    private static IndentWriter lastInitedWriter = null;
    private static OutputStream lastStreamforClose = null;

    private synchronized static Writer initWriter()
    {
        OutputStreamWriter output = new OutputStreamWriter(System.out);
        IndentWriter iw = new IndentWriter(output);
        lastInitedWriter = iw;
        return iw;
    }

    private synchronized static Writer initWriter(File file, boolean append, Charset cs)
    {
        if (file == null) {
            throw new IllegalArgumentException("file == null");
        }
//        if (cs == null) {
//            throw new IllegalArgumentException("cs == null");
//        }

        FileOutputStream fout;
        try {
            fout = new FileOutputStream(file,append);
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("File not found");
        }
        OutputStreamWriter output =
            cs==null ?
                new OutputStreamWriter(fout):
                new OutputStreamWriter(fout,cs);

        IndentWriter iw = new IndentWriter(output);
        lastInitedWriter = iw;
        lastStreamforClose = fout;
        return iw;
    }

    private synchronized static Writer initWriter(File file, boolean append, String cs)
    {
        if (file == null) {
            throw new IllegalArgumentException("file == null");
        }

        FileOutputStream fout;
        try {
            fout = new FileOutputStream(file,append);
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("File not found");
        }
        OutputStreamWriter output = null;
        try {
            output =
                cs==null ?
                    new OutputStreamWriter(fout):
                    new OutputStreamWriter(fout,cs);
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            output = new OutputStreamWriter(fout);
        }
        IndentWriter iw = new IndentWriter(output);
        lastInitedWriter = iw;
        lastStreamforClose = fout;
        return iw;
    }

    private synchronized static Writer initWriter(OutputStream outputStream)
    {
        OutputStreamWriter output = new OutputStreamWriter(outputStream);
        IndentWriter iw = new IndentWriter(output);
        lastInitedWriter = iw;
        return iw;
    }

    private synchronized static Writer initWriter(OutputStream outputStream,Charset cs)
    {
        OutputStreamWriter output = new OutputStreamWriter(outputStream, cs);
        IndentWriter iw = new IndentWriter(output);
        lastInitedWriter = iw;
        return iw;
    }

    private synchronized static Writer initWriter(OutputStream outputStream,String cs)
    {
        OutputStreamWriter output;
        try {
            output = new OutputStreamWriter(outputStream, cs);
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            output = new OutputStreamWriter(outputStream);
        }
        IndentWriter iw = new IndentWriter(output);
        lastInitedWriter = iw;
        return iw;
    }

    private synchronized static Writer initWriter(Writer writer)
    {
        IndentWriter iw = new IndentWriter(writer);
        lastInitedWriter = iw;
        return iw;
    }

    /**
     * Возвращает отступ
     * @return отступ
     */
    public String getIndent() {
        return indentWriter.getIndent();
    }

    /**
     * Указывает отступ
     * @param indent отступ
     */
    public void setIndent(String indent) {
        indentWriter.setIndent(indent);
    }

    /**
     * Указывает уровень отступа
     * @return уровень отступа
     */
    public int getLevel() {
        return indentWriter.getLevel();
    }

    /**
     * Указывает отступ
     * @param level отступ
     */
    public void setLevel(int level) {
        indentWriter.setLevel(level);
    }

    /**
     * Увеличивает уровень отступа
     */
    public void incLevel() {
        indentWriter.incLevel();
    }

    /**
     * Уменьшает уровень отступа
     */
    public void decLevel() {
        indentWriter.decLevel();
    }

    public String getPrefix() {
        return indentWriter.getLinePrefix();
    }

    @Override
    public void close()
    {
        if( toClose!=null )
        {
            try {
                toClose.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            toClose = null;
        }

        super.close();
    }
}
