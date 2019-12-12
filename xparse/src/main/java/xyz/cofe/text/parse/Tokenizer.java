package xyz.cofe.text.parse;

import xyz.cofe.iter.Eterable;

import java.util.function.Function;

/**
 * Лексический анализатор
 */
public class Tokenizer implements Eterable<Token> {
    @SafeVarargs
    public Tokenizer(String source, int offset, Function<CharPointer,? extends Token>... parsers){
        if( source==null ) throw new IllegalArgumentException("source==null");
        if( offset<0 ) throw new IllegalArgumentException("offset<0");
        if( parsers==null ) throw new IllegalArgumentException("parsers==null");

        pointer = new BasicCharPointer(source,0);
        this.parsers = parsers;
    }

    @SuppressWarnings("unchecked")
    public Tokenizer( String source, int offset, Iterable<Function<CharPointer,? extends Token>> parsers){
        if( source==null ) throw new IllegalArgumentException("source==null");
        if( offset<0 ) throw new IllegalArgumentException("offset<0");
        if( parsers==null ) throw new IllegalArgumentException("parsers==null");

        pointer = new BasicCharPointer(source,0);
        this.parsers = Eterable.of(parsers).toList().toArray(new Function[]{});
    }


    private CharPointer pointer;
    public CharPointer getPointer(){
        return pointer;
    }

    private Function<CharPointer, ? extends Token>[] parsers;
    public Function<CharPointer, ? extends Token>[] getParsers(){
        return parsers;
    }

    @Override
    public TokenIterator iterator(){
        return new TokenIterator(pointer, parsers);
    }

    @SafeVarargs
    public static Token parse( String source, int offset, Function<? super CharPointer, ? extends Token>... parsers ){
        if( source==null ) throw new IllegalArgumentException("source==null");
        if( offset<0 ) throw new IllegalArgumentException("offset<0");
        if( parsers==null ) throw new IllegalArgumentException("parsers==null");
        if( parsers.length<1 ) return null;

        CharPointer tp = new BasicCharPointer(source, offset);
        for( Function<? super CharPointer, ? extends Token> parser : parsers ){
            if( parser==null ) continue;
            Token t = parser.apply(tp);
            if( t!=null ){
                return t;
            }
        }
        return null;
    }

    @SafeVarargs
    public static Token parse( String source, Function<? super CharPointer, ? extends Token>... parsers ){
        if( source==null ) throw new IllegalArgumentException("source==null");
        if( parsers==null ) throw new IllegalArgumentException("parsers==null");
        if( parsers.length<1 ) return null;

        return parse(source, 0, parsers);
    }
}
