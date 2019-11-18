open module xyz.cofe.gui.swing {
    requires java.base;
    requires java.xml;
    requires transitive java.logging;
    requires transitive xyz.cofe.ecolls;
    requires transitive java.desktop;
    requires transitive java.scripting;
    requires transitive xyz.cofe.xml.utl;
    requires transitive xyz.cofe.text.out;
    requires transitive xyz.cofe.text.lex;
    requires transitive balloontip;
    exports xyz.cofe.gui.swing;
    exports xyz.cofe.gui.swing.text.str;
    exports xyz.cofe.gui.swing.menu;
}