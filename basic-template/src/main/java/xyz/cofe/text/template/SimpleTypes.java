package xyz.cofe.text.template;

/**
 * Проверка на простые типы
 * @author gocha
 */
public class SimpleTypes
{
    private static final Class cBool1 = boolean.class;
    private static final Class cBool2 = Boolean.class;
    private static final Class cByte1 = byte.class;
    private static final Class cByte2 = Byte.class;
    private static final Class cChar1 = char.class;
    private static final Class cChar2 = Character.class;
    private static final Class cDouble1 = double.class;
    private static final Class cDouble2 = Double.class;
    private static final Class cFloat1 = float.class;
    private static final Class cFloat2 = Float.class;
    private static final Class cInteger1 = int.class;
    private static final Class cInteger2 = Integer.class;
    private static final Class cLong1 = long.class;
    private static final Class cLong2 = Long.class;
    private static final Class cShort1 = short.class;
    private static final Class cShort2 = Short.class;
    private static final Class cVoid1 = Void.class;
    private static final Class cVoid2 = void.class;

    private static final Class[] simpleTypes =
        {
            cBool1, cBool2, cByte1, cByte2, cChar1, cChar2, cDouble1, cDouble2,
            cFloat1, cFloat2, cInteger1, cInteger2, cLong1, cLong2, cShort1, cShort2,
            cVoid1, cVoid2
        };

    /**
     * Возвращает простые типы
     * @return Простые типы
     */
    public static final Class[] simpleTypes(){ return simpleTypes; }

    public static final Class voidObject(){ return cVoid1; }

    public static final Class boolObject(){ return cBool2; }
    public static final Class byteObject(){ return cByte2; }
    public static final Class charObject(){ return cChar2; }
    public static final Class doubleObject(){ return cDouble2; }
    public static final Class floatObject(){ return cFloat2; }
    public static final Class intObject(){ return cInteger2; }
    public static final Class longObject(){ return cLong2; }
    public static final Class shortObject(){ return cShort2; }

    public static final Class _void(){ return cVoid2; }

    public static final Class _bool(){ return cBool1; }
    public static final Class _byte(){ return cByte1; }
    public static final Class _char(){ return cChar1; }
    public static final Class _double(){ return cDouble1; }
    public static final Class _float(){ return cFloat1; }
    public static final Class _int(){ return cInteger1; }
    public static final Class _long(){ return cLong1; }
    public static final Class _short(){ return cShort1; }

    private static final Class[] boolTypes = new Class[]{ cBool1, cBool2 };
    private static final Class[] byteTypes = new Class[]{ cByte1, cByte2 };
    private static final Class[] charTypes = new Class[]{ cChar1, cChar2 };
    private static final Class[] doubleTypes = new Class[]{ cDouble1, cDouble2 };
    private static final Class[] floatTypes = new Class[]{ cFloat1, cFloat2 };
    private static final Class[] intTypes = new Class[]{ cInteger1, cInteger2 };
    private static final Class[] longTypes = new Class[]{ cLong1, cLong2 };
    private static final Class[] shortTypes = new Class[]{ cShort1, cShort2 };

    public static final Class[] boolTypes(){ return boolTypes; }
    public static final Class[] byteTypes(){ return byteTypes; }
    public static final Class[] charTypes(){ return charTypes; }
    public static final Class[] doubleTypes(){ return doubleTypes; }
    public static final Class[] floatTypes(){ return floatTypes; }
    public static final Class[] intTypes(){ return intTypes; }
    public static final Class[] longTypes(){ return longTypes; }
    public static final Class[] shortTypes(){ return shortTypes; }

    /**
     * Проверка что указанный класс являеться простым типом данных<br>
     * boolean, byte, char, double, float, int, long, void, short
     * @param c класс
     * @return true - является, false - не является
     */
    public static final boolean isSimple(Class c)
    {
        return isBoolean(c) ||
            isByte(c) ||
            isChar(c) ||
            isDouble(c) ||
            isFloat(c) ||
            isInt(c) ||
            isLong(c) ||
            isVoid(c) ||
            isShort(c);
    }

    public static final boolean isNullable(Class c){
        if( c==null )throw new IllegalArgumentException( "c==null" );
        if( c.equals(cBool2) )return true;
        if( c.equals(cByte2) )return true;
        if( c.equals(cChar2) )return true;
        if( c.equals(cDouble2) )return true;
        if( c.equals(cFloat2) )return true;
        if( c.equals(cInteger2) )return true;
        if( c.equals(cLong2) )return true;
        if( c.equals(cShort2) )return true;
        return false;
    }

    public static final boolean isVoid(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cVoid2.equals(c) || cVoid1.equals(c);
    }

    public static final boolean isBoolean(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cBool1.equals(c) || cBool2.equals(c);
    }

    public static final boolean isByte(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cByte1.equals(c) || cByte2.equals(c);
    }

    public static final boolean isChar(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cChar1.equals(c) || cChar2.equals(c);
    }

    public static final boolean isDouble(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cDouble1.equals(c) || cDouble2.equals(c);
    }

    public static final boolean isFloat(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cFloat1.equals(c) || cFloat2.equals(c);
    }

    public static final boolean isInt(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cInteger1.equals(c) || cInteger2.equals(c);
    }

    public static final boolean isLong(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cLong1.equals(c) || cLong2.equals(c);
    }

    public static final boolean isShort(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cShort1.equals(c) || cShort2.equals(c);
    }
}
