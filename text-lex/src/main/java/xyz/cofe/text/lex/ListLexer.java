package xyz.cofe.text.lex;

import xyz.cofe.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Реализация Lexer как контейнера распознователей (TokenParser)
 * @author gocha
 */
public class ListLexer implements Lexer
{
    /**
     * Конструктор по умолчанию
     */
    public ListLexer(){
    }

    /**
     * Разпознователи (TokenParser)
     */
    protected List<TokenParser> parsers = new ArrayList<TokenParser>();

    /**
     * Возвращает список разпознователей (TokenParser)
     * @return Парсеры
     */
    public List<TokenParser> getParsers() {
        return parsers;
    }

    /**
     * Разпознает цепочку символов последовательно перебирая из списка разпознователей
     * @param source Исходня цепочка символов
     * @return Токены
     */
    public List<Token> parse( String source){
        return parse(source, null);
    }

    /**
     * Разпознает цепочку символов последовательно перебирая из списка разпознователей
     * @param source Исходня цепочка символов
     * @param errorReciver Приемник ошибок
     */
    @Override
    public List<Token> parse( String source, Consumer<String> errorReciver) {
        if (source== null) {
            throw new IllegalArgumentException("source==null");
        }

        List<Token> tokens = new ArrayList<Token>();

        if( source.length()==0 ){
            return tokens;
        }

        int offset = 0;
        while(true){
            if( offset>=source.length() )break;

            Token tok = null;
            TokenParser lastParser = null;
            for( TokenParser tparser : getParsers() ){
                if( tparser==null )continue;
                lastParser = tparser;
                tok = tparser.parse(source, offset);
                if( tok!=null )break;
            }
            if( tok==null ){
                if( errorReciver!=null ){
                    StringBuilder sb = new StringBuilder();
                    sb.append("Не найден подходящий парсер, позиция в тексте = ");
                    sb.append(offset);
                    if( offset>0 ){
                        int o = offset-5;
                        if( o<0 )o=0;
                        sb.append( "\n" );
                        sb.append( "Текст перед позицией: ... " );
                        sb.append( Text.encodeStringConstant( source.substring(o,offset) ) );

                        sb.append( "\n" );
                        sb.append( "Текст после позиции: " );
                        String tafter = source.substring(offset);
                        if( tafter.length()>50 )tafter = tafter.substring(0,50);
                        sb.append(Text.encodeStringConstant(tafter));
                    }else{
                        sb.append( "\n" );
                        sb.append( "Текст после позиции: " );
                        String tafter = source.substring(offset);
                        if( tafter.length()>50 )tafter = tafter.substring(0,50);
                        sb.append(Text.encodeStringConstant(tafter));
                    }

                    errorReciver.accept(sb.toString());
                }
                break;
            }

            int l = tok.getLength();
            if( l<=0 ){
                if( errorReciver!=null )
                    errorReciver.accept(
                        "Нуленвая длина лексемы = "+tok.getClass().getName()+
                            ", позиция в тексте = "+offset+
                            " парсер="+lastParser);
                break;
            }

            tokens.add(tok);
            offset += l;
        }

        return tokens;
    }
}
