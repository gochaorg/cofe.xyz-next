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

import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import xyz.cofe.text.Text;

/**
 * Путь XML элементов, от корня до элемента
 * @author gocha
 */
public class XEventPath extends Stack<XMLEvent>
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(XEventPath.class.getName());
    private static final Level logLevel = logger.getLevel();
    
    private static final boolean isLogSevere = 
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.SEVERE.intValue();
    
    private static final boolean isLogWarning = 
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.WARNING.intValue();
    
    private static final boolean isLogInfo = 
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.INFO.intValue();
    
    private static final boolean isLogFine = 
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.FINE.intValue();
    
    private static final boolean isLogFiner = 
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.FINER.intValue();
    
    private static final boolean isLogFinest = 
        logLevel==null 
        ? true
        : logLevel.intValue() <= Level.FINEST.intValue();

    private static void logFine(String message,Object ... args){
        logger.log(Level.FINE, message, args);
    }
    
    private static void logFiner(String message,Object ... args){
        logger.log(Level.FINER, message, args);
    }
    
    private static void logFinest(String message,Object ... args){
        logger.log(Level.FINEST, message, args);
    }
    
    private static void logInfo(String message,Object ... args){
        logger.log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        logger.log(Level.WARNING, message, args);
    }
    
    private static void logSevere(String message,Object ... args){
        logger.log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        logger.log(Level.SEVERE, null, ex);
    }

    private static void logEntering(String method,Object ... params){
        logger.entering(XEventPath.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(XEventPath.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(XEventPath.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Возвращает имя тэга (getLocalPart():String)
     * @return имя тэга
     */
    public String getName(){
        StartElement e = getLastElement();
        if( e==null )return null;
        return e.getName().getLocalPart();
    }
    
    /**
     * Проверяет наличие атрибута
     * @param attrName имя атрибута
     * @return true - атрибут присуствует
     */
    public boolean hasAttribute(String attrName){
        return getAttributeValue(attrName)!=null;
    }

    /**
     * Проверяет наличие атрибута
     * @param attrName имя атрибута
     * @param ignoreCase игнориовать регистр
     * @return true - атрибут присуствует
     */
    public boolean hasAttribute(String attrName,boolean ignoreCase){
        return getAttributeValue(attrName,ignoreCase)!=null;
    }
    
//    public boolean hasAttribute(String name){
//        StartElement se = getLastElement();
//        return se.
//    }
    
    /**
     * Создает клон объекта
     * @return клоннированый объект
     */
    @Override
    public XEventPath clone(){
        XEventPath res = new XEventPath();
        res.addAll(this);
        return res;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<size();i++ ){
            XMLEvent e = get(i);
            if( e instanceof StartElement ){
                sb.append("/");
                StartElement se = (StartElement)e;
                
                sb.append( se.getName().getLocalPart() );
                
                int j=-1;
                Iterator iAttrs = se.getAttributes();
                while(iAttrs.hasNext()){
                    Object oa = iAttrs.next();
                    if( oa==null )continue;
                    if( !(oa instanceof Attribute) )continue;
                    Attribute attr = (Attribute)oa;
                    j++;
                    if( j==0 ){
                        sb.append("[");
                    }
                    sb.append(attr.getName());
                    sb.append("=");
                    sb.append(Text.encodeStringConstant(attr.getValue()));
                }
                if( j>=0 )sb.append("]");
            }else if( e instanceof StartDocument ){
                sb.append("#DOC");
            }
        }
        return sb.toString();
    }
    
    /**
     * Возвращает последний XML элемент (StartElement)
     * @return XML элемент
     */
    public StartElement getLastElement(){
        if( size()<1 )return null;
        for( int i=size()-1; i>=0; i-- ){
            Object o = get(i);
            if( o instanceof StartElement ){
                return (StartElement)o;
            }
        }
        return null;
    }

    /**
     * Возвращает значение атрибута последнего элемента в списке
     * @param name Имя атрибута
     * @return Значение или null
     */
    public String getAttributeValue(String name){
        return getAttributeValue(name, false);
    }
    
    /**
     * Возвращает значение атрибута как строку
     * @param attributeName Имя атрибута
     * @param defaultValue значение по умолчанию
     * @return Значение
     */
    public String readAttributeAsString( String attributeName, String defaultValue ){
        String str = getAttributeValue( attributeName );
        return str==null ? defaultValue : str;
    }
    
    /**
     * Возвращает значение атрибута как булево
     * @param attributeName Имя атрибута
     * @param defaultValue значение по умолчанию
     * @return Значение
     */
    public Boolean readAttributeAsBoolean( String attributeName, Boolean defaultValue ){
        String str = getAttributeValue( attributeName );
        if( str==null )return defaultValue;
        if( Text.in(str.toLowerCase().trim(), "true", "1", "on", "yes" ) ) return true;
        if( Text.in(str.toLowerCase().trim(), "false", "0", "off", "no" ) ) return false;
        logWarning("unparsed xml attribute {0} = {1} as {3}", attributeName, str, "boolean");
        return defaultValue;
    }
    
    /**
     * Возвращает значение атрибута как число
     * @param attributeName Имя атрибута
     * @param defaultValue значение по умолчанию
     * @return Значение
     */
    public Integer readAttributeAsInteger( String attributeName, Integer defaultValue ){
        String str = getAttributeValue( attributeName );
        if( str==null )return defaultValue;
        try{
            Integer v = Integer.parseInt(str);
            return v;
        }catch( NumberFormatException ex ){
            logWarning("unparsed xml attribute {0} = {1} as {3} - error {4}", attributeName, str, "Integer", ex.getLocalizedMessage());
        }
        return defaultValue;
    }
    
    /**
     * Возвращает значение атрибута как число
     * @param attributeName Имя атрибута
     * @param defaultValue значение по умолчанию
     * @return Значение
     */
    public Long readAttributeAsLong( String attributeName, Long defaultValue ){
        String str = getAttributeValue( attributeName );
        if( str==null )return defaultValue;
        try{
            Long v = Long.parseLong(str);
            return v;
        }catch( NumberFormatException ex ){
            logWarning("unparsed xml attribute {0} = {1} as {3} - error {4}", attributeName, str, "Long", ex.getLocalizedMessage());
        }
        return defaultValue;
    }
    
    /**
     * Возвращает значение атрибута как число
     * @param attributeName Имя атрибута
     * @param defaultValue значение по умолчанию
     * @return Значение
     */
    public Double readAttributeAsDouble( String attributeName, Double defaultValue ){
        String str = getAttributeValue( attributeName );
        if( str==null )return defaultValue;
        try{
            Double v = Double.parseDouble(str);
            return v;
        }catch( NumberFormatException ex ){
            logWarning("unparsed xml attribute {0} = {1} as {3} - error {4}", attributeName, str, "Double", ex.getLocalizedMessage());
        }
        return defaultValue;
    }
    
    /**
     * Возвращает значение атрибута последнего элемента в списке
     * @param name Имя атрибута
     * @param ignoreCase Игнорирование рег. в имени
     * @return Значение или null
     */
    public String getAttributeValue(String name,boolean ignoreCase){
        if (name== null) {            
            throw new IllegalArgumentException("name==null");
        }
        StartElement e = getLastElement();
        if( e==null )return  null;
        Iterator itrAttr = e.getAttributes();
        while( itrAttr.hasNext() ){
            Object o = itrAttr.next();
            if( o instanceof Attribute ){
                Attribute a = (Attribute)o;
                String n = a.getName().getLocalPart();
                if( ignoreCase ){
                    if( n.equalsIgnoreCase(name) ){
                        return a.getValue();
                    }
                }else{
                    if( n.equals(name) ){
                        return a.getValue();
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Возвращает новый путь, без последнего XML элемента
     * @return XML путь
     */
    public XEventPath dropLastElement(){
        if( size()<1 )return new XEventPath();
        
        int lastEl = -1;
        for( int i=size()-1; i>=0; i-- ){
            Object o = get(i);
            if( o instanceof StartElement ){
                lastEl = i;
                break;
            }
        }
        if( lastEl<=0 )return new XEventPath();
        
        XEventPath path = new XEventPath();
        for( int i=0; i<lastEl; i++ ){
            path.add(get(i));
        }
        
        return path;
    }
}
