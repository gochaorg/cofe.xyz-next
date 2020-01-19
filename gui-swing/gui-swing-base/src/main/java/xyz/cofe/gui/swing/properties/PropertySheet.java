/*
 * The MIT License
 *
 * Copyright 2017 user.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package xyz.cofe.gui.swing.properties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.PropertyEditor;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import xyz.cofe.collection.NodesExtracter;
import xyz.cofe.fn.Fn0;
import xyz.cofe.fn.Fn1;
import xyz.cofe.fn.Fn2;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.gui.swing.bean.UiBean;
import xyz.cofe.gui.swing.properties.editor.EnumEditor;
import xyz.cofe.gui.swing.properties.editor.TreeTableWrapEditor;
import xyz.cofe.gui.swing.table.TableFocusListener;
import xyz.cofe.gui.swing.tree.TreeTable;
import xyz.cofe.gui.swing.tree.TreeTableHelper;
import xyz.cofe.gui.swing.tree.TreeTableNode;
import xyz.cofe.gui.swing.tree.TreeTableNodeBasic;
import xyz.cofe.gui.swing.tree.TreeTableNodeFormat;
import xyz.cofe.gui.swing.tree.TreeTableNodeFormatBasic;
import xyz.cofe.gui.swing.tree.TreeTableNodeGetFormatOf;
import xyz.cofe.gui.swing.tree.TreeTableNodeValueEditor;
import xyz.cofe.iter.Eterable;
import xyz.cofe.text.Text;
import xyz.cofe.text.out.Output;

/**
 * Реадктор свойств
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class PropertySheet extends JPanel {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(PropertySheet.class.getName());
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
        logger.entering(PropertySheet.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(PropertySheet.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(PropertySheet.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор по умолчанию
     */
    public PropertySheet(){
//        prepareIcons();
        buildUI();
        //setDescriptionVisible(true);
        configureTreeTable( getTreeTable() );

        listen(PropertySheetReadFail.class, (PropertySheetReadFail ev) -> {
            logger.log(
                Level.SEVERE,
                "read property "+
                    ev.getProperty().getName() + " : " + ev.getProperty().getPropertyType() +
                    " fail",
                ev.getError());
        });
        listen(PropertySheetWriteFail.class, (PropertySheetWriteFail ev) -> {
            logger.log(
                Level.SEVERE,
                "write property "+
                    ev.getProperty().getName() + " : " + ev.getProperty().getPropertyType() +
                    " fail",
                ev.getError());
        });

        if( table!=null ){
            TableFocusListener tfl = new TableFocusListener(table, true){
                @Override
                protected void onFocusedRowChanged(JTable jtable, int oldRow, int curRow) {
                    Object o = PropertySheet.this.table.getFocusedNode();
                    if( o instanceof TreeTableNodeBasic ){
                        TreeTableNodeBasic ttnb = (TreeTableNodeBasic)o;
                        Object op = ttnb.getData();
                        if( op instanceof Property ){
                            Property p = (Property)op;
                            if( descriptionPanel!=null && isDescriptionVisible() ){
                                buildDescription(descriptionPanel, p);
                            }
                        }
                    }
                }
            };
        }
    }

    //<editor-fold defaultstate="collapsed" desc="listeners">
    protected final ListenersHelper<PropertySheetListener,PropertySheetEvent> listeners
        = new ListenersHelper<>( (PropertySheetListener ls, PropertySheetEvent ev) -> {
                if( ls!=null )ls.propertySheetEvent(ev);
        }
    );

    /**
     * Проверяет наличие подписчика
     * @param listener подписчик
     * @return true - подписка оформлена
     */
    public boolean hasListener(PropertySheetListener listener) {
        return listeners.hasListener(listener);
    }

    /**
     * Возвращает подписчиков
     * @return подписчики
     */
    public Set<PropertySheetListener> getListeners() {
        return listeners.getListeners();
    }

    /**
     * Добавляет подписчика на события
     * @param listener подписчик
     * @return отписка
     */
    public AutoCloseable addListener(PropertySheetListener listener) {
        return listeners.addListener(listener);
    }

    /**
     * Добавляет подписчика на события
     * @param listener подписчик
     * @param weakLink true - добавить подписчика как weak ссылку
     * @return отписка
     */
    public AutoCloseable addListener(PropertySheetListener listener, boolean weakLink) {
        return listeners.addListener(listener, weakLink);
    }

    /**
     * Удаляет подписчика на события
     * @param listener подписчик
     */
    public void removeListener(PropertySheetListener listener) {
        listeners.removeListener(listener);
    }

    /**
     * Рассылает уведомление подписчикам
     * @param event уведомление
     */
    public void fireEvent(PropertySheetEvent event) {
        listeners.fireEvent(event);
    }

    /**
     * Добавлет подписчика на события
     * @param <T> тип события
     * @param cls тип события
     * @param consumer подписчик
     * @return отписка от событий
     */
    public <T extends PropertySheetEvent> AutoCloseable listen( final Class<T> cls, final Consumer<T> consumer ){
        if( cls==null )throw new IllegalArgumentException("cls == null");
        if( consumer==null )throw new IllegalArgumentException("consumer == null");

        return addListener( ev -> {
            if( ev==null )return;
            if( ev.getClass().equals(cls) ){
                consumer.accept((T)ev);
            }
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="treeTable">
    protected TreeTable table;
    /**
     * Возвращает ссылку на TreeTable
     * @return TreeTable данного редактора
     */
    public TreeTable getTreeTable(){
        return table;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="treeTableScroll">
    protected JScrollPane scroll;
    /**
     * Возвращает скроллер содержащий TreeTabke компонент
     * @return Скроллер
     */
    public JScrollPane getTreeTableScroll(){ return scroll; }
    //</editor-fold>

    protected JSplitPane splitPane;
    /**
     * Возвращает сплит-панель между TreeTable и описанием
     * @return сплит панель
     */
    public JSplitPane getSplitPane() {
        if( splitPane==null ){
            splitPane = new JSplitPane();
            splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitPane.setResizeWeight(1);
            splitPane.setDividerLocation(0.75);
            //splitPane.setDividerLocation(200);
        }
        splitPane.setRightComponent(getDescriptionPanel());
        return splitPane;
    }

    protected JPanel descriptionPanel;
    /**
     * Возвращает панель описания свойства
     * @return панель описания свойства
     */
    public JPanel getDescriptionPanel(){
        if( descriptionPanel==null ){
            descriptionPanel = new JPanel();
        }
        return descriptionPanel;
    }

    /**
     * Возвращает видима ли панель описания свойства
     * @return true панель описания отображается
     */
    public boolean isDescriptionVisible(){
        return descriptionPanel!=null && descriptionPanel.isVisible();
    }

    private int setDescriptionVisible_cnt = 0;
    /**
     * Указывает видима ли панель описания свойства
     * @param visible отображать панель описания
     */
    public void setDescriptionVisible( boolean visible ){
        if( Objects.equals(isDescriptionVisible(), visible) )return;
        if( visible ){
            removeAll();
            setLayout(new BorderLayout());

            add( getSplitPane() );

            scroll = new JScrollPane(table);
            getSplitPane().setLeftComponent(scroll);

            setDescriptionVisible_cnt++;
            if( setDescriptionVisible_cnt==1 ){
                Runnable r = new Runnable() {
                    public void run() {
                        getSplitPane().setDividerLocation(0.75);
                    }
                };
                SwingUtilities.invokeLater(r);
            }
        }else{
            removeAll();
            setLayout(new BorderLayout());

            scroll = new JScrollPane(table);
            add( scroll );
        }

        revalidate();
        invalidate();
        repaint();
    }

    /**
     * Создает текстовое описание свойства
     * @param descPanel панель описания свойства
     * @param property свойство
     */
    protected void buildDescription( JPanel descPanel, Property property ){
        if( descPanel==null )return;
        descPanel.removeAll();

        if( property==null )return;

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");

        descPanel.setLayout(new BorderLayout());
        descPanel.add( new JScrollPane(textPane) );

        StringWriter sw = new StringWriter();
        Output out = new Output(sw,true);

        String desc = property.getShortDescription();
        if( desc==null ){
            desc = "";
        }else{
            desc = Text.join(
                Text.convert(
                    Text.splitNewLinesIterable(desc),
                    Text.Convertors.htmlEncode
                ),
                "<br/>"
            );
        }

        out.template(
            "<div><b>${name}</b> : ${type}</div>"
                +   "${shortDesc}"
        )
            .bind("name",
                Text.htmlEncode( property.getName()) )
            .bind("type",
                Text.htmlEncode( property.getPropertyType()!=null ? property.getPropertyType().getName() : "") )
            .bind("shortDesc", desc )
            .println();

        out.flush();
        textPane.setText(sw.toString());

        String htmlDesc = property.getHtmlDescription();
        if( htmlDesc!=null && htmlDesc.trim().length()>0 ){
            textPane.setText(htmlDesc);
        }

        descPanel.revalidate();
        descPanel.repaint();
    }

    //<editor-fold defaultstate="collapsed" desc="buildUI()">
    /**
     * Создание пользовательских графических компонент
     */
    protected void buildUI(){
        table = new TreeTable();
        table.setFillsViewportHeight(true);
        scroll = new JScrollPane(table);

        setLayout(new BorderLayout());
        add( scroll );

        revalidate();
        invalidate();
        repaint();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="pdb">
    protected volatile PropertyDB pdb;
    /**
     * Возвращает "базу" свойств
     * @return база свойств
     */
    public PropertyDB getPropertyDB(){
        if( pdb!=null )return pdb;
        synchronized(this){
            if( pdb!=null )return pdb;
            pdb = new PropertyDB();
            return pdb;
        }
    }
    /**
     * Указывает "базу" свойств
     * @param newPdb база свойств
     */
    public void setPropertyDB(PropertyDB newPdb){
        synchronized(this){
            this.pdb = newPdb;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="nullValueFormat">
    //<editor-fold defaultstate="collapsed" desc="nullIcon">
    protected volatile Icon nullIcon = null;
    /**
     * Возвращает иконку обозначающую null значение
     * @return иконка null значения
     */
    public Icon getNullIcon(){
        if( nullIcon!=null )return nullIcon;
        synchronized(this){
            if( nullIcon!=null )return nullIcon;
            nullIcon = Icons.getNullIcon();
            return nullIcon;
        }
    }
    /**
     * Указывает иконку обозначающую null значение
     * @param ico иконка null значения
     */
    public void setNullIcon( Icon ico ){
        synchronized(this){
            nullIcon = ico;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="get/set nullValueFormat">
    protected volatile TreeTableNodeFormat nullValueFormat;

    /**
     * Возвращает настройки форматирования null значения
     * @return настройки форматирования
     */
    public TreeTableNodeFormat getNullValueFormat() {
        TreeTableNodeFormat fmt = nullValueFormat;
        if( fmt!=null )return fmt;

        synchronized( this ){
            fmt = nullValueFormat;
            if( fmt!=null )return fmt;

            fmt = new TreeTableNodeFormatBasic();

            //fmt.setBold(true);
            fmt.setItalic(true);

            Icon ico = getNullIcon();
            if( ico!=null ){
                fmt.getIcons().add(ico);
            }

            nullValueFormat = fmt;
            return nullValueFormat;
        }
    }

    /**
     * Указывает настройки форматирования null значения
     * @param nullValueFormat Настройки форматирования null значения
     */
    public void setNullValueFormat(TreeTableNodeFormat nullValueFormat) {
        synchronized(this){
            this.nullValueFormat = nullValueFormat;
        }
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="configureTreeTable()">
    private void configureTreeTable(
        final TreeTable table
    ){
        //<editor-fold defaultstate="collapsed" desc="подготовка 1">
        if (table== null) {
            throw new IllegalArgumentException("table==null");
        }

        // Высота строки
        table.setRowHeight(20);

        // Не Отображать корень
        //table.getDirectModel().setRootVisible(false);
        table.setRootVisible(false);

        // Имя node колонки
        table.setNodeColumnName("property");
        table.setNodeColumnHeaderValue("property");

        // Корень
        TreeTableNodeBasic root = new TreeTableNodeBasic();

        // Развернуть сразу
        root.expand();

        // время кэширования
        root.setCacheLifeTime((long)1000 * 5);

        // устанавливаем корень
        table.setRoot(root);
        //</editor-fold>

        // Помощник в настройке table
        TreeTableHelper helper = new TreeTableHelper(table);

        //<editor-fold defaultstate="collapsed" desc="Обработка Set.class">
        helper.
            node(Set.class).
            follow( new NodesExtracter<Set, Object>() {
                @Override
                public Iterable<Object> extract(Set from) {
                    return from;
                }
            } ).
            apply();
        //</editor-fold>

        configureTreeTableProperty(helper);

        //<editor-fold defaultstate="collapsed" desc="Обработка NamedEntry">
        helper.node(NamedEntries.class).
            naming( (NamedEntries named) -> named.getName() ).
            follow( (NamedEntries named) -> named.getEntries() ).
            column("value").
            reader( (arg) -> { return ""; } ).
            apply().
            apply();

        helper.node(NamedEntry.class)
            .naming( (NamedEntry named) -> named.getName() )
            .follow( (NamedEntry named) -> {
                Object en = named.getEntry();
                if( en!=null ){
                    PropertyDB pdb = getPropertyDB();
                    return pdb.readBeadNodes(en);
                }
                return null;
                })
            .apply();

        helper.
            node(NamedEntry.class).
            column("value").reader( (NamedEntry en) ->  en.getEntry()).
            apply();
        //</editor-fold>
    }

    private void configureTreeTableProperty(
        TreeTableHelper helper
    ) {
        helper.
            node(Property.class).
            naming( propertyNamingFn ).
            column("value").
            reader(propertyValueReaderFn).
            writer(propertyValueWriterFn).
            editor(propertyValueEditorFn).
            type(propertyValueTypeFn).
            format(propertyValueFormatFn).
            apply().
            followable( propertyValueFollowableFn ).
            follow( propertyValueFollowFn ).
            apply();
    }

    private Fn1<Property,String> propertyNamingFn = (Property prop) -> prop.getDisplayName();
    private Fn1<Property,Object> propertyValueReaderFn = (Property prop) -> {
        try{
            Object val = prop.read();
            return val;
        }catch( Throwable err ){
            fireEvent(new PropertySheetReadFail(PropertySheet.this, prop, err));
            return null;
        }
    };
    private Fn2<Property, Object, Object> propertyValueWriterFn = (Property prop, Object val) -> {
        try{
            prop.write(val);
            fireEvent(new PropertySheetWrited(PropertySheet.this, prop, val));
        } catch ( Throwable err ){
            fireEvent(new PropertySheetWriteFail(PropertySheet.this, prop, val, err));
        }
        return val;
    };
    private Fn2<Property, TreeTableNode, TreeTableNodeValueEditor.Editor> propertyValueEditorFn = (Property prop, TreeTableNode ttn) -> {
        // Проверка на read only
        if( prop.isReadOnly() )return null;

        // Проверка на read only Descent

//        List dataPath = ttn.getDataPath();

        List dataPath = ((TreeTableNode<?>)ttn).path().stream().map( x ->
            x instanceof TreeTableNode ? ((TreeTableNode)x).getData() : null ).collect(Collectors.toList());

        List pdataPath = null;
        if( dataPath==null ){
            pdataPath = new ArrayList();
        }else{
            pdataPath = new LinkedList(dataPath);
            if( !pdataPath.isEmpty() ){
                pdataPath.remove( pdataPath.size()-1 );
            }
        }

        Collections.reverse(pdataPath);
        for( Object pd : pdataPath ){
            if( pd instanceof Property ){
                Property p = (Property)pd;
                Boolean ro = p.getReadOnlyDescent();
                if( ro!=null && ro ){
                    return null;
                }
            }
        }

        Class ptype = prop.getPropertyType();

        if( ptype!=null && ptype.isEnum() ){
            return new EnumEditor(ptype, true);
        }

        PropertyDB cpdb = getPropertyDB();
        if( cpdb!=null ){
            PropertyEditor pe = cpdb.getPropertyEditorOf(prop);
            if( pe!=null ){
                if( pe instanceof TreeTableNodeValueEditor.Editor ){
                    return (TreeTableNodeValueEditor.Editor)pe;
                }else{
                    return new TreeTableWrapEditor(pe);
                }
            }
        }

        return null;
    };
    private Fn2<Property, TreeTableNode, TreeTableNodeFormat> propertyValueFormatFn =  (Property prop, TreeTableNode node) -> {
        TreeTableNodeFormat fmt = new TreeTableNodeFormatBasic();

        Object val = null;

        try{
            val = prop.read();
        }catch( final Throwable ex ){
            fmt.setConvertor( (Object from) -> {
                String err = ex.getMessage();
                String errc = ex.getClass().getSimpleName();
                return errc+(err!=null ? " "+err : "");
            });
            fmt.setForeground(Color.RED);
            return fmt;
        }

        if( val==null ){
            TreeTableNodeFormat nfmt = getNullValueFormat();
            if( nfmt!=null ){
                fmt.merge( nfmt );
            }
        }

        boolean readOnly = prop.isReadOnly();

        // Проверка на read only Descent
        // List dataPath = node.getDataPath();
        List dataPath = ((TreeTableNode<?>)node).path().stream().map( x ->
            x instanceof TreeTableNode ? ((TreeTableNode)x).getData() : null ).collect(Collectors.toList());


        List pdataPath = null;
        if( dataPath==null ){
            pdataPath = new ArrayList();
        }else{
            pdataPath = new LinkedList(dataPath);
            if( !pdataPath.isEmpty() ){
                pdataPath.remove( pdataPath.size()-1 );
            }
        }

        Collections.reverse(pdataPath);
        for( Object pd : pdataPath ){
            if( pd instanceof Property ){
                Property p = (Property)pd;
                Boolean ro = p.getReadOnlyDescent();
                if( ro!=null && ro ){
                    readOnly = true;
                    break;
                }
            }
        }

        if( readOnly ){
            fmt.setForeground(Color.gray);
        }

        Class propType = prop.getPropertyType();

        if( propType!=null ){
            PropertyDB cpdb = getPropertyDB();
            if( cpdb!=null ){
                Set<TreeTableNodeGetFormatOf> propFormats = cpdb.getFormattersOf(propType);
                if( propFormats!=null && !propFormats.isEmpty() ){
                    TreeTableNodeGetFormatOf getNF = propFormats.iterator().next();
                    if( getNF!=null ){
                            /* if( getNF instanceof PropertyEditor ){
                            PropertyEditor pe = (PropertyEditor)getNF;
                            pe.setValue(val);
                            String customText = pe.getAsText();
                            } */

                        TreeTableNodeFormat cfmt = getNF.getTreeTableNodeFormatOf(val);
                        if( cfmt!=null ){
                            fmt.merge( cfmt );
                        }
                    }
                }
            }
        }

        return fmt;
    };

    private Fn2<Property, TreeTableNode,Class> propertyValueTypeFn = (Property prop, TreeTableNode node) -> prop.getPropertyType();
    private Fn1<Property,Boolean> propertyValueFollowableFn = (Property prop) -> {
        PropertyDB pdb = getPropertyDB();
        return pdb.isExpandableType(prop.getPropertyType());
    };

    private NodesExtracter<Property, Object> propertyValueFollowFn =
        new NodesExtracter<Property, Object>() {
            @Override
            public Iterable<Object> extract(Property prop) {
                Object propval = prop.read();
                if( propval!=null ){
                    PropertyDB pdb = getPropertyDB();

                    Eterable iter = pdb.readBeadNodes(propval);

                    UiBean uib = prop.getUiBean();
                    if( uib!=null ){
                        final String[] hidden = uib.hiddenPeroperties();
                        if( hidden!=null && hidden.length>0 ){
                            Predicate filterHidden = new Predicate() {
                                @Override
                                public boolean test(Object value) {
                                    if( value instanceof Property ){
                                        Property prop = (Property)value;
                                        if( Text.in(prop.getName(), hidden) ){
                                            return false;
                                        }
                                    }
                                    return true;
                                }
                            };

                            //iter = Iterators.predicate(iter, filterHidden);
                            iter = iter.filter(filterHidden);
                        }
                    }

                    iter = iter.notNull();

                    List result = new ArrayList();

                    Map<String,List> categories = new TreeMap<>();
                    List uncategoried = new ArrayList();

                    for( Object nodeData : iter ){
                        String cat = null;

                        if( nodeData instanceof Property ){
                            uib = ((Property) nodeData).getUiBean();
                            if( uib!=null && uib.category().length()>0 ){
                                cat = uib.category();
                            }
                        }

                        if( cat!=null ){
                            List l = categories.get(cat);
                            if( l==null ){
                                l = new ArrayList();
                                categories.put(cat, l);
                            }
                            l.add(nodeData);
                        }else{
                            uncategoried.add(nodeData);
                        }
                    }

                    if( !categories.isEmpty() ){
                        for( String cat : categories.keySet() ){
                            List catv = categories.get(cat);
                            NamedEntries ne = new NamedEntries(cat, catv);
                            result.add(ne);
                        }
                    }
                    for( Object nodeData : uncategoried ){
                        result.add(nodeData);
                    }

                    return result;
                }
                return (Iterable<Object>)null;
            }
        };
    //</editor-fold>

    /**
     * Редактирует свойства указанного бина
     * @param bean бин
     */
    public void edit( Object bean ){
        edit(bean, null);
    }

    /**
     * Редактирует свойства указанного бина
     * @param bean бин
     * @param extraProperties дополнительный набор свойств или null
     */
    public void edit( Object bean, Iterable<Property> extraProperties ){
        PropertySheetEdit editEvent = new PropertySheetEdit(this);
        editEvent.setBean(bean);

        TreeTableNodeBasic root = table.getRoot();
        if( root!=null ){
            root.dropCache();
            //root.getChildrenList().clear();
            root.clear();

            TreeTableNodeBasic nroot = root.clone(false, true);
            nroot.setData(bean);

            nroot.setExpanded(true);

            Map<String,List> categories = new TreeMap<>();
            List uncategoried = new ArrayList();

            PropertyDB pdb = getPropertyDB();

            if( bean!=null ){
                for( Object nodeData : pdb.readBeadNodes(bean) ){
                    String cat = null;

                    if( nodeData instanceof Property ){
                        UiBean uib = ((Property) nodeData).getUiBean();
                        if( uib!=null && uib.category().length()>0 ){
                            cat = uib.category();
                        }
                    }

                    if( cat!=null ){
                        List l = categories.get(cat);
                        if( l==null ){
                            l = new ArrayList();
                            categories.put(cat, l);
                        }
                        l.add(nodeData);
                    }else{
                        uncategoried.add(nodeData);
                    }
                }
            }

            if( extraProperties!=null ){
                for( Property p : extraProperties ){
                    if( p==null )continue;

                    String cat = null;

                    UiBean uib = p.getUiBean();
                    if( uib!=null && uib.category().length()>0 ){
                        cat = uib.category();
                    }

                    if( cat!=null ){
                        List l = categories.get(cat);
                        if( l==null ){
                            l = new ArrayList();
                            categories.put(cat, l);
                        }
                        l.add(p);
                    }else{
                        uncategoried.add(p);
                    }
                }
            }

            if( !categories.isEmpty() ){
                for( String cat : categories.keySet() ){
                    List catv = categories.get(cat);
                    NamedEntries ne = new NamedEntries(cat, catv);

                    TreeTableNodeBasic node = new TreeTableNodeBasic(ne);
                    //nroot.getChildrenList().add(node);
                    nroot.append(node);

                    List catValues = categories.get(cat);
                    if( catValues!=null ){
                        for( Object p : catValues ){
                            if( p instanceof Property ){
                                editEvent.getProperties().add((Property)p);
                            }
                        }
                    }
                }
            }
            for( Object nodeData : uncategoried ){
                TreeTableNodeBasic node = new TreeTableNodeBasic(nodeData);
                //nroot.getChildrenList().add(node);
                nroot.append(node);

                if( nodeData instanceof Property ){
                    editEvent.getProperties().add((Property)nodeData);
                }
            }

            table.setRoot(nroot);
            fireEvent(editEvent);
        }
        revalidate();
    }

    /**
     * Построение редактора свойств
     */
    public static class PropBuilder extends PropertyBuilderGeneric<PropBuilder>
    {
        protected EditProperties editProperties;

        public PropBuilder(EditProperties editProperties){
            if (editProperties== null) {
                throw new IllegalArgumentException("editProperties==null");
            }
            this.editProperties = editProperties;
        }

        /**
         * Добавляет редактируемые свойства
         * @return редактор
         */
        public EditProperties add(){
            Property p = build();
            if( p!=null ){
                editProperties.add(p);
            }
            return editProperties;
        }
    }

    /**
     * Редактор свойств
     */
    public static class EditProperties {
        protected Edit edit;
        public EditProperties(Edit edit){
            if (edit== null) {
                throw new IllegalArgumentException("edit==null");
            }
            this.edit = edit;
        }

        /**
         * Добавить свойство
         * @param prop свойство
         * @return редактор
         */
        public Edit add( Property prop ){
            if (prop== null) {
                throw new IllegalArgumentException("prop==null");
            }
            edit.getProperties().add(prop);
            return edit;
        }

        /**
         * Добавить объект как свойство
         * @param name Имя свойства
         * @param value Значение
         * @return редактор
         */
        public Edit addAsProperty( Object value, String name ){
            if (name== null) {
                throw new IllegalArgumentException("name==null");
            }
            if (value== null) {
                throw new IllegalArgumentException("value==null");
            }
            PropBuilder pb = new PropBuilder(this).name(name);
            pb.value(value);
            return add( pb.build() );
        }

        /**
         * Добавить свойства указанного объекта
         * @param bean объект
         * @param properties свойства
         * @return редактор
         */
        public Edit include( Object bean, String ... properties ){
            if( bean==null )throw new IllegalArgumentException("bean == null");
            if( properties==null )throw new IllegalArgumentException("properties == null");
            Set<Property> pset = Property.propertiesOf(
                bean.getClass(), bean,
                Property
                    .propertyQuery()
                    .include(properties)
                    //.skip().hidden(false)
                    .build()
            );
            edit.getProperties().addAll(pset);
            return edit;
        }

        /**
         * Добавить свойства указанного объекта, за исключением указанных
         * @param bean объект
         * @param properties свойства которые не включать в набор
         * @return редактор
         */
        public Edit exclude( Object bean, String ... properties ){
            if( bean==null )throw new IllegalArgumentException("bean == null");
            if( properties==null )throw new IllegalArgumentException("properties == null");
            Set<Property> pset = Property.propertiesOf(
                bean.getClass(), bean,
                Property
                    .propertyQuery()
                    .exclude(properties)
                    //.skip().hidden(false)
                    .build()
            );
            edit.getProperties().addAll(pset);
            return edit;
        }

        /**
         * Добавить/создать read-only свойство
         * @param <T> Тип свойства
         * @param name Имя свойства
         * @param type тип
         * @param r функция чтения свойства
         * @return редактор
         */
        public <T> Edit add( String name, Class<T> type, Fn0<T> r ){
            if (name== null) {
                throw new IllegalArgumentException("name==null");
            }
            if (type== null) {
                throw new IllegalArgumentException("type==null");
            }
            if (r== null) {
                throw new IllegalArgumentException("r==null");
            }
            PropBuilder pb = new PropBuilder(this).name(name);
            pb.type(type);
            pb.reader(r);
            return add( pb.build() );
        }

        /**
         * Добавить/создать read-write свойство
         * @param <T> Тип свойства
         * @param name Имя свойства
         * @param type тип
         * @param r функция чтения свойства
         * @param w функция записи свойства
         * @return редактор
         */
        public <T> Edit add( String name, Class<T> type, Fn0<T> r, Fn1<Object,T> w ){
            if (name== null) {
                throw new IllegalArgumentException("name==null");
            }
            if (type== null) {
                throw new IllegalArgumentException("type==null");
            }
            if (r== null) {
                throw new IllegalArgumentException("r==null");
            }
            PropBuilder pb = new PropBuilder(this).name(name);
            pb.type(type);
            pb.reader(r);
            pb.writer(w);
            return add( pb.build() );
        }

        /**
         * Создать/добавить свойство
         * @param name имя свойства
         * @return редактор
         */
        public PropBuilder create( String name ){
            return new PropBuilder(this).name(name);
        }
    }

    public static class Edit {
        protected PropertySheet ps;
        public Edit(PropertySheet ps){
            if (ps== null) {
                throw new IllegalArgumentException("ps==null");
            }
            this.ps = ps;
        }

        protected Object bean;
        /**
         * Редактировать объект
         * @param bean объект или null
         * @return редактор
         */
        public Edit bean( Object bean ){
            //PropertySheet.this.edit(bean);
            this.bean = bean;
            return this;
        }

        protected List<Property> properties;
        /**
         * Указывает дополнительные свойства (которые не входят в данный объект)
         * @return свойства
         */
        public List<Property> getProperties(){
            if( properties==null ){
                properties = new ArrayList<>();
            }
            return properties;
        }

        /**
         * Указывает дополнительные свойства (которые не входят в данный объект)
         * @param properties свойства
         * @return редактор
         */
        public Edit properties( Iterable<Property> properties ){
            getProperties().clear();
            if( properties!=null ){
                for( Property p : properties ){
                    if( p!=null ){
                        getProperties().add(p);
                    }
                }
            }
            return this;
        }

        /**
         * Реадктирование списка свойств
         * @return свойства
         */
        public EditProperties properties(){
            return new EditProperties(this);
        }

        /**
         * Применить изменения
         */
        public void apply(){
            ps.edit(bean, getProperties());
        }
    }

    /**
     * Редактирование объекта/свойства
     * @return редактор
     */
    public Edit edit(){ return new Edit(this); }

    //<editor-fold defaultstate="collapsed" desc="cacheLifeTime">
    /**
     * Указывает время кэширования структуры дерева свойств
     * @return время кэширования
     */
    public Long getCacheLifeTime(){
        return table.getRoot().getCacheLifeTime();
    }

    /**
     * Указывает время кэширования структуры дерева свойств
     * @param lifeTime время кэширования
     */
    public void setCacheLifeTime(Long lifeTime){
        table.getRoot().setCacheLifeTime(lifeTime);
    }
    //</editor-fold>
}
