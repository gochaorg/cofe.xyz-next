package xyz.cofe.text.parse.toks;

import xyz.cofe.ecolls.Fn3;
import xyz.cofe.text.parse.Arr;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Повтор нескольких правил подряд
 */
@SuppressWarnings({ "unused", "WeakerAccess", "MethodDoesntCallSuperMethod" })
public class CharRepeat<T extends Token> implements Function<CharPointer, T> {
    private final Function<CharPointer, Token> expression;

    /**
     * Возвращает повторяющиеся правило
     * @return правило
     */
    public Function<CharPointer, Token> getExpression(){ return expression; }

    /**
     * Конструктор
     * @param expression правило
     */
    public CharRepeat( Function<CharPointer, Token> expression ){
        if( expression==null ) throw new IllegalArgumentException("expression==null");
        this.expression = expression;
    }

    //region clone()
    /**
     * Конструктор копирования
     * @param sample образец для копирования
     */
    public CharRepeat( CharRepeat<T> sample ){
        if( sample==null ) throw new IllegalArgumentException("sample==null");
        this.expression = sample.expression;
        this.builder = sample.builder;
        this.max = sample.max;
        this.min = sample.min;
    }

    /**
     * Клонирование
     * @return клон
     */
    public CharRepeat<T> clone(){
        return new CharRepeat<>(this);
    }
    //endregion

    //region min : int = 0
    private int min = 0;

    /**
     * Возвращает минимальное кол-во повторов
     * @return минимальное кол-во
     */
    public int getMin(){
        return min;
    }

    /**
     * Указывает минимальное кол-во повторов
     * @param min минимальное кол-во
     */
    public void setMin( int min ){
        this.min = min;
    }

    /**
     * Указывает минимальное кол-во повторов
     * @param min минимальное кол-во
     * @return self ссылка
     */
    public CharRepeat<T> min( int min ){
        setMin(min);
        return this;
    }
    //endregion
    //region max : int = 0
    /**
     * Возвращает максимальное кол-во повторов
     */
    private int max = 0;

    /**
     * Возвращает максимальное кол-во повторов
     * @return максимальное кол-во или 0
     */
    public int getMax(){
        return max;
    }

    /**
     * Указывает максимальное кол-во повторов
     * @param max максимальное кол-во
     */
    public void setMax( int max ){
        this.max = max;
    }

    /**
     * Указывает максимальное кол-во повторов
     * @param max максимальное кол-во
     * @return self ссылка
     */
    public CharRepeat<T> max( int max ){
        setMax(max);
        return this;
    }
    //endregion

    @Override
    public T apply( CharPointer tp ){
        if( tp==null ) throw new IllegalArgumentException("tp==null");
        if( tp.eof() )return null;

        CharPointer begin = tp;

        ArrayList<Token> toks = new ArrayList<>();
        while( !tp.eof() ){
            if( max>0 && toks.size()>=max )break;

            Token t = expression.apply(tp);
            if( t!=null ){
                CharPointer nptr = t.getEnd();
                if( tp.compareTo(nptr)>=0 )throw new Error("parser return empty token");

                toks.add(t);
                tp = t.getEnd();
                continue;
            }

            break;
        }

        if( min>0 && toks.size()<min )return null;

        return build(begin,tp,new Arr<>(toks));
    }

    private Fn3<CharPointer, CharPointer,Arr<Token>,T> builder;

    /**
     * Функция для создания лексемы
     * @return функция создатель лексемы
     */
    public Fn3<CharPointer, CharPointer, Arr<Token>, T> getBuilder(){
        return builder;
    }

    @SuppressWarnings("unchecked")
    private T build( CharPointer begin, CharPointer end, Arr<Token> tokens ){
        if( builder!=null ){
            return builder.apply(begin,end,tokens);
        }
        return (T)new Token(begin,end);
    }

    /**
     * Указывает функцию для построения
     * @param fn функция для построени
     * @param <R> возвращаемый тип лексемы
     * @return парсер
     */
    public <R extends Token> CharRepeat<R> build( BiFunction<CharPointer, CharPointer,R> fn ){
        if( fn==null ) throw new IllegalArgumentException("fn==null");
        CharRepeat<R> rep = new CharRepeat<>(expression);
        rep.builder = ( b, e, l) -> fn.apply(b,e);
        rep.max = max;
        rep.min = min;
        return rep;
    }

    /**
     * Указывает функцию для построения
     * @param fn функция для построени
     * @param <R> возвращаемый тип лексемы
     * @return парсер
     */
    public <R extends Token> CharRepeat<R> build( Fn3<CharPointer, CharPointer,Arr<Token>,R> fn ){
        if( fn==null ) throw new IllegalArgumentException("fn==null");
        CharRepeat<R> rep = new CharRepeat<>(expression);
        rep.builder = fn;
        rep.max = max;
        rep.min = min;
        return rep;
    }

//    /**
//     * Указывает функцию для построения
//     * @param fn функция для построени
//     * @param <R> возвращаемый тип лексемы
//     * @return парсер
//     */
//    public <R extends Token> CharRepeat<R> create( Function<Arr<Token>,R> fn ){
//        if( fn==null ) throw new IllegalArgumentException("fn==null");
//        CharRepeat<R> rep = new CharRepeat<>(expression);
//        rep.builder = (a,b,lst) -> fn.apply(lst);
//        rep.max = max;
//        rep.min = min;
//        return rep;
//    }
}
