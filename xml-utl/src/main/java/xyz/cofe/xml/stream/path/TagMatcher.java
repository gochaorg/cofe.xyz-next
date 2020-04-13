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
import java.util.function.Predicate;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Описывает совпадения узла (тэга) согласно формальным правилам
 * @author gocha
 */
public class TagMatcher implements PathExpression
{
    public TagMatcher(){
    }
    public TagMatcher( Predicate<XMLEvent> xmlElementPred){
        setElementPredicate(xmlElementPred);
    }
    
    protected Predicate<XMLEvent> elementPredicate = null;

    /**
     * Указывает предикат проверки
     * @return предикат проверки
     */
    public Predicate<XMLEvent> getElementPredicate() {
        return elementPredicate;
    }

    /**
     * Указывает предикат проверки
     * @param elementPredicate предикат проверки
     */
    public void setElementPredicate(Predicate<XMLEvent> elementPredicate) {
        this.elementPredicate = elementPredicate;
    }
    
    @Override
    public boolean test(XEventPath path) {
        if (path== null) {            
            throw new IllegalArgumentException("path==null");
        }
        if( elementPredicate==null )return true;
        if( path.size()<1 )return false;
        
        StartElement se = path.getLastElement();
        if( se==null )return false;
        
        return elementPredicate.test(se);
    }
    
    /**
     * Создает предикат проверки наличия атрибута у тэга
     * @param name имя атрибута
     * @param ignoreCase true - при сравнении игнорировать регистр
     * @return предикат
     */
    public static Predicate<XMLEvent> hasAttribute(final String name,final boolean ignoreCase){
        return new Predicate<XMLEvent>() {
            @Override
            public boolean test(XMLEvent value) {
                if( !(value instanceof StartElement) )return false;
                StartElement se = (StartElement)value;
                
                if( name==null )return false;
                if( value==null )return false;
                Iterator itrAttr = se.getAttributes();
                while( itrAttr.hasNext() ){
                    Object o = itrAttr.next();
                    if( o instanceof Attribute ){
                        Attribute attr = (Attribute)o;
                        String aname = attr.getName().getLocalPart();
                        if( ignoreCase ){
                            if( aname.equalsIgnoreCase(name) )return true;
                        }else{
                            if( aname.equals(name) )return true;
                        }
                    }
                }
                return false;
            }
        };
    }
    
    /**
     * Создает предикат проверки значения атрибута
     * @param attrNamePredicate предикат проверяемых атрибутов
     * @param valuePredicate предикат проверяемых значений
     * @return Предикат
     */
    public static Predicate<XMLEvent> attr(Predicate<String> attrNamePredicate,Predicate<String> valuePredicate){
        final Predicate<String> valuep = valuePredicate;
        final Predicate<String> namep = attrNamePredicate;
        return new Predicate<XMLEvent>() {
            @Override
            public boolean test(XMLEvent value) {
                if( namep==null )return false;
                if( valuep==null )return false;
                if( !(value instanceof StartElement) )return false;
                StartElement se = (StartElement)value;
                
                Iterator itrAttr = se.getAttributes();
                while( itrAttr.hasNext() ){
                    Object o = itrAttr.next();
                    if( o instanceof Attribute ){
                        Attribute attr = (Attribute)o;
                        String aname = attr.getName().getLocalPart();
                        if( namep.test(aname) ){
                            String avalue = attr.getValue();
                            if( valuep.test(avalue) ){
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        };
    }
    
    /**
     * Создает предикат проверки имении тэга
     * @param name имя тэга
     * @return предикат
     */
    public static Predicate<XMLEvent> nameEquals(String name){
        final String nm = name;
        return new Predicate<XMLEvent>() {
            @Override
            public boolean test(XMLEvent value) {
                if( value==null )return false;
                if( !(value instanceof StartElement) )return false;
                StartElement se = (StartElement)value;
                return se.getName().getLocalPart().equals(nm);
            }
        };
    }

    /**
     * Создает предикат проверки имении тэга
     * @param name имя тэга
     * @return предикат
     */
    public static Predicate<XMLEvent> nameEqualsIgnoreCase(String name){
        final String nm = name;
        return new Predicate<XMLEvent>() {
            @Override
            public boolean test(XMLEvent value) {
                if( value==null )return false;
                if( !(value instanceof StartElement) )return false;
                StartElement se = (StartElement)value;
                return se.getName().getLocalPart().equalsIgnoreCase(nm);
            }
        };
    }

    /**
     * Создает предикат проверки имении тэга
     * @param name регулярное выражение
     * @return предикат
     */
    public static Predicate<XMLEvent> nameMatches(String name){
        final String nm = name;
        return new Predicate<XMLEvent>() {
            @Override
            public boolean test(XMLEvent value) {
                if( value==null )return false;
                if( !(value instanceof StartElement) )return false;
                StartElement se = (StartElement)value;
                return se.getName().getLocalPart().matches(nm);
            }
        };
    }
}
