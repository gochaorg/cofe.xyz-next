package xyz.cofe.collection;

/**
 * Событие изменения дерева
 * @param <A> тип узла дерева
 */
public class TreeEvent<A extends Tree<A>> {
    /**
     * Событие изменения состава дерева
     * @param <A> тип узла дерева
     */
    public static class ParentChild<A extends Tree<A>> extends TreeEvent<A> implements CollectionEvent<A,A> {
        /**
         * Конструктрор
         * @param parent родительский узел
         * @param child дочерний узел
         */
        public ParentChild(Tree<A> parent, A child) {
            this.parent = (A)parent;
            this.child = child;
        }

        protected A parent;

        /**
         * Возвращает ссылку на родительский узел
         * @return родительский узел
         */
        public A getParent() { return parent; }

        /**
         * Указывает ссылку на родительский узел
         * @param parent родительский узел
         */
        public void setParent(A parent) { this.parent = parent; }

        protected A child;

        /**
         * Возвращает дочерний узел
         * @return дочерний узел
         */
        public A getChild() { return child; }

        /**
         * Устанавливает дочерний узел
         * @param child дочерний узел
         */
        public void setChild(A child) { this.child = child; }

        /**
         * Возвращает источник события
         * @return источник события
         */
        @Override
        public A getSource() {
            return getParent();
        }
    }

    /**
     * Событие добавление дочернего узла
     * @param <A> тип узла дерева
     */
    public static class Added<A extends Tree<A>> extends ParentChild<A> implements AddedEvent<A,A> {
        /**
         * Конструктор
         * @param parent родительский узел
         * @param child дочерний узел
         */
        public Added(Tree<A> parent, A child) {
            super(parent, child);
        }

        @Override
        public A getNewItem() {
            return getChild();
        }
    }

    /**
     * Событие добавление дочернего узла с указанием позиции вставки
     * @param <A> тип узла дерева
     */
    public static class Inserted<A extends Tree<A>> extends Added<A> implements InsertedEvent<A,Integer,A> {
        /**
         * Конструктор
         * @param parent родительский узел
         * @param child дочерний узел
         * @param index позиция в списке дочерних узлов
         */
        public Inserted(Tree<A> parent, A child, Integer index) {
            super(parent, child);
            this.index = index;
        }
        protected Integer index;
        @Override
        public Integer getIndex() {
            return this.index;
        }
    }

    /**
     * Событие удаления дочернего узла
     * @param <A> тип узла дерева
     */
    public static class Removed<A extends Tree<A>> extends ParentChild<A> implements RemovedEvent<A,A> {
        /**
         * Конструктор
         * @param parent родительский узел
         * @param child дочерний узел
         */
        public Removed(Tree<A> parent, A child) {
            super(parent, child);
        }

        @Override
        public A getOldItem() {
            return getChild();
        }
    }

    /**
     * Событие удаления дочернего узла с указанием позиции удаления
     * @param <A> тип узла дерева
     */
    public static class Deleted<A extends Tree<A>> extends Removed<A> implements DeletedEvent<A,Integer,A> {
        /**
         * Конструктор
         * @param parent родительский узел
         * @param child дочерний узел
         * @param index позиция в списке дочерних узлов
         */
        public Deleted(Tree<A> parent, A child, Integer index){
            super(parent,child);
            this.index = index;
        }
        protected Integer index;
        @Override
        public Integer getIndex() {
            return this.index;
        }
    }

    /**
     * Событие замены дочернего элемента
     * @param <A> Тип узла
     */
    public static class Updated<A extends Tree<A>> extends TreeEvent<A> implements UpdatedEvent<A,Integer,A>
    {
        /**
         * Конструктор
         * @param parent родительский узел
         * @param idx позиция замены
         * @param oldChild предыдущий узел
         * @param newChild текущий узел
         */
        public Updated(Tree<A> parent, Integer idx, A oldChild, A newChild){
            source = (A)parent;
            index = idx;
            oldItem = oldChild;
            newItem = newChild;
        }

        protected A newItem;

        @Override
        public A getNewItem() {
            return newItem;
        }

        protected Integer index;

        @Override
        public Integer getIndex() {
            return index;
        }

        protected A oldItem;

        @Override
        public A getOldItem() {
            return oldItem;
        }

        protected A source;

        @Override
        public A getSource() {
            return source;
        }
    }

    /**
     * Подписчик на событие
     * @param <A> тип узла дерева
     */
    public interface Listener<A extends Tree<A>> {
        void treeEvent(TreeEvent<A> event);
    }
}
