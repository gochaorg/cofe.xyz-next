package xyz.cofe.iter;

import org.junit.Assert;
import org.junit.Test;

public class EtrableReduceTest {
    @Test
    public void reduce01(){
        int sum = Eterable.of(1,2,3,4,5).reduce(0,(r,a)->r+a);
        System.out.println(sum);
        Assert.assertTrue(sum == (1+2+3+4+5));
    }
}
