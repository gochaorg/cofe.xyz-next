package xyz.cofe.text.parse;

/**
 * Указатель по тексту
 */
public interface CharPointer extends CommonPointer<CharPointer,Character,Integer> {
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
    String text( int length );

}
