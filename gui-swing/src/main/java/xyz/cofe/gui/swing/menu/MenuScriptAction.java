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

package xyz.cofe.gui.swing.menu;


import xyz.cofe.gui.swing.*;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.w3c.dom.Element;

/**
 * Скрипт на действие меню. <br>
 * Скрипт может быть выполнен без всякой цели, или с определенной целю.
 * Под целю понимается текущий выделенный (сфокусированный javax.swing.FocusManager)
 * объект оопределенного класса.
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class MenuScriptAction extends BasicAction
implements UpdateUI, GetTarget
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(MenuScriptAction.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(MenuScriptAction.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(MenuScriptAction.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(MenuScriptAction.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(MenuScriptAction.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(MenuScriptAction.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(MenuScriptAction.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="script : String">
    private String script;

    /**
     * Указывает исполняемый скрипт
     * @return скрипт
     */
    public String getScript() {
        return script;
    }

    /**
     * Указывает исполняемый скрипт
     * @param script скрипт
     */
    public void setScript(String script) {
        this.script = script;
        logFiner("setScript():\n{0}",script);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="scriptEngine : ScriptEngine">
    private ScriptEngine scriptEngine;

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public void setScriptEngine(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
        logFiner("setScriptEngine():\n{0}",scriptEngine);
    }
//</editor-fold>

    private Element menuElement;

    public Element getMenuElement() {
        return menuElement;
    }

    public void setMenuElement(Element menuElement) {
        this.menuElement = menuElement;
        logFiner("setMenuElement():\n{0}",menuElement);
    }

    //<editor-fold defaultstate="collapsed" desc="targetVar : String">
    private String targetVar;

    /**
     * Указывает имя переменной - цели
     * @return имя переменной - цели
     */
    public String getTargetVar() {
        if( targetVar==null ){
            targetVar = "target";
        }
        return targetVar;
    }

    /**
     * Указывает имя переменной - цели
     * @param targetVar имя переменной - цели
     */
    public void setTargetVar(String targetVar) {
        this.targetVar = targetVar;
        logFiner("setTargetVar():\n{0}",targetVar);
    }
//</editor-fold>

    private String actionVar = null;

    public String getActionVar() {
        if( actionVar==null ){
            if( menuElement!=null ){
                String val = XmlMenuBuilder.readAttrOrText(menuElement, "actionVar", "action");
                if( val==null ){
                    actionVar = "action";
                }else{
                    actionVar = val;
                }
            }else{
                actionVar = "action";
            }
        }
        return actionVar;
    }

    public void setActionVar(String actionVar) {
        this.actionVar = actionVar;
        logFiner("setActionVar():\n{0}",actionVar);
    }

    private String updateScript = null;

    public String getUpdateScript() {
        return updateScript;
    }

    public void setUpdateScript(String updateScript) {
        this.updateScript = updateScript;
        logFiner("setUpdateScript():\n{0}",updateScript);
    }

    private Boolean iterableTargets;

    public boolean isIterableTargets() {
        if( iterableTargets==null ){
            if( menuElement!=null ){
                String val = XmlMenuBuilder.readAttrOrText(menuElement, "iterableTargets", "true");
                if( val==null ){
                    iterableTargets = true;
                }else{
                    iterableTargets = val.equalsIgnoreCase("true");
                }
            }else{
                iterableTargets = true;
            }
        }
        return iterableTargets;
    }

    public void setIterableTargets(boolean iterableTargets) {
        this.iterableTargets = iterableTargets;
    }

    protected void runScript(String script, Object targetObject){
        if( scriptEngine==null ){
            logFiner("scriptEngine is null");
            return;
        }
        if( script==null ){
            logFiner("script is null");
            return;
        }

        Bindings bnd = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        if( bnd==null ){
            logFiner("bindings not exists");
            return;
        }

        Class target = getTarget();

        if( target!=null ){
            String tvar = getTargetVar();
            if( tvar==null ){
                logFiner( "targetVar is null" );
                return;
            }

            Object targetObj = targetObject;
            targetObj = targetObj!=null ? targetObject : FocusFinder.findThroughFocus(target);
            if( targetObj==null )targetObj = FocusFinder.findThroughMouse(target);
            if( targetObj==null ){
                logFiner( "target not found" );
                return;
            }

            bnd.put(tvar, targetObj);

            if( targetObj instanceof Iterable && isIterableTargets() ){
                for( Object t : ((Iterable)targetObj) ){
                    if( t==null )continue;

                    if( actionVar!=null )bnd.put(actionVar, this);
                    bnd.put(tvar, t);

                    try {
                        logFiner("runScript:\n{0}",script);
                        scriptEngine.eval(script);
                    } catch (ScriptException ex) {
                        logException(ex);
                    }
                }
            }else{
                bnd.put(tvar, targetObj);
                if( actionVar!=null )bnd.put(actionVar, this);

                try {
                    logFiner("runScript:\n{0}",script);
                    scriptEngine.eval(script);
                } catch (ScriptException ex) {
                    logException(ex);
                }
            }
        }else{
            if( actionVar!=null )bnd.put(actionVar, this);

            try {
                logFiner("runScript:\n{0}",script);
                scriptEngine.eval(script);
            } catch (ScriptException ex) {
                logException(ex);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logFine("actionPerformed:\n{0}",script);
        if( e instanceof GetFocusedObject ){
            runScript(script, ((GetFocusedObject)e).getFocusedObject() );
        }else{
            runScript(script, null);
        }
    }

    private Boolean autoUpdateEnabled;

    public boolean isAutoUpdateEnabled() {
        if( autoUpdateEnabled==null ){
            if( menuElement!=null ){
                String val = XmlMenuBuilder.readAttrOrText(menuElement, "autoUpdateEnabled", "true");
                if( val==null ){
                    autoUpdateEnabled = true;
                }else{
                    autoUpdateEnabled = val.equalsIgnoreCase("true");
                }
            }else{
                autoUpdateEnabled = true;
            }
        }
        return autoUpdateEnabled;
    }

    public void setAutoUpdateEnabled(boolean autoUpdateEnabled) {
        this.autoUpdateEnabled = autoUpdateEnabled;
    }

    public void updateUI(){
        logFine("updateUI:\n{0}",updateScript);

        Object targetObj = null;

        Class target = getTarget();

        if( target!=null ){
            String tvar = getTargetVar();
            if( tvar==null ){
                logFiner( "targetVar is null" );
                if( isAutoUpdateEnabled() ){
                    setEnabled(false);
                }
            }else{
                Object trgt = FocusFinder.findThroughFocus(target);
                if( trgt==null )trgt = FocusFinder.findThroughMouse(target);
                if( trgt==null ){
                    logFiner( "target not found" );
                    setEnabled(false);
                }else{
                    if( trgt instanceof Iterable && isIterableTargets() ){
                        if( trgt instanceof Collection ){
                            if( !((Collection)trgt).isEmpty() ){
                                targetObj = trgt;
                                setEnabled(true);
                            }
                        }else{
                            targetObj = trgt;
                            setEnabled(true);
                        }
                    }else{
                        targetObj = trgt;
                        setEnabled(true);
                    }
                }
            }
        }else{
            setEnabled(true);
        }

        runScript(updateScript,targetObj);
    }
}
