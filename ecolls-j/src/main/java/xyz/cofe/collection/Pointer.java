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
package xyz.cofe.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Числовой указатель на смешение в списке.
 * Основные функции:
 * <ul>
 * <li>Получить элемент с заданым смещение относительно указателя</li>
 * <li>Передвинуть указатель</li>
 * <li>Сохранить/Восстановить указатель из стека</li>
 * </ul>
 * @author gocha
 * @param <A> Элемент списка
 */
public class Pointer<A>
{
    /**
     * Список элеменотов
     */
    protected List<A> elements = null;

    /**
     * Текущее смещение
     */
    protected int index = 0;

    /**
     * Стек смещений
     */
    protected Stack<Integer> stack = new Stack<Integer>();

    /**
     * Конструктор
     * @param tokens Исходный список объектов (копирует объекты в собственный список)
     */
    public Pointer(Iterable<A> tokens) {
        if (tokens == null) {
            throw new IllegalArgumentException("tokens==null");
        }
        this.elements = new ArrayList<A>();
        for( A a : tokens )this.elements.add(a);
    }

    /**
     * Конструткор
     * @param tokens Исходный список объектов
     */
    public Pointer(List<A> tokens) {
        if (tokens == null) {
            throw new IllegalArgumentException("tokens==null");
        }
        this.elements = tokens;
    }

    /**
     * Возвращает объект из списка относительно текущего элемента
     * @param offset Смещение относительно указателя
     * @return Объект или null, если смещение+указатель за границой списка объектов
     */
    public A lookup(int offset) {
        int t = offset + index;
        if (t < 0 || t >= elements.size())
            return null;
        return elements.get(t);
    }

    /**
     * Возвращает список объектов
     * @return Список объектов
     */
    public List<A> getList(){
        return this.elements;
    }

    /**
     * Возвращает стек указателей
     * @return Стек указателей
     */
    public Stack<Integer> getStack(){
        return this.stack;
    }

    /**
     * Перемещает указатель на указанное кол-во элементов
     * @param offset Указатель
     */
    public void move(int offset) {
        this.setIndex(this.index + offset);
    }

    /**
     * Возвращает указатель
     * @return Указатель
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Устанавливает новое значение указателя
     * @param idx Указатель
     */
    public void setIndex(Integer idx) {
        if (idx == null)
            return;
        Object old = this.index;
        this.index = idx;
    }

    /**
     * Сохранить указатель в стеке
     */
    public void push() {
        stack.push(getIndex());
    }

    /**
     * Прочитать указатель из стека и удалить верхнее значение. <p>
     * Указатель <b>НЕ перемещается</b> на восстановленное место.
     * @return Сохраненный указатель или null
     * @see #restore()
     */
    public Integer pop() {
        if (stack.isEmpty())
            return null;
        return stack.pop();
    }

    /**
     * Посмотреть указатель на верху стека.
     * @return Сохраненный указатель или null т.к. стек пуст
     */
    public Integer peek() {
        if (stack.isEmpty())
            return null;
        return stack.peek();
    }

    /**
     * Восстанавлиает ранее сохраненный указатель в стеке. <p>
     * Указатель <b>перемещается</b> на восстановленное место.
     * @return true - указатель был восстановлен; false - не был, ибо стек пуст
     */
    public boolean restore(){
        Integer i = pop();
        if( i!=null ){
            setIndex(i);
            return true;
        }
        return false;
    }
}
