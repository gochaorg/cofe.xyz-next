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
package xyz.cofe.text;

import java.io.IOException;
import java.io.Writer;

/**
 * Поток вывода символов с поддержкой отступов
 * @author gocha
 */
public class IndentWriter extends PrefixWriter
{
    private int level = 0;
    private String singleIndentText = "   ";

    /**
     * Конструктор
     * @param delegateWriter Куда писать текст с отступом
     */
    public IndentWriter(Writer delegateWriter)
    {
        super(delegateWriter);
        if (delegateWriter == null) {
            throw new IllegalArgumentException("delegateWriter == null");
        }
    }

    /**
     * Возвращает текст отступа для одного уровня.
     * <p>По умолчанию - четыре пробела</p>
     * @return текст отступа
     */
    public String getIndent() {
        if( singleIndentText==null )singleIndentText="    ";
        return singleIndentText;
    }

    /**
     * Устанавливает текст отступа для одного уровня.
     * @param indent текст отступа, null - по умолчанию (четыре пробела)
     */
    public void setIndent(String indent) {
        if( indent==null )indent="    ";
        this.singleIndentText = indent;
    }

    /**
     * Возвращает текущий уровень отсутпа
     * @return уровень отступа
     */
    public int getLevel()
    {
        return level;
    }

    /**
     * Устанавливает уровень отступа
     * @param level уровень отступа
     */
    public void setLevel(int level)
    {
        if( level<0 )level = 0;
        if( this.level!=level )
        {
//            this.lastlevel = this.level;
            this.level = level;
        }
    }

    /**
     * Увличивает уровень отступа
     */
    public void incLevel()
    {
        setLevel(getLevel()+1);
    }

    /**
     * Уменьшает уровень отступа
     */
    public void decLevel()
    {
        setLevel(getLevel()-1);
    }

    @Override
    public void flush() throws IOException
    {
        this.writer.flush();
    }

    @Override
    public void close() throws IOException
    {
        this.writer.close();
    }

    @Override
    public String getLinePrefix() {
        String ind = getIndent();
        int l = getLevel();
        if( l>0 && ind!=null ){
            if( l==1 )return ind;
            return Text.repeat(ind, l);
        }
        return null;
    }
}