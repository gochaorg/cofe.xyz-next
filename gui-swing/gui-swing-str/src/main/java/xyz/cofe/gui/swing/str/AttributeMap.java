package xyz.cofe.gui.swing.str;

import java.text.Annotation;
import java.text.AttributedCharacterIterator;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Копия из оригинального javax.swing
 * @author Kamnev Georgiy
 */
public class AttributeMap extends AbstractMap<AttributedCharacterIterator.Attribute,Object> {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(AttributeMap.class.getName());

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
        logger.entering(AttributeMap.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(AttributeMap.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(AttributeMap.class.getName(), method, result);
    }
    //</editor-fold>

    protected int runIndex;
    protected int beginIndex;
    protected int endIndex;
    protected BaseAString astring;

    public AttributeMap(BaseAString astring, int runIndex, int beginIndex, int endIndex) {
        if( astring==null )throw new IllegalArgumentException("astring == null");
        this.astring = astring;
        this.runIndex = runIndex;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    @Override
    public Set entrySet() {
        HashSet set = new HashSet();
        synchronized (astring) {
            int size = astring.getRunAttributes()[runIndex].size();
            for (int i = 0; i < size; i++) {
                AttributedCharacterIterator.Attribute key = (AttributedCharacterIterator.Attribute) astring.getRunAttributes()[runIndex].get(i);
                Object value = astring.getRunAttributeValues()[runIndex].get(i);
                if (value instanceof Annotation) {
                    value = astring.getAttributeCheckRange(key,
                        runIndex, beginIndex, endIndex);
                    if (value == null) {
                        continue;
                    }
                }
                Entry entry = new AttributeEntry(key, value);
                set.add(entry);
            }
        }
        return set;
    }

    @Override
    public Object get(Object key) {
        return astring.getAttributeCheckRange((AttributedCharacterIterator.Attribute) key, runIndex, beginIndex, endIndex);
    }
}
