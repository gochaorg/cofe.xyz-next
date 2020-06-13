package xyz.cofe.collection;

import org.junit.Assert;
import org.junit.Test;
import xyz.cofe.fn.Pair;
import xyz.cofe.fn.TripleConsumer;
import xyz.cofe.fn.Tuple3;
import xyz.cofe.fn.Tuple4;
import xyz.cofe.iter.Eterable;

import java.util.*;

public class PreEventListTest {
    public void dump( List<?> lst ){
        if( lst.size()>0 ){
            System.out.println("dump {");
            int idx = -1;
            for( Object o : lst ){
                idx++;
                System.out.println("  [" + idx + "]=" + o);
            }
            System.out.println("}");
        }else{
            System.out.println("dump {}");
        }
    }
    public <T> void listing( StdEventList<T> list ){
        list.onInserting( (idx,old,cur)->{
            System.out.println("inserting ["+idx+"] "+old+" => "+cur);
        });
        list.onUpdating( (idx,old,cur)->{
            System.out.println("updating ["+idx+"] "+old+" => "+cur);
        });
        list.onDeleting( (idx,old,cur)->{
            System.out.println("deleting ["+idx+"] "+old+" => "+cur);
        });

        list.onInserted( (idx,old,cur)->{
            System.out.println("inserted ["+idx+"] "+old+" => "+cur);
        });
        list.onUpdated( (idx,old,cur)->{
            System.out.println("updated ["+idx+"] "+old+" => "+cur);
        });
        list.onDeleted( (idx,old,cur)->{
            System.out.println("deleted ["+idx+"] "+old+" => "+cur);
        });
    }

    public enum Action {
        INSERTING(true,false), INSERTED(false,true),
        UPDATING(true,false), UPDATED(false,true),
        DELETING(true,false), DELETED(false,true);

        public final boolean before;
        public final boolean after;

        Action(boolean before, boolean after){
            this.before = before;
            this.after = after;
        }
    }
    public <T> TripleConsumer<Integer,T,T> addLog( List<Tuple4<Action,Integer,T,T>> log, Action action ){
        return (idx, old, cur) -> {
            log.add( Tuple4.of(action, idx, old, cur) );
        };
    }
    public <T> void logging( List<Tuple4<Action,Integer,T,T>> log, PreEventList<T> list ){
        list.onInserting( addLog(log, Action.INSERTING) );
        list.onInserted( addLog(log, Action.INSERTED) );

        list.onUpdating( addLog(log, Action.UPDATING) );
        list.onUpdated( addLog(log, Action.UPDATED) );

        list.onDeleting( addLog(log, Action.DELETING) );
        list.onDeleted( addLog(log, Action.DELETED) );
    }

    public <T> void checkOrder( Iterable<Tuple4<Action,Integer,T,T>> log ){
        int before = -1;
        int after = -1;
        int idx = -1;
        for( Tuple4<Action,Integer,T,T> ev : log ){
            idx++;
            if( ev.a().before ){
                if( before<0 ){
                    before = idx;
                }
                if( after>=0 && before>after ){
                    throw new IllegalStateException("checkOrder");
                }
            }else if( ev.a().after ){
                if( after<0 ){
                    after = idx;
                }
                if( before>=0 && before>after ){
                    throw new IllegalStateException("checkOrder");
                }
            }
        }
    }
    public <T> void symetricCheck( Iterable<Tuple4<Action,Integer,T,T>> log ){
        Map<Action,Action> directRel = new LinkedHashMap<>();
        Map<Action,Action> backRel = new LinkedHashMap<>();

        directRel.put(Action.INSERTING, Action.INSERTED);
        backRel.put(Action.INSERTED, Action.INSERTING);

        directRel.put(Action.UPDATING, Action.UPDATED);
        backRel.put(Action.UPDATED, Action.UPDATING);

        directRel.put(Action.DELETING, Action.DELETED);
        backRel.put(Action.DELETED, Action.DELETING);

        Map<Action, Map<Integer, Pair<T,T>>> events = new LinkedHashMap<>();

        for( Tuple4<Action,Integer,T,T> ev : log ){
            Action act = ev.a();
            if( directRel.containsKey(act) ){
                Map<Integer, Pair<T,T>> typedEv = events.computeIfAbsent(act, a->new LinkedHashMap<>());

                Integer idx = ev.b();
                if( typedEv.containsKey(idx) ){
                    System.err.println("duplicate idx="+idx);
                }
                typedEv.put(idx, Pair.of(ev.c(), ev.d()));
            }else if( backRel.containsKey(act) ){
                Action dirEv = backRel.get(act);

                Map<Integer, Pair<T,T>> typedEv = events.get(dirEv);
                if( typedEv==null ){
                    throw new IllegalStateException("expected event="+dirEv+" not found");
                }

                Integer idx = ev.b();
                if( !typedEv.containsKey(idx) ){
                    throw new IllegalStateException("expected event="+dirEv+" with idx="+idx+" not found");
                }

                Pair<T,T> prvValues = typedEv.get(idx);
                Pair<T,T> curValues = Pair.of(ev.c(), ev.d());

                if( !Objects.equals(prvValues.a(), curValues.a()) ){
                    throw new IllegalStateException("values not equals");
                }

                if( !Objects.equals(prvValues.b(), curValues.b()) ){
                    throw new IllegalStateException("values not equals");
                }

                typedEv.remove(idx);
            }
        }

        long cnt = events.values().stream().filter( m -> m.size()>0 ).count();
        if( cnt>0 ){
            throw new IllegalStateException("not found pair events");
        }
    }

    public <T> void checks( List<Tuple4<Action,Integer,T,T>> log, boolean dropLog ){
        checkOrder(log);
        symetricCheck(log);
        if( dropLog ){
            log.clear();
        }
    }

    @Test
    public void test01(){
        BasicEventList<Tuple4<Action,Integer,String,String>> log = new BasicEventList<>();

        StdEventList<String> list = new StdEventList<>();
        listing(list);
        logging(log,list);

        System.out.println("----");
        list.add("abc");
        checks(log,true);

        System.out.println("----");
        list.addAll(Arrays.asList("def","rfg"));
        checks(log,true);

        System.out.println("----");
        list.set(1,"abcBADIxc");
        checks(log,true);

        System.out.println("----");
        dump(list);
        list.remove("abc");
        checks(log,true);

        System.out.println("----");
        list.remove(0);
        checks(log,true);

        System.out.println("----");
        list.clear();
        checks(log,true);
        dump(list);

        System.out.println("----");
        list.addAll(Arrays.asList("A","B"));
        checks(log,true);

        list.addAll(0,Arrays.asList("a","a","a","a","b"));
        checks(log,true);

        list.add(1,"C");
        checks(log,true);

        dump(list);

        list.removeAll( Arrays.asList("a") );
        checks(log,true);
        dump(list);

        list.retainAll( Arrays.asList("A","B") );
        checks(log,true);
        dump(list);

        list.replaceAll( a -> a.equals("A") ? "C" : a );
        checks(log,true);
        dump(list);

        list.removeIf( a -> a.equals("C") );
        checks(log,true);
        dump(list);
    }

    @Test
    public void test02(){
        StdEventList<String> list = new StdEventList<>();
        listing(list);

        list.onInserting( (idx,old,cur)->{
            if( "asd".equals(cur) ){
                throw new Error("forbidden");
            }
        });

        list.add("abc");
        list.add("qwe");

        boolean catched = false;
        try {
            list.add("asd");
        } catch( Error err ){
            System.out.println("catch "+err.getMessage());
            catched = true;
        }
        Assert.assertTrue(catched);

        list.add("sdf");
        dump(list);
    }
}
