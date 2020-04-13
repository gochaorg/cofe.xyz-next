package xyz.cofe.text.lex;

import xyz.cofe.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Парсер лексемы AnyChar - любого текстового символа
 * @see AnyChar
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class AnyCharParser implements TokenParser {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(AnyCharParser.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(AnyCharParser.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(AnyCharParser.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(AnyCharParser.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(AnyCharParser.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(AnyCharParser.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(AnyCharParser.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public final List<String> stopList;

    public AnyCharParser(){
        this.stopList = new ArrayList<>();
    }

    public AnyCharParser(String ... stopList){
        this.stopList = new ArrayList<String>();
        this.stopList.addAll( Arrays.asList(stopList) );
    }

    @Override
    public Token parse( String source, int offset) {
        if( source==null )return null;
        if( offset>=source.length() )return null;

        int offStart = offset;

        String[] stopArray = stopList.toArray(new String[]{});
        int minCapture = Integer.MAX_VALUE;
        for( String stopWord : stopArray ){
            if( stopWord==null )continue;
            int l = stopWord.length();
            if( l<minCapture )minCapture = l;
        }

        StringBuilder sb = new StringBuilder();

        while( true ){
            if( offset>=source.length() )break;

            boolean isStopWordMatched = false;
            for( String stopWord : stopArray ){
                if( stopWord==null )continue;
                String lookWord = Text.lookupText(source, offset, stopWord.length());
                if( stopWord.equals(lookWord) ){
                    isStopWordMatched = true;
                    break;
                }
            }
            if( isStopWordMatched )break;

            String capture = Text.lookupText(source, offset, minCapture);
            if( capture.length()>0 ){
                sb.append(capture);
                offset += capture.length();
            }else{
                break;
            }
        }

        if( sb.length()==0 )return null;
        return new AnyChar("anyChar", source, offStart, sb.length());
    }
}
