package xyz.cofe.collection;

import xyz.cofe.iter.*;

import java.util.Iterator;

/**
 * Узел дерева
 * @param <A> тип элемента дерева
 */
@SuppressWarnings("unchecked")
public interface Tree<A extends Tree<A>> {
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
    default A get(int idx) {
        return TreeImpl.node(this, idx);
    }

    /**
     * Возвращает дочерние элементы
     * @return дочерние элементы
     */
    default Eterable<A> nodes(){
        return new EterableProxy<>(TreeImpl.nodesOf(this));
    }

    //region modify children nodes

    /**
     * Добавляет дочерний узел
     * @param node узел
     */
    default void append( A node ){
        TreeImpl.append(this,node);
    }

    /**
     * Добавляет дочерние узлы
     * @param nodes дочерние узлы
     */
    default void appends( A ... nodes ){
        TreeImpl.append(this,nodes);
    }

    /**
     * Добавляет дочерние узлы
     * @param nodes дочерние узлы
     */
    default void appends( Iterable<A> nodes ){
        TreeImpl.append(this,nodes);
    }

    /**
     * Добавляет дочерний узел
     * @param idx индекс в какую позицию будет добавлен узел
     * @param node узел
     */
    default void insert( int idx, A node ){
        TreeImpl.insert(this,idx, node);
    }

    /**
     * Добавляет дочерние узлы
     * @param idx индекс в какую позицию будет добавлены узлы
     * @param nodes дочерние узлы
     */
    default void inserts( int idx, A ... nodes ){
        TreeImpl.insert(this,idx, nodes);
    }

    /**
     * Добавляет дочерние узлы
     * @param idx индекс в какую позицию будет добавлены узлы
     * @param nodes дочерние узлы
     */
    default void inserts( int idx, Iterable<A> nodes ){
        TreeImpl.insert(this,idx, nodes);
    }

    /**
     * Указывает/заменяет узлы
     * @param idx индекс
     * @param node узел
     */
    default void set( int idx, A node ){
        TreeImpl.set(this,idx, node);
    }

    /**
     * Указывает/заменяет узлы
     * @param idx индекс
     * @param nodes узелы
     */
    default void sets( int idx, A ... nodes ){
        TreeImpl.set(this,idx, nodes);
    }

    /**
     * Указывает/заменяет дочерние узлы
     * @param idx индекс
     * @param nodes узелы
     */
    default void sets( int idx, Iterable<A> nodes ){
        TreeImpl.set(this,idx, nodes);
    }

    /**
     * Удаляет дочерние узлы
     * @param indexes индексы узлов
     */
    default void remove( int indexes ){
        TreeImpl.deleteByIndex(this, indexes);
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
    default void delete( A node ){
        TreeImpl.deleteByValue(this, node);
    }

    /**
     * Удаляет дочерние узлы
     * @param nodes дочерние узлы
     */
    default void deletes( A ... nodes ){
        TreeImpl.deleteByValue(this, nodes);
    }

    /**
     * Удаляет дочерние узлы
     * @param nodes дочерние узлы
     */
    default void deletes( Iterable<A> nodes ){
        TreeImpl.deleteByValue(this, nodes);
    }

    /**
     * Удаляет дочерние узлы
     */
    default void clear(){
        TreeImpl.clear(this);
    }
    //endregion

    //region walk
    /**
     * Обход дочерних элементов
     * @param <A> тип дочерних элементов
     */
    public static class Walk<A extends Tree<A>> implements Eterable<A> {
        protected final Tree<A> from;

        /**
         * Конструктор
         * @param from начальный узел
         */
        public Walk(Tree<A> from){
            if( from == null )throw new IllegalArgumentException( "from == null" );
            this.from = from;
        }

        /**
         * Итератор по узлам дерева
         * @return итератор
         */
        public Eterable<TreeStep<A>> tree(){
            return TreeIterator.of( (A)from, Tree::nodes );
        }

        /**
         * Итератор по узлам дерева
         * @return итератор
         */
        public Eterable<A> go(){
            return new MapIterable<TreeStep<A>,A>(tree(), TreeStep::getNode);
        }

        @Override
        public Iterator<A> iterator() {
            return go().iterator();
        }
    }

    /**
     * Обход дочерних элементов
     * @return итератор по дочерним узлам
     */
    default Walk<A> walk(){
        return new Walk<A>(this);
    }
    //endregion
}
