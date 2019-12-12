package xyz.cofe.text.parse.mtest;

import xyz.cofe.iter.Eterable;
import xyz.cofe.text.parse.MathTest;
import xyz.cofe.text.parse.TokenPointer;

public class BaseExpr implements Expr {
    protected BaseExpr(){
    }

    protected BaseExpr(BaseExpr sample){
        if( sample!=null ){
            this.begin = sample.begin;
            this.end = sample.end;
        }
    }

    public BaseExpr( TokenPointer begin, TokenPointer end){
        this.begin = begin;
        this.end = end;
    }

    protected TokenPointer begin;
    public TokenPointer getBegin(){
        return begin;
    }
    public BaseExpr begin(TokenPointer newBegin){
        BaseExpr e = clone();
        e.begin = newBegin;
        return e;
    }

    protected TokenPointer end;
    @Override public TokenPointer getEnd(){
        return end;
    }
    public BaseExpr end(TokenPointer newEnd){
        BaseExpr e = clone();
        e.end = newEnd;
        return e;
    }

    public BaseExpr clone(){
        return new BaseExpr(this);
    }

    @Override
    public Eterable<Expr> nodes(){
        return Eterable.empty();
    }
}