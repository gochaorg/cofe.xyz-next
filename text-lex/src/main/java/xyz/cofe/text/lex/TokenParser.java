package xyz.cofe.text.lex;

import xyz.cofe.text.lex.Token;

/**
 * Разпознователь токена из цепочки символов
 * @author gocha
 */
public interface TokenParser
{
    /**
     * Возвращает токен если ему соответ цепочка сиволов в указаной точке
     * @param source Цепочка символов
     * @param offset Смещение от начала цепочки символов
     * @return Токен или null, если нет совпадения
     */
    Token parse( String source, int offset);
}
