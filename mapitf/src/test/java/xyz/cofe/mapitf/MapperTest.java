package xyz.cofe.mapitf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @Test
    public void test04(){
        Map<String,@Nullable Object> root = new LinkedHashMap<>();
        root.put("str","hello");

        List<Map<String,@Nullable Object>> simples = new ArrayList<>();
        Map<String,@Nullable Object> simple1 = new LinkedHashMap<>();
        simple1.put("num", 1);
        simple1.put("str", "abc");

        Map<String,@Nullable Object> simple2 = new LinkedHashMap<>();
        simple2.put("num", 2);
        simple2.put("str", "def");

        simples.add(simple1);
        simples.add(simple2);

        root.put("simples",simples);

        RefCompaund rootRef = Mapper.bind(root, RefCompaund.class);
        System.out.println(rootRef.str());
        rootRef.simples().forEach( s -> {
            System.out.println("str="+s.str());
            System.out.println("num="+s.num());
        });

        ObjectMapper om = new ObjectMapper();
        om.configure(SerializationFeature.INDENT_OUTPUT,true);
        try {
            System.out.println(om.writeValueAsString(root));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
