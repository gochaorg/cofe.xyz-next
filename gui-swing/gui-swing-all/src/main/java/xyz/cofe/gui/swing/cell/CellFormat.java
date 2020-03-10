package xyz.cofe.gui.swing.cell;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import xyz.cofe.gui.swing.bean.UiBean;

/**
 * Форматирование ячейки
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class CellFormat
    implements
    GetDateFormat, SetDateFormat,
    GetNumberFormat, SetNumberFormat
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(CellFormat.class.getName());
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
        logger.entering(CellFormat.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(CellFormat.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(CellFormat.class.getName(), method, result);
    }
    //</editor-fold>

    /**
     * Конструктор
     */
    public CellFormat(){}

    /**
     * Конструктор копирования
     * @param sample Образец для копирования
     */
    public CellFormat(CellFormat sample){
        if( sample!=null ){
            this.textAlign = sample.textAlign;

            this.halign = sample.halign;
            this.valign = sample.valign;

            this.textPadLeft = sample.textPadLeft;
            this.textPadRight = sample.textPadRight;
            this.textPadTop = sample.textPadTop;
            this.textPadBottom = sample.textPadBottom;

            this.textAliasing = sample.textAliasing;

            this.color = sample.color;
            this.backgroundColor = sample.backgroundColor;
            this.font = sample.font;

            this.icon = sample.icon;
            this.iconPlaceholder = sample.iconPlaceholder!=null ?
                new Dimension(sample.iconPlaceholder) : null;
            this.autoIconPlaceholder = sample.autoIconPlaceholder;

            this.padLeft = sample.padLeft;
            this.padTop = sample.padTop;
            this.padRight = sample.padRight;
            this.padBottom = sample.padBottom;

            this.iconPadLeft = sample.iconPadLeft;
            this.iconPadTop = sample.iconPadTop;
            this.iconPadBottom = sample.iconPadBottom;
            this.iconPadRight = sample.iconPadRight;

            this.borderLeftColor = sample.borderLeftColor;
            this.borderLeftWidth = sample.borderLeftWidth;
            this.borderLeftDash = sample.borderLeftDash;
            this.borderRightColor = sample.borderRightColor;
            this.borderRightWidth = sample.borderRightWidth;
            this.borderRightDash = sample.borderRightDash;
            this.borderTopColor = sample.borderTopColor;
            this.borderTopWidth = sample.borderTopWidth;
            this.borderTopDash = sample.borderTopDash;
            this.borderBottomColor = sample.borderBottomColor;
            this.borderBottomWidth = sample.borderBottomWidth;
            this.borderBottomDash = sample.borderBottomDash;
            if( sample.numberFormat!=null )this.numberFormat = (NumberFormat)sample.numberFormat.clone();
            if( sample.dateFormat!=null )this.dateFormat = (DateFormat)sample.dateFormat.clone();
            this.maxLineLength = sample.maxLineLength;
            this.maxLinesCount = sample.maxLinesCount;
        }
    }

    /**
     * Клонирование
     * @return  клон
     */
    @Override
    public CellFormat clone(){
        return new CellFormat(this);
    }

    //<editor-fold defaultstate="collapsed" desc="reset()">
    /**
     * Сброс форматирования
     * @return self ссылка
     */
    public CellFormat reset(){
        textAlign = 0d;
        halign = 0d;
        valign = 0d;
        textPadLeft = 0d;
        textPadTop = 0d;
        textPadBottom = 0d;
        textPadRight = 0d;
        textAliasing = TextAliasing.ON;
        color = null;
        backgroundColor = null;
        font = null;
        icon = null;
        iconPlaceholder = null;
        autoIconPlaceholder = true;
        padLeft = padRight = padTop = padBottom = 0d;
        iconPadLeft = iconPadRight = iconPadTop = iconPadBottom = 0d;
        borderLeftColor = borderRightColor = borderTopColor = borderBottomColor = null;
        borderLeftDash = borderRightDash = borderTopDash = borderBottomDash = null;
        borderLeftWidth = borderRightWidth = borderTopWidth = borderBottomWidth = 0;
        numberFormat = null;
        dateFormat = null;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="numberFormat : NumberFormat">
    protected NumberFormat numberFormat;

    /**
     * Указывает формат чисел
     * @return формат чисел
     */
    @Override
    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    /**
     * Указывает формат чисел
     * @param numberFormat формат чисел
     */
    @Override
    public void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    /**
     * Указывает формат чисел.
     *
     * <table summary="Описание формата чисел">
     * <tr style="font-weight: bold"><td>Symbol</td><td>Location</td><td>Localized?</td><td>Meaning</td></tr>
     * <tr><td>0</td><td>Number</td><td>Yes</td><td>Digit</td></tr>
     * <tr><td>#</td><td>Number</td><td>Yes</td><td>Digit, zero shows as absent</td></tr>
     * <tr><td>.</td><td>Number</td><td>Yes</td><td>Decimal separator or monetary decimal separator</td></tr>
     * <tr><td>-</td><td>Number</td><td>Yes</td><td>Minus sign</td></tr>
     * <tr><td>,</td><td>Number</td><td>Yes</td><td>Grouping separator</td></tr>
     * <tr><td>E</td><td>Number</td><td>Yes</td><td>Separates mantissa and exponent in scientific notation. Need not be quoted in prefix or suffix.</td></tr>
     * <tr><td>;</td><td>Subpattern boundary</td><td>Yes</td><td>Separates positive and negative subpatterns</td></tr>
     * <tr><td>%</td><td>Prefix or suffix</td><td>Yes</td><td>Multiply by 100 and show as percentage</td></tr>
     * <tr><td>\u2030</td><td>Prefix or suffix</td><td>Yes</td><td>Multiply by 1000 and show as per mille value</td></tr>
     * <tr><td>¤ (\u00A4)</td><td>Prefix or suffix</td><td>No</td><td>Currency sign, replaced by currency symbol. If doubled, replaced by international currency symbol. If present in a pattern, the monetary decimal separator is used instead of the decimal separator.</td></tr>
     * <tr><td>'</td><td>Prefix or suffix</td><td>No</td><td>Used to quote special characters in a prefix or suffix, for example, "'#'#" formats 123 to "#123". To create a single quote itself, use two in a row: "# o''clock".</td></tr>
     * </table>
     * @param pattern шаблон
     * @param symb набор симвлов
     * @return self ссылка
     */
    public CellFormat numberFormat(String pattern, DecimalFormatSymbols symb){
        if( pattern==null ){
            numberFormat = null;
            return this;
        }

        numberFormat =
            symb!=null ? new DecimalFormat(pattern) : new DecimalFormat(pattern, symb);

        return this;
    }

    /**
     * Указывает формат чисел
     * @param pattern шаблон
     * @return self ссылка
     * @see #numberFormat(String, DecimalFormatSymbols)
     */
    public CellFormat numberFormat(String pattern){
        if( pattern==null ){
            numberFormat = null;
            return this;
        }

        numberFormat = new DecimalFormat(pattern);
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="dateFormat : DateFormat">
    protected DateFormat dateFormat;

    /**
     * Указывает формат даты/времени
     * @return формат даты/времени
     */
    @Override
    public DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * Указывает формат даты/времени
     * @param dateFormat формат даты/времени
     */
    @Override
    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * Указывает формат даты/времени.
     *
     * <table summary="Описание формата дат">
     * <tr style="font-weight: bold"><td>Letter</td><td>Date or Time Component</td><td>Presentation</td><td>Examples</td></tr>
     * <tr><td>G</td><td>Era designator</td><td>Text</td><td>AD</td></tr>
     * <tr><td>y</td><td>Year</td><td>Year</td><td>1996; 96</td></tr>
     * <tr><td>Y</td><td>Week year</td><td>Year</td><td>2009; 09</td></tr>
     * <tr><td>M</td><td>Month in year</td><td>Month</td><td>July; Jul; 07</td></tr>
     * <tr><td>w</td><td>Week in year</td><td>Number</td><td>27</td></tr>
     * <tr><td>W</td><td>Week in month</td><td>Number</td><td>2</td></tr>
     * <tr><td>D</td><td>Day in year</td><td>Number</td><td>189</td></tr>
     * <tr><td>d</td><td>Day in month</td><td>Number</td><td>10</td></tr>
     * <tr><td>F</td><td>Day of week in month</td><td>Number</td><td>2</td></tr>
     * <tr><td>E</td><td>Day name in week</td><td>Text</td><td>Tuesday; Tue</td></tr>
     * <tr><td>u</td><td>Day number of week (1 = Monday, ..., 7 = Sunday)</td><td>Number</td><td>1</td></tr>
     * <tr><td>a</td><td>Am/pm marker</td><td>Text</td><td>PM</td></tr>
     * <tr><td>H</td><td>Hour in day (0-23)</td><td>Number</td><td>0</td></tr>
     * <tr><td>k</td><td>Hour in day (1-24)</td><td>Number</td><td>24</td></tr>
     * <tr><td>K</td><td>Hour in am/pm (0-11)</td><td>Number</td><td>0</td></tr>
     * <tr><td>h</td><td>Hour in am/pm (1-12)</td><td>Number</td><td>12</td></tr>
     * <tr><td>m</td><td>Minute in hour</td><td>Number</td><td>30</td></tr>
     * <tr><td>s</td><td>Second in minute</td><td>Number</td><td>55</td></tr>
     * <tr><td>S</td><td>Millisecond</td><td>Number</td><td>978</td></tr>
     * <tr><td>z</td><td>Time zone</td><td>General time zone</td><td>Pacific Standard Time; PST; GMT-08:00</td></tr>
     * <tr><td>Z</td><td>Time zone</td><td>RFC 822 time zone</td><td>-0800</td></tr>
     * <tr><td>X</td><td>Time zone</td><td>ISO 8601 time zone</td><td>-08; -0800; -08:00</td></tr>
     * </table>
     *
     * <p>
     * Examples
     *
     * <table summary="Пример формата дат">
     * <tr style="font-weight: bold"><td>Date and Time Pattern</td><td>Result</td></tr>
     * <tr><td>"yyyy.MM.dd G 'at' HH:mm:ss z"</td><td>2001.07.04 AD at 12:08:56 PDT</td></tr>
     * <tr><td>"EEE, MMM d, ''yy"</td><td>Wed, Jul 4, '01</td></tr>
     * <tr><td>"h:mm a"</td><td>12:08 PM</td></tr>
     * <tr><td>"hh 'o''clock' a, zzzz"</td><td>12 o'clock PM, Pacific Daylight Time</td></tr>
     * <tr><td>"K:mm a, z"</td><td>0:08 PM, PDT</td></tr>
     * <tr><td>"yyyyy.MMMMM.dd GGG hh:mm aaa"</td><td>02001.July.04 AD 12:08 PM</td></tr>
     * <tr><td>"EEE, d MMM yyyy HH:mm:ss Z"</td><td>Wed, 4 Jul 2001 12:08:56 -0700</td></tr>
     * <tr><td>"yyMMddHHmmssZ"</td><td>010704120856-0700</td></tr>
     * <tr><td>"yyyy-MM-dd'T'HH:mm:ss.SSSZ"</td><td>2001-07-04T12:08:56.235-0700</td></tr>
     * <tr><td>"yyyy-MM-dd'T'HH:mm:ss.SSSXXX"</td><td>2001-07-04T12:08:56.235-07:00</td></tr>
     * <tr><td>"YYYY-'W'ww-u"</td><td>2001-W27-3</td></tr>
     * </table>
     *
     * @param simpleDateFormat шаблон
     * @return self ссылка
     */
    public CellFormat dateFormat(String simpleDateFormat){
        if( simpleDateFormat==null ){
            this.dateFormat = null;
        }else{
            this.dateFormat = new SimpleDateFormat(simpleDateFormat);
        }
        return this;
    }

    /**
     * Указывает формат даты/времени.
     * @param simpleDateFormat шаблон
     * @param loc локаль
     * @return self ссылка
     * @see #dateFormat(String)
     */
    public CellFormat dateFormat(String simpleDateFormat, Locale loc){
        if( simpleDateFormat==null ){
            this.dateFormat = null;
        }else{
            this.dateFormat =
                loc!=null ? new SimpleDateFormat(simpleDateFormat, loc) : new SimpleDateFormat(simpleDateFormat);
        }
        return this;
    }

    /**
     * Указывает формат даты/времени.
     * @param simpleDateFormat шаблон
     * @param symbls локаль
     * @return self ссылка
     * @see #dateFormat(String)
     */
    public CellFormat dateFormat(String simpleDateFormat, DateFormatSymbols symbls){
        if( simpleDateFormat==null ){
            this.dateFormat = null;
        }else{
            this.dateFormat =
                symbls!=null
                    ? new SimpleDateFormat(simpleDateFormat, symbls)
                    : new SimpleDateFormat(simpleDateFormat);
        }
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="maxLinesCount : int = -1 - max text lines">
    /**
     * Указывает максимальное отображаемое кол-во линий текста
     */
    protected Integer maxLinesCount = -1;

    /**
     * Указывает максимальное отображаемое кол-во линий текста
     * @return кол-во или -1/0 - без ограничения
     */
    public Integer getMaxLinesCount() {
        return maxLinesCount;
    }

    /**
     * Указывает максимальное отображаемое кол-во линий текста
     * @param maxLinesCount кол-во или -1/0 - без ограничения
     */
    public void setMaxLinesCount(Integer maxLinesCount) {
        this.maxLinesCount = maxLinesCount;
    }

    /**
     * Указывает максимальное отображаемое кол-во линий текста
     * @param maxLinesCount кол-во или -1/0 - без ограничения
     * @return self ссылка
     */
    public CellFormat maxLinesCount(Integer maxLinesCount){
        this.maxLinesCount = maxLinesCount;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="maxLineLength : int = -1 - max line len">
    /**
     * Указывает максимальное отображаемое кол-во символов в строке
     */
    protected Integer maxLineLength = -1;

    /**
     * Указывает максимальное отображаемое кол-во символов в строке
     * @return кол-во или -1/0 - без ограничения
     */
    public Integer getMaxLineLength() {
        return maxLineLength;
    }

    /**
     * Указывает максимальное отображаемое кол-во символов в строке
     * @param maxLineLength максимальное кол-во символов - 0 и меньше - без ограничения (по умолчанию)
     */
    public void setMaxLineLength(Integer maxLineLength) {
        this.maxLineLength = maxLineLength;
    }

    /**
     * Указывает максимальное отображаемое кол-во символов в строке
     * @param maxLineLength кол-во или -1/0 - без ограничения (по умолчанию)
     * @return self ссылка
     */
    public CellFormat maxLineLength(Integer maxLineLength){
        this.maxLineLength = maxLineLength;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="textAlign : double">
    protected Double textAlign = 0d;
    /**
     * Возвращает выравнивание текста по горизонтали относительно наибольшей строки текста.
     * @return 0 - по левому краю ... 0.5 - по центру ... 1 по правому
     */
    public Double getTextAlign(){ return textAlign; }
    /**
     * Возвращает выравнивание текста по горизонтали относительно наибольшей строки текста.
     * @param defaultValue значение по умолчанию
     * @return 0 - по левому краю ... 0.5 - по центру ... 1 по правому
     */
    public double getTextAlign(double defaultValue){ return textAlign!=null ? textAlign : defaultValue; }
    /**
     * Указывает выравнивание текста по горизонтали относительно наибольшей строки текста.
     * @param v 0 - по левому краю ... 0.5 - по центру ... 1 по правому
     */
    public void setTextAlign(Double v){ textAlign=v; }
    /**
     * Указывает выравнивание текста по горизонтали относительно наибольшей строки текста.
     * @param v 0 - по левому краю ... 0.5 - по центру ... 1 по правому
     * @return self ссылка
     */
    public CellFormat textAlign(Double v){
        textAlign=v;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="halign : double">
    protected Double halign = 0d;

    /**
     * Возвращает выравнивание относительно контекста
     * @return 0-по левому краю; ... 0.5-по центру; ... 1-по правому.
     */
    public Double getHalign(){ return halign; }

    /**
     * Возвращает выравнивание относительно контекста
     * @param defaultValue значени по умолчанию
     * @return 0-по левому краю; ... 0.5-по центру; ... 1-по правому.
     */
    public double getHalign(double defaultValue){ return halign!=null ? halign : defaultValue; }

    /**
     * Указывает выравнивание относительно контекста
     * @param v 0-по левому краю; ... 0.5-по центру; ... 1-по правому.
     */
    public void setHalign(Double v){ halign=v; }
    /**
     * Указывает выравнивание относительно контекста
     * @param v 0-по левому краю; ... 0.5-по центру; ... 1-по правому.
     * @return self ссылка
     */
    public CellFormat halign(Double v){
        halign=v;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="valign : double">
    protected Double valign = 0d;
    /**
     * Возвращает выравнивание относительно контекста
     * @return 0-по верхнему краю; ... 0.5-по центру; ... 1-по нижнему.
     */
    public Double getValign(){ return valign; }
    /**
     * Возвращает выравнивание относительно контекста
     * @param defaultValue значени по умолчанию
     * @return 0-по верхнему краю; ... 0.5-по центру; ... 1-по нижнему.
     */
    public double getValign(double defaultValue){ return valign!=null ? valign : defaultValue; }

    /**
     * Указывает выравнивание относительно контекста
     * @param v 0-по верхнему краю; ... 0.5-по центру; ... 1-по нижнему.
     */
    public void setValign(Double v){ valign=v; }
    /**
     * Указывает выравнивание относительно контекста
     * @param v 0-по верхнему краю; ... 0.5-по центру; ... 1-по нижнему.
     * @return self ссылка
     */
    public CellFormat valign(Double v){
        valign=v;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="padLeft/padRight/padTop/padBottom">
    //<editor-fold defaultstate="collapsed" desc="padLeft : double">
    protected Double padLeft = 0d;
    /**
     * Возвращает отступ слева
     * @return отступ
     */
    public Double getPadLeft(){ return padLeft; }
    /**
     * Возвращает отступ слева
     * @param defaultValue значени по умолчанию
     * @return отступ
     */
    public double getPadLeft(double defaultValue){ return padLeft!=null ? padLeft : defaultValue; }

    /**
     * Указывает отступ слева
     * @param v отступ
     */
    public void setPadLeft(Double v){ padLeft=v; }

    /**
     * Указывает отступ слева
     * @param v отступ
     * @return self ссылка
     */
    public CellFormat padLeft(Double v){
        padLeft=v;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="padRight : double">
    protected Double padRight = 0d;
    /**
     * Возвращает отступ справа
     * @return отступ
     */
    public Double getPadRight(){ return padRight; }
    /**
     * Возвращает отступ справа
     * @param defaultValue значени по умолчанию
     * @return отступ
     */
    public double getPadRight(double defaultValue){ return padRight!=null ? padRight : defaultValue; }

    /**
     * Указывает отступ справа
     * @param v отступ
     */
    public void setPadRight(Double v){ padRight=v; }

    /**
     * Указывает отступ справа
     * @param v отступ
     * @return self ссылка
     */
    public CellFormat padRight(Double v){
        padRight=v;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="padTop : double">
    protected Double padTop = 0d;
    /**
     * Возвращает отступ сверху
     * @return отступ
     */
    public Double getPadTop(){ return padTop; }
    /**
     * Возвращает отступ сверху
     * @param defaultValue значени по умолчанию
     * @return отступ
     */
    public double getPadTop(double defaultValue){ return padTop!=null ? padTop : defaultValue; }

    /**
     * Указывает отступ сверху
     * @param v отступ
     */
    public void setPadTop(Double v){ padTop=v; }

    /**
     * Указывает отступ сверху
     * @param v отступ
     * @return self ссылка
     */
    public CellFormat padTop(Double v){
        padTop=v;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="padBottom : double">
    protected Double padBottom = 0d;
    /**
     * Возвращает отступ снизу
     * @return отступ
     */
    public Double getPadBottom(){ return padBottom; }
    /**
     * Возвращает отступ снизу
     * @param defaultValue значени по умолчанию
     * @return отступ
     */
    public double getPadBottom(double defaultValue){ return padBottom!=null ? padBottom : defaultValue; }

    /**
     * Указывает отступ снизу
     * @param v отступ
     */
    public void setPadBottom(Double v){ padBottom=v; }

    /**
     * Указывает отступ снизу
     * @param v отступ
     * @return self ссылка
     */
    public CellFormat padBottom(Double v){
        padBottom=v;
        return this;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="textPadLeft/textPadRight/textPadTop/textPadBottom">
    //<editor-fold defaultstate="collapsed" desc="textPadLeft : double">
    protected Double textPadLeft = 0d;
    /**
     * Указывает отступ слева в текстовом блоке
     * @return отступ
     */
    public Double getTextPadLeft(){ return textPadLeft; }

    /**
     * Указывает отступ слева в текстовом блоке
     * @param defaultValue значение по умолчанию
     * @return отступ
     */
    public double getTextPadLeft(double defaultValue){ return textPadLeft!=null ? textPadLeft : defaultValue; }

    /**
     * Указывает отступ слева в текстовом блоке
     * @param v отступ
     */
    public void setTextPadLeft(Double v){ textPadLeft=v; }

    /**
     * Указывает отступ слева в текстовом блоке
     * @param v отступ
     * @return self ссылка
     */
    public CellFormat textPadLeft(Double v){
        textPadLeft=v;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="textPadRight : double">
    protected Double textPadRight = 0d;

    /**
     * Указывает отступ справа в текстовом блоке
     * @return отступ
     */
    public Double getTextPadRight(){ return textPadRight; }

    /**
     * Указывает отступ справа в текстовом блоке
     * @param defaultValue значение по умолчанию
     * @return отступ
     */
    public double getTextPadRight(double defaultValue){ return textPadRight!=null ? textPadRight : defaultValue; }
    /**
     * Указывает отступ справа в текстовом блоке
     * @param v отступ
     */
    public void setTextPadRight(Double v){ textPadRight=v; }
    /**
     * Указывает отступ справа в текстовом блоке
     * @param v отступ
     * @return self ссылка
     */
    public CellFormat textPadRight(Double v){
        textPadRight=v;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="textPadTop : double">
    protected Double textPadTop = 0d;
    /**
     * Указывает отступ сверху в текстовом блоке
     * @return отступ
     */
    public Double getTextPadTop(){ return textPadTop; }

    /**
     * Указывает отступ сверху в текстовом блоке
     * @param defaultValue значение по умолчанию
     * @return отступ
     */
    public double getTextPadTop(double defaultValue){ return textPadTop!=null ? textPadTop : defaultValue; }
    /**
     * Указывает отступ сверху в текстовом блоке
     * @param v отступ
     */
    public void setTextPadTop(Double v){ textPadTop=v; }
    /**
     * Указывает отступ сверху в текстовом блоке
     * @param v отступ
     * @return self ссылка
     */
    public CellFormat textPadTop(Double v){
        textPadTop=v;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="textPadBottom : double">
    protected Double textPadBottom = 0d;
    /**
     * Указывает отступ снизу в текстовом блоке
     * @return отступ
     */
    public Double getTextPadBottom(){ return textPadBottom; }

    /**
     * Указывает отступ снизу в текстовом блоке
     * @param defaultValue значение по умолчанию
     * @return отступ
     */
    public double getTextPadBottom(double defaultValue){ return textPadBottom!=null ? textPadBottom : defaultValue; }
    /**
     * Указывает отступ снизу в текстовом блоке
     * @param v отступ
     */
    public void setTextPadBottom(Double v){ textPadBottom=v; }
    /**
     * Указывает отступ снизу в текстовом блоке
     * @param v отступ
     * @return self ссылка
     */
    public CellFormat textPadBottom(Double v){
        textPadBottom=v;
        return this;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="textAliasing : TextAliasing">
    protected TextAliasing textAliasing=TextAliasing.ON;

    /**
     * Возвращает способ сглаживания текста
     * @return способ сглаживания
     */
    public TextAliasing getTextAliasing() { return textAliasing; }

    /**
     * Указывает способ сглаживания текста
     * @param textAliasing способ сглаживания
     */
    public void setTextAliasing(TextAliasing textAliasing) { this.textAliasing = textAliasing; }

    /**
     * Указывает способ сглаживания текста
     * @param aliasing способ сглаживания
     * @return self ссылка
     */
    public CellFormat textAliasing(TextAliasing aliasing) { this.textAliasing = aliasing; return this; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="color : Color">
    protected Color color;
    /**
     * Указывает цвет текста
     * @return цвет текста
     */
    public Color getColor() { return color; }

    /**
     * Указывает цвет текста
     * @param color цвет текста
     */
    public void setColor(Color color) { this.color = color; }

    /**
     * Указывает цвет текста
     * @param color цвет текста
     * @return self ссылка
     */
    public CellFormat color(Color color) { this.color = color; return this; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="backgroundColor : Color">
    protected Color backgroundColor;

    /**
     * Возвращает цвет фона текста
     * @return цвет фона
     */
    public Color getBackgroundColor() { return backgroundColor; }

    /**
     * Указывает цвет фона текста
     * @param color цвет фона
     */
    public void setBackgroundColor(Color color) { this.backgroundColor = color; }

    /**
     * Указывает цвет фона текста
     * @param color цвет фона
     * @return self ссылка
     */
    public CellFormat backgroundColor(Color color) { this.backgroundColor = color; return this; }

    /**
     * Указывает цвет фона текста
     * @param color цвет фона
     * @return self ссылка
     */
    public CellFormat bgColor(Color color) { this.backgroundColor = color; return this; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="font : Font">
    protected Font font;

    /**
     * Возвращает шрифт используемый для отображения
     * @return шрифт
     */
    public Font getFont(){ return font; }

    /**
     * Указывает шрифт используемый для отображения
     * @param f шрифт
     */
    public void setFont(Font f){ font=f; }

    /**
     * Указывает шрифт используемый для отображения
     * @param f шрифт
     * @return self ссылка
     */
    public CellFormat font(Font f){ font=f; return this; }

    /**
     * Указывает шрифт используемый для отображения.
     *
     * <p>шрифт уже должен быть указан (getFont()!=null)
     * @param fontName имя шрифта
     * @param size размер
     * @param bold true - жирный шрифт
     * @param italic true - наклонный шрифт
     * @return self ссылка
     */
    public CellFormat font(String fontName, float size, boolean bold, boolean italic ){
        if( fontName==null )throw new IllegalArgumentException("fontName==null");
        font = new Font(fontName,
            (bold ? Font.BOLD : 0) | (italic ? Font.ITALIC : 0),
            10
        ).deriveFont(size);
        return this;
    }

    /**
     * Указывает использовать наклонный шрифт.
     *
     * <p>шрифт уже должен быть указан (getFont()!=null)
     * @param italic true - использовать наклонный шрифт
     * @return self ссылка
     * @see #fontFamily(String)
     */
    public CellFormat italic(boolean italic){
        Font f = font;
        if( f!=null ){
            int style = (f.isBold() ? Font.BOLD : 0) | (italic ? Font.ITALIC : 0);
            font = f.deriveFont(style);
        }
        return this;
    }

    /**
     * Указывает использовать жирный шрифт.
     *
     * <p>шрифт уже должен быть указан (getFont()!=null)
     * @param bld true - жирный шрифт
     * @return self ссылка
     * @see #fontFamily(String)
     */
    public CellFormat bold(boolean bld){
        Font f = font;
        if( f!=null ){
            int style = (bld ? Font.BOLD : 0) | (f.isItalic() ? Font.ITALIC : 0);
            font = f.deriveFont(style);
        }
        return this;
    }

    /**
     * Указывает размер используемого шрифта.
     *
     * <p>шрифт уже должен быть указан (getFont()!=null)
     * @param pointSize размер
     * @return self ссылка
     * @see #fontFamily(String)
     */
    public CellFormat fontSize(float pointSize){
        Font f = font;
        if( f!=null ){
            font = f.deriveFont(pointSize);
        }
        return this;
    }

    /**
     * Указывает имя (семейство) шрифта.
     *
     * <p>Если шрифт уже указан (getFont()!=null), то изменяется только его имя,
     * <p>Если шрифт еще не указан (getFont()!=null), то используется шрифт с указаным именем, приямой, и обычной тяжести.
     * @param name имя шрифта
     * @return self ссылка
     */
    public CellFormat fontFamily(String name){
        Font f = font;
        if( f!=null && name!=null ){
            f = new Font(name,f.getStyle(),f.getSize());
            font = f;
        }else if( f==null && name!=null ){
            f = new Font(name, Font.PLAIN, 11);
            font = f;
        }
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="icon : Icon">
    protected Icon icon;

    /**
     * Возвращает иконку отображаемую рядом с текстом
     * @return иконка
     */
    public Icon getIcon() { return icon; }

    /**
     * Указывает иконку отображаемую рядом с текстом
     * @param icon иконка
     */
    public void setIcon(Icon icon) { this.icon = icon; }

    /**
     * Указывает иконку отображаемую рядом с текстом
     * @param ico иконка
     * @return self ссылка
     */
    public CellFormat icon(Icon ico){ this.icon = ico; return this; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="iconPadLeft/iconPadRight/iconPadTop/iconPadBottom">
    //<editor-fold defaultstate="collapsed" desc="iconPadLeft : double">
    protected Double iconPadLeft = 0d;
    /**
     * Возвращает отступ слева для иконки
     * @return отступ
     */
    public Double getIconPadLeft(){ return iconPadLeft; }

    /**
     * Возвращает отступ слева для иконки
     * @param defaultValue значение по умолчанию
     * @return отступ
     */
    public double getIconPadLeft(double defaultValue){
        return iconPadLeft!=null ? iconPadLeft : defaultValue;
    }

    /**
     * Указывает отступ слева для иконки
     * @param v отступ
     */
    public void setIconPadLeft(Double v){ iconPadLeft=v; }

    /**
     * Указывает отступ слева для иконки
     * @param v отступ
     * @return self ссылка
     */
    public CellFormat iconPadLeft(Double v){
        iconPadLeft=v;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="iconPadRight : double">
    protected Double iconPadRight = 0d;
    /**
     * Возвращает отступ справа для иконки
     * @return отступ
     */
    public Double getIconPadRight(){ return iconPadRight; }

    /**
     * Возвращает отступ справа для иконки
     * @param defaultValue значение по умолчанию
     * @return отступ
     */
    public double getIconPadRight(double defaultValue){
        return iconPadRight!=null ? iconPadRight : defaultValue;
    }

    /**
     * Указывает отступ справа для иконки
     * @param v отступ
     */
    public void setIconPadRight(Double v){ iconPadRight=v; }

    /**
     * Указывает отступ справа для иконки
     * @param v отступ
     * @return self ссылка
     */
    public CellFormat iconPadRight(Double v){
        iconPadRight=v;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="iconPadTop : double">
    protected Double iconPadTop = 0d;
    /**
     * Возвращает отступ сверху для иконки
     * @return отступ
     */
    public Double getIconPadTop(){ return iconPadTop; }

    /**
     * Возвращает отступ сверху для иконки
     * @param defaultValue значение по умолчанию
     * @return отступ
     */
    public double getIconPadTop(double defaultValue){
        return iconPadTop!=null ? iconPadTop : defaultValue;
    }

    /**
     * Указывает отступ сверху для иконки
     * @param v отступ
     */
    public void setIconPadTop(Double v){ iconPadTop=v; }

    /**
     * Указывает отступ сверху для иконки
     * @param v отступ
     * @return self ссылка
     */
    public CellFormat iconPadTop(Double v){
        iconPadTop=v;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="iconPadBottom : double">
    protected Double iconPadBottom = 0d;
    /**
     * Возвращает отступ сверху для иконки
     * @return отступ
     */
    public Double getIconPadBottom(){ return iconPadBottom; }

    /**
     * Возвращает отступ сверху для иконки
     * @param defaultValue значение по умолчанию
     * @return отступ
     */
    public double getIconPadBottom(double defaultValue){
        return iconPadBottom!=null ? iconPadBottom : defaultValue;
    }

    /**
     * Указывает отступ сверху для иконки
     * @param v отступ
     */
    public void setIconPadBottom(Double v){ iconPadBottom=v; }

    /**
     * Указывает отступ сверху для иконки
     * @param v отступ
     * @return self ссылка
     */
    public CellFormat iconPadBottom(Double v){
        iconPadBottom=v;
        return this;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="iconPlaceholder : Dimension">
    protected Dimension iconPlaceholder;

    /**
     * Указывает резервирование места под иконку
     * @return зарезервированное место
     */
    public Dimension getIconPlaceholder() { return iconPlaceholder; }

    /**
     * Указывает резервирование места под иконку
     * @param iconPlaceholder зарезервированное место
     */
    public void setIconPlaceholder(Dimension iconPlaceholder) {
        this.iconPlaceholder = iconPlaceholder;
    }

    /**
     * Указывает резервирование места под иконку
     * @param placeholder зарезервированное место
     * @return self ссылка
     */
    public CellFormat iconPlaceholder(Dimension placeholder){
        setIconPlaceholder(iconPlaceholder);
        return this;
    }

    /**
     * Указывает резервирование места под иконку
     * @param width ширина
     * @param height высота
     * @return self ссылка
     */
    public CellFormat iconPlaceholder(double width, double  height){
        if( width>0 && height>0 ){
            setIconPlaceholder(new Dimension((int)width,(int)height));
        }
        return this;
    }

    /**
     * Указывает резервирование места под иконку
     * @param width ширина
     * @param height высота
     * @return self ссылка
     */
    public CellFormat iconPlaceholder(int width, int height){
        setIconPlaceholder(new Dimension(width,height));
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="autoIconPlaceholder : boolean">
    protected boolean autoIconPlaceholder = true;

    /**
     * Указывает автоматическое резервирование места под иконку
     * @return true - автоматическое резервирование
     */
    public boolean isAutoIconPlaceholder() { return autoIconPlaceholder; }

    /**
     * Указывает автоматическое резервирование места под иконку
     * @param autoIconPlaceholder  true - автоматическое резервирование
     */
    public void setAutoIconPlaceholder(boolean autoIconPlaceholder) { this.autoIconPlaceholder = autoIconPlaceholder; }

    /**
     * Указывает автоматическое резервирование места под иконку
     * @param autoIconPlaceholder true - автоматическое резервирование
     * @return self ссылка
     */
    public CellFormat autoIconPlaceholder(boolean autoIconPlaceholder) {
        this.autoIconPlaceholder = autoIconPlaceholder;
        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderLeft/borderRight/borderTop/borderBottom">
    //<editor-fold defaultstate="collapsed" desc="borderLeft">
    //<editor-fold defaultstate="collapsed" desc="borderLeftColor : Color">
    protected Color   borderLeftColor;

    /**
     * Цвет бордюра - левая сторона
     * @return цвет
     */
    public Color getBorderLeftColor() { return borderLeftColor; }

    /**
     * Цвет бордюра - левая сторона
     * @param borderLeftColor цвет
     */
    public void setBorderLeftColor(Color borderLeftColor) { this.borderLeftColor = borderLeftColor; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderLeftWidth : float">
    protected float   borderLeftWidth;

    /**
     * Ширина бордюра - левая сторона
     * @return ширина бордюра
     */
    public float getBorderLeftWidth() { return borderLeftWidth; }

    /**
     * Ширина бордюра - левая сторона
     * @param borderLeftWidth ширина бордюра
     */
    public void setBorderLeftWidth(float borderLeftWidth) { this.borderLeftWidth = borderLeftWidth; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderLeftDash : float[]">
    protected float[] borderLeftDash;

    /**
     * Паттерн бордюра - левая сторона
     * @return Паттерн бордюра
     */
    //TODO @UiBean(propertyEditor = DashEditor.class)
    public float[] getBorderLeftDash(){
        if( borderLeftDash==null )return new float[]{};
        return borderLeftDash;
    }

    /**
     * Паттерн бордюра - левая сторона
     * @param dash Паттерн бордюра
     */
    public void setBorderLeftDash(float[] dash){
        borderLeftDash = dash;
    }

    /**
     * Устанавливает левою границу бордюра
     * @param color цвет
     * @param width ширина
     * @param dash паттер
     * @return self ссылка
     */
    public CellFormat borderLeft(Color color,float width,float ... dash){
        borderLeftColor = color;
        borderLeftWidth = width;
        borderLeftDash = dash;
        return this;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderRight">
    //<editor-fold defaultstate="collapsed" desc="borderRightColor : Color">
    protected Color   borderRightColor;

    /**
     * Цвет бордюра - правая сторона
     * @return цвет
     */
    public Color getBorderRightColor() { return borderRightColor; }

    /**
     * Цвет бордюра - правая сторона
     * @param color цвет
     */
    public void setBorderRightColor(Color color) { this.borderRightColor = color; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderRightWidth : float">
    protected float   borderRightWidth;
    /**
     * Ширина бордюра - правая сторона
     * @return ширина бордюра
     */
    public float getBorderRightWidth() { return borderRightWidth; }

    /**
     * Ширина бордюра - правая сторона
     * @param width ширина бордюра
     */
    public void setBorderRightWidth(float width) { this.borderRightWidth = width; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderRightDash : float[]">
    protected float[] borderRightDash;
    /**
     * Паттерн бордюра - правая сторона
     * @return Паттерн бордюра
     */
    //TODO @UiBean(propertyEditor = DashEditor.class)
    public float[] getBorderRightDash(){
        if( borderRightDash==null )return new float[]{};
        return borderRightDash;
    }

    /**
     * Паттерн бордюра - правая сторона
     * @param dash Паттерн бордюра
     */
    public void setBorderRightDash(float[] dash){
        borderRightDash = dash;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderRight()">
    /**
     * Устанавливает правую границу бордюра
     * @param color цвет
     * @param width ширина
     * @param dash паттер
     * @return self ссылка
     */
    public CellFormat borderRight(Color color,float width,float ... dash){
        borderRightColor = color;
        borderRightWidth = width;
        borderRightDash = dash;
        return this;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderTop">
    //<editor-fold defaultstate="collapsed" desc="borderTopColor : Color">
    protected Color   borderTopColor;

    /**
     * Возвращает цвет верхнего бордюра
     * @return цвет
     */
    public Color getBorderTopColor() { return borderTopColor; }

    /**
     * Устанавливает цвет верхнего бордюра
     * @param color цвет
     */
    public void setBorderTopColor(Color color) { this.borderTopColor = color; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderTopWidth : float">
    protected float   borderTopWidth;

    /**
     * Возвращает ширину верхнего бордюра
     * @return ширина
     */
    public float getBorderTopWidth() { return borderTopWidth; }

    /**
     * Устанавливает ширину верхнего бордюра
     * @param width ширина
     */
    public void setBorderTopWidth(float width) { this.borderTopWidth = width; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderTopDash : float[]">
    protected float[] borderTopDash;

    /**
     * Паттерн бордюра - врехняя сторона
     * @return паттерн бордюра
     */
    //TODO @UiBean(propertyEditor = DashEditor.class)
    public float[] getBorderTopDash(){
        if( borderTopDash==null )return new float[]{};
        return borderTopDash;
    }

    /**
     * Паттерн бордюра - врехняя сторона
     * @param dash паттерн бордюра
     */
    public void setBorderTopDash(float[] dash){
        borderTopDash = dash;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderTop()">
    /**
     * Устанавливает верхнюю границу бордюра (рамки)
     * @param color цвет
     * @param width ширина
     * @param dash паттерн
     * @return self ссылка
     */
    public CellFormat borderTop(Color color,float width,float ... dash){
        borderTopColor = color;
        borderTopWidth = width;
        borderTopDash = dash;
        return this;
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderBottom">
    //<editor-fold defaultstate="collapsed" desc="borderBottomColor : Color">
    protected Color   borderBottomColor;

    /**
     * Возвращает цвет нижнего бордюра (рамки)
     * @return цвет нижнего бордюра (рамки)
     */
    public Color getBorderBottomColor() { return borderBottomColor; }

    /**
     * Устанавливает цвет нижнего бордюра (рамки)
     * @param color цвет нижнего бордюра (рамки)
     */
    public void setBorderBottomColor(Color color) { this.borderBottomColor = color; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderBottomWidth : float">
    protected float   borderBottomWidth;

    /**
     * Возвращает ширину нижнего бордюра (рамки)
     * @return ширина
     */
    public float getBorderBottomWidth() { return borderBottomWidth; }

    /**
     * Устанавливает нижнего бордюра (рамки)
     * @param width ширина
     */
    public void setBorderBottomWidth(float width) { this.borderBottomWidth = width; }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderBottomDash : float[]">
    protected float[] borderBottomDash;

    /**
     * Возвращает паттерн нижнего бордюра (рамки)
     * @return паттерн
     */
    //TODO @UiBean(propertyEditor = DashEditor.class)
    public float[] getBorderBottomDash(){
        if( borderBottomDash==null )return new float[]{};
        return borderBottomDash;
    }

    /**
     * Устанавливает паттерн нижнего бордюра (рамки)
     * @param dash паттерн
     */
    public void setBorderBottomDash(float[] dash){
        borderBottomDash = dash;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="borderBottom()">
    /**
     * Устанавливает нижнию границу бордюра (рамки)
     * @param color цвет нижнего бордюра (рамки)
     * @param width ширина
     * @param dash паттерн
     * @return self ссылка
     */
    public CellFormat borderBottom(Color color,float width,float ... dash){
        borderBottomColor = color;
        borderBottomWidth = width;
        borderBottomDash = dash;
        return this;
    }
    //</editor-fold>
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="applyBorder()">
    /**
     * Применяет настройки бардюра
     * @param lbr рендер бордюра
     */
    public void applyBorder(LineBorderRender lbr){
        if( lbr==null )return;

        CellFormat cf = this;
        LineBorderRender lineBorderRender = lbr;

        lineBorderRender.left(cf.getBorderLeftColor(), cf.getBorderLeftWidth(), cf.getBorderLeftDash());
        lineBorderRender.right(cf.getBorderRightColor(), cf.getBorderRightWidth(), cf.getBorderRightDash());
        lineBorderRender.top(cf.getBorderTopColor(), cf.getBorderTopWidth(), cf.getBorderTopDash());
        lineBorderRender.bottom(cf.getBorderBottomColor(), cf.getBorderBottomWidth(), cf.getBorderBottomDash());
    }
    //</editor-fold>
}
