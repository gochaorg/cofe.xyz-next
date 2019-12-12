package xyz.cofe.text.parse;

import java.util.Objects;

/**
 * Базовая реализация {@link CharPointer}
 */
public class BasicCharPointer implements CharPointer {
    private final String source;
    private final int pointer;

    public BasicCharPointer( String source, int ptr ){
        if( source == null ) throw new IllegalArgumentException("source==null");
        this.source = source;
        this.pointer = ptr;
    }

    public BasicCharPointer( BasicCharPointer sample ){
        if( sample == null ) throw new IllegalArgumentException("sample==null");
        String src = sample.source;
        this.source = src == null ? "" : src;
        this.pointer = sample.pointer();
    }


    @Override
    public Integer pointer(){
        return pointer;
    }

    @Override
    public boolean eof(){
        return pointer >= source.length() || pointer < 0;
    }

    @Override
    public String text( int length ){
        if( eof() ) return "";
        if( length<=0 )return "";
        int t = pointer + length;
        if( t <= pointer ) return "";
        if( t <= source.length() ) return source.substring(pointer, t);
        return source.substring(pointer);
    }

    @Override
    public Character lookup(){
        if( eof() ) return 0;
        return source.charAt(pointer);
    }

    @Override
    public BasicCharPointer move( int offset ){
        return new BasicCharPointer(source, pointer + offset);
    }

    @Override
    public boolean equals( Object o ){
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        BasicCharPointer that = (BasicCharPointer) o;
        return pointer == that.pointer &&
            source.equals(that.source);
    }

    @Override
    public int hashCode(){
        return Objects.hash(source, pointer);
    }

    @Override
    public String toString(){
        return "<Ptr pointer="+pointer+'>';
    }

    @Override
    public int compareTo( Pointer o ){
        if( o==null )return 0;
        if( o.getClass()!=BasicCharPointer.class )return 0;
        BasicCharPointer p = (BasicCharPointer)o;
        if( p.source!=this.source )return 0;
        return this.pointer < p.pointer ? -1 : (this.pointer==p.pointer ? 0 : 1);
    }
}
