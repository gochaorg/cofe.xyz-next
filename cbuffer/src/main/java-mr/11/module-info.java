open module xyz.cofe.cbuffer {
    requires java.base;
    requires transitive java.logging;
    requires transitive xyz.cofe.io.fn;
    requires transitive xyz.cofe.ecolls;
    exports xyz.cofe.cbuffer;
}