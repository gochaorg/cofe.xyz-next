open module xyz.cofe.text.lex {
    requires java.base;
    requires transitive java.logging;
    requires transitive java.desktop;
    requires transitive java.scripting;
    requires transitive xyz.cofe.text;
    exports xyz.cofe.text.lex;
    //uses xyz.cofe.typeconv.spi.GetTypeConvertor;
}