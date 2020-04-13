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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.collection.graph.Path;
import xyz.cofe.fn.Fn2;
import xyz.cofe.typeconv.ExtendedCastGraph;
import xyz.cofe.typeconv.TypeCastGraph;

/**
 * Адаптер XVisitor. <br>
 * @see PathMatch
 * @author gocha
 */
public class XVisitorAdapter implements XVisitor
{
    //<editor-fold defaultstate="collapsed" desc="XVisitorAdapter()">
    /**
     * Конструктор
     */
    public XVisitorAdapter(){
        TypeCastGraph tcg = getTypeCastGraph();
        
        Method[] methods = this.getClass().getMethods();
        
        PathParser parser = new PathParser();
        for( Method m : methods ){
            PathMatch pm = m.getAnnotation(PathMatch.class);
            if( pm==null )continue;
            
            Class[] params = m.getParameterTypes();
            if( params.length==1 ){
                if( XEventPath.class.equals( params[0] ) ){
                    if( pm.enter().length()>0 ){
                        PathPatternMethod mpd = new PathPatternMethod();
                        mpd.setExpression( parser.parse(pm.enter()) );
                        mpd.setEnter( true );
                        mpd.setExit( false );
                        mpd.setOwner( this );
                        mpd.setMethod( m );
                        pathPatterns.add(mpd);
                    }
                    if( pm.exit().length()>0 ){
                        PathPatternMethod mpd = new PathPatternMethod();
                        mpd.setExpression( parser.parse(pm.exit()) );
                        mpd.setEnter( false );
                        mpd.setExit( true );
                        mpd.setOwner( this );
                        mpd.setMethod( m );
                        pathPatterns.add(mpd);
                    }
                }else if( String.class.equals(params[0]) ){
                    if( pm.content().length()>0 ){
                        ContentPatternMethod mcd = new ContentPatternMethod();
                        mcd.setTypeCastGraph( tcg );
                        mcd.setExpression( parser.parse(pm.content()) );
                        mcd.setMethod( m );
                        mcd.setOwner( this );
                        mcd.setArgs( new int[]{ ContentPatternMethod.ARG_CONTENT } );
                        contentPatterns.add(mcd);
                    }
                }
            }else if( params.length==2 ){
                if( XEventPath.class.equals(params[0]) ){
                    Path p = tcg.findPath(String.class, params[1]);
                    if( p!=null ){
                        //if( String.class.equals(params[1]) ){
                        if( pm.content().length()>0 ){
                            ContentPatternMethod mcd = new ContentPatternMethod();
                            mcd.setTypeCastGraph( tcg );
                            mcd.setExpression( parser.parse(pm.content()) );
                            mcd.setMethod( m );
                            mcd.setOwner( this );
                            mcd.setArgs( new int[]{
                                ContentPatternMethod.ARG_PATH,
                                ContentPatternMethod.ARG_CONTENT
                            });
                            contentPatterns.add(mcd);
                        }
                    }
                }
                if( XEventPath.class.equals(params[1]) ){
                    Path p = tcg.findPath(String.class, params[0]);
                    if( p!=null ){
                        //if( String.class.equals(params[0]) ){
                        if( pm.content().length()>0 ){
                            ContentPatternMethod mcd = new ContentPatternMethod();
                            mcd.setTypeCastGraph( tcg );
                            mcd.setExpression( parser.parse(pm.content()) );
                            mcd.setMethod( m );
                            mcd.setOwner( this );
                            mcd.setArgs( new int[]{
                                ContentPatternMethod.ARG_CONTENT,
                                ContentPatternMethod.ARG_PATH
                            });
                            contentPatterns.add(mcd);
                        }
                    }
                }
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Шаблоны при сощении тэга">
    //<editor-fold defaultstate="collapsed" desc="pathPatterns">
    protected final List<PathPattern> pathPatterns = new ArrayList<>();
    
    /**
     * Указывает шаблоны сопоставления XEventPath и соответ обработчик
     * @return список шаблонов
     */
    public List<PathPattern> getPathPatterns(){ return pathPatterns; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="PathPattern">
    /**
     * Шаблон-обработчик при обходе узла
     */
    public interface PathPattern {
        /**
         * Указывает шаблон
         * @return шаблон
         */
        public PathExpression getExpression();
        /**
         * Указывает что шаблон срабатывает при входе в узел
         * @return true - срабатывает при входе в узел
         */
        public boolean isEnter();
        /**
         * Указывает что шаблон срабатывает при выходе из узела
         * @return true - шаблон срабатывает
         */
        public boolean isExit();
        /**
         * Делегирует обработку при посещении узла
         * @param path обрабатываемый узел
         */
        public void delegate( XEventPath path );
    }
    
    //<editor-fold defaultstate="collapsed" desc="PathPatternAbstract">
    /**
     * Абстрактная реализация PathPattern
     */
    public static abstract class PathPatternAbstract implements PathPattern {
        /**
         * Конструктор
         */
        public PathPatternAbstract(){}
        
        /**
         * Конструктор
         * @param pexp шаблон
         * @param enter шаблон срабатывает при входе в узел
         * @param exit шаблон срабатывает при выходе из узела
         */
        public PathPatternAbstract(PathExpression pexp, boolean enter, boolean exit){
            this.expression = pexp;
            this.enter = enter;
            this.exit = exit;
        }
        
        private PathExpression expression;
        private boolean enter;
        private boolean exit;
        
        /**
         * Указывает шаблон
         * @return шаблон
         */
        @Override
        public PathExpression getExpression() {
            return expression;
        }
        
        /**
         * Указывает шаблон
         * @param expression шаблон
         */
        public void setExpression(PathExpression expression) {
            this.expression = expression;
        }
        
        /**
         * Указывает что шаблон срабатывает при входе в узел
         * @return true - срабатывает при входе в узел
         */
        @Override
        public boolean isEnter() {
            return enter;
        }
        
        /**
         * Указывает что шаблон срабатывает при входе в узел
         * @param enter true - срабатывает при входе в узел
         */
        public void setEnter(boolean enter) {
            this.enter = enter;
        }
        
        /**
         * Указывает что шаблон срабатывает при выходе из узела
         * @return true - срабатывает при выходе
         */
        @Override
        public boolean isExit() {
            return exit;
        }
        
        /**
         * Указывает что шаблон срабатывает при выходе из узела
         * @param exit срабатывает при выходе
         */
        public void setExit(boolean exit) {
            this.exit = exit;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="PathPatternMethod">
    /**
     * Шаблон обработчик, при совпадении передаует совпавший узел (XEventPath) в качестве параметра методу
     */
    public static class PathPatternMethod extends PathPatternAbstract {
        /**
         * Конструктор
         */
        public PathPatternMethod(){}
        
        /**
         * Конструктор
         * @param pexp шаблон
         * @param enter срабатывает при входе в узел
         * @param exit срабатывает при выходу из узла
         * @param ownr обработчик
         * @param meth метод обработчика ( ownr.meth( XEventPath path ) )
         */
        public PathPatternMethod(PathExpression pexp, boolean enter, boolean exit, Object ownr, Method meth){
            super(pexp, enter, exit);
            this.owner = ownr;
            this.method = meth;
        }
        
        private Object owner;
        private Method method;
        
        /**
         * Указывает объект - обработчик
         * @return объект - обработчик
         */
        public Object getOwner() {
            return owner;
        }
        
        /**
         * Указывает объект - обработчик
         * @param owner объект - обработчик
         */
        public void setOwner(Object owner) {
            this.owner = owner;
        }
        
        /**
         * Указывает метод обработчика ( ownr.meth( XEventPath path ) )
         * @return метод обработчика
         */
        public Method getMethod() {
            return method;
        }
        
        /**
         * Указывает метод обработчика ( ownr.meth( XEventPath path ) )
         * @param method метод обработчика
         */
        public void setMethod(Method method) {
            this.method = method;
        }
        
        @Override
        public void delegate( XEventPath path ){
            try {
                method.invoke(owner, path);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(XVisitorAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(XVisitorAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(XVisitorAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    //</editor-fold>
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Шаблоны при сощении текстового узла">
    //<editor-fold defaultstate="collapsed" desc="contentPatterns">
    protected final List<ContentPattern> contentPatterns = new ArrayList<>();
    
    /**
     * Указывает шаблоны сопоставления XEventPath при входу в текстовый узел и соответ обработчик
     * @return список шаблонов
     */
    public List<ContentPattern> getContentPatterns(){ return contentPatterns; }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="ContentPattern">
    /**
     * Итерфейс проверки текстового содержания (для текстовых узлов)
     */
    public interface ContentPattern {
        /**
         * Указывает шаблон проверки узла
         * @return шаблон проверки
         */
        public PathExpression getExpression();
        
        /**
         * Делегирует узел (XEventPath) при совпадении
         * @param path узел (XEventPath)
         * @param content текстовое содержание
         */
        public void delegate( XEventPath path, String content );
    }
    
    /**
     * Абстрактный класс проверки совпадения
     */
    public static abstract class ContentPatternAbstract implements ContentPattern {
        private PathExpression expression;

        /**
         * Указывает шаблон для XEventPath
         * @return шаблон или null
         */
        @Override
        public PathExpression getExpression() {
            return expression;
        }

        /**
         * Указывает шаблон для XEventPath
         * @param expression шаблон или null
         */
        public void setExpression(PathExpression expression) {
            this.expression = expression;
        }
        
        @Override
        public abstract void delegate( XEventPath path, String content );
    }
    
    /**
     * Класс для проверки содержания текстового узла
     */
    public static class ContentPatternMethod extends ContentPatternAbstract {
        private Object owner;
        private Method method;
        private TypeCastGraph typeCastGraph;
        
        public static final int ARG_PATH=1;
        public static final int ARG_CONTENT=2;
        
        private int[] args = {};

        /**
         * Указывает объект которому будет делегирован вызов при совпадении шаблона
         * @return объект приемник или null
         */
        public Object getOwner() {
            return owner;
        }

        /**
         * Указывает объект которому будет делегирован вызов при совпадении шаблона
         * @param owner объект приемник
         */
        public void setOwner(Object owner) {
            this.owner = owner;
        }

        /**
         * Указывает метод объекта которому будет делегирован вызов при совпадении шаблона
         * @return метод или null
         */
        public Method getMethod() {
            return method;
        }

        /**
         * Указывает метод объекта которому будет делегирован вызов при совпадении шаблона
         * @param method метод
         */
        public void setMethod(Method method) {
            this.method = method;
        }

        /**
         * Указывает граф для преобразования передаваемых значений
         * @return граф преобразования типов данных
         */
        public TypeCastGraph getTypeCastGraph() {
            return typeCastGraph;
        }

        /**
         * * Указывает граф для преобразования передаваемых значений
         * @param typeCastGraph граф преобразования типов данных
         */
        public void setTypeCastGraph(TypeCastGraph typeCastGraph) {
            this.typeCastGraph = typeCastGraph;
        }

        /**
         * Указывает какие аргументы в какой последовательности передавать методу
         * @return последовательность аргументов
         */
        public int[] getArgs() {
            return args;
        }

        /**
         * Указывает какие аргументы в какой последовательности передавать методу
         * @param args последовательность аргументов
         */
        public void setArgs(int[] args) {
            this.args = args;
        }
        
        @Override
        public void delegate( XEventPath path, String content ){
            try {
                int[] args = this.args;
                if( args==null )throw new IllegalStateException("property args is null");
                
                Object[] params = new Object[args.length];
                for( int ai=0; ai<args.length; ai++ ){
                    switch(args[ai]){
                        case ARG_PATH: params[ai]=path; break;
                        case ARG_CONTENT: {
                            Class trgtCls = method.getParameterTypes()[ai];
                            String str = content;
                            Object val = typeCastGraph.cast(str, trgtCls);
                            params[ai] = val;
                        }
                        break;
                    }
                }
                
                method.invoke(owner, params);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(XVisitorAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(XVisitorAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(XVisitorAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    //</editor-fold>
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="builders">
    //<editor-fold defaultstate="collapsed" desc="ContentPatternBuilder<T>">
    public static abstract class ContentPatternBuilder<T> {
        //<editor-fold defaultstate="collapsed" desc="patternBuilder">
        protected PatternBuilder patternBuilder;
        public PatternBuilder getPatternBuilder() {
            return patternBuilder;
        }
        public void setPatternBuilder(PatternBuilder patternBuilder) {
            this.patternBuilder = patternBuilder;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="consumers">
        protected List<Fn2<XEventPath,String,Object>> consumers;
        
        public List<Fn2<XEventPath,String,Object>> getConsumers() {
            if( consumers!=null )return consumers;
            consumers = new ArrayList<>();
            return consumers;
        }
        
        public void setConsumers(List<Fn2<XEventPath,String,Object>> consumers) {
            this.consumers = consumers;
        }
        //</editor-fold>
        
        protected abstract T result();
        
        public T consumer( Fn2<XEventPath,String,Object> consumer ){
            if( consumer==null )throw new IllegalArgumentException("consumer == null");
            getConsumers().add(consumer);
            return result();
        }
        public T consumer( final Consumer<String> consumer ){
            if( consumer==null )throw new IllegalArgumentException("consumer == null");
            getConsumers().add(( arg1, arg2 )->{
                consumer.accept(arg2);
                return null;
            });
            return result();
        }
        
        public ContentPatternAbstract buildContentPattern(){
            List<Fn2<XEventPath,String,Object>> recivers = new ArrayList<Fn2<XEventPath,String,Object>>(getConsumers());
            ContentPatternAbstract ppa = new ContentPatternAbstract() {
                @Override
                public void delegate(XEventPath path, String content) {
                    for( Fn2<XEventPath,String,Object> r : recivers ){
                        r.apply(path,content);
                    }
                }
            };
            ppa.setExpression(getPatternBuilder().getExpression());
            return ppa;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="TagPatternBuilder<T>">
    public static abstract class TagPatternBuilder<T> {
        //<editor-fold defaultstate="collapsed" desc="patternBuilder">
        protected PatternBuilder patternBuilder;
        public PatternBuilder getPatternBuilder() {
            return patternBuilder;
        }
        public void setPatternBuilder(PatternBuilder patternBuilder) {
            this.patternBuilder = patternBuilder;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="consumers">
        protected List<Consumer<XEventPath>> consumers;
        
        public List<Consumer<XEventPath>> getConsumers() {
            if( consumers!=null )return consumers;
            consumers = new ArrayList<>();
            return consumers;
        }
        
        public void setConsumers(List<Consumer<XEventPath>> consumers) {
            this.consumers = consumers;
        }
        //</editor-fold>
        
        protected abstract T result();
        
        public T consumer( Consumer<XEventPath> consumer ){
            if( consumer==null )throw new IllegalArgumentException("consumer == null");
            getConsumers().add(consumer);
            return result();
        }
        
        public PathPatternAbstract buildPathPattern(){
            final List<Consumer<XEventPath>> recivers = new ArrayList<>(getConsumers());
            PathPatternAbstract ppa = new PathPatternAbstract() {
                @Override
                public void delegate(XEventPath path) {
                    for( Consumer<XEventPath> r : recivers ){
                        r.accept(path);
                    }
                }
            };
            ppa.setExpression(getPatternBuilder().getExpression());
            return ppa;
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="EnterPatternBuilder">
    public static class EnterPatternBuilder extends TagPatternBuilder<EnterPatternBuilder> {
        @Override protected EnterPatternBuilder result() { return this; }
        public PatternBuilder listen(){
            PathPatternAbstract ptrn = buildPathPattern();
            ptrn.setEnter(true);
            ptrn.setExit(false);
            
            PatternBuilder pb = getPatternBuilder();
            pb.getXVisitorAdapter().getPathPatterns().add(ptrn);
            
            return pb;
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="ExitPatternBuilder">
    public static class ExitPatternBuilder extends TagPatternBuilder<ExitPatternBuilder> {
        @Override protected ExitPatternBuilder result() { return this; }
        public PatternBuilder listen(){
            PathPatternAbstract ptrn = buildPathPattern();
            ptrn.setEnter(false);
            ptrn.setExit(true);
            
            PatternBuilder pb = getPatternBuilder();
            pb.getXVisitorAdapter().getPathPatterns().add(ptrn);
            
            return pb;
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="TextPatternBuilder">
    static class TextPatternBuilder extends ContentPatternBuilder<TextPatternBuilder> {
        @Override protected TextPatternBuilder result() { return this; }
        public PatternBuilder listen(){
            ContentPatternAbstract ptrn = buildContentPattern();
            
            PatternBuilder pb = getPatternBuilder();
            pb.getXVisitorAdapter().getContentPatterns().add(ptrn);
            
            return pb;
        }
    }
    //</editor-fold>
    
    /**
     * Построение шаблонов обработчиков
     */
    public static class PatternBuilder {
        public PatternBuilder(XVisitorAdapter xv){
            this.xVisitorAdapter = xv;
        }
        
        //<editor-fold defaultstate="collapsed" desc="XVisitorAdapter">
        protected XVisitorAdapter xVisitorAdapter;
        
        /**
         * Указывает "постетителя" XML узлов
         * @return посетитель или null
         */
        public XVisitorAdapter getXVisitorAdapter() {
            return xVisitorAdapter;
        }
        
        /**
         * Указывает "постетителя" XML узлов
         * @param xVisitorAdapter посетитель или null
         */
        public void setXVisitorAdapter(XVisitorAdapter xVisitorAdapter) {
            this.xVisitorAdapter = xVisitorAdapter;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="expression">
        protected PathExpression expression;
        /**
         * Указывает шаблон для проверки XEventPath
         * @return шаблон или null
         */
        public PathExpression getExpression() {
            return expression;
        }
        /**
         * Указывает шаблон для проверки XEventPath
         * @param expression шаблон или null
         */
        public void setExpression(PathExpression expression) {
            this.expression = expression;
        }
        /**
         * Указывает шаблон для проверки XEventPath
         * @param exp шаблон
         * @return self - ссылка
         */
        public PatternBuilder expression(PathExpression exp){            
            this.expression = exp;
            return this;
        }
        /**
         * Указывает шаблон для проверки XEventPath
         * @param exp шаблон
         * @return self - ссылка
         */
        public PatternBuilder expression(String exp){
            if( exp!=null ){
                PathParser parser = new PathParser();
                this.expression = parser.parse(exp);
            }else{
                this.expression = null;
            }
            return this;
        }
        //</editor-fold>
        
        /**
         * Добавляет приемников когда шаблон стработал (есть совпадение) при входе в XML узел
         * @param consumer приемники
         * @return Ссылка на шаблон
         */
        public EnterPatternBuilder enter(Consumer<XEventPath> ... consumer){
            EnterPatternBuilder b = new EnterPatternBuilder();
            b.setPatternBuilder(this);
            for( Consumer<XEventPath> r : consumer ){
                b = b.consumer(r);
            }
            return b;
        }
        /**
         * Добавляет приемников когда шаблон стработал (есть совпадение) при выходе из XML узела
         * @param consumer приемники
         * @return Ссылка на шаблон
         */
        public ExitPatternBuilder exit(Consumer<XEventPath> ... consumer){
            ExitPatternBuilder b = new ExitPatternBuilder();
            b.setPatternBuilder(this);
            for( Consumer<XEventPath> r : consumer ){
                b = b.consumer(r);
            }
            return b;
        }
        /**
         * Добавляет приемников когда шаблон стработал (есть совпадение) при входе из XML TEXT узел
         * @param consumer приемники
         * @return Ссылка на шаблон
         */
        public TextPatternBuilder text( Fn2<XEventPath,String,Object> ... consumer ){
            TextPatternBuilder b = new TextPatternBuilder();
            b.setPatternBuilder(this);
            for( Fn2<XEventPath,String,Object> r : consumer ){
                b = b.consumer(r);
            }
            return b;
        }
        /**
         * Добавляет приемников когда шаблон стработал (есть совпадение) при входе из XML TEXT узел
         * @param consumer приемники
         * @return Ссылка на шаблон
         */        
        public TextPatternBuilder text( Consumer<String> ... consumer ){
            TextPatternBuilder b = new TextPatternBuilder();
            b.setPatternBuilder(this);
            for( Consumer<String> r : consumer ){
                b = b.consumer(r);
            }
            return b;
        }
    }
    
    /**
     * Создает строителя для обработки шаблонных XML сообщений
     * @param expr выражение
     * @return Строитель шаблона
     */
    public PatternBuilder pattern( String expr ){
        return new PatternBuilder(this).expression(expr);
    }
    /**
     * Создает строителя для обработки шаблонных XML сообщений
     * @param expr выражение
     * @return Строитель шаблона
     */
    public PatternBuilder pattern( PathExpression expr ){
        return new PatternBuilder(this).expression(expr);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="typeCastGraph">
    protected TypeCastGraph typeCastGraph;
    /**
     * Граф преобразование типов данных
     * @return граф преобразование типов данных
     */
    public TypeCastGraph getTypeCastGraph(){
        synchronized(this){
            if( typeCastGraph!=null )return typeCastGraph;
            typeCastGraph = new ExtendedCastGraph();
            return typeCastGraph;
        }
    }
    /**
     * Граф преобразование типов данных
     * @param typeCastGraph преобразование типов данных
     */
    public void setTypeCastGraph(TypeCastGraph typeCastGraph) {
        this.typeCastGraph = typeCastGraph;
    }
    /**
     * Указывает преобразование типов данных
     * @param tc граф преобразования
     * @return self ссылка
     */
    public XVisitorAdapter typeCast(TypeCastGraph tc){
        this.typeCastGraph = tc;
        return this;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="enter/exit/characters">
    @Override
    public void enter(XEventPath path) {
        if( path==null )return;
        for( PathPattern mpd : pathPatterns ){
            if( mpd==null )continue;
            if( mpd.getExpression()==null )continue;
            if( !mpd.isEnter() )continue;
            if( !mpd.getExpression().test(path) )continue;
            mpd.delegate(path);
        }
    }
    
    @Override
    public void exit(XEventPath path) {
        if( path==null )return;
        for( PathPattern mpd : pathPatterns ){
            if( mpd==null )continue;
            if( mpd.getExpression()==null )continue;
            if( !mpd.isExit() )continue;
            if( !mpd.getExpression().test(path) )continue;
            mpd.delegate(path);
        }
    }
    
    @Override
    public void characters(XEventPath path, String text) {
        if( path==null )return;
        for( ContentPattern mcd : contentPatterns ){
            if( mcd==null )continue;
            if( mcd.getExpression()==null )continue;
            if( !mcd.getExpression().test(path) )continue;
            mcd.delegate(path,text);
        }
    }
    //</editor-fold>
}
