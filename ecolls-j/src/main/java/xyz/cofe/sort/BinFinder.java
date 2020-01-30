package xyz.cofe.sort;

import xyz.cofe.fn.Consumer1;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Поиск в сортированом списке
 * @param <LIST> Тип списка
 * @param <E> Тип значения
 */
public interface SFinder<LIST,E> {
    /**
     * Получение элемента по его индексу
     * @param lst список
     * @param index индекс
     * @return элемент
     */
    E get( LIST lst, int index);

    public default void equals( Consumer<E> consumer, LIST lst, Comparator<E> cmp, E target, int begin, int endex ){
        if( consumer==null ) throw new IllegalArgumentException("consumer==null");
        if( lst==null ) throw new IllegalArgumentException("lst==null");
        if( cmp==null ) throw new IllegalArgumentException("cmp==null");

        if( begin>endex ){
            int t = begin;
            begin = endex;
            endex = t;
        }

        while( true ){
            int areasize = endex - begin;
            if( areasize<0 ){
                // область поиска - нулевая
                break;
            }

            if( areasize==0 ){
                // область поиска сужена до одного элемента
                E e = get(lst, begin);
                if( cmp.compare(e,target)==0 ){
                    consumer.accept(e);
                }
                break;
            }else{
                int leftBegin = begin;
                int leftEndex = begin+areasize/2;

                int rightBegin = leftEndex;
                int rightEndex = endex;

                // Берем центральный элемент
                E e = get(lst,leftEndex);
                int c = cmp.compare(e, target);

                // Возможны след комбинации
                //        | left
                // c <  0 | не искать | искать
                // c == 0 | искать    | искать
                // c >  0 | искать    | не искать

                if( c<0 ){
                    begin = leftBegin;
                    endex = leftEndex;
                    continue;
                }else if( c==0 ){
                    consumer.accept(e);
                    // искать влево
                    equals(consumer,lst,cmp,target,leftBegin,leftEndex);
                    // искать вправо
                    equals(consumer,lst,cmp,target,rightBegin+1,rightEndex);
                }else if( c>0 ){
                    begin = rightBegin;
                    endex = rightEndex;
                    continue;
                }
            }
        }
    }

    public default int headIndex( LIST lst, Comparator<E> cmp, E target, int begin, int endex ){
        if( consumer==null ) throw new IllegalArgumentException("consumer==null");
        if( lst==null ) throw new IllegalArgumentException("lst==null");
        if( cmp==null ) throw new IllegalArgumentException("cmp==null");

        if( begin>endex ){
            int t = begin;
            begin = endex;
            endex = t;
        }

        while( true ){
            int areasize = endex-begin;
            if( areasize<0 ){
                // область поиска - нулевая
                break;
            }
        }
    }
}
