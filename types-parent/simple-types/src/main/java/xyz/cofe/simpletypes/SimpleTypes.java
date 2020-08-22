package xyz.cofe.simpletypes;

/**
 * Проверка на простые типы
 * @author gocha
 */
@SuppressWarnings({ "rawtypes", "unused" })
public class SimpleTypes {
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
     * Возвращает все простые типы включая boxing типы
     * @return Простые типы
     */
    public static Class[] simpleTypes(){ return simpleTypes; }

    /**
     * Возвращает тип void
     * @return тип void
     */
    public static Class voidObject(){ return cVoid1; }

    /**
     * Возвращает тип boolean
     * @return тип boolean
     */
    public static Class boolObject(){ return cBool2; }

    /**
     * Возвращает тип byte
     * @return тип byte
     */
    public static Class byteObject(){ return cByte2; }

    /**
     * Возвращает тип char
     * @return тип char
     */
    public static Class charObject(){ return cChar2; }

    /**
     * Возвращает тип double
     * @return тип double
     */
    public static Class doubleObject(){ return cDouble2; }

    /**
     * Возвращает тип float
     * @return тип float
     */
    public static Class floatObject(){ return cFloat2; }

    /**
     * Возвращает тип int
     * @return тип int
     */
    public static Class intObject(){ return cInteger2; }

    /**
     * Возвращает тип long
     * @return тип long
     */
    public static Class longObject(){ return cLong2; }

    /**
     * Возвращает тип short
     * @return тип short
     */
    public static Class shortObject(){ return cShort2; }

    /**
     * Возвращает тип void
     * @return тип void
     */
    public static Class _void(){ return cVoid2; }


    /**
     * Возвращает тип boolean
     * @return тип boolean
     */
    public static Class _bool(){ return cBool1; }

    /**
     * Возвращает тип byte
     * @return тип byte
     */
    public static Class _byte(){ return cByte1; }

    /**
     * Возвращает тип char
     * @return тип char
     */
    public static Class _char(){ return cChar1; }

    /**
     * Возвращает тип double
     * @return тип double
     */
    public static Class _double(){ return cDouble1; }

    /**
     * Возвращает тип float
     * @return тип float
     */
    public static Class _float(){ return cFloat1; }

    /**
     * Возвращает тип int
     * @return тип int
     */
    public static Class _int(){ return cInteger1; }

    /**
     * Возвращает тип long
     * @return тип long
     */
    public static Class _long(){ return cLong1; }

    /**
     * Возвращает тип short
     * @return тип short
     */
    public static Class _short(){ return cShort1; }

    private static final Class[] boolTypes = new Class[]{ cBool1, cBool2 };
    private static final Class[] byteTypes = new Class[]{ cByte1, cByte2 };
    private static final Class[] charTypes = new Class[]{ cChar1, cChar2 };
    private static final Class[] doubleTypes = new Class[]{ cDouble1, cDouble2 };
    private static final Class[] floatTypes = new Class[]{ cFloat1, cFloat2 };
    private static final Class[] intTypes = new Class[]{ cInteger1, cInteger2 };
    private static final Class[] longTypes = new Class[]{ cLong1, cLong2 };
    private static final Class[] shortTypes = new Class[]{ cShort1, cShort2 };

    /**
     * Возвращает типы boolean.class, Boolean.class
     * @return типы boolean.class, Boolean.class
     */
    public static Class[] boolTypes(){ return boolTypes; }

    /**
     * Возвращает типы byte.class, Byte.class
     * @return типы boolean.class, Boolean.class
     */
    public static Class[] byteTypes(){ return byteTypes; }

    /**
     * Возвращает типы char.class, Character.class
     * @return типы char.class, Character.class
     */
    public static Class[] charTypes(){ return charTypes; }

    /**
     * Возвращает типы double.class, Double.class
     * @return типы double.class, Double.class
     */
    public static Class[] doubleTypes(){ return doubleTypes; }

    /**
     * Возвращает типы float.class, Float.class
     * @return типы float.class, Float.class
     */
    public static Class[] floatTypes(){ return floatTypes; }

    /**
     * Возвращает типы int.class, Integer.class
     * @return типы int.class, Integer.class
     */
    public static Class[] intTypes(){ return intTypes; }

    /**
     * Возвращает типы long.class, Long.class
     * @return типы long.class, Long.class
     */
    public static Class[] longTypes(){ return longTypes; }

    /**
     * Возвращает типы short.class, Short.class
     * @return типы short.class, Short.class
     */
    public static Class[] shortTypes(){ return shortTypes; }

    /**
     * Проверка что указанный класс являеться простым типом данных<br>
     * boolean, byte, char, double, float, int, long, void, short
     * @param c класс
     * @return true - является, false - не является
     */
    @SuppressWarnings({ "unused" })
    public static boolean isSimple(Class c)
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

    /**
     * Проверка что указанный класс может принимать null значения
     * @param c класс
     * @return true - может принимать null значения (Boxing) / false - примитив который не может быть ссылкой
     */
    @SuppressWarnings({ "RedundantIfStatement", "unused" })
    public static boolean isNullable( Class c){
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

    /**
     * Проверяет что указанный класс относится к void
     * @param c класс
     * @return true - является void / Void
     */
    public static boolean isVoid(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cVoid2.equals(c) || cVoid1.equals(c);
    }

    /**
     * Проверяет что указанный класс относится к boolean
     * @param c класс
     * @return true - является boolean / Boolean
     */
    public static boolean isBoolean(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cBool1.equals(c) || cBool2.equals(c);
    }

    /**
     * Проверяет что указанный класс относится к byte
     * @param c класс
     * @return true - является byte / Byte
     */
    public static boolean isByte(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cByte1.equals(c) || cByte2.equals(c);
    }

    /**
     * Проверяет что указанный класс относится к char
     * @param c класс
     * @return true - является char / Character
     */
    public static boolean isChar(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cChar1.equals(c) || cChar2.equals(c);
    }

    /**
     * Проверяет что указанный класс относится к double
     * @param c класс
     * @return true - является double / Double
     */
    public static boolean isDouble(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cDouble1.equals(c) || cDouble2.equals(c);
    }

    /**
     * Проверяет что указанный класс относится к float
     * @param c класс
     * @return true - является float / Float
     */
    public static boolean isFloat(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cFloat1.equals(c) || cFloat2.equals(c);
    }

    /**
     * Проверяет что указанный класс относится к double
     * @param c класс
     * @return true - является int / Integer
     */
    public static boolean isInt(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cInteger1.equals(c) || cInteger2.equals(c);
    }

    /**
     * Проверяет что указанный класс относится к long
     * @param c класс
     * @return true - является long / Long
     */
    public static boolean isLong(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cLong1.equals(c) || cLong2.equals(c);
    }

    /**
     * Проверяет что указанный класс относится к short
     * @param c класс
     * @return true - является short / Short
     */
    public static boolean isShort(Class c)
    {
        if (c == null) {
            throw new IllegalArgumentException("c == null");
        }
        return cShort1.equals(c) || cShort2.equals(c);
    }
}
