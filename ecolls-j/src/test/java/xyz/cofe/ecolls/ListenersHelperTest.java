package xyz.cofe.ecolls;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;

public class ListenersHelperTest {
    public static class PEvent {
        public final String message;

        public PEvent(String message){
            this.message = message;
        }
    }

    public interface PListener {
        void pevent(PEvent ev);
    }

    public static class Publisher {
        private final ListenersHelper<PListener,PEvent> lh = new ListenersHelper<>(PListener::pevent);

        public boolean hasListener(PListener listener){
            return lh.hasListener(listener);
        }

        public AutoCloseable addListener(PListener listener){
            return lh.addListener(listener);
        }

        public AutoCloseable addListener(PListener listener, boolean weakLink){
            return lh.addListener(listener, weakLink);
        }

        public AutoCloseable addListener(PListener listener, boolean weakLink, int limitCalls){
            return lh.addListener(listener, weakLink, limitCalls);
        }

        public void removeListener(PListener listener){
            lh.removeListener(listener);
        }

        public void removeAllListeners(){
            lh.removeAllListeners();
        }

        public void fireEvent(PEvent event){
            lh.fireEvent(event);
        }

        public void call1(String message){
            System.out.println(message);
            fireEvent(new PEvent("call1()"));
        }
    }

    @Test
    public void test01(){
        AtomicInteger cnt1 = new AtomicInteger(0);
        AtomicInteger cnt2 = new AtomicInteger(0);

        Publisher pub = new Publisher();
        pub.addListener(e -> {
            System.out.println("limited call");
            cnt1.incrementAndGet();
        }, false, 3);
        pub.addListener(e -> {
            System.out.println("unlimited call");
            cnt2.incrementAndGet();
        }, false);

        for( int i=0; i<10; i++ ){
            pub.call1("calling "+i);
        }

        Assert.assertTrue( cnt1.get()==3 );
        Assert.assertTrue( cnt2.get()>3 );
    }
}
