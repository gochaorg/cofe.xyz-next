package xyz.cofe.text.tparse;

public class NumberAST extends ASTBase<NumberAST> {
    public NumberAST(NumberAST sample) {
        super(sample);
        if( sample!=null ){
            this.numberToken = sample.numberToken;
        }
    }

    public NumberAST(TPointer begin, NumberToken numberToken) {
        if(begin==null)throw new IllegalArgumentException("begin==null");
        if( numberToken==null )throw new IllegalArgumentException("numberToken==null");
        this.begin = begin;
        this.end = begin.move(1);
        this.numberToken = numberToken;
    }

    public NumberAST clone(){ return new NumberAST(this); }

    protected NumberToken numberToken;
    public NumberToken numberToken(){ return numberToken; }
    public NumberAST numberToken(NumberToken t ){
        if( t==null )throw new IllegalArgumentException("t==null");
        NumberAST c = clone();
        c.numberToken = t;
        return c;
    }

    public double doubleValue(){ return numberToken.doubleValue(); };
    public long longValue(){ return numberToken.longValue(); };
    public boolean isFloat(){ return numberToken.isFloat(); };

    @Override
    public String toString() {
        return NumberAST.class.getSimpleName()+" "+(isFloat() ? doubleValue() : longValue());
    }
}
