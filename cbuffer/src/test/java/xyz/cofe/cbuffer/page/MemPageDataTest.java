package xyz.cofe.cbuffer.page;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class MemPageDataTest {
    @Test
    public void testNonAlign01(){
        MemPagedData pd = new MemPagedData(1024, 1024*8+512);
        System.out.println(pd.memoryInfo());

        byte[] bytes = "abcde".getBytes(StandardCharsets.UTF_8);
        pd.writePage(8, bytes);
        System.out.println(pd.memoryInfo());

        pd.extendPages(2);
        System.out.println(pd.memoryInfo());
    }
}
