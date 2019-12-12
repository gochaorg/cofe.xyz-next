package xyz.cofe.text.parse.mtest;

import xyz.cofe.iter.Eterable;
import xyz.cofe.text.parse.TokenPointer;

public class Atom extends BaseExpr {
    private String toString;
    private Expr expr;

    public Atom( TokenPointer tok ){
        super(tok, tok.move(1));
        toString = "Atom{"+
            ""+(begin != null ? begin.lookup() : null)+
            '}';
    }

    public Atom( TokenPointer tBegin, TokenPointer tEnd, Expr expr ){
        super(tBegin, tEnd);
        toString = "Atom{"+
            ""+(expr)+
            '}';
        this.expr = expr;
    }

    public Atom(Atom sample){
        super(sample);
        if( sample!=null ){
            this.expr = sample.expr;
            this.toString = sample.toString;
        }
    }

    @Override
    public String toString(){
        return toString!=null ? toString : super.toString();
    }

    @Override
    public Eterable<Expr> nodes(){
        return expr!=null ? Eterable.single(expr) : super.nodes();
    }
}
