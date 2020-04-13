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
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Читает XML поток и ведет сведения о текущем уровне
 * @author GoCha
 */
public class LevelXMLReader implements XMLStreamReader
{
    private XMLStreamReader reader;
    private Integer level = 0;
    
    public LevelXMLReader(XMLStreamReader reader,int level)
    {
        if (reader == null)        
        {
            throw new IllegalArgumentException("reader == null");
        }
        this.reader = reader;
        this.level = level;
    }

    public LevelXMLReader(XMLStreamReader reader)
    {
        this(reader,0);
    }

    /**
     * Возвращает текущий уровень вложенности
     * @return текущий уровень вложенности
     */
    public Integer getLevel()
    {
        return level;
    }

    /**
     * Указывает текущий уровень вложенности
     * @param level текущий уровень вложенности
     */
    public void setLevel(int level)
    {
        this.level = level;
    }
    
    protected void incremetnLevel()
    {
        this.level++;
    }
    
    protected void decrementLevel()
    {
        this.level--;
    }
    
    protected void checkEvent()
    {
        switch(getEventType())
        {
            case XMLStreamReader.START_ELEMENT:
                incremetnLevel();
                break;
            case XMLStreamReader.END_ELEMENT:
                decrementLevel();
                break;
        }
    }

    public XMLStreamReader getReader()
    {
        return reader;
    }

    @Override
    public boolean standaloneSet()
    {
        return reader.standaloneSet();
    }

    @Override
    public void require(int type, String namespaceURI, String localName) throws XMLStreamException
    {
        reader.require(type, namespaceURI, localName);
    }

    @Override
    public int nextTag() throws XMLStreamException
    {
        int res = reader.nextTag();
        checkEvent();
        return res;
    }

    @Override
    public int next() throws XMLStreamException
    {
        int res = reader.next();
        checkEvent();
        return res;
    }

    @Override
    public boolean isWhiteSpace()
    {
        return reader.isWhiteSpace();
    }

    @Override
    public boolean isStartElement()
    {
        return reader.isStartElement();
    }

    @Override
    public boolean isStandalone()
    {
        return reader.isStandalone();
    }

    @Override
    public boolean isEndElement()
    {
        return reader.isEndElement();
    }

    @Override
    public boolean isCharacters()
    {
        return reader.isCharacters();
    }

    @Override
    public boolean isAttributeSpecified(int index)
    {
        return reader.isAttributeSpecified(index);
    }

    @Override
    public boolean hasText()
    {
        return reader.hasText();
    }

    @Override
    public boolean hasNext() throws XMLStreamException
    {
        return reader.hasNext();
    }

    @Override
    public boolean hasName()
    {
        return reader.hasName();
    }

    @Override
    public String getVersion()
    {
        return reader.getVersion();
    }

    @Override
    public int getTextStart()
    {
        return reader.getTextStart();
    }

    @Override
    public int getTextLength()
    {
        return reader.getTextLength();
    }

    @Override
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException
    {
        return reader.getTextCharacters(sourceStart, target, targetStart, length);
    }

    @Override
    public char[] getTextCharacters()
    {
        return reader.getTextCharacters();
    }

    @Override
    public String getText()
    {
        return reader.getText();
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException
    {
        return reader.getProperty(name);
    }

    @Override
    public String getPrefix()
    {
        return reader.getPrefix();
    }

    @Override
    public String getPITarget()
    {
        return reader.getPITarget();
    }

    @Override
    public String getPIData()
    {
        return reader.getPIData();
    }

    @Override
    public String getNamespaceURI()
    {
        return reader.getNamespaceURI();
    }

    @Override
    public String getNamespaceURI(int index)
    {
        return reader.getNamespaceURI(index);
    }

    @Override
    public String getNamespaceURI(String prefix)
    {
        return reader.getNamespaceURI(prefix);
    }

    @Override
    public String getNamespacePrefix(int index)
    {
        return reader.getNamespacePrefix(index);
    }

    @Override
    public int getNamespaceCount()
    {
        return reader.getNamespaceCount();
    }

    @Override
    public NamespaceContext getNamespaceContext()
    {
        return reader.getNamespaceContext();
    }

    @Override
    public QName getName()
    {
        return reader.getName();
    }

    @Override
    public Location getLocation()
    {
        return reader.getLocation();
    }

    @Override
    public String getLocalName()
    {
        return reader.getLocalName();
    }

    @Override
    public int getEventType()
    {
        return reader.getEventType();
    }

    @Override
    public String getEncoding()
    {
        return reader.getEncoding();
    }

    @Override
    public String getElementText() throws XMLStreamException
    {
        return reader.getElementText();
    }

    @Override
    public String getCharacterEncodingScheme()
    {
        return reader.getCharacterEncodingScheme();
    }

    @Override
    public String getAttributeValue(int index)
    {
        return reader.getAttributeValue(index);
    }

    @Override
    public String getAttributeValue(String namespaceURI, String localName)
    {
        return reader.getAttributeValue(namespaceURI, localName);
    }

    @Override
    public String getAttributeType(int index)
    {
        return reader.getAttributeType(index);
    }

    @Override
    public String getAttributePrefix(int index)
    {
        return reader.getAttributePrefix(index);
    }

    @Override
    public String getAttributeNamespace(int index)
    {
        return reader.getAttributeNamespace(index);
    }

    @Override
    public QName getAttributeName(int index)
    {
        return reader.getAttributeName(index);
    }

    @Override
    public String getAttributeLocalName(int index)
    {
        return reader.getAttributeLocalName(index);
    }

    @Override
    public int getAttributeCount()
    {
        return reader.getAttributeCount();
    }

    @Override
    public void close() throws XMLStreamException
    {
        reader.close();
    }
}
