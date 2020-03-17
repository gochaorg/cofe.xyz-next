open module xyz.cofe.xparse {
    requires java.base;
    requires transitive java.logging;
    requires transitive xyz.cofe.ecolls;
    exports xyz.cofe.text.tparse;
}