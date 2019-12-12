package xyz.cofe.iter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Итератор по дереву
 * @param <A> тип узла дерева
 */
@SuppressWarnings("WeakerAccess")
public class TreeIterator<A> implements Iterator<TreeStep<A>> {

    /**
     * Конструктор
     * @param init начальный узел
     * @param follow функция перехода к дочерним узлам
     */
    public TreeIterator(
        Iterable<? extends A> init,
        Function<A, Iterable<? extends A>> follow
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
    public static <A> Eterable<TreeStep<A>> of(Iterable<? extends A> init,Function<A, Iterable<? extends A>> follow){
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
        Iterable<? extends A> init,
        Function<A, Iterable<? extends A>> follow,
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
    public static <A> Eterable<TreeStep<A>> of(Iterable<? extends A> init, Function<A, Iterable<? extends A>> follow,
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
        Iterable<? extends A> init,
        Function<A, Iterable<? extends A>> follow,
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

        syncWorkset( ()->{
            boolean nullAdded = false;
            for( A a : init ){
                if( a==null ){
                    if( !nullAdded ){
                        nullAdded = true;
                        getWorkset().add(new TreeStep<>(a));
                    }
                } else if( !visited.contains(a) ){
                    getWorkset().add(new TreeStep<>(a));
                    visited.add(a);
                }
            }
        });
    }

    /**
     * Конструктор
     * @param init начальный узел
     * @param follow функция перехода к дочерним узлам
     */
    public TreeIterator(
        A init,
        Function<A, Iterable<? extends A>> follow
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
    public static <A> Eterable<TreeStep<A>> of(A init, Function<A, Iterable<? extends A>> follow) {
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
        Function<A, Iterable<? extends A>> follow,
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
    public static <A> Eterable<TreeStep<A>> of(A init, Function<A, Iterable<? extends A>> follow,
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
     * @param poll (nullable) функция выбора узла из рабочего набора узлов
     * @param push (nullable) функция помещения очередного узла в рабочий набор
     * @param allow (nullable) функция проверки допустимости перехода к указанному узлу
     */
    public TreeIterator(
        A init,
        Function<A, Iterable<? extends A>> follow,
        Function<List<TreeStep<A>>, TreeStep<A>> poll,
        Consumer<PushStep<A>> push,
        Predicate<TreeStep<A>> allow
    ){
        this(init,follow,poll,push,allow,null,null);
    }

    /**
     * Конструктор
     * @param init начальный узел
     * @param follow функция перехода к дочерним узлам
     * @param poll (nullable) функция выбора узла из рабочего набора узлов
     * @param push (nullable) функция помещения очередного узла в рабочий набор
     * @param allow (nullable) функция проверки допустимости перехода к указанному узлу
     * @param syncObj (nullable) объект который используеться для синхронизации workset
     */
    public TreeIterator(
        A init,
        Function<A, Iterable<? extends A>> follow,
        Function<List<TreeStep<A>>, TreeStep<A>> poll,
        Consumer<PushStep<A>> push,
        Predicate<TreeStep<A>> allow,
        Object syncObj
    ){
        this(init,follow,poll,push,allow,syncObj,null);
    }

    /**
     * Конструктор
     * @param init начальный узел
     * @param follow функция перехода к дочерним узлам
     * @param poll (nullable) функция выбора узла из рабочего набора узлов
     * @param push (nullable) функция помещения очередного узла в рабочий набор
     * @param allow (nullable) функция проверки допустимости перехода к указанному узлу
     * @param syncObj (nullable) объект который используеться для синхронизации workset
     * @param syncWs (nullable) функция синхронизации
     */
    public TreeIterator(
        A init,
        Function<A, Iterable<? extends A>> follow,
        Function<List<TreeStep<A>>, TreeStep<A>> poll,
        Consumer<PushStep<A>> push,
        Predicate<TreeStep<A>> allow,
        Object syncObj,
        Function<Supplier<Object>,Object> syncWs
    ) {
        if( init == null ) throw new IllegalArgumentException("init == null");
        if( follow == null ) throw new IllegalArgumentException("follow == null");
        this.follow = follow;
        this.poll = poll != null ? poll : pollFirst();
        this.push = push != null ? push : pushOrdered();
        this.allow = allow != null ? allow : checkCycles();
        this.syncWorkset = syncWs;
        this.syncWorksetObject = syncObj;

        syncWorkset( ()->{
            getWorkset().add(new TreeStep<>(init));
        });
    }

    @Override
    public boolean hasNext() {
        return syncWorkset( ()-> !getWorkset().isEmpty() );
    }

    protected volatile List<TreeStep<A>> worksetInstance;
    protected List<TreeStep<A>> getWorkset(){
        if( worksetInstance!=null )return worksetInstance;
        synchronized( this ){
            if( worksetInstance!=null )return worksetInstance;
            worksetInstance = new ArrayList<>();
            return worksetInstance;
        }
    }

    protected volatile Object syncWorksetObject = this;
    protected volatile Function<Supplier<Object>,Object> syncWorkset;

    public Function<Supplier<Object>,Object> getSyncWorkset(){
        return syncWorkset;
    }

    public void setSyncWorkset( Function<Supplier<Object>,Object> syncWorkset ){
        this.syncWorkset = syncWorkset;
    }

    protected <R> R syncWorkset( Supplier<R> code ){
        if( code==null ) throw new IllegalArgumentException("code==null");

        Function syncFn = getSyncWorkset();
        if( syncFn!=null ){
            Object res = syncFn.apply(code);
            return (R)res;
        }

        Object syncObj = syncWorksetObject;
        if( syncObj!=null ){
            synchronized( syncObj ){
                return code.get();
            }
        }
        return code.get();
    }

    protected void syncWorkset( Runnable code ){
        if( code==null ) throw new IllegalArgumentException("code==null");

        Function syncFn = getSyncWorkset();
        if( syncFn!=null ){
            syncFn.apply(code);
            return;
        }

        Object syncObj = syncWorksetObject;
        if( syncObj!=null ){
            synchronized( syncObj ){
                code.run();
                return;
            }
        }
        code.run();
    }

    protected final Function<A, Iterable<? extends A>> follow;

    protected final Function<List<TreeStep<A>>, TreeStep<A>> poll;

    /**
     * Создает функцию для извлечения первого узла из очереди
     * @param <A> Тип узла дерева
     * @return функция извлечения
     */
    public static <A> Function<List<TreeStep<A>>, TreeStep<A>> pollFirst() {
        return (ws)->{
            if( ws.isEmpty() ) return null;
            return ws.remove(0);
        };
    }

    /**
     * Создает функцию для извлечения последнего узла из очереди
     * @param <A> Тип узла дерева
     * @return функция извлечения
     */
    public static <A> Function<List<TreeStep<A>>, TreeStep<A>> pollLast() {
        return (ws)->{
            if( ws.isEmpty() ) return null;
            return ws.remove(ws.size()-1);
        };
    }

    /**
     * Данные для вставки узла в список рабочих узлов
     * @param <A> Тип узла дерева
     */
    public static class PushStep<A> {
        /**
         * Конструктор - данные для вставки
         * @param workset очередь
         * @param node добавляемый узел
         * @param nodeIndex индекс узла согласно очередной порции полученных данных
         */
        public PushStep(List<TreeStep<A>> workset, TreeStep<A> node, int nodeIndex) {
            this.workset = workset;
            this.node = node;
            this.nodeIndex = nodeIndex;
        }

        /**
         * Конструктор - данные для вставки
         * @param workset очередь
         * @param node добавляемый узел
         */
        public PushStep(List<TreeStep<A>> workset, TreeStep<A> node) {
            this(workset,node,0);
        }

        /**
         * Конструктор по умолчанию
         */
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

    /**
     * Создает функцию для добвления узлов в конец очереди обработки
     * @param <A> Тип узла дерева
     * @return функция вставки
     */
    public static <A> Consumer<PushStep<A>> pushLast() {
        return (ps)->{
            ps.getWorkset().add(ps.getNode());
        };
    }

    /**
     * Создает функцию для добвления узлов в начало очереди обработки
     * @param <A> Тип узла дерева
     * @return функция вставки
     */
    public static <A> Consumer<PushStep<A>> pushFirst() {
        return (ps)->{
            ps.getWorkset().add(0, ps.getNode());
        };
    }

    /**
     * Создает фуркцию для добавления узлов в очередь обратки, согласно порядку следования
     * @param <A> Тип узла дерева
     * @return функция вставки
     */
    public static <A> Consumer<PushStep<A>> pushOrdered() {
        return (ps)->{
            ps.getWorkset().add(ps.getNodeIndex(), ps.getNode());
        };
    }

    protected final Predicate<TreeStep<A>> allow;

    /**
     * Создает фильтр, который не допускает наличие циклов при проходе по дереву
     * @param <A> Тип узла дерева
     * @return фильтр
     */
    public static <A> Predicate<TreeStep<A>> checkCycles() {
        return (ts)->{
            return !ts.hasCycles();
        };
    }

    @Override
    public TreeStep<A> next() {
        return syncWorkset( () -> {
            if( getWorkset().isEmpty() ) return null;
            TreeStep<A> ts = poll.apply(getWorkset());

            Iterable<? extends A> nextset = follow.apply(ts.getNode());
            if( nextset!=null ){
                PushStep<A> push = new PushStep<>();
                push.setWorkset(getWorkset());
                int idx = -1;
                for( A a : nextset ){
                    TreeStep<A> nexts = ts.follow(a);
                    if( nexts!=null && allow.test(nexts) ){
                        idx++;
                        push.setNode(nexts);
                        push.setNodeIndex(idx);
                        this.push.accept(push);
                    }
                }
            }

            return ts;
        });
    }
}
