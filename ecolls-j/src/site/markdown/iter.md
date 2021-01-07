Алгоритмы итерации
=====================

Эволюция итерации в java

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

