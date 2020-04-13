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
 * Интерфейс извлечения очередного пути
 * @param <N> Тип вершины
 * @param <E> Тип ребра
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public interface GraphIteratorPoller<N,E> {
    /**
     * Извелчение пути из списка существующих.
     * Извлекаемый путь, должен быть удален из списка
     * @param paths Список путей
     * @return Извлеченный путь
     */
    Path<N,E> poll( List<Path<N,E>> paths );

    /**
     * Извлекает первый элемент из списка
     * @param <N> Тип вершины
     * @param <E> Тип ребра
     */
    public static class FirstPoller<N,E> implements GraphIteratorPoller<N,E> {
        @Override
        public Path<N, E> poll(List<Path<N, E>> paths) {
            if( paths==null )return null;
            if( paths.isEmpty() )return null;
            return paths.remove((int)0);
        }
    }
}
