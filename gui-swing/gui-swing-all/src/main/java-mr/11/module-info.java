open module xyz.cofe.gui.swing.all {
    requires java.base;

    requires java.logging;
    requires java.desktop;
    requires java.scripting;

    requires xyz.cofe.ecolls;
    requires transitive xyz.cofe.xml.utl;
    requires xyz.cofe.text.out;
    requires xyz.cofe.text.lex;
    requires xyz.cofe.j2d;
    requires xyz.cofe.gui.swing.base;
    requires xyz.cofe.gui.swing.str;

    requires balloontip;

    exports xyz.cofe.gui.swing.text;
    exports xyz.cofe.gui.swing.menu;
    exports xyz.cofe.gui.swing.bean;
    exports xyz.cofe.gui.swing.border;
    exports xyz.cofe.gui.swing.cell;
    exports xyz.cofe.gui.swing.color;
    exports xyz.cofe.gui.swing.properties;
    exports xyz.cofe.gui.swing.properties.editor;
    exports xyz.cofe.gui.swing.table;
    exports xyz.cofe.gui.swing.table.de;
    exports xyz.cofe.gui.swing.table.impl;
    exports xyz.cofe.gui.swing.tree;
    exports xyz.cofe.gui.swing.typeconv.impl;

    uses xyz.cofe.gui.swing.properties.PropertyDBService;
    provides xyz.cofe.gui.swing.properties.PropertyDBService
        with xyz.cofe.gui.swing.properties.ReadBeanArray,
            xyz.cofe.gui.swing.properties.ReadBeanList,
            xyz.cofe.gui.swing.properties.ReadBeanMap,
            xyz.cofe.gui.swing.properties.ReadBeanProperties;
}