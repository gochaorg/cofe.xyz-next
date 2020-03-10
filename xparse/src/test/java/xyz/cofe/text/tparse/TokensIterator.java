package xyz.cofe.text.tparse;

import xyz.cofe.fn.Tuple2;
import xyz.cofe.iter.Eterable;

import java.util.*;

public class TokensIterator<P extends Pointer<?,?,P>, T extends Tok<P>> implements Iterator<T> {
    private final List<GR<P,T>> rules;
    private P pointer;

    public TokensIterator(P initial, Iterable<GR<P,T>> rules ){
        if( rules==null )throw new IllegalArgumentException("rules==null");
        if( initial==null )throw new IllegalArgumentException("initial==null");
        List<GR<P,T>> arules = new ArrayList<>();
        for( GR<P,T> r : rules ){
            if( r==null )throw new IllegalArgumentException("rules contains empty element");
            arules.add(r);
        }
        this.rules = Collections.unmodifiableList(arules);

        this.pointer = initial;
        initialFetch(pointer);
    }

    public TokensIterator(P initial, GR<P,T> ... rules ){
        if( rules==null )throw new IllegalArgumentException("rules==null");
        if( initial==null )throw new IllegalArgumentException("initial==null");
        List<GR<P,T>> arules = new ArrayList<>();
        for( GR<P,T> r : rules ){
            if( r==null )throw new IllegalArgumentException("rules contains empty element");
            arules.add(r);
        }
        this.rules = Collections.unmodifiableList(arules);

        this.pointer = initial;
        initialFetch(pointer);
    }

    private TokensIterator(P initial, List<GR<P,T>> rules ){
        if( rules==null )throw new IllegalArgumentException("rules==null");
        if( initial==null )throw new IllegalArgumentException("initial==null");
        this.rules = rules;
        this.pointer = initial;
        initialFetch(initial);
    }

    public P pointer(){ return pointer; }

    private void initialFetch(P pointer) {
        fetched = fetch(pointer);
        if( fetched!=null ){
            pointer = fetched.end();
        }
    }

    private T fetched;

    private T fetch(P pointer){
        if( pointer==null )return null;
        if( pointer.eof() )return null;
        for( GR<P,T> r :rules ){
            Optional<T> ot = r.apply(pointer);
            if( ot==null )throw new IllegalStateException("bug");
            if( !ot.isPresent() )continue;
            if( ot.get()==null )throw new IllegalStateException("bug");
            T t = ot.get();
            return t;
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return fetched!=null;
    }

    @Override
    public T next() {
        T r = fetched;
        fetched = fetch(pointer);
        if( fetched!=null ){
            pointer = fetched.end();
        }
        return r;
    }
}
