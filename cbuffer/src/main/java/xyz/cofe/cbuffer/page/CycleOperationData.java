package xyz.cofe.cbuffer.page;

import xyz.cofe.collection.IndexSet;
import xyz.cofe.fn.Consumer3;
import xyz.cofe.fn.Tuple2;

import java.util.*;

public class CycleOperationData<
    DATA,
    DURATION extends Comparable<DURATION> & Duration<DURATION>,
    TIME extends Comparable<TIME> & Distance<TIME,DURATION>
    >
    implements OperationData<DATA,DURATION,TIME>
{
    protected List<DATA> dataList;
    protected List<TIME> beginList;
    protected List<TIME> endList;
    protected Map<DATA,Integer> counts;
    protected Map<DATA,DURATION> duration;
    protected Map<DATA,TIME> beginLastTime;
    protected Map<DATA,TIME> endLastTime;

    protected int limit;
    protected long ptr;

    public CycleOperationData(int limit){
        if( limit<1 )throw new IllegalArgumentException( "limit<1" );
        dataList = new ArrayList<>();
        beginList = new ArrayList<>();
        endList = new ArrayList<>();
        ptr = 0;
        this.limit = limit;
        counts = new HashMap<>();
        duration = new HashMap<>();
        beginLastTime = new HashMap<>();
        endLastTime = new HashMap<>();
    }

    public Map<DATA,Integer> counts(){ return counts; }
    public Map<DATA,DURATION> duration(){ return duration; }
    public Optional<Tuple2<TIME,TIME>> last(DATA data){
        if( data==null )throw new IllegalArgumentException( "data==null" );

        TIME begin = beginLastTime.get(data);
        TIME end = endLastTime.get(data);
        if( begin==null || end==null )return Optional.empty();

        return Optional.of(Tuple2.of(begin,end));
    }

    private <T> Optional<T> collect(List<T> list, int trgt, T value ){
        if( list.size()<=trgt ){
            int addBefore = trgt - list.size();
            for( int i=0;i<addBefore;i++ ){
                list.add(value);
            }
            list.add(value);
            return Optional.empty();
        }else{
            return Optional.of(
                list.set(trgt, value)
            );
        }
    }

    @Override
    public void collect(TIME begin, TIME end, DATA data) {
        if( begin==null )throw new IllegalArgumentException( "begin==null" );
        if( end==null )throw new IllegalArgumentException( "end==null" );

        int cmp1 = begin.compareTo(end);
        if( cmp1>0 )throw new IllegalArgumentException( "begin > end" );

        int trgt = (int)(ptr % limit);

        Optional<DATA> prevData = collect( dataList, trgt, data );
        Optional<TIME> prevBegin = collect( beginList, trgt, begin );
        Optional<TIME> prevEnd = collect( endList, trgt, end );
        ptr++;

        beginLastTime.put(data,begin);
        endLastTime.put(data,end);

        counts.put(data,counts.getOrDefault(data,0)+1);

        DURATION zeroDuration = begin.distance(begin);
        DURATION d = duration.getOrDefault(data,zeroDuration);
        d = d.add(end.distance(begin));
        duration.put(data,d);

        if( prevData.isPresent() && prevEnd.isPresent() && prevBegin.isPresent() ){
            DATA p_data = prevData.get();
            TIME p_begin = prevBegin.get();
            TIME p_end = prevEnd.get();

            int cnt = counts.getOrDefault(p_data,0);
            if( cnt>0 ){
                cnt = cnt - 1;
                if( cnt<=0 ){
                    counts.remove(p_data);
                    duration.remove(p_data);
                }else{
                    counts.put(p_data,cnt);
                    DURATION d1 = p_end.distance(p_begin);
                    DURATION d2 = duration.get(p_data);
                    if( d2!=null ){
                        DURATION d3 = d2.sub(d1);
                        if( zeroDuration.compareTo(d3)>0 ){
                            duration.remove(p_data);
                        }else{
                            duration.put(p_data, d3);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void read(Consumer3<TIME, TIME, DATA> data) {
        if( data==null )throw new IllegalArgumentException( "data==null" );

        int trgt = (int)(ptr % limit);
        if( dataList.size()!= beginList.size() )return;
        if( dataList.size()!= endList.size() )return;

        int size = dataList.size();
        if( trgt<ptr ) {
            for (int i = trgt; i < size; i++) {
                data.accept(beginList.get(i), endList.get(i), dataList.get(i));
            }
        }
        for( int i=0; i<trgt; i++ ){
            data.accept(beginList.get(i), endList.get(i), dataList.get(i));
        }
    }
}
