package xyz.cofe.text.parse;

import org.junit.Test;
import xyz.cofe.ecolls.Fn3;
import xyz.cofe.iter.Eterable;
import xyz.cofe.text.parse.mtest.Atom;
import xyz.cofe.text.parse.mtest.BinaryExpr;
import xyz.cofe.text.parse.mtest.BrCloseTok;
import xyz.cofe.text.parse.mtest.BrOpenTok;
import xyz.cofe.text.parse.mtest.Expr;
import xyz.cofe.text.parse.mtest.Lexer;
import xyz.cofe.text.parse.mtest.MulTok;
import xyz.cofe.text.parse.mtest.ProxyFn;
import xyz.cofe.text.parse.mtest.SumTok;
import xyz.cofe.text.parse.mtest.UnaryExpr;
import xyz.cofe.text.parse.toks.FloatNumberToken;
import xyz.cofe.text.parse.toks.IntegerNumberToken;
import static xyz.cofe.text.parse.Builder.*;

import java.util.function.Function;
import java.util.function.Predicate;

public class MathTest {
    //region Binary op
    public <Result extends Expr> Result binaryOp(
        TokenPointer ptr,
        Predicate<Token> operator,
        Function<TokenPointer,Result> leftArg,
        Function<TokenPointer,Result> rightArg,
        Function<Token,Function<Result,Function<Result,Result>>> builder
    ){
        if( ptr.eof() )return null;

        Result leftRes = leftArg.apply(ptr);
        if( leftRes==null )return null;

        ptr = leftRes.getEnd();
        Result curRes = leftRes;

        while( true ){
            if( ptr.eof() )break;

            Token op = ptr.lookup();
            if( operator.test( op ) ){
                Result rightRes = rightArg.apply(ptr.move(1));
                if( rightRes!=null ){
                    curRes = builder.apply(op).apply(curRes).apply(rightRes);
                    ptr = curRes.getEnd();
                    continue;
                }
            }

            break;
        }

        return curRes;
    }

    public <Result extends Expr> Result binaryOp(
        TokenPointer ptr,
        Predicate<Token> operator,
        Function<TokenPointer,Result> leftArg,
        Function<TokenPointer,Result> rightArg,
        Fn3<Token,Result,Result,Result> builder
    ){
        return binaryOp(ptr,operator,leftArg,rightArg,op->l->r->builder.apply(op,l,r));
    }

    public <R extends Expr> Function<TokenPointer,R> binary(
        Predicate<Token> operator,
        Function<TokenPointer,R> leftArg,
        Function<TokenPointer,R> rightArg,
        Fn3<Token,R,R,R> builder
    ){
        return t -> binaryOp( t, operator, leftArg, rightArg, builder );
    }
    //endregion

    @Test
    public void test01(){
        String source = "5.0 + 1 * 2.0 - 3.4 * ( 6.7 - 3.2 )";
        Eterable<Token> toks = Lexer.tokens(source);

        toks.forEach(System.out::println);

        /////////////////////////////

        Alternatives atom1 = alt( Atom::parseFloat, Atom::parseInteger );

        ProxyFn<TokenPointer,Expr> exp = new ProxyFn<>(null);

        Sequence atom2b = seq(
            BrOpenTok::ref,
            exp,
            BrCloseTok::ref
        ).build( (begin,end,seq)->new Atom(begin, end, seq.get(1)) );

        Sequence atom3 = seq(
            SumTok::unaryMinus,
            exp
        ).build( (begin,end,seq)->new UnaryExpr( begin.lookup(), seq.get(1), begin, end) );

        Alternatives atom = alt(
            atom1, atom2b, atom3
        );

        Function binMul = binary(
            t -> t instanceof MulTok,
            atom,
            atom,
            BinaryExpr::new
        );

        Function binSum = binary(
            t -> t instanceof SumTok,
            binMul,
            binMul,
            BinaryExpr::new
        );

        exp.setTarget( binSum );

        TokenPointer ptr = new BasicTokenPointer(toks.toList(),0);

        Expr binOp = exp.apply(ptr);

        if( binOp!=null ){
            System.out.println(Str.repeat("-",40));
            binOp.walk().tree().forEach(node->System.out.println(
                Str.repeat("..",node.getLevel())+" "+node.getNode()
            ));

            System.out.println(Str.repeat("-",40));
            System.out.println("eval="+binOp.eval());
        }
    }
}
