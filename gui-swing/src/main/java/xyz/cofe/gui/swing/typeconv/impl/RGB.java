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

package xyz.cofe.gui.swing.typeconv.impl;


import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nt.gocha@gmail.com
 */
public class RGB {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(RGB.class.getName());
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

    public static String rgb(Color color)
    {
        if( color==null )return null;

        String sRed = Integer.toHexString(color.getRed());
        String sGreen = Integer.toHexString(color.getGreen());
        String sBlue = Integer.toHexString(color.getBlue());

        if( sRed.length()<2 )sRed = "0" + sRed;
        if( sGreen.length()<2 )sGreen = "0" + sGreen;
        if( sBlue.length()<2 )sBlue = "0" + sBlue;

        return "#"+sRed+sGreen+sBlue;
    }

    public static Color rgb(String rgbValue)
    {
        if( rgbValue==null )return Color.black;

        if( rgbValue.matches("(?i)#[0-9a-f]{6}") )
        {
            String sRedComponent = rgbValue.substring(1, 3).toUpperCase();
            String sGreenComponent = rgbValue.substring(3, 5).toUpperCase();
            String sBlueComponent = rgbValue.substring(5, 7).toUpperCase();

            int iRed = Integer.parseInt(sRedComponent,16);
            int iGreen = Integer.parseInt(sGreenComponent,16);
            int iBlue = Integer.parseInt(sBlueComponent,16);

            return new Color(iRed, iGreen, iBlue);
        }

        return Color.black;
    }
}
