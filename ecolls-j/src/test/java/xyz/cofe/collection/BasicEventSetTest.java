package xyz.cofe.collection;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;

public class BasicEventSetTest {
    public interface Pattern<A> {
        boolean match(CollectionEvent<EventSet<A>, A> ev);

        static <A> Pattern<A> added(A a) {
            return (ev)->{
                if( !(ev instanceof AddedEvent) ) return false;
                if( !(ev.getSource() instanceof EventSet) ) return false;
                if( !Objects.equals(((AddedEvent) ev).getNewItem(), a) ) return false;
                return true;
            };
        }
        static <A> Pattern<A> removed(A a) {
            return (ev)->{
                if( !(ev instanceof RemovedEvent) ) return false;
                if( !(ev.getSource() instanceof EventSet) ) return false;
                if( !Objects.equals(((RemovedEvent) ev).getOldItem(), a) ) return false;
                return true;
            };
        }
    }
    public static class PatternMatcher<A> {
        public final EvLog<A> evLog;
        public final List<Pattern<A>> patterns = new ArrayList<>();

        public PatternMatcher(EvLog<A> evLog) {
            this.evLog = evLog;
        }
        public PatternMatcher<A> added(A a) {
            patterns.add(Pattern.added(a));
            return this;
        }
        public PatternMatcher<A> removed(A a) {
            patterns.add(Pattern.removed(a));
            return this;
        }

        public boolean match() {
            if( patterns.size()>evLog.log.size() ) return false;
            for( int i = 0; i<patterns.size(); i++ ){
                int ti = evLog.log.size()-patterns.size()+i;
                if( !patterns.get(i).match(evLog.log.get(ti)) ) return false;
            }
            return true;
        }
    }
    public static class EvLog<A> {
        public final List<CollectionEvent<EventSet<A>, A>> log = new ArrayList<>();
        public EvLog<A> listen(EventSet<A> eset) {
            if( eset == null ) throw new IllegalArgumentException("eset == null");
            eset.addCollectionListener(log::add);
            eset.addCollectionListener(System.out::println);
            return this;
        }
        public void clear() {
            log.clear();
        }
        public PatternMatcher<A> pattern(){
            return new PatternMatcher<>(this);
        }
    }

    public boolean equals(Set s1, Set s2) {
        if( s1 == null ) throw new IllegalArgumentException("s1 == null");
        if( s2 == null ) throw new IllegalArgumentException("s2 == null");
        if( s1.size() != s2.size() ) return false;
        for( Object o1 : s1 ){
            if( !s2.contains(o1) ) return false;
        }
        return true;
    }
    void addfn(Set<String> set) {
        set.add("a");
        set.add("a");
        set.add("b");
        set.add("c");
        set.addAll(Arrays.asList("b", "e", "c"));
        set.addAll(Arrays.asList("d", "e", "f"));
    }
    void removefn(Set<String> set) {
        set.remove("b");
        set.removeAll(Arrays.asList("c", "d"));
    }

    @Test
    public void test01() {
        BasicEventSet<String> eset = new BasicEventSet<>();
        LinkedHashSet<String> contrlSet = new LinkedHashSet<>();

        EvLog<String> elog = new EvLog<String>().listen(eset);

        addfn(eset);
        addfn(contrlSet);
        Assert.assertTrue(equals(contrlSet, eset));
        Assert.assertTrue(
            elog.pattern()
                .added("a")
                .added("b")
                .added("c")
                .added("e")
                .added("d")
                .added("f")
                .match());
        elog.clear();

        removefn(eset);
        removefn(contrlSet);
        Assert.assertTrue(equals(contrlSet, eset));
        Assert.assertTrue(
            elog.pattern()
                .removed("b")
                .removed("c")
                .removed("d")
                .match());


    }
}
