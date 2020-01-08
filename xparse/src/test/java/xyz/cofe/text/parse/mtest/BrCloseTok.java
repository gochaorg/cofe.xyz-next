package xyz.cofe.text.parse.mtest;

import xyz.cofe.text.parse.CharPointer;
import xyz.cofe.text.parse.Token;
import xyz.cofe.text.parse.TokenPointer;

import java.util.function.Function;

public class BrCloseTok extends Token {
    public BrCloseTok( CharPointer begin, CharPointer end ){
        super(begin, end);
    }
    public BrCloseTok( Token sample ){
        super(sample);
    }
    public static Expr ref( TokenPointer p ){
        if( p==null ) throw new IllegalArgumentException("p==null");
        return p.lookup( t -> t instanceof BrCloseTok ? new TokRef(p) : null );
    }
}
