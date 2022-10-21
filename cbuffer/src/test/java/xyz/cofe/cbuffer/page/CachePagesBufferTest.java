package xyz.cofe.cbuffer.page;

import org.junit.Test;
import xyz.cofe.cbuffer.Common;
import xyz.cofe.cbuffer.CachePagesBuffer;
import xyz.cofe.text.BytesDump;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertTrue;

public class CachePagesBufferTest extends Common {
    @Test
    public void testAligned(){
        int pageSize = 512;

        var fast = new MemFlatPaged(pageSize, pageSize*4);
        var slow = new MemFlatPaged(pageSize, pageSize*64);
        var cache = new CachePaged<>(fast,slow);
        var pages = new CachePagesBuffer<>(cache);

        var data = new ArrayList<byte[]>();
        for( int i=0;i<slow.memoryInfo().pageCount();i++ ){
            data.add(generateRandom(pageSize));
        }

        for( var i=0;i<data.size();i++ ){
            var d = data.get(i);
            pages.set((long) i *pageSize, d, 0, d.length );
        }

        for( var i=0;i<data.size();i++ ){
            var sample = data.get(i);
            var reads = pages.get((long) i *pageSize, sample.length);
            assertTrue(equals(reads,sample));
        }
    }

    @Test
    public void nonAlignedBlocks(){
        int pageSize = 32;

        var fast = new MemFlatPaged(pageSize, pageSize*4);
        var slow = new MemFlatPaged(pageSize, pageSize*64);
        var cache = new CachePaged<>(fast,slow);
        var pages = new CachePagesBuffer<>(cache);

        var events = new ArrayList<PageEvent>();
        pages.getPages().addListener(events::add);

        for( var i=0; i<100; i++ ){
            events.clear();
            var data = generateRandom(ThreadLocalRandom.current().nextInt(pageSize/2,pageSize*8));
            var off = ThreadLocalRandom.current().nextLong(pageSize*2);
            pages.set(
                off,
                data,
                0,
                data.length
                );
            var reads =pages.get(off,data.length);
            var eq = equals(reads,data);
            if( !eq ){
                System.out.println("iteration "+i);
                System.out.println("off = "+off);
                System.out.println("len = "+data.length);

                var dump = new BytesDump();

                System.out.println("wrote");
                System.out.println(dump.dump(data));

                System.out.println("reads");
                System.out.println(dump.dump(reads));

                System.out.println("events");
                events.forEach(System.out::println);
            }
            assertTrue(eq);
        }
    }

    @Test
    public void nonAlignedBlocksCase1(){
        int pageSize = 32;

        var fast = new MemFlatPaged(pageSize, pageSize*4);
        var slow = new MemFlatPaged(pageSize, pageSize*64);
        var cache = new CachePaged<>(fast,slow);
        var pages = new CachePagesBuffer<>(cache);

        var someBytes = generateRandom(pageSize*64);
        System.arraycopy(someBytes,0,slow.buffer(),0,someBytes.length);

        var reads = pages.get(38,17);
        assertTrue(reads.length==17);
    }
}
