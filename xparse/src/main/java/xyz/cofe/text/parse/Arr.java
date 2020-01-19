package xyz.cofe.text.parse;

import xyz.cofe.fn.Pair;
import xyz.cofe.fn.QuadConsumer;
import xyz.cofe.fn.QuintConsumer;
import xyz.cofe.fn.TripleConsumer;
import xyz.cofe.iter.Eterable;

import java.util.*;
import java.util.function.*;

/**
 * Массив элементов
 * @param <A> тип элементов массива
 */
public class Arr<A> implements Eterable<A> {
    private final List<A> arr = new ArrayList<>();

    /**
     * Конструктор
     * @param itms элементы
     */
    public Arr(Iterable<A> itms){
        if( itms!=null ){
            for( A a : itms ){
                arr.add(a);
            }
        }
    }

    /**
     * Конструктор
     */
    public Arr(){
        this(null);
    }

    /**
     * Возвращает размер массива
     * @return размер массива
     */
    public int size(){
        return arr.size();
    }

    /**
     * Возвращает признак что массив пустой
     * @return true - массив пустой
     */
    public boolean isEmpty(){
        return arr.isEmpty();
    }

    /**
     * Возвращае что массив содержит указанный элемент
     * @param o элемент
     * @return true - массив сожержит элемент
     */
    public boolean contains( Object o ){
        return arr.contains(o);
    }

    /**
     * Итератор по массиву
     * @return итератор
     */
    public Iterator<A> iterator(){
        return arr.iterator();
    }

    /**
     * Проверка что массив содержит все указанные элементы
     * @param c элементы
     * @return true - все содердаться в массиве
     */
    public boolean containsAll( Collection<?> c ){
        return arr.containsAll(c);
    }

    /**
     * Возвращает жлемент по индексу
     * @param index индекс
     * @return элемент
     */
    public A get( int index ){
        return arr.get(index);
    }

    /**
     * Возвращает индекс элемента
     * @param o элемент
     * @return индекс или значение меньше 0
     */
    public int indexOf( Object o ){
        return arr.indexOf(o);
    }

    /**
     * Возвращает последний индекс элемента
     * @param o элемент
     * @return индекс или значение меньше 0
     */
    public int lastIndexOf( Object o ){
        return arr.lastIndexOf(o);
    }

    public void each( Consumer<? super A> action ){
        arr.forEach(action);
    }

    public Arr<A> sort( Comparator<A> cmp ){
        if( cmp==null ) throw new IllegalArgumentException("cmp==null");
        Arr<A> arr = new Arr(this);
        return arr;
    }

    @Override
    public Arr<A> filter( Predicate<A> filter ){
        if( filter==null ) throw new IllegalArgumentException("filter==null");
        return new Arr<>( Eterable.super.filter(filter) );
    }

    @Override
    public Arr<A> notNull(){
        return new Arr<>( Eterable.super.notNull() );
    }

    @Override
    public Arr<A> union( Iterable<A>... iter ){
        if( iter==null ) throw new IllegalArgumentException("iter==null");
        return new Arr<>( Eterable.super.union(iter) );
    }

    @Override
    public <B> Arr<B> map( Function<A, B> map ){
        if( map==null ) throw new IllegalArgumentException("map==null");
        return new Arr<>( Eterable.super.map(map) );
    }

    @Override
    public Arr<A> limit( long limit ){
        return new Arr<>( Eterable.super.limit(limit));
    }

    @Override
    public <B> Arr<Pair<A, B>> product( Iterable<B> source ){
        return new Arr<>( Eterable.super.product(source) );
    }

    @Override
    public <B> Arr<A> product( Iterable<B> source, BiConsumer<A, B> consumer ){
        Eterable.super.product(source, consumer);
        return this;
    }

    @Override
    public <B, C> Eterable<A> product( Iterable<B> source1, Iterable<C> source2, TripleConsumer<A, B, C> consumer ){
        Eterable.super.product(source1,source2,consumer);
        return this;
    }

    @Override
    public <B, C, D> Eterable<A> product( Iterable<B> source1, Iterable<C> source2, Iterable<D> source3, QuadConsumer<A, B, C, D> consumer ){
        Eterable.super.product(source1,source2,source3, consumer);
        return this;
    }

    @Override
    public <B, C, D, E> Eterable<A> product( Iterable<B> source1, Iterable<C> source2, Iterable<D> source3, Iterable<E> source4, QuintConsumer<A, B, C, D, E> consumer ){
        Eterable.super.product(source1,source2,source3,source4,consumer);
        return this;
    }

    @Override
    public Optional<A> first(){
        return Eterable.super.first();
    }

    @Override
    public List<A> toList(){
        return Eterable.super.toList();
    }

    public Arr<IndexValue<A>> indexes(){
        Arr<IndexValue<A>> arr = new Arr<>();
        int idx = -1;
        for( A a : this ){
            idx++;
            arr.arr.add(new IndexValue<>(idx,a));
        }
        return arr;
    }

    public Arr<A> reverse(){
        Arr<A> arr = new Arr<>();
        for( A a : this ){
            arr.arr.add(0,a);
        }
        return arr;
    }

    public <R> R reduce( R init, BiFunction<R,A,R> fn ){
        if( fn==null ) throw new IllegalArgumentException("fn==null");
        R r = init;
        for( A a : this ){
            r = fn.apply(r, a);
        }
        return r;
    }

    public ArrPattern<A> pattern(){
        return new ArrPattern<>(this);
    }
}
