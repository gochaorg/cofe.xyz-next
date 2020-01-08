package xyz.cofe.text.parse.mtest;

import xyz.cofe.text.parse.CharPointer;
import xyz.cofe.text.parse.Token;
import xyz.cofe.text.parse.TokenPointer;

import java.util.function.Function;

public class BrOpenTok extends Token {
    public BrOpenTok( CharPointer begin, CharPointer end ){
        super(begin, end);
    }
    public BrOpenTok( Token sample ){
        super(sample);
    }
    public static Expr ref( TokenPointer p ){
        return p.lookup( t -> t instanceof BrOpenTok ? new TokRef(p) : null );
    }
}
