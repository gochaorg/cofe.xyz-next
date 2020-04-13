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
 * Математические операции над Number
 * @author Kamnev Georgiy
 */
public class SingleBase {
    protected Numbers<Number> numbers;
    protected Number a;

    public SingleBase(Numbers numbers, Number a) {
        if( numbers==null )throw new IllegalArgumentException("numbers == null");
        if( a==null )throw new IllegalArgumentException("a == null");
        this.numbers = numbers;
        this.a = a;
    }

    public Numbers<Number> getNumbers() {
        return numbers;
    }

    public Number getA() {
        return a;
    }

    public Number zero() {
        return numbers.zero();
    }

    public Number one() {
        return numbers.one();
    }

    public boolean zero(Number n) {
        return numbers.zero(n);
    }

    public boolean undefined(Number n) {
        return numbers.undefined(n);
    }

    public boolean infinity(Number n) {
        return numbers.infinity(n);
    }

    public Number add(Number a, Number b) {
        return numbers.add(a, b);
    }

    public Number sub(Number a, Number b) {
        return numbers.sub(a, b);
    }

    public Number mul(Number a, Number b) {
        return numbers.mul(a, b);
    }

    public Number div(Number a, Number b) {
        return numbers.div(a, b);
    }

    public Number remainder(Number a, Number b) {
        return numbers.remainder(a, b);
    }

    public boolean equals(Number a, Number b) {
        return numbers.equals(a, b);
    }

    public boolean more(Number a, Number b) {
        return numbers.more(a, b);
    }

    public boolean less(Number a, Number b) {
        return numbers.less(a, b);
    }

    public Number next(Number n) {
        return numbers.next(n);
    }

    public Number prev(Number n) {
        return numbers.prev(n);
    }
    
    
}
