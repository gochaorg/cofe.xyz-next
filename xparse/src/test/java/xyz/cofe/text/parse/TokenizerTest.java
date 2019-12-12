package xyz.cofe.text.parse;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;
import xyz.cofe.text.parse.tmpl.InOutTemplate;
import xyz.cofe.text.parse.toks.*;

import java.util.function.BiFunction;
import java.util.function.Function;

import static xyz.cofe.text.parse.TokBuilder.*;
import static xyz.cofe.text.parse.CharType.*;

public class TokenizerTest {
    @Test
    public void test02(){
        Function[] parsers = new Function[]{
            IdToken.parse,
            WhiteSpaceToken.parse
        };

        Token t1 = Tokenizer.parse("  bla bla",0, parsers);
        System.out.println(t1);
        Assert.assertTrue(t1!=null);

        Token t2 = Tokenizer.parse("bla bla",0, parsers);
        System.out.println(t2);
        Assert.assertTrue(t2!=null);
    }

    @Test
    public void test03(){
        var idParser = sequence(
            Letter,
            repeat(LetterOrDigit)
        ).build( IdToken::new );

        Token t1 = Tokenizer.parse( "aa2bc",
            idParser
        );
        System.out.println(t1);
        assertTrue(t1!=null);

        Token t2 = Tokenizer.parse( "a123", idParser );
        System.out.println(t2);
        assertTrue(t2!=null);

        Token t3 = Tokenizer.parse( "123", idParser );
        System.out.println(t3);
        assertTrue(t3==null);

        BiFunction bf = null;
        bf.andThen( a -> null );
    }

    @Test
    public void test04(){
        var tmp = InOutTemplate.parse("input ${in} output ${out}");
        String txt = tmp.apply("123", "abc");
        System.out.println(txt);

        assertTrue(txt!=null);
        assertTrue(txt.equals("input 123 output abc"));
    }

    @Test
    public void test05(){
        String str = Letter.exprTemplate("ptr");
        System.out.println(str);
    }

    @Test
    public void test06(){
        String txt = "aa1 123.45";

        var idParser = sequence(
            Letter,
            repeat(LetterOrDigit)
        ).build( IdToken::new );

        var wsParser = repeat(Whitespace).min(1).build( WhiteSpaceToken::new );

        var numParser = alt(
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
            }),
            repeat(Digit).min(1)
        );

        var tokenizer = new Tokenizer(txt,0,idParser,wsParser, numParser);
        int idx = -1;
        for( Token t : tokenizer ){
            idx++;
            System.out.println("["+idx+"] "+t);
        }
    }
}
