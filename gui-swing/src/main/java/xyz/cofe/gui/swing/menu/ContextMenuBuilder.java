/*
 * The MIT License
 *
 * Copyright 2016 Kamnev Georgiy (nt.gocha@gmail.com).
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

package xyz.cofe.gui.swing.menu;


import xyz.cofe.gui.swing.*;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;

/**
 * Создание контекстного меню
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class ContextMenuBuilder {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ContextMenuBuilder.class.getName());
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
    
    protected Iterable<Action> actions;

    public Iterable<Action> getActions() {
        return actions;
    }

    public void setActions(Iterable<Action> actions) {
        this.actions = actions;
    }
    
    public ContextMenuBuilder actionsOf( MenuItem mi ){
        if( mi==null ){
            actions = null;
            return this;
        }
        actions = MenuItem.actionsOf(mi);
        return this;
    }
    
    protected Predicate<Action> actionFilter;

    public Predicate<Action> getActionFilter() {
        return actionFilter;
    }

    public void setActionFilter(Predicate<Action> actionFilter) {
        this.actionFilter = actionFilter;
    }
    
    public ContextMenuBuilder targets( Class ... targets ){
        if( targets==null || targets.length==0 ){
            this.actionFilter = null;
            return this;
        }
        
        this.actionFilter = BasicAction.Filter.targetAssignableFrom(targets);
        return this;
    }

    protected Iterable<MenuItem> menuItems;
    protected Predicate<MenuItem> menuFilter;
    
    protected Function<Action,MenuItem> menuFromAction = new Function<Action,MenuItem>() {
        @Override
        public MenuItem apply(Action act) {
            if( act==null )return null;
            
            MenuActionItem ma = new MenuActionItem(act);
            
            if( act instanceof MenuScriptAction ){
                String type = XmlMenuBuilder.readAttrOrText(
                    ((MenuScriptAction)act).getMenuElement(),"type", "Default"
                );
                if( type!=null ){
                    if( MenuActionItem.Type.Checked.name().equalsIgnoreCase(type) ){
                        ma.setType(MenuActionItem.Type.Checked);
                    }
                }
            }
            
            return ma;
        }
    };
    
    protected MenuItem menu(){
        MenuContainer mc = new MenuContainer(){
            @Override
            protected void assignParentToChild(MenuItem child) {
            }
        };
        
        if( menuItems!=null ){
            for( MenuItem mi : menuItems ){
                if( mi==null )continue;
                if( menuFilter!=null && menuFilter.test(mi) ){
                    MenuItem.addChild(mc, mi);
                }else{
                    MenuItem.addChild(mc, mi);
                }
            }
        }
        
        Iterable<Action> acts = actions;
        if( acts!=null ){
            for( Action a : acts ){
                if( a==null )continue;
                if( actionFilter!=null && !actionFilter.test(a)){
                    continue;
                }
                MenuItem mi = menuFromAction.apply(a);
                if( mi==null )continue;
                MenuItem.addChild(mc, mi);
            }
        }
        
        return mc;
    }
    
    public ObserverMenuPopup popup(JComponent popupOwner){
        ObserverMenuPopup popup = new ObserverMenuPopup();
        popup.setMenu(menu());
        if( popupOwner!=null ){
            popupOwner.setComponentPopupMenu(popup);
        }
        return popup;
    }
}
