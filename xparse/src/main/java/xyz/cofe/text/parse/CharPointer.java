package xyz.cofe.text.parse.toks;

import xyz.cofe.text.parse.Pointer;

/**
 * Указатель по тексту
 */
public interface CharPointer extends Pointer {
    /**
     * Возвращает текущий указатель в тексте
     * @return указатель
     */
    int pointer();

    /**
     * Возвращает признак что достигнут конец текста
     * @return true - достигнут конец текста
     */
    boolean eof();

    /**
     * Просмотр текста на заданное кол0во символов относительно указателя
     * @param length кол-во просматриваемых символов
     * @return текст
     */
    String lookup( int length );

    /**
     * Просмотр одного символа относительно указателя
     * @return символ
     */
    char lookup();

    /**
     * Перемещение указателя на заданное кол-во символов вперед
     * @param offset смещение указателя
     * @return новый указатель
     */
    CharPointer move( int offset );
}
