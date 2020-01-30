package xyz.cofe.text.template;

import xyz.cofe.fn.Fn0;
import xyz.cofe.fn.Fn1;
import xyz.cofe.fn.Fn2;
import xyz.cofe.text.Align;
import xyz.cofe.text.EndLine;
import xyz.cofe.text.Text;
import xyz.cofe.text.table.TextCell;
import xyz.cofe.text.table.TextCellBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Создание форматирования - выравнивания
 * @author user
 */
public class AlignFormatBuilder
    implements FormatBuilder
{
    private TextCellBuilder buildTextCell( String formatting ){
        TextCellBuilder tcb = new TextCellBuilder();
        Pattern ptrn = Pattern.compile("(?is)^"+formattingRegex+".*");
        Matcher m = ptrn.matcher(formatting);
        if( m.matches() ){
            String align = m.group(2);
            String width = m.group(3);

            if( align.equals("") || align.equals("<") ){ tcb.setHorzAlign(Align.Begin); }
            if( align.equals("=") ){ tcb.setHorzAlign(Align.Center); }
            if( align.equals(">") ){ tcb.setHorzAlign(Align.End); }

            tcb.setWidth(Integer.parseInt(width));
            tcb.setMultiLine(true);
        }
        return tcb;
    }

    private final String formattingRegex = "((<?|=|>)(\\d+))";

    @Override
    public <T> Fn1 build(
        final BasicTemplate template,
        final Fn1<T,Object> setContext,
        final Fn1<String,String> evalCode
    ){
        final List rows = new ArrayList();
        final List cells = new ArrayList();
        final Map<Object,TextCellBuilder> codeFormat = new LinkedHashMap<>();

        template.getParser().eval(
            template.getAst(),

            // eval text
            new Fn1() {
                @Override
                public Object apply(final Object text) {
                    int i = -1;
                    for( String line : xyz.cofe.text.Text.splitNewLines(text.toString()) ){
                        final String fline = line;
                        i++;
                        if( i>0 ){
                            rows.add(
                                Arrays.asList(cells.toArray())
                            );
                            cells.clear();
                        }

                        Fn1 cellFun = new Fn1(){
                            @Override
                            public Object apply(Object context) {
                                return fline;
                            }
                        };
                        cells.add( cellFun );
                    }

                    return text;
                }
            },

            // eval code
            new Fn1() {
                @Override
                public Object apply(Object code) {
                    Pattern ptrn = Pattern.compile("(?s)^(.*?)\\:("+formattingRegex+")$");
                    Matcher m = ptrn.matcher(code.toString());

                    String srccode = code.toString();
                    TextCellBuilder tcb = null;
                    if( m.matches() ){
                        srccode = m.group(1);
                        String format = m.group(2);
                        tcb = buildTextCell(format);
                    }

                    final String fsrccode = srccode;
                    Fn1 cellFun = new Fn1(){
                        @Override
                        public Object apply(Object context) {
                            setContext.apply((T)context);
                            return evalCode.apply(fsrccode);
                        }
                    };
                    cells.add( cellFun );
                    codeFormat.put( cellFun, tcb);

                    return fsrccode;
                }
            },

            // init res
            new Fn0(){
                @Override
                public Object apply() {
                    return "";
                }
            },

            // append text
            new Fn2(){
                @Override
                public Object apply(Object res, Object text) {
                    return "";
                }
            },

            // append code
            new Fn2(){
                @Override
                public Object apply(Object res, Object code) {
                    return "";
                }
            }
        ).apply();

        if( !cells.isEmpty() )rows.add(cells);

        Fn1 evalCtx = new Fn1(){
            @Override
            public Object apply(Object ctx) {
                List<String> strings = new ArrayList<String>();
                for( Object ocells : rows ){
                    if( ocells instanceof List ){
                        List<TextCell> ltc = new ArrayList<TextCell>();

                        for( Object ocell : ((List)ocells) ){
                            String txtval = "";
                            if( ocell instanceof Fn1 ){
                                Object cellval = ((Fn1)ocell).apply(ctx);
                                if( cellval!=null ){
                                    txtval = cellval.toString();
                                }
                            }
                            TextCellBuilder tcb = codeFormat.get(ocell);
                            if( tcb!=null ){
                                TextCell tc = tcb.build(txtval);
                                ltc.add(tc);
                            }else{
                                tcb = new TextCellBuilder();
                                tcb.setWidth(txtval.length());
                                TextCell tc = tcb.build(txtval);
                                ltc.add(tc);
                            }
                        }

                        List<String> lstr = TextCell.horizontalJoin(ltc);
                        if( lstr!=null && lstr.isEmpty() ){
                            strings.add("");
                        }else{
                            strings.addAll(lstr);
                        }
                    }
                }
                return Text.join(strings, EndLine.Default.get());
            }
        };

        return evalCtx;
    }
}
