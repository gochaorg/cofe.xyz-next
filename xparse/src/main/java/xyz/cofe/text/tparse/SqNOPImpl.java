package xyz.cofe.text.tparse;

import java.util.*;

public class SqNOPImpl<P extends Pointer<?,?,P>> {
    @SafeVarargs
    public SqNOPImpl(GR<P,? extends Tok<P>> ... expressions ){
        if( expressions==null )throw new IllegalArgumentException("expressions==null");
        for( int i=0; i<expressions.length; i++ ){
            if( expressions[i]==null )throw new IllegalArgumentException("expression["+i+"]==null");
        }
        this.expressions = Arrays.asList(expressions);
    }

    final List<GR<P,? extends Tok<P>>> expressions;

    public Optional<List<? extends Tok<P>>> match( P ptr ){
        if( ptr==null )throw new IllegalArgumentException("ptr == null");
        ArrayList<Tok<P>> matched = new ArrayList<>();

        for (GR<P, ? extends Tok<P>> expression : expressions) {
            if (ptr.eof()) break;

            GR<P, ? extends Tok<P>> exp = expression;
            if (exp == null) throw new ImplementError("null expr match");

            Optional<? extends Tok<P>> tok = exp.apply(ptr);
            //noinspection OptionalAssignedToNull
            if (tok == null) throw new MapResultError("return null");
            if (!tok.isPresent()) break;

            //noinspection ConstantConditions
            if (tok.get() == null) throw new MapResultError("return null");

            P next = tok.get().end();
            if (next == null) throw new MapResultError("return null");
            if (ptr.compareTo(next) >= 0) {
                throw new ImplementError("pointer order");
            }

            matched.add(tok.get());
            ptr = next;
        }

        if( matched.size()==expressions.size() ){
            return Optional.of(matched);
        }

        return Optional.empty();
    }

    private String name;

    public SqNOPImpl<P> name( String name ){
        this.name = name;
        return this;
    }

    public String name(){ return name; }

    @Override
    public String toString(){
        if( name!=null )return name;
        return super.toString();
    }
}
