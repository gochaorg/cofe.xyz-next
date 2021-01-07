Функциональные интерфейсы
=============================

Пакет `xyz.cofe.fn.*` предоставляет 3 основных сущьности

- Кортеж (_xyz.cofe.fn.TupleXX_) - упорядоченное множество разнотипных элементов
- Функция (_xyz.cofe.fn.FnXX_) - функции с аргментами и результатом
- "Потребитель" (_xyz.cofe.fn.ConsumerXX_) - функция без результата

Кортеж
-------------------------

Кортеж - структура данных - упорядоченное множество, фиксированного размера.

Основные отличия от массива, списков, множеств:

- Элементы в структуре могут быть разных типов
- Доступ к элементу производиться по его индексу (a(), b(), … y())

**Потребность** в этой структуре — передача нескольких (2 и более) типизированных значений из функции (результат вызова) в другую функцию без объявления типа

Без кортежей так:

```java
class Result1 {
  public Result1( String name, int count ){
    this.name = name;
    this.count = count;
  }
  public String name;
  public int count;
}

Result1 compute( String inputArgs ){
...
}

void processResult( Result1 res ){
...
}
```

С кортежами

```java
import xyz.cofe.fn.*;

Tuple2<String,Integer> compute(String inputArgs){
    ...
    return Tuple.of( "abc", 123 );
}

void process( Tuple2<String,Integer> abc ){
    ...
}
```

Кортежи могут быть вложенные

```java
Tuple2<Tuple3<String,String,Boolean>,Double> value
    = Tuple.of( Tuple.of( "a", "b", true ), 12.5 )
```

Для чего еще можно:

Сигнатуру анонимной функции можно описать как кортеж (Tuple2) из двух элементов:
- Входные аргументы
- Исходящий результат

Не анонимной функции — кортеж из трех элементов - Tuple3
- Входные аргументы
- Исходящий результат
- Имя функции

Кортеж можно применить (apply()) к функции Fn1...Fn25 и к потребителю Consumer1...Consumer25

**Java 11**

```java
void visit( Fn2<String,Integer> visitor ){
    var res = Tuple.of( a, b ).apply( f );
    ...
}
```

Функции и потребители
-----------------------

Начиная с java 8 появились лямбды в java.

- java.util.Function,
- java.util.Consumer

Consumer - отличается (по существу) от Function, только тем что не возвращает результат

в java 8 есть функции для 2 аргументов - BiFunction, а также потребители для 2 аргументов.

Для большего кол-ва аргументов нет.
Объяснения на [stackoverflow](https://stackoverflow.com/questions/18400210/java-8-where-is-trifunction-and-kin-in-java-util-function-or-what-is-the-alt) напоминает, что кто-то объелся грибов с хаскелем.

Передача функции с тремя аргументами

```java
import xyz.cofe.fn.*;

void call( Fn3<Integer,Integer,Integer,Double> f ){
    Double result = f.apply(1,2,3);
}
```

Передача Consumer с тремя аргументами

```java
import xyz.cofe.fn.*;

void call( Consumer3<Integer,Integer,Integer> f ){
    f.accept(1,2,3);
}
```