package xyz.cofe.text.parse.toks;

import xyz.cofe.text.parse.CharPointer;
import xyz.cofe.text.parse.Token;

import java.util.function.Function;

import static xyz.cofe.text.parse.CharType.DIGIT_DOT;
import static xyz.cofe.text.parse.CharType.Digit;
import static xyz.cofe.text.parse.TokBuilder.*;

public class IntegerNumberToken extends Token {
    public IntegerNumberToken( CharPointer begin, CharPointer end ){
        super(begin, end);
    }
    public IntegerNumberToken( Token sample ){
        super(sample);
    }

    private long value;
    public long getValue(){
        return value;
    }
    public void setValue( long value ){
        this.value = value;
    }
    public IntegerNumberToken value( long value ){
        this.value = value;
        return this;
    }

    @Override
    public String toString(){
        return "<"+IntegerNumberToken.class.getSimpleName()+" text='"+getText()+"' value="+getValue()+">";
    }

    public final static Function<CharPointer,Token> parser =
        repeat(Digit).min(1).build(
            (b,e,lst)->new IntegerNumberToken(b,e).value(
                lst.map(t -> (long)"0123456789".indexOf(t.getText())).reverse().indexes().reduce(0L, (summ, digit) ->
                    summ + digit.getValue() * (long)(Math.pow( 10, digit.getIndex() ))
                )
            ));
}
