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

import javax.swing.Action;

/**
 * Пункт меню - действие
 * @author gocha
 */
public class MenuActionItem extends MenuItem
{
    /**
     * Конструктор по умолчанию
     */
    public MenuActionItem(){
    }

    /**
     * Конструктор
     * @param action действие
     */
    public MenuActionItem(Action action){
        this(null,action);
    }

    /**
     * Конструктор
     * @param parent "Родительский" контейнер для пункта меню
     * @param action Действие
     */
    public MenuActionItem(MenuContainer parent,Action action){
        setAction(action);
        if( parent!=null )parent.getChildren().add( this );
    }

    protected Action action = null;

    /**
     * Указывает действие связанное с меню
     * @return Действие
     */
    public Action getAction()
    {
        return action;
    }

    /**
     * Указывает действие связанное с меню
     * @param action Действие
     */
    public void setAction(Action action)
    {
        Object old = this.action;
        this.action = action;
        firePropertyChanged("action", old, action);
        fireMenuEvent(new PropertyChangedEvent(this,"action",old,this.action));
    }

    // <editor-fold defaultstate="collapsed" desc="Тип представления">
    /**
     * Указывает тип представления action
     */
    public static enum Type
    {

        /**
         * Использовать по умолчанию
         */
        Default,
        /**
         * Исползовать checkbox
         */
        Checked
    }
    private Type type = Type.Default;

    /**
     * Указывает тип представления
     * @return тип представления
     */
    public Type getType() {
        if (type == null) {
            type = Type.Default;
        }
        return type;
    }

    /**
     * Указывает тип представления
     * @param type тип представления
     */
    public void setType(Type type) {
        Object old = this.type;
        this.type = type;
        firePropertyChanged("type", old, getType());
        fireMenuEvent(new PropertyChangedEvent(this, "type", old, getType()));
    }
    // </editor-fold>
}
