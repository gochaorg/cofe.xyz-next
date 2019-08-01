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
 * Математические операции над Long
 * @author Kamnev Georgiy
 */
public class LongNumbers implements Numbers<Long>
{

    @Override
    public Long zero() {
        return 0L;
    }

    @Override
    public Long one() {
        return 1L;
    }

    @Override
    public boolean zero(Long n) {
        if( n==null )return false;
        return n == 0L;
    }

    @Override
    public boolean undefined(Long n) {
        if( n==null )return true;
        return false;
    }

    @Override
    public boolean infinity(Long n) {
        return false;
    }

    @Override
    public Long add(Long a, Long b) {
        if( a==null || b==null )return null;
        return a+b;
    }

    @Override
    public Long sub(Long a, Long b) {
        if( a==null || b==null )return null;
        return a-b;
    }

    @Override
    public Long mul(Long a, Long b) {
        if( a==null || b==null )return null;
        return a*b;
    }

    @Override
    public Long div(Long a, Long b) {
        if( a==null || b==null )return null;
        if( b==0L )return null;
        return a / b;
    }

    @Override
    public Long remainder(Long a, Long b) {
        if( a==null || b==null )return null;
        if( b==0L )return null;
        return a % b;
    }

    @Override
    public boolean equals(Long a, Long b) {
        return Objects.equals(a, b);
    }

    @Override
    public boolean more(Long a, Long b) {
        if( a==null && b==null )return false;
        if( a==null )return false;
        return a.compareTo(b) > 0;
    }

    @Override
    public boolean less(Long a, Long b) {
        if( a==null && b==null )return false;
        if( a==null )return true;
        return a.compareTo(b) < 0;
    }

    @Override
    public Long next(Long n) {
        if( n==null )return null;
        if( n==Long.MAX_VALUE )return null;
        return n+1;
    }

    @Override
    public Long prev(Long n) {
        if( n==null )return null;
        if( n==Long.MIN_VALUE )return null;
        return n-1;
    }
}
