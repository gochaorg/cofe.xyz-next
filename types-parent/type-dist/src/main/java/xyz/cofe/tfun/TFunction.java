package xyz.cofe.tfun;

import xyz.cofe.fn.*;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Функция с описанием типов
 */
public interface TFunction {
    /**
     * Возвращает типы данных функции
     *
     * @return массив: [ Аргументы, Реузультат ]
     */
    Class<?>[] types();

    /**
     * Возвращает типы аргментов функции
     *
     * @return массив типов аргментов
     */
    default Class<?>[] input(){
        Class<?>[] ts = types();
        if( ts.length < 2 ) return new Class[0];
        return Arrays.copyOf(ts, ts.length - 1);
    }

    /**
     * Возвращает тип результата
     *
     * @return тип результата
     */
    default Class<?> returns(){
        Class<?>[] ts = types();
        if( ts.length > 0 ) return ts[ts.length - 1];
        throw new IllegalStateException("function no return");
    }

    /**
     * Вызов функции
     *
     * @param args аргументы
     * @return рузельтат
     */
    Object call( Object... args );

    //region Создание функций от 1 до 5 аргументов
    /**
     * Создание функции от одного аргумента
     *
     * @param arg тип аргумента
     * @param res тип результата
     * @param fn  функция
     * @param <A> тип аргумента
     * @param <Z> тип результата
     * @return функция с описанием типов
     */
    public static <A, Z> TFunction of( Class<A> arg, Class<Z> res, Function<A, Z> fn ){
        if( arg == null ) throw new IllegalArgumentException("arg==null");
        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{ arg, res };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 1 ) throw new IllegalArgumentException("args.length!=1");
                //noinspection unchecked
                return fn.apply((A) args[0]);
            }
        };
    }

    /**
     * Создание функции от 2-ух аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A, B, Z> TFunction of( Class<A> arg0, Class<B> arg1, Class<Z> res, BiFunction<A, B, Z> fn ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");
        Class<?>[] ts = new Class[]{ arg0, arg1, res };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 2 ) throw new IllegalArgumentException("args.length!=2");
                return fn.apply((A) args[0], (B) args[1]);
            }
        };
    }

    /**
     * Создание функции от 3-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A, B, C, Z> TFunction of( Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<Z> res, Fn3<A, B, C, Z> fn ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");
        Class<?>[] ts = new Class[]{ arg0, arg1, arg2, res };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 3 ) throw new IllegalArgumentException("args.length!=3");
                return fn.apply((A) args[0], (B) args[1], (C) args[2]);
            }
        };
    }

    /**
     * Создание функции от 4-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A, B, C, D, Z> TFunction of( Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<Z> res, Fn4<A, B, C, D, Z> fn ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");
        Class<?>[] ts = new Class[]{ arg0, arg1, arg2, arg3, res };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 4 ) throw new IllegalArgumentException("args.length!=4");
                return fn.apply((A) args[0], (B) args[1], (C) args[2], (D) args[3]);
            }
        };
    }

    /**
     * Создание функции от 5-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A, B, C, D, E, Z> TFunction of( Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4, Class<Z> res, Fn5<A, B, C, D, E, Z> fn ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg3==null");
        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");
        Class<?>[] ts = new Class[]{ arg0, arg1, arg2, arg3, arg4, res };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 5 ) throw new IllegalArgumentException("args.length!=5");
                return fn.apply((A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4]);
            }
        };
    }
    //endregion

    //region Создание функции от 6 .. 9 -х аргументов
    /**
     * Создание функции от 6-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5,
        Class<Z> res,
        Fn6<A,B,C,D,E,F,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 6 ) throw new IllegalArgumentException("args.length!=6");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5]
                );
            }
        };
    }

    /**
     * Создание функции от 7-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6,
        Class<Z> res,
        Fn7<A,B,C,D,E,F,G,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 7 ) throw new IllegalArgumentException("args.length!=7");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6]
                );
            }
        };
    }

    /**
     * Создание функции от 8-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7,
        Class<Z> res,
        Fn8<A,B,C,D,E,F,G,H,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 8 ) throw new IllegalArgumentException("args.length!=8");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7]
                );
            }
        };
    }

    /**
     * Создание функции от 9-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8,
        Class<Z> res,
        Fn9<A,B,C,D,E,F,G,H,I,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 9 ) throw new IllegalArgumentException("args.length!=9");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8]
                );
            }
        };
    }
    //endregion

    //region Создание функции от 10 .. 14 -х аргументов
    /**
     * Создание функции от 10-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<Z> res,
        Fn10<A,B,C,D,E,F,G,H,I,J,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 10 ) throw new IllegalArgumentException("args.length!=10");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9]
                );
            }
        };
    }

    /**
     * Создание функции от 11-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10,
        Class<Z> res,
        Fn11<A,B,C,D,E,F,G,H,I,J,K,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 11 ) throw new IllegalArgumentException("args.length!=11");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10]
                );
            }
        };
    }

    /**
     * Создание функции от 12-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param arg11 тип 12-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <L>  тип 12-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10, Class<L> arg11,
        Class<Z> res,
        Fn12<A,B,C,D,E,F,G,H,I,J,K,L,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");
        if( arg11 == null ) throw new IllegalArgumentException("arg11==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10, arg11,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 12 ) throw new IllegalArgumentException("args.length!=12");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10], (L) args[11]
                );
            }
        };
    }

    /**
     * Создание функции от 13-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param arg11 тип 12-го аргумента
     * @param arg12 тип 13-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <L>  тип 12-го аргумента
     * @param <M>  тип 13-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10, Class<L> arg11, Class<M> arg12,
        Class<Z> res,
        Fn13<A,B,C,D,E,F,G,H,I,J,K,L,M,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");
        if( arg11 == null ) throw new IllegalArgumentException("arg11==null");
        if( arg12 == null ) throw new IllegalArgumentException("arg12==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10, arg11, arg12,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 13 ) throw new IllegalArgumentException("args.length!=13");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10], (L) args[11], (M) args[12]
                );
            }
        };
    }

    /**
     * Создание функции от 14-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param arg11 тип 12-го аргумента
     * @param arg12 тип 13-го аргумента
     * @param arg13 тип 14-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <L>  тип 12-го аргумента
     * @param <M>  тип 13-го аргумента
     * @param <N>  тип 14-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10, Class<L> arg11, Class<M> arg12, Class<N> arg13,
        Class<Z> res,
        Fn14<A,B,C,D,E,F,G,H,I,J,K,L,M,N,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");
        if( arg11 == null ) throw new IllegalArgumentException("arg11==null");
        if( arg12 == null ) throw new IllegalArgumentException("arg12==null");
        if( arg13 == null ) throw new IllegalArgumentException("arg13==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10, arg11, arg12, arg13,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 14 ) throw new IllegalArgumentException("args.length!=14");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10], (L) args[11], (M) args[12], (N) args[13]
                );
            }
        };
    }
    //endregion

    //region Создание функции от 15 .. 20 -х аргументов
    /**
     * Создание функции от 15-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param arg11 тип 12-го аргумента
     * @param arg12 тип 13-го аргумента
     * @param arg13 тип 14-го аргумента
     * @param arg14 тип 15-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <L>  тип 12-го аргумента
     * @param <M>  тип 13-го аргумента
     * @param <N>  тип 14-го аргумента
     * @param <O>  тип 15-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10, Class<L> arg11, Class<M> arg12, Class<N> arg13, Class<O> arg14,
        Class<Z> res,
        Fn15<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");
        if( arg11 == null ) throw new IllegalArgumentException("arg11==null");
        if( arg12 == null ) throw new IllegalArgumentException("arg12==null");
        if( arg13 == null ) throw new IllegalArgumentException("arg13==null");
        if( arg14 == null ) throw new IllegalArgumentException("arg14==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10, arg11, arg12, arg13, arg14,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 15 ) throw new IllegalArgumentException("args.length!=15");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10], (L) args[11], (M) args[12], (N) args[13], (O) args[14]
                );
            }
        };
    }

    /**
     * Создание функции от 16-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param arg11 тип 12-го аргумента
     * @param arg12 тип 13-го аргумента
     * @param arg13 тип 14-го аргумента
     * @param arg14 тип 15-го аргумента
     * @param arg15 тип 16-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <L>  тип 12-го аргумента
     * @param <M>  тип 13-го аргумента
     * @param <N>  тип 14-го аргумента
     * @param <O>  тип 15-го аргумента
     * @param <P>  тип 16-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10, Class<L> arg11, Class<M> arg12, Class<N> arg13, Class<O> arg14,
        Class<P> arg15,
        Class<Z> res,
        Fn16<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");
        if( arg11 == null ) throw new IllegalArgumentException("arg11==null");
        if( arg12 == null ) throw new IllegalArgumentException("arg12==null");
        if( arg13 == null ) throw new IllegalArgumentException("arg13==null");
        if( arg14 == null ) throw new IllegalArgumentException("arg14==null");
        if( arg15 == null ) throw new IllegalArgumentException("arg15==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10, arg11, arg12, arg13, arg14,
            arg15,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 16 ) throw new IllegalArgumentException("args.length!=16");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10], (L) args[11], (M) args[12], (N) args[13], (O) args[14],
                    (P) args[15]
                );
            }
        };
    }

    /**
     * Создание функции от 17-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param arg11 тип 12-го аргумента
     * @param arg12 тип 13-го аргумента
     * @param arg13 тип 14-го аргумента
     * @param arg14 тип 15-го аргумента
     * @param arg15 тип 16-го аргумента
     * @param arg16 тип 17-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <L>  тип 12-го аргумента
     * @param <M>  тип 13-го аргумента
     * @param <N>  тип 14-го аргумента
     * @param <O>  тип 15-го аргумента
     * @param <P>  тип 16-го аргумента
     * @param <Q>  тип 17-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10, Class<L> arg11, Class<M> arg12, Class<N> arg13, Class<O> arg14,
        Class<P> arg15, Class<Q> arg16,
        Class<Z> res,
        Fn17<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");
        if( arg11 == null ) throw new IllegalArgumentException("arg11==null");
        if( arg12 == null ) throw new IllegalArgumentException("arg12==null");
        if( arg13 == null ) throw new IllegalArgumentException("arg13==null");
        if( arg14 == null ) throw new IllegalArgumentException("arg14==null");
        if( arg15 == null ) throw new IllegalArgumentException("arg15==null");
        if( arg16 == null ) throw new IllegalArgumentException("arg16==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10, arg11, arg12, arg13, arg14,
            arg15, arg16,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 17 ) throw new IllegalArgumentException("args.length!=17");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10], (L) args[11], (M) args[12], (N) args[13], (O) args[14],
                    (P) args[15], (Q) args[16]
                );
            }
        };
    }

    /**
     * Создание функции от 18-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param arg11 тип 12-го аргумента
     * @param arg12 тип 13-го аргумента
     * @param arg13 тип 14-го аргумента
     * @param arg14 тип 15-го аргумента
     * @param arg15 тип 16-го аргумента
     * @param arg16 тип 17-го аргумента
     * @param arg17 тип 18-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <L>  тип 12-го аргумента
     * @param <M>  тип 13-го аргумента
     * @param <N>  тип 14-го аргумента
     * @param <O>  тип 15-го аргумента
     * @param <P>  тип 16-го аргумента
     * @param <Q>  тип 17-го аргумента
     * @param <R>  тип 18-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10, Class<L> arg11, Class<M> arg12, Class<N> arg13, Class<O> arg14,
        Class<P> arg15, Class<Q> arg16, Class<R> arg17,
        Class<Z> res,
        Fn18<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");
        if( arg11 == null ) throw new IllegalArgumentException("arg11==null");
        if( arg12 == null ) throw new IllegalArgumentException("arg12==null");
        if( arg13 == null ) throw new IllegalArgumentException("arg13==null");
        if( arg14 == null ) throw new IllegalArgumentException("arg14==null");
        if( arg15 == null ) throw new IllegalArgumentException("arg15==null");
        if( arg16 == null ) throw new IllegalArgumentException("arg16==null");
        if( arg17 == null ) throw new IllegalArgumentException("arg17==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10, arg11, arg12, arg13, arg14,
            arg15, arg16, arg17,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 18 ) throw new IllegalArgumentException("args.length!=18");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10], (L) args[11], (M) args[12], (N) args[13], (O) args[14],
                    (P) args[15], (Q) args[16], (R) args[17]
                );
            }
        };
    }

    /**
     * Создание функции от 19-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param arg11 тип 12-го аргумента
     * @param arg12 тип 13-го аргумента
     * @param arg13 тип 14-го аргумента
     * @param arg14 тип 15-го аргумента
     * @param arg15 тип 16-го аргумента
     * @param arg16 тип 17-го аргумента
     * @param arg17 тип 18-го аргумента
     * @param arg18 тип 19-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <L>  тип 12-го аргумента
     * @param <M>  тип 13-го аргумента
     * @param <N>  тип 14-го аргумента
     * @param <O>  тип 15-го аргумента
     * @param <P>  тип 16-го аргумента
     * @param <Q>  тип 17-го аргумента
     * @param <R>  тип 18-го аргумента
     * @param <S>  тип 19-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10, Class<L> arg11, Class<M> arg12, Class<N> arg13, Class<O> arg14,
        Class<P> arg15, Class<Q> arg16, Class<R> arg17, Class<S> arg18,
        Class<Z> res,
        Fn19<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");
        if( arg11 == null ) throw new IllegalArgumentException("arg11==null");
        if( arg12 == null ) throw new IllegalArgumentException("arg12==null");
        if( arg13 == null ) throw new IllegalArgumentException("arg13==null");
        if( arg14 == null ) throw new IllegalArgumentException("arg14==null");
        if( arg15 == null ) throw new IllegalArgumentException("arg15==null");
        if( arg16 == null ) throw new IllegalArgumentException("arg16==null");
        if( arg17 == null ) throw new IllegalArgumentException("arg17==null");
        if( arg18 == null ) throw new IllegalArgumentException("arg18==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10, arg11, arg12, arg13, arg14,
            arg15, arg16, arg17, arg18,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 19 ) throw new IllegalArgumentException("args.length!=19");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10], (L) args[11], (M) args[12], (N) args[13], (O) args[14],
                    (P) args[15], (Q) args[16], (R) args[17], (S) args[18]
                );
            }
        };
    }

    /**
     * Создание функции от 20-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param arg11 тип 12-го аргумента
     * @param arg12 тип 13-го аргумента
     * @param arg13 тип 14-го аргумента
     * @param arg14 тип 15-го аргумента
     * @param arg15 тип 16-го аргумента
     * @param arg16 тип 17-го аргумента
     * @param arg17 тип 18-го аргумента
     * @param arg18 тип 19-го аргумента
     * @param arg19 тип 20-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <L>  тип 12-го аргумента
     * @param <M>  тип 13-го аргумента
     * @param <N>  тип 14-го аргумента
     * @param <O>  тип 15-го аргумента
     * @param <P>  тип 16-го аргумента
     * @param <Q>  тип 17-го аргумента
     * @param <R>  тип 18-го аргумента
     * @param <S>  тип 19-го аргумента
     * @param <T>  тип 20-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10, Class<L> arg11, Class<M> arg12, Class<N> arg13, Class<O> arg14,
        Class<P> arg15, Class<Q> arg16, Class<R> arg17, Class<S> arg18, Class<T> arg19,
        Class<Z> res,
        Fn20<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");
        if( arg11 == null ) throw new IllegalArgumentException("arg11==null");
        if( arg12 == null ) throw new IllegalArgumentException("arg12==null");
        if( arg13 == null ) throw new IllegalArgumentException("arg13==null");
        if( arg14 == null ) throw new IllegalArgumentException("arg14==null");
        if( arg15 == null ) throw new IllegalArgumentException("arg15==null");
        if( arg16 == null ) throw new IllegalArgumentException("arg16==null");
        if( arg17 == null ) throw new IllegalArgumentException("arg17==null");
        if( arg18 == null ) throw new IllegalArgumentException("arg18==null");
        if( arg19 == null ) throw new IllegalArgumentException("arg19==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10, arg11, arg12, arg13, arg14,
            arg15, arg16, arg17, arg18, arg19,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 20 ) throw new IllegalArgumentException("args.length!=20");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10], (L) args[11], (M) args[12], (N) args[13], (O) args[14],
                    (P) args[15], (Q) args[16], (R) args[17], (S) args[18], (T) args[19]
                );
            }
        };
    }
    //endregion

    //region Создание функции от 21-х .. 25  аргументов
    /**
     * Создание функции от 21-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param arg11 тип 12-го аргумента
     * @param arg12 тип 13-го аргумента
     * @param arg13 тип 14-го аргумента
     * @param arg14 тип 15-го аргумента
     * @param arg15 тип 16-го аргумента
     * @param arg16 тип 17-го аргумента
     * @param arg17 тип 18-го аргумента
     * @param arg18 тип 19-го аргумента
     * @param arg19 тип 20-го аргумента
     * @param arg20 тип 21-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <L>  тип 12-го аргумента
     * @param <M>  тип 13-го аргумента
     * @param <N>  тип 14-го аргумента
     * @param <O>  тип 15-го аргумента
     * @param <P>  тип 16-го аргумента
     * @param <Q>  тип 17-го аргумента
     * @param <R>  тип 18-го аргумента
     * @param <S>  тип 19-го аргумента
     * @param <T>  тип 20-го аргумента
     * @param <U>  тип 21-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10, Class<L> arg11, Class<M> arg12, Class<N> arg13, Class<O> arg14,
        Class<P> arg15, Class<Q> arg16, Class<R> arg17, Class<S> arg18, Class<T> arg19,
        Class<U> arg20,
        Class<Z> res,
        Fn21<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");
        if( arg11 == null ) throw new IllegalArgumentException("arg11==null");
        if( arg12 == null ) throw new IllegalArgumentException("arg12==null");
        if( arg13 == null ) throw new IllegalArgumentException("arg13==null");
        if( arg14 == null ) throw new IllegalArgumentException("arg14==null");
        if( arg15 == null ) throw new IllegalArgumentException("arg15==null");
        if( arg16 == null ) throw new IllegalArgumentException("arg16==null");
        if( arg17 == null ) throw new IllegalArgumentException("arg17==null");
        if( arg18 == null ) throw new IllegalArgumentException("arg18==null");
        if( arg19 == null ) throw new IllegalArgumentException("arg19==null");
        if( arg20 == null ) throw new IllegalArgumentException("arg20==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10, arg11, arg12, arg13, arg14,
            arg15, arg16, arg17, arg18, arg19,
            arg20,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 21 ) throw new IllegalArgumentException("args.length!=21");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10], (L) args[11], (M) args[12], (N) args[13], (O) args[14],
                    (P) args[15], (Q) args[16], (R) args[17], (S) args[18], (T) args[19],
                    (U) args[20]
                );
            }
        };
    }

    /**
     * Создание функции от 22-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param arg11 тип 12-го аргумента
     * @param arg12 тип 13-го аргумента
     * @param arg13 тип 14-го аргумента
     * @param arg14 тип 15-го аргумента
     * @param arg15 тип 16-го аргумента
     * @param arg16 тип 17-го аргумента
     * @param arg17 тип 18-го аргумента
     * @param arg18 тип 19-го аргумента
     * @param arg19 тип 20-го аргумента
     * @param arg20 тип 21-го аргумента
     * @param arg21 тип 22-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <L>  тип 12-го аргумента
     * @param <M>  тип 13-го аргумента
     * @param <N>  тип 14-го аргумента
     * @param <O>  тип 15-го аргумента
     * @param <P>  тип 16-го аргумента
     * @param <Q>  тип 17-го аргумента
     * @param <R>  тип 18-го аргумента
     * @param <S>  тип 19-го аргумента
     * @param <T>  тип 20-го аргумента
     * @param <U>  тип 21-го аргумента
     * @param <V>  тип 22-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10, Class<L> arg11, Class<M> arg12, Class<N> arg13, Class<O> arg14,
        Class<P> arg15, Class<Q> arg16, Class<R> arg17, Class<S> arg18, Class<T> arg19,
        Class<U> arg20, Class<V> arg21,
        Class<Z> res,
        Fn22<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");
        if( arg11 == null ) throw new IllegalArgumentException("arg11==null");
        if( arg12 == null ) throw new IllegalArgumentException("arg12==null");
        if( arg13 == null ) throw new IllegalArgumentException("arg13==null");
        if( arg14 == null ) throw new IllegalArgumentException("arg14==null");
        if( arg15 == null ) throw new IllegalArgumentException("arg15==null");
        if( arg16 == null ) throw new IllegalArgumentException("arg16==null");
        if( arg17 == null ) throw new IllegalArgumentException("arg17==null");
        if( arg18 == null ) throw new IllegalArgumentException("arg18==null");
        if( arg19 == null ) throw new IllegalArgumentException("arg19==null");
        if( arg20 == null ) throw new IllegalArgumentException("arg20==null");
        if( arg21 == null ) throw new IllegalArgumentException("arg21==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10, arg11, arg12, arg13, arg14,
            arg15, arg16, arg17, arg18, arg19,
            arg20, arg21,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 22 ) throw new IllegalArgumentException("args.length!=22");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10], (L) args[11], (M) args[12], (N) args[13], (O) args[14],
                    (P) args[15], (Q) args[16], (R) args[17], (S) args[18], (T) args[19],
                    (U) args[20], (V) args[21]
                );
            }
        };
    }

    /**
     * Создание функции от 23-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param arg11 тип 12-го аргумента
     * @param arg12 тип 13-го аргумента
     * @param arg13 тип 14-го аргумента
     * @param arg14 тип 15-го аргумента
     * @param arg15 тип 16-го аргумента
     * @param arg16 тип 17-го аргумента
     * @param arg17 тип 18-го аргумента
     * @param arg18 тип 19-го аргумента
     * @param arg19 тип 20-го аргумента
     * @param arg20 тип 21-го аргумента
     * @param arg21 тип 22-го аргумента
     * @param arg22 тип 23-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <L>  тип 12-го аргумента
     * @param <M>  тип 13-го аргумента
     * @param <N>  тип 14-го аргумента
     * @param <O>  тип 15-го аргумента
     * @param <P>  тип 16-го аргумента
     * @param <Q>  тип 17-го аргумента
     * @param <R>  тип 18-го аргумента
     * @param <S>  тип 19-го аргумента
     * @param <T>  тип 20-го аргумента
     * @param <U>  тип 21-го аргумента
     * @param <V>  тип 22-го аргумента
     * @param <W>  тип 23-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10, Class<L> arg11, Class<M> arg12, Class<N> arg13, Class<O> arg14,
        Class<P> arg15, Class<Q> arg16, Class<R> arg17, Class<S> arg18, Class<T> arg19,
        Class<U> arg20, Class<V> arg21, Class<W> arg22,
        Class<Z> res,
        Fn23<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");
        if( arg11 == null ) throw new IllegalArgumentException("arg11==null");
        if( arg12 == null ) throw new IllegalArgumentException("arg12==null");
        if( arg13 == null ) throw new IllegalArgumentException("arg13==null");
        if( arg14 == null ) throw new IllegalArgumentException("arg14==null");
        if( arg15 == null ) throw new IllegalArgumentException("arg15==null");
        if( arg16 == null ) throw new IllegalArgumentException("arg16==null");
        if( arg17 == null ) throw new IllegalArgumentException("arg17==null");
        if( arg18 == null ) throw new IllegalArgumentException("arg18==null");
        if( arg19 == null ) throw new IllegalArgumentException("arg19==null");
        if( arg20 == null ) throw new IllegalArgumentException("arg20==null");
        if( arg21 == null ) throw new IllegalArgumentException("arg21==null");
        if( arg22 == null ) throw new IllegalArgumentException("arg22==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10, arg11, arg12, arg13, arg14,
            arg15, arg16, arg17, arg18, arg19,
            arg20, arg21, arg22,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 23 ) throw new IllegalArgumentException("args.length!=23");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10], (L) args[11], (M) args[12], (N) args[13], (O) args[14],
                    (P) args[15], (Q) args[16], (R) args[17], (S) args[18], (T) args[19],
                    (U) args[20], (V) args[21], (W) args[22]
                );
            }
        };
    }

    /**
     * Создание функции от 24-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param arg11 тип 12-го аргумента
     * @param arg12 тип 13-го аргумента
     * @param arg13 тип 14-го аргумента
     * @param arg14 тип 15-го аргумента
     * @param arg15 тип 16-го аргумента
     * @param arg16 тип 17-го аргумента
     * @param arg17 тип 18-го аргумента
     * @param arg18 тип 19-го аргумента
     * @param arg19 тип 20-го аргумента
     * @param arg20 тип 21-го аргумента
     * @param arg21 тип 22-го аргумента
     * @param arg22 тип 23-го аргумента
     * @param arg23 тип 24-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <L>  тип 12-го аргумента
     * @param <M>  тип 13-го аргумента
     * @param <N>  тип 14-го аргумента
     * @param <O>  тип 15-го аргумента
     * @param <P>  тип 16-го аргумента
     * @param <Q>  тип 17-го аргумента
     * @param <R>  тип 18-го аргумента
     * @param <S>  тип 19-го аргумента
     * @param <T>  тип 20-го аргумента
     * @param <U>  тип 21-го аргумента
     * @param <V>  тип 22-го аргумента
     * @param <W>  тип 23-го аргумента
     * @param <X>  тип 24-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10, Class<L> arg11, Class<M> arg12, Class<N> arg13, Class<O> arg14,
        Class<P> arg15, Class<Q> arg16, Class<R> arg17, Class<S> arg18, Class<T> arg19,
        Class<U> arg20, Class<V> arg21, Class<W> arg22, Class<X> arg23,
        Class<Z> res,
        Fn24<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");
        if( arg11 == null ) throw new IllegalArgumentException("arg11==null");
        if( arg12 == null ) throw new IllegalArgumentException("arg12==null");
        if( arg13 == null ) throw new IllegalArgumentException("arg13==null");
        if( arg14 == null ) throw new IllegalArgumentException("arg14==null");
        if( arg15 == null ) throw new IllegalArgumentException("arg15==null");
        if( arg16 == null ) throw new IllegalArgumentException("arg16==null");
        if( arg17 == null ) throw new IllegalArgumentException("arg17==null");
        if( arg18 == null ) throw new IllegalArgumentException("arg18==null");
        if( arg19 == null ) throw new IllegalArgumentException("arg19==null");
        if( arg20 == null ) throw new IllegalArgumentException("arg20==null");
        if( arg21 == null ) throw new IllegalArgumentException("arg21==null");
        if( arg22 == null ) throw new IllegalArgumentException("arg22==null");
        if( arg23 == null ) throw new IllegalArgumentException("arg23==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10, arg11, arg12, arg13, arg14,
            arg15, arg16, arg17, arg18, arg19,
            arg20, arg21, arg22, arg23,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 24 ) throw new IllegalArgumentException("args.length!=24");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10], (L) args[11], (M) args[12], (N) args[13], (O) args[14],
                    (P) args[15], (Q) args[16], (R) args[17], (S) args[18], (T) args[19],
                    (U) args[20], (V) args[21], (W) args[22], (X) args[23]
                );
            }
        };
    }

    /**
     * Создание функции от 25-х аргументов
     *
     * @param arg0 тип 1-го аргумента
     * @param arg1 тип 2-го аргумента
     * @param arg2 тип 3-го аргумента
     * @param arg3 тип 4-го аргумента
     * @param arg4 тип 5-го аргумента
     * @param arg5 тип 6-го аргумента
     * @param arg6 тип 7-го аргумента
     * @param arg7 тип 8-го аргумента
     * @param arg8 тип 9-го аргумента
     * @param arg9 тип 10-го аргумента
     * @param arg10 тип 11-го аргумента
     * @param arg11 тип 12-го аргумента
     * @param arg12 тип 13-го аргумента
     * @param arg13 тип 14-го аргумента
     * @param arg14 тип 15-го аргумента
     * @param arg15 тип 16-го аргумента
     * @param arg16 тип 17-го аргумента
     * @param arg17 тип 18-го аргумента
     * @param arg18 тип 19-го аргумента
     * @param arg19 тип 20-го аргумента
     * @param arg20 тип 21-го аргумента
     * @param arg21 тип 22-го аргумента
     * @param arg22 тип 23-го аргумента
     * @param arg23 тип 24-го аргумента
     * @param arg24 тип 25-го аргумента
     * @param res  тип результата
     * @param fn   функция
     * @param <A>  тип 1-го аргумента
     * @param <B>  тип 2-го аргумента
     * @param <C>  тип 3-го аргумента
     * @param <D>  тип 4-го аргумента
     * @param <E>  тип 5-го аргумента
     * @param <F>  тип 6-го аргумента
     * @param <G>  тип 7-го аргумента
     * @param <H>  тип 8-го аргумента
     * @param <I>  тип 9-го аргумента
     * @param <J>  тип 10-го аргумента
     * @param <K>  тип 11-го аргумента
     * @param <L>  тип 12-го аргумента
     * @param <M>  тип 13-го аргумента
     * @param <N>  тип 14-го аргумента
     * @param <O>  тип 15-го аргумента
     * @param <P>  тип 16-го аргумента
     * @param <Q>  тип 17-го аргумента
     * @param <R>  тип 18-го аргумента
     * @param <S>  тип 19-го аргумента
     * @param <T>  тип 20-го аргумента
     * @param <U>  тип 21-го аргумента
     * @param <V>  тип 22-го аргумента
     * @param <W>  тип 23-го аргумента
     * @param <X>  тип 24-го аргумента
     * @param <Y>  тип 25-го аргумента
     * @param <Z>  тип результата
     * @return функция с описанием типов
     */
    public static <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z> TFunction of(
        Class<A> arg0, Class<B> arg1, Class<C> arg2, Class<D> arg3, Class<E> arg4,
        Class<F> arg5, Class<G> arg6, Class<H> arg7, Class<I> arg8, Class<J> arg9,
        Class<K> arg10, Class<L> arg11, Class<M> arg12, Class<N> arg13, Class<O> arg14,
        Class<P> arg15, Class<Q> arg16, Class<R> arg17, Class<S> arg18, Class<T> arg19,
        Class<U> arg20, Class<V> arg21, Class<W> arg22, Class<X> arg23, Class<Y> arg24,
        Class<Z> res,
        Fn25<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z> fn
    ){
        if( arg0 == null ) throw new IllegalArgumentException("arg0==null");
        if( arg1 == null ) throw new IllegalArgumentException("arg1==null");
        if( arg2 == null ) throw new IllegalArgumentException("arg2==null");
        if( arg3 == null ) throw new IllegalArgumentException("arg3==null");
        if( arg4 == null ) throw new IllegalArgumentException("arg4==null");
        if( arg5 == null ) throw new IllegalArgumentException("arg5==null");
        if( arg6 == null ) throw new IllegalArgumentException("arg6==null");
        if( arg7 == null ) throw new IllegalArgumentException("arg7==null");
        if( arg8 == null ) throw new IllegalArgumentException("arg8==null");
        if( arg9 == null ) throw new IllegalArgumentException("arg9==null");
        if( arg10 == null ) throw new IllegalArgumentException("arg10==null");
        if( arg11 == null ) throw new IllegalArgumentException("arg11==null");
        if( arg12 == null ) throw new IllegalArgumentException("arg12==null");
        if( arg13 == null ) throw new IllegalArgumentException("arg13==null");
        if( arg14 == null ) throw new IllegalArgumentException("arg14==null");
        if( arg15 == null ) throw new IllegalArgumentException("arg15==null");
        if( arg16 == null ) throw new IllegalArgumentException("arg16==null");
        if( arg17 == null ) throw new IllegalArgumentException("arg17==null");
        if( arg18 == null ) throw new IllegalArgumentException("arg18==null");
        if( arg19 == null ) throw new IllegalArgumentException("arg19==null");
        if( arg20 == null ) throw new IllegalArgumentException("arg20==null");
        if( arg21 == null ) throw new IllegalArgumentException("arg21==null");
        if( arg22 == null ) throw new IllegalArgumentException("arg22==null");
        if( arg23 == null ) throw new IllegalArgumentException("arg23==null");
        if( arg24 == null ) throw new IllegalArgumentException("arg24==null");

        if( res == null ) throw new IllegalArgumentException("res==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");

        Class<?>[] ts = new Class[]{
            arg0, arg1, arg2, arg3, arg4,
            arg5, arg6, arg7, arg8, arg9,
            arg10, arg11, arg12, arg13, arg14,
            arg15, arg16, arg17, arg18, arg19,
            arg20, arg21, arg22, arg23, arg24,
            res
        };
        return new TFunction() {
            @Override
            public Class<?>[] types(){
                return ts;
            }

            @SuppressWarnings("unchecked")
            @Override
            public Object call( Object[] args ){
                if( args == null ) throw new IllegalArgumentException("args==null");
                if( args.length != 25 ) throw new IllegalArgumentException("args.length!=25");
                return fn.apply(
                    (A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4],
                    (F) args[5], (G) args[6], (H) args[7], (I) args[8], (J) args[9],
                    (K) args[10], (L) args[11], (M) args[12], (N) args[13], (O) args[14],
                    (P) args[15], (Q) args[16], (R) args[17], (S) args[18], (T) args[19],
                    (U) args[20], (V) args[21], (W) args[22], (X) args[23], (Y) args[24]
                );
            }
        };
    }
    //endregion
}