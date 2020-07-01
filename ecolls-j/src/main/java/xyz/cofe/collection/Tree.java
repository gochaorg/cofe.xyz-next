package xyz.cofe.collection;

import xyz.cofe.iter.*;

/**
 * Узел дерева
 * @param <SELF> тип элемента дерева
 */
@SuppressWarnings("unchecked")
public interface Tree<SELF extends Tree<SELF>> extends ImTree<SELF>, ImTreeWalk<SELF> {
    /**
     * Возвращает кол-во дочерних элементов
     * @return кол-во элементов
     */
    default int count() {
        return TreeImpl.nodesCount(this);
    }

    /**
     * Возвращает дочерний элемент по его индексу
     * @param idx индекс дочернего элемента
     * @return дочерний элемент
     */
    default SELF get( int idx) {
        return TreeImpl.node(this, idx);
    }

    /**
     * Возвращает дочерние элементы
     * @return дочерние элементы
     */
    default Eterable<SELF> nodes(){
        return new EterableProxy<>(TreeImpl.nodesOf(this));
    }

    //region modify children nodes

    /**
     * Добавляет дочерний узел
     * @param node узел
     */
    default void append( SELF node ){
        TreeImpl.append((SELF)this,node);
    }

    /**
     * Добавляет дочерние узлы
     * @param nodes дочерние узлы
     */
    default void appends( SELF... nodes ){
        TreeImpl.append((SELF)this,nodes);
    }

    /**
     * Добавляет дочерние узлы
     * @param nodes дочерние узлы
     */
    default void appends( Iterable<SELF> nodes ){
        TreeImpl.append((SELF)this,nodes);
    }

    /**
     * Добавляет дочерний узел
     * @param idx индекс в какую позицию будет добавлен узел
     * @param node узел
     */
    default void insert( int idx, SELF node ){
        TreeImpl.insert((SELF)this,idx, node);
    }

    /**
     * Добавляет дочерние узлы
     * @param idx индекс в какую позицию будет добавлены узлы
     * @param nodes дочерние узлы
     */
    default void inserts( int idx, SELF... nodes ){
        TreeImpl.insert(this,idx, nodes);
    }

    /**
     * Добавляет дочерние узлы
     * @param idx индекс в какую позицию будет добавлены узлы
     * @param nodes дочерние узлы
     */
    default void inserts( int idx, Iterable<SELF> nodes ){
        TreeImpl.insert(this,idx, nodes);
    }

    /**
     * Указывает/заменяет узлы
     * @param idx индекс
     * @param node узел
     */
    default void set( int idx, SELF node ){
        TreeImpl.set(this,idx, node);
    }

    /**
     * Указывает/заменяет узлы
     * @param idx индекс
     * @param nodes узелы
     */
    default void sets( int idx, SELF... nodes ){
        TreeImpl.set(this,idx, nodes);
    }

    /**
     * Указывает/заменяет дочерние узлы
     * @param idx индекс
     * @param nodes узелы
     */
    default void sets( int idx, Iterable<SELF> nodes ){
        TreeImpl.set(this,idx, nodes);
    }

    /**
     * Удаляет дочерний узел
     * @param index индексы узлов
     */
    default void remove( int index ){
        TreeImpl.deleteByIndex(this, index);
    }

    /**
     * Удаляет дочерние узлы
     * @param indexes индексы узлов
     */
    default void removes( int ... indexes ){
        TreeImpl.deleteByIndex(this, indexes);
    }

    /**
     * Удаляет дочерние узлы
     * @param indexes индексы узлов
     */
    default void removes( Iterable<Integer> indexes ){
        TreeImpl.deleteByIndex(this, indexes);
    }

    /**
     * Удаляет дочерние узлы
     * @param node дочерние узлы
     */
    default void delete( SELF node ){
        TreeImpl.deleteByValue(this, node);
    }

    /**
     * Удаляет дочерние узлы
     * @param nodes дочерние узлы
     */
    default void deletes( SELF... nodes ){
        TreeImpl.deleteByValue(this, nodes);
    }

    /**
     * Удаляет дочерние узлы
     * @param nodes дочерние узлы
     */
    default void deletes( Iterable<SELF> nodes ){
        TreeImpl.deleteByValue(this, nodes);
    }

    /**
     * Удаляет дочерние узлы
     */
    default void clear(){
        TreeImpl.clear(this);
    }
    //endregion
}
