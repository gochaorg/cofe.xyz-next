package xyz.cofe.text.parse.mtest;

import xyz.cofe.text.parse.CharPointer;
import xyz.cofe.text.parse.Token;

public class SumTok extends Token {
    public SumTok( CharPointer begin, CharPointer end ){
        super(begin, end);
    }
    public SumTok( Token sample ){
        super(sample);
    }
}
