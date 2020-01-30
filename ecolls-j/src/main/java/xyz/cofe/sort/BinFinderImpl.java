package xyz.cofe.sort;

import java.util.Comparator;

/**
 * Реализация функций бинарного поиска
 */
public class BinFinderImpl {
    /**
     * Поиск "головы" - ищет в списке начало некого значения.
     *
     * <p></p>
     * Пример есть список: <br>
     * [0] = 2 // &lt;- это будет "голова" для искомого значения 3 <br>
     * [1] = 2 <br>
     * [2] = 3 <br>
     * [3] = 5 // &lt;- это будет "голова" для искомого значения 7 <br>
     * [4] = 8 <br>
     * [5] = 8 <br>
     * [6] = 9 <br>
     * @param finder ссылка на интерфейс BinFinder, для доступа к функции {@link BinFinder#get(Object, int)}
     * @param lst список
     * @param cmp функция согласно которой отсортированы элементы в списке
     * @param target искомое значение
     * @param begin начало области поиска
     * @param endex конец (исключительно) области поиска
     * @return индекс соответ голове или -1, еслли не найдено
     */
    public static <LIST,E> int headIndex( BinFinder<LIST,E> finder, LIST lst, Comparator<E> cmp, E target, int begin, int endex ){
        if( finder==null ) throw new IllegalArgumentException("finder==null");
        if( lst==null ) throw new IllegalArgumentException("lst==null");
        if( cmp==null ) throw new IllegalArgumentException("cmp==null");

        if( begin>endex ){
            int t = begin;
            begin = endex;
            endex = t;
        }

        while( true ){
            int areasize = endex-begin;

            if( areasize<=0 ){
                // область поиска - нулевая
                break;
            }

            if( areasize==1 ){
                // область поиска - 1 эл
                // если эл меньше, target - и он последний среди области поиска
                // то возвращаем его
                E e = finder.get(lst,begin);
                if( cmp.compare(e,target)<0 ){
                    return begin;
                }
                break;
            }

            int leftBegin = begin;
            int leftEndex = begin+areasize/2;

            int rightBegin = leftEndex;
            int rightEndex = endex;

            // Берем центральный элемент
            E e = finder.get(lst,leftEndex);
            int c = cmp.compare(e,target);
            //        | left   | right
            // c <  0 | -      | search
            // c == 0 | search | -
            // c >  0 | search | -
            if( c<0 ){
                begin = rightBegin;
                endex = rightEndex;
                continue;
            }

            begin = leftBegin;
            endex = leftEndex;
        }

        return -1;
    }

    /**
     * Поиск "хвоста" - ищет в списке хвост некого значения.
     *
     * <p></p>
     * Пример есть список: <br>
     * [0] = 2 <br>
     * [1] = 2 <br>
     * [2] = 3 // &lt;- это будет "хвост" для искомого значения 2 <br>
     * [3] = 5 // &lt;- это будет "хвост" для искомого значения 4 <br>
     * [4] = 8 <br>
     * [5] = 8 <br>
     * [6] = 9 // &lt;- это будет "хвост" для искомого значения 8 <br>
     * @param finder ссылка на интерфейс BinFinder, для доступа к функции {@link BinFinder#get(Object, int)}
     * @param lst список
     * @param cmp функция согласно которой отсортированы элементы в списке
     * @param target искомое значение
     * @param begin начало области поиска
     * @param endex конец (исключительно) области поиска
     * @param found ранее найденое значение
     * @param found ранее найденый индекс
     * @return индекс соответ хвосту или -1, еслли не найдено
     */
    public static <LIST,E> int tailIndex( BinFinder<LIST,E> finder, LIST lst, Comparator<E> cmp, E target, int begin, int endex, E found, int foundIndex ){
        if( lst==null ) throw new IllegalArgumentException("lst==null");
        if( cmp==null ) throw new IllegalArgumentException("cmp==null");
        if( finder==null ) throw new IllegalArgumentException("finder==null");

        if( begin>endex ){
            int t = begin;
            begin = endex;
            endex = t;
        }

        while( true ){
            int areasize = endex-begin;

            if( areasize<=0 ){
                // область поиска - нулевая
                break;
            }

            if( areasize==1 ){
                // область поиска - 1 эл
                // если эл больше, target - и он последний среди области поиска
                // то возвращаем его
                E e = finder.get(lst, begin);
                if( cmp.compare(e, target)>0 ){
                    return begin;
                }
                break;
            }

            int leftBegin = begin;
            int leftEndex = begin+areasize/2;

            int rightBegin = leftEndex;
            int rightEndex = endex;

            // Берем центральный элемент
            E centerEl = finder.get(lst,leftEndex);
            int cmpCenter   = cmp.compare(centerEl,target);

            //        | left   | right
            // c <  0 | -      | search
            // c == 0 | -      | search
            // c >  0 | search | -
            if( cmpCenter<=0 ){
                begin = rightBegin;
                endex = rightEndex;
                continue;
            }

            begin = leftBegin;
            endex = leftEndex;

            if( cmp.compare(centerEl, found)<0 ){
                found = centerEl;
                foundIndex = leftEndex;
            }
        }

        return foundIndex;
    }

    /**
     * Поиск "хвоста" - ищет в списке хвост некого значения.
     *
     * <p></p>
     * Пример есть список: <br>
     * [0] = 2 <br>
     * [1] = 2 <br>
     * [2] = 3 // &lt;- это будет "хвост" для искомого значения 2 <br>
     * [3] = 5 // &lt;- это будет "хвост" для искомого значения 4 <br>
     * [4] = 8 <br>
     * [5] = 8 <br>
     * [6] = 9 // &lt;- это будет "хвост" для искомого значения 8 <br>
     * @param finder ссылка на интерфейс BinFinder, для доступа к функции {@link BinFinder#get(Object, int)}
     * @param lst список
     * @param cmp функция согласно которой отсортированы элементы в списке
     * @param target искомое значение
     * @param begin начало области поиска
     * @param endex конец (исключительно) области поиска
     * @return индекс соответ хвосту или -1, еслли не найдено
     */
    public static <LIST,E> int tailIndex( BinFinder<LIST,E> finder, LIST lst, Comparator<E> cmp, E target, int begin, int endex ){
        if( finder==null ) throw new IllegalArgumentException("finder==null");
        if( lst==null ) throw new IllegalArgumentException("lst==null");
        if( cmp==null ) throw new IllegalArgumentException("cmp==null");

        if( begin>endex ){
            int t = begin;
            begin = endex;
            endex = t;
        }

        while( true ){
            int areasize = endex-begin;

            if( areasize<=0 ){
                // область поиска - нулевая
                break;
            }

            if( areasize==1 ){
                // область поиска - 1 эл
                // если эл больше, target - и он последний среди области поиска
                // то возвращаем его
                E e = finder.get(lst, begin);
                if( cmp.compare(e, target)>0 ){
                    return begin;
                }
                break;
            }

            int leftBegin = begin;
            int leftEndex = begin+areasize/2;

            int rightBegin = leftEndex;
            int rightEndex = endex;

            // Берем центральный элемент
            E centerEl = finder.get(lst,leftEndex);
            int cmpCenter   = cmp.compare(centerEl,target);

            //        | left   | right
            // c <  0 | -      | search
            // c == 0 | -      | search
            // c >  0 | search | -
            if( cmpCenter<=0 ){
                begin = rightBegin;
                endex = rightEndex;
                continue;
            }
//            begin = leftBegin;
//            endex = leftEndex;
            return tailIndex(finder,lst,cmp,target,leftBegin,leftEndex,centerEl,leftEndex);
        }

        return -1;
    }
}
