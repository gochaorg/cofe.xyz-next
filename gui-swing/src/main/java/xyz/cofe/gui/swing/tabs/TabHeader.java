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

package xyz.cofe.gui.swing.tabs;

import xyz.cofe.collection.BasicEventList;
import xyz.cofe.collection.EventList;
import xyz.cofe.ecolls.ListenersHelper;
import xyz.cofe.ecolls.TripleConsumer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/**
 * Заголовок вкладки
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TabHeader extends JPanel implements Closeable, TabPane.TabQuery
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(TabHeader.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(TabHeader.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(TabHeader.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(TabHeader.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(TabHeader.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(TabHeader.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(TabHeader.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /**
     * Контейнер вкладок
     */
    private JTabbedPane tabbedPane = null;

    /**
     * Компонент соответ. вкладке
     */
    private Component tabOwner = null;

    /**
     * Текст вкладки
     */
    private JLabel titleLabel = null;

    private List<Action> actions = new ArrayList<Action>();
    private Map<Action,JButton> actions2buttonMap = new HashMap<Action, JButton>();
    private Map<JButton,Action> button2actionMap = new WeakHashMap<JButton, Action>();

    private String fontFamily = null;
    private float fontSize = 10;
    private boolean fontItalic = false;
    private boolean fontBold = false;
    private boolean useOwnerNameAsTitle = false;

    /**
     * Использовать свойство name у объекта владельца вкладки
     * @return true - использовать свойство name
     * @see #setTabOwner(java.awt.Component) 
     */
    public boolean isUseOwnerNameAsTitle() {
        return useOwnerNameAsTitle;
    }

    /**
     * Использовать свойство name у объекта владельца вкладки
     * @param useOwnerNameAsTitle использовать свойство name
     * @see #setTabOwner(java.awt.Component) 
     */
    public void setUseOwnerNameAsTitle(boolean useOwnerNameAsTitle) {
        if( this.useOwnerNameAsTitle ){
            if( tabOwner!=null ){
                tabOwner.removePropertyChangeListener("name",ownerNameListener);
            }
        }
        this.useOwnerNameAsTitle = useOwnerNameAsTitle;
        if( useOwnerNameAsTitle ){
            if( tabOwner!=null ){
                tabOwner.addPropertyChangeListener("name",ownerNameListener);
                if( titleLabel!=null ){
                    String label = tabOwner.getName();
                    if( label==null )label = "";
                    titleLabel.setText(label);
                }
            }
        }
    }

    /**
     * Конструктор заголовка.
     * Компонент соответ. вкладке должен быть на момент вызова конструктора присоединен к контейнеру.
     * @param tabbedPane Контейнер вкладок
     * @param tabOwner Компонент соответ. вкладке
     * @param useOwnerName Использовать имя (свойство name) компонент соответ. вкладке (tabOwner)
     */
    public TabHeader(JTabbedPane tabbedPane, Component tabOwner, boolean useOwnerName){
        if (tabbedPane== null) {
            throw new IllegalArgumentException("tabbedPane==null");
        }
        if (tabOwner== null) {
            throw new IllegalArgumentException("tabComponent==null");
        }

        int index = tabbedPane.indexOfComponent(tabOwner);
//        if( index<0 )throw new Error("tabComponent не присоединен к tabbedPane (indexOfComponent < 0)");

        this.tabOwner = tabOwner;
        this.tabbedPane = tabbedPane;

        String titleText = null;

        this.useOwnerNameAsTitle = useOwnerName;
        if( useOwnerName ){
            titleText = tabOwner.getName();
            tabOwner.addPropertyChangeListener("name", ownerNameListener);
        }else{
            if( index>=0 )
                titleText = tabbedPane.getTitleAt(index);
            else
                titleText = getName();
        }

        if( titleText==null )
//            throw new Error("Не указан заголовок (текст) вкладки (getTitleAt/tabOwner.getName())");
            titleText = "tab title";

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        // title & ico
        titleLabel = null;
        
        javax.swing.Icon titleIco = null;
        if( index>=0 )titleIco = tabbedPane.getIconAt(index);
        
        if( titleIco!=null ){
            titleLabel = new JLabel(titleText, titleIco, SwingConstants.LEFT);
        }else{
            titleLabel = new JLabel(titleText);
            titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        }
        titleLabel.setOpaque(false);
        add(titleLabel);

        // space
        JPanel spacePanel = new JPanel();
        spacePanel.setMinimumSize(new Dimension(3, 3));
        spacePanel.setPreferredSize(new Dimension(3, 3));
        spacePanel.setOpaque(false);
        add(spacePanel);

        // tip
        String tip = null;
        if( index>=0 ){
            tip = tabbedPane.getToolTipTextAt(index);
        }else{
            tip = getToolTipText();
        }
        if( tip!=null )titleLabel.setToolTipText(tip);

        // buttons
        for( Action a : actions ){
            JButton but = createButton(a);
            add( but );
        }

        Font fnt = titleLabel.getFont();
        fontFamily = fnt.getFamily();
        fontSize = fnt.getSize2D();
        fontBold = fnt.isBold();
        fontItalic = fnt.isItalic();
    }

    private PropertyChangeListener ownerNameListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if( tabOwner!=null && titleLabel!=null ){
                String label = tabOwner.getName();
                if( label==null )label = "";
                titleLabel.setText(label);
            }
        }
    };

    private MouseAdapter closeButtonML = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            onMouseEnteredOnButton(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            onMouseExitedFromButton(e);
        }
    };

    // <editor-fold defaultstate="collapsed" desc="Свойства шрифта">
    /**
     * Указывает размер шрифта
     * @return размер шрифта
     */
    public float getFontSize() {
        return fontSize;
    }

    /**
     * Указывает имя/семейство шрифта
     * @return имя/семейство шрифта
     */
    public String getFontFamily() {
        return fontFamily;
    }

    /**
     * Указывает наклонность шрифта
     * @return шрифт наклонен
     */
    public boolean isFontItalic() {
        return fontItalic;
    }

    /**
     * Указывает жирность шрифта
     * @return жирный шрифт
     */
    public boolean isFontBold() {
        return fontBold;
    }

    /**
     * Указывает размер шрифта
     * @param size размер шрифта
     */
    public void setFontSize(float size) {
        if (size <= 0)
            throw new IllegalArgumentException("size<=0");
        Object old = fontSize;
        fontSize = size;
        setTitleLabelFont();
        firePropertyChange("fontSize", old, size);
    }

    /**
     * Указывает имя/семейство шрифта
     * @param family имя/семейство шрифта
     */
    public void setFontFamily(String family) {
        if (family == null)
            throw new IllegalArgumentException("family==null");
        Object old = fontFamily;
        fontFamily = family;
        setTitleLabelFont();
        firePropertyChange("fontFamily", old, family);
    }

    /**
     * Указывает наклонность шрифта
     * @param italic наклонность шрифта
     */
    public void setFontItalic(boolean italic) {
        Object old = fontItalic;
        fontItalic = italic;
        setTitleLabelFont();
        firePropertyChange("fontItalic", old, fontItalic);
    }

    /**
     * Указывает жирность шрифта
     * @param bold жирность шрифта
     */
    public void setFontBold(boolean bold) {
        Object old = fontBold;
        fontBold = bold;
        setTitleLabelFont();
        firePropertyChange("fontBold", old, fontBold);
    }

    protected void setTitleLabelFont() {
        int style = 0;
        if (fontBold || fontItalic) {
            if (fontBold)
                style |= Font.BOLD;
            if (fontItalic)
                style |= Font.ITALIC;
        } else {
            style = Font.PLAIN;
        }

        Font fnt = new Font(fontFamily, style, (int) fontSize);
        titleLabel.setFont(fnt);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Списки действий">
    private EventList<Action> elActions = null;

    /**
     * Возвращает список действий рядом сназванием вкладки
     * @return список действий (кнопок)
     */
    public EventList<Action> getActions() {
        if (elActions != null)
            return elActions;

        elActions = new BasicEventList<>(actions);
        //elActions.addEventListListener(actionsListener);
        TripleConsumer<Integer,Action,Action> onChanged = (k,old,cur) -> {
            if( old!=null ){
                JButton but = destroyButton(old);
                TabHeader.this.remove(but);
            }
            if( cur!=null ){
                JButton but = createButton(cur);

                int co = elActions.size();
                int addIdx = -1;

                if (k < (co - 1) && k >= 0) {
                    if( k < (elActions.size()-1) ){
                        if( elActions.size()>1 ){
                            Action aftAct = elActions.get(1);
                            JButton aftBut = actions2buttonMap.get(aftAct);
                            if( aftBut!=null ){
                                Component[] cmpts = TabHeader.this.getComponents();
                                int ci = -1;
                                for( Component c : cmpts ){
                                    ci++;
                                    if( c==aftBut ){
                                        addIdx = ci;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if( addIdx<0 ){
                    TabHeader.this.add(but);
                }else{
                    TabHeader.this.add(but, addIdx);
                }
            }
        };
        elActions.onInserted(onChanged);
        elActions.onDeleted(onChanged);
        elActions.onDeleted(onChanged);
        return elActions;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Создание и удаление кнопки">
    /**
     * Создает кнопку для отображения ее в заголовке
     * @param action действие
     * @return кнопка
     */
    protected JButton createButton(Action action) {
        if (actions == null) {
            throw new IllegalArgumentException("actions==null");
        }

        if (actions2buttonMap.containsKey(action)) {
            return actions2buttonMap.get(action);
        }

        JButton button = null;
        button = new JButton();

        button.setAction(action);
        button.setOpaque(false);
        button.setBorderPainted(false);

        javax.swing.Icon ico = (javax.swing.Icon) action.getValue(Action.SMALL_ICON);
        if (ico != null) {
            int prefAddX = 2;
            int prefAddY = 2;
            Dimension closeButtonPrefSize = new Dimension(
                    ico.getIconWidth() + prefAddX,
                    ico.getIconHeight() + prefAddY);
            button.setPreferredSize(closeButtonPrefSize);
        }

        button.addMouseListener(closeButtonML);
        button.addMouseMotionListener(closeButtonML);

        actions2buttonMap.put(action, button);
        button2actionMap.put(button, action);

        return button;
    }

    /**
     * Удаляет кнопку с заголовка
     * @param action действие связанное с кнопкой
     * @return Удаленная кнопка
     */
    protected JButton destroyButton(Action action) {
        if (!actions2buttonMap.containsKey(action))
            return null;

        JButton closeButton = actions2buttonMap.get(action);
        if (closeButton != null) {
            closeButton.removeMouseListener(closeButtonML);
            closeButton.removeMouseMotionListener(closeButtonML);
        }
        closeButton.setAction(null);

        actions2buttonMap.remove(action);
        if( closeButton!=null )button2actionMap.remove(closeButton);

        return closeButton;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Наведение мыши на кнопку">
    /**
     * Вызывается при наведении на кнопку.
     * Отображает бордюр вокруг кнопки.
     * @param e Событие мыши
     */
    private void onMouseEnteredOnButton(MouseEvent e) {
        if (isClosed())
            return;

        Object source = e.getSource();
        if (source != null && source instanceof JButton) {
            JButton src = (JButton) source;
            src.setBorderPainted(true);
        }
    }

    /**
     * Вызывается при снятии курсора мыши с кнопки.
     * Скрывает бордюр вокруг кнопки.
     * @param e Событие мыши
     */
    private void onMouseExitedFromButton(MouseEvent e) {
        if (isClosed())
            return;

        Object source = e.getSource();
        if (source != null && source instanceof JButton) {
            JButton src = (JButton) source;
            src.setBorderPainted(false);
        }
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="events + listeners">
    /**
     * Подписчик на события заголовка
     */
    public interface Listener {
        public void tabHeaderEvent( Event event );
    }

    /**
     * События заголовка
     */
    public interface Event {
    }

    /**
     * Адаптер события заголовка
     */
    public static class Adapter implements Listener
    {
        @Override
        public void tabHeaderEvent(Event event) {
            if( event instanceof CloseEvent )close((CloseEvent)event);
        }

        /**
         * Вызывается при закрытии вкладки
         * @param ev событие закрытия вкладки
         */
        protected void close(CloseEvent ev){
        }
    }

    /**
     * событие закрытия вкладки
     */
    public static class CloseEvent implements Event
    {
        private final TabHeader tabHeader;

        public CloseEvent(TabHeader tabHeader) {
            this.tabHeader = tabHeader;
        }

        public TabHeader getTabHeader() {
            return tabHeader;
        }
    }

    private ListenersHelper<Listener, Event> listenersHelper = new ListenersHelper<>(
        (listener, ev) -> {
            logFiner("listenersHelper.apply( {0}, {1} )",listener, ev);
            listener.tabHeaderEvent(ev);
        });

    /**
     * Проверяет наличие подписчика на события
     * @param listener подписчик
     * @return true подписчик установлен
     */
    public boolean hasTabHeaderListener(Listener listener) {
        return listenersHelper.hasListener(listener);
    }

    /**
     * Возвращает подписчиков
     * @return подписчики
     */
    public Set getTabHeaderListeners() {
        return listenersHelper.getListeners();
    }

    /**
     * Добавляет подписчика на события
     * @param listener подписчик
     * @return отписка от уведомлений
     */
    public AutoCloseable addTabHeaderListener(Listener listener) {
        logFine("addScriptListener( {0} )",listener);
        return listenersHelper.addListener(listener);
    }

    /**
     * Добавляет подписчика на события
     * @param listener подписчик
     * @param weakLink true - добавить подписчика как weak ссылка / false - добавить как обычную ссылку
     * @return отписка от уведомлений
     */
    public AutoCloseable addTabHeaderListener(Listener listener, boolean weakLink) {
        logFine("addScriptListener( {0}, {1} )",listener, weakLink);
        return listenersHelper.addListener(listener, weakLink);
    }

    /**
     * Отписка от уведомлений
     * @param listener подписчик
     */
    public void removeTabHeaderListener(Listener listener) {
        logFine("removeScriptListener( {0} )",listener);
        listenersHelper.removeListener(listener);
    }

    /**
     * Рассылка уведомления подписчикам
     * @param event уведомление
     */
    protected void fireEvent(Event event) {
        logFine("fireEvent( {0} )",event);
        listenersHelper.fireEvent(event);
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Закрытие объекта и связанных с ним действий">
    /**
     * Проверят что данный объект уже закрыт для использования (отсуствуют ссылка на внешние объекты)
     * @return true - данный объект уже закрыт
     */
    protected boolean isClosed() {
        return tabbedPane == null && tabOwner == null;
    }

    /**
     * Вызывается контейнером вкладок, когда компонент удаляется из контейнера.
     * Удаляет ссылки на компоненты
     * @throws IOException Ошибка IO
     */
    @Override
    public void close() throws IOException {
        if( !isClosed() ){
            fireEvent(new CloseEvent(this));
        }

        if (tabbedPane != null)
            tabbedPane = null;

        if (tabOwner != null){
            if( useOwnerNameAsTitle ){
                tabOwner.removePropertyChangeListener("name", ownerNameListener);
            }
            tabOwner = null;
        }

        if (titleLabel != null) {
            titleLabel = null;
        }

        if (actions != null) {
            for (Action a : actions) {
                JButton but = destroyButton(a);
                if (but != null)
                    remove(but);

                if (a instanceof Closeable) {
                    try {
                        ((Closeable) a).close();
                    } catch (IOException e) {
                        Logger.getLogger(TabHeader.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
            actions.clear();
//            actions = null;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TabOwner">
    /**
     * Указывает компонент содержимое вкладки
     * @param component компонент содержимое вкладки
     */
    //@Override
    public void setTabOwner(Component component) {
        this.tabOwner = component;
    }

    /**
     * Указывет компонент содержимое вкладки
     * @return компонент содержимое вкладки
     */
    public Component getTabOwner() {
        return tabOwner;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TabbedPane">
    /**
     * Указывает компонент вкладки
     * @return компонент вкладки
     */
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    /**
     * Указывает компонент вкладки
     * @param tabbedPane компонент вкладки
     */
    public void setTabbedPane(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DisabledIcon">
    /**
     * Возвращает иконку обозначающую заблокированную вкладку
     * @param disabledIcon иконка заблокированной вкладку
     */
    //@Override
    public void setTabDisabledIcon(javax.swing.Icon disabledIcon) {
        if (isClosed())return;
        titleLabel.setDisabledIcon(disabledIcon);
    }

    /**
     * Указывает иконку обозначающую заблокированную вкладку
     * @return иконка заблокированной вкладку
     */
    public javax.swing.Icon getTabDisabledIcon() {
        if (isClosed())return null;
        return titleLabel.getDisabledIcon();
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DisplayedMnemonicIndex">
    //@Override
    public void setTabDisplayedMnemonicIndex(int mnemonicIndex) {
        if (isClosed())
            return;
        titleLabel.setDisplayedMnemonicIndex(mnemonicIndex);
    }

    public int getTabDisplayedMnemonicIndex() {
        if (isClosed())
            return -1;
        return titleLabel.getDisplayedMnemonicIndex();
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Icon">
    /**
     * Указывает иконку отображаемую перед именем вкладки
     * @param icon иконка вкладки
     */
    //@Override
    public void setTabIcon(javax.swing.Icon icon) {
        if (isClosed())
            return;
        titleLabel.setIcon(icon);
    }

    /**
     * Указывает иконку отображаемую перед именем вкладки
     * @return иконка вкладки
     */
    public javax.swing.Icon getTabIcon() {
        if (isClosed())
            return null;
        return titleLabel.getIcon();
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Mnemonic">
    //@Override
    public void setTabMnemonic(int mnemonic) {
        if (isClosed())
            return;
        titleLabel.setDisplayedMnemonic(mnemonic);
    }

    public int getTabMnemonic() {
        if (isClosed())
            return -1;
        return titleLabel.getDisplayedMnemonic();
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Title">
    /**
     * Указывает текст закладки
     * @param title текст закладки
     */
    //@Override
    public void setTabTitle(String title) {
        if (isClosed())
            return;
        titleLabel.setText(title == null ? "" : title);
    }

    /**
     * Возвращает текст закладки
     * @return текст закладки
     */
    public String getTabTitle() {
        if (isClosed())
            return null;
        return titleLabel.getText();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TabBackground">
    /**
     * Возвращает цвет фона
     * @param background цвет фона
     */
    //@Override
    public void setTabBackground(Color background) {
        titleLabel.setForeground(background);
    }

    /**
     * Указывает цвет фона
     * @return цвет фона
     */
    public Color getTabBackground() {
        return titleLabel.getBackground();
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TabEnabled">
    /**
     * Указывает "разрешено ли" пользователю нажимать на кнопки заголовка
     * @param enabled true - пользователю разрешено нажимать на кнопки
     */
    //@Override
    public void setTabEnabled(boolean enabled) {
        this.setEnabled(enabled);
        for (Component c : getComponents()) {
            if (enabled) {
                if (c instanceof JButton) {
                    JButton but = (JButton) c;
                    Action a = but.getAction();
                    if (a != null) {
                        but.setEnabled(a.isEnabled());
                    } else {
                        c.setEnabled(enabled);
                    }
                } else {
                    c.setEnabled(enabled);
                }
            } else {
                c.setEnabled(enabled);
            }
        }
    }

    /**
     * Указывает "разрешено ли" пользователю нажимать на кнопки заголовка
     * @return true - пользователю разрешено нажимать на кнопки
     */
    public boolean isTabEnabled() {
        return this.isEnabled();
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TabForeground">
    /**
     * Указывает цвет текста
     * @param foreground цвет текста
     */
    //@Override
    public void setTabForeground(Color foreground) {
        titleLabel.setForeground(foreground);
    }

    /**
     * Указывает цвет текста
     * @return цвет текста
     */
    public Color getTabForeground() {
        return titleLabel.getBackground();
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TabToolTipText">
    /**
     * Указывает текст подсказки всплывающей при наведении мыши
     * @param toolTipText текст подсказки
     */
    @Override
    public void setTabToolTipText(String toolTipText) {
        titleLabel.setToolTipText(toolTipText);
    }

    /**
     * Указывает текст подсказки всплывающей при наведении мыши
     * @return текст подсказки
     */
    public String getTabToolTipText() {
        return titleLabel.getToolTipText();
    }// </editor-fold>
}
