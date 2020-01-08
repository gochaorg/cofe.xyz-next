package xyz.cofe.text.parse.mtest;

import xyz.cofe.iter.Eterable;
import xyz.cofe.text.parse.TokenPointer;
import xyz.cofe.text.parse.toks.FloatNumberToken;
import xyz.cofe.text.parse.toks.IntegerNumberToken;

import java.util.function.Function;

public class Atom extends BaseExpr {
    private String toString;
    private Expr expr;

    public Atom( TokenPointer ptr ){
        super(ptr, ptr.move(1));
        toString = Atom.class.getSimpleName()+"{"+
            ""+(begin != null ? begin.lookup() : null)+
            '}';

        var t = ptr.lookup();
        if( t instanceof IntegerNumberToken ){
        }
    }

    public Atom( TokenPointer tBegin, TokenPointer tEnd, Expr expr ){
        super(tBegin, tEnd);
        toString = Atom.class.getSimpleName()+"{"+expr+'}';
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

    public static Expr parseFloat( TokenPointer p ){
        if( p==null ) throw new IllegalArgumentException("p==null");
        return p.lookup( t -> t instanceof FloatNumberToken ? new Atom(p) : null );
    }

    public static Expr parseInteger( TokenPointer p ){
        if( p==null ) throw new IllegalArgumentException("p==null");
        return p.lookup( t -> t instanceof IntegerNumberToken ? new Atom(p) : null );
    }

    @Override
    public Atom clone(){
        return new Atom(this);
    }

    @Override
    public Number eval(){
        var ex = expr;
        if( ex!=null )return ex.eval();

        var p = getBegin();
        if( p!=null ){
            var t = p.lookup();
            if( t!=null ){
                if( t instanceof IntegerNumberToken ){
                    return ((IntegerNumberToken) t).getValue();
                }else if( t instanceof FloatNumberToken ){
                    return ((FloatNumberToken) t).getValue();
                }
            }
        }

        throw new Error("can't evaluate number for "+p);
    }
}
