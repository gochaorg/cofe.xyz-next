/*
 * The MIT License
 *
 * Copyright 2017 Kamnev Georgiy <nt.gocha@gmail.com>.
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

package xyz.cofe.gui.swing.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.font.LineMetrics;
import java.net.URL;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.ModernBalloonStyle;
import net.java.balloontip.styles.RoundedBalloonStyle;
import xyz.cofe.gui.swing.SwingListener;

/**
 * Редактор текста с валидацией
 * @author nt.gocha@gmail.com
 */
public class ValidatedTextField extends JTextField {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(ValidatedTextField.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }
    private static boolean isLogSevere(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.SEVERE.intValue();
    }
    private static boolean isLogWarning(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.WARNING.intValue();
    }
    private static boolean isLogInfo(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.INFO.intValue();
    }
    private static boolean isLogFine(){
        Level ll = logLevel();
        return ll == null
            ? true
            : ll.intValue() <= Level.FINE.intValue();
    }
    private static boolean isLogFiner(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINER.intValue();
    }
    private static boolean isLogFinest(){
        Level ll = logLevel();
        return ll == null
            ? false
            : ll.intValue() <= Level.FINEST.intValue();
    }

    private static void logEntering(String method,Object ... args){
        logger.entering(ValidatedTextField.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(ValidatedTextField.class.getName(), method, result);
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
    //</editor-fold>

    public ValidatedTextField(){
        Color col = getForeground();
        if( col==null ) col = Color.black;

        validForeground = col;
        invalidForeground = Color.red;

        SwingListener.onTextChanged(this, new Runnable() {
            public void run(){
                validateText();
            }});

        URL warnUrl = ValidatedTextField.class.getResource("warning03.png");
        if( warnUrl!=null ){
            setWarningIcon(new ImageIcon(warnUrl));
        }
    }

    //<editor-fold defaultstate="collapsed" desc="validForeground">
    protected Color validForeground;

    /**
     * Указывает цвет корретного значения
     * @return цвет текста
     */
    public Color getValidForeground() {
        return validForeground;
    }

    /**
     * Указывает цвет корретного значения
     * @param validForeground цвет текста
     */
    public void setValidForeground(Color validForeground) {
        if( validForeground==null )throw new IllegalArgumentException("validForeground==null");
        this.validForeground = validForeground;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="invalidForeground">
    protected Color invalidForeground;

    /**
     * Указывает цвет не корретного значения
     * @return цвет текста
     */
    public Color getInvalidForeground() {
        return invalidForeground;
    }

    /**
     * Указывает цвет не корретного значения
     * @param invalidForeground цвет текста
     */
    public void setInvalidForeground(Color invalidForeground) {
        if( invalidForeground==null )throw new IllegalArgumentException("invalidForeground==null");
        this.invalidForeground = invalidForeground;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="filter">
    protected Predicate<String> filter;

    /**
     * Указывает функцию проверки текста
     * @return функция проверки текста
     */
    public Predicate<String> getFilter() {
        return filter;
    }

    /**
     * Указывает функцию проверки текста
     * @param filter функция проверки текста
     */
    public void setFilter(Predicate<String> filter) {
        this.filter = filter;
        validateText();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="warningIcon">
    protected Icon warningIcon = null;

    /**
     * Указывает иконку предупреждающую о не корректном значении
     * @return иконка не корректного значения
     */
    public Icon getWarningIcon() {
        return warningIcon;
    }

    /**
     * Указывает иконку предупреждающую о не корректном значении
     * @param warningIcon иконка не корректного значения
     */
    public void setWarningIcon(Icon warningIcon) {
        this.warningIcon = warningIcon;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="warningIconVisible">
    protected boolean warningIconVisible = false;

    /**
     * Указывает видна ли иконка предупреждения
     * @return true иконка отображается
     */
    public boolean isWarningIconVisible() {
        return warningIconVisible;
    }

    /**
     * Указывает видна ли иконка предупреждения
     * @param warningIconVisible отображать иконку
     */
    public void setWarningIconVisible(boolean warningIconVisible) {
        this.warningIconVisible = warningIconVisible;
        repaint();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="warningIconRect">
    /**
     * Возвращает расположение иконки
     * @return расположение иконки
     */
    public Rectangle getWarningIconRect(){
        Icon ico = getWarningIcon();
        if( ico==null )return null;

        Insets insets = getInsets();
        if( insets==null )insets = new Insets(0, 0, 0, 0);

        float cw = getWidth();
        float ch = getHeight();

        float aw = cw - insets.left - insets.right;
        float ah = ch - insets.top - insets.bottom;

        int x = (int)(aw - ico.getIconWidth() - warningIconRight);
        int y = (int)0;

        x += insets.left;
        y += insets.top;

        return new Rectangle(x, y, ico.getIconWidth(), ico.getIconHeight());
    }
    //</editor-fold>

    protected Integer warningIconRight = 3;
    protected Integer warningIconTop = 3;

    //<editor-fold defaultstate="collapsed" desc="isTextValid()">
    /**
     * Указывает введенный текст корректен или нет
     * @return true - текст корректен
     */
    public boolean isTextValid(){
        Predicate<String> p = getFilter();
        if( p==null )return true;

        return p.test(getText());
    }

    /**
     * Указывает введенный текст корректен или нет
     * @param valid true - текст корректен
     */
    protected void seTextValid( boolean valid ){
        warningIconVisible = !valid;
        if( valid ){
            setForeground(validForeground);
        }else{
            setForeground(invalidForeground);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="validateText()">
    /**
     * Проверет текст на корректность
     */
    public void validateText(){
        Predicate<String> p = getFilter();

        if( p!=null ){
            seTextValid(p.test(getText()));
        }else{
            seTextValid(true);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="placeholder">
    protected String placeholder;

    /**
     * Указывает текст заполнитель отображаемый при пустом значении
     * @return текст заполнитель
     */
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * Указывает текст заполнитель отображаемый при пустом значении
     * @param placeholder текст заполнитель
     */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="balloonText">
    protected String balloonText;

    /**
     * Указывает текст подсказку при не корректном значении
     * @return текст подсказска
     */
    public String getBalloonText() {
        if( balloonText!=null )return balloonText;
        balloonText = "";
        return balloonText;
    }

    /**
     * Указывает текст подсказку при не корректном значении
     * @param balloonText текст подсказска
     */
    public void setBalloonText(String balloonText) {
        this.balloonText = balloonText;
        //getBalloon().setTextContents(balloonText);
    }
    //</editor-fold>

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if( warningIconVisible && e.getID()==MouseEvent.MOUSE_PRESSED ){

            Rectangle rect = getWarningIconRect();
            String btxt = getBalloonText();

            if( rect!=null &&
                rect.contains(e.getX(), e.getY()) &&
                btxt!=null && btxt.trim().length()>0 ){

                BalloonTip balloon = new BalloonTip(this, btxt,new RoundedBalloonStyle(7,7,Color.white,Color.black),true);

                return;
            }
        }
        super.processMouseEvent(e);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        String txt = getText();
        String pholder = getPlaceholder();
        if( pholder!=null  && pholder.length()>0 && (txt==null || txt.length()<1) ){
            paintPlaceholder((Graphics2D)g);
        }

        if( warningIconVisible ){
            paintWarningIcon((Graphics2D)g);
        }
    }

    protected void paintWarningIcon(Graphics2D gs){
        Icon ico = getWarningIcon();
        Rectangle rect = getWarningIconRect();
        if( ico==null || rect==null )return;

        ico.paintIcon(this, gs, rect.x, rect.y);
    }

    protected void paintPlaceholder(Graphics2D gs){
        String txt = getPlaceholder();
        gs.setColor(Color.gray);

        Font fnt = gs.getFont();
        if( fnt==null )return;

        Insets insets = getInsets();
        if( insets==null )insets = new Insets(0, 0, 0, 0);

        LineMetrics lm = fnt.getLineMetrics(txt, gs.getFontRenderContext());

        float cw = getWidth();
        float ch = getHeight();

        float aw = cw - insets.left - insets.right;
        float ah = ch - insets.top - insets.bottom;

        float th = lm.getAscent() + lm.getDescent() + lm.getLeading();

        float x = 0;
        float y = th;

        y += (ah - th)/2f;

        y -= lm.getDescent();

        //System.out.println("y="+y+" ah="+ah+" th="+th+" insets.top="+insets.top);

        //y += ;

        x += insets.left;
        y += insets.top;

        gs.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gs.drawString( txt, x, y );
    }
}
