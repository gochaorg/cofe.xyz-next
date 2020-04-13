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

package xyz.cofe.gui.swing.properties.editor;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import xyz.cofe.collection.Pointer;
import xyz.cofe.gui.swing.properties.Icons;
import xyz.cofe.gui.swing.properties.Property;
import xyz.cofe.gui.swing.properties.PropertyValue;
import xyz.cofe.gui.swing.properties.SetPropertyEditorOpts;
import xyz.cofe.gui.swing.tree.TreeTableNode;
import xyz.cofe.gui.swing.tree.TreeTableNodeValue;
import xyz.cofe.text.Text;
import xyz.cofe.text.lex.LexerUtil;
import xyz.cofe.text.lex.ListLexer;
import xyz.cofe.text.lex.Token;
import xyz.cofe.text.lex.Identifier;
import xyz.cofe.text.lex.IdentifierParser;
import xyz.cofe.text.lex.Keyword;
import xyz.cofe.text.lex.KeywordsParser;
import xyz.cofe.text.lex.TextConst;
import xyz.cofe.text.lex.TextConstParser;
import xyz.cofe.text.lex.WhiteSpace;
import xyz.cofe.text.lex.WhiteSpaceParser;

/**
 * Редактор ComboBox для текстовых данных. <p>
 * Примеры использования: <p>
 *
 * <b>Пример с использование спика значений</b> <p>
 *
 * <code style="font-size: 12pt;">
 * &#64;UiBean( <br>
 * &nbsp; <i style="color: #006000">// Указываем редактор </i><br>
 * &nbsp; propertyEditor = ComboBoxEditor.class, <br>
 * &nbsp; <i style="color: #006000">// Указываем опции редактору <br>
 * &nbsp; // Формат опций <br>
 * &nbsp; // variants: перечисление вариантов через косую черту </i> <br>
 * &nbsp; editorOpts = "variants: ab | cd | ed | \"d e1\" | 'e2 a' "  <br>
 * )  <br>
 * public String getComboVar() { ... }  <br>
 * public void setComboVar(String svar) { ...  }
 * </code>
 * <p>
 *
 * <b>Пример с использованием сосденего метода</b>, для получения списка значений: <p>
 *
 * <code style="font-size: 12pt;">
 * &#64;UiBean( <br>
 * &nbsp; <i style="color: #006000">// Указываем редактор</i> <br>
 * &nbsp; propertyEditor = ComboBoxEditor.class,  <br>
 * &nbsp; <i style="color: #006000">// Указываем опции редактору <br>
 * &nbsp; // Формат <br>
 * &nbsp; // variants call method имя_меода <br>
 * </i>
 * &nbsp; editorOpts = "variants call method <i>getSvarVariants</i>"  <br>
 * )  <br>
 * public String getSvar() { ... }  <br>
 * public void setSvar(String svar) { ... }  <br>
 * public List&lt;String&gt; <i>getSvarVariants</i>(){ ... }  <br>
 * </code>
 * <p>
 *
 * <b>Пример с использование внешнего класса</b>, для получения списка значений: <p>
 *
 * <code style="font-size: 12pt;">
 * <i style="color: #006000">// Бин с редактируемым свойством</i> <br>
 * public class BBean { <br>
 * ... <br>
 * &#64;UiBean( <br>
 * &nbsp; // Указываем редактор <br>
 * &nbsp; propertyEditor = ComboBoxEditor.class,
 * <br>
 * &nbsp; <i style="color: #006000">// Указываем опции редактору <br>
 * &nbsp; // Формат опций <br>
 * &nbsp; // variants call class Имя_вспомогательного_класса method имя_метода<br>
 * &nbsp; // Имя класса может быть без имени пакета, <br>
 * &nbsp; // если находятся в одном пакете</i> <br>
 * &nbsp; editorOpts = "variants call
 * class <i>BBeanHelper</i>
 * method <i>getComboVariants</i>"
 * <br>
 * ) <br>
 * public String getComboVar() { ... } <br>
 * public void setComboVar( String val ) { ... } <br>
 * ... <br>
 * } <br>
 * <br>
 * <i style="color: #006000">// Вспомогательный класс</i> <br>
 * public class <i>BBeanHelper</i> { <br>
 * ... <br>
 * <i style="color: #006000">
 * // Вспомогательный метод, возвращающий массив или Iterable </i>
 * <br>
 * public Object[] <i>getComboVariants</i>(){ <br>
 * &nbsp; return new Object[]{ "bvar1", "bvar2", "bvar3", }; <br>
 * } <br>
 * ... <br>
 * }
 * </code>
 * @author Kamnev Georgiy
 */
public class ComboBoxEditor extends CustomEditor implements SetPropertyEditorOpts
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ComboBoxEditor.class.getName());

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
        logger.entering(ComboBoxEditor.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(ComboBoxEditor.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(ComboBoxEditor.class.getName(), method, result);
    }
    //</editor-fold>

    public ComboBoxEditor(){
    }

    public ComboBoxEditor(ComboBoxEditor sample){
    }

    @Override
    public ComboBoxEditor clone() {
        return new ComboBoxEditor(this);
    }

    protected ListCellRenderer listCellRenderer = new BasicComboBoxRenderer(){
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            Object renderValue = value;

            if( value==null ){
                renderValue = "null";
            }

            super.getListCellRendererComponent(list, renderValue, index, isSelected, cellHasFocus);

            if( value == null ){
                setIcon(Icons.getNullIcon());
            }else{
                setIcon(null);
            }

            return this;
        }
    };

    /**
     * Парсинг опций:
     * <table>
     * <tr valign="top">
     * <td> optsString </td>
     * <td> ::= </td>
     * <td> <b style="color: blue">'variants'</b>
     * (
     * <b style="color: blue">'='</b>
     * |
     * <b style="color: blue">':'</b>
     * )
     * <i>variant</i> { <b style="color: blue">'|'</b> <i>variant</i> }
     * </td>
     * </tr>
     *
     * <tr>
     * <td></td>
     * <td>| </td>
     * <td>
     * <b style="color: blue">'variants'</b>
     * <b style="color: blue">'call'</b>
     * [ classOpt ] methodOpt
     * </td>
     * </tr>
     *
     * <tr>
     * <td> variant </td>
     * <td> ::= </td>
     * <td> textWord | stringConst
     * </td>
     * </tr>
     *
     * <tr>
     * <td>classOpt</td>
     * <td>::=</td>
     * <td><b style="color: blue">'class'</b> className</td>
     * </tr>
     *
     * <tr>
     * <td>methodOpt</td>
     * <td>::=</td>
     * <td><b style="color: blue">'method'</b> methodName</td>
     * </tr>
     *
     * </table>
     *
     * @param optsString
     * @param context Контекст
     */
    private void parseOptionsString( String optsString, Object context ){
        if( optsString==null || optsString.length()==0 ){
            setVartiants(new Object[]{});
            return;
        }

        Matcher m = Pattern.compile("(?is)^variants\\s*[=:](?<vars>.*)").matcher(optsString);
        if( m.matches() ){
            String varsGroup = m.group("vars");
            parseVariants(varsGroup);
            return;
        }

        m = Pattern.compile("(?is)^variants\\s+call\\s+(?<vcall>.+)").matcher(optsString);
        if( m.matches() ){
            parseVariantsCall(m.group("vcall"), context);
            return;
        }

        setVartiants(new Object[]{});
    }

    private void parseVariants( String variantsString ){
        ListLexer ll = new ListLexer();
        ll.getParsers().add(new WhiteSpaceParser("ws"));
        ll.getParsers().add(new KeywordsParser(false, "|"));
        ll.getParsers().add(new TextConstParser("text"));
        ll.getParsers().add(new IdentifierParser("id"));

        List<Token> tokens = ll.parse(variantsString);
        tokens = LexerUtil.filter(tokens, WhiteSpace.class);

        ArrayList variants = new ArrayList();
        Pointer<Token> ptr = new Pointer<>(tokens);
        int state = 0;
        while( state >= 0 ){
            switch( state ){
                case 0:
                {
                    Token t = ptr.lookup(0);
                    if( t==null ){
                        state = -1;
                        break;
                    }
                    ptr.move(1);

                    if( t instanceof Keyword ){
                        switch( ((Keyword)t).getKeyword() ){
                            case "|":
                                break;
                            default:
                                state = -2;
                                break;
                        }
                    }

                    if( t instanceof TextConst ){
                        variants.add(((TextConst)t).getDecodedText());
                    }

                    if( t instanceof Identifier ){
                        variants.add( ((Identifier)t).getMatchedText() );
                    }
                }
                break;
            }
        }

        setVartiants(variants.toArray());
    }

    private void parseVariantsCall( String variantsCallString, Object context ){
        Matcher m = Pattern.compile(
            "(?is)^\\s*(class\\s+(?<clsName>[\\w_][\\.\\w_\\d]+)\\s*)?"
                + "(method\\s+(?<meth>[\\w_][\\w\\d_]+))")
            .matcher(variantsCallString);

        if( m.matches() ){
            String clsName = m.group("clsName");
            String meth = m.group("meth");

            Object ctx = null;
            if( context instanceof PropertyValue ){
                Property prop = ((PropertyValue)context).getProperty();
                ctx = prop!=null ? prop.getBean() : null;
            }else if( context instanceof TreeTableNode ){
                TreeTableNode ttn = (TreeTableNode)context;
                ctx = ttn.getData();
            }else if( context instanceof TreeTableNodeValue ){
                TreeTableNodeValue ttnv = (TreeTableNodeValue)context;
                Object oval = ttnv.getDataOfNode();
                if( oval instanceof Property ){
                    Property prop = (Property)oval;
                    ctx = prop!=null ? prop.getBean() : null;
                }
            }

            Class vclass = null;
            if( clsName!=null && clsName.trim().length()>0 && !clsName.contains(".") && ctx!=null ){
                ClassLoader cl = ctx.getClass().getClassLoader();
                String pkg = ctx.getClass().getPackage().getName();
                String tcls =
                    pkg.trim().length()>0
                        ? Text.trimEnd(pkg, ".") + "." + clsName
                        : clsName;

                try {
                    vclass = Class.forName(tcls, true, cl);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ComboBoxEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else if( clsName!=null && clsName.trim().length()>0 ){
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if( cl==null ){
                    cl = ComboBoxEditor.class.getClassLoader();
                }
                if( ctx!=null ){ cl = ctx.getClass().getClassLoader(); }
                try {
                    vclass = Class.forName(clsName, true, cl);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ComboBoxEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            callVariants(ctx, vclass, meth);
        }
    }

    private void callVariants( Object ctx, Class vclass, String method ){
        if( vclass!=null && method!=null ){
            try {
                Object octx = vclass.newInstance();
                Method m = vclass.getMethod(method);
                Object ovars = m.invoke(octx);
                initVariants(ovars);
                return;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ComboBoxEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if( ctx!=null && method!=null ){
            try {
                Method m = ctx.getClass().getMethod(method);
                Object ovars = m.invoke(ctx);
                initVariants(ovars);
                return;
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ComboBoxEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void initVariants( Object ovars ){
        if( ovars!=null ){
            Class c = ovars.getClass();
            if( c.isArray() ){
                ArrayList l = new ArrayList();
                int len = Array.getLength(ovars);
                for( int ai=0; ai<len; ai++ ){
                    Object el = Array.get(ovars, ai);
                    if( el==null )continue;
                    l.add(el);
                }
                setVartiants(l.toArray());
            }else{
                if( ovars instanceof Iterable ){
                    ArrayList l = new ArrayList();
                    for( Object el : ((Iterable)ovars) ){
                        if( el==null )continue;
                        l.add(el);
                    }
                    setVartiants(l.toArray());
                }
            }
        }else{
            setVartiants(new Object[]{});
        }
    }

    @Override
    public void startEditing(Object value, Object context) {
        parseOptionsString(optionsString, context);
        super.startEditing(value, context);
    }

    protected String optionsString = null;

    @Override
    public void setPropertyEditorOpts(String opts) {
        optionsString = opts;
    }

    //<editor-fold defaultstate="collapsed" desc="variants : Object[]">
    public Object[] getVaraints(){
        ArrayList l = new ArrayList();
        JComboBox cb = getComboBox();
        ComboBoxModel cbm = cb.getModel();
        for( int i=0; i<cbm.getSize(); i++ ){
            l.add(cbm.getElementAt(i));
        }
        return l.toArray();
    }

    public void setVartiants( Object[] variants ){
        if( variants==null )throw new IllegalArgumentException("variants == null");
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel(variants);
        getComboBox().setModel(dcbm);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="comboBox : JComboBox">
    protected JComboBox comboBox;

    public JComboBox getComboBox(){
        synchronized(this){
            if( comboBox!=null )return comboBox;
            comboBox = new JComboBox();
            comboBox.setEditable(true);
            comboBox.setRenderer(listCellRenderer);

            javax.swing.ComboBoxEditor cbeditor = comboBox.getEditor();

            Component cmpt = cbeditor.getEditorComponent();
            if( cmpt!=null ){
                cmpt.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if( e==null )return;
                        if( e.getKeyCode() == KeyEvent.VK_ENTER ){
                            //System.out.println("cbEditor enter pressed");
                            fireEditingStopped(ComboBoxEditor.this);
                        }
                    }
                });
                //System.out.println("binded keyListener to cbEditor");
            }

            return comboBox;
        }
    }

    @Override
    protected JComponent createComponent() {
        return getComboBox();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="value : Object">
    @Override
    public void setValue(Object value) {
        JComboBox cb = getComboBox();
        if( value==null ){
            cb.getEditor().setItem(null);
        }else{
            cb.getEditor().setItem(value);
        }
    }

    @Override
    public Object getValue() {
        JComboBox cb = getComboBox();
        Object val = cb.getEditor().getItem();
        return val;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getJavaInitializationString()">
    @Override
    public String getJavaInitializationString() {
        Object val = getValue();
        if( val==null )return "null";
        return val.toString();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="get/set as text">
    @Override
    public String getAsText() {
        Object val = getValue();
        return val == null ? "" : val.toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(text);
    }
    //</editor-fold>
}
