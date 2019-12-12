package xyz.cofe.text.parse.mtest;

import java.util.function.Function;

public class ProxyFn<A,R> implements Function<A,R> {
    private volatile Function<A,R> target;

    public ProxyFn( Function<A,R> target){
        //if( target==null ) throw new IllegalArgumentException("target==null");
        this.target = target;
    }

    @Override
    public R apply( A a ){
        var target = this.target;
        if( target!=null ) return target.apply(a);
        return null;
    }

    public Function<A, R> getTarget(){
        return target;
    }

    public void setTarget( Function<A, R> target ){
        this.target = target;
    }
}
