package xyz.cofe.notbad;

import org.junit.Test;
import xyz.cofe.collection.tree.TreeTest2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

public class ChainMethodSample {
    public static class Man {
        // Конструктор по умолчанию
        public Man(){}

        // Конструктор копирования
        public Man(Man sample){
            if( sample!=null ){
                this.name = sample.name;
                this.age = sample.age;
            }
        }

        // Создание клона
        public Man clone(){ return new Man(this); }

        private String name;
        public String getName(){ return name; }
        public void setName( String name ){ this.name = name; }
        public Man name( String name ){ this.name = name; return this; }

        private int age;
        public int getAge(){ return age; }
        public void setAge( int age ){ this.age = age; }
        public Man age( int age ){ this.age = age; return this; }

        public String toString(){
            return name+" "+age;
        }
    }

    private void println( Object ... args ){
        for(Object arg:args){
            System.out.print(arg);
        }
        System.out.println();
    }

    @Test
    public void test01(){
        Man n = new Man().name( "Vasya" ).age( 18 );
        println( n );
        println( n.clone().name("Petya").age(19) );
    }

    public static class TreeString {
        protected String value;
        public String getValue(){ return value; }
        public void setValue(String value){ this.value = value; }

        protected final List<TreeString> children = new ArrayList<>();
        public int getChildCount(){ return children.size(); }
        public TreeString getChild(int index){ return children.get(index); }
        public void appendChild( TreeString str ){ children.add(str); }
    }

    public static class Builder {
        protected TreeString createNode(){ return new TreeString(); }
        protected Queue<Consumer<TreeString>> builders = new ArrayDeque<>();

        public Builder val(String str){
            builders.add(n ->n.setValue(str));
            return this;
        }
        public Builder add(String str){
            builders.add(n -> {
                TreeString c = createNode();
                c.setValue(str);
                n.appendChild(c);
            });
            return this;
        }
        public Builder add(String str, Consumer<Builder> child){
            builders.add(n -> {
                TreeString c = createNode();
                c.setValue(str);
                n.appendChild(c);
                if( child!=null ){
                    Builder b = new Builder();
                    child.accept(b);
                    b.apply(c);
                }
            });
            return this;
        }
        public TreeString apply(TreeString t){
            if( t == null )throw new IllegalArgumentException( "t == null" );
            builders.forEach(x->x.accept(t));
            return t;
        }

        public TreeString build(){
            TreeString t = createNode();
            builders.forEach(x->x.accept(t));
            return t;
        }
    }

    @Test
    public void treeBuild(){
        Builder bld = new Builder()
            .val("a")
            .add("b", c -> c
                    .add("d")
                    .add("e"))
            .add("d")
            .add("c");
    }
}
