open module xyz.cofe.gui.swing {
    requires java.base;
    requires transitive java.logging;
    requires transitive xyz.cofe.ecolls;
    requires transitive java.desktop;
    exports xyz.cofe.gui.swing;
}