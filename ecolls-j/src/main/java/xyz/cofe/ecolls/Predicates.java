package xyz.cofe.ecolls;

import java.util.function.Predicate;

/**
 * Набор общих предикатов
 */
public class Predicates {
    /**
     * Предикат возвращает true, если НЕ нулевая ссылка
     * @param <T> Тип объекта
     * @return предикат
     */
    public static <T> Predicate<T> isNotNull()
    {
        return value->value!=null;
    }

    /**
     * Предикат возвращает true, если нулевая ссылка
     * @param <T> Тип объекта
     * @return предикат
     */
    public static <T> Predicate<T> isNull()
    {
        return value->value==null;
    }

//    /**
//     * Предикат возвращает true, если объект находиться в указанной последовательности
//     * @param <T> Тип объекта
//     * @param src Последовательность
//     * @return предикат
//     */
//    public static <T> Predicate<T> in(Iterable src)
//    {
//        if (src == null) {
//            throw new IllegalArgumentException("src == null");
//        }
//        final Iterable fsrc = src;
//        return new Predicate<T>()
//        {
//            @Override
//            public boolean validate(T value)
//            {
//                return Iterators.in(value, fsrc);
//            }
//        };
//    }

    /**
     * Предикат возвращает true, если объект равен указанному объекту
     * @param <T> Тип объекта
     * @param value Образец
     * @return предикат
     */
    public static <T> Predicate<T> isEquals(Object value)
    {
        final Object fvalue = value;
        return value1->{
            if( fvalue==null && value1==null )return true;
            if( fvalue!=null && value1==null )return false;
            if( fvalue==null && value1!=null )return false;
            return fvalue.equals(value1);
        };
    }

    /**
     * Предикат возвращает true, если предикат A(объект) и предикат B(объект) возвращают true
     * @param <T> Тип объекта
     * @param a предикаты (A,B,C,...)
     * @return предикат
     */
    public static <T> Predicate<T> and(Predicate<T> ... a)
    {
        if (a == null) {
            throw new IllegalArgumentException("a == null");
        }
        final Predicate[] fa = a;
        return value->{
            if( fa==null )return false;
            int co = 0;
            for( Predicate<T> p : fa ){
                if( p==null )continue;
                co++;
                if( !p.test(value) ){
                    return false;
                }
            }
            return co > 0;
        };
    }

    /**
     * Предикат возвращает true, если предикат A(объект) и предикат B(объект) возвращают true
     * @param <T> Тип объекта
     * @param a предикаты (A,B,C,...)
     * @return предикат
     */
    public static <T> Predicate<T> or(Predicate<T> ... a)
    {
        if (a == null) {
            throw new IllegalArgumentException("a == null");
        }
        final Predicate[] fa = a;
        return value->{
            if( fa==null )return false;
            for( Predicate<T> p : fa ){
                if( p==null )continue;
                if( p.test(value) ){
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Предикат возвращает true, если предикат A(объект) возвращает false
     * @param <T> Тип объекта
     * @param a предикат A(объект)
     * @return предикат
     */
    public static <T> Predicate<T> not(Predicate<T> a)
    {
        if (a == null) {
            throw new IllegalArgumentException("a == null");
        }
        final Predicate fa = a;
        return value->!fa.test(value);
    }

    /**
     * Предикат возвращает true, если предикат A(объект) (НЕ ИЛИ) предикат B(объект) возвращают true
     * @param <T> Тип объекта
     * @param a предикат A(объект)
     * @param b предикат B(объект)
     * @return предикат
     */
    public static <T> Predicate<T> xor( Predicate<T> a, Predicate<T> b)
    {
        if (a == null) {
            throw new IllegalArgumentException("a == null");
        }
        if (b == null) {
            throw new IllegalArgumentException("b == null");
        }
        final Predicate fa = a;
        final Predicate fb = b;
        return value->{
            boolean va = fa.test(value);
            boolean vb = fb.test(value);
            return !(va==vb);
        };
    }
}
