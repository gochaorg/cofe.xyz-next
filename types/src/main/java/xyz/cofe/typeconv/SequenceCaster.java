/*
 * The MIT License
 *
 * Copyright 2015 Kamnev Georgiy (nt.gocha@gmail.com).
 *
 * Данная лицензия разрешает, безвозмездно, лицам, получившим копию данного программного
 * обеспечения и сопутствующей документации (в дальнейшем именуемыми "Программное Обеспечение"),
 * использовать Программное Обеспечение без ограничений, включая неограниченное право на
 * использование, копирование, изменение, объединение, публикацию, распространение, сублицензирование
 * и/или продажу копий Программного Обеспечения, также как и лицам, которым предоставляется
 * данное Программное Обеспечение, при соблюдении следующих условий:
 *
 * Вышеупомянутый копирайт и данные условия должны быть включены во все копии
 * или значимые части данного Программного Обеспечения.
 *
 * ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ ПРЕДОСТАВЛЯЕТСЯ «КАК ЕСТЬ», БЕЗ ЛЮБОГО ВИДА ГАРАНТИЙ,
 * ЯВНО ВЫРАЖЕННЫХ ИЛИ ПОДРАЗУМЕВАЕМЫХ, ВКЛЮЧАЯ, НО НЕ ОГРАНИЧИВАЯСЬ ГАРАНТИЯМИ ТОВАРНОЙ ПРИГОДНОСТИ,
 * СООТВЕТСТВИЯ ПО ЕГО КОНКРЕТНОМУ НАЗНАЧЕНИЮ И НЕНАРУШЕНИЯ ПРАВ. НИ В КАКОМ СЛУЧАЕ АВТОРЫ
 * ИЛИ ПРАВООБЛАДАТЕЛИ НЕ НЕСУТ ОТВЕТСТВЕННОСТИ ПО ИСКАМ О ВОЗМЕЩЕНИИ УЩЕРБА, УБЫТКОВ
 * ИЛИ ДРУГИХ ТРЕБОВАНИЙ ПО ДЕЙСТВУЮЩИМ КОНТРАКТАМ, ДЕЛИКТАМ ИЛИ ИНОМУ, ВОЗНИКШИМ ИЗ, ИМЕЮЩИМ
 * ПРИЧИНОЙ ИЛИ СВЯЗАННЫМ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ ИЛИ ИСПОЛЬЗОВАНИЕМ ПРОГРАММНОГО ОБЕСПЕЧЕНИЯ
 * ИЛИ ИНЫМИ ДЕЙСТВИЯМИ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ.
 */

package xyz.cofe.typeconv;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import xyz.cofe.collection.graph.Edge;
import xyz.cofe.collection.graph.Path;

/**
 * Последовательность caster-ов
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public class SequenceCaster
    extends MutableWeightedCaster
    implements
    Function<Object, Object>,
    GetWeight
{
    //<editor-fold defaultstate="collapsed" desc="log Функции">
    private static void logFine(String message,Object ... args){
        Logger.getLogger(SequenceCaster.class.getName()).log(Level.FINE, message, args);
    }

    private static void logFiner(String message,Object ... args){
        Logger.getLogger(SequenceCaster.class.getName()).log(Level.FINER, message, args);
    }

    private static void logFinest(String message,Object ... args){
        Logger.getLogger(SequenceCaster.class.getName()).log(Level.FINEST, message, args);
    }

    private static void logInfo(String message,Object ... args){
        Logger.getLogger(SequenceCaster.class.getName()).log(Level.INFO, message, args);
    }

    private static void logWarning(String message,Object ... args){
        Logger.getLogger(SequenceCaster.class.getName()).log(Level.WARNING, message, args);
    }

    private static void logSevere(String message,Object ... args){
        Logger.getLogger(SequenceCaster.class.getName()).log(Level.SEVERE, message, args);
    }

    private static void logException(Throwable ex){
        Logger.getLogger(SequenceCaster.class.getName()).log(Level.SEVERE, null, ex);
    }
    //</editor-fold>

    protected List<Function<Object,Object>> convertors = null;
    protected double defaultItemWeight = 1;

    /**
     * Конструктор
     * @param path последовательность преобразователей 
     */
    public SequenceCaster( Path<Class, Function<Object,Object>> path ){
        this.convertors = new ArrayList<Function<Object,Object>>();

        for( Edge<Class,Function<Object,Object>> ed : path.fetch(0, path.nodeCount()) ){
            if( ed!=null ){
                this.convertors.add(ed.getEdge());
            }
        }

//        path.stream().filter((ed) -> ( ed!=null )).forEach((ed) -> {
//            this.convertors.add(ed.getEdge());
//        });

        attachListener();
    }

    private void attachListener() {
//        this.convertors.stream()
//            .filter( (c) -> ( c instanceof WeightChangeSender ) )
//            .forEach( (c) -> {
//                ((WeightChangeSender)c).addWeightChangeListener(listener,true); 
//            });

        for( Function<Object,Object> c : this.convertors ){
            if( c instanceof WeightChangeSender ){
                ((WeightChangeSender)c).addWeightChangeListener(listener, true);
            }
        }
    }

    private final WeightChangeListener listener = new WeightChangeListener() {
        @Override
        public void weightChanged(WeightChangeEvent event) {
            Double oldw = SequenceCaster.this.weight;
            SequenceCaster.this.weight = null;
            fireEvent(oldw, null);
        }
    };

    /**
     * Конструктор
     * @param convertors последовательность преобразователей 
     */
    public SequenceCaster( Iterable<Function<Object,Object>> convertors ){
        this.convertors = new ArrayList<Function<Object,Object>>();
        for( Function<Object,Object> conv : convertors ){
            if( conv!=null ){
                this.convertors.add(conv);
            }
        }

        attachListener();
    }

    /**
     * Возвращает массив преобразователей
     * @return массив преобразователей
     */
    public Function<Object,Object>[] getConvertors(){
        return convertors.toArray(new Function[]{});
    }

    /**
     * конвертирует исходный объект, пропуская его через цепочку преобразователей
     * @param from исходный объект
     * @return сконвертированный объект
     */
    @Override
    public Object apply(Object from) {
        Object v = from;
        for( Function<Object,Object> conv : convertors ){
            v = conv.apply(v);
        }
        return v;
    }

    @Override
    public Double getWeight() {
        if( weight!=null )return weight;

        double w = 0;
        for( Function<Object,Object> conv : convertors ){
            if( conv instanceof GetWeight ){
                Double wc = ((GetWeight)conv).getWeight();
                if( wc!=null )
                    w += wc;
                else
                    w += defaultItemWeight;
            }else{
                w += defaultItemWeight;
            }
        }

        weight = w;
        fireEvent(null, w);
        return weight;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        Object w = getWeight();
        sb.append("Sequence");
        sb.append(" w=").append(w);
        sb.append(" {");
        int i = -1;
        for( Object conv : getConvertors() ){
            i++;
            Object wc = defaultItemWeight;
            if( conv instanceof GetWeight ){
                wc = ((GetWeight)conv).getWeight();
            }
            if( i>0 )sb.append(", ");
            sb.append(conv).append(" w=").append(wc);
        }
        sb.append("}");
        return sb.toString();
    }
}
