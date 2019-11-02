package xyz.cofe.text.lex;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Парсер fieldAccess ::= identifier { '.' identifier }
 * @see FieldAccess
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class FieldAccessParser implements TokenParser {
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(FieldAccessParser.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(FieldAccessParser.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(FieldAccessParser.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(FieldAccessParser.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(FieldAccessParser.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(FieldAccessParser.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(FieldAccessParser.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    private final IdentifierParser idParser;
    private final WhiteSpaceParser wsParser;
    private final String[] fieldAccess;

    public FieldAccessParser(){
        idParser = new IdentifierParser("id");
        wsParser = new WhiteSpaceParser("ws");
        fieldAccess = new String[]{ "." };
    }

    @Override
    public FieldAccess parse(String source, int offset) {
        if( source==null )return null;
        if( offset<0 )return null;
        if( offset>=source.length() )return null;

        List<Token> idList = new ArrayList<Token>();
        Token beginTok = null;
        Token endTok = null;

        int begin = offset;
        int end = offset;

        Token ws = wsParser.parse(source, offset);
        if( ws!=null ){
            offset += ws.getLength();
            beginTok = ws;
        }

        Token id = idParser.parse(source, offset);
        if( id==null )return null;
        offset += id.getLength();
        idList.add(id);

        if( beginTok==null )beginTok = id;
        if( endTok==null )endTok = id;
        end = offset;

        ws = wsParser.parse(source, offset);
        if( ws!=null ){
            offset += ws.getLength();
        }

        // repeat block
        boolean repeat = true;

        while( repeat ){
            boolean matched = false;
            String matchedText = null;
            for( String fa : fieldAccess ){
                if( LexerUtil.match(source, offset, false, fa) ){
                    matchedText = fa;
                    offset += matchedText.length();
                    matched = true;
                    break;
                }
            }

            if( matched ){
                ws = wsParser.parse(source, offset);
                if( ws!=null )offset += ws.getLength();

                id = idParser.parse(source, offset);
                if( id==null )return null;
                idList.add(id);
                offset += id.getLength();
                end = offset;
                endTok = id;
            }else{
                repeat = false;
            }
        }
        // repeat end block

        return new FieldAccess(
            "fieldAccess",
            source,
            beginTok,
            endTok,
            idList.toArray(new Token[]{})
        );
    }
}
