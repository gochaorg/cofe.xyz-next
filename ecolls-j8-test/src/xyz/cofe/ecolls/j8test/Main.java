package xyz.cofe.ecolls.j8test;

import xyz.cofe.collection.LRUCache;

public class Main {
    public static void main(String[] args){
        System.out.println("test ecolls for jvm-8");
        System.out.println(
            "current vm version="+System.getProperty("java.version")
        );

        LRUCache<String,String> cache = new LRUCache<>();
        cache.setCacheSizeMax(10);
        cache.put("a","abc");
        cache.put("b","abc");
    }
}
