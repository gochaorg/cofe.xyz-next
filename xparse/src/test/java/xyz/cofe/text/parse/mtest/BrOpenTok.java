package xyz.cofe.text.parse.mtest;

import xyz.cofe.text.parse.CharPointer;
import xyz.cofe.text.parse.Token;

public class BrOpenTok extends Token {
    public BrOpenTok( CharPointer begin, CharPointer end ){
        super(begin, end);
    }
    public BrOpenTok( Token sample ){
        super(sample);
    }
}
