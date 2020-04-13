/*
 * The MIT License
 *
 * Copyright 2018 user.
 *
 * Данная лицензия разрешает, безвозмездно, лицам, получившим копию данного программного
 * обеспечения и сопутствующей документации (в дальнейшем именуемыми "Программное Обеспечение"), 
 * использовать Программное Обеспечение без ограничений, включая неограниченное право на 
 * использование, копирование, изменение, объединение, публикацию, распространение, сублицензирование 
 * и/или продажу копий Программного Обеспечения, также как и лицам, которым предоставляется 
 * данное Программное Обеспечение, при соблюдении следующих условий:
 *
 * Вышеупомянутый копирайт и данные условия должны быть включены во все копии 
 * или значимые части данного Программного Обеспечения.
 *
 * ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ ПРЕДОСТАВЛЯЕТСЯ «КАК ЕСТЬ», БЕЗ ЛЮБОГО ВИДА ГАРАНТИЙ, 
 * ЯВНО ВЫРАЖЕННЫХ ИЛИ ПОДРАЗУМЕВАЕМЫХ, ВКЛЮЧАЯ, НО НЕ ОГРАНИЧИВАЯСЬ ГАРАНТИЯМИ ТОВАРНОЙ ПРИГОДНОСТИ, 
 * СООТВЕТСТВИЯ ПО ЕГО КОНКРЕТНОМУ НАЗНАЧЕНИЮ И НЕНАРУШЕНИЯ ПРАВ. НИ В КАКОМ СЛУЧАЕ АВТОРЫ 
 * ИЛИ ПРАВООБЛАДАТЕЛИ НЕ НЕСУТ ОТВЕТСТВЕННОСТИ ПО ИСКАМ О ВОЗМЕЩЕНИИ УЩЕРБА, УБЫТКОВ 
 * ИЛИ ДРУГИХ ТРЕБОВАНИЙ ПО ДЕЙСТВУЮЩИМ КОНТРАКТАМ, ДЕЛИКТАМ ИЛИ ИНОМУ, ВОЗНИКШИМ ИЗ, ИМЕЮЩИМ 
 * ПРИЧИНОЙ ИЛИ СВЯЗАННЫМ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ ИЛИ ИСПОЛЬЗОВАНИЕМ ПРОГРАММНОГО ОБЕСПЕЧЕНИЯ 
 * ИЛИ ИНЫМИ ДЕЙСТВИЯМИ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ.
 */

package xyz.cofe.num;

/**
 * Математические операции над Byte
 * @author Kamnev Georgiy
 */
public class ByteNumbers implements Numbers<Byte> {
    @Override
    public Byte zero() {
        return (Byte)(byte)0;
    }

    @Override
    public Byte one() {
        return (Byte)(byte)1;
    }

    @Override
    public boolean zero(Byte n) {
        if( n==null )return false;
        return n==0;
    }

    @Override
    public boolean undefined(Byte n) {
        return n==null;
    }

    @Override
    public boolean infinity(Byte n) {
        return false;
    }

    @Override
    public Byte add(Byte a, Byte b) {
        if( a==null || b==null )return null;
        return new Byte((byte)(a + b));
    }

    @Override
    public Byte sub(Byte a, Byte b) {
        if( a==null || b==null )return null;
        return new Byte((byte)(a - b));
    }

    @Override
    public Byte mul(Byte a, Byte b) {
        if( a==null || b==null )return null;
        return new Byte((byte)(a * b));
    }

    @Override
    public Byte div(Byte a, Byte b) {
        if( a==null || b==null )return null;
        if( b==0 )return null;
        return new Byte((byte)(a / b));
    }

    @Override
    public Byte remainder(Byte a, Byte b) {
        if( a==null || b==null )return null;
        if( b==0 )return null;
        return new Byte((byte)(a % b));
    }

    @Override
    public boolean equals(Byte a, Byte b) {
        if( a==null && b==null )return true;
        if( a==null )return false;
        return a.equals(b);
    }

    @Override
    public boolean more(Byte a, Byte b) {
        if( a==null && b==null )return false;
        if( a==null )return false;
        return a.compareTo(b) > 0;
    }

    @Override
    public boolean less(Byte a, Byte b) {
        if( a==null && b==null )return false;
        if( a==null )return true;
        return a.compareTo(b) < 0;
    }

    @Override
    public Byte next(Byte n) {
        if( n==null )return null;
        if( n.equals(Byte.MAX_VALUE) )return null;
        return (byte)(n + 1);            
    }

    @Override
    public Byte prev(Byte n) {
        if( n==null )return null;
        if( n.equals(Byte.MIN_VALUE) )return null;
        return (byte)(n - 1);            
    }
}
