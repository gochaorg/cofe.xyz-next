package xyz.cofe.text.lex;

/**
 * Лексема - число
 * @author gocha
 */
public class NumberConst extends Token
{
    public NumberConst(){
        this.id = "numberConst";
    }

    public NumberConst( NumberConst src ){
        if( src!=null ){
            this.isFloat = src.isFloat;
            this.number = src.number;
        }
    }

    @Override
    public NumberConst clone() {
        return new NumberConst(this);
    }

    @Override
    public String getId() {
        if( id==null )id = "numberConst";
        return super.getId();
    }

    protected boolean isFloat = false;
    protected Number number = null;

    public boolean isIsFloat() {
        return isFloat;
    }

    public void setIsFloat(boolean isFloat) {
        this.isFloat = isFloat;
    }

    public Number getNumber() {
        return number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }

    @Override
    public String toString(){
        Number num = number;
        return getId() + ( num==null ? "" : " "+num );
    }
}
