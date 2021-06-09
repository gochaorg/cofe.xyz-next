package xyz.cofe.udp.atom;

import xyz.cofe.fn.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.Adler32;

/**
 * Чтение потока сообщений состоящих из блоков данных.
 *
 * <p>
 *
 * Предполагается что блоки данных могут приходить
 * <ul>
 *     <li>
 *         в любом порядке
 *     </li>
 *     <li>
 *         Дублироваться
 *     </li>
 *     <li>
 *         Отсуствовать
 *     </li>
 * </ul>
 *
 * Класс будет производить сборку сообщения исходя из поступивших блоков.
 */
public class MessageConsumer implements Consumer<byte[]> {
    private final Adler32 adler32 = new Adler32();

    protected Block decode(byte[] buff){
        return BlockCodec.decode(buff,0,buff.length);
    }

    /**
     * Получение очередного блока данных
     * @param data блок данных
     */
    public synchronized void accept(byte[] data){
        if( data==null )throw new IllegalArgumentException( "data==null" );

        Block block = decode(data);
        if( block==null ){
            acceptFail(data);
            return;
        }

        if( !block.dataValid(adler32) ){
            acceptFail(data,block);
            return;
        }

        int msgId = block.getMessageId();
        int blkId = block.getBlockId();

        TreeMap<Integer,AcceptedBlock> blocks = messageBlocks.computeIfAbsent(msgId, m -> new TreeMap<>());

        // приходит пакет с id более высоким чем есть
        if( blocks.isEmpty() ){
            if( blkId>0 ){
                reorgCounter++;
            }
        }else{
            // Ожидается блок с id который больше на 1 от последнего
            int lastBid = blocks.lastKey();
            if( lastBid!=(blkId-1) ){
                reorgCounter++;
            }
        }

        MessageConsumer.AcceptedBlock blk = blocks.computeIfAbsent( blkId, b -> new AcceptedBlock(block) );
        blk.setAcceptTime(System.currentTimeMillis());

        collectBlocks();
    }

    private volatile int reorgCounter = 0;
    public int getReorgCounter(){
        return reorgCounter;
    }

    public void resetCounters(){
        reorgCounter = 0;
    }

    public static class AcceptedBlock extends Block {
        public AcceptedBlock(){
        }
        public AcceptedBlock(Block sample){
            if( sample==null )throw new IllegalArgumentException( "sample==null" );
            setMessageId(sample.getMessageId());
            setBlockId(sample.getBlockId());

            setBlockSize(sample.getBlockSize());
            setDataSize(sample.getDataSize());
            setHeadSize(sample.getHeadSize());

            setHeadBytes(sample.getHeadBytes());
            setData(sample.getData());
        }

        protected long acceptTime;

        public long getAcceptTime() {
            return acceptTime;
        }
        public void setAcceptTime(long acceptTime) {
            this.acceptTime = acceptTime;
        }
    }

    private final TreeMap<Integer, TreeMap<Integer, AcceptedBlock>> messageBlocks = new TreeMap<>();

    private void acceptFail(byte[] data){
        System.out.println("acceptFail 1");
    }

    private volatile Consumer2<byte[], Block> failBlock;
    public MessageConsumer failBlock( Consumer2<byte[], Block> fail ){
        this.failBlock = fail;
        return this;
    }

    private void acceptFail(byte[] data, Block block){
        Consumer2<byte[],Block> f = failBlock;
        if( f!=null ){
            f.accept(data, block);
        }
    }

    private int acceptedDataSize(TreeMap<Integer, AcceptedBlock> blocks){
        if( blocks.isEmpty() )return 0;
        return blocks.values().stream().map(Block::getDataSize).reduce(0, Integer::sum);
    }

    private Optional<Integer> expectedDataSize(TreeMap<Integer, AcceptedBlock> blocks){
        if( blocks.isEmpty() )return Optional.empty();
        return Optional.of(blocks.firstEntry().getValue().getHead().messageSize());
    }

    private Map<Integer, TreeMap<Integer, AcceptedBlock>> fullyAccepted(){
        return messageBlocks.entrySet().stream().filter( e ->
            expectedDataSize(e.getValue()).map( exp -> Objects.equals(exp,acceptedDataSize(e.getValue())) ).orElse(false)
        ).collect( Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue) );
    }

    private byte[] dataOf( TreeMap<Integer, AcceptedBlock> blocks ){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        blocks.values().forEach( block -> block.read().ifPresent( data -> {
            try {
                ba.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        return ba.toByteArray();
    }

    private Map<Integer,Long> lastUpdated(){
        return messageBlocks.entrySet().stream().filter( e -> !e.getValue().isEmpty() )
            .map( e -> Tuple2.of(e.getKey(), e.getValue().values().stream().map(AcceptedBlock::getAcceptTime).max(Long::compare).get()) )
            .collect( Collectors.toMap(Tuple2::a, Tuple2::b) );
    }

    private volatile long TTLms = 1000L * 30L;

    /**
     * Указывает время хранения блоков, по истечению которых блоки будут отброшены.
     * @return (ms) время хранения блоков
     */
    public long getTTLms(){ return TTLms; }

    /**
     * Указывает время хранения блоков, по истечению которых блоки будут отброшены.
     * @param t (ms) время хранения блоков
     */
    public void setTTLms(long t){
        if( t<0 )throw new IllegalArgumentException( "t<0" );
        this.TTLms = t;
    }

    private volatile Consumer<Tuple3<byte[], Integer, ? extends Collection<? extends Block>>> reader;

    /**
     * Указывает функцию чтения сообщения
     * @param fn функция чтения сообщения
     * @return SELF ссылка
     */
    public MessageConsumer reader(Consumer<byte[]> fn){
        this.reader = fn==null ? null : e -> fn.accept(e.a());
        return this;
    }

    /**
     * Указывает функцию чтения сообщения
     * @param fn функция чтения сообщения
     * @return SELF ссылка
     */
    public MessageConsumer reader(Consumer2<byte[],Integer> fn){
        this.reader = fn==null ? null : e -> fn.accept(e.a(), e.b());
        return this;
    }

    /**
     * Указывает функцию чтения сообщения
     * @param fn функция чтения сообщения: сообщение, ид.сообщения, блоки из которых составлено сообщение
     * @return SELF ссылка
     */
    public MessageConsumer reader(Consumer3<byte[],Integer,Collection<? extends Block>> fn){
        this.reader = fn==null ? null : e -> fn.accept(e.a(), e.b(), e.c());
        return this;
    }

    private void collectBlocks(){
        Map<Integer,TreeMap<Integer, AcceptedBlock>> accepted = fullyAccepted();
        accepted.keySet().forEach(messageBlocks::remove);

        Consumer<Tuple3<byte[], Integer, ? extends Collection<? extends Block>>> r = reader;
        if( r!=null ) {
            accepted.forEach((key, value) -> r.accept(Tuple3.of(dataOf(value), key, value.values())));
        }

        lastUpdated().entrySet().stream().filter( e -> (System.currentTimeMillis()-e.getValue())>TTLms ).forEach( e -> messageBlocks.remove(e.getKey()) );
    }
}
