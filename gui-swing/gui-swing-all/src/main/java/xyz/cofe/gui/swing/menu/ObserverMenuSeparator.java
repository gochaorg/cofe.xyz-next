package xyz.cofe.gui.swing.menu;

import javax.swing.*;

/**
 * Разделитель между пунктами меню
 * @author gocha
 */
public class ObserverMenuSeparator extends JSeparator implements ObserverMenu
{
    public ObserverMenuSeparator(){
    }

    private MenuItem menu = null;

    @Override
    public MenuItem getMenu(){
        return menu;
    }

    @Override
    public void setMenu(MenuItem menu){
        this.menu = menu;
    }
}
