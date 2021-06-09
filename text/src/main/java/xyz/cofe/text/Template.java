package xyz.cofe.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Простые текстовые шаблоны.
 *
 * <p>
 *   Шаблон задается в виде строки с переменными, пример: <code>"hello ${text}"</code>
 * </p>
 *
 * <p>
 * В дальнейшем этот шаблон можно использовать, подставляя на место переменной реальные значения.
 * </p>
 *
 * Пример:
 *
 * <pre>
 *     public static class Item {
 *         public final int a;
 *         public final int b;
 *
 *         public Item(int a, int b) {
 *             this.a = a;
 *             this.b = b;
 *         }
 *     }
 *
 *     public void test03(){
 *         var str = Template
 *             .parse("a=${a} b=${b}",Item.class)
 *             .bind("a", v-&gt;""+v.a )
 *             .bind("b", v-&gt;""+v.b )
 *             .eval(new Item(1,3));
 *
 *         System.out.println(str);
 *         // будет выведено: a=1 b=3
 *     }
 * </pre>
 */
public class Template<V> {
    /**
     * Парсинг шаблонов
     * @param template шаблон
     * @param text функция интерпретации текста
     * @param code функция интерпретации переменной/кода
     * @param <A> тип результата интерпретации фрагмента
     * @return фрагменты
     */
    public static <A> List<A> parse(String template, Function<String,A> text, Function<String,A> code){
        if( template==null )throw new IllegalArgumentException( "template==null" );
        if( text==null )throw new IllegalArgumentException( "text==null" );
        if( code==null )throw new IllegalArgumentException( "code==null" );

        int state = 0;
        List<A> list = new ArrayList<>();

        StringBuilder buff = new StringBuilder();
        int[] level = new int[]{0};
        Runnable switch2code = () -> {
            if( buff.length()>0 ){
                list.add( text.apply(buff.toString()) );
            }
            buff.setLength(0);
        };
        Runnable switch2text = () -> {
            if( buff.length()>0 ){
                list.add( code.apply(buff.toString()) );
            }
            buff.setLength(0);
        };

        for( int ci=0;ci<template.length();ci++ ){
            char c = template.charAt(ci);
            switch (state) {
                case 0:
                    switch (c) {
                        case '$':
                            state = 1;
                            break;
                        case '\\':
                            state = 2;
                            break;
                        default:
                            buff.append(c);
                            break;
                    }
                    break;
                case 1:
                    switch (c){
                        case '{':
                            state = 50;
                            switch2code.run();
                            level[0] = 1;
                            break;
                        default:
                            buff.append("$").append(c);
                            state = 0;
                            break;
                    }
                    break;
                case 2:
                    state = 0;
                    buff.append(c);
                    break;
                case 50:
                    switch (c) {
                        case '{':
                            level[0]++;
                            buff.append(c);
                            break;
                        case '}':
                            level[0]--;
                            if( level[0]<=0 ) {
                                switch2text.run();
                                state = 0;
                            }else{
                                buff.append(c);
                            }
                            break;
                        default:
                            buff.append(c);
                    }
                    break;
            }
        }
        if( buff.length()>0 ){
            if( state==50 ){
                switch2text.run();
            }else{
                switch2code.run();
            }
        }

        return list;
    }

    /**
     * Конструктор
     * @param items элементы шаблона
     */
    public Template(List<Item<V>> items){
        if( items==null )throw new IllegalArgumentException( "items==null" );
        ArrayList<Item<V>> itms = new ArrayList<>();
        int idx = -1;
        for( Item<V> itm : items ){
            idx++;
            if( itm==null )throw new IllegalArgumentException("items["+idx+"]==null");
            itms.add(itm);
        }
        this.items = Collections.unmodifiableList(itms);
    }

    /**
     * Элементы шаблона
     */
    protected final List<Item<V>> items;

    /**
     * Возвращает элементы шаблона
     * @return Элементы шаблона
     */
    public List<Item<V>> items(){
        return items;
    }

    /**
     * Возвращает текст шаблона
     * @return текст шаблона
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for( Item<V> itm : items() ){
            if( itm==null ){
                sb.append("null");
            }else {
                if( itm instanceof CodeItem ){
                    sb.append("${").append(itm.text).append("}");
                }else{
                    sb.append(itm.text);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Парсинг шаблона
     * @param template  шаблон
     * @param valueType тип значения
     * @param <V> тип значения
     * @return шаблон
     */
    public static <V> Template<V> parse(String template, Class<V> valueType){
        if( template==null )throw new IllegalArgumentException( "template==null" );
        return new Template<>(
            parse(template, TextItem::new, CodeItem::new)
        );
    }

    /**
     * Парсинг шаблона
     * @param template шаблон
     * @param <V> тип значения
     * @return шаблон
     */
    public static <V> Template<V> parse(String template){
        if( template==null )throw new IllegalArgumentException( "template==null" );
        return new Template<>(
            parse(template, TextItem::new, CodeItem::new)
        );
    }

    /**
     * Фрагмент шаблона
     * @param <V> тип значения передаваемое в шаблон
     */
    public abstract static class Item<V> {
        public final String text;
        public Item(String text){
            this.text = text;
        }
        public abstract String eval(V value);
    }

    /**
     * Фрагмент шаблона - обычный текст
     * @param <V> тип значения передаваемое в шаблон
     */
    public static class TextItem<V> extends Item<V> {
        public TextItem(String text) {
            super(text);
        }

        @Override
        public String eval(V value) {
            return text;
        }
    }

    /**
     * Фрагмент шаблона - переменная которая еще не связана с функций
     * @param <V> тип значения передаваемое в шаблон
     */
    public static class CodeItem<V> extends Item<V> {
        public CodeItem(String text) {
            super(text);
        }

        @Override
        public String eval(V value) {
            return "";
        }
    }

    /**
     * Фрагмент шаблона - переменная которая уже связана с функций
     * @param <V> тип значения передаваемое в шаблон
     */
    public static class BindedCodeItem<V> extends CodeItem<V> {
        /**
         * Функция интерпретатор значения переменной
         */
        public final Function<V,String> interpret;

        /**
         * Конструктор
         * @param text исходный текст
         * @param interpret функция интерпретации
         */
        public BindedCodeItem(String text, Function<V,String> interpret) {
            super(text);
            if( interpret==null )throw new IllegalArgumentException( "code==null" );
            this.interpret = interpret;
        }

        /**
         * Конструктор
         * @param text исходный текст
         * @param interpret функция интерпретации
         */
        public BindedCodeItem(String text, Supplier<String> interpret) {
            super(text);
            if( interpret==null )throw new IllegalArgumentException( "code==null" );
            this.interpret = x -> interpret.get();
        }

        @Override
        public String eval(V value) {
            return interpret.apply(value);
        }
    }

    /**
     * Клонирует и привязывает наименование переменной в шаблоне с функцией генератором значения.
     * <br/>
     * Ранее связанные переменные не переопределяются.
     * @param binder функция связывающая шаблон с значением
     * @return новый шаблон
     */
    public Template<V> binder(Function<String, Optional<Function<V,String>>> binder){
        if( binder==null )throw new IllegalArgumentException( "binder==null" );
        return new Template<>( items.stream().map( itm -> {
            if( itm instanceof CodeItem && !(itm instanceof BindedCodeItem) ){
                return binder.apply( itm.text ).map( f -> (Item<V>)new BindedCodeItem<V>(itm.text, f) ).orElse( itm );
            }
            return itm;
        }).collect(Collectors.toList())
        );
    }

    /**
     * Клонирует и привязывает наименование переменной в шаблоне с функцией генератором значения.
     * <br/>
     * Ранее связанные переменные не переопределяются.
     * @param code наименование переменной
     * @param evalFun функция связывающая шаблон с значением
     * @return новый шаблон
     * @see #binder(Function)
     */
    public Template<V> bind(String code,Function<V,String> evalFun){
        if( code==null )throw new IllegalArgumentException( "code==null" );
        if( evalFun==null )throw new IllegalArgumentException( "evalFun==null" );
        return binder(c -> {
            if( code.equals(c) )return Optional.of(evalFun);
            return Optional.empty();
        });
    }

    /**
     * Интерпретация шаблона
     * @param value значение подставляемое в шаблон
     * @return текстовое представление
     */
    public String eval( V value ){
        StringBuilder sb = new StringBuilder();
        for( Item<V> itm : items() ){
            if( itm==null ){
                sb.append("null");
                continue;
            }

            String s = itm.eval(value);
            sb.append(s);
        }
        return sb.toString();
    }
}