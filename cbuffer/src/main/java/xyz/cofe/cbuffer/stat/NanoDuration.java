package xyz.cofe.cbuffer.stat;

import java.util.Objects;

public class NanoDuration implements Comparable<NanoDuration>, Duration<NanoDuration> {
    public final long time;
    public NanoDuration(long t){
        time = t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NanoDuration that = (NanoDuration) o;
        return time == that.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(time);
    }

    @Override
    public int compareTo(NanoDuration o) {
        if( o==null )return 0;
        return Long.compare(time, o.time);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("duration ");
        sb.append(time);
        return sb.toString();
    }

    @Override
    public NanoDuration add(NanoDuration nanoDuration) {
        if( nanoDuration==null )throw new IllegalArgumentException( "nanoDuration==null" );
        return new NanoDuration( time+ nanoDuration.time );
    }

    @Override
    public NanoDuration sub(NanoDuration nanoDuration) {
        if( nanoDuration==null )throw new IllegalArgumentException( "nanoDuration==null" );
        return new NanoDuration( time- nanoDuration.time );
    }
}
