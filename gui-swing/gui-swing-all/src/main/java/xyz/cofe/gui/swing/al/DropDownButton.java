package xyz.cofe.gui.swing.al;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * Кнопка с выпадающим меню.
 * Использует метод getComponentPopupMenu для контекстного меню.
 * @author gocha
 * @deprecated
 */
public class DropDownButton extends JButton
{
    private javax.swing.Icon dropdownIco = null;
    private javax.swing.Icon dropdownIcoOver = null;
    private javax.swing.Icon dropdownIcoDisable = null;
    private int drowDownIconWidth = -1;
    private int drowDownIconHeight = -1;

    /**
     * Конструктор
     */
    public DropDownButton(){
        initIcons();
    }

    /* (non-Javadoc) @see JButton */
    @Override
    public void setComponentPopupMenu(JPopupMenu popup) {
        JPopupMenu old = getComponentPopupMenu();
        if( old!=null ){
            old.removePopupMenuListener(popupMenuListener);
        }
        super.setComponentPopupMenu(popup);
        if( popup!=null ){
            popup.addPopupMenuListener(popupMenuListener);
        }
    }

    private PopupMenuListener popupMenuListener = new PopupMenuListener() {
        @Override
        public void popupMenuWillBecomeVisible( PopupMenuEvent e) {
            onPopupMenuWillBecomeVisible(e);
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            onPopupMenuWillBecomeInvisible(e);
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            onPopupMenuCanceled(e);
        }
    };

    /**
     Вызывается перед тем когда Popup меню станет видимым
     @param e Событие
     */
    protected void onPopupMenuWillBecomeVisible(PopupMenuEvent e){
    }

    /**
     Вызывается перед тем когда Popup меню станет невидимым
     @param e Событие
     */
    protected void onPopupMenuWillBecomeInvisible(PopupMenuEvent e){
    }

    /**
     Вызывается после того когда Popup меню отеняет выбор
     @param e Событие
     */
    protected void onPopupMenuCanceled(PopupMenuEvent e){
    }

    /**
     * Конструктор
     * @param action Действие
     */
    public DropDownButton(Action action){
        super(action);
        initIcons();
    }

    /**
     * Конструктор
     * @param action Действие
     * @param popupMenu Контекстное меню
     */
    public DropDownButton(Action action,JPopupMenu popupMenu){
        super(action);
        initIcons();
        setComponentPopupMenu(popupMenu);
    }

    private void initIcons(){
//        dropdownIco = new Icon(new ResourceObject(DropDownButton.class, "dropdown-a.png"));
        dropdownIco = new Icon(DropDownButton.class.getResource("dropdown-a.png"));
//        dropdownIcoOver = new Icon(new ResourceObject(DropDownButton.class, "dropdown-b.png"));
        dropdownIcoOver = new Icon(DropDownButton.class.getResource("dropdown-b.png"));
//        dropdownIcoDisable = new Icon(new ResourceObject(DropDownButton.class, "dropdown-c.png"));
        dropdownIcoDisable = new Icon(DropDownButton.class.getResource("dropdown-c.png"));

        drowDownIconWidth = dropdownIco.getIconWidth();
        drowDownIconHeight = dropdownIco.getIconHeight();
    }

    protected boolean mouseOver = false;

    /**
     * Вызывается когда мышь вошла в пределы компонента
     * @param e Сообщение перемешения мыши
     */
    protected void onMouseEnter( MouseEvent e){
        repaint();
    }

    /**
     * Вызывается когда мышь вышла за пределы компонента
     * @param e Сообщение перемешения мыши
     */
    protected void onMouseExit(MouseEvent e){
        repaint();
    }

    /**
     * Вызывается когда нажата иконка выпадающего меню
     * @param e Событие мыши
     */
    protected void onDropDownIcoPressed(MouseEvent e){
        JPopupMenu pMenu = getComponentPopupMenu();
        if( pMenu==null )return;

//        int w = getWidth();
        int h = getHeight();

        pMenu.show(this, 0, h-1);
    }

    /**
     * Отображает кнопку и икноку выпадающего меню
     * @param g Контекст отображения
     */
    @Override
    protected void paintComponent( Graphics g)
    {
        super.paintComponent(g);

        Graphics2D gs = (Graphics2D)g;
        javax.swing.Icon ico = isEnabled() ? (mouseOver ? dropdownIcoOver : dropdownIco) : dropdownIcoDisable;
        int w = getWidth();
        int h = getHeight();

        ico.paintIcon(this, g, w-drowDownIconWidth, h-drowDownIconHeight);
    }

    /**
     * Обрабатывает сообщения мыши, перехватывая управление при нажатии на иконку выпадающего меню
     * @param e События
     */
    @Override
    protected void processEvent(AWTEvent e)
    {
        if( e instanceof MouseEvent ){
            int id = ((MouseEvent)e).getID();
            if( id==MouseEvent.MOUSE_ENTERED ){
                if( !mouseOver ){
                    mouseOver = true;
                    onMouseEnter((MouseEvent)e);
                }
            }
            if( id==MouseEvent.MOUSE_EXITED ){
                if( mouseOver ){
                    mouseOver = false;
                    onMouseExit((MouseEvent)e);
                }
            }
            if( id==MouseEvent.MOUSE_PRESSED ){
                MouseEvent me = (MouseEvent)e;
                int eX = me.getX();
                int eY = me.getY();
                int w = getWidth();
                int h = getHeight();
                if( eX>=(w-drowDownIconWidth) && eY>=(h-drowDownIconHeight)
                    && eX<=w && eY<=h ){
                    onDropDownIcoPressed(me);
                    return;
                }else{
                    ActionListener[] aListeners = this.getActionListeners();
                    Action a = this.getAction();
                    if( a==null && (aListeners==null || aListeners.length<1) ){
                        onDropDownIcoPressed(me);
                    }
                }
            }
        }
        super.processEvent(e);
    }
}
