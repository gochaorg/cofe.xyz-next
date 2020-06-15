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
package xyz.cofe.sql.stream;

/**
 * Пишет результат запроса SQL.
 * 
 * <p>
 * В обычном случае должна быть примерно такая последоавтельность вызовов:
 * 
 * <ol>
 * <li>queryStreamBegin()
 * <ol>
 * <li> tableBegin() Запись табличных данных - возможно вызов несколько раз
 * <ol>
 * <li> Запись мета данных
 * <ol>
 * <li> metaBegin() - Запись мета данных
 * <li> columnBegin() - Запись данных о колонке - возможно (скорей всего) вызов несколько раз
 * <li> columnProperty()
 * <li> columnEnd()
 * <li> metaEnd()
 * </ol>
 * <li> Запись данных
 * <ol>
 * <li> dataBegin()
 * <li> rowBegin()
 * <li> cell()
 * <li> rowEnd()
 * <li> dataEnd()
 * </ol>
 * </ol>
 * <li> tableEnd()
 * <li> generatedKeysBegin() - Запись данных о автоматически сгенерированых первичных ключах
 * <ol>
 * <li> последовательность вызовов аналогична внутриннястям tableBegin() .. tableEnd()
 * </ol>
 * <li>generatedKeysEnd() - конец записи о сгенерированных первичных ключах
 * <li>updateCount() - кол-во измененных строк
 * <li>error() - сообщение о ошибке *
 * <li>message() - сообщение генерируемое базой при выборке данных (PRINT/DBMS_OUTPUT) *
 * </ol>
 * <li>queryStreamEnd() - конец табличных данных
 * </ol>
 * * - вызов error() и message() - может происходить из другого потока для синхронизации 
 * событий используются метки ScnMark
 * @author nt.gocha@gmail.com
 * @see ScnMark
 */
public interface QueryStream {
    /**
     * Отмечает начало записи SQL выборки.
     * <p>
     * В процессе работы вызываются все остальные методы, послединй меотд обычно должен быть queryStreamEnd.
     * <br>
     * Возможна ситуация, что в процессе выборки события message и error будут происходить из другого трэда ОС.
     * @see #queryStreamEnd() 
     */
    void queryStreamBegin();
    
    //<editor-fold defaultstate="collapsed" desc="table stream">
    /**
     * Начало табличных данных
     * @param tableIndex индекс табличных данных, 0 - первая выборка, возможно более одной выборки табличных данных в результате запроса
     * @param scm отмета SCN
     */
    void tableBegin(int tableIndex, ScnMark scm);
    
    /**
     * Начало описания табличных данных (мета данные)
     * @param scm отметка SCN
     */
    void metaBegin(ScnMark scm);
    
    /**
     * Начало колонки (мета данные)
     * @param columnIndex индекс колонки
     * @param scm отметка SCN
     */
    void columnBegin(int columnIndex, ScnMark scm);
    
    /**
     * Описание колонки (мета данные)
     * @param key имя свойства
     * @param value значение свойства
     * @param scm отметка SCN
     */
    void columnProperty( String key, Object value, ScnMark scm);
    
    /**
     * Конец описания колонки (мета данные)
     */
    void columnEnd();
    
    /**
     * Конец описания мета данных выборки
     */
    void metaEnd();
    
    /**
     * Начало данных выборки
     * @param scm отметка SCN
     */
    void dataBegin(ScnMark scm);
    
    /**
     * Начало данных строки из выборки
     * @param rowIndex иднекс строки
     * @param scm отметка SCN
     */
    void rowBegin(int rowIndex, ScnMark scm);
    
    /**
     * Данные ячейки строки из выборки
     * @param columnIndex индекс колонки
     * @param value значение
     * @param scm отметка SCN
     */
    void cell( int columnIndex, Object value, ScnMark scm);
    
    /**
     * Конец данных строки из выборки
     */
    void rowEnd();
    
    /**
     * Конец данных выборки
     */
    void dataEnd();
    
    /**
     * Конец табличных данных
     */
    void tableEnd();
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="updateCount()">
    /**
     * Отмечает кол-во созданых/измененных строк
     * @param count кол-во строк
     * @param scm отметка SCN
     */
    void updateCount(int count, ScnMark scm);
    //</editor-fold>
       
    //<editor-fold defaultstate="collapsed" desc="message()">
    /**
     * Сообщения генерируемое базой при выборке данных (PRINT/DBMS_OUTPUT)
     * @param message сообщение
     */
    void message( Message message);
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="error()">
    /**
     * Событие ошибки при чтении данных/получении выборки
     * @param err описание ошибки
     * @param scm отметка SCN
     */
    void error( Err err, ScnMark scm );
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="generatedKeys ..">
    /**
     * Отмечает начало табличных данных сгенерируемых первичных ключей
     * @param scm отметка SCN
     */
    void generatedKeysBegin(ScnMark scm);
    
    /**
     * Отмечает конец дпнных о первичных ключах
     */
    void generatedKeysEnd();
    //</editor-fold>
    
    /**
     * Отмечает конец записи SQL выборки
     */
    void queryStreamEnd();
}
