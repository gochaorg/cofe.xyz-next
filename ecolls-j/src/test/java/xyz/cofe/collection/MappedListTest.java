package xyz.cofe.collection;

import org.junit.Test;

public class MappedListTest {
    public String reverse( String src ){
        if( src==null )return null;
        if( src.length()<2 )return src;
        StringBuilder sb = new StringBuilder();
        for( int i=0; i<src.length(); i++ ){
            sb.insert(0,src.charAt(i));
        }
        return sb.toString();
    }

    @Test
    public void test01(){
        BasicEventList<String> srcList = new BasicEventList<>();
        srcList.add("abc");
        srcList.add("abcdef");

        System.out.println("source list: ");
        srcList.forEach(System.out::println);

        MappedList<String,String> mappedList = new MappedList<>(
            srcList, this::reverse, this::reverse
        );

        System.out.println("\nmapped list: ");
        mappedList.forEach(System.out::println);

        String add1 = "qwerty";
        System.out.println("\nappend "+add1);
        srcList.add(add1);

        System.out.println("\nmapped list: ");
        mappedList.forEach(System.out::println);

        System.out.println("\nremove from result cba");
        mappedList.remove("cba");

        System.out.println("\nsource list: ");
        srcList.forEach(System.out::println);
    }
}
