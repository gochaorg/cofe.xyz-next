package xyz.cofe.text.parse.toks;

import xyz.cofe.text.parse.CharPointer;
import xyz.cofe.text.parse.Token;

public class NumberStart extends Token {
    public NumberStart( CharPointer begin, CharPointer end ){
        super(begin, end);
    }

    public NumberStart( Token sample ){
        super(sample);
    }

    private long value;

    public long getValue(){
        return value;
    }

    public void setValue( long value ){
        this.value = value;
    }

    public NumberStart value( long value ){
        this.value = value;
        return this;
    }

    @Override
    public String toString(){
        return "<"+NumberStart.class.getSimpleName()+" text='"+getText()+"' value="+getValue()+">";
    }
}