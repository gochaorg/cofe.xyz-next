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

package xyz.cofe.gui.swing.menu;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xyz.cofe.collection.CollectionEvent;
import xyz.cofe.collection.CollectionListener;
import xyz.cofe.collection.EventMap;
import xyz.cofe.gui.swing.BasicAction;
import xyz.cofe.gui.swing.KeyboardShortcut;
import xyz.cofe.xml.XmlUtil;

/**
 * Построение меню по xml. <br>
 * Формат XML: <br>
 * <div style="margin-bottom:3mm">
 * <b style="font-size:140%">&lt;menu&gt;</b> - описывает меню / под меню
 * <div style="padding-left:5mm">
 * Атрибуты / дочерние тэги: <br>
 * <b>text</b> - отображаемый текст <br>
 * <b>id</b> - идентификатор <br>
 * Дочерние тэги: <i>line, action, menu</i> <br>
 * Пример:
 * <pre>
 * &lt;menu id="root"&gt;
 *     &lt;menu text="file"&gt;
 *         &lt;action name="file.open"&gt;&lt;/action&gt;
 *         &lt;line /&gt;
 *         &lt;action name="exit"&gt;&lt;/action&gt;
 *     &lt;/menu&gt;
 * &lt;/menu&gt;
 * </pre>
 * </div>
 * </div>
 *
 * <div style="margin-bottom:3mm">
 * <b style="font-size:140%">&lt;line&gt;</b> - горизонтальная линия
 * </div>
 *
 * <div style="margin-bottom:3mm">
 * <b style="font-size:140%">&lt;action&gt;</b> - описывает действие
 * <div style="padding-left:5mm">
 * Обязательный один из указанных атрибутов / дочерние тэги: <br>
 * <b>name</b> - имя действия, если указано, то будет зайдствовано свойств actionByNameResolver для поиска соответ. действия<br>
 * <b>class</b> - имя java класса, будет создан соответ экзмепляр<br>
 * <b>onclick</b> - скрипт который будет вызван для соот. пунктв. Использует actionByOnClickResolver для поиска соответ. действия <br>
 * <br>
 *
 * Не обязательные атрибуты / дочерние тэги: <br>
 *
 * <b>onshow</b> - скрипт который будет вызван при отображении меню <br>
 *
 * <b>target</b> - Цель. Скрипт может быть выполнен без всякой цели, или с определенной целю.
 * Под целю понимается текущий выделенный (сфокусированный javax.swing.FocusManager)
 * объект оопределенного класса.<br>
 *
 * <b>targetVar</b> - Имя переменной в скрипте которая указывает на цель.
 * По умолчанию - <b>target</b>
 * <br>
 *
 * <b>actionVar</b> - Имя переменной в скрипте которая на объект javax.swing.Action для данного пункта меню.
 * По умолчанию - <b>action</b>
 * <br>
 *
 * <b>language / lang</b> - Язык скрипта, по умолчанию JavaScript. Будет передан в actionByOnClickResolver <br>
 *
 * <b>id</b> - Идентификатор<br>
 *
 * <b>text</b> - Текст пункта меню<br>
 *
 * <b>type</b> - тип меню, возможные след. варианты: <b>Default</b> или <b>Checked</b> <br>
 *
 * <b>desc / shortDesc / title</b> - краткое описание <br>
 *
 * <b>desc / longDesc</b> - полное описание <br>
 *
 * <b>key / keyStroke</b> - Комбинация клавиш <br>
 *
 * <b>ico / icoSmall</b> - Малельная иконка <br>
 *
 * <b>ico / icoLarge</b> - Большая иконка <br>
 *
 * <b>iterableTargets</b> - По умолчанию true, - выполнять action (onclick) для каждого элемента <br>
 *
 * <br>
 * Дочерние тэги: <i>keys</i>
 * </div>
 * </div>
 *
 * <div style="margin-bottom:3mm">
 * <b style="font-size:140%">&lt;keys&gt;</b> - описывает комбинации клавиш
 * <div style="padding-left:5mm">
 * Не обязательные атрибуты / дочерние тэги: <br>
 * <b>key / keyStroke</b> - Комбинация клавиш <br>
 * <b>target</b> - Окно/Объект (Класс) владеющее фокусом <br>
 * </div>
 * </div>
 *
 * <div>
 * <b>Комбинация клаиш</b>
 * <div style="padding-left: 5mm">
 * Описывается так: <br>
 * <font style="font-family:monospaced">
 * Комбинация клаиш := модификатор* (typedID | pressedReleasedID) <br>
 * модификатор := shift | control | ctrl | meta | alt | altGraph <br>
 * typedID := typed typedKey <br>
 * typedKey := Строка из одного Unicode символа <br>
 * pressedReleasedID := (pressed | released) key <br>
 * key := имя KeyEvent, т.е. имя следующее за "VK_"
* </font>
* </div>
 * </div>
 *
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class XmlMenuBuilder {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(XmlMenuBuilder.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(XmlMenuBuilder.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(XmlMenuBuilder.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(XmlMenuBuilder.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(XmlMenuBuilder.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(XmlMenuBuilder.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(XmlMenuBuilder.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

//    /**
//     * Парсинг XML и создание соот. меню
//     *
//     * @param ro Ссылка на файл меню
//     * @return Меню
//     */
//    public MenuItem parseXML(ResourceObject ro) {
//        return parseXML(ro.getResourceURL());
//    }

    /**
     * Парсинг XML и создание соот. меню
     * @param url Ссылка на файл меню
     * @return Меню
     */
    public MenuItem parseXML( URL url) {
        if (url == null) {
            throw new IllegalArgumentException("url==null");
        }
        InputStream in = null;
        try {
            try {
                in = url.openStream();
            } catch (IOException ex) {
                logException(ex);
            }
            return parseXML(in);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    logException(ex);
                }
            }
        }
    }

    /**
     * Парсинг XML и создание соот. меню
     * @param file Файл
     * @return Меню
     */
    public MenuItem parseXML(File file){
        try {
            if (file== null) {
                throw new IllegalArgumentException("file==null");
            }
            return parseXML(file.toURI().toURL());
        } catch (MalformedURLException ex) {
            logException(ex);
        }
        return null;
    }

    /**
     * Парсинг XML и создание соот. меню
     * @param input XML файл
     * @return Меню
     */
    public MenuItem parseXML(InputStream input) {
        if (input == null) {
            throw new IllegalArgumentException("input==null");
        }
        Document doc = XmlUtil.parseXml(input);
        return parseXML(doc);
    }

    /**
     * Создает меню по XML.
     * @param xml XML описание меню
     * @return Меню либо null
     */
    public MenuItem parseXML(String xml) {
        if (xml == null) {
            throw new IllegalArgumentException("xml==null");
        }
        Document xdoc = XmlUtil.parseXml(xml);
        Element root = xdoc.getDocumentElement();
        return parseElement(root);
    }

    /**
     * Парсинг XML и создание соот. меню
     * @param xmlDoc XML файл
     * @return Меню
     */
    public MenuItem parseXML(Document xmlDoc) {
        if (xmlDoc == null) {
            throw new IllegalArgumentException("xmlDoc==null");
        }
        Element root = xmlDoc.getDocumentElement();
        return parseElement(root);
    }

    //<editor-fold defaultstate="collapsed" desc="listenTranslation">
    protected boolean listenTranslation = false;

    public boolean isListenTranslation() {
        return listenTranslation;
    }

    public void setListenTranslation(boolean listenTranslation) {
        this.listenTranslation = listenTranslation;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Перевод элементов">
    protected Function<String, String> translate = null;

    public Function<String, String> getTranslate() {
        return translate;
    }

    public void setTranslate(Function<String, String> translate) {
        this.translate = translate;
    }

    protected String translate(String text){
        if( translate!=null ){
            String t = translate.apply(text);
            if( t!=null )return t;
        }
        return text;
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="VisitElement">
    public static class VisitElement{
        protected Element el = null;
        protected Map<String,Object> pushVars = null;

        public VisitElement(){
            this.pushVars = new HashMap<String, Object>();
        }

        public VisitElement(Element el,Map<String,Object> map){
            this.pushVars = map;
            this.el = el;
        }

        public VisitElement(Element el){
            super();
            this.el = el;
        }

        public Element getEl() {
            return el;
        }

        public Map<String, Object> getPushVars() {
            if( pushVars==null )pushVars = new HashMap<String, Object>();
            return pushVars;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="parseElement()">
    public MenuItem parseElement(Element el){
        Stack<VisitElement> stack = new Stack<VisitElement>();
        MenuItem mi = parseElement(el,stack);
        return mi;
    }

    public static class MContainer extends MenuContainer
    implements CollectionListener
    {
        private String listenKey;
        public String getListenKey(){ return listenKey; }
        public void setListenKey(String k){ listenKey = k; }

        private Map listenMap;
        public Map getListenMap(){ return listenMap; }
        public void setListenMap(Map m){ listenMap = m; }

        @Override
        public void collectionEvent( CollectionEvent event ){
            if( listenMap!=null && listenKey!=null ){
                Object v = listenMap.get(listenKey);
                if( v!=null && v instanceof String ){
                    String str = (String)v;
                    setText(str);
                }
            }
        }
    }

    protected MenuItem parseElement(Element el,Stack<VisitElement> stack){
        if (el== null) {
            throw new IllegalArgumentException("el==null");
        }
        if (stack== null) {
            throw new IllegalArgumentException("stack==null");
        }

        VisitElement ve = new VisitElement(el);
        stack.push(ve);

//        ve.getPushVars().put("useTemplate", this.useTemplate);

        try{
//            String useTemplateString = readAttr(el, "useTemplate", null);
//            if( useTemplateString!=null ){
//                if( useTemplateString.equalsIgnoreCase("true") ){
//                    useTemplate = true;
//                }else if( useTemplateString.equalsIgnoreCase("false") ){
//                    useTemplate = false;
//                }
//            }

            String name = el.getNodeName();
            if( name.equalsIgnoreCase("menu") ){
                final MContainer mc = new MContainer();
                String txt = readAttrOrText( el, "text", null );
                if( txt!=null ){
                    //el.hasAttribute("text") ) {
//                    String txt = el.getAttribute("text");
//                    if( useTemplate && this.varResolver!=null ){
//                        txt = xyz.cofe.common.Text.template(txt, varResolver);
//                    }
                    final String text = txt;
//                    if( listenTranslation && translate instanceof TranslateMap){
                    if( listenTranslation &&
                        translate instanceof EventMap
                    ){
//                        final TranslateMap tmap = (TranslateMap)translate;
//                        tmap.getRawMap().onKeyChanged(TranslateMap.TRANSLATED_PREFIX+text, new Runnable() {
//                            @Override
//                            public void run() {
//                                mc.setText(tmap.convert(text));
//                            }
//                        });
                        EventMap emap = (EventMap)translate;
                        emap.addCollectionListener(true,mc);
                    }
                    mc.setText(translate(text));
                }
                String id = readAttrOrText(el, "id", null);
                if( id!=null ){//el.hasAttribute("id") ) {
//                    String id = el.getAttribute("id");
                    mc.setId(id);
                }
                if( el.hasChildNodes() ){
                    NodeList nl = el.getChildNodes() ;
                    for( int i=0; i<nl.getLength(); i++ ){
                        Object o = nl.item(i);
                        if( o instanceof Element ){
                            MenuItem mi = parseElement((Element)o,stack);
                            if( mi!=null ){
                                mi.setParent(mc);
                                mc.getChildren().add(mi);
                            }
                        }
                    }
                }
                return mc;
            }else if( name.equalsIgnoreCase("line") ){
                return new MenuSeparatorItem();
            }else if( name.equalsIgnoreCase("action") ){
                return parseElementAction(el);
            }
            return null;
        }
        finally{
            if( !stack.empty() ){
                stack.pop();
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readAttrOrText">
    public static String readAttr(Element actionElement,String fieldName,String defaultValue){
        if( actionElement.hasAttribute(fieldName) ){
            String text = actionElement.getAttribute(fieldName);
//            boolean ut = useTemplate;
//            if( ut && this.varResolver!=null ){
//                text = xyz.cofe.common.Text.template(text, this.varResolver);
//            }
            return text;
        }
        return defaultValue;
    }

    public static String readText(Element actionElement,String fieldName,String defaultValue){
        for( Node n : XmlUtil.children(actionElement) ){
            if( n instanceof Element ){
                Element e = (Element)n;
                if( e.getNodeName().equals(fieldName) ){
                    String text = e.getTextContent();
                    return text;
                }
            }
        }

        return defaultValue;
    }

    public static String readAttrOrText(Element actionElement,String fieldName,String defaultValue){
        String attr = readAttr(actionElement, fieldName, null);
        if( attr!=null ) {
            return attr;
        }
        String text = readText(actionElement, fieldName, null);
        if( text==null )return defaultValue;
        return text;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="actionByNameResolver">
    protected Function<String, Action> actionByNameResolver = null;

    public Function<String, Action> getActionByNameResolver() {
        return actionByNameResolver;
    }

    public void setActionByNameResolver(Function<String, Action> actionByNameResolver) {
        this.actionByNameResolver = actionByNameResolver;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="OnClick">
    public class OnClick {
        private String script = null;
        private String language = null;
        private Element element = null;

        public OnClick( String script, String language, Element el ){
            this.element = el;
            this.script = script;
            this.language = language;
        }

        public String getScript() {
            return script;
        }

        public String getLanguage() {
            return language;
        }

        public Element getElement() {
            return element;
        }

        public String getName(){
            return readAttrOrText(element, "name", null);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="actionByOnClickResolver">
    protected Function<OnClick, Action> actionByOnClickResolver = null;

    public Function<OnClick, Action> getActionByOnClickResolver() {
        return actionByOnClickResolver;
    }

    public void setActionByOnClickResolver(Function<OnClick, Action> actionByOnClickResolver) {
        this.actionByOnClickResolver = actionByOnClickResolver;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fireCantCreateAction()">
    protected void fireCantCreateAction(Element el){
        String xml = XmlUtil.toXMLString(el);
        logWarning("Не возможно создать action, возможно не указанны необходимые атрибуты ({0}) {1}", "name | class | onclick", xml );
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="RegAction">
    public static class RegAction {
        private Element element;
        private Action action;
        private String name;

        public RegAction( Action action, Element el, String name ){
            this.action = action;
            this.element = el;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Element getElement() {
            return element;
        }

        public Action getAction() {
            return action;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="actionReciver">
    protected Consumer<RegAction> actionReciver = null;

    public Consumer<RegAction> getActionReciver() {
        return actionReciver;
    }

    public void setActionReciver(Consumer<RegAction> actionReciver) {
        this.actionReciver = actionReciver;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="scriptEngineManager">
    private ScriptEngineManager scriptEngineManager = null;

    public ScriptEngineManager getScriptEngineManager() {
        if( scriptEngineManager==null )scriptEngineManager = new ScriptEngineManager();
        return scriptEngineManager;
    }

    public void setScriptEngineManager(ScriptEngineManager scriptEngineManager) {
        this.scriptEngineManager = scriptEngineManager;
    }
//</editor-fold>

    private WeakHashMap<String,ScriptEngine> scriptEngineMap = new WeakHashMap<String, ScriptEngine>();

    //<editor-fold defaultstate="collapsed" desc="parseElementAction()">

    public static class MActionItem
        extends MenuActionItem
        implements CollectionListener
    {
        private String listenKey;
        public String getListenKey(){ return listenKey; }
        public void setListenKey(String k){ listenKey = k; }

        private Map listenMap;
        public Map getListenMap(){ return listenMap; }
        public void setListenMap(Map m){ listenMap = m; }

        @Override
        public void collectionEvent( CollectionEvent event ){
            if( listenMap!=null && listenKey!=null ){
                Object v = listenMap.get(listenKey);
                if( v!=null && v instanceof String ){
                    String str = (String)v;
//                    setText(str);
                    Action a = getAction();
                    if( a!=null ){
                        a.putValue(javax.swing.Action.NAME, str);
                    }
                }
            }
        }
    }

    protected MenuItem parseElementAction(Element el) {
        MActionItem mi = null;
        javax.swing.Action action = null;

        final String actionName = readAttrOrText( el, "name", null );
        String actionClassName = readAttrOrText( el, "class", null );
        String onClickScript = readAttrOrText( el, "onclick", null );
        String onShowScript = readAttrOrText( el, "onshow", null );
        String language = readAttrOrText( el, "language", readAttrOrText( el, "lang", null) );
        String targetClassName = readAttrOrText( el, "target", readAttrOrText( el, "target", null) );
//        String targetVar = readAttrOrText( el, "targetVar", readAttrOrText( el, "targetVar", "target") );
        String targetVar = readAttrOrText( el, "targetVar", "target");
//        String actionVar = readAttrOrText( el, "actionVar", readAttrOrText( el, "actionVar", "action") );
        String actionVar = readAttrOrText( el, "actionVar", "action");

        // Попытка найти по имени
        if( actionName!=null && actionByNameResolver!=null ){
            javax.swing.Action act = actionByNameResolver.apply(actionName);
            if( act!=null ){
                action = act;
                logFiner("fetched action by name={0}", actionName);
            }
        }

        // Попытка найти по имени класса
        if( action==null && actionClassName!=null ){
            try {
                Class clazz = Class.forName(actionClassName);
                Object o = clazz.newInstance();
                if( o instanceof Action ){
                    action = (Action)o;
                    logFiner("created action by className={0}", actionClassName);
                }
            } catch (InstantiationException ex) {
                logException(ex);
            } catch (IllegalAccessException ex) {
                logException(ex);
            } catch (ClassNotFoundException ex) {
                logException(ex);
            }
        }

        // Попытка найти по скрипту
        if( action==null && onClickScript!=null && actionByOnClickResolver!=null ){
            Action act = actionByOnClickResolver.apply(new OnClick(onClickScript, language, el));
            if( act!=null ){
                action = act;
                logFiner("fetched action by onclick={0}", onClickScript);
            }
        }

        Class targetCls = null;
        if( targetClassName!=null ){
            try {
                targetCls = Class.forName(targetClassName);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(XmlMenuBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Попытка создать из описанных данных
        if( action==null && onClickScript!=null ){
            ScriptEngineManager seman = getScriptEngineManager();
            String lang = language==null ? "JavaScript" : language;
            ScriptEngine se = scriptEngineMap.get(lang);
            if( se==null ){
                se = seman.getEngineByName(lang);
                scriptEngineMap.put(lang, se);
            }

            if( se!=null ){
                MenuScriptAction ma = new MenuScriptAction();
                ma.setMenuElement(el);
                ma.setScript(onClickScript);
                ma.setScriptEngine(se);
                ma.setTargetVar(targetVar);
                ma.setTarget(targetCls);
                ma.setActionVar(actionVar);
                ma.setUpdateScript(onShowScript);
                action = ma;

                logFiner("created action {1} by onclick={0}", onClickScript, actionName);
            }
        }

        // Сообщение о не возможности создать элемент
        if( action==null ){
            fireCantCreateAction(el);
            return null;
        }

        if( action instanceof BasicAction && targetCls!=null ){
            BasicAction ba = (BasicAction)action;
            if( ba.getTarget()==null )ba.setTarget(targetCls);
        }

        if( actionReciver!=null )actionReciver.accept(new RegAction(action, el, actionName));

        mi = new MActionItem();
        mi.setAction(action);

        logFiner("created MenuActionItem name={0}, class={1}, onclick={2}", actionName, actionClassName, onClickScript);

        // attrib ID
        String id = readAttrOrText(el, "id", null );
        if( id!=null ){
            mi.setId(id);
            logFiner("set MenuAction id={0}", id);
        }

        // attrib TYPE
        String type = readAttrOrText(el, "type", null);
        if( type!=null ){
            if( MenuActionItem.Type.Checked.name().equalsIgnoreCase(type) ){
                mi.setType(MenuActionItem.Type.Checked);
                logFiner("set MenuAction type={0}", type);
            }else if( MenuActionItem.Type.Default.name().equalsIgnoreCase(type) ){
                mi.setType(MenuActionItem.Type.Default);
                logFiner("set MenuAction type={0}", type);
            }
        }

        // attrib Text
        final String actionText = readAttrOrText( el, "text", null );
        if( actionText!=null ){
            String txt = translate(actionText);
//            if( listenTranslation && translate instanceof TranslateMap ){
//            if( listenTranslation && translate instanceof TranslateMap ){
//                final TranslateMap tmap = (TranslateMap)translate;
//                final javax.swing.Action faction = action;
//                tmap.getRawMap().onKeyChanged(TranslateMap.TRANSLATED_PREFIX+actionText, new Runnable() {
//                    @Override
//                    public void run() {
//                        String ntxt = tmap.convert(actionText);
//                        faction.putValue(javax.swing.Action.NAME, ntxt);
//                    }
//                });
//            }

            if( listenTranslation &&
                translate instanceof EventMap
            ){
                EventMap emap = (EventMap)translate;
                emap.addCollectionListener(true,mi);
            }

            action.putValue(javax.swing.Action.NAME, txt);
            logFiner("set MenuAction text={0} translated={1}", actionText, txt);
        }

        // attrib desc
        String shortDesc = readAttrOrText(el, "desc", null);

        // attrib shortDesc
        shortDesc = readAttrOrText(el, "shortDesc", shortDesc);

        // attrib title
        shortDesc = readAttrOrText(el, "title", shortDesc);
        if( shortDesc!=null ){
            String txt = translate(shortDesc);
            action.putValue(javax.swing.Action.SHORT_DESCRIPTION, txt);

//            if( listenTranslation && translate instanceof TranslateMap ){
//                final String tkey = shortDesc;
//                final javax.swing.Action fact = action;
//                final TranslateMap tmap = (TranslateMap)translate;
//                tmap.getRawMap().onKeyChanged(TranslateMap.TRANSLATED_PREFIX+tkey, new Runnable() {
//                    @Override
//                    public void run() {
//                        String ntxt = tmap.convert(tkey);
//                        fact.putValue(javax.swing.Action.SHORT_DESCRIPTION, ntxt);
//                    }
//                });
//            }

            logFiner("set MenuAction short desc={0}, translated={1}", shortDesc, txt);
        }

        // attrib longDesc
        String longDesc = readAttrOrText(el, "desc", null);
        longDesc = readAttrOrText(el, "longDesc", longDesc);
        if( longDesc!=null ){
            String txt = translate(longDesc);
            action.putValue(javax.swing.Action.LONG_DESCRIPTION, txt);

//            if( listenTranslation && translate instanceof TranslateMap ){
//                final String tkey = longDesc;
//                final javax.swing.Action fact = action;
//                final TranslateMap tmap = (TranslateMap)translate;
//                tmap.getRawMap().onKeyChanged(TranslateMap.TRANSLATED_PREFIX+tkey, new Runnable() {
//                    @Override
//                    public void run() {
//                        String ntxt = tmap.convert(tkey);
//                        fact.putValue(javax.swing.Action.LONG_DESCRIPTION, ntxt);
//                    }
//                });
//            }

            logFiner("set MenuAction long desc={0}, translated={1}", longDesc, txt);
        }

        // attrib keys
        String keyStroke = readAttrOrText( el, "key", null );
        if( keyStroke==null )keyStroke = readAttrOrText( el, "keyStroke", null );
//        if( keyStroke==null )keyStroke = readAttr( el, "keys", null );
        if( keyStroke!=null ){
            KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
            if( ks!=null ){
                action.putValue(javax.swing.Action.ACCELERATOR_KEY, ks);
                logFiner("set MenuAction ACCELERATOR_KEY={0}", ks);
            }
        }

        List<KeyboardShortcut> ksList = new ArrayList<KeyboardShortcut>();
        if( el.hasChildNodes() ){
            NodeList nl = el.getChildNodes();
            for( int i=0; i<nl.getLength(); i++ ){
                Node cn = nl.item(i);
                if( cn instanceof Element ){
                    Element ce = (Element)cn;
                    String tagName = ce.getNodeName();
                    if( tagName.equals("keys") ){
                        String keyStr = null;
                        String target = null;
                        if( ce.hasAttribute("keyStroke") )keyStr = ce.getAttribute("keyStroke");
                        if( ce.hasAttribute("key") )keyStr = ce.getAttribute("key");
                        if( ce.hasAttribute("target") )target = ce.getAttribute("target");

                        if( keyStr==null ){
                            continue;
                        }
                        KeyStroke ks = KeyStroke.getKeyStroke(keyStr);
                        if( ks==null )continue;
                        Class c1 = null;
                        if( target!=null ){
                            try {
                                c1 = Class.forName(target);
                            } catch (ClassNotFoundException ex) {
                                logException(ex);
                            }
                        }
                        KeyboardShortcut kst = new KeyboardShortcut();
                        kst.setTarget(c1);
//                        kst.setIsWindowClass(c2);
                        kst.setKeyStroke(ks);
                        ksList.add(kst);

                        logFiner("set MenuAction KeyboardShortcut={0} target={1}", ks, c1);
                    }
                }
            }
            if( action instanceof BasicAction && ksList.size()>0 ){
                ((BasicAction)action).getKeyboardShortcuts().addAll(ksList);
                logFiner("set MenuAction add keyboard shortcuts count={0}", ksList.size());
            }
        }

        // attrib ico
        String icoString = readAttrOrText(el, "ico", null);
        String smallIcoString = readAttrOrText(el, "icoSmall", icoString);
        String largeIcoString = readAttrOrText(el, "icoLarge", icoString);

        javax.swing.Icon smallIco = readIco(smallIcoString, el);
        javax.swing.Icon largeIco = readIco(largeIcoString, el);

        if( smallIco!=null ){
            action.putValue(javax.swing.Action.SMALL_ICON, smallIco);
            logFiner("set MenuAction small ico {0}", smallIco);
        }
        if( largeIco!=null ){
            action.putValue(javax.swing.Action.LARGE_ICON_KEY, largeIco);
            logFiner("set MenuAction large ico {0}", largeIco);
        }

        logFine( "parsed menu action, name={0}", actionName );
        return mi;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readIco()">
    protected javax.swing.Icon readIco(String icoString,Element el){
//        javax.swing.Icon ico = null;
        if( icoString==null )return null;
//        ico = FileUtil.readResource(icoString, this.getClass(), FileUtil.urlIconReader, FileUtil.fileIconReader);

        Class c = this.getClass();
        java.net.URL u = c.getResource(icoString);
        if( u!=null ){
            xyz.cofe.gui.swing.Icon icon = new xyz.cofe.gui.swing.Icon(u);
            return icon;
        }

//        return ico;
        return null;
    }
    //</editor-fold>
}
