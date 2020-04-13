package xyz.cofe.text.tparse.internal;

public class HListTest {
    public void test01(){
        HList<HList<HList<HList<Void,Integer>,String>,Boolean>,Long> x =
        HList.of(1).plus("string").plus(true).plus(12L);
    }
}
