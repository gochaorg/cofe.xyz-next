package xyz.cofe.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import xyz.cofe.fn.TripleConsumer;

public class MappedList<FROM,TO> implements EventList<TO> {
    public final Function<FROM,TO> mapping;
    public final Function<TO,FROM> unmapping;
    public final EventList<FROM> source;
    protected final EventList<TO> result;
    protected volatile boolean sourceSync = false;
    protected volatile boolean resultSync = false;

    public MappedList( EventList<FROM> source, Function<FROM,TO> mapping, Function<TO,FROM> unmapping ){
        if( source==null )throw new IllegalArgumentException( "source==null" );
        if( mapping==null )throw new IllegalArgumentException( "mapping==null" );
        if( unmapping==null )throw new IllegalArgumentException( "unmapping==null" );

        this.source = source;
        this.result = createResultList();
        this.mapping = mapping;
        this.unmapping = unmapping;

        source.onInserted(true, (idx,old,cur)->{
            onInsertedSource(idx,cur);
        });
        source.onUpdated(true, this::onUpdatedSource);
        source.onDeleted(true, (idx,old,cur)->{
            onDeletedSource(idx,old);
        });

        int sidx = -1;
        for(FROM from : source){
            sidx++;
            onInsertedSource(sidx,from);
        }

        result.onInserted( (idx,old,cur) -> onInsertedResult(idx,cur) );
        result.onUpdated(this::onUpdatedResult);
        result.onDeleted( (idx,old,cur) -> onDeletedResult(idx,old) );
    }
    protected EventList<TO> createResultList(){ return new BasicEventList<>(new ArrayList<>()); }

    //region source sync
    protected void onInsertedSource(int idx, FROM newItem ){
        if( resultSync )return;
        try{
            sourceSync = true;
            TO to = mapping.apply(newItem);
            result.add(idx, to);
        } finally {
            sourceSync = false;
        }
    }
    protected void onUpdatedSource(int idx, FROM deleted, FROM newItem ){
        if( resultSync )return;
        try{
            sourceSync = true;
            result.remove(idx);
            TO to = mapping.apply(newItem);
            result.add(idx, to);
        } finally {
            sourceSync = false;
        }
    }
    protected void onDeletedSource(int idx, FROM deleted ){
        if( resultSync )return;
        try {
            sourceSync = true;
            result.remove(idx);
        } finally {
            sourceSync = false;
        }
    }
    //endregion

    //region result sync
    protected void onInsertedResult( int idx, TO newItem ){
        if( sourceSync )return;
        try {
            resultSync = true;
            FROM from = unmapping.apply(newItem);
            source.add(idx, from);
        } finally {
            resultSync = false;
        }
    }

    protected void onUpdatedResult( int idx, TO deleted, TO newItem ){
        if( sourceSync )return;
        try {
            resultSync = true;
            source.remove(idx);
            FROM from = unmapping.apply(newItem);
            source.add(idx, from);
        } finally {
            resultSync = false;
        }
    }

    protected void onDeletedResult( int idx, TO deleted ){
        if( sourceSync )return;
        try {
            resultSync = true;
            source.remove(idx);
        } finally {
            resultSync = false;
        }
    }
    //endregion

    //region delegate to result
    /**
     * Возвращает целевой список, над которым происходят преобразования
     * @return целевой список
     */
    @Override
    public List<TO> target(){
        return result.target();
    }

    /**
     * Добавляет подписчика на событие добавления данных
     * @param ls подписчик - fn( key:Integer, oldValue:E=null, newValue:E )
     * @return отписка от уведомлений
     */
    @Override
    public AutoCloseable onInserted(TripleConsumer<Integer, TO, TO> ls){
        return result.onInserted(ls);
    }

    /**
     * Добавляет подписчика на событие добавления данных
     * @param weak true - добавить подписчика как weak ссылку
     * @param ls подписчик - fn( key:Integer, oldValue:E=null, newValue:E )
     * @return отписка от уведомлений
     */
    @Override
    public AutoCloseable onInserted(boolean weak, TripleConsumer<Integer, TO, TO> ls){
        return result.onInserted(weak, ls);
    }

    /**
     * Добавляет подписчика на событие изменения данных
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E )
     * @return отписка от уведомлений
     */
    @Override
    public AutoCloseable onUpdated(TripleConsumer<Integer, TO, TO> ls){
        return result.onUpdated(ls);
    }

    /**
     * Добавляет подписчика на событие изменения данных
     * @param weak true - добавить подписчика как weak ссылку
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E )
     * @return отписка от уведомлений
     */
    @Override
    public AutoCloseable onUpdated(boolean weak, TripleConsumer<Integer, TO, TO> ls){
        return result.onUpdated(weak, ls);
    }

    /**
     * Добавляет подписчика на событие удаления данных
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E=null )
     * @return отписка от уведомлений
     */
    @Override
    public AutoCloseable onDeleted(TripleConsumer<Integer, TO, TO> ls){
        return result.onDeleted(ls);
    }

    /**
     * Добавляет подписчика на событие удаления данных
     * @param weak true - добавить подписчика как weak ссылку
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E=null )
     * @return отписка от уведомлений
     */
    @Override
    public AutoCloseable onDeleted(boolean weak, TripleConsumer<Integer, TO, TO> ls){
        return result.onDeleted(weak, ls);
    }

    /**
     * Добавляет подписчика на событие изменения/добавления/удаления данных
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E )
     * @return отписка от уведомлений
     */
    @Override
    public AutoCloseable onChanged(TripleConsumer<Integer, TO, TO> ls){
        return result.onChanged(ls);
    }

    /**
     * Добавляет подписчика на событие изменения/добавления/удаления данных
     * @param weak true - добавить подписчика как weak ссылку
     * @param ls подписчик - fn( key:Integer, oldValue:E, newValue:E )
     * @return отписка от уведомлений
     */
    @Override
    public AutoCloseable onChanged(boolean weak, TripleConsumer<Integer, TO, TO> ls){
        return result.onChanged(weak, ls);
    }

    @Override
    public void fireCollectionEvent(CollectionEvent<EventList<TO>, TO> event){
        result.fireCollectionEvent(event);
    }

    /**
     * Рассылка уведомления подписчикам о добавлении элемента
     * @param index индекс
     * @param to элемент
     */
    @Override
    public void fireInserted(int index, TO to){
        result.fireInserted(index, to);
    }

    /**
     * Рассылка уведомления подписчикам о обновлении элемента
     * @param index индекс
     * @param old предыдущее значение
     * @param current текущее значение
     */
    @Override
    public void fireUpdated(int index, TO old, TO current){
        result.fireUpdated(index, old, current);
    }

    /**
     * Рассылка уведомления подписчикам о удалении элемента
     * @param index индекс
     * @param to элемент
     */
    @Override
    public void fireDeleted(int index, TO to){
        result.fireDeleted(index, to);
    }

    /**
     * Возвращает кол-во элементов в коллекции
     * @return кол-во элементов
     * @see #isEmpty()
     */
    @Override
    public int size(){
        return result.size();
    }

    /**
     * Проверяет что коллекция пуста
     * @return true - коллекция пуста
     */
    @Override
    public boolean isEmpty(){
        return result.isEmpty();
    }

    /**
     * Проверяет наличие элемента в коллеции
     * @param o элемент
     * @return true - элемент присуствует в коллекции
     */
    @Override
    public boolean contains(Object o){
        return result.contains(o);
    }

    /**
     * Создает массив объектов коллеции
     * @return массив объектов коллеции
     */
    @Override
    public Object[] toArray(){
        return result.toArray();
    }

    /**
     * Создает массив объектов коллеции
     * @param a пустой массив (изза особенности реализации Generic)
     * @return массив
     */
    @Override
    public <T> T[] toArray(T[] a){
        //noinspection SuspiciousToArrayCall
        return result.toArray(a);
    }

    /**
     * Проверяет наличие всех элементов в коллеции
     * @param c искомые объекты
     * @return true - если все искомые объекты есть в коллеции
     */
    @Override
    public boolean containsAll(Collection<?> c){
        return result.containsAll(c);
    }

    /**
     * Получение объекта по его индексу
     * @param index индекс элемента
     * @return Элемент
     */
    @Override
    public TO get(int index){
        return result.get(index);
    }

    /**
     * Поиск первого индекса элемента в коллекции
     * @param o элемент
     * @return индекс или -1, если не найден
     */
    @Override
    public int indexOf(Object o){
        return result.indexOf(o);
    }

    /**
     * Поиск последнего индекса элемента в коллекции
     * @param o элемент
     * @return индекс или -1, если не найден
     */
    @Override
    public int lastIndexOf(Object o){
        return result.lastIndexOf(o);
    }

    /**
     * Получение итератора по списку, от начала к концу
     * @return итератор
     */
    @Override
    public Iterator<TO> iterator(){
        return result.iterator();
    }

    /**
     * Получение итератора по списку
     * @return итератор
     */
    @Override
    public ListIterator<TO> listIterator(){
        return result.listIterator();
    }

    /**
     * Получение итератора по списку,, начала итерации согласно указанному индексу
     * @param index индекс начала итерирования
     * @return итератор
     */
    @Override
    public ListIterator<TO> listIterator(int index){
        return result.listIterator(index);
    }

    /**
     * Создание проекции списка
     * @param fromIndex начальный индекс
     * @param toIndex конечный индекс
     * @return Проекция
     */
    @Override
    public List<TO> subList(int fromIndex, int toIndex){
        return result.subList(fromIndex, toIndex);
    }

    /**
     * Добавляет элемент в список
     * @param to элемент
     * @return true - список изменен
     */
    @Override
    public boolean add(TO to){
        return result.add(to);
    }

    /**
     * Удаляет элемент из списка
     * @param o элемент
     * @return true - список изменен
     */
    @Override
    public boolean remove(Object o){
        return result.remove(o);
    }

    /**
     * Добавляет все элементы в список
     * @param c элементы
     * @return true - список изменен
     */
    @Override
    public boolean addAll(Collection<? extends TO> c){
        return result.addAll(c);
    }

    /**
     * Добавляет все элементы в список, в указанную позицию
     *
     * @param index позиция (индекс) вставки
     * @param c элементы
     * @return true - список изменен
     */
    @Override
    public boolean addAll(int index, Collection<? extends TO> c){
        return result.addAll(index, c);
    }

    /**
     * Удаляет указанные элементы из списка
     * @param coll элементы
     * @return true - список изменен
     */
    @Override
    public boolean removeAll(Collection<?> coll){
        return result.removeAll(coll);
    }

    /**
     * Удаляет элементы из списока, за исключением указанных
     * @param coll элементы
     * @return true - список изменен
     */
    @Override
    public boolean retainAll(Collection<?> coll){
        return result.retainAll(coll);
    }

    /**
     * Удаляет все элементы
     */
    @Override
    public void clear(){
        result.clear();
    }

    /**
     * Записиывает элемент в определенную позицию
     * @param index индекс
     * @param element элемент
     * @return предыдущее значение
     */
    @Override
    public TO set(int index, TO element){
        return result.set(index, element);
    }

    /**
     * Вставляет элемент в определнное место списка
     * @param index индекс
     * @param element элемент
     */
    @Override
    public void add(int index, TO element){
        result.add(index, element);
    }

    /**
     * Удялет элемент из списка
     * @param index индекс элемента
     * @return удаленный элемент
     */
    @Override
    public TO remove(int index){
        return result.remove(index);
    }

    /**
     * Замена всех элементов
     * @param operator функция замены
     */
    @Override
    public void replaceAll(UnaryOperator<TO> operator){
        result.replaceAll(operator);
    }

    /**
     * Сортировка списка
     * @param c функция сравнения
     */
    @Override
    public void sort(Comparator<? super TO> c){
        result.sort(c);
    }

    /**
     * Удаление элементов согласно фильтру
     * @param filter фильтр
     * @return факт удаления
     */
    @Override
    public boolean removeIf(Predicate<? super TO> filter){
        return result.removeIf(filter);
    }
    //endregion
}
