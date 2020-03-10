package xyz.cofe.text.tparse;

import java.util.Optional;

/**
 * Указатель на список символов/лексем
 * @param <POS> Тип указателя (число или номер строки+номер колонки)
 * @param <TOK> Тип лексемы
 * @param <SELF> Собственный тип
 */
public interface Pointer<TOK,POS,SELF extends Pointer<TOK,POS,SELF>> extends Comparable<SELF> {
    /**
     * Проверка что указаетль находиться за границей списка
     * @return true - за границей списка
     */
    boolean eof();

    /**
     * Получение значения текущего указателя
     * @return указатель
     */
    POS position();

    /**
     * Перемещение указателя n позиций вперед/назад
     * @param pos кол-во позиций
     * @return Новый указатель
     */
    SELF move(POS pos);

    /**
     * Предпросмотр n-ой лексемы относительно текущего указателя
     * @param pos Номер лексемы/символа
     * @return Лексема или символ
     */
    Optional<TOK> lookup(POS pos);
}
