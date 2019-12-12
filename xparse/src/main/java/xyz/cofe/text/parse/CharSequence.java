package xyz.cofe.text.parse;

import xyz.cofe.ecolls.Fn3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Последовательность правил
 * @param <T> лексема
 */
@SuppressWarnings({ "unused", "WeakerAccess", "MethodDoesntCallSuperMethod" })
public class CharSequence<T extends Token> implements Function<CharPointer, Token> {
    private final Function<CharPointer, Token>[] expressions;

    /**
     * Возвращает воследовательность правил
     * @return последовательность
     */
    public Function<CharPointer, Token>[] getExpressions(){ return expressions; }

    /**
     * Конструктор
     * @param expressions последовательность правил
     */
    public CharSequence( Function<CharPointer, Token>[] expressions ){
        if( expressions==null ) throw new IllegalArgumentException("expressions==null");
        this.expressions = expressions;
    }

    //region clone()
    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public CharSequence( CharSequence<T> sample ){
        if( sample==null ) throw new IllegalArgumentException("sample==null");
        this.expressions = sample.getExpressions();
        this.builder = sample.getBuilder();
    }

    /**
     * Клонирование правила
     * @return клон
     */
    public CharSequence<T> clone(){
        return new CharSequence<>(this);
    }
    //endregion

    @Override
    public T apply( CharPointer ptr ){
        if( ptr==null ) throw new IllegalArgumentException("ptr==null");
        if( expressions==null || expressions.length<1 )return null;

        List<Token> toks = new ArrayList<>();
        CharPointer begin = ptr;

        for( Function<CharPointer, Token> exp : expressions ){
            if( exp==null )continue;

            Token t = exp.apply(ptr);
            if( t==null )return null;

            CharPointer nptr = t.getEnd();
            if( ptr.compareTo(nptr)>=0 )throw new Error("parser return empty token");

            ptr = nptr;
            toks.add(t);
        }

        if( toks.isEmpty() )return null;

        CharPointer end = ptr;
        if( begin.compareTo(end)>=0 )throw new Error("parser catch empty token");

        return build(begin,end,new Arr<>(toks));
    }

    /**
     * Указывает функцию для построения
     * @param fn функция для построени
     * @param <R> возвращаемый тип лексемы
     * @return парсер
     */
    public <R extends Token> CharSequence<R> build( BiFunction<CharPointer, CharPointer,R> fn ){
        if( fn==null ) throw new IllegalArgumentException("fn==null");
        CharSequence<R> seq = new CharSequence<>(expressions);
        seq.builder = ( b, e, l) -> fn.apply(b,e);
        return seq;
    }

    /**
     * Указывает функцию для построения
     * @param fn функция для построени
     * @param <R> возвращаемый тип лексемы
     * @return парсер
     */
    public <R extends Token> CharSequence<R> build( Fn3<CharPointer, CharPointer,Arr<Token>,R> fn ){
        if( fn==null ) throw new IllegalArgumentException("fn==null");
        CharSequence<R> seq = new CharSequence<>(expressions);
        seq.builder = fn;
        return seq;
    }

    private Fn3<CharPointer, CharPointer,Arr<Token>,T> builder;

    /**
     * Функция для создания лексемы
     * @return функция создатель лексемы
     */
    public Fn3<CharPointer, CharPointer, Arr<Token>, T> getBuilder(){
        return builder;
    }

    private T build( CharPointer begin, CharPointer end, Arr<Token> tokens ){
        if( builder!=null ){
            return builder.apply(begin,end,tokens);
        }
        return (T)new Token(begin,end);
    }
}
