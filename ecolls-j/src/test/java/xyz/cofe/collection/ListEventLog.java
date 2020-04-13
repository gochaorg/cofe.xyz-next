package xyz.cofe.collection;

import org.junit.Assert;
import xyz.cofe.fn.TripleConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ListEventLog<C extends EventList<E>, E> {
    public ListEventLog listen(EventList<E> elist){
        Function<String,TripleConsumer<Integer,E,E>> logListener = (prefix) -> {
            TripleConsumer<Integer,E,E> ls = (idx, old, cur) -> {
                System.out.println(prefix+" "+elist.scn()+" ["+idx+"] \""+old+"\" => \""+cur+"\"");
            };
            return ls;
        };

        elist.onInserted(logListener.apply("inserted"));
        elist.onUpdated(logListener.apply("updated"));
        elist.onDeleted(logListener.apply("deleted"));
        elist.addCollectionListener(new CollectionListener<EventList<E>, E>() {
            @Override
            public void collectionEvent(CollectionEvent<EventList<E>, E> event) {
                events.add(event);
            }
        });

        elist.onScn((oldscn,curscn,cause)->{
            System.out.println("scn changed "+oldscn+" => "+curscn+" cause:"+cause);
        });

        return this;
    }

    protected List<CollectionEvent> events = new ArrayList<>();

    public List<CollectionEvent> getEvents() {
        return events;
    }

    public static class Pattern<E> {
        public Pattern(Class event, E element) {
            this.element = element;
            this.event = event;
        }

        public Pattern(Class event, int index, E element) {
            this.element = element;
            this.event = event;
            this.index = index;
        }

        public Pattern(Class event, int index, E element, E oldElement) {
            this.element = element;
            this.event = event;
            this.index = index;
            this.oldElement = oldElement;
        }

        protected Integer index;
        public Integer getIndex() { return index; }
        public void setIndex(Integer index) { this.index = index; }

        protected E element;
        public E getElement() { return element; }
        public void setElement(E element) { this.element = element; }

        protected E oldElement;
        public E getOldElement() { return oldElement; }
        public void setOldElement(E element) { this.oldElement = element; }

        protected Class event;
        public Class getEvent() { return event; }
        public void setEvent(Class event) { this.event = event; }
    }

    public static class PatternBuilder<E> {
        public PatternBuilder(List<CollectionEvent> events) {
            if( events == null ) throw new IllegalArgumentException("events == null");
            this.events = events;
        }

        protected List<Pattern<E>> pattern = new ArrayList<>();
        protected List<CollectionEvent> events = new ArrayList<>();

        public PatternBuilder<E> inserted(E e) {
            pattern.add(new Pattern<>(InsertedEvent.class, e));
            return this;
        }

        public PatternBuilder<E> inserted(int index,E e) {
            pattern.add(new Pattern<>(InsertedEvent.class, index, e));
            return this;
        }

        public PatternBuilder<E> updated(E e) {
            pattern.add(new Pattern<>(UpdatedEvent.class, e));
            return this;
        }

        public PatternBuilder<E> updated(int index, E e) {
            pattern.add(new Pattern<>(UpdatedEvent.class, index, e));
            return this;
        }

        public PatternBuilder<E> updated(int index, E e, E eOld) {
            pattern.add(new Pattern<>(UpdatedEvent.class, index, e, eOld));
            return this;
        }

        public PatternBuilder<E> deleted(E e) {
            pattern.add(new Pattern<>(DeletedEvent.class, e));
            return this;
        }

        public PatternBuilder<E> deleted(int index, E e) {
            pattern.add(new Pattern<>(DeletedEvent.class, index, e));
            return this;
        }

        public boolean match() {
            Assert.assertTrue("pattern size more than event log", events.size() >= pattern.size());
            for( int idx = 0; idx<pattern.size(); idx++ ){
                E ptrnElement = pattern.get(idx).getElement();
                E ptrnOldElement = pattern.get(idx).getOldElement();
                Class ptrnCls = pattern.get(idx).getEvent();
                Integer ptrnIdx = pattern.get(idx).getIndex();

                int eventIdx = events.size()-pattern.size()+idx;

                CollectionEvent ce = events.get(eventIdx);

                Assert.assertTrue("collected event is null", ce != null);

                Assert.assertTrue("event["+eventIdx+"] class="+ce.getClass().getName()+" need="+ptrnCls.getName(),
                    ptrnCls.isAssignableFrom(ce.getClass())
                );

                if( ce instanceof UpdatedEvent ){

                }else
                if( ptrnCls.isAssignableFrom(InsertedEvent.class) && ce instanceof InsertedEvent){
                    Assert.assertTrue("event[" + eventIdx + "] class=" + ce.getClass().getSimpleName() + " need=" + ptrnElement,
                        Objects.equals(ptrnElement, ((InsertedEvent) ce).getNewItem())
                    );
                }else if( ce instanceof DeletedEvent && ptrnCls.isAssignableFrom(DeletedEvent.class) ){
                    Assert.assertTrue("event[" + eventIdx + "] class=" + ce.getClass().getSimpleName() + " need=" + ptrnElement,
                        Objects.equals(ptrnElement, ((DeletedEvent) ce).getOldItem())
                    );
                }

                if( ce instanceof ItemIndex && ptrnIdx!=null ){
                    Object ceIdx = ((ItemIndex)ce).getIndex();
                    Assert.assertTrue(
                        "event["+eventIdx+"] index="+ceIdx+" need="+ptrnIdx,
                        Objects.equals(ceIdx,ptrnIdx)
                    );
                }

                if( ptrnCls.isAssignableFrom(UpdatedEvent.class) && ptrnOldElement!=null && ce instanceof UpdatedEvent ){
                    Assert.assertTrue(
                        "event["+eventIdx+"] old updated not matched",
                        Objects.equals(((UpdatedEvent)ce).getOldItem(), ptrnOldElement)
                    );
                }
            }
            return false;
        }
    }

    public PatternBuilder<E> pattern(){
        return new PatternBuilder<>(getEvents());
    }
}
