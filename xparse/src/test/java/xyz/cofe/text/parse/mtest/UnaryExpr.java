package xyz.cofe.text.parse.mtest;

import xyz.cofe.iter.Eterable;
import xyz.cofe.num.BaseNumbers;
import xyz.cofe.num.BitCount;
import xyz.cofe.num.SingleBase;
import xyz.cofe.text.parse.Token;
import xyz.cofe.text.parse.TokenPointer;

public class UnaryExpr extends BaseExpr {
    public UnaryExpr( Token op, Expr target, TokenPointer begin, TokenPointer end ){
        if( target==null ) throw new IllegalArgumentException("target==null");
        if( op==null ) throw new IllegalArgumentException("op==null");
        if( begin==null ) throw new IllegalArgumentException("begin==null");
        if( end==null ) throw new IllegalArgumentException("end==null");

        this.target = target;
        this.op = op;
        this.begin = begin;
        this.end = end;
    }

    protected UnaryExpr( UnaryExpr sample){
        super(sample);
        if( sample!=null ){
            this.target = sample.target;
            this.op = sample.op;
        }
    }

    public UnaryExpr clone(){
        return new UnaryExpr(this);
    }

    protected Expr target;
    public Expr getTarget(){
        return target;
    }

    protected Token op;
    public Token getOp(){
        return op;
    }

    @Override
    public Eterable<Expr> nodes(){
        return Eterable.of(target);
    }

    @Override
    public String toString(){
        return UnaryExpr.class.getSimpleName()+"{"+
            "op="+op+
            '}';
    }

    @Override
    public Number eval(){
        Expr exp = getTarget();
        if( exp==null )throw new IllegalStateException("can't evaluate target operand - not defined");

        Number value = exp.eval();
        if( value==null )throw new IllegalStateException("can't evaluate target operand - return null");

        SingleBase cbase = BaseNumbers.singleBase(value, BitCount.of(value));

        Token tOp = getOp();
        if( tOp==null )throw new IllegalStateException("can't evaluate operator not defined");

        switch( tOp.getText() ){
            case "+": return value;
            case "-": return cbase.sub( cbase.zero(), value );
        }

        return null;
    }
}
