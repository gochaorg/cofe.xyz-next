package xyz.cofe.text.tparse;

import java.util.List;
import java.util.Optional;

public class CToken implements Tok<CharPointer> {
    private final CharPointer begin;
    private final CharPointer end;

    public CToken(CharPointer begin, CharPointer end){
        if( begin==null )throw new IllegalArgumentException("begin == null");
        if( end==null )throw new IllegalArgumentException("end == null");
        if( begin.compareTo(end)>0 )throw new IllegalArgumentException("begin > end");
        this.begin = begin;
        this.end = end;
    }

    public CToken(CToken begin, CToken end){
        if( begin==null )throw new IllegalArgumentException("begin == null");
        if( end==null )throw new IllegalArgumentException("end == null");

        this.begin = begin.begin();
        this.end = end.end();

        if( this.begin.compareTo(this.end)>0 )throw new IllegalArgumentException("begin > end");
    }

    public CToken(List<CToken> tokens){
        if( tokens==null )throw new IllegalArgumentException("tokens == null");
        if( tokens.isEmpty() )throw new IllegalArgumentException("tokens is empty");
        CharPointer b = null;
        CharPointer e = null;
        for( CToken t : tokens ){
            if( t==null )throw new IllegalArgumentException("tokens contains null element");
            if( b==null || e==null ){
                b = t.begin();
                e = t.end();
                continue;
            }
            if( b.compareTo(t.begin())>0 ) b = t.begin();
            if( b.compareTo(t.end())>0 ) b = t.end();
            if( e.compareTo(t.begin())<0 ) e = t.begin();
            if( e.compareTo(t.end())<0 ) e = t.end();
        }
        this.begin = b;
        this.end = e;
        if( this.begin.compareTo(this.end)==0 )throw new IllegalStateException("bug!");
    }

    @Override
    public CharPointer begin() {
        return begin;
    }

    @Override
    public CharPointer end() {
        return end;
    }

    public String text(){
        int len = end.position() - begin.position();
        if( len>0 ){
            StringBuilder sb = new StringBuilder();
            for( int i=0; i<len; i++ ){
                Optional<Character> c = begin.lookup(i);
                if( c==null || !c.isPresent() )throw new IllegalStateException("bug");
                sb.append( c.get() );
            }
            return sb.toString();
        }else{
            return "";
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()+" \"");
        String str = text();
        for( int i=0;i<str.length(); i++ ){
            char c = str.charAt(i);
            int ci = (int)c;
            if( c=='\n' ) {
                sb.append("\\n");
            }else if( c=='\r' ){
                sb.append("\\n");
            }else if( c=='\t' ){
                sb.append("\\t");
            }else if( c=='"' ){
                sb.append("\\\"");
            }else if( ci<32 ){
                sb.append("\\h");
                String h = Integer.toString(ci,16);
                if( h.length()==1 ) sb.append("0");
                sb.append(h);
            }else{
                sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
