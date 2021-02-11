package xyz.cofe.collection;

import java.util.Arrays;
import org.junit.Test;

public class SampleEvListTest {
    @Test
    public void test01(){
        BasicEventList<String> evlist = new BasicEventList<>();
        evlist.addCollectionListener(new CollectionListener<EventList<String>, String>() {
            @Override
            public void collectionEvent(CollectionEvent<EventList<String>, String> event){
                if( event!=null ){
                    System.out.println("  event:");
                    System.out.println("    class: "+event.getClass());
                    Arrays.stream(event.getClass().getGenericInterfaces()).forEach( t ->
                        System.out.println("    itf: "+t)
                    );
                    if( event instanceof InsertedEvent ){
                        InsertedEvent<EventList<String>,Integer,String> insEv = (InsertedEvent<EventList<String>,Integer,String>)event;
                        System.out.println("  inserted:");
                        System.out.println("    new item: "+insEv.getNewItem());
                        System.out.println("    key: "+insEv.getIndex());
                    }
                    if( event instanceof UpdatedEvent ){
                        UpdatedEvent<EventList<String>,Integer,String> updEv = (UpdatedEvent<EventList<String>, Integer, String>) event;
                        System.out.println("  updated:");
                        System.out.println("    new item: "+updEv.getNewItem());
                        System.out.println("    old item: "+updEv.getOldItem());
                        System.out.println("    key: "+updEv.getIndex());
                    }
                    if( event instanceof DeletedEvent ){
                        DeletedEvent<EventList<String>,Integer,String> dltEv = (DeletedEvent<EventList<String>, Integer, String>) event;
                        System.out.println("  deleted:");
                        System.out.println("    old item: "+dltEv.getOldItem());
                        System.out.println("    key: "+dltEv.getIndex());
                    }
                    if( event instanceof RemovedEvent ){
                        RemovedEvent<EventList<String>,String> addEv = (RemovedEvent<EventList<String>, String>) event;
                        System.out.println("  removed:");
                        System.out.println("    old item: "+addEv.getOldItem());
                    }
                    if( event instanceof AddedEvent ){
                        AddedEvent<EventList<String>,String> addEv = (AddedEvent<EventList<String>, String>) event;
                        System.out.println("  added:");
                        System.out.println("    new item: "+addEv.getNewItem());
                    }
                }
            }
        });

        System.out.println("add( \"abc\" )");
        evlist.add("abc");

        System.out.println("addAll( \"bcd\", \"cde\" )");
        evlist.addAll(Arrays.asList("bcd","cde"));

        System.out.println("set( 0, \"def\" )");
        evlist.set(0,"def");

        System.out.println("remove( 0 )");
        evlist.remove(0);

        System.out.println("clear");
        evlist.clear();
    }
}
