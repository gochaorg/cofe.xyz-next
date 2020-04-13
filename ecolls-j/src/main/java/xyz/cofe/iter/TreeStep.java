package xyz.cofe.iter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Один шаг при обходе дерева
 * @param <A> дерево
 */
public class TreeStep<A> {
    /**
     * Ссылка на узел
     */
    protected final A node;

    /**
     * Ссылка на родительский узел
     */
    protected final TreeStep<A> parent;

    /**
     * Конструктор
     * @param node узел
     */
    public TreeStep(A node){
        this.node = node;
        this.parent = null;
    }

    /**
     * Конструктор
     * @param node узел
     * @param parent родительский узел
     */
    public TreeStep(A node,TreeStep<A> parent){
        this.node = node;
        this.parent = parent;
    }

    /**
     * Получение текущего узла
     * @return текущий узел
     */
    public A getNode() {
        return node;
    }

    /**
     * Получение родительского узла
     * @return родительский "узел"
     */
    public TreeStep<A> getParent() {
        return parent;
    }

    /**
     * Создание шага для дочернего узла
     * @param a дочерний узел
     * @return шаг
     */
    public TreeStep<A> follow(A a){
        return new TreeStep<>(a,this);
    }

    /**
     * Возвращает уровень вложенности для узла
     * @return 0 - корень
     */
    public int getLevel(){
        TreeStep<A> ts = this;
        int level = 0;
        while( ts.parent!=null ){
            level++;
            ts = ts.parent;
        }
        return level;
    }

    /**
     * Получение пути ввиде массива узлов
     * @param nodeClass Тип жлементов массива
     * @return массив узлов
     */
    public A[] nodePath(Class<A> nodeClass){
        if( nodeClass == null )throw new IllegalArgumentException( "nodeClass == null" );
        A[] arr = (A[])Array.newInstance(nodeClass,getLevel()+1);
        int idx = arr.length-1;
        TreeStep<A> ts = this;
        while( true ){
            arr[idx] = ts.node;
            ts = ts.parent;
            idx--;
            if(ts==null)break;
        }
        return arr;
    }

    /**
     * Получение пути ввиде списка
     * @return список
     */
    public List<A> nodeList(){
        ArrayList<A> lst = new ArrayList<>();
        TreeStep<A> ts = this;
        while( true ){
            lst.add( 0, ts.node );
            ts = ts.parent;
            if(ts==null)break;
        }
        return lst;
    }

    /**
     * Получение пути ввиде итератора
     * @return путь
     */
    public Eterable<A> nodes(){
        return Eterable.of(nodeList());
    }

    /**
     * Обход узлов пути от дочернего к корню
     * @param visitor посититель
     */
    public void each(Consumer<A> visitor){
        if( visitor == null )throw new IllegalArgumentException( "visitor == null" );
        TreeStep<A> ts = this;
        while( true ){
            visitor.accept(ts.getNode());
            ts = ts.getParent();
            if(ts==null)break;
        }
    }

    /**
     * Получение частотности узлов в пути
     * @return частотность узлов (узел / количесто сслок)
     */
    public Map<A,Integer> frequency(){
        Map<A,Integer> f = new LinkedHashMap<>();
        each( x->{
            f.put(x,f.getOrDefault(x,0)+1);
        });
        return f;
    }

    /**
     * Проверка на наличие циклов в пути
     * @return true - циклы есть
     */
    public boolean hasCycles(){
        return frequency().values().stream().filter(x->x>1).count()>0;
    }
}
