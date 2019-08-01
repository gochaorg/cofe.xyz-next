package xyz.cofe.ecolls;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Пара значений.
 * <p>
 *     Полный список возможных наименований звучит так
 * <pre>
 * 1 Single
 * 2 Double
 * 3 Triple
 * 4 Quadruple
 * 5	Quintuple  "Quint"
 * 6	Sextuple  "Sex"
 * 7	Septuple  "Sept"
 * 8	Octuple  "Oct"
 * 9	Nonuple  "Non"
 * 10	Decuple  "Dec"
 * 11	Undecuple  "Undec"
 * 12	Duodecuple  "Duodec"
 * 13	Tredecuple  "Tredec"
 * 14	Quattuordecuple  "Quattuordec"
 * 15	Quindecuple  "Quindec"
 * 16	Sexdecuple  "Sexdec"
 * 17	Septendecuple  "Septendec"
 * 18	Octodecuple  "Octodec"
 * 19	Novemdecuple  "Novemdec"
 * 20	Viguple  "Vig"
 * 21	Unviguple  "Unvig"
 * 22	Duoviguple  "Duovig"
 * 23	Treviguple  "Trevig"
 * 24	Quattuorviguple  "Quattuorvig"
 * 25	Quinviguple  "Quinvig"
 * 26	Sexviguple  "Sexvig"
 * 27	Septenviguple  "Septenvig"
 * 28	Octoviguple  "Octovig"
 * 29	Novemviguple  "Novemvig"
 * 30	Triguple  "Trig"
 * 31	Untriguple  "Untrig"
 * 32	Duotriguple  "Duotrig"
 * 33	Tretriguple  "Tretrig"
 * 34	Quattuortriguple  "Quattuortrig"
 * 35	Quintriguple  "Quintrig"
 * 36	Sextriguple  "Sextrig"
 * 37	Septentriguple  "Septentrig"
 * 38	Octotriguple  "Octotrig"
 * 39	Novemtriguple  "Novemtrig"
 * 40	Quadraguple  "Quadrag"
 * 41	Unquadraguple  "Unquadrag"
 * 42	Duoquadraguple  "Duoquadrag"
 * 43	Trequadraguple  "Trequadrag"
 * 44	Quattuorquadraguple  "Quattuorquadrag"
 * 45	Quinquadraguple  "Quinquadrag"
 * 46	Sexquadraguple  "Sexquadrag"
 * 47	Septenquadraguple  "Septenquadrag"
 * 48	Octoquadraguple  "Octoquadrag"
 * 49	Novemquadraguple  "Novemquadrag"
 * 50	Quinquaguple  "Quinquag"
 * 51	Unquinquaguple  "Unquinquag"
 * 52	Duoquinquaguple  "Duoquinquag"
 * 53	Trequinquaguple  "Trequinquag"
 * 54	Quattuorquinquaguple  "Quattuorquinquag"
 * 55	Quinquinquaguple  "Quinquinquag"
 * 56	Sexquinquaguple  "Sexquinquag"
 * 57	Septenquinquaguple  "Septenquinquag"
 * 58	Octoquinquaguple  "Octoquinquag"
 * 59	Novemquinquaguple  "Novemquinquag"
 * 60	Sexaguple  "Sexag"
 * 61	Unsexaguple  "Unsexag"
 * 62	Duosexaguple  "Duosexag"
 * 63	Tresexaguple  "Tresexag"
 * 64	Quattuorsexaguple  "Quattuorsexag"
 * 65	Quinsexaguple  "Quinsexag"
 * 66	Sexsexaguple  "Sexsexag"
 * 67	Septensexaguple  "Septensexag"
 * 68	Octosexaguple  "Octosexag"
 * 69	Novemsexaguple  "Novemsexag"
 * 70	Septuaguple  "Septuag"
 * 71	Unseptuaguple  "Unseptuag"
 * 72	Duoseptuaguple  "Duoseptuag"
 * 73	Treseptuaguple  "Treseptuag"
 * 74	Quattuorseptuaguple  "Quattuorseptuag"
 * 75	Quinseptuaguple  "Quinseptuag"
 * 76	Sexseptuaguple  "Sexseptuag"
 * 77	Septenseptuaguple  "Septenseptuag"
 * 78	Octoseptuaguple  "Octoseptuag"
 * 79	Novemseptuaguple  "Novemseptuag"
 * 80	Octoguple  "Octog"
 * 81	Unoctoguple  "Unoctog"
 * 82	Duooctoguple  "Duooctog"
 * 83	Treoctoguple  "Treoctog"
 * 84	Quattuoroctoguple  "Quattuoroctog"
 * 85	Quinoctoguple  "Quinoctog"
 * 86	Sexoctoguple  "Sexoctog"
 * 87	Septoctoguple  "Septoctog"
 * 88	Octooctoguple  "Octooctog"
 * 89	Novemoctoguple  "Novemoctog"
 * 90	Nonaguple  "Nonag"
 * 91	Unnonaguple  "Unnonag"
 * 92	Duononaguple  "Duononag"
 * 93	Trenonaguple  "Trenonag"
 * 94	Quattuornonaguple  "Quattuornonag"
 * 95	Quinnonaguple  "Quinnonag"
 * 96	Sexnonaguple  "Sexnonag"
 * 97	Septennonaguple  "Septennonag"
 * 98	Octononaguple  "Octononag"
 * 99	Novemnonaguple  "Novemnonag"
 * 100	Centuple  "Cent"
 * 1000	Millidruple  "Mil"
 * million	Megadruple	"Mega"
 * billion	Gigadruple	"Gig"
 * trillion	Teradruple	"Tera"
 * </pre>
 * @author gocha
 */
public interface Pair<A,B>
{
    /**
     * Возвращает первый элемент пары
     * @return первый элемент пары
     */
    A a();

    /**
     * Возвращает второй элемент пары
     * @return второй элемент пары
     */
    B b();

    /**
     * Врзвращает пару
     * @param a первый элемент
     * @param b второй элемент
     * @param <A> тип первого элемента
     * @param <B> тип второго элемента
     * @return пара значений
     */
    static <A,B> Pair<A,B> of(A a, B b){
        return new Pair<A, B>() {
            @Override
            public A a() {
                return a;
            }

            @Override
            public B b() {
                return b;
            }
        };
    }

    /**
     * Передает значения элементов в функцию
     * @param consumer функция приемник
     * @return self ссылка
     */
    default Pair<A,B> apply(BiConsumer<A,B> consumer){
        if(consumer==null)throw new IllegalArgumentException("consumer == null");
        consumer.accept(a(),b());
        return this;
    }

    /**
     * Передает значения элементов в функцию
     * @param fn функция приемник
     * @return результат вызова функции
     */
    default <Z> Z apply(BiFunction<A,B,Z> fn){
        if(fn==null)throw new IllegalArgumentException("fn == null");
        return fn.apply(a(),b());
    }
}
