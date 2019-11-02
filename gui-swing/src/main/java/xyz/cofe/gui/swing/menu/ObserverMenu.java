package xyz.cofe.gui.swing.menu;

import xyz.cofe.gui.swing.menu.MenuItem;

/**
 * @author gocha
 */
public interface ObserverMenu
{
    public MenuItem getMenu();
    public void setMenu(MenuItem menu);
}