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
package xyz.cofe.sql.proxy;

import java.lang.reflect.Method;

/**
 * Переписываение вызова метода исходного объета
 * @author user
 */
public interface MethodCallRewrite {
    /**
     * Определяет надо переопределять вызов метода или нет
     * @param proxy прокси объекта
     * @param source исходный объект
     * @param method метод
     * @param args аргументы метода
     * @return true - необходимо переопределить вызов - вызвать rewriteCall
     * @see #rewriteCall(Object, Object, Method, Object[])
     */
    boolean isRewriteCall( Object proxy, Object source, Method method, Object[] args );
    
    /**
     * Вызывается вместо вызова исходного метода, если вызов метода isRewriteCall вернуло true
     * @param proxy прокси объекта
     * @param source исходный объект
     * @param method метод
     * @param args аргументы метода
     * @return результат вызова
     */
    Object rewriteCall( Object proxy, Object source, Method method, Object[] args );
}
