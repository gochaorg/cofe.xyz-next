/*
 * The MIT License
 *
 * Copyright 2017 user.
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
package xyz.cofe.xml.stream.path;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Аннотации вызовов методов при обходе xml.
 * XML Путь (поддерживаемый PathParser) <p>
 * 
 * Примеры:
 * <pre style="font-size:12pt">
 * &#64;PathMatch(enter = "tag/subtag")
 * public void begin( XEventPath path ){ ... }
 * </pre>
 * 
 * <pre style="font-size:12pt">
 * &#64;PathMatch(exit = "/root/subtag")
 * public void finish( XEventPath path ){ ... }
 * </pre>
 * 
 * <pre style="font-size:12pt">
 * &#64;PathMatch(content = "tag1")
 * public void finish( XEventPath path, String content ){ ... }
 * </pre>
 * 
 * <pre style="font-size:12pt">
 * &#64;PathMatch(content = "tag2")
 * public void finish( String content, XEventPath path ){ ... }
 * </pre>
 * @author user
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PathMatch {
    /**
     * делает вызов при наличии контента
     * @return Путь
     */
    String content() default "";
    
    /**
     * Сделать вызов при входе в узел 
     * @return Путь
     */
    String enter() default "";
    
    /**
     * Сделать вызов при вызов при выходе
     * @return Путь
     */
    String exit() default "";
}
