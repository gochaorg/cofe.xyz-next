package xyz.cofe.text.tparse;

import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.Optional;

public class LPointer<T> implements Pointer<T,Integer,LPointer<T>> {
    public LPointer(List<T> tokens, int pos){
        if( tokens==null )throw new IllegalArgumentException("tokens==null");
        this.position = pos;
        this.tokens = tokens;
    }

    public LPointer(List<T> tokens){
        if( tokens==null )throw new IllegalArgumentException("tokens==null");
        this.position = 0;
        this.tokens = tokens;
    }

    protected LPointer(LPointer<T> sample){
        if( sample==null )throw new IllegalArgumentException("sample == null");
        this.position = sample.position;
        this.tokens = sample.tokens;
    }

    public LPointer<T> clone(){
        return new LPointer<>(this);
    }

    private final List<T> tokens;

    public List<T> tokens(){ return tokens; }

    @Override
    public boolean eof() {
        if( position<0 )return true;
        if( position>=tokens.size() )return true;
        return false;
    }

    private int position;

    @Override
    public Integer position() {
        return position;
    }

    @Override
    public LPointer<T> move(Integer off) {
        if( off==null )throw new IllegalArgumentException("off==null");
        LPointer<T> c = clone();
        c.position = c.position + off;
        return c;
    }

    @Override
    public Optional<T> lookup(Integer off) {
        if( off==null )throw new IllegalArgumentException("off==null");
        int t = position + off;
        if( t<0 || t>=tokens.size() )return Optional.empty();
        return Optional.of( tokens.get(t) );
    }

    @Override
    public int compareTo(LPointer<T> o) {
        if( o==null )return 0;
        if( o==this )return 0;
        if( o.tokens!=tokens )return 0;
        return Integer.compare(position, o.position);
    }

    @Override
    public String toString() {
        return LPointer.class.getSimpleName() + " " + position();
    }
}
