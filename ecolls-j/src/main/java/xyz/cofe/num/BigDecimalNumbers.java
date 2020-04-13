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

import java.math.BigDecimal;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Математические операции над BigDecimal
 * @author Kamnev Georgiy
 */
public class BigDecimalNumbers implements Numbers<BigDecimal> {

    @Override
    public BigDecimal zero() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal one() {
        return BigDecimal.ONE;
    }

    @Override
    public boolean zero(BigDecimal n) {
        return n==null ? false : n.equals(zero());
    }

    @Override
    public boolean undefined(BigDecimal n) {
        return n==null;
    }

    @Override
    public boolean infinity(BigDecimal n) {
        return false;
    }

    @Override
    public BigDecimal add(BigDecimal a, BigDecimal b) {
        if( a==null || b==null )return null;
        return a.add(b);
    }

    @Override
    public BigDecimal sub(BigDecimal a, BigDecimal b) {
        if( a==null || b==null )return null;
        return a.subtract(b);
    }

    @Override
    public BigDecimal mul(BigDecimal a, BigDecimal b) {
        if( a==null || b==null )return null;
        return a.multiply(b);
    }

    @Override
    public BigDecimal div(BigDecimal a, BigDecimal b) {
        if( a==null || b==null )return null;
        if( zero(b) )return null;
        return a.divide(b);
    }

    @Override
    public BigDecimal remainder(BigDecimal a, BigDecimal b) {
        if( a==null || b==null )return null;
        if( zero(b) )return null;
        return a.remainder(b);
    }

    @Override
    public boolean equals(BigDecimal a, BigDecimal b) {
        return Objects.equals(a, b);
    }

    @Override
    public boolean more(BigDecimal a, BigDecimal b) {
        if( a==null && b==null )return false;
        if( a==null )return false;
        return a.compareTo(b) > 0;
    }

    @Override
    public boolean less(BigDecimal a, BigDecimal b) {
        if( a==null && b==null )return false;
        if( a==null )return true;
        return a.compareTo(b) < 0;
    }

    @Override
    public BigDecimal next(BigDecimal n) {
        return null;
    }

    @Override
    public BigDecimal prev(BigDecimal n) {
        return null;
    }
}
