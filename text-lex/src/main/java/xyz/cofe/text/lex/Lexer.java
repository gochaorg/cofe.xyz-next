package xyz.cofe.text.lex;

import xyz.cofe.text.lex.Token;

import java.util.List;
import java.util.function.Consumer;

/**
 * Интерфес лексического анализатора
 * @author gocha
 */
public interface Lexer
{
    /**
     * Анализ входной цепочки символов
     * @param source Источник
     * @param errorReciver Прием ошибок
     * @return Цепочка токенов
     */
    public List<Token> parse( String source, Consumer<String> errorReciver);
}
