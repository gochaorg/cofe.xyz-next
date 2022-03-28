package xyz.cofe.fn;

import org.junit.Test;

public class EitherTest {
    @Test
    public void test01(){
        Either<Integer,String> et = Either.left(10);
        et.left().map( x -> x*2 ).left().get();
    }
}
