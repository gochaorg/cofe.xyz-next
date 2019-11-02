package xyz.cofe.text.template;

import xyz.cofe.text.lex.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Лексический анализатор
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class TemplateLexer extends ListLexer
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(TemplateLexer.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(TemplateLexer.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(TemplateLexer.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(TemplateLexer.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(TemplateLexer.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(TemplateLexer.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(TemplateLexer.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public TemplateLexer(){
        Keywords keywords = new Keywords(false);
        keywords
            .put("${", "codeBegin")
            .put("\\$", "escape")
            .put("\\\\", "escape")
            .put("\\${", "escape")
            .put("{", "blockBegin")
            .put("}", "blockEnd")
        ;

        KeywordsParser keyWords = new KeywordsParser(keywords);
        AnyCharParser anyChar = new AnyCharParser(keywords.getKeywords());

        List<TokenParser> parsers = getParsers();
        parsers.add(keyWords);
        parsers.add(anyChar);
    }

    public TemplateLexer(String[] codeBegin, String[] escape, String[] blockBegin, String[] blockEnd){
        Keywords keywords = new Keywords(false);

        for( String k : codeBegin )keywords.put(k, "codeBegin");
        for( String k : escape )keywords.put(k, "escape");
        for( String k : blockBegin )keywords.put(k, "blockBegin");
        for( String k : blockEnd )keywords.put(k, "blockEnd");

        KeywordsParser keyWords = new KeywordsParser(keywords);
        AnyCharParser anyChar = new AnyCharParser(keywords.getKeywords());

        List<TokenParser> parsers = getParsers();
        parsers.add(keyWords);
        parsers.add(anyChar);
    }

    public TemplateLexer(Keywords keywords){
        if( keywords==null )throw new IllegalArgumentException("keywords == null");

        KeywordsParser keyWords = new KeywordsParser(keywords);
        AnyCharParser anyChar = new AnyCharParser(keywords.getKeywords());

        List<TokenParser> parsers = getParsers();
        parsers.add(keyWords);
        parsers.add(anyChar);
    }
}
