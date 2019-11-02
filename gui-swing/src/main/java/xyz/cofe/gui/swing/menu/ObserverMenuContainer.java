package xyz.cofe.gui.swing.menu;

import xyz.cofe.collection.*;
import xyz.cofe.gui.swing.SwingListener;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

public class ObserverMenuContainer extends JMenu
    implements PropertyChangeListener,
    CollectionListener,
    ObserverMenu
{
    public ObserverMenuContainer(){
        SwingListener.onMenuSelected(this, e -> onSelected(e));
    }

    protected void onSelected(javax.swing.event.MenuEvent ev){
        if( menu!=null ){
            for( Object o : xyz.cofe.gui.swing.menu.MenuItem.actionsOf(menu) ){
                if( o instanceof UpdateUI ){
                    ((UpdateUI)o).updateUI();
                }
            }
        }
    }

    private xyz.cofe.gui.swing.menu.MenuItem menu = null;

    @Override
    public xyz.cofe.gui.swing.menu.MenuItem getMenu()
    {
        return menu;
    }

    @Override
    public void setMenu( xyz.cofe.gui.swing.menu.MenuItem menu)
    {
        if( this.menu!=null )detach();
        unbindChildren();

        this.menu = menu;

        refreshView();
        bindChildren();
        if( this.menu!=null )attach();
    }

    private void detach(){
        menu.removePropertyChangeListener(this);
        if( menu instanceof xyz.cofe.gui.swing.menu.MenuContainer )((xyz.cofe.gui.swing.menu.MenuContainer)menu).getChildren().removeCollectionListener(this);
    }

    private void attach(){
        menu.addPropertyChangeListener(this);
        if( menu instanceof xyz.cofe.gui.swing.menu.MenuContainer )((xyz.cofe.gui.swing.menu.MenuContainer)menu).getChildren().addCollectionListener(this);
    }

    private void refreshView(){
        setText();
        setIcon( (menu==null || !(menu instanceof xyz.cofe.gui.swing.menu.MenuContainer)) ? null : ((xyz.cofe.gui.swing.menu.MenuContainer)menu).getIcon() );
    }

    private void unbindChildren(){
        Collection<Component> toRemove = new ArrayList<Component>();
        for( Component c : getMenuComponents() ){
            if( c==null )continue;
            if( !(c instanceof ObserverMenu) )continue;
//                MenuItem cmi = ((ObserverMenu)c).getMenu();
            toRemove.add( c );
            ((ObserverMenu)c).setMenu(null);
        }
        for( Component c : toRemove )remove(c);
    }

    private void bindChildren(){
        if( menu instanceof xyz.cofe.gui.swing.menu.MenuContainer ){
            xyz.cofe.gui.swing.menu.MenuContainer mc = ((xyz.cofe.gui.swing.menu.MenuContainer)menu);
            int idx = -1;
            for( xyz.cofe.gui.swing.menu.MenuItem mi : mc.getChildren() ){
                idx++;
//                int idx = mc.getChildren().indexOf(mi);
                add(idx,mi);
            }
        }
    }

    private void setText()
    {
        String text = (menu==null || !(menu instanceof xyz.cofe.gui.swing.menu.MenuContainer)) ? null : ((xyz.cofe.gui.swing.menu.MenuContainer)menu).getText();
        if( text==null )text = (menu!=null ? menu.getId() : null);
        if( text==null )text = "???";
        setText(text);
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt)
    {
        if( evt==null )return;
        refreshView();
    }

    private CollectionListener mlAdapter = new CollectionListener() {
        @Override
        public void collectionEvent( CollectionEvent event ){
            if( event instanceof RemovedEvent && ((RemovedEvent) event).getOldItem() instanceof xyz.cofe.gui.swing.menu.MenuItem ){
                remove( (xyz.cofe.gui.swing.menu.MenuItem) ((RemovedEvent) event).getOldItem() );
            }else if( event instanceof AddedEvent && ((AddedEvent) event).getNewItem() instanceof xyz.cofe.gui.swing.menu.MenuItem ){
                xyz.cofe.gui.swing.menu.MenuItem mi = (xyz.cofe.gui.swing.menu.MenuItem) ((AddedEvent) event).getNewItem();
                int idx = event instanceof ItemIndex && ((ItemIndex) event).getIndex() instanceof Number ?
                    ((Number)((ItemIndex) event).getIndex()).intValue() : 0;
                add(idx,mi);
            }
        }
    };

    private void remove( xyz.cofe.gui.swing.menu.MenuItem mi){
        Collection<Component> toRemove = new ArrayList<>();
        for( Component c : getMenuComponents() ){
            if( c==null )continue;
            if( !(c instanceof ObserverMenu) )continue;
            xyz.cofe.gui.swing.menu.MenuItem cmi = ((ObserverMenu)c).getMenu();
            if( cmi!=null && cmi==mi ){
                toRemove.add( c );
                ((ObserverMenu)c).setMenu(null);
            }
        }
        for( Component c : toRemove )remove(c);
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

    protected ObserverMenuSeparator createObserverMenuSeparator(){
        return new ObserverMenuSeparator();
    }

    private void add( int idx, MenuItem mi){
        int size = ObserverMenuContainer.this.getMenuComponentCount();
        if( idx>size )idx = size;

        if( mi instanceof MenuActionItem ){
            Component c = null;
            ObserverMenu om = null;

            switch( ((MenuActionItem)mi).getType() ){
                case Checked:
                    ObserverMenuItemCheked coa = createObserverMenuItemCheked();
                    c = coa;
                    om = coa;
                    break;
                case Default:
                default:
                    ObserverMenuItem oa = createObserverMenuItem();
                    c = oa;
                    om = oa;
                    break;
            }

            om.setMenu((MenuActionItem)mi);

            if( idx>=0 ){
                super.add(c,idx);
            }else{
                super.add(c);
            }
        }

        if( mi instanceof xyz.cofe.gui.swing.menu.MenuContainer ){
            ObserverMenuContainer oc = createObserverMenuContainer();
            oc.setMenu((xyz.cofe.gui.swing.menu.MenuContainer)mi);
            if( idx>=0 ){
                super.add(oc,idx);
            }else{
                super.add(oc);
            }
        }

        if( mi instanceof MenuSeparatorItem ){
            ObserverMenuSeparator os = createObserverMenuSeparator();
            os.setMenu(mi);
            if( idx>=0 ){
                super.add(os,idx);
            }else{
                super.add(os);
            }
        }
    }

    @Override
    public void collectionEvent( CollectionEvent event ){
        mlAdapter.collectionEvent(event);
    }
}
