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
 * Делегирует вызовы к указанному объекту QueryStream
 * @author nt.gocha@gmail.com
 */
public class QueryStreamWrapper implements QueryStream {
    public QueryStreamWrapper( QueryStream target) {
        this.target = target;
    }
    
    protected QueryStream target;
    public QueryStream getTargetQueryStream() {
        return target;
    }
    public void setTargetQueryStream( QueryStream target) {
        this.target = target;
    }

    @Override
    public void queryStreamBegin() {
        target.queryStreamBegin();
    }

    @Override
    public void tableBegin(int tableIndex, ScnMark scm) {
        target.tableBegin(tableIndex, scm);
    }

    @Override
    public void metaBegin(ScnMark scm) {
        target.metaBegin(scm);
    }

    @Override
    public void columnBegin(int columnIndex, ScnMark scm) {
        target.columnBegin(columnIndex, scm);
    }

    @Override
    public void columnProperty( String key, Object value, ScnMark scm) {
        target.columnProperty(key, value, scm);
    }

    @Override
    public void columnEnd() {
        target.columnEnd();
    }

    @Override
    public void metaEnd() {
        target.metaEnd();
    }

    @Override
    public void dataBegin(ScnMark scm) {
        target.dataBegin(scm);
    }

    @Override
    public void rowBegin(int rowIndex, ScnMark scm) {
        target.rowBegin(rowIndex, scm);
    }

    @Override
    public void cell( int columnIndex, Object value, ScnMark scm) {
        target.cell(columnIndex, value, scm);
    }

    @Override
    public void rowEnd() {
        target.rowEnd();
    }

    @Override
    public void dataEnd() {
        target.dataEnd();
    }

    @Override
    public void tableEnd() {
        target.tableEnd();
    }

    @Override
    public void queryStreamEnd() {
        target.queryStreamEnd();
    }

    @Override
    public void updateCount(int count, ScnMark scm) {
        target.updateCount(count, scm);
    }

    @Override
    public void message( Message message) {
        target.message(message);
    }

    @Override
    public void error( Err err, ScnMark scm) {
        target.error(err,scm);
    }

    @Override
    public void generatedKeysBegin(ScnMark scm) {
        target.generatedKeysBegin(scm);
    }

    @Override
    public void generatedKeysEnd() {
        target.generatedKeysEnd();
    }
}
