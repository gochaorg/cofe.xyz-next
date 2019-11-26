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
    exports xyz.cofe.gui.swing.text;
    exports xyz.cofe.gui.swing.text.str;
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
    exports xyz.cofe.j2d;
    uses xyz.cofe.gui.swing.properties.PropertyDBService;
    provides xyz.cofe.gui.swing.properties.PropertyDBService
        with xyz.cofe.gui.swing.properties.ReadBeanArray,
            xyz.cofe.gui.swing.properties.ReadBeanList,
            xyz.cofe.gui.swing.properties.ReadBeanMap,
            xyz.cofe.gui.swing.properties.ReadBeanProperties;
}