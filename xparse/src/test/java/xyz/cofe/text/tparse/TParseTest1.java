package xyz.cofe.text.tparse;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static xyz.cofe.text.tparse.Chars.*;

public class TParseTest1 {
    public final GR<CharPointer,CToken> digits = digit.repeat().map(CToken::new);

    @Test
    public void digitsTest01(){
        CharPointer cptr = new CharPointer("123");

        Optional<CToken> odigits = digits.apply(cptr);
        System.out.println(odigits);

        Assert.assertTrue(odigits!=null);
        Assert.assertTrue(odigits.isPresent());
        Assert.assertTrue(odigits.get().text()!=null);
        Assert.assertTrue(odigits.get().text().equals("123"));
    }

    public static class WS extends CToken {
        public WS(CharPointer begin, CharPointer end) { super(begin, end); }
        public WS(CToken begin, CToken end) { super(begin, end); }
        public WS(List<CToken> tokens) { super(tokens); }
    }

    public final GR<CharPointer,WS> ws = whitespace.repeat().map(WS::new);

    @Test
    public void wsTest01(){
        CharPointer cptr = new CharPointer("  ");

        Optional<WS> ws = this.ws.apply(cptr);
        System.out.println(ws);

        Assert.assertTrue(ws!=null);
        Assert.assertTrue(ws.isPresent());
        Assert.assertTrue(ws.get().text()!=null);
        Assert.assertTrue(ws.get().text().equals("  "));
    }
}
