open module xyz.cofe.sql.cpool {
    requires java.base;
    requires transitive java.logging;
    requires transitive xyz.cofe.ecolls;
    requires transitive java.sql;
    requires transitive java.desktop;
    requires transitive xyz.cofe.data.events;
    requires transitive xyz.cofe.text;
    requires transitive xyz.cofe.xml.utl;
    exports xyz.cofe.sql.cpool;
    exports xyz.cofe.sql.cpool.proxy;
}