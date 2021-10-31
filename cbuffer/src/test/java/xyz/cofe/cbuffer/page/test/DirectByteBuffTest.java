package xyz.cofe.cbuffer.page.test;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DirectByteBuffTest {

    @Test
    public void allocFree01(){
        System.out.println("memory alloc / free test");

        Status st = Status.selfStatus();
        System.out.println(st.VmSize.toHumanReadable());
        System.out.println(st.RssAnon.toHumanReadable());

        List<ByteBuffer> buffers = new ArrayList<>();
        for( int i=0; i<(2*4)*2; i++ ){
            int allocSize = 128 * 1024 * 1024; // 128 Mb
            System.out.print("try "+i+" alloc 128Mb ");
            ByteBuffer bb = ByteBuffer.allocateDirect(allocSize);
            buffers.add(bb);
            st = Status.selfStatus();
            System.out.println(st.RssAnon.toHumanReadable());
        }

        long sleep_t = 100;
        int idx = -1;
        while( !buffers.isEmpty() ){
            idx++;
            System.out.print("free "+idx);
            buffers.remove(0);

            System.out.print("  run gc");
            System.gc();

            try {
                System.out.print("  sleep "+sleep_t);
                Thread.sleep(sleep_t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            st = Status.selfStatus();
            System.out.println("  "+st.RssAnon.toHumanReadable());
        }

        try {
            System.out.println("pause");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
