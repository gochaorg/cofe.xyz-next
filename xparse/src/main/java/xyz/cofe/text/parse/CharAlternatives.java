package xyz.cofe.text.parse;

import xyz.cofe.ecolls.Fn3;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Несколько альтернативных правил
 */
@SuppressWarnings({ "unused", "WeakerAccess", "MethodDoesntCallSuperMethod" })
public class CharAlternatives<T extends Token> implements Function<CharPointer, Token> {
    private final Function<CharPointer, Token>[] expressions;

    /**
     * Возвращает список возможных вариантов
     *
     * @return варианты
     */
    public Function<CharPointer, Token>[] getExpressions(){
        return expressions;
    }

    /**
     * Конструктор
     *
     * @param expressions список возможных вариантов
     */
    public CharAlternatives( Function<CharPointer, Token>[] expressions ){
        if( expressions==null ) throw new IllegalArgumentException("expressions==null");
        this.expressions = expressions;
    }

    //region clone()
    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public CharAlternatives( CharAlternatives<T> sample ){
        if( sample==null ) throw new IllegalArgumentException("sample==null");
        this.expressions = sample.expressions;
        this.builder = sample.builder;
    }

    /**
     * Клонирование
     * @return клон
     */
    public CharAlternatives<T> clone(){
        return new CharAlternatives<>(this);
    }
    //endregion

    @Override
    public T apply( CharPointer tp ){
        if( tp==null ) throw new IllegalArgumentException("tp==null");
        if( tp.eof() ) return null;

        CharPointer begin = tp;
        for( Function<CharPointer, Token> f : expressions ){
            if( f==null ) continue;
            Token t = f.apply(tp);

            if( t==null )continue;

            CharPointer nptr = t.getEnd();
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
    public <R extends Token> CharAlternatives<R> build( BiFunction<CharPointer, CharPointer, R> fn ){
        if( fn==null ) throw new IllegalArgumentException("fn==null");
        CharAlternatives<R> seq = new CharAlternatives<>(expressions);
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
    public <R extends Token> CharAlternatives<R> build( Fn3<CharPointer, CharPointer, Token, R> fn ){
        if( fn==null ) throw new IllegalArgumentException("fn==null");
        CharAlternatives<R> seq = new CharAlternatives<>(expressions);
        seq.builder = fn;
        return seq;
    }

    private Fn3<CharPointer, CharPointer, Token, T> builder;

    /**
     * Функция для создания лексемы
     *
     * @return функция создатель лексемы
     */
    public Fn3<CharPointer, CharPointer, Token, T> getBuilder(){
        return builder;
    }

    private T build( CharPointer begin, CharPointer end, Token token ){
        if( builder!=null ){
            return builder.apply(begin, end, token);
        }
        return (T)token;
    }
}
