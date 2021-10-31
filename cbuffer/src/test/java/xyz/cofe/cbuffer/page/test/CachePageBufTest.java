package xyz.cofe.cbuffer.page.test;

import org.junit.Test;

public class CachePageBufTest {
    @Test
    public void test01(){
        System.setProperty("xyz.cofe.cbuffer.page.test.CachePageBuffer.log","true");

        CachePageBuffer pb = new CachePageBuffer(
            new MemBuffer(),
            new MemBuffer(),
            1024,
            8
        );

        System.out.println("test 01");
        byte[] buff = "hello".getBytes();
        pb.set(0,buff,0,buff.length);

        System.out.println("test 02");
        byte[] buff2 = " world".getBytes();
        pb.set(buff.length,buff2,0,buff2.length);

        System.out.println("test 03 ("+(buff.length+buff2.length)+")");
        byte[] buff3 = pb.get(0,buff.length+buff2.length);
        System.out.println("readed: "+buff3.length+" \""+new String(buff3)+"\"");


    }
}
