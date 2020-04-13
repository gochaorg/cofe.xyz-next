package xyz.cofe.gui.swing.str;

import java.text.Annotation;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Модификация существующего класса java.text.AttributedString
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
@SuppressWarnings("UseOfObsoleteCollectionType")
public class BaseAString {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(BaseAString.class.getName());
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

    private static void logEntering(String method,Object ... params){
        logger.entering(BaseAString.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(BaseAString.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(BaseAString.class.getName(), method, result);
    }
    //</editor-fold>

    private long scn = 0;
    protected synchronized void nextScn(){ scn++; }
    public synchronized long getScn(){ return scn; }

    private static final int ARRAY_SIZE_INCREMENT = 10;

    // field holding the text
    private String text;

    // fields holding run attribute information
    // run attributes are organized by run
    private int runArraySize;               // current size of the arrays
    private int runCount;                   // actual number of runs, <= runArraySize
    public synchronized int getRunCount(){ return runCount; }

    private int runStarts[];                // start index for each run
    public synchronized int[] getRunStarts(){ return runStarts; }

    private Vector runAttributes[];         // vector of attribute keys for each run
    public synchronized Vector[] getRunAttributes(){ return runAttributes; }

    private Vector runAttributeValues[];    // parallel vector of attribute values for each run
    public synchronized Vector[] getRunAttributeValues(){ return runAttributeValues; }

    //<editor-fold defaultstate="collapsed" desc="construct">
    public BaseAString(AttributedCharacterIterator[] iterators) {
        if (iterators == null) {
            throw new NullPointerException("Iterators must not be null");
        }

        if (iterators.length == 0) {
            text = "";
        }
        else {
            // Build the String contents
            StringBuffer buffer = new StringBuffer();
            for (AttributedCharacterIterator iterator : iterators) {
                appendContents(buffer, iterator);
            }

            text = buffer.toString();

            if (text.length() > 0) {
                // Determine the runs, creating a new run when the attributes
                // differ.
                int offset = 0;
                Map last = null;

                for (AttributedCharacterIterator iterator : iterators) {
                    int start = iterator.getBeginIndex();
                    int end = iterator.getEndIndex();
                    int index = start;

                    while (index < end) {
                        iterator.setIndex(index);

                        Map attrs = iterator.getAttributes();

                        if (mapsDiffer(last, attrs)) {
                            setAttributes(attrs, index - start + offset);
                        }
                        last = attrs;
                        index = iterator.getRunLimit();
                    }
                    offset += (end - start);
                }
            }
        }
    }

    public BaseAString(String text) {
        if (text == null) {
            throw new NullPointerException();
        }
        this.text = text;
    }

    @SuppressWarnings("UseOfObsoleteCollectionType")
    public BaseAString(String text,
                       Map<? extends AttributedCharacterIterator.Attribute, ?> attributes)
    {
        if (text == null || attributes == null) {
            throw new NullPointerException();
        }
        this.text = text;

        if (text.length() == 0) {
            if (attributes.isEmpty())
                return;
            throw new IllegalArgumentException("Can't add attribute to 0-length text");
        }

        int attributeCount = attributes.size();
        if (attributeCount > 0) {
            createRunAttributeDataVectors();
            Vector newRunAttributes = new Vector(attributeCount);
            Vector newRunAttributeValues = new Vector(attributeCount);
            runAttributes[0] = newRunAttributes;
            runAttributeValues[0] = newRunAttributeValues;
            for (Map.Entry entry : attributes.entrySet()) {
                newRunAttributes.addElement(entry.getKey());
                newRunAttributeValues.addElement(entry.getValue());
            }
        }
    }

    public BaseAString(AttributedCharacterIterator text) {
        // If performance is critical, this constructor should be
        // implemented here rather than invoking the constructor for a
        // subrange. We can avoid some range checking in the loops.
        this(text, text.getBeginIndex(), text.getEndIndex(), null);

        /*if( text==null ){
            length = 0;
        }else{
            length = length(text);
        }*/
    }

    public BaseAString(AttributedCharacterIterator text, int beginIndex, int endIndex) {
        //super(text, beginIndex, endIndex);
        this(text, beginIndex, endIndex, null);
        /*if( beginIndex <= endIndex ){
            length = endIndex - beginIndex;
        }else{
            length = 0;
        }*/
    }

    public BaseAString(AttributedCharacterIterator text, int beginIndex, int endIndex,
                       AttributedCharacterIterator.Attribute[] attributes) {
        if (text == null) {
            throw new NullPointerException();
        }

        // Validate the given subrange
        int textBeginIndex = text.getBeginIndex();
        int textEndIndex = text.getEndIndex();
        if (beginIndex < textBeginIndex || endIndex > textEndIndex || beginIndex > endIndex)
            throw new IllegalArgumentException("Invalid substring range");

        // Copy the given string
        StringBuilder textBuffer = new StringBuilder();
        text.setIndex(beginIndex);
        for (char c = text.current(); text.getIndex() < endIndex; c = text.next())
            textBuffer.append(c);
        this.text = textBuffer.toString();

        if (beginIndex == endIndex)
            return;

        // Select attribute keys to be taken care of
        HashSet keys = new HashSet();
        if (attributes == null) {
            keys.addAll(text.getAllAttributeKeys());
        } else {
            keys.addAll(Arrays.asList(attributes));
            keys.retainAll(text.getAllAttributeKeys());
        }
        if (keys.isEmpty())
            return;

        // Get and set attribute runs for each attribute name. Need to
        // scan from the top of the text so that we can discard any
        // Annotation that is no longer applied to a subset text segment.
        Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            AttributedCharacterIterator.Attribute attributeKey = (AttributedCharacterIterator.Attribute)itr.next();
            text.setIndex(textBeginIndex);
            while (text.getIndex() < endIndex) {
                int start = text.getRunStart(attributeKey);
                int limit = text.getRunLimit(attributeKey);
                Object value = text.getAttribute(attributeKey);

                if (value != null) {
                    if (value instanceof Annotation) {
                        if (start >= beginIndex && limit <= endIndex) {
                            addAttribute(attributeKey, value, start - beginIndex, limit - beginIndex);
                        } else {
                            if (limit > endIndex)
                                break;
                        }
                    } else {
                        // if the run is beyond the given (subset) range, we
                        // don't need to process further.
                        if (start >= endIndex)
                            break;
                        if (limit > beginIndex) {
                            // attribute is applied to any subrange
                            if (start < beginIndex)
                                start = beginIndex;
                            if (limit > endIndex)
                                limit = endIndex;
                            if (start != limit) {
                                addAttribute(attributeKey, value, start - beginIndex, limit - beginIndex);
                            }
                        }
                    }
                }
                text.setIndex(limit);
            }
        }

        /*
        if( beginIndex <= endIndex ){
            length = endIndex - beginIndex;
        }else{
            length = 0;
        }
        */
    }

    /**
     * Конструктор копирования
     * @param astr образец
     */
    public BaseAString(AttributedString astr){
        this( astr.getIterator() );
    }

    /**
     * Конструктор копирования
     * @param astr образец
     */
    public BaseAString(BaseAString astr){
        this( astr.getIterator() );
    }

    @Override
    public BaseAString clone(){
        return new BaseAString(this);
    }
    //</editor-fold>

    /**
     * Appends the contents of the CharacterIterator iterator into the
     * StringBuffer buf.
     * @param buf string buffer
     * @param iterator char iter
     */
    protected void appendContents(StringBuffer buf,
                                  CharacterIterator iterator) {
        int index = iterator.getBeginIndex();
        int end = iterator.getEndIndex();

        while (index < end) {
            iterator.setIndex(index++);
            buf.append(iterator.current());
        }
    }

    /**
     * Returns true if the attributes specified in last and attrs differ.
     * @param last doc it
     * @param attrs doc it
     * @return bla bla
     */
    protected static boolean mapsDiffer(Map last, Map attrs) {
        if (last == null) {
            return (attrs != null && attrs.size() > 0);
        }
        return (!last.equals(attrs));
    }

    /**
     * Sets the attributes for the range from offset to the next run break
     * (typically the end of the text) to the ones specified in attrs.
     * This is only meant to be called from the constructor!
     * @param attrs doc it
     * @param offset doc it
     */
    protected synchronized void setAttributes(Map attrs, int offset) {
        if (runCount == 0) {
            createRunAttributeDataVectors();
        }

        int index = ensureRunBreak(offset, false);
        //int size;
        int size = attrs!=null ? attrs.size() : 0;

        if (attrs != null /* && (size = attrs.size()) > 0 */ ) {
            Vector runAttrs = new Vector(size);
            Vector runValues = new Vector(size);
            Iterator iterator = attrs.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();

                runAttrs.add(entry.getKey());
                runValues.add(entry.getValue());
            }
            runAttributes[index] = runAttrs;
            runAttributeValues[index] = runValues;
            nextScn();
        }
    }

    protected synchronized void createRunAttributeDataVectors() {
        // use temporary variables so things remain consistent in case of an exception
        int newRunStarts[] = new int[ARRAY_SIZE_INCREMENT];
        Vector newRunAttributes[] = new Vector[ARRAY_SIZE_INCREMENT];
        Vector newRunAttributeValues[] = new Vector[ARRAY_SIZE_INCREMENT];
        runStarts = newRunStarts;
        runAttributes = newRunAttributes;
        runAttributeValues = newRunAttributeValues;
        runArraySize = ARRAY_SIZE_INCREMENT;
        runCount = 1; // assume initial run starting at index 0

        nextScn();
    }

    /**
     * Adds an attribute to the entire string.
     * @param attribute the attribute key
     * @param value the value of the attribute; may be null
     * @exception NullPointerException if <code>attribute</code> is null.
     * @exception IllegalArgumentException if the AttributedString has length 0
     * (attributes cannot be applied to a 0-length range).
     */
    protected synchronized void addAttribute(AttributedCharacterIterator.Attribute attribute, Object value) {

        if (attribute == null) {
            throw new NullPointerException();
        }

        int len = length();
        if (len == 0) {
            throw new IllegalArgumentException("Can't add attribute to 0-length text");
        }

        addAttributeImpl(attribute, value, 0, len);
    }

    /**
     * Adds an attribute to a subrange of the string.
     * @param attribute the attribute key
     * @param value The value of the attribute. May be null.
     * @param beginIndex Index of the first character of the range.
     * @param endIndex Index of the character following the last character of the range.
     * @exception NullPointerException if <code>attribute</code> is null.
     * @exception IllegalArgumentException if beginIndex is less then 0, endIndex is
     * greater than the length of the string, or beginIndex and endIndex together don't
     * define a non-empty subrange of the string.
     */
    protected synchronized void addAttribute(AttributedCharacterIterator.Attribute attribute, Object value,
                                             int beginIndex, int endIndex) {

        if (attribute == null) {
            throw new NullPointerException();
        }

        if (beginIndex < 0 || endIndex > length() || beginIndex >= endIndex) {
            throw new IllegalArgumentException("Invalid substring range");
        }

        addAttributeImpl(attribute, value, beginIndex, endIndex);
    }

    /**
     * Adds a set of attributes to a subrange of the string.
     * @param attributes The attributes to be added to the string.
     * @param beginIndex Index of the first character of the range.
     * @param endIndex Index of the character following the last
     * character of the range.
     * @exception NullPointerException if <code>attributes</code> is null.
     * @exception IllegalArgumentException if beginIndex is less then
     * 0, endIndex is greater than the length of the string, or
     * beginIndex and endIndex together don't define a non-empty
     * subrange of the string and the attributes parameter is not an
     * empty Map.
     */
    protected synchronized void addAttributes(Map<? extends AttributedCharacterIterator.Attribute, ?> attributes,
                                              int beginIndex, int endIndex)
    {
        if (attributes == null) {
            throw new NullPointerException();
        }

        if (beginIndex < 0 || endIndex > length() || beginIndex > endIndex) {
            throw new IllegalArgumentException("Invalid substring range");
        }
        if (beginIndex == endIndex) {
            if (attributes.isEmpty())
                return;
            throw new IllegalArgumentException("Can't add attribute to 0-length text");
        }

        // make sure we have run attribute data vectors
        if (runCount == 0) {
            createRunAttributeDataVectors();
        }

        // break up runs if necessary
        int beginRunIndex = ensureRunBreak(beginIndex);
        int endRunIndex = ensureRunBreak(endIndex);

        for (Map.Entry entry : attributes.entrySet()) {
            addAttributeRunData((AttributedCharacterIterator.Attribute) entry.getKey(), entry.getValue(), beginRunIndex, endRunIndex);
        }
    }

    protected synchronized void removeAttributes(
        Set<? extends AttributedCharacterIterator.Attribute> attributes,
        int beginIndex, int endIndex )
    {
        if( attributes==null )throw new IllegalArgumentException("attributes == null");
        if( attributes.isEmpty() )return;

        if( beginIndex>endIndex )
            throw new IllegalArgumentException("beginIndex("+beginIndex+")>endIndex("+endIndex+")");

        if( beginIndex<0 )
            throw new IllegalArgumentException("beginIndex("+beginIndex+")<0");
        if( endIndex<0 )
            throw new IllegalArgumentException("endIndex("+beginIndex+")<0");

        if( beginIndex>=length() ){
            return;
        }

        if( runCount<=0 )return;

        for( AttributedCharacterIterator.Attribute a : attributes ){
            if( a==null )continue;

            removeAttributeImpl(a, beginIndex, endIndex);
        }
    }

    protected synchronized void clearAttributes(
        int beginIndex, int endIndex )
    {
        if( beginIndex>endIndex )
            throw new IllegalArgumentException("beginIndex("+beginIndex+")>endIndex("+endIndex+")");

        if( beginIndex<0 )
            throw new IllegalArgumentException("beginIndex("+beginIndex+")<0");
        if( endIndex<0 )
            throw new IllegalArgumentException("endIndex("+beginIndex+")<0");

        if( beginIndex>=length() ){
            return;
        }

        if( runCount<=0 )return;

        clearAttributesImpl(beginIndex, endIndex);
    }

    protected synchronized void addAttributeImpl(AttributedCharacterIterator.Attribute attribute, Object value,
                                                 int beginIndex, int endIndex) {

        // make sure we have run attribute data vectors
        if (runCount == 0) {
            createRunAttributeDataVectors();
        }

        // break up runs if necessary
        int beginRunIndex = ensureRunBreak(beginIndex);
        int endRunIndex = ensureRunBreak(endIndex);

        addAttributeRunData(attribute, value, beginRunIndex, endRunIndex);
    }

    protected synchronized void removeAttributeImpl(
        AttributedCharacterIterator.Attribute attribute,
        int beginIndex,
        int endIndex)
    {
        // нет атрибутов
        if( runCount<=0 ){
            return;
        }

        if( beginIndex>endIndex )
            throw new IllegalArgumentException("beginIndex("+beginIndex+")>endIndex("+endIndex+")");

        if( beginIndex<0 )
            throw new IllegalArgumentException("beginIndex("+beginIndex+")<0");
        if( endIndex<0 )
            throw new IllegalArgumentException("endIndex("+beginIndex+")<0");

        if( beginIndex>=length() ){
            return;
        }

        if( beginIndex==endIndex ){
            FoundRunBreak f1 = findRunBreak(beginIndex);
            if( !f1.found )return;

            removeAttributeRunData(attribute, f1.runIndex, f1.runIndex+1);

        }else{
            FoundRunBreak f1 = findRunBreak(beginIndex);
            if( !f1.found )return;

            // search for the run index where this offset should be
            int runIndex = f1.runIndex;
            while (runIndex < runCount && runStarts[runIndex] < endIndex) {
                removeAttributeRunData(attribute, runIndex, runIndex+1);
                // .....
                runIndex++;
            }
        }
    }

    protected synchronized void clearAttributesImpl(
        int beginIndex,
        int endIndex)
    {
        // нет атрибутов
        if( runCount<=0 ){
            return;
        }

        if( beginIndex>endIndex )
            throw new IllegalArgumentException("beginIndex("+beginIndex+")>endIndex("+endIndex+")");

        if( beginIndex<0 )
            throw new IllegalArgumentException("beginIndex("+beginIndex+")<0");
        if( endIndex<0 )
            throw new IllegalArgumentException("endIndex("+beginIndex+")<0");

        if( beginIndex>=length() ){
            return;
        }

        if( beginIndex==endIndex ){
            FoundRunBreak f1 = findRunBreak(beginIndex);
            if( !f1.found )return;

            clearAttributesRunData(f1.runIndex, f1.runIndex+1);

        }else{
            FoundRunBreak f1 = findRunBreak(beginIndex);
            if( !f1.found )return;

            // search for the run index where this offset should be
            int runIndex = f1.runIndex;
            while (runIndex < runCount && runStarts[runIndex] < endIndex) {
                clearAttributesRunData(runIndex, runIndex+1);
                // .....
                runIndex++;
            }
        }
    }

    protected static class FoundRunBreak {
        public boolean found;
        public int runIndex;
        public int lastOffset;

        public FoundRunBreak(boolean found, int runBreak, int lastOffset) {
            this.found = found;
            this.runIndex = runBreak;
            this.lastOffset = lastOffset;
        }

        public FoundRunBreak() {
        }
    }

    protected synchronized FoundRunBreak findRunBreak(int offset) {
        if( runCount<=0 )return new FoundRunBreak(false, -1, -1);

        FoundRunBreak res = new FoundRunBreak();
        res.found = false;
        res.runIndex = -1;
        res.lastOffset = -1;

        // search for the run index where this offset should be
        int runIndex = 0;
        while (runIndex < runCount && runStarts[runIndex] < offset) {
            res.runIndex = runIndex;
            res.lastOffset = runStarts[runIndex];
            runIndex++;
        }

        // if the offset is at a run start already, we're done
        if (runIndex < runCount && runStarts[runIndex] == offset) {
            res.found = true;
            res.lastOffset = offset;
            res.runIndex = runIndex;
        }

        return res;
    }

    // ensure there's a run break at offset, return the index of the run
    protected synchronized int ensureRunBreak(int offset) {
        return ensureRunBreak(offset, true);
    }

    /**
     * Ensures there is a run break at offset, returning the index of
     * the run. If this results in splitting a run, two things can happen:
     * <ul>
     * <li>If copyAttrs is true, the attributes from the existing run
     *     will be placed in both of the newly created runs.
     * <li>If copyAttrs is false, the attributes from the existing run
     * will NOT be copied to the run to the right (&gt;= offset) of the break,
     * but will exist on the run to the left (&lt; offset).
     * </ul>
     * @param offset <b>doc it</b>
     * @param copyAttrs <b>doc it</b>
     * @return <b>doc it</b>
     */
    protected synchronized int ensureRunBreak(int offset, boolean copyAttrs) {
        if (offset == length()) {
            return runCount;
        }

        // search for the run index where this offset should be
        int runIndex = 0;
        while (runIndex < runCount && runStarts[runIndex] < offset) {
            runIndex++;
        }

        // if the offset is at a run start already, we're done
        if (runIndex < runCount && runStarts[runIndex] == offset) {
            return runIndex;
        }

        // we'll have to break up a run
        // first, make sure we have enough space in our arrays
        if (runCount == runArraySize) {
            int newArraySize = runArraySize + ARRAY_SIZE_INCREMENT;
            int newRunStarts[] = new int[newArraySize];
            Vector newRunAttributes[] = new Vector[newArraySize];
            Vector newRunAttributeValues[] = new Vector[newArraySize];
            for (int i = 0; i < runArraySize; i++) {
                newRunStarts[i] = runStarts[i];
                newRunAttributes[i] = runAttributes[i];
                newRunAttributeValues[i] = runAttributeValues[i];
            }
            runStarts = newRunStarts;
            runAttributes = newRunAttributes;
            runAttributeValues = newRunAttributeValues;
            runArraySize = newArraySize;

            nextScn();
        }

        // make copies of the attribute information of the old run that the new one used to be part of
        // use temporary variables so things remain consistent in case of an exception
        Vector newRunAttributes = null;
        Vector newRunAttributeValues = null;

        if (copyAttrs) {
            Vector oldRunAttributes = runAttributes[runIndex - 1];
            Vector oldRunAttributeValues = runAttributeValues[runIndex - 1];
            if (oldRunAttributes != null) {
                newRunAttributes = (Vector) oldRunAttributes.clone();
            }
            if (oldRunAttributeValues != null) {
                newRunAttributeValues = (Vector) oldRunAttributeValues.clone();
            }
        }

        // now actually break up the run
        runCount++;

        for (int i = runCount - 1; i > runIndex; i--) {
            runStarts[i] = runStarts[i - 1];
            runAttributes[i] = runAttributes[i - 1];
            runAttributeValues[i] = runAttributeValues[i - 1];
        }
        runStarts[runIndex] = offset;
        runAttributes[runIndex] = newRunAttributes;
        runAttributeValues[runIndex] = newRunAttributeValues;
        nextScn();

        return runIndex;
    }

    // add the attribute attribute/value to all runs where beginRunIndex <= runIndex < endRunIndex
    protected synchronized void addAttributeRunData(AttributedCharacterIterator.Attribute attribute, Object value,
                                                    int beginRunIndex, int endRunIndex) {

        for (int i = beginRunIndex; i < endRunIndex; i++) {
            int keyValueIndex = -1; // index of key and value in our vectors; assume we don't have an entry yet
            if (runAttributes[i] == null) {
                Vector newRunAttributes = new Vector();
                Vector newRunAttributeValues = new Vector();
                runAttributes[i] = newRunAttributes;
                runAttributeValues[i] = newRunAttributeValues;
                nextScn();
            } else {
                // check whether we have an entry already
                keyValueIndex = runAttributes[i].indexOf(attribute);
            }

            if (keyValueIndex == -1) {
                // create new entry
                int oldSize = runAttributes[i].size();
                runAttributes[i].addElement(attribute);
                try {
                    runAttributeValues[i].addElement(value);
                }
                catch (Exception e) {
                    runAttributes[i].setSize(oldSize);
                    runAttributeValues[i].setSize(oldSize);
                }
                nextScn();
            } else {
                // update existing entry
                runAttributeValues[i].set(keyValueIndex, value);
                nextScn();
            }
        }
    }

    protected synchronized void removeAttributeRunData(
        AttributedCharacterIterator.Attribute attribute,
        int beginRunIndex,
        int endRunIndex
    ) {
        for (int i = beginRunIndex; i < endRunIndex; i++) {
            int keyValueIndex = -1; // index of key and value in our vectors;

            if (runAttributes[i] == null) {
                continue;
            } else {
                // check whether we have an entry already
                keyValueIndex = runAttributes[i].indexOf(attribute);
            }

            if (keyValueIndex >= 0) {
                // remove existing entry
                runAttributes[i].remove(keyValueIndex);
                runAttributeValues[i].remove(keyValueIndex);
                nextScn();
            }
        }
    }

    protected synchronized void clearAttributesRunData(
        int beginRunIndex,
        int endRunIndex
    ) {
        for (int i = beginRunIndex; i < endRunIndex; i++) {

            if (runAttributes[i] == null) {
                continue;
            }

            runAttributes[i].clear();
            runAttributeValues[i].clear();
            nextScn();
        }
    }

    /**
     * Creates an AttributedCharacterIterator instance that provides access to the entire contents of
     * this string.
     *
     * @return An iterator providing access to the text and its attributes.
     */
    public AttributedCharacterIterator getIterator() {
        return getIterator(null, 0, length());
    }

    /**
     * Creates an AttributedCharacterIterator instance that provides access to
     * selected contents of this string.
     * Information about attributes not listed in attributes that the
     * implementor may have need not be made accessible through the iterator.
     * If the list is null, all available attribute information should be made
     * accessible.
     *
     * @param attributes a list of attributes that the client is interested in
     * @return an iterator providing access to the entire text and its selected attributes
     */
    public AttributedCharacterIterator getIterator(AttributedCharacterIterator.Attribute[] attributes) {
        return getIterator(attributes, 0, length());
    }

    /**
     * Creates an AttributedCharacterIterator instance that provides access to
     * selected contents of this string.
     * Information about attributes not listed in attributes that the
     * implementor may have need not be made accessible through the iterator.
     * If the list is null, all available attribute information should be made
     * accessible.
     *
     * @param attributes a list of attributes that the client is interested in
     * @param beginIndex the index of the first character
     * @param endIndex the index of the character following the last character
     * @return an iterator providing access to the text and its attributes
     * @exception IllegalArgumentException if beginIndex is less then 0,
     * endIndex is greater than the length of the string, or beginIndex is
     * greater than endIndex.
     */
    public AttributedCharacterIterator getIterator(AttributedCharacterIterator.Attribute[] attributes, int beginIndex, int endIndex) {
        return new AttributedStringIterator(this, attributes, beginIndex, endIndex);
    }

    public char charAt(int index) {
        return text.charAt(index);
    }

    protected synchronized Object getAttribute(AttributedCharacterIterator.Attribute attribute, int runIndex) {
        Vector currentRunAttributes = runAttributes[runIndex];
        Vector currentRunAttributeValues = runAttributeValues[runIndex];
        if (currentRunAttributes == null) {
            return null;
        }
        int attributeIndex = currentRunAttributes.indexOf(attribute);
        if (attributeIndex != -1) {
            return currentRunAttributeValues.elementAt(attributeIndex);
        }
        else {
            return null;
        }
    }

    // gets an attribute value, but returns an annotation only if it's range does not extend outside the range beginIndex..endIndex
    protected Object getAttributeCheckRange(AttributedCharacterIterator.Attribute attribute, int runIndex, int beginIndex, int endIndex) {
        Object value = getAttribute(attribute, runIndex);
        if (value instanceof Annotation) {
            // need to check whether the annotation's range extends outside the iterator's range
            if (beginIndex > 0) {
                int currIndex = runIndex;
                int runStart = runStarts[currIndex];
                while (runStart >= beginIndex &&
                    valuesMatch(value, getAttribute(attribute, currIndex - 1))) {
                    currIndex--;
                    runStart = runStarts[currIndex];
                }
                if (runStart < beginIndex) {
                    // annotation's range starts before iterator's range
                    return null;
                }
            }
            int textLength = length();
            if (endIndex < textLength) {
                int currIndex = runIndex;
                int runLimit = (currIndex < runCount - 1) ? runStarts[currIndex + 1] : textLength;
                while (runLimit <= endIndex &&
                    valuesMatch(value, getAttribute(attribute, currIndex + 1))) {
                    currIndex++;
                    runLimit = (currIndex < runCount - 1) ? runStarts[currIndex + 1] : textLength;
                }
                if (runLimit > endIndex) {
                    // annotation's range ends after iterator's range
                    return null;
                }
            }
            // annotation's range is subrange of iterator's range,
            // so we can return the value
        }
        return value;
    }

    // returns whether all specified attributes have equal values in the runs with the given indices
    protected boolean attributeValuesMatch(Set attributes, int runIndex1, int runIndex2) {
        Iterator iterator = attributes.iterator();
        while (iterator.hasNext()) {
            AttributedCharacterIterator.Attribute key = (AttributedCharacterIterator.Attribute) iterator.next();
            if (!valuesMatch(getAttribute(key, runIndex1), getAttribute(key, runIndex2))) {
                return false;
            }
        }
        return true;
    }

    // returns whether the two objects are either both null or equal
    public static boolean valuesMatch(Object value1, Object value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="length() : int">
    public int length(){ return text.length(); }

    public static int length( AttributedCharacterIterator astr ){
        if (astr== null) {
            throw new IllegalArgumentException("astr==null");
        }
        return astr.getEndIndex();
    }
    //</editor-fold>

    public String text(){ return text; }
}
