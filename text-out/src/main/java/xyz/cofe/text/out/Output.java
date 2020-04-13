/*
 * The MIT License
 *
 * Copyright 2015 nt.gocha@gmail.com.
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

package xyz.cofe.text.out;


//import com.sun.org.apache.xerces.internal.utils.Objects;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import xyz.cofe.text.EndLine;
import xyz.cofe.text.PrefixWriter;
import xyz.cofe.text.UnionWriter;
import xyz.cofe.text.template.BasicTemplate;

/**
 * Поток вывода информации
 * @author nt.gocha@gmail.com
 */
public class Output
    extends PrintWriter
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(Output.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(Output.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(Output.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(Output.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(Output.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(Output.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(Output.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /**
     * Через данный Writer осуществляется вывод
     */
    private PrefixWriter prefixWriter;

    //<editor-fold defaultstate="collapsed" desc="Output(...)">
    /**
     * Конструктор по умолчанию
     */
    public Output() {
        super(System.out,true);

        prefixWriter = createPrefixWriter(out);
        this.out = prefixWriter;

        //autoFlush = false;
    }

    /**
     * Конструктор вывода в файл
     * @param file файл
     * @param cs Кодировка (или null)
     * @return Вывод
     */
    private static Writer createWriter( File file, Charset cs ) {
        try {
            if (file== null) {
                throw new IllegalArgumentException("file==null");
            }
            if( cs==null )cs = Charset.defaultCharset();

            FileOutputStream fout = new FileOutputStream(file);
            OutputStreamWriter sw = new OutputStreamWriter(fout, cs);

            return sw;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Output.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Конструктор вывода в файл
     * @param file файл
     * @param cs Кодировка (или null)
     */
    public Output(File file, Charset cs) {
        super(createWriter(file, cs),true);

        prefixWriter = createPrefixWriter(out);
        this.out = prefixWriter;
    }

    /**
     * Конструктор вывода в файл
     * @param file файл
     * @param cs Кодировка (или null)
     * @return Вывод
     */
    private static Writer createWriter( Path file, Charset cs ) {
        try {
            if (file== null) {
                throw new IllegalArgumentException("file==null");
            }
            if( cs==null )cs = Charset.defaultCharset();

            Writer w = Files.newBufferedWriter(file, cs);
            return w;
        } catch (IOException ex) {
            Logger.getLogger(Output.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    /**
     * Конструктор вывода в файл
     * @param file файл
     * @param cs Кодировка (или null)
     */
    public Output(Path file, Charset cs) {
        super(createWriter(file, cs),true);

        prefixWriter = createPrefixWriter(out);
        this.out = prefixWriter;
    }

    /**
     * Конструктор вывода в файл
     * @param file файл
     * @param cs Кодировка (или null)
     * @return Вывод
     */
    private static Writer createWriter( xyz.cofe.io.fs.File file, Charset cs ) {
        if (file== null) {
            throw new IllegalArgumentException("file==null");
        }
        if( cs==null )cs = Charset.defaultCharset();

        OutputStream fout = file.writeStream();
        Writer w = new OutputStreamWriter(fout, cs);
        return w;
    }

    /**
     * Конструктор вывода в файл
     * @param file файл
     * @param cs Кодировка (или null)
     */
    public Output(xyz.cofe.io.fs.File file, Charset cs) {
        super(createWriter(file, cs),true);

        prefixWriter = createPrefixWriter(out);
        this.out = prefixWriter;
    }

    /**
     * Конструктор вывода
     * @param out куда выводить
     */
    public Output( Writer out ) {
        super(out);

        prefixWriter = createPrefixWriter(out);
        this.out = prefixWriter;
    }

    /**
     * Конструктор вывода
     * @param out куда выводить
     * @param autoFlush При каждом вызове println вызывать flush
     */
    public Output( Writer out, boolean autoFlush ) {
        super(out, autoFlush);

        prefixWriter = createPrefixWriter(out);
        this.out = prefixWriter;
    }

    /**
     * Конструктор вывода
     * @param out куда выводить
     */
    public Output( OutputStream out ) {
        super(out);

        prefixWriter = createPrefixWriter(
            new OutputStreamWriter(System.out, Charset.defaultCharset()) );

        this.out = prefixWriter;
    }

    /**
     * Конструктор вывода
     * @param out куда выводить
     * @param autoFlush При каждом вызове println вызывать flush
     */
    public Output( OutputStream out, boolean autoFlush ) {
        super(out, autoFlush);

        prefixWriter = createPrefixWriter(
            new OutputStreamWriter(System.out, Charset.defaultCharset()) );

        this.out = prefixWriter;
    }
//</editor-fold>

    private synchronized PrefixWriter createPrefixWriter( Writer wr ){
        PrefixWriter elrw = new PrefixWriter(wr);
        return elrw;
    }

    //<editor-fold defaultstate="collapsed" desc="endl : String - Символы перевода строки">
    /**
     * Символы перевода строки
     * @return Символы перевода строки
     */
    public String getEndl(){
        Object lo = this.lock;
        if( lo!=null ){
            synchronized(lo){
                return getEndl0();
            }
        }else{
            return getEndl0();
        }
    }

    private String getEndl0(){
        return prefixWriter==null
            ? System.getProperty("line.separator", "\n")
            : prefixWriter.getEndl();
    }

    /**
     * Символы перевода строки
     * @param txt символы перевода строки
     */
    public void setEndl( String txt ){
        Object lo = this.lock;
        if( lo!=null ){
            synchronized(lo){
                setEndl0( txt );
            }
        }else{
            setEndl0( txt );
        }
    }

    private void setEndl0( String txt ){
        if( prefixWriter!=null ){
            prefixWriter.setEndl( txt==null ? EndLine.Default.get() : txt );
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="reset() - Перенаправление вывода">
    protected boolean closeOnReset = false;

    /**
     * Указывает закрывать поток, при сбросе (reset) вывода
     * @return true - закрываь
     */
    public synchronized boolean isCloseOnReset(){ return closeOnReset; }

    /**
     * Указывает закрывать поток, при сбросе (reset) вывода
     * @param close true - закрываь
     */
    public synchronized void setCloseOnReset( boolean close ){ closeOnReset = close; }

    /**
     * Сброс потока вывода, указывает новый поток вывода
     * @param output куда выводить
     */
    public synchronized void reset( final Writer output ){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Output.this.flush();

                if( closeOnReset ){
                    Output.this.close();
                }

                prefixWriter = createPrefixWriter(
                    output==null
                        ? new OutputStreamWriter(System.out, Charset.defaultCharset())
                        : output
                );

                Output.this.out = prefixWriter;
            }
        };

        Object lo = this.lock;
        if( lo!=null ){
            synchronized(lo){
                r.run();
            }
        }else{
            r.run();
        }
    }

    /**
     * Сброс вывода
     */
    public synchronized void reset(){
        reset(null);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="appendWriter()">
    /**
     * Добавление потока вывода
     * @param w куда еще выводить
     */
    public synchronized void appendWriter( Writer w ){
        if( w==null )throw new IllegalArgumentException( "w==null" );

        Object lo = this.lock;

        if( lo!=null ){
            synchronized( lo ){
                appendWriter0( w );
            }
        }else{
            appendWriter0(w);
        }
    }

    private void appendWriter0( Writer w ){
        if( this.out instanceof UnionWriter ){
            ((UnionWriter)this.out).getWriters().add(w);
        }else{
            UnionWriter uw = new UnionWriter( this.out, w );
            this.out = uw;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="removeWriter()">
    /**
     * Удаление потока вывода
     * @param writer удаляемый поток
     */
    public synchronized void removeWriter( Writer writer ){
        if( writer==null )throw new IllegalArgumentException( "writer==null" );

        Object lo = this.lock;

        if( lo!=null ){
            synchronized( lo ){
                if( this.out instanceof UnionWriter ){
                    UnionWriter uw = (UnionWriter)this.out;
                    uw.getWriters().remove(writer);
                }
            }
        }else{
            if( this.out instanceof UnionWriter ){
                UnionWriter uw = (UnionWriter)this.out;
                uw.getWriters().remove(writer);
            }
        }
    }
    //</editor-fold>

    public BasicTemplate.EasyTemplate template( String template ){
        if( template==null )throw new IllegalArgumentException( "template==null" );

        BasicTemplate.EasyTemplate tmpl = BasicTemplate.template(template);
        tmpl.output(this);
        tmpl.align();
        tmpl.outputFlushing();
//        tmpl.useJavaScript();
        return tmpl;
    }

    //<editor-fold defaultstate="collapsed" desc="linePrefix">
    /**
     * Указывает префикс в начале строки
     * @param fn функция - префикс
     */
    public synchronized void setLinePrefixFn( Supplier<String> fn ){
        PrefixWriter pw = prefixWriter;
        if( pw==null )throw new IllegalStateException("prefixWriter == null");

        pw.setLinePrefixFn(fn);
    }

    /**
     * Указывает префикс в начале строки
     * @return функция - префикс
     */
    public synchronized Supplier<String> getLinePrefixFn(){
        PrefixWriter pw = prefixWriter;
        if( pw==null )throw new IllegalStateException("prefixWriter == null");

        return pw.getLinePrefixFn();
    }

    /**
     * Указывает префикс в начале строки
     * @return префикс
     */
    public synchronized String getLinePrefix(){
        PrefixWriter pw = prefixWriter;
        if( pw==null )throw new IllegalStateException("prefixWriter == null");

        return pw.getLinePrefix();
    }

    /**
     * Указывает префикс в начале строки. меняет свойтсво <b>lineConvertor</b>
     * @param prefix префикс
     */
    public synchronized void setLinePrefix( final String prefix ){
        PrefixWriter pw = prefixWriter;
        if( pw==null )throw new IllegalStateException("prefixWriter == null");

        pw.setLinePrefix(prefix);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="lineConvertor : Convertor<String,String> - функцию конвертации строки">
    /**
     * Указывает функцию конвертации строки
     * @return fn( Исходная строка ) : Конечная строка
     */
    public synchronized Function<String,String> getLineConvertor(){
        PrefixWriter pw = prefixWriter;
        if( pw==null )throw new IllegalStateException("prefixWriter == null");

        return pw.getLineConvertor();
    }

    /**
     * Указывает функцию конвертации строки
     * @param conv fn( Исходная строка ) : Конечная строка
     */
    public synchronized void setLineConvertor(Function<String,String> conv){
        PrefixWriter pw = prefixWriter;
        if( pw==null )throw new IllegalStateException("prefixWriter == null");
        if( conv==null )throw new IllegalStateException("conv == null");

        pw.setLineConvertor(conv);
    }
//</editor-fold>
}
