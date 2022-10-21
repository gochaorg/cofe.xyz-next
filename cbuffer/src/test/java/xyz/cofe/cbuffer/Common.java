package xyz.cofe.cbuffer;

import java.util.concurrent.ThreadLocalRandom;

public class Common {
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

    public static byte[] generateRandom(int size){
        var buff = new byte[size];
        var rnd = ThreadLocalRandom.current();
        rnd.nextBytes(buff);
        return buff;
    }
}
