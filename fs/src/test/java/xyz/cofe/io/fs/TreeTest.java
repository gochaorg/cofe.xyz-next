package xyz.cofe.io.fs;

import org.junit.Test;

public class TreeTest {
    @Test
    public void test01(){
        File file = new File("target");
        file.walk().forEach(System.out::println);
    }
}
