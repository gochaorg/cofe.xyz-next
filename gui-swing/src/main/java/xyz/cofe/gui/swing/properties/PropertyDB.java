package xyz.cofe.gui.swing.properties;

import xyz.cofe.collection.BasicEventMap;
import xyz.cofe.collection.ClassMap;
import xyz.cofe.collection.EventMap;
import xyz.cofe.ecolls.Pair;
import xyz.cofe.gui.swing.bean.UiBean;
import xyz.cofe.gui.swing.properties.editor.AbstractPropertyEditor;
import xyz.cofe.gui.swing.properties.editor.CustomEditor;
import xyz.cofe.gui.swing.tree.TreeTableNodeGetFormatOf;
import xyz.cofe.iter.Eterable;
import xyz.cofe.text.Text;
import xyz.cofe.text.template.SimpleTypes;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Редакторы свойств
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class PropertyDB {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(PropertyDB.class.getName());
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
        logger.entering(PropertyDB.class.getName(),method,params);
    }

    private static void logExiting(String method,Object result){
        logger.exiting(PropertyDB.class.getName(),method,result);
    }

    private static void logExiting(String method){
        logger.exiting(PropertyDB.class.getName(),method);
    }
    //</editor-fold>

    protected final Object sync;

    /**
     * Конструктор
     */
    public PropertyDB(){
        this.sync = this;
        for( PropertyDBService srvc : ServiceLoader.load(PropertyDBService.class) ){
            if( srvc!=null ){
                logFine( "loaded service: {0}", srvc.getClass().getName() );
                srvc.register(this);
            }
        }
    }

    private static PropertyEditor clonePE( PropertyEditor pe, Map<PropertyEditor,PropertyEditor> clones ){
        if( pe instanceof AbstractPropertyEditor ){
            AbstractPropertyEditor cpe = null;

            cpe = (AbstractPropertyEditor)clones.get(pe);
            if( cpe == null ){
                cpe = ((AbstractPropertyEditor)pe).clone();
                clones.put(pe, cpe);
            }

            return cpe;
        }if( pe instanceof CustomEditor ){
            CustomEditor cpe = null;

            cpe = (CustomEditor)clones.get(pe);
            if( cpe == null ){
                cpe = ((CustomEditor)pe).clone();
                clones.put(pe, cpe);
            }

            return cpe;
        }else if( pe!=null ){
            return pe;
        }

        return null;
    }

    /**
     * Конструктор копиования
     * @param sample образец для копирования
     */
    public PropertyDB( PropertyDB sample ){
        this.sync = this;
        if( sample!=null ){
            beanReaders.addAll(sample.beanReaders);
            beanReadersOrder = Arrays.copyOf(beanReadersOrder, beanReadersOrder.length);
            failedPropertyEditor.putAll(sample.failedPropertyEditor);

            Map<String,PropertyEditor> neditors = getNamedEditors();
            Map<PropertyEditor, PropertyEditor> clones = new LinkedHashMap<>();

            /*sample.getNamedEditors().forEach( (name,pe) -> {
                if( name!=null ){
                    if( pe!=null )neditors.put(name, clonePE(pe, clones));
                }
            } );*/

            for( Map.Entry<String,PropertyEditor> en : sample.getNamedEditors().entrySet() ){
                String name = en.getKey();
                PropertyEditor pe = en.getValue();
                if( name!=null ){
                    if( pe!=null )neditors.put(name, clonePE(pe, clones));
                }
            }

            /*sample.propertyDescEditorMap.forEach( (pd,pe) -> {
                if( pd!=null ){
                    propertyDescEditorMap.put( pd, clonePE(pe, clones) );
                }
            } );*/

            for( Map.Entry<PropertyDescriptor,PropertyEditor> en : sample.propertyDescEditorMap.entrySet() ){
                PropertyDescriptor pd = en.getKey();
                PropertyEditor pe = en.getValue();
                if( pd!=null ){
                    propertyDescEditorMap.put( pd, clonePE(pe, clones) );
                }
            }

            /*sample.typeEditors.forEach( (t,pse) -> {
                if( t!=null ){
                    if( pse!=null ){
                        LinkedHashSet<PropertyEditor> set = new LinkedHashSet<>();
                        pse.forEach( pe -> {
                            if( pe!=null ){
                                set.add(clonePE(pe, clones));
                            }
                        } );
                        typeEditors.put(t, set);
                    }
                }
            } );*/

            for( Map.Entry<Class,Set<PropertyEditor>> en : sample.typeEditors.entrySet() ){
                Class t = en.getKey();
                Set<PropertyEditor> pse = en.getValue();
                if( t!=null ){
                    if( pse!=null ){
                        LinkedHashSet<PropertyEditor> set = new LinkedHashSet<PropertyEditor>();
                        for( PropertyEditor pe : pse ){
                            if( pe!=null ){
                                set.add(clonePE(pe, clones));
                            }
                        }
                        typeEditors.put(t, set);
                    }
                }
            }

            /*sample.propertyEditorWeightMap.forEach( (pe,w) -> {
                if( pe!=null && w!=null ){
                    propertyEditorWeightMap.put(clonePE(pe, clones), w);
                }
            } );*/
            for( Map.Entry<PropertyEditor, Double> en : sample.propertyEditorWeightMap.entrySet() ){
                PropertyEditor pe = en.getKey();
                Double w = en.getValue();
                if( pe!=null && w!=null ){
                    propertyEditorWeightMap.put(clonePE(pe, clones), w);
                }
            }

            /*sample.getPropertySettingsMap().forEach( (t,names) -> {
                if( t==null || names==null )return;
                names.forEach( (pname,ps) -> {
                    if( ps==null )return;
                    setPropertySettings(t, pname, ps.clone());
                } );
            } );*/

            for( Map.Entry<String,Map<String,PropertySettings>> en : sample.getPropertySettingsMap().entrySet() ){
                String t = en.getKey();
                Map<String,PropertySettings> names = en.getValue();
                for( Map.Entry<String,PropertySettings> en2 : names.entrySet() ){
                    String pname = en2.getKey();
                    PropertySettings ps = en2.getValue();
                    setPropertySettings(t, pname, ps.clone());
                }
            }
        }else{
            for( PropertyDBService srvc : ServiceLoader.load(PropertyDBService.class) ){
                if( srvc!=null ){
                    logFine( "loaded service: {0}", srvc.getClass().getName() );
                    srvc.register(this);
                }
            }
        }
    }

    /**
     * Редакторы свойств для конкретного свойства
     */
    protected final WeakHashMap<PropertyDescriptor,PropertyEditor>
        propertyDescEditorMap = new WeakHashMap<PropertyDescriptor,PropertyEditor>();

    /**
     * Классы редакторов которые InstantiationException/IllegalAccessException
     */
    protected final WeakHashMap<Class,Boolean> failedPropertyEditor = new WeakHashMap<Class,Boolean>();

    //<editor-fold defaultstate="collapsed" desc="namedEditors : Map<String,PropertyEditor>">
    protected EventMap<String,PropertyEditor> namedEditors;

    /**
     * Возвращает именнованые редакторы свойств
     * @return редакторы
     */
    public EventMap<String, PropertyEditor> getNamedEditors() {
        synchronized(sync) {
            if( namedEditors==null ){
                namedEditors = new BasicEventMap<>();
            }
            return namedEditors;
        }
    }

    /**
     * Указывает именнованые редакторы свойств
     * @param namedEditors редакторы
     */
    public void setNamedEditors(EventMap<String, PropertyEditor> namedEditors) {
        synchronized( this ){
            this.namedEditors = namedEditors;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getPropertyEditorOf( PropertyValue pv )">
    /**
     * Получение редактора для конкретного свойства
     * @param pv свойство
     * @return редактор или null
     */
    public PropertyEditor getPropertyEditorOf( PropertyValue pv ){
        synchronized(sync){
            if( pv==null )return null;

            Property p = pv.getProperty();
            if( p==null )return null;

            UiBean uib = p.getUiBean();

            PropertyEditor pe = getPropertyEditorOf(pv.getProperty());
            if( pe!=null )return pe;

            Object val = pv.getValue();
            if( val!=null ){
                pe = getTypeEditor(val.getClass());

                if( pe instanceof SetPropertyEditorOpts ){
                    if( uib!=null && uib.editorOpts()!=null && uib.editorOpts().length()>0 ){
                        ((SetPropertyEditorOpts)pe).setPropertyEditorOpts(uib.editorOpts());
                    }else{
                        ((SetPropertyEditorOpts)pe).setPropertyEditorOpts("");
                    }
                }

                if( pe!=null )return pe;
            }


            return null;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="assignEditor()">
    protected final WeakHashMap<Property,PropertyEditor> assignedPropertyEditor = new WeakHashMap<Property,PropertyEditor>();

    /**
     * Назначение редактор для указанного свойства
     * @param prop свойство
     * @param pe редактор
     */
    public void assignEditor( Property prop, PropertyEditor pe ){
        if (prop== null) {
            throw new IllegalArgumentException("prop==null");
        }

        synchronized(sync){
            if( pe!=null ){
                assignedPropertyEditor.remove(prop);
            }else{
                assignedPropertyEditor.put(prop, pe);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getPropertyEditorOf( Property p )">
    /**
     * Получение редактора для конкретного свойства
     * @param p свойство
     * @return редактор или null
     */
    public PropertyEditor getPropertyEditorOf( Property p ){
        if( p==null )return null;

        synchronized(sync){
            PropertyEditor ape = assignedPropertyEditor.get(p);
            PropertyEditor pe = ape!=null ? ape : getPropertyEditorOf(p.getPropertyDescriptor());

            UiBean uib = p.getUiBean();

            if( pe instanceof SetPropertyEditorOpts ){
                if( uib!=null && uib.editorOpts()!=null && uib.editorOpts().length()>0 ){
                    ((SetPropertyEditorOpts)pe).setPropertyEditorOpts(uib.editorOpts());
                }else{
                    ((SetPropertyEditorOpts)pe).setPropertyEditorOpts("");
                }
            }

            if( pe==null ){
                PropertyEditor tpe = getTypeEditor( p.getPropertyType() );

                if( tpe instanceof SetPropertyEditorOpts ){
                    if( uib!=null && uib.editorOpts()!=null && uib.editorOpts().length()>0 ){
                        ((SetPropertyEditorOpts)tpe).setPropertyEditorOpts(uib.editorOpts());
                    }else{
                        ((SetPropertyEditorOpts)tpe).setPropertyEditorOpts("");
                    }
                }

                if( tpe!=null )return tpe;
            }

            return pe;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getPropertyEditorOf( PropertyDescriptor pd )">
    /**
     * Получение редактора для конкретного свойства
     * @param propertyDescriptor свойство
     * @return редактор или null
     */
    public PropertyEditor getPropertyEditorOf( PropertyDescriptor propertyDescriptor ){
        if( propertyDescriptor==null )return null;

        PropertyEditor pe = null;

        synchronized( sync ){
            Class cls = propertyDescriptor.getPropertyEditorClass();
            if( cls != null && !failedPropertyEditor.containsKey(cls) ){
                try {
                    Object o = cls.newInstance();
                    if( o instanceof java.beans.PropertyEditor ){
                        pe = (java.beans.PropertyEditor)o;
                        propertyDescEditorMap.put(propertyDescriptor, pe);
                    }else{
                        failedPropertyEditor.put(cls, true);
                    }
                } catch (InstantiationException ex) {
                    Logger.getLogger(PropertyDB.class.getName()).log(Level.SEVERE, null, ex);
                    failedPropertyEditor.put(cls, true);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(PropertyDB.class.getName()).log(Level.SEVERE, null, ex);
                    failedPropertyEditor.put(cls, true);
                }
            }
        }

        return pe;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getPaintablesOf() - специф. отображение">
    /**
     * Возвращает редакторы поддерживающие отображение (isPaintable) указанных типов
     * @param type тип данных который будет отображаться
     * @return Редакторы
     */
    public Set<PropertyEditor> getPaintablesOf( Class type ){
        Set<PropertyEditor> res = new LinkedHashSet<PropertyEditor>();
        if( type==null )return res;

        Set<PropertyEditor> peSet = getTypeEditors().fetch(type);

        for( PropertyEditor pe : peSet ){
            if( pe==null )continue;
            if( pe.isPaintable() ){
                res.add(pe);
            }
        }

        /*peSet.forEach( pe -> {
            if( pe==null )return;
            if( pe.isPaintable() ){
                res.add(pe);
            }
        });*/

        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getFormattersOf() - Форматирование">
    /**
     * Возвращает форматтеры указанного типа данных
     * @param type тип данных который будет отображаться
     * @return форматтеры
     */
    public Set<TreeTableNodeGetFormatOf> getFormattersOf( Class type ){
        Set<TreeTableNodeGetFormatOf> res = new LinkedHashSet<TreeTableNodeGetFormatOf>();
        if( type==null )return res;

        Set<PropertyEditor> peSet = getTypeEditors().fetch(type);

        if( peSet!=null ){
            /*peSet.forEach( pe -> {
                if( pe==null )return;
                if( pe instanceof TreeTableNodeGetFormatOf ){
                    res.add( (TreeTableNodeGetFormatOf)pe );
                }
            });*/
            for( PropertyEditor pe : peSet ){
                if( pe==null )continue;
                if( pe instanceof TreeTableNodeGetFormatOf ){
                    res.add( (TreeTableNodeGetFormatOf) pe );
                }
            };
        }

        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="typeEditors">
    protected final ClassMap<Set<PropertyEditor>> typeEditors = new ClassMap();

    /**
     * Возвращает карту редакторов свойств
     * @return редакторый свойств
     */
    public ClassMap<Set<PropertyEditor>> getTypeEditors(){
        synchronized(sync){
            return typeEditors;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="registerTypeEditor( type, pe )">
    /**
     * Регистрация редактора свойств для заданного типа данных
     * @param type тип данных
     * @param pe редактор
     */
    public void registerTypeEditor( Class type, PropertyEditor pe ){
        PropertyDB.this.registerTypeEditor(type, pe, null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="registerTypeEditor( type, pe, weight )">
    /**
     * Регистрация редактора свойств для заданного типа данных
     * @param type тип данных
     * @param pe редактор
     * @param weight вес/приоритет редактора
     */
    public void registerTypeEditor( Class type, PropertyEditor pe, Double weight ){
        PropertyDB.this.registerTypeEditor(type, pe, weight, null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="registerTypeEditor( type, pe, weight, id )">
    private void registerTypeEditor( Class type, PropertyEditor pe, Double weight, String id ){
        synchronized(sync){
            if( type==null )throw new IllegalArgumentException( "type==null" );
            if( pe==null )throw new IllegalArgumentException( "pe==null" );

            logFine( "registry {0} editor ({1})", type, pe.getClass() );

            Set<PropertyEditor> editors = typeEditors.get(type);
            if( editors==null ){
                editors = new LinkedHashSet<PropertyEditor>();
                typeEditors.put(type, editors);
            }

            editors.add(pe);

            failedPropertyEditor.clear();

            if( weight!=null )propertyEditorWeightMap.put(pe, weight);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getTypeEditor( type )">
    /**
     * Возвращает редактор свойства для заданного типа данных
     * @param type тип данных
     * @return редактор или null
     */
    public PropertyEditor getTypeEditor( Class type ){
        if( type==null )return null;

        synchronized(sync){
            Set<PropertyEditor> editors = typeEditors.fetch(type);
            if( editors==null || editors.isEmpty() )return null;

            Double minw = null;
            PropertyEditor minpe = null;

            Double maxw = null;
            PropertyEditor maxpe = null;

            for( PropertyEditor pe : editors ){
                double w = getWeightOf(pe);
                if( minw==null || minw > w ){
                    minw = w;
                    minpe = pe;
                }
                if( maxw==null || maxw < w ){
                    maxw = w;
                    maxpe = pe;
                }
            }

            return maxpe;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getWeightOf( pe )">
    protected final Map<PropertyEditor,Double> propertyEditorWeightMap = new LinkedHashMap();

    /**
     * Получение веса/приоритета редактора
     * @param pe редактор
     * @return вес
     */
    public double getWeightOf( PropertyEditor pe ){
        if( pe==null )return -99;

        synchronized(sync){

            Double w = propertyEditorWeightMap.get(pe);
            if( w!=null )return w;

            if( pe.supportsCustomEditor() )return 2;

            return 1;
        }
    }

    /**
     * Указание веса/приоритета редактора
     * @param pe редактор
     * @param weight вес/приоритет
     */
    public void setWeightOf( PropertyEditor pe, double weight ){
        if( pe==null )return;

        synchronized( sync ){
            propertyEditorWeightMap.put(pe, weight);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="isExpandableType( type ):boolean">
    /**
     * Возвращает признак, что указанный тип данных подвергается интроспекции
     * @param t тип данных
     * @return true - подвергать интроспекции / false - оставить как есть.
     */
    public boolean isExpandableType( Class t ){
        synchronized(sync){
            if( SimpleTypes.isSimple(t) )return false;
            if( t==String.class )return false;
            return true;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="beanReaders : Set<ReadBeanNodes>">
    /**
     * Чтение дочерних узлов
     */
    protected final LinkedHashSet<ReadBeanNodes> beanReaders = new LinkedHashSet<ReadBeanNodes>();

    /**
     * Возвращает "читателей" дочерних узлов
     * @return "читателей" дочерних узлов
     */
    public Set<ReadBeanNodes> getBeanNodeReaders(){
        synchronized(sync){
            return beanReaders;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="registerReadBeanNodes(r) -  Регистрация читателя дочерних узлов бина">
    /**
     * Регистрация читателя дочерних узлов бина
     * @param rb читатель
     */
    public void registerReadBeanNodes( ReadBeanNodes rb ){
        if( rb==null )throw new IllegalArgumentException( "rb==null" );
        synchronized(sync){
            boolean addedNew = beanReaders.add(rb);
            if( addedNew ){
                logFine("registerReadBean {0}", rb);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="filter(properties,uib):properties - Фильтрация свойств">
    /**
     * Фильтрация свойств
     * @param properties свойства
     * @param uib условия фильтра
     * @return отфильтрованные свойства
     */
    public Set<Property> filter( Set<Property> properties, UiBean uib ){
        synchronized(sync){
            if( properties==null )throw new IllegalArgumentException( "properties==null" );
            if( properties.isEmpty() )return properties;

            LinkedHashSet<String> hiddenProperties = new LinkedHashSet<String>();
            if( uib!=null ){
                hiddenProperties.addAll( Arrays.asList(uib.hiddenPeroperties()) );
            }

            /*LinkedHashSet<String> hiddenProperties = new LinkedHashSet<String>(
                uib!=null
                ? Arrays.asList(uib.hiddenPeroperties())
                : Arrays.asList()
            );*/

            LinkedHashSet<Property> removeset = new LinkedHashSet<Property>();
            for( Property prop2 : properties ){
                //properties.forEach( prop2 -> {
                // remove not readable
                if( prop2.getReadMethod() == null && prop2.getReadFn() == null ){
                    removeset.add(prop2);
                    //return;
                    continue;
                }

                // remove hidden
                UiBean cuib = prop2.getUiBean();
                if( cuib!=null ){
                    if( cuib.forceHidden() ){
                        removeset.add(prop2);
                        //return;
                        continue;
                    }
                }

                if( hiddenProperties.contains(prop2.getName()) ){
                    removeset.add(prop2);
                    //return;
                    continue;
                }
            }

            properties.removeAll(removeset);
            return properties;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="propertySettings">
    private volatile Map<String,Map<String,PropertySettings>> propertySettings;

    private Map<String,Map<String,PropertySettings>> getPropertySettingsMap(){
        if( propertySettings!=null )return propertySettings;
        synchronized(sync){
            if( propertySettings!=null )return propertySettings;
            propertySettings = new TreeMap<>();
            return propertySettings;
        }
    }

    private Map<String,PropertySettings> getPropertySettingsOf( Class cls ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }
        synchronized(sync){
            return getPropertySettingsOf( cls.getName() );
        }
    }

    private Map<String,PropertySettings> getPropertySettingsOf( String clsName ){
        if (clsName== null) {
            throw new IllegalArgumentException("cls==null");
        }

        synchronized(sync){
            Map<String,Map<String,PropertySettings>> mm = getPropertySettingsMap();
            Map<String,PropertySettings> m = mm.get(clsName);
            if( m==null ){
                m = new TreeMap<String,PropertySettings>();
                mm.put(clsName, m);
            }

            return m;
        }
    }

    private void setPropertySettingsOf( Class cls, Map<String,PropertySettings> m ){
        if (cls== null) {
            throw new IllegalArgumentException("cls==null");
        }

        synchronized(sync){
            setPropertySettingsOf(cls.getName(), m);
        }
    }

    private void setPropertySettingsOf( String clsName, Map<String,PropertySettings> m ){
        if (clsName== null) {
            throw new IllegalArgumentException("clsName==null");
        }

        synchronized(sync){
            if( m==null ){
                getPropertySettingsMap().remove(clsName);
            }else{
                getPropertySettingsMap().put(clsName, m);
            }
        }
    }

    private PropertySettings hasPropertySettings( Class cls, String name ){
        if( cls==null || name==null )return null;

        Map<String,Map<String,PropertySettings>> psMap = getPropertySettingsMap();
        if( psMap==null )return null;
        if( !psMap.containsKey(cls.getName()) )return null;

        Map<String,PropertySettings> m = psMap.get(cls.getName());
        if( m==null )return null;

        if( !m.containsKey(name) )return null;

        PropertySettings ps = m.get(name);
        return ps;
    }

    private PropertySettings getPropertySettingsOf( Class cls, String name ){
        if (name== null) {
            throw new IllegalArgumentException("name==null");
        }

        synchronized(sync){
            Map<String,PropertySettings> m = getPropertySettingsOf(cls);
            PropertySettings ps = m.get( name );
            if( ps==null ){
                ps = new PropertySettings();
                m.put(name, ps);
            }

            return ps;
        }
    }

    private void setPropertySettingsOf( Class cls, String name, PropertySettings ps ){
        if (name== null) {
            setPropertySettingsOf(cls, null);
            return;
        }

        synchronized(sync){
            Map<String,PropertySettings> m = getPropertySettingsOf(cls);
            if( ps==null ){
                m.remove(name);
            }else{
                m.put(name, ps);
            }
        }
    }

    private synchronized void setPropertySettingsOf( String clsName, String name, PropertySettings ps ){
        if (name== null) {
            setPropertySettingsOf(clsName, null);
            return;
        }

        synchronized(sync){
            Map<String,PropertySettings> m = getPropertySettingsOf(clsName);
            if( ps==null ){
                m.remove(name);
            }else{
                m.put(name, ps);
            }
        }
    }

    /**
     * Возвращает настройки свойства
     * @param pd свойство
     * @return настройки или null
     */
    public PropertySettings getPropertySettingsOf( PropertyDescriptor pd ){
        Method mread = pd.getReadMethod();
        Method mwrite = pd.getWriteMethod();

        Class cls = mread!=null ? mread.getDeclaringClass() : ( mwrite!=null ? mwrite.getDeclaringClass() : null );
        if( cls==null )return null;

        /*String rname = mread!=null ? mread.getName() : null;
        String wname = mwrite!=null ? mwrite.getName() : null;
        String name = null;

        if( rname!=null && rname.startsWith(rname) ){

        }*/

        String name = pd.getName();
        if( name==null ){
            return null;
        }

        return getPropertySettingsOf(cls, name);
    }

    /**
     * Возвращает настройки свойства
     * @param cls класс
     * @param name свойство класса
     * @return настройки или null
     */
    public synchronized PropertySettings getPropertySetting( Class cls, String name ){
        return getPropertySettingsOf(cls, name);
    }

    /**
     * Возвращает настройки свойства
     * @param clsName класс
     * @param name свойство класса
     * @param createIfNotExists создавать настройки по умолчанию
     * @return настройки или null
     */
    public synchronized PropertySettings getPropertySettings( String clsName, String name, boolean createIfNotExists ){
        if (clsName== null) {
            throw new IllegalArgumentException("clsName==null");
        }

        Map<String,PropertySettings> m = getPropertySettingsOf(clsName);
        PropertySettings ps = m.get( name );
        if( ps==null && createIfNotExists ){
            ps = new PropertySettings();
            m.put(name, ps);
        }

        return ps;
    }

    /**
     * Указывает настройки свойства
     * @param clsName класс
     * @param propertyName свойство класса
     * @param ps настройки
     */
    public void setPropertySettings( Class clsName, String propertyName, PropertySettings ps ){
        if (clsName== null) {
            throw new IllegalArgumentException("clsName==null");
        }
        if (propertyName== null) {
            throw new IllegalArgumentException("propertyName==null");
        }
        setPropertySettingsOf( clsName, propertyName, ps );
    }

    /**
     * Указывает настройки свойства
     * @param clsName класс
     * @param propertyName свойство класса
     * @param ps настройки
     */
    public void setPropertySettings( String clsName, String propertyName, PropertySettings ps ){
        if (clsName== null) {
            throw new IllegalArgumentException("clsName==null");
        }
        if (propertyName== null) {
            throw new IllegalArgumentException("propertyName==null");
        }
        setPropertySettingsOf( clsName, propertyName, ps );
    }

    protected WeakHashMap<PropertyDescriptor,Boolean> appliedPropertySettings = new WeakHashMap<PropertyDescriptor,Boolean>();

    /**
     * Сброс истории примененных настроек
     */
    public void propertySettingsCacheDrop(){
        synchronized(sync){
            appliedPropertySettings.clear();
        }
    }

    /**
     * Сброс истории примененных настроек и самих настроек
     */
    public synchronized void propertySettingsClear(){
        synchronized(sync){
            appliedPropertySettings.clear();
            getPropertySettingsMap().clear();
        }
    }

    /**
     * Создание свойства для указанного объекта
     * @param bean объект
     * @param pd описание свойства
     * @return свойство
     */
    public synchronized Property createProperty( Object bean, PropertyDescriptor pd ){
        Property prop = new Property(bean, pd);

        if( pd!=null ){
            synchronized(sync){
                Boolean t = appliedPropertySettings.get(pd);
                if( t==null ){
                    PropertySettings ps = getPropertySettingsOf(pd);
                    if( ps!=null ){
                        ps.applyTo(prop,this);
                        appliedPropertySettings.put(pd, true);
                    }
                }
            }
        }

        return prop;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="allPropertyEditors - Перечисление всех редакторов">
    /**
     * Получение всех редакторов
     * @return все редакторы
     */
    public Eterable<PropertyEditor> getAllPropertyEditors(){
        List<Iterable<PropertyEditor>> list = new ArrayList<>();
        synchronized(sync){
            //getTypeEditors().forEach( (type,pset) -> {list.add(pset);} );
            for( Map.Entry<Class,Set<PropertyEditor>> en : getTypeEditors().entrySet() ){
                Class type = en.getKey();
                Set<PropertyEditor> pset = en.getValue();
                list.add(pset);
            }

            if( propertyEditorWeightMap!=null ){
                list.add( propertyEditorWeightMap.keySet() );
            }
            if( namedEditors!=null ){
                list.add( namedEditors.values() );
            }
        }
        var itr = Eterable.<PropertyEditor>empty().union(list);
        return itr;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="export">
    public class ExportPropertySettings implements Runnable {
        protected Consumer<Pair<PropertyID,PropertySettings>> consumer;

        public synchronized Consumer<Pair<PropertyID, PropertySettings>> getConsumer() {
            return consumer;
        }

        public synchronized void setConsumer(Consumer<Pair<PropertyID, PropertySettings>> consumer) {
            this.consumer = consumer;
        }

        @Override
        public synchronized void run() {
            if( consumer==null )return;

            synchronized( PropertyDB.this ){
                for( Map.Entry<String,Map<String,PropertySettings>> en : getPropertySettingsMap().entrySet() ){
                    String clsName = en.getKey();
                    Map<String,PropertySettings> propMap = en.getValue();
                    for( Map.Entry<String,PropertySettings> en2 : propMap.entrySet() ){
                        String propName = en2.getKey();
                        PropertySettings ps = en2.getValue();
                        if( propName==null )return;
                        if( ps==null )return;

                        PropertyID pid = new PropertyID(clsName, propName);
                        consumer.accept(Pair.<PropertyID,PropertySettings>of(pid, ps));
                    }
                }
            }
        }
    }

    public class Export implements Runnable {
        protected Set<Runnable> exports = new LinkedHashSet<Runnable>();

        public Export propertySettings( Consumer<Pair<PropertyID, PropertySettings>> consumer ){
            if (consumer== null) {
                throw new IllegalArgumentException("consumer==null");
            }

            ExportPropertySettings exp = new ExportPropertySettings();
            exports.add(exp);

            exp.setConsumer(consumer);
            return this;
        }

        @Override
        public void run() {
            for( Runnable e : exports ){
                if(e!=null){
                    e.run();
                }
            }
        }
    }

    public Export exports(){
        return new Export();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="import">
    public class ImportPropertySettings implements Runnable {
        protected Iterable<Pair<PropertyID,PropertySettings>> source;

        public synchronized Iterable<Pair<PropertyID, PropertySettings>> getSource() {
            return source;
        }

        public synchronized void setSource(Iterable<Pair<PropertyID, PropertySettings>> source) {
            this.source = source;
        }

        public ImportPropertySettings source(Iterable<Pair<PropertyID, PropertySettings>> source) {
            setSource(source);
            return this;
        }

        protected Import parent;

        public Import getParent() {
            return parent;
        }

        public void setParent(Import parent) {
            this.parent = parent;
        }

        public Import set(){
            if( parent!=null ){
                parent.getPropertySettings().add(this);
            }
            return parent;
        }

        @Override
        public synchronized void run() {
            if( source!=null ){
                for( Pair<PropertyID, PropertySettings> en : source ){
                    PropertyID pid = en.a();
                    PropertySettings ps = en.b();
                    if( pid!=null && ps!=null && pid.getName()!=null && pid.getType()!=null ){
                        setPropertySettingsOf(pid.getType(), pid.getName(), ps);
                    }
                }
            }
        }
    }

    public class Import implements Runnable {
        protected Set<ImportPropertySettings> importPropSettings = new LinkedHashSet<ImportPropertySettings>();

        public Set<ImportPropertySettings> getPropertySettings(){
            return importPropSettings;
        }

        public ImportPropertySettings propertySettings(){
            ImportPropertySettings ips = new ImportPropertySettings();
            ips.setParent(this);
            return ips;
        }

        public Import propertySettings(Iterable<Pair<PropertyID, PropertySettings>> source){
            ImportPropertySettings ips = new ImportPropertySettings();
            ips.setParent(this);
            ips.setSource(source);
            return this;
        }

        @Override
        public synchronized void run() {
            if( !importPropSettings.isEmpty() ){
                appliedPropertySettings.clear();
                getPropertySettingsMap().clear();

                for( ImportPropertySettings i : importPropSettings ){
                    i.run();
                }
            }
        }
    }

    public Import imports(){
        return new Import();
    }
//</editor-fold>

    /**
     * Последовательность дочерних узлов
     */
    private String[] beanReadersOrder = new String[]{
        ReadBeanProperties.class.getName(),
        ReadBeanArray.class.getName(),
        ReadBeanMap.class.getName(),
        ReadBeanList.class.getName(),
        ReadBeanSet.class.getName(),
    };

    //<editor-fold defaultstate="collapsed" desc="propertySettingsApplyAtRead">
    protected boolean propertySettingsApplyAtRead = true;

    /**
     * Применение настроек свойство во время чтения их из объекта
     * @return true применянть настройки
     */
    public boolean isPropertySettingsApplyAtRead() {
        return propertySettingsApplyAtRead;
    }

    /**
     * Применение настроек свойство во время чтения их из объекта
     * @param propertySettingsApplyAtRead true применянть настройки
     */
    public void setPropertySettingsApplyAtRead(boolean propertySettingsApplyAtRead) {
        this.propertySettingsApplyAtRead = propertySettingsApplyAtRead;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="readBeadNodes(bean):Iterable - Читение дочерних узлов">
    /**
     * Читение дочерних узлов
     * @param bean бин
     * @return дочерние узлы или null
     */
    public Iterable readBeadNodes( Object bean ){
        if( bean==null )return null;
        synchronized( sync ){
            final Eterable[] res = new Eterable[1];

            Map<Integer,Set<ReadBeanNodes>> order = new TreeMap<Integer,Set<ReadBeanNodes>>();

            for( ReadBeanNodes reader : beanReaders ){
                if( reader==null )continue;
                int pos = Text.indexOf(reader.getClass().getName(), beanReadersOrder);
                Set<ReadBeanNodes> s = order.get(pos);
                if( s==null ){ s = new LinkedHashSet<ReadBeanNodes>(); order.put(pos, s); }
                s.add(reader);
            }

            for( Map.Entry<Integer,Set<ReadBeanNodes>> en : order.entrySet() ){
                Integer k = en.getKey();
                Set<ReadBeanNodes> s = en.getValue();

                for( ReadBeanNodes reader : s ){
                    Eterable ires = reader.readBeanNodes(bean);
                    if( ires==null )continue;

                    if( res[0]==null ){
                        res[0] = ires;
                    }else{
                        res[0] = res[0].union(ires);
                    }
                }
            }

            Eterable iter = res[0];

            if( iter!=null && propertySettingsApplyAtRead ){
                final Class beanCls = bean.getClass();
                final UiBean uib = (UiBean)beanCls.getAnnotation(UiBean.class);
                final String[] hiddenPropName = uib!=null ? uib.hiddenPeroperties() : new String[]{};

                Predicate filter = new Predicate<>() {
                    @Override
                    public boolean test(Object value) {
                        if( value instanceof Property ){
                            Property p = (Property)value;
                            if( p.isHidden() )return false;
                            if( Text.in(p.getName(), hiddenPropName) ){
                                return false;
                            }

                            PropertySettings ps = hasPropertySettings(beanCls, p.getName());
                            if( ps!=null ){
                                if( ps.getHidden()!=null && ps.getHidden() ){
                                    return false;
                                }

                                String dname = ps.getDisplayName();
                                if( dname!=null )p.setDisplayName(dname);

                                Boolean ro = ps.getReadOnly();
                                if( ro!=null )p.setReadOnly(ro);

                                Boolean exp = ps.getExpert();
                                if( exp!=null ){
                                    p.setExpert(exp);
                                }

                                Boolean pref = ps.getPreferred();
                                if( pref!=null ){
                                    p.setPreferred(pref);
                                }

                                String sdesc = ps.getShortDescription();
                                if( sdesc!=null ){
                                    p.setShortDescription(sdesc);
                                }

                                Boolean constr = ps.getConstrained();
                                if( constr!=null ){
                                    p.setConstrained(constr);
                                }
                            }
                        }
                        return true;
                    }
                };

                iter = iter.filter(filter);
            }

            return iter;
        }
    }
    //</editor-fold>
}
