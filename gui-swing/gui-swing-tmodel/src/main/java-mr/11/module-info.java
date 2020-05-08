open module xyz.cofe.gui.swing.tmodel {
    requires java.base;
    requires java.logging;
    requires java.desktop;

    requires transitive xyz.cofe.ecolls;
    requires transitive xyz.cofe.text.out;

    exports xyz.cofe.gui.swing.tmodel;
    exports xyz.cofe.gui.swing.tmodel.impl;
}