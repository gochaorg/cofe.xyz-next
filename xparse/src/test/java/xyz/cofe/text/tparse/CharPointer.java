package xyz.cofe.text.tparse;

import java.util.MissingFormatArgumentException;
import java.util.Optional;

public class CharPointer implements Pointer<Character,Integer,CharPointer> {
    public CharPointer(String source, int offset){
        this.source = source;
        this.offset = offset;
    }

    public CharPointer(String source){
        this.source = source;
        this.offset = 0;
    }

    private final String source;
    public String source(){ return source; }

    @Override public boolean eof() {
        if( source==null )return true;
        if( offset<0 )return true;
        return offset >= source.length();
    }

    private final int offset;
    @Override public Integer position() { return offset; }

    @Override
    public CharPointer move(Integer offset) {
        if( offset==null )throw new IllegalArgumentException("offset == null");
        return new CharPointer(source, this.offset + offset);
    }

    @Override
    public Optional<Character> lookup(Integer offset) {
        if( offset==null )throw new IllegalArgumentException("offset == null");
        if( eof() )return Optional.empty();
        int t = (position()+offset);
        if( t<0 )return Optional.empty();
        if( t>=source.length() )return Optional.empty();
        return Optional.of( source.charAt(t) );
    }

    @Override
    public int compareTo(CharPointer o) {
        if( o==null )return 0;
        // if( o.getClass()!=CharPointer.class )return 0;
        // noinspection StringEquality
        if( source!=o.source ) {
            if (!o.source.equals(this.source)) return 0;
        }
        int x = o.position();
        return Integer.compare(offset, x);
    }

    @Override
    public String toString() {
        return "CharPointer "+position();
    }
}
