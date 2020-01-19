package xyz.cofe.text.template;

import xyz.cofe.fn.Fn0;
import xyz.cofe.fn.Fn1;
import xyz.cofe.fn.Fn2;
import xyz.cofe.text.template.ast.Block;
import xyz.cofe.text.template.ast.Code;
import xyz.cofe.text.template.ast.Escape;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvalVisitor<ResultType,EvalCode,EvalText> extends TemplateASTVisitor
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(EvalVisitor.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(EvalVisitor.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(EvalVisitor.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(EvalVisitor.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(EvalVisitor.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(EvalVisitor.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(EvalVisitor.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    public Fn1<String,EvalText> evalText;
    public Fn1<String,EvalCode> evalCode;
    public Fn0<ResultType> initResult;
    public Fn2<ResultType, EvalText, ResultType> appendText;
    public Fn2<ResultType, EvalCode, ResultType> appendCode;

    public List<Fn0> funs = new ArrayList<>();
    public Map<Fn0,Boolean> funAsCode = new LinkedHashMap<Fn0, Boolean>();
    public boolean codeContext = false;
    public StringBuilder sbCode = new StringBuilder();

    //public LinkedHashMap<String,String> escapeRewriteMap = new LinkedHashMap<String, String>();

    @Override
    public void visit(xyz.cofe.text.template.ast.Text text) {
        if( codeContext ){
            sbCode.append(text.token.getMatchedText());
        }else{
            final String srcText = text.token.getMatchedText();
            Fn0<EvalText> f = new Fn0<EvalText>() {
                @Override
                public EvalText apply() {
                    return evalText.apply(srcText);
                }
            };

            funs.add(f);
            funAsCode.put(f, false);
        }
    }

    @Override
    public void visit( Escape escape) {
        if( codeContext ){
            sbCode.append(escape.token.getMatchedText());
        }else{
            String srcText = escape.token.getMatchedText();
            for( Map.Entry<String,String> en : escape.rewriteMap.entrySet() ){
                srcText = srcText.replace(en.getKey(), en.getValue());
            }
            final String rewritedText = srcText;

            Fn0<EvalText> f = new Fn0<EvalText>() {
                @Override
                public EvalText apply() {
                    return evalText.apply(rewritedText);
                }
            };
            funs.add(f);
            funAsCode.put(f, false);
        }
    }

    @Override
    public void enter( Block block) {
        if( codeContext ){
            sbCode.append(block.begin.getMatchedText());
        }else{
            final String srccode = block.begin.getMatchedText();
            Fn0<EvalText> f = new Fn0<EvalText>() {
                @Override
                public EvalText apply() {
                    return evalText.apply(srccode);
                }
            };
            funs.add(f);
            funAsCode.put(f, false);
        }
    }

    @Override
    public void exit(Block block) {
        if( codeContext ){
            sbCode.append(block.end.getMatchedText());
        }else{
            final String srccode = block.end.getMatchedText();
            Fn0<EvalText> f = new Fn0<EvalText>() {
                @Override
                public EvalText apply() {
                    return evalText.apply(srccode);
                }
            };
            funs.add(f);
            funAsCode.put(f, false);
        }
    }

    @Override
    public void enter( Code code) {
        codeContext = true;
        sbCode.setLength(0);
    }

    @Override
    public void exit(Code code) {
        codeContext = false;
        if( sbCode.length()>0 ){
            final String srccode = sbCode.toString();

            Fn0<EvalCode> f = new Fn0<EvalCode>() {
                @Override
                public EvalCode apply() {
                    return evalCode.apply(srccode);
                }
            };

            funs.add(f);
            funAsCode.put(f, true);
        }
    }
}
