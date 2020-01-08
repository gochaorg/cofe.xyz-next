package xyz.cofe.text.parse.toks;

import xyz.cofe.text.parse.CharPointer;
import xyz.cofe.text.parse.Token;
import xyz.cofe.text.parse.TokenPointer;
import xyz.cofe.text.parse.mtest.BrOpenTok;
import xyz.cofe.text.parse.mtest.Expr;
import xyz.cofe.text.parse.mtest.TokRef;

import java.util.function.Function;

import static xyz.cofe.text.parse.TokBuilder.*;
import static xyz.cofe.text.parse.CharType.*;

public class FloatNumberToken extends Token {
    public FloatNumberToken( CharPointer begin, CharPointer end ){
        super(begin, end);
    }
    public FloatNumberToken( Token sample ){
        super(sample);
    }

    private double value;
    public double getValue(){
        return value;
    }
    public void setValue( double value ){
        this.value = value;
    }
    public FloatNumberToken value( double value ){
        this.value = value;
        return this;
    }

    @Override
    public String toString(){
        return "<"+FloatNumberToken.class.getSimpleName()+" text='"+getText()+"' value="+getValue()+">";
    }

    public final static Function<CharPointer,Token> parser =
        sequence(
            repeat(Digit).min(1).build(
                (b,e,lst)->new NumberStart(b,e).value(
                    lst.map(t -> (long)"0123456789".indexOf(t.getText())).reverse().indexes().reduce(0L, (summ, digit) ->
                        summ + digit.getValue() * (long)(Math.pow( 10, digit.getIndex() ))
                    )
                )),
            DIGIT_DOT,
            repeat(Digit).min(1).build(
                (b,e,lst)->new NumberPart(b,e).value(
                    lst.map(t -> (double)"0123456789".indexOf(t.getText())).indexes().reduce( 0.0, (summ,digit) ->
                        summ + digit.getValue() * Math.pow(10, -1-digit.getIndex())
                    )
                )
            )
        ).build( (a,b,toks)->{
            var fnum = toks.pattern()
                .like(0, NumberStart.class)
                .like(2, NumberPart.class).match( (start,part)->
                    new FloatNumberToken(start.getBegin(), part.getEnd())
                        .value( ((double) start.getValue()) + part.getValue() )
                );

            return fnum.isPresent() ? fnum.get() : new Token(a,b);
        });
}
