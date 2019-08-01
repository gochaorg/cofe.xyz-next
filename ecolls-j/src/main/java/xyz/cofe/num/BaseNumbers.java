/*
 * The MIT License
 *
 * Copyright 2018 user.
 *
 * Данная лицензия разрешает, безвозмездно, лицам, получившим копию данного программного
 * обеспечения и сопутствующей документации (в дальнейшем именуемыми "Программное Обеспечение"), 
 * использовать Программное Обеспечение без ограничений, включая неограниченное право на 
 * использование, копирование, изменение, объединение, публикацию, распространение, сублицензирование 
 * и/или продажу копий Программного Обеспечения, также как и лицам, которым предоставляется 
 * данное Программное Обеспечение, при соблюдении следующих условий:
 *
 * Вышеупомянутый копирайт и данные условия должны быть включены во все копии 
 * или значимые части данного Программного Обеспечения.
 *
 * ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ ПРЕДОСТАВЛЯЕТСЯ «КАК ЕСТЬ», БЕЗ ЛЮБОГО ВИДА ГАРАНТИЙ, 
 * ЯВНО ВЫРАЖЕННЫХ ИЛИ ПОДРАЗУМЕВАЕМЫХ, ВКЛЮЧАЯ, НО НЕ ОГРАНИЧИВАЯСЬ ГАРАНТИЯМИ ТОВАРНОЙ ПРИГОДНОСТИ, 
 * СООТВЕТСТВИЯ ПО ЕГО КОНКРЕТНОМУ НАЗНАЧЕНИЮ И НЕНАРУШЕНИЯ ПРАВ. НИ В КАКОМ СЛУЧАЕ АВТОРЫ 
 * ИЛИ ПРАВООБЛАДАТЕЛИ НЕ НЕСУТ ОТВЕТСТВЕННОСТИ ПО ИСКАМ О ВОЗМЕЩЕНИИ УЩЕРБА, УБЫТКОВ 
 * ИЛИ ДРУГИХ ТРЕБОВАНИЙ ПО ДЕЙСТВУЮЩИМ КОНТРАКТАМ, ДЕЛИКТАМ ИЛИ ИНОМУ, ВОЗНИКШИМ ИЗ, ИМЕЮЩИМ 
 * ПРИЧИНОЙ ИЛИ СВЯЗАННЫМ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ ИЛИ ИСПОЛЬЗОВАНИЕМ ПРОГРАММНОГО ОБЕСПЕЧЕНИЯ 
 * ИЛИ ИНЫМИ ДЕЙСТВИЯМИ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ.
 */

package xyz.cofe.num;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kamnev Georgiy
 */
public class BaseNumbers implements Numbers<Number> {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static final Logger logger = Logger.getLogger(BaseNumbers.class.getName());

    private static Level logLevel(){ return logger.getLevel(); }
    
    private static boolean isLogSevere(){ 
        Level logLevel = logger.getLevel();
        return logLevel==null ? true : logLevel.intValue() <= Level.SEVERE.intValue();
    }
    
    private static boolean isLogWarning(){
        Level logLevel = logger.getLevel(); 
        return logLevel==null  ? true : logLevel.intValue() <= Level.WARNING.intValue();
    }
    
    private static boolean isLogInfo(){ 
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.INFO.intValue();
    }
    
    private static boolean isLogFine(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINE.intValue();
    }
    
    private static boolean isLogFiner(){
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINER.intValue();
    }
    
    private static boolean isLogFinest(){ 
        Level logLevel = logger.getLevel();
        return logLevel==null  ? true : logLevel.intValue() <= Level.FINEST.intValue();
    }

    private static void logFine(String message,Object ... args){
        logger.log(Level.FINE, message, args);
    }
    
    private static void logFiner(String message,Object ... args){
        logger.log(Level.FINER, message, args);
    }
    
    private static void logFinest(String message,Object ... args){
        logger.log(Level.FINEST, message, args);
    }
    
    private static void logInfo(String message,Object ... args){
        logger.log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        logger.log(Level.WARNING, message, args);
    }
    
    private static void logSevere(String message,Object ... args){
        logger.log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        logger.log(Level.SEVERE, null, ex);
    }

    private static void logEntering(String method,Object ... params){
        logger.entering(BaseNumbers.class.getName(), method, params);
    }
    
    private static void logExiting(String method){
        logger.exiting(BaseNumbers.class.getName(), method);
    }
    
    private static void logExiting(String method, Object result){
        logger.exiting(BaseNumbers.class.getName(), method, result);
    }
    //</editor-fold>
    
    public static boolean integerNumber( Number n ){
        if( n==null )return false;
        if( n instanceof Byte )return true;
        if( n instanceof Short )return true;
        if( n instanceof Integer )return true;
        if( n instanceof Long )return true;
        if( n instanceof AtomicInteger )return true;
        if( n instanceof AtomicLong )return true;
        if( n instanceof BigInteger )return true;
        return false;
    }
    
    public static boolean ratioNumber( Number n ){
        if( n==null )return false;
        if( n instanceof Float )return true;
        if( n instanceof Double )return true;
        if( n instanceof BigDecimal )return true;
        return false;
    }
    
    public static int bitCount( Number n ){
        if( n==null )return 0;
        if( n instanceof Byte )return 8;
        if( n instanceof Short )return 8*2;
        if( n instanceof Integer )return 8*4;
        if( n instanceof Long )return 8*8;
        if( n instanceof AtomicInteger )return 8*4;
        if( n instanceof AtomicLong )return 8*8;
        if( n instanceof BigInteger ){
            BigInteger bi = (BigInteger)n;
            return bi.bitCount();
        }
        if( n instanceof Float )return 8*4;
        if( n instanceof Double )return 8*8;
        if( n instanceof BigDecimal ){
            BigDecimal bd = (BigDecimal)n;
            return bd.unscaledValue().bitCount() + Long.SIZE;
        }
        return -1;
    }
    
    public static boolean unlimited( Number n ){
        if( n==null )return false;
        if( n instanceof BigInteger )return true;
        if( n instanceof BigDecimal )return true;
        return false;
    }
    
    public static Number cast( Number num, Class base ){
        if( num==null )throw new IllegalArgumentException("num == null");
        if( base==null )throw new IllegalArgumentException("base == null");
        if( base.equals(Byte.class) ){
            return num.byteValue();
        }else if( base.equals(Short.class) ){
            return num.shortValue();
        }else if( base.equals(Integer.class) ){
            return num.intValue();
        }else if( base.equals(Long.class) ){
            return num.longValue();
        }else if( base.equals(Float.class) ){
            return num.floatValue();
        }else if( base.equals(Double.class) ){
            return num.doubleValue();
        }else if( base.equals(BigInteger.class) ){
            if( num instanceof BigInteger )return (BigInteger)num;
            if( num instanceof BigDecimal )return ((BigDecimal)num).toBigInteger();
            return BigInteger.valueOf(num.longValue());
        }else if( base.equals(BigDecimal.class) ){
            if( num instanceof BigInteger )return new BigDecimal( (BigInteger)num );
            if( num instanceof BigDecimal )return num;
            if( num instanceof Long )return new BigDecimal( (Long)num );
            if( num instanceof Integer )return new BigDecimal( (Integer)num );
            return new BigDecimal(num.doubleValue());
        }else if( base.equals(AtomicInteger.class) ){
            return new AtomicInteger(num.intValue());
        }else if( base.equals(AtomicLong.class) ){
            return new AtomicLong(num.longValue());
        }else{
            throw new IllegalArgumentException("unsupported base "+base);
        }
    }
    
    public static SingleBase singleBase( Number n0, int intBitCntMin ){
        if( n0 == null )return null;
        if( unlimited(n0) ){
            if( ratioNumber(n0) ){
                return new SingleBase(new BigDecimalNumbers(), cast(n0, BigDecimal.class));
            }
            return new SingleBase(new BigIntegerNumbers(), cast(n0, BigInteger.class));
        }else if( ratioNumber(n0) ){
            if( n0 instanceof Float )return new SingleBase(new FloatNumbers(), cast(n0, Float.class));
            return new SingleBase(new DoubleNumbers(), cast(n0, Double.class));
        }else{
            int bc = Math.max(bitCount(n0), intBitCntMin);
            if( bc==8 )return new SingleBase(new ByteNumbers(), cast(n0, Byte.class));
            if( bc==8*2 )return new SingleBase(new ShortNumbers(), cast(n0, Short.class));
            if( bc==8*4 )return new SingleBase(new IntegerNumbers(), cast(n0, Integer.class));
            return new SingleBase(new LongNumbers(), cast(n0, Long.class));
        }
    }
    
    public static CommonBase commonBase( Number n0, Number n1, int intBitCntMin ){
        if( n0 == null )return null;
        if( n1 == null )return null;

        if( unlimited(n0) ){
            if( ratioNumber(n0) ){
                // Результат BigDecimal
                return new CommonBase(
                    new BigDecimalNumbers(), 
                    cast(n0, BigDecimal.class), 
                    cast(n1, BigDecimal.class)
                );
            }else{
                if( ratioNumber(n1) ){
                    // Результат BigDecimal
                    return new CommonBase(
                        new BigDecimalNumbers(), 
                        cast(n0, BigDecimal.class), 
                        cast(n1, BigDecimal.class)
                    );
                }else{
                    // Результат BigInteger
                    return new CommonBase(
                        new BigIntegerNumbers(), 
                        cast(n0, BigInteger.class), 
                        cast(n1, BigInteger.class)
                    );
                }
            }
        }else if( unlimited(n1) ){
            if( ratioNumber(n1) ){
                // Результат BigDecimal
                return new CommonBase(
                    new BigDecimalNumbers(), 
                    cast(n0, BigDecimal.class), 
                    cast(n1, BigDecimal.class)
                );
            }else{
                if( ratioNumber(n0) ){
                    // Результат BigDecimal
                    return new CommonBase(
                        new BigDecimalNumbers(), 
                        cast(n0, BigDecimal.class), 
                        cast(n1, BigDecimal.class)
                    );
                }else{
                    // Результат BigInteger
                    return new CommonBase(
                        new BigIntegerNumbers(), 
                        cast(n0, BigInteger.class), 
                        cast(n1, BigInteger.class)
                    );
                }
            }
        }else{
            // n0 и n1 - числа с не бесконечной точностю
            if( n0 instanceof AtomicInteger && n1 instanceof AtomicInteger ){
                return new CommonBase(
                    new AtomicIntegerNumbers(), 
                    cast(n0, AtomicInteger.class), 
                    cast(n1, AtomicInteger.class)
                );
            }else if( n0 instanceof AtomicInteger && n1 instanceof AtomicLong ){
                return new CommonBase(
                    new AtomicLongNumbers(), 
                    cast(n0, AtomicLongNumbers.class), 
                    cast(n1, AtomicLongNumbers.class)
                );
            }else if( n0 instanceof AtomicLong && n1 instanceof AtomicInteger ){
                return new CommonBase(
                    new AtomicLongNumbers(), 
                    cast(n0, AtomicLongNumbers.class), 
                    cast(n1, AtomicLongNumbers.class)
                );
            }else if( n0 instanceof AtomicLong && n1 instanceof AtomicLong ){
                return new CommonBase(
                    new AtomicLongNumbers(), 
                    cast(n0, AtomicLongNumbers.class), 
                    cast(n1, AtomicLongNumbers.class)
                );
            }
            
            if( ratioNumber(n0) || ratioNumber(n1) ){
                // Резуьтат дробное число
                int bitCnt = Math.max(bitCount(n0), bitCount(n1));
                if( bitCnt==8*4 ){
                    return new CommonBase(
                        new FloatNumbers(), 
                        cast(n0, Float.class), 
                        cast(n1, Float.class)
                    );
                }else{
                    return new CommonBase(
                        new DoubleNumbers(), 
                        cast(n0, Double.class), 
                        cast(n1, Double.class)
                    );
                }
            }else{
                // Резуьтат целое число
                int bitCnt = 
                    Math.max(
                        Math.max(bitCount(n0), bitCount(n1)),
                        intBitCntMin
                    );
                
                if( bitCnt==8 ){
                    return new CommonBase(
                        new ByteNumbers(), 
                        cast(n0, Byte.class), 
                        cast(n1, Byte.class)
                    );
                }else if( bitCnt==8*2 ){
                    return new CommonBase(
                        new ShortNumbers(), 
                        cast(n0, Short.class), 
                        cast(n1, Short.class)
                    );
                }else if( bitCnt==8*4 ){
                    return new CommonBase(
                        new IntegerNumbers(), 
                        cast(n0, Integer.class), 
                        cast(n1, Integer.class)
                    );
                }else{
                    return new CommonBase(
                        new LongNumbers(), 
                        cast(n0, Long.class), 
                        cast(n1, Long.class)
                    );
                }
            }
        }
    }
    
    protected final int intBitCntMin;
    protected final Number zero;
    protected final Number one;
    
    public BaseNumbers(){
        intBitCntMin = 8*4;
        zero = 0;
        one = 1;
    }
    
    public BaseNumbers(int intBitCntMin, Number zero, Number one){
        if( zero==null )throw new IllegalArgumentException("zero == null");
        if( one==null )throw new IllegalArgumentException("one == null");
        this.intBitCntMin = intBitCntMin;
        this.zero = zero;
        this.one = one;
    }
    
    public static class BaseNumbersBuilder {
        public BaseNumbers bytes(){ return new BaseNumbers(8, (byte)0 , (byte)1); }
        public BaseNumbers shorts(){ return new BaseNumbers(8*2, (short)0 , (short)1); }
        public BaseNumbers ints(){ return new BaseNumbers(8*4, (int)0 , (int)1); }
        public BaseNumbers longs(){ return new BaseNumbers(8*8, (long)0 , (long)1); }
    }
    
    public static BaseNumbersBuilder base(){
        return new BaseNumbersBuilder();
    }

    @Override
    public Number zero() {
        return zero;
    }

    @Override
    public Number one() {
        return one;
    }

    @Override
    public boolean zero(Number n) {
        if( n==null )return false;
        if( unlimited(n) ){
            if( n instanceof BigDecimal ){
                return new BigDecimalNumbers().zero((BigDecimal)n);
            }else if( n instanceof BigInteger ){
                return new BigIntegerNumbers().zero((BigInteger)n);
            }
            return n.doubleValue()== 0.0;
        }else{
            if( n instanceof Long ){
                return n.longValue() == 0L;
            }else if( n instanceof Integer ){
                return n.intValue() == 0;
            }else if( n instanceof AtomicInteger ){
                return n.intValue() == 0;
            }else if( n instanceof AtomicLong ){
                return n.longValue() == 0;
            }else if( n instanceof Double ){
                return n.doubleValue() == 0.0;
            }else if( n instanceof Float ){
                return n.floatValue()== 0.0;
            }
            return n.intValue() == 0;
        }
    }

    @Override
    public boolean undefined(Number n) {
        if( n==null )return true;
        if( unlimited(n) ){
            return false;
        }else if( ratioNumber(n) ){
            if( n instanceof Double )return ((Double)n).isNaN();
            if( n instanceof Float )return ((Float)n).isNaN();
            return Double.isNaN(n.doubleValue());
        }
        return n==null;
    }

    @Override
    public boolean infinity(Number n) {
        if( n==null )return false;
        if( unlimited(n) ){
            return false;
        }else if( ratioNumber(n) ){
            return Double.isInfinite(n.doubleValue());
        }
        return false;
    }

    @Override
    public Number add(Number a, Number b) {
        if( a==null || b==null )return null;
        return commonBase(a, b, intBitCntMin).add();
    }

    @Override
    public Number sub(Number a, Number b) {
        if( a==null || b==null )return null;
        return commonBase(a, b, intBitCntMin).sub();
    }

    @Override
    public Number mul(Number a, Number b) {
        if( a==null || b==null )return null;
        return commonBase(a, b, intBitCntMin).mul();
    }

    @Override
    public Number div(Number a, Number b) {
        if( a==null || b==null )return null;
        return commonBase(a, b, intBitCntMin).div();
    }

    @Override
    public Number remainder(Number a, Number b) {
        if( a==null || b==null )return null;
        return commonBase(a, b, intBitCntMin).remainder();
    }

    @Override
    public boolean equals(Number a, Number b) {
        if( a==null && b==null )return true;
        if( a==null || b==null )return false;
        return commonBase(a, b, intBitCntMin).equals();
    }

    @Override
    public boolean more(Number a, Number b) {
        if( a==null && b==null )return false;
        if( a==null )return false;
        return commonBase(a, b, intBitCntMin).more();
    }

    @Override
    public boolean less(Number a, Number b) {
        if( a==null && b==null )return false;
        if( a==null )return true;
        return commonBase(a, b, intBitCntMin).less();
    }

    @Override
    public Number next(Number n) {
        if( n==null )return null;
        return singleBase(n, intBitCntMin).next(n);
    }

    @Override
    public Number prev(Number n) {
        if( n==null )return null;
        return singleBase(n, intBitCntMin).prev(n);
    }
}
