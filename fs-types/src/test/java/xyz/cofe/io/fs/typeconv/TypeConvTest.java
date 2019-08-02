package xyz.cofe.io.fs.typeconv;

import org.junit.Test;
import xyz.cofe.typeconv.ExtendedCastGraph;

public class TypeConvTest {
    @Test
    public void test01(){
        System.out.println("ExtendedCastGraph nodes:");

        ExtendedCastGraph cg = new ExtendedCastGraph();
        cg.getNodes().forEach(System.out::println);
    }
}
