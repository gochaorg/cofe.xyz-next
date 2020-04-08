package xyz.cofe.gui.swing.str;

import java.text.AttributedCharacterIterator;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kamnev Georgiy
 */
public class AttributedStringIterator implements AttributedCharacterIterator {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(AttributedStringIterator.class.getName());

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
        logger.entering(AttributedStringIterator.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(AttributedStringIterator.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(AttributedStringIterator.class.getName(), method, result);
    }
    //</editor-fold>

    protected BaseAString astring;


    // note on synchronization:
    // we don't synchronize on the iterator, assuming that an iterator is only used in one thread.
    // we do synchronize access to the AttributedString however, since it's more likely to be shared between threads.

    // start and end index for our iteration
    private int beginIndex;
    private int endIndex;

    // attributes that our client is interested in
    private Attribute[] relevantAttributes;

    // the current index for our iteration
    // invariant: beginIndex <= currentIndex <= endIndex
    private int currentIndex;

    // information about the run that includes currentIndex
    private int currentRunIndex;
    private int currentRunStart;
    private int currentRunLimit;

    // constructor
    AttributedStringIterator(BaseAString astring, Attribute[] attributes, int beginIndex, int endIndex) {
        if( astring==null )throw new IllegalArgumentException("astring == null");
        this.astring = astring;

        if (beginIndex < 0 || beginIndex > endIndex || endIndex > astring.length()) {
            throw new IllegalArgumentException("Invalid substring range");
        }

        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.currentIndex = beginIndex;
        updateRunInfo();
        if (attributes != null) {
            relevantAttributes = (Attribute[]) attributes.clone();
        }
    }

    // Object methods. See documentation in that class.

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AttributedStringIterator)) {
            return false;
        }

        AttributedStringIterator that = (AttributedStringIterator) obj;

        if (astring != that.getString())
            return false;

        if (currentIndex != that.currentIndex || beginIndex != that.beginIndex || endIndex != that.endIndex)
            return false;
        return true;
    }

    public int hashCode() {
        return astring.text().hashCode() ^ currentIndex ^ beginIndex ^ endIndex;
    }

    public Object clone() {
        try {
            AttributedStringIterator other = (AttributedStringIterator) super.clone();
            return other;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    // CharacterIterator methods. See documentation in that interface.

    public char first() {
        return internalSetIndex(beginIndex);
    }

    public char last() {
        if (endIndex == beginIndex) {
            return internalSetIndex(endIndex);
        } else {
            return internalSetIndex(endIndex - 1);
        }
    }

    public char current() {
        if (currentIndex == endIndex) {
            return DONE;
        } else {
            return astring.charAt(currentIndex);
        }
    }

    public char next() {
        if (currentIndex < endIndex) {
            return internalSetIndex(currentIndex + 1);
        }
        else {
            return DONE;
        }
    }

    public char previous() {
        if (currentIndex > beginIndex) {
            return internalSetIndex(currentIndex - 1);
        }
        else {
            return DONE;
        }
    }

    public char setIndex(int position) {
        if (position < beginIndex || position > endIndex)
            throw new IllegalArgumentException("Invalid index");
        return internalSetIndex(position);
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public int getIndex() {
        return currentIndex;
    }

    // AttributedCharacterIterator methods. See documentation in that interface.

    public int getRunStart() {
        return currentRunStart;
    }

    public int getRunStart( Attribute attribute) {
        if (currentRunStart == beginIndex || currentRunIndex == -1) {
            return currentRunStart;
        } else {
            Object value = getAttribute(attribute);
            int runStart = currentRunStart;
            int runIndex = currentRunIndex;
            while (runStart > beginIndex &&
                BaseAString.valuesMatch(value, astring.getAttribute(attribute, runIndex - 1))) {
                runIndex--;
                runStart = astring.getRunStarts()[runIndex];
            }
            if (runStart < beginIndex) {
                runStart = beginIndex;
            }
            return runStart;
        }
    }

    public int getRunStart(Set<? extends Attribute> attributes) {
        if (currentRunStart == beginIndex || currentRunIndex == -1) {
            return currentRunStart;
        } else {
            int runStart = currentRunStart;
            int runIndex = currentRunIndex;
            while (runStart > beginIndex &&
                astring.attributeValuesMatch(attributes, currentRunIndex, runIndex - 1)) {
                runIndex--;
                runStart = astring.getRunStarts()[runIndex];
            }
            if (runStart < beginIndex) {
                runStart = beginIndex;
            }
            return runStart;
        }
    }

    public int getRunLimit() {
        return currentRunLimit;
    }

    public int getRunLimit( Attribute attribute) {
        if (currentRunLimit == endIndex || currentRunIndex == -1) {
            return currentRunLimit;
        } else {
            Object value = getAttribute(attribute);
            int runLimit = currentRunLimit;
            int runIndex = currentRunIndex;
            while (runLimit < endIndex &&
                BaseAString.valuesMatch(value, astring.getAttribute(attribute, runIndex + 1))) {
                runIndex++;
                runLimit = runIndex < astring.getRunCount() - 1 ? astring.getRunStarts()[runIndex + 1] : endIndex;
            }
            if (runLimit > endIndex) {
                runLimit = endIndex;
            }
            return runLimit;
        }
    }

    public int getRunLimit(Set<? extends Attribute> attributes) {
        if (currentRunLimit == endIndex || currentRunIndex == -1) {
            return currentRunLimit;
        } else {
            int runLimit = currentRunLimit;
            int runIndex = currentRunIndex;
            while (runLimit < endIndex &&
                astring.attributeValuesMatch(attributes, currentRunIndex, runIndex + 1)) {
                runIndex++;
                runLimit = runIndex < astring.getRunCount() - 1 ? astring.getRunStarts()[runIndex + 1] : endIndex;
            }
            if (runLimit > endIndex) {
                runLimit = endIndex;
            }
            return runLimit;
        }
    }

    public Map<Attribute,Object> getAttributes() {
        if (astring.getRunAttributes() == null || currentRunIndex == -1 || astring.getRunAttributes()[currentRunIndex] == null) {
            // ??? would be nice to return null, but current spec doesn't allow it
            // returning Hashtable saves AttributeMap from dealing with emptiness
            return new Hashtable();
        }
        return new AttributeMap(astring, currentRunIndex, beginIndex, endIndex);
    }

    public Set<Attribute> getAllAttributeKeys() {
        // ??? This should screen out attribute keys that aren't relevant to the client
        if (astring.getRunAttributes() == null) {
            // ??? would be nice to return null, but current spec doesn't allow it
            // returning HashSet saves us from dealing with emptiness
            return new HashSet();
        }
        synchronized (astring) {
            // ??? should try to create this only once, then update if necessary,
            // and give callers read-only view
            Set keys = new HashSet();
            int i = 0;
            while (i < astring.getRunCount()) {
                if (astring.getRunStarts()[i] < endIndex && (i == astring.getRunCount() - 1 || astring.getRunStarts()[i + 1] > beginIndex)) {
                    Vector currentRunAttributes = astring.getRunAttributes()[i];
                    if (currentRunAttributes != null) {
                        int j = currentRunAttributes.size();
                        while (j-- > 0) {
                            keys.add(currentRunAttributes.get(j));
                        }
                    }
                }
                i++;
            }
            return keys;
        }
    }

    @Override
    public Object getAttribute( Attribute attribute) {
        int runIndex = currentRunIndex;
        if (runIndex < 0) {
            return null;
        }
        return astring.getAttributeCheckRange(attribute, runIndex, beginIndex, endIndex);
    }

    // internally used methods

    private BaseAString getString() {
        return astring;
    }

    // set the current index, update information about the current run if necessary,
    // return the character at the current index
    private char internalSetIndex(int position) {
        currentIndex = position;
        if (position < currentRunStart || position >= currentRunLimit) {
            updateRunInfo();
        }
        if (currentIndex == endIndex) {
            return DONE;
        } else {
            return astring.charAt(position);
        }
    }

    // update the information about the current run
    private void updateRunInfo() {
        if (currentIndex == endIndex) {
            currentRunStart = currentRunLimit = endIndex;
            currentRunIndex = -1;
        } else {
            synchronized (astring) {
                int runIndex = -1;
                while (runIndex < astring.getRunCount() - 1 && astring.getRunStarts()[runIndex + 1] <= currentIndex)
                    runIndex++;
                currentRunIndex = runIndex;
                if (runIndex >= 0) {
                    currentRunStart = astring.getRunStarts()[runIndex];
                    if (currentRunStart < beginIndex)
                        currentRunStart = beginIndex;
                }
                else {
                    currentRunStart = beginIndex;
                }
                if (runIndex < astring.getRunCount() - 1) {
                    currentRunLimit = astring.getRunStarts()[runIndex + 1];
                    if (currentRunLimit > endIndex)
                        currentRunLimit = endIndex;
                }
                else {
                    currentRunLimit = endIndex;
                }
            }
        }
    }
}
