/*
 * The MIT License
 *
 * Copyright 2015 Kamnev Georgiy (nt.gocha@gmail.com).
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

/**
 * Взято с сайта: http://java-swing-tips.blogspot.com/2008/04/drag-and-drop-tabs-in-jtabbedpane.html
 * И модифицировано
 */

package xyz.cofe.gui.swing.tabs;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.EventObject;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.gui.swing.al.CloseableTabHeader;
import xyz.cofe.iter.Eterable;


/**
 * Панель вкладок с дополнительными функциями 
 * Перемещение вкладок
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TabPane extends JTabbedPane
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(TabPane.class.getName()).log(Level.FINE, message, args);
    }
    
    private static void logFiner(String message,Object ... args){
        Logger.getLogger(TabPane.class.getName()).log(Level.FINER, message, args);
    }
    
    private static void logFinest(String message,Object ... args){
        Logger.getLogger(TabPane.class.getName()).log(Level.FINEST, message, args);
    }
    
    private static void logInfo(String message,Object ... args){
        Logger.getLogger(TabPane.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(TabPane.class.getName()).log(Level.WARNING, message, args);
    }
    
    private static void logSevere(String message,Object ... args){
        Logger.getLogger(TabPane.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(TabPane.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>
    
    private ListenersHelper<Listener,Event> listenersHelper =
        new ListenersHelper<Listener, Event>(Listener::tabPaneEvent);

    // <editor-fold defaultstate="collapsed" desc="События tab">
    /**
     * Событие
     */
    public static class Event extends EventObject
    {
        /**
         * Конструктор
         * @param tabPane панель вкладок
         */
        public Event(TabPane tabPane) {
            super(tabPane);
        }
    }

    /**
     * Событие перемещения вкладки
     */
    public static class TabExchagedEvent extends Event
    {
        /**
         * Конструктор
         * @param tabPane панель вкладок
         * @param prev Предыдущая вкладка
         * @param next Следующая вкладка 
         */
        public TabExchagedEvent(TabPane tabPane, int prev, int next) {
            super(tabPane);
            previousTabIndex = prev;
            nextTabIndex = next;
        }
        private int previousTabIndex = 0;
        private int nextTabIndex = 0;

        /**
         * Возвращает индекс следующей вкладки
         * @return Индекс вкладки
         */
        public int getNextTabIndex() {
            return nextTabIndex;
        }

        /**
         * Возвращает индекс предыдущей вкладки
         * @return Индекс вкладки
         */
        public int getPreviousTabIndex() {
            return previousTabIndex;
        }
    }

    /**
     * Событие смены индекса вкладки
     */
    public static class TabSelectedEvent extends Event
    {
        /**
         * Конструктор
         * @param tabPane панель вкладок
         * @param prev Предыдущая вкладка
         * @param next Следующая вкладка
         */
        public TabSelectedEvent(TabPane tabPane, int prev, int next) {
            super(tabPane);
            previousTabIndex = prev;
            nextTabIndex = next;
        }
        private int previousTabIndex = 0;
        private int nextTabIndex = 0;

        /**
         * Возвращает индекс следующей вкладки
         * @return Индекс вкладки
         */
        public int getSelectedTabIndex() {
            return nextTabIndex;
        }

        /**
         * Возвращает индекс предыдущей вкладки
         * @return Индекс вкладки
         */
        public int getOldSelectedTabIndex() {
            return previousTabIndex;
        }
    }

    /**
     * Событие смены индекса вкладки
     */
    public static class TabInsertedEvent extends Event
    {

        /**
         * Конструктор
         * @param tabPane панель вкладок
         * @param component компонент вкладки
         * @param index Индекс вкладки
         */
        public TabInsertedEvent(TabPane tabPane, Component component, int index) {
            super(tabPane);
            this.component = component;
            this.index = index;
        }
        private Component component = null;
        private int index = -1;

        /**
         * Компонент вкладки
         * @return компонент вкладки
         */
        public Component getComponent() {
            return component;
        }

        /**
         * Индекс вкладки
         * @return Индекс вкладки
         */
        public int getIndex() {
            return index;
        }
    }

    /**
     * Событие смены индекса вкладки
     */
    public static class TabDeletedEvent extends Event
    {
        /**
         * Конструктор
         * @param tabPane панель вкладок
         * @param component компонент вкладки
         * @param index Индекс вкладки
         */
        public TabDeletedEvent(TabPane tabPane, Component component, int index) {
            super(tabPane);
            this.component = component;
            this.index = index;
        }
        
        private Component component = null;
        private int index = -1;

        /**
         * Компонент вкладки
         * @return компонент вкладки
         */
        public Component getComponent() {
            return component;
        }

        /**
         * Индекс вкладки
         * @return Индекс вкладки
         */
        public int getIndex() {
            return index;
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Подписчик на события и его адаптер">
    /**
     * Подписчик на события панели вкладок
     */
    public interface Listener
    {

        /**
         * Событие TabPaneEvent
         * @param e Событие
         */
        void tabPaneEvent( Event e );
    }

    /**
     * Адаптер подписчика на сообщения
     */
    public static class Adapter implements Listener
    {
        /**
         * Событие TabPaneEvent
         * @param e Событие
         */
        @Override
        public void tabPaneEvent(Event e) {
            if (e == null)
                return;
            Object _src = e.getSource();
            if (!(_src instanceof TabPane))
                return;

            TabPane src = (TabPane) _src;
            if (e instanceof TabDeletedEvent) {
                tabDeleted(src, ((TabDeletedEvent) e).getComponent(), ((TabDeletedEvent) e).getIndex());
            } else if (e instanceof TabInsertedEvent) {
                tabInserted(src, ((TabInsertedEvent) e).getComponent(), ((TabInsertedEvent) e).getIndex());
            } else if (e instanceof TabSelectedEvent) {
                int i = ((TabSelectedEvent) e).getSelectedTabIndex();
                Component c = src.getSelectedComponent();
                tabSelected(src, c, i, ((TabSelectedEvent) e).getOldSelectedTabIndex());
            } else if (e instanceof TabExchagedEvent) {
                tabExchanged(src, ((TabExchagedEvent) e).getNextTabIndex(), ((TabExchagedEvent) e).getPreviousTabIndex());
            }
        }

        /**
         * Вкладка удалена
         * @param src Источник сообщения
         * @param component Компонент вкладки
         * @param index Индекс вкладки
         */
        protected void tabDeleted(TabPane src, Component component, int index) {
        }

        /**
         * Добавлена новая вкладка
         * @param src Источник сообщения
         * @param component Компонент вкладки
         * @param index Индекс вкладки
         */
        protected void tabInserted(TabPane src, Component component, int index) {
        }

        /**
         * Выбрана другая закладка
         * @param src Источник сообщения
         * @param component Компонент вкладки
         * @param index Индекс вкладки
         * @param oldIndex Предыдущая выбранная вкладка
         */
        protected void tabSelected(TabPane src, Component component, int index, int oldIndex) {
        }

        /**
         * Вкладки поменялись местами
         * @param src Источник сообщения
         * @param newIndex Индекс текущей вкладки
         * @param oldIndex индекс вкладки с которой был произведен обмен
         */
        protected void tabExchanged(TabPane src, int newIndex, int oldIndex) {
        }
    }
    // </editor-fold>

    /**
     * Возвращает коллекцию подписчиков
     * @return Коллекция подписчиков
     */
    public Listener[] getTabPaneListeners() { 
        return listenersHelper.getListeners().toArray(new Listener[]{});
    }

    /**
     * Проверяет наличие подписчика
     * @param listener подписчик
     * @return true подписчик установлен
     */
    public boolean hasTabPaneListener(Listener listener) {
        return listenersHelper.hasListener(listener);
    }

    /**
     * Добавление подписчика на события
     * @param listener подписчик
     * @return Отписка от уведомлений
     */
    public AutoCloseable addTabPaneListener(Listener listener) {
        return listenersHelper.addListener(listener);
    }

    /**
     * Добавление подписчика на события
     * @param listener подписчик
     * @param weakLink добавить подписчика как weak (true) ссылку / обычную (false) ссылку
     * @return Отписка от уведомлений
     */
    public AutoCloseable addTabPaneListener(Listener listener, boolean weakLink) {
        return listenersHelper.addListener(listener, weakLink);
    }

    /**
     * Удаление подписчика из списка рассылки / отписка от уведомлений
     * @param listener подписчик
     */
    public void removeTabPaneListener(Listener listener) {
        listenersHelper.removeListener(listener);
    }
    
    /**
     * Добавлет подписчика на определенный класс событий
     * @param <EventType> Тип события
     * @param type Тип события
     * @param weak добавить подписчика как weak (true) ссылку / обычную (false) ссылку
     * @param consumer подписчик
     * @return отписка от уведомлений
     */
    public <EventType> AutoCloseable onTabPanEvent(
        final Class<EventType> type, 
        boolean weak, final Consumer<EventType> consumer ){
        return addTabPaneListener( 
            new Listener() {
            @Override
            public void tabPaneEvent(Event ev) {
            if( ev!=null && type!=null && consumer!=null ){
                Class etype = ev.getClass();
                if( type.isAssignableFrom(etype) ){
                    consumer.accept((EventType)ev);
                }
            }
            }} , weak);
    }

    public <EventType> AutoCloseable onTabPanEvent( Class<EventType> type, Consumer<EventType> consumer ){
        return onTabPanEvent(type, false, consumer);
    }
    
    /**
     * Разрешает/запрещает рассылку уведомлений
     */
    protected boolean enableTabEvent = true;
    
    /**
     * Флаг выполнения операции обмена вкладок
     */
    protected boolean exchangeTab = false;

    /**
     * Рассылает уведомление о событии.
     * Управляется флагом enableTabEvent
     * @param e Событие
     * @see #enableTabEvent
     */
    protected void fireTabPaneEvent( Event e ){
        if( !enableTabEvent )return;
        listenersHelper.fireEvent(e);
    }

    private int selectedTabIndex = -1;

    // Обработка изменения состояния
    private void handleChangeState(){
        int ti = getSelectedIndex();
        if( ti!=selectedTabIndex ){
            fireTabPaneEvent(new TabSelectedEvent(this,selectedTabIndex,ti));
            selectedTabIndex = ti;
        }
    }

    /**
     * Перечисляет закладки (содержимое)
     * @return закладки (содержимое)
     */
    public Eterable<Component> getTabs(){
        Component[] arr = new Component[getTabCount()];
        for( int i=0; i<arr.length; i++ ){
            arr[i] = getComponentAt(i);
        }
        return Eterable.of(arr);
    }

    // <editor-fold defaultstate="collapsed" desc="Отслеживаение создания / удаления вкладки">
    private boolean addTab = false;

    @Override
    public void addTab(String title, Icon icon, Component component, String tip) {
        boolean lock = false;
        if( !addTab ){
            addTab = true;
            lock = true;
        }

        try{
            super.addTab(title, icon, component, tip);
            if( lock ){
                Component cmpHeader = createHeaderForComponent(component);
                if( cmpHeader!=null ){
                    if( cmpHeader instanceof TabHeader ){
                        TabHeader th = (TabHeader)cmpHeader;
                        if( title!=null )th.setTabTitle(title);
                        if( icon!=null )th.setTabIcon(icon);
                        if( tip!=null )th.setTabToolTipText(tip);
                    }
                }
            }
        }finally{
            if( lock )addTab = false;
        }

        int co = getTabCount();
        if( lock )fireTabPaneEvent(new TabInsertedEvent(this, component, co - 1));
    }

    @Override
    public void addTab(String title, Icon icon, Component component) {
        boolean lock = false;
        if( !addTab ){
            addTab = true;
            lock = true;
        }

        try{
            super.addTab(title, icon, component);
            if( lock ){
                Component cmpHeader = createHeaderForComponent(component);
                if( cmpHeader!=null ){
                    if( cmpHeader instanceof TabHeader ){
                        TabHeader th = (TabHeader)cmpHeader;
                        if( title!=null )th.setTabTitle(title);
                        if( icon!=null )th.setTabIcon(icon);
                    }
                }
            }
        }finally{
            if( lock )addTab = false;
        }

        int co = getTabCount();
        if( lock )fireTabPaneEvent(new TabInsertedEvent(this, component, co - 1));
    }

    @Override
    public void addTab(String title, Component component) {
        boolean lock = false;
        if( !addTab ){
            addTab = true;
            lock = true;
        }

        try{
            super.addTab(title, component);
            if( lock ){
                Component cmpHeader = createHeaderForComponent(component);
                if( cmpHeader!=null && title!=null ){
                    if( cmpHeader instanceof TabHeader ){
                        TabHeader th = (TabHeader)cmpHeader;
                        th.setTabTitle(title);
                    }
                }
            }
        }finally{
            if( lock )addTab = false;
        }

        int co = getTabCount();
        if( lock )fireTabPaneEvent(new TabInsertedEvent(this, component, co - 1));
    }
    
    public void addTab(TabHeader tabHeader, Component component) {
        if( tabHeader==null )throw new IllegalArgumentException( "tabHeader==null" );
        if( component==null )throw new IllegalArgumentException( "component==null" );
        
        boolean lock = false;
        if( !addTab ){
            addTab = true;
            lock = true;
        }

        try{
            super.addTab(tabHeader.getTabTitle(), component);
            if( lock ){
                int index = indexOfComponent(component);
                setTabComponentAt(index, tabHeader);
            }
        }finally{
            if( lock )addTab = false;
        }

        int co = getTabCount();
        if( lock )fireTabPaneEvent(new TabInsertedEvent(this, component, co - 1));
    }

    @Override
    public void insertTab(String title, Icon icon, Component component, String tip, int index) {
        boolean lock = false;
        if( !addTab ){
            addTab = true;
            lock = true;
        }

        try{
            super.insertTab(title, icon, component, tip, index);
            if( lock ){
                Component cmpHeader = createHeaderForComponent(component);
                if( cmpHeader!=null && title!=null ){
                    if( cmpHeader instanceof TabHeader ){
                        TabHeader th = (TabHeader)cmpHeader;
                        if( title!=null )th.setTabTitle(title);
                        if( icon!=null )th.setTabIcon(icon);
                        if( tip!=null )th.setTabToolTipText(tip);
                    }
                }
            }
        }finally{
            if( lock )addTab = false;
        }

        if( lock )fireTabPaneEvent(new TabInsertedEvent(this, component, index));
    }
    
    public void insertTab(TabHeader tabHeader, Component component, int index) {
        if( tabHeader==null )throw new IllegalArgumentException( "tabHeader==null" );
        if( component==null )throw new IllegalArgumentException( "component==null" );
        
        boolean lock = false;
        if( !addTab ){
            addTab = true;
            lock = true;
        }

        try{
            super.insertTab(
                tabHeader.getTabTitle(), 
                tabHeader.getTabIcon(), 
                component, 
                tabHeader.getTabToolTipText(), 
                index
            );
            if( lock ){
                int index2 = indexOfComponent(component);
                setTabComponentAt(index2, tabHeader);
            }
        }finally{
            if( lock )addTab = false;
        }

        if( lock )fireTabPaneEvent(new TabInsertedEvent(this, component, index));
    }

    @Override
    public void removeTabAt(int index) {
        Component tabC = getTabComponentAt(index);
        Component tab = getComponentAt(index);
        
        if( !exchangeTab ){
            releaseTab(tab, tabC);
        }

        super.removeTabAt(index);
        fireTabPaneEvent(new TabDeletedEvent(this, tab, index));
    }
    // </editor-fold>

    public interface TabQuery
    {
        void setTabBackground( Color background );
        void setTabOwner( Component component );
        void setTabDisabledIcon( Icon disabledIcon );
        void setTabDisplayedMnemonicIndex( int mnemonicIndex );
        void setTabEnabled( boolean enabled );
        void setTabForeground( Color foreground );
        void setTabIcon( Icon icon );
        void setTabMnemonic( int mnemonic );
        void setTabTitle( String title );
        void setTabToolTipText( String toolTipText );
    }

    @Override
    public void setBackgroundAt(int index, Color background) {
        Component cmpt = this.getTabComponentAt(index);
        if( cmpt instanceof TabQuery )((TabQuery)cmpt).setTabBackground(background);

        super.setBackgroundAt(index, background);
    }

    @Override
    public void setComponentAt(int index, Component component) {
        Component cmpt = this.getTabComponentAt(index);
        if( cmpt instanceof TabQuery )((TabQuery)cmpt).setTabOwner(component);

        super.setComponentAt(index, component);
    }

    @Override
    public void setDisabledIconAt(int index, Icon disabledIcon) {
        Component cmpt = this.getTabComponentAt(index);
        if( cmpt instanceof TabQuery )((TabQuery)cmpt).setTabDisabledIcon(disabledIcon);

        super.setDisabledIconAt(index, disabledIcon);
    }

    @Override
    public void setDisplayedMnemonicIndexAt(int tabIndex, int mnemonicIndex) {
        Component cmpt = this.getTabComponentAt(tabIndex);
        if( cmpt instanceof TabQuery )((TabQuery)cmpt).setTabDisplayedMnemonicIndex(mnemonicIndex);

        super.setDisplayedMnemonicIndexAt(tabIndex, mnemonicIndex);
    }

    @Override
    public void setEnabledAt(int index, boolean enabled) {
        Component cmpt = this.getTabComponentAt(index);
        if( cmpt instanceof TabQuery )((TabQuery)cmpt).setTabEnabled(enabled);

        super.setEnabledAt(index, enabled);
    }

    @Override
    public void setForegroundAt(int index, Color foreground) {
        Component cmpt = this.getTabComponentAt(index);
        if( cmpt instanceof TabQuery )((TabQuery)cmpt).setTabForeground(foreground);

        super.setForegroundAt(index, foreground);
    }

    @Override
    public void setIconAt(int index, Icon icon) {
        Component cmpt = this.getTabComponentAt(index);
        if( cmpt instanceof TabQuery )((TabQuery)cmpt).setTabIcon(icon);

        super.setIconAt(index, icon);
    }

    @Override
    public void setMnemonicAt(int tabIndex, int mnemonic) {
        Component cmpt = this.getTabComponentAt(tabIndex);
        if( cmpt instanceof TabQuery )((TabQuery)cmpt).setTabMnemonic(mnemonic);

        super.setMnemonicAt(tabIndex, mnemonic);
    }

    @Override
    public void setTitleAt(int index, String title) {
        Component cmpt = this.getTabComponentAt(index);
        if( cmpt instanceof TabQuery )((TabQuery)cmpt).setTabTitle(title);

        super.setTitleAt(index, title);
    }

    @Override
    public void setToolTipTextAt(int index, String toolTipText) {
        Component cmpt = this.getTabComponentAt(index);
        if( cmpt instanceof TabQuery )((TabQuery)cmpt).setTabToolTipText(toolTipText);

        super.setToolTipTextAt(index, toolTipText);
    }

    @Override
    public void setTabComponentAt(int index, Component component) {
        Component cmpt = this.getTabComponentAt(index);
        super.setTabComponentAt(index, component);

        if( cmpt instanceof AutoCloseable && (!exchangeTab) ){
            try{
                ((AutoCloseable)cmpt).close();
            }
            catch(Exception e){
                Logger.getLogger(TabPane.class.getName()).log(
                        Level.SEVERE,null,e
                        );
            }
        }
    }
    
    /**
     * Обменивает две вкладки местами
     * @param tabIdx1 Первая вкладка
     * @param tabIdx2 Вторая вкладка
     */
    public void swapTab(int tabIdx1,int tabIdx2){
        int tabCo = getTabCount();
        if( tabIdx1<0 )throw new IllegalArgumentException("tabIdx1<0");
        if( tabIdx2<0 )throw new IllegalArgumentException("tabIdx2<0");
        
        if( tabIdx1>=tabCo )throw new IllegalArgumentException("tabIdx1 >= tab count");
        if( tabIdx2>=tabCo )throw new IllegalArgumentException("tabIdx2 >= tab count");
        
        if( tabIdx1==tabIdx2 )return;
        
        enableTabEvent = false;
        exchangeTab = true;
        
        if( tabIdx1>tabIdx2 ){
            int t = tabIdx1;
            tabIdx1 = tabIdx2;
            tabIdx2 = t;
        }
        
        Component cmp1 = getComponentAt(tabIdx1);
        Component cmpHeader1 = getTabComponentAt(tabIdx1);
        String title1 = getTitleAt(tabIdx1);
        String toolTip1 = getToolTipTextAt(tabIdx1);
        Icon icon1 = getIconAt(tabIdx1);
        
        Component cmp2 = getComponentAt(tabIdx2);
        Component cmpHeader2 = getTabComponentAt(tabIdx2);
        String title2 = getTitleAt(tabIdx2);
        String toolTip2 = getToolTipTextAt(tabIdx2);
        Icon icon2 = getIconAt(tabIdx2);
        
        remove(tabIdx2);
        remove(tabIdx1);
        
        insertTab(title2, icon2, cmp2, toolTip2, tabIdx1);
        setTabComponentAt(tabIdx1, cmpHeader2);
        
        insertTab(title1, icon1, cmp1, toolTip1, tabIdx2);
        setTabComponentAt(tabIdx2, cmpHeader1);
        
        enableTabEvent = true;
        exchangeTab = false;
    }

    // <editor-fold defaultstate="collapsed" desc="Создание и удаление заголовка">
    /**
     * Вызывается когда закрывается вкладка.
     * Если заголовок вкладки или сам компонент вкладки реализуют AutoCloseable,
     * то для них вызывается метод close да бы уведомить о закрытии
     * @param tabComponent Вкладка
     * @param header Заголовок
     */
    protected void releaseTab(Component tabComponent, Component header) {
        if (tabComponent != null && tabComponent instanceof AutoCloseable) {
            try {
                ((AutoCloseable) tabComponent).close();
            } catch (Exception ex) {
                Logger.getLogger(TabPane.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (header != null && header instanceof AutoCloseable) {
            try {
                ((AutoCloseable) header).close();
            } catch (Exception ex) {
                Logger.getLogger(TabPane.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Создает и устанавливает заголовок для указанного компонента
     * @param owner Дочерний компонент tabPane для котороого устанавливается заголовок
     * @return Компонент заголовка
     * @see #getHeaderCreator()
     */
    protected Component createHeaderForComponent(Component owner) {
        if( !exchangeTab ){
            int index = indexOfComponent(owner);
            if (index < 0)
                return null;

            Component cmp = getHeaderCreator().apply(owner);
            if (cmp != null) {
                setTabComponentAt(index, cmp);
            }
            
            return cmp;
        }
        
        return null;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Метод создания заголовков">
    private Function<Component, Component> headerCreator = null;

    /**
     * Указывает на метод создания заголовков
     * @return Создание заголовков
     */
    public Function<Component, Component> getHeaderCreator() {
        if (headerCreator != null)
            return headerCreator;
        headerCreator = defaultHeader;
        return headerCreator;
    }

    /**
     * Указывает на метод создания заголовков
     * @param creator Создание заголовков
     */
    public void setHeaderCreator(Function<Component, Component> creator) {
        headerCreator = creator;
    }
    
    /**
     * Создает заголовок TabHeader
     */
    protected final Function<Component, Component> defaultHeader = new Function<Component, Component>()
    {
        @Override
        public Component apply(Component from) {
            if (from == null) {
                throw new IllegalArgumentException("from==null");
            }
            TabPane tp = TabPane.this;
            TabHeader th = new CloseableTabHeader(tp, from, true);
            return th;
        }
    };
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Волшебный код">
    public TabPane() {
        super();
        final DragSourceListener dsl = new DragSourceListener()
        {

            @Override
            public void dragEnter(DragSourceDragEvent e) {
                e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
            }

            @Override
            public void dragExit(DragSourceEvent e) {
                e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
                lineRect.setRect(0, 0, 0, 0);
                glassPane.setPoint(new Point(-1000, -1000));
                glassPane.repaint();
            }

            @Override
            public void dragOver(DragSourceDragEvent e) {
                Point glassPt = e.getLocation();
                SwingUtilities.convertPointFromScreen(glassPt, glassPane);
                int targetIdx = getTargetTabIndex(glassPt);
                //if(getTabAreaBounds().contains(tabPt) && targetIdx>=0 &&
                if (getTabAreaBounds().contains(glassPt) && targetIdx >= 0
                        && targetIdx != dragTabIndex && targetIdx != dragTabIndex + 1) {
                    e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
                    glassPane.setCursor(DragSource.DefaultMoveDrop);
                } else {
                    e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
                    glassPane.setCursor(DragSource.DefaultMoveNoDrop);
                }
            }

            @Override
            public void dragDropEnd(DragSourceDropEvent e) {
                lineRect.setRect(0, 0, 0, 0);
                dragTabIndex = -1;
                glassPane.setVisible(false);
                if (hasGhost()) {
                    glassPane.setVisible(false);
                    glassPane.setImage(null);
                }
            }

            @Override
            public void dropActionChanged(DragSourceDragEvent e) {
            }
        };
        final Transferable t = new Transferable()
        {

            private final DataFlavor FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);

            @Override
            public Object getTransferData(DataFlavor flavor) {
                return TabPane.this;
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                DataFlavor[] f = new DataFlavor[1];
                f[0] = this.FLAVOR;
                return f;
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.getHumanPresentableName().equals(NAME);
            }
        };
        final DragGestureListener dgl = new DragGestureListener()
        {

            @Override
            public void dragGestureRecognized(DragGestureEvent e) {
                if (getTabCount() <= 1)
                    return;
                Point tabPt = e.getDragOrigin();
                dragTabIndex = indexAtLocation(tabPt.x, tabPt.y);
                //"disabled tab problem".
                if (dragTabIndex < 0 || !isEnabledAt(dragTabIndex))
                    return;
                initGlassPane(e.getComponent(), e.getDragOrigin());
                try {
                    e.startDrag(DragSource.DefaultMoveDrop, t, dsl);
                } catch (InvalidDnDOperationException idoe) {
                    idoe.printStackTrace();
                }
            }
        };
        new DropTarget(glassPane, DnDConstants.ACTION_COPY_OR_MOVE, new CDropTargetListener(), true);
        new DragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, dgl);

        selectedTabIndex = getSelectedIndex();

        // by gocha
        final ChangeListener ch = new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e) {
                handleChangeState();
            }
        };
        addChangeListener(ch);
    }

    class GhostGlassPane extends JPanel
    {

        private final AlphaComposite composite;
        private Point location = new Point(0, 0);
        private BufferedImage draggingGhost = null;

        public GhostGlassPane() {
            setOpaque(false);
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
            //http://bugs.sun.com/view_bug.do?bug_id=6700748
            //setCursor(null);
        }

        public void setImage(BufferedImage draggingGhost) {
            this.draggingGhost = draggingGhost;
        }

        public void setPoint(Point location) {
            this.location = location;
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(composite);
            if (isPaintScrollArea() && getTabLayoutPolicy() == SCROLL_TAB_LAYOUT) {
                g2.setPaint(Color.RED);
                g2.fill(rBackward);
                g2.fill(rForward);
            }
            if (draggingGhost != null) {
                double xx = location.getX() - (draggingGhost.getWidth(this) / 2d);
                double yy = location.getY() - (draggingGhost.getHeight(this) / 2d);
                g2.drawImage(draggingGhost, (int) xx, (int) yy, null);
            }
            if (dragTabIndex >= 0) {
                g2.setPaint(lineColor);
                g2.fill(lineRect);
            }
        }
    }
    private static final int LINEWIDTH = 3;
    private static final String NAME = "test";
    private final GhostGlassPane glassPane = new GhostGlassPane();
    private final Rectangle lineRect = new Rectangle();
    private final Color lineColor = new Color(0, 100, 255);
    private int dragTabIndex = -1;

    private void clickArrowButton(String actionKey) {
        javax.swing.ActionMap map = getActionMap();
        if (map != null) {
            Action action = map.get(actionKey);
            if (action != null && action.isEnabled()) {
                action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null, 0, 0));
            }
        }
    }
    private static Rectangle rBackward = new Rectangle();
    private static Rectangle rForward = new Rectangle();
    private static int rwh = 20;
    private static int buttonsize = 30; //xxx magic number of scroll button size

    private void autoScrollTest(Point glassPt) {
        Rectangle r = getTabAreaBounds();
        int _tabPlacement = getTabPlacement();
        if (_tabPlacement == TOP || _tabPlacement == BOTTOM) {
            rBackward.setBounds(r.x, r.y, rwh, r.height);
            rForward.setBounds(r.x + r.width - rwh - buttonsize, r.y, rwh + buttonsize, r.height);
        } else if (_tabPlacement == LEFT || _tabPlacement == RIGHT) {
            rBackward.setBounds(r.x, r.y, r.width, rwh);
            rForward.setBounds(r.x, r.y + r.height - rwh - buttonsize, r.width, rwh + buttonsize);
        }
        if (rBackward.contains(glassPt)) {
            //System.out.println(new java.util.Date() + "Backward");
            clickArrowButton("scrollTabsBackwardAction");
        } else if (rForward.contains(glassPt)) {
            //System.out.println(new java.util.Date() + "Forward");
            clickArrowButton("scrollTabsForwardAction");
        }
    }

    class CDropTargetListener implements DropTargetListener
    {

        @Override
        public void dragEnter(DropTargetDragEvent e) {
            if (isDragAcceptable(e))
                e.acceptDrag(e.getDropAction());
            else
                e.rejectDrag();
        }

        @Override
        public void dragExit(DropTargetEvent e) {
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent e) {
        }
        private Point pt_ = new Point();

        @Override
        public void dragOver(final DropTargetDragEvent e) {
            Point pt = e.getLocation();
            if (getTabPlacement() == JTabbedPane.TOP || getTabPlacement() == JTabbedPane.BOTTOM) {
                initTargetLeftRightLine(getTargetTabIndex(pt));
            } else {
                initTargetTopBottomLine(getTargetTabIndex(pt));
            }
            if (hasGhost()) {
                glassPane.setPoint(pt);
            }
            if (!pt_.equals(pt))
                glassPane.repaint();
            pt_ = pt;
            autoScrollTest(pt);
        }

        @Override
        public void drop(DropTargetDropEvent e) {
            if (isDropAcceptable(e)) {
                exchangeTab(dragTabIndex, getTargetTabIndex(e.getLocation()));
                e.dropComplete(true);
            } else {
                e.dropComplete(false);
            }
            repaint();
        }

        public boolean isDragAcceptable(DropTargetDragEvent e) {
            Transferable t = e.getTransferable();
            if (t == null)
                return false;
            DataFlavor[] f = e.getCurrentDataFlavors();
            if (t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
                return true;
            }
            return false;
        }

        public boolean isDropAcceptable(DropTargetDropEvent e) {
            Transferable t = e.getTransferable();
            if (t == null)
                return false;
            DataFlavor[] f = t.getTransferDataFlavors();
            if (t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
                return true;
            }
            return false;
        }
    }
    private boolean hasGhost = true;

    public void setPaintGhost(boolean flag) {
        hasGhost = flag;
    }

    public boolean hasGhost() {
        return hasGhost;
    }
    private boolean isPaintScrollArea = true;

    public void setPaintScrollArea(boolean flag) {
        isPaintScrollArea = flag;
    }

    public boolean isPaintScrollArea() {
        return isPaintScrollArea;
    }

    private int getTargetTabIndex(Point glassPt) {
        Point tabPt = SwingUtilities.convertPoint(glassPane, glassPt, TabPane.this);
        boolean isTB = getTabPlacement() == JTabbedPane.TOP || getTabPlacement() == JTabbedPane.BOTTOM;
        for (int i = 0; i < getTabCount(); i++) {
            Rectangle r = getBoundsAt(i);
            if (isTB)
                r.setRect(r.x - r.width / 2, r.y, r.width, r.height);
            else
                r.setRect(r.x, r.y - r.height / 2, r.width, r.height);
            if (r.contains(tabPt))
                return i;
        }
        Rectangle r = getBoundsAt(getTabCount() - 1);
        if (isTB)
            r.setRect(r.x + r.width / 2, r.y, r.width, r.height);
        else
            r.setRect(r.x, r.y + r.height / 2, r.width, r.height);
        return r.contains(tabPt) ? getTabCount() : -1;
    }

    private void exchangeTab(int prev, int next) {
        if (next < 0 || prev == next) {
            return;
        }

        enableTabEvent = false;
        exchangeTab = true;

        Component cmp = getComponentAt(prev);
        Component tab = getTabComponentAt(prev);
        String str = getTitleAt(prev);
        Icon icon = getIconAt(prev);
        String tip = getToolTipTextAt(prev);
        boolean flg = isEnabledAt(prev);
        int targetIndex = prev > next ? next : next - 1;
        remove(prev);
        insertTab(str, icon, cmp, tip, targetIndex);
        setEnabledAt(targetIndex, flg);
        //When you drag'n'drop a disabled tab, it finishes enabled and selected.
        //pointed out by dlorde
        if (flg)
            setSelectedIndex(targetIndex);

        //I have a component in all tabs (jlabel with an X to close the tab) and when i move a tab the component disappear.
        //pointed out by Daniel Dario Morales Salas
        setTabComponentAt(targetIndex, tab);

        exchangeTab = false;
        enableTabEvent = true;

        // by gocha
        fireTabPaneEvent(new TabExchagedEvent(this, prev, next));
        handleChangeState();
    }

    private void initTargetLeftRightLine(int next) {
        if (next < 0 || dragTabIndex == next || next - dragTabIndex == 1) {
            lineRect.setRect(0, 0, 0, 0);
        } else if (next == 0) {
            Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(0), glassPane);
            lineRect.setRect(r.x - LINEWIDTH / 2, r.y, LINEWIDTH, r.height);
        } else {
            Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(next - 1), glassPane);
            lineRect.setRect(r.x + r.width - LINEWIDTH / 2, r.y, LINEWIDTH, r.height);
        }
    }

    private void initTargetTopBottomLine(int next) {
        if (next < 0 || dragTabIndex == next || next - dragTabIndex == 1) {
            lineRect.setRect(0, 0, 0, 0);
        } else if (next == 0) {
            Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(0), glassPane);
            lineRect.setRect(r.x, r.y - LINEWIDTH / 2, r.width, LINEWIDTH);
        } else {
            Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(next - 1), glassPane);
            lineRect.setRect(r.x, r.y + r.height - LINEWIDTH / 2, r.width, LINEWIDTH);
        }
    }

    private void initGlassPane(Component c, Point tabPt) {
        getRootPane().setGlassPane(glassPane);
        if (hasGhost()) {
            Rectangle rect = getBoundsAt(dragTabIndex);
            BufferedImage image = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();
            c.paint(g);
            rect.x = rect.x < 0 ? 0 : rect.x;
            rect.y = rect.y < 0 ? 0 : rect.y;
            image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
            glassPane.setImage(image);
        }
        Point glassPt = SwingUtilities.convertPoint(c, tabPt, glassPane);
        glassPane.setPoint(glassPt);
        glassPane.setVisible(true);
    }

    private Rectangle getTabAreaBounds() {
        Rectangle tabbedRect = getBounds();
        //pointed out by daryl. NullPointerException: i.e. addTab("Tab",null)
        //Rectangle compRect   = getSelectedComponent().getBounds();
        Component comp = getSelectedComponent();
        int idx = 0;
        while (comp == null && idx < getTabCount())
            comp = getComponentAt(idx++);
        Rectangle compRect = (comp == null) ? new Rectangle() : comp.getBounds();
        int _tabPlacement = getTabPlacement();
        if (_tabPlacement == TOP) {
            tabbedRect.height = tabbedRect.height - compRect.height;
        } else if (_tabPlacement == BOTTOM) {
            tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
            tabbedRect.height = tabbedRect.height - compRect.height;
        } else if (_tabPlacement == LEFT) {
            tabbedRect.width = tabbedRect.width - compRect.width;
        } else if (_tabPlacement == RIGHT) {
            tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
            tabbedRect.width = tabbedRect.width - compRect.width;
        }
        tabbedRect.grow(2, 2);
        return tabbedRect;
    }
    // </editor-fold>
}
