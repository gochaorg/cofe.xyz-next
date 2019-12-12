package xyz.cofe.text.parse.mtest;

import xyz.cofe.text.parse.CharPointer;
import xyz.cofe.text.parse.Token;

public class BrCloseTok extends Token {
    public BrCloseTok( CharPointer begin, CharPointer end ){
        super(begin, end);
    }
    public BrCloseTok( Token sample ){
        super(sample);
    }
}
