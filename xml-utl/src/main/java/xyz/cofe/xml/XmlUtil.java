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
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
//import org.codehaus.staxmate.dom.DOMConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import xyz.cofe.collection.BasicVisitor;
import xyz.cofe.collection.NodesExtracter;
import xyz.cofe.collection.Visitor;
import xyz.cofe.ecolls.Predicates;
import xyz.cofe.io.fn.IOFun;
import xyz.cofe.iter.Eterable;
import xyz.cofe.iter.TreeStep;
import xyz.cofe.text.Text;

/**
 * Упрощение работы с XML
 * @author gocha
 */
public class XmlUtil
{
    // <editor-fold defaultstate="collapsed" desc="XSLT Функции">
    /**
     * Создание XSLT Transformer из XML/XSLT шаблона
     *
     * @param xslStyle XML/XSLT шаблон
     * @return XSLT Transformer или null
     */
    public static Transformer createXSLT(String xslStyle) {
        try {
            if (xslStyle == null) {
                return null;
            }
            
            StringReader stringReader = new StringReader(xslStyle);
            Source source = new StreamSource(stringReader);
            Transformer t = TransformerFactory.newInstance().newTransformer(source);
            return t;
        } catch (TransformerConfigurationException ex) {
            throw new Error(ex.getMessage(), ex);
        }
    }

    /**
     * Создание XSLT Transformer из XML/XSLT шаблона
     *
     * @param urlXSLT XML/XSLT шаблон
     * @param cs Кодировка XML/XSLT шаблона
     * @return XSLT Transformer или null
     */
    public static Transformer createXSLT(URL urlXSLT, Charset cs) {
        if (urlXSLT == null)
            return null;
        if (cs == null)
            return null;
        
        String text;
        try {
            text = IOFun.readText(urlXSLT, cs);
        } catch (IOException ex) {
//            Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error( ex.getMessage(), ex );
        }
        if (text == null)
            return null;
        
        return createXSLT(text);
    }

//    /**
//     * Создание XSLT Transformer из XML/XSLT шаблона
//     *
//     * @param urlXSLT XML/XSLT шаблон
//     * @param cs Кодировка XML/XSLT шаблона
//     * @return XSLT Transformer или null
//     */
//    public static Transformer createXSLT(URL urlXSLT, String cs) {
//        if (urlXSLT == null)
//            return null;
//        if (cs == null)
//            return null;
//        
//        String text = FileUtil.readAllText(urlXSLT, cs);
//        if (text == null)
//            return null;
//        
//        return createXSLT(text);
//    }

//    /**
//     * Создание XSLT Transformer из XML/XSLT шаблона
//     *
//     * @param xsltFile XML/XSLT шаблон
//     * @param cs Кодировка XML/XSLT шаблона
//     * @return XSLT Transformer или null
//     */
//    public static Transformer createXSLT(File xsltFile, String cs) {
//        if (xsltFile == null)
//            return null;
//        if (cs == null)
//            return null;
//        
//        String text = FileUtil.readAllText(xsltFile, cs);
//        if (text == null)
//            return null;
//        
//        return createXSLT(text);
//    }

    /**
     * Создание XSLT Transformer из XML/XSLT шаблона
     *
     * @param xsltFile XML/XSLT шаблон
     * @param cs Кодировка XML/XSLT шаблона
     * @return XSLT Transformer или null
     */
    public static Transformer createXSLT(File xsltFile, Charset cs) {
        if (xsltFile == null)
            return null;
        if (cs == null)
            return null;
        
        String text;
        try {
            text = IOFun.readText(xsltFile, cs);
        } catch (IOException ex) {
//            Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error(ex.getMessage(), ex);
        }
        
        return createXSLT(text);
    }
    private static String trasformErrorResult = "XSLT ERROR";

    /**
     * Преобразование XSLT
     *
     * @param trans XSLT Transformer Шаблон
     * @param xml XML данные
     * @param errResult Возвращаемый результат в случаи ошибки
     * @return Преобразованный результат
     */
    public static String toStringXSLT(Transformer trans, String xml, String errResult) {
        if (trans == null) {
            return errResult;
        }
        if (xml == null) {
            return errResult;
        }
        
        StringReader sread = new StringReader(xml);
        StringWriter swriter = new StringWriter();
        
        try {
            FormatXMLWriter fxmlWriter = new FormatXMLWriter(swriter);
            fxmlWriter.setWriteStartDocument(false);
//            fxmlWriter.setWriteOutline(false);

            Result result = //new StreamResult(swriter);
                    new StAXResult(fxmlWriter);
            StreamSource source = new StreamSource(sread);
            
            trans.transform(source, result);
            
            fxmlWriter.flush();
        } catch (TransformerException ex) {
            throw new Error(ex.getMessage(),ex);
        } catch (XMLStreamException ex) {
//            Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error(ex.getMessage(),ex);
        }
        
        return swriter.toString();
    }

    /**
     * Преобразование XSLT
     *
     * @param xsl Шаблон XSLT
     * @param srcXml XML данные
     * @return Преобразованный результат или null
     */
    public static String toStringXSLT(String xsl, String srcXml) {
        if (xsl == null) {
            throw new IllegalArgumentException("xsl == null");
        }
        
        Transformer trfm = createXSLT(xsl);
        if (trfm == null)
            return null;
        
        return toStringXSLT(trfm, srcXml);
    }

    /**
     * Преобразование XSLT
     *
     * @param trans XSLT Transformer Шаблон
     * @param xml XML данные
     * @return Преобразованный результат
     */
    public static String toStringXSLT(Transformer trans, String xml) {
        return toStringXSLT(trans, xml, trasformErrorResult);
    }
    // </editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="parseXml/Write">
    //<editor-fold defaultstate="collapsed" desc="write(...)">
    /**
     * Сохраняет XMLDOM документ в текст
     * @param writer Поток куда записывать документ
     * @param doc XMLDOM узел
     */
    public static void write(Node doc, final XMLStreamWriter writer)
    {
        if( writer==null )throw new IllegalArgumentException( "writer==null" );
        if (doc == null) {
            throw new IllegalArgumentException("doc == null");
        }
        
        final HashSet failedElements = new HashSet();
        
        Visitor<Node> v = new Visitor<Node>()
        {
            @Override
            public boolean enter(Node n)
            {
                if( n instanceof Element ){
                    String namespaceURI = n.getNamespaceURI();
                    String prefix = n.getPrefix();
                    String name = n.getNodeName();
                    
                    if( namespaceURI==null ){
                        try {
                            writer.writeStartElement(name);
                        } catch (XMLStreamException ex) {
                            Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
                            failedElements.add(n);
                            return false;
                        }
                    }else{
                        if( prefix!=null ){
                            try {
                                writer.writeStartElement(prefix,name,namespaceURI);
                            } catch (XMLStreamException ex) {
                                Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }else{
                            try {
                                writer.writeStartElement(namespaceURI,name);
                            } catch (XMLStreamException ex) {
                                Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    
                    if( n.hasAttributes() ){
                        NamedNodeMap nnm = n.getAttributes();
                        for( int i=0; i<nnm.getLength(); i++ ){
                            Node nAttr = nnm.item(i);
                            if( nAttr==null )continue;
                            
                            String attrNamespaceURI = nAttr.getNamespaceURI();
                            String attrPrefix = nAttr.getPrefix();
                            String attrName = nAttr.getNodeName();
                            String attrValue = nAttr.getTextContent();
                            
                            if( attrNamespaceURI!=null && attrPrefix!=null && attrName!=null ){
                                try {
                                    writer.writeAttribute(
                                        attrPrefix,
                                        attrNamespaceURI,
                                        attrName,
                                        attrValue==null ? "" : attrValue);
                                } catch (XMLStreamException ex) {
                                    Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }else if(
                                attrNamespaceURI!=null && attrPrefix==null && attrName!=null
                                ){
                                try {
                                    writer.writeAttribute(
                                        attrNamespaceURI,
                                        attrName,
                                        attrValue==null ? "" : attrValue);
                                } catch (XMLStreamException ex) {
                                    Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }else if(
                                attrNamespaceURI==null && attrPrefix==null && attrName!=null
                                ){
                                try {
                                    writer.writeAttribute(
                                        attrName,
                                        attrValue==null ? "" : attrValue);
                                } catch (XMLStreamException ex) {
                                    Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }else if( n instanceof org.w3c.dom.Text){
                    org.w3c.dom.Text txt = (org.w3c.dom.Text)n;
                    String value = txt.getNodeValue();
                    try {
                        writer.writeCharacters(value);
                    } catch (XMLStreamException ex) {
                        Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else if( n instanceof org.w3c.dom.CDATASection){
                    org.w3c.dom.CDATASection cdata = (org.w3c.dom.CDATASection)n;
                    try {
                        writer.writeCData(cdata.getNodeValue());
                    } catch (XMLStreamException ex) {
                        Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else if( n instanceof org.w3c.dom.Comment){
                    org.w3c.dom.Comment comment = (org.w3c.dom.Comment)n;
                    try {
                        writer.writeComment(comment.getNodeValue());
                    } catch (XMLStreamException ex) {
                        Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else if( n instanceof org.w3c.dom.ProcessingInstruction){
                    org.w3c.dom.ProcessingInstruction instr = (org.w3c.dom.ProcessingInstruction)n;
                    String target = instr.getTarget();
                    String data = instr.getData();
                    if( data!=null ){
                        try {
                            writer.writeProcessingInstruction(target,data);
                        } catch (XMLStreamException ex) {
                            Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }else{
                        try {
                            writer.writeProcessingInstruction(target);
                        } catch (XMLStreamException ex) {
                            Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
//                }else if( n instanceof org.w3c.dom.){
                }
                
                return true;
            }
            
            @Override
            public void exit(Node n)
            {
                if( n instanceof Element ){
                    if( failedElements.contains(n) )return;
                    try {
                        writer.writeEndElement();
                    } catch (XMLStreamException ex) {
                        Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                }
            }
        };
        
        visit(v, doc);
    }
    
    /**
     * Сохраняет XMLDOM документ в файл
     * @param node xmldom узел
     * @param file файл
     * @param cs Кодировка, или null - тогда utf-8
     */
    public static void write(Node node, java.io.File file, Charset cs){
        if(node==null)throw new IllegalArgumentException("node==null");
        if(file==null)throw new IllegalArgumentException("file==null");
        if(cs==null)cs = Charset.forName("utf-8");
        FormatXMLWriter fwr = null;
        try {
            fwr = new FormatXMLWriter(file, cs);
            write(node, fwr);
            fwr.flush();
            fwr.close();
        } catch (XMLStreamException ex) {
            throw new IOError(ex);
        }
    }
    
    /**
     * Сохраняет XMLDOM документ в файл с кодировкой utf-8
     * @param node xmldom узел
     * @param file файл
     */
    public static void write(Node node, java.io.File file){
        write(node,file,null);
    }
    
    /**
     * Сохраняет XMLDOM документ в файл
     * @param node xmldom узел
     * @param file файл
     * @param cs Кодировка, или null - тогда utf-8
     */
    public static void write(Node node, java.nio.file.Path file, Charset cs){
        if(node==null)throw new IllegalArgumentException("node==null");
        if(file==null)throw new IllegalArgumentException("file==null");
        if(cs==null)cs = Charset.forName("utf-8");
        FormatXMLWriter fwr = null;
        try {
            fwr = new FormatXMLWriter(file, cs);
            write(node, fwr);
            fwr.flush();
            fwr.close();
        } catch (XMLStreamException ex) {
            throw new IOError(ex);
        }
    }
    
    /**
     * Сохраняет XMLDOM документ в файл с кодировкой utf-8
     * @param node xmldom узел
     * @param file файл
     */
    public static void write(Node node, java.nio.file.Path file){
        write(node,file,null);
    }    
    /**
     * Сохраняет XMLDOM документ в файл
     * @param node xmldom узел
     * @param file файл
     * @param cs Кодировка, или null - тогда utf-8
     */
    public static void write(Node node, xyz.cofe.io.fs.File file, Charset cs){
        if(node==null)throw new IllegalArgumentException("node==null");
        if(file==null)throw new IllegalArgumentException("file==null");
        if(cs==null)cs = Charset.forName("utf-8");
        FormatXMLWriter fwr = null;
        try {
            fwr = new FormatXMLWriter(file, cs);
            write(node, fwr);
            fwr.flush();
            fwr.close();
        } catch (XMLStreamException ex) {
            throw new IOError(ex);
        }
    }
    
    /**
     * Сохраняет XMLDOM документ в файл с кодировкой utf-8
     * @param node xmldom узел
     * @param file файл
     */
    public static void write(Node node, xyz.cofe.io.fs.File file){
        write(node,file,null);
    }
    
    /**
     * Сохраняет XMLDOM документ в поток
     * @param node xmldom узел
     * @param writer текстовый поток
     */
    public static void write(Node node, Writer writer){
        if(node==null)throw new IllegalArgumentException("node==null");
        if(writer==null)throw new IllegalArgumentException("writer==null");
        FormatXMLWriter fwr = null;
        try {
            fwr = new FormatXMLWriter(writer);
            write(node, fwr);
            fwr.flush();
        } catch (XMLStreamException ex) {
            throw new IOError(ex);
        }
    }
    
    /**
     * Сохраняет XMLDOM документ как строку
     * @param node xmldom узел
     * @return Строка xml
     */
    public static String writeAsString(Node node){
        StringWriter sw = new StringWriter();
        write(node, sw);
        return sw.toString();
    }
    
    /**
     * Сохраняет XMLDOM документ в поток
     * @param node xmldom узел
     * @param writer текстовый поток
     * @param cs Кодировка, или null - тогда utf-8
     */
    public static void write(Node node, OutputStream writer, Charset cs){
        if(node==null)throw new IllegalArgumentException("node==null");
        if(writer==null)throw new IllegalArgumentException("writer==null");
        if(cs==null)cs = Charset.forName("utf-8");
        FormatXMLWriter fwr = null;
        try {
            fwr = new FormatXMLWriter(writer,cs);
            write(node, fwr);
            fwr.flush();
        } catch (XMLStreamException ex) {
            throw new IOError(ex);
        }
    }
    
    /**
     * Сохраняет XMLDOM документ в поток с кодировкой utf-8.
     * @param node xmldom узел
     * @param writer текстовый поток
     */
    public static void write(Node node, OutputStream writer){
        if(node==null)throw new IllegalArgumentException("node==null");
        if(writer==null)throw new IllegalArgumentException("writer==null");
        write(node,writer,null);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="parseXml()">
    /**
     * Парсинг XML из файла
     * @param file файл
     * @return XML DOM документ
     */
    public static Document parseXml( File file ) {
        if (file== null) {
            throw new IllegalArgumentException("file==null");
        }
        FileInputStream fin=null;
        Document doc = null;
        try{
            fin = new FileInputStream(file);
            doc = parseXml(fin);
        }catch(IOException ex){
            throw new IOError(ex);
        }finally{
            if( fin!=null ){
                try {
                    fin.close();
                } catch (IOException ex1) {
                    throw new IOError(ex1);
                }
            }
        }
        return doc;
    }
    
    /**
     * Парсинг XML из файла
     * @param file файл
     * @return XML DOM документ
     */
    public static Document parseXml( xyz.cofe.io.fs.File file ) {
        if (file== null) {
            throw new IllegalArgumentException("file==null");
        }
        InputStream fin=null;
        Document doc = null;
        try{
            fin = file.readStream();
            doc = parseXml(fin);
        }finally{
            if( fin!=null ){
                try {
                    fin.close();
                } catch (IOException ex1) {
                    throw new IOError(ex1);
                }
            }
        }
        return doc;
    }
    
    /**
     * Парсинг XML из файла
     * @param file файл
     * @return XML DOM документ
     */
    public static Document parseXml( java.nio.file.Path file ) {
        if (file== null) {
            throw new IllegalArgumentException("file==null");
        }
        InputStream fin=null;
        Document doc = null;
        try{
            fin = Files.newInputStream(file);
            doc = parseXml(fin);
        } catch (IOException ex) {
            throw new IOError(ex);
        }finally{
            if( fin!=null ){
                try {
                    fin.close();
                } catch (IOException ex1) {
                    throw new IOError(ex1);
                }
            }
        }
        return doc;
    }
    
    /**
     * Востанавливает XMLDOM документ из текста
     * @param xml Текст
     * @return XMLDOM документ
     */
    public static Document parseXml(InputStream xml)
    {
        if (xml == null) {
            throw new IllegalArgumentException("xml == null");
        }
        DocumentBuilder b = docBuilder();
        Document res = null;
        if( b!=null )
        {
            InputSource inSrc = new InputSource(xml);
            try
            {
                res = b.parse(inSrc);
            }
            catch (SAXException | IOException ex) {
                throw new Error(ex.getMessage(),ex);
            }
        }
        return res;
    }
    
    /**
     * Востанавливает XMLDOM документ из текста
     * @param xml Текст
     * @return XMLDOM документ
     * @throws IOException Ошибка IO
     * @throws SAXException Ошибка XML
     */
    public static Document parseXml(Reader xml) throws IOException, SAXException
    {
        if (xml == null) {
            throw new IllegalArgumentException("xml == null");
        }
        DocumentBuilder b = docBuilder();
        Document res = null;
        if( b!=null )
        {
            InputSource inSrc = new InputSource(xml);
            
            try
            {
                res = b.parse(inSrc);
            }
            catch (SAXException ex) {
                throw new Error(ex.getMessage(),ex);
            }
            catch (IOException ex) {
                throw new Error(ex.getMessage(),ex);
            }
            finally {
            }
        }
        return res;
    }
    
    /**
     * Востанавливает XMLDOM документ из текста
     * @param xml Текст
     * @return XMLDOM документ
     */
    public static Document parseXml(String xml)
    {
        if (xml == null) {
            throw new IllegalArgumentException("xml == null");
        }
        DocumentBuilder b = docBuilder();
        Document res = null;
        if( b!=null )
        {
            StringReader strRD = new StringReader(xml);
            InputSource inSrc = new InputSource(strRD);
            
            try
            {
                res = b.parse(inSrc);
            }
            catch (SAXException ex) {
                throw new Error(ex.getMessage(),ex);
            }
            catch (IOException ex) {
                throw new Error(ex.getMessage(),ex);
            }
            finally {
                strRD.close();
            }
        }
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="toXMLString()">
    /**
     * Сохраняет XMLDOM узлы в текст
     * @param nodeList XMLDOM узлы
     * @return Текстовое представлении или null;
     */
    public static String toXMLString(NodeList nodeList)
    {
        if (nodeList == null) {
            throw new IllegalArgumentException("nodeList == null");
        }
        
        StringBuilder sb = new StringBuilder();
        for( int i=0; i<nodeList.getLength(); i++ ){
            sb.append( toXMLString(nodeList.item(i)) );
        }
        return sb.toString();
    }
    
    /**
     * Сохраняет XMLDOM документ в текст
     * @param node XMLDOM документ
     * @return Текстовое представлении или null;
     */
    public static String toXMLString(Node node)
    {
        if (node == null) {
            throw new IllegalArgumentException("doc == null");
        }
        
        StringWriter sw = new StringWriter();
        
        DOMSource srcDom = new DOMSource(node);
        //        DOMResult resDom = new DOMResult();
        javax.xml.transform.stream.StreamResult
            resStrm = new javax.xml.transform.stream.StreamResult();
        resStrm.setWriter(sw);

        TransformerFactory tfact = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tfact.newTransformer();
        //            transformer.transform(srcDom, resDom);
        transformer.transform(srcDom, resStrm);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error( ex.getMessage(), ex );
        } catch (TransformerException ex) {
            Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error( ex.getMessage(), ex );
        }

        return sw.toString();
    }
    //</editor-fold>
    //</editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc="DocumentBuilder">
    private static DocumentBuilderFactory docBuilderFactory = null;

    /**
     * DocumentBuilderFactory по умолч.
     *
     * @return DocumentBuilderFactory по умолч.
     */
    public static DocumentBuilderFactory docBuilderFactory() {
        if (docBuilderFactory == null) {
            docBuilderFactory = DocumentBuilderFactory.newInstance();
        }
        return docBuilderFactory;
    }
    private static DocumentBuilder builder = null;

    /**
     * DocumentBuilder по умолч.
     *
     * @return DocumentBuilder по умолч.
     */
    public static DocumentBuilder docBuilder() {
        if (builder == null) {
            try {
                builder = docBuilderFactory().newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                builder = null;
                throw new Error(ex.getMessage(),ex);
            }
        }
        return builder;
    }

    /**
     * Создает новый XMLDOM документ
     *
     * @return XMLDOM документ или null
     */
    public static Document createDocument() {
        DocumentBuilder b = docBuilder();
        Document res = null;
        if (b != null)
            res = b.newDocument();
        return res;
    }
    // </editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="visit()">
    /**
     * Обход XML узлов
     * @param start Начальный узел
     * @param visitor Посетитель
     */
    public static void visit(Visitor<Node> visitor, Node start){
        if (start== null) {
            throw new IllegalArgumentException("start==null");
        }
        if (visitor== null) {
            throw new IllegalArgumentException("visitor==null");
        }
        BasicVisitor.visit(visitor,start,followers());
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="children()">
    /**
     * Перечисляет дочерние узлы, первого уровня вложенности
     * @param node Узел
     * @return Дочерние узлы
     */
    public static Eterable<Node> children(Node node){
        if (node== null) {
            throw new IllegalArgumentException("node==null");
        }
        if( !node.hasChildNodes() )return Eterable.empty();
        return iterable(node.getChildNodes());
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="elements()">
    /**
     * Перечисляет дочерние тэги, первого уровня вложенности
     * @param node Узел
     * @return Дочерние тэги
     */
    public static Eterable<org.w3c.dom.Element> elements(Node node){
        if (node== null) {
            throw new IllegalArgumentException("node==null");
        }
        if( !node.hasChildNodes() )return Eterable.empty();
        return
            iterable(
                node.getChildNodes()
            ).filter(Predicates.isElement).map( from -> from instanceof Element ? (Element)from : null );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="filter()">
    /**
     * Предикаты/фильтры
     */
    public static class Predicates {
        /**
         * Предикат: возвращает только тэги (instanceof org.w3c.dom.Element)
         */
        public static final Predicate<Node> isElement = value->{
            if( value==null )return false;
            if( value instanceof Element )return true;
            return false;
        };
        
        /**
         * Предикат: возвращает тэги чье имя совпадает с указанным
         * @param nodeNamePredicate фильтр имени
         * @return отфильтрованные узлы
         */
        public static Predicate<Node> nodeName( final Predicate<String> nodeNamePredicate ){
            return value->{
                if( value==null )return false;
                if( nodeNamePredicate==null )return false;
                return nodeNamePredicate.test(value.getNodeName());
            };
        }
        
        /**
         * Предикат: возвращает тэги чье имя совпадает с указанным
         * @param nodeName имя узла
         * @param ignoreCase игнорировать регистр в имени
         * @return предикат
         */
        public static Predicate<Node> nodeName( String nodeName, boolean ignoreCase ){
            return nodeName( ignoreCase ?  Text.Predicates.equalsIgnoreCase(nodeName) : Text.Predicates.equals(nodeName) );
        }
        
        /**
         * Создает логический предикат (фильтр) AND от указанных предикаторв
         * @param preds аргументы AND
         * @return предикат
         */
        public static Predicate<Node> and( Predicate<Node> ... preds ){
            return xyz.cofe.ecolls.Predicates.and(preds);
        }
        
        /**
         * Создает логический предикат (фильтр) OR от указанных предикаторв
         * @param preds аргументы OR
         * @return предикат
         */
        public static Predicate<Node> or( Predicate<Node> ... preds ){
            return xyz.cofe.ecolls.Predicates.or(preds);
        }
        
        /**
         * Создает предикат инвертирующий результат исходного предиката
         * @param pred исходный предикат
         * @return предикат
         */
        public static Predicate<Node> not( Predicate<Node> pred ){
            return xyz.cofe.ecolls.Predicates.not(pred);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="iterable(nodeList)">
    /**
     * Итератор по списку XML узлов
     * @param nl Список XML узлов
     * @return Последовательность XML Узлов
     */
    public static Eterable<org.w3c.dom.Node> iterable(org.w3c.dom.NodeList nl)
    {
        return new XMLNodeIterable(nl);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="tree(node)">
    /**
     * Создает нисходящий итератор для обхода XML дерева
     * @param node исходный корневой узлел
     * @return итератор по дереву (поддереву) XML
     */
    public static Eterable<TreeStep<Node>> tree( Node node){
        if (node== null) {
            throw new IllegalArgumentException("node==null");
        }
        return Eterable.tree(node, followers()).go();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="walk(node)">
    /**
     * Создает нисходящий итератор для обхода XML дерева
     * @param node исходный корневой узлел
     * @return итератор по дереву (поддереву) XML
     */
    public static Eterable<Node> walk(Node node){
        if (node== null) {
            throw new IllegalArgumentException("node==null");
        }
        return Eterable.tree(node, followers()).walk();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="isElement(node)">
    /**
     * Проверят что указанный узел является тэгом
     * @param node узел
     * @return true - является тэгом
     */
    public static boolean isElement(Node node){
        return node instanceof Element;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="isText(node)">
    /**
     * Проверят что указанный узел является текстовым
     * @param node узел
     * @return true - является текстовым
     */
    public static boolean isText(Node node){
        return node instanceof org.w3c.dom.Text;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="isComment(node)">
    /**
     * Проверят что указанный узел является комментарием
     * @param node узел
     * @return true - является комментарием
     */
    public static boolean isComment(Node node){
        return node instanceof org.w3c.dom.Comment;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="isDocument(node)">
    /**
     * Проверят что указанный узел является документом
     * @param node узел
     * @return true - является документом
     */
    public static boolean isDocument(Node node){
        return node instanceof org.w3c.dom.Document;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getTagName(node)">
    /**
     * Имя тега/узла
     * @param node узел
     * @return имя
     */
    public static String getTagName( Node node ){
        if( !(node instanceof Element) ){
            return node.getNodeName();
        }else{
            return ((Element)node).getTagName();
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getText(context)">
    /**
     * Получение текста узлан
     * @param n узел
     * @return текст
     */
    public static String getText(Node n){
        if (n == null) {
            throw new IllegalArgumentException("n == null");
        }
        
        final StringBuilder sb = new StringBuilder();
        Visitor<Node> v = new Visitor<Node>()
        {
            @Override
            public boolean enter(Node n)
            {
                if( n instanceof org.w3c.dom.Text){
                    sb.append(n.getNodeValue());
                }
                return true;
            }
            
            @Override
            public void exit(Node n) { }
        };
        
        visit(v, n);
        
        return sb.toString();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="attributes">
    /**
     * Возвращает именовааные узлы как карту
     * @param nnm именованные узлы
     * @return карта
     */
    public static Map<String,String> asMap( NamedNodeMap nnm ){
        if (nnm== null) {
            throw new IllegalArgumentException("nnm==null");
        }
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        for( int ai=0; ai<nnm.getLength(); ai++ ){
            Node na = nnm.item(ai);
            if( na==null )continue;
            String name = na.getNodeName();
            String value = na.getNodeValue();
            if( name==null )continue;
            if( value==null )value = "";
            map.put(name, value);
        }
        return map;
    }
    
    /**
     * Возвращает карту атрибутов для узла
     * @param n узел
     * @return карта
     */
    public static Map<String,String> getAttrs(Node n){
        if (n== null) {
            throw new IllegalArgumentException("n==null");
        }
        if( !(n instanceof Element) ){
            return new LinkedHashMap<>();
        }
        
        Element el = (Element)n;
        NamedNodeMap nnm = el.getAttributes();
        if( nnm!=null ) return asMap(nnm);
        return new LinkedHashMap<>();
    }
    
    /**
     * Возвразает значение атрибута
     * @param n узел (тэг)
     * @param name имя атрибута
     * @return значение или null
     */
    public static String getAttribute( Node n, String name ){
        if( n==null )throw new IllegalArgumentException("n == null");
        if( name==null )throw new IllegalArgumentException("name == null");
        
        NamedNodeMap nnm = n.getAttributes();
        if( nnm!=null ){
            for( int ai=0; ai<nnm.getLength(); ai++ ){
                Node na = nnm.item(ai);
                if( na==null )continue;
                
                String aname = na.getNodeName();
                String avalue = na.getNodeValue();
                if( aname==null )continue;
                if( !name.equals(aname) )continue;
                return avalue;
            }
        }
        
        return null;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="nodeFollowers">
    /**
     * Извлекает дочерние элементы
     * @return Итерфес доступа к дочерним объектам
     */
    public static NodesExtracter<Node,Node> followers(){
        return followers(true);
    }

    /**
     * Извлекает дочерние элементы
     * @param detectCycle Детекитить появления циклов при извлечении
     * @return Итерфес доступа к дочерним объектам
     */
    public static NodesExtracter<Node,Node> followers(final boolean detectCycle){
        return new NodesExtracter<Node, Node>()
        {
            protected Set<Node> visited;
            
            @Override
            public Iterable<Node> extract(Node from)
            {                
                if( from==null )return null;
                
                if( detectCycle ){
                    if( visited==null ){
                        visited = new LinkedHashSet<>();
                    }
                    if( visited.contains(from) ){
                        Logger.getLogger(XmlUtil.class.getName()).log(
                            Level.WARNING,
                            "detect cycle in xml dom"
                        );
                        return null;
                    }
                    visited.add(from);
                }
                
                if( from instanceof Element )return iterable(((Element)from).getChildNodes());
                if( from instanceof Document ){
                    Document d = (Document)from;
                    Element e = d.getDocumentElement();
                    if( e instanceof Node ){
                        return Eterable.single((Node)e);
                    }
                }
                return null;
            }
        };
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="XPath запросы">
    //<editor-fold defaultstate="collapsed" desc="XPathFactory">
    private static XPathFactory xpathFactory = null;
    
    /**
     * XPath фабрика
     *
     * @return XPath фабрика
     */
    public synchronized static XPathFactory getXPathFactory() {
        if (xpathFactory == null) {
            xpathFactory = XPathFactory.newInstance();
        }
        return xpathFactory;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="XPath">
    private static XPath xpath = null;
    
    /**
     * XPath объект
     *
     * @return XPath объект
     */
    public synchronized static XPath getXPath() {
        if (xpath == null) {
            XPathFactory f = getXPathFactory();
            if (f != null)
                xpath = f.newXPath();
        }
        return xpath;
    }
    //</editor-fold>
    
    /**
     * Создание запросов xpath
     */
    public static class XPathQuery {
        //<editor-fold defaultstate="collapsed" desc="construct">
        /**
         * Конструктор
         * @param node узел относительно которого создается xpath запрос
         */
        public XPathQuery(Node node){
            this.context = node;
        }
        
        /**
         * Конструктор
         * @param node узел относительно которого выполняется xpath запрос
         * @param queryString xpath запрос
         */
        public XPathQuery(Node node, String queryString){
            this.context = node;
            this.query = queryString;
        }
        
        /**
         * Конструктор
         * @param node узел относительно которого выполняется xpath запрос
         * @param xpath xpath запрос
         */
        public XPathQuery(Node node, XPath xpath){
            this.context = node;
            this.xpath = xpath;
        }

        /**
         * Конструктор копирования
         * @param node узел относительно которого выполняется xpath запрос
         * @param queryString xpath запрос
         * @param xpath xpath запрос
         */
        public XPathQuery(Node node, String queryString, XPath xpath){
            this.context = node;
            this.query = queryString;
            this.xpath = xpath;
        }
        
        /**
         * Конструктор копирования
         * @param sample образец
         */
        public XPathQuery(XPathQuery sample){
            if( sample!=null ){
                xpath = sample.xpath;
                context = sample.context;
                query = sample.query;
            }
        }
        
        //<editor-fold defaultstate="collapsed" desc="clone">
        /**
         * Создание клона запроса
         * @return клон
         */
        @Override
        public XPathQuery clone(){
            synchronized(this){
                return new XPathQuery(context, xpath);
            }
        }
        //</editor-fold>
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="xpath : XPath">
        protected XPath xpath;
        
        /**
         * Указывает XPath объект
         * @return XPath объект
         */
        public synchronized XPath getXpath() {
            if( xpath==null ){
                xpath = getXPath();
            }
            return xpath;
        }
        
        /**
         * Указывает XPath объект
         * @param xpath XPath объект
         */
        public synchronized void setXpath(XPath xpath) {
            this.xpath = xpath;
        }
        
        /**
         * Указывает XPath объект
         * @param xpath XPath объект
         * @return self ссылка
         */
        public XPathQuery xpath( XPath xpath ){
            setXpath(xpath);
            return this;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="context : Node">
        protected Node context;
        
        /**
         * Указывает узел относительно которого выполняется xpath запрос
         * @return узел xml
         */
        public synchronized Node getContext() {
            return context;
        }
        
        /**
         * Указывает узел относительно которого выполняется xpath запрос
         * @param context узел xml
         */
        public synchronized void setContext(Node context) {
            this.context = context;
        }
        
        /**
         * Указывает узел относительно которого выполняется xpath запрос
         * @param node узел xml
         * @return self ссылка
         */
        public XPathQuery context(Node node){
            setContext(node);
            return this;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="query : String">
        protected String query = null;
        
        /**
         * Указывает исходный текст запроса
         * @return исходный текст запроса
         */
        public synchronized String getQuery() {
            return query;
        }
        
        /**
         * Указывает исходный текст запроса
         * @param query исходный текст запроса
         */
        public synchronized void setQuery(String query) {
            this.query = query;
        }
        
        /**
         * Указывает исходный текст запроса
         * @param query исходный текст запроса
         * @return self ссылка
         */
        public XPathQuery query(String query){
            setQuery(query);
            return this;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="string : String">
        /**
         * Получение строки согласно xpath запросу
         * @return Строка или null
         */
        public synchronized String getString(){
            XPath xpath = getXpath();
            if( xpath==null )throw new IllegalStateException("xpath not set");
            if( context==null )throw new IllegalStateException("node not set");
            if( query==null )throw new IllegalStateException("query not set");
            try {
                return (String) xpath.evaluate(query, context, XPathConstants.STRING);
            } catch (XPathExpressionException ex) {
                throw new Error(ex.getMessage(), ex);
            }
        }

        /**
         * Получение строки согласно xpath запросу
         * @param defaultValue значение по умолчанию
         * @return строка
         */
        public String as(String defaultValue){
            String v = getString();
            return v!=null ? v : defaultValue;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="nodeList : NodeList">
        /**
         * Получение списка узлов
         * @return список узлов или null
         */
        public synchronized NodeList getNodeList(){
            XPath xpath = getXpath();
            if( xpath==null )throw new IllegalStateException("xpath not set");
            if( context==null )throw new IllegalStateException("node not set");
            if( query==null )throw new IllegalStateException("query not set");
            try {
                return (NodeList) xpath.evaluate(query, context, XPathConstants.NODESET);
            } catch (XPathExpressionException ex) {
                throw new Error(ex.getMessage(), ex);
            }
        }

        /**
         * Получение списка узлов
         * @param defaultValue значение по умолчанию
         * @return список узлов
         */
        public NodeList as(NodeList defaultValue){
            NodeList v = getNodeList();
            return v!=null ? v : defaultValue;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="node : Node">
        /**
         * Получение узла
         * @return узел или null
         */
        public synchronized Node getNode(){
            XPath xpath = getXpath();
            if( xpath==null )throw new IllegalStateException("xpath not set");
            if( context==null )throw new IllegalStateException("context not set");
            if( query==null )throw new IllegalStateException("query not set");
            try {
                return (Node) xpath.evaluate(query, context, XPathConstants.NODE);
            } catch (XPathExpressionException ex) {
                throw new Error(ex.getMessage(), ex);
            }
        }

        /**
         * Получение узла
         * @param defaultValue значение по умолчанию
         * @return узел
         */
        public Node as(Node defaultValue){
            Node v = getNode();
            return v!=null ? v : defaultValue;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="double : Double">
        /**
         * Получение числа
         * @return число или null
         */
        public synchronized Double getDouble(){
            XPath xpath = getXpath();
            if( xpath==null )throw new IllegalStateException("xpath not set");
            if( context==null )throw new IllegalStateException("node not set");
            if( query==null )throw new IllegalStateException("query not set");
            try {
                return (Double) xpath.evaluate(query, context, XPathConstants.NUMBER);
            } catch (XPathExpressionException ex) {
                throw new Error(ex.getMessage(), ex);
            }
        }

        /**
         * Получение числа
         * @param defaultValue значение по умолчанию
         * @return число
         */
        public Double as(Double defaultValue){
            Double v = getDouble();
            return v!=null ? v : defaultValue;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="boolean : Boolean - получение Boolean значения по xpath">
        /**
         * Получение булева значения
         * @return булево или null
         */
        public synchronized Boolean getBoolean(){
            XPath xpath = getXpath();
            if( xpath==null )throw new IllegalStateException("xpath not set");
            if( context==null )throw new IllegalStateException("node not set");
            if( query==null )throw new IllegalStateException("query not set");
            try {
                return (Boolean) xpath.evaluate(query, context, XPathConstants.BOOLEAN);
            } catch (XPathExpressionException ex) {
                throw new Error(ex.getMessage(), ex);
            }
        }

        /**
         * Получение булева значения
         * @param defaultValue значение по умолчанию
         * @return булево
         */
        public Boolean as(Boolean defaultValue){
            Boolean v = getBoolean();
            return v!=null ? v : defaultValue;
        }
        //</editor-fold>
    }
    
    //<editor-fold defaultstate="collapsed" desc="xpath(node,query)">
    /**
     * Создание xpatg запроса к узлу
     * @param node Узел
     * @param query запрос
     * @return Выполнение запроса
     */
    public static XPathQuery xpath( Node node, String query ){
        if (node== null) {
            throw new IllegalArgumentException("node==null");
        }
        if (query== null) {
            throw new IllegalArgumentException("query==null");
        }
        return new XPathQuery(node,query);
    }
    //</editor-fold>
    // </editor-fold>
}
