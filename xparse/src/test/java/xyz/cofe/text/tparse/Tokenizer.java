package xyz.cofe.text.tparse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Tokenizer<P extends Pointer<?,?,P>, T extends Tok<P>> implements Iterable<T> {
    private final List<GR<P,T>> rules;
    private final P pointer;

    public Tokenizer(P initial, Iterable<GR<P,T>> rules ){
        if( rules==null )throw new IllegalArgumentException("rules==null");
        if( initial==null )throw new IllegalArgumentException("initial==null");
        List<GR<P,T>> arules = new ArrayList<>();
        for( GR<P,T> r : rules ){
            if( r==null )throw new IllegalArgumentException("rules contains empty element");
            arules.add(r);
        }
        this.rules = arules;
        this.pointer = initial;
    }

    public Tokenizer(P initial, GR<P,T> ... rules ){
        if( rules==null )throw new IllegalArgumentException("rules==null");
        if( initial==null )throw new IllegalArgumentException("initial==null");
        List<GR<P,T>> arules = new ArrayList<>();
        for( GR<P,T> r : rules ){
            if( r==null )throw new IllegalArgumentException("rules contains empty element");
            arules.add(r);
        }
        this.rules = arules;
        this.pointer = initial;
    }

    @Override
    public TokensIterator<P,T> iterator() {
        return new TokensIterator<P, T>(pointer, rules);
    }

    public static Tokenizer<CharPointer, CToken> lexer(String source, Iterable<GR<CharPointer,CToken>> rules ){
        if( source==null )throw new IllegalArgumentException("source == null");
        if( rules==null )throw new IllegalArgumentException("rules == null");

        CharPointer cptr = new CharPointer(source);
        return new Tokenizer<CharPointer, CToken>(cptr, rules);
    }

    public static Tokenizer<CharPointer, CToken> lexer( String source, GR<CharPointer,CToken> ... rules ){
        if( source==null )throw new IllegalArgumentException("source == null");
        if( rules==null )throw new IllegalArgumentException("rules == null");

        CharPointer cptr = new CharPointer(source);
        return new Tokenizer<CharPointer, CToken>(cptr, rules);
    }
}
