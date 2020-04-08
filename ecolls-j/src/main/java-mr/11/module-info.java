/**
 * Базовые алгоритмы
 */
open module xyz.cofe.ecolls {
    requires java.base;
    requires transitive java.logging;

    exports xyz.cofe.ecolls;
    exports xyz.cofe.collection.graph;
    exports xyz.cofe.collection;
    exports xyz.cofe.fn;
    exports xyz.cofe.iter;
    exports xyz.cofe.num;
    exports xyz.cofe.scn;
    exports xyz.cofe.sort;
}