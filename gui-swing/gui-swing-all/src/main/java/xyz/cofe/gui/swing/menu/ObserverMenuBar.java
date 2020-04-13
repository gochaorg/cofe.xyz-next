package xyz.cofe.gui.swing.menu;

import xyz.cofe.collection.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashSet;

/**
 * В РАЗРАБОТКЕ
 * Менюбар с автматическим обновлением при измении дерва меню
 * @author gocha
 */
public class ObserverMenuBar
    extends JMenuBar
    implements CollectionListener,
    ObserverMenu
{
    /**
     * Конструктор
     */
    public ObserverMenuBar()
    {
    }

    // <editor-fold defaultstate="collapsed" desc="menu">
    private MenuItem menu = null;

    /**
     * Указывает меню
     * @return меню
     */
    public MenuItem getMenu() {
        return menu;
    }

    /**
     * Указывает меню
     * @param menu меню
     */
    public void setMenu( MenuItem menu) {
        detach();
        Object old = this.menu;
        this.menu = menu;
        attach();
        refresh();
        firePropertyChange("menu", old, menu);
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="root visible">
    private boolean rootVisible = false;

    /**
     * Указывает оторбражать/нет корневой элемент меню
     * @return true - оторбажать; false - отображать дочерние как корни
     */
    public boolean isRootVisible() {
        return rootVisible;
    }

    /**
     * Указывает оторбражать/нет корневой элемент меню
     * @param rootVisible true - оторбажать; false - отображать дочерние как корни
     */
    public void setRootVisible(boolean rootVisible) {
        Object old = this.rootVisible;
        this.rootVisible = rootVisible;
        firePropertyChange("rootVisible", old, rootVisible);
    }// </editor-fold>

    private void detach(){
        Collection<Component> removeList = new HashSet<>();
        for( int i=0; i<this.getMenuCount(); i++ ){
            Component c = getMenu(i);
            if( c instanceof ObserverMenu ){
                ((ObserverMenu)c).setMenu(null);
                removeList.add(c);
            }
        }

        for( Component c : getComponents() ){
            if( c instanceof ObserverMenu ){
                ((ObserverMenu)c).setMenu(null);
                removeList.add(c);
            }
        }

        for( Component c : removeList ){
            remove(c);
        }

        //--------------
        if( menu instanceof MenuContainer )
            ((MenuContainer)menu).getChildren().removeCollectionListener(this);
    }

    private void attach(){
        if( menu instanceof MenuContainer ){
            MenuContainer mc = (MenuContainer)menu;
            int idx = -1;
            for( MenuItem mi : mc.getChildren() ){
                idx++;
                add(idx,mi);
            }
        }

        if( menu instanceof MenuContainer )
            ((MenuContainer)menu).getChildren().addCollectionListener(this);
    }

    protected ObserverMenuItem createObserverMenuItem(){
        return new ObserverMenuItem();
    }

    protected ObserverMenuItemCheked createObserverMenuItemCheked(){
        return new ObserverMenuItemCheked();
    }

    protected ObserverMenuContainer createObserverMenuContainer(){
        return new ObserverMenuContainer();
    }

    private void add( int idx, MenuItem mi){
        int size = this.getMenuCount();
        if( idx>size )idx = size;
        if( idx<-1 )idx=-1;

        if( mi instanceof MenuActionItem ){
            MenuActionItem mai = (MenuActionItem)mi;
            switch( mai.getType() ){
                case Checked:
                    ObserverMenuItemCheked coa = createObserverMenuItemCheked();
                    coa.setMenu((MenuActionItem)mi);
                    super.add(coa,idx);
                    break;
                case Default:
                default:
                    ObserverMenuItem oa = createObserverMenuItem();
                    oa.setMenu((MenuActionItem)mi);
                    super.add(oa,idx);
                    break;
            }
        }

        if( mi instanceof MenuContainer ){
            ObserverMenuContainer oc = createObserverMenuContainer();
            oc.setMenu((MenuContainer)mi);
            super.add(oc,idx);
        }

        refresh();
    }

    private CollectionListener mlAdapter = new CollectionListener() {
        @Override
        public void collectionEvent( CollectionEvent event ){
            if( event instanceof RemovedEvent && ((RemovedEvent) event).getOldItem() instanceof MenuItem ){
                remove( (MenuItem) ((RemovedEvent) event).getOldItem() );
            }else if( event instanceof AddedEvent && ((AddedEvent) event).getNewItem() instanceof MenuItem ){
                MenuItem mi = (MenuItem) ((AddedEvent) event).getNewItem();
                int idx = event instanceof ItemIndex && ((ItemIndex) event).getIndex() instanceof Number ?
                    ((Number)((ItemIndex) event).getIndex()).intValue() : 0;
                add(idx,mi);
            }
        }
    };

    private void remove( MenuItem mi){
        Collection<Component> removeList = new HashSet<Component>();
        for( int i=0; i<this.getMenuCount(); i++ ){
            Component c = getMenu(i);
            if( c instanceof ObserverMenu ){
                MenuItem cmi = ((ObserverMenu)c).getMenu();
                if( cmi!=null && cmi==mi ){
                    ((ObserverMenu)c).setMenu(null);
                    removeList.add(c);
                }
            }
        }

        for( Component c : getComponents() ){
            if( c instanceof ObserverMenu ){
                MenuItem cmi = ((ObserverMenu)c).getMenu();
                if( cmi!=null && cmi==mi ){
                    ((ObserverMenu)c).setMenu(null);
                    removeList.add(c);
                }
            }
        }

        for( Component c : removeList ){
            remove(c);
        }

        refresh();
    }

    @Override
    public void collectionEvent( CollectionEvent event ){
        mlAdapter.collectionEvent(event);
    }

    private void refresh(){
//        invalidate();
        validate();
//        repaint();
    }
}
