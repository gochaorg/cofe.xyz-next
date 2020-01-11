package xyz.cofe.text.parse.mtest;

import xyz.cofe.iter.Eterable;
import xyz.cofe.num.BaseNumbers;
import xyz.cofe.num.BitCount;
import xyz.cofe.num.CommonBase;
import xyz.cofe.text.parse.Token;

import java.math.BigDecimal;

public class BinaryExpr extends BaseExpr {
    public BinaryExpr( Token op, Expr left, Expr right ){
        if( left==null ) throw new IllegalArgumentException("left==null");
        if( right==null ) throw new IllegalArgumentException("right==null");
        if( op==null ) throw new IllegalArgumentException("op==null");

        this.left = left;
        this.right = right;
        this.op = op;
        this.begin = left.getBegin();
        this.end = right.getEnd();
    }

    public BinaryExpr( Expr left, Token op, Expr right ){
        if( left==null ) throw new IllegalArgumentException("left==null");
        if( right==null ) throw new IllegalArgumentException("right==null");
        if( op==null ) throw new IllegalArgumentException("op==null");

        this.left = left;
        this.right = right;
        this.op = op;
        this.begin = left.getBegin();
        this.end = right.getEnd();
    }

    protected BinaryExpr(BinaryExpr sample){
        super(sample);
        if( sample!=null ){
            this.left = sample.left;
            this.right = sample.right;
            this.op = sample.op;
        }
    }

    public BinaryExpr clone(){
        return new BinaryExpr(this);
    }

    protected Expr left;
    public Expr getLeft(){
        return left;
    }

    protected Expr right;
    public Expr getRight(){
        return right;
    }

    protected Token op;
    public Token getOp(){
        return op;
    }

    @Override
    public Eterable<Expr> nodes(){
        return Eterable.of(left, right);
    }

    @Override
    public String toString(){
        return "BinaryExpr{"+
            "op="+op+
            '}';
    }

    @Override
    public Number eval(){
        Expr lExp = getLeft();
        if( lExp==null )throw new IllegalStateException("can't evaluate left operand - not defined");

        Number lVal = lExp.eval();
        if( lVal==null )throw new IllegalStateException("can't evaluate left operand - return null");

        Expr rExp = getRight();
        if( rExp==null )throw new IllegalStateException("can't evaluate right operand - not defined");

        Number rVal = rExp.eval();
        if( rVal==null )throw new IllegalStateException("can't evaluate right operand - return null");

        CommonBase cbase = BaseNumbers.commonBase(lVal,rVal, BitCount.max(lVal,rVal));

        Token tOp = getOp();
        if( tOp==null )throw new IllegalStateException("can't evaluate operator not defined");

        switch( tOp.getText() ){
            case "+": return cbase.add();
            case "-": return cbase.sub();
            case "*": return cbase.mul();
            case "/": return cbase.div();
        }

        return null;
    }
}
