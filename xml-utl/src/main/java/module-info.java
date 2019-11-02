open module xyz.cofe.xml.utl {
    requires java.base;
    requires transitive java.logging;
    requires transitive xyz.cofe.ecolls;
    requires transitive xyz.cofe.text;
    requires transitive xyz.cofe.text.lex;
    requires transitive xyz.cofe.text.template.basic;
    requires transitive xyz.cofe.io.fs;
    requires transitive xyz.cofe.typeconv;
    exports xyz.cofe.xml;
    exports xyz.cofe.xml.stream.path;
}