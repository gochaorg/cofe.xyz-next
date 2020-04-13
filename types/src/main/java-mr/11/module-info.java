open module xyz.cofe.typeconv {
    requires java.base;
    requires transitive java.logging;
    requires transitive xyz.cofe.typeconv.spi;
    requires xyz.cofe.ecolls;
    exports xyz.cofe.typeconv;
    uses xyz.cofe.typeconv.spi.GetTypeConvertor;
}