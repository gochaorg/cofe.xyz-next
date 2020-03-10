/*
 * The MIT License
 *
 * Copyright 2014 Kamnev Georgiy (nt.gocha@gmail.com).
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
package xyz.cofe.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URL;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import xyz.cofe.collection.BasicEventSet;
import xyz.cofe.collection.EventSet;
import xyz.cofe.iter.Eterable;

/**
 * Базовый класс действия
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class BasicAction extends javax.swing.AbstractAction implements GetTarget
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(BasicAction.class.getName());
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

    /**
	 * Информация о действии
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Desc {
        /**
         * Идентификатор
         * @return Идентификатор
         */
        String id() default "";

		/**
		 * Отображаемое имя
         * @return Отображаемое имя
		 */
		String name() default "";

		/**
		 * Краткое описание
         * @return  Краткое описание
		 */
		String shortDesc() default "";

		/**
		 * Длинное описание
         * @return Длинное описание
		 */
		String longDesc() default "";

        /**
         * Клавиатурное сокращение. <br>
         * &lt;modifiers&gt;* (&lt;typedID&gt; | &lt;pressedReleasedID&gt;) <br>
         * modifiers := shift | control | ctrl | meta | alt | altGraph <br>
         * typedID := typed &lt;typedKey&gt; <br>
         * typedKey := string of length 1 giving Unicode character. <br>
         * pressedReleasedID := (pressed | released) key <br>
         * key := KeyEvent key code NAME_PROPERTY, i.e. the NAME_PROPERTY following "VK_".
         * @return Клавиатурное сокращение
         */
        String keyStroke() default "";

        /**
         * Имя ресурса иконки
         * @return Имя ресурса иконки
         */
        String smallIconResource() default "";

        /**
         * Имя ресурса иконки
         * @return Имя ресурса иконки
         */
        String largeIconResource() default "";
    }

    /**
     * Конструктор по умолчанию
     */
    public BasicAction()
    {
    }

    /**
     * Конструктор
     * @param name Имя (отображаемый текст)
     */
    public BasicAction(String name)
    {
        if( name!=null )setName(name);
    }

    /**
     * Конструктор
     * @param name Имя (отображаемый текст)
     * @param listener Обработчик
     */
    public BasicAction(String name,ActionListener listener)
    {
        if( name!=null )setName(name);
        if( listener!=null ){ setActionListener(actionListener); }
    }

    /**
     * Конструктор
     * @param name Имя (отображаемый текст)
     * @param listener Обработчик
     */
    public BasicAction(String name, final Runnable listener)
    {
        if( name!=null )setName(name);
        if( listener!=null ){ 
            setActionListener( 
                    new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.run();
                }
            }); 
        }
    }

    /**
     * Конструктор копирования
     * @param action Образец
     */
    public BasicAction(Action action){
        this(action, DEF);        
    }

    //<editor-fold defaultstate="collapsed" desc="apply()">
    /**
     * Применяет указанную информацию к "действию"
     * @param act Действие
     * @param desc Информаия
     */
    public static void apply(Action act,Desc desc){
        if (act== null) {
            throw new IllegalArgumentException("act==null");
        }
        if (desc== null) {
            throw new IllegalArgumentException("desc==null");
        }
        if( desc.keyStroke().length()>0 ){
            KeyStroke ks = KeyStroke.getKeyStroke(desc.keyStroke());
            if( ks!=null )act.putValue(Action.ACCELERATOR_KEY, ks);
        }
        if( desc.name().length()>0 ){
            act.putValue(Action.NAME, desc.name());
        }
        if( desc.shortDesc().length()>0 ){
            act.putValue(Action.SHORT_DESCRIPTION, desc.shortDesc());
        }
        if( desc.longDesc().length()>0 ){
            act.putValue(Action.LONG_DESCRIPTION, desc.longDesc());
        }
        if( desc.smallIconResource().length()>0 ){
            URL ico = act.getClass().getResource(desc.smallIconResource());
            if( ico!=null ){
                ImageIcon iicon = new ImageIcon(ico);
                act.putValue(Action.SMALL_ICON, iicon);
            }
        }
        if( desc.largeIconResource().length()>0 ){
            URL ico = act.getClass().getResource(desc.largeIconResource());
            if( ico!=null ){
                ImageIcon iicon = new ImageIcon(ico);
                act.putValue(Action.LARGE_ICON_KEY, iicon);
            }
        }
    }
//</editor-fold>

    /**
     * Конструктор копирования
     * @param action Образец
     * @param copyProperties Копируемые значения
     */
    public BasicAction(Action action,int copyProperties){
        if( action!=null ){
            if( (copyProperties&NAME_PROPERTY)==NAME_PROPERTY ){
                Object _name = action.getValue(Action.NAME);
                if( _name!=null && _name instanceof String )setName((String)_name);
            }

            if( (copyProperties&SMALLICON_PROPERTY)==SMALLICON_PROPERTY ){
                Object smallIco = action.getValue(Action.SMALL_ICON);
                if( smallIco!=null && smallIco instanceof javax.swing.Icon )setSmallIcon((javax.swing.Icon)smallIco);
            }

            if( (copyProperties&LARGEICON_PROPERTY)==LARGEICON_PROPERTY ){
                Object largeIco = action.getValue(Action.LARGE_ICON_KEY);
                if( largeIco!=null && largeIco instanceof javax.swing.Icon )setLargeIcon((javax.swing.Icon)largeIco);
            }

            if( (copyProperties&ACCELERATOR_PROPERTY)==ACCELERATOR_PROPERTY ){
                Object acc = action.getValue(Action.ACCELERATOR_KEY);
                if( acc!=null && acc instanceof KeyStroke )setAccelerator((KeyStroke)acc);
            }

            if( (copyProperties&SHORTDESCRIPTION_PROPERTY)==SHORTDESCRIPTION_PROPERTY ){
                Object shortDesc = action.getValue(Action.SHORT_DESCRIPTION);
                if( shortDesc!=null && shortDesc instanceof String )setShortDescription((String)shortDesc);
            }

            if( (copyProperties&LONGDESCRIPTION_PROPERTY)==LONGDESCRIPTION_PROPERTY ){
                Object longDesc = action.getValue(Action.LONG_DESCRIPTION);
                if( longDesc!=null && longDesc instanceof String )setLongDescription((String)longDesc);
            }

            if( (copyProperties&DIPLAYMNEMONICINDEX_PROPERTY)==DIPLAYMNEMONICINDEX_PROPERTY ){
                Object dmi = action.getValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY);
                if( dmi!=null && dmi instanceof Integer )setDisplayedMnemonicIndex((Integer)dmi);
            }

            if( (copyProperties&ACTIONCOMMAND_PROPERTY)==ACTIONCOMMAND_PROPERTY ){
                Object acC = action.getValue(Action.ACTION_COMMAND_KEY);
                if( acC!=null && acC instanceof String )setActionCommand((String)acC);
            }

            if( (copyProperties&MNEMONIC_PROPERTY)==MNEMONIC_PROPERTY ){
                Object mnK = action.getValue(Action.MNEMONIC_KEY);
                if( mnK!=null && mnK instanceof Integer )setMnemonic((Integer)mnK);
            }

            if( (copyProperties&SELECTED_PROPERTY)==SELECTED_PROPERTY ){
                Object select = action.getValue(Action.SELECTED_KEY);
                if( select!=null && select instanceof Boolean )setSelected((Boolean)select);
            }

            if( (copyProperties&KEYBOARDSHORTCUTS_PROPERTY)==KEYBOARDSHORTCUTS_PROPERTY && action instanceof BasicAction ){
                getKeyboardShortcuts().clear();
                for( KeyboardShortcut ksSrc : ((BasicAction)action).getKeyboardShortcuts() ){
                    KeyboardShortcut ks = new KeyboardShortcut(ksSrc);
                    getKeyboardShortcuts().add(ks);
                }
            }

            if( (copyProperties&TARGET_PROPERTY)==TARGET_PROPERTY ){
                if( action instanceof BasicAction ){
                    setTarget( ((BasicAction)action).getTarget() );
                }
            }

            if( (copyProperties&ACTIONLISTENER_PROPERTY)==ACTIONLISTENER_PROPERTY ){
                if( action instanceof BasicAction ){
                    setActionListener(((BasicAction)action).getActionListener());
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="SELECTED_PROPERTY">
    public static final int SELECTED_PROPERTY = 0x01;
    
    /**
     * Имя свойства "selected"
     */
    public static final String SELECTED_PROP = "selected";
    
    /**
     * Возвращает выделен/выбран ли объект UI пользователем
     * @return true объект выделен
     */
    public boolean isSelected()
    {
        Boolean selected = (Boolean) super.getValue(Action.SELECTED_KEY);
        if (selected == null) {
            return false;
        }
        return selected;
    }

    /**
     * Указывает выделен/выбран ли объект UI пользователем
     * @param selected true объект выделен/выбран
     */
    public void setSelected(boolean selected)
    {
        Object old = isSelected();
        putValue(Action.SELECTED_KEY, (Boolean) selected);
        firePropertyChange(SELECTED_PROP, old, selected);
//        fireChanged();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Name">
    public static final int NAME_PROPERTY = 0x02;
    public static final String NAME_PROP = "name";
    
    /**
     * Возвращает имя/текст действия
     * @return имя/текст действия
     */
    public String getName()
    {
        String text = (String) super.getValue(Action.NAME);
        return text;
    }
    
    /**
     * Возвращает имя/текст действия
     * @param act действие
     * @return имя/текст
     */
    public static String getName(Action act){
        if( act==null )throw new IllegalArgumentException("act == null");
        return (String)act.getValue(Action.NAME);
    }

    /**
     * Указывает имя/текст действия
     * @param text текст
     */
    public void setName(String text)
    {
        Object old = getName();
        putValue(Action.NAME, text);
        firePropertyChange(NAME_PROP, old, text);
//        fireChanged();
    }

    /**
     * Указывает имя/текст действия
     * @param act действие
     * @param name имя/текст
     */
    public static void setName(Action act,String name){
        if( act==null )throw new IllegalArgumentException("act == null");
        act.putValue(Action.NAME, name);
    }
    
    /**
     * Указывает имя/текст действия
     * @param text имя/текст
     * @return self ссылка
     */
    public BasicAction name(String text){
        setName(text);
        return this;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SmallIcon">
    public static final int SMALLICON_PROPERTY = 0x04;
    public static final String SMALL_ICON_PROP = "smallIcon";
    
    /**
     * Возвращает иконку (малую) действия
     * @return иконка
     */
    public javax.swing.Icon getSmallIcon()
    {
        Object o = getValue(SMALL_ICON);
        if( o!=null && o instanceof javax.swing.Icon )return (javax.swing.Icon)o;
        return null;
    }
    
    /**
     * Возвращает иконку (малую) действия
     * @param act действие
     * @return иконка
     */
    public static javax.swing.Icon getSmallIcon(Action act){
        if( act==null )throw new IllegalArgumentException("act == null");
        return (javax.swing.Icon)act.getValue(Action.SMALL_ICON);
    }

    /**
     * Указывает иконку (малую) действия
     * @param newIcon иконка
     */
    public void setSmallIcon(javax.swing.Icon newIcon)
    {
        Object old = getSmallIcon();
        putValue(SMALL_ICON, newIcon);
        firePropertyChange(SMALL_ICON_PROP, old, newIcon);
//        fireChanged();
    }

    /**
     * Указывает иконку (малую) действия
     * @param newIcon иконка
     * @return self ссылка
     */
    public BasicAction smallIcon(javax.swing.Icon newIcon){
        setSmallIcon(newIcon);
        return this;
    }
    
    /**
     * Указывает иконку (малую) действия
     * @param act действие
     * @param newIcon иконка
     */
    public static void setSmallIcon(Action act,javax.swing.Icon newIcon){
        if( act==null )throw new IllegalArgumentException("act == null");
        act.putValue(Action.SMALL_ICON, newIcon);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LargeIcon">
    public static final int LARGEICON_PROPERTY = 0x08;
    public static final String LARGE_ICON_PROP = "largeIcon";

    /**
     * Возвращает (большую) иконку действия
     * @return иконка
     */
    public javax.swing.Icon getLargeIcon()
    {
        Object o = getValue(Action.LARGE_ICON_KEY);
        if( o!=null && o instanceof Icon )return (Icon)o;
        return null;
    }

    /**
     * Возвращает (большую) иконку действия
     * @param act действие
     * @return иконка
     */
    public static javax.swing.Icon getLargeIcon(Action act){
        if( act==null )throw new IllegalArgumentException("act == null");
        return (javax.swing.Icon)act.getValue(Action.LARGE_ICON_KEY);
    }

    /**
     * Указывает (большую) иконку действия
     * @param i иконка
     */
    public void setLargeIcon(javax.swing.Icon i)
    {
        Object old = getLargeIcon();
        putValue(Action.LARGE_ICON_KEY, i);
        firePropertyChange(LARGE_ICON_PROP, old, i);
//        fireChanged();
    }

    /**
     * Указывает (большую) иконку действия
     * @param i иконка
     * @return self ссылка
     */
    public BasicAction largeIcon(javax.swing.Icon i){
        setLargeIcon(i);
        return this;
    }

    /**
     * Указывает (большую) иконку действия
     * @param act действие
     * @param newIcon иконка
     */
    public static void setLargeIcon(Action act,javax.swing.Icon newIcon){
        if( act==null )throw new IllegalArgumentException("act == null");
        act.putValue(Action.LARGE_ICON_KEY, newIcon);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="AcceleratorBuilder">
    public final static int ACCELERATOR_PROPERTY=0x10;
    public final static String ACCELERATOR_PROP = "accelerator";
    public static KeyStroke getAccelerator(Action a)
    {
        if(a==null)return null;
        return (KeyStroke) ((Action)a).getValue(Action.ACCELERATOR_KEY);
    }

    public KeyStroke getAccelerator()
    {
        return getAccelerator(this);
    }

    public void setAccelerator(KeyStroke newAcceleratorKeyStroke)
    {
        Object old = getAccelerator();
        putValue(Action.ACCELERATOR_KEY, newAcceleratorKeyStroke);
        firePropertyChange(ACCELERATOR_PROP, old, newAcceleratorKeyStroke);
    }
    
    public BasicAction accelerator(KeyStroke newAcceleratorKeyStroke){
        setAccelerator(newAcceleratorKeyStroke);
        return this;
    }
    
    public static void setAccelerator(Action act, KeyStroke newAcceleratorKeyStroke)
    {
        if( act==null )throw new IllegalArgumentException("act == null");
        act.putValue(Action.ACCELERATOR_KEY, newAcceleratorKeyStroke);
    }

    /**
     * Создание комбинации клавиш
     */
    public static class AcceleratorBuilder {
        //<editor-fold defaultstate="collapsed" desc="keyCode">
        protected Integer keyCode = null;

        /**
         * Указывает код клавиши
         * @return код клавиши
         */
        public Integer getKeyCode() {
            return keyCode;
        }

        /**
         * Указывает код клавиши
         * @param keyCode код клавиши
         */
        public void setKeyCode(Integer keyCode) {
            this.keyCode = keyCode;
        }

        /**
         * Указывает код клавиши
         * @param keycode код клавиши
         * @return self ссылка
         */
        public AcceleratorBuilder code(int keycode){
            this.keyCode = keycode;
            keyChar = null;
            return this;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="keyXxxx()">
        public AcceleratorBuilder keyEnter(){ return code( java.awt.event.KeyEvent.VK_ENTER ); }
        public AcceleratorBuilder keyTab(){ return code( java.awt.event.KeyEvent.VK_TAB ); }
        public AcceleratorBuilder keySpace(){ return code( java.awt.event.KeyEvent.VK_SPACE ); }
        
        public AcceleratorBuilder key0(){ return code( java.awt.event.KeyEvent.VK_0 ); }
        public AcceleratorBuilder key1(){ return code( java.awt.event.KeyEvent.VK_1 ); }
        public AcceleratorBuilder key2(){ return code( java.awt.event.KeyEvent.VK_2 ); }
        public AcceleratorBuilder key3(){ return code( java.awt.event.KeyEvent.VK_3 ); }
        public AcceleratorBuilder key4(){ return code( java.awt.event.KeyEvent.VK_4 ); }
        public AcceleratorBuilder key5(){ return code( java.awt.event.KeyEvent.VK_5 ); }
        public AcceleratorBuilder key6(){ return code( java.awt.event.KeyEvent.VK_6 ); }
        public AcceleratorBuilder key7(){ return code( java.awt.event.KeyEvent.VK_7 ); }
        public AcceleratorBuilder key8(){ return code( java.awt.event.KeyEvent.VK_8 ); }
        public AcceleratorBuilder key9(){ return code( java.awt.event.KeyEvent.VK_9 ); }
        
        public AcceleratorBuilder keyA(){ return code( java.awt.event.KeyEvent.VK_A ); }
        public AcceleratorBuilder keyB(){ return code( java.awt.event.KeyEvent.VK_B ); }
        public AcceleratorBuilder keyC(){ return code( java.awt.event.KeyEvent.VK_C ); }
        public AcceleratorBuilder keyD(){ return code( java.awt.event.KeyEvent.VK_D ); }
        public AcceleratorBuilder keyE(){ return code( java.awt.event.KeyEvent.VK_E ); }
        public AcceleratorBuilder keyF(){ return code( java.awt.event.KeyEvent.VK_F ); }
        public AcceleratorBuilder keyS(){ return code( java.awt.event.KeyEvent.VK_S ); }
        public AcceleratorBuilder keyR(){ return code( java.awt.event.KeyEvent.VK_R ); }
        public AcceleratorBuilder keyQ(){ return code( java.awt.event.KeyEvent.VK_Q ); }
        public AcceleratorBuilder keyP(){ return code( java.awt.event.KeyEvent.VK_P ); }
        public AcceleratorBuilder keyO(){ return code( java.awt.event.KeyEvent.VK_O ); }
        public AcceleratorBuilder keyN(){ return code( java.awt.event.KeyEvent.VK_N ); }
        public AcceleratorBuilder keyM(){ return code( java.awt.event.KeyEvent.VK_M ); }
        public AcceleratorBuilder keyL(){ return code( java.awt.event.KeyEvent.VK_L ); }
        public AcceleratorBuilder keyK(){ return code( java.awt.event.KeyEvent.VK_K ); }
        public AcceleratorBuilder keyI(){ return code( java.awt.event.KeyEvent.VK_I ); }
        public AcceleratorBuilder keyJ(){ return code( java.awt.event.KeyEvent.VK_J ); }
        public AcceleratorBuilder keyH(){ return code( java.awt.event.KeyEvent.VK_H ); }
        public AcceleratorBuilder keyG(){ return code( java.awt.event.KeyEvent.VK_G ); }
        public AcceleratorBuilder keyT(){ return code( java.awt.event.KeyEvent.VK_T ); }
        public AcceleratorBuilder keyU(){ return code( java.awt.event.KeyEvent.VK_U ); }
        public AcceleratorBuilder keyX(){ return code( java.awt.event.KeyEvent.VK_X ); }
        public AcceleratorBuilder keyY(){ return code( java.awt.event.KeyEvent.VK_Y ); }
        public AcceleratorBuilder keyZ(){ return code( java.awt.event.KeyEvent.VK_Z ); }
        
        public AcceleratorBuilder keyF1(){ return code( java.awt.event.KeyEvent.VK_F1 ); }
        public AcceleratorBuilder keyF2(){ return code( java.awt.event.KeyEvent.VK_F2 ); }
        public AcceleratorBuilder keyF3(){ return code( java.awt.event.KeyEvent.VK_F3 ); }
        public AcceleratorBuilder keyF4(){ return code( java.awt.event.KeyEvent.VK_F4 ); }
        public AcceleratorBuilder keyF5(){ return code( java.awt.event.KeyEvent.VK_F5 ); }
        public AcceleratorBuilder keyF6(){ return code( java.awt.event.KeyEvent.VK_F6 ); }
        public AcceleratorBuilder keyF7(){ return code( java.awt.event.KeyEvent.VK_F7 ); }
        public AcceleratorBuilder keyF8(){ return code( java.awt.event.KeyEvent.VK_F8 ); }
        public AcceleratorBuilder keyF9(){ return code( java.awt.event.KeyEvent.VK_F9 ); }
        public AcceleratorBuilder keyF10(){ return code( java.awt.event.KeyEvent.VK_F10 ); }
        public AcceleratorBuilder keyF11(){ return code( java.awt.event.KeyEvent.VK_F11 ); }
        public AcceleratorBuilder keyF12(){ return code( java.awt.event.KeyEvent.VK_F12 ); }
        public AcceleratorBuilder keyF13(){ return code( java.awt.event.KeyEvent.VK_F13 ); }
        public AcceleratorBuilder keyF14(){ return code( java.awt.event.KeyEvent.VK_F14 ); }
        public AcceleratorBuilder keyF15(){ return code( java.awt.event.KeyEvent.VK_F15 ); }
        public AcceleratorBuilder keyF16(){ return code( java.awt.event.KeyEvent.VK_F16 ); }
        public AcceleratorBuilder keyF17(){ return code( java.awt.event.KeyEvent.VK_F17 ); }
        public AcceleratorBuilder keyF18(){ return code( java.awt.event.KeyEvent.VK_F18 ); }
        public AcceleratorBuilder keyF19(){ return code( java.awt.event.KeyEvent.VK_F19 ); }
        public AcceleratorBuilder keyF20(){ return code( java.awt.event.KeyEvent.VK_F20 ); }
        public AcceleratorBuilder keyF21(){ return code( java.awt.event.KeyEvent.VK_F21 ); }
        public AcceleratorBuilder keyF22(){ return code( java.awt.event.KeyEvent.VK_F22 ); }
        public AcceleratorBuilder keyF23(){ return code( java.awt.event.KeyEvent.VK_F23 ); }
        public AcceleratorBuilder keyF24(){ return code( java.awt.event.KeyEvent.VK_F24 ); }
        
        public AcceleratorBuilder keyWindows(){ return code( java.awt.event.KeyEvent.VK_WINDOWS ); }
        
        public AcceleratorBuilder keyUp(){ return code( java.awt.event.KeyEvent.VK_UP ); }
        public AcceleratorBuilder keyDown(){ return code( java.awt.event.KeyEvent.VK_DOWN ); }
        public AcceleratorBuilder keyLeft(){ return code( java.awt.event.KeyEvent.VK_LEFT ); }
        public AcceleratorBuilder keyRight(){ return code( java.awt.event.KeyEvent.VK_RIGHT ); }
        
        public AcceleratorBuilder keyAdd(){ return code( java.awt.event.KeyEvent.VK_ADD ); }
        public AcceleratorBuilder keyAgain(){ return code( java.awt.event.KeyEvent.VK_AGAIN ); }
        public AcceleratorBuilder keyAllCandidates(){ return code( java.awt.event.KeyEvent.VK_ALL_CANDIDATES ); }
        public AcceleratorBuilder keyAlphanumeric(){ return code( java.awt.event.KeyEvent.VK_ALPHANUMERIC ); }
        public AcceleratorBuilder keyAmpersand(){ return code( java.awt.event.KeyEvent.VK_AMPERSAND ); }
        public AcceleratorBuilder keyAsterisk(){ return code( java.awt.event.KeyEvent.VK_ASTERISK ); }
        public AcceleratorBuilder keyAt(){ return code( java.awt.event.KeyEvent.VK_AT ); }
        public AcceleratorBuilder keyBackQuote(){ return code( java.awt.event.KeyEvent.VK_BACK_QUOTE ); }
        public AcceleratorBuilder keyBackSlash(){ return code( java.awt.event.KeyEvent.VK_BACK_SLASH ); }
        public AcceleratorBuilder keyBackSpace(){ return code( java.awt.event.KeyEvent.VK_BACK_SPACE ); }
        public AcceleratorBuilder keyBegin(){ return code( java.awt.event.KeyEvent.VK_BEGIN ); }
        public AcceleratorBuilder keyBraceleft(){ return code( java.awt.event.KeyEvent.VK_BRACELEFT ); }
        public AcceleratorBuilder keyBraceright(){ return code( java.awt.event.KeyEvent.VK_BRACERIGHT ); }
        public AcceleratorBuilder keyCancel(){ return code( java.awt.event.KeyEvent.VK_CANCEL ); }
        public AcceleratorBuilder keyCapsLock(){ return code( java.awt.event.KeyEvent.VK_CAPS_LOCK ); }
        public AcceleratorBuilder keyCircumflex(){ return code( java.awt.event.KeyEvent.VK_CIRCUMFLEX ); }
        public AcceleratorBuilder keyClear(){ return code( java.awt.event.KeyEvent.VK_CLEAR ); }
        public AcceleratorBuilder keyCloseBracket(){ return code( java.awt.event.KeyEvent.VK_CLOSE_BRACKET ); }
        public AcceleratorBuilder keyCodeInput(){ return code( java.awt.event.KeyEvent.VK_CODE_INPUT ); }
        public AcceleratorBuilder keyColon(){ return code( java.awt.event.KeyEvent.VK_COLON ); }
        public AcceleratorBuilder keyComma(){ return code( java.awt.event.KeyEvent.VK_COMMA ); }
        public AcceleratorBuilder keyCompose(){ return code( java.awt.event.KeyEvent.VK_COMPOSE ); }
        public AcceleratorBuilder keyContextMenu(){ return code( java.awt.event.KeyEvent.VK_CONTEXT_MENU ); }
        public AcceleratorBuilder keyConvert(){ return code( java.awt.event.KeyEvent.VK_CONVERT ); }
        public AcceleratorBuilder keyControl(){ return code( java.awt.event.KeyEvent.VK_CONTROL ); }
        public AcceleratorBuilder keyCopy(){ return code( java.awt.event.KeyEvent.VK_COPY ); }
        public AcceleratorBuilder keyCut(){ return code( java.awt.event.KeyEvent.VK_CUT ); }
        public AcceleratorBuilder keyDelete(){ return code( java.awt.event.KeyEvent.VK_DELETE ); }
        public AcceleratorBuilder keyDollar(){ return code( java.awt.event.KeyEvent.VK_DOLLAR ); }
        public AcceleratorBuilder keyEnd(){ return code( java.awt.event.KeyEvent.VK_END ); }
        public AcceleratorBuilder keyEscape(){ return code( java.awt.event.KeyEvent.VK_ESCAPE ); }
        public AcceleratorBuilder keyEquals(){ return code( java.awt.event.KeyEvent.VK_EQUALS ); }
        public AcceleratorBuilder keyEuroSign(){ return code( java.awt.event.KeyEvent.VK_EURO_SIGN ); }
        public AcceleratorBuilder keyExclamationMark(){ return code( java.awt.event.KeyEvent.VK_EXCLAMATION_MARK ); }
        public AcceleratorBuilder keyFinal(){ return code( java.awt.event.KeyEvent.VK_FINAL ); }
        public AcceleratorBuilder keyFind(){ return code( java.awt.event.KeyEvent.VK_FIND ); }
        public AcceleratorBuilder keyFullWidth(){ return code( java.awt.event.KeyEvent.VK_FULL_WIDTH ); }
        public AcceleratorBuilder keyGreater(){ return code( java.awt.event.KeyEvent.VK_GREATER ); }
        public AcceleratorBuilder keyHalfWidth(){ return code( java.awt.event.KeyEvent.VK_HALF_WIDTH ); }
        public AcceleratorBuilder keyHelp(){ return code( java.awt.event.KeyEvent.VK_HELP ); }
        public AcceleratorBuilder keyHiragana(){ return code( java.awt.event.KeyEvent.VK_HIRAGANA ); }
        public AcceleratorBuilder keyHome(){ return code( java.awt.event.KeyEvent.VK_HOME ); }
        public AcceleratorBuilder keyInsert(){ return code( java.awt.event.KeyEvent.VK_INSERT ); }
        public AcceleratorBuilder keyKpDown(){ return code( java.awt.event.KeyEvent.VK_KP_DOWN ); }
        public AcceleratorBuilder keyKpLeft(){ return code( java.awt.event.KeyEvent.VK_KP_LEFT ); }
        public AcceleratorBuilder keyKpRight(){ return code( java.awt.event.KeyEvent.VK_KP_RIGHT ); }
        public AcceleratorBuilder keyKpUp(){ return code( java.awt.event.KeyEvent.VK_KP_UP ); }
        public AcceleratorBuilder keyLess(){ return code( java.awt.event.KeyEvent.VK_LESS ); }
        public AcceleratorBuilder keyLeftParenthesis(){ return code( java.awt.event.KeyEvent.VK_LEFT_PARENTHESIS ); }
        public AcceleratorBuilder keyMinus(){ return code( java.awt.event.KeyEvent.VK_MINUS ); }
        public AcceleratorBuilder keyMeta(){ return code( java.awt.event.KeyEvent.VK_META ); }
        public AcceleratorBuilder keyModechange(){ return code( java.awt.event.KeyEvent.VK_MODECHANGE ); }
        public AcceleratorBuilder keyNonconvert(){ return code( java.awt.event.KeyEvent.VK_NONCONVERT ); }
        public AcceleratorBuilder keyPlus(){ return code( java.awt.event.KeyEvent.VK_PLUS ); }
        public AcceleratorBuilder keyNumLock(){ return code( java.awt.event.KeyEvent.VK_NUM_LOCK ); }
        public AcceleratorBuilder keyNumberSign(){ return code( java.awt.event.KeyEvent.VK_NUMBER_SIGN ); }
        public AcceleratorBuilder keyNumpad0(){ return code( java.awt.event.KeyEvent.VK_NUMPAD0 ); }
        public AcceleratorBuilder keyNumpad1(){ return code( java.awt.event.KeyEvent.VK_NUMPAD1 ); }
        public AcceleratorBuilder keyNumpad2(){ return code( java.awt.event.KeyEvent.VK_NUMPAD2 ); }
        public AcceleratorBuilder keyNumpad3(){ return code( java.awt.event.KeyEvent.VK_NUMPAD3 ); }
        public AcceleratorBuilder keyNumpad4(){ return code( java.awt.event.KeyEvent.VK_NUMPAD4 ); }
        public AcceleratorBuilder keyNumpad5(){ return code( java.awt.event.KeyEvent.VK_NUMPAD5 ); }
        public AcceleratorBuilder keyNumpad6(){ return code( java.awt.event.KeyEvent.VK_NUMPAD6 ); }
        public AcceleratorBuilder keyNumpad7(){ return code( java.awt.event.KeyEvent.VK_NUMPAD7 ); }
        public AcceleratorBuilder keyNumpad8(){ return code( java.awt.event.KeyEvent.VK_NUMPAD8 ); }
        public AcceleratorBuilder keyNumpad9(){ return code( java.awt.event.KeyEvent.VK_NUMPAD9 ); }
        public AcceleratorBuilder keyOpenBracket(){ return code( java.awt.event.KeyEvent.VK_OPEN_BRACKET ); }
        public AcceleratorBuilder keyPageDown(){ return code( java.awt.event.KeyEvent.VK_PAGE_DOWN ); }
        public AcceleratorBuilder keyPageUp(){ return code( java.awt.event.KeyEvent.VK_PAGE_UP ); }
        public AcceleratorBuilder keyPaste(){ return code( java.awt.event.KeyEvent.VK_PASTE ); }
        public AcceleratorBuilder keyPause(){ return code( java.awt.event.KeyEvent.VK_PAUSE ); }
        public AcceleratorBuilder keyPreviousCandidate(){ return code( java.awt.event.KeyEvent.VK_PREVIOUS_CANDIDATE ); }
        public AcceleratorBuilder keyPrintscreen(){ return code( java.awt.event.KeyEvent.VK_PRINTSCREEN ); }
        public AcceleratorBuilder keyProps(){ return code( java.awt.event.KeyEvent.VK_PROPS ); }
        public AcceleratorBuilder keyQuote(){ return code( java.awt.event.KeyEvent.VK_QUOTE ); }
        public AcceleratorBuilder keyQuotedbl(){ return code( java.awt.event.KeyEvent.VK_QUOTEDBL ); }
        public AcceleratorBuilder keyRightParenthesis(){ return code( java.awt.event.KeyEvent.VK_RIGHT_PARENTHESIS ); }
        public AcceleratorBuilder keyRomanCharacters(){ return code( java.awt.event.KeyEvent.VK_ROMAN_CHARACTERS ); }
        public AcceleratorBuilder keyScrollLock(){ return code( java.awt.event.KeyEvent.VK_SCROLL_LOCK ); }
        public AcceleratorBuilder keySemicolon(){ return code( java.awt.event.KeyEvent.VK_SEMICOLON ); }
        public AcceleratorBuilder keySeparater(){ return code( java.awt.event.KeyEvent.VK_SEPARATER ); }
        public AcceleratorBuilder keySeparator(){ return code( java.awt.event.KeyEvent.VK_SEPARATOR ); }
        public AcceleratorBuilder keySlash(){ return code( java.awt.event.KeyEvent.VK_SLASH ); }
        public AcceleratorBuilder keyShift(){ return code( java.awt.event.KeyEvent.VK_SHIFT ); }
        public AcceleratorBuilder keyStop(){ return code( java.awt.event.KeyEvent.VK_STOP ); }
        public AcceleratorBuilder keySubstract(){ return code( java.awt.event.KeyEvent.VK_SUBTRACT ); }
        public AcceleratorBuilder keyUndefined(){ return code( java.awt.event.KeyEvent.VK_UNDEFINED ); }
        public AcceleratorBuilder keyUnderscope(){ return code( java.awt.event.KeyEvent.VK_UNDERSCORE ); }
        public AcceleratorBuilder keyUndo(){ return code( java.awt.event.KeyEvent.VK_UNDO ); }
//</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="keyChar">
        protected Character keyChar = null;
        
        public Character getKeyChar() {
            return keyChar;
        }
        
        public void setKeyChar(Character keyChar) {
            this.keyChar = keyChar;
        }
        
        public AcceleratorBuilder chr( char ch ){
            keyCode = null;
            keyChar = ch;
            return this;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="shift">
        protected boolean shift = false;
        
        public boolean isShift() {
            return shift;
        }
        
        public void setShift(boolean shift) {
            this.shift = shift;
        }

        public AcceleratorBuilder shift(boolean shift){
            setShift(shift);
            return this;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="ctrl">
        protected boolean ctrl = false;
        
        public boolean isCtrl() {
            return ctrl;
        }
        
        public void setCtrl(boolean ctrl) {
            this.ctrl = ctrl;
        }

        public AcceleratorBuilder ctrl(boolean ctrl){
            setCtrl(ctrl);
            return this;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="meta">
        protected boolean meta = false;
        
        public boolean isMeta() {
            return meta;
        }
        
        public void setMeta(boolean meta) {
            this.meta = meta;
        }

        public AcceleratorBuilder meta(boolean meta){
            setMeta(meta);
            return this;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="alt">
        protected boolean alt = false;
        
        public boolean isAlt() {
            return alt;
        }
        
        public void setAlt(boolean alt) {
            this.alt = alt;
        }

        public AcceleratorBuilder alt(boolean alt){
            setAlt(alt);
            return this;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="altGraph">
        protected boolean altGraph = false;
        
        public boolean isAltGraph() {
            return altGraph;
        }
        
        public void setAltGraph(boolean altGraph) {
            this.altGraph = altGraph;
        }
        
        public AcceleratorBuilder altGraph(boolean altg){
            setAltGraph(altg);
            return this;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="modifiers">
        public AcceleratorBuilder modifiers(boolean alt,boolean ctrl,boolean shift){
            setAlt(alt);
            setCtrl(ctrl);
            setShift(shift);
            return this;
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="set/reset">
        public void reset(Action act){
            if (act== null) throw new IllegalArgumentException("act==null");
            if( act instanceof BasicAction ){
                ((BasicAction) act).setAccelerator(null);
            }else{
                setAccelerator(act, null);
            }
        }
        
        public KeyStroke keyStroke(){
            if( keyChar==null && keyCode==null ){ return null; }
            if( keyChar!=null && keyCode!=null ){ return null; }
            
            int mod = 0;
            if( shift )mod = mod | java.awt.event.InputEvent.SHIFT_DOWN_MASK;
            if( ctrl )mod = mod | java.awt.event.InputEvent.CTRL_DOWN_MASK;
            if( meta )mod = mod | java.awt.event.InputEvent.META_DOWN_MASK;
            if( alt )mod = mod | java.awt.event.InputEvent.ALT_DOWN_MASK;
            if( altGraph )mod = mod | java.awt.event.InputEvent.ALT_GRAPH_DOWN_MASK;
            
            if( keyChar!=null ){
                KeyStroke ks = KeyStroke.getKeyStroke(keyChar, mod);
                return ks;
            }
            
            if( keyCode!=null ){
                KeyStroke ks = KeyStroke.getKeyStroke(keyCode, mod);
                return ks;
            }
            
            return null;
        }
        
        public void set(Action act){
            if (act== null) throw new IllegalArgumentException("act==null");
            if( keyChar==null && keyCode==null ){ reset(act); return; }
            if( keyChar!=null && keyCode!=null ){ reset(act); return; }
            
            int mod = 0;
            if( shift )mod = mod | java.awt.event.InputEvent.SHIFT_DOWN_MASK;
            if( ctrl )mod = mod | java.awt.event.InputEvent.CTRL_DOWN_MASK;
            if( meta )mod = mod | java.awt.event.InputEvent.META_DOWN_MASK;
            if( alt )mod = mod | java.awt.event.InputEvent.ALT_DOWN_MASK;
            if( altGraph )mod = mod | java.awt.event.InputEvent.ALT_GRAPH_DOWN_MASK;
            
            if( keyChar!=null ){
                KeyStroke ks = KeyStroke.getKeyStroke(keyChar, mod);
                if( act instanceof BasicAction ){
                    ((BasicAction) act).setAccelerator(ks);
                }else{
                    setAccelerator(act,ks);
                }
                return;
            }
            
            if( keyCode!=null ){
                KeyStroke ks = KeyStroke.getKeyStroke(keyCode, mod);
                if( act instanceof BasicAction ){
                    ((BasicAction) act).setAccelerator(ks);
                }else{
                    setAccelerator(act,ks);
                }
                return;
            }
            
            reset(act);
        }
        //</editor-fold>
    }

    /**
     * Создание комбинации клавиш
     */
    public class BasicAcceleratorBuilder extends AcceleratorBuilder {
        //<editor-fold defaultstate="collapsed" desc="set/reset">
        public BasicAction reset(){
            BasicAction.this.accelerator(null);
            return BasicAction.this;
        }
        
        public BasicAction set(){
            set(BasicAction.this);
            return BasicAction.this;
        }
        //</editor-fold>
    }

    /**
     * Создание комбинации клавиш
     * @return строитель комбинации
     */
    public AcceleratorBuilder accelerator(){
        return new AcceleratorBuilder();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ShortDescription">
    public static final int SHORTDESCRIPTION_PROPERTY = 0x20;
    public static final String SHORT_DESCRIPTION_PROP = "shortDescription";

    /**
     * Возвращает краткое описание
     * @return краткое описание
     */
    public String getShortDescription()
    {
        Object o = getValue(Action.SHORT_DESCRIPTION);
        if (o instanceof String) {
            return (String) o;
        }
        return null;
    }

    /**
     * Указывает краткое описание
     * @param text краткое описание
     */
    public void setShortDescription(String text)
    {
        Object old = getShortDescription();
        putValue(Action.SHORT_DESCRIPTION, text);
        firePropertyChange(SHORT_DESCRIPTION_PROP, old, text);
//        fireChanged();
    }

    /**
     * Указывает краткое описание
     * @param text краткое описание
     * @return self ссылка
     */
    public BasicAction shortDescription(String text){
        setShortDescription(text);
        return this;
    }

    /**
     * Возвращает краткое описание
     * @param act действие
     * @return краткое описание
     */
    public static String getShortDescription(Action act){
        if( act==null )throw new IllegalArgumentException("act == null");
        return (String)act.getValue(Action.SHORT_DESCRIPTION);
    }

    /**
     * Указывает краткое описание
     * @param act действие
     * @param newValue краткое описание
     */
    public static void setShortDescription(Action act, String newValue)
    {
        if( act==null )throw new IllegalArgumentException("act == null");
        act.putValue(Action.SHORT_DESCRIPTION, newValue);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="LongDescription">
    public static final int LONGDESCRIPTION_PROPERTY = 0x40;
    public static final String LONG_DESCRIPTION_PROP = "longDescription";

    /**
     * Возвращает полное описание
     * @return полное описание
     */
    public String getLongDescription()
    {
        Object o = getValue(Action.LONG_DESCRIPTION);
        if (o instanceof String) {
            return (String) o;
        }
        return null;
    }

    /**
     * Указывает полное описание
     * @param text полное описание
     */
    public void setLongDescription(String text)
    {
        Object old = getLongDescription();
        putValue(Action.LONG_DESCRIPTION, text);
        firePropertyChange(LONG_DESCRIPTION_PROP, old, text);
//        fireChanged();
    }

    /**
     * Указывает полное описание
     * @param text полное описание
     * @return self ссылка
     */
    public BasicAction longDescription(String text){
        setLongDescription(text);
        return this;
    }

    /**
     * Возвращает полное описание
     * @param act действие
     * @return полное описание
     */
    public static String getLongDescription(Action act){
        if( act==null )throw new IllegalArgumentException("act == null");
        return (String)act.getValue(Action.LONG_DESCRIPTION);
    }

    /**
     * Указывает полное описание
     * @param act действие
     * @param newValue полное описание
     */
    public static void setLongDescription(Action act, String newValue)
    {
        if( act==null )throw new IllegalArgumentException("act == null");
        act.putValue(Action.LONG_DESCRIPTION, newValue);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DisplayedMnemonicIndex">
    public static final int DIPLAYMNEMONICINDEX_PROPERTY = 0x80;
    public static final String DISPLAY_MNEMONIC_INDEX_PROP = "displayedMnemonicIndex";

    /**
     * Возвращает индекс мнемноники
     * @return индекс мнемноники
     */
    public Integer getDisplayedMnemonicIndex()
    {
        Object v = getValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY);
        if (v != null && v instanceof Integer) {
            return (Integer) v;
        }
        return null;
    }

    /**
     * Указывает индекс мнемноники
     * @param index индекс мнемноники
     */
    public void setDisplayedMnemonicIndex(Integer index)
    {
        Object old = getDisplayedMnemonicIndex();
        putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, index);
        firePropertyChange(DISPLAY_MNEMONIC_INDEX_PROP, old, index);
//        fireChanged();
    }

    /**
     * Указывает индекс мнемноники
     * @param index индекс мнемноники
     * @return self ссылка
     */
    public BasicAction displayedMnemonicIndex(Integer index){
        setDisplayedMnemonicIndex(index);
        return this;
    }

    /**
     * Возвращает индекс мнемноники
     * @param act действие
     * @return индекс
     */
    public static Integer getDisplayedMnemonicIndex(Action act){
        if( act==null )throw new IllegalArgumentException("act == null");
        return (Integer)act.getValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY);
    }

    /**
     * Указывает индекс мнемноники
     * @param act действие
     * @param newValue индекс мнемноники
     */
    public static void setDisplayedMnemonicIndex(Action act, Integer newValue)
    {
        if( act==null )throw new IllegalArgumentException("act == null");
        act.putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, newValue);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ActionCommand">
    public final static int ACTIONCOMMAND_PROPERTY = 0x100;
    public final static String ACTION_COMMAND_PROP = "actionCommand";
    public String getActionCommand()
    {
        Object v = getValue(Action.ACTION_COMMAND_KEY);
        if (v != null && v instanceof String) {
            return (String) v;
        }
        return null;
    }

    public void setActionCommand(String action)
    {
        Object old = getActionCommand();
        putValue(Action.ACTION_COMMAND_KEY, action);
        firePropertyChange(ACTION_COMMAND_PROP, old, action);
//        fireChanged();
    }
    
    public BasicAction actionCommand(String action){
        setActionCommand(action);
        return this;
    }

    public static String getActionCommand(Action act){
        if( act==null )throw new IllegalArgumentException("act == null");
        return (String)act.getValue(Action.ACTION_COMMAND_KEY);
    }
    public static void setActionCommand(Action act, String newValue)
    {
        if( act==null )throw new IllegalArgumentException("act == null");
        act.putValue(Action.ACTION_COMMAND_KEY, newValue);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Mnemonic">
    public final static int MNEMONIC_PROPERTY = 0x200;
    public final static String MNEMONIC_PROP = "mnemonic";
    public Integer getMnemonic()
    {
        Object v = getValue(Action.MNEMONIC_KEY);
        if (v != null && v instanceof Integer) {
            return (Integer) v;
        }
        return null;
    }

    public void setMnemonic(Integer mnemonic)
    {
        Object old = getMnemonic();
        putValue(Action.MNEMONIC_KEY, mnemonic);
        firePropertyChange(MNEMONIC_PROP, old, mnemonic);
//        fireChanged();
    }
    
    public BasicAction mnemonic(Integer mnemonic){
        setMnemonic(mnemonic);
        return this;
    }

    public static Integer getMnemonic(Action act){
        if( act==null )throw new IllegalArgumentException("act == null");
        return (Integer)act.getValue(Action.MNEMONIC_KEY);
    }
    public static void setMnemonic(Action act, Integer newValue)
    {
        if( act==null )throw new IllegalArgumentException("act == null");
        act.putValue(Action.MNEMONIC_KEY, newValue);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="KEYBOARDSHORTCUTS_PROPERTY">
    public final static int KEYBOARDSHORTCUTS_PROPERTY = 0x400;
    private EventSet<KeyboardShortcut> _keyboardShortcuts = null;

    public EventSet<KeyboardShortcut> getKeyboardShortcuts(){
        if( _keyboardShortcuts!=null )return _keyboardShortcuts;
        _keyboardShortcuts = new BasicEventSet<>(new HashSet());
        return _keyboardShortcuts;
    }
    
    public BasicAction addShortcut( KeyboardShortcut ks ){
        if( ks!=null ){
            getKeyboardShortcuts().add(ks);
        }
        return this;
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="target">
    private Class target = null;
    public final static int TARGET_PROPERTY = 0x800;

    @Override
    public Class getTarget() {
        return target;
    }

    public void setTarget( Class target ) {
        Object old = this.target;
        this.target = target;
        logFiner("setTarget():\n{0}",target);
        firePropertyChange("target", old, target);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="actionListener">
    public static final int ACTIONLISTENER_PROPERTY = 0x1000;
    public static final String ACTIONLISTENER_PROP = "actionListener";
    
    /**
     * Подписчик на action
     */
    protected ActionListener actionListener;
    
    /**
     * Указывает ActionListener
     * @return actionListener
     */
    public ActionListener getActionListener()
    {
        return actionListener;
    }
    
    /**
     * Указывает ActionListener
     * @param actionListener Подписчик
     */
    public void setActionListener(ActionListener actionListener)
    {
        ActionListener old = this.actionListener;
        this.actionListener = actionListener;
        firePropertyChange(ACTIONLISTENER_PROP, old, actionListener);
    }
    
    public BasicAction actionListener(ActionListener listener){
        setActionListener(listener);
        return this;
    }
    
    public BasicAction actionListener(final Runnable listener){
        if( listener!=null ){ 
            setActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                listener.run();
                }}); 
        }else{
            setActionListener(null);
        }
        return this;
    }
    //</editor-fold>

    /**
     * Перечисляет все свойства<br>
     * @see #SELECTED_PROPERTY
     * @see #NAME_PROPERTY
     * @see #SMALLICON_PROPERTY
     * @see #LARGEICON_PROPERTY
     * @see #ACCELERATOR_PROPERTY
     * @see #SHORTDESCRIPTION_PROPERTY
     * @see #LONGDESCRIPTION_PROPERTY
     * @see #DIPLAYMNEMONICINDEX_PROPERTY
     * @see #ACTIONCOMMAND_PROPERTY
     * @see #MNEMONIC_PROPERTY
     * @see #KEYBOARDSHORTCUTS_PROPERTY
     * @see #TARGET_PROPERTY
     * @see #ACTIONLISTENER_PROPERTY
     */
    public static final int ALL =
            SELECTED_PROPERTY |
            NAME_PROPERTY |
            SMALLICON_PROPERTY |
            LARGEICON_PROPERTY |
            ACCELERATOR_PROPERTY |
            SHORTDESCRIPTION_PROPERTY |
            LONGDESCRIPTION_PROPERTY |
            DIPLAYMNEMONICINDEX_PROPERTY |
            ACTIONCOMMAND_PROPERTY |
            MNEMONIC_PROPERTY |
            KEYBOARDSHORTCUTS_PROPERTY |
            TARGET_PROPERTY | 
            ACTIONLISTENER_PROPERTY;

    /**
     * Перечисляет все свойства по умолчанию<br>
     * @see #NAME_PROPERTY
     * @see #SMALLICON_PROPERTY
     * @see #LARGEICON_PROPERTY
     * @see #ACCELERATOR_PROPERTY
     * @see #SHORTDESCRIPTION_PROPERTY
     * @see #LONGDESCRIPTION_PROPERTY
     * @see #DIPLAYMNEMONICINDEX_PROPERTY
     * @see #MNEMONIC_PROPERTY
     * @see #KEYBOARDSHORTCUTS_PROPERTY
     */
    public static final int DEF =
            NAME_PROPERTY |
            SMALLICON_PROPERTY |
            LARGEICON_PROPERTY |
            ACCELERATOR_PROPERTY |
            SHORTDESCRIPTION_PROPERTY |
            LONGDESCRIPTION_PROPERTY |
            DIPLAYMNEMONICINDEX_PROPERTY |
            MNEMONIC_PROPERTY |
            KEYBOARDSHORTCUTS_PROPERTY |
            TARGET_PROPERTY;

    //<editor-fold defaultstate="collapsed" desc="copyTo()">
    /**
     * Копируемые значения
     * @param action Действие которому присваиваются свойства.
     * @param copyProperties Комбинация OR копируемых свойств, например NAME_PROPERTY | SMALLICON_PROPERTY.
     */
    public void copyTo(Action action,int copyProperties){
        if( (copyProperties&NAME_PROPERTY)==NAME_PROPERTY ){
            action.putValue(Action.NAME, getName());
        }
        
        if( (copyProperties&SMALLICON_PROPERTY)==SMALLICON_PROPERTY ){
            action.putValue(SMALL_ICON, getSmallIcon());
        }
        
        if( (copyProperties&LARGEICON_PROPERTY)==LARGEICON_PROPERTY ){
//            Object largeIco = action.getValue(Action.LARGE_ICON_KEY);
//            if( largeIco!=null && largeIco instanceof javax.swing.Icon ){
            action.putValue(LARGE_ICON_KEY, getLargeIcon());
//                setLargeIcon((javax.swing.Icon)largeIco);
//            }
        }
        
        if( (copyProperties&ACCELERATOR_PROPERTY)==ACCELERATOR_PROPERTY ){
//            Object acc = action.getValue(Action.ACCELERATOR_KEY);
//            if( acc!=null && acc instanceof KeyStroke ){
            action.putValue(ACCELERATOR_KEY, getAccelerator());
//                setAccelerator((KeyStroke)acc);
//            }
        }
        
        if( (copyProperties&SHORTDESCRIPTION_PROPERTY)==SHORTDESCRIPTION_PROPERTY ){
//            Object shortDesc = action.getValue(Action.SHORT_DESCRIPTION);
//            if( shortDesc!=null && shortDesc instanceof String )setShortDescription((String)shortDesc);
            action.putValue(SHORT_DESCRIPTION, getShortDescription());
        }
        
        if( (copyProperties&LONGDESCRIPTION_PROPERTY)==LONGDESCRIPTION_PROPERTY ){
//            Object longDesc = action.getValue(Action.LONG_DESCRIPTION);
//            if( longDesc!=null && longDesc instanceof String )setLongDescription((String)longDesc);
            action.putValue(LONG_DESCRIPTION, getLongDescription());
        }
        
        if( (copyProperties&DIPLAYMNEMONICINDEX_PROPERTY)==DIPLAYMNEMONICINDEX_PROPERTY ){
//            Object dmi = action.getValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY);
//            if( dmi!=null && dmi instanceof Integer )setDisplayedMnemonicIndex((Integer)dmi);
            action.putValue(DISPLAYED_MNEMONIC_INDEX_KEY, getDisplayedMnemonicIndex());
        }
        
        if( (copyProperties&ACTIONCOMMAND_PROPERTY)==ACTIONCOMMAND_PROPERTY ){
//            Object acC = action.getValue(Action.ACTION_COMMAND_KEY);
//            if( acC!=null && acC instanceof String )setActionCommand((String)acC);
            action.putValue(ACTION_COMMAND_KEY, getActionCommand());
        }
        
        if( (copyProperties&MNEMONIC_PROPERTY)==MNEMONIC_PROPERTY ){
//            Object mnK = action.getValue(Action.MNEMONIC_KEY);
//            if( mnK!=null && mnK instanceof Integer )setMnemonic((Integer)mnK);
            action.putValue(MNEMONIC_KEY, getMnemonic());
        }
        
        if( (copyProperties&SELECTED_PROPERTY)==SELECTED_PROPERTY ){
//            Object select = action.getValue(Action.SELECTED_KEY);
//            if( select!=null && select instanceof Boolean )setSelected((Boolean)select);
            action.putValue(SELECTED_KEY, isSelected());
        }
        
        if( (copyProperties&KEYBOARDSHORTCUTS_PROPERTY)==KEYBOARDSHORTCUTS_PROPERTY ){
            Eterable<KeyboardShortcut> itr = getKeyboardShortcuts();
            if( action instanceof BasicAction && itr!=null ){
                ((BasicAction)action).getKeyboardShortcuts().clear();
                for( KeyboardShortcut ksSrc : itr ){
                    if( ksSrc==null )continue;
                    KeyboardShortcut ks = new KeyboardShortcut(ksSrc);
                    ((BasicAction)action).getKeyboardShortcuts().add(ks);
                }
            }
        }
        
        if( (copyProperties&TARGET_PROPERTY)==TARGET_PROPERTY ){
            if( action instanceof BasicAction ){
                ((BasicAction)action).setTarget(getTarget());
            }
        }
    }
//</editor-fold>

    /*
     * (non-Javadoc) @see javax.swing.Action
     */
    @Override
    public void putValue(String key, Object newValue) {
        super.putValue(key, newValue);
//        fireChanged();
    }

    //<editor-fold defaultstate="collapsed" desc="actionPerformed(ActionEvent)">
    /*
     * (non-Javadoc) @see javax.swing.AbstractAction
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
//        String n = getName();
//        String c = getActionCommand();
//        String txt = "Action"+(n==null ? (c==null ? "" : " "+c) : " "+n)+" performed";
//        System.out.println(txt);
        ActionListener al = actionListener;
        if( al!=null ){
            al.actionPerformed(e);
        }
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Filter">
    /**
     * Функции фильтрации
     */
    public static class Filter {
        /**
         * Фильтр когда действие направлено на класс или подкласс объектов
         * @param classes классы объектов
         * @return фильтр
         */
        public static Predicate<Action> targetAssignableFrom(
            final Iterable<Class> classes
        ){
            return new Predicate<Action>() {
                @Override
                public boolean test(Action value) {
                    if( !(value instanceof GetTarget) )return false;
                    if( classes==null )return false;
                    
                    Class target = ((GetTarget)value).getTarget();
                    for( Class c : classes ){
                        if( c==null )continue;
                        if( target.isAssignableFrom(c) )
                            return true;
                    }
                    
                    return false;
                }
            };
        }
        
        /**
         * Фильтр когда действие направлено на класс или подкласс объектов
         * @param classes классы объектов
         * @return фильтр
         */
        public static Predicate<Action> targetAssignableFrom(
            final Class ... classes
        ){
            return new Predicate<Action>() {
                @Override
                public boolean test(Action value) {
                    if( !(value instanceof GetTarget) )return false;
                    if( classes==null )return false;
                    
                    Class target = ((GetTarget)value).getTarget();
                    if( target==null )return false;
                    
                    for( Class c : classes ){
                        if( c==null )continue;
                        if( target.isAssignableFrom(c) )
                            return true;
                    }
                    
                    return false;
                }
            };
        }
    }
//</editor-fold>
}
