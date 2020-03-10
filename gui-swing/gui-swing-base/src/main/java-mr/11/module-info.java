open module xyz.cofe.gui.swing.base {
    requires java.base;

    requires java.logging;
    requires java.desktop;

    requires xyz.cofe.ecolls;
    requires xyz.cofe.text;
    requires xyz.cofe.text.out;
    //requires xyz.cofe.text.lex;
    //requires xyz.cofe.j2d;

    requires balloontip;

    exports xyz.cofe.gui.swing;
}