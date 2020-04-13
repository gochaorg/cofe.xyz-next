package xyz.cofe.iter;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Создание итератора по дереву
 *
 * @param <A> тип узла дерева
 */
public interface TreeIterBuilder<A> {
    /**
     * Создание итератора
     *
     * @return итератор
     */
    Eterable<A> walk();

    /**
     * Создание итератора
     * @return итератор
     */
    Eterable<TreeStep<A>> go();

    /**
     * Указывает функцию извлечения очередного узла из рабочего набора
     * @param poll функция
     * @return self ссылка
     */
    TreeIterBuilder<A> poll( Function<List<TreeStep<A>>, TreeStep<A>> poll );

    /**
     * Указывает функцию помещения очередного узла в рабочий набор
     * @param push функция
     * @return self ссылка
     */
    TreeIterBuilder<A> push( Consumer<TreeIterator.PushStep<A>> push );

    /**
     * Указывает функцию фильтрации
     * @param allow функция фильтрации
     * @return self ссылка
     */
    TreeIterBuilder<A> filter( Predicate<TreeStep<A>> allow );

    /**
     * Помещать в начало набора очередной узел
     * @return self ссылка
     */
    TreeIterBuilder<A> pushFirst();

    /**
     * Помещать в конец набора очередной узел
     * @return self ссылка
     */
    TreeIterBuilder<A> pushLast();

    /**
     * Помещать в начало набора очередной узел, в порядке извлечения
     * @return self ссылка
     */
    TreeIterBuilder<A> pushOrdered();

    /**
     * Брать первый узел из рабочего набора узлов
     * @return self ссылка
     */
    TreeIterBuilder<A> pollFirst();

    /**
     * Брать последний узел из рабочего набора узлов
     * @return self ссылка
     */
    TreeIterBuilder<A> pollLast();

    /**
     * Проверять на циклы при обходе
     * @return self ссылка
     */
    TreeIterBuilder<A> checkCycles();
}
