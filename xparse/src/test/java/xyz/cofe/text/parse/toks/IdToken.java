package xyz.cofe.text.parse.toks;

import xyz.cofe.text.parse.CharPointer;
import xyz.cofe.text.parse.Token;

import java.util.function.Function;

public class IdToken extends Token {
    public IdToken( CharPointer begin, CharPointer end ){
        super(begin, end);
    }

    public IdToken( Token sample ){
        super(sample);
    }

    public static final Function<CharPointer, Token> parse = tp -> {
        if( tp==null )return null;
        if( tp.eof() )return null;

        char c1 = tp.lookup();
        if( !Character.isLetter(c1) ) return null;

        CharPointer begin = tp;
        tp = tp.move(1);
        while( !tp.eof() ){
            char c2 = tp.lookup();
            if( Character.isLetter(c2) || Character.isDigit(c2) ){
                tp = tp.move(1);
                continue;
            }
            break;
        }

        return new IdToken(begin, tp);
    };
}
