package xyz.cofe.text.parse.toks;

import xyz.cofe.text.parse.CharPointer;
import xyz.cofe.text.parse.Token;

public class NumberPart extends Token {
    public NumberPart( CharPointer begin, CharPointer end ){
        super(begin, end);
    }

    public NumberPart( Token sample ){
        super(sample);
    }

    private double value;

    public double getValue(){
        return value;
    }

    public void setValue( double value ){
        this.value = value;
    }

    public NumberPart value( double value ){
        this.value = value;
        return this;
    }

    @Override
    public String toString(){
        return "<"+NumberPart.class.getSimpleName()+" text='"+getText()+"' value="+getValue()+">";
    }
}
