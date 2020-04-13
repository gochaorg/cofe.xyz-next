package xyz.cofe.gui.swing.str;

import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Копия из оригинального javax.swing
 * @author Kamnev Georgiy
 */
public class AttributeEntry implements Map.Entry {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(AttributeEntry.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }

    private static boolean isLogSevere(){
        Level logLevel = logger.getLevel();
        return logLevel==null ? true : logLevel.intValue() <= Level.SEVERE.intValue();
    }

    private static boolean isLogWarning(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.WARNING.intValue();
    }

    private static boolean isLogInfo(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.INFO.intValue();
    }

    private static boolean isLogFine(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINE.intValue();
    }

    private static boolean isLogFiner(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINER.intValue();
    }

    private static boolean isLogFinest(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINEST.intValue();
    }

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

    private static void logEntering(String method,Object ... params){
        logger.entering(AttributeEntry.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(AttributeEntry.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(AttributeEntry.class.getName(), method, result);
    }
    //</editor-fold>

    protected AttributedCharacterIterator.Attribute key;
    protected Object value;

    AttributeEntry(AttributedCharacterIterator.Attribute key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AttributeEntry)) {
            return false;
        }
        AttributeEntry other = (AttributeEntry) o;
        return other.key.equals(key) &&
            (value == null ? other.value == null : other.value.equals(value));
    }

    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Object setValue(Object newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return key.hashCode() ^ (value==null ? 0 : value.hashCode());
    }

    @Override
    public String toString() {
        return key.toString()+"="+value.toString();
    }
}
