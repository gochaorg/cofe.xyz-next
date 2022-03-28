package xyz.cofe.cbuffer.map;

import xyz.cofe.cbuffer.ContentBuffer;
import xyz.cofe.fn.Consumer3;
import xyz.cofe.fn.Fn1;
import xyz.cofe.fn.Fn2;
import xyz.cofe.fn.Tuple2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PropertyMapper<T> extends ValueMapper {
    private Method readMethod;
    private Method writeMethod;
    private Fn2<byte[],Integer,T> readBuff;
    private Consumer3<byte[],Integer,T> writeBuff;

    public PropertyMapper(
        int offset,
        int valueSize,

        Method writeMethod,
        Fn2<byte[],Integer,T> readBuff,

        Method readMethod,
        Consumer3<byte[],Integer,T> writeBuff
    ) {
        if( readMethod==null )throw new IllegalArgumentException( "readMethod==null" );
        if( readBuff==null )throw new IllegalArgumentException( "readBuff==null" );

        if( writeMethod==null )throw new IllegalArgumentException( "writeMethod==null" );
        if( writeBuff==null )throw new IllegalArgumentException( "writeBuff==null" );

        this.readMethod = readMethod;
        this.readBuff = readBuff;

        this.writeMethod = writeMethod;
        this.writeBuff = writeBuff;

        this.offset = offset;
        //this.valueSize
    }

    @Override
    public void read(Object obj, byte[] buff, int offset) {
        try {
            writeMethod.invoke(obj,readBuff.apply(buff,offset));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new Error(e);
        }
    }

    @Override
    public void write(byte[] buff, int offset, Object obj) {
        try {
            T v = (T)readMethod.invoke(obj);
            writeBuff.accept(buff,offset,v);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static class IntProp extends PropertyMapper<Integer> {
        public IntProp(int offset, int valueSize, Method writeMethod, Fn2<byte[], Integer, Integer> readBuff, Method readMethod, Consumer3<byte[], Integer, Integer> writeBuff) {
            super(offset, valueSize, writeMethod, readBuff, readMethod, writeBuff);
        }
        //public static IntProp
    }
}
