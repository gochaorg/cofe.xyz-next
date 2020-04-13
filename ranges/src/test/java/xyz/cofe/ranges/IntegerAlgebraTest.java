package xyz.cofe.ranges;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class IntegerAlgebraTest {
    @Test
    public void test01(){
        IntegerAlgebra iAlg = new IntegerAlgebra() {};
        assertTrue( iAlg.sum(10,15)==25 );
        assertTrue( iAlg.sum( iAlg.invert(15), 10 ) == -15 + 10 );
    }
}
