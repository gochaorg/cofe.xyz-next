package xyz.cofe.text.parse;

import xyz.cofe.ecolls.Fn3;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Несколько альтернативных правил
 */
@SuppressWarnings({ "unused", "WeakerAccess", "MethodDoesntCallSuperMethod" })
public class Alternatives<P extends Pointer,T extends Tok<P>> implements Function<P, T> {
    private final Function<P, T>[] expressions;

    /**
     * Возвращает список возможных вариантов
     *
     * @return варианты
     */
    public Function<P, T>[] getExpressions(){
        return expressions;
    }

    /**
     * Конструктор
     *
     * @param expressions список возможных вариантов
     */
    public Alternatives( Function<P, T>[] expressions ){
        if( expressions==null ) throw new IllegalArgumentException("expressions==null");
        this.expressions = expressions;
    }

    //region clone()
    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public Alternatives( Alternatives<P,T> sample ){
        if( sample==null ) throw new IllegalArgumentException("sample==null");
        this.expressions = sample.expressions;
        this.builder = sample.builder;
    }

    /**
     * Клонирование
     * @return клон
     */
    public Alternatives<P,T> clone(){
        return new Alternatives<>(this);
    }
    //endregion

    @Override
    public T apply( P tp ){
        if( tp==null ) throw new IllegalArgumentException("tp==null");
        if( tp.eof() ) return null;

        P begin = tp;
        for( Function<P, T> f : expressions ){
            if( f==null ) continue;
            T t = f.apply(tp);

            if( t==null )continue;

            P nptr = t.getEnd();
            if( tp.compareTo(nptr) >= 0 ) throw new Error("parser return empty token");

            return build(begin, nptr, t);
        }

        return null;
    }

    /**
     * Указывает функцию для построения
     *
     * @param fn  функция для построени
     * @param <R> возвращаемый тип лексемы
     * @return парсер
     */
    public <R extends T> Alternatives<P,R> build( BiFunction<P, P, R> fn ){
        if( fn==null ) throw new IllegalArgumentException("fn==null");
        Alternatives<P,R> seq = new Alternatives(expressions);
        seq.builder = ( b, e, l )->fn.apply(b, e);
        return seq;
    }

    /**
     * Указывает функцию для построения
     *
     * @param fn  функция для построени
     * @param <R> возвращаемый тип лексемы
     * @return парсер
     */
    public <R extends T> Alternatives<P,R> build( Fn3<P, P, T, R> fn ){
        if( fn==null ) throw new IllegalArgumentException("fn==null");
        Alternatives<P,R> seq = new Alternatives(expressions);
        seq.builder = fn;
        return seq;
    }

    private Fn3<P, P, ? super T, ? extends T> builder;

    /**
     * Функция для создания лексемы
     *
     * @return функция создатель лексемы
     */
    public Fn3<P, P, ? super T, ? extends T> getBuilder(){
        return builder;
    }

    private T build( P begin, P end, T token ){
        if( builder!=null ){
            return builder.apply(begin, end, token);
        }
        return (T)token;
    }
}
