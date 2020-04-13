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

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Stack;

/**
 * Writer с поддержкой отступов и стека
 */
public class IndentStackWriter extends PrintWriter
{
    protected StackWriter stackWriter = null;
    protected IndentWriter indentWriter = null;
    public static class State
    {
        public int level = 0;
        public String endl = null;
        public String indent = null;
    }
    protected Stack<State> stack = new Stack<State>();

    public IndentStackWriter(Writer writer) {
        super(writer);
        stackWriter = new StackWriter(writer);
        indentWriter = new IndentWriter(stackWriter);
        out = indentWriter;
    }

    public void push() {
        stackWriter.push();
        State s = new State();
        s.endl = indentWriter.getEndl();
        s.indent = indentWriter.getIndent();
        s.level = indentWriter.getLevel();
        stack.push(s);
        indentWriter.setLevel(0);
    }

    public String pop() {
        String res = stackWriter.pop();
        if( stack.size()>0 ){
            State s = stack.pop();
            indentWriter.setLevel(s.level);
            indentWriter.setEndl(s.endl);
            indentWriter.setIndent(s.indent);
        }
        return res;
    }

    public void setLevel(int level) {
        indentWriter.setLevel(level);
    }

    public int getLevel() {
        return indentWriter.getLevel();
    }

    public void setIndent(String indent) {
        indentWriter.setIndent(indent);
    }

    public String getIndent() {
        return indentWriter.getIndent();
    }

    public void incLevel() {
        indentWriter.incLevel();
    }

    public String getEndl() {
        return indentWriter.getEndl();
    }

    public void setEndl(String endl) {
        indentWriter.setEndl(endl);
    }

    public void setEndLine(EndLine endline) {
        indentWriter.setEndLine(endline);
    }

    public EndLine getEndLine() {
        return indentWriter.getEndLine();
    }

    public void decLevel() {
        indentWriter.decLevel();
    }

    @Override
    public void close() {
        super.close();
        stack.clear();
    }

//	public void template(String template,Object ... values){
//		if (template== null) {
//			throw new IllegalArgumentException("template==null");
//		}
//		if (values== null) {
//			throw new IllegalArgumentException("values==null");
//		}
//		String text = Text.template(template, values);
//		this.print(text);
//	}
//	public void templateln(String template,Object ... values){
//		if (template== null) {
//			throw new IllegalArgumentException("template==null");
//		}
//		if (values== null) {
//			throw new IllegalArgumentException("values==null");
//		}
//		String text = Text.template(template, values);
//		this.println(text);
//	}
//
//	public void template(String template,Convertor<String,String> values){
//		if (template== null) {
//			throw new IllegalArgumentException("template==null");
//		}
//		if (values== null) {
//			throw new IllegalArgumentException("values==null");
//		}
//		String text = Text.template(template, values);
//		this.print(text);
//	}
//	public void templateln(String template,Convertor<String,String> values){
//		if (template== null) {
//			throw new IllegalArgumentException("template==null");
//		}
//		if (values== null) {
//			throw new IllegalArgumentException("values==null");
//		}
//		String text = Text.template(template, values);
//		this.println(text);
//	}
//
//	public void template(String template,Map<String,String> values){
//		if (template== null) {
//			throw new IllegalArgumentException("template==null");
//		}
//		if (values== null) {
//			throw new IllegalArgumentException("values==null");
//		}
//		String text = Text.template(template, values);
//		this.print(text);
//	}
//	public void templateln(String template,Map<String,String> values){
//		if (template== null) {
//			throw new IllegalArgumentException("template==null");
//		}
//		if (values== null) {
//			throw new IllegalArgumentException("values==null");
//		}
//		String text = Text.template(template, values);
//		this.println(text);
//	}
}
