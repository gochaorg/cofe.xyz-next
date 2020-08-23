package xyz.cofe.typedist;

import xyz.cofe.iter.Eterable;
import xyz.cofe.iter.TreeStep;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Растояние между типами A &lt;=&gt; B
 * <ul>
 *     <li>Значение 0 - типы совпадают</li>
 *     <li>Значение -1 - соответ что между A и B стоит один растояние: A instanceof B </li>
 *     <li>Значение -2 - соответ что между A и B стоит два растояние: A instanceof X instanceof B </li>
 *     <li>Значение -3 - соответ что между A и B стоит три растояние: A instanceof Y instanceof X instanceof B </li>
 *     <li>Значение 1  - соответ что между A и B стоит одно растояние: B instanceof A </li>
 *     <li>Значение 2  - соответ что между A и B стоит два растояние: B instanceof X instanceof A </li>
 *     <li>Значение 3  - соответ что между A и B стоит три растояние: B instanceof Y instanceof X instanceof A </li>
 *     <li>И т.д.</li>
 * </ul>
 * Если указывается перечисление (0..3 или -3..0)
 * соответствует для интерфейсов,
 * следует использовать крайнее  значение от нуля.
 */
public class TypeDistance {
    /**
     * Конструктор, min=0, max=0
     */
    public TypeDistance(){
        this.min = 0;
        this.max = 0;
    }

    /**
     * Конструктор
     * @param min минимальная дистанция
     * @param max максимальное дистанция
     */
    public TypeDistance(int min,int max){
        this.min = Math.min(min,max);
        this.max = Math.max(min,max);
    }

    /**
     * минимальное значение
     */
    protected final int min;

    /**
     * Возвращает минимальное значение
     * @return минимальная дистанция
     */
    public int min(){ return min; }

    /**
     * максимальное значение
     */
    protected final int max;

    /**
     * Возвращает максимальное значение
     * @return максимальное дистанция
     */
    public int max(){ return max; }

    @Override
    public String toString() {
        return min+" ... "+max;
    }

    /**
     * Расчет растояния между двумя классами
     * @param a Первый класс
     * @param b Второй класс
     * @param dist Примиает растояние
     */
    @SuppressWarnings("unchecked")
    public static void cdistance(Class a, Class b, Consumer<Integer> dist) {
        if (a == null) throw new IllegalArgumentException("a==null");
        if (b == null) throw new IllegalArgumentException("b==null");
        if( dist==null )throw new IllegalArgumentException( "dist==null" );
        if (a == b){
            dist.accept(0);
            return;
        }

        if (a.isPrimitive() || b.isPrimitive()) {
            return;
        }

        if (a.isAssignableFrom(b)) {
            //noinspection UnnecessaryLocalVariable
            Class x = a;
            Class y = b;
            int l = 0;
            while (true) {
                if (x == y) break;
                if (x.isAssignableFrom(y)) {
                    l -= 1;

                    if (y == Object.class) break;

                    Class yy = y.getSuperclass();
                    if (yy == null) break;

                    y = yy;
                } else {
                    break;
                }
            }

            dist.accept(l);
        } else if (b.isAssignableFrom(a)) {
            Integer[] resultDist = new Integer[]{ null };

            cdistance(b, a, rr -> resultDist[0] = rr );
            if (resultDist[0]!=null) {
                dist.accept( -resultDist[0] );
            }
        }
    }

    private static final Function<Class, Iterable<? extends Class>> interfacesFollow =
        from -> {
            if( from==null )return null;
            return Arrays.asList(from.getInterfaces());
        };

    private static Eterable<TreeStep<Class>> interfaceTree(Class c){
        return Eterable.tree(c,interfacesFollow).checkCycles().filter( ts -> ts.getLevel()<=30 ).go();
    }

    /**
     * Расчет растояния между двумя интерфейсами
     * @param a Первый интерфейс
     * @param b Второй интерфейс
     * @param dist Примиает растояние
     */
    @SuppressWarnings("unchecked")
    public static void idistance(Class a, Class b, Consumer<Integer> dist){
        if( a==null )throw new IllegalArgumentException( "a==null" );
        if( b==null )throw new IllegalArgumentException( "b==null" );
        if( dist==null )throw new IllegalArgumentException( "dist==null" );
        if( a==b ){
            dist.accept(0);
            return;
        }

        if( a.isAssignableFrom(b) && a.isInterface() ){
            for( TreeStep<Class> ts : interfaceTree(b) ){
                Class bx = ts.getNode();
                if( a.isAssignableFrom(bx) ){
                    dist.accept( -ts.getLevel() );
                }
            }
        }else if( b.isAssignableFrom(a) && b.isInterface() ){
            for( TreeStep<Class> ts : interfaceTree(a) ){
                Class ax = ts.getNode();
                if( b.isAssignableFrom(ax) ){
                    dist.accept( ts.getLevel() );
                }
            }
        }
    }

    /**
     * Вычисление дистанции между типами
     * @param a первй тип
     * @param b второй тип
     * @return дистанция или null
     */
    public static Optional<TypeDistance> distance(Class a,Class b){
        if( a==null )throw new IllegalArgumentException( "a==null" );
        if( b==null )throw new IllegalArgumentException( "b==null" );

        TreeSet<Integer> dist = new TreeSet<>();
        cdistance(a,b,dist::add);
        idistance(a,b,dist::add);

        if( dist.isEmpty() )return Optional.empty();

        return Optional.of(new TypeDistance(dist.first(), dist.last()));
    }

    /**
     * Типы A и B коварианты: A instanceof B
     * @return true - Типы A и B коварианты
     */
    public boolean covariant(){
        return min < 0 && max <= 0;
    }

    /**
     * Типы A и B контрварианты: B instanceof A
     * @return true - Типы A и B коварианты
     */
    public boolean contrvariant(){
        return min >= 0 && max > 0;
    }

    /**
     * Типы A и B равны
     * @return true - равны
     */
    public boolean equals(){ return min==max && max==0; }

    /**
     * Выбирает крайнее растоение от нуля
     * @return растоения или emptry - кода min &lt; 0 и max &gt; 0
     */
    public Optional<Integer> dist(){
        if( covariant() )return Optional.of(Math.min(min,max));
        if( contrvariant() )return Optional.of(Math.max(min,max));
        if( equals() )return Optional.of(0);
        return Optional.empty();
    }
}
