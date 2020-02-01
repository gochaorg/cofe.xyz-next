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

import java.io.IOException;
import java.io.Writer;
import java.util.function.Function;

/**
 * Преобразовывает известные символы переводы строк (Windows,Linux,Mac,...)
 * в указанную форму.<br><br>
 *
 * Пример,
 * исходная строка: <br>
 * <code>"linux<b style="color:blue">\n</b>Mac<b style="color:blue">\r</b>Windows<b style="color:blue">\r\n</b>Other<b style="color:blue">\n\r</b>..."</code> <br><br>
 *
 * Будет переведена как, если указана форма Linux (\n):<br>
 *
 * <code>"linux<b style="color:green">\n</b>Mac<b  style="color:green">\n</b>Windows<b style="color:green">\n</b>Other<b style="color:green">\n</b>..."</code> <br>
 *
 * @author gocha
 */
public class EndLineReWriter extends Writer
{
    /**
     * сюда записывается преобразованный текст
     */
    protected Writer writer = null;

    /**
     * Символы перевода строк
     */
    protected String endl = System.getProperty("line.separator", "\n");

    /**
     * Символы перевода строк в которые необходимо перевести обнаруженные
     * @return целевые символы перевода строк
     */
    public synchronized String getEndl(){
        return endl;
    }

    /**
     * Указывает символы перевода строк в которые необходимо перевести обнаруженные
     * @param endl целевые символы перевода строк
     */
    public synchronized void setEndl(String endl){
        if( endl==null ){
            endl = System.getProperty("line.separator","\n");
        }
        this.endl = endl;
    }

    /**
     * Указывает символы перевода строк в которые необходимо перевести обнаруженные
     * @param endline целевые символы перевода строк
     */
    public synchronized void setEndLine(EndLine endline){
        if( endline==null ){
            endline = EndLine.Default;
        }
        this.endl = endline.get();
    }

    /**
     * Символы перевода строк в которые необходимо перевести обнаруженные
     * @return целевые символы перевода строк.
     * Вычисляет на основании совпадения свойства endl,
     * если нет совпадения, то возвращает null.
     */
    public synchronized EndLine getEndLine(){
        if( endl==null )return null;
        if( endl.equals(EndLine.Default.get()) )return EndLine.Default;
        if( endl.equals(EndLine.Linux.get()) )return EndLine.Linux;
        if( endl.equals(EndLine.Mac.get()) )return EndLine.Mac;
        if( endl.equals(EndLine.Windows.get()) )return EndLine.Windows;
        if( endl.equals(EndLine.Other.get()) )return EndLine.Other;
        return null;
    }

    /**
     * Cостояние: <br>
     * <b>0</b> - Начальное состояние <br>
     * <table border="1">
     * <tr>
     *      <td colspan="2" align="right">Состояние &rarr;</td>
     *      <td rowspan="2">0</td><td rowspan="2">1</td><td rowspan="2">2</td>
     * </tr>
     * <tr>
     *      <td colspan="2">&darr; Входящие символы</td>
     * </tr>
     * <tr>
     *      <td colspan="2">"\n\r"</td>
     *      <td>flush<br>endl</td>
     * </tr>
     * <tr>
     *      <td colspan="2">"\r\n"</td>
     *      <td>flush<br>endl</td>
     * </tr>
     * <tr>
     *      <td colspan="2">"<b>\n</b><i>char</i>"</td>
     *      <td>flush<br>endl</td>
     * </tr>
     * <tr>
     *      <td colspan="2">"<b>\r</b><i>char</i>"</td>
     *      <td>flush<br>endl</td>
     * </tr>
     * <tr>
     *      <td colspan="2">"<b>\r</b><i>end</i>"</td>
     *      <td>flush<br>endl<br>state=1</td>
     * </tr>
     * <tr>
     *      <td colspan="2">"<b>\n</b><i>end</i>"</td>
     *      <td>flush<br>endl<br>state=2</td>
     * </tr>
     * <tr>
     *      <td colspan="2">
     *          "\n"
     *      </td>
     *      <td>
     *      </td>
     *      <td>state=0</td>
     *      <td>state=2<br>flush<br>endl</td>
     * </tr>
     * <tr>
     *      <td colspan="2">
     *          "\r"
     *      </td>
     *      <td>
     *      </td>
     *      <td>state=1<br>flush<br>endl</td>
     *      <td>state=0</td>
     * </tr>
     * <tr>
     *      <td colspan="2">
     *          any
     *      </td>
     *      <td>
     *      </td>
     *      <td>state=0<br>append</td>
     *      <td>state=0<br>append</td>
     * </tr>
     * </table>
     * <br>
     * Где <br>
     * <b>flush</b> - lineBufferFlush(force=true) <br>
     * <b>endl</b> - writeEndLine(...) <br>
     * <b>append</b> - lineBufferAppend(...) <br>
     * <b>char</b> - ! ( "\n" | "\r" ) <br>
     * <b>end</b> - Конец текста <br>
     */
    private int state = 0;

    //<editor-fold defaultstate="collapsed" desc="EndLineReWriter(...)">
    /**
     * Конструктор
     * @param writer сюда записывается преобразованный текст
     */
    public EndLineReWriter(Writer writer){
        if( writer==null ){
            throw new IllegalArgumentException("writer==null");
        }
        this.writer = writer;
    }

    /**
     * Конструктор
     * @param writer сюда записывается преобразованный текст
     * @param endl Символы перевода строк
     */
    public EndLineReWriter(Writer writer,String endl){
        if( writer==null ){
            throw new IllegalArgumentException("writer==null");
        }
        if( endl==null ){
            throw new IllegalArgumentException("endl==null");
        }
        this.writer = writer;
        this.endl = endl;
    }

    /**
     * Конструктор
     * @param writer сюда записывается преобразованный текст
     * @param endl Символы перевода строк
     */
    public EndLineReWriter(Writer writer,EndLine endl){
        if( writer==null ){
            throw new IllegalArgumentException("writer==null");
        }
        if( endl==null ){
            throw new IllegalArgumentException("endl==null");
        }
        this.writer = writer;
        this.endl = endl.get();
    }
//</editor-fold>

    private final StringBuilder lineBuffer = new StringBuilder();

    /**
     * Буфер текущей строки строки
     * @return буфер
     */
    protected StringBuilder getLineBuffer(){ return lineBuffer; }

    /*
    private synchronized void lineBufferReset(){
        lineBuffer.setLength(0);
    }
    */

    private synchronized void lineBufferFlush(Writer to) throws IOException {
        if( lineBuffer.length()>0 ){
            to.write(lineBuffer.toString());
            lineBuffer.setLength(0);
        }
    }

    /**
     * Функция преобразование строки.
     * Может использования для добавления префикса или еще каких либо действий
     */
    protected Function<String,String> lineConvertor;

    /**
     * Указываеют функцию преобразования строки
     * @return функция конвертирования
     */
    public synchronized Function<String,String> getLineConvertor(){ return lineConvertor; }

    /**
     * Указываеют функцию преобразования строки
     * @param newLineConv функция конвертирования
     */
    public synchronized void setLineConvertor( Function<String,String> newLineConv ){
        lineConvertor = newLineConv;
    }

    /**
     * Сброс буфера текущей строки
     * @param forceConvert true - Конвертировать строку без учета длины буфера
     * @throws IOException Ошибка IO
     */
    protected synchronized void lineBufferFlush(boolean forceConvert) throws IOException {
        if( lineConvertor!=null && lineBuffer!=null ){
            String srcline = lineBuffer.toString();
            String resline = lineConvertor.apply(srcline);
            if( forceConvert ){
                if( resline!=null ){
                    lineBuffer.setLength(0);
                    lineBuffer.append(resline);
                }
            }else{
                if( lineBuffer.length()>0 && resline!=null ){
                    lineBuffer.setLength(0);
                    lineBuffer.append(resline);
                }
            }
        }
        lineBufferFlush(writer);
    }

    /**
     * Добавить символ в буфер строки
     * @param c символ
     */
    protected synchronized void lineBufferAppend( char c ){
        lineBuffer.append(c);
    }

    /*
    private synchronized void lineBufferAppend( String str ){
        lineBuffer.append(str);
    }
    */

    /* (non-Javadoc) @see Writer */
    @Override
    public synchronized void write(char[] cbuf, int off, int len) throws IOException {
        int idx = -1;
        char[] chars1 = new char[1];
        char[] chars2 = new char[2];

        for( int i=off; i<off+len; i++ ){
            idx++;
            char c0 = cbuf[i];
            int c1 = i<(off+len-1) ? cbuf[i+1] : -1;

            chars2[0] = c0;
            chars2[1] = (char)c1;
            chars1[0] = c0;

            if( state==0 ){
                if( c0=='\n' && c1=='\r' ){
                    i+=1;
                    lineBufferFlush(true);
                    writeEndLine(cbuf,off,len,chars2,i-1);
                }else if( c0=='\r' && c1=='\n' ){
                    i+=1;
                    lineBufferFlush(true);
                    writeEndLine(cbuf,off,len,chars2,i-1);
                }else if( c0=='\n' && c1!='\r' && c1!=-1 ){
                    lineBufferFlush(true);
                    writeEndLine(cbuf,off,len,chars1,i);
                }else if( c0=='\r' && c1!='\n' && c1!=-1 ){
                    lineBufferFlush(true);
                    writeEndLine(cbuf,off,len,chars1,i);
                }else if( c0=='\r' && c1==-1 ){
                    state=1;
                    lineBufferFlush(true);
                    writeEndLine(cbuf,off,len,chars1,i);
                }else if( c0=='\n' && c1==-1 ){
                    state=2;
                    lineBufferFlush(true);
                    writeEndLine(cbuf,off,len,chars1,i);
                }else{
                    //writeChar(c0);
                    lineBufferAppend(c0);
                }
            }else if( state==1 ){
                if( c0=='\n' ){
                    state=0;
                }else if( c0=='\r' ){
                    state=1;
                    lineBufferFlush(true);
                    writeEndLine(cbuf,off,len,chars1,i);
                }else{
                    state=0;
                    //writeChar(c0);
                    lineBufferAppend(c0);
                }
            }else if( state==2 ){
                if( c0=='\r' ){
                    state=0;
                }else if( c0=='\n' ){
                    state=2;
                    lineBufferFlush(true);
                    writeEndLine(cbuf,off,len,chars1,i);
                }else{
                    state=0;
                    //writeChar(c0);
                    lineBufferAppend(c0);
                }
            }
        }

        //lineBufferFlush();
    }

    /**
     * Записывает перевод строки в поток
     * @throws IOException Ошибка IO
     */
    protected synchronized void writeEndLine() throws IOException{
        writer.write(getEndl());
    }

    /**
     * Записывает перевод строки в поток
     * @param cbuff Буффер байтов
     * @param coff  Смещение начала текста в буфере
     * @param clen  Размер текста
     * @param newlineChar  Размер символы перевода строк
     * @param nlOff Смещение в буфере символов перевода строк
     * @throws IOException Ошибка IO
     */
    protected synchronized void writeEndLine(char[] cbuff, int coff, int clen, char[] newlineChar, int nlOff) throws IOException{
        //writer.write(getEndl());
        writeEndLine();
    }

    /**
     * Записывает символ в поток
     * @param c0 Символ
     * @throws IOException Ошибка IO
     */
    protected synchronized void writeChar(char c0) throws IOException {
        writer.write(c0);
    }

    /* (non-Javadoc) @see Writer */
    @Override
    public synchronized void flush() throws IOException {
        lineBufferFlush(false);
        writer.flush();
    }

    /* (non-Javadoc) @see Writer */
    @Override
    public synchronized void close() throws IOException {
        writer.close();
    }
}
