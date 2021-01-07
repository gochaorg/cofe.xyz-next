package xyz.cofe.fn;

import org.junit.Test;

public class EitherTest {
    @Test
    public void test01(){
        Either<String,Integer> e = Either.left("a");
        e.right().get();
        e.left().get();
    }
}
