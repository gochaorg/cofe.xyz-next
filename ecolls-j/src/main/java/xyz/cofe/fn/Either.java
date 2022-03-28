package xyz.cofe.fn;

import xyz.cofe.iter.Eterable;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Интерфейс для ИЛИ, либо левое значение, либо правое
 * @param <A> Левое значение
 * @param <B> Правое значение
 */
public interface Either<A,B> extends Serializable {
    /**
     * Значение
     * @param <A> тип значения
     */
    public interface View<A> extends Serializable {
        /**
         * Значение присутствует или нет
         * @return true - присутствует
         */
        boolean isPresent();

        public default boolean isEmpty(){ return !isPresent(); }

        /**
         * Возвращает значение или исключение {@link NoSuchElementException}
         * @return значение
         */
        A get();

        /**
         * Возвращает Optional
         * @return опциональное значение
         */
        default public Optional<A> option(){
            return isPresent() ? Optional.of(get()) : Optional.empty();
        }

        /**
         * Итератор
         * @return итератор
         */
        default public Eterable<A> iterable(){
            if( isPresent() ){
                return Eterable.single(get());
            }else{
                return Eterable.empty();
            }
        }
    }

    /**
     * Левая проекция
     * @param <A> левый тип
     * @param <B> правый тип
     */
    public static class LeftView<A,B> implements View<A>, Serializable {
        private final A value;
        private final boolean present;

        private LeftView( A value, boolean present ){
            this.value = value;
            this.present = present;
        }

        /**
         * Создание проекции со значением
         * @param a значение
         * @param <A> левый тип
         * @param <B> правый тип
         * @return проекция
         */
        public static <A,B> LeftView<A,B> viewOf( A a ){
            return new LeftView<>(a, true);
        }

        /**
         * Создание пустой проекции
         * @param <A> левый тип
         * @param <B> правый тип
         * @return проекция
         */
        public static <A,B> LeftView<A,B> empty(){
            return new LeftView<>(null, false);
        }

        @Override
        public boolean isPresent(){
            return present;
        }

        @Override
        public A get(){
            if( !present )throw new NoSuchElementException();
            return value;
        }

        private RightView<A,B> pair;
        private LeftView<A,B> pair( RightView<A,B> pair ){
            this.pair = pair;
            return this;
        }

        /**
         * преобразование Или[A,B] в Или[U,B]
         * @param functor функтор
         * @param <U> целевой тип
         * @return Структура ИЛИ
         */
        public <U> Either<U,B> map( Fn1<A,U> functor ) {
            if( functor==null )throw new IllegalArgumentException( "functor==null" );
            if( isPresent() ){
                return left( functor.apply(get()) );
            }else{
                return right( pair.get() );
            }
        }

        /**
         * Получение результата
         * @param functor функтор
         * @return результат
         */
        public A or( Fn1<B,A> functor ) {
            if( functor==null )throw new IllegalArgumentException( "functor==null" );
            if( isPresent() ){
                return get();
            }else{
                return functor.apply(pair.get());
            }
        }
    }

    /**
     * Правая проекция
     * @param <A> левый тип
     * @param <B> правый тип
     */
    public static class RightView<A,B> implements View<B>, Serializable {
        private final B value;
        private final boolean present;

        private RightView( B value, boolean present ){
            this.value = value;
            this.present = present;
        }

        /**
         * Создание проекции со значением
         * @param b значение
         * @param <A> левый тип
         * @param <B> правый тип
         * @return проекция
         */
        public static <A,B> RightView<A,B> viewOf( B b ){
            return new RightView<>(b, true);
        }

        /**
         * Создание пустой проекции
         * @param <A> левый тип
         * @param <B> правый тип
         * @return проекция
         */
        public static <A,B> RightView<A,B> empty(){
            return new RightView<>(null, false);
        }

        @Override
        public boolean isPresent(){
            return present;
        }

        @Override
        public B get(){
            if( !present )throw new NoSuchElementException();
            return value;
        }

        private LeftView<A,B> pair;
        private RightView<A,B> pair( LeftView<A,B> pair ){
            this.pair = pair;
            return this;
        }

        /**
         * преобразование Или[A,B] в Или[A,U]
         * @param functor функтор
         * @param <U> целевой тип
         * @return Структура ИЛИ
         */
        public <U> Either<A,U> map( Fn1<B,U> functor ){
            if( functor==null )throw new IllegalArgumentException( "functor==null" );
            if( isPresent() ){
                return right( functor.apply(get()) );
            }else{
                return left( pair.get() );
            }
        }

        /**
         * Получение результата
         * @param functor функтор
         * @return результат
         */
        public B or( Fn1<A,B> functor ) {
            if( functor==null )throw new IllegalArgumentException( "functor==null" );
            if( isPresent() ){
                return get();
            }else{
                return functor.apply(pair.get());
            }
        }
    }

    /**
     * Левое значение
     * @return левое значение
     */
    LeftView<A,B> left();

    /**
     * Правое значение
     * @return правое
     */
    RightView<A,B> right();

    /**
     * Создает левое значение
     * @param left левое значение
     * @param cls правое значение
     * @param <A> тип левого значения
     * @param <B> тип правого значения
     * @return Структура ИЛИ
     */
    static <A,B> Either<A,B> left( A left, Class<B> cls ){
        LeftView<A,B> lft = LeftView.viewOf(left);
        RightView<A,B> rgt = RightView.empty();
        return new Either<A, B>() {
            @Override
            public LeftView<A, B> left(){
                return lft;
            }

            @Override
            public RightView<A, B> right(){
                return rgt;
            }
        };
    }

    /**
     * Создает левое значение
     * @param left значение
     * @param <A> тип левого значения
     * @param <B> тип правого значения
     * @return Структура ИЛИ
     */
    static <A,B> Either<A,B> left( A left ){
        LeftView<A,B> lft = LeftView.viewOf(left);
        RightView<A,B> rgt = RightView.empty();
        lft.pair(rgt);
        rgt.pair(lft);
        return new Either<A, B>() {
            @Override
            public LeftView<A, B> left(){
                return lft;
            }

            @Override
            public RightView<A, B> right(){
                return rgt;
            }
        };
    }

    /**
     * Создает правое значение
     * @param right правое значение
     * @param cls тип левого значения
     * @param <A> тип левого значения
     * @param <B> тип правого значения
     * @return Структура ИЛИ
     */
    static <A,B> Either<A,B> right( B right, Class<A> cls ){
        LeftView<A,B> lft = LeftView.empty();
        RightView<A,B> rgt = RightView.viewOf(right);
        return new Either<A, B>() {
            @Override
            public LeftView<A, B> left(){
                return lft;
            }

            @Override
            public RightView<A, B> right(){
                return rgt;
            }
        };
    }

    /**
     * Создает правое значение
     * @param right правое значение
     * @param <A> тип левого значения
     * @param <B> тип правого значения
     * @return Структура ИЛИ
     */
    static <A,B> Either<A,B> right( B right ){
        LeftView<A,B> lft = LeftView.empty();
        RightView<A,B> rgt = RightView.viewOf(right);
        return new Either<A, B>() {
            @Override
            public LeftView<A, B> left(){
                return lft;
            }

            @Override
            public RightView<A, B> right(){
                return rgt;
            }
        };
    }

    /**
     * Свертка
     * @param fnAU функтор
     * @param fnBU функтор
     * @param <U> целевой тип
     * @return результат
     */
    public default <U> U reduce( Fn1<A,U> fnAU, Fn1<B,U> fnBU ) {
        if( fnAU==null )throw new IllegalArgumentException( "fnAU==null" );
        if( fnBU==null )throw new IllegalArgumentException( "fnBU==null" );
        if( left().isPresent() ){
            return fnAU.apply(left().get());
        }else{
            return fnBU.apply(right().get());
        }
    }
}
