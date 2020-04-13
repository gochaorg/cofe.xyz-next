/*
 * The MIT License
 *
 * Copyright 2018 user.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package xyz.cofe.collection.graph;

import java.util.List;
import xyz.cofe.collection.graph.Path;

/**
 * Интерфейс вставки/добавление путей к списку существующих
 * @param <N> Тип вершины
 * @param <E> Тип ребра
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public interface GraphIteratorPusher<N,E> {
    /**
     * Добавление/вставка новых путей
     * @param reciveList Принимающий список
     * @param pushList Список добавляемых путей
     */
    void push( List<Path<N,E>> reciveList, List<Path<N,E>> pushList );

    /**
     * Добавляет элементы в конец списка
     * @param <N> Тип вершины
     * @param <E> Тип ребра
     */
    public static class AppendPusher<N,E> implements GraphIteratorPusher<N,E> {
        @Override
        public void push(List<Path<N, E>> reciveList, List<Path<N, E>> pushList) {
            if( reciveList==null )return;
            if( pushList==null )return;
            for( Path<N, E> p : pushList ){
                if( p!=null ){
                    reciveList.add(p);
                }
            }
        }
    }

    /**
     * Добавляет элементы в начало списка
     * @param <N> Тип вершины
     * @param <E> Тип ребра
     */
    public static class PrependPusher<N,E> implements GraphIteratorPusher<N,E> {
        @Override
        public void push(List<Path<N, E>> reciveList, List<Path<N, E>> pushList) {
            if( reciveList==null )return;
            if( pushList==null )return;
            int idx = -1;
            for( Path<N, E> p : pushList ){
                if( p!=null ){
                    idx++;
                    reciveList.add(idx,p);
                }
            }
        }
    }
}
