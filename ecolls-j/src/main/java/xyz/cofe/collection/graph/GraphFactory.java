/*
 * The MIT License
 *
 * Copyright 2014 Kamnev Georgiy (nt.gocha@gmail.com).
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
package xyz.cofe.collection.graph;

import xyz.cofe.ecolls.ReadWriteLockSupport;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Интерфейс фабрики классов графа
 * @author GoCha
 * @param <N> Тип вершины
 * @param <E> Тип дуги/ребра
 */
public interface GraphFactory<N,E>
{
    /**
     * Указывает блокировки чтения/записи
     * @param rwLocks блокировки
     * @return self ссылка
     */
    default GraphFactory<N,E> readWriteLocks( ReadWriteLock rwLocks ){
        return this;
    }

    /**
     * Указывает блокировки чтения/записи
     * @param rwLocks блокировки
     * @return self ссылка
     */
    default GraphFactory<N,E> readWriteLocks( ReadWriteLockSupport rwLocks ){
        return this;
    }

    /**
     * Указывает блокировки чтения/записи
     * @param readLock блокировка чтения
     * @param writeLock блокировка записи
     * @return self ссылка
     */
    default GraphFactory<N,E> readWriteLocks( Lock readLock, Lock writeLock ){
        return this;
    }

    /**
     * Создание ребра с вершинами
     * @param a Вершина А
     * @param e Ребро между вершинами А и Б
     * @param b Вершина Б
     * @return Ребро
     */
    public abstract Edge<N, E> createEdge(N a, N b, E e);

    /**
     * Создание списка ребр с вершинами
     * @return Список ребр
     */
    Collection<Edge<N, E>> createEdgePairs();

    /**
     * Создаение списка вершин
     * @return Список вершин
     */
    Collection<N> createNodes();

    /**
     * Создание списка ребр
     * @return Список ребр
     */
    Collection<E> createEdges();
}
