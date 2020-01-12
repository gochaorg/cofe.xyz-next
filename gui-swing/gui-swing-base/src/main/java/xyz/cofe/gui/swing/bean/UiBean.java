package xyz.cofe.gui.swing.bean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UiBean {
    /**
     * Категория свойства (в текущий момент не реализована в PropertiesPanel)
     * @return категория
     */
    String category() default "";

    /**
     * Краткое описание
     * @return краткое описание
     */
    String shortDescription() default "";

    /**
     * Описание в формате html
     * @return html текст
     */
    String htmlDescription() default "";

    /**
     * Отображаемое имя
     * @return отображаемое имя
     */
    String displayName() default "";

    /**
     * Редактор свойства
     * @return редактор свойства
     */
    Class<? extends java.beans.PropertyEditor> propertyEditor() default java.beans.PropertyEditor.class;

    /**
     * Использовать только для чтения
     * @return true - использовать только для чтения
     */
    boolean forceReadOnly() default false;

    /**
     * Использовать только для чтения включая потомков
     * @return true - использовать только для чтения включая потомков
     */
    boolean forceReadOnlyDescent() default false;

    /**
     * Не отображать свойтсво в редакторе
     * @return true - скрытое свойство
     */
    boolean forceHidden() default false;

    /**
     * Список скрытых свойств
     * @return скрываемые свойства
     */
    String[] hiddenPeroperties() default {};

    /**
     * Свойство не можут быть null значением
     * @return true - свойство не может быть null значением
     */
    boolean forceNotNull() default false;

    /**
     * Опции редактора для этого свойства
     * @return опции
     */
    String editorOpts() default "";
}
