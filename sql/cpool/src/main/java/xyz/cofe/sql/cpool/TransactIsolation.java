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

package xyz.cofe.sql.cpool;

import java.sql.Connection;

/**
 * Режим изоляции транзакций.
 * 
 * <p>
 * <a href="https://ru.wikipedia.org/wiki/%D0%A3%D1%80%D0%BE%D0%B2%D0%B5%D0%BD%D1%8C_%D0%B8%D0%B7%D0%BE%D0%BB%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%BD%D0%BE%D1%81%D1%82%D0%B8_%D1%82%D1%80%D0%B0%D0%BD%D0%B7%D0%B0%D0%BA%D1%86%D0%B8%D0%B9">
 * Основная статя на wiki
 * </a>
 * 
 * При параллельном выполнении транзакций возможны следующие проблемы:
 * <ul>
 * <li> потерянное обновление (англ. lost update) — при одновременном изменении одного блока данных разными 
 * транзакциями одно из изменений теряется;
 * 
 * <li> «грязное» чтение (англ. dirty read) — чтение данных, добавленных или изменённых транзакцией, 
 * которая впоследствии не подтвердится (откатится);
 * 
 * <li> неповторяющееся чтение (англ. non-repeatable read) — при повторном чтении 
 * в рамках одной транзакции ранее прочитанные данные оказываются изменёнными;
 * 
 * <li> фантомное чтение (англ. phantom reads) — одна транзакция в ходе своего выполнения 
 * несколько раз выбирает множество строк по одним и тем же критериям. 
 * 
 * Другая транзакция в интервалах между этими выборками добавляет или удаляет 
 * строки или изменяет столбцы некоторых строк, используемых 
 * в критериях выборки первой транзакции, и успешно заканчивается. 
 * 
 * В результате получится, что одни и те же выборки в первой транзакции дают разные множества строк.
 * </ul>
 * 
 * @author Kamnev Georgiy (nt.gocha@gmail.com)
 */
public enum TransactIsolation {
    /**
     * чтение незафиксированных данных.
     * 
     * <p>
     * Низший (первый) уровень изоляции. 
     * 
     * <p>
     * Он гарантирует только отсутствие потерянных обновлений. 
     * 
     * <p>
     * Если несколько параллельных транзакций пытаются изменять одну и ту же строку таблицы, 
     * то в окончательном варианте строка будет иметь значение, определенное 
     * всем набором успешно выполненных транзакций. 
     * 
     * <p>
     * При этом возможно считывание не только логически несогласованных данных, но и данных, 
     * изменения которых ещё не зафиксированы.
     * 
     * <p>
     * Типичный способ реализации данного уровня изоляции — блокировка данных 
     * на время выполнения команды изменения, что гарантирует, 
     * что команды изменения одних и тех же строк, запущенные параллельно, 
     * фактически выполнятся последовательно, и ни одно из изменений не потеряется. 
     * 
     * <p>
     * Транзакции, выполняющие только чтение, при данном уровне изоляции никогда не блокируются.
     */
    Uncommitted,
    
    /**
     * чтение фиксированных данных.
     * 
     * <p>
     * Большинство промышленных СУБД, в частности, Microsoft SQL Server, PostgreSQL и Oracle, 
     * по умолчанию используют именно этот уровень. 
     * 
     * <p>
     * На этом уровне обеспечивается защита от чернового, «грязного» чтения, 
     * тем не менее, в процессе работы одной транзакции другая может быть успешно завершена и сделанные 
     * ею изменения зафиксированы. 
     * 
     * <p>
     * В итоге первая транзакция будет работать с другим набором данных.
     * 
     * <p>
     * Реализация завершённого чтения может основываться на одном из двух подходов: блокировании или версионности.
     * 
     * <p>
     * <b>Блокирование читаемых и изменяемых данных.</b>
     * 
     * <p>
     * Заключается в том, что пишущая транзакция блокирует изменяемые данные для читающих транзакций, 
     * работающих на уровне read committed или более высоком, до своего завершения, препятствуя, 
     * таким образом, «грязному» чтению, а данные, блокируемые читающей транзакцией, 
     * освобождаются сразу после завершения операции SELECT 
     * (таким образом, ситуация «неповторяющегося чтения» может возникать на данном уровне изоляции).
     * 
     * <p>
     * <b>Сохранение нескольких версий параллельно изменяемых строк.</b>
     * 
     * <p>
     * При каждом изменении строки СУБД создаёт новую версию этой строки, 
     * с которой продолжает работать изменившая данные транзакция, 
     * в то время как любой другой «читающей» транзакции возвращается 
     * последняя зафиксированная версия. 
     * 
     * <p>
     * Преимущество такого подхода в том, что он обеспечивает большую скорость, так как предотвращает блокировки. 
     * 
     * <p>
     * Однако он требует, по сравнению с первым, существенно большего расхода оперативной памяти, 
     * которая тратится на хранение версий строк. 
     * 
     * <p>
     * Кроме того, при параллельном изменении данных несколькими транзакциями может создаться ситуация, 
     * когда несколько параллельных транзакций произведут несогласованные изменения одних и тех же данных 
     * (поскольку блокировки отсутствуют, ничто не помешает это сделать). 
     * 
     * <p>
     * Тогда та транзакция, которая зафиксируется первой, 
     * сохранит свои изменения в основной БД, 
     * а остальные параллельные транзакции окажется невозможно зафиксировать 
     * (так как это приведёт к потере обновления первой транзакции). 
     * 
     * <p>
     * Единственное, что может в такой ситуации СУБД — это откатить остальные транзакции и 
     * выдать сообщение об ошибке «Запись уже изменена».
     * 
     * <p>
     * Конкретный способ реализации выбирается разработчиками СУБД, 
     * а в ряде случаев может настраиваться. 
     * 
     * <p>
     * Так, по умолчанию MS SQL использует блокировки, но (в версии 2005 и выше) 
     * при установке параметра READ_COMMITTED_SNAPSHOT базы данных переходит на стратегию версионности, 
     * 
     * <p>
     * Oracle исходно работает только по версионной схеме. 
     * 
     * <p>
     * В Informix можно предотвратить конфликты между читающими и пишущими транзакциями, 
     * установив параметр конфигурации USELASTCOMMITTED (начиная с версии 11.1), 
     * 
     * при этом читающая транзакция будет получать последние подтвержденные данные
     */
    Committed,
    
    /**
     * повторяемость чтения.
     * 
     * <p>
     * Уровень, при котором читающая транзакция «не видит» изменения данных, которые были ею ранее прочитаны. 
     * 
     * <p>
     * При этом никакая другая транзакция не может изменять данные, читаемые текущей транзакцией, пока та не окончена.
     * 
     * <p>
     * Блокировки в разделяющем режиме применяются ко всем данным, 
     * считываемым любой инструкцией транзакции, и сохраняются до её завершения. 
     * 
     * <p>
     * Это запрещает другим транзакциям изменять строки, которые были считаны незавершённой транзакцией. 
     * 
     * <p>
     * Однако другие транзакции могут вставлять новые строки, соответствующие условиям поиска инструкций, 
     * содержащихся в текущей транзакции. 
     * 
     * <p>
     * При повторном запуске инструкции текущей транзакцией будут извлечены новые строки, 
     * что приведёт к фантомному чтению. 
     * 
     * <p>
     * Учитывая то, что разделяющие блокировки сохраняются до завершения транзакции, 
     * а не снимаются в конце каждой инструкции, степень параллелизма ниже, 
     * чем при уровне изоляции READ COMMITTED. 
     * 
     * <p>
     * Поэтому пользоваться данным и более высокими уровнями транзакций без необходимости обычно не рекомендуется.  
     */
    Repeatable,
    
    /**
     * упорядочиваемость.
     * 
     * <p>
     * Самый высокий уровень изолированности; 
     * 
     * <p>
     * транзакции полностью изолируются друг от друга, каждая выполняется так, 
     * как будто параллельных транзакций не существует. 
     * 
     * <p>
     * Только на этом уровне параллельные транзакции не подвержены эффекту «фантомного чтения».
     */
    Serializable;

    public int value(){
        switch(this){
            case Uncommitted: return Connection.TRANSACTION_READ_UNCOMMITTED;
            case Committed: return Connection.TRANSACTION_READ_COMMITTED;
            case Repeatable: return Connection.TRANSACTION_REPEATABLE_READ;
            case Serializable: return Connection.TRANSACTION_SERIALIZABLE;
            default:
                return Connection.TRANSACTION_REPEATABLE_READ;
        }
    }
}