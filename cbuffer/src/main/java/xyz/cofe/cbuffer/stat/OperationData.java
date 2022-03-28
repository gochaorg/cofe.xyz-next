package xyz.cofe.cbuffer.stat;

import xyz.cofe.fn.Consumer3;
import xyz.cofe.fn.Tuple2;

import java.util.*;

/**
 * Учет операционных данных
 * @param <DATA> Тип данных|операция
 * @param <DURATION> Продолжительность
 * @param <TIME> Время
 */
public interface OperationData<DATA,DURATION extends Comparable<DURATION>, TIME extends Comparable<TIME> & Distance<TIME,DURATION>> {
    /**
     * Регистрация данных
     * @param begin начало операции
     * @param end конец операции
     * @param data данные
     */
    void collect(TIME begin, TIME end, DATA data);

    /**
     * Получение зарегистрированных данных
     * @param data функция fn( begin, end, data ) - см {@link #collect(Comparable, Comparable, Object)}
     */
    void read(Consumer3<TIME,TIME,DATA> data);

    /**
     * Возвращает время начала-конца, последней операции для определенных данных
     * @param data данные
     * @return время начала-конца
     */
    Optional<Tuple2<TIME,TIME>> last(DATA data);

    /**
     * Возвращает кол-во операций
     * @return ключ - операция, значение - кол-во
     */
    Map<DATA,Integer> counts();

    /**
     * Возвращает отсортированные операции по кол-ву
     * @return ключ-кол-во, значение-операции
     */
    default NavigableMap<Integer, List<DATA>> countsSorted(){
        TreeMap<Integer,List<DATA>> map = new TreeMap<>();
        counts().forEach( (data,key) -> {
            map.computeIfAbsent(key, x -> new ArrayList<>()).add(data);
        });
        return map;
    }

    /**
     * Возвращает продолжительность операций
     * @return ключ-операция, значение-продолжительность
     */
    Map<DATA,DURATION> duration();

    /**
     * Возвращает отсортированные операции по продолжительности
     * @return ключ-продолжительность, значение-операции
     */
    default NavigableMap<DURATION,List<DATA>> durationSorted(){
        TreeMap<DURATION,List<DATA>> map = new TreeMap<>();
        duration().forEach( (data,key) -> {
            map.computeIfAbsent(key, x -> new ArrayList<>()).add(data);
        });
        return map;
    }
}
