package xyz.cofe.gui.swing.menu;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author gocha
 */
public class ObserverMenuItem extends JMenuItem
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
            Action act = ((MenuActionItem)menu).getAction();
            setAction(act);

//            if( act instanceof BasicAction ){
//                BasicAction bact = (BasicAction)act;
//                if( bact.getAccelerator()==null && !bact.getKeyboardShortcuts().isEmpty() ){
//                    for( KeyboardShortcut kbs : bact.getKeyboardShortcuts() ){
//                        KeyStroke ks = kbs.getKeyStroke();
//                        setA
//                    }
//                }
//            }
        }
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt)
    {
        if( evt==null )return;
        refreshView();
    }
}
