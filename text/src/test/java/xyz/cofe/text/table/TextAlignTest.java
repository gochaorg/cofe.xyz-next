package xyz.cofe.text.table;

import org.junit.Test;
import xyz.cofe.text.Align;
import xyz.cofe.text.Text;

public class TextAlignTest {
    @Test
    public void aleignLeft(){
        String input1 = "  a";
        String out1 = Text.align(input1, Align.Begin, "x", 20);
        System.out.println("\""+out1+"\"");
    }
}
