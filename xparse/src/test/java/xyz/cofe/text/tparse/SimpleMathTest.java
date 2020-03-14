package xyz.cofe.text.tparse;

import org.junit.Assert;
import org.junit.Test;
import xyz.cofe.text.parse.an.Char;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static xyz.cofe.text.tparse.Chars.*;

public class SimpleMathTest {
    //region number : gr
    public static final GR<CharPointer,CToken> dot = test( c -> c=='.' );

    public static final GR<CharPointer, DigitsToken> digits
        = digit.repeat().map(DigitsToken::new);

    public static final GR<CharPointer, NumberToken> integerNumber
        = digit.repeat().map( digits -> new NumberToken( new DigitsToken(digits) ) );

    public static final GR<CharPointer, NumberToken> floatNumber
        = digits.next(dot).next(digits)
        .map( (intDigits,dot,floatDigits)->new NumberToken(intDigits,floatDigits) );

    public static final GR<CharPointer, NumberToken> number
        = floatNumber.another(integerNumber)
        .map( t->(NumberToken)t );
    //endregion
    //region ws : gr
    public static class WS extends CToken {
        public WS(List<CToken> tokens) { super(tokens); }
    }

    public final GR<CharPointer, WS> ws = whitespace.repeat().map(WS::new);
    //endregion

    @Test
    public void numbersSeq(){
        List<? extends CToken> toks =
            Tokenizer
            .lexer("10.1 12 23", ws, number)
            .filter( t -> !(t instanceof WS))
            .toList();

        toks.forEach(System.out::println);

        Assert.assertTrue(toks.stream().allMatch(t -> t instanceof NumberToken));
        Assert.assertTrue(toks.size()==3);

        List<NumberToken> ntoks = toks.stream().map(t -> (NumberToken)t).collect(Collectors.toList());
        Assert.assertTrue( ntoks.get(0).doubleValue()>10 && ntoks.get(0).doubleValue()<11 );
        Assert.assertTrue( ntoks.get(0).isFloat() );
        Assert.assertTrue( !ntoks.get(1).isFloat() );
    }

    public static <T extends CToken, A extends AST>
        GR<LPointer<CToken>,A> atomic(Class<T> target, BiFunction<LPointer<CToken>,T,A> map ){
        if( target==null )throw new IllegalArgumentException("target == null");
        return ptr -> {
            CToken t = ptr.lookup(0).orElseGet( null );
            if( t!=null && target.isAssignableFrom(t.getClass()) ){
                return Optional.of( map.apply(ptr,(T)t) );
            }
            return Optional.empty();
        };
    }

    public static <T extends CToken, A extends AST>
        GR<LPointer<CToken>,A> atomic(Class<T> target, Predicate<T> filter, BiFunction<LPointer<CToken>, T, A> map ){
        if( target==null )throw new IllegalArgumentException("target == null");
        return ptr -> {
            CToken t = ptr.lookup(0).orElse( null );
            if( t!=null && target.isAssignableFrom(t.getClass()) && (filter==null || filter.test((T)t)) ){
                return Optional.of( map.apply(ptr,(T)t) );
            }
            return Optional.empty();
        };
    }

    public static final GR<LPointer<CToken>, NumberAST> numb = atomic(
        NumberToken.class, NumberAST::new
    );

    public static final GR<LPointer<CToken>, KeywordAST> sumOperator = atomic(
        CToken.class,
        tok -> Arrays.asList("+","-").contains(tok.text()),
        KeywordAST::new
    );

    public static final GR<LPointer<CToken>, KeywordAST> mulOperator = atomic(
        CToken.class,
        tok -> Arrays.asList("*","/").contains(tok.text()),
        KeywordAST::new
    );

    public static final GR<LPointer<CToken>, BinaryOpAST> binOp1
        = numb.next(sumOperator).next(numb)
        .map(BinaryOpAST::new);

    public static GR<LPointer<CToken>, BinaryOpAST> binaryOp(
        GR<LPointer<CToken>,? extends AST> grLeft,
        GR<LPointer<CToken>,? extends KeywordAST> operator,
        GR<LPointer<CToken>,? extends AST> grRight
    ) {
        if( grLeft==null )throw new IllegalArgumentException( "grLeft==null" );
        if( operator==null )throw new IllegalArgumentException( "operator==null" );
        if( grRight==null )throw new IllegalArgumentException( "grRight==null" );

        return ptr -> {
            if( ptr==null || ptr.eof() )return Optional.empty();

            //LPointer<CToken> beginPtr = ptr;

            Optional<? extends AST> left = grLeft.apply(ptr);
            if( !left.isPresent() )return Optional.empty();

            BinaryOpAST binOp = null;

            while ( true ) {
                Optional<? extends KeywordAST> op = operator.apply(
                    binOp==null ?
                        left.get().end() :
                        binOp.right().end()
                    );
                if (!op.isPresent()) break;

                Optional<? extends AST> right = grRight.apply(op.get().end());
                if( !right.isPresent() )break;

                if( binOp==null ){
                    binOp = new BinaryOpAST(left.get(), op.get(), right.get());
                }else{
                    binOp = new BinaryOpAST(binOp, op.get(), right.get());
                }
            }

            if( binOp==null )return Optional.empty();

            return Optional.of( binOp );
        };
    }

    public static final GR<LPointer<CToken>,KeywordAST> brOpen = atomic(
        CToken.class, t->t.text().equals("("), KeywordAST::new
    );

    public static final GR<LPointer<CToken>,KeywordAST> brClose = atomic(
        CToken.class, t->t.text().equals(")"), KeywordAST::new
    );

    public static final GR<LPointer<CToken>,AST> dummy = ptr -> Optional.empty();

    public static final ProxyGR<LPointer<CToken>,AST> expression = new ProxyGR<>(dummy);

    public static final GR<LPointer<CToken>,? super AST> brAtomValue
        = brOpen.next(expression).next(brClose).map((b0, e, b1)->e);

    public static final GR<LPointer<CToken>,AST> atomValue
        = numb.another(brAtomValue).map( t -> (AST)t );

    public static final GR<LPointer<CToken>,? extends AST> mulOp
        = binaryOp( atomValue, mulOperator, atomValue ).another( atomValue ).map( t->(AST)t );

    public static final GR<LPointer<CToken>,? extends AST> sumOp
        = binaryOp( mulOp, sumOperator, mulOp ).another( mulOp ).map( t->(AST)t );

    {
        expression.setTarget(sumOp);
    }

    private static final Predicate<Character> isOperator = c -> c=='+' || c=='-' || c=='*' || c=='/' || c=='(' || c==')' ;

    @Test
    public void binaryOpTest1(){
        List<? extends CToken> toks =
            Tokenizer
                .lexer("10 + 12 + 23", ws, number, Chars.test(isOperator) )
                .filter( t -> !(t instanceof WS))
                .toList();

        toks.forEach(System.out::println);

        LPointer<CToken> tptr = new LPointer(toks);
        Optional<NumberAST> r = numb.apply(tptr);
        Assert.assertTrue(r!=null && r.isPresent() );

        System.out.println("binOp1");
        System.out.println( binOp1.apply(tptr) );
    }

    @Test
    public void binaryOpTest2(){
        List<? extends CToken> toks =
            Tokenizer
                .lexer("1 + 2 + 3", ws, number, Chars.test(isOperator) )
                .filter( t -> !(t instanceof WS))
                .toList();

        toks.forEach(System.out::println);

        LPointer<CToken> tptr = new LPointer(toks);

        System.out.println("expression");
        System.out.println( expression.apply(tptr) );
    }
}
