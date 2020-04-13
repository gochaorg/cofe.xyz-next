/*
 * The MIT License
 *
 * Copyright 2017 user.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package xyz.cofe.text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Формат десятичного числа. <br>
 * Синтаксис формата: <br>
 * <div style="padding-left:15mm; margin-bottom: 15px;">
 * <i>Знак</i><sub>опц</sub> <i>Целая_часть</i> <i>Вторая_часть</i><sub>опц</sub>
 * </div>
 *
 * Целая_часть: <br> <div style="padding-left:15mm; margin-bottom: 15px;">
 * <i>Числовая_группа</i><sub>повтор 1+</sub>
 * </div>
 *
 * Знак: одно из значений<br>
 * <div style="padding-left:15mm; margin-bottom: 15px;">
 *      Плюс
 *         <div style="color:#666666; padding-left: 15mm;">
 * - Для положит. числел будет добавляться плюс<br>
 * - Для отрицательных числел будет добавляться минус<br>
 * - Для нуля будет добавляться пробел
 * </div>
 * </div>
 *
 * Числовая_группа: одно из значений<br>
 * <div style="padding-left:15mm; margin-bottom: 15px;">
 *      Решетка
 *         <div style="color:#666666; padding-left: 15mm;">используется в качестве подстановки цифры или пробела</div>
 *      Ноль
 *         <div style="color:#666666; padding-left: 15mm;">используется в качестве подстановки цифры</div>
 *      Пробел
 *         <div style="color:#666666; padding-left: 15mm;">разделитель группы</div>
 * </div>
 *
 * Вторая_часть: <br> <div style="padding-left:15mm; margin-bottom: 15px;">
 * <i>Разделитель</i><sub>опц</sub>
 * <i>Числовая_группа</i><sub>повтор 1+</sub>
 * <i>Фактор</i><sub>опц</sub>
 * </div>
 *
 * Разделитель:  одно из значений <div style="padding-left:15mm; margin-bottom: 15px;">
 *      Запятая
 *         <div style="color:#666666; padding-left: 15mm;">использование запятой в качестве десятичной точки</div>
 *      Точка
 *         <div style="color:#666666; padding-left: 15mm;">использование точки в качестве десятичной точки</div>
 * </div>
 *
 * Фактор: <br> <div style="padding-left:15mm; margin-bottom: 15px;">
 * * <i>Знак2</i><sub>опц</sub>
 *   <i>Множитель</i>
 *   <i>Сдвиг</i><sub>опц</sub>
 * </div>
 *
 * Сдвиг: <br> <div style="padding-left:15mm; margin-bottom: 15px;">
 *   <i>Знак2</i>
 *   <i>Число</i>
 * </div>
 *
 * Знак2: одно из значений<br>
 * <div style="padding-left:15mm; margin-bottom: 15px;">
 *      Плюс
 *         <div style="color:#666666; padding-left: 15mm;">Для сложения сдвига</div>
 *      Минус
 *         <div style="color:#666666; padding-left: 15mm;">Для вычитания сдвига</div>
 * </div>
 *
 * <br>
 * Пример форматов:
 * <table>
 *     <caption>Описание формата чисел</caption>
 * <tr>
 * <td>+00000000.000</td>
 * <td>12.23456</td>
 * <td><span
 *  style="background-color:#eeeeee;letter-spacing:2px;">"+00012345.235"</span></td>
 *
 * </tr><tr>
 *
 * <td>00000000.000</td>
 * <td>12.23456</td>
 * <td><span style="background-color:#eeeeee">"00012345.235"</span></td>
 * </tr><tr>
 *
 * <td>### ###.## ##</td>
 * <td>1234567.2345678</td>
 * <td><span style="background-color:#eeeeee">"1 234 567.23 45 67 8"</span></td>
 *
 * </tr><tr>
 *
 * <td>+###000.00</td>
 * <td>12.23456</td>
 * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;&nbsp;+012.23"</span></td>
 *
 * </tr><tr>
 *
 * <td>###000.0#</td>
 * <td>12.23456</td>
 * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;&nbsp;012.23"</span></td>
 *
 * </tr><tr>
 *
 * <td>###000.00####</td>
 * <td>12.2345678</td>
 * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;&nbsp;012.234568"</span></td>
 *
 * </tr><tr>
 *
 * <td>###000.00####</td>
 * <td>12.2345</td>
 * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;&nbsp;012.2345&nbsp;&nbsp;"</span></td>
 *
 * </tr><tr>
 *
 * <td>###000.00####</td>
 * <td>12.23</td>
 * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;&nbsp;012.23&nbsp;&nbsp;&nbsp;&nbsp;"</span></td>
 *
 * </tr><tr>
 *
 * <td>###000.00####</td>
 * <td>12.2</td>
 * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;&nbsp;012.20&nbsp;&nbsp;&nbsp;&nbsp;"</span></td>
 *
 * </tr><tr>
 *
 * <td>#</td>
 * <td>1234.2345</td>
 * <td><span style="background-color:#eeeeee">"1234"</span></td>
 *
 * </tr><tr>
 *
 * <td>+###000,00####</td>
 * <td>1234.2345</td>
 * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;+1234,2345&nbsp;&nbsp;"</span></td>
 *
 * </tr><tr>
 *
 * <td>+###000,00####</td>
 * <td>0</td>
 * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"</span></td>
 *
 * </tr><tr>
 *
 * <td>+###000,00####*100</td>
 * <td>12</td>
 * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;+1200&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"</span></td>
 *
 * </tr><tr>
 *
 * <td>+###000,00####*100+2</td>
 * <td>12</td>
 * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;+1202&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"</span></td>
 *
 * </tr><tr>
 *
 * <td>+###000,00####*-5.5-2.1</td>
 * <td>12</td>
 * <td><span style="background-color:#eeeeee">"&nbsp;&nbsp;&nbsp;&nbsp;-68.1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"</span></td>
 *
 * </tr>
 *
 * </table>
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class FullDecFormat {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(FullDecFormat.class.getName());
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
        logger.entering(FullDecFormat.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(FullDecFormat.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(FullDecFormat.class.getName(), method, result);
    }
    //</editor-fold>

    public FullDecFormat(){
    }
    public FullDecFormat(FullDecFormat sample){
        if( sample!=null ){
            assign(sample);
        }
    }

    public FullDecFormat(String format){
        FullDecFormat sample = create(format);
        if( sample==null ){
            throw new IllegalArgumentException("format == null");
        }
        assign(sample);
    }

    public synchronized void assign( FullDecFormat sample ){
        if( sample!=null ){
            synchronized( sample ){
                intZeroDigits = sample.intZeroDigits;
                intGroupSize = sample.intGroupSize;
                intGroupDelimiter = sample.intGroupDelimiter;

                floatPoint = sample.floatPoint;

                floatZeroDigits = sample.floatZeroDigits;
                floatGroupSize = sample.floatGroupSize;
                floatGroupDelimiter = sample.floatGroupDelimiter;

                forceFloatPoint = sample.forceFloatPoint;

                positiveInfinity = sample.positiveInfinity;
                negativeInfinity = sample.negativeInfinity;
                nan = sample.nan;
                nullValue = sample.nullValue;

                positiveSign = sample.positiveSign;
                negativeSign = sample.negativeSign;
                zeroSign = sample.zeroSign;

                intWidth = sample.intWidth; intAlign = sample.intAlign; intPad = sample.intPad;
                signIntWidth = sample.signIntWidth; signIntAlign = sample.signIntAlign; signIntPad = sample.signIntPad;
                width = sample.width; align = sample.align; pad = sample.pad;

                floatWidth = sample.floatWidth; floatPad = sample.floatPad; floatAlign = sample.floatAlign;

                multiplier = sample.multiplier; addition = sample.addition;
            }
        }
    }

    @Override
    public synchronized FullDecFormat clone(){
        return new FullDecFormat(this);
    }

    //<editor-fold defaultstate="collapsed" desc="intZeroDigits">
    private int intZeroDigits = 0;

    public synchronized int getIntZeroDigits() {
        return intZeroDigits;
    }

    public synchronized void setIntZeroDigits(int intZeroDigits) {
        this.intZeroDigits = intZeroDigits;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="intGroupSize">
    private int intGroupSize= -1;
    public synchronized int getIntGroupSize() {
        return intGroupSize;
    }

    public synchronized void setIntGroupSize(int intGroupSize) {
        this.intGroupSize = intGroupSize;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="intGroupDelimiter">
    private String intGroupDelimiter = "`";
    public synchronized String getIntGroupDelimiter() {
        return intGroupDelimiter;
    }

    public synchronized void setIntGroupDelimiter(String intGroupDelimiter) {
        this.intGroupDelimiter = intGroupDelimiter;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="floatZeroDigits">
    private int floatZeroDigits = 0;
    public synchronized int getFloatZeroDigits() {
        return floatZeroDigits;
    }
    public synchronized void setFloatZeroDigits(int floatZeroDigits) {
        this.floatZeroDigits = floatZeroDigits;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="limitFloat">
    private boolean limitFloat = true;

    public synchronized boolean isLimitFloat() {
        return limitFloat;
    }

    public synchronized void setLimitFloat(boolean limitFloat) {
        this.limitFloat = limitFloat;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="floatGroupSize">
    private int floatGroupSize= -1;
    public synchronized int getFloatGroupSize() {
        return floatGroupSize;
    }

    public synchronized void setFloatGroupSize(int floatGroupSize) {
        this.floatGroupSize = floatGroupSize;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="floatGroupDelimiter">
    private String floatGroupDelimiter = "`";
    public synchronized String getFloatGroupDelimiter() {
        return floatGroupDelimiter;
    }

    public synchronized void setFloatGroupDelimiter(String floatGroupDelimiter) {
        this.floatGroupDelimiter = floatGroupDelimiter;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="floatPoint">
    private String floatPoint = ".";
    public synchronized String getFloatPoint() {
        return floatPoint;
    }

    public synchronized void setFloatPoint(String floatPoint) {
        this.floatPoint = floatPoint;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="forceFloatPoint">
    private boolean forceFloatPoint = false;

    public synchronized boolean isForceFloatPoint() {
        return forceFloatPoint;
    }

    public synchronized void setForceFloatPoint(boolean forceFloatPoint) {
        this.forceFloatPoint = forceFloatPoint;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="positiveSign">
    private String positiveSign = "";
    public synchronized String getPositiveSign() {
        return positiveSign;
    }

    public synchronized void setPositiveSign(String positiveSign) {
        this.positiveSign = positiveSign;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="zeroSign">
    private String zeroSign = "";
    public synchronized String getZeroSign() {
        return zeroSign;
    }

    public synchronized void setZeroSign(String zeroSign) {
        this.zeroSign = zeroSign;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="negativeSign">
    private String negativeSign = "-";
    public synchronized String getNegativeSign() {
        return negativeSign;
    }

    public synchronized void setNegativeSign(String negativeSign) {
        this.negativeSign = negativeSign;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="nullValue">
    private String nullValue = null;

    public synchronized String getNullValue() {
        return nullValue;
    }

    public synchronized void setNullValue(String nullValue) {
        this.nullValue = nullValue;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="nan">
    private String nan = "NaN";

    public synchronized String getNan() {
        return nan;
    }

    public synchronized void setNan(String nan) {
        this.nan = nan;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="positiveInfinity">
    private String positiveInfinity = "+Inf";

    public synchronized String getPositiveInfinity() {
        return positiveInfinity;
    }

    public synchronized void setPositiveInfinity(String positiveInfinity) {
        this.positiveInfinity = positiveInfinity;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="negativeInfinity">
    private String negativeInfinity = "-Inf";

    public synchronized String getNegativeInfinity() {
        return negativeInfinity;
    }

    public synchronized void setNegativeInfinity(String negativeInfinity) {
        this.negativeInfinity = negativeInfinity;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="intWidth">
    private int intWidth = -1;

    public synchronized int getIntWidth() {
        return intWidth;
    }

    public synchronized void setIntWidth(int intWidth) {
        this.intWidth = intWidth;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="intAlign">
    private Align intAlign = Align.End;

    public synchronized Align getIntAlign() {
        return intAlign;
    }

    public synchronized void setIntAlign(Align intAlign) {
        this.intAlign = intAlign;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="intPad">
    private String intPad = " ";

    public synchronized String getIntPad() {
        return intPad;
    }

    public synchronized void setIntPad(String intPad) {
        this.intPad = intPad;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="floatWidth">
    private int floatWidth = -1;

    public synchronized int getFloatWidth() {
        return floatWidth;
    }

    public synchronized void setFloatWidth(int floatWidth) {
        this.floatWidth = floatWidth;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="floatPad">
    private String floatPad = " ";

    public synchronized String getFloatPad() {
        return floatPad;
    }

    public synchronized void setFloatPad(String floatPad) {
        this.floatPad = floatPad;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="floatAlign">
    private Align floatAlign = Align.End;

    public synchronized Align getFloatAlign() {
        return floatAlign;
    }

    public synchronized void setFloatAlign(Align floatAlign) {
        this.floatAlign = floatAlign;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="signIntWidth">
    private int signIntWidth = -1;

    public synchronized int getSignIntWidth() {
        return signIntWidth;
    }

    public synchronized void setSignIntWidth(int signIntWidth) {
        this.signIntWidth = signIntWidth;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="signIntAlign">
    private Align signIntAlign = Align.End;
    public synchronized Align getSignIntAlign() {
        return signIntAlign;
    }

    public synchronized void setSignIntAlign(Align signIntAlign) {
        this.signIntAlign = signIntAlign;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="signIntPad">
    private String signIntPad = " ";

    public synchronized String getSignIntPad() {
        return signIntPad;
    }

    public synchronized void setSignIntPad(String signIntPad) {
        this.signIntPad = signIntPad;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="width">
    private int width = -1;

    public synchronized int getWidth() {
        return width;
    }

    public synchronized void setWidth(int width) {
        this.width = width;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="align">
    private Align align = Align.End;

    public synchronized Align getAlign() {
        return align;
    }

    public synchronized void setAlign(Align align) {
        this.align = align;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="pad">
    private String pad = " ";

    public synchronized String getPad() {
        return pad;
    }

    public synchronized void setPad(String pad) {
        this.pad = pad;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="multiplier">
    private Double multiplier;

    public synchronized Double getMultiplier() {
        return multiplier;
    }

    public synchronized void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="addition">
    private Double addition;

    public synchronized Double getAddition() {
        return addition;
    }

    public synchronized void setAddition(Double addition) {
        this.addition = addition;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="format(num):String">
    public synchronized String format( Number num ){
        //<editor-fold defaultstate="collapsed" desc="null;nan;pos/neg inf">
        if( num==null )return nullValue;

        if( num instanceof Double ){
            if( ((Double)num).isNaN() )return nan;
            if( ((Double)num).isInfinite() && Objects.equals(num,Double.POSITIVE_INFINITY) )
                return positiveInfinity;
            if( ((Double)num).isInfinite() && Objects.equals(num,Double.NEGATIVE_INFINITY) )
                return negativeInfinity;
        }

        if( num instanceof Float ){
            if( ((Float)num).isNaN() )return nan;
            if( ((Float)num).isInfinite() && Objects.equals(num,Float.POSITIVE_INFINITY) )
                return positiveInfinity;
            if( ((Float)num).isInfinite() && Objects.equals(num,Float.NEGATIVE_INFINITY) )
                return negativeInfinity;
        }
        //</editor-fold>

        if( multiplier!=null ){
            num = num.doubleValue() * multiplier;
        }

        if( addition!=null ){
            num = num.doubleValue() + addition;
        }

        //<editor-fold defaultstate="collapsed" desc="decimal format">
        String dfFloat =
            "###########################################################"
                + "###########################################################";
        if( floatZeroDigits>=0 && limitFloat ){
            int cnt = floatZeroDigits;
            if( floatWidth>0 && floatZeroDigits<floatWidth ){
                cnt = floatWidth;
            }
            dfFloat = Text.repeat("#", cnt);
        }

        DecimalFormat df = new DecimalFormat(
            "#."+dfFloat,
            new DecimalFormatSymbols(Locale.US));
        //</editor-fold>

        String str = df.format(num);
        if( str.startsWith("-") ){str = Text.trimStart(str, "-");}

        String[] strParts = Text.split(str, ".");
        if( strParts.length==1 ){
            strParts = new String[]{ strParts[0], "" };
        }

        StringBuilder intstr = new StringBuilder();
        intstr.append(strParts[0]);

        StringBuilder floatstr = new StringBuilder();
        floatstr.append(strParts[1]);

        //<editor-fold defaultstate="collapsed" desc="int pad zero, group">
        if( intZeroDigits>intstr.length() ){
            int padZero = intZeroDigits - intstr.length();
            for( int ci=0; ci<padZero; ci++ ){
                intstr.insert(0, "0");
            }
        }

        if( intGroupSize>0 && intGroupDelimiter!=null && intGroupDelimiter.length()>0 ){
            StringBuilder sb = new StringBuilder();
            int i = -1;
            for( int ci=(intstr.length()-1); ci>=0; ci-- ){
                i++;
                if( i>0 ){
                    if( intGroupSize==1 ){
                        sb.insert(0,intGroupDelimiter);
                    }else if( (i%intGroupSize)==0 ){
                        sb.insert(0,intGroupDelimiter);
                    }
                    sb.insert(0,intstr.charAt(ci));
                }else{
                    sb.insert(0,intstr.charAt(ci));
                }
            }
            intstr = sb;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="float pad zero, group">
        if( floatZeroDigits>floatstr.length() ){
            int padZero = floatZeroDigits - floatstr.length();
            for( int ci=0; ci<padZero; ci++ ){
                floatstr.append("0");
            }
        }

        if( floatGroupSize>0 && floatGroupDelimiter!=null && floatGroupDelimiter.length()>0 ){
            StringBuilder sb = new StringBuilder();
            int i = -1;
            for( int ci=0; ci<=(floatstr.length()-1); ci++ ){
                i++;
                if( i>0 ){
                    if( floatGroupSize==1 ){
                        sb.append(floatGroupDelimiter);
                    }else if( (i%floatGroupSize)==0 ){
                        sb.append(floatGroupDelimiter);
                    }
                    sb.append(floatstr.charAt(ci));
                }else{
                    sb.append(floatstr.charAt(ci));
                }
            }
            floatstr = sb;
        }

        if( floatWidth>0 && floatWidth>floatstr.length() &&
            floatPad!=null && floatPad.length()>0 &&
            floatAlign!=null
        ){
            int padCnt = floatWidth - floatstr.length();

            int padLeftCnt = padCnt;
            int padRightCnt = 0;

            if( floatAlign==Align.Center ){
                padLeftCnt = padCnt / 2;
                padRightCnt = padCnt - padLeftCnt;
            }else if( floatAlign==Align.End ){
                padRightCnt = padCnt;
                padLeftCnt = 0;
            }

            String padLeft  = Text.align("", Align.Begin, floatPad, padLeftCnt, true);
            String padRight = Text.align("", Align.Begin, floatPad, padRightCnt, true);

            String s = floatstr.toString();
            floatstr.setLength(0);
            floatstr.append(padLeft);
            floatstr.append(s);
            floatstr.append(padRight);
        }
        //</editor-fold>

        if( intWidth>0
            &&  intAlign!=null
            &&  intPad!=null
            &&  intPad.length()>0
            &&  intWidth > intstr.length()
        ){
            String s = Text.align(intstr.toString(), intAlign, intPad, intWidth, true);
            intstr.setLength(0);
            intstr.append(s);
        }

        StringBuilder res = new StringBuilder();
        res.append(intstr);

        if( num.doubleValue() < 0 && negativeSign!=null ){
            res.insert(0, negativeSign);
        }else if( num.doubleValue() > 0 && positiveSign!=null ){
            res.insert(0, positiveSign);
        }else if( zeroSign!=null ) {
            res.insert(0, zeroSign);
        }

        if( signIntWidth>0
            &&  signIntAlign!=null
            &&  signIntPad!=null
            &&  signIntPad.length()>0
            &&  signIntWidth > res.length()
        ){
            String s = Text.align(res.toString(), signIntAlign, signIntPad, signIntWidth, true);
            res.setLength(0);
            res.append(s);
        }

        if( floatPoint!=null ){
            if( forceFloatPoint ){
                res.append(floatPoint);
            }else{
                if( ( floatstr.toString().trim().length()>0 ) ){
                    res.append(floatPoint);
                }else if( floatstr.toString().length()>0 ){
                    res.append(Text.repeat(" ", floatPoint.length()));
                }
            }
        }

        res.append(floatstr);

        //private int width = -1;
        //private Align align = Align.Begin;
        //private String pad = " ";

        if( width>0
            &&  align!=null
            &&  pad!=null
            &&  pad.length()>0
            &&  width > res.length()
        ){
            int padCnt = width - res.length();
            int padLeftCnt = padCnt;
            int padRightCnt = 0;

            if( align==Align.Center ){
                padLeftCnt = padCnt / 2;
                padRightCnt = padCnt - padLeftCnt;
            }else if( align==Align.End ){
                padRightCnt = padCnt;
                padLeftCnt = 0;
            }

            String padLeft  = Text.align("", Align.Begin, pad, padLeftCnt, true);
            String padRight = Text.align("", Align.Begin, pad, padRightCnt, true);

            String s = res.toString();
            res.setLength(0);
            res.append(padLeft);
            res.append(s);
            res.append(padRight);
        }

        return res.toString();
    }
    //</editor-fold>

    // +0`000<25,0`000<25
    private final static Pattern longPtrn =
        Pattern.compile("(?is)^"
            + "(?<sign>\\+)?"
            + "(?<num1>[0\\#` ]+)"
            + "((?<a1><|>|=)(?<ac1>\\d+))?"
            + "("
            + "(?<fpoint>\\,|\\.)"
            + "(?<num2>[0\\#` ]+)"
            + ")?"
            + "((?<a2><|>|=)(?<ac2>\\d+))?"
            //+ "(\\*(?<kof>\\-?\\d+(\\.\\d+)?)((<?shiftOp>\\+|\\-)(?<shiftNum>\\d+(\\.\\d+)?)))?"
            + "(\\*(?<kof>[\\+\\-]?\\d+(\\.\\d+)?)(?<shiftNum>[\\+\\-]\\d+(\\.\\d+)?)?)?"
        );

    private static int countOf( String str, String find ){
        if( find==null || find.length()==0 )return 0;
        if( str==null || str.length()==0 )return 0;

        int from = 0;
        int cnt = 0;
        while( true ){
            int f = str.indexOf(find,from);
            if( f<0 )break;
            from = f + find.length();
            cnt++;
        }

        return cnt;
    }

    // +#`###>25.#`###>25
    public static FullDecFormat create( String format ){
        if( format==null )throw new IllegalArgumentException("format==null");

        FullDecFormat fmt = new FullDecFormat();

        Matcher m = longPtrn.matcher(format);
        if( !m.matches() )return null;

        String kof = m.group("kof");
        String shiftNum = m.group("shiftNum");
        if( kof!=null && kof.length()>0 ){
            fmt.multiplier = Double.parseDouble(kof);
        }
        if( shiftNum!=null && shiftNum.length()>0 ){
            fmt.addition = Double.parseDouble(shiftNum);
        }

        String signIntAlign=m.group("a1");
        String signIntWidth=m.group("ac1");
        String align=m.group("a2");
        String width=m.group("ac2");

        String sign=m.group("sign");
        if( "+".equals(sign) ){
            fmt.setNegativeSign("-");
            fmt.setZeroSign(" ");
            fmt.setPositiveSign("+");
        }else{
            fmt.setNegativeSign("-");
            fmt.setZeroSign("");
            fmt.setPositiveSign("");
        }

        String num1=m.group("num1");

        int cntIntZero = countOf(num1, "0");
        int cntIntDummy = countOf(num1, "#");

        int cntWidthAdd = 0;
        if( "+".equals(sign) ){
            cntWidthAdd++;
        }

        if( cntIntZero>0 && cntIntDummy==0 ){
            fmt.setIntZeroDigits(cntIntZero);
        }else if( cntIntZero==0 && cntIntDummy>0 ){
            fmt.setSignIntWidth(cntIntDummy+cntWidthAdd);
        }else if( cntIntZero>0 && cntIntDummy>0 && (signIntAlign==null || signIntAlign.length()<1) ){
            fmt.setSignIntWidth(cntIntZero+cntIntDummy+cntWidthAdd);
            fmt.setIntZeroDigits(cntIntZero);
        }

        int num1grpStart1 = num1.lastIndexOf("`");
        int num1grpStart2 = num1.lastIndexOf(" ");
        int num1grpStart = Math.max(num1grpStart1, num1grpStart2);

        if( num1grpStart>=0 && num1grpStart<(num1.length()-1) ){
            String grp = num1.substring(num1grpStart+1);
            if( grp.length()>0 ){
                fmt.setIntGroupSize(grp.length());
                fmt.setFloatGroupSize(grp.length());
            }
            if( num1grpStart2 > num1grpStart1 ){
                fmt.setIntGroupDelimiter(" ");
                fmt.setFloatGroupDelimiter(" ");
            }
        }

        String num2=m.group("num2");

        int cntFloatZero = countOf(num2, "0");
        if( cntFloatZero>0 ){
            fmt.setFloatZeroDigits(cntFloatZero);
        }

        int cntFloatDummy = countOf(num2, "#");

        if( cntFloatDummy>0 && cntFloatZero==0 ){
            fmt.setLimitFloat(false);
            //fmt.setFloatZeroDigits(cntFloatDummy);
            fmt.setFloatWidth(cntFloatDummy);
            fmt.setForceFloatPoint(false);
        }else if( cntFloatDummy==0 && cntFloatZero>0 ){
            fmt.setLimitFloat(true);
            fmt.setFloatZeroDigits(cntFloatZero);
            fmt.setFloatWidth(cntFloatZero);
        }else if( cntFloatDummy>0 && cntFloatZero>0 ){
            fmt.setLimitFloat(true);
            int s = cntFloatDummy + cntFloatZero;
            fmt.setFloatZeroDigits(cntFloatZero);
            fmt.setFloatWidth(s);
        }else if( cntFloatDummy==0 && cntFloatZero==0 ){
            fmt.setLimitFloat(true);
            fmt.setFloatZeroDigits(0);
            fmt.setFloatWidth(0);
        }

        int num2grpStart1 = num2==null ? -1 : num2.lastIndexOf("`");
        int num2grpStart2 = num2==null ? -1 : num2.lastIndexOf(" ");
        int num2grpStart = Math.max(num2grpStart1, num2grpStart2);

        if( num2grpStart>=0 && (num2!=null && num2grpStart<(num2.length()-1)) ){
            String grp = num2.substring(num2grpStart+1);
            if( grp.length()>0 ){
                fmt.setFloatGroupSize(grp.length());
            }
            if( num1grpStart2 > num1grpStart1 ){
                fmt.setFloatGroupDelimiter(" ");
            }
        }

        String fpoint=m.group("fpoint");
        if( fpoint!=null && fpoint.length()>0 ){
            fmt.setFloatPoint(fpoint);
        }

        if( signIntAlign!=null && signIntAlign.length()>0 && signIntWidth!=null && signIntWidth.length()>0 ){
            fmt.setSignIntWidth(Integer.parseInt(signIntWidth));
            if( "<".equals(signIntAlign) ){
                fmt.setSignIntAlign(Align.Begin);
            }else if( "=".equals(signIntAlign) ){
                fmt.setSignIntAlign(Align.Center);
            }else if( ">".equals(signIntAlign) ){
                fmt.setSignIntAlign(Align.End);
            }
        }

        if( align!=null && align.length()>0 && width!=null && width.length()>0 ){
            fmt.setWidth(Integer.parseInt(width));
            if( "<".equals(align) ){
                fmt.setAlign(Align.End);
            }else if( "=".equals(align) ){
                fmt.setAlign(Align.Center);
            }else if( ">".equals(align) ){
                fmt.setAlign(Align.Begin);
            }
        }

        return fmt;
    }

    // +#`###.000<25
    // +#`###.000
    // +000`000`000.000

    /*public static FullDecFormat create( String format ){
        if( format==null )throw new IllegalArgumentException("format == null");
    }*/

    /**
     * Парсинг строки согласно формату
     * @param str строка
     * @return Число или nan (если multiplier = 0)
     */
    public synchronized Double parseDouble( String str ){
        if( str==null ){
            throw new IllegalArgumentException("str==null");
        }
        str = str.replaceAll(" +", "");
        Double d = Double.parseDouble(str);

        if( addition!=null ){
            d = d - addition;
        }

        if( multiplier!=null ){
            if( multiplier == 0 ){
                return Double.NaN;
            }
            d = d / multiplier;
        }

        return d;
    }
}
