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

import java.util.ArrayList;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * XMLWriter с поддержкой отступов
 * @author gocha
 */
public class OutlineXMLWriter extends LevelXMLWriter
{

    private ArrayList<Boolean> deepChild = new ArrayList<Boolean>();
    private String newline = null;
    private String spacer = "    ";
    
    //<editor-fold defaultstate="collapsed" desc="writeOutline : boolean = true">
    private boolean writeOutline = true;
    
    /**
     * Указывает писать (по умолчанию true) отступы
     * @return true - писать отступы
     */
    public boolean isWriteOutline() {
        return writeOutline;
    }
    
    /**
     * Указывает писать (по умолчанию true) отступы
     * @param writeOutline true - писать отступы
     */
    public void setWriteOutline(boolean writeOutline) {
        this.writeOutline = writeOutline;
    }
    //</editor-fold>

    /**
     * Конструктор
     * @param writer через какой интерфейс XML писать данные
     */
    public OutlineXMLWriter(XMLStreamWriter writer)
    {
        super(writer);
    }

    /**
     * Указывает символы перевода строк
     * @return Перевод на новую строку
     */
    public String getNewline()
    {
        if( newline==null ){
            newline = System.getProperty("line.separator","\n");
        }
        return newline;
    }

    /**
     * Указывает символы перевода строк
     * @param newline Перевод на новую строку
     */
    public void setNewline(String newline)
    {
        if (newline == null)
        {
            throw new IllegalArgumentException("newline==null");
        }
        this.newline = newline;
    }

    /**
     * Указывает отступ
     * @return Отступ
     */
    public String getSpacer()
    {
        if( spacer==null )spacer = "\t";
        return spacer;
    }

    /**
     * Указывает отступ
     * @param spacer Отступ
     */
    public void setSpacer(String spacer)
    {
        if (spacer == null)
        {
            throw new IllegalArgumentException("spacer==null");
        }
        this.spacer = spacer;
    }

    /**
     * Записывает в поток перевод строки
     * @param level уровень вложенности
     * @param newline true - записать в начале перевод строки
     * @throws XMLStreamException Ошибка записи XML
     */
    protected void writeOutline(int level, boolean newline) throws XMLStreamException
    {
        if (newline)
        {
            getWriter().writeCharacters(this.getNewline());
        }
        if (level > 0)
        {
            for (int i = 0; i < level; i++)
            {
                getWriter().writeCharacters(getSpacer());
            }
        }
    }

    /**
     * Указывает содержит ли на текущей вложенности дочерние узлы
     * @param level уровень вложенности
     * @param has true - есть дочерние узлы
     */
    protected void setLevelHasChildren(int level, boolean has)
    {
        if (level < 0)
        {
            return;
        }
        if (level >= deepChild.size())
        {
            int needInsert = level - deepChild.size() + 1;
            for (int i = 0; i < needInsert; i++)
            {
                deepChild.add(false);
            }
        }

        deepChild.set(level, has);
    }

    /**
     * Указывает содержит ли на текущей вложенности дочерние узлы
     * @param level уровень вложенности
     * @return true - есть дочерние узлы
     */
    protected boolean isLevelHasChildren(int level)
    {
        if (level < 0 || level >= deepChild.size())
        {
            return false;
        }

        return deepChild.get(level);
    }

    @Override
    protected void beginElement() throws XMLStreamException
    {
        int _level = getLevel();
        if(writeOutline) writeOutline(_level, _level > 0);

        setLevelHasChildren(_level, true);
        incrementLevel();
        setLevelHasChildren(_level+1, false);
        
        //String txt = "";
    }

    @Override
    protected void endElement() throws XMLStreamException
    {
        int _level = getLevel();
        if (isLevelHasChildren(_level))
        {
            if(writeOutline) writeOutline(_level - 1, true);
        }

        decrementLevel();
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException
    {
        super.writeStartDocument(encoding, version);
        getWriter().writeCharacters(this.newline);
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException
    {
        super.writeStartDocument(version);
        getWriter().writeCharacters(this.newline);
    }
    
    @Override
    public void writeStartDocument() throws XMLStreamException
    {
        super.writeStartDocument();
        getWriter().writeCharacters(this.newline);
    }
}
