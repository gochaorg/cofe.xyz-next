package xyz.cofe.gui.swing.menu;

import javax.swing.*;

/**
 * Пункт меню действие, временное и не для сохранения в MenuStore
 * @author gocha
 */
public class MenuTmpActionItem
    extends MenuActionItem
//implements TransientObject
{
    /**
     * Конструктор по умолчанию
     */
    public MenuTmpActionItem(){
        super();
    }

    /**
     * Конструктор
     * @param action Действие
     */
    public MenuTmpActionItem( Action action){
        super(action);
    }

    /**
     * Конструктор
     * @param parent "Родительский" контейнер для пункта меню
     * @param action Действие
     */
    public MenuTmpActionItem(MenuContainer parent,Action action){
        super(parent,action);
    }
}
