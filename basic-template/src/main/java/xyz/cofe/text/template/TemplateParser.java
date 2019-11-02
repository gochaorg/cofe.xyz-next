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
package xyz.cofe.text.template;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.collection.Pointer;
import xyz.cofe.ecolls.*;
import xyz.cofe.text.lex.Token;
import xyz.cofe.text.template.ast.*;

/**
 * Парсер шаблонов. <br>
 * Синтаксис:
 * <pre>
 * <b>Правила</b>
 * start ::= startAnyChar
 *         | startCodeBegin
 *         | startEscape
 *         .
 *
 * startAnyChar ::= startText start
 *                | startText
 *                .
 *
 * startText ::= anyChar | blockBegin | blockEnd .
 *
 * startCodeBegin ::= code start
 *                  | code
 *                  .
 *
 * startEscape ::= escape start
 *               | escape
 *               .
 *
 * code ::= codeBegin blockBody blockEnd .
 *
 * blockBody ::= anyChar blockBody
 *             | anyChar
 *             | escape blockBody
 *             | escape
 *             | block blockBody
 *             | block
 *             .
 *
 * block ::= blockBegin blockBody blockEnd .
 *
 * <b>Лексемы</b>
 * <i>Лексемы перечисле в порядке уменьшения приоритета</i>
 *
 * escape ::= <font style='background-color:#bbbbbb; color:#000000'>\$</font>  <span style="color: #666666"># интерпретирует как $</span>
 *          | <font style='background-color:#bbbbbb; color:#000000'>\\</font>  <span style="color: #666666"># интерпретирует как \</span>
 *          | <font style='background-color:#bbbbbb; color:#000000'>\${</font> <span style="color: #666666"># интерпретирует как ${</span>
 *          .
 *
 * codeBegin  ::= <font style='background-color:#bbbbbb; color:#000000'>${</font> .
 * blockBegin ::= <font style='background-color:#bbbbbb; color:#000000'>{</font> .
 * blockEnd   ::= <font style='background-color:#bbbbbb; color:#000000'>}</font> .
 *
 * anyChar <span style="color: #666666"># любой символ кроме выше указанных лексем</span>
 * </pre>
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TemplateParser {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(TemplateParser.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(TemplateParser.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(TemplateParser.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(TemplateParser.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(TemplateParser.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(TemplateParser.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(TemplateParser.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public TemplateParser(){
        //codeBeginLexems = new String[]{ "${" };
        //escapeLexems = new String[]{ "\\${", "\\$", "\\\\" };
        //blockBeginLexems = new String[]{ "{" };
        //blockEndLexems = new String[]{ "}" };
    }

    //<editor-fold defaultstate="collapsed" desc="escapeRewriteMap">
    protected LinkedHashMap<String,String> escapeRewriteMap;
    protected synchronized LinkedHashMap<String,String> getEscapeRewriteMap(){
        if( escapeRewriteMap != null ) return escapeRewriteMap;
        escapeRewriteMap = createEscapeRewriteMap();
        return escapeRewriteMap;
    }

    protected synchronized LinkedHashMap<String,String> createEscapeRewriteMap() {
        LinkedHashMap<String,String> escapeRewriteMap = new LinkedHashMap<String, String>();
        escapeRewriteMap.put("\\${", "${");
        escapeRewriteMap.put("\\$", "$");
        escapeRewriteMap.put("\\\\", "\\");
        return escapeRewriteMap;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="lexems">
    //<editor-fold defaultstate="collapsed" desc="codeBeginLexems">
    protected String[] codeBeginLexems;
    protected String[] getCodeBeginLexems(){
        if( codeBeginLexems!=null )return codeBeginLexems;
        codeBeginLexems = new String[]{ "${" };
        return codeBeginLexems;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="escapeLexems">
    protected String[] escapeLexems;
    protected String[] getEscapeLexems(){
        if( escapeLexems!=null )return escapeLexems;
        escapeLexems = new String[]{ "\\${", "\\$", "\\\\" };
        return escapeLexems;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="blockBeginLexems">
    protected String[] blockBeginLexems;
    protected String[] getBlockBeginLexems(){
        if( blockBeginLexems!=null )return blockBeginLexems;
        blockBeginLexems = new String[]{ "{" };
        return blockBeginLexems;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="blockEndLexems">
    protected String[] blockEndLexems;
    protected String[] getBlockEndLexems(){
        if( blockEndLexems!=null )return blockEndLexems;
        blockEndLexems = new String[]{ "}" };
        return blockEndLexems;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="eval()">
    /**
     * Обход шаблона (AST дерева) и форматирование сообщения
     * @param <ResultType> Результирующий тип данных
     * @param <EvalCode> Тип данных в результате вычисления кода в внутри шаблона
     * @param <EvalText> Тип данных в результате вычисления текста в внутри шаблона
     * @param tree AST дерево шаблона (результат парсинга шаблона)
     * @param evalText функция вычисления текста
     * @param evalCode функция вычисления кода (вставок в шаблон <b>${}</b>)
     * @param initResult функция начального результирующего значения
     * @param appendText функция сумирования результирующего значения и текста
     * @param appendCode функция сумирования результирующего значения и кода
     * @return Результирующее значение
     */
    public <ResultType,
        EvalCode,
        EvalText
        >
    Fn0<ResultType>
    eval( AstNode tree,
          final Fn1<String,EvalText> evalText,
          final Fn1<String,EvalCode> evalCode,
          final Fn0<ResultType> initResult,
          final Fn2<ResultType, EvalText, ResultType> appendText,
          final Fn2<ResultType, EvalCode, ResultType> appendCode
    )
    {
        final List<Fn0> funs = new ArrayList<>();
        final Map<Fn0,Boolean> funAsCode = new LinkedHashMap<>();

        EvalVisitor ev = new EvalVisitor();
        ev.appendCode = appendCode;
        ev.appendText = appendText;
        ev.initResult = initResult;
        ev.evalCode = evalCode;
        ev.evalText = evalText;
        ev.funAsCode = funAsCode;
        ev.funs = funs;
        ev.codeContext = false;
        ev.sbCode = new StringBuilder();
        EvalVisitor.visit(tree, ev);

        return ()->{
            ResultType res = initResult.apply();
            for( Fn0 f : funs ){
                if( funAsCode.get(f) ){
                    res = appendCode.apply(res, (EvalCode)f.apply());
                }else{
                    res = appendText.apply(res, (EvalText)f.apply());
                }
            }
            return res;
        };
    }

    /**
     * Обход шаблона (AST дерева) и форматирование сообщения
     * @param tree AST дерево шаблона (результат парсинга шаблона)
     * @param evalCode функция вычисления кода (вставок в шаблон <b>${}</b>)
     * @return Результирующее значение
     */
    public Fn0<String> evalAndPrint( AstNode tree, final Fn1<String,String> evalCode ){
        if( tree==null )throw new IllegalArgumentException( "tree==null" );
        if( evalCode==null )throw new IllegalArgumentException( "evalCode==null" );

        return eval(
            tree,
            arg->arg,
            evalCode,
            ()->"",
            ( src, append )->src + append,
            ( src, append )->src + append
        );
    }

    /**
     * Обход шаблона (AST дерева) и форматирование сообщения
     * @param tree AST дерево шаблона (результат парсинга шаблона)
     * @param evalText функция вычисления текста
     * @param evalCode функция вычисления кода (вставок в шаблон <b>${}</b>)
     * @return Результирующее значение
     */
    public Fn0<String> evalAndPrint( AstNode tree, final Fn1<String,String> evalText, final Fn1<String,String> evalCode ){
        if( tree==null )throw new IllegalArgumentException( "tree==null" );
        if( evalCode==null )throw new IllegalArgumentException( "evalCode==null" );
        if( evalText==null )throw new IllegalArgumentException( "evalText==null" );

        return eval(
            tree,
            evalText,
            evalCode,
            ()->"",
            ( src, append )->src + append,
            ( src, append )->src + append
        );
    }
    //</editor-fold>

    protected LinkedHashMap<String,String> currentEscapeRewriteMap;

    //<editor-fold defaultstate="collapsed" desc="parse()">
    /**
     * Парсинг шаблона и формирование соответ AST дерева
     * @param source шаблон
     * @return AST дерево
     */
    public synchronized AstNode parse( String source ){
        if( source==null )throw new IllegalArgumentException( "source==null" );

        currentEscapeRewriteMap = new LinkedHashMap<>( getEscapeRewriteMap() );

        TemplateLexer lexer = new TemplateLexer(
            getCodeBeginLexems(),
            getEscapeLexems(),
            getBlockBeginLexems(),
            getBlockEndLexems()
        );

        List<Token> tokens = lexer.parse(source);
        Pointer<Token> ptr = new Pointer<Token>(tokens);

        AstNode v = start(ptr);
        return v;
    }
    //</editor-fold>

    protected class Mapping<K,V> extends LinkedHashMap<K, V>{
        public Mapping<K,V> map(K k, V v){
            put(k, v);
            return this;
        }
    }

    protected <K,V> Mapping<K,V> map(K k, V v){
        Mapping<K,V> m = new Mapping<>();
        m.put(k, v);
        return m;
    }

    //<editor-fold defaultstate="collapsed" desc="parse ast tree nodes">
    private static boolean in( String id, String ... arr ){
        return xyz.cofe.text.Text.in(id, arr);
    }

    //<editor-fold defaultstate="collapsed" desc="start( ptr ) : AstNode">
    protected final Fn1<Pointer<Token>,AstNode> start_code = new Fn1<>() {
        @Override
        public AstNode apply(Pointer<Token> ptr) {
            AstNode c = code( ptr );
            if( c==null )return null;

            AstNode n = start(ptr);
            if( n==null )return c;

            return new Sequence(c, n);
        }
    };
    protected final Fn1<Pointer<Token>,AstNode> start_any = new Fn1<>() {
        @Override
        public AstNode apply(Pointer<Token> ptr) {
            Token t = ptr.lookup(0);

            xyz.cofe.text.template.ast.Text text = new xyz.cofe.text.template.ast.Text(t);
            ptr.move(1);

            AstNode v = start(ptr);
            if( v==null )return text;

            return new Sequence(text, v);
        }
    };
    protected final Fn1<Pointer<Token>,AstNode> start_escape = new Fn1<>() {
        @Override
        public AstNode apply(Pointer<Token> ptr) {
            Token t = ptr.lookup(0);

            Escape esc = new Escape(t,currentEscapeRewriteMap);
            ptr.move(1);

            AstNode v = start(ptr);
            if( v==null )return esc;

            return new Sequence(esc, v);
        }
    };
    protected final Map<String,Fn1<Pointer<Token>,AstNode>> start_patterns =
        map("codeBegin", start_code)
            .map("anyChar", start_any)
            .map("blockBegin", start_any)
            .map("blockEnd", start_any)
            .map("escape", start_escape)
        ;

    protected synchronized AstNode start( Pointer<Token> ptr ){
        Token t = ptr.lookup(0);
        if( t==null )return null;

        String id = t.getId();
        for( var me : start_patterns.entrySet() ){
            String mid = me.getKey();
            Fn1<Pointer<Token>,AstNode> fn = me.getValue();
            if( mid.equals(id) ){
                return fn.apply(ptr);
            }
        }

        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="code( ptr ) : AstNode">
    private synchronized AstNode code( Pointer<Token> ptr ){
        Token t = ptr.lookup(0);
        if( t==null )return null;

        String id = t.getId();
        if( !id.equals("codeBegin") )return null;

        ptr.push();
        ptr.move(1);

        BlockBody blockBody = blockBody(ptr);
        if( blockBody==null ){
            ptr.restore();
            return null;
        }

        Token tEnd = ptr.lookup(0);
        if( tEnd==null ){
            ptr.restore();
            return null;
        }

        if( !tEnd.getId().equals("blockEnd") ){
            ptr.restore();
            return null;
        }
        ptr.pop();
        ptr.move(1);

        return new Code(t,blockBody,tEnd);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="blockBody( ptr ) : BlockBody">
    private synchronized BlockBody blockBody( Pointer<Token> ptr ){
        Token t = ptr.lookup(0);
        if( t==null )return null;

        String id = t.getId();
        if( in( id, "anyChar" ) ){
            xyz.cofe.text.template.ast.Text txt = new xyz.cofe.text.template.ast.Text(t);
            ptr.move(1);

            AstNode v = blockBody(ptr);
            if( v==null )return new BlockBody( txt );

            return new BlockBody( new Sequence(txt, v) );
        }

        if( in( id, "escape" ) ){
            Escape esc = new Escape(t,currentEscapeRewriteMap);
            ptr.move(1);

            AstNode v = blockBody(ptr);
            if( v==null )return new BlockBody( esc );

            return new BlockBody( new Sequence(esc, v) );
        }

        if( in( id, "blockBegin" ) ){
            AstNode v = block(ptr);
            if( v==null )return null;

            BlockBody bb = blockBody(ptr);
            if( bb==null )return new BlockBody( v );

            return new BlockBody( new Sequence(v, bb) );
        }

        return null;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="block( ptr ) : Block">
    private synchronized Block block( Pointer<Token> ptr ){
        Token t0 = ptr.lookup(0);
        if( t0==null )return null;

        String id0 = t0.getId();
        if( !in( id0, "blockBegin" ) )return null;
        ptr.push();
        ptr.move(1);

        AstNode body = blockBody(ptr);
        if( body==null ){
            ptr.restore();
            return null;
        }

        Token t1 = ptr.lookup(0);
        String id1 = t1.getId();
        if( !in(id1,"blockEnd") ){
            ptr.restore();
            return null;
        }

        ptr.pop();
        ptr.move(1);

        return new Block(t0, body, t1);
    }
    //</editor-fold>
    //</editor-fold>
}
