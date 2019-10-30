package xyz.cofe.text.parse;

public class IndexValue<A> {
    private int index;
    private A value;
    public IndexValue( int idx, A val ){
        this.index = idx;
        this.value = val;
    }
    public int i(){ return this.index; }
    public int getIndex(){ return this.index; }

    public A v(){ return this.value; }
    public A getValue(){ return this.value; }
}
