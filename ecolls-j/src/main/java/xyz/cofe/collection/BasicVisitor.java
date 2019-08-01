/*
 * The MIT License
 *
 * Copyright 2014 Kamnev Georgiy (nt.gocha@gmail.com).
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
package xyz.cofe.collection;

/**
 * Визитер, простая реализация
 * @author gocha
 */
public class BasicVisitor<T> implements Visitor<T>
{
    /* (non-Javadoc)
     * @see Visitor
     */
    @Override
    public boolean enter(T obj)
    {
        return true;
    }

    /* (non-Javadoc)
     * @see Visitor
     */
    @Override
    public void exit(T obj)
    {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Обходит дерево
     * @param <T> Тип узла дерева
     * @param visitor Визитер
     * @param start Начальный узел
     * @param extracter Дочерние узлы
     */
    public static <T> void visit(Visitor<T> visitor, T start,NodesExtracter<T,T> extracter){
        if (visitor == null) {
            throw new IllegalArgumentException("visitor == null");
        }
        if (extracter == null) {
            throw new IllegalArgumentException("extracter == null");
        }

        boolean res = visitor.enter(start);
        if( res ){
            Iterable<T> children =  extracter.extract(start);
            if( children!=null ){
                for( T n : children ){
                    visit(visitor, n, extracter);
                }
            }
        }
        visitor.exit(start);
    }
}
