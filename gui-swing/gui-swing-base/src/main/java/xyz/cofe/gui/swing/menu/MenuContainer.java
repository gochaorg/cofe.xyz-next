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

import xyz.cofe.collection.BasicEventList;
import xyz.cofe.ecolls.TripleConsumer;

/**
 * Контейнер для меню
 * @author gocha
 */
public class MenuContainer extends MenuItem
{
    /**
     * Событие изменения структуры дочерних элементов
     */
    public static class MenuContainerEvent extends MenuEvent
    {
        private MenuItem item = null;

        public MenuContainerEvent(MenuContainer parent, MenuItem item){
            super(parent);
            this.item = item;
        }

        public MenuItem getMenuItem() {
            return item;
        }
    }

    /**
     * Событие изменения структуры дочерних элементов
     */
    public static class ItemAddedEvent extends MenuContainerEvent
    {
        public ItemAddedEvent(MenuContainer parent, MenuItem item){
            super(parent,item);
        }
    }

    /**
     * Событие изменения структуры дочерних элементов
     */
    public static class ItemRemovedEvent extends MenuContainerEvent
    {
        private int position;

        public ItemRemovedEvent(MenuContainer parent, MenuItem item, int pos){
            super(parent,item);
            this.position = pos;
        }
        public int getRemovedItemPosition() {
            return position;
        }
    }

    /**
     * Конструктор
     */
    public MenuContainer(){
    }

    /**
     * Конструктор
     * @param text Отображаемый текст меню
     */
    public MenuContainer(String text){
        setText(text);
    }

    /**
     * Конструктор
     * @param parent Родительский пункт меню
     * @param text Отображаемый текст меню
     */
    public MenuContainer(MenuContainer parent,String text){
        setText(text);
        if( parent!=null )parent.getChildren().add(this);
    }

    private Children children = null;

    /**
     * Дочерние пункты меню
     */
    public static class Children extends BasicEventList<MenuItem>
    {
        private MenuContainer owner = null;

        /**
         * Конструктор
         * @param owner Владелец списка дочерних меню
         */
        public Children(MenuContainer owner){
            if (owner == null) {
                throw new IllegalArgumentException("owner == null");
            }
            this.owner = owner;
        }

        /**
         * Указывает владелеца списка дочерних меню
         * @return Владелец списка дочерних меню
         */
        public MenuContainer getOwner()
        {
            return owner;
        }
    }

    /**
     * Дочерние пункты меню
     * @return Дочерние пункты меню
     */
    public Children getChildren(){
        if( children==null ){
            children = new Children(this);
            TripleConsumer<Integer,MenuItem,MenuItem> ls = (idx,old,cur)->{
                if( old!=null ){
                    if( idx==null )throw new IllegalStateException("не передан индекс удаленного элемента меню");
                    fireMenuEvent(new ItemRemovedEvent(MenuContainer.this, old, idx));
                }
                if( cur!=null ){
                    assignParentToChild(cur);
                    fireMenuEvent(new ItemAddedEvent(MenuContainer.this, cur));
                }
            };
            children.onInserted(ls);
            children.onDeleted(ls);
            children.onUpdated(ls);
        }
        return children;
    }

    /**
     * Назначение свойство parent для укзанного дочернего пункта
     * @param child дочерний пункт меню
     */
    protected void assignParentToChild( MenuItem child ){
        if( child!=null ){
            child.setParent(this);
        }
    }

    protected String text = null;
    protected javax.swing.Icon icon = null;

    /**
     * Указывает иконку пункта меню
     * @return Иконка
     */
    public javax.swing.Icon getIcon()
    {
        return icon;
    }

    /**
     * Указывает иконку пункта меню
     * @param icon Иконка
     */
    public void setIcon(javax.swing.Icon icon)
    {
        Object old = this.icon;
        this.icon = icon;
        firePropertyChanged("icon", old, this.icon);
        fireMenuEvent(new PropertyChangedEvent(this,"icon",old,this.icon));
    }

    /**
     * Указывает текст пункта меню
     * @return Текст пункта меню
     */
    public String getText()
    {
        return text;
    }

    /**
     * Указывает текст пункта меню
     * @param text Текст пункта меню
     */
    public void setText(String text)
    {
        Object old = this.text;
        this.text = text;
        firePropertyChanged("text", old, this.text);
        fireMenuEvent(new PropertyChangedEvent(this,"text",old,this.text));
    }
}
