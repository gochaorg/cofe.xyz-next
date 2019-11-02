open module xyz.cofe.gui.swing {
    requires java.base;
    requires java.xml;
    requires transitive java.logging;
    requires transitive xyz.cofe.ecolls;
    requires transitive java.desktop;
    requires transitive java.scripting;
    requires transitive xyz.cofe.xml.utl;
    exports xyz.cofe.gui.swing;
}