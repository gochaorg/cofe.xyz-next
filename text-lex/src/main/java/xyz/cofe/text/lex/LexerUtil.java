package xyz.cofe.text.lex;

import xyz.cofe.ecolls.Predicates;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Лексер исползующий список анализоров тект. лексем
 * @author gocha
 */
public class LexerUtil
{
    /**
     * Фильтрует токены в списке.
     * @param toks токены
     * @param whiteSpaceTokens список игнорируемых токенов
     * @return Очищенный список
     */
    public static List<Token> filter( List<Token> toks, Class ... whiteSpaceTokens){
        if (toks== null) {
            throw new IllegalArgumentException("toks==null");
        }

//		List<Token> removed = new ArrayList<Token>();
//		Stack<Integer> r = new Stack<Integer>();
//		for(Integer i=0;i<toks.size();i++){
//			Token t = toks.get(i);
//			if( t==null ){
//				r.push(i);
//			}else{
//				Class cls = t.getClass();
//				for( Class wsC : whiteSpaceTokens ){
//					if( cls.equals(wsC) ){
//						r.push(i);
//					}
//				}
//			}
//		}
//		while(!r.empty()){
//			int i = r.pop();
//			removed.add(toks.get(i));
//			toks.remove(i);
//		}
//
//		return removed;
        List<Token> res = new ArrayList<Token>();
        for( int i=0; i<toks.size(); i++ ){
            Token o = toks.get(i);
            if( o!=null ){
                boolean skip = false;
                for( Class c : whiteSpaceTokens ){
                    if( o.getClass().equals(c) ){
                        skip = true;
                        break;
                    }
                }
                if( !skip )res.add(o);
            }else{
                res.add(o);
            }
        }
        return res;
    }

    /**
     * Возвращает символ в указанной позиции текста
     * @param str Исходный текст
     * @param off Смещение в тексте
     * @param nullc Возвращаемый символ если превышены границы текста
     * @return Символ
     */
    public static char lookup(String str,int off,char nullc){
        if( str==null )return nullc;
        if( off<0 )return nullc;
        if( str.length()==0 )return nullc;
        if( off >= str.length() )return nullc;
        return str.charAt(off);
    }

    /**
     * Возвращает кол-во символов которые совпадают с предикатаом
     * @param src текст
     * @param offset позиция проверяемого символа
     * @param charPred предикат проверки символа
     * @return кол-во совпадений
     */
    public static int isRepeat(String src,int offset,Predicate<Character> charPred){
        char nullc = (char)(0);
        int co = 0;
        while( true ){
            char c = lookup(src, offset+co, nullc);
            if( c==nullc )break;
            if( charPred.test(c) ){
                co++;
            }else{
                break;
            }
        }
        return co;
    }

    /**
     * Проверяет последовательность символов в соответ с предикатами.<br>
     * Пример: <br>
     * Последовательность предикатов: letter, letter, digit<br>
     * Последовательность символов: <code>AB123CD</code>, проверит три символаи и в данном
     * случаи вернте true.<br>
     * Последовательность символов: <code>1B123CD</code>, проверит три символаи и в данном
     * случаи вернте false.<br>
     * @param src Последовательность символов
     * @param offset Смещение в последовательности символов (с какого начать, 0 - начало)
     * @param charPreds Последовательность предикатов
     * @return true - совпало
     */
    public static boolean match(String src,int offset,Predicate<Character> ... charPreds){
        if( charPreds==null )return false;
        if( src==null )return false;

        if( offset>=src.length() )return false;
        if( offset<0 )return false;

        if( charPreds.length > (src.length() - offset) )return false;
        if( charPreds.length==0 )return true;

        for( int i=0; i<charPreds.length; i++ ){
            Predicate<Character> charPred = charPreds[i];
            char nullc = (char)0;
            char cs = lookup(src, i+offset, nullc);
            if( !charPred.test(cs) )return false;
        }
        return true;
    }

    /**
     * Проверяет текст на совпадение
     * @param text Искомый текст
     * @param src Исходный текст
     * @param offset Смещение в исходном тексте
     * @param ignoreCase Игнорировать регистр букв
     * @return true - Совпал; false - не совпал
     */
    public static boolean match(String src, int offset, boolean ignoreCase, String text){
        if( text==null )return false;
        if( src==null )return false;
        if( offset>=src.length() )return false;
        if( offset<0 )return false;
        if( text.length()>(src.length()-offset) )return false;
        if( text.length()==0 )return true;
        for( int i=0; i<text.length(); i++ ){
            char ct = text.charAt(i);
            char cs = lookup(src, i+offset, ct);
            if( ignoreCase ){
                if( Character.isLetter(ct) && Character.isLetter(cs) ){
                    ct = Character.toLowerCase(ct);
                    cs = Character.toLowerCase(cs);
                    if( ct!=cs )return false;
                }else{
                    if( ct!=cs )return false;
                }
            }else{
                if( ct!=cs )return false;
            }
        }
        return true;
    }

    public static Predicate<Character>[] array(Predicate<Character> ... chr){
        return chr;
    }

    /**
     * Присоединяет к массиву еще массив предикатов
     * @param src Исходный массив
     * @param chr Присоединяемый массив
     * @return Склееный массив
     */
    public static Predicate<Character>[] append(Predicate<Character>[] src,Predicate<Character> ... chr){
        if( src==null && chr==null )return new Predicate[]{};
        if( src==null && chr!=null )return chr;
        if( src!=null && chr==null )return src;
        Predicate[] res = new Predicate[src.length + chr.length];
        System.arraycopy(src, 0, res, 0, src.length);
        System.arraycopy(chr, 0, res, src.length, chr.length);
        return res;
    }

    public static final Predicate<Character> not(Predicate<Character> pred){
        if( pred==null )throw new IllegalArgumentException("pred==null");
        final Predicate<Character> f = pred;
        return value->{
            boolean r = f.test(value);
            return !r;
        };
    }

    /**
     * Предикат - это буква
     */
    public static final Predicate<Character> isLetter = value->{
        if( value==null )return false;
        return Character.isLetter(value);
    };

    /**
     * Предикат - это цифра
     */
    public static final Predicate<Character> isDigit = value->value==null ? false : Character.isDigit(value);

    /**
     * Создает предикат, символ находится в указанном списке
     * @param listOfChars Список символов
     * @return Предикат
     */
    public static Predicate<Character> isInList(String listOfChars){
        final String chars = listOfChars;
        return value->{
            if( value==null )return false;
            if( chars==null || chars.length()==0 )return false;
            int i=0;
            for( i=0; i<chars.length(); i++ ){
                char c = chars.charAt(i);
                if( value.equals(c) )return true;
            }
            return false;
        };
    }

    /**
     * Создает предикат, символ находится в указанном списке
     * @param listOfChars Список символов
     * @return Предикаты
     */
    public static Predicate<Character> isInList(char ... listOfChars){
        final char[] chars = listOfChars;
        return value->{
            if( value==null )return false;
            for( char c : chars ){
                if( value.equals(c) )return true;
            }
            return false;
        };
    }

    /**
     * Создает предикат ИЛИ
     * @param p Список предикатов
     * @return Предикаты
     */
    public static Predicate<Character> or( Predicate<Character> ... p){
        return Predicates.or(p);
    }
}
