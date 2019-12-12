package xyz.cofe.text.parse;

import org.junit.Test;
import xyz.cofe.collection.ImTree;
import xyz.cofe.collection.ImTreeWalk;
import xyz.cofe.ecolls.Fn3;
import xyz.cofe.iter.Eterable;
import xyz.cofe.text.parse.mtest.Atom;
import xyz.cofe.text.parse.mtest.BaseExpr;
import xyz.cofe.text.parse.mtest.BinaryExpr;
import xyz.cofe.text.parse.mtest.BrCloseTok;
import xyz.cofe.text.parse.mtest.BrOpenTok;
import xyz.cofe.text.parse.mtest.Expr;
import xyz.cofe.text.parse.mtest.Lexer;
import xyz.cofe.text.parse.mtest.MulTok;
import xyz.cofe.text.parse.mtest.ProxyFn;
import xyz.cofe.text.parse.mtest.SumTok;
import xyz.cofe.text.parse.toks.FloatNumberToken;
import xyz.cofe.text.parse.toks.WhiteSpaceToken;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static xyz.cofe.text.parse.TokBuilder.*;
import static xyz.cofe.text.parse.CharType.*;

public class MathTest {
    public <Result extends Expr> Result binaryOp(
        TokenPointer ptr,
        Predicate<Token> operator,
        Function<TokenPointer,Result> leftArg,
        Function<TokenPointer,Result> rightArg,
        Function<Token,Function<Result,Function<Result,Result>>> builder
    ){
        if( ptr.eof() )return null;

        var leftRes = leftArg.apply(ptr);
        if( leftRes==null )return null;

        ptr = leftRes.getEnd();
        var curRes = leftRes;

        while( true ){
            if( ptr.eof() )break;

            var op = ptr.lookup();
            if( operator.test( op ) ){
                var rightRes = rightArg.apply(ptr.move(1));
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

    @Test
    public void test01(){
        String source = "5.0 + 1.0 * 2.0 - 3.4 * ( 6.7 - 3.2 )";
        var toks = Lexer.tokens(source);

        toks.forEach( tok ->
            System.out.println(tok)
        );

        Function<TokenPointer,Expr> atom1 = t ->
            t.lookup() instanceof FloatNumberToken ?
                new Atom(t) : null;

        ProxyFn<TokenPointer,Expr> exp = new ProxyFn<>(null);
        Function<TokenPointer,Expr> atom2 = (t) -> {
            if( t.lookup() instanceof BrOpenTok ){
                var e = exp.apply(t.move(1));
                if( e!=null ){
                    if( e.getEnd().lookup() instanceof BrCloseTok ){
                        if( e instanceof BaseExpr ){
                            return ((BaseExpr)e).begin(t).end(((BaseExpr) e).getEnd().move(1));
                        }
                        return new Atom( t, e.getEnd().move(1), e );
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
            }else{
                return null;
            }
        };

        Alternatives<TokenPointer,Expr> atom = new Alternatives<>(
            new Function[]{
                atom1, atom2
            }
        );

        var binMul = binary(
            t -> t instanceof MulTok,
            atom,
            atom,
            BinaryExpr::new
        );

        var binSum = binary(
            t -> t instanceof SumTok,
            binMul,
            binMul,
            BinaryExpr::new
        );

        exp.setTarget( binSum );

        var ptr = new BasicTokenPointer(toks.toList(),0);

        var binOp = binSum.apply(ptr);

        System.out.println(".........................................");
        binOp.walk().tree().forEach( node -> System.out.println(
            "..".repeat(node.getLevel())+" "+node.getNode()
        ) );
    }
}
