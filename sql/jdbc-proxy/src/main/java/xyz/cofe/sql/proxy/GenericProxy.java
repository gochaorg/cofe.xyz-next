/*
 * The MIT License
 *
 * Copyright 2017 user.
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

package xyz.cofe.sql.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Общий класс для создания прокси объектов
 * @author nt.gocha@gmail.com
 */
public class GenericProxy 
implements InvocationHandler
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(GenericProxy.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }
    
    private static boolean isLogSevere(){ 
        Level logLevel = logger.getLevel();
        return logLevel==null ? true : logLevel.intValue() <= Level.SEVERE.intValue();
    }
    
    private static boolean isLogWarning(){
        Level logLevel = logger.getLevel(); 
        return logLevel==null  ? true : logLevel.intValue() <= Level.WARNING.intValue();
    }
    
    private static boolean isLogInfo(){ 
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.INFO.intValue();
    }
    
    private static boolean isLogFine(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINE.intValue();
    }
    
    private static boolean isLogFiner(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINER.intValue();
    }
    
    private static boolean isLogFinest(){ 
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINEST.intValue();
    }

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
        logger.entering(GenericProxy.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(GenericProxy.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(GenericProxy.class.getName(), method, result);
    }
    //</editor-fold>
    
    protected Object source;
    
    /**
     * Подписчики на обработку вызоыва метода целевого объекта.
     * 
     * <p>
     * Если подписчик реализовывает интерфейс MethodArgsRewrite, то он будет вызван перед вызовом целевого метода
     */
    protected final List<MethodCallListener> methodCallListeners = new CopyOnWriteArrayList<>();
    protected boolean finalizeSource = false;
    protected final Map<String,Level> methodLogLevel = new LinkedHashMap<>();
    
    public GenericProxy(Object source){
        this.source = source;
    }

    //<editor-fold defaultstate="collapsed" desc="loggerName">
    protected String loggerName = GenericProxy.class.getName();
    public String getLoggerName(){
        synchronized( this ){
            return loggerName;
        }
    }
    public void setLoggerName(String lgrName){
        synchronized(this){
            this.loggerName = lgrName;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="finalize()">
    @Override
    @SuppressWarnings("FinalizeDeclaration")
    protected void finalize() throws Throwable {
        String lgrName = getLoggerName();
        
        if( lgrName!=null ){
            Logger lgr = Logger.getLogger(lgrName);
            lgr.log(Level.FINE, "finalize");
        }else{
            logFine("finalize");
        }
        
        if( source!=null ){
            if( finalizeSource ){
                try{
                    if( source instanceof AutoCloseable ){
                        if( lgrName!=null ){
                            Logger lgr = Logger.getLogger(lgrName);
                            lgr.log(Level.FINE, "close source {0}",source);
                        }else{
                            logFine("close surce {0}",source);
                        }
                        ((AutoCloseable)source).close();
                    }
                } catch (Throwable err){
                    if( lgrName!=null ){
                        Logger lgr = Logger.getLogger(lgrName);
                        lgr.log(Level.SEVERE, null, err);
                    }else{
                        logException(err);
                    }
                }
            }
            source = null;
        }
        
        methodCallListeners.clear();
        methodLogLevel.clear();
        
        super.finalize();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="logInvoke()">
    protected void logInvoke(Object proxy, Method method, Object[] args){
        String mname = method!=null ? method.getName() : null;
        
        String lgrName = getLoggerName();
        
        String lgrname = lgrName==null ? GenericProxy.class.getName() : lgrName;
        Level lvlm = methodLogLevel.get(mname);
        Level lvl = lvlm!=null ? lvlm : Level.FINEST;
        
        Logger lgr = Logger.getLogger(lgrname);
        lgr.log(lvl, "invoke {0}", mname);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="fireBeginCall()">
    /**
     * Вызывается перед началом вызова целевого метода
     * @param proxy прокси объект
     * @param method целевой метод
     * @param args аргументы вызова
     * @return Переопределенные аргументы
     */
    protected HookArguments fireBeginCall(Object proxy, Method method, Object[] args){
        HookArguments reargs = null;
        List<Object[]> prevArgs = new ArrayList<>();
        
        for( MethodCallListener l : methodCallListeners ){
            if( l==null )continue;
            l.beginCall(this, source, method, args);
            
            if( l instanceof MethodArgsRewrite && ((MethodArgsRewrite)l).isRewriteArguments() ){
                Object[] curArgs = reargs!=null && reargs.getArgs()!=null 
                    ? reargs.getArgs()
                    : args;
                
                HookArguments newReArgs = ((MethodArgsRewrite)l).rewriteArguments(this, source, method, curArgs, prevArgs);
                if( newReArgs!=null && newReArgs.getArgs()!=null ){
                    reargs = newReArgs;
                    prevArgs.add(curArgs);
                }
                
                //if( ((MethodArgsRewrite)l).isChainArgs() ){
                //    reargs = ((MethodArgsRewrite)l).rewriteArguments(
                //        this, source, method, reargs!=null && reargs.getArgs()!=null ? reargs.getArgs() : args
                //    );
                //}else{
                //    reargs = ((MethodArgsRewrite)l).rewriteArguments(this, source, method, args);
                //}
            }
            
        }
        return reargs;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="fireEndCall()">
    protected Object fireEndCall( Object proxy, Method method, Object[] args, Object result, Throwable err ){
        for( MethodCallListener l : methodCallListeners ){
            if( l==null )continue;
            HookResult rrcall = l.endCall(proxy, source, method, args,result,null);
            if( rrcall!=null ){
                logFine( "redefine result call from {0} to {1}",result,rrcall.getResult() );
                result = rrcall.getResult();
            }
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="invoke() - Вызов proxy метода">
    /**
     * Вызов целевого метода.
     * 
     * <p>
     * Сначала вызывается fireBeginCall который модет вернуть новые аргументы вызова
     * 
     * <p>
     * Если подписчик поддерживает MethodCallRewrite то будет вызван последний 
     * подписчик который поддерживает MethodCallRewrite
     * 
     * <p>
     * Последним будет вызван fireEndCall с результатом вызова
     * 
     * @param proxy прокси
     * @param method метод целевого объекта
     * @param args аргумент
     * @return результат вызова
     * @throws Throwable Ошибка вызова метода
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logInvoke(proxy, method, args);
        
        //<editor-fold defaultstate="collapsed" desc="Вызов метода getProxyTarget">
        boolean method_from_GetProxyTarget = 
            method != null 
            ? method.getDeclaringClass().equals(GetProxyTarget.class)
            : false;
        
        if( method!=null 
        && "getProxyTarget".equals( method.getName() )
        && method_from_GetProxyTarget
        && (args==null || args.length==0) 
        ){
            return source;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Вызов метода getMethodCallListeners">
        boolean method_from_GetMethodCallListeners = 
            method != null 
            ? method.getDeclaringClass().equals(GetMethodCallListeners.class)
            : false;
        
        if( method_from_GetMethodCallListeners ){
            return methodCallListeners;
        }
        //</editor-fold>
        
        Object result = null;
        
        // try call direct
        try {
            HookArguments hargs = fireBeginCall(proxy,method,args);
            
            Object[] callArgs = hargs!=null && hargs.getArgs()!=null ? hargs.getArgs() : args;
            if( callArgs==null )callArgs = args;
            
            MethodCallRewrite callRewrite = null;
            if( methodCallListeners!=null ){
                for( int mci=methodCallListeners.size()-1; mci>=0; mci-- ){
                    Object mc = methodCallListeners.get(mci);
                    if( mc instanceof MethodCallRewrite && ((MethodCallRewrite)mc).isRewriteCall(this, source, method, callArgs) ){
                        callRewrite = (MethodCallRewrite)mc;
                        break;
                    }
                }
            }
            
            if( callRewrite!=null ){
                result = callRewrite.rewriteCall(this, source, method, callArgs);
            }else{
                result = method.invoke(source, callArgs);
            }
            
            result = fireEndCall(proxy, method, callArgs, result, null);
            return result;
        } catch (InvocationTargetException e) {
            fireEndCall(proxy, method, args, null, e.getTargetException());
            throw e.getTargetException();
        } catch (Throwable e){
            fireEndCall(proxy, method, args, result, e);
            throw e;
        }
        
        //throw new Error("proxy error for call method: "+method.getName());
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Builder - Создание Proxy">
    /**
     * Конструирование Proxy
     * @param <T> Интерфейс для которого создается Proxy
     */
    public static class Builder<T> {
        protected T source;
        protected Class<?>[] interf;
        
        protected ArrayList<MethodCallListener> methodCallListeners = new ArrayList<>();
        protected boolean finalizeSource = false;
        protected String loggerName = GenericProxy.class.getName();
        
        /**
         * Конструктор
         * @param src исходный объект
         * @param interf реализуемые интерфейсы
         */
        public Builder(T src, Class<?>[] interf ){
            this.source = src;
            
            Set<Class> interfs = new LinkedHashSet<>();
            for( Class itf : interf ){
                if( itf!=null )interfs.add(itf);
            }
            interfs.add(GetProxyTarget.class);
            interfs.add(GetMethodCallListeners.class);

            this.interf = interfs.toArray(new Class[]{});
        }
        
        /**
         * Добавление подписчика на вызовы методов
         * @param ls Подписчик
         * @return Конструктор proxy
         */
        public Builder<T> add( MethodCallListener ls ){
            if( ls!=null ){
                methodCallListeners.add(ls);
            }
            return this;
        }
        
        /**
         * Указание имени логгера для proxy
         * @param name Имя логгера для данного объекта
         * @return Конструктор proxy
         */
        public Builder<T> loggerName( String name ){
            if( name==null )throw new IllegalArgumentException("name == null");
            this.loggerName = name;
            return this;
        }
        
        /**
         * Создание proxy объекта
         * @return proxy объект
         */
        public T create(){
            GenericProxy cproxy = new GenericProxy(source);
            
            cproxy.methodCallListeners.addAll(methodCallListeners);
            cproxy.finalizeSource = finalizeSource;
            if( loggerName!=null )cproxy.loggerName = loggerName;
            
            return (T) Proxy.newProxyInstance(
                source.getClass().getClassLoader(),
                interf,
                cproxy
            );
        }
    }
    
    /**
     * Конструирование Proxy объекта
     * @param <T>   Интерфейс для которого создается Proxy
     * @param conn  Исходный объект
     * @param itfs  Интерфейсы которые реализуются
     * @return Конструктор proxy
     */
    public static <T> Builder<T> builder( T conn, Class ... itfs ) {
        return new Builder<T>( conn, itfs );
    }
    //</editor-fold>
}
