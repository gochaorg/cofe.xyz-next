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

import java.util.List;
import java.util.function.Consumer;

import xyz.cofe.collection.Pointer;
import xyz.cofe.text.Text;
import xyz.cofe.text.lex.*;
import static xyz.cofe.xml.stream.path.Expressions.*;


/**
 * Парсер для проверки XEventPath на соот выражению
 * <br><br>
 * 
 * Синтаксис
 * <pre>
 * path        ::= root | [root] tagptrn {pathOp tagptrn}
 * tagptrn     ::= anyTagPtrn | tag
 * anyTagPtrn  ::= '*'
 * root        ::= '/'
 * pathOp      ::= '/'
 * tag         ::= qname
 * qname       ::= localName [ ':' localName]
 * qprefix     ::= ( Буква | Цифра | '-' | '_' )
 * </pre>
 * @author gocha
 */
public class PathParser
{
    public PathExpression parse(String text){
        if (text== null) {            
            throw new IllegalArgumentException("text==null");
        }
        List<Token> toks = lexParse(text);
        Pointer<Token> ptr = new Pointer<Token>(toks);
        return parsePath(ptr);
    }
    
    protected PathExpression tagPattern(Pointer<Token> ptr){
        Token t = ptr.lookup(0);
        if( t==null )return null;
        
        if( t instanceof Keyword ){
            Keyword k = (Keyword)t;
            if( k.getKeyword().equals("*") ){
                ptr.move(1);
                return tagNameMatches(Text.wildcard("*", false, true).pattern());
            }
        }
        
        if( t instanceof TagName ){
            TagName tn = (TagName)t;
            ptr.move(1);
            return tagNameEquals(tn.getLocalName());
        }
        
        return null;
    }
    
    protected PathExpression parsePath(Pointer<Token> ptr){
        PathExpression e = null;
        boolean rootExp = false;

        Token t = null;
        t = ptr.lookup(0);
        if( t==null )return null;
        
        boolean rootPath = false;
        if( t instanceof Keyword ){
            Keyword k = (Keyword)t;
            if( k.getKeyword().equals("/") ){
                e = rootPath();
                ptr.move(1);
                rootExp = true;
            }else{
                return null;
            }
        }
        
        //t = ptr.lookup(0);
        /*
        if( !(t instanceof TagName) )return e;
        TagName tn = (TagName)t;
        ptr.move(1);
        if( e==null )
            e = tagNameEquals(tn.getLocalName());
                else
            e = andPath( parentPath(e), tagNameEquals(tn.getLocalName()) );
        */
        
        PathExpression eTag = tagPattern(ptr);
        if( eTag==null )return e;
        e = e!=null ? andPath( parentPath(e), eTag ) : eTag;
        
        while( true ){
            t = ptr.lookup(0);
            if( t instanceof Keyword ){
                Keyword k = (Keyword)t;
                if( k.getKeyword().equals("/") ){
                    ptr.move(1);
                    
                    eTag = tagPattern(ptr);
                    if( eTag==null )break;
                    
                    e = andPath(
                        parentPath(e),
                        eTag
                    );
                }else{
                    break;
                }
            }else{
                break;
            }
        }
        
        return e;
    }
    
    protected ListLexer createLexer(){
        ListLexer ll = new ListLexer();
        
        KeywordsParser kw = new KeywordsParser("op");
        kw.setKeywords( new Keywords(false).put("/").put("*") );
        ll.getParsers().add(kw);
        
//        KeywordsParser specialTagNames = new KeywordsParser("tagMatch");
//        specialTagNames.setKeywords(
//            new Keywords(false).put("*")
//        );
//        ll.getParsers().add(specialTagNames);
        
        TagNameParser tn = new TagNameParser("tag");
        ll.getParsers().add(tn);
        
        WhiteSpaceParser ws = new WhiteSpaceParser("ws");
        ll.getParsers().add(ws);
        
        return ll;
    }
    
    protected List<Token> lexParse(String text){
        ListLexer ll = createLexer();
        List<Token> toks = ll.parse(text, (String obj) -> {
                System.out.println("lexer error "+obj);
            });
        toks = LexerUtil.filter(toks, WhiteSpace.class);
        return toks;
    }
}
