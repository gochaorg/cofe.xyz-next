/*
 * The MIT License
 *
 * Copyright 2016 nt.gocha@gmail.com.
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

package xyz.cofe.gui.swing.color;


import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Цвета
 * <table border="1" cellpadding="2" cellspacing="0" summary="Таблица цветов">
 *   <tr>
 *     <td>HUE</td><td>Saturation 100%</td><td>Saturation 80%</td><td>Saturation 50%</td>
 *   </tr>
 *   <tr>
 *     <td>0</td>
 *     <td style="background-color:rgb(255, 0, 0)">rgb(255, 0, 0)</td>
 *     <td style="background-color:rgb(255, 51,  51)">rgb(255, 51,  51)</td>
 *     <td style="background-color:rgb(255, 128, 128)">rgb(255, 128, 128)</td>
 *   </tr>
 *   <tr>
 *     <td>15</td>
 *     <td style="background-color:rgb(255, 64,   0)">rgb(255, 64,   0)</td>
 *     <td style="background-color:rgb(255, 102, 51)">rgb(255, 102, 51)</td>
 *     <td style="background-color:rgb(255, 159, 128)">rgb(255, 159, 128)</td>
 *   </tr>
 *   <tr>
 *     <td>30</td>
 *     <td style="background-color:rgb(255, 128, 0)">rgb(255, 128, 0)</td>
 *     <td style="background-color:rgb(255, 153, 51)">rgb(255, 153, 51)</td>
 *     <td style="background-color:rgb(255, 191, 128)">rgb(255, 191, 128)</td>
 *   </tr>
 *   <tr>
 *     <td>45</td>
 *     <td style="background-color:rgb(255, 191, 0)">rgb(255, 191, 0)</td>
 *     <td style="background-color:rgb(255, 204, 51)">rgb(255, 204, 51)</td>
 *     <td style="background-color:rgb(255, 223, 128)">rgb(255, 223, 128)</td>
 *   </tr>
 *   <tr>
 *     <td>60</td>
 *     <td style="background-color:rgb(255, 255, 0)">rgb(255, 255, 0)</td>
 *     <td style="background-color:rgb(255, 255, 51)">rgb(255, 255, 51)</td>
 *     <td style="background-color:rgb(255, 255, 128)">rgb(255, 255, 128)</td>
 *   </tr>
 *   <tr>
 *     <td>75</td>
 *     <td style="background-color:rgb(191, 255, 0)">rgb(191, 255, 0)</td>
 *     <td style="background-color:rgb(204, 255, 51)">rgb(204, 255, 51)</td>
 *     <td style="background-color:rgb(223, 255, 128)">rgb(223, 255, 128)</td>
 *   </tr>
 *   <tr>
 *     <td>90</td>
 *     <td style="background-color:rgb(128, 255, 0)">rgb(128, 255, 0)</td>
 *     <td style="background-color:rgb(153, 255, 51)">rgb(153, 255, 51)</td>
 *     <td style="background-color:rgb(191, 255, 128)">rgb(191, 255, 128)</td>
 *   </tr>
 *   <tr>
 *     <td>105</td>
 *     <td style="background-color:rgb(64, 255, 0)">rgb(64, 255, 0)</td>
 *     <td style="background-color:rgb(102, 255, 51)">rgb(102, 255, 51)</td>
 *     <td style="background-color:rgb(159, 255, 128)">rgb(159, 255, 128)</td>
 *   </tr>
 *   <tr>
 *     <td>120</td>
 *     <td style="background-color:rgb(0, 255, 0)">rgb(0, 255, 0)</td>
 *     <td style="background-color:rgb(51, 255, 51)">rgb(51, 255, 51)</td>
 *     <td style="background-color:rgb(128, 255, 128)">rgb(128, 255, 128)</td>
 *   </tr>
 *   <tr>
 *     <td>135</td>
 *     <td style="background-color:rgb(0, 255, 64)">rgb(0, 255, 64)</td>
 *     <td style="background-color:rgb(51, 255, 102)">rgb(51, 255, 102)</td>
 *     <td style="background-color:rgb(128, 255, 159)">rgb(128, 255, 159)</td>
 *   </tr>
 *   <tr>
 *     <td>150</td>
 *     <td style="background-color:rgb(0, 255, 128)">rgb(0, 255, 128)</td>
 *     <td style="background-color:rgb(51, 255, 153)">rgb(51, 255, 153)</td>
 *     <td style="background-color:rgb(128, 255, 191)">rgb(128, 255, 191)</td>
 *   </tr>
 *   <tr>
 *     <td>165</td>
 *     <td style="background-color:rgb(0, 255, 191)">rgb(0, 255, 191)</td>
 *     <td style="background-color:rgb(51, 255, 204)">rgb(51, 255, 204)</td>
 *     <td style="background-color:rgb(128, 255, 223)">rgb(128, 255, 223)</td>
 *   </tr>
 *   <tr>
 *     <td>180</td>
 *     <td style="background-color:rgb(0, 255, 255)">rgb(0, 255, 255)</td>
 *     <td style="background-color:rgb(51, 255, 255)">rgb(51, 255, 255)</td>
 *     <td style="background-color:rgb(128, 255, 255)">rgb(128, 255, 255)</td>
 *   </tr>
 *   <tr>
 *     <td>195</td>
 *     <td style="background-color:rgb(0, 191, 255)">rgb(0, 191, 255)</td>
 *     <td style="background-color:rgb(51, 204, 255)">rgb(51, 204, 255)</td>
 *     <td style="background-color:rgb(128, 223, 255)">rgb(128, 223, 255)</td>
 *   </tr>
 *   <tr>
 *     <td>210</td>
 *     <td style="background-color:rgb(0, 128, 255)">rgb(0, 128, 255)</td>
 *     <td style="background-color:rgb(51, 153, 255)">rgb(51, 153, 255)</td>
 *     <td style="background-color:rgb(128, 191, 255)">rgb(128, 191, 255)</td>
 *   </tr>
 *   <tr>
 *     <td>225</td>
 *     <td style="background-color:rgb(0, 64, 255)">rgb(0, 64, 255)</td>
 *     <td style="background-color:rgb(51, 102, 255)">rgb(51, 102, 255)</td>
 *     <td style="background-color:rgb(128, 159, 255)">rgb(128, 159, 255)</td>
 *   </tr>
 *   <tr>
 *     <td>240</td>
 *     <td style="background-color:rgb(0, 0, 255)">rgb(0, 0, 255)</td>
 *     <td style="background-color:rgb(51, 51, 255)">rgb(51, 51, 255)</td>
 *     <td style="background-color:rgb(128, 128, 255)">rgb(128, 128, 255)</td>
 *   </tr>
 *   <tr>
 *     <td>255</td>
 *     <td style="background-color:rgb(64, 0, 255)">rgb(64, 0, 255)</td>
 *     <td style="background-color:rgb(102, 51, 255)">rgb(102, 51, 255)</td>
 *     <td style="background-color:rgb(159, 128, 255)">rgb(159, 128, 255)</td>
 *   </tr>
 *   <tr>
 *     <td>270</td>
 *     <td style="background-color:rgb(127, 0, 255)">rgb(127, 0, 255)</td>
 *     <td style="background-color:rgb(153, 51, 255)">rgb(153, 51, 255)</td>
 *     <td style="background-color:rgb(191, 128, 255)">rgb(191, 128, 255)</td>
 *   </tr>
 *   <tr>
 *     <td>285</td>
 *     <td style="background-color:rgb(191, 0, 255)">rgb(191, 0, 255)</td>
 *     <td style="background-color:rgb(204, 51, 255)">rgb(204, 51, 255)</td>
 *     <td style="background-color:rgb(223, 128, 255)">rgb(223, 128, 255)</td>
 *   </tr>
 *   <tr>
 *     <td>300</td>
 *     <td style="background-color:rgb(255, 0, 255)">rgb(255, 0, 255)</td>
 *     <td style="background-color:rgb(255, 51, 255)">rgb(255, 51, 255)</td>
 *     <td style="background-color:rgb(255, 128, 255)">rgb(255, 128, 255)</td>
 *   </tr>
 *   <tr>
 *     <td>315</td>
 *     <td style="background-color:rgb(255, 0, 191)">rgb(255, 0, 191)</td>
 *     <td style="background-color:rgb(255, 51, 204)">rgb(255, 51, 204)</td>
 *     <td style="background-color:rgb(255, 128, 223)">rgb(255, 128, 223)</td>
 *   </tr>
 *   <tr>
 *     <td>330</td>
 *     <td style="background-color:rgb(255, 0, 127)">rgb(255, 0, 127)</td>
 *     <td style="background-color:rgb(255, 51, 153)">rgb(255, 51, 153)</td>
 *     <td style="background-color:rgb(255, 128, 191)">rgb(255, 128, 191)</td>
 *   </tr>
 *   <tr>
 *     <td>345</td>
 *     <td style="background-color:rgb(255, 0, 64)">rgb(255, 0, 64)</td>
 *     <td style="background-color:rgb(255, 51, 102)">rgb(255, 51, 102)</td>
 *     <td style="background-color:rgb(255, 128, 159)">rgb(255, 128, 159)</td>
 *   </tr>
 * </table>
 * @author nt.gocha@gmail.com
 */
public class Colors {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(Colors.class.getName());
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

    //<editor-fold defaultstate="collapsed" desc="hue 0">
    /**
     * <span style="background-color:rgb(255, 0,   0)"  >hsb:0/100/100</span>
     * <span style="background-color:rgb(255, 51,  51)" >hsb:0/80/100</span>
     * <span style="background-color:rgb(255, 128, 128)">hsb:0/50/100</span>
     */
    public static Color HUE_0   = Color.getHSBColor(0,          1f, 1f);

    /**
     * <span style="background-color:rgb(255, 0,   0)"  >hsb:0/100/100</span>
     * <span style="background-color:rgb(255, 51,  51)" >hsb:0/80/100</span>
     * <span style="background-color:rgb(255, 128, 128)">hsb:0/50/100</span>
     */
    public static Color HUE_000 = Color.getHSBColor(0,          1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 15">
    /**
     * <span style="background-color:rgb(255, 64,   0)" >hsb:15/100/100</span>
     * <span style="background-color:rgb(255, 102, 51)" >hsb:15/80/100</span>
     * <span style="background-color:rgb(255, 159, 128)">hsb:15/50/100</span>
     */
    public static Color HUE_15  = Color.getHSBColor(15f/360f,   1f, 1f);

    /**
     * <span style="background-color:rgb(255, 64,   0)" >hsb:15/100/100</span>
     * <span style="background-color:rgb(255, 102, 51)" >hsb:15/80/100</span>
     * <span style="background-color:rgb(255, 159, 128)">hsb:15/50/100</span>
     */
    public static Color HUE_015 = Color.getHSBColor(15f/360f,   1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 30">
    /**
     * <span style="background-color:rgb(255, 128, 0)" >hsb:30/100/100</span>
     * <span style="background-color:rgb(255, 153, 51)" >hsb:30/80/100</span>
     * <span style="background-color:rgb(255, 191, 128)">hsb:30/50/100</span>
     */
    public static Color HUE_30  = Color.getHSBColor(30f/360f,   1f, 1f);

    /**
     * <span style="background-color:rgb(255, 128, 0)" >hsb:30/100/100</span>
     * <span style="background-color:rgb(255, 153, 51)" >hsb:30/80/100</span>
     * <span style="background-color:rgb(255, 191, 128)">hsb:30/50/100</span>
     */
    public static Color HUE_030 = Color.getHSBColor(30f/360f,   1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 45">
    /**
     * <span style="background-color:rgb(255, 191, 0)" >hsb:45/100/100</span>
     * <span style="background-color:rgb(255, 204, 51)" >hsb:45/80/100</span>
     * <span style="background-color:rgb(255, 223, 128)">hsb:45/50/100</span>
     */
    public static Color HUE_45  = Color.getHSBColor(45f/360f,   1f, 1f);

    /**
     * <span style="background-color:rgb(255, 191, 0)" >hsb:45/100/100</span>
     * <span style="background-color:rgb(255, 204, 51)" >hsb:45/80/100</span>
     * <span style="background-color:rgb(255, 223, 128)">hsb:45/50/100</span>
     */
    public static Color HUE_045 = Color.getHSBColor(45f/360f,   1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 60">
    /**
     * <span style="background-color:rgb(255, 255, 0)" >hsb:60/100/100</span>
     * <span style="background-color:rgb(255, 255, 51)" >hsb:60/80/100</span>
     * <span style="background-color:rgb(255, 255, 128)">hsb:60/50/100</span>
     */
    public static Color HUE_60  = Color.getHSBColor(60f/360f,   1f, 1f);

    /**
     * <span style="background-color:rgb(255, 255, 0)" >hsb:60/100/100</span>
     * <span style="background-color:rgb(255, 255, 51)" >hsb:60/80/100</span>
     * <span style="background-color:rgb(255, 255, 128)">hsb:60/50/100</span>
     */
    public static Color HUE_060 = Color.getHSBColor(60f/360f,   1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 75">
    /**
     * <span style="background-color:rgb(191, 255, 0)" >hsb:75/100/100</span>
     * <span style="background-color:rgb(204, 255, 51)" >hsb:75/80/100</span>
     * <span style="background-color:rgb(223, 255, 128)">hsb:75/50/100</span>
     */
    public static Color HUE_75  = Color.getHSBColor(75f/360f,   1f, 1f);

    /**
     * <span style="background-color:rgb(191, 255, 0)" >hsb:75/100/100</span>
     * <span style="background-color:rgb(204, 255, 51)" >hsb:75/80/100</span>
     * <span style="background-color:rgb(223, 255, 128)">hsb:75/50/100</span>
     */
    public static Color HUE_075 = Color.getHSBColor(75f/360f,   1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 90">
    /**
     * <span style="background-color:rgb(128, 255, 0)" >hsb:90/100/100</span>
     * <span style="background-color:rgb(153, 255, 51)" >hsb:90/80/100</span>
     * <span style="background-color:rgb(191, 255, 128)">hsb:90/50/100</span>
     */
    public static Color HUE_90  = Color.getHSBColor(90f/360f,   1f, 1f);

    /**
     * <span style="background-color:rgb(128, 255, 0)" >hsb:90/100/100</span>
     * <span style="background-color:rgb(153, 255, 51)" >hsb:90/80/100</span>
     * <span style="background-color:rgb(191, 255, 128)">hsb:90/50/100</span>
     */
    public static Color HUE_090 = Color.getHSBColor(90f/360f,   1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 105">
    /**
     * <span style="background-color:rgb(64, 255, 0)" >hsb:105/100/100</span>
     * <span style="background-color:rgb(102, 255, 51)" >hsb:105/80/100</span>
     * <span style="background-color:rgb(159, 255, 128)">hsb:105/50/100</span>
     */
    public static Color HUE_105 = Color.getHSBColor(105f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 120">
    /**
     * <span style="background-color:rgb(0, 255, 0)" >hsb:120/100/100</span>
     * <span style="background-color:rgb(51, 255, 51)" >hsb:120/80/100</span>
     * <span style="background-color:rgb(128, 255, 128)">hsb:120/50/100</span>
     */
    public static Color HUE_120 = Color.getHSBColor(120f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 135">
    /**
     * <span style="background-color:rgb(0, 255, 64)" >hsb:135/100/100</span>
     * <span style="background-color:rgb(51, 255, 102)" >hsb:135/80/100</span>
     * <span style="background-color:rgb(128, 255, 159)">hsb:135/50/100</span>
     */
    public static Color HUE_135 = Color.getHSBColor(135f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 150">
    /**
     * <span style="background-color:rgb(0, 255, 128)" >hsb:150/100/100</span>
     * <span style="background-color:rgb(51, 255, 153)" >hsb:150/80/100</span>
     * <span style="background-color:rgb(128, 255, 191)">hsb:150/50/100</span>
     */
    public static Color HUE_150 = Color.getHSBColor(150f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 165">
    /**
     * <span style="background-color:rgb(0, 255, 191)" >hsb:165/100/100</span>
     * <span style="background-color:rgb(51, 255, 204)" >hsb:165/80/100</span>
     * <span style="background-color:rgb(128, 255, 223)">hsb:165/50/100</span>
     */
    public static Color HUE_165 = Color.getHSBColor(165f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 180">
    /**
     * <span style="background-color:rgb(0, 255, 255)" >hsb:180/100/100</span>
     * <span style="background-color:rgb(51, 255, 255)" >hsb:180/80/100</span>
     * <span style="background-color:rgb(128, 255, 255)">hsb:180/50/100</span>
     */
    public static Color HUE_180 = Color.getHSBColor(180f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 195">
    /**
     * <span style="background-color:rgb(0, 191, 255)" >hsb:195/100/100</span>
     * <span style="background-color:rgb(51, 204, 255)" >hsb:195/80/100</span>
     * <span style="background-color:rgb(128, 223, 255)">hsb:195/50/100</span>
     */
    public static Color HUE_195 = Color.getHSBColor(195f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 210">
    /**
     * <span style="background-color:rgb(0, 128, 255)" >hsb:210/100/100</span>
     * <span style="background-color:rgb(51, 153, 255)" >hsb:210/80/100</span>
     * <span style="background-color:rgb(128, 191, 255)">hsb:210/50/100</span>
     */
    public static Color HUE_210 = Color.getHSBColor(210f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 225">
    /**
     * <span style="background-color:rgb(0, 64, 255)" >hsb:225/100/100</span>
     * <span style="background-color:rgb(51, 102, 255)" >hsb:225/80/100</span>
     * <span style="background-color:rgb(128, 159, 255)">hsb:225/50/100</span>
     */
    public static Color HUE_225 = Color.getHSBColor(225f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 240">
    /**
     * <span style="background-color:rgb(0, 0, 255)" >hsb:240/100/100</span>
     * <span style="background-color:rgb(51, 51, 255)" >hsb:240/80/100</span>
     * <span style="background-color:rgb(128, 128, 255)">hsb:240/50/100</span>
     */
    public static Color HUE_240 = Color.getHSBColor(240f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 255">
    /**
     * <span style="background-color:rgb(64, 0, 255)" >hsb:255/100/100</span>
     * <span style="background-color:rgb(102, 51, 255)" >hsb:255/80/100</span>
     * <span style="background-color:rgb(159, 128, 255)">hsb:255/50/100</span>
     */
    public static Color HUE_255 = Color.getHSBColor(255f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 270">
    /**
     * <span style="background-color:rgb(127, 0, 255)" >hsb:270/100/100</span>
     * <span style="background-color:rgb(153, 51, 255)" >hsb:270/80/100</span>
     * <span style="background-color:rgb(191, 128, 255)">hsb:270/50/100</span>
     */
    public static Color HUE_270 = Color.getHSBColor(270f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 285">
    /**
     * <span style="background-color:rgb(191, 0, 255)" >hsb:285/100/100</span>
     * <span style="background-color:rgb(204, 51, 255)" >hsb:285/80/100</span>
     * <span style="background-color:rgb(223, 128, 255)">hsb:285/50/100</span>
     */
    public static Color HUE_285 = Color.getHSBColor(285f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 300">
    /**
     * <span style="background-color:rgb(255, 0, 255)" >hsb:300/100/100</span>
     * <span style="background-color:rgb(255, 51, 255)" >hsb:300/80/100</span>
     * <span style="background-color:rgb(255, 128, 255)">hsb:300/50/100</span>
     */
    public static Color HUE_300 = Color.getHSBColor(300f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 315">
    /**
     * <span style="background-color:rgb(255, 0, 191)" >hsb:315/100/100</span>
     * <span style="background-color:rgb(255, 51, 204)" >hsb:315/80/100</span>
     * <span style="background-color:rgb(255, 128, 223)">hsb:315/50/100</span>
     */
    public static Color HUE_315 = Color.getHSBColor(315f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 330">
    /**
     * <span style="background-color:rgb(255, 0, 127)" >hsb:15/100/100</span>
     * <span style="background-color:rgb(255, 51, 153)" >hsb:15/80/100</span>
     * <span style="background-color:rgb(255, 128, 191)">hsb:15/50/100</span>
     */
    public static Color HUE_330 = Color.getHSBColor(330f/360f,  1f, 1f);
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="hue 345">
    /**
     * <span style="background-color:rgb(255, 0, 64)" >hsb:15/100/100</span>
     * <span style="background-color:rgb(255, 51, 102)" >hsb:15/80/100</span>
     * <span style="background-color:rgb(255, 128, 159)">hsb:15/50/100</span>
     */
    public static Color HUE_345 = Color.getHSBColor(345f/360f,  1f, 1f);
//</editor-fold>
}
