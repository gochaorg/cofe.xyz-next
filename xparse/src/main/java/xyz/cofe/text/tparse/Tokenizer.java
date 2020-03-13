package xyz.cofe.text.tparse;

import xyz.cofe.iter.Eterable;

import java.util.*;

public class Tokenizer<P extends Pointer<?,?,P>, T extends Tok<P>> implements Eterable<T> {
    private final List<GR<P,? super T>> rules;
    private final P pointer;

    public Tokenizer(P initial, Iterable<GR<P,T>> rules ){
        if( rules==null )throw new IllegalArgumentException("rules==null");
        if( initial==null )throw new IllegalArgumentException("initial==null");
        List<GR<P,? super T>> arules = new ArrayList<>();
        for( GR<P,? super T> r : rules ){
            if( r==null )throw new IllegalArgumentException("rules contains empty element");
            arules.add(r);
        }
        this.rules = arules;
        this.pointer = initial;
    }

    @Override
    public TokensIterator<P,T> iterator() {
        return new TokensIterator(pointer, rules);
    }

    public static Tokenizer<CharPointer, CToken> lexer(String source, Iterable<GR<CharPointer,? extends CToken>> rules ){
        if( source==null )throw new IllegalArgumentException("source == null");
        if( rules==null )throw new IllegalArgumentException("rules == null");

        CharPointer cptr = new CharPointer(source);
        return new Tokenizer(cptr, rules);
    }

    public static Tokenizer<CharPointer, ? extends CToken> lexer( String source, GR<CharPointer,? extends CToken> ... rules ){
        if( source==null )throw new IllegalArgumentException("source == null");
        if( rules==null )throw new IllegalArgumentException("rules == null");

        CharPointer cptr = new CharPointer(source);
        return new Tokenizer(cptr,  Arrays.asList(rules));
    }
}
