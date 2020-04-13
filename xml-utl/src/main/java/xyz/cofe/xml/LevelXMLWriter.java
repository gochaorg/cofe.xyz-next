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

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * XMLWriter с подсчем вложенности
 * @author GoCha
 */
public class LevelXMLWriter implements XMLStreamWriter
{
    private XMLStreamWriter writer = null;
    private Integer level = 0;

    /**
     * Конструктор
     * @param writer через какой интерфейс XML писать данные
     */
    public LevelXMLWriter(XMLStreamWriter writer)
    {
        if (writer == null)
        {
            throw new IllegalArgumentException("writer==null");
        }
        this.writer = writer;
    }

    /**
     * @return Текущий уровень вложения (1 - соответствует первому открытому тегу)
     */
    public Integer getLevel()
    {
        return level;
    }
    
    /**
     * Устанавливает уровень вложенности
     * @param level Номер уровня вложенности
     * @return Старый уровень вложенности
     */
    protected Integer setLevel(int level)
    {
        int old = this.level;
        this.level = level;
        return old;
    }

    /**
     * Увеличивает на единицу уровень вложенности
     */
    protected void incrementLevel()
    {
        level++;
    }

    /**
     * Уменьшает на единицу уровень вложенности
     */
    protected void decrementLevel()
    {
        level--;
    }

    /**
     * Возвращает интерфес через который происходит делегирование
     * @return XML интерфейс
     */
    public XMLStreamWriter getWriter()
    {
        return writer;
    }
    
    protected void beginElement() throws XMLStreamException
    {
        incrementLevel();
    }

    protected void endElement() throws XMLStreamException
    {
        decrementLevel();
    }

    /**
     * Открывает элемент (тег)
     * @param prefix Префикс
     * @param localName Имя
     * @param namespaceURI Пространство имен
     * @throws javax.xml.stream.XMLStreamException всякие ошибки
     */
    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException
    {
        beginElement();
        writer.writeStartElement(prefix, localName, namespaceURI);
    }

    /**
     * Открывает элемент (тег)
     * @param namespaceURI Пространство имен
     * @param localName Имя
     * @throws javax.xml.stream.XMLStreamException всякие ошибки
     */
    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException
    {
        beginElement();
        writer.writeStartElement(namespaceURI, localName);
    }

    /**
     * Открывает элемент (тег)
     * @param localName Имя
     * @throws javax.xml.stream.XMLStreamException всякие ошибки
     */
    @Override
    public void writeStartElement(String localName) throws XMLStreamException
    {
        beginElement();
        writer.writeStartElement(localName);
    }

    /**
     * Открывает документ
     * @param encoding Кодировка
     * @param version версия
     * @throws javax.xml.stream.XMLStreamException всякие ошибки
     */
    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException
    {
//        beginElement();
        writer.writeStartDocument(encoding, version);
    }

    /**
     * Открывает документ
     * @param version версия
     * @throws javax.xml.stream.XMLStreamException всякие ошибки
     */
    @Override
    public void writeStartDocument(String version) throws XMLStreamException
    {
//        beginElement();
        writer.writeStartDocument(version);
    }

    /**
     * Открывает документ
     * @throws javax.xml.stream.XMLStreamException всякие ошибки
     */
    @Override
    public void writeStartDocument() throws XMLStreamException
    {
//        beginElement();
        writer.writeStartDocument();
    }

    @Override
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException
    {
        writer.writeProcessingInstruction(target, data);
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException
    {
        writer.writeProcessingInstruction(target);
    }

    @Override
    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException
    {
        writer.writeNamespace(prefix, namespaceURI);
    }

    @Override
    public void writeEntityRef(String name) throws XMLStreamException
    {
        writer.writeEntityRef(name);
    }

    /**
     * Закрывает элемент (тег)
     * @throws javax.xml.stream.XMLStreamException всякие ошибки
     */
    @Override
    public void writeEndElement() throws XMLStreamException
    {
        endElement();
        writer.writeEndElement();
    }

    /**
     * Закрывает документ
     * @throws javax.xml.stream.XMLStreamException всякие ошибки
     */
    @Override
    public void writeEndDocument() throws XMLStreamException
    {
//        endElement();
        writer.writeEndDocument();
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException
    {
        writer.writeEmptyElement(localName);
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException
    {
        writer.writeEmptyElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException
    {
        writer.writeEmptyElement(namespaceURI, localName);
    }

    @Override
    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException
    {
        writer.writeDefaultNamespace(namespaceURI);
    }

    @Override
    public void writeDTD(String dtd) throws XMLStreamException
    {
        writer.writeDTD(dtd);
    }

    /**
     * Пишет коментарии
     * @param data коментарии
     * @throws javax.xml.stream.XMLStreamException всякие ошибки
     */
    @Override
    public void writeComment(String data) throws XMLStreamException
    {
        writer.writeComment(data);
    }

    /**
     * Пишет набор символов (которые нужно еще экранировать)
     * @param text набор
     * @param start начальный символ
     * @param len кол-во
     * @throws javax.xml.stream.XMLStreamException всякие ошибки
     */
    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException
    {
        writer.writeCharacters(text, start, len);
    }

    /**
     * Пишет набор символов (которые нужно еще экранировать)
     * @param text набор
     * @throws javax.xml.stream.XMLStreamException всякие ошибки
     */
    @Override
    public void writeCharacters(String text) throws XMLStreamException
    {
        writer.writeCharacters(text);
    }

    @Override
    public void writeCData(String data) throws XMLStreamException
    {
        writer.writeCData(data);
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException
    {
        writer.writeAttribute(namespaceURI, localName, value);
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException
    {
        writer.writeAttribute(prefix, namespaceURI, localName, value);
    }

    /**
     * Пишет атрибут
     * @param localName имя атрибута
     * @param value значение
     * @throws javax.xml.stream.XMLStreamException всякие ошибки
     */
    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException
    {
        writer.writeAttribute(localName, value);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException
    {
        writer.setPrefix(prefix, uri);
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException
    {
        writer.setNamespaceContext(context);
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException
    {
        writer.setDefaultNamespace(uri);
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException
    {
        return writer.getProperty(name);
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException
    {
        return writer.getPrefix(uri);
    }

    @Override
    public NamespaceContext getNamespaceContext()
    {
        return writer.getNamespaceContext();
    }

    @Override
    public void flush() throws XMLStreamException
    {
        writer.flush();
    }

    @Override
    public void close() throws XMLStreamException
    {
        writer.close();
    }
}
