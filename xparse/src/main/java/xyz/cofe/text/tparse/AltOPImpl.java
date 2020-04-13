package xyz.cofe.text.tparse;

import xyz.cofe.iter.Eterable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

/**
 * Реализация функции грамматики - альтернативного выбора
 * @param <P> Указатель
 * @param <T> Лексема/Токен
 */
public class AltOPImpl<P extends Pointer<?,?,P>, T extends Tok<P>> implements AltOP<P,T> {
    public AltOPImpl( GR<P,T> ... exps ){
        if( exps==null )throw new IllegalArgumentException("exps==null");
        this.exps = Eterable.of(exps);
    }

    public AltOPImpl( Iterable<GR<P,T>> exps ){
        if( exps==null )throw new IllegalArgumentException("exps==null");
        this.exps = Eterable.of(exps);
    }

    private final Eterable<GR<P,T>> exps;

    /**
     * Список выражений - алтернатив
     * @return список выражений альтернатив
     */
    public Eterable<GR<P,T>> expressions(){ return exps; }

    /**
     * Указывает как отобразить распознаною последовательность на указанный токен
     * @param map функция отображения
     * @param <U> тип токена - результата
     * @return функция грамматического правила
     */
    @Override
    public <U extends Tok<P>> GR<P, U> map(Function<T, U> map) {
        if( map==null )throw new IllegalArgumentException("map==null");
        return new GR<P, U>() {
            @Override
            public Optional<U> apply(P ptr) {
                if(ptr==null)throw new IllegalArgumentException("ptr==null");

                Iterator<GR<P,T>> grIt = exps.iterator();
                //noinspection ConstantConditions
                if( grIt==null )return Optional.empty();

                Optional<T> found = Optional.empty();
                while( grIt.hasNext() ){
                    GR<P,T> gr = grIt.next();
                    if( gr==null )continue;

                    found = gr.apply(ptr);
                    if( found==null || !found.isPresent() ){
                        continue;
                    }

                    P p = found.get().end();
                    if( p==null )throw new IllegalStateException("token return null on end");

                    //noinspection rawtypes,unchecked
                    if( ((Pointer)ptr).compareTo(((P)p))>=0 ){
                        throw new IllegalStateException("bug of parser, end pointer as begin");
                    }

                    break;
                }

                if( found!=null && found.isPresent() ){
                    return Optional.of( map.apply( found.get() ) );
                }

                return Optional.empty();
            }
        };
    }
}
