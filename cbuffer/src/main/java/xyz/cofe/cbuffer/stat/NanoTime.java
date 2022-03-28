package xyz.cofe.cbuffer.stat;

import java.util.Objects;

public class NanoTime implements Comparable<NanoTime>, Distance<NanoTime,NanoDuration> {
    public final long time;
    public NanoTime(){
        time = System.nanoTime();
    }
    public NanoTime(long t){
        time = t;
    }
    public NanoTime(NanoTime sample){
        if( sample==null )throw new IllegalArgumentException( "sample==null" );
        time = sample.time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NanoTime nanoTime = (NanoTime) o;
        return time == nanoTime.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(time);
    }

    @Override
    public int compareTo(NanoTime o) {
        if( o==null )return 0;
        return Long.compare(this.time, o.time);
    }

    @Override
    public NanoDuration distance(NanoTime a) {
        if( a==null )return null;
        return new NanoDuration(this.time - a.time);
    }
}
