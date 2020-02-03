package xyz.cofe.text.parse;

import xyz.cofe.fn.Fn2;
import xyz.cofe.fn.Fn3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Последовательность правил
 * @param <T> лексема
 */
@SuppressWarnings({ "unused", "WeakerAccess", "MethodDoesntCallSuperMethod" })
public class Sequence<P extends Pointer, T extends Tok<P>> implements Function<P, T> {
    /**
     * Конструктор
     * @param expressions последовательность правил
     * @param builder функция которая строит токен
     */
    public Sequence( Function<P,T>[] expressions, Fn3<P, P, Arr<T>, T> builder ){
        if( expressions==null ) throw new IllegalArgumentException("expressions==null");
        if( builder==null ) throw new IllegalArgumentException("builder==null");
        this.expressions = expressions;
        this.builder = builder;
    }

    private final Function<P,T>[] expressions;

    /**
     * Возвращает воследовательность правил
     * @return последовательность
     */
    public Function<P, T>[] getExpressions(){ return expressions; }

    @Override
    public T apply( P ptr ){
        if( ptr==null ) throw new IllegalArgumentException("ptr==null");
        if( expressions==null || expressions.length<1 )return null;

        List<T> toks = new ArrayList<>();
        P begin = ptr;

        for( Function<P, T> exp : expressions ){
            if( exp==null )continue;

            T t = exp.apply(ptr);
            if( t==null )return null;

            P nptr = t.getEnd();
            if( ptr.compareTo(nptr)>=0 )throw new Error("parser return empty token");

            ptr = nptr;
            toks.add(t);
        }

        if( toks.isEmpty() )return null;

        P end = ptr;
        if( begin.compareTo(end)>=0 )throw new Error("parser catch empty token");

        return build(begin,end,new Arr<>(toks));
    }

    /**
     * Указывает функцию для построения
     * @param fn функция для построени
     * @param <R> возвращаемый тип лексемы
     * @return парсер
     */
    public <R extends T> Sequence<P,R> build( Fn3<P, P,Arr<R>,R> fn ){
        if( fn==null ) throw new IllegalArgumentException("fn==null");
        Sequence seq = new Sequence(expressions, fn);
        return seq;
    }

    /**
     * Указывает функцию для построения
     * @param fn функция для построени
     * @param <R> возвращаемый тип лексемы
     * @return парсер
     */
    public <R extends T> Sequence<P,R> build( Fn2<P, P, R> fn ){
        if( fn==null ) throw new IllegalArgumentException("fn==null");
        Sequence seq = new Sequence<>(expressions, (begin,end,toks)->fn.apply(begin,end));
        return seq;
    }

    private final Fn3<P, P, Arr<T>, T> builder;

    /**
     * Функция для создания лексемы
     * @return функция создатель лексемы
     */
    public Fn3<P, P, Arr<T>, T> getBuilder(){
        return builder;
    }

    private T build( P begin, P end, Arr<T> tokens ){
        return builder.apply(begin,end,tokens);
    }
}
