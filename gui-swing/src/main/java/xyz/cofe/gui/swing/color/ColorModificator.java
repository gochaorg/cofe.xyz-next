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
 * <b>Цветовая модификация.</b> <br>
 * Конечное значение (тон/яркость/насыщенность) описывается тремя значениями:
 * result = source * source_factor + new_value * new_factor.<br>
 * <b>Таблица цветов:</b> <br>
 * newHue оттенок, значение в диапазоне: <br>
 * <div style="background-color:rgb(255, 0, 0); color: #ffffff">
 * 0 - красный, red - middle
 * </div>
 * <br>
 *
 * <div style="background-color:rgb(255, 64, 0); color: #ffffff">
 * 15/360 - красный-теплый, red - warm
 * </div><br>
 *
 * <div style="background-color:rgb(255, 128, 0)">
 * 30/360 - оранжевый, orange
 * </div><br>
 *
 * <div style="background-color:rgb(255, 191, 0)">
 * 45/360 - желтый-теплый, yellow - warm
 * </div><br>
 *
 * <div style="background-color:rgb(255, 255, 0)">
 * 60/360 - желтый, yellow - middle
 * </div><br>
 *
 * <div style="background-color:rgb(191, 255, 0)">
 * 75/360 - желтый-холодный, yellow - cool
 * </div><br>
 *
 * <div style="background-color:rgb(128, 255, 0)">
 * 90/360 - желтый-зеленый
 * </div><br>
 *
 * <div style="background-color:rgb(64, 255, 0)">
 * 105/360 - зеленый-теплый</div><br>
 *
 * <div style="background-color:rgb(0, 255, 0)">
 * 120/360 - зеленый</div><br>
 *
 * <div style="background-color:rgb(0, 255, 64)">
 * 135/360 - зеленый-холодный</div><br>
 *
 * <div style="background-color:rgb(0, 255, 128)">
 * 150/360 - зеленый-циан (сине-зеленый)</div><br>
 *
 * <div style="background-color:rgb(0, 255, 191)">
 * 165/360 - циан-теплый (сине-зеленый) </div><br>
 *
 * <div style="background-color:rgb(0, 255, 255)">
 * 180/360 - циан-средний (сине-зеленый - морская волна) </div><br>
 *
 * <div style="background-color:rgb(0, 191, 255)">
 * 195/360 - циан-холодный (сине-зеленый) </div><br>
 *
 * <div style="background-color:rgb(0, 128, 255); color: #ffffff">
 * 210/360 - циан-синий (синий) </div><br>
 *
 * <div style="background-color:rgb(0, 64, 255); color: #ffffff">
 * 225/360 - синий-холодный </div><br>
 *
 * <div style="background-color:rgb(0, 0, 255); color: #ffffff">
 * 240/360 - синий-средний </div><br>
 *
 * <div style="background-color:rgb(64, 0, 255); color: #ffffff">
 * 255/360 - синий-теплый </div><br>
 *
 * <div style="background-color:rgb(127, 0, 255); color: #ffffff">
 * 270/360 - фиолетовый </div><br>
 *
 * <div style="background-color:rgb(191, 0, 255); color: #ffffff">
 * 285/360 - пурпур </div><br>
 *
 * <div style="background-color:rgb(255, 0, 255); color: #ffffff">
 * 300/360 - фуксия, пурпурный, magenta </div><br>
 *
 * <div style="background-color:rgb(255, 0, 191); color: #ffffff"
 * >315/360 - пурпурный-теплый </div><br>
 *
 * <div style="background-color:rgb(255, 0, 127); color: #ffffff">
 * 330/360 - пурпурный-красный </div><br>
 *
 * <div style="background-color:rgb(255, 0, 64); color: #ffffff">
 * 345/360 - красный - холодный </div><br>
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class ColorModificator {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ColorModificator.class.getName());
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

    public ColorModificator(){
        sourceHueFactor = 1f;
        newHueFactor = 0f;
        newHue = 0f;

        sourceBrightFactor = 1f;
        newBrightFactor = 0f;
        newBright = 0f;

        sourceSaturationFactor = 1f;
        newSaturationFactor = 0f;
        newSaturation = 0f;

        sourceOpacityFactor = 1f;
        newOpacityFactor = 0f;
        newOpacity = 0f;
    }

    public ColorModificator( ColorModificator src ){
        if( src!=null ){
            sourceHueFactor = src.sourceHueFactor;
            newHueFactor = src.newHueFactor;
            newHue = src.newHue;

            sourceBrightFactor = src.sourceBrightFactor;
            newBrightFactor = src.newBrightFactor;
            newBright = src.newBright;

            sourceSaturationFactor = src.sourceSaturationFactor;
            newSaturationFactor = src.newSaturationFactor;
            newSaturation = src.newSaturation;

            sourceOpacityFactor = src.sourceOpacityFactor;
            newOpacityFactor = src.newOpacityFactor;
            newOpacity = src.newOpacity;
        }else{
            sourceHueFactor = 1f;
            newHueFactor = 0f;
            newHue = 0f;

            sourceBrightFactor = 1f;
            newBrightFactor = 0f;
            newBright = 0f;

            sourceSaturationFactor = 1f;
            newSaturationFactor = 0f;
            newSaturation = 0f;

            sourceOpacityFactor = 1f;
            newOpacityFactor = 0f;
            newOpacity = 0f;
        }
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public ColorModificator clone(){
        return new ColorModificator(this);
    }

    //<editor-fold defaultstate="collapsed" desc="hue">
    //<editor-fold defaultstate="collapsed" desc="sourceHueFactor">
    private Float sourceHueFactor;

    public Float getSourceHueFactor() {
        return sourceHueFactor;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="newHueFactor">
    private Float newHueFactor;

    public Float getNewHueFactor() {
        return newHueFactor;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="newHue">
    private Float newHue;

    public Float getNewHue() {
        return newHue;
    }
//</editor-fold>
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bright">
    private Float sourceBrightFactor;
    public Float getSourceBrightFactor() {
        return sourceBrightFactor;
    }

    private Float newBrightFactor;
    public Float getNewBrightFactor() {
        return newBrightFactor;
    }

    private Float newBright;
    public Float getNewBright() {
        return newBright;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="saturation">
    private Float sourceSaturationFactor;
    private Float newSaturationFactor;
    private Float newSaturation;

    public Float getSourceSaturationFactor() {
        return sourceSaturationFactor;
    }

    public Float getNewSaturationFactor() {
        return newSaturationFactor;
    }

    public Float getNewSaturation() {
        return newSaturation;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="opacity">
    private Float sourceOpacityFactor;
    private Float newOpacityFactor;
    private Float newOpacity;

    public Float getSourceOpacityFactor() {
        return sourceOpacityFactor;
    }

    public Float getNewOpacityFactor() {
        return newOpacityFactor;
    }

    public Float getNewOpacity() {
        return newOpacity;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="apply()">
    public Color apply( Color color ){
        if( color==null )throw new IllegalArgumentException( "color==null" );

        float[] hsb_arr = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float s_hue = hsb_arr[0];
        float s_saturation = hsb_arr[1];
        float s_bright = hsb_arr[2];

        float r_hue =
            s_hue * (sourceHueFactor==null ? 0f : sourceHueFactor)
                + (newHue==null ? 0f : newHue) * (newHueFactor==null ? 0f : newHueFactor);

        if( r_hue > 1 ){
            int sub = (int)r_hue;
            r_hue = r_hue - (float)sub;
        }else if( r_hue < 0 ){
            int add_i = (int)(-(r_hue)) + 1;
            r_hue = r_hue + (float)add_i;
        }

        float r_saturation =
            s_saturation * (sourceSaturationFactor==null ? 0f : sourceSaturationFactor)
                + (newSaturation==null ? 0f : newSaturation) * (newSaturationFactor==null ? 0f : newSaturationFactor);

        r_saturation = r_saturation < 0 ? 0 : r_saturation;
        r_saturation = r_saturation > 1 ? 1 : r_saturation;

        float r_bright =
            s_bright * (sourceBrightFactor==null ? 0f : sourceBrightFactor)
                + (newBright==null ? 0f : newBright) * (newBrightFactor==null ? 0f : newBrightFactor);

        r_bright = r_bright < 0 ? 0 : r_bright;
        r_bright = r_bright > 1 ? 1 : r_bright;

        float s_alpha = (float)color.getAlpha() / 255f;

        float r_opacity =
            s_alpha * (sourceOpacityFactor==null ? 0f : sourceOpacityFactor)
                + (newOpacity==null ? 0f : newOpacity) * (newOpacityFactor==null ? 0f : newOpacityFactor);

        r_opacity = r_opacity < 0 ? 0 : r_opacity;
        r_opacity = r_opacity > 1 ? 1 : r_opacity;

        Color col = Color.getHSBColor(r_hue, r_saturation, r_bright);

        int c_red   = col.getRed();
        int c_green = col.getGreen();
        int c_blue  = col.getBlue();
        col = new Color(c_red, c_green, c_blue, (int)(r_opacity * 255f));

        return col;
    }
    //</editor-fold>

    /**
     * Устанавливает абсолютную яркость
     * @param newBright яркость, значение в диапазоне 0 (черный) .. 1 (белый)
     * @return Модификатор
     */
    public ColorModificator bright( float newBright ){
        ColorModificator cm = clone();
        cm.sourceBrightFactor = 0f;
        cm.newBrightFactor = 1f;
        cm.newBright = newBright;
        return cm;
    }

    /**
     * Увеличивает/уменьшает яркость
     * @param addBright На сколько увеличить/уменьшить яркость, значение в диапазоне 0 (черный) .. 1 (белый)
     * @return Модификатор
     */
    public ColorModificator brighter( float addBright ){
        ColorModificator cm = clone();
        cm.sourceBrightFactor = 1f;
        cm.newBrightFactor = 1f;
        cm.newBright = addBright;
        return cm;
    }

    /**
     * Устанавливает абсолютную насыщенность
     * @param newSaturation насыщенность, значение в диапазоне 0 (серый - ахроматические) .. 1 (цветной - хроматические)
     * @return Модификатор
     */
    public ColorModificator saturation( float newSaturation ){
        ColorModificator cm = clone();
        cm.sourceSaturationFactor = 0f;
        cm.newSaturationFactor = 1f;
        cm.newSaturation = newSaturation;
        return cm;
    }

    /**
     * Добавляет насыщенность
     * @param addSaturation насыщенность, значение в диапазоне 0 (серый - ахроматические) .. 1 (цветной - хроматические)
     * @return Модификатор
     */
    public ColorModificator sate( float addSaturation ){
        ColorModificator cm = clone();
        cm.sourceSaturationFactor = 1f;
        cm.newSaturationFactor = 1f;
        cm.newSaturation = addSaturation;
        return cm;
    }

    /**
     * Устанавливает абсолютный оттенок
     * @param newHue оттенок, значение в диапазоне: <br>
     * <div style="background-color:rgb(255, 0, 0); color: #ffffff">
     * 0 - красный, red - middle
     * </div>
     * <br>
     *
     * <div style="background-color:rgb(255, 64, 0); color: #ffffff">
     * 15/360 - красный-теплый, red - warm
     * </div><br>
     *
     * <div style="background-color:rgb(255, 128, 0)">
     * 30/360 - оранжевый, orange
     * </div><br>
     *
     * <div style="background-color:rgb(255, 191, 0)">
     * 45/360 - желтый-теплый, yellow - warm
     * </div><br>
     *
     * <div style="background-color:rgb(255, 255, 0)">
     * 60/360 - желтый, yellow - middle
     * </div><br>
     *
     * <div style="background-color:rgb(191, 255, 0)">
     * 75/360 - желтый-холодный, yellow - cool
     * </div><br>
     *
     * <div style="background-color:rgb(128, 255, 0)">
     * 90/360 - желтый-зеленый
     * </div><br>
     *
     * <div style="background-color:rgb(64, 255, 0)">
     * 105/360 - зеленый-теплый</div><br>
     *
     * <div style="background-color:rgb(0, 255, 0)">
     * 120/360 - зеленый</div><br>
     *
     * <div style="background-color:rgb(0, 255, 64)">
     * 135/360 - зеленый-холодный</div><br>
     *
     * <div style="background-color:rgb(0, 255, 128)">
     * 150/360 - зеленый-циан (сине-зеленый)</div><br>
     *
     * <div style="background-color:rgb(0, 255, 191)">
     * 165/360 - циан-теплый (сине-зеленый) </div><br>
     *
     * <div style="background-color:rgb(0, 255, 255)">
     * 180/360 - циан-средний (сине-зеленый - морская волна) </div><br>
     *
     * <div style="background-color:rgb(0, 191, 255)">
     * 195/360 - циан-холодный (сине-зеленый) </div><br>
     *
     * <div style="background-color:rgb(0, 128, 255); color: #ffffff">
     * 210/360 - циан-синий (синий) </div><br>
     *
     * <div style="background-color:rgb(0, 64, 255); color: #ffffff">
     * 225/360 - синий-холодный </div><br>
     *
     * <div style="background-color:rgb(0, 0, 255); color: #ffffff">
     * 240/360 - синий-средний </div><br>
     *
     * <div style="background-color:rgb(64, 0, 255); color: #ffffff">
     * 255/360 - синий-теплый </div><br>
     *
     * <div style="background-color:rgb(127, 0, 255); color: #ffffff">
     * 270/360 - фиолетовый </div><br>
     *
     * <div style="background-color:rgb(191, 0, 255); color: #ffffff">
     * 285/360 - пурпур </div><br>
     *
     * <div style="background-color:rgb(255, 0, 255); color: #ffffff">
     * 300/360 - фуксия, пурпурный, magenta </div><br>
     *
     * <div style="background-color:rgb(255, 0, 191); color: #ffffff"
     * >315/360 - пурпурный-теплый </div><br>
     *
     * <div style="background-color:rgb(255, 0, 127); color: #ffffff">
     * 330/360 - пурпурный-красный </div><br>
     *
     * <div style="background-color:rgb(255, 0, 64); color: #ffffff">
     * 345/360 - красный - холодный </div><br>
     * @return Модификатор
     */
    public ColorModificator hue( float newHue ){
        ColorModificator cm = clone();
        cm.sourceHueFactor = 0f;
        cm.newHueFactor = 1f;
        cm.newHue = newHue;
        return cm;
    }

    /**
     * Устанавливает абсолютный оттенок
     * @param newHue оттенок (0 .. 360)
     * @return Модификатор
     * @see #hue(float)
     */
    public ColorModificator hue(int newHue){
        return hue( (float)newHue / (float)360.0 );
    }

    /**
     * Смещение тона/оттенка
     * @param rotateHue велечина смещения оттенка
     * @return Модификатор
     */
    public ColorModificator rotate( float rotateHue ){
        ColorModificator cm = clone();
        cm.sourceHueFactor = 1f;
        cm.newHueFactor = 1f;
        cm.newHue = rotateHue;
        return cm;
    }

    /**
     * Смещение тона/оттенка
     * @param rotateHue велечина смещения оттенка
     * @return Модификатор
     */
    public ColorModificator rotate( int rotateHue ){
        return rotate( rotateHue / 360.0F );
    }

    /**
     * Увеличение альфа канала
     * @param addAplha велечина смещения прозрачности: <br>
     * меньше нуля - увеличение прозрачности, <br>
     * больше нуля - увеличение плотности (уменьшение прозрачности), <br>
     * @return Модификатор
     */
    public ColorModificator alpher( float addAplha ){
        ColorModificator cm = clone();
        cm.sourceOpacityFactor = 1f;
        cm.newOpacityFactor = 1f;
        cm.newOpacity = addAplha;
        return cm;
    }

    /**
     * Устанавливает абсолютную плотность
     * @param alpha велечина плотности: 0 - прозрачный ... 1 - плотный <br>
     * @return Модификатор
     */
    public ColorModificator alpha( float alpha ){
        ColorModificator cm = clone();
        cm.sourceOpacityFactor = 0f;
        cm.newOpacityFactor = 1f;
        cm.newOpacity = alpha;
        return cm;
    }
}
