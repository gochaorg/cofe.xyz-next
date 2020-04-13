package xyz.cofe.gui.swing.menu;

import xyz.cofe.collection.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class ObserverMenuPopup extends JPopupMenu
    implements PropertyChangeListener,
    CollectionListener,
    ObserverMenu
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ObserverMenuPopup.class.getName());
    private static final Level logLevel = logger.getLevel();

    private static final boolean isLogSevere =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.SEVERE.intValue();

    private static final boolean isLogWarning =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.WARNING.intValue();

    private static final boolean isLogInfo =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.INFO.intValue();

    private static final boolean isLogFine =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINE.intValue();

    private static final boolean isLogFiner =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINER.intValue();

    private static final boolean isLogFinest =
        logLevel==null
            ? true
            : logLevel.intValue() <= Level.FINEST.intValue();

    private static void logFine(String message,Object ... args){
        logger.log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        logger.log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        logger.log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        logger.log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        logger.log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        logger.log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        logger.log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public ObserverMenuPopup(){
        addComponentListener(new ComponentListener() {

            @Override
            public void componentResized( ComponentEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void componentMoved(ComponentEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void componentShown(ComponentEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                onSelected(null);
            }

            @Override
            public void componentHidden(ComponentEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }

    private void update_menu_ui(){
        if( menu!=null ){
            for( Object o : MenuItem.actionsOf(menu) ){
                if( o instanceof UpdateUI ){
                    ((UpdateUI)o).updateUI();
                }
            }
        }
    }

    protected void onSelected(javax.swing.event.MenuEvent ev){
        update_menu_ui();
    }

    private MenuItem menu = null;

    @Override
    public MenuItem getMenu()
    {
        return menu;
    }

    @Override
    public void setMenu( MenuItem menu)
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
        if( menu instanceof MenuContainer )((MenuContainer)menu).getChildren().removeCollectionListener(this);
    }

    private void attach(){
        menu.addPropertyChangeListener(this);
        if( menu instanceof MenuContainer )((MenuContainer)menu).getChildren().addCollectionListener(this);
    }

    private void refreshView(){
        setText();
//        setIcon( (menu==null || !(menu instanceof MenuContainer)) ? null : ((MenuContainer)menu).getIcon() );
    }

    private void unbindChildren(){
        Collection<Component> toRemove = new ArrayList<Component>();
        for( Component c : getComponents() ){
            if( c==null )continue;
            if( !(c instanceof ObserverMenu) )continue;
//                MenuItem cmi = ((ObserverMenu)c).getMenu();
            toRemove.add( c );
            ((ObserverMenu)c).setMenu(null);
        }
        for( Component c : toRemove )remove(c);
    }

    private void bindChildren(){
        if( menu instanceof MenuContainer ){
            MenuContainer mc = ((MenuContainer)menu);
            int idx = -1;
            for( MenuItem mi : mc.getChildren() ){
                idx++;
//                int idx = mc.getChildren().indexOf(mi);
                add(idx,mi);
            }
        }
    }

    private void setText()
    {
        String text = (menu==null || !(menu instanceof MenuContainer)) ? null : ((MenuContainer)menu).getText();
        if( text==null )text = (menu!=null ? menu.getId() : null);
        if( text==null )text = "???";
//        setText(text);
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
        Collection<Component> toRemove = new ArrayList<Component>();
        for( Component c : getComponents() ){
            if( c==null )continue;
            if( !(c instanceof ObserverMenu) )continue;
            MenuItem cmi = ((ObserverMenu)c).getMenu();
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
        int size = getComponentCount();
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

        if( mi instanceof MenuContainer ){
            ObserverMenuContainer oc = createObserverMenuContainer();
            oc.setMenu((MenuContainer)mi);
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

    @Override
    public void show( Component invoker, int x, int y ) {
        update_menu_ui();
        super.show(invoker, x, y);
    }
}
