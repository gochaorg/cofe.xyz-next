package xyz.cofe.text.tparse;

import java.util.Objects;
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

    /**
     * Выбор минимального указателя
     * @param ptrs указатели, должен быть указан хотя бы один
     * @param <TOK> лексема (тип)
     * @param <POS> позиция (тип)
     * @param <SELF> указатель
     * @return минимальный указатель
     */
    public static <TOK,POS,SELF extends Pointer<TOK,POS,SELF>> SELF min( SELF ... ptrs ){
        if( ptrs==null )throw new IllegalArgumentException("ptrs==null");
        if( ptrs.length<1 )throw new IllegalArgumentException("ptrs.length<1");
        if( ptrs.length==1 )return ptrs[0];
        SELF r = ptrs[0];
        for( int i=1; i<ptrs.length; i++ ){
            SELF x = ptrs[i];
            if( x==null )throw new IllegalArgumentException("ptrs["+i+"]==null");
            if( r.compareTo(x)>0 ){
                r = x;
            }
        }
        return r;
    }

    /**
     * Выбор максимальный указателя
     * @param ptrs указатели, должен быть указан хотя бы один
     * @param <TOK> лексема (тип)
     * @param <POS> позиция (тип)
     * @param <SELF> указатель
     * @return максимальный указатель
     */
    public static <TOK,POS,SELF extends Pointer<TOK,POS,SELF>> SELF max( SELF ... ptrs ){
        if( ptrs==null )throw new IllegalArgumentException("ptrs==null");
        if( ptrs.length<1 )throw new IllegalArgumentException("ptrs.length<1");
        if( ptrs.length==1 )return ptrs[0];
        SELF r = ptrs[0];
        for( int i=1; i<ptrs.length; i++ ){
            SELF x = ptrs[i];
            if( x==null )throw new IllegalArgumentException("ptrs["+i+"]==null");
            if( r.compareTo(x)<0 ){
                r = x;
            }
        }
        return r;
    }
}
