package xyz.cofe.gui.swing;

import xyz.cofe.fn.Consumer5;
import xyz.cofe.iter.Eterable;

import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Связывает событие изменение свойства c функцией
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

    /**
     * Объект владелец свойства
     */
    private Object bean;

    /**
     * Используеться слабая ссылка на владельца
     */
    private boolean beanWeakReference = true;

    /**
     * Фильтр свойств
     */
    private Predicate<String> listenProperties;

    /**
     * Конструктор
     */
    protected Binder(){
    }

    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
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

    /**
     * Объект чье свойство(а) слушаем
     * @return владелец свойств
     */
    public Object getBean() {
        return bean;
    }

//    /**
//     * Объект чье свойство(а) слушаем
//     * @param bean владелец свойств
//     */
//    public void setBean(Object bean) {
//        this.bean = bean;
//    }

    /**
     * Создание подписки на извещения о изменении свойств
     * @param bean владелец свойств
     * @return Построение слушателя
     */
    public static Binder bean(Object bean){
        if( bean==null )throw new IllegalArgumentException("bean == null");
        Binder b = new Binder();
        b.bean = (bean);
        return b;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="weakBean()">

    /**
     * Хранение ссылки на владельца ввиде слабой ({@link java.lang.ref.WeakReference})
     * @return true - будет создана слабая ссылка на владельца
     */
    public boolean isBeanAsWeak(){ return beanWeakReference; }

//    /**
//     * Хранение ссылки на владельца ввиде слабой ({@link java.lang.ref.WeakReference})
//     * @param weak true - будет создана слабая ссылка на владельца
//     */
//    public void setBeanAsWeak( boolean weak ){ beanWeakReference = weak; }

    /**
     *  Хранение ссылки на владельца ввиде слабой ({@link java.lang.ref.WeakReference})
     * @param weakRef true - будет создана слабая ссылка на владельца
     * @return Новый строитель слушателя
     */
    public Binder weakBean(boolean weakRef){
        Binder b = clone();
        b.beanWeakReference = (weakRef);
        return b;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="properties()">
    /**
     * Возвращает предикат определяющий какие свойства необходимо слушать
     * @return фильтр свойств
     */
    public Predicate<String> getProperties(){ return listenProperties; }

    /**
     * Указывает предикат определяющий какие свойства необходимо слушать
     * @param props фильтр свойств
     * @return Новый строитель слушателя
     */
    public TypedBinder<Object> properties(Predicate<String> props){
        Binder b = clone();
        b.listenProperties = (props);

        TypedBinder<Object> bnd = new TypedBinder<>(b,Object.class);
        return bnd;
    }

    /**
     * Указывает список свойств которые необходимо слушать
     * @param props список свойств
     * @return Новый строитель слушателя
     */
    public TypedBinder<Object> properties(String ... props){
        Binder b = clone();
        if( props==null ){
            b.listenProperties = (null);
        }else{
            b.listenProperties = n -> Eterable.of(props).filter(p->Objects.equals(p,n)).count()>0;
        }

        TypedBinder<Object> bnd = new TypedBinder<>(b,Object.class);
        return bnd;
    }

    /**
     * Указывает свойство и его тип
     * @param name Имя свойства
     * @param vtype Тип свойства
     * @param <T> Тип свойства
     * @return Создание слушателя
     */
    public <T> TypedBinder<T> property( String name, Class<T> vtype ){
        if( name==null )throw new IllegalArgumentException("name==null");
        if( vtype==null )throw new IllegalArgumentException("vtype==null");
        Binder b = clone();
        b.listenProperties = ( n -> Objects.equals(name,n) );
        TypedBinder<T> t = new TypedBinder<T>( b, vtype );
        return t;
    }
    //</editor-fold>

    public static class TypedBinder<T> extends Binder {
        public TypedBinder(Binder sample, Class<T> vtype) {
            super(sample);
            this.vtype = vtype;
        }

        private Class<T> vtype;
        public Class<T> getValueType(){ return  vtype; }

        public TargetBinder<T> listen( Consumer<T> ls ){
            if( ls==null )throw new IllegalArgumentException("ls == null");
            TargetBinder<T> t = new TargetBinder<T>(
                this, (pe,src,name,old,cur)->{
                    ls.accept(cur);
                }
            );
            return t.valueType(vtype);
        }

        public PropertyChangeDelegator bind( Consumer<T> ls ){
            if( ls==null )throw new IllegalArgumentException("ls == null");
            TargetBinder<T> t = new TargetBinder<T>(
                    this, (pe,src,name,old,cur)->{
                ls.accept(cur);
            }
            );
            return t.valueType(vtype).bind();
        }
    }

    //<editor-fold defaultstate="collapsed" desc="TargetBinder">
    /**
     * Привязка к целевой функции
     */
    public static class TargetBinder<T> extends Binder {
        private TargetBinder(Binder sample){
            super(sample);
        }
        public TargetBinder(Binder sample, Consumer5<PropertyChangeEvent,Object,String,T,T> consumer) {
            super(sample);
            this.consumer = consumer;
        }
        public TargetBinder(TargetBinder<T> sample){
            super(sample);
            if( sample!=null ){
                this.consumer = sample.consumer;
                this.consumerAsWeakReference = sample.consumerAsWeakReference;
                this.valueType = sample.valueType;
            }
        }

        private Class<T> valueType;

        /**
         * Возвращает тип значения
         * @return тип значения
         */
        public Class<T> getValueType() {
            return valueType;
        }

        /**
         * Указывает тип занчения
         * @param vtype тип значения
         * @return клон с новыми настройками
         */
        public <U> TargetBinder<U> valueType(Class<U> vtype){
            if( vtype==null )throw new IllegalArgumentException("vtype==null");
            TargetBinder t = new TargetBinder(this);
            t.valueType  = vtype;
            return t;
        }

        //<editor-fold defaultstate="collapsed" desc="clone()">
        @Override
        public TargetBinder<T> clone(){
            return new TargetBinder<T>(this);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="weakTarget()">
        private boolean consumerAsWeakReference = false;

        /**
         * Указывает как хранить ссылку на целевую функцию
         * @return true - как weak ссылка / false - strong ссылка
         */
        public boolean isTargetAsWeak(){ return consumerAsWeakReference; }

        /**
         * Указывает как хранить ссылку на целевую функцию
         * @param weak true - как weak ссылка / false - strong ссылка
         * @return Клон с настройками с настройками
         */
        public TargetBinder<T> weakTarget(boolean weak){
            TargetBinder<T> b = clone();
            b.consumerAsWeakReference = weak;
            return b;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="bind()">
        private Consumer5<PropertyChangeEvent,Object,String,T,T> consumer;

        /**
         * Привязка к целевой функции
         * @return функция
         */
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
                    bean,
                        mRemove,
                        isBeanAsWeak(),
                        getProperties(),
                        (pe,src,name,old,cur)->{
                            if( valueType!=null ){
                                if( (old==null || valueType.isAssignableFrom(old.getClass()))
                                &&  (cur==null || valueType.isAssignableFrom(old.getClass()))
                                ){
                                    consumer.accept(pe,src,name,(T)old,(T)cur);
                                }
                            }else{
                                consumer.accept(pe,src, name, (T)old, (T)cur);
                            }
                        },
                        isTargetAsWeak(),
                        null);

                mAdd.invoke(bean, pcd);

                return pcd;
            } catch (NoSuchMethodException|IllegalAccessException|InvocationTargetException ex) {
                throw new Error("can't bind", ex);
            }
        }
        //</editor-fold>
    }
    //</editor-fold>
}
