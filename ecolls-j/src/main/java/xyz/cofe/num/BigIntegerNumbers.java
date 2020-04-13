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

import java.math.BigInteger;

/**
 * Математические операции над BigInteger
 * @author Kamnev Georgiy
 */
public class BigIntegerNumbers implements Numbers<BigInteger>
{
    @Override
    public BigInteger zero() {
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger one() {
        return BigInteger.ONE;
    }

    @Override
    public boolean zero(BigInteger n) {
        return n==null ? false : n.equals(BigInteger.ZERO);
    }

    @Override
    public boolean undefined(BigInteger n) {
        return n==null;
    }

    @Override
    public boolean infinity(BigInteger n) {        
        return false;
    }

    @Override
    public BigInteger add(BigInteger a, BigInteger b) {
        if( a==null || b==null )return null;
        return a.add(b);
    }

    @Override
    public BigInteger sub(BigInteger a, BigInteger b) {
        if( a==null || b==null )return null;
        return a.subtract(b);
    }

    @Override
    public BigInteger mul(BigInteger a, BigInteger b) {
        if( a==null || b==null )return null;
        return a.multiply(b);
    }

    @Override
    public BigInteger div(BigInteger a, BigInteger b) {
        if( a==null || b==null )return null;
        if( zero(b) )return null;
        return a.divide(b);
    }

    @Override
    public BigInteger remainder(BigInteger a, BigInteger b) {
        if( a==null || b==null )return null;
        if( zero(b) )return null;
        return a.remainder(b);
    }

    @Override
    public boolean equals(BigInteger a, BigInteger b) {
        if( a==null && b==null )return true;
        if( a==null )return false;
        return a.equals(b);
    }

    @Override
    public boolean more(BigInteger a, BigInteger b) {
        if( a==null && b==null )return false;
        if( a==null )return false;
        return a.compareTo(b) > 0;
    }

    @Override
    public boolean less(BigInteger a, BigInteger b) {
        if( a==null && b==null )return false;
        if( a==null )return true;
        return a.compareTo(b) < 0;
    }

    @Override
    public BigInteger next(BigInteger n) {
        return n==null ? null : add(n,one());
    }

    @Override
    public BigInteger prev(BigInteger n) {
        return n==null ? null : sub(n,one());
    }
}
