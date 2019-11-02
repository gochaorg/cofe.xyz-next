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

import javax.xml.stream.events.XMLEvent;
import xyz.cofe.text.Text;

import java.util.function.Predicate;

/**
 * Содержит функции для построения шаблона PathExpression
 * @author gocha
 */
public class Expressions
{
    //<editor-fold defaultstate="collapsed" desc="rootPath">
    private static PathExpression rootPath = value->{
        if( value==null )return false;
        for( XMLEvent e : value ){
            if( !(e.isStartDocument()) )return false;
        }
        return true;
    };
    
    /**
     * Шаблон проверяющий что в текущий момент XEventPath находиться в начале документа
     * @return шаблон
     */
    public static PathExpression rootPath(){
        return rootPath;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="andPath">
    /**
     * Объединение шаблонов в последовательность AND выражений шаблонов
     * @param expressions шаблоны
     * @return шаблон - логическая функция AND
     */
    public static PathExpression andPath(PathExpression ... expressions){
        if (expressions== null) {
            throw new IllegalArgumentException("expressions==null");
        }
        final PathExpression[] exp = expressions;
        return value->{
            for( PathExpression e : exp ){
                if( e==null )continue;
                if( !e.test(value) )return false;
            }
            return true;
        };
    }
    //</editor-fold>

    /**
     * Объединение шаблонов в последовательность OR выражений шаблонов
     * @param expressions шаблоны
     * @return шаблон - логическая функция OR
     */
    public static PathExpression orPath(PathExpression ... expressions){
        if (expressions== null) {            
            throw new IllegalArgumentException("expressions==null");
        }
        final PathExpression[] exp = expressions;
        return value->{
            for( PathExpression e : exp ){
                if( e==null )continue;
                if( e.test(value) )return true;
            }
            return false;
        };
    }

    /**
     * Создает шаблон - логическую функцию NOT от существующего шаблона
     * @param expression исходный шаблон
     * @return шаблон функция инвертор
     */
    public static PathExpression notPath(PathExpression expression){
        if (expression== null) {            
            throw new IllegalArgumentException("expression==null");
        }
        final PathExpression exp = expression;
        return value->!exp.test(value);
    }
    
    /**
     * Создает шаблон-функцию которая проверяет выше стояший путь (родительский узел)
     * @param expression исходная функция
     * @return функция для проверки родительского узла
     */
    public static PathExpression parentPath(PathExpression expression){
        if (expression== null) {            
            throw new IllegalArgumentException("expression==null");
        }
        return new ParentMatcher(expression);
    }

    /**
     * Проверяет что текущий узел (последний элемент XEventPath) содержит атрибут
     * @param name имя атрибута
     * @param ignoreCase игнорировать регистр
     * @return шаблон-функция проверки
     */
    public static PathExpression hasAttribute(String name,boolean ignoreCase){
        if (name== null) {            
            throw new IllegalArgumentException("name==null");
        }
        return new TagMatcher(TagMatcher.hasAttribute(name,ignoreCase));
    }

    /**
     * Проверяет что текущий узел (последний элемент XEventPath) содержит атрибут
     * @param name имя атрибута
     * @return шаблон-функция проверки
     */
    public static PathExpression hasAttribute(String name){
        if (name== null) {            
            throw new IllegalArgumentException("name==null");
        }
        return new TagMatcher(TagMatcher.hasAttribute(name,false));
    }
    
    /**
     * Создает шаблон-функцию что текущий узел (последний элемент XEventPath) содержит хотябы один атрибут удовлетворяющий критерию
     * @param namePredicate фильтр проверяемых атрибутов
     * @param valuePredicate фильтр проверяемых значений атрибута
     * @return шаблон - функция
     */
    public static PathExpression attr( Predicate<String> namePredicate, Predicate<String> valuePredicate){
        if( namePredicate==null ) throw new IllegalArgumentException( "namePredicate==null" );
        if( valuePredicate==null ) throw new IllegalArgumentException( "valuePredicate==null" );
        return new TagMatcher(TagMatcher.attr(namePredicate, valuePredicate));
    }
    
    /**
     * Создает шаблон-функцию что текущий узел (последний элемент XEventPath) содержит атрибут с числовым значением
     * @param attrName имя атрибута
     * @return шаблон - функция
     */
    public static PathExpression attrIsNumeric(String attrName){
        return attr( Text.Predicates.equals(attrName), Text.Predicates.isNumeric() );
    }
    
    /**
     * Создает шаблон-функцию что текущий узел (последний элемент XEventPath) является указанным элементом (имя тэга)
     * @param name имя тэга
     * @return шаблон - функция
     */
    public static PathExpression tagNameEquals(String name){
        if (name== null) {            
            throw new IllegalArgumentException("name==null");
        }
        return new TagMatcher(TagMatcher.nameEquals(name));
    }

    /**
     * Создает шаблон-функцию что текущий узел (последний элемент XEventPath) является указанным элементом (имя тэга)
     * @param name имя тэга
     * @return шаблон - функция
     */
    public static PathExpression tagNameEqualsIgnoreCase(String name){
        if (name== null) {            
            throw new IllegalArgumentException("name==null");
        }
        return new TagMatcher(TagMatcher.nameEqualsIgnoreCase(name));
    }
    
    /**
     * Создает шаблон-функцию что текущий узел (последний элемент XEventPath) является указанным элементом (имя тэга)
     * @param name шаблон имени тэга
     * @return шаблон - функция
     */
    public static PathExpression tagNameMatches(String name){
        if (name== null) {            
            throw new IllegalArgumentException("name==null");
        }
        return new TagMatcher(TagMatcher.nameMatches(name));
    }
}
