open module xyz.cofe.text.out {
    requires java.base;
    requires transitive java.logging;
    requires transitive java.desktop;
    requires transitive java.scripting;
    requires transitive xyz.cofe.text;
    requires transitive xyz.cofe.text.template.basic;
    requires transitive xyz.cofe.io.fs;
    exports xyz.cofe.text.out;
    //uses xyz.cofe.typeconv.spi.GetTypeConvertor;
}