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
import java.io.StringWriter;
import java.io.Writer;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Writer с поддержкой стековой памяти
 * @author gocha
 */
public class StackWriter extends Writer
{
    /**
     * Текущий writer
     */
    protected Writer output = null;

    /**
     * Стек памяти writer-ов
     */
    protected Stack<Writer> stack = new Stack<Writer>();

    /**
     * Конструктор
     * @param output куда производить вывод
     */
    public StackWriter(Writer output){
        if (output== null) {
            throw new IllegalArgumentException("output==null");
        }
        this.output = output;
    }

    /**
     * Указывает стек памяти writer-ов
     * @return Стек памяти writer-ов
     */
    public Stack<Writer> getStack(){ return stack; }

    /**
     * Сохраняет в стеке текущий writer. Создает новый writer в памяти и устанавливает как текущий.
     */
    public void push() {
        stack.push(output);
        output = new StringWriter();
    }

    /**
     * Извлекает из текущего writer-а что было напечатано, восстанавливает из стека текущий writer.
     * Что было в предыдущем writer-е НЕ помещается в текущий writer.
     * @return Что было напечатано от предыдущего вызова push();
     */
    public String pop() {
        String res = "";
        if( output instanceof StringWriter ){
            try {
                StringWriter s = (StringWriter)output;
                s.flush();
                res = s.toString();
                s.close();
            } catch (IOException ex) {
                Logger.getLogger(StackWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if( stack.size()>0 ){
            output = stack.pop();
        }
        return res;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        output.write(cbuf, off, len);
    }

    /**
     * Скидывает содежимое всего стека в основной writer, в той же последовательности что и соот. вызовы write(...)
     * @throws IOException Ошибка IO
     */
    @Override
    public void flush() throws IOException {
        output.flush();
        if( stack.size()>0 ){
            Writer w = output;
            for( int i=stack.size()-1; i>=0; i-- ){
                Writer top = stack.get(i);
                if( w instanceof StringWriter ){
                    StringWriter sw = (StringWriter)w;
                    sw.flush();
                    String buff = sw.toString();
                    top.write(buff);

                    sw.getBuffer().setLength(0);
                    sw.getBuffer().trimToSize();
                }
                w = top;
            }
        }
    }

    /**
     * Вызывает flush(), а затем в обратной последовательности close() для стека writer-ов.
     * После чего удаляет содержимое стека.
     * @throws IOException Ошибка IO
     */
    @Override
    public void close() throws IOException {
        flush();

        output.close();
        if( stack.size()>0 ){
            for( int i=stack.size()-1; i>=0; i-- ){
                Writer top = stack.get(i);
                top.close();
            }
            output = stack.get(0);
            stack.clear();
        }
    }
}
