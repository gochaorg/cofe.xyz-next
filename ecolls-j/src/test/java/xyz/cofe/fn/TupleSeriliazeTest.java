package xyz.cofe.fn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.junit.Test;

public class TupleSeriliazeTest {
    @Test
    public void test01(){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        try{
            ObjectOutputStream oos = new ObjectOutputStream(ba);
            oos.writeObject(Tuple4.of(1,1,1,1));
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
