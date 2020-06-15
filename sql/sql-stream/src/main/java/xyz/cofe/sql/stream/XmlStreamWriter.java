/*
 * The MIT License
 *
 * Copyright 2018 user.
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
package xyz.cofe.sql.stream;

import xyz.cofe.scn.LongScn;
import xyz.cofe.xml.FormatXMLWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOError;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Пишет результат запроса в XML поток
 * @author nt.gocha@gmail.com
 */
public class XmlStreamWriter extends DataStreamAbstract implements QueryStream, LongScn<XmlStreamWriter,XmlStreamWriter> {
    protected FormatXMLWriter out;
    
    /**
     * Конструктор
     * @param xw поток для записи XML
     */
    public XmlStreamWriter( XMLStreamWriter xw){
        if( xw==null )throw new IllegalArgumentException("xw == null");
        if( xw instanceof FormatXMLWriter ){
            out = (FormatXMLWriter)xw;
        }else{
            out = (FormatXMLWriter)new FormatXMLWriter(xw);
        }
    }
    
    /**
     * Конструктор
     * @param xw поток для записи XML
     */
    public XmlStreamWriter( FormatXMLWriter xw){
        if( xw==null )throw new IllegalArgumentException("xw == null");
        out = xw;
    }

    /**
     * Конструктор
     * @param wr поток для записи XML
     */
    public XmlStreamWriter( Writer wr){
        if( wr==null )throw new IllegalArgumentException("wr");
        try {
            out = new FormatXMLWriter(wr);
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("XMLStreamException "+ex.toString(),ex);
        }
    }

    /**
     * Конструктор
     * @param out поток для записи XML
     * @param cs кодировка
     */
    public XmlStreamWriter( OutputStream out, Charset cs){
        if( out==null )throw new IllegalArgumentException("out");
        try {
            this.out = new FormatXMLWriter(out, cs!=null ? cs : Charset.defaultCharset());
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("XMLStreamException "+ex.toString(),ex);
        }
    }
    
    /**
     * Конструктор
     * @param out поток для записи XML
     * @param cs кодировка
     */
    public XmlStreamWriter( OutputStream out, String cs){
        if( out==null )throw new IllegalArgumentException("out");
        try {
            this.out = new FormatXMLWriter(out, cs!=null ? Charset.forName(cs) : Charset.defaultCharset());
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("XMLStreamException "+ex.toString(),ex);
        }
    }
    
    /**
     * Генерирует новую метку SCN
     * @return метка SCN
     */
    protected ScnMark nextScnMark(){
        return new ScnMark(nextscn().b(), System.currentTimeMillis(), Thread.currentThread().getId());
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="overwriteScnMark = true">
    protected boolean overwriteScnMark = true;
    
    /**
     * Переписывать метку SCN.
     * 
     * <p>
     * При значении overwriteScnMark=true и generateScnMark=true - 
     * создает метку, если она указана - то переопределяет ее
     * @return true - переопределять метку SCN
     */
    public synchronized boolean isOverwriteScnMark() {
        return overwriteScnMark;
    }
    
    /**
     * Переписывать метку SCN
     * @param overwriteScnMark true - переопределять метку SCN
     * @see #isOverwriteScnMark() 
     */
    public synchronized void setOverwriteScnMark(boolean overwriteScnMark) {
        this.overwriteScnMark = overwriteScnMark;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="generateScnMark = true">
    public boolean generateScnMark = true;
    
    /**
     * Генерировать метку scn
     * @return true - генерировать метку scn
     * @see #isOverwriteScnMark() 
     */
    public synchronized boolean isGenerateScnMark() {
        return generateScnMark;
    }
    
    /**
     * Генерировать метку scn
     * @param generateScnMark true - генерировать метку scn
     * @see #isOverwriteScnMark() 
     */
    public synchronized void setGenerateScnMark(boolean generateScnMark) {
        this.generateScnMark = generateScnMark;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="queryStreamBegin()">
    @Override
    public synchronized void queryStreamBegin() {
        try {
            writeQueryStreamBegin();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    /**
     * Начало xml потока - тэг &lt;query&gt;
     * @throws XMLStreamException ошибка I/O
     */
    protected synchronized void writeQueryStreamBegin() throws XMLStreamException{
        out.writeStartElement("query");
        //if(writeScn)out.writeAttribute("scn", Long.toString(nextscn().B()));
        //if(writeTime)out.writeAttribute("ti", Long.toString(System.currentTimeMillis()));
        //if(writeThreadId)out.writeAttribute("th", Long.toString(Thread.currentThread().getId()));
    }
    //</editor-fold>
 
    //<editor-fold defaultstate="collapsed" desc="tableBegin()">
    @Override
    public synchronized void tableBegin(int tableIndex,ScnMark mark) {
        try {
            writeTableBegin(tableIndex,mark);
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    /**
     * Начало табличных данных - &lt;resultSet&gt;
     * @param tableIndex индекс
     * @param mark отметка
     * @throws XMLStreamException Ошибка I/O
     */
    protected synchronized void writeTableBegin(int tableIndex,ScnMark mark) throws XMLStreamException{
        out.writeStartElement("resultSet");
        out.writeAttribute("index", Integer.toString(tableIndex));
        
        if( mark!=null && generateScnMark && overwriteScnMark ){
            mark = nextScnMark();
        }else if( mark==null && generateScnMark ){
            mark = nextScnMark();
        }

        if( mark!=null ){
            mark.write(out);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="metaBegin()">
    @Override
    public synchronized void metaBegin(ScnMark mark) {
        try {
            writeMetaBegin(mark);
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    /**
     * Мета данные - &lt;meta&gt;
     * @param mark метка scn
     * @throws XMLStreamException Ошибка I/O
     */
    protected synchronized void writeMetaBegin(ScnMark mark) throws XMLStreamException{
        out.writeStartElement("meta");
        
        if( mark!=null && generateScnMark && overwriteScnMark ){
            mark = nextScnMark();
        }else if( mark==null && generateScnMark ){
            mark = nextScnMark();
        }

        if( mark!=null ){
            mark.write(out);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="columnBegin()">
    @Override
    public synchronized void columnBegin(int columnIndex, ScnMark mark) {
        try {
            writeColumnBegin(columnIndex, mark);
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    /**
     * мета данные колонки &lt;column&gt;
     * @param columnIndex индекс колонки
     * @param mark метка
     * @throws XMLStreamException Ошибка I/O
     */
    protected synchronized void writeColumnBegin(int columnIndex, ScnMark mark) throws XMLStreamException{
        out.writeStartElement("column");
        out.writeAttribute("index", Integer.toString(columnIndex));

        if( mark!=null && generateScnMark && overwriteScnMark ){
            mark = nextScnMark();
        }else if( mark==null && generateScnMark ){
            mark = nextScnMark();
        }

        if( mark!=null ){
            mark.write(out);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="columnProperty()">
    @Override
    public synchronized void columnProperty( String key, Object value, ScnMark mark) {
        try {
            if( key!=null && value!=null ){
                if( value instanceof Class ){
                    writeColumnProperty(key,((Class)value).getName(), mark);
                }else{
                    writeColumnProperty(key,getTypeCast().cast(value, String.class), mark);
                }
            }
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    /**
     * Пишет свойство колонки в виде тэга &lt;имя_свойства&gt;значение&lt;/имя свойства&gt;
     * @param key имя свойства - должно начинаться с буквы
     * @param value значение
     * @param mark метка scn
     * @throws XMLStreamException Ошибка I/O
     */
    protected synchronized void writeColumnProperty( String key, String value, ScnMark mark) throws XMLStreamException{
        if( key!=null && value!=null && key.length()>0 && value.length()>0 &&
            key.matches("(?is)\\w[\\w\\d:\\-_]*")
            )
        {
            out.writeStartElement(key);
        
            if( mark!=null && generateScnMark && overwriteScnMark ){
                mark = nextScnMark();
            }else if( mark==null && generateScnMark ){
                mark = nextScnMark();
            }

            if( mark!=null ){
                mark.write(out);
            }
            
            out.writeCharacters(value);
            out.writeEndElement();
            //out.flush();
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="columnEnd()">
    @Override
    public synchronized void columnEnd() {
        try {
            writeColumnEnd();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected synchronized void writeColumnEnd() throws XMLStreamException{
        out.writeEndElement();
        //out.flush();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="metaEnd()">
    @Override
    public synchronized void metaEnd() {
        try {
            writeMetaEnd();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected synchronized void writeMetaEnd() throws XMLStreamException{
        out.writeEndElement();
        out.flush();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dataBegin()">
    @Override
    public synchronized void dataBegin( ScnMark mark ) {
        try {
            writeDataBegin( mark );
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected synchronized void writeDataBegin( ScnMark mark) throws XMLStreamException{
        out.writeStartElement("data");

        if( mark!=null && generateScnMark && overwriteScnMark ){
            mark = nextScnMark();
        }else if( mark==null && generateScnMark ){
            mark = nextScnMark();
        }

        if( mark!=null ){
            mark.write(out);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rowBegin()">
    protected volatile int currentRowIndex = -1;
    
    @Override
    public synchronized void rowBegin(int rowIndex, ScnMark mark ) {
        try {
            currentRowIndex = rowIndex;
            writeRowBegin(rowIndex, mark);
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected synchronized void writeRowBegin(int rowIndex, ScnMark mark) throws XMLStreamException{
        out.writeStartElement("row");
        
        if( mark!=null && generateScnMark && overwriteScnMark ){
            mark = nextScnMark();
        }else if( mark==null && generateScnMark ){
            mark = nextScnMark();
        }

        if( mark!=null ){
            mark.write(out);
        }
        
        out.writeAttribute("ri", Integer.toString(rowIndex));
        //out.flush();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cell()">
    @Override
    public synchronized void cell( int columnIndex, Object value, ScnMark mark) {
        try {
            writeCell(columnIndex,value, mark);
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected synchronized void writeCell( int columnIndex, Object value, ScnMark mark) throws XMLStreamException{
        out.writeStartElement("cell");

        
        if( mark!=null && generateScnMark && overwriteScnMark ){
            mark = nextScnMark();
        }else if( mark==null && generateScnMark ){
            mark = nextScnMark();
        }

        if( mark!=null ){
            mark.write(out);
        }

        out.writeAttribute("ci", Integer.toString(columnIndex));
        if( value!=null ){
            out.writeAttribute("null","false");
            out.writeAttribute("type",value.getClass().getName());
            String str = null;
            try{
                str = getTypeCast().cast(value, String.class);
            }catch( Throwable err){
                throw new Error("can't cast from ("+value+") to String; rowIndex="+currentRowIndex+", columnIndex"+columnIndex,err);
            }
            out.writeCharacters(str);
        }else{
            out.writeAttribute("null","true");
        }
        out.writeEndElement();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="rowEnd()">
    @Override
    public synchronized void rowEnd() {
        try {
            writeRowEnd();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected int flushEachRows = 100;

    public synchronized int getFlushEachRows() {
        return flushEachRows;
    }

    public synchronized void setFlushEachRows(int flushEachRows) {
        this.flushEachRows = flushEachRows;
    }
    
    protected synchronized void writeRowEnd() throws XMLStreamException{
        out.writeEndElement();
        if( flushEachRows>0 ){
            if( flushEachRows==1 ){
                out.flush();
            }else{
                if( ((currentRowIndex+1)%flushEachRows)==0 ){
                    out.flush();
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dataEnd()">
    @Override
    public synchronized void dataEnd() {
        try {
            writeDataEnd();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected synchronized void writeDataEnd() throws XMLStreamException{
        out.writeEndElement();
        out.flush();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="tableEnd()">
    @Override
    public synchronized void tableEnd() {
        try {
            writeTableEnd();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected synchronized void writeTableEnd() throws XMLStreamException{
        out.writeEndElement();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="queryStreamEnd()">
    @Override
    public synchronized void queryStreamEnd() {
        try {
            writeQueryStreamEnd();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected synchronized void writeQueryStreamEnd() throws XMLStreamException{
        writeMessages();
        out.writeEndElement();
        out.flush();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="updateCount()">
    @Override
    public synchronized void updateCount(int count, ScnMark mark) {
        try {
            writeUpdateCount(count, mark);
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected synchronized void writeUpdateCount(int count, ScnMark mark) throws XMLStreamException{
        out.writeStartElement("updateCount");
        
        if( mark!=null && generateScnMark && overwriteScnMark ){
            mark = nextScnMark();
        }else if( mark==null && generateScnMark ){
            mark = nextScnMark();
        }

        if( mark!=null ){
            mark.write(out);
        }

        out.writeCharacters(Integer.toString(count));
        out.writeEndElement();
    }
    //</editor-fold>

    protected boolean messageBuffer = false;

    /**
     * Буфферизировать сообщения (по умолчанию false).
     * 
     * <p>
     * если включенна буферизация, то все сообщения будут записаны в один блок &lt;messages&gt;
     * @return true - буфферизировать сообщения
     * @see #writeMessages() 
     */
    public synchronized boolean isMessageBuffer() {
        return messageBuffer;
    }

    /**
     * Буфферизировать сообщения (по умолчанию false).
     * @param messageBuffer true - буфферизировать сообщения
     */
    public synchronized void setMessageBuffer(boolean messageBuffer) {
        this.messageBuffer = messageBuffer;
    }

    //<editor-fold defaultstate="collapsed" desc="messages">
    protected final ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<>();
    
    //<editor-fold defaultstate="collapsed" desc="message()">
    @Override
    public synchronized void message(Message mes) {
        if( mes!=null ){
            if( messageBuffer ){
                Message m = mes.clone();
                m.setScn(nextscn().b());
                m.setDate(new Date());
                m.setThreadId(Thread.currentThread().getId());
                messages.add(m);
            }else{
                try {
                    messageIndex++;
                    writeMessage(mes);
                    if( flushEachMessages>0 ){
                        if( flushEachMessages==1 || (messageIndex % flushEachMessages)==0 ){
                            out.flush();
                        }
                    }
                } catch ( XMLStreamException ex) {
                    Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IOError(ex);
                }
            }
        }
    }
    
    protected volatile int flushEachMessages = 1;

    public synchronized int getFlushEachMessages() {
        return flushEachMessages;
    }

    public synchronized void setFlushEachMessages(int flushEachMessages) {
        this.flushEachMessages = flushEachMessages;
    }
    
    protected volatile int messageIndex = -1;
    
    /**
     * Пишет сообщение в XML поток
     * &lt;message&gt;
     * @param mes сообщение
     * @throws XMLStreamException ошибка записи
     */
    protected synchronized void writeMessage(Message mes) throws XMLStreamException{
        if( mes==null )return;
        
        out.writeStartElement("message");
        
        out.writeAttribute("scn", Long.toString(mes.getScn()));
        
        Date d = mes.getDate();
        out.writeAttribute("ti", Long.toString(d==null ? System.currentTimeMillis() : d.getTime()));
        
        out.writeAttribute("th", Long.toString(mes.getThreadId()));
        
        out.writeAttribute("code", Integer.toString(mes.getCode()));
        
        String state = mes.getState();
        if( state!=null )out.writeAttribute("state",state);
        
        String txt = mes.getMessage();
        String ltxt = mes.getLocalizedMessage();
        
        if( txt!=null ){
            out.writeStartElement("text");
            out.writeCharacters(txt);
            out.writeEndElement();
        }
        
        if( ltxt!=null ){
            out.writeStartElement("textLocal");
            out.writeCharacters(ltxt);
            out.writeEndElement();
        }
        
        out.writeEndElement();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="writeMessages()">
    /**
     * Пишет все накопленные сообщения из буфера одним блоком (&lt;messages&gt; сообщения &lt;/messages&gt;) освобождая буфер
     * @throws XMLStreamException Ошибка I/O
     */
    protected synchronized void writeMessages() throws XMLStreamException{
        if( messages.isEmpty() )return;
        out.writeStartElement("messages");
        while( true ){
            Message mes = messages.poll();
            if( mes==null )break;
            
            writeMessage(mes);
        }
        out.writeEndElement();
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="error()">
    @Override
    public void error(Err err, ScnMark mark) {
        if( err==null )return;
        try {
            writeError(err, mark);
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    protected synchronized void writeError(Err err, ScnMark mark) throws XMLStreamException{
        if( mark!=null && generateScnMark && overwriteScnMark ){
            mark = nextScnMark();
        }else if( mark==null && generateScnMark ){
            mark = nextScnMark();
        }
        
        writeError0(err, mark, null);
    }
    
    private synchronized void writeError0( Err err, ScnMark mark, Set<Err> visited ) throws XMLStreamException{
        if( visited==null )visited = new LinkedHashSet<>();
        if( visited.contains(err) )return;
        visited.add(err);
        
        out.writeStartElement("error");
        
        if( mark!=null ){
            mark.write(out);
        }
        
        if( err.getErrorClass()!=null ){
            out.writeAttribute("class", err.getErrorClass());
        }
        
        if( err.getSqlErrorCode()!=null ){
            out.writeAttribute("sqlErrorCode", Integer.toString(err.getSqlErrorCode()));
        }
        
        if( err.getMessage()!=null ){
            out.writeAttribute("message",err.getMessage());
        }
        
        if( err.getMessage()!=null ){
            out.writeAttribute("messageLocal",err.getLocalizedMessage());
        }
        
        ErrStackElement[] stack = err.getStack();
        if( stack!=null && stack.length>0 ){
            out.writeStartElement("stack");
            int i = -1;
            for( ErrStackElement el : stack ){
                i++;
                if( el==null )continue;
                out.writeStartElement("call");
                out.writeAttribute("native", Boolean.toString(el.isNativeMethod()));
                if( el.getClassName()!=null )out.writeAttribute("class", el.getClassName());
                if( el.getMethodName()!=null )out.writeAttribute("method", el.getMethodName());
                if( el.getFileName()!=null )out.writeAttribute("file", el.getFileName());
                out.writeAttribute("line", Integer.toString(el.getLineNumber()));
                out.writeEndElement();
            }
            out.writeEndElement();
        }
        
        if( err.getCause()!=null ){
            writeError0(err.getCause(), mark, visited);
        }
        
        out.writeEndElement();
    }
    //</editor-fold>

    @Override
    public void generatedKeysBegin(ScnMark scm) {
        try {
            writeGeneratedKeysBegin(scm);
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }
    
    /**
     * Пишет информацию о автоматически генерируемых первичных ключах
     * &lt;generated&gt;
     * @param mark отметка SCN
     * @throws XMLStreamException Ошибка I/O
     */
    protected synchronized void writeGeneratedKeysBegin(ScnMark mark) throws XMLStreamException{
        out.writeStartElement("generated");
        
        if( mark!=null && generateScnMark && overwriteScnMark ){
            mark = nextScnMark();
        }else if( mark==null && generateScnMark ){
            mark = nextScnMark();
        }

        if( mark!=null ){
            mark.write(out);
        }
    }

    @Override
    public void generatedKeysEnd() {
        try {
            writeGeneratedKeysEnd();
        } catch ( XMLStreamException ex) {
            Logger.getLogger(XmlStreamQueryWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOError(ex);
        }
    }

    protected synchronized void writeGeneratedKeysEnd() throws XMLStreamException{
        out.writeEndElement();
    }
}
