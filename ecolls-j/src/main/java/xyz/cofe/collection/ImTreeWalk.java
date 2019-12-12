package xyz.cofe.collection;

import xyz.cofe.iter.Eterable;
import xyz.cofe.iter.MapIterable;
import xyz.cofe.iter.TreeIterator;
import xyz.cofe.iter.TreeStep;

import java.util.Iterator;

public interface ImTreeWalk<A extends ImTree<? extends A>> extends ImTree<A> {
    /**
     * Обход дочерних элементов
     * @param <A> тип дочерних элементов
     */
    public static class Walk<A extends ImTree<? extends A>> implements Eterable<A> {
        protected final ImTree<A> from;

        /**
         * Конструктор
         * @param from начальный узел
         */
        public Walk(ImTree<A> from){
            if( from == null )throw new IllegalArgumentException( "from == null" );
            this.from = from;
        }

        /**
         * Итератор по узлам дерева
         * @return итератор
         */
        public Eterable<TreeStep<A>> tree(){
            return TreeIterator.of( (A)from, ImTree::nodes );
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
        return new Walk(this);
    }
}
