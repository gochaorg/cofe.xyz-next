/*
 * The MIT License
 *
 * Copyright 2014 Kamnev Georgiy (nt.gocha@gmail.com).
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

package xyz.cofe.gui.swing;

import xyz.cofe.ecolls.Pair;
import xyz.cofe.gui.swing.menu.MenuItem;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Менеджер клавиатурных сокращений
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class ShortcutManager extends DefaultKeyboardFocusManager
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(ShortcutManager.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(ShortcutManager.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(ShortcutManager.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(ShortcutManager.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(ShortcutManager.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(ShortcutManager.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(ShortcutManager.class.getName()).log(Level.SEVERE, null, ex);
    }

    //</editor-fold>

    private static ShortcutManager instance = null;

    /**
     * Возвращает экземпляр менеджера
     * @return экземпляр
     */
    public static ShortcutManager get(){
        if( instance!=null )return instance;
        instance = new ShortcutManager();
        return instance;
    }

    /**
     * Устанавливает менеджер клавиатурных комбинаций
     */
    public static void install(){
        Object o =
            ShortcutManager.getCurrentKeyboardFocusManager();

        if( !(o instanceof ShortcutManager) ){
            ShortcutManager.setCurrentKeyboardFocusManager(get());
        }
    }

    @Override
    public boolean dispatchEvent(AWTEvent e) {
        if( e instanceof KeyEvent ){
            if( hook((KeyEvent)e) )return true;
        }
        return super.dispatchEvent(e); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent e) {
//        if( hook(e) )return true;
//        return super.dispatchKeyEvent(e);
//    }

    protected boolean hook(KeyEvent e){
        int eventID = e.getID();
        int code = e.getKeyCode();
        char typeChar = e.getKeyChar();

        if( eventID==KeyEvent.KEY_PRESSED ){
            int modif = 0;
            if( e.isAltDown() ) modif |= KeyEvent.ALT_DOWN_MASK;
            if( e.isControlDown() ) modif |= KeyEvent.CTRL_DOWN_MASK;
            if( e.isMetaDown() ) modif |= KeyEvent.META_DOWN_MASK;
            if( e.isShiftDown() ) modif |= KeyEvent.SHIFT_DOWN_MASK;
            KeyStroke ks = null;
            ks = KeyStroke.getKeyStroke(code, modif, false);
            return hook(ks, e);
        }else if( eventID==KeyEvent.KEY_RELEASED ){
            int modif = 0;
            if( e.isAltDown() ) modif |= KeyEvent.ALT_DOWN_MASK;
            if( e.isControlDown() ) modif |= KeyEvent.CTRL_DOWN_MASK;
            if( e.isMetaDown() ) modif |= KeyEvent.META_DOWN_MASK;
            if( e.isShiftDown() ) modif |= KeyEvent.SHIFT_DOWN_MASK;
            KeyStroke ks = null;
            ks = KeyStroke.getKeyStroke(code, modif, true);
            return hook(ks, e);
        }else if( eventID==KeyEvent.KEY_TYPED ){
            int modif = 0;
            if( e.isAltDown() ) modif |= KeyEvent.ALT_DOWN_MASK;
            if( e.isControlDown() ) modif |= KeyEvent.CTRL_DOWN_MASK;
            if( e.isMetaDown() ) modif |= KeyEvent.META_DOWN_MASK;
            if( e.isShiftDown() ) modif |= KeyEvent.SHIFT_DOWN_MASK;
            KeyStroke ks = null;
            ks = KeyStroke.getKeyStroke(typeChar, modif);
            return hook(ks, e);
        }

        return false;
    }

    //<editor-fold defaultstate="collapsed" desc="allowMultipleActions">
    private boolean allowMultipleActions = false;

    /**
     * Указывает допускается ли вызов нескольких обработчиков
     * @return true - допускается вызов нескольких обработчиков / false - вызывается первый уместный обработчик
     */
    public boolean isAllowMultipleActions() {
        return allowMultipleActions;
    }

    /**
     * Указывает допускается ли вызов нескольких обработчиков
     * @param allowMultipleActions true - допускается вызов нескольких обработчиков 
     * / false - вызывается первый уместный обработчик
     */
    public void setAllowMultipleActions(boolean allowMultipleActions) {
        this.allowMultipleActions = allowMultipleActions;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="actions">
    private Iterable<Action> actions = null;

    /**
     * Указывает обработчики пользоватеских действий
     * @return пользовательские действия
     */
    public Iterable<Action> getActions() {
        return actions;
    }

    /**
     * Указывает обработчики пользоватеских действий
     * @param actions пользовательские действия
     */
    public void setActions(Iterable<Action> actions) {
        this.actions = actions;
    }

    /**
     * Указывает обработчики пользоватеских действий из пункта меню
     * @param miRoot коревой пункт меню
     */
    public void setActionsOf( MenuItem miRoot ){
        this.actions = MenuItem.actionsOf(miRoot);
    }

    /**
     * Указывает обработчики пользоватеских действий из пункта меню
     * @param miRoot корневые элементы меню
     */
    public void setActionsOf( Iterable<MenuItem> miRoot ){
        this.actions = MenuItem.actionsOf(miRoot);
    }
    //</editor-fold>

    private Comparator<Pair<Action,Object>> cmpAct = new Comparator<Pair<Action,Object>>() {
        @Override
        public int compare(Pair<Action,Object> pa1, Pair<Action,Object> pa2) {
            Action a1 = pa1.a();
            Action a2 = pa2.a();

            if( a1==a2 )return 0;

            int cmpByName = ((String)a1.getValue(Action.NAME))
                    .compareTo(((String)a2.getValue(Action.NAME)));

            if( a1 instanceof BasicAction &&
                a2 instanceof BasicAction
            ){
                BasicAction ba1 = (BasicAction)a1;
                BasicAction ba2 = (BasicAction)a2;

                if( ba1.getKeyboardShortcuts().isEmpty() &&
                    ba2.getKeyboardShortcuts().isEmpty() )return cmpByName;

                boolean hasWnd1 = false;
                for( KeyboardShortcut ks : ba1.getKeyboardShortcuts() ){
                    if( ks.getTarget()!=null ){
                        hasWnd1 = true;
                    }
                }
                boolean hasWnd2 = false;
                for( KeyboardShortcut ks : ba2.getKeyboardShortcuts() ){
                    if( ks.getTarget()!=null ){
                        hasWnd2 = true;
                    }
                }

                if( hasWnd1==hasWnd2 )return cmpByName;
                if( hasWnd1 && !hasWnd2 )return -1;
                if( !hasWnd1 && hasWnd2 )return 1;
            }else if( !(a1 instanceof BasicAction) &&
                a2 instanceof BasicAction
            ){
                BasicAction ba2 = (BasicAction)a2;
                if( ba2.getKeyboardShortcuts().isEmpty() )return cmpByName;
                for( KeyboardShortcut ks : ba2.getKeyboardShortcuts() ){
                    if( ks.getTarget()!=null ){
                        return 1;
                    }
                }
            }else if( a1 instanceof BasicAction &&
                !(a2 instanceof BasicAction)
            ){
                BasicAction ba1 = (BasicAction)a1;
                if( ba1.getKeyboardShortcuts().isEmpty() )return cmpByName;
                for( KeyboardShortcut ks : ba1.getKeyboardShortcuts() ){
                    if( ks.getTarget()!=null ){
                        return -1;
                    }
                }
            }

            return cmpByName;
        }
    };

    private Iterable<Pair<Action,Object>> findActions( KeyStroke ks ){
        List<Pair<Action,Object>> res = new ArrayList<Pair<Action,Object>>();

        Iterable<Action> iact = actions;
        if( iact==null )return res;

        List<Action> lacts = new ArrayList<Action>();
        for( Action a : iact ){
            lacts.add(a);
//            logInfo("added to search {0}", a.getValue(Action.NAME));
        }

        for( Action a : lacts ){
//            logInfo("findActions {0}, action={1}", ks, a.getValue(Action.NAME));
            if( a instanceof BasicAction ){
                BasicAction ba = (BasicAction)a;
                Pair<Boolean, Object> matched = matchKeyStroke(ks, ba);
                if( matched.a() ){
//                    logInfo("added findActions {0}, action={1}", ks, a.getValue(Action.NAME));
                    res.add(Pair.<Action, Object>of(a, matched.b()));
                }
            }else{
                if( matchKeyStroke(ks, a) ){
                    res.add(Pair.<Action, Object>of(a, null));
                }
            }
        }

        Collections.sort(res, cmpAct);
        return res;
    }

    private boolean matchKeyStroke(  KeyStroke ks1, KeyStroke ks2 ){
        int mod1 = ks1.getModifiers();
        int mod2 = ks2.getModifiers();

        boolean shift1 =
            ((mod1 & KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK) ||
            ((mod1 & KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK)
            ;
        boolean alt1 =
            ((mod1 & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK) ||
            ((mod1 & KeyEvent.ALT_MASK) == KeyEvent.ALT_MASK)
            ;
        boolean ctrl1 =
            ((mod1 & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) ||
            ((mod1 & KeyEvent.CTRL_MASK) == KeyEvent.CTRL_MASK)
            ;

        boolean shift2 =
            ((mod2 & KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK) ||
            ((mod2 & KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK)
            ;
        boolean alt2 =
            ((mod2 & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK) ||
            ((mod2 & KeyEvent.ALT_MASK) == KeyEvent.ALT_MASK)
            ;
        boolean ctrl2 =
            ((mod2 & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) ||
            ((mod2 & KeyEvent.CTRL_MASK) == KeyEvent.CTRL_MASK)
            ;

        if( shift1!=shift2 )return false;
        if( ctrl1!=ctrl2 )return false;
        if( alt1!=alt2 )return false;

        int ev1 = ks1.getKeyEventType();
        int ev2 = ks2.getKeyEventType();
        if( ev1!=ev2 )return false;

        if( ev1 == KeyEvent.KEY_TYPED ){
            char c1 = ks1.getKeyChar();
            char c2 = ks2.getKeyChar();
            if( c1!=c2 )return false;
        }else if( ev1 == KeyEvent.KEY_PRESSED ){
            int c1 = ks1.getKeyCode();
            int c2 = ks2.getKeyCode();
            if( c1!=c2 )return false;
        }else if( ev1 == KeyEvent.KEY_RELEASED ){
            int c1 = ks1.getKeyCode();
            int c2 = ks2.getKeyCode();
            if( c1!=c2 )return false;
        }

        return true;
    }

    private boolean matchKeyStroke( KeyStroke ks, Action a ){
        Object oks = a.getValue(Action.ACCELERATOR_KEY);
        if( !(oks instanceof KeyStroke ) ){
            return false;
        }

        KeyStroke aks = (KeyStroke)oks;
        return matchKeyStroke(ks, aks);
    }

    private Pair<Boolean,Object> matchKeyStroke( KeyStroke ks, BasicAction a ){
//        logInfo("matchKeyStroke( ks={0} basicaction={1} )", ks, a.getName());
        Class trgt = a instanceof GetTarget ? ((GetTarget)a).getTarget() : null;

        KeyStroke bks = a.getAccelerator();
        if( bks!=null ){
            if( matchKeyStroke(ks, bks) ){
                if( trgt!=null ){
                    Pair<Boolean,Object> res = matchWindowClass(trgt);
                    return res;
                }

                return Pair.of(true, null);
            }
        }

        for( KeyboardShortcut eks1 : a.getKeyboardShortcuts() ){
            KeyStroke eks = eks1.getKeyStroke();
            if( matchKeyStroke(ks, eks) ){
//                logInfo("matched matchKeyStroke( ks={0} eks={2} basicaction={1} )", ks, a.getName(), eks);

                Class wcls = eks1.getTarget();
                wcls = wcls==null ? trgt : null;

                if( wcls!=null ){
                    Pair<Boolean,Object> res = matchWindowClass(wcls);
//                    logInfo("matched window = {0}",res);
                    return res;
                }else{
                    return Pair.of(true, null);
                }
            }
        }

        return Pair.of(false, null);
    }

    private Pair<Boolean,Object> matchWindowClass( Class targetClass ){
//        logInfo("matchWindowClass( {0} )", targetClass);

        Object throughFocus = FocusFinder.findThroughFocus(targetClass);
        if( throughFocus!=null )return Pair.of(true, throughFocus);

        Object throughMouse = FocusFinder.findThroughMouse(targetClass);
        if( throughMouse!=null )return Pair.of(true, throughMouse);

        Component c = this.getFocusOwner();
        Pair<Boolean,Object> byFocus = FocusFinder.matchWindowClass(c, targetClass);

        return Pair.of(false, null);
    }

//    public static Pair<Boolean,Object> matchWindowClass( Component cmpt, Class targetClass ){
////        logInfo("matchWindowClass( {1}, {0} )", targetClass, cmpt.getClass());
//        if( cmpt==null )return new BasicPair<Boolean, Object>(false, null);
//
//        Class ccls = cmpt.getClass();
//        boolean asgn = targetClass.isAssignableFrom(ccls);
//        boolean asgn2 = ccls.isAssignableFrom(targetClass);
//
//        if( asgn ){
//            return new BasicPair<Boolean, Object>(true, cmpt);
//        }
//
//        if( cmpt instanceof FocusFinder.FindSelected ){
//            List l = ((FocusFinder.FindSelected)cmpt).findSelected(targetClass);
//            if( l!=null && !l.isEmpty() ){
//                return new BasicPair<Boolean, Object>(true,l.get(0));
//            }
//        }
//
//        return matchWindowClass(cmpt.getParent(), targetClass);
//    }

    //<editor-fold defaultstate="collapsed" desc="minLastCallTimeout">
    private int minLastCallTimeout = 150;

    public int getMinLastCallTimeout() {
        return minLastCallTimeout;
    }

    public void setMinLastCallTimeout(int minLastCallTimeout) {
        this.minLastCallTimeout = minLastCallTimeout;
    }
//</editor-fold>

    private final WeakHashMap<Action,Long> lastCall = new WeakHashMap<Action, Long>();

    protected boolean hook( KeyStroke keyStroke, KeyEvent keyEvent ){
        logFine("hook( {0} )", keyStroke);

        Long curTime = new Date().getTime();

        Iterable<Pair<Action,Object>> acts = findActions( keyStroke );

        int co = 0;

        if( acts!=null ){
            for( final Pair<Action,Object> pa : acts ){
                if( pa==null )continue;
                final Action a = pa.a();

                if( a==null )continue;
                logFinest("matched action {0}", a.getValue(Action.NAME));

                if( !a.isEnabled() )continue;

                Long lCall = lastCall.get(a);
                if( lCall!=null && minLastCallTimeout>0 ){
                    long diff = curTime - lCall;
//                    System.out.println("call diff = "+diff);

                    if( diff<minLastCallTimeout && diff>=0 ){
                        logFinest( "skip by min last call timeout" );
                        continue;
                    }
                }

                logFine("fire actionPerformed {0}", a.getValue(Action.NAME));

                if( a instanceof SetMatchedComponent ){
                    Object c = pa.b();
                    if( c!=null && c instanceof java.awt.Component ){
                        ((SetMatchedComponent)a).setMatchedComponent((java.awt.Component)c);
                    }
                }

                Runnable r = new Runnable() {
                    public void run() {
                        if( pa.b()!=null ){
                            FocusedObjectActionEvent ae = new FocusedObjectActionEvent(this, 0, "shortcut");
                            ae.setFocusedObject(pa.b());
                            a.actionPerformed(ae);
                        }else{
                            a.actionPerformed(
                                new ActionEvent(this, 0, "shortcut")
                            );
                        }
                    }
                };
                SwingUtilities.invokeLater(r);
                co++;

                lastCall.put(a, curTime);

                if( !allowMultipleActions )break;
            }
        }
        return co>0 ;
    }

    /**
     * Указывает совпавший элемент, актуально для Action которым требуется знать какой элемент совпал
     */
    public interface SetMatchedComponent {
        /**
         * Указывает совпавший элемент
         * @param cmp Совпавший компонент
         */
        void setMatchedComponent( Component cmp );
    }

    /**
     * Событие описывающее целевой объект для которого применяется Action
     */
    public static class FocusedObjectActionEvent
    extends ActionEvent
    implements GetFocusedObject
    {
        public FocusedObjectActionEvent(Object source, int id, String command) {
            super(source, id, command);
        }

        public FocusedObjectActionEvent(Object source, int id, String command, int modifiers) {
            super(source, id, command, modifiers);
        }

        public FocusedObjectActionEvent(Object source, int id, String command, long when, int modifiers) {
            super(source, id, command, when, modifiers);
        }

        private Object focusedObject;

        @Override
        public Object getFocusedObject() {
            return focusedObject;
        }

        public void setFocusedObject(Object focusedObject) {
            this.focusedObject = focusedObject;
        }
    }
}
