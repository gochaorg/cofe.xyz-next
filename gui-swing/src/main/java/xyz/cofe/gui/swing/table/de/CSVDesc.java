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

package xyz.cofe.gui.swing.table.de;


import xyz.cofe.collection.BasicEventList;
import xyz.cofe.collection.EventList;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * Описание CSV файла
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class CSVDesc {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(CSVDesc.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(CSVDesc.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(CSVDesc.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(CSVDesc.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(CSVDesc.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(CSVDesc.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(CSVDesc.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="PropertyChangeSupport">
    protected final PropertyChangeSupport psupp = new SwingPropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        psupp.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        psupp.removePropertyChangeListener(listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return psupp.getPropertyChangeListeners();
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        psupp.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        psupp.removePropertyChangeListener(propertyName, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return psupp.getPropertyChangeListeners(propertyName);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        psupp.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(PropertyChangeEvent event) {
        psupp.firePropertyChange(event);
    }
//</editor-fold>

    /**
     * Конструктор по умолчанию
     */
    public CSVDesc(){
    }

    /**
     * Конструктор копирования
     * @param src образец для копирования
     */
    public CSVDesc( CSVDesc src ){
        if( src!=null ){
            this.cellDelimiter = src.cellDelimiter;
            this.cellQuote = src.cellQuote;
            this.skipEmptyLines = src.skipEmptyLines;
            this.skipFirstWS = src.skipFirstWS;
            this.quoteVariants = src.quoteVariants;
            this.skipLines = src.skipLines;
            this.firstLineAsName = src.firstLineAsName;
            this.fixedWidth = src.fixedWidth;
            if( src.columns!=null ){
                if( columns!=null ){
                    columns.clear();
                }else{
                    columns = new ArrayList<FixedColumn>();
                }
                for( FixedColumn fc : src.columns ){
                    if( fc!=null ){
                        columns.add(fc.clone());
                    }else{
                        columns.add(null);
                    }
                }
            }
        }
    }

    /**
     * Клонирование объекта
     * @return клон
     */
    @Override
    public CSVDesc clone(){
        return new CSVDesc(this);
    }

    //<editor-fold defaultstate="collapsed" desc="cellDelimiter - Разделитель между ячейчами строки">
    protected String cellDelimiter = ",";

    /**
     * Возвращает разделитель между ячейками строки. <br>
     * Обычно запятая
     * @return Разделитель между ячейчами строки
     */
    public String getCellDelimiter() {
        if( cellDelimiter==null )cellDelimiter = ",";
        return cellDelimiter;
    }

    /**
     * Указывает разделитель между ячейками строки.  <br>
     * @param cellDelimiter Разделитель между ячейчами строки
     */
    public void setCellDelimiter(String cellDelimiter) {
        Object old = this.getCellDelimiter();
        this.cellDelimiter = cellDelimiter;
        firePropertyChange("cellDelimiter", old, getCellDelimiter());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cellQuote - Экранирование ячейки">
    protected String cellQuote = "\"";

    /**
     * Возвращает символ экранирования ячейки, обычно двойные кавычки.
     * @return Экранирование ячейки
     */
    public String getCellQuote() {
        if( cellQuote==null ) cellQuote = "\"";
        return cellQuote;
    }

    /**
     * Указывает символ экранирования ячейки, обычно двойные кавычки.
     * @param cellQuote Экранирование ячейки
     */
    public void setCellQuote(String cellQuote) {
        Object old = this.getCellQuote();
        this.cellQuote = cellQuote;
        firePropertyChange("cellQuote", old, getCellQuote());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="skipEmptyLines - Пропускать пустые строки">
    protected boolean skipEmptyLines = true;

    /**
     * Пропускать пустые строки
     * @return Пропускать пустые строки
     */
    public boolean isSkipEmptyLines() {
        return skipEmptyLines;
    }

    /**
     * Пропускать пустые строки
     * @param skipEmptyLines Пропускать пустые строки
     */
    public void setSkipEmptyLines(boolean skipEmptyLines) {
        Object old = this.isSkipEmptyLines();
        this.skipEmptyLines = skipEmptyLines;
        firePropertyChange("skipEmptyLines", old, isSkipEmptyLines());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="skipFirstWS - пропускать начальные пробелы">
    protected boolean skipFirstWS = false;

    /**
     * Указывает пропускать начальные пробелы
     * @return true - пропускать начальные пробелы
     */
    public boolean isSkipFirstWS() {
        return skipFirstWS;
    }

    /**
     * Указывает пропускать начальные пробелы
     * @param skipFirstWS true - пропускать начальные пробелы
     */
    public void setSkipFirstWS(boolean skipFirstWS) {
        Object old = this.isSkipFirstWS();
        this.skipFirstWS = skipFirstWS;
        firePropertyChange("skipFirstWS", old, isSkipFirstWS());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="quoteVariants - Варианты экранирования">
    /**
     * Варианты экранирования
     */
    public static enum QuoteVariants {
        /**
         * Всегда
         */
        Always,

        /**
         * Иногда (например числа не экранируются
         */
        Sometimes,

        /**
         * Никогда
         */
        Never
    }

    protected QuoteVariants quoteVariants = QuoteVariants.Sometimes;

    /**
     * Указывает режим экранирования значений
     * @return режим экранирования
     */
    public QuoteVariants getQuoteVariants() {
        if( quoteVariants==null )quoteVariants = QuoteVariants.Sometimes;
        return quoteVariants;
    }

    /**
     * Указывает режим экранирования значений
     * @param quoteVariants режим экранирования
     */
    public void setQuoteVariants(QuoteVariants quoteVariants) {
        Object old = this.getQuoteVariants();
        this.quoteVariants = quoteVariants;
        firePropertyChange("quoteVariants", old, getQuoteVariants());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="skipLines - сколько строк пропускать">
    protected int skipLines = 0;

    /**
     * Указывает сколько строк пропускать перед началом интерпретации
     * @return сколько строк пропускать
     */
    public int getSkipLines() {
        return skipLines;
    }

    /**
     * Указывает сколько строк пропускать перед началом интерпретации
     * @param skipLines сколько строк пропускать
     */
    public void setSkipLines(int skipLines) {
        Object old = this.getSkipLines();
        this.skipLines = skipLines;
        firePropertyChange("skipLines", old, getSkipLines());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="firstLineAsName true - первая строка - имена колонок">
    protected boolean firstLineAsName = true;

    /**
     * Указывает: первая строка - имена колонок
     * @return true - первая строка имена колонок, false - первая строка данные
     */
    public boolean isFirstLineAsName() {
        return firstLineAsName;
    }

    /**
     * Указывает: первая строка - имена колонок
     * @param firstLineAsName true - первая строка имена колонок, false - первая строка данные
     */
    public void setFirstLineAsName(boolean firstLineAsName) {
        Object old = this.isFirstLineAsName();
        this.firstLineAsName = firstLineAsName;
        firePropertyChange("firstLineAsName", old, isFirstLineAsName());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fixedWidth - Фиксированная ширина колонки">
    /**
     * Фиксированная ширина колонки
     */
    protected boolean fixedWidth = false;

    /**
     * Указывает фиксирована колонка или нет
     * @return true - фиксирована / false - не фиксирована
     */
    public boolean isFixedWidth() {
        return fixedWidth;
    }

    /**
     * Указывает фиксирована колонка или нет
     * @param fixedWidth true - фиксирована / false - не фиксирована
     */
    public void setFixedWidth(boolean fixedWidth) {
        Object old = this.isFixedWidth();
        this.fixedWidth = fixedWidth;
        firePropertyChange("fixedWidth", old, isFixedWidth());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="columns - Перечень фикс. колонок">
    /**
     * Перечень фиксированных колонок
     */
    protected List<FixedColumn> columns = new BasicEventList<FixedColumn>();

    /**
     * Указывает перечень фиксированных колонок
     * @return Перечень фикс. колонок
     */
    public List<FixedColumn> getColumns() {
        if( columns==null )columns = new BasicEventList<>();
        return columns;
    }

    /**
     * Указывает перечень фиксированных колонок
     * @param columns Перечень фикс. колонок
     */
    public void setColumns(List<FixedColumn> columns) {
        this.columns = columns;
    }
    //</editor-fold>
}
