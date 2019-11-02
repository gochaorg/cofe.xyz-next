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


import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import xyz.cofe.ecolls.Fn0;
import xyz.cofe.ecolls.Fn1;
import xyz.cofe.text.template.ast.AstNode;
import xyz.cofe.typeconv.ExtendedCastGraph;
import xyz.cofe.typeconv.TypeCastGraph;

/**
 * Базовое форматирование сообщений, формат шаблона описывается парсером
 * @see TemplateParser
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class BasicTemplate {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(BasicTemplate.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(BasicTemplate.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(BasicTemplate.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(BasicTemplate.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(BasicTemplate.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(BasicTemplate.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(BasicTemplate.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    private String source = null;
    private AstNode tree = null;
    private TemplateParser parser = null;

    /**
     * Конструктор
     * @param template шаблон сообщения
     * @see BasicTemplate
     */
    public BasicTemplate( String template ){
        if( template==null )throw new IllegalArgumentException( "template==null" );

        source = template;
        parser = new TemplateParser();
        tree = parser.parse(template);
    }

    /**
     * Конструктор
     * @param template шаблон сообщения
     * @param parser Парсинг шаблона
     * @see BasicTemplate
     */
    public BasicTemplate( String template, TemplateParser parser ){
        if( template==null )throw new IllegalArgumentException( "template==null" );
        if( parser==null )throw new IllegalArgumentException( "parser==null" );

        source = template;
        this.parser = parser;
        tree = this.parser.parse(template);
    }

    /**
     * Конструктор копирования
     * @param sample образец
     */
    public BasicTemplate( BasicTemplate sample ){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        this.tree = sample.tree;
        this.parser = sample.parser;
        this.source = sample.source;
    }

    /**
     * Исходный текст шаблона
     * @return текст шаблона
     */
    public String getSource(){
        return source;
    }

    /**
     * AST дерева шаблона
     * @return AST дерево
     */
    public AstNode getAst(){
        return tree;
    }

    /**
     * Парсер шаблона
     * @return парсер
     */
    public TemplateParser getParser(){
        return parser;
    }

    //<editor-fold defaultstate="collapsed" desc="evalAndPrint()">
    public Fn0<String> evalAndPrint( Fn1<String, String> evalCode) {
        if( evalCode==null )throw new IllegalArgumentException( "evalCode==null" );
        return parser.evalAndPrint(tree, evalCode);
    }

    public Fn0<String> evalAndPrint(Fn1<String, String> evalText, Fn1<String, String> evalCode) {
        if( evalText==null )throw new IllegalArgumentException( "evalText==null" );
        if( evalCode==null )throw new IllegalArgumentException( "evalCode==null" );
        return parser.evalAndPrint(tree, evalText, evalCode);
    }
    //</editor-fold>

    @Override
    public BasicTemplate clone(){
        return new BasicTemplate(this);
    }

    //<editor-fold defaultstate="collapsed" desc="eval()">
    public String eval( Object context ){
        final Object ctx = context;

        Context cctx = context(ctx.getClass()).build();
        cctx.context = ctx;
        Fn0<String> ev = parser.evalAndPrint(tree, cctx.evalCode);

        return ev.apply();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="class Context<T>">
    public static class Context<T>
    {
        protected T context;
        //        private BasicTemplate template;
        protected final Fn0<String> evalFun;
        protected final TypeCastGraph typeCast;

        private boolean usingScriptEngine = false;
        private String scriptLanguage = "JavaScript";

        private Map<String,Object> bindings = null;
        private PropertyAccessResolver resolver = null;

        private boolean allowToString = true;

        //<editor-fold defaultstate="collapsed" desc="constructors">
        public Context(
            TypeCastGraph typeCast,
            BasicTemplate template,
            Class<T> clazz
        ){
            if( typeCast==null )typeCast = new ExtendedCastGraph();
            this.typeCast = typeCast;

            if( template==null )throw new IllegalArgumentException( "template==null" );
//            this.template = template;

            if( clazz==null )throw new IllegalArgumentException( "clazz==null" );
            context = null;

            resolver = new PropertyAccessResolver(clazz);
            evalFun = lasyEvalFunc(template);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="bindings">
        public Map<String, Object> getBindings() {
            if( bindings!=null )return bindings;
            bindings = new LinkedHashMap<String, Object>();
            return bindings;
        }

        public void setBindings(Map<String, Object> bindings) {
            this.bindings = bindings;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="usingScriptEngine">
        public boolean isUsingScriptEngine() {
            return usingScriptEngine;
        }

        public void setUsingScriptEngine(boolean usingScriptEngine) {
            this.usingScriptEngine = usingScriptEngine;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="scriptLanguage">
        public String getScriptLanguage() {
            if( scriptLanguage==null )scriptLanguage = "JavaScript";
            return scriptLanguage;
        }

        public void setScriptLanguage(String scriptLanguage) {
            this.scriptLanguage = scriptLanguage;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="allowToString">
        public boolean isAllowToString() {
            return allowToString;
        }

        public void setAllowToString(boolean allowToString) {
            this.allowToString = allowToString;
        }
//</editor-fold>

        public String eval( T context ){
            this.context = context;
            return evalFun.apply();
        }

        //<editor-fold defaultstate="collapsed" desc="formatBuilder">
        private FormatBuilder formatBuilder = null;

        public FormatBuilder getFormatBuilder() {
            return formatBuilder;
        }

        public void setFormatBuilder(FormatBuilder formatBuilder) {
            this.formatBuilder = formatBuilder;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="fun lasyEvalFunc">
        private Fn0 lasyEvalFunc = null;
        private Fn0 lasyEvalFunc(final BasicTemplate template){
            return new Fn0() {
                @Override
                public Object apply() {
                    if( lasyEvalFunc!=null )return lasyEvalFunc.apply();

                    if( formatBuilder!=null ){
                        final Fn1 f1 = formatBuilder.build(
                            template,
                            new Fn1<T,Object>() {
                                @Override
                                public Object apply(T arg) {
                                    Context.this.context = arg;
                                    return null;
                                }
                            },
                            evalCode
                        );
                        lasyEvalFunc = new Fn0() {
                            @Override
                            public Object apply() {
                                return f1.apply(context);
                            }
                        };

                        return lasyEvalFunc.apply();
                    }

                    lasyEvalFunc =
                        template.getParser()
                            .evalAndPrint(template.getAst(), evalCode);

                    return lasyEvalFunc.apply();
                }
            };
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="fun evalCode">
        private Fn1<String,String> evalCode = new Fn1<String,String>(){
            @Override
            public String apply(String code) {
                try{
                    if( usingScriptEngine ){
                        Object val = evalCodeByScript(context, code, getScriptLanguage());
                        if( val==null )return "null";
                        if( allowToString ){
                            try{
                                String str = typeCast.cast(val, String.class);
                                return str;
                            }catch( Throwable t ){
                                return val.toString();
                            }
                        }else{
                            String str = typeCast.cast(val, String.class);
                            return str;
                        }
                    }else{
                        Object val = getVarValueByName(code);
                        if( val==null ){
                            return "null";
                        }
                        if( allowToString ){
                            try{
                                String str = typeCast.cast(val, String.class);
                                return str;
                            }catch( Throwable t ){
                                return val.toString();
                            }
                        }else{
                            String str = typeCast.cast(val, String.class);
                            return str;
                        }
                    }
                }catch(Throwable ex){
                    Logger.getLogger(BasicTemplate.class.getName()).log(Level.SEVERE, null, ex);
                    return ex.getMessage();
                }
            }
        };
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="scriptEngine">
        private ScriptEngine scriptEngine = null;

        public ScriptEngine getScriptEngine() {
            if( scriptEngine!=null )return scriptEngine;
            if( !usingScriptEngine )return null;
            scriptEngine = getScriptEngineManager().getEngineByName(getScriptLanguage());
            return scriptEngine;
        }

        public void setScriptEngine(ScriptEngine scriptEngine) {
            this.scriptEngine = scriptEngine;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="scriptEngineManager">
        private ScriptEngineManager scriptEngineManager = null;

        public ScriptEngineManager getScriptEngineManager() {
            if( scriptEngineManager!=null )return scriptEngineManager;
            if( !usingScriptEngine )return null;
            scriptEngineManager = new ScriptEngineManager();
            return scriptEngineManager;
        }

        public void setScriptEngineManager(ScriptEngineManager scriptEngineManager) {
            this.scriptEngineManager = scriptEngineManager;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="evalCodeByScript()">
        private Object evalCodeByScript(Object ctx, String code,String lang){
            ScriptEngine se = getScriptEngine();
            Bindings bnd = se.getBindings(ScriptContext.ENGINE_SCOPE);

            if( ctx instanceof Map ){
                for( Object oEn : ((Map)ctx).entrySet() ){
                    if( !(oEn instanceof Map.Entry) ){
                        continue;
                    }
                    Map.Entry en = (Map.Entry)oEn;
                    Object oKey = en.getKey();
                    if( oKey==null )continue;

                    Object oValue = en.getValue();

                    if( !(oKey instanceof String) )continue;

                    bnd.put((String)oKey, oValue);
                }
            }

            // extract fields of ctx object
            for( Map.Entry<String,FieldController> fce : resolver.getFields().entrySet() ){
                String n = fce.getKey();
                FieldController fc = fce.getValue();
                fc.setOwner(ctx);
                try {
                    Object v = fc.getValue();
                    bnd.put(n, v);
                } catch (Throwable ex) {
                    Logger.getLogger(BasicTemplate.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            // extract properties of ctx object
            for( Map.Entry<String,PropertyDescriptor> pce : resolver.getProperties().entrySet() ){
                String n = pce.getKey();
                PropertyDescriptor pd = pce.getValue();
                Method readMethod = pd.getReadMethod();
                if( readMethod!=null ){
                    Object v;
                    try {
                        v = readMethod.invoke(ctx);
                        bnd.put(n, v);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(BasicTemplate.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(BasicTemplate.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(BasicTemplate.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

//                pd.setOwner(ctx);
//                try {
//                    Object v = pd.getValue();
//                    bnd.put(n, v);
//                } catch (Throwable ex) {
//                    Logger.getLogger(BasicTemplate.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }

            for( Map.Entry<String,Object> e : getBindings().entrySet() ){
                bnd.put(e.getKey(), e.getValue());
            }

            try {
                Object v = se.eval(code);
                return v;
            } catch (ScriptException ex) {
                Logger.getLogger(BasicTemplate.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="getVarValueByName()">
        private Object getVarValueByName(String name){
            if( name==null )return null;
            String nm = name.trim();

            if( nm.contains(".") ){
                String[] nidx = name.split("\\s*\\.\\s*");
                return resolver.resolve(context, nidx);
            }else{
                return resolver.resolve(context, name);
            }
        }
//</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="class ContextBuilder<T>">
    public class ContextBuilder<T> {
        private Class<T> ctx;
        private TypeCastGraph typecast = null;
        private boolean useScript = false;
        private String scriptLang = "JavaScript";
        private Map<String,Object> bindings = new LinkedHashMap<String, Object>();
        private FormatBuilder formatBuilder = null;

        public ContextBuilder(Class<T> context){
            this.ctx = context;
        }

        public ContextBuilder<T> bind(String name, Object ref){
            if( name==null )throw new IllegalArgumentException( "name==null" );
            bindings.put(name, ref);
            return this;
        }

        public ContextBuilder<T> useJavaScript(){
            useScript = true;
            scriptLang = "JavaScript";
            return this;
        }

        public ContextBuilder<T> align(){
            formatBuilder = new AlignFormatBuilder();
            return this;
        }

        public ContextBuilder<T> typeCast( TypeCastGraph typeCast ){
            this.typecast = typeCast;
            return this;
        }

        public Context<T> build(){
            Context<T> co = new Context<T>(typecast, BasicTemplate.this, ctx);
            co.setUsingScriptEngine(useScript);
            co.setScriptLanguage(scriptLang);
            co.getBindings().putAll(bindings);
            co.setFormatBuilder(formatBuilder);
            return co;
        }
    }
//</editor-fold>

    public <T> ContextBuilder<T> context(Class<T> ctx){
        return new ContextBuilder<T>(ctx);
    }

    public static class EasyTemplate extends BasicTemplate {
        private LinkedHashMap<String,Object> contextVars = new LinkedHashMap<String, Object>();
        private boolean align = false;
        private boolean javascript = false;
        private Writer writer = null;
        private boolean flushWriter = false;
        private String endln = null;

        public EasyTemplate(String template) {
            super(template);
            endln = xyz.cofe.text.EndLine.Default.get();
        }

        public EasyTemplate bind( String varName, Object varValue ){
            if( varName!=null ){
                contextVars.put(varName, varValue);
            }
            return this;
        }

        public EasyTemplate align(){
            this.align = true;
            return this;
        }

        public EasyTemplate outputFlushing(){
            this.flushWriter = true;
            return this;
        }

        public EasyTemplate endLine(String endl){
            this.endln = endl;
            return this;
        }

        public EasyTemplate useJavaScript(){
            this.javascript = true;
            return this;
        }

        public EasyTemplate output(Writer writer){
            this.writer = writer;
            return this;
        }

        public EasyTemplate output(OutputStream output){
            return output(output, null);
        }

        public EasyTemplate output(OutputStream output,Charset cs){
            if( output==null )throw new IllegalArgumentException( "output==null" );
            if( cs==null )cs = Charset.defaultCharset();
            this.writer = new OutputStreamWriter(output, cs);
            return this;
        }

        public String eval(){
            ContextBuilder ctxb = this.context(contextVars.getClass());
            if( align ){
                ctxb = ctxb.align();
            }
            if( contextVars!=null && !contextVars.isEmpty() ){
                for( Map.Entry<String,Object> ve : contextVars.entrySet() ){
                    String varn = ve.getKey();
                    if( varn==null )continue;
                    ctxb = ctxb.bind(varn, ve.getValue());
                }
            }
            if( javascript )ctxb = ctxb.useJavaScript();

            Context ctx = ctxb.build();
            return ctx.eval(contextVars);
        }

        public void println(){
            if( writer!=null ){
                try {
                    writer.write(eval());
                    if( endln!=null )writer.write(endln);
                    if( flushWriter )writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(BasicTemplate.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                System.out.println(eval());
            }
        }

        public void print(){
            if( writer!=null ){
                try {
                    writer.write(eval());
//                    if( endln!=null )writer.write(endln);
                    if( flushWriter )writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(BasicTemplate.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                System.out.print(eval());
            }
        }
    }

    private static WeakHashMap<String,EasyTemplate> cache = new WeakHashMap<String, EasyTemplate>();

    public static EasyTemplate template(String template){
        if( template==null )throw new IllegalArgumentException( "template==null" );
        EasyTemplate et = cache.get(template);
        if( et==null ){
            et = new EasyTemplate(template);
            cache.put(template, et);
        }
        return et;
    }
}
