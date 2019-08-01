package xyz.cofe.iter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Итератор по дереву
 * @param <A> тип узла дерева
 */
public class TreeIterator<A> implements Iterator<TreeStep<A>> {

    /**
     * Конструктор
     * @param init начальный узел
     * @param follow функция перехода к дочерним узлам
     */
    public TreeIterator(
        Iterable<A> init,
        Function<A, Iterable<A>> follow
    ) {
        this(init, follow, pollFirst(), pushLast(), checkCycles());
    }

    /**
     * Конструктор
     * @param init начальный узел
     * @param follow функция перехода к дочерним узлам
     * @param <A> тип узлов
     * @return итератор
     */
    public static <A> Eterable<TreeStep<A>> of(Iterable<A> init,Function<A, Iterable<A>> follow){
        if( init == null )throw new IllegalArgumentException( "init == null" );
        if( follow == null )throw new IllegalArgumentException( "follow == null" );
        return () ->  new TreeIterator<A>(init,follow);
    }

    /**
     * Конструктор
     * @param init начальный узел
     * @param follow функция перехода к дочерним узлам
     * @param poll функция выбора узла из рабочего набора узлов
     * @param push функция помещения очередного узла в рабочий набор
     */
    public TreeIterator(
        Iterable<A> init,
        Function<A, Iterable<A>> follow,
        Function<List<TreeStep<A>>, TreeStep<A>> poll,
        Consumer<PushStep<A>> push
                       ) {
        this(init, follow, poll, push, checkCycles());
    }

    /**
     * Конструктор
     * @param init начальный узел
     * @param follow функция перехода к дочерним узлам
     * @param poll функция выбора узла из рабочего набора узлов
     * @param push функция помещения очередного узла в рабочий набор
     * @param <A> тип узлов
     * @return итератор
     */
    public static <A> Eterable<TreeStep<A>> of(Iterable<A> init, Function<A, Iterable<A>> follow,
                                               Function<List<TreeStep<A>>, TreeStep<A>> poll,
                                               Consumer<PushStep<A>> push) {
        if( init == null ) throw new IllegalArgumentException("init == null");
        if( follow == null ) throw new IllegalArgumentException("follow == null");
        return ()->new TreeIterator<A>(init, follow, poll, push);
    }

    /**
     * Конструктор
     * @param init начальный узел
     * @param follow функция перехода к дочерним узлам
     * @param poll функция выбора узла из рабочего набора узлов
     * @param push функция помещения очередного узла в рабочий набор
     * @param allow функция проверки допустимости перехода к указанному узлу
     */
    public TreeIterator(
        Iterable<A> init,
        Function<A, Iterable<A>> follow,
        Function<List<TreeStep<A>>, TreeStep<A>> poll,
        Consumer<PushStep<A>> push,
        Predicate<TreeStep<A>> allow
                       ) {
        if( init == null ) throw new IllegalArgumentException("init == null");
        if( follow == null ) throw new IllegalArgumentException("follow == null");
        this.follow = follow;
        this.poll = poll != null ? poll : pollFirst();
        this.push = push != null ? push : pushOrdered();
        this.allow = allow != null ? allow : checkCycles();

        Set<A> visited = new LinkedHashSet<>();
        boolean nullAdded = false;
        for( A a : init ){
            if( a == null ){
                if( !nullAdded ){
                    nullAdded = true;
                    workset.add(new TreeStep<>(a));
                }
            } else if( !visited.contains(a) ){
                workset.add(new TreeStep<>(a));
                visited.add(a);
            }
        }
    }

    /**
     * Конструктор
     * @param init начальный узел
     * @param follow функция перехода к дочерним узлам
     */
    public TreeIterator(
        A init,
        Function<A, Iterable<A>> follow
    ) {
        this(init,follow,pollFirst(),pushOrdered(),checkCycles());
    }

    /**
     * Конструктор
     * @param init начальный узел
     * @param follow функция перехода к дочерним узлам
     * @param <A> тип узлов
     * @return итератор
     */
    public static <A> Eterable<TreeStep<A>> of(A init, Function<A, Iterable<A>> follow) {
        if( init == null ) throw new IllegalArgumentException("init == null");
        if( follow == null ) throw new IllegalArgumentException("follow == null");
        return ()->new TreeIterator<A>(init, follow);
    }

    /**
     * Конструктор
     * @param init начальный узел
     * @param follow функция перехода к дочерним узлам
     * @param poll функция выбора узла из рабочего набора узлов
     * @param push функция помещения очередного узла в рабочий набор
     */
    public TreeIterator(
        A init,
        Function<A, Iterable<A>> follow,
        Function<List<TreeStep<A>>, TreeStep<A>> poll,
        Consumer<PushStep<A>> push
    ) {
        this(init,follow,poll,push,checkCycles());
    }

    /**
     * Конструктор
     * @param init начальный узел
     * @param follow функция перехода к дочерним узлам
     * @param poll функция выбора узла из рабочего набора узлов
     * @param push функция помещения очередного узла в рабочий набор
     * @param <A> тип узлов
     * @return итератор
     */
    public static <A> Eterable<TreeStep<A>> of(A init, Function<A, Iterable<A>> follow,
                                               Function<List<TreeStep<A>>, TreeStep<A>> poll,
                                               Consumer<PushStep<A>> push) {
        if( init == null ) throw new IllegalArgumentException("init == null");
        if( follow == null ) throw new IllegalArgumentException("follow == null");
        return ()->new TreeIterator<A>(init, follow, poll, push);
    }

    /**
     * Конструктор
     * @param init начальный узел
     * @param follow функция перехода к дочерним узлам
     * @param poll функция выбора узла из рабочего набора узлов
     * @param push функция помещения очередного узла в рабочий набор
     * @param allow функция проверки допустимости перехода к указанному узлу
     */
    public TreeIterator(
        A init,
        Function<A, Iterable<A>> follow,
        Function<List<TreeStep<A>>, TreeStep<A>> poll,
        Consumer<PushStep<A>> push,
        Predicate<TreeStep<A>> allow
    ) {
        if( init == null ) throw new IllegalArgumentException("init == null");
        if( follow == null ) throw new IllegalArgumentException("follow == null");
        this.follow = follow;
        this.poll = poll != null ? poll : pollFirst();
        this.push = push != null ? push : pushOrdered();
        this.allow = allow != null ? allow : checkCycles();

        Set<A> visited = new LinkedHashSet<>();
        boolean nullAdded = false;

        workset.add(new TreeStep<>(init));
    }

    @Override
    public boolean hasNext() {
        return !workset.isEmpty();
    }

    protected final List<TreeStep<A>> workset = new ArrayList<>();
    protected final Function<A, Iterable<A>> follow;

    protected final Function<List<TreeStep<A>>, TreeStep<A>> poll;
    public static <A> Function<List<TreeStep<A>>, TreeStep<A>> pollFirst() {
        return (ws)->{
            if( ws.isEmpty() ) return null;
            return ws.remove(0);
        };
    }
    public static <A> Function<List<TreeStep<A>>, TreeStep<A>> pollLast() {
        return (ws)->{
            if( ws.isEmpty() ) return null;
            return ws.remove(ws.size()-1);
        };
    }

    /**
     * Данные для вставки узла в список рабочих узлов
     * @param <A> тип узла
     */
    public static class PushStep<A> {
        public PushStep(List<TreeStep<A>> workset, TreeStep<A> node, int nodeIndex) {
            this.workset = workset;
            this.node = node;
            this.nodeIndex = nodeIndex;
        }

        public PushStep(List<TreeStep<A>> workset, TreeStep<A> node) {
            this(workset,node,0);
        }

        public PushStep() {
            this(null,null,0);
        }

        protected List<TreeStep<A>> workset;

        public List<TreeStep<A>> getWorkset() {
            return workset;
        }

        public void setWorkset(List<TreeStep<A>> workset) {
            this.workset = workset;
        }

        protected TreeStep<A> node;

        public TreeStep<A> getNode() {
            return node;
        }

        public void setNode(TreeStep<A> node) {
            this.node = node;
        }

        protected int nodeIndex;

        public int getNodeIndex() {
            return nodeIndex;
        }

        public void setNodeIndex(int nodeIndex) {
            this.nodeIndex = nodeIndex;
        }
    }
    protected final Consumer<PushStep<A>> push;
    protected static <A> Consumer<PushStep<A>> pushLast() {
        return (ps)->{
            ps.getWorkset().add(ps.getNode());
        };
    }
    protected static <A> Consumer<PushStep<A>> pushFirst() {
        return (ps)->{
            ps.getWorkset().add(0, ps.getNode());
        };
    }
    protected static <A> Consumer<PushStep<A>> pushOrdered() {
        return (ps)->{
            ps.getWorkset().add(ps.getNodeIndex(), ps.getNode());
        };
    }

    protected final Predicate<TreeStep<A>> allow;
    protected static <A> Predicate<TreeStep<A>> checkCycles() {
        return (ts)->{
            return !ts.hasCycles();
        };
    }

    @Override
    public TreeStep<A> next() {
        if( workset.isEmpty() ) return null;
        TreeStep<A> ts = poll.apply(workset);

        Iterable<A> nextset = follow.apply(ts.getNode());
        if( nextset != null ){
            PushStep<A> push = new PushStep<>();
            push.setWorkset(workset);
            int idx = -1;
            for( A a : nextset ){
                TreeStep<A> nexts = ts.follow(a);
                if( nexts != null && allow.test(nexts) ){
                    idx++;
                    push.setNode(nexts);
                    push.setNodeIndex(idx);
                    this.push.accept(push);
                }
            }
        }

        return ts;
    }
}
