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

//import com.sun.org.apache.xalan.internal.utils.Objects;
import java.util.Objects;

/**
 * Математические операции над Float
 * @author Kamnev Georgiy
 */
public class FloatNumbers implements Numbers<Float>
{
    @Override
    public Float zero() {
        return 0f;
    }

    @Override
    public Float one() {
        return 1f;
    }

    @Override
    public boolean zero(Float n) {
        return n==null ? false : n==0f;
    }

    @Override
    public boolean undefined(Float n) {
        return n==null ? true : n.isNaN();
    }

    @Override
    public boolean infinity(Float n) {
        return n==null ? false : n.isInfinite();
    }

    @Override
    public Float add(Float a, Float b) {
        if( a==null || b==null )return Float.NaN;
        return a+b;
    }

    @Override
    public Float sub(Float a, Float b) {
        if( a==null || b==null )return Float.NaN;
        return a-b;
    }

    @Override
    public Float mul(Float a, Float b) {
        if( a==null || b==null )return Float.NaN;
        return a*b;
    }

    @Override
    public Float div(Float a, Float b) {
        if( a==null || b==null )return Float.NaN;
        if( b==0f )return Float.NaN;
        return a/b;
    }

    @Override
    public Float remainder(Float a, Float b) {
        if( a==null || b==null )return Float.NaN;
        if( b==0f )return Float.NaN;
        return a % b;
    }

    @Override
    public boolean equals(Float a, Float b) {
        return Objects.equals(a, b);
    }

    @Override
    public boolean more(Float a, Float b) {
        if( a==null && b==null )return false;
        if( a==null )return false;
        return a.compareTo(b) > 0;
    }

    @Override
    public boolean less(Float a, Float b) {
        if( a==null && b==null )return false;
        if( a==null )return true;
        return a.compareTo(b) < 0;
    }

    @Override
    public Float next(Float n) {
        return Float.NaN;
    }

    @Override
    public Float prev(Float n) {
        return Float.NaN;
    }
}
