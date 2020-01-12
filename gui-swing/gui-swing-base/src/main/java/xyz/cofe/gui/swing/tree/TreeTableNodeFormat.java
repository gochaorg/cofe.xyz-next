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
import java.text.AttributedString;
import java.util.List;
import java.util.function.Function;
import javax.swing.Icon;
import xyz.cofe.gui.swing.color.ColorModificator;

/**
 * Настройки формаитрования узла
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public interface TreeTableNodeFormat {
    //<editor-fold defaultstate="collapsed" desc="background">
    /**
     * Указывает фон ячейки
     * @return цвет фона
     */
    public Color getBackground();

    /**
     * Указывает фон ячейки
     * @param background цвет фона
     */
    public void setBackground( Color background );
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="foreground">
    /**
     * Указывает цвет текста
     * @return цвет текста
     */
    public Color getForeground();

    /**
     * Указывает цвет текста
     * @param foreground цвет текста
     */
    public void setForeground( Color foreground );
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="icons">
    /**
     * Указывает иконки отображаемые рядом с текстом
     * @return иконки
     */
    public List<Icon> getIcons();

    /**
     * Указывает иконки отображаемые рядом с текстом
     * @param icons иконки
     */
    public void setIcons( List<Icon> icons );
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fontFamily">
    /**
     * Указывает шрифт текста
     * @return имя шрифта
     */
    public String getFontFamily();

    /**
     * Указывает шрифт текста
     * @param fontFamily имя шрифта
     */
    public void setFontFamily( String fontFamily );
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="fontSize">
    /**
     * Указывает шрифт текста
     * @return размер шрифта
     */
    public Float getFontSize();

    /**
     * Указывает шрифт текста
     * @param fontSize размер шрифта
     */
    public void setFontSize( Float fontSize );
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="italic">
    /**
     * Указывает шрифт текста
     * @return true - есть наклон / false - без наклона
     */
    public Boolean getItalic();

    /**
     * Указывает шрифт текста
     * @param italic true - есть наклон / false - без наклона
     */
    public void setItalic( Boolean italic );
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bold">
    /**
     * Указывает шрифт текста
     * @return true - жирный шрифт / false - обычный
     */
    public Boolean getBold();

    /**
     * Указывает шрифт текста
     * @param bold true - жирный шрифт / false - обычный
     */
    public void setBold( Boolean bold );
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="weight">
    /**
     * Указывает шрифт текста
     * @return вес шрифта
     */
    public Float getWeight();

    /**
     * Указывает шрифт текста
     * @param weight вес шрифта
     */
    public void setWeight( Float weight );
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="iconWidthMin">
    /**
     * Указывает минимальную ширину резервируемую для иконок
     * @return минимальная резервируемая ширина
     */
    public Integer getIconWidthMin();

    /**
     * Указывает минимальную ширину резервируемую для иконок
     * @param iconWidthMin минимальная резервируемая ширина
     */
    public void setIconWidthMin( Integer iconWidthMin );
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="convertor">
    /**
     * Указывает функцию конвертации данных
     * @return функция конвертации данных
     */
    public Function<Object, String> getConvertor();

    /**
     * Указывает функцию конвертации данных
     * @param convertor функция конвертации данных
     */
    public void setConvertor( Function<Object, String> convertor );
    //</editor-fold>

    /**
     * Указывает модификацию базового цвета
     * @return модификация базового цвета
     */
    public ColorModificator getBaseModificator();

    /**
     * Указывает модификацию базового цвета
     * @param cm модификация базового цвета
     */
    public void setBaseModificator( ColorModificator cm );

    /**
     * Указывает модификацию цвета текста
     * @return модификация цвета текста
     */
    public ColorModificator getForegroundModificator();

    /**
     * Указывает модификацию цвета текста
     * @param cm модификация цвета текста
     */
    public void setForegroundModificator( ColorModificator cm );

    /**
     * Указывает модификацию цвета фона
     * @return модификация цвета фона
     */
    public ColorModificator getBackgroundModificator();

    /**
     * Указывает модификацию цвета фона
     * @param cm модификация цвета фона
     */
    public void setBackgroundModificator( ColorModificator cm );

    /**
     * Создание строки текста с атрибуами отображения
     * @param text текстовое прдеставление значения
     * @param value значение
     * @return Строка с атрибутами
     */
    public AttributedString createAttributedString( String text, Object value );

    /**
     * Клонирование настроек форматирования
     * @return клон формаирования
     */
    public TreeTableNodeFormat clone();

    /**
     * Слияние настоек форматирования
     * @param fmt настройки форматирования
     */
    public void merge( TreeTableNodeFormat fmt );
}
