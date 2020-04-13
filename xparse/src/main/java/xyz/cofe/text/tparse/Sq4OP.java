package xyz.cofe.text.tparse;

import xyz.cofe.fn.Fn4;

/**
 * Конструктор грамматического правила - последовательности
 * @param <P> Тип указателя
 * @param <T1> Токен первого правила
 * @param <T2> Токен второго правила
 * @param <T3> Токен третьего правила
 * @param <T4> Токен 4го правила
 */
public interface Sq4OP<
        P extends Pointer<?,?,P>,
        T1 extends Tok<P>,
        T2 extends Tok<P>,
        T3 extends Tok<P>,
        T4 extends Tok<P>
        > {
    /**
     * Отображения реузльтата совпадения на токен
     * @param map функция отображения
     * @param <U> тип токена
     * @return грамматическое правило разбора
     */
    <U extends Tok<P>> GR<P,U> map(Fn4<T1, T2, T3, T4, U> map);

    /**
     * Создание последовательности правил
     * @param then следующее правило
     * @param <U> тип токена следующего правила
     * @return конструктор последовательности
     */
    <U extends Tok<P>> Sq5OP<P,T1,T2,T3,T4,U> next(GR<P,U> then );
}
