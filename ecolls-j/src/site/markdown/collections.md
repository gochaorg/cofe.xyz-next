Коллекции
==================

Пакет `xyz.cofe.collection` реализует следующие функции

- Коллекции с поддержкой уведомлений
    - Списки - расширенный интерфейс `java.util.List` 
      - с поддержкой уведомлений INSERT/UPDATE/DELETE после факта изменения - см `xyz.cofe.collection.BasicEventList` 
      - с поддержкой уведомлений INSERT/UPDATE/DELETE перед фактом+после факта изменения - см `xyz.cofe.collection.StdEventList` 
    - Карты - расширенный интерфейс `java.util.Map` с поддержкой уведомлений INSERT/UPDATE/DELETE
    - Множества - расширенный интерфейс `java.util.Set` с поддержкой уведомлений INSERT/UPDATE/DELETE
- Индексированные множества `xyz.cofe.collection.IndexSet`
- Биекция одного множества на другое `xyz.cofe.collection.Bijection`
- Специализированные коллекции
    - Упорядоченная карта, где ключ - класс (`java.lang.Class`)
    - Упорядоченное множество классов (`java.lang.Class`)
    - Текстовые карты (ключ - `String`) без учета регистра
    - Текстовые карты с префиксом
- Деревья
    - С поддержкой уведомления
- Указатель со стеком
- Графы
    - Направленный граф
    - Направленный мультиграф
    - Поиск в графе
    - Уведомления об изменении графа

Коллекции с поддержкой уведомлений
----------------------------------

Коллекции с поддержкой уведомлений реализуют интерфейс 
`xyz.cofe.collection.CollectionEventPublisher<C,E>`

где
- `C` - Тип коллекции
- `E` - Тип уведомления

Для списка будет следующий дочерний интерфейс

```java
public interface EventList<E>
    extends List<E>,
            CollectionEventPublisher<EventList<E>, E>,
            ...
```

Для карты

```java
public interface EventMap<K,V>
    extends
        Map<K,V>,
        CollectionEventPublisher<EventMap<K,V>, V>,
        ...
```

Для множества

```java
public interface EventSet<E>
    extends
        Set<E>,
        CollectionEventPublisher<EventSet<E>, E>,
        ...
```

### Методы интерфейса `CollectionEventPublisher`

- `AutoCloseable addCollectionListener(CollectionListener<C,E> listener)`
  Добавления подписчика
- `void removeCollectionListener(CollectionListener<C,E> listener)`
  Удаления подписчика
- `void fireCollectionEvent(CollectionEvent<C,E> event)`
  Уведомления подписчиков о наступлении событии

Подписчик это экземпляр интерфейса `CollectionListener`

```java
package xyz.cofe.collection;

/**
 * Подписчик на события коллекции
 * @param <C> тип коллекции
 * @param <E> тип элемента коллекции
 */
public interface CollectionListener<C,E> {
    /**
     * Уведомление о соыбитии коллекции
     * @param event событие
     */
    void collectionEvent(CollectionEvent<C,E> event);
}
```

Метод `collectionEvent` принимает событие

```java
package xyz.cofe.collection;

/**
 * Событие изменения колллекции
 * @param <C> тип коллекции
 * @param <E> тип элемента коллекции
 */
public interface CollectionEvent<C,E> {
    /**
     * Ссылка на коллекцию
     * @return коллеция
     */
    C getSource();
}
```

У события есть метод `getSource()` - который вернет ссылку на коллекцию.

Подписка на события выглядит примерно таким образом

```java
EventList<A> list =
list.addaddCollectionListener( event -> {
        // Тут код обработчика события
});
```

Полный пример работы смотрите ниже
  
onInsert/onUpdate/onDelete
------------------------------

Пример с CollectionListener
------------------------------

```java
package xyz.cofe.collection;

import java.util.Arrays;
import org.junit.Test;

public class SampleEvListTest {
    @Test
    public void test01(){
        BasicEventList<String> evlist = new BasicEventList<>();
        evlist.addCollectionListener(new CollectionListener<EventList<String>, String>() {
            @Override
            public void collectionEvent(CollectionEvent<EventList<String>, String> event){
                if( event!=null ){
                    System.out.println("  event:");
                    System.out.println("    class: "+event.getClass());
                    Arrays.stream(event.getClass().getGenericInterfaces()).forEach( t ->
                        System.out.println("    itf: "+t)
                    );
                    if( event instanceof InsertedEvent ){
                        InsertedEvent<EventList<String>,Integer,String> insEv = (InsertedEvent<EventList<String>,Integer,String>)event;
                        System.out.println("  inserted:");
                        System.out.println("    new item: "+insEv.getNewItem());
                        System.out.println("    key: "+insEv.getIndex());
                    }
                    if( event instanceof UpdatedEvent ){
                        UpdatedEvent<EventList<String>,Integer,String> updEv = (UpdatedEvent<EventList<String>, Integer, String>) event;
                        System.out.println("  updated:");
                        System.out.println("    new item: "+updEv.getNewItem());
                        System.out.println("    old item: "+updEv.getOldItem());
                        System.out.println("    key: "+updEv.getIndex());
                    }
                    if( event instanceof DeletedEvent ){
                        DeletedEvent<EventList<String>,Integer,String> dltEv = (DeletedEvent<EventList<String>, Integer, String>) event;
                        System.out.println("  deleted:");
                        System.out.println("    old item: "+dltEv.getOldItem());
                        System.out.println("    key: "+dltEv.getIndex());
                    }
                    if( event instanceof RemovedEvent ){
                        RemovedEvent<EventList<String>,String> addEv = (RemovedEvent<EventList<String>, String>) event;
                        System.out.println("  removed:");
                        System.out.println("    old item: "+addEv.getOldItem());
                    }
                    if( event instanceof AddedEvent ){
                        AddedEvent<EventList<String>,String> addEv = (AddedEvent<EventList<String>, String>) event;
                        System.out.println("  added:");
                        System.out.println("    new item: "+addEv.getNewItem());
                    }
                }
            }
        });

        System.out.println("add( \"abc\" )");
        evlist.add("abc");

        System.out.println("addAll( \"bcd\", \"cde\" )");
        evlist.addAll(Arrays.asList("bcd","cde"));

        System.out.println("set( 0, \"def\" )");
        evlist.set(0,"def");

        System.out.println("remove( 0 )");
        evlist.remove(0);

        System.out.println("clear");
        evlist.clear();
    }
}
```

Лог

```
add( "abc" )
  event:
    class: class xyz.cofe.collection.InsertedEvent$1
    itf: xyz.cofe.collection.InsertedEvent<C, K, E>
  inserted:
    new item: abc
    key: 0
  added:
    new item: abc
addAll( "bcd", "cde" )
  event:
    class: class xyz.cofe.collection.InsertedEvent$1
    itf: xyz.cofe.collection.InsertedEvent<C, K, E>
  inserted:
    new item: bcd
    key: 1
  added:
    new item: bcd
  event:
    class: class xyz.cofe.collection.InsertedEvent$1
    itf: xyz.cofe.collection.InsertedEvent<C, K, E>
  inserted:
    new item: cde
    key: 2
  added:
    new item: cde
set( 0, "def" )
  event:
    class: class xyz.cofe.collection.UpdatedEvent$1
    itf: xyz.cofe.collection.UpdatedEvent<C, K, E>
  updated:
    new item: def
    old item: abc
    key: 0
  removed:
    old item: abc
  added:
    new item: def
remove( 0 )
  event:
    class: class xyz.cofe.collection.DeletedEvent$1
    itf: xyz.cofe.collection.DeletedEvent<C, K, E>
  deleted:
    old item: def
    key: 0
  removed:
    old item: def
clear
  event:
    class: class xyz.cofe.collection.DeletedEvent$1
    itf: xyz.cofe.collection.DeletedEvent<C, K, E>
  deleted:
    old item: cde
    key: 1
  removed:
    old item: cde
  event:
    class: class xyz.cofe.collection.DeletedEvent$1
    itf: xyz.cofe.collection.DeletedEvent<C, K, E>
  deleted:
    old item: bcd
    key: 0
  removed:
    old item: bcd
```