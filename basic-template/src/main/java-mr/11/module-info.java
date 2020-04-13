open module xyz.cofe.text.template.basic {
    requires java.base;
    requires transitive java.logging;
    requires transitive java.desktop;
    requires transitive java.scripting;
    requires transitive xyz.cofe.typeconv.spi;
    requires transitive xyz.cofe.text.lex;
    requires xyz.cofe.ecolls;
    requires xyz.cofe.text;
    requires xyz.cofe.typeconv;
    exports xyz.cofe.text.template;
    exports xyz.cofe.text.template.ast;
    uses xyz.cofe.typeconv.spi.GetTypeConvertor;
}