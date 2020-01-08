package xyz.cofe.text.parse.mtest;

import xyz.cofe.text.parse.CharPointer;
import xyz.cofe.text.parse.Token;
import xyz.cofe.text.parse.TokenPointer;

public class SumTok extends Token {
    public SumTok( CharPointer begin, CharPointer end ){
        super(begin, end);
    }
    public SumTok( Token sample ){
        super(sample);
    }

    public static Expr unaryMinus( TokenPointer p ){
        return p.lookup( t -> "-".equals(t.getText()) ? new TokRef(p) : null );
    }
}
