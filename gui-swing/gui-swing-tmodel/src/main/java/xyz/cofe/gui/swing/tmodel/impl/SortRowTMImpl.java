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

package xyz.cofe.gui.swing.tmodel.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Настройки реализации SortRowTM
 * @author Kamnev Georgiy nt.gocha@gmail.com
 */
public class SortRowTMImpl {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(SortRowTMImpl.class.getName());
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
        logger.entering(SortRowTMImpl.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(SortRowTMImpl.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(SortRowTMImpl.class.getName(), method, result);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fitnessOnInsertEach">
    private static volatile Integer fitnessOnInsertEach = null;

    /**
     * Сис. свойство <b>xyz.cofe.gui.swing.tmodel.SortRowTM.fitness.onInsertEach</b> <i>=100</i>.
     * <p>
     *
     * Указывает через сколько вставок проводить проверку на сортировку
     * @return кол-во, 0-не проводить, 1-каждая, 2-каждая вторая
     */
    public static int getFitnessOnInsertEach() {
        if( fitnessOnInsertEach==null ){
            String val =
                System.getProperties().getProperty(
                    "xyz.cofe.gui.swing.tmodel.SortRowTM.fitness.onInsertEach", "100");

            if( val==null || !val.matches("-?\\d+") ){
                fitnessOnInsertEach = 100;
            }else{
                fitnessOnInsertEach = Integer.parseInt(val);
            }
        }
        return fitnessOnInsertEach;
    }

    /**
     * см ссылку
     * @param fitnessOnInsertEach Проверять индекс на кажой N вставке
     * @see #getFitnessOnInsertEach()
     */
    public static void setFitnessOnInsertEach(int fitnessOnInsertEach) {
        SortRowTMImpl.fitnessOnInsertEach = fitnessOnInsertEach;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fitnessOnUpdateEach">
    private static volatile Integer fitnessOnUpdateEach = null;

    /**
     * Сис. свойство <b>xyz.cofe.gui.swing.tmodel.SortRowTM.fitness.onUpdateEach</b> <i>=100</i>.
     * <p>
     *
     * Указывает через сколько обновлений проводить проверку на сортировку
     * @return кол-во, 0-не проводить, 1-каждая, 2-каждая вторая
     */
    public static Integer getFitnessOnUpdateEach() {
        if( fitnessOnUpdateEach==null ){
            String val =
                System.getProperties().getProperty(
                    "xyz.cofe.gui.swing.tmodel.SortRowTM.fitness.onUpdateEach", "100");

            if( val==null || !val.matches("-?\\d+") ){
                fitnessOnUpdateEach = 100;
            }else{
                fitnessOnUpdateEach = Integer.parseInt(val);
            }

            //fitnessOnUpdateEach = 100;
        }
        return fitnessOnUpdateEach;
    }

    public static void setFitnessOnUpdateEach(Integer fitnessOnUpdateEach) {
        SortRowTMImpl.fitnessOnUpdateEach = fitnessOnUpdateEach;
    }
    //</editor-fold>
}
