package xyz.cofe.text.parse.mtest;

import xyz.cofe.iter.Eterable;
import xyz.cofe.text.parse.Alternatives;
import xyz.cofe.text.parse.CharAlternatives;
import xyz.cofe.text.parse.Repeat;
import xyz.cofe.text.parse.Token;
import xyz.cofe.text.parse.Tokenizer;
import xyz.cofe.text.parse.toks.FloatNumberToken;
import xyz.cofe.text.parse.toks.IntegerNumberToken;
import xyz.cofe.text.parse.toks.WhiteSpaceToken;

import static xyz.cofe.text.parse.CharType.Whitespace;
import static xyz.cofe.text.parse.TokBuilder.alt;
import static xyz.cofe.text.parse.TokBuilder.repeat;

public class Lexer {
    public static Tokenizer tokenizer(String source){
        CharAlternatives numParser = alt( FloatNumberToken.parser, IntegerNumberToken.parser );
        CharAlternatives sumParser = alt( c -> (c.lookup()=='+' || c.lookup()=='-') ? new SumTok(c,c.move(1)) : null );
        CharAlternatives mulParser = alt( c -> (c.lookup()=='*' || c.lookup()=='/') ? new MulTok(c,c.move(1)) : null );
        CharAlternatives br1Parser =  alt( c -> (c.lookup()=='(' ) ? new BrOpenTok(c,c.move(1)) : null );
        CharAlternatives br2Parser =  alt( c -> (c.lookup()==')' ) ? new BrCloseTok(c,c.move(1)) : null );

        Repeat wsParser = repeat(Whitespace).min(1).build( WhiteSpaceToken::new );
        return new Tokenizer(source,0, numParser, sumParser, mulParser, wsParser, br1Parser, br2Parser);
    }

    public static Eterable<Token> tokens(String source){
        return tokenizer(source).filter(x -> !(x instanceof WhiteSpaceToken));
    }
}
