package xyz.cofe.mapitf;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

public class MapperTest {
    @Test
    public void test01(){
        LinkedHashMap<String,@Nullable Object> vals = new LinkedHashMap<>();
        vals.put("num",10);
        vals.put("str","abc");

        Simple s = Mapper.bind(vals, Simple.class);
        System.out.println(s);

        s.num(12);
        System.out.println(s);

        s.str("xcvb");
        System.out.println(s);
    }

    @Test
    public void test02(){
        LinkedHashMap<String,@Nullable Object> vals = new LinkedHashMap<>();
        Compaund cmpd = Mapper.bind(vals, Compaund.class);

        System.out.println(cmpd.simples());
        System.out.println(cmpd);

        Simple s = cmpd.create();
        cmpd.simples().add(s);
        System.out.println(cmpd);

        s.str("adv");
        System.out.println(cmpd);
    }

    @Test
    public void test03(){
        LinkedHashMap<String,@Nullable Object> vals = new LinkedHashMap<>();
        RefCompaund ref1 = Mapper.bind(vals, RefCompaund.class);
        System.out.println("ref1="+ref1);

        RefCompaund ref2 = ref1.createRef();
        System.out.println("ref2="+ref2);

        ref1.refs().add(ref2);
        System.out.println("ref1="+ref1);

        ref2.refs().add(ref1);
        System.out.println("ref2="+ref2);

        ref1.str("asd");
        System.out.println("ref1="+ref1);
        System.out.println("ref2="+ref2);
    }
}
