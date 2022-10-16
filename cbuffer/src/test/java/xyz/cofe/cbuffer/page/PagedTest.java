package xyz.cofe.cbuffer.page;

import org.junit.Test;
import xyz.cofe.cbuffer.MemContentBuffer;
import xyz.cofe.fn.Fn2;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PagedTest {
    public static byte[] generateRandom(int size){
        var buff = new byte[size];
        var rnd = ThreadLocalRandom.current();
        rnd.nextBytes(buff);
        return buff;
    }

    @Test
    public void memAlignedTest(){
        var pageSize = 1024;

        var memPaged = new MemPaged(pageSize, pageSize*16);
        var mi = memPaged.memoryInfo();
        assertTrue(mi.pageCount()==16);
        assertTrue(mi.lastPageSize()==pageSize);
    }

    @Test
    public void memNonAlignedTest(){
        var pageSize = 1024;

        var memPaged = new MemPaged(pageSize, pageSize*8+pageSize/2);
        var mi = memPaged.memoryInfo();
        assertTrue(mi.pageCount()==9);
        assertTrue(mi.lastPageSize()==pageSize/2);

        memPaged.resizePages(9);
        mi = memPaged.memoryInfo();
        assertTrue(mi.pageCount()==9);
        assertTrue(mi.lastPageSize()==pageSize);

        assertTrue(memPaged.buffer().length == pageSize*9);
    }

    @Test
    public void cbuffAlignedTest(){
        var pageSize = 1024;

        var mem = new MemContentBuffer();
        mem.setSize(pageSize*16);
        var cbuff = new CBuffPaged(mem, pageSize,true,-1);

        var mi = cbuff.memoryInfo();
        assertTrue(mi.pageCount()==16);
        assertTrue(mi.lastPageSize()==pageSize);
    }

    @Test
    public void cbuffNonAlignedTest(){
        var pageSize = 1024;

        var mem = new MemContentBuffer();
        mem.setSize(pageSize*8+pageSize/2);
        var cbuff = new CBuffPaged(mem, pageSize,true,-1);

        var mi = cbuff.memoryInfo();
        assertTrue(mi.pageCount()==9);
        assertTrue(mi.lastPageSize()==pageSize/2);

        cbuff.resizePages(9);
        mi = cbuff.memoryInfo();
        assertTrue(mi.pageCount()==9);
        assertTrue(mi.lastPageSize()==pageSize);

        assertTrue(mem.getSize()==pageSize*9);
    }

    private void testCheckErr(Paged paged){
        var someExtraData = generateRandom(paged.memoryInfo().pageSize()+2);
        var someNormData = generateRandom(paged.memoryInfo().pageSize());
        try {
            paged.writePage(0, someExtraData);
            fail();
        } catch (Throwable err){
            System.out.println(err);
        }

        try {
            paged.writePage(-1, someNormData);
            fail();
        } catch (Throwable err){
            System.out.println(err);
        }

        try {
            paged.writePage(paged.memoryInfo().pageCount(), someNormData);
            fail();
        } catch (Throwable err){
            System.out.println(err);
        }

        var mi = paged.memoryInfo();
        if( mi.lastPageSize()!=mi.pageSize() ){
            try {
                paged.writePage(mi.pageCount()-1, generateRandom(mi.lastPageSize()+1));
                fail();
            } catch (Throwable err){
                System.out.println(err);
            }
        }

        paged.writePage(0, new byte[0]);
    }

    @Test
    public void testMemErr(){
        var pageSize = 1024;
        var memPaged = new MemPaged(pageSize, pageSize*8+pageSize/2);
        testCheckErr(memPaged);
    }

    @Test
    public void testCBuffErr(){
        var pageSize = 1024;

        var mem = new MemContentBuffer();
        mem.setSize(pageSize*8+pageSize/2);
        var cbuff = new CBuffPaged(mem, pageSize,true,-1);
        testCheckErr(cbuff);
    }

    public static boolean equals(byte[] arr0, byte[] arr1){
        if( arr0==null && arr1==null )return true;
        if( arr0!=null && arr1==null )return false;
        if( arr0==null && arr1!=null )return false;

        if( arr1.length!=arr0.length )return false;
        for(int i=0;i<arr1.length;i++ ){
            if(arr0[i]!=arr1[i])return false;
        }
        return true;
    }

    public void testWrite(Paged paged, Fn2<Integer,Integer,byte[]> readBytes){
        var firstDataSend = generateRandom(paged.memoryInfo().pageSize());
        paged.writePage(0,firstDataSend);

        var firstDataRecive = readBytes.apply(0,firstDataSend.length);
        assertTrue(equals(firstDataSend,firstDataRecive));

        var firstDataRead = paged.readPage(0);
        assertTrue(equals(firstDataSend,firstDataRead));

        var lastDataSend = generateRandom(paged.memoryInfo().lastPageSize());
        paged.writePage(paged.memoryInfo().pageCount()-1,lastDataSend);

        var lastDataReceive = readBytes.apply(
            (paged.memoryInfo().pageCount()-1)* paged.memoryInfo().pageSize(),
            (paged.memoryInfo().pageCount()-1)* paged.memoryInfo().pageSize()+ paged.memoryInfo().lastPageSize()
        );
        assertTrue(equals(lastDataSend,lastDataReceive));

        var lastDataRead = paged.readPage((paged.memoryInfo().pageCount()-1));
        assertTrue(equals(lastDataSend,lastDataRead));
    }

    @Test
    public void testWriteMem() {
        var pageSize = 1024;
        var memPaged = new MemPaged(pageSize, pageSize*8+pageSize/2);

        testWrite(memPaged, (from,to)-> Arrays.copyOfRange(memPaged.buffer(), from, to) );

        var mem = new MemContentBuffer();
        mem.setSize(pageSize*8+pageSize/2);
        var cbuff = new CBuffPaged(mem, pageSize,true,-1);
        testWrite(cbuff, (from,to)->mem.get(from, to-from));
    }

    @Test
    public void testWriteCBuff() {
        var pageSize = 1024;

        var mem = new MemContentBuffer();
        mem.setSize(pageSize*8+pageSize/2);
        var cbuff = new CBuffPaged(mem, pageSize,true,-1);
        testWrite(cbuff, (from,to)->mem.get(from, to-from));
    }
}
