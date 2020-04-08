package xyz.cofe.text.tparse;

import xyz.cofe.iter.Eterable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Правило алтернативной грамматической конструкции - т.е. соответ вертикальной черте в грамматике BNF
 * {@link GR}
 * @param <P> Указатель
 * @param <T> Лексема/Токен
 */
public interface AltOP<P extends Pointer<?,?,P>, T extends Tok<P>> {
    /**
     * Список выражений - алтернатив
     * @return список выражений альтернатив
     */
    Eterable<GR<P,T>> expressions();

    /**
     * Указывает как отобразить распознаною последовательность на указанный токен
     * @param map функция отображения
     * @param <U> тип токена - результата
     * @return функция грамматического правила
     */
    default <U extends Tok<P>> GR<P,U> map(Function<T,U> map) {
        if( map==null )throw new IllegalArgumentException("map==null");
        return new GR<P, U>() {
            @Override
            public Optional<U> apply(P ptr) {
                if(ptr==null)throw new IllegalArgumentException("ptr==null");
                return Optional.empty();
            }
        };
    }

    /**
     * Создает функцию грамматики
     * @return функция грамматического правила
     */
    default GR<P,T> map() {
        return map(x->x);
    }

    /**
     * Указывает дополнительную альтернативу
     * @param another2 правило
     * @param <PA> тип указателя
     * @param <TA> тип токена
     * @return правило вывода
     */
    default <PA extends Pointer<?,?,PA>, TA extends Tok<PA>> AltOP<PA,TA> another( GR<PA,TA> another2 ){
        if( another2==null )throw new IllegalArgumentException( "another2==null" );
        List lst = expressions().toList();

        GR[] grs = new GR[lst.size()+1];
        for( int i=0; i<lst.size(); i++ ){
            grs[i] = (GR)lst.get(i);
        }
        grs[grs.length-1] = another2;

        AltOPImpl alt = new AltOPImpl(grs);
        return alt;
    }
}
