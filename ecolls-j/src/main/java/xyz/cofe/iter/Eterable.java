package xyz.cofe.iter;

import xyz.cofe.fn.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Расширение стандартного итератора
 * @param <A> тип элементов в итераторе
 */
public interface Eterable<A> extends Iterable<A> {
    /**
     * Создает итератор по единичному значению
     * @param <A> тип значения
     * @param a значение
     * @return итератор
     */
    static <A> Eterable<A> single(A a){
        return new SingleIterable<>(a);
    }

    /**
     * Итератор по массиву
     * @param a массив
     * @param <A> тип элементов в массиве
     * @return итератор
     */
    static <A> Eterable<A> of(A ... a){
        return new ArrayIterable<>(a);
    }

    /**
     * Создает итератор
     * @param a исходный итератор
     * @param <A> тип элементов
     * @return итератор
     */
    static <A> Eterable<A> of(Iterable<? extends A> a){
        return new EterableProxy<>(a);
    }

    /**
     * "Пустой" Итератор
     * @param <A> тип элементов
     * @return итератор
     */
    static <A> Eterable<A> empty(){
        return new EmptyIterable<>();
    }

    /**
     * Фильтрация исходных данных
     * @param filter фильтр
     * @return итератор
     */
    default Eterable<A> filter(Predicate<A> filter){
        if( filter == null )throw new IllegalArgumentException( "filter == null" );
        return new FilterIterable<>(filter,this);
    }

    /**
     * Фильтрация исходного набора - удаление null значений
     * @return итератор
     */
    default Eterable<A> notNull(){
        return filter(Objects::nonNull);
    }

    /**
     * Присоединение данных к исходным
     * @param iter присоединяемые данные
     * @return итератор
     */
    default Eterable<A> union(Iterable<A> ... iter){
        if( iter == null )throw new IllegalArgumentException( "iter == null" );
        Iterable<A>[] niter = Arrays.copyOf(iter,iter.length+1);
        for( int i=0; i<iter.length; i++ ){
            niter[i+1] = iter[i];
        }
        niter[0] = this;
        return new UnionIterable<>(niter);
    }

    /**
     * Присоединение данных к исходным
     * @param iter присоединяемые данные
     * @return итератор
     */
    default Eterable<A> union(Iterable<Iterable<A>> iter){
        if( iter == null )throw new IllegalArgumentException( "iter == null" );

        List<Iterable<A>> lst = new ArrayList<>();
        iter.forEach( it -> lst.add(it) );

        Iterable<A>[] niter = lst.toArray(new Iterable[0]);
        return new UnionIterable<>(niter);
    }

    /**
     * Отображения одного набора данных на другой
     * @param map фунция отображения
     * @param <B> целевой тип данных
     * @return итератор
     */
    default <B> Eterable<B> map(Function<A,B> map){
        if( map == null )throw new IllegalArgumentException( "map == null" );
        return new MapIterable<A,B>(this,map);
    }

    /**
     * Ограничение исходного набора
     * @param limit максимаьлное кол-во выбираемых значений
     * @return кол-во
     */
    default Eterable<A> limit(long limit){
        return new LimitIterable<>(this,limit);
    }

    /**
     * Декартово произведение
     * @param source множество на которое производиться умножение
     * @param <B> Тип дополнительного множества
     * @return Декартово произведение
     */
    default <B> Eterable<Pair<A,B>> product( Iterable<B> source){
        if(source==null)throw new IllegalArgumentException("source == null");
        return BiProductIterable.of(this,source);
    }

    /**
     * Декартово произведение
     * @param source множество на которое производиться умножение
     * @param consumer сторона которая принимает произведение
     * @param <B> Тип дополнительного множества
     * @return Исходный итератор (self ссылка)
     */
    default <B> Eterable<A> product(Iterable<B> source, BiConsumer<A,B> consumer){
        if(source==null)throw new IllegalArgumentException("source == null");
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        for( Pair<A,B> pair : product(source)){
            if(pair!=null){
                pair.apply(consumer);
            }
        }
        return this;
    }

    /**
     * Декартово произведение
     * @param source1 множество на которое производиться умножение
     * @param source2 множество на которое производиться умножение
     * @param consumer сторона которая принимает произведение
     * @param <B> Тип дополнительного множества
     * @param <C> Тип дополнительного множества
     * @return Исходный итератор (self ссылка)
     */
    default <B,C> Eterable<A> product(Iterable<B> source1, Iterable<C> source2, TripleConsumer<A,B,C> consumer){
        if(source1==null)throw new IllegalArgumentException("source1 == null");
        if(source2==null)throw new IllegalArgumentException("source2 == null");
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        product(source1).product(source2).forEach( x -> consumer.accept(x.a().a(),x.a().b(),x.b()));
        return this;
    }

    /**
     * Декартово произведение
     * @param source1 множество на которое производиться умножение
     * @param source2 множество на которое производиться умножение
     * @param source3 множество на которое производиться умножение
     * @param consumer сторона которая принимает произведение
     * @param <B> Тип дополнительного множества
     * @param <C> Тип дополнительного множества
     * @param <D> Тип дополнительного множества
     * @return Исходный итератор (self ссылка)
     */
    default <B,C,D> Eterable<A> product(Iterable<B> source1, Iterable<C> source2, Iterable<D> source3, QuadConsumer<A,B,C,D> consumer){
        if(source1==null)throw new IllegalArgumentException("source1 == null");
        if(source2==null)throw new IllegalArgumentException("source2 == null");
        if(source3==null)throw new IllegalArgumentException("source3 == null");
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        product(source1)
            .product(source2)
            .product(source3)
            .forEach( x -> consumer.accept
                ( x.a().a().a()
                , x.a().a().b()
                , x.a().b()
                , x.b()
                ));
        return this;
    }

    /**
     * Декартово произведение
     * @param source1 множество на которое производиться умножение
     * @param source2 множество на которое производиться умножение
     * @param source3 множество на которое производиться умножение
     * @param source4 множество на которое производиться умножение
     * @param consumer сторона которая принимает произведение
     * @param <B> Тип дополнительного множества
     * @param <C> Тип дополнительного множества
     * @param <D> Тип дополнительного множества
     * @param <E> Тип дополнительного множества
     * @return Исходный итератор (self ссылка)
     */
    default <B,C,D,E> Eterable<A> product(Iterable<B> source1, Iterable<C> source2, Iterable<D> source3, Iterable<E> source4, QuintConsumer<A,B,C,D,E> consumer){
        if(source1==null)throw new IllegalArgumentException("source1 == null");
        if(source2==null)throw new IllegalArgumentException("source2 == null");
        if(source3==null)throw new IllegalArgumentException("source3 == null");
        if(source4==null)throw new IllegalArgumentException("source4 == null");
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        product(source1)
            .product(source2)
            .product(source3)
            .product(source4)
            .forEach( x -> consumer.accept
                ( x.a().a().a().a()
                , x.a().a().a().b()
                , x.a().a().b()
                , x.a().b()
                , x.b()
                ));
        return this;
    }

    /**
     * Возвращает первый элемент
     * @return первый элемент
     */
    default Optional<A> first(){
        Iterator<A> itr = iterator();
        if( itr.hasNext() ){
            return Optional.of(itr.next());
        }
        return Optional.empty();
    }

    /**
     * Преобразование в список
     * @return список
     */
    default List<A> toList(){
        ArrayList<A> lst = new ArrayList<>();
        this.forEach( lst::add );
        return lst;
    }

    /**
     * Создание итератора по дереву
     * @param root корень
     * @param follow следование
     * @param <A> тип узла дерева
     * @return создание итератора
     */
    public static <A> TreeIterBuilder<A> tree( A root, Function<A,Iterable<? extends A>> follow ){
        if( root==null ) throw new IllegalArgumentException("root==null");
        if( follow==null ) throw new IllegalArgumentException("follow==null");

        return new TreeIterBuilderDefault<A>(root,follow);
    }

    /**
     * Посчет кол-ва элементов
     * @return кол-во элементов
     */
    default long count(){
        long tot = 0;
        for( A a : this ){
            tot++;
        }
        return tot;
    }

    /**
     * Свертка значений
     * @param initial начальное значение
     * @param reducer функция светки
     * @param <R> Тип результата
     * @return результат
     */
    default <R> R reduce(R initial, BiFunction<R,A,R> reducer){
        if( initial==null )throw new IllegalArgumentException( "initial==null" );
        if( reducer==null )throw new IllegalArgumentException( "reducer==null" );
        Acum<R> result = new Acum<>(initial);
        forEach( v -> {
            result.set( reducer.apply(result.get(), v) );
        });
        return result.get();
    }

    /**
     * Пропуск N элементов
     * @param count сколько пропустить
     * @return Итератор
     */
    default Eterable<A> skip( long count ){
        Iterator<A> src = iterator();
        return new Eterable<A>() {
            @Override
            public Iterator<A> iterator(){
                return new SkipIterator<>(src, count);
            }
        };
    }

    /**
     * Пропуск N элементов
     * @param count сколько пропустить
     * @return Итератор
     */
    default Eterable<A> skip( int count ){
        return skip((long) count);
    }
}
