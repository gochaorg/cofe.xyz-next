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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Математические операции над AtomicInteger
 * @author Kamnev Georgiy
 */
public class AtomicIntegerNumbers implements Numbers<AtomicInteger>{
    @Override
    public AtomicInteger zero() {
        return new AtomicInteger(0);
    }

    @Override
    public AtomicInteger one() {
        return new AtomicInteger(1);
    }

    @Override
    public boolean zero(AtomicInteger n) {
        return n==null ? false : n.get()==0;
    }

    @Override
    public boolean undefined(AtomicInteger n) {
        return n==null;
    }

    @Override
    public boolean infinity(AtomicInteger n) {
        return false;
    }

    @Override
    public AtomicInteger add(AtomicInteger a, AtomicInteger b) {
        if( a==null || b==null )return null;
        return new AtomicInteger( a.get() + b.get() );
    }

    @Override
    public AtomicInteger sub(AtomicInteger a, AtomicInteger b) {
        if( a==null || b==null )return null;
        return new AtomicInteger( a.get() - b.get() );
    }

    @Override
    public AtomicInteger mul(AtomicInteger a, AtomicInteger b) {
        if( a==null || b==null )return null;
        return new AtomicInteger( a.get() * b.get() );
    }

    @Override
    public AtomicInteger div(AtomicInteger a, AtomicInteger b) {
        if( a==null || b==null )return null;
        int bn = b.get();
        if( bn==0 )return null;
        return new AtomicInteger( a.get() / bn );
    }

    @Override
    public AtomicInteger remainder(AtomicInteger a, AtomicInteger b) {
        if( a==null || b==null )return null;
        int bn = b.get();
        if( bn==0 )return null;
        return new AtomicInteger( a.get() % bn );
    }

    @Override
    public boolean equals(AtomicInteger a, AtomicInteger b) {
        if( a==null && b==null )return true;
        if( a==null )return false;
        Integer ai = a.get();
        Integer bi = b.get();
        return ai.equals(bi);
    }

    @Override
    public boolean more(AtomicInteger a, AtomicInteger b) {
        if( a==null && b==null )return false;
        if( a==null )return false;
        Integer ai = a.get();
        Integer bi = b.get();
        return ai.compareTo(bi) > 0;
    }

    @Override
    public boolean less(AtomicInteger a, AtomicInteger b) {
        if( a==null && b==null )return false;
        if( a==null )return false;
        Integer ai = a.get();
        Integer bi = b.get();
        return ai.compareTo(bi) < 0;
    }

    @Override
    public AtomicInteger next(AtomicInteger n) {
        if( n==null )return null;
        Integer ni = n.get();
        if( ni==Integer.MAX_VALUE )return null;
        return new AtomicInteger(ni+1);
    }

    @Override
    public AtomicInteger prev(AtomicInteger n) {
        if( n==null )return null;
        Integer ni = n.get();
        if( ni==Integer.MIN_VALUE )return null;
        return new AtomicInteger(ni-1);
    }
}
