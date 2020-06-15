open module xyz.cofe.data.table {
    requires java.base;
    requires transitive java.logging;
    requires transitive java.desktop;
    requires transitive java.sql;
    requires transitive xyz.cofe.ecolls;
    requires transitive xyz.cofe.data.events;
    requires transitive xyz.cofe.simpletypes;
    requires transitive xyz.cofe.typeconv;
    requires transitive xyz.cofe.xml.utl;
    requires transitive xyz.cofe.text.out;
    exports xyz.cofe.data.table;
    exports xyz.cofe.data.table.store;
}