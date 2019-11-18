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

package xyz.cofe.gui.swing.tree;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import xyz.cofe.gui.swing.color.ColorModificator;

/**
 * Настройки формаитрования узла
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TreeTableNodeFormatBasic implements TreeTableNodeFormat {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTableNodeFormatBasic.class.getName());
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
        logger.entering(TreeTableNodeFormatBasic.class.getName(), method, params);
    }

    private static void logExiting(String method){
        logger.exiting(TreeTableNodeFormatBasic.class.getName(), method);
    }

    private static void logExiting(String method, Object result){
        logger.exiting(TreeTableNodeFormatBasic.class.getName(), method, result);
    }
    //</editor-fold>

    private static boolean eq( Object a, Object b ){
        if( a==null && b==null )return true;
        if( a==null && b!=null )return false;
        if( a!=null && b==null )return false;
        return a.equals(b);
    }

    public TreeTableNodeFormatBasic(){
    }

    public TreeTableNodeFormatBasic(TreeTableNodeFormatBasic sample){
        if( sample!=null ){
            this.background = sample.background;
            this.foreground = sample.foreground;

            this.fontFamily = sample.fontFamily;
            this.weight = sample.weight;
            this.italic = sample.italic;
            this.fontSize = sample.fontSize;
            this.iconWidthMin = sample.iconWidthMin;
            this.convertor = sample.convertor;

            if( sample.icons!=null ){
                getIcons().addAll(sample.icons);
            }
        }
    }

    @Override
    public TreeTableNodeFormatBasic clone(){
        return new TreeTableNodeFormatBasic(this);
    }

    @Override
    public AttributedString createAttributedString( String text, Object value ){
        if( text==null )throw new IllegalArgumentException( "text==null" );

        var txtConv = convertor;
        String txt = text.toString();

        if( txtConv!=null ){
            String txtc = txtConv.apply(value);
            if( txtc!=null ){
                txt = txtc;
            }
        }

        AttributedString astr = new AttributedString(txt);
        if( txt.length()>0 ){
            if( fontFamily!=null ){
                astr.addAttribute(TextAttribute.FAMILY, fontFamily);
            }
            if( fontSize!=null ){
                astr.addAttribute(TextAttribute.SIZE, fontSize);
            }
            if( foreground!=null ){
                astr.addAttribute(TextAttribute.FOREGROUND, foreground);
            }
            if( weight!=null ){
                astr.addAttribute(TextAttribute.WEIGHT, weight);
            }
            if( italic!=null ){
                if( italic ){
                    astr.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
                }else{
                    astr.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);
                }
            }
        }
        return astr;
    }

    //<editor-fold defaultstate="collapsed" desc="fontFamily">
    protected String fontFamily;

    @Override
    public String getFontFamily() {
        return fontFamily;
    }

    @Override
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public TreeTableNodeFormatBasic fontFamily(String ffamily){
        setFontFamily(ffamily);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="fontSize">
    protected Float fontSize;

    @Override
    public Float getFontSize() {
        return fontSize;
    }

    @Override
    public void setFontSize(Float fontSize) {
        this.fontSize = fontSize;
    }

    public TreeTableNodeFormatBasic fontSize(Float fsize){
        setFontSize(fsize);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="italic">
    protected Boolean italic;

    @Override
    public Boolean getItalic() {
        return italic;
    }

    @Override
    public void setItalic(Boolean italic) {
        this.italic = italic;
    }

    public TreeTableNodeFormatBasic italic( Boolean italic ){
        this.italic = italic;
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="bold">
    @Override
    public Boolean getBold(){
        if( weight==null )return null;
        if( weight > TextAttribute.WEIGHT_REGULAR )return true;
        return false;
    }

    @Override
    public void setBold( Boolean bold ){
        if( bold==null ){
            weight = null;
            return;
        }

        if( bold ){
            weight = TextAttribute.WEIGHT_BOLD;
        }else{
            weight = TextAttribute.WEIGHT_REGULAR;
        }
    }

    public TreeTableNodeFormatBasic bold(Boolean bld){
        setBold(bld);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="weight">
    protected Float weight;

    @Override
    public Float getWeight() {
        return weight;
    }

    @Override
    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public TreeTableNodeFormatBasic weight(Float w){
        setWeight(w);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="background">
    private Color background;
    @Override public Color getBackground() { return background; }
    @Override public void setBackground(Color background) { this.background = background; }
    public TreeTableNodeFormatBasic background(Color background){
        setBackground(background);
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="foreground">
    private Color foreground;
    @Override
    public Color getForeground() { return foreground; }

    @Override
    public void setForeground(Color foreground) { this.foreground = foreground; }

    public TreeTableNodeFormatBasic foreground(Color fg){
        this.foreground = fg;
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="baseModificator">
    private ColorModificator baseModificator;

    @Override
    public ColorModificator getBaseModificator() {
        return baseModificator;
    }

    @Override
    public void setBaseModificator(ColorModificator baseModificator) {
        this.baseModificator = baseModificator;
    }

    public TreeTableNodeFormatBasic base(ColorModificator baseMod) {
        this.baseModificator = baseMod;
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="foregroundModificator">
    private ColorModificator foregroundModificator;

    @Override
    public ColorModificator getForegroundModificator() {
        return foregroundModificator;
    }

    @Override
    public void setForegroundModificator(ColorModificator foregroundModificator) {
        this.foregroundModificator = foregroundModificator;
    }

    public TreeTableNodeFormatBasic foreground(ColorModificator foregroundModificator) {
        this.foregroundModificator = foregroundModificator;
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="backgroundModificator">
    private ColorModificator backgroundModificator;

    @Override
    public ColorModificator getBackgroundModificator() {
        return backgroundModificator;
    }

    @Override
    public void setBackgroundModificator(ColorModificator backgroundModificator) {
        this.backgroundModificator = backgroundModificator;
    }

    public TreeTableNodeFormatBasic background(ColorModificator backgroundModificator) {
        this.backgroundModificator = backgroundModificator;
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="icons">
    private List<Icon> icons;

    @Override
    public List<Icon> getIcons(){
        if( icons!=null )return icons;
        icons = new ArrayList<Icon>();
        return icons;
    }
    @Override
    public void setIcons( List<Icon> icons ){
        this.icons = icons;
    }

    public TreeTableNodeFormatBasic icons(List<Icon> icons){
        setIcons(icons);
        return this;
    }

    public TreeTableNodeFormatBasic icons(Icon ... icons){
        getIcons().addAll( Arrays.asList(icons) );
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="convertor">
    protected Function<Object,String> convertor;

    @Override
    public Function<Object, String> getConvertor() {
        return convertor;
    }

    @Override
    public void setConvertor(Function<Object, String> convertor) {
        this.convertor = convertor;
    }

    public TreeTableNodeFormatBasic convertor(Function<Object, String> convertor){
        this.convertor = convertor;
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="iconWidthMin">
    protected Integer iconWidthMin = 0;

    @Override
    public Integer getIconWidthMin() {
        return iconWidthMin;
    }

    @Override
    public void setIconWidthMin(Integer iconWidthMin) {
        this.iconWidthMin = iconWidthMin;
    }

    public TreeTableNodeFormatBasic iconWidthMin(Integer iconWidthMin) {
        this.iconWidthMin = iconWidthMin;
        return this;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="merge()">
    @Override
    public void merge( TreeTableNodeFormat fmt ){
        if( fmt==null )return;

        if( fmt.getFontFamily()!=null ){
            setFontFamily(fmt.getFontFamily());
        }

        if( fmt.getFontSize()!=null ){
            setFontSize(fmt.getFontSize());
        }

        if( fmt.getItalic()!=null ){
            setItalic(fmt.getItalic());
        }

        if( fmt.getWeight()!=null ){
            setWeight(fmt.getWeight());
        }

        if( fmt.getBackground()!=null ){
            setBackground(fmt.getBackground());
        }

        if( fmt.getForeground()!=null ){
            setForeground(fmt.getForeground());
        }

        if( fmt.getIconWidthMin()!=null ){
            setIconWidthMin(fmt.getIconWidthMin());
        }

        if( fmt.getConvertor()!=null ){
            setConvertor(fmt.getConvertor());
        }

        if( fmt.getIcons()!=null && !fmt.getIcons().isEmpty() ){
            getIcons().clear();
            getIcons().addAll(fmt.getIcons());
        }
    }
    //</editor-fold>
}
