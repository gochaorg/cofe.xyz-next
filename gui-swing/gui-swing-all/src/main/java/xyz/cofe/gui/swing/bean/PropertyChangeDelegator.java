package xyz.cofe.gui.swing.bean;

import xyz.cofe.fn.Fn2;
import xyz.cofe.fn.Fn3;
import xyz.cofe.fn.Fn4;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Делегирует событие PropertyChangeEvent в Func1/Fn2/Fn3/Fn4/Reciver/Runnable
 * @author Kamnev Georgiy
 */
public class PropertyChangeDelegator implements AutoCloseable, PropertyChangeListener
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(PropertyChangeDelegator.class.getName());

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
        logger.entering(PropertyChangeDelegator.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(PropertyChangeDelegator.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(PropertyChangeDelegator.class.getName(), method, result);
    }
    //</editor-fold>

    protected volatile Object bean;
    protected volatile Method removeListener;
    protected volatile Predicate<String> propertyNameFilter;
    protected volatile Object target;

    //<editor-fold defaultstate="collapsed" desc="PropertyChangeDelegator()">
    /**
     * Конструктор
     * @param bean прослушиваемый бин
     * @param mremove метод для удаления слушателя
     * @param beanAsWeak true - weak ссылка прослушиваемый бин / false - stong ссылка
     * @param propertyNameFilter фильтр прослушиваемых свойств
     * @param consumer подписчик принимающий события
     * @param consumerAsWeak true - подписичк храниться как weak ссылка / false - strong ссылка
     */
    public PropertyChangeDelegator(
        Object bean,
        Method mremove,
        boolean beanAsWeak,
        Predicate<String> propertyNameFilter,
        Fn4<Object,String,Object,Object,Object> consumer,
        boolean consumerAsWeak
    ){
        if( bean==null )throw new IllegalArgumentException("bean == null");
        if( mremove==null )throw new IllegalArgumentException("mremove == null");
        if( consumer==null )throw new IllegalArgumentException("consumer == null");

        this.bean = beanAsWeak ? new WeakReference<>(bean) : bean;
        this.removeListener = mremove;
        this.propertyNameFilter = propertyNameFilter;
        this.target = consumerAsWeak ? new WeakReference( consumer ) : consumer;
    }

    /**
     * Конструктор
     * @param bean прослушиваемый бин
     * @param mremove метод для удаления слушателя
     * @param beanAsWeak true - weak ссылка прослушиваемый бин / false - stong ссылка
     * @param propertyNameFilter фильтр прослушиваемых свойств
     * @param consumer подписчик принимающий события
     * @param consumerAsWeak true - подписичк храниться как weak ссылка / false - strong ссылка
     */
    public PropertyChangeDelegator(
        Object bean,
        Method mremove,
        boolean beanAsWeak,
        Predicate<String> propertyNameFilter,
        Fn3<String,Object,Object,Object> consumer,
        boolean consumerAsWeak
    ){
        if( bean==null )throw new IllegalArgumentException("bean == null");
        if( mremove==null )throw new IllegalArgumentException("mremove == null");
        if( consumer==null )throw new IllegalArgumentException("consumer == null");

        this.bean = beanAsWeak ? new WeakReference(bean) : bean;
        this.removeListener = mremove;
        this.propertyNameFilter = propertyNameFilter;
        this.target = consumerAsWeak ? new WeakReference( consumer ) : consumer;
    }

    /**
     * Конструктор
     * @param bean прослушиваемый бин
     * @param mremove метод для удаления слушателя
     * @param beanAsWeak true - weak ссылка прослушиваемый бин / false - stong ссылка
     * @param propertyNameFilter фильтр прослушиваемых свойств
     * @param consumer подписчик принимающий события
     * @param consumerAsWeak true - подписичк храниться как weak ссылка / false - strong ссылка
     */
    public PropertyChangeDelegator(
        Object bean,
        Method mremove,
        boolean beanAsWeak,
        Predicate<String> propertyNameFilter,
        Fn2<Object,Object,Object> consumer,
        boolean consumerAsWeak
    ){
        if( bean==null )throw new IllegalArgumentException("bean == null");
        if( mremove==null )throw new IllegalArgumentException("mremove == null");
        if( consumer==null )throw new IllegalArgumentException("consumer == null");

        this.bean = beanAsWeak ? new WeakReference(bean) : bean;
        this.removeListener = mremove;
        this.propertyNameFilter = propertyNameFilter;
        this.target = consumerAsWeak ? new WeakReference( consumer ) : consumer;
    }

    /**
     * Конструктор
     * @param bean прослушиваемый бин
     * @param mremove метод для удаления слушателя
     * @param beanAsWeak true - weak ссылка прослушиваемый бин / false - stong ссылка
     * @param propertyNameFilter фильтр прослушиваемых свойств
     * @param consumer подписчик принимающий события
     * @param consumerAsWeak true - подписичк храниться как weak ссылка / false - strong ссылка
     */
    public PropertyChangeDelegator(
        Object bean,
        Method mremove,
        boolean beanAsWeak,
        Predicate<String> propertyNameFilter,
        Function<PropertyChangeEvent,Object> consumer,
        boolean consumerAsWeak
    ){
        if( bean==null )throw new IllegalArgumentException("bean == null");
        if( mremove==null )throw new IllegalArgumentException("mremove == null");
        if( consumer==null )throw new IllegalArgumentException("consumer == null");

        this.bean = beanAsWeak ? new WeakReference(bean) : bean;
        this.removeListener = mremove;
        this.propertyNameFilter = propertyNameFilter;
        this.target = consumerAsWeak ? new WeakReference( consumer ) : consumer;
    }

    /**
     * Конструктор
     * @param bean прослушиваемый бин
     * @param mremove метод для удаления слушателя
     * @param beanAsWeak true - weak ссылка прослушиваемый бин / false - stong ссылка
     * @param propertyNameFilter фильтр прослушиваемых свойств
     * @param consumer подписчик принимающий события
     * @param consumerAsWeak true - подписичк храниться как weak ссылка / false - strong ссылка
     */
    public PropertyChangeDelegator(
        Object bean,
        Method mremove,
        boolean beanAsWeak,
        Predicate<String> propertyNameFilter,
        Consumer consumer,
        boolean consumerAsWeak
    ){
        if( bean==null )throw new IllegalArgumentException("bean == null");
        if( mremove==null )throw new IllegalArgumentException("mremove == null");
        if( consumer==null )throw new IllegalArgumentException("consumer == null");

        this.bean = beanAsWeak ? new WeakReference(bean) : bean;
        this.removeListener = mremove;
        this.propertyNameFilter = propertyNameFilter;
        this.target = consumerAsWeak ? new WeakReference( consumer ) : consumer;
    }

    /**
     * Конструктор
     * @param bean прослушиваемый бин
     * @param mremove метод для удаления слушателя
     * @param beanAsWeak true - weak ссылка прослушиваемый бин / false - stong ссылка
     * @param propertyNameFilter фильтр прослушиваемых свойств
     * @param consumer подписчик принимающий события
     * @param consumerAsWeak true - подписичк храниться как weak ссылка / false - strong ссылка
     */
    public PropertyChangeDelegator(
        Object bean,
        Method mremove,
        boolean beanAsWeak,
        Predicate<String> propertyNameFilter,
        Runnable consumer,
        boolean consumerAsWeak
    ){
        if( bean==null )throw new IllegalArgumentException("bean == null");
        if( mremove==null )throw new IllegalArgumentException("mremove == null");
        if( consumer==null )throw new IllegalArgumentException("consumer == null");

        this.bean = beanAsWeak ? new WeakReference(bean) : bean;
        this.removeListener = mremove;
        this.propertyNameFilter = propertyNameFilter;
        this.target = consumerAsWeak ? new WeakReference( consumer ) : consumer;
    }

    /**
     * Конструктор
     * @param bean прослушиваемый бин
     * @param mremove метод для удаления слушателя
     * @param beanAsWeak true - weak ссылка прослушиваемый бин / false - stong ссылка
     * @param propertyNameFilter фильтр прослушиваемых свойств
     * @param consumer подписчик принимающий события
     * @param consumerAsWeak true - подписичк храниться как weak ссылка / false - strong ссылка
     * @param convertor конвертор
     */
    public PropertyChangeDelegator(Object bean,
                                   Method mremove,
                                   boolean beanAsWeak,
                                   Predicate<String> propertyNameFilter,
                                   Object consumer,
                                   boolean consumerAsWeak,
                                   Function convertor
    ){
        if( bean==null )throw new IllegalArgumentException("bean == null");
        if( mremove==null )throw new IllegalArgumentException("mremove == null");
        if( consumer==null )throw new IllegalArgumentException("consumer == null");

        this.bean = beanAsWeak ? new WeakReference(bean) : bean;
        this.removeListener = mremove;
        this.propertyNameFilter = propertyNameFilter;
        this.target = consumerAsWeak ? new WeakReference( consumer ) : consumer;
        this.convertor = convertor;
    }

    /**
     * Конструктор
     * @param bean прослушиваемый бин
     * @param mremove метод для удаления слушателя
     * @param beanAsWeak true - weak ссылка прослушиваемый бин / false - stong ссылка
     * @param propertyNameFilter фильтр прослушиваемых свойств
     * @param consumer подписчик принимающий события
     * @param consumerAsWeak true - подписичк храниться как weak ссылка / false - strong ссылка
     */
    public PropertyChangeDelegator(Object bean,
                                   Method mremove,
                                   boolean beanAsWeak,
                                   Predicate<String> propertyNameFilter,
                                   Object consumer,
                                   boolean consumerAsWeak
    ){
        if( bean==null )throw new IllegalArgumentException("bean == null");
        if( mremove==null )throw new IllegalArgumentException("mremove == null");
        if( consumer==null )throw new IllegalArgumentException("consumer == null");

        this.bean = beanAsWeak ? new WeakReference(bean) : bean;
        this.removeListener = mremove;
        this.propertyNameFilter = propertyNameFilter;
        this.target = consumerAsWeak ? new WeakReference( consumer ) : consumer;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="unsubscribe()">
    /**
     * Отписывается от уведомлений
     */
    protected void unsubscribe(){
        synchronized(this){
            if( bean!=null && removeListener!=null ){
                Object bn = bean;
                if( bean instanceof WeakReference ){
                    bn = ((WeakReference)bean).get();
                    if( bn==null ){
                        bean = null;
                    }
                }

                try {
                    removeListener.invoke(bn, this);
                } catch (IllegalAccessException | IllegalArgumentException |InvocationTargetException ex) {
                    Logger.getLogger(PropertyChangeDelegator.class.getName()).log(Level.SEVERE, null, ex);
                }

                bean = null;
                removeListener = null;
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="releaseTarget()">
    /**
     * Освобождает ссылку на получатель собщений
     */
    protected void releaseTarget(){
        synchronized(this){
            if( target!=null ){
                target = null;
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="releasePropertyNameFilter()">
    /**
     * Освобождает ссылка на фильтр
     */
    protected void releasePropertyNameFilter(){
        synchronized(this){
            if( propertyNameFilter!=null ){
                propertyNameFilter = null;
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="closed : boolean">
    /**
     * Возвращает отписан или еще нет от получения уведомлений
     * @return true - объект уже не принимает сообщение
     */
    public boolean isClosed(){
        synchronized(this){
            return bean == null;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="close()">
    /**
     * Отписка от получения уведомлений
     * @throws Exception по идее не должно произойти
     */
    @Override
    public void close() throws Exception {
        synchronized(this){
            unsubscribe();
            releaseTarget();
            releasePropertyNameFilter();
            releaseConvertor();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="convertor">
    /**
     * конвертор значения
     */
    protected volatile Function convertor;

    /**
     * Возвращает конвертор значения свойства
     * @return конвертор
     */
    public Function getConvertor() {
        synchronized(this){
            return convertor;
        }
    }

    /**
     * Указывает конвертор значения
     * @param convertor конвертор
     */
    public void setConvertor(Function convertor) {
        synchronized(this){
            this.convertor = convertor;
        }
    }

    /**
     * Указывает конвертор значения
     * @param conv конвертор
     * @return self ссылка
     */
    public PropertyChangeDelegator convertor(Function conv){
        setConvertor(conv);
        return this;
    }

    /**
     * Освобождает ссылка на конвертор
     */
    protected void releaseConvertor(){
        setConvertor(null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="preventRecursion : boolean">
    private volatile boolean preventRecursion = true;

    /**
     * Указывает предотврощать рекурсивный вызов
     * @return true - предотврощать
     */
    public boolean isPreventRecursion() {
        synchronized(this){
            return preventRecursion;
        }
    }

    /**
     * Указывает предотврощать рекурсивный вызов
     * @param preventRecursion true - предотврощать
     */
    public void setPreventRecursion(boolean preventRecursion) {
        synchronized(this){
            this.preventRecursion = preventRecursion;
        }
    }

    /**
     * Предотврощать рекурсивный вызов
     * @param prevent true - предотврощать
     * @return self ссылки
     */
    public PropertyChangeDelegator preventRecursion(boolean prevent){
        setPreventRecursion(prevent);
        return this;
    }
    //</editor-fold>

    private final AtomicInteger preventRecursionCall = new AtomicInteger(0);

    /**
     * Вызывается при получении уведомления
     * @param evt уведомление
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if( evt==null )return;
        synchronized(this){
            int lvl = preventRecursionCall.incrementAndGet();
            if( lvl>1 && preventRecursion ){
                return;
            }

            try{
                if( bean==null || removeListener==null || target==null )return;

                Object trgt = target instanceof WeakReference ?
                    ((WeakReference)target).get() : target;

                if( (bean instanceof WeakReference && ((WeakReference)bean).get()==null )
                    ||  (trgt==null ) )
                {
                    try {
                        close();
                    } catch (Exception ex) {
                        Logger.getLogger(PropertyChangeDelegator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return;
                }

                Object src = evt.getSource();
                String pname = evt.getPropertyName();
                Object oldv = evt.getOldValue();
                Object newv = evt.getNewValue();
                if( convertor!=null ){
                    oldv = convertor.apply(oldv);
                    newv = convertor.apply(newv);
                }

                if( propertyNameFilter!=null ){
                    if( !propertyNameFilter.test(pname) )return;
                }

                if( trgt instanceof Fn4 ){
                    ((Fn4)trgt).apply(src, pname, oldv, newv);
                }

                if( trgt instanceof Fn3 ){
                    ((Fn3) trgt).apply(pname, oldv, newv);
                }

                if( trgt instanceof Fn2 ){
                    ((Fn2) trgt).apply(oldv, newv);
                }

                if( trgt instanceof Fn2 ){
                    ((Fn2) trgt).apply(oldv, newv);
                }

                if( trgt instanceof Function ){
                    PropertyChangeEvent pevnt = evt;
                    if( convertor!=null ){
                        pevnt = new PropertyChangeEvent(
                            src,
                            pname,
                            convertor.apply(oldv),
                            convertor.apply(newv)
                        );
                    }
                    ((Function) trgt).apply(pevnt);
                }

                if( trgt instanceof Consumer ){
                    ((Consumer) trgt).accept(newv);
                }

                if( trgt instanceof Runnable ){
                    ((Runnable) trgt).run();
                }
            }finally{
                preventRecursionCall.decrementAndGet();
            }
        }
    }
}
