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

import java.util.Objects;

/**
 * Операции над short (16ти битными) числами
 * @author Kamnev Georgiy
 */
public class ShortNumbers implements Numbers<Short>{

    @Override
    public Short zero() {
        return (short)0;
    }

    @Override
    public Short one() {
        return (short)1;
    }

    @Override
    public boolean zero(Short n) {
        if( n==null )return false;
        return n == 0;
    }

    @Override
    public boolean undefined(Short n) {
        if( n==null )return true;
        return false;
    }

    @Override
    public boolean infinity(Short n) {
        return false;
    }

    @Override
    public Short add(Short a, Short b) {
        if( a==null || b==null )return null;
        return (short)(a + b);
    }

    @Override
    public Short sub(Short a, Short b) {
        if( a==null || b==null )return null;
        return (short)(a - b);
    }

    @Override
    public Short mul(Short a, Short b) {
        if( a==null || b==null )return null;
        return (short)(a * b);
    }

    @Override
    public Short div(Short a, Short b) {
        if( a==null || b==null )return null;
        if( b==0 )return null;
        return (short)(a / b);
    }

    @Override
    public Short remainder(Short a, Short b) {
        if( a==null || b==null )return null;
        if( b==0 )return null;
        return (short)(a % b);
    }

    @Override
    public boolean equals(Short a, Short b) {
        if( a==null && b==null )return true;
        if( a==null )return false;
        return Objects.equals(a, b);
    }

    @Override
    public boolean more(Short a, Short b) {
        if( a==null && b==null )return false;
        if( a==null )return false;
        return a.compareTo(b) > 0;
    }

    @Override
    public boolean less(Short a, Short b) {
        if( a==null && b==null )return false;
        if( a==null )return true;
        return a.compareTo(b) < 0;
    }

    @Override
    public Short next(Short n) {
        if( n==null )return null;
        if( n==Short.MAX_VALUE )return null;
        return (short)(n+1);
    }

    @Override
    public Short prev(Short n) {
        if( n==null )return null;
        if( n==Short.MIN_VALUE )return null;
        return (short)(n-1);
    }
}
