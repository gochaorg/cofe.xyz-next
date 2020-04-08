package xyz.cofe.ranges;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class IntegerRangeTest {
    @DisplayName("include test")
    @Test
    public void test01(){
        IntegerRange r1 = IntegerRange.of(10,true,20,false);
        IntegerRange r2 = IntegerRange.of(12,18);

        assertTrue(r1.include(r2));
        assertTrue(!r2.include(r1));

        assertTrue(!r1.include(9));
        assertTrue(r1.include(10));
        assertTrue(r1.include(19));
        assertTrue(!r1.include(20));

        IntegerRange r1_1 = IntegerRange.of(10,20);
        assertTrue(r1.include(r1_1));
        assertTrue(r1_1.include(r1));
    }
}
