package xyz.cofe.text.lex;

/**
 * Парсер числа
 * @see NumberConst
 * @author gocha
 */
public class NumberConstParser implements TokenParser
{
    protected int radix = 10;
    protected boolean isFloat = false;
    protected String id = null;

    /**
     * Конструктор, по умолчанию используется 10-чася система счисления, целые числа
     */
    public NumberConstParser(){
    }

    /**
     * Конструктор
     * @param radix система счисления (2..16)
     * @param isFloat плавующее / целое числа
     */
    public NumberConstParser(int radix,boolean isFloat){
        if (radix<2 || radix>16) {
            throw new IllegalArgumentException("radix<2||radix>16");
        }
        this.radix = radix;
        this.isFloat = isFloat;
    }

    public NumberConstParser(String id,int radix,boolean isFloat){
        this.id = id;
        if (radix<2 || radix>16) {
            throw new IllegalArgumentException("radix<2||radix>16");
        }
        this.radix = radix;
        this.isFloat = isFloat;
    }

    protected static int getDigit(char c,int radix){
        if( radix<2 || radix>16 )return -2;
        switch( c ){
            case '0': if( radix>=2 )return 0; break;
            case '1': if( radix>=2 )return 1; break;
            case '2': if( radix>=3 )return 2; break;
            case '3': if( radix>=4 )return 3; break;
            case '4': if( radix>=5 )return 4; break;
            case '5': if( radix>=6 )return 5; break;
            case '6': if( radix>=7 )return 6; break;
            case '7': if( radix>=8 )return 7; break;
            case '8': if( radix>=9 )return 8; break;
            case '9': if( radix>=10 )return 9; break;
            case 'a': case 'A': if( radix>=11 )return 10; break;
            case 'b': case 'B': if( radix>=12 )return 11; break;
            case 'c': case 'C': if( radix>=13 )return 12; break;
            case 'd': case 'D': if( radix>=14 )return 13; break;
            case 'e': case 'E': if( radix>=15 )return 14; break;
            case 'f': case 'F': if( radix>=16 )return 15; break;
        }
        return -1;
    }

    protected NumberConst createNumberConst(){
        return new NumberConst();
    }

    protected volatile char[] floatPointChars = new char[]{ '.' };

    public synchronized char[] getFloatPointChars() {
        return floatPointChars;
    }

    public synchronized void setFloatPointChars(char[] floatPointChars) {
        if( floatPointChars==null || floatPointChars.length<1 )throw new IllegalArgumentException("must has one or more chars");
        this.floatPointChars = floatPointChars;
    }

    @Override
    public synchronized NumberConst parse(String source, int offset) {
        if( source==null )throw new IllegalArgumentException( "source==null" );
        if( offset<0 )throw new IllegalArgumentException( "offset<0" );

        if( floatPointChars==null )throw new IllegalStateException("floatPointChars == null");
        if( floatPointChars.length<1 )throw new IllegalStateException("floatPointChars.length<1");

        StringBuilder intPartSB = new StringBuilder();
        StringBuilder floatPartSB = new StringBuilder();
        int state = 0;
        int idx = -1;

        while( true ){
            idx++;
            if( (idx+offset) >= source.length() )break;

            char c = source.charAt(idx+offset);
            int d = getDigit(c,radix);
            if( d<0 ){
                if( isFloat ){
                    boolean nextIter = false;
                    for( char floatPntChar : floatPointChars ){
                        if( c==floatPntChar && state == 0 ){
                            state = 1;
                            nextIter = true;
                            break;
                        }
                    }
                    if( nextIter )continue;
                }
                if( idx==0 )return null;
                break;
            }
            if( state==0 )intPartSB.insert(0, c);
            else if( state==1 )floatPartSB.append(c);
        }

        long intPart = 0;
        if( intPartSB.length()>0 ){
            long kof = 1;
            for( int i=0; i<intPartSB.length(); i++ ){
                long d = getDigit(intPartSB.charAt(i), radix);
                intPart += d * kof;
                kof = kof * (long)radix;
            }
        }

        double floatPart = 0;
        if( floatPartSB.length()>0 ){
            double kof = 1;
            for( int i=0; i<floatPartSB.length(); i++ ){
                int d = getDigit(floatPartSB.charAt(i), radix);
                floatPart += ((double)d) * kof;
                kof = kof / ((double)radix);
            }
        }

        NumberConst num = createNumberConst();
        num.setBegin(offset);
        num.setLength(idx);
        num.setSource(source);
        num.setIsFloat(isFloat);
        if( isFloat ){
            Number n = ((double)intPart) + floatPart;
            num.setNumber(n);
        }else{
            Number n = (Long)intPart;
            num.setNumber(n);
        }
        if( id!=null )num.setId(id);

        return num;
    }
}
