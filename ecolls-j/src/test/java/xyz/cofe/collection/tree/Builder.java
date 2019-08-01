package xyz.cofe.collection.tree;

import xyz.cofe.collection.UpTree;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Builder<T extends UpTree<T>,V> {
    protected Function<V,T> nodeBuilder;
    protected BiConsumer<T,V> valueAssigner;
    public Builder(Function<V,T> nodeBuilder, BiConsumer<T,V> valueAssigner){
        this.nodeBuilder = nodeBuilder;
        this.valueAssigner = valueAssigner;
    }

    protected Queue<Consumer<T>> builders = new ArrayDeque<>();
    public Builder<T,V> val(V str){
        //builders.add(n -> n.setValue(str));
        builders.add(n -> valueAssigner.accept(n,str));
        return this;
    }
    public Builder<T,V> add(V str){
        builders.add(n -> {
            T c = nodeBuilder.apply(str);
            n.append(c);
        });
        return this;
    }
    public Builder<T,V> add(V str, Consumer<Builder<T,V>> child){
        builders.add(n -> {
            T c = nodeBuilder.apply(str);
            n.append(c);
            if( child!=null ){
                Builder<T,V> b = new Builder<T,V>(nodeBuilder, valueAssigner);
                child.accept(b);
                b.apply(c);
            }
        });
        return this;
    }
    public T apply(T t){
        if( t == null )throw new IllegalArgumentException( "t == null" );
        builders.forEach(x->x.accept(t));
        return t;
    }
    public T build(){
        T t = nodeBuilder.apply(null);
        builders.forEach(x->x.accept(t));
        return t;
    }
}
