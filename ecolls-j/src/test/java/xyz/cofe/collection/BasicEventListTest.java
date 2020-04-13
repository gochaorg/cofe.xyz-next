package xyz.cofe.collection;

import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.*;

public class BasicEventListTest {
    private void dump(List<String> lst){
        if( lst.isEmpty() )return;
        if( lst.size()==1 ){
            System.out.println("dump ["+lst.get(0)+"]");
            return;
        }
        System.out.println("dump ["+
                           lst.stream().reduce(
                               (a,b)->a+","+b).orElse("")+
                           "]");
    }

    @Test
    public void testAdd(){
        BasicEventList<String> elist = new BasicEventList<>();

        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.add("abc");
        elist.add("bcd");
        elist.add(0,"cde");
        elist.addAll(Arrays.asList("def","efg","fgh"));
        elist.addAll(0,Arrays.asList("ghi","hij","ijk"));

        dump(elist);
        evLog.pattern()
             .inserted(0,"abc")
             .inserted(1,"bcd")
             .inserted(0,"cde")
             .inserted(3,"def")
             .inserted(4,"efg")
             .inserted(5,"fgh")
             .inserted(0,"ghi")
             .inserted(1,"hij")
             .inserted(2,"ijk")
             .match();
    }

    @Test
    public void testSet(){
        BasicEventList<String> elist = new BasicEventList<>();

        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll(Arrays.asList("abc","bcd","cde"));
        elist.set(1,"def");
        dump(elist);

        evLog.pattern().updated(1,"def", "bcd").match();
    }

    @Test
    public void testClear(){
        BasicEventList<String> elist = new BasicEventList<>();

        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll(Arrays.asList("abc","bcd","cde"));
        elist.clear();
        dump(elist);

        evLog.pattern()
             .deleted(2,"cde")
             .deleted(1,"bcd")
             .deleted(0,"abc")
             .match();
    }

    @Test
    public void testRemoveE(){
        BasicEventList<String> elist = new BasicEventList<>();

        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll(Arrays.asList("abc","bcd","cde","abc"));
        ArrayList<String> alist = new ArrayList<>(elist);

        elist.remove("abc");
        alist.remove("abc");
        dump(elist);

        evLog.pattern()
             .deleted(0,"abc")
             .match();

        Assert.assertTrue(elist.size()==alist.size());

        elist.remove(1);
        evLog.pattern().deleted(1,"cde").match();
    }

    @Test
    public void testRemove(){
        BasicEventList<String> elist = new BasicEventList<>();

        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll(Arrays.asList("abc","bcd","cde","abc"));
        ArrayList<String> alist = new ArrayList<>(elist);

        elist.remove("abc");
        alist.remove("abc");
        dump(elist);

        evLog.pattern()
             .deleted(0,"abc")
             .match();

        Assert.assertTrue(elist.size()==alist.size());

        elist.remove(1);
        evLog.pattern().deleted(1,"cde").match();
    }

    @Test
    public void testRemoveAll(){
        BasicEventList<String> elist = new BasicEventList<>();

        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll(Arrays.asList("abc","bcd","cde","abc"));
        ArrayList<String> alist = new ArrayList<>(elist);
        elist.removeAll(Collections.singleton("abc"));
        alist.removeAll(Collections.singleton("abc"));

        Assert.assertTrue(elist.size()==alist.size());
        Assert.assertTrue(elist.size()==2);

        evLog.pattern()
             .deleted(3,"abc")
             .deleted(0,"abc")
             .match();
    }

    @Test
    public void testRetain(){
        BasicEventList<String> elist = new BasicEventList<>();

        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll(Arrays.asList("abc","bcd","cde","abc"));
        ArrayList<String> alist = new ArrayList<>(elist);
        List<String> retlst = Arrays.asList("cde","xyz");
        elist.retainAll(retlst);
        alist.retainAll(retlst);

        Assert.assertTrue(elist.size()==alist.size());
        dump(elist);
//        Assert.assertTrue(elist.size()==2);
//
//        evLog.pattern()
//             .deleted(3,"abc")
//             .deleted(0,"abc")
//             .match();
    }

    @Test
    public void iterator01(){
        System.out.println("iterator01");
        System.out.println("========================");

        BasicEventList<String> elist = new BasicEventList<>();
        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll( Arrays.asList("a","b","c","a","b") );

        Iterator<String> itr = elist.iterator();
        itr.next();
        itr.remove();

        dump(elist);
        evLog.pattern()
             .deleted(0,"a")
             .match();
    }

    @Test
    public void iterator02(){
        System.out.println("iterator02");
        System.out.println("========================");

        BasicEventList<String> elist = new BasicEventList<>();
        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll( Arrays.asList("a","b","c","a","b") );

        Iterator<String> itr = elist.iterator();
        itr.next();
        itr.next();
        itr.remove();

        dump(elist);
        evLog.pattern()
             .deleted(1,"b")
             .match();
    }

    @Test
    public void iterator03(){
        System.out.println("iterator03");
        System.out.println("========================");

        BasicEventList<String> elist = new BasicEventList<>();
        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll( Arrays.asList("a","b","c","a","b") );
        ArrayList<String> alist = new ArrayList<>(elist);

        Iterator<String> itr = elist.iterator();
        itr.next();
        itr.remove();
        itr.next();
        itr.remove();

        dump(elist);

        itr = alist.iterator();
        itr.next();
        itr.remove();
        itr.next();
        itr.remove();

        dump(alist);
        Assert.assertArrayEquals(alist.toArray(),elist.toArray());
    }

    @Test
    public void iterator04(){
        System.out.println("iterator04");
        System.out.println("========================");

        BasicEventList<String> elist = new BasicEventList<>();
        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll( Arrays.asList("a","b","c","x","y") );
        ArrayList<String> alist = new ArrayList<>(elist);

        Assert.assertArrayEquals(alist.toArray(),elist.toArray());
        dump(alist);

        System.out.println("elist iter");
        ListIterator<String> itr = elist.listIterator();
        String e1 = itr.next();
        String e2 = itr.next();
        String e3 = itr.previous();
        System.out.println(e1);
        System.out.println(e2);
        System.out.println(e3);
        //itr.remove();

        System.out.println("alist iter");
        itr = alist.listIterator();
        String a1 = itr.next();
        String a2 = itr.next();
        String a3 = itr.previous();
        System.out.println(a1);
        System.out.println(a2);
        System.out.println(a3);
        //itr.remove();

        assertTrue(Objects.equals(a1,e1));
        assertTrue(Objects.equals(a2,e2));
        assertTrue(Objects.equals(a3,e3));

        dump(elist);
        dump(alist);

        itr = elist.listIterator();
        e1 = itr.next();
        e2 = itr.next();
        itr.remove();
        itr.next();
        itr.add("r");
        e3 = itr.next();
        itr.set("R");

        System.out.println("e1="+e1);
        System.out.println("e2="+e2);
        System.out.println("e3="+e3);
        dump(elist);

        itr = alist.listIterator();
        a1 = itr.next();
        a2 = itr.next();
        itr.remove();
        itr.next();
        itr.add("r");
        a3 = itr.next();
        itr.set("R");

        System.out.println("a1="+a1);
        System.out.println("a2="+a2);
        System.out.println("a3="+a3);
        dump(alist);

        evLog.pattern()
             .deleted("b").inserted("r").updated("R")
             .match();

        Assert.assertArrayEquals(alist.toArray(),elist.toArray());
    }

    @Test
    public void subList01_clear(){
        System.out.println("subList01_clear");
        System.out.println("========================");

        BasicEventList<String> elist = new BasicEventList<>();
        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll( Arrays.asList("a","b","c","x","y") );
        ArrayList<String> alist = new ArrayList<>(elist);

        elist.subList(1, 1+3).clear();
        alist.subList(1, 1+3).clear();

        dump(elist);
        dump(alist);
        Assert.assertArrayEquals(alist.toArray(),elist.toArray());

        evLog.pattern()
             .deleted("x").deleted("c").deleted("b")
             .match();
    }

    @Test
    public void subList02_add(){
        System.out.println("subList02_add");
        System.out.println("========================");

        BasicEventList<String> elist = new BasicEventList<>();
        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll( Arrays.asList("a","b","c","x","y") );
        ArrayList<String> alist = new ArrayList<>(elist);

        elist.subList(1, 1+3).add( "add" );
        alist.subList(1, 1+3).add( "add" );

        dump(elist);
        dump(alist);
        Assert.assertArrayEquals(alist.toArray(),elist.toArray());
    }

    @Test
    public void subList03_add_ie(){
        System.out.println("subList03_add_ie");
        System.out.println("========================");

        BasicEventList<String> elist = new BasicEventList<>();
        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll( Arrays.asList("a","b","c","x","y") );
        ArrayList<String> alist = new ArrayList<>(elist);

        elist.subList(1, 1+3).add( 0,"add" );
        alist.subList(1, 1+3).add( 0,"add" );

        dump(elist);
        dump(alist);
        Assert.assertArrayEquals(alist.toArray(),elist.toArray());
    }
//
    @Test
    public void subList_addAll(){
        System.out.println("subList_addAll");
        System.out.println("========================");

        BasicEventList<String> elist = new BasicEventList<>();
        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll( Arrays.asList("a","b","c","x","y") );
        ArrayList<String> alist = new ArrayList<>(elist);

        elist.subList(1, 1+3).addAll( Arrays.asList("e","f","g") );
        alist.subList(1, 1+3).addAll( Arrays.asList("e","f","g") );

        dump(elist);
        dump(alist);
        Assert.assertArrayEquals(alist.toArray(),elist.toArray());
    }

    @Test
    public void subList_addAll_i(){
        System.out.println("subList_addAll_i");
        System.out.println("========================");

        BasicEventList<String> elist = new BasicEventList<>();
        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll( Arrays.asList("a","b","c","x","y") );
        ArrayList<String> alist = new ArrayList<>(elist);

        elist.subList(1, 1+3).addAll( 0, Arrays.asList("e","f","g") );
        alist.subList(1, 1+3).addAll( 0, Arrays.asList("e","f","g") );

        dump(elist);
        dump(alist);
        Assert.assertArrayEquals(alist.toArray(),elist.toArray());
    }

    @Test
    public void subList_set(){
        System.out.println("subList_addAll_i");
        System.out.println("========================");

        BasicEventList<String> elist = new BasicEventList<>();
        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll( Arrays.asList("a","b","c","x","y") );
        ArrayList<String> alist = new ArrayList<>(elist);

        elist.subList(1, 1+3).set(1, "set");
        alist.subList(1, 1+3).set(1, "set");

        dump(elist);
        dump(alist);
        Assert.assertArrayEquals(alist.toArray(),elist.toArray());
    }

    @Test
    public void subList_remove(){
        System.out.println("subList_remove");
        System.out.println("========================");

        BasicEventList<String> elist = new BasicEventList<>();
        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll( Arrays.asList("a","b","c","x","y") );
        ArrayList<String> alist = new ArrayList<>(elist);

        elist.subList(1, 1+3).remove("c");
        alist.subList(1, 1+3).remove("c");

        dump(elist);
        dump(alist);
        Assert.assertArrayEquals(alist.toArray(),elist.toArray());
    }

    @Test
    public void subList_removeAll(){
        System.out.println("subList_removeAll");
        System.out.println("========================");

        BasicEventList<String> elist = new BasicEventList<>();
        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll( Arrays.asList("a","b","c","x","y","c") );
        ArrayList<String> alist = new ArrayList<>(elist);

        elist.subList(1, 1+3).removeAll( Arrays.asList("c") );
        alist.subList(1, 1+3).removeAll( Arrays.asList("c") );

        dump(elist);
        dump(alist);
        Assert.assertArrayEquals(alist.toArray(),elist.toArray());
    }

    @Test
    public void subList_retainAll(){
        System.out.println("subList_retainAll");
        System.out.println("========================");

        BasicEventList<String> elist = new BasicEventList<>();
        ListEventLog<BasicEventList<String>,String> evLog = new ListEventLog<>();
        evLog.listen(elist);

        elist.addAll( Arrays.asList("a","b","c","x","y","c") );
        ArrayList<String> alist = new ArrayList<>(elist);

        elist.subList(1, 1+3).retainAll(Arrays.asList("c") );
        alist.subList(1, 1+3).retainAll(Arrays.asList("c") );

        dump(elist);
        dump(alist);
        Assert.assertArrayEquals(alist.toArray(),elist.toArray());
    }
//
//    @Test
//    public void subList_iterator(){
//        System.out.println("subList_iterator");
//        System.out.println("========================");
//
//        list.clear();
//
//        list.addAll( Arrays.asList("a","b","c","a","b") );
//        counter.reset();
//
//        Iterator itr = list.subList(1, 1+3).iterator();
//        itr.next();
//        itr.remove();
//
//        System.out.println(list);
//        System.out.println(counter);
//        assertTrue(counter!=null);
////        assertTrue(counter.addCalls==1);
////        assertTrue(counter.positionalAddCalls==counter.addCalls);
//        assertTrue(counter.removeCalls==1);
//        assertTrue(counter.positionalRemoveCalls==counter.removeCalls);
//    }
//
//
//    @Test
//    public void subList_listIterator_add(){
//        System.out.println("subList_iterator");
//        System.out.println("========================");
//
//        list.clear();
//
//        list.addAll( Arrays.asList("a","b","c","a","b") );
//        counter.reset();
//
//        ListIterator itr = list.subList(1, 1+3).listIterator();
//        itr.next();
//        itr.add("add");
//
//        System.out.println(list);
//        System.out.println(counter);
//        assertTrue(counter!=null);
//        assertTrue(counter.addCalls==1);
//        assertTrue(counter.positionalAddCalls==counter.addCalls);
////        assertTrue(counter.removeCalls==1);
////        assertTrue(counter.positionalRemoveCalls==counter.removeCalls);
//    }
//
//    @Test
//    public void subList_listIterator_remove(){
//        System.out.println("subList_iterator");
//        System.out.println("========================");
//
//        list.clear();
//
//        list.addAll( Arrays.asList("a","b","c","a","b") );
//        counter.reset();
//
//        ListIterator itr = list.subList(1, 1+3).listIterator();
//        itr.next();
//        itr.remove();
//
//        System.out.println(list);
//        System.out.println(counter);
//        assertTrue(counter!=null);
////        assertTrue(counter.addCalls==1);
////        assertTrue(counter.positionalAddCalls==counter.addCalls);
//        assertTrue(counter.removeCalls==1);
//        assertTrue(counter.positionalRemoveCalls==counter.removeCalls);
//    }
//
//    @Test
//    public void subList_listIterator_set(){
//        System.out.println("subList_iterator");
//        System.out.println("========================");
//
//        list.clear();
//
//        list.addAll( Arrays.asList("a","b","c","a","b") );
//        counter.reset();
//
//        ListIterator itr = list.subList(1, 1+3).listIterator();
//        itr.next();
//        itr.set("set");
//
//        System.out.println(list);
//        System.out.println(counter);
//        assertTrue(counter!=null);
////        assertTrue(counter.addCalls==1);
////        assertTrue(counter.positionalAddCalls==counter.addCalls);
//        assertTrue(counter.removeCalls==1);
//        assertTrue(counter.positionalRemoveCalls==counter.removeCalls);
//    }
}
