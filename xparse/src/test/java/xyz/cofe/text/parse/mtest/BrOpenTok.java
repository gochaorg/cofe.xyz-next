package xyz.cofe.text.parse.mtest;

import xyz.cofe.text.parse.CharPointer;
import xyz.cofe.text.parse.Token;

public class BracketTok extends Token {
    public BracketTok( CharPointer begin, CharPointer end ){
        super(begin, end);
    }
    public BracketTok( Token sample ){
        super(sample);
    }
}
