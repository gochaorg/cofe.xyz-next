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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.Action;

import xyz.cofe.collection.NodesExtracter;
import xyz.cofe.gui.swing.al.BasicAction;
import xyz.cofe.iter.Eterable;

/**
 * Пункт меню
 * @author gocha
 */
public abstract class MenuItem
{
    private String id = null;
    private MenuContainer parent = null;
    
    /**
     * Событие изменения свойства меню
     */
    public class PropertyChangedEvent extends MenuEvent
    {
        private String name = null;
        private Object oldValue = null;
        private Object newValue = null;
        
        /**
         * Конструктор
         * @param source исходный объект / объект владельца
         * @param name имя свойства
         * @param oldValue предыдущее значение
         * @param newValue текущее значение
         */
        public PropertyChangedEvent(Object source, String name, Object oldValue, Object newValue){
            super(source);
            this.name = name;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        /**
         * Возвращает имя свойства
         * @return имя свойства
         */
        public String getProperty() {
            return name;
        }

        /**
         * Возвращает предыдущее значение
         * @return предыдущее значение
         */
        public Object getNewValue() {
            return newValue;
        }

        /**
         * Возвращает текущее значение
         * @return текущее значение
         */
        public Object getOldValue() {
            return oldValue;
        }
        
        /**
         * Возвращает пункт меню чье значение изменилось
         * @return пункт меню
         */
        public MenuItem getMenuItem(){
            return (MenuItem)this.source;
        }
    }
    
    /**
     * Указывает идентификатор
     * @return Идентификатор
     */
    public String getId()
    {
        return id;
    }

    /**
     * Указывает идентификатор
     * @param id Идентификатор
     */
    public void setId(String id)
    {
        Object old = this.id;
        this.id = id;
        firePropertyChanged("id", old, this.id);
        fireMenuEvent(new PropertyChangedEvent(this,"id",old,this.id));
    }

    /**
     * Указывает родительский пункт меню
     * @return Родительский пункт или null
     */
    public MenuContainer getParent()
    {
        return parent;
    }

    /**
     * Указывает родительский пункт меню
     * @param parent Родительский пункт или null
     */
    public void setParent(MenuContainer parent)
    {
        Object old = this.parent;
        this.parent = parent;
        firePropertyChanged("parent", old, this.parent);
    }

    /**
     * Возвращает путь от корня до текущего элемента
     * @return Путь
     */
    public List<MenuItem> getPath(){
        ArrayList<MenuItem> path = new ArrayList<MenuItem>();
        MenuItem mi = this;
        while(true){
            if( path.contains(mi) )break;
            if( path.size()>0 )
                path.add(0,mi);
            else
                path.add(mi);
            mi = mi.getParent();
            if( mi==null )break;
        }
        return path;
    }
    
    protected Collection<MenuListener> menuListener = null;
    
    /**
     * Добавляет подписчика на события пункта меню
     * @param listener подписчик
     */
    public void addMenuListener(MenuListener listener){
        if( listener==null )return;
        if( menuListener==null )menuListener = new HashSet<MenuListener>();
        menuListener.add(listener);
    }
    
    /**
     * Удаляет подписчика на события пункта меню
     * @param listener подписчик
     */
    public void removeMenuListener(MenuListener listener){
        if( menuListener==null )return;
        menuListener.remove(listener);
    }
    
    /**
     * Уведомляет подписчиков о событии пункта меню
     * @param event событие пункта меню
     */
    protected void fireMenuEvent( MenuEvent event ){
        if( event==null )return;
        if( menuListener!=null ) for( MenuListener l : menuListener )l.menuEvent(event);
        if( parent!=null )parent.fireMenuEvent(event);
    }

    /**
     * Подписчики изменения свойств
     */
    protected Collection<PropertyChangeListener> propertiesListeners = new HashSet<PropertyChangeListener>();

    /**
     * Добавляет подписчика свойств
     * @param l Подписчик
     */
    public void addPropertyChangeListener(PropertyChangeListener l){
        if( l!=null )
            propertiesListeners.add(l);
    }

    /**
     * Удаляет подписчика свойств
     * @param l Подписчик
     */
    public void removePropertyChangeListener(PropertyChangeListener l){
        propertiesListeners.remove(l);
    }

    /**
     * Возвращает подписчиков свойств
     * @return Подписчики
     */
    public Collection<PropertyChangeListener> getPropertyChangeListeners(){ return propertiesListeners; }

    /**
     * Уведомляет о измении свойства
     * @param name имя свойства
     * @param old предыдущее значение
     * @param _new текузее значение
     */
    protected void firePropertyChanged(String name,Object old,Object _new){
        for( Object o : propertiesListeners.toArray() ){
            if( o instanceof PropertyChangeListener )
                ((PropertyChangeListener)o).propertyChange(new PropertyChangeEvent(this, name, old, _new));
        }
    }

    /**
     * Поиск меню по ID
     * @param root Корень начиная с которого осуществлять поиск
     * @param id Искомый ID
     * @return Найденые меню
     */
    public static List<MenuItem> findMenuItemById(MenuItem root,String id){
        if (root== null) {
            throw new IllegalArgumentException("root==null");
        }
        if (id== null) {
            throw new IllegalArgumentException("id==null");
        }

        ArrayList<MenuItem> res = new ArrayList<MenuItem>();
        for( MenuItem mi : iterable(root) ){
            if( mi==null )continue;
            String mid = mi.getId();
            boolean eq = id.equals(mid);
            if( eq ){
                res.add( mi );
            }
        }
        return res;
    }
    
    /**
     * Поиск меню по ID.
     * @param root Корень начиная с которого осуществлять поиск
     * @param id Искомый ID
     * @return Найденое меню или null
     */
    public static MenuItem findById(MenuItem root,String id){
        if( root==null )throw new IllegalArgumentException( "root==null" );
        if( id==null )throw new IllegalArgumentException( "id==null" );
        List<MenuItem> lmi = findMenuItemById(root, id);
        if( lmi.size()>0 )return lmi.get(0);
        return null;
    }

    /**
     * Создает итератор (обход деоева) по элементам меню
     * @param root Корень меню
     * @return Итератор
     */
    public static Eterable<MenuItem> iterable(MenuItem root){
        if (root== null) {
            throw new IllegalArgumentException("root==null");
        }
        return Eterable.<MenuItem>tree( root, (NodesExtracter)(new MenuItemNodesExtracter()) ).walk();
    }

    /**
     * Сверяет совпадение id меню с указаным
     * @param id Искомое значение
     * @return Предикат
     */
    public static Predicate<MenuItem> menuIdPredicate( String id){
        final String targetId = id;
        return value->{
            if( value!=null ){
                String srcId = value.getId();
                if( srcId==null && targetId==null )return true;
                if( srcId!=null && targetId==null )return false;
                if( srcId==null && targetId!=null )return true;
                return srcId.equals(targetId);
            }
            return false;
        };
    }

    /**
     * Удаление дочерних меню/элементов
     * @param mi родительский пункт меню
     * @return удаленные элементы
     */
    public static Map<MenuContainer,List<MenuItem>> clearChildren(Iterable<MenuItem> mi){
        Map<MenuContainer,List<MenuItem>> removed = new LinkedHashMap<MenuContainer, List<MenuItem>>();
        if( mi == null )return removed;
        for( MenuItem mi1 : mi ){
            if( mi1==null )continue;
            if( !(mi1 instanceof MenuContainer) )continue;
            removed.put(
                (MenuContainer)mi1, clearChildren(mi1));
        }
        return removed;
    }
    
    /**
     * Удаление дочерних меню/элементов
     * @param mi родительский пункт меню
     * @return удаленные элементы
     */
    public static List<MenuItem> clearChildren(MenuItem mi){
        List<MenuItem> removed = new ArrayList<MenuItem>();
        if( mi == null )return removed;
        if( !(mi instanceof MenuContainer) )return removed;
        MenuContainer mc = (MenuContainer)mi;
        MenuItem[] mch = mc.getChildren().toArray(new MenuItem[]{});
        for( MenuItem m : mch ){
            if( m!=null ){
                mc.getChildren().remove(m);
                removed.add(m);
            }
        }
        return removed;
    }

    /**
     * Добавляет к родительскому пункту меню дочерний пункт
     * @param parent Родительский пункт, производится проверка instanceof.
     * @param child Дочерний пункт
     * @return Успешно/или нет
     */
    public static boolean addChild(MenuItem parent,MenuItem child){
        if (parent== null) {
            throw new IllegalArgumentException("parent==null");
        }
        if (child== null) {
            throw new IllegalArgumentException("child==null");
        }
        if( parent instanceof MenuContainer ){
            MenuContainer mc = (MenuContainer)parent;
            return mc.getChildren().add(child);
        }
        return false;
    }

    /**
     * Добавляет к родительскому пункту меню дочерний пункт
     * @param parents Родительские пункты
     * @param childs Дочерние пункты
     * @return Кол-во добавленых пунктов (parents.count * childs.count)
     */
    public static int addChild(Iterable<MenuItem> parents,Iterable<MenuItem> childs){
        int co = 0;
        if (parents== null) {
            throw new IllegalArgumentException("parents==null");
        }
        if (childs== null) {
            throw new IllegalArgumentException("childs==null");
        }
        for( MenuItem miParent : parents ){
            if( miParent==null )continue;
            for( MenuItem miChild : childs ){
                if( miChild==null )continue;
                if( addChild(miParent, miChild) )co++;
            }
        }
        return co;
    }

    /**
     * Удаляет дочерние элементы из контейнера
     * @param parent контейнер
     * @param child дочерний элемнт
     */
    public static void removeChild(MenuItem parent,MenuItem child){
        if (parent== null) {
            throw new IllegalArgumentException("parent==null");
        }
        if (child== null) {
            throw new IllegalArgumentException("child==null");
        }
        if( parent instanceof MenuContainer ){
            MenuContainer mc = (MenuContainer)parent;
            mc.getChildren().remove(child);
        }
    }

    /**
     * Удаляет дочерние элементы из контейнера
     * @param parent контейнер
     * @param child дочерний элемнт
     */
    public static void removeChild(Iterable<MenuItem> parent,Iterable<MenuItem> child){
        if (parent== null) {
            throw new IllegalArgumentException("parent==null");
        }
        if (child== null) {
            throw new IllegalArgumentException("child==null");
        }
        for( MenuItem miP : parent ){
            if( miP==null )continue;
            for( MenuItem miC : child ){
                if( miC==null )continue;
                removeChild(miP, miC);
            }
        }
    }

    /**
     * Создание последовательности с единственным элементов
     * @param mi элемент
     * @return последовательность
     */
    public static Iterable<MenuItem> single(MenuItem mi){
        return Eterable.single(mi);
    }
    
    /**
     * Создание "пути" из текстового описания. Например: файл/открыть/xml
     * @param mi пункт меню
     * @return путь
     */
    public static List<String> pathof( MenuItem mi ){
        List<String> path = new ArrayList<String>();
        if( mi==null )return path;
        
        if( mi instanceof MenuContainer ){
            path.add( ((MenuContainer)mi).getText() );
        }
        
        while(true){
            MenuContainer miParent = mi.getParent();
            if( miParent==null )break;
            
            path.add( miParent.getText() );
            mi = miParent;
        }
        
        Collections.reverse(path);
        return path;
    }
    
    private static Function<MenuItem,Action> convertMenuItemToAction = from->{
        if( from instanceof MenuActionItem ){
            MenuActionItem mai = (MenuActionItem)from;
            return mai.getAction();
        }
        return null;
    };
    
    /**
     * Создание последовательности действий содержащиеся в меню
     * @param mis меню
     * @return действия
     */
    public static Eterable<Action> actionsOf( Iterable<MenuItem> mis ){
        if( mis==null )return Eterable.empty();

        Eterable itr = Eterable.empty();
        int idx = -1;
        for( MenuItem mi : mis ){
            if( mi==null )continue;
            idx++;
            if( idx>0 ){
                itr = itr.union(actionsOf(mi));
            }else{
                itr = actionsOf(mi);
            }
        }
        return itr;
    }
    
    /**
     * Создание последовательности действий содержащиеся в меню
     * @param mi меню
     * @return действия
     */
    public static Eterable<Action> actionsOf( MenuItem mi ){
        if( mi == null )return Eterable.empty();

        Eterable<MenuItem> allMi = iterable(mi);
        //Iterable<Action> acts = Iterators.convert(allMi, convertMenuItemToAction);
        Eterable<Action> acts = allMi.map(convertMenuItemToAction);
        //Eterable<Action> actsWoNulls = Iterators.notNullFilter(acts);
        Eterable<Action> actsWoNulls = acts.filter(x->x!=null);
        return actsWoNulls;
    }

    /**
     * Фильтрация действий из заданной области
     * @param mi область поиска
     * @param targetPredicate условие поиска
     * @return действия
     */
    public static Eterable<Action> filteredActionsOf( MenuItem mi, Predicate<Action> targetPredicate ){
        if( mi == null )return Eterable.empty();
        if( targetPredicate==null )return actionsOf(mi);
//        return Eterable.predicate(actionsOf(mi), targetPredicate);
        return actionsOf(mi).filter(targetPredicate);
    }

    /**
     * Фильтрация действий из заданной области
     * @param mis область поиска
     * @param targetPredicate условие поиска
     * @return действия
     */
    public static Eterable<Action> filteredActionsOf( Iterable<MenuItem> mis, Predicate<Action> targetPredicate ){
        if( mis == null )return Eterable.empty();
        if( targetPredicate==null )return actionsOf(mis);
        //return Iterators.predicate(actionsOf(mis), targetPredicate);
        return actionsOf(mis).filter(targetPredicate);
    }
    
    /**
     * Фильтрация действий из заданной области,
     * когда действие направлено на класс или подкласс объектов
     * @param mi область поиска
     * @param targets классы объектов
     * @return действия
     */
    public static Eterable<Action> targetedActionsOf( MenuItem mi, Class ... targets )
    {
        if( mi == null || targets==null || targets.length==0 )return Eterable.empty();
        return filteredActionsOf(mi, BasicAction.Filter.targetAssignableFrom(targets));
    }
    
    /**
     * Фильтрация действий из заданной области,
     * когда действие направлено на класс или подкласс объектов
     * @param mis область поиска
     * @param targets классы объектов
     * @return действия
     */
    public static Eterable<Action> targetedActionsOf( Iterable<MenuItem> mis, Class ... targets )
    {
        if( mis == null || targets==null || targets.length==0 )return Eterable.empty();
        return filteredActionsOf(mis, BasicAction.Filter.targetAssignableFrom(targets));
    }
}
