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
package xyz.cofe.xml.stream.path;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

/**
 * Чтение XML структуры, получая узлы в XVisitor
 * @author gocha
 */
public class XmlReader
{
    /**
     * Конструктор
     * @param url Читаемый файл/ресурс
     * @param cs кодировка
     * @param visitor Получаntkm XPath
     * @throws IOException Ошибка читения XML
     * @throws XMLStreamException Ошибка читения XML
     */
    public XmlReader(URL url, Charset cs, XVisitor visitor) throws IOException, XMLStreamException {
        if( url==null )throw new IllegalArgumentException( "url==null" );
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        if( cs==null )cs = Charset.defaultCharset();
        
        InputStream in = url.openStream();
        InputStreamReader reader = new InputStreamReader(in, cs);
        init(reader, visitor);
        reader.close();
    }
    
    /**
     * Конструктор
     * @param url Читаемый файл/ресурс
     * @param visitor Получаntkm XPath
     * @throws IOException Ошибка читения XML
     * @throws XMLStreamException  Ошибка читения XML
     */
    public XmlReader(URL url, XVisitor visitor) throws IOException, XMLStreamException {
        if( url==null )throw new IllegalArgumentException( "url==null" );
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        InputStream in = url.openStream();
        init(in, visitor);
        in.close();
    }
    
    /**
     * Конструктор
     * @param file Читаемый файл/ресурс
     * @param visitor Получаntkm XPath
     * @throws IOException Ошибка читения XML
     * @throws XMLStreamException  Ошибка читения XML
     */
    public XmlReader(File file, XVisitor visitor) throws IOException, XMLStreamException {
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        FileInputStream fin = new FileInputStream(file);
        init(fin, visitor);
        fin.close();
    }
    
    /**
     * Конструктор
     * @param file Читаемый файл/ресурс
     * @param cs кодировка
     * @param visitor Получаntkm XPath
     * @throws IOException Ошибка читения XML
     * @throws XMLStreamException  Ошибка читения XML
     */
    public XmlReader(File file, Charset cs, XVisitor visitor) throws IOException, XMLStreamException {
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        if( cs==null )cs = Charset.defaultCharset();
        FileInputStream fin = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(fin, cs);
        init(reader, visitor);
        fin.close();
    }
    
    /**
     * Конструктор
     * @param file Читаемый файл/ресурс
     * @param cs кодировка
     * @param visitor Получаntkm XPath
     * @throws IOException Ошибка читения XML
     * @throws XMLStreamException  Ошибка читения XML
     */
    public XmlReader(java.nio.file.Path file, Charset cs, XVisitor visitor) throws IOException, XMLStreamException {
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        if( cs==null )cs = Charset.defaultCharset();
        InputStream fin = Files.newInputStream(file);
        InputStreamReader reader = new InputStreamReader(fin, cs);
        init(reader, visitor);
        fin.close();
    }
    
    /**
     * Конструктор
     * @param file Читаемый файл/ресурс
     * @param visitor Получаntkm XPath
     * @throws IOException Ошибка читения XML
     * @throws XMLStreamException  Ошибка читения XML
     */
    public XmlReader(java.nio.file.Path file, XVisitor visitor) throws IOException, XMLStreamException {
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        InputStream fin = Files.newInputStream(file);
        init(fin, visitor);
        fin.close();
    }
    
    /**
     * Конструктор
     * @param file Читаемый файл/ресурс
     * @param cs кодировка
     * @param visitor Получаntkm XPath
     * @throws IOException Ошибка читения XML
     * @throws XMLStreamException  Ошибка читения XML
     */
    public XmlReader(xyz.cofe.io.fs.File file, Charset cs, XVisitor visitor) throws IOException, XMLStreamException {
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        if( cs==null )cs = Charset.defaultCharset();
        InputStream fin = file.readStream();
        InputStreamReader reader = new InputStreamReader(fin, cs);
        init(reader, visitor);
        fin.close();
    }
    
    /**
     * Конструктор
     * @param file Читаемый файл/ресурс
     * @param visitor Получаntkm XPath
     * @throws IOException Ошибка читения XML
     * @throws XMLStreamException  Ошибка читения XML
     */
    public XmlReader(xyz.cofe.io.fs.File file, XVisitor visitor) throws IOException, XMLStreamException {
        if( file==null )throw new IllegalArgumentException( "file==null" );
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        InputStream fin = file.readStream();
        init(fin, visitor);
        fin.close();
    }
    
    /**
     * Конструктор
     * @param reader Поток XML данных
     * @param visitor Получаntkm XPath
     * @throws XMLStreamException  Ошибка читения XML
     */
    public XmlReader(Reader reader, XVisitor visitor) throws XMLStreamException {
        if (reader== null) {            
            throw new IllegalArgumentException("reader==null");
        }
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        init(reader, visitor);
    }
    
    /**
     * Конструктор
     * @param stream Поток XML данных
     * @param cs кодировка
     * @param visitor Получаntkm XPath
     * @throws XMLStreamException  Ошибка читения XML
     */
    public XmlReader(InputStream stream,Charset cs,XVisitor visitor) throws XMLStreamException {
        if (stream== null) {            
            throw new IllegalArgumentException("stream==null");
        }
        if (cs== null) {            
            throw new IllegalArgumentException("cs==null");
        }
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        InputStreamReader reader = new InputStreamReader(stream, cs);
        init(reader, visitor);
    }
    
    /**
     * Конструктор
     * @param stream Поток XML данных
     * @param visitor Получаntkm XPath
     * @throws XMLStreamException  Ошибка читения XML
     */
    public XmlReader(InputStream stream,XVisitor visitor) throws XMLStreamException {
        if (stream== null) {            
            throw new IllegalArgumentException("stream==null");
        }
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        init(stream, visitor);
    }
    
    /**
     * Конструктор
     * @param source Поток XML данных
     * @param visitor Получаntkm XPath
     * @throws XMLStreamException  Ошибка читения XML
     */
    public XmlReader(String source,XVisitor visitor) throws XMLStreamException {
        if (source== null) {            
            throw new IllegalArgumentException("source==null");
        }
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        StringReader reader = new StringReader(source);
        init(reader, visitor);
    }
    
    /**
     * Конструктор
     * @param source Поток XML данных
     * @param visitor Получаntkm XPath
     * @throws XMLStreamException Ошибка читения XML
     */
    public XmlReader(javax.xml.transform.Source source,XVisitor visitor) throws XMLStreamException {
        if (source== null) {            
            throw new IllegalArgumentException("source==null");
        }
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        init(source, visitor);
    }
    
    /**
     * Конструктор
     * @param source Поток XML данных
     * @param visitor Получаntkm XPath
     * @throws XMLStreamException Ошибка читения XML
     */
    public XmlReader(javax.xml.stream.XMLStreamReader source,XVisitor visitor) throws XMLStreamException {
        if (source== null) {            
            throw new IllegalArgumentException("source==null");
        }
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        init(source, visitor);
    }

    /**
     * Конструктор
     * @param source Поток XML данных
     * @param visitor Получаntkm XPath
     * @throws XMLStreamException Ошибка читения XML
     */
    public XmlReader(XMLEventReader source,XVisitor visitor) throws XMLStreamException {
        if (source== null) {            
            throw new IllegalArgumentException("source==null");
        }
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        init(source, visitor);
    }

    /**
     * Конструктор
     * @param reader Поток XML данных
     * @param visitor Получаntkm XPath
     * @throws XMLStreamException Ошибка читения XML
     */
    protected void init(Reader reader,XVisitor visitor) throws XMLStreamException {
        if (reader== null) {            
            throw new IllegalArgumentException("reader==null");
        }
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        
        XMLEventReader xmlReader =
        XMLInputFactory.newFactory().createXMLEventReader(reader);
        
        read(xmlReader,visitor,new XEventPath());
//        xmlReader.close();
    }
    
    /**
     * Конструктор
     * @param reader Поток XML данных
     * @param visitor Получаntkm XPath
     * @throws XMLStreamException Ошибка читения XML
     */
    protected void init(InputStream reader,XVisitor visitor) throws XMLStreamException {
        if (reader== null) {            
            throw new IllegalArgumentException("reader==null");
        }
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        
        XMLEventReader xmlReader =
        XMLInputFactory.newFactory().createXMLEventReader(reader);
        
        read(xmlReader,visitor,new XEventPath());
//        xmlReader.close();
    }
    
    /**
     * Конструктор
     * @param reader Поток XML данных
     * @param visitor Получаntkm XPath
     * @throws XMLStreamException Ошибка читения XML
     */
    protected void init(XMLEventReader reader,XVisitor visitor) throws XMLStreamException {
        if (reader== null) {            
            throw new IllegalArgumentException("reader==null");
        }
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        
//        XMLEventReader xmlReader =
//        XMLInputFactory.newFactory().createXMLEventReader(reader);
        
        read(reader,visitor,new XEventPath());
//        xmlReader.close();
    }
    
    /**
     * Конструктор
     * @param reader Поток XML данных
     * @param visitor Получаntkm XPath
     * @throws XMLStreamException Ошибка читения XML
     */
    protected void init(javax.xml.transform.Source reader,XVisitor visitor) throws XMLStreamException {
        if (reader== null) {            
            throw new IllegalArgumentException("reader==null");
        }
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        
        XMLEventReader xmlReader =
        XMLInputFactory.newFactory().createXMLEventReader(reader);
        
        read(xmlReader,visitor,new XEventPath());
//        xmlReader.close();
    }
    
    /**
     * Конструктор
     * @param reader Поток XML данных
     * @param visitor Получаntkm XPath
     * @throws XMLStreamException Ошибка читения XML
     */
    protected void init(javax.xml.stream.XMLStreamReader reader,XVisitor visitor) throws XMLStreamException {
        if (reader== null) {            
            throw new IllegalArgumentException("reader==null");
        }
        if (visitor== null) {            
            throw new IllegalArgumentException("visitor==null");
        }
        
        XMLEventReader xmlReader =
        XMLInputFactory.newFactory().createXMLEventReader(reader);
        
        read(xmlReader,visitor,new XEventPath());
//        xmlReader.close();
    }
    
    /**
     * Читает XML поток 
     * @param reader Поток XML данных
     * @param visitor Получаntkm XPath
     * @param path текущий объект xml
     * @throws XMLStreamException Ошибка читения XML
     */
    protected void read(XMLEventReader reader,XVisitor visitor,XEventPath path) throws XMLStreamException{
        while(reader.hasNext()){
            XMLEvent e = reader.nextEvent();
            if( e.isStartDocument() ){
                path.add(e);
                visitor.enter(path);
            }
            else if( e.isEndDocument() ){
                visitor.exit(path);
                path.pop();
            }
            else if( e.isStartElement() ){
                path.add(e);
                visitor.enter(path);
            }
            else if( e.isEndElement() ){
                visitor.exit(path);
                path.pop();
            }
            else if( e instanceof Characters ){
                Characters chrs = (Characters)e;
                String chars = chrs.getData();
//                path.push(e);
                visitor.characters(path, chars);
//                visitor.enter(path);
//                visitor.exit(path);
//                path.pop();
            }
//            else if( e.isAttribute() ){
//                path.push(e);
//                visitor.enter(path);
//                visitor.exit(path);
//                path.pop();
//            }
            else if( e.isNamespace() ){
                path.push(e);
                visitor.enter(path);
                visitor.exit(path);
                path.pop();
            }
            else if( e.isProcessingInstruction() ){
                path.push(e);
                visitor.enter(path);
                visitor.exit(path);
                path.pop();
            }
        }
    }
}
