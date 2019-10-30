package xyz.cofe.text.parse.tmpl;

import java.util.HashMap;

/**
 * Функция генерации текста по шаблону с двумя переменными in, out
 */
public class InOutTemplate {
    private final Template template;

    /**
     * Конструктор
     * @param template функция генерации
     */
    public InOutTemplate(Template template){
        if( template==null ) throw new IllegalArgumentException("exprTemplate==null");
        this.template = template;
    }

    /**
     * Парсинг шаблона
     * @param template шаблон
     * @return функция генерации текста
     */
    public static InOutTemplate parse( String template ){
        if( template==null ) throw new IllegalArgumentException("exprTemplate==null");
        return new InOutTemplate(Template.parse(template));
    }

    /**
     * Генерация текста
     * @param in значение переменной шаблона in
     * @param out значение переменной шаблона out
     * @return текст
     */
    public String apply( Object in, Object out ){
        HashMap map = new HashMap();
        map.put("in",in);
        map.put("out",out);
        return template.apply(map);
    }
}
