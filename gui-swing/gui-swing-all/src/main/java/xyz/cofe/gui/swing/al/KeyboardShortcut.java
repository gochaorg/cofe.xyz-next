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
package xyz.cofe.gui.swing.al;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.KeyStroke;

/**
 * Клавиатурные сокращения
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class KeyboardShortcut {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(KeyboardShortcut.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(KeyboardShortcut.class.getName()).log(Level.FINER, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(KeyboardShortcut.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(KeyboardShortcut.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(KeyboardShortcut.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(KeyboardShortcut.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /**
     * Конструктор
     */
    public KeyboardShortcut(){
    }

    public KeyboardShortcut(KeyboardShortcut ks){
        if( ks==null )return;
//        this.isWindowClass = ks.isWindowClass;
        this.keyStroke = ks.keyStroke;
        this.target = ks.target;
    }

    // <editor-fold defaultstate="collapsed" desc="propertyChangeSupport">
    /**
     * Поддержка PropertyChangeEvent
     */
    private transient java.beans.PropertyChangeSupport propertyChangeSupport = null;
    /**
     * Поддержка PropertyChangeEvent
     * @return Поддержка PropertyChangeEvent
     */
    protected java.beans.PropertyChangeSupport propertySupport(){
        if( propertyChangeSupport!=null )return propertyChangeSupport;
        propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
        return propertyChangeSupport;
    }

    /**
     * Уведомляет подписчиков о измении свойства
     * @param property Свойство
     * @param oldValue Старое значение
     * @param newValue Новое значение
     */
    protected void firePropertyChange(String property,Object oldValue, Object newValue){
        propertySupport().firePropertyChange(property, oldValue, newValue);
    }

    /**
     * Добавляет подписчика
     * @param listener Подписчик
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertyChangeSupport.addPropertyChangeListener( listener );
    }

    /**
     * Удаляет подписчика
     * @param listener Подписчик
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener )
    {
        propertyChangeSupport.removePropertyChangeListener( listener );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="keyStroke">
    /**
     * Клавиатурная комбинация клавиш
     */
    protected KeyStroke keyStroke = null;

    /**
     * Указывает клав. комбинацию клавиш
     * @return клав. комбинацию клавиш
     */
    public KeyStroke getKeyStroke() {
        return keyStroke;
    }

    /**
     * Указывает клав. комбинацию клавиш
     * @param keyStroke клав. комбинацию клавиш
     */
    public void setKeyStroke(KeyStroke keyStroke) {
        Object old = this.keyStroke;
        this.keyStroke = keyStroke;
        firePropertyChange("keyStroke", old, keyStroke);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="target">
    /**
     * Класс окна для которого эта комбинация предназначена. <br>
     */
    protected Class target = null;
    /**
     * Указывает класс окна для которого эта комбинация предназначена. <br>
     * @return Класс
     */
    public Class getTarget() {
        return target;
    }

    /**
     * Указывает класс окна для которого эта комбинация предназначена. <br>
     * @param windowClass Класс
     */
    public void setTarget(Class windowClass) {
        Object old = this.target;
        this.target = target;
        firePropertyChange("target", old, windowClass);
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="equals()">
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final KeyboardShortcut other = (KeyboardShortcut) obj;
        if (this.keyStroke != other.keyStroke && (this.keyStroke == null || !this.keyStroke.equals(other.keyStroke)))
            return false;
        if (this.target != other.target && (this.target == null || !this.target.equals(other.target)))
            return false;
        return true;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hashCode()">
    @Override
    public int hashCode() {
        int hash = 7;
        // hash = 23 * hash + (this.isWindowClass != null ? this.isWindowClass.hashCode() : 0);
        hash = 23 * hash + (this.keyStroke != null ? this.keyStroke.hashCode() : 0);
        hash = 23 * hash + (this.target != null ? this.target.hashCode() : 0);
        return hash;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="toString()">
    @Override
    public String toString() {
        return
            "KeyboardShortcut{" +
            "keyStroke=" + keyStroke +
            ", target=" + target +
            '}';
    }
    //</editor-fold>
}
