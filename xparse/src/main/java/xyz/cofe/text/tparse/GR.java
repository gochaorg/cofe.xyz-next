package xyz.cofe.text.tparse;

import xyz.cofe.fn.Fn2;
import xyz.cofe.fn.Fn3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Граматическое правило - это токое правло согласно которому входная
 * последовтельность символов либо принадлежит (совпадает) с данным правилом , либо нет.
 * <p>
 * Примеры правил:
 * <br>
 *     Правило идентификаторов языка java:
 * <ul>
 *   <li>идентифактор может содержать буквы, цифры и знак подчеркивания</li>
 *   <li>идентифактор должен начинаться с буквы или знака подчеркивания</li>
 * </ul>
 * Данное правило может быть записано в форме <a href="https://ru.wikipedia.org/wiki/%D0%A4%D0%BE%D1%80%D0%BC%D0%B0_%D0%91%D1%8D%D0%BA%D1%83%D1%81%D0%B0_%E2%80%94_%D0%9D%D0%B0%D1%83%D1%80%D0%B0">Бэкуса Нура</a>:
 * <br>
 * <code>идентификатор ::= (буква | подчеркивание) {буква | цифра | подчеркивание}</code>
 * <br>
 *     Где:
 *     <ul>
 *         <li>Круглые скобки <b>()</b> - задают</li> группу правил
 *         <li>Фигурные скобки <b>{}</b> - задают, что содержание может повторяться 0 и более раз</li>
 *         <li>Вертикальная черта <b>|</b> - задает альтернативное правило</li>
 *     </ul>
 * <p>
 * В коде java может быть представленно так:
 * <pre>
 * GR letter = ...;
 * GR digit  = ...;
 * GR underscope  = ...;
 * GR firstIdSymbol = letter.another( underscope ).map();
 * GR otherIdSymbol = letter.another( underscope ).map().another( digit ).map();
 * GR id = firstIdSymbol.next(otherIdSymbol.repeat().map()).map().another(
 *           firstIdSymbol
 *         ).map()
 * </pre>
 * @param <P> Указатель на входную последовательность символов
 * @param <T> Токен/Лексема
 */
public interface GR<P extends Pointer<?,?,P>, T extends Tok<P>> extends Function<P, Optional<T>> {
    /**
     * Создает новое правило - последовательность правил:
     * текущего и последущего правила для анализа цепочкивходых символов.
     * <br>
     *     Допустим текущее правило задает firstIdSymbol,
     *     то для создания id создадим правило id = this.next( otherIdSymbol.repeat() )
     * @param then следующее правило
     * @param <U> тип лексемы
     * @return Последовательность правил
     */
    default <U extends Tok<P>> Sq2OP<P,T,U> next(GR<P,U> then) {
        if( then==null )throw new IllegalArgumentException("then==null");
        return new Sq2OPImpl<>(this,then);
    }

    /**
     * Создает правило из текущего - которое говорит о повторе 1 и более раз
     * @return Правило повтора
     */
    default RptOP<P,T> repeat(){
        return new RptOPImpl<>(this,0,0,true);
    }

    /**
     * Правило альтрентивного выбора -
     * т.е. когда входная последовательность символов может быть распознана текущим правилом или
     * альтернативным
     * @param rule алтернатиное правило
     * @param <U> токен
     * @return Альтернативное правило
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default <U extends Tok<P>> AltOP<P,U> another(GR<P,? extends Tok<P>> rule) {
        if( rule==null )throw new IllegalArgumentException("rule == null");

        @SuppressWarnings("rawtypes") GR self = this;
        return new AltOPImpl( self, rule );
    }
}
