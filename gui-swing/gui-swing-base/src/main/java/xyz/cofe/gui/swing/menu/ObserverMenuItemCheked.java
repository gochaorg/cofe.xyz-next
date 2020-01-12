package xyz.cofe.gui.swing.menu;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author gocha
 */
public class ObserverMenuItemCheked extends JCheckBoxMenuItem
    implements PropertyChangeListener,
    ObserverMenu
{
    private MenuItem menu = null;

    @Override
    public MenuItem getMenu()
    {
        return menu;
    }

    @Override
    public void setMenu(MenuItem menu)
    {
        if( this.menu!=null )detach();
        this.menu = menu;
        refreshView();
        if( this.menu!=null )attach();
    }

    private void detach(){
        menu.removePropertyChangeListener(this);
    }

    private void attach(){
        menu.addPropertyChangeListener(this);
    }

    private void refreshView(){
        if( menu==null || !(menu instanceof MenuActionItem) ){
            setAction(null);
            setText("???");
        }else{
            setAction(((MenuActionItem)menu).getAction());
        }
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt)
    {
        if( evt==null )return;
        refreshView();
    }
}
