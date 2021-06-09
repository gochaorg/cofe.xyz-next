package xyz.cofe.text;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.Test;
import xyz.cofe.fn.Tuple2;

public class BytesDumpTest {
    @Test
    public void test01(){
        byte[] bytes = (
            "0123456789abcdef _ABCDEFG"
        ).getBytes(StandardCharsets.ISO_8859_1);

        BytesDump dump = new BytesDump();

        dump.setPreview( d -> {
            if( d.getPointer()==2 ) return d.respone(2,"2 chars: "+new String(d.getBytes(),d.getPointer(),2,StandardCharsets.ISO_8859_1));
            if( d.getPointer()==4 ) return d.respone(4,"4 chars: "+new String(d.getBytes(),d.getPointer(),4,StandardCharsets.ISO_8859_1));
            return Optional.empty();
        });
        System.out.println(dump.dump(bytes,2,bytes.length-2));
    }

    @Test
    public void test02(){
        byte[] bytes = (
            "0123456789abcdef _ABCDEFG"
        ).getBytes(StandardCharsets.ISO_8859_1);

        BytesDump dump = new BytesDump();

        System.out.println(dump.dump(bytes,0,bytes.length));
    }

    @Test
    public void test03(){
        byte[] bytes = (
            "0123456789abcdef _ABCDEFG"
        ).getBytes(StandardCharsets.ISO_8859_1);

        BytesDump dump = new BytesDump.Builder().relative( decoder -> {
            decoder
                .name(0, 2, "head")
                .decode(4, bytes1 -> new String(bytes1,StandardCharsets.ISO_8859_1));
        }).build();

        System.out.println(dump.dump(bytes));
    }
}
