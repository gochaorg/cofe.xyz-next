package xyz.cofe.cbuffer;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import xyz.cofe.cbuffer.page.MemPagedData;
import xyz.cofe.cbuffer.page.UsedPagesInfo;

import java.util.Arrays;

public class PagedDataTest {
    public static boolean equals( byte[] arr1, byte[] arr2 ){
        if( arr1==null )throw new IllegalArgumentException( "arr1==null" );
        if( arr2==null )throw new IllegalArgumentException( "arr2==null" );
        if( arr1.length!= arr2.length )return false;
        for( int i=0;i<arr1.length;i++ ){
            if( arr1[i]!=arr2[i] )return false;
        }
        return true;
    }

    @Test
    public void test01(){
        byte[] buff = new byte[256*100];
        Arrays.fill(buff,(byte)0);

        MemPagedData memPagedData = new MemPagedData(256,buff.length, buff, 0);

        byte[] bytes1 = "abc123".getBytes();
        assertTrue(bytes1.length>0);
        memPagedData.writePage(0,bytes1);

        byte[] bytes1_r = Arrays.copyOfRange(buff,0,bytes1.length);
        assertTrue(equals(bytes1_r,bytes1));

        UsedPagesInfo memInfo = memPagedData.memoryInfo();
        System.out.println("pageSize="+memInfo.pageSize()+" pageCount="+memInfo.pageCount()+" lastPageSize="+memInfo.lastPageSize());

        memPagedData.writePage(1,bytes1);
        memInfo = memPagedData.memoryInfo();
        System.out.println("pageSize="+memInfo.pageSize()+" pageCount="+memInfo.pageCount()+" lastPageSize="+memInfo.lastPageSize());
    }
}
