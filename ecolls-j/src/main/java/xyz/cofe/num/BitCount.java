package xyz.cofe.num;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Кол-во бит на число
 */
public enum BitCount {
    /**
     * 1 Байт = 8 Бит
     */
    BYTE(8),

    /**
     * 2 Байта
     */
    SHORT(8*2),

    /**
     * 4 Байта
     */
    INT(8*4),

    /**
     * 8 Байт
     */
    LONG(8*8),

    /**
     * Большие числа (BigInteger/BigDecimal)
     */
    BIG(8*80)
    ;

    /**
     * Кол-во бит на число
     */
    public final int bits;

    /**
     * Конструктор
     * @param bits кол-во бит на число
     */
    BitCount(int bits){
        this.bits = bits;
    }

    /**
     * Возвращает приблизительное кол-во используемых бит для указанного числа
     * @param n число
     * @return кол-во бит или null если не возможно определить
     */
    public static BitCount of( Number n ){
        if( n==null )return null;
        if( n instanceof Byte )return BYTE;
        if( n instanceof Short )return SHORT;
        if( n instanceof Integer )return INT;
        if( n instanceof Long )return LONG;
        if( n instanceof AtomicInteger )return INT;
        if( n instanceof AtomicLong )return LONG;
        if( n instanceof BigInteger ){
            return BIG;
        }
        if( n instanceof Float )return INT;
        if( n instanceof Double )return LONG;
        if( n instanceof BigDecimal ){
            return BIG;
        }
        return null;
    }

    /**
     * Возвращает максимальное преблизительное кол-во бит для указанных чисел,
     * если нет возможности определить {@link #of(Number)}:
     * <ul>
     *     <li>есть хоть одно значение, то испольуеться оно</li>
     *     <li>если отсуствуют оба, то используется INT</li>
     * </ul>
     * @param n1 число
     * @param n2 число
     * @return кол-во бит
     */
    public static BitCount max( Number n1, Number n2 ){
        BitCount bc1 = of(n1);
        BitCount bc2 = of(n2);

        if( bc1==null && bc2!=null )return bc2;
        if( bc1!=null && bc2==null )return bc1;

        bc1 = bc1!=null ? bc1 : INT;
        bc2 = bc2!=null ? bc2 : INT;

        if( bc1.bits > bc2.bits )return bc1;
        return bc2;
    }
}
