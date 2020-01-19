package xyz.cofe.text.template;

import xyz.cofe.fn.Fn1;

/**
 * Построение функции форматирования
 * @author user
 */
public interface FormatBuilder {
    /**
     * Создание функции форматирования
     * @param <T> Тип объекта-контекста переменных шаблона
     * @param template Шаблон
     * @param setContext Установка контекста
     * @param evalCode Функция интерпретации кода шаблона
     * @return Функция форматирования
     */
    <T> Fn1<String,T> build(
        BasicTemplate template,
        Fn1<T,Object> setContext,
        Fn1<String,String> evalCode
    );
}
