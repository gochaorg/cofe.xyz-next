package xyz.cofe.text.parse;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Итератор по списку лексем
 */
public class TokenIterator implements Iterator<Token> {
    public TokenIterator( CharPointer pointer, Function<CharPointer, ? extends Token>[] parsers){
        if( parsers==null ) throw new IllegalArgumentException("parsers==null");
        this.parsers = parsers;

        current = fetch(pointer);
    }

    protected Token fetch( CharPointer pointer){
        if( pointer==null )return null;

        var parsers = getParsers();
        if( parsers==null )return null;

        for( var parser : parsers ){
            if( parser==null ) continue;
            var t = parser.apply(pointer);
            if( t!=null ){
                return t;
            }
        }

        return null;
    }

    private final Function<CharPointer, ? extends Token>[] parsers;
    public Function<CharPointer, ? extends Token>[] getParsers(){
        return parsers;
    }

    protected Token current;

    @Override
    public boolean hasNext(){
        return current!=null;
    }

    @Override
    public Token next(){
        Token result = current;
        if( result==null )return null;

        CharPointer cptr = result.getEnd();
        if( cptr != null ){
            Token next = fetch(cptr);
            if( next!=null ){
                CharPointer nptr = next.getEnd();
                if( nptr==null )throw new IllegalStateException("next text pointer not available");
                if( cptr.compareTo(nptr)>=0 )throw new IllegalStateException("next text pointer ref. to same (or less) text position");
            }

            current = next;
        }else {
            current = null;
        }

        return result;
    }
}
