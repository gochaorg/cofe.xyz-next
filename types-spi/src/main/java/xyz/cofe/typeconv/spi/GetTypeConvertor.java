package xyz.cofe.typeconv.spi;

import java.util.function.Function;

/**
 * Интерфейс для написания расширения (ExtendedClassGraph) прербразования типов данных
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 * @ see ExtendedCastGraph
 */
public interface GetTypeConvertor {
    /**
     * Указывает исходный тип данных
     * @return исходный тип данных
     */
    public Class getSourceType();
    /**
     * Указывает целевой тип данных
     * @return целевой тип данных
     */
    public Class getTargetType();
    /**
     * Возвращает конвертор преобразующий данный исходного типа в данные целевого типа
     * @return конвертор данных
     */
    public Function<Object,Object> getConvertor();
}
