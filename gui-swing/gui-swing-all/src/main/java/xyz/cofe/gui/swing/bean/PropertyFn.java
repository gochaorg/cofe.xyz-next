package xyz.cofe.gui.swing.bean;

import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import xyz.cofe.fn.Fn4;
import xyz.cofe.text.Text;

/**
 * Общая поддержа работы со свойствами (bean)
 * @author Kamnev Georgiy
 */
public class PropertyFn {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(PropertyFn.class.getName());

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
        logger.entering(PropertyFn.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(PropertyFn.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(PropertyFn.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Добавляет подписчика на изменение свойств объекта. <br>
     * Целевой объект добавляется как weak ссылка, ПОдписчик как hard ссылка
     * @param bean Целевой объект
     * @param listenFn Подписчик
     * @param propertyNames свойства
     * @return Отписка от событий целевого объекта
     */
    public static AutoCloseable onPropertyChanged(
        final Object bean,
        final Consumer listenFn,
        final String ... propertyNames
    ){
        if( propertyNames!=null ){
            for( int pi=0; pi<propertyNames.length; pi++ ){
                if( propertyNames[pi]==null ){
                    throw new IllegalArgumentException("propertyNames["+pi+"] == null");
                }
            }
        }
        return onPropertyChanged(bean,
            propertyNames==null ? null : Text.Predicates.in(false,propertyNames),
            listenFn, true, false);
    }

    /**
     * Добавляет подписчика на изменение свойств объекта. <br>
     * Целевой объект добавляется как weak ссылка, ПОдписчик как hard ссылка
     * @param bean Целевой объект
     * @param propertyName свойство
     * @param listenFn Подписчик
     * @return Отписка от событий целевого объекта
     */
    public static AutoCloseable onPropertyChanged(final Object bean, final String propertyName, final Consumer listenFn){
        return onPropertyChanged(bean, listenFn, propertyName);
    }

    /**
     * Добавляет подписчика на изменение свойств объекта. <br>
     * Целевой объект добавляется как weak ссылка, ПОдписчик как hard ссылка
     * @param bean Целевой объект
     * @param propertyName Перечень свойств
     * @param listenFn Подписчик
     * @return Отписка от событий целевого объекта
     */
    public static AutoCloseable onPropertyChanged(
        final Object bean,
        final Predicate<String> propertyName,
        final Consumer listenFn
    ){
        return onPropertyChanged(bean, propertyName, listenFn, true, false);
    }

    /**
     * Добавляет подписчика на изменение свойств объекта
     * @param bean Целевой объект
     * @param propertyName Перечень свойств
     * @param listenFn Подписчик
     * @param beanAsWeak Целевой объект добавлен как weak ссылка
     * @param listenAsWeak Подписчик добавлен как weak ссылка
     * @return Отписка от событий целевого объекта
     */
    public static AutoCloseable onPropertyChanged(
        final Object bean,
        final Predicate<String> propertyName,
        final Consumer listenFn,
        boolean beanAsWeak,
        boolean listenAsWeak
    ){
        if( bean==null )throw new IllegalArgumentException("bean == null");
        if( propertyName==null )throw new IllegalArgumentException("propertyName == null");
        if( listenFn==null )throw new IllegalArgumentException("listenFn == null");

        Class cls = bean.getClass();

        try {
            final Method mAdd = cls.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
            final Method mRemove = cls.getMethod("removePropertyChangeListener", PropertyChangeListener.class);

            PropertyChangeDelegator pcd = new PropertyChangeDelegator(
                bean, mRemove, beanAsWeak, propertyName, listenFn, listenAsWeak);

            mAdd.invoke(bean, pcd);

            return pcd;
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(PropertyFn.class.getName()).log(
                Level.WARNING,
                "bean("+cls+") not contains methods: "
                    + "addPropertyChangeListener, removePropertyChangeListener "
                    + ", return dummy closeable",
                ex);
            return new AutoCloseable() {
                @Override
                public void close() throws Exception {
                }
            };
        } catch (SecurityException ex) {
            Logger.getLogger(PropertyFn.class.getName()).log(
                Level.SEVERE,
                "bean("+cls+") fail fetch methods: "
                    + "addPropertyChangeListener, removePropertyChangeListener "
                    + ", SecurityException:"+ex.getMessage()
                    + ", return dummy closeable",
                ex);
            return new AutoCloseable() {
                @Override
                public void close() throws Exception {
                }
            };
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PropertyFn.class.getName()).log(
                Level.SEVERE,
                "bean("+cls+") fail attach listener: "
                    + ", "+ex.getClass().getSimpleName()+":"+ex.getMessage()
                    + ", return dummy closeable",
                ex);
            return new AutoCloseable() {
                @Override
                public void close() throws Exception {
                }
            };
        }
    }


    /**
     * Добавляет подписчика на изменение свойств объекта
     * @param bean Целевой объект
     * @param propertyName Перечень свойств
     * @param listenFn Подписчик - функция ( Целевой объект, Свойство, Пред значение, Текущее значение )
     * @param beanAsWeak Целевой объект добавлен как weak ссылка
     * @param listenAsWeak Подписчик добавлен как weak ссылка
     * @return Отписка от событий целевого объекта
     */
    public static AutoCloseable onPropertyChanged(
        final Object bean,
        final Predicate<String> propertyName,
        final Fn4<Object,String,Object,Object,Object> listenFn,
        boolean beanAsWeak, boolean listenAsWeak
    )
    {
        if( bean==null )throw new IllegalArgumentException("bean == null");
        if( propertyName==null )throw new IllegalArgumentException("propertyName == null");
        if( listenFn==null )throw new IllegalArgumentException("listenFn == null");

        Class cls = bean.getClass();

        try {
            final Method mAdd = cls.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
            final Method mRemove = cls.getMethod("removePropertyChangeListener", PropertyChangeListener.class);

            PropertyChangeDelegator pcd = new PropertyChangeDelegator(
                bean, mRemove, beanAsWeak, propertyName, listenFn, listenAsWeak);

            mAdd.invoke(bean, pcd);

            return pcd;
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(PropertyFn.class.getName()).log(
                Level.WARNING,
                "bean("+cls+") not contains methods: "
                    + "addPropertyChangeListener, removePropertyChangeListener "
                    + ", return dummy closeable",
                ex);
            return new AutoCloseable() {
                @Override
                public void close() throws Exception {
                }
            };
        } catch (SecurityException ex) {
            Logger.getLogger(PropertyFn.class.getName()).log(
                Level.SEVERE,
                "bean("+cls+") fail fetch methods: "
                    + "addPropertyChangeListener, removePropertyChangeListener "
                    + ", SecurityException:"+ex.getMessage()
                    + ", return dummy closeable",
                ex);
            return new AutoCloseable() {
                @Override
                public void close() throws Exception {
                }
            };
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(PropertyFn.class.getName()).log(
                Level.SEVERE,
                "bean("+cls+") fail attach listener: "
                    + ", "+ex.getClass().getSimpleName()+":"+ex.getMessage()
                    + ", return dummy closeable",
                ex);
            return new AutoCloseable() {
                @Override
                public void close() throws Exception {
                }
            };
        }
    }
}
