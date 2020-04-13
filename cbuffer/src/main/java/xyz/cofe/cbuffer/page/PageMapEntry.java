package xyz.cofe.cbuffer.page;

import xyz.cofe.fn.Pair;

/**
 * Информация о событие отображения fast на slow страницу
 */
public interface PageMapEntry extends Pair<Integer,Integer> {
    /**
     * Индекс кэш страницы
     * @return индекс кэш страницы
     */
    public default Integer fastPage(){ return a(); }

    /**
     * Индекс страницы данных
     * @return Индекс страницы данных
     */
    public default Integer slowPage(){ return b(); }

    /**
     * Создание
     * @param fast индекс кэш страницы
     * @param slow индекс страницы данных
     * @return отображение страницы
     */
    public static PageMapEntry of( int fast, int slow ){
        return new PageMapEntry() {
            @Override
            public Integer a(){
                return fast;
            }

            @Override
            public Integer b(){
                return slow;
            }
        };
    }
}
