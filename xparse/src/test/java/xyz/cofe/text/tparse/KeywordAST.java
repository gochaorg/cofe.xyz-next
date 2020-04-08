package xyz.cofe.text.tparse;

public class KeywordAST extends ASTBase<KeywordAST> {
    public KeywordAST(KeywordAST sample) {
        super(sample);
        if( sample!=null ){
            this.keywordTok = sample.keywordTok;
        }
    }

    public KeywordAST(TPointer begin, CToken keywordTok) {
        if(begin==null)throw new IllegalArgumentException("begin==null");
        if( keywordTok==null )throw new IllegalArgumentException("keywordTok==null");
        this.begin = begin;
        this.end = begin.move(1);
        this.keywordTok = keywordTok;
    }

    public KeywordAST clone(){ return new KeywordAST(this); }

    protected CToken keywordTok;
    public CToken keywordTok(){ return keywordTok; }
    public KeywordAST keywordTok(CToken t ){
        if( t==null )throw new IllegalArgumentException("t==null");
        KeywordAST c = clone();
        c.keywordTok = t;
        return c;
    }
}
