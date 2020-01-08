package xyz.cofe.text.parse.mtest;

import xyz.cofe.iter.Eterable;
import xyz.cofe.text.parse.Token;
import xyz.cofe.text.parse.TokenPointer;

public class TokRef extends BaseExpr {
    private String toString;

    public TokRef( TokenPointer tok ){
        super(tok, tok.move(1));
        toString = TokRef.class.getSimpleName()+"{"+
            ""+(begin != null ? begin.lookup() : null)+
            '}';
    }

    public TokRef( TokRef sample){
        super(sample);
        if( sample!=null ){
            this.toString = sample.toString;
        }
    }

    @Override
    public String toString(){
        return toString!=null ? toString : super.toString();
    }

    @Override
    public TokRef clone(){
        return new TokRef(this);
    }

    @Override
    public Number eval(){
        throw new IllegalStateException("can't evaluate for TokRef");
    }
}
