package xyz.cofe.text.table;

import org.junit.Test;
import xyz.cofe.text.Align;
import xyz.cofe.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TableTest {
    @Test
    public void test01(){
        TableFormat tf = TableFormats.asciiTable().clone();

        tf.getMiddleCell().width(10).align(Align.Center);
        tf.getDefaultCell().setTrimSpaces(false);
        tf.getFirstCell().setTrimSpaces(false);

//        CellFormat cf = new CellFormat();
//        cf.setWidth(5);
        tf.getLastCell().setWidth(10);
        tf.getLastCell().setHorzAlign(Align.End);
        tf.getLastCell().setVertAlign(Align.End);

        List<String> lines = new ArrayList<>();

        lines.addAll( tf.formatHeader("A", "B", "c") );
        lines.addAll( tf.formatFirstRow("12", "asd", "xcfr") );
        lines.addAll( tf.formatMiddleRow("13", "r xx", "x\nrty") );
        lines.addAll( tf.formatMiddleRow("  12\nx   axt  ","flg","e r f") );
        lines.addAll( tf.formatLastRow("q", "s", "c") );

        System.out.println(Text.join(lines, "\n"));
    }
}
