/*
 * The MIT License
 *
 * Copyright 2017 user.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package xyz.cofe.gui.swing.tree;

import xyz.cofe.io.fn.IOFun;
import xyz.cofe.iter.Eterable;
import xyz.cofe.text.Text;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class TreeTableTest
    extends javax.swing.JFrame {

    /**
     * Creates new form TreeTableTest
     */
    public TreeTableTest() {
        initComponents();

        setLocationRelativeTo(null);

        treeTable1.setRowHeight(22);

        TreeTableHelper helper = new TreeTableHelper(treeTable1);

        helper
            .node(File.class)
            .follow( file -> file.isDirectory() ? Arrays.asList(file.listFiles()) : null )
            .followable( file -> file.isDirectory() )
            .naming( file -> file.getName() )
            .apply();

        helper.nodes().cacheLifeTime(2000L).apply();

        helper
            .column("value")
            .reader(
                File.class,
                f -> {
                    if( f.isFile() ){
                        return Long.toString(f.length());
                    }
                    return "?";
                }
            ).apply()
        ;

        helper.root(new File(".")).visible(true).apply();

        TreeTableNodeBasic nroot = treeTable1.getRoot();
//        nroot.collapse();

//        LinkedHashMap map = new LinkedHashMap();
//        map1 = map;
//        map.put("k1", "v1");
//        map.put("k2", "v2");
//        map.put("k3", "v3");
//
//        nroot.append(new TreeTableNodeBasic(map));

//        helper.node( Map.class )
//            .naming( map -> "map#"+map.hashCode() )
//            .follow( map -> map.entrySet() )
//            .apply();
//
//        helper.node(Map.Entry.class)
//            .naming( en -> en.getKey()!=null ? en.getKey().toString() : "null" )
//            .follow( en -> en.getValue()!=null ? Eterable.single((Object) en.getValue()) : null )
//            .column( "value" )
//            .reader( en -> en.getValue()!=null ? en.getValue().toString() : null )
//            .writer( (en, v) -> {
//                        System.out.println("update entry, key="+en.getKey()+" value="+v);
//                        en.setValue(v);
//                        return v;
//                }
//            )
//            .apply()
//            .apply();

//        helper.column("value")
//            .addValueReader(Map.Entry.class, en -> en.getValue()!=null ? en.getValue().toString() : null)
//            .apply();

    }

    private LinkedHashMap map1;

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        treeTable1 = new TreeTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        dumpMapMI = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tree table test");

        jScrollPane1.setViewportView(treeTable1);

        jMenu1.setText("Tree");

        dumpMapMI.setText("dump map");
        dumpMapMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dumpMapMIActionPerformed(evt);
            }
        });
        jMenu1.add(dumpMapMI);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addContainerGap())
        );

        pack();
    }// </editor-fold>

    private void dumpMapMIActionPerformed(java.awt.event.ActionEvent evt) {
        if( map1==null )return;
        System.out.println("dump of map");
        for( Object en : map1.entrySet() ){
            Object k = ((Map.Entry)en).getKey();
            Object v = ((Map.Entry)en).getValue();
            System.out.println(""+k+"="+v);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TreeTableTest.class.getName()).log(Level.SEVERE, null,
                ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(TreeTableTest.class.getName()).log(Level.SEVERE, null,
                ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(TreeTableTest.class.getName()).log(Level.SEVERE, null,
                ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(TreeTableTest.class.getName()).log(Level.SEVERE, null,
                ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable(){
            public void run(){
                new TreeTableTest().setVisible(true);
            }});
    }

    // Variables declaration - do not modify
    private javax.swing.JMenuItem dumpMapMI;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private TreeTable treeTable1;
    // End of variables declaration
}