package xyz.cofe.gui.swing.bean;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.fn.Fn2;
import xyz.cofe.fn.Fn3;
import xyz.cofe.fn.Fn4;
import xyz.cofe.text.Text;

/**
 * Связывает событие изменение свойства
 * @author user
 */
public class Binder {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(Binder.class.getName());
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
        logger.entering(Binder.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(Binder.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(Binder.class.getName(), method, result);
    }
    //</editor-fold>

    private Object bean;
    private boolean beanWeakReference = true;
    private Predicate<String> listenProperties;

    protected Binder(){
    }
    protected Binder(Binder sample){
        if( sample==null )throw new IllegalArgumentException("sample == null");
        this.bean = sample.bean;
        this.beanWeakReference = sample.beanWeakReference;
        this.listenProperties = sample.listenProperties;
    }

    @Override
    public Binder clone(){
        return new Binder(this);
    }

    //<editor-fold defaultstate="collapsed" desc="bean : Object">
    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public static Binder bean(Object bean){
        if( bean==null )throw new IllegalArgumentException("bean == null");
        Binder b = new Binder();
        b.setBean(bean);
        return b;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="weakBean()">
    public boolean isBeanAsWeak(){ return beanWeakReference; }
    public void setBeanAsWeak( boolean weak ){ beanWeakReference = weak; }
    public Binder weakBean(boolean weakRef){
        Binder b = clone();
        b.setBeanAsWeak(weakRef);
        return b;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="properties()">
    public Predicate<String> getProperties(){ return listenProperties; }
    public void setProperties(Predicate<String> props){ listenProperties = props; }
    public Binder properties(Predicate<String> props){
        Binder b = clone();
        b.setProperties(props);
        return b;
    }
    public Binder properties(String ... props){
        Binder b = clone();
        if( props==null ){
            b.setProperties(null);
        }else{
            b.setProperties(Text.Predicates.in(false, props));
        }
        return b;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TargetBinder">
    public static class TargetBinder extends Binder {
        private Object consumer;
        private boolean consumerAsWeakReference = false;
        private Function convertor;

        public TargetBinder(Binder sample, Object consumer) {
            super(sample);
            this.consumer = consumer;
        }
        public TargetBinder(TargetBinder sample){
            if( sample!=null ){
                this.consumer = sample.consumer;
                this.consumerAsWeakReference = sample.consumerAsWeakReference;
                this.convertor = sample.convertor;
            }
        }

        //<editor-fold defaultstate="collapsed" desc="clone()">
        @Override
        public TargetBinder clone(){
            return new TargetBinder(this);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="weakTarget()">
        public boolean isTargetAsWeak(){ return consumerAsWeakReference; }
        public void setTargetAsWeak(boolean weak){ consumerAsWeakReference = weak; }
        public TargetBinder weakTarget(boolean weak){
            TargetBinder b = clone();
            b.setTargetAsWeak(weak);
            return b;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="convertor()">
        public Function getFunction() {
            return convertor;
        }

        public void setFunction(Function convertor) {
            this.convertor = convertor;
        }

        public TargetBinder convertor(Function conv){
            this.convertor = conv;
            return this;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="bind()">
        public PropertyChangeDelegator bind(){
            Object bean = getBean();
            if( bean==null )throw new IllegalStateException("bean == null");

            Object target = consumer;
            if( target==null )throw new IllegalStateException("target == null");

            Class cls = bean.getClass();

            try {
                final Method mAdd = cls.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
                final Method mRemove = cls.getMethod("removePropertyChangeListener", PropertyChangeListener.class);

                PropertyChangeDelegator pcd = new PropertyChangeDelegator(
                    bean, mRemove, isBeanAsWeak(), getProperties(), consumer, isTargetAsWeak());

                if( convertor!=null ){
                    pcd = pcd.convertor(convertor);
                }

                mAdd.invoke(bean, pcd);

                return pcd;
            } catch (NoSuchMethodException|IllegalAccessException|InvocationTargetException ex) {
                throw new Error("can't bind", ex);
            }
        }
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="target()">
    public TargetBinder listen(Runnable reciver){
        return new TargetBinder(this, reciver);
    }
    public <T> TargetBinder listen( Consumer<T> reciver){
        return new TargetBinder(this, reciver);
    }
    public TargetBinder listen( Function<PropertyChangeEvent,Object> reciver){
        return new TargetBinder(this, reciver);
    }
    public <T> TargetBinder listen( Fn2<T,T,Object> reciver){
        return new TargetBinder(this, reciver);
    }
    public <T> TargetBinder listen( Fn3<String,T,T,Object> reciver){
        return new TargetBinder(this, reciver);
    }
    public <B,T> TargetBinder listen( Fn4<B,String,T,T,Object> reciver){
        return new TargetBinder(this, reciver);
    }
    //</editor-fold>

    public TargetBinder target(final Object bean,String property){
        if( bean==null )throw new IllegalArgumentException("bean == null");
        if( property==null )throw new IllegalArgumentException("property == null");

        try {
            PropertyDescriptor[] pds = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
            PropertyDescriptor pdTarget = null;
            for(PropertyDescriptor pd : pds){
                if( property.equals(pd.getName()) ){
                    pdTarget = pd;
                    break;
                }
            }
            if( pdTarget==null )throw new Error("can't bind - target property \""+property+"\" not found");
            if( pdTarget.getWriteMethod()==null )throw new Error("can't bind - target property \""+property+"\" not writeable");
            final Method mwrite = pdTarget.getWriteMethod();
            return new TargetBinder(this, (Fn2) ( oldv, newv )->{
                try {
                    mwrite.invoke(bean, newv);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Binder.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            });
        } catch (IntrospectionException ex) {
            Logger.getLogger(Binder.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("can't bind to target property \""+property+"\" : "+ex.getMessage(), ex);
        }
    }

    public static class Listener<T> {
        private Binder binder;
        private String property;
        private Consumer consumer;
        private Listener parent;

        public Listener(Binder binder, String property, Consumer consumer){
            this.binder = binder;
            this.consumer = consumer;
            this.property = property;
        }

        public Listener(Listener parent, Binder binder, String property, Consumer consumer){
            this.binder = binder;
            this.consumer = consumer;
            this.property = property;
            this.parent = parent;
        }

        private Function convertor;

        public <F> Listener<T> convert( Function<F,T> conv ){
            convertor = conv;
            return this;
        }

        public AutoCloseable start(){
            final Queue<AutoCloseable> listeners = new ConcurrentLinkedQueue<>();

            if( property!=null ){
                binder = binder.clone().properties(property);
            }

            TargetBinder tBinder = new TargetBinder(binder, consumer);
            if( convertor!=null ){
                tBinder = tBinder.convertor(convertor);
            }

            listeners.add( tBinder.bind() );

            Listener lprnt = parent;
            if( lprnt!=null ){
                listeners.add( lprnt.start() );
            }

            return new AutoCloseable() {
                @Override
                public void close() throws Exception {
                    while(true){
                        AutoCloseable cl = listeners.poll();
                        if( cl==null )break;
                        cl.close();
                    }
                }
            };
        }

        public <N> Listener<N> listen(String property, Consumer<N> consumer ){
            return new Listener<>( this, binder, property, consumer );
        }
    }

//    public <T> Listener<T> listen(String property, Class<T> type, Reciver<T> consumer){
//        return null;
//    }

    public <T> Listener<T> listen(String property, Consumer<T> consumer){
        return new Listener<>(this, property, consumer);
    }
}
