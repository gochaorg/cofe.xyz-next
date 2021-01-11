Алгоритмы итерации
=====================

Эволюция итерации в java
------------------------

**Java 4+**

```java
package java.util;
public interface Enumeration<E> { 
  boolean  hasMoreElements()
  E        nextElement()
}
```

Очень простой интерфейс, лего реализовывать, ничего лишнего.

Использовать примерно так:

```java
Enumeration<String> enumerat = ...
while( enumerat.hasMoreElements() ){
    println( enumerat.nextElement() )
}
```

**Java 5+**
```java
package java.util;

public interface Iterator<E> {
  default void forEachRemaining(Consumer<? super E> action)
  boolean hasNext()
  E next()
  default void remove()
}
```

Данный интерфейс встроен в язык - в конструкцию for

В частности любой объект, который должен работать в `for(...)` должен реализовать интерфейс `Iterable`
```java
package java.util;
import java.util.Iterator;

public interface Iterable<E> {
    Iterator<E> iterator()
}
```

И после его можно использовать так

```java
Iterable<String> iter = List.of( "a", "b" );
for( String str : iter ){
    
}
```

Лишнее в интерфейсе `Iterator` метод `remove()` - он мутирует коллекции

**Java 8+**

```java

public java.util;
interface Spliterator<T> {
    int    characteristics()
    long    estimateSize()
    forEachRemaining(java.util.function.Consumer<? super T>) 
    default void    forEachRemaining(Consumer<? super T> action)
    default Comparator<? super T>    getComparator()
    default long    getExactSizeIfKnown()
    default boolean    hasCharacteristics(int characteristics)
    boolean    tryAdvance(Consumer<? super T> action)
    Spliterator<T>    trySplit()
}
```

Появился еще один способ итерации:

```java
List<String> lst = ...
lst.stream().filter( y ).forEach( x -> x )
```

Почему нельзя было встроить сразу `Iterable` `default` методы - не понятно, а так-то было бы удобно:

```java
lst.filter( y ).forEach( x -> x )
```

Eterable
-----------

Eterable - название - просто игра букв на изначальным словом Iterable.

`Eterable` - расширение существующего итерфейса `java.util.Iterable`

Ключевые функции

- "Ленивые" вычисления - по факту вычисления будут выполняться при выводе на экран, но не раньше - следствие - можно строить большие объемы данных.
- Дополнительные методы работы с итераторами

```java
public interface Eterable<A> extends java.lang.Iterable<A> {
// "Пустой" Итератор
static <A> Eterable<A>    empty() 

// Итератор по массиву
static <A> Eterable<A>    of(A... a)

// Создает итератор
static <A> Eterable<A>    of(java.lang.Iterable<? extends A> a)    

// Создает итератор по единичному значению
static <A> Eterable<A>    single(A a)    

// Создание итератора по дереву
static <A> TreeIterBuilder<A>    tree(A root, java.util.function.Function<A,java.lang.Iterable<? extends A>> follow)    

// Подсчет кол-ва элементов
default long    count()    

// Фильтрация исходных данных
default Eterable<A>    filter(java.util.function.Predicate<A> filter) 

// Возвращает первый элемент
default java.util.Optional<A>    first()    

// Ограничение исходного набора
default Eterable<A>    limit(long limit)    

// Отображения одного набора данных на другой
default <B> Eterable<B>    map(java.util.function.Function<A,B> map)    

// Фильтрация исходного набора - удаление null значений
default Eterable<A>    notNull()    

// Свертка значений
default <R> R    reduce(R initial, java.util.function.BiFunction<R,A,R> reducer)    

// Декартово произведение
default <B> Eterable<Pair<A,B>> product(Iterable<B> source)    

default <B,C> Eterable<A> product(
  Iterable<B> source1, 
  Iterable<C> source2, TripleConsumer<A,B,C> consumer)

...

// Преобразование в список
default java.util.List<A>    toList()    

// Присоединение данных к исходным
default Eterable<A> union(java.lang.Iterable<A>... iter)    

// Присоединение данных к исходным
default Eterable<A> union(Iterable<Iterable<A>> iter)    
}
```

Пример использования обычных операций

```java
Eterable<Integer> seq1 = 
    Eterable.single( 1 ).
    union( Eterable.of( 2, 3, 4, 5 ) );

seq1.
    product( seq1 ).
    map( p -> p.a() * p.b() ).
    filter( x -> x % 3 == 0 ).
    forEach( System.out::println );
```

1. Создаем последовательность из одного элемента [ 1 ] - `single( 1 )`
2. Объединяем ее с другой [2, 3, 4, 5] - получаем [1, 2, 3, 4, 5] - `union( …)`
3. Производим Декартово произведение самого на себя - `product( … )`
    - Получаем пары:
      - [ [1, 1], [1, 2], [1,3], [1,4], [1,5],
        [2, 1], [2, 2], … [5, 5] ]
4. Умножаем значение в парах - `map( … )`
5. фильтруем кратные трем - `filter( … )`
6. выводим

Итерация по дереву
-------------------

```java
File rootDirectory = new File(".");
Eterable.tree(
    // задаем корень
    rootDirectory,

    // задаем функцию перехода от текущего узла к дочерним
    dir -> dir.isDirectory() ? Arrays.asList(
        Objects.requireNonNull(dir.listFiles())
    ) : null
).walk().forEach( System.out::println );
```

Можно задавать следующие опции обхода дерева:

- Обход в глубину/ в ширину
- Пропуск ветвей
- Обнаружение циклов при обходе
- Получение информации о пути обхода - 
    с какого узла начали, какие узлы посетили, что бы добраться до текущего узла.
  
```java
@Test
public void sample3(){
    File rootDirectory = new File(".");

    Eterable<TreeStep<File>> iter = Eterable.tree(
        // задаем корень
        rootDirectory,

        // задаем функцию перехода от текущего узла к дочерним
        dir -> dir.isDirectory() ? Arrays.asList(
            Objects.requireNonNull(dir.listFiles())
        ) : null
    ).go();

    iter = iter.filter( step -> {
        String path = step.nodes().map(File::getName).reduce("", (a, b)->a + "/" +b);
        return path.startsWith("/./src/main/java/xyz/cofe/fn/Fn");
    });

    System.out.println("files "+iter.count());
    for( TreeStep<File> ts : iter ){
        System.out.println("file "+ts.getNode());
    }
}
```