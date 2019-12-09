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

package xyz.cofe.gui.swing.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.net.URL;
import java.text.AttributedString;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import xyz.cofe.ecolls.Fn2;
import xyz.cofe.gui.swing.color.ColorModificator;
import xyz.cofe.gui.swing.color.Colors;
import xyz.cofe.gui.swing.properties.PropertyValue;

/**
 * Рендер для данных TreeTable. <br>
 * Отображает следующие типы данных:
 * <ul>
 * <li>Object,
 * <li>TreeTableNode - колонка TreeTableNodeColumn,
 * <li>TreeTableNodeValue - колонка TreeTableNodeValueColumn
 * </ul>
 * @author nt.gocha@gmail.com
 */
//@Deprecated
public class TreeTableNodeRender
    extends JComponent
    implements TableCellRenderer
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(TreeTableNodeRender.class.getName());

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
        logger.entering(TreeTableNodeRender.class.getName(), method, args);
    }
    private static void logExiting(String method,Object result){
        logger.exiting(TreeTableNodeRender.class.getName(), method, result);
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

    //<editor-fold defaultstate="collapsed" desc="foregroundBase : Color">
    protected Color foregroundBase;

    /**
     * Возвращает базовый цвет текста
     * @return базовый цвет текста
     */
    public Color getForegroundBase() {
        return foregroundBase;
    }

    /**
     * Указывает базовый цвет текста
     * @param foregroundBase базовый цвет текста
     */
    public void setForegroundBase(Color foregroundBase) {
        this.foregroundBase = foregroundBase;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="backgroundBase : Color">
    protected Color backgroundBase;

    /**
     * Возвращает базовый цвет фона
     * @return базовый цвет фона
     */
    public Color getBackgroundBase() {
        return backgroundBase;
    }

    /**
     * Указывает базовый цвет фона
     * @param backgroundBase базовый цвет фона
     */
    public void setBackgroundBase(Color backgroundBase) {
        this.backgroundBase = backgroundBase;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="foregroundAlternative : Color">
    protected Color foregroundAlternative;

    /**
     * Возвращает базовый цвет текста для альтернативной строки таблицы
     * @return базовый цвет текста
     */
    public Color getForegroundAlternative() {
        return foregroundAlternative;
    }

    /**
     * Указывает базовый цвет текста для альтернативной строки таблицы
     * @param foregroundAlternative базовый цвет текста
     */
    public void setForegroundAlternative(Color foregroundAlternative) {
        this.foregroundAlternative = foregroundAlternative;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="backgroundAlternative : Color">
    protected Color backgroundAlternative;

    /**
     * Возвращает базовый цвет фона для альтернативной строки таблицы
     * @return базовый цвет фона
     */
    public Color getBackgroundAlternative() {
        return backgroundAlternative;
    }

    /**
     * Указывает базовый цвет фона для альтернативной строки таблицы
     * @param backgroundAlternative базовый цвет фона
     */
    public void setBackgroundAlternative(Color backgroundAlternative) {
        this.backgroundAlternative = backgroundAlternative;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="foregroundSelected : Color">
    protected Color foregroundSelected;

    /**
     * Возвращает цвет текста для выделенной строки таблицы
     * @return цвет текста
     */
    public Color getForegroundSelected() {
        return foregroundSelected;
    }

    /**
     * Указывает цвет текста для выделенной строки таблицы
     * @param foregroundSelected цвет текста
     */
    public void setForegroundSelected(Color foregroundSelected) {
        this.foregroundSelected = foregroundSelected;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="backgroundSelected : Color">
    protected Color backgroundSelected;

    /**
     * Возвращает цвет фона для выделенной строки таблицы
     * @return цвет фона
     */
    public Color getBackgroundSelected() {
        return backgroundSelected;
    }

    /**
     * Указывает цвет фона для выделенной строки таблицы
     * @param backgroundSelected цвет фона
     */
    public void setBackgroundSelected(Color backgroundSelected) {
        this.backgroundSelected = backgroundSelected;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="foregroundSelectedAlternative : Color">
    protected Color foregroundSelectedAlternative;

    /**
     * Возвращает цвет текста для альтернативной выбранной строки
     * @return цвет текста
     */
    public Color getForegroundSelectedAlternative() {
        return foregroundSelectedAlternative;
    }

    /**
     * Указывает цвет текста для альтернативной выбранной строки
     * @param foregroundSelectedAlternative цвет текста
     */
    public void setForegroundSelectedAlternative(Color foregroundSelectedAlternative) {
        this.foregroundSelectedAlternative = foregroundSelectedAlternative;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="backgroundSelectedAlternative : Color">
    protected Color backgroundSelectedAlternative;

    /**
     * Возвращает цвет фона для альтернативной выбранной строки таблицы
     * @return цвет фона
     */
    public Color getBackgroundSelectedAlternative() {
        return backgroundSelectedAlternative;
    }

    /**
     * Указывает цвет фона для альтернативной выбранной строки таблицы
     * @param backgroundSelectedAlternative цвет фона
     */
    public void setBackgroundSelectedAlternative(Color backgroundSelectedAlternative) {
        this.backgroundSelectedAlternative = backgroundSelectedAlternative;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="backgroundFocused : Color">
    protected Color foregroundFocused;

    /**
     * Возвращает цвет текста для ячейки содержащей фокус
     * @return цвет текста
     */
    public Color getForegroundFocused() {
        return foregroundFocused;
    }

    /**
     * Указывает цвет текста для ячейки содержащей фокус
     * @param foregroundFocused цвет текста
     */
    public void setForegroundFocused(Color foregroundFocused) {
        this.foregroundFocused = foregroundFocused;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="backgroundFocused : Color">
    protected Color backgroundFocused;

    /**
     * Возвращает цвет фона для ячейки содержащей фокус
     * @return цвет фона
     */
    public Color getBackgroundFocused() {
        return backgroundFocused;
    }

    /**
     * Указывает цвет фона для ячейки содержащей фокус
     * @param backgroundFocused цвет фона
     */
    public void setBackgroundFocused(Color backgroundFocused) {
        this.backgroundFocused = backgroundFocused;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="foregroundFocusedAlternative : Color">
    protected Color foregroundFocusedAlternative;

    /**
     * Возвращает цвет текста для ячейки, в альтерантивной строке, содержащей фокус
     * @return цвет текста
     */
    public Color getForegroundFocusedAlternative() {
        return foregroundFocusedAlternative;
    }

    /**
     * Указывает цвет текста для ячейки, в альтерантивной строке, содержащей фокус
     * @param foregroundFocusedAlternative цвет текста
     */
    public void setForegroundFocusedAlternative(Color foregroundFocusedAlternative) {
        this.foregroundFocusedAlternative = foregroundFocusedAlternative;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="backgroundFocusedAlternative : Color">
    protected Color backgroundFocusedAlternative;

    /**
     * Возвращает цвет фона для ячейки, в альтерантивной строке, содержащей фокус
     * @return цвет фона
     */
    public Color getBackgroundFocusedAlternative() {
        return backgroundFocusedAlternative;
    }

    /**
     * Указывает цвет фона для ячейки, в альтерантивной строке, содержащей фокус
     * @param backgroundFocusedAlternative цвет фона
     */
    public void setBackgroundFocusedAlternative(Color backgroundFocusedAlternative) {
        this.backgroundFocusedAlternative = backgroundFocusedAlternative;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="baseModificator : ColorModificator">
    protected ColorModificator baseModificator;

    /**
     * Возвращает базовый модификатор цвета
     * @return базовый модификатор цвета
     */
    public ColorModificator getBaseModificator() {
        return baseModificator;
    }

    /**
     * Указывает базовый модификатор цвета
     * @param baseModificator базовый модификатор цвета
     */
    public void setBaseModificator(ColorModificator baseModificator) {
        this.baseModificator = baseModificator;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="foregroundModificator : ColorModificator">
    protected ColorModificator foregroundModificator;

    /**
     * Возвращает модификатор цвета текста
     * @return модификатор цвета текста
     */
    public ColorModificator getForegroundModificator() {
        return foregroundModificator;
    }

    /**
     * Указывает модификатор цвета текста
     * @param foregroundModificator модификатор цвета текста
     */
    public void setForegroundModificator(ColorModificator foregroundModificator) {
        this.foregroundModificator = foregroundModificator;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="backgroundModificator : ColorModificator">
    protected ColorModificator backgroundModificator;

    /**
     * Возвращает модификатор цвета фона
     * @return модификатор цвета фона
     */
    public ColorModificator getBackgroundModificator() {
        return backgroundModificator;
    }

    /**
     * Указывает модификатор цвета фона
     * @param backgroundModificator модификатор цвета фона
     */
    public void setBackgroundModificator(ColorModificator backgroundModificator) {
        this.backgroundModificator = backgroundModificator;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="text : String">
    protected String text = "";

    /**
     * Возвращает отображаемый текст
     * @return отображаемый текст
     */
    public synchronized String getText() {
        return text;
    }

    /**
     * Указывает отображаемый текст
     * @param text отображаемый текст
     */
    public synchronized void setText(String text) {
        this.text = text;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="row : int">
    protected int row = -1;

    /**
     * Возвращает индекс строки таблицы
     * @return индекс строки таблицы
     */
    public int getRow() {
        return row;
    }

    /**
     * Указывает индекс строки таблицы
     * @param row индекс строки таблицы
     */
    public void setRow(int row) {
        this.row = row;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="column : int">
    protected int column = -1;

    /**
     * Возвращает индекс колонки таблицы
     * @return индекс колонки таблицы
     */
    public int getColumn() {
        return column;
    }

    /**
     * Указывает индекс колонки таблицы
     * @param column индекс колонки таблицы
     */
    public void setColumn(int column) {
        this.column = column;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="focused : boolean">
    protected boolean focused = false;

    /**
     * Возвращает содержит ли ячейка фокус ввода
     * @return содержит фокус ввода
     */
    public boolean isFocused() {
        return focused;
    }

    /**
     * Указывает содержит ли ячейка фокус ввода
     * @param focused содержит фокус ввода
     */
    public void setFocused(boolean focused) {
        this.focused = focused;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="selected : boolean">
    protected boolean selected = false;

    /**
     * Возвращает выбрана ли строка пользователем
     * @return строка выбрана пользователем
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Указывает выбрана ли строка пользователем
     * @param selected строка выбрана пользователем
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="level : int">
    protected int level = 0;

    /**
     * Возвращает уровень вложенности узла дерева
     * @return уровень узла в дереве
     */
    public int getLevel() {
        return level;
    }

    /**
     * Укаызвает уровень вложенности узла дерева
     * @param level уровень узла в дереве
     */
    public void setLevel(int level) {
        this.level = level;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="levelIndent : int">
    protected int levelIndent = 20;

    /**
     * Возвращает велечину отступа для одного уровня вложенности
     * @return величина отступа
     */
    public int getLevelIndent() {
        return levelIndent;
    }

    /**
     * Указывает велечину отступа для одного уровня вложенности
     * @param levelIndent величина отступа
     */
    public void setLevelIndent(int levelIndent) {
        this.levelIndent = levelIndent;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="textAntialiasing : boolean">
    protected boolean textAntialiasing = true;

    /**
     * Указывает использовать ли antialiasing для отображения текста
     * @return использовать antialiasing
     */
    public boolean isTextAntialiasing() {
        return textAntialiasing;
    }

    /**
     * Указывает использовать ли antialiasing для отображения текста
     * @param textAntialiasing использовать antialiasing
     */
    public void setTextAntialiasing(boolean textAntialiasing) {
        this.textAntialiasing = textAntialiasing;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="nullIcon : Icon">
    protected Icon nullIcon;

    /**
     * Возвращает иконку для отображения null значения
     * @return иконка null
     */
    public Icon getNullIcon() {
        return nullIcon;
    }

    /**
     * Указывает иконку для отображения null значения
     * @param nullIcon иконка null
     */
    public void setNullIcon(Icon nullIcon) {
        this.nullIcon = nullIcon;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="leafIcon : Icon">
    protected Icon leafIcon;

    /**
     * Возвращает иконку для обозначения листа дерева
     * @return икона "листа"
     */
    public Icon getLeafIcon() {
        return leafIcon;
    }

    /**
     * Указывает иконку для обозначения листа дерева
     * @param leafIcon икона "листа"
     */
    public void setLeafIcon(Icon leafIcon) {
        this.leafIcon = leafIcon;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="expandedIcon : Icon">
    protected Icon expandedIcon;

    /**
     * Возвращает иконку для обозначения развернутого узла дерева
     * @return иконка "минус"
     */
    public Icon getExpandedIcon() {
        return expandedIcon;
    }

    /**
     * Указывает иконку для обозначения развернутого узла дерева
     * @param expandedIcon иконка "минус"
     */
    public void setExpandedIcon(Icon expandedIcon) {
        this.expandedIcon = expandedIcon;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="collapsedIcon : Icon">
    protected Icon collapsedIcon;

    /**
     * Возвращает иконку для обозначения свернутого узла дерева
     * @return иконка "плюс"
     */
    public Icon getCollapsedIcon() {
        return collapsedIcon;
    }

    /**
     * Указывает иконку для обозначения свернутого узла дерева
     * @param collapsedIcon иконка "плюс"
     */
    public void setCollapsedIcon(Icon collapsedIcon) {
        this.collapsedIcon = collapsedIcon;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="nodeIcon : Icon">
    protected Icon nodeIcon;

    /**
     * Указывает иконку узла
     * @return иконка узла
     */
    public Icon getNodeIcon() {
        return nodeIcon;
    }

    /**
     * Указывает иконку узла
     * @param nodeIcon иконка узла
     */
    public void setNodeIcon(Icon nodeIcon) {
        this.nodeIcon = nodeIcon;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="nodeIconWidthMax : int">
    protected int nodeIconWidthMax;

    /**
     * Указывает зарезервированное место для иконок узла
     * @return зарезервированое место для иконок
     */
    public int getNodeIconWidthMax() {
        return nodeIconWidthMax;
    }

    /**
     * Указывает зарезервированное место для иконок узла
     * @param nodeIconWidthMax зарезервированое место для иконок
     */
    public void setNodeIconWidthMax(int nodeIconWidthMax) {
        this.nodeIconWidthMax = nodeIconWidthMax;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="nodeIconVAlign : float">
    protected float nodeIconVAlign = 0.5f;

    /**
     * Указывает выравнивание иконки по вертикали
     * @return выравнивание (0...1)
     */
    public float getNodeIconVAlign() {
        return nodeIconVAlign;
    }

    /**
     * Указывает выравнивание иконки по вертикали
     * @param nodeIconVAlign выравнивание (0...1)
     */
    public void setNodeIconVAlign(float nodeIconVAlign) {
        this.nodeIconVAlign = nodeIconVAlign;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="nodeIconPaddingLeft : int">
    protected int nodeIconPaddingLeft;

    /**
     * Указывает отступ для иконки слева
     * @return отступ слева
     */
    public int getNodeIconPaddingLeft() {
        return nodeIconPaddingLeft;
    }

    /**
     * Указывает отступ для иконки слева
     * @param nodeIconPaddingLeft отступ слева
     */
    public void setNodeIconPaddingLeft(int nodeIconPaddingLeft) {
        this.nodeIconPaddingLeft = nodeIconPaddingLeft;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="nodeIconPaddingRight : int">
    protected int nodeIconPaddingRight;

    /**
     * Указывает отступ для иконки справа
     * @return отступ с права
     */
    public int getNodeIconPaddingRight() {
        return nodeIconPaddingRight;
    }

    /**
     * Указывает отступ для иконки справа
     * @param nodeIconPaddingRight отступ с права
     */
    public void setNodeIconPaddingRight(int nodeIconPaddingRight) {
        this.nodeIconPaddingRight = nodeIconPaddingRight;
    }
    //</editor-fold>

    protected int iconWidthMin = 0;

    //<editor-fold defaultstate="collapsed" desc="icons : Icon[]">
    protected Icon[] icons = null;

    /**
     * Возвращает иконки для отображения
     * @return иконки
     */
    public Icon[] getIcons() {
        return icons;
    }

    /**
     * Указывает иконки для отображения
     * @param icons иконки
     */
    public void setIcons(Icon[] icons) {
        this.icons = icons;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="customPainter">
    protected Fn2<Graphics,Rectangle,Object> customPainter;

    /**
     * Возвращает функцию для ручного отображения
     * @return функция для ручного отображения
     */
    public Fn2<Graphics, Rectangle,Object> getCustomPainter() {
        return customPainter;
    }

    /**
     * Указывает функцию для ручного отображения
     * @param customPainter функция для ручного отображения
     */
    public void setCustomPainter( Fn2<Graphics, Rectangle, Object> customPainter) {
        this.customPainter = customPainter;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="atext : AttributedString">
    protected AttributedString atext;

    /**
     * Возвращает строку с атрибутами для рендера
     * @return строка для рендера
     */
    public AttributedString getAtext() {
        return atext;
    }

    /**
     * Указывает строку с атрибутами для рендера
     * @param atext строка для рендера
     */
    public void setAtext(AttributedString atext) {
        this.atext = atext;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="isLastRow">
    protected boolean isLastRow = false;

    /**
     * Указывает что это послендяя отображаемая строка
     * @return последняя отображаемая строка
     */
    public boolean isIsLastRow() {
        return isLastRow;
    }

    /**
     * Указывает что это послендяя отображаемая строка
     * @param isLastRow последняя отображаемая строка
     */
    public void setIsLastRow(boolean isLastRow) {
        this.isLastRow = isLastRow;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="lastRowBorder">
    protected Border lastRowBorder = null;

    /**
     * Возвращает бордюр для отображения последней строки
     * @return бордюбр
     */
    public Border getLastRowBorder() {
        return lastRowBorder;
    }

    /**
     * Указывает бордюр для отображения последней строки
     * @param lastRowBorder бордюбр
     */
    public void setLastRowBorder(Border lastRowBorder) {
        this.lastRowBorder = lastRowBorder;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="cellBorder">
    protected Border cellBorder = null;

    /**
     * Возвращает бордюр для отображения ячейки
     * @return бордюр
     */
    public Border getCellBorder() {
        return cellBorder;
    }

    /**
     * Указывает бордюр для отображения ячейки
     * @param cellBorder бордюр
     */
    public void setCellBorder(Border cellBorder) {
        this.cellBorder = cellBorder;
    }
    //</editor-fold>

    protected boolean valueIsNode = false;

    //<editor-fold defaultstate="collapsed" desc="directModel">
    protected TreeTableDirectModel directModel;

    /**
     * Возвращает модель таблицы-дерева
     * @return модель таблицы дерева
     */
    public TreeTableDirectModel getDirectModel() {
        return directModel;
    }

    /**
     * Указывает модель таблицы-дерева
     * @param directModel модель таблицы-дерева
     */
    public void setDirectModel(TreeTableDirectModel directModel) {
        this.directModel = directModel;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TreeTableNodeRender()">
    /**
     * Конструктор по умолчанию
     */
    public TreeTableNodeRender(){
        text = "";

        baseModificator = null;
        foregroundModificator = null;
        backgroundModificator = null;

        foregroundBase = Color.black;
        backgroundBase = Color.white;

        foregroundAlternative = foregroundBase;
        backgroundAlternative = new ColorModificator().brighter(-0.1f).apply(backgroundBase);

        foregroundSelected = foregroundBase;
        backgroundSelected = new ColorModificator().bright(0.9f).saturation(0.25f).apply(Colors.HUE_210);

        foregroundSelectedAlternative = foregroundBase;
        backgroundSelectedAlternative = new ColorModificator().brighter(-0.1f).apply(backgroundSelected);

        foregroundFocused = Color.white;
        backgroundFocused = Color.black;

        foregroundFocusedAlternative = new ColorModificator().brighter(+0.1f).apply(foregroundFocused);
        backgroundFocusedAlternative = new ColorModificator().brighter(+0.1f).apply(backgroundFocused);

        URL uNullIcon = TreeTableNodeRender.class.getResource("/xyz/cofe/gui/swing/properties/editor/null.png");
        nullIcon = uNullIcon==null ? null : new ImageIcon(uNullIcon);

        URL uExpandedIcon = TreeTableNodeRender.class.getResource("/xyz/cofe/gui/swing/table/node-minus-v1-12x12.png");
        expandedIcon = uExpandedIcon==null ? null : new ImageIcon(uExpandedIcon);

        URL uCollapsedIcon = TreeTableNodeRender.class.getResource("/xyz/cofe/gui/swing/table/node-plus-v1-12x12.png");
        collapsedIcon = uCollapsedIcon==null ? null : new ImageIcon(uCollapsedIcon);

        leafIcon = null;

        for( Icon ico : new Icon[]{ expandedIcon, collapsedIcon, leafIcon } ){
            if( ico==null )continue;
            int iw = ico.getIconWidth();
            if( nodeIconWidthMax<iw )nodeIconWidthMax = iw;
        }

        nodeIconPaddingLeft = 2;

        nodeIconPaddingRight = 2;
    }
    //</editor-fold>

    public void prepareDefaults( JTable tbl ){
        if( tbl==null )throw new IllegalArgumentException("tbl == null");
        tbl.setDefaultRenderer(TreeTableNode.class, this);
        tbl.setDefaultRenderer(TreeTableNodeBasic.class, this);
        tbl.setDefaultRenderer(FormattedValue.class, this);
        tbl.setDefaultRenderer(PropertyValue.class, this);
        tbl.setDefaultRenderer(TreeTableNodeValue.class, this);
    }

    //<editor-fold defaultstate="collapsed" desc="alternativeRow : boolean">
    protected boolean isAlternativeRow(){
        return getRow() % 2 == 1;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="getTreeLevelOf(node):int">
    /**
     * Возвращает уровень вложенности узла дерева
     * @param node узел дерева
     * @return уровень вложенности с попоравкой на isRootVisible
     * @see TreeTableDirectModel#isRootVisible()
     */
    public int getTreeLevelOf( TreeTableNode node ){
        if( node==null )return 0;

        TreeTableDirectModel dm = directModel;
        boolean showRoot = true;
        if( dm!=null ){
            showRoot = dm.isRootVisible();
        }

        int nlevel = node.level() - ( showRoot ? 1 : 2);
        if( nlevel<0 )nlevel = 0;
        return nlevel;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="getTableCellRendererComponent(..):Component">
    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column)
    {
        // super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        // setBorder(new EmptyBorder(0, 0, 0, 0));
        baseModificator = null;
        foregroundModificator = null;
        backgroundModificator = null;

        setSelected(isSelected);
        setFocused(hasFocus);
        setRow(row);
        setColumn(column);
        setLevel(0);
        setText( value==null ? "" : value.toString() );
        setNodeIcon(null);
        setIcons(null);
        setCustomPainter(null);
        setAtext(null);
        iconWidthMin = 0;
        setIsLastRow(false);

        valueIsNode = value instanceof TreeTableNode;

        prepareColors();

        setText(value==null ? "" : value.toString());

        if( table!=null ){
            int rcount = table.getRowCount();
            setIsLastRow( row==(rcount-1) );
        }

        if( value instanceof TreeTableNode ){
            prepareTreeTableNode((TreeTableNode)value);
        }else if( value instanceof TreeTableNodeValue ){
            TreeTableNodeValue ttNodeValue = (TreeTableNodeValue)value;
            prepareTreeTableNodeValue(ttNodeValue);
        }else if( value instanceof FormattedValue ){
            prepareFormattedValue((FormattedValue)value);
        }

        return this;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="prepareColors()">
    /**
     * Подготавливает цвет текста и фона перед рендерингом
     */
    protected void prepareColors() {
        Color bg;
        Color fg;

        if( isSelected() ){
            if( isFocused() ){
                if( isAlternativeRow() ){
                    fg = getForegroundFocusedAlternative();
                    bg = getBackgroundFocusedAlternative();
                }else{
                    fg = getForegroundFocused();
                    bg = getBackgroundFocused();
                }
            }else{
                if( isAlternativeRow() ){
                    fg = getForegroundSelectedAlternative();
                    bg = getBackgroundSelectedAlternative();
                }else{
                    fg = getForegroundSelected();
                    bg = getBackgroundSelected();
                }
            }
        }else{
            if( isAlternativeRow() ){
                fg = getForegroundAlternative();
                bg = getBackgroundAlternative();
            }else{
                fg = getForegroundBase();
                bg = getBackgroundBase();
            }
        }

        setForeground(fg);
        setBackground(bg);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="prepareTreeTableNode(node)">
    /**
     * Подготавливает цвета и прочие настройки для рендернига узла дерева
     * @param node узел дерева
     */
    protected void prepareTreeTableNode(TreeTableNode node)
    {
        valueIsNode = true;

        String text = null;
        if( node instanceof TreeTableNodeGetText ){
            text = ((TreeTableNodeGetText)node).treeTableNodeGetText();
            if( text!=null ){
                setText(text);
            }else{
                Object data = node.getData();
                if( data!=null ){
                    text = data.toString();
                    setText(text);
                }else{
                    text = "null";
                    setText(text);
                }
            }
        }

        if( node instanceof TreeTableNodeGetFormat ){
            TreeTableNodeFormat fmt = ((TreeTableNodeGetFormat)node).getTreeTableNodeFormat();
            if( fmt!=null ){
                if( !isFocused() ){
                    Color bg = fmt.getBackground();
                    if( bg!=null )setBackground(bg);

                    Color fg = fmt.getForeground();
                    if( fg!=null )setForeground(fg);
                }

                List<Icon> ics = fmt.getIcons();
                if( ics!=null ){
                    setIcons(ics.toArray(new Icon[]{}));
                }

                AttributedString astr = fmt.createAttributedString(text,node);
                if( astr!=null ){
                    setAtext(astr);
                }

                Integer icWMin = fmt.getIconWidthMin();
                if( icWMin!=null && icWMin>=0 ){
                    iconWidthMin = icWMin;
                }

                setBaseModificator(fmt.getBaseModificator());
                setForegroundModificator(fmt.getForegroundModificator());
                setBackgroundModificator(fmt.getBackgroundModificator());
            }
        }

        int nlevel = getTreeLevelOf(node);
        setLevel(nlevel);

        boolean hasChildren = node.count()>0;
        boolean isLeaf = !hasChildren;

        if( node instanceof TreeTableNodeExpander ){
            isLeaf = false;
        }else if( node instanceof TreeTableNodeBasic ){
            TreeTableNodeBasic ttnb = (TreeTableNodeBasic)node;

            if( isLeaf ){
                var extractable = ttnb.getPreferredDataFollowable();
                Object data = ttnb.getData();
                if( extractable!=null ){
                    boolean canExtract = extractable.apply(data);
                    if( canExtract )isLeaf = false;
                }else{
                    Object extracter = ttnb.getPreferredDataFollower();
                    Object extractFinished = ttnb.getFollowFinished();
                    if( extracter!=null && extractFinished==null ){
                        isLeaf = false;
                    }
                }
            }
        }

        if( isLeaf ){
            setNodeIcon(getLeafIcon());
        }else{
            if( node.isExpanded() ){
                setNodeIcon(getExpandedIcon());
            }else{
                setNodeIcon(getCollapsedIcon());
            }
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="prepareTreeTableNodeValue(nodeValue)">
    /**
     * Подготавливает цвета и прочие настройки для рендернига узла дерева
     * @param nodeValue узел дерева
     */
    protected void prepareTreeTableNodeValue(TreeTableNodeValue nodeValue){
        Object value = nodeValue.getValue();
        String text = value==null ? "null" : value.toString();
        setText(text);

        TreeTableNodeFormat fmt = nodeValue.getFormat();
        if( fmt!=null ){
            if( !isFocused() ){
                Color bg = fmt.getBackground();
                if( bg!=null )setBackground(bg);

                Color fg = fmt.getForeground();
                if( fg!=null )setForeground(fg);
            }

            List<Icon> ics = fmt.getIcons();
            if( ics!=null ){
                setIcons(ics.toArray(new Icon[]{}));
            }

            AttributedString astr = fmt.createAttributedString(text, value);
            if( astr!=null ){
                setAtext(astr);
            }

            Integer icWMin = fmt.getIconWidthMin();
            if( icWMin!=null && icWMin>=0 ){
                iconWidthMin = icWMin;
            }

            setBaseModificator(fmt.getBaseModificator());
            setForegroundModificator(fmt.getForegroundModificator());
            setBackgroundModificator(fmt.getBackgroundModificator());
        }

        var custPaint = nodeValue.getCustomPainter();
        if( custPaint!=null )setCustomPainter(custPaint);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="prepareFormattedValue()">
    /**
     * Подготавливает настройки для рендернинга форматированного значения
     * @param propertyValue форматированное значение
     */
    protected void prepareFormattedValue(FormattedValue propertyValue){
        Object value = propertyValue.getValue();

        String text = value==null ? "" : value.toString();
        setText(text);
        if( value instanceof Icon ){
            setIcons(new Icon[]{(Icon)value});
        }

        TreeTableNodeFormat fmt = null;
        if( propertyValue instanceof TreeTableNodeGetFormat ){
            fmt = ((TreeTableNodeGetFormat)propertyValue).getTreeTableNodeFormat();
        }else if( propertyValue instanceof TreeTableNodeGetFormatOf ){
            fmt = ((TreeTableNodeGetFormatOf)propertyValue).getTreeTableNodeFormatOf(value);
        }

        if( fmt!=null ){
            if( !isFocused() ){
                Color bg = fmt.getBackground();
                if( bg!=null )setBackground(bg);

                Color fg = fmt.getForeground();
                if( fg!=null )setForeground(fg);
            }

            List<Icon> ics = fmt.getIcons();
            if( ics!=null ){
                setIcons(ics.toArray(new Icon[]{}));
            }

            AttributedString astr = fmt.createAttributedString(text, value);
            if( astr!=null ){
                setAtext(astr);
            }

            Integer icWMin = fmt.getIconWidthMin();
            if( icWMin!=null && icWMin>=0 ){
                iconWidthMin = icWMin;
            }

            setBaseModificator(fmt.getBaseModificator());
            setForegroundModificator(fmt.getForegroundModificator());
            setBackgroundModificator(fmt.getBackgroundModificator());
        }
    }
    //</editor-fold>

    /**
     * Возвращает горизонтальную координату начала иконки сворачивания/разворачивания/листа дерева
     * @param level уровень вложености ячейки
     * @return кордината X
     */
    public int getNodeIconXBeginForLevel( int level ){
        float nodeico_x = level * getLevelIndent() + getNodeIconPaddingLeft();
        return (int)nodeico_x;
    }
    /**
     * Возвращает горизонтальную ширину иконки сворачивания/разворачивания/листа дерева
     * @param level уровень вложености ячейки
     * @return ширина иконки
     */
    public int getNodeIconXWidthForLevel( int level ){
        return getNodeIconWidthMax();
    }
    /**
     * Возвращает горизонтальную координату конца иконки сворачивания/разворачивания/листа дерева
     * @param level уровень вложености ячейки
     * @return кордината X
     */
    public int getNodeIconXEndForLevel( int level ){
        int begin = getNodeIconXBeginForLevel(level);
        return begin + getNodeIconXWidthForLevel(level) - 1;
    }

    //<editor-fold defaultstate="collapsed" desc="paintComponent(g)">
    @Override
    protected void paintComponent(Graphics g) {
        if( !(g instanceof Graphics2D) )throw new IllegalArgumentException("g not instanceof Graphics2D");
        Graphics2D gs = (Graphics2D)g;

        Color bg = getBackground();
        Color fg = getForeground();

        bg = bg==null ? Color.white : bg;
        fg = fg==null ? Color.black : fg;

        ColorModificator basemod = baseModificator;
        ColorModificator foremod = foregroundModificator;
        ColorModificator backmod = backgroundModificator;

        if( bg!=null ){
            if( basemod!=null )bg = basemod.apply(bg);
            if( backmod!=null )bg = backmod.apply(bg);
        }
        if( fg!=null ){
            if( basemod!=null )fg = basemod.apply(fg);
            if( foremod!=null )fg = foremod.apply(fg);
        }

        int w = getWidth();
        int h = getHeight();

        gs.setPaint(bg);
        gs.fillRect(0, 0, w, h);

        Icon nodeIco = getNodeIcon();

        String txt = getText();
        AttributedString atext = getAtext();
        Font fnt = getFont();

        int maxIcoWidth = getNodeIconWidthMax();

        int lvl = getLevel();

        int ico_x = (int)getNodeIconXBeginForLevel(lvl);

        float text_x = lvl * getLevelIndent() + maxIcoWidth + getNodeIconPaddingLeft() + getNodeIconPaddingRight();

        if( !valueIsNode ){
            ico_x = getNodeIconPaddingLeft();
            if( nodeIco!=null ){
                text_x = getNodeIconPaddingLeft() + maxIcoWidth + getNodeIconPaddingRight();
            }else{
                text_x = getNodeIconPaddingLeft();
            }
        }

        //<editor-fold defaultstate="collapsed" desc="render node icon">
        if( nodeIco!=null ){
            int ih = nodeIco.getIconHeight();
            if( ih >= h ){
                nodeIco.paintIcon(this, g, ico_x, 0);
            }else{
                float ihExtra = h - ih;
                float iconVAlign = getNodeIconVAlign();
                float ioff = ihExtra * iconVAlign;

                nodeIco.paintIcon(this, g, ico_x, (int)(ioff));
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="render icons">
        Icon[] icons = this.icons;

        if( icons!=null && icons.length>0 ){
            int iconCount = 0;

            int iconWidthSummary = 0;
            int iconWidthMin = Integer.MAX_VALUE;
            int iconWidthMax = Integer.MIN_VALUE;

            int iconHeightSummary = 0;
            int iconHeightMin = Integer.MAX_VALUE;
            int iconHeightMax = Integer.MIN_VALUE;

            for( Icon ico : icons ){
                if( ico==null )continue;
                int icoW = ico.getIconWidth();
                int icoH = ico.getIconHeight();

                iconCount++;

                iconWidthSummary += icoW;
                iconHeightSummary += icoH;

                iconWidthMin = iconWidthMin > icoW ? icoW : iconWidthMin;
                iconHeightMin = iconHeightMin > icoH ? icoH : iconHeightMin;

                iconWidthMax = iconWidthMax < icoW ? icoW : iconWidthMax;
                iconHeightMax = iconHeightMax < icoH ? icoH : iconHeightMax;
            }

            if( iconCount>0 ){
                float x_icon = text_x;

                int iconXOutput = (int)x_icon;
                int iconYOutput = 0;
                int iconsWidthSum = 0;

                int cHeight = getHeight();
                double vAlign = 0.5;

                for( Icon ico : icons ){
                    if( ico==null )continue;

                    int wico = ico.getIconWidth();
                    int hico = ico.getIconHeight();

                    double icoHeightDouble = (double)hico;
                    double alignPointY = cHeight * vAlign;
                    double icoTopOffset = -(hico * vAlign);

                    ico.paintIcon(this, g, iconXOutput, (int)(alignPointY + icoTopOffset));

                    iconXOutput += wico + 2;
                    iconsWidthSum += wico + 2;
                }

                text_x += (this.iconWidthMin>0 && this.iconWidthMin>iconsWidthSum ) ? this.iconWidthMin : iconsWidthSum;
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="redner text">
        if( customPainter!=null ){
            Rectangle rect = new Rectangle((int)text_x, 0, (int)(w - text_x), h);
            customPainter.apply(g, rect);
        }else{
            if( (txt!=null || atext!=null) && fnt!=null ){
                LineMetrics lm = fnt.getLineMetrics(txt, gs.getFontRenderContext());

                float fnt_ascent = lm.getAscent();
                float fnt_height = lm.getHeight();
                float fnt_descent = lm.getDescent();
                float fnt_leading = lm.getLeading();

                float fnt_h = fnt_ascent;
                float c_height = getHeight();

                float ty = fnt_h;
                if( fnt_h < c_height ){
                    ty = (c_height - fnt_h) / 2.0f + fnt_h;
                }

                gs.setPaint(fg);
                if( isTextAntialiasing() ){
                    gs.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                }else{
                    gs.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                }

                if( atext!=null ){
                    gs.drawString(atext.getIterator(), text_x, ty);
                }else{
                    gs.drawString(txt, text_x, ty);
                }
            }
        }
        //</editor-fold>

        Border brd = cellBorder;

        if( isLastRow && lastRowBorder!=null ){
            brd = lastRowBorder;
        }

        if( brd!=null ){
            brd.paintBorder(this, g, 0, 0, w, h);
        }
    }
    //</editor-fold>
}
