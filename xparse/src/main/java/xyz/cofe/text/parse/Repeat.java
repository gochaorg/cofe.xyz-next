package xyz.cofe.text.parse;

import xyz.cofe.ecolls.Fn2;
import xyz.cofe.ecolls.Fn3;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Повтор нескольких правил подряд
 */
@SuppressWarnings({ "unused", "WeakerAccess", "MethodDoesntCallSuperMethod" })
public class Repeat<P extends Pointer, T extends Tok<P>> implements Function<P, T> {
    /**
     * Конструктор
     *
     * @param expression правило
     */
    public Repeat( Function<P, T> expression, Fn3<P, P, Arr<? super T>, T> builder ){
        if( expression==null ) throw new IllegalArgumentException("expression==null");
        if( builder==null ) throw new IllegalArgumentException("builder==null");
        this.expression = expression;
        this.builder = builder;
    }

    //region clone()

    /**
     * Конструктор копирования
     *
     * @param sample образец для копирования
     */
    public Repeat( Repeat<P, T> sample ){
        if( sample==null ) throw new IllegalArgumentException("sample==null");
        this.expression = sample.expression;
        this.builder = sample.builder;
        this.max = sample.max;
        this.min = sample.min;
    }

    /**
     * Клонирование
     *
     * @return клон
     */
    public Repeat<P, T> clone(){
        return new Repeat<>(this);
    }
    //endregion

    //region min : int = 0
    private int min = 0;

    /**
     * Возвращает минимальное кол-во повторов
     *
     * @return минимальное кол-во
     */
    public int getMin(){
        return min;
    }

    /**
     * Указывает минимальное кол-во повторов
     *
     * @param min минимальное кол-во
     */
    public void setMin( int min ){
        this.min = min;
    }

    /**
     * Указывает минимальное кол-во повторов
     *
     * @param min минимальное кол-во
     * @return self ссылка
     */
    public Repeat<P, T> min( int min ){
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
     *
     * @return максимальное кол-во или 0
     */
    public int getMax(){
        return max;
    }

    /**
     * Указывает максимальное кол-во повторов
     *
     * @param max максимальное кол-во
     */
    public void setMax( int max ){
        this.max = max;
    }

    /**
     * Указывает максимальное кол-во повторов
     *
     * @param max максимальное кол-во
     * @return self ссылка
     */
    public Repeat<P, T> max( int max ){
        setMax(max);
        return this;
    }
    //endregion
    //region expression : Function<P, T>
    private final Function<P, T> expression;

    /**
     * Возвращает повторяющиеся правило
     *
     * @return правило
     */
    public Function<P, T> getExpression(){
        return expression;
    }
    //endregion

    @Override
    public T apply( P tp ){
        if( tp==null ) throw new IllegalArgumentException("tp==null");
        if( tp.eof() ) return null;

        P begin = tp;

        ArrayList<T> toks = new ArrayList<>();
        while( !tp.eof() ){
            if( max>0 && toks.size() >= max ) break;

            T t = expression.apply(tp);
            if( t!=null ){
                P nptr = t.getEnd();
                if( tp.compareTo(nptr) >= 0 ) throw new Error("parser return empty token");

                toks.add(t);
                tp = t.getEnd();
                continue;
            }

            break;
        }

        if( min>0 && toks.size()<min ) return null;

        return build(begin, tp, new Arr<>(toks));
    }

    private final Fn3<P, P, Arr<? super T>, T> builder;

    /**
     * Функция для создания лексемы
     *
     * @return функция создатель лексемы
     */
    public Fn3<P, P, Arr<? super T>, T> getBuilder(){
        return builder;
    }

    @SuppressWarnings("unchecked")
    private T build( P begin, P end, Arr<T> tokens ){
        return builder.apply(begin, end, tokens);
    }

    /**
     * Указывает функцию для построения
     *
     * @param fn  функция для построени
     * @param <R> возвращаемый тип лексемы
     * @return парсер
     */
    public <R extends T> Repeat<P, R> build( Fn3<P, P,Arr<R>, R> fn ){
        if( fn==null ) throw new IllegalArgumentException("fn==null");
        Repeat<P, R> rep = new Repeat(expression, fn);
        rep.setMax(getMax());
        rep.setMin(getMin());
        return rep;
    }

    /**
     * Указывает функцию для построения
     *
     * @param fn  функция для построени
     * @param <R> возвращаемый тип лексемы
     * @return парсер
     */
    public <R extends T> Repeat<P, R> build( Fn2<P, P, R> fn ){
        if( fn==null ) throw new IllegalArgumentException("fn==null");
        Repeat rep = new Repeat<>(expression, (begin,end,lst) -> fn.apply(begin,end));
        rep.setMax(getMax());
        rep.setMin(getMin());
        return rep;
    }
}
