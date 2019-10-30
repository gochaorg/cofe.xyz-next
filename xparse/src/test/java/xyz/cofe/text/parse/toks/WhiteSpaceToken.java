package xyz.cofe.text.parse.toks;

import java.util.function.Function;

public class WhiteSpaceToken extends Token {
    public WhiteSpaceToken( CharPointer begin, CharPointer end ){
        super(begin, end);
    }

    public WhiteSpaceToken( Token sample ){
        super(sample);
    }

    public static final Function<CharPointer,Token> parse = tp -> {
        if( tp==null )return null;
        if( tp.eof() )return null;

        char c1 = tp.lookup();
        if( !Character.isWhitespace(c1) ) return null;

        CharPointer begin = tp;
        tp = tp.move(1);
        while( !tp.eof() ){
            char c2 = tp.lookup();
            if( Character.isWhitespace(c2) ){
                tp = tp.move(1);
                continue;
            }
            break;
        }

        return new WhiteSpaceToken(begin, tp);

    };
}
