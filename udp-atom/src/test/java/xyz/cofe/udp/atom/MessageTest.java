package xyz.cofe.udp.atom;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.Test;

public class MessageTest {
    @Test
    public void test01(){
        AtomicInteger sentSize = new AtomicInteger();
        AtomicInteger receivedSize = new AtomicInteger();

        MessageConsumer cons = new MessageConsumer().reader(data -> {
            receivedSize.addAndGet(data.length);
        });
        MessageProducer prod = new MessageProducer(cons).blockSize(500);

        Random rnd = new Random();
        for( int i=0; i<10000; i++ ){
            byte[] buff = new byte[rnd.nextInt(1000)+300];
            rnd.nextBytes(buff);
            prod.write(buff);
            sentSize.addAndGet(buff.length);
        }

        System.out.println("sent     "+sentSize);
        System.out.println("received "+receivedSize);
    }

    private String toString(Block block){
        StringBuilder sb = new StringBuilder();
        sb.append("block ").append(block.getMessageId()).append("/").append(block.getBlockId()).append("\n");
        BlockHead h = block.getHead();
        if( h!=null ) {
            sb.append("  adler32   ").append(h.adler32()).append("\n");
        }else{
            sb.append("  head null").append("\n");
        }
        sb.append("  data size ").append(block.getDataSize()).append("\n");
        sb.append("  data len  ").append(block.getData() != null ? block.getData().length : "null").append("\n");
        return sb.toString();
    }

    private String toString(MessageProducer.SendBlock block){
        return "send block "+block.messageId+"/"+block.blockId+"\n"+
            "  adler32  "+block.adlr32+"\n" +
            "  sendSize "+block.sendSize+"\n"+
            "  ptr      "+block.ptr+"\n" +
            "  off      "+block.off+" " +
            "  len      "+block.len;
    }

    @Test
    public void test02(){
        AtomicInteger sentSize = new AtomicInteger();
        AtomicInteger receivedSize = new AtomicInteger();

        AtomicReference<MessageProducer.SendBlock> sentBlock = new AtomicReference<>();

        MessageConsumer cons = new MessageConsumer().reader(data -> {
            receivedSize.addAndGet(data.length);
        }).failBlock( (data,block) -> {
            System.out.println("fail block");
            System.out.println("  data accept"+data.length);
            System.out.println(toString(block));
            System.out.println("---");
            System.out.println(toString(sentBlock.get()));
            System.out.println();
        });
        MessageProducer prod = new MessageProducer(cons).blockSize(500).onSend(sentBlock::set);

        Random rnd = new Random();

        for( int i=0; i<100; i++ ) {
            byte[] buff = new byte[rnd.nextInt(2000) + 10000];
            rnd.nextBytes(buff);
            prod.write(buff);
            sentSize.addAndGet(buff.length);
        }

        System.out.println("sent     "+sentSize);
        System.out.println("received "+receivedSize);
    }

    public static class RandomWriter implements Consumer<byte[]> {
        public final Consumer<byte[]> writer;
        public final Random random = new Random();

        public RandomWriter(Consumer<byte[]> writer){
            if( writer==null )throw new IllegalArgumentException( "writer==null" );
            this.writer = writer;
        }

        public RandomWriter configure( Consumer<RandomWriter> conf ){
            conf.accept(this);
            return this;
        }

        public int asis = 100;
        public int reorder = 20;
        public int duplicate = 20;
        public int drop = 20;

        public TreeMap<Integer, Queue<byte[]>> lag = new TreeMap<>();
        public int reorderLag = 10;
        public int duplicateLag = 10;
        public int eventId = 0;

        public int asisCount = 0;
        public int reorderCount = 0;
        public int duplicateCount = 0;
        public int dropCount = 0;
        public AtomicInteger acceptCount;

        @Override
        public void accept(byte[] bytes) {
            AtomicInteger acnt = acceptCount;
            if( acnt!=null ){
                acnt.incrementAndGet();
            }

            int evId = eventId;
            eventId++;

            int rndRange = asis + reorder + drop + duplicate;
            int rndIdx = random.nextInt(rndRange);
            if( rndIdx < asis ){
                writer.accept(bytes);
                asisCount++;
            }else if( rndIdx < asis + reorder ){
                lag.computeIfAbsent( eventId+random.nextInt(reorderLag), k -> new LinkedList<>() ).add(bytes);
                reorderCount++;
            }else if( rndIdx < asis + reorder + duplicate ){
                writer.accept(bytes);
                lag.computeIfAbsent( eventId+random.nextInt(duplicateLag), k -> new LinkedList<>() ).add(bytes);
                duplicateCount++;
            }else if( rndIdx < asis + reorder + duplicate + drop ){
                dropCount++;
            }else{
                writer.accept(bytes);
            }

            NavigableMap<Integer,Queue<byte[]>> h = lag.headMap(eventId,false);
            h.values().forEach( q -> {
                while (true) {
                    byte[] b = q.poll();
                    if( b==null )break;
                    writer.accept(b);
                }
            });
            h.clear();
        }

        public void flush(){
            lag.values().forEach( q -> {
                while (true) {
                    byte[] b = q.poll();
                    if( b==null )break;
                    writer.accept(b);
                }
            });
            lag.clear();
        }
    }

    private MessageDigest md;

    {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private String hash(byte[] data){
        if( data==null )throw new IllegalArgumentException( "data==null" );
        if( md==null )throw new IllegalStateException( "md==null" );
        md.reset();
        md.update(data);
        byte[] dig = md.digest();
        StringBuilder sb = new StringBuilder();
        for( int i=0; i<dig.length; i++ ){
            int b = (dig[i] & 0xFF);
            int h = (b & 0xF0) >> 4;
            int l = (b & 0x0F);
            sb.append(Integer.toHexString(h)).append(Integer.toHexString(l));
        }
        return sb.toString();
    }

    @Test
    public void randomTest01(){
        Map<String,byte[]> sentData = new HashMap<>();
        Map<String,byte[]> receivedData = new HashMap<>();

        AtomicInteger blockDataSizeMax = new AtomicInteger(0);
        AtomicInteger blockSizeMax = new AtomicInteger(0);

        AtomicInteger sentSize = new AtomicInteger();
        AtomicInteger receivedSize = new AtomicInteger();
        List<Integer> splitCount = new ArrayList<>();

        MessageConsumer cons = new MessageConsumer().reader((data,msgId,blocks) -> {
            receivedSize.addAndGet(data.length);
            receivedData.put(hash(data), data);

            blockDataSizeMax.set(
                Math.max(
                    blocks.stream().map(b->b.read().map(bb->bb.length).orElse(0)).max(Integer::compareTo).orElse(-1),
                    blockDataSizeMax.get())
            );

            blockSizeMax.set(
                Math.max(
                    blocks.stream().map(Block::getBlockSize).max(Integer::compareTo).orElse(-1),
                    blockSizeMax.get())
            );
        });

        AtomicInteger acceptCount = new AtomicInteger();

        RandomWriter rndWriter = new RandomWriter(cons);
        rndWriter.asis = 100;
        rndWriter.reorder = 200;
        rndWriter.reorderLag = 30;
        rndWriter.duplicate = 50;
        rndWriter.drop = 50;

        rndWriter.acceptCount = acceptCount;
        MessageProducer prod = new MessageProducer(rndWriter).blockSize(300);

        Random rnd = new Random();
        for( int i=0; i<500; i++ ){
            acceptCount.set(0);

            byte[] buff = new byte[rnd.nextInt(1000)+300];

            rnd.nextBytes(buff);
            prod.write(buff);

            sentSize.addAndGet(buff.length);

            splitCount.add(acceptCount.get());
            sentData.put(hash(buff), buff);
        }

        rndWriter.flush();

        System.out.println("sent     "+sentSize);
        System.out.println("received "+receivedSize);

        System.out.println("rnd:");
        System.out.println("  asis  "+rndWriter.asisCount);
        System.out.println("  dupl  "+rndWriter.duplicateCount);
        System.out.println("  drop  "+rndWriter.dropCount);
        System.out.println("  reorg "+rndWriter.reorderCount);

        System.out.println("splitCount:");
        int splitCountSum = splitCount.stream().reduce(0, Integer::sum);
        int splitCountSamples = splitCount.size();
        System.out.println("  avg "+(splitCountSamples>0 ? (double)splitCountSum/(double) splitCountSamples : null));

        int brokenCount = 0;
        int succReceivedCount = 0;
        System.out.println("compare received / sent data");
        for( String recHash : receivedData.keySet() ){
            if( !sentData.containsKey(recHash) ){
                //System.out.println("!received different hash="+recHash);
                brokenCount++;
            }else {
                succReceivedCount++;
            }
        }

        int dropCount = 0;
        int succDeliveredCount = 0;
        System.out.println("compare sent / received data");
        for( String sentHash : sentData.keySet() ){
            if( !receivedData.containsKey(sentHash) ){
                //System.out.println("!dropped hash="+sentHash);
                dropCount++;
            }else {
                succDeliveredCount++;
            }
        }

        double totBlocks = rndWriter.asisCount+ rndWriter.dropCount+rndWriter.reorderCount+rndWriter.duplicateCount;
        double totWrites = dropCount+succDeliveredCount;

        System.out.println("delivery:");
        System.out.println("  dropped count "+dropCount);
        System.out.println("  success count "+succDeliveredCount + " "+(100.0*succDeliveredCount/totWrites)+"%");
        System.out.println("  total count   "+(dropCount+succDeliveredCount));
        System.out.println("  writed bytes ");
        System.out.println("    avg "+sentData.values().stream().map(a->a.length).reduce(0, Integer::sum).doubleValue() / sentData.size() );
        System.out.println("    max "+sentData.values().stream().map(a->a.length).max(Integer::compareTo).orElse(-1) );

        System.out.println("accepted:");
        System.out.println("  broken count  "+brokenCount);
        System.out.println("  success count "+succReceivedCount);
        System.out.println("  total count   "+(succReceivedCount+brokenCount));
        System.out.println("  reorg counter "+cons.getReorgCounter());
        System.out.println("  block size max "+blockSizeMax.get());
        System.out.println("  block data size max "+blockDataSizeMax.get());

        System.out.println("rnd pct:");
        System.out.println("  asis "+(100.0*rndWriter.asisCount/totBlocks));
        System.out.println("  dupl "+(100.0*rndWriter.duplicateCount/totBlocks));
        System.out.println("  drop "+(100.0*rndWriter.dropCount/totBlocks));
        System.out.println("  drop "+(100.0*rndWriter.reorderCount/totBlocks));

        System.out.println("rnd pct-dupl:");
        System.out.println("  asis  "+(100.0*rndWriter.asisCount/(totBlocks- rndWriter.duplicateCount)));
        System.out.println("  dupl  "+(100.0*rndWriter.duplicateCount/(totBlocks- rndWriter.duplicateCount)));
        System.out.println("  drop  "+(100.0*rndWriter.dropCount/(totBlocks- rndWriter.duplicateCount)));
        System.out.println("  reorg "+(100.0*rndWriter.reorderCount/(totBlocks- rndWriter.duplicateCount)));
    }
}
