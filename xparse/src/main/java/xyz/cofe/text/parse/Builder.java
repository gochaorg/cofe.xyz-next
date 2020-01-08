package xyz.cofe.text.parse;

import xyz.cofe.ecolls.Fn3;

import java.util.function.Function;

/**
 * Функции облегчающие построение анализаторов
 */
public class Builder {
    //region seq() - Последовательность
    /**
     * Создание последовательности
     * @param <P> Указатель
     * @param <T> Тип элемента указателя
     */
    public static class SeqBuilder<P extends Pointer,T extends Tok<P>> {
        private Function<P,T>[] seq;

        /**
         * Конструктор
         * @param seq последовательность
         */
        public SeqBuilder(Function<P,T>[] seq){
            if( seq==null ) throw new IllegalArgumentException("seq==null");
            this.seq = seq;
        }

        /**
         * Создаент функцию - последовательнсть
         * @param builder Построение результата
         * @return функция
         */
        public Sequence<P,T> build( Fn3<P, P, Arr<T>, T> builder ){
            if( builder==null ) throw new IllegalArgumentException("builder==null");
            return new Sequence<>(seq,builder);
        }
    }

    /**
     * Создание функции-правила последовательности правил
     * @param seq Последовательность правил
     * @param <P> Указатель
     * @param <T> Тип элемента указателя
     * @return создание функции-правила
     */
    @SafeVarargs
    public static <P extends Pointer,T extends Tok<P>> SeqBuilder<P,T> seq(
        Function<P,T> ... seq
    ){
        if( seq==null ) throw new IllegalArgumentException("seq==null");
        if( seq.length<1 ) throw new IllegalArgumentException("seq.length<1");
        return new SeqBuilder<>(seq);
    }
    //endregion

    @SafeVarargs
    public static <P extends Pointer,T extends Tok<P>> Alternatives<P,T> alt( Function<P,T> ... alts){
        if( alts==null ) throw new IllegalArgumentException("alts==null");
        if( alts.length<1 ) throw new IllegalArgumentException("alts.length<1");
        return new Alternatives<>(alts);
    }

    public static class RepBuilder<P extends Pointer,T extends Tok<P>> {
        private Function<P, T> expression; //, Fn3<P, P, Arr<? super T>, T> builder
        public RepBuilder( Function<P, T> expression ){
            if( expression==null ) throw new IllegalArgumentException("expression==null");
            this.expression = expression;
        }

        public Repeat<P,T> build( Fn3<P, P, Arr<? super T>, T> builder ){
            if( builder==null ) throw new IllegalArgumentException("builder==null");
            return new Repeat<>(expression,builder);
        }
    }
}
