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
 * Пустышка - ничего не пишет
 * @author nt.gocha@gmail.com
 */
public class QueryStreamDummy implements QueryStream {
    @Override
    public void queryStreamBegin() {
    }

    @Override
    public void tableBegin(int tableIndex,ScnMark scm) {
    }

    @Override
    public void metaBegin(ScnMark scm) {
    }

    @Override
    public void columnBegin(int columnIndex, ScnMark scm) {
    }

    @Override
    public void columnProperty( String key, Object value, ScnMark scm) {
    }
    
    @Override
    public void columnEnd() {
    }

    @Override
    public void metaEnd() {
    }

    @Override
    public void dataBegin(ScnMark scm) {
    }

    @Override
    public void rowBegin(int rowIndex, ScnMark scm) {
    }

    @Override
    public void cell( int columnIndex, Object value, ScnMark scm) {
    }
    
    @Override
    public void rowEnd() {
    }
    
    @Override
    public void dataEnd() {
    }

    @Override
    public void tableEnd() {
    }

    @Override
    public void queryStreamEnd() {
    }

    @Override
    public void updateCount(int count, ScnMark scm) {
    }

    @Override
    public void message( Message message) {
    }

    @Override
    public void error( Err err, ScnMark scm) {
    }

    @Override
    public void generatedKeysBegin(ScnMark scm){
    }
    
    @Override
    public void generatedKeysEnd(){
    }
}
