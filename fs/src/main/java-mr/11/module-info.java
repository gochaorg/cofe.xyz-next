open module xyz.cofe.io.fs {
    requires java.base;
    requires transitive java.logging;
    requires transitive xyz.cofe.ecolls;
    requires transitive xyz.cofe.cbuffer;
    requires transitive xyz.cofe.io.fn;
    exports xyz.cofe.io.fs;
}