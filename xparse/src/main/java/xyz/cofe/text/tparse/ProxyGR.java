package xyz.cofe.text.tparse;

import java.util.Optional;
import java.util.function.Consumer;

public class ProxyGR<P extends Pointer<?,?,P>, T extends Tok<P>> implements GR<P,T> {
    public ProxyGR( GR<P,T> initial ){
        if( initial==null )throw new IllegalArgumentException( "initial==null" );
        this.target = initial;
    }

    public ProxyGR<P,T> conf(Consumer<ProxyGR<P,T>> conf){
        if( conf==null )throw new IllegalArgumentException( "conf==null" );
        conf.accept(this);
        return this;
    }

    private GR<P,? extends T> target;
    public GR<P,? extends T> getTarget(){ return target; }
    public void setTarget(GR<P,? extends T> newTarget){
        if( newTarget==null )throw new IllegalArgumentException( "newTarget==null" );
        this.target = newTarget;
    }

    @Override
    public Optional<T> apply(P ptr) {
        if( ptr==null )throw new IllegalArgumentException( "ptr==null" );
        Optional<? extends T> res = target.apply(ptr);
        //noinspection OptionalIsPresent
        return res.isPresent() ? Optional.of(res.get()) : Optional.empty();
    }
}
