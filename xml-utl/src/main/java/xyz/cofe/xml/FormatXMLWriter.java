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
package xyz.cofe.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXResult;
import org.w3c.dom.Node;

/**
 * XMLWriter с поддержкой отступов, и поддержкой экранирования
 * @author gocha
 */
public class FormatXMLWriter implements XMLStreamWriter
{
    private static javax.xml.stream.XMLOutputFactory outFactory = null;
    private OutlineXMLWriter outline = null;
    private boolean escapeWriteChars = false;
    private boolean writeStartDocument = true;
//    private boolean closeStream = 

    /**
     * Указывает писать (по умолчанию true) отступы
     * @return true - писать отступы
     */
    public boolean isWriteOutline() {
        if( outline==null )return true;
        return outline.isWriteOutline();
    }

    /**
     * Указывает писать (по умолчанию true) отступы
     * @param writeOutline true - писать отступы
     */
    public void setWriteOutline(boolean writeOutline) {
        if( outline==null )return;
        outline.setWriteOutline(writeOutline);
    }

    /**
     * Конструктор
     * @param file файл
     * @param encoding кодировка
     */
    public FormatXMLWriter(File file,Charset encoding)
    {
        if (file == null) {
            throw new IllegalArgumentException("file == null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("encoding == null");
        }

        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file);
        } catch (FileNotFoundException ex) {
            throw new Error(ex.getMessage(),ex);
        }
        
        Writer wr = new OutputStreamWriter(fout, encoding);

        try
        {
            outline = new OutlineXMLWriter(createXMLWriter(wr));
        }
        catch(XMLStreamException ex)
        {
            throw new Error(ex.getMessage(),ex);
        }
    }

    /**
     * Конструктор
     * @param file файл
     * @param encoding кодировка
     */
    public FormatXMLWriter(java.nio.file.Path file,Charset encoding)
    {
        if (file == null) {
            throw new IllegalArgumentException("file == null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("encoding == null");
        }

        OutputStream fout = null;
        try {
            fout = Files.newOutputStream(file);
        } catch (IOException ex) {
            throw new Error(ex.getMessage(),ex);
        }
        
        Writer wr = new OutputStreamWriter(fout, encoding);

        try
        {
            outline = new OutlineXMLWriter(createXMLWriter(wr));
        }
        catch(XMLStreamException ex)
        {
            throw new Error(ex.getMessage(),ex);
        }
    }
    
    /**
     * Конструктор
     * @param file файл
     * @param encoding кодировка
     */
    public FormatXMLWriter(xyz.cofe.io.fs.File file,Charset encoding)
    {
        if (file == null) {
            throw new IllegalArgumentException("file == null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("encoding == null");
        }

        OutputStream fout = null;
        fout = file.writeStream();
        
        Writer wr = new OutputStreamWriter(fout, encoding);

        try
        {
            outline = new OutlineXMLWriter(createXMLWriter(wr));
        }
        catch(XMLStreamException ex)
        {
            throw new Error(ex.getMessage(),ex);
        }
    }

    /**
     * Конструктор по умолчанию
     */
    public FormatXMLWriter()
    {
        try
        {
            outline = new OutlineXMLWriter(createXMLWriter(System.out));
        } catch (XMLStreamException ex) {
            throw new Error(ex.getMessage(),ex);
        }
    }

    /**
     * Конструктор
     * @param writer исходный XMLStreamWriter
     */
    public FormatXMLWriter(XMLStreamWriter writer)
    {
        if (writer == null)
        {
            throw new IllegalArgumentException("writer==null");
        }

        outline = new OutlineXMLWriter(writer);
    }

    /**
     * Конструктор
     * @param writer исходный Writer
     * @throws javax.xml.stream.XMLStreamException Ошибка IO
     */
    public FormatXMLWriter(Writer writer) throws XMLStreamException
    {
        if (writer == null)
        {
            throw new IllegalArgumentException("writer==null");
        }

        outline = new OutlineXMLWriter(createXMLWriter(writer));
    }

    /**
     * Конструктор
     * @param stream Поток куда будет писаться
     * @param enconding Кодировка
     * @throws XMLStreamException Мало ли что
     */
    public FormatXMLWriter(OutputStream stream, String enconding) throws XMLStreamException
    {
        if (stream == null)
        {
            throw new IllegalArgumentException("stream==null");
        }
        if (enconding == null)
        {
            throw new IllegalArgumentException("enconding==null");
        }

        outline = new OutlineXMLWriter(createXMLWriter(stream, enconding));
    }

    /**
     * Конструктор
     * @param stream Поток куда будет писаться
     * @param enconding Кодировка
     * @throws XMLStreamException Мало ли что
     */
    public FormatXMLWriter(OutputStream stream, Charset enconding) throws XMLStreamException
    {
        if (stream == null)
        {
            throw new IllegalArgumentException("stream==null");
        }
        if (enconding == null)
        {
            throw new IllegalArgumentException("enconding==null");
        }

        outline = new OutlineXMLWriter(createXMLWriter(stream, enconding));
    }

    /**
     * Конструктор
     * @param stream Поток куда будет писаться
     * @throws javax.xml.stream.XMLStreamException Мало ли что
     */
    public FormatXMLWriter(OutputStream stream) throws XMLStreamException
    {
        if (stream == null)
        {
            throw new IllegalArgumentException("stream==null");
        }

        outline = new OutlineXMLWriter(createXMLWriter(stream));
    }

    /**
     * Конструктр
     * @param result Поток куда будет писаться
     * @throws javax.xml.stream.XMLStreamException Мало ли что
     */
    public FormatXMLWriter(javax.xml.transform.Result result) throws XMLStreamException
    {
        if (result == null)
        {
            throw new IllegalArgumentException("result==null");
        }

        outline = new OutlineXMLWriter(createXMLWriter(result));
    }

    private static XMLStreamWriter createXMLWriter(OutputStream stream, String enconding) throws XMLStreamException
    {
        return outFactory().createXMLStreamWriter(stream, enconding);
    }

    private static XMLStreamWriter createXMLWriter(OutputStream stream, Charset cs) throws XMLStreamException
    {        
        return outFactory().createXMLStreamWriter(stream, cs.name());
    }

    private static XMLStreamWriter createXMLWriter(OutputStream stream) throws XMLStreamException
    {
        return outFactory().createXMLStreamWriter(stream);
    }

    private static XMLStreamWriter createXMLWriter(Writer writer) throws XMLStreamException
    {
        return outFactory().createXMLStreamWriter(writer);
    }

    private static XMLStreamWriter createXMLWriter(javax.xml.transform.Result result) throws XMLStreamException
    {
        return outFactory().createXMLStreamWriter(result);
    }

    private static javax.xml.stream.XMLOutputFactory outFactory()
    {
        if (outFactory == null)
        {
            outFactory = XMLOutputFactory.newInstance();
        }
        return outFactory;
    }

    /**
     * Экранирует символы проходящие через методы writeCharacters. (по умол. нет)
     * @return Экранирует (по умол. нет)
     */
    public boolean isEscapeWriteChars()
    {
        return escapeWriteChars;
    }

    /**
     * Устанавливае Экранирует символы проходящие через методы writeCharacters. (по умол. нет)
     * @param escapeWriteChars Экранирует (по умол. нет)
     */
    public void setEscapeWriteChars(boolean escapeWriteChars)
    {
        this.escapeWriteChars = escapeWriteChars;
    }

    /**
     * Кодирует строку в формат html (замееняет символы &amp;,&lt;,&gt; на соответствующие)
     * @param text Текст
     * @return Кодированный текст
     */
    public static String htmlEncode(String text)
    {
        if (text == null)
        {
            return null;
        }

        String escaped = text.replace("&", "&amp;");
        escaped = escaped.replace("<", "&lt;");
        escaped = escaped.replace(">", "&gt;");

        return escaped;
    }

    /**
     * Декодирует строку html (замееняет символы &amp;,&lt;,&gt; на соответствующие)
     * @param html Текст
     * @return Декодированный текст
     */
    public static String htmlDecode(String html)
    {
        if (html == null)
        {
            return null;
        }
        
        String decode = html.replace("&lt;", "<");
        decode = decode.replace("&gt;", ">");
        decode = decode.replace("&amp;", "&");
        
        return decode;
    }


    /**
     * Кодирует и пишет текст
     * @param text Текст
     * @throws javax.xml.stream.XMLStreamException IO Ошибка
     */
    public void writeEscapeCharacters(String text) throws XMLStreamException
    {
        if( outline==null )return;
        if (text == null)
        {
            outline.writeCharacters(text);
            return;
        }

        outline.writeCharacters(htmlEncode(text));
    }

    public void writeEscapeCharacters(char[] text, int start, int len) throws XMLStreamException
    {
        if( outline==null )return;
        if (text == null)
        {
            outline.writeCharacters(text, start, len);
            return;
        }

        String srcText = new String(text, start, len);
        outline.writeCharacters(htmlEncode(srcText));
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeStartElement(namespaceURI, localName);
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeStartElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException
    {
        if( outline==null )return;
        if( writeStartDocument )outline.writeStartDocument();
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException
    {
        if( outline==null )return;
        if( writeStartDocument )outline.writeStartDocument(version);
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException
    {
        if( outline==null )return;
        if( writeStartDocument )outline.writeStartDocument(encoding, version);
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeProcessingInstruction(target);
    }

    @Override
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeProcessingInstruction(target, data);
    }

    @Override
    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeNamespace(prefix, namespaceURI);
    }

    @Override
    public void writeEntityRef(String name) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeEntityRef(name);
    }

    @Override
    public void writeEndElement() throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException
    {
        if( outline==null )return;
        if( writeStartDocument )outline.writeEndDocument();
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeEmptyElement(namespaceURI, localName);
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeEmptyElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeEmptyElement(localName);
    }

    @Override
    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeDefaultNamespace(namespaceURI);
    }

    @Override
    public void writeDTD(String dtd) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeDTD(dtd);
    }

    @Override
    public void writeComment(String data) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeComment(data);
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException
    {
        if( outline==null )return;
        if (isEscapeWriteChars())
        {
            writeEscapeCharacters(text);
        }
        else
        {
            outline.writeCharacters(text);
        }
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException
    {
        if( outline==null )return;
        if (isEscapeWriteChars())
        {
            writeEscapeCharacters(text, start, len);
        }
        else
        {
            outline.writeCharacters(text, start, len);
        }
    }

    @Override
    public void writeCData(String data) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeCData(data);
    }

    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeAttribute(localName, value);
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException
    {
        if( outline==null )return;
        outline.writeAttribute(namespaceURI, localName, value);
    }

    public void setSpacer(String spacer)
    {
        if( outline==null )return;
        outline.setSpacer(spacer);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException
    {
        if( outline==null )return;
        outline.setPrefix(prefix, uri);
    }

    public void setNewline(String newline)
    {
        if( outline==null )return;
        outline.setNewline(newline);
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException
    {
        if( outline==null )return;
        outline.setNamespaceContext(context);
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException
    {
        if( outline==null )return;
        outline.setDefaultNamespace(uri);
    }

    public XMLStreamWriter getWriter()
    {
        if( outline==null )return null;
        return outline.getWriter();
    }

    public String getSpacer()
    {
        if( outline==null )return null;
        return outline.getSpacer();
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException
    {
        if( outline==null )return null;
        return outline.getProperty(name);
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException
    {
        if( outline==null )return null;
        return outline.getPrefix(uri);
    }

    public String getNewline()
    {
        if( outline==null )return null;
        return outline.getNewline();
    }

    @Override
    public NamespaceContext getNamespaceContext()
    {
        if( outline==null )return null;
        return outline.getNamespaceContext();
    }

    public Integer getLevel()
    {
        if( outline==null )return 0;
        return outline.getLevel();
    }

    @Override
    public void flush() throws XMLStreamException
    {
        if( outline==null )return;
        outline.flush();
    }

    @Override
    public void close() throws XMLStreamException
    {
        if( outline==null )return;
        outline.close();
    }

    public boolean isWriteStartDocument()
    {
        return writeStartDocument;
    }

    public void setWriteStartDocument(boolean writeStartDocument)
    {
        this.writeStartDocument = writeStartDocument;
    }

    /**
     * Конвертирует XMLDOM древо в текстовое представление
     * @param xmlDocument XMLDOM древо
     * @return Текстовое представление XMLDOM древа
     * @throws javax.xml.stream.XMLStreamException Если не смогло
     * @throws javax.xml.transform.TransformerConfigurationException Если не смогло
     * @throws javax.xml.transform.TransformerException Если не смогло
     * @throws java.io.IOException Если не смогло
     */
    public static String toStringWithException(Node xmlDocument)
            throws
            XMLStreamException,
            TransformerConfigurationException,
            TransformerException,
            IOException
    {
        if (xmlDocument == null)
        {
            throw new IllegalArgumentException("xmlDocument == null");
        }

        Transformer tr = TransformerFactory.newInstance().newTransformer();

        DOMSource domSrc= new DOMSource(xmlDocument);

        StringWriter sWiter = new StringWriter();
        XMLStreamWriter xml2format = new FormatXMLWriter(sWiter);

        StAXResult sResult = new StAXResult(xml2format);
        Result result = sResult;

        tr.transform(domSrc, result);

//        sWiter.flush();
        xml2format.writeEndDocument();
        xml2format.flush();

        String toOutput = sWiter.toString();

        xml2format.close();
        sWiter.close();

        return toOutput;
    }

    /**
     * Конвертирует XMLDOM древо в текстовое представление с подовлением исключений
     * @param xmlDocument XMLDOM древо
     * @param defaultResult Результат возвращаем в случае, если не смогло сковертировать
     * @return Текстовое представление XMLDOM древа
     */
    public static String toStringWithoutException(Node xmlDocument, String defaultResult)
    {
        try 
        {
            return toStringWithException(xmlDocument);
        }
        catch (IOException ex) {
//            Logger.getLogger(FormatingXMLWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (TransformerException ex) {
//            Logger.getLogger(FormatingXMLWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (XMLStreamException ex) {
//            Logger.getLogger(FormatingXMLWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return defaultResult;
    }
}
