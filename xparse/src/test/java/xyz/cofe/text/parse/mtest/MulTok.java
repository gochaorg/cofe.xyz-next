package xyz.cofe.text.parse.mtest;

import xyz.cofe.text.parse.CharPointer;
import xyz.cofe.text.parse.Token;

public class MulTok extends Token {
    public MulTok( CharPointer begin, CharPointer end ){
        super(begin, end);
    }
    public MulTok( Token sample ){
        super(sample);
    }
}