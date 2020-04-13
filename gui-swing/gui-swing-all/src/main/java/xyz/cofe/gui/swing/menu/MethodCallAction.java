package xyz.cofe.gui.swing.menu;

import xyz.cofe.gui.swing.al.BasicAction;
import xyz.cofe.gui.swing.al.FocusFinder;
import xyz.cofe.gui.swing.al.GetTarget;
import xyz.cofe.gui.swing.al.ShortcutManager;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Пользовательское действие которые вызывает метод.
 * <p>
 * Метод может не содержать параметер, тогда он будет просто вызван,
 * Либо принимать в качетсве аргумента объект UI который содержит фокус ввода.
 * @author nt.gocha@gmail.com
 * @see ShortcutManager
 * @see BasicAction#getTarget()
 * @see FocusFinder
 */
public class MethodCallAction
    extends BasicAction
    implements GetTarget, UpdateUI
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(MethodCallAction.class.getName());
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
    //</editor-fold>

    /**
     * Конструктор
     */
    public MethodCallAction() {
    }

    /**
     * Конструктор
     * @param name имя действия
     */
    public MethodCallAction( String name ) {
        super(name);
    }

    /**
     * Конструктор
     * @param name имя действия
     * @param own владелей метода
     * @param meth метод для UI действия
     */
    public MethodCallAction( String name, Object own, Method meth ) {
        super(name);
        this.owner = own;
        this.method = meth;
    }

    //<editor-fold defaultstate="collapsed" desc="method">
    protected Method method;

    public Method getMethod() {
        return method;
    }

    public void setMethod( Method method ) {
        this.method = method;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="owner">
    protected Object owner;

    public Object getOwner() {
        return owner;
    }

    public void setOwner( Object owner ) {
        this.owner = owner;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getTargets()">
    protected Collection getTargets(){
        Class targetCls = getTarget();
        if( targetCls==null )return new LinkedHashSet();

        Object trgt = FocusFinder.findThroughFocus(targetCls);
        if( trgt==null )trgt = FocusFinder.findThroughMouse(targetCls);

        if( trgt instanceof Collection ){
            return (Collection)trgt;
        }

        LinkedHashSet res = new LinkedHashSet();
        if( trgt!=null )res.add( trgt );
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="actionPerformed()">
    @Override
    public void actionPerformed( ActionEvent e ) {
        Method m = this.method;
        Object own = this.owner;
        try {
            if( m!=null ){
                Object target = null;
                Object firstTarget = null;
                Collection col = null;

                if( e instanceof ShortcutManager.FocusedObjectActionEvent ){
                    target = ((ShortcutManager.FocusedObjectActionEvent)e).getFocusedObject();
                }else{
                    target = getTargets();
                }

                if( target instanceof Collection ){
                    col = (Collection)target;
                    if( col.isEmpty() ){
                        firstTarget = null;
                    }else{
                        Iterator itr = col.iterator();
                        if( itr.hasNext() ){
                            firstTarget = itr.next();
                        }
                    }
                }else{
                    firstTarget = target;
                }

                Class[] params = m.getParameterTypes();
                if( params.length>0 ){
                    if( params.length==1 ){
                        Class param = params[0];
                        if( firstTarget!=null && param.isAssignableFrom(firstTarget.getClass()) ){
                            m.invoke(own, firstTarget);
                        }
                    }
                }else{
                    m.invoke(own);
                }
            }
        } catch( IllegalAccessException ex ) {
            Logger.getLogger(ReflectMenuBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch( IllegalArgumentException ex ) {
            Logger.getLogger(ReflectMenuBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch( InvocationTargetException ex ) {
            Logger.getLogger(ReflectMenuBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="updateUI()">
    @Override
    public void updateUI() {
        Class targetCls = getTarget();
        if( targetCls==null ){
            setEnabled(true);
            return;
        }

        Collection targets = getTargets();

        if( targets.isEmpty() ){
            setEnabled(false);
        }else{
            setEnabled(true);
        }
    }
    //</editor-fold>
}
