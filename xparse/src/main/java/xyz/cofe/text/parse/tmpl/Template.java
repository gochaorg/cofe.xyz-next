package xyz.cofe.text.parse.tmpl;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

/**
 * Функция генерации текста по шаблону
 */
public interface Template extends Function<Map,String> {
    /**
     * Генерация текста
     * @param args переменные шаблона
     * @return текст
     */
    String apply( Map args );

    /**
     * Парсинг шаблона.
     * @param tmpl Шаблон.
     *             <p>
     *             пример: <code>"some ${var}"</code>
     * @return функция генерации текста
     */
    public static Template parse( String tmpl ){
        if( tmpl==null ) throw new IllegalArgumentException("tmpl==null");
        ArrayList<Function<Map,String>> fns = new ArrayList<>();

        StringBuilder buff = new StringBuilder();
        String state = "begin";
        for( int ci=0;ci<tmpl.length();ci++ ){
            char c0 = tmpl.charAt(ci);
            char c1 = ci<tmpl.length()-1 ? tmpl.charAt(ci+1) : 0;
            switch( state ){
                case "begin":
                    switch( c0 ){
                        case '\\': state = "escape"; break;
                        case '$':
                            state = "var0";
                            if( buff.length()>0 ){
                                String res = buff.toString();
                                fns.add( args -> res );
                                buff.setLength(0);
                            }
                        break;
                        default: buff.append(c0); break;
                    }
                    break;
                case "escape":
                    buff.append(c0);
                    break;
                case "var0":
                    switch( c0 ){
                        case '{': state="var1"; break;
                        default: state="begin"; break;
                    }
                    break;
                case "var1":
                    switch( c0 ){
                        case '}':
                            String varRef = buff.toString();
                            buff.setLength(0);
                            state="begin";
                            fns.add( args -> args.getOrDefault(varRef,"").toString() );
                            break;
                        default: buff.append(c0); break;
                    }
                    break;
                default:
                    break;
            }
        }

        if( "begin".equals(state) && buff.length()>0 ){
            String res = buff.toString();
            fns.add( args -> res );
            buff.setLength(0);
        }

        return (args) -> {
            if( args==null )return "";
            StringBuilder sb = new StringBuilder();
            fns.forEach( fn -> sb.append(fn.apply(args)));
            return sb.toString();
        };
    }
}
