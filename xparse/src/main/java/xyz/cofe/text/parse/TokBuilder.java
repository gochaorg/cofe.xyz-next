package xyz.cofe.text.parse;

import java.util.function.Function;

/**
 * Построение лексических анализаторов
 */
public class TokBuilder {
    /**
     * Последовательность
     * @param exprs выражения последовательности
     * @return парсер
     */
    @SafeVarargs
    public static Sequence<CharPointer,Token> sequence( Function<CharPointer, Token>... exprs ){
        if( exprs==null ) throw new IllegalArgumentException("exprs==null");
        return new Sequence<>(exprs, (begin,end,toks) -> new Token(begin,end));
    }

    /**
     * Повтор правила
     * @param exp правило
     * @return парсер
     */
    public static Repeat<CharPointer,Token> repeat( Function<CharPointer, Token> exp ){
        if( exp==null ) throw new IllegalArgumentException("exp==null");
        return new Repeat<>(exp, (begin,end,toks) -> new Token(begin,end));
    }

    /**
     * Альтернативный набор правил
     * @param <T> тип токенов
     * @param expressions правила
     * @return парсер
     */
    @SafeVarargs
    public static <T extends Token> CharAlternatives<T> alt( Function<CharPointer, Token> ... expressions ){
        if( expressions==null ) throw new IllegalArgumentException("expressions==null");
        return new CharAlternatives<>(expressions);
    }
}
