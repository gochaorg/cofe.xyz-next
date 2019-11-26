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
package xyz.cofe.gui.swing.table;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import xyz.cofe.gui.swing.GuiUtil;
import xyz.cofe.gui.swing.text.UITextWriter;
import xyz.cofe.text.Text;
import xyz.cofe.text.out.Output;

// TODO: Test 1 - when filter set - insert in begin of list
// TODO: Test 2 - when filter set - insert in middle of list
// TODO: Test 3 - when filter set - insert in end of list

// TODO: Test 4 - when filter set - delete in begin of list
// TODO: Test 5 - when filter set - delete in middle of list
// TODO: Test 6 - when filter set - delete in end of list

// TODO: Test 7 - when filter set - set in begin of list
// TODO: Test 8 - when filter set - set in middle of list
// TODO: Test 9 - when filter set - set in end of list

/**
 *
 * @author user
 */
public class FilterTMTest
    extends javax.swing.JFrame
{
    private static boolean eq( Object a, Object b ){
        if( a==null && b==null )return true;
        if( a==null && b!=null )return false;
        if( a!=null && b==null )return false;
        return a.equals(b);
    }

    ListTM listTM = new ListTM();
    FilterRowTM filterTM = new FilterRowTM();
    JTable table = new JTable();
    Output log;

    public static class RowRecord {
        protected String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public RowRecord(String text) {
            this.text = text;
        }
    }

    /**
     * Creates new form FilterTMTest
     */
    public FilterTMTest() {
        initComponents();

        log = new Output(new UITextWriter().create(logTextArea, null));

        listTM.getList().add( new RowRecord("line1") );
        listTM.getList().add( new RowRecord("line2") );

        listTM.getColumns().add(
            new Column()
                .name("text")
                .type(String.class)
                //.reader( rr -> ((RowRecord)rr).getText() )
                .reader( rr -> ((RowRecord)rr).getText())
                .writer( cell -> {
                            ((RowRecord)cell.object).setText(cell.newValue.toString());
                            return true;
                        })
        );

        listTM.getColumns().add(
            new Column()
                .name("idx")
                .type(Integer.class)
                .reader( rr -> listTM.getList().indexOf(rr) )
        );

        tablePanel.add( new JScrollPane( table ) );

        filterTM.setTableModel(listTM);

        filterTM.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if( !logFilterCheckBox.isSelected() ){
                    return;
                }

                String type = null;

                if( e.getType()==TableModelEvent.UPDATE )type = "UPDATE";
//                else if( e.getType()==TableModelEvent.ALL_COLUMNS )type = "ALL_COLUMNS";
                else if( e.getType()==TableModelEvent.DELETE )type = "DELETE";
                else if( e.getType()==TableModelEvent.INSERT )type = "INSERT";
//                else if( e.getType()==TableModelEvent.HEADER_ROW )type = "HEADER_ROW";
                else type = Integer.toString(e.getType());

                log.template("filterTM type=${type} f=${e.firstRow} l=${e.lastRow} c=${e.column}")
                    .bind("e", e)
                    .bind("type", type)
                    .println();

                if( e.getLastRow() < Integer.MAX_VALUE &&
                    e.getFirstRow() >=0 &&
                    e.getFirstRow() <= e.getLastRow() )
                {
                    if( e.getType()==TableModelEvent.INSERT || e.getType()==TableModelEvent.UPDATE ){
                        for( int ri=e.getFirstRow(); ri<=e.getLastRow(); ri++ ){
                            log.template("filterTM ${type} ${ri} ${v}")
                                .bind("ri", ri)
                                .bind("type", type)
                                .bind("v", filterTM.getValueAt(ri, 0))
                                .println();
                        }
                    }
                }
            }
        });

        listTM.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if( !logListCB.isSelected() ){
                    return;
                }

                String type = null;

                if( e.getType()==TableModelEvent.UPDATE )type = "UPDATE";
                    //else if( e.getType()==TableModelEvent.ALL_COLUMNS )type = "ALL_COLUMNS";
                else if( e.getType()==TableModelEvent.DELETE )type = "DELETE";
                else if( e.getType()==TableModelEvent.INSERT )type = "INSERT";
//                else if( e.getType()==TableModelEvent.HEADER_ROW )type = "HEADER_ROW";
                else type = Integer.toString(e.getType());

                log.template("listTM type=${type} f=${e.firstRow} l=${e.lastRow} c=${e.column}")
                    .bind("e", e)
                    .bind("type", type)
                    .println();
            }
        });

        table.setModel(filterTM);

        JTable srcTable = new JTable(listTM);
        srcTableScrollPane.setViewportView(srcTable);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                GuiUtil.setWindowDesktopSize(FilterTMTest.this, 0.75, 0.5);
                GuiUtil.centerWindow(FilterTMTest.this);
            }
        });
    }

    public void onAdd(){
        listTM.getList().add( new RowRecord( addTextField.getText() ) );
    }

    public Integer getInsertSetPos(){
        try{
            return Integer.parseInt(insertToTextField.getText());
        }catch( Throwable e ){
            return null;
        }
    }

    public void onInsert(){
        Integer pos = getInsertSetPos();
        if( pos==null )return;

        listTM.getList().add(pos, new RowRecord( addTextField.getText() ) );
    }

    public void onSet(){
        Integer pos = getInsertSetPos();
        if( pos==null )return;

        listTM.getList().set(pos, new RowRecord( addTextField.getText() ) );
    }

    public Integer getRemovePos(){
        try{
            return Integer.parseInt(removeIdxTextField.getText());
        }catch( Throwable e ){
            return null;
        }
    }

    public void removeByIdx(){
        Integer pos = getRemovePos();
        if( pos==null )return;

        listTM.getList().remove((int)pos);
    }

    public void removeAll1(){
        String ptrn = removeAllTextField.getText();
        LinkedHashSet rmset = new LinkedHashSet();
        for( Object o : listTM.getList() ){
            if( o instanceof RowRecord ){
                RowRecord rr = (RowRecord)o;
                if( ptrn.equalsIgnoreCase(rr.getText()) ){
                    rmset.add(rr);
                }
            }
        }

        listTM.getList().removeAll(rmset);
    }

    public void dump(){
        log.println("dump list:");
        int idx = -1;
        for( Object o : listTM.getList() ){
            idx++;
            if( o instanceof RowRecord ){
                RowRecord rr = (RowRecord)o;
                log.template("${i:2}. ${r.text}")
                    .bind("r", rr)
                    .bind("i", idx)
                    .println();
            }else{
                log.template("${i:2}. ${r}")
                    .bind("r", o)
                    .bind("i", idx)
                    .println();
            }
        }

        /*
        log.println("dump row2source / sour2row:");

        TreeMap<Integer,Integer> r2s = filterTM.getRowToSourceMap();
        TreeMap<Integer,Integer> s2r = filterTM.getSourceToRowMap();

        //r2s.forEach( (di1,si1) -> {
        for( Map.Entry<Integer,Integer> en : r2s.entrySet() ){
            Integer di1 = en.getKey();
            Integer si1 = en.getValue();
            Integer si2 = s2r.containsKey(si1) ? si1 : null;
            Integer di2 = si2==null ? null : s2r.get(si2);
            boolean suc1 = di1!=null;
            boolean suc2 = si1!=null;
            boolean suc3 = si2!=null;
            boolean suc4 = di2!=null;
            boolean suc5 = eq(di1, di2);
            boolean suc6 = eq(si1, si2);
            boolean succ = suc1 && suc2 && suc3 && suc4 && suc5 && suc6;
            log.template("${di1:>4} -> ${si1:4} | ${si2:>4} -> ${di2:4} | ${succ}")
                .bind("si1", si1)
                .bind("si2", si2)
                .bind("di1", di1)
                .bind("di2", di2)
                .bind("succ", succ)
                .println();
        }
        */
    }

    public void setFilter(){
        final Pattern ptrn = Text.wildcard(filterTextField.getText(), false, true);

        filterTM.setRowFilter(new Predicate<RowData>() {
            @Override
            public boolean test(RowData rdata) {
                Object otxt = rdata.getValue(0);
                if( otxt==null )return true;

                Matcher m = ptrn.matcher(otxt.toString());
                if( m.matches() ){
                    return true;
                }

                return false;
            }
        });

//        filterTM.applyFilter();
    }

    public void onRepaint(){
        table.repaint();
    }

    public void onRevalidate(){
        table.revalidate();
    }

    public void onFireAllChanged(){
        filterTM.fireAllChanged();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        mainPanel = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        addTextField = new javax.swing.JTextField();
        insertButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        insertToTextField = new javax.swing.JTextField();
        removeByIdxButton = new javax.swing.JButton();
        removeIdxTextField = new javax.swing.JTextField();
        removeAllButton = new javax.swing.JButton();
        removeAllTextField = new javax.swing.JTextField();
        setButton = new javax.swing.JButton();
        dumpButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        filterTextField = new javax.swing.JTextField();
        setFilterButton = new javax.swing.JButton();
        logFilterCheckBox = new javax.swing.JCheckBox();
        logListCB = new javax.swing.JCheckBox();
        repaintButton = new javax.swing.JButton();
        revalidateButton = new javax.swing.JButton();
        fireAllChangedButton = new javax.swing.JButton();
        tablePanel = new javax.swing.JPanel();
        srcTableScrollPane = new javax.swing.JScrollPane();
        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("fliter row tm");

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        mainPanel.setLayout(new java.awt.GridBagLayout());

        controlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("controls"));

        insertButton.setText("insert");
        insertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertButtonActionPerformed(evt);
            }
        });

        addButton.setText("add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        insertToTextField.setText("0");
        insertToTextField.setMinimumSize(new java.awt.Dimension(40, 19));

        removeByIdxButton.setText("remove by idx");
        removeByIdxButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeByIdxButtonActionPerformed(evt);
            }
        });

        removeIdxTextField.setText("0");

        removeAllButton.setText("remove all");
        removeAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllButtonActionPerformed(evt);
            }
        });

        setButton.setText("set");
        setButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setButtonActionPerformed(evt);
            }
        });

        dumpButton.setText("dump");
        dumpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dumpButtonActionPerformed(evt);
            }
        });

        setFilterButton.setText("filter");
        setFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setFilterButtonActionPerformed(evt);
            }
        });

        logFilterCheckBox.setText("log filter");

        logListCB.setText("log list");

        repaintButton.setText("repaint");
        repaintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repaintButtonActionPerformed(evt);
            }
        });

        revalidateButton.setText("revalidate");
        revalidateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revalidateButtonActionPerformed(evt);
            }
        });

        fireAllChangedButton.setText("fire all changed");
        fireAllChangedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireAllChangedButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout controlPanelLayout = new javax.swing.GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setHorizontalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(addTextField)
                .addGroup(controlPanelLayout.createSequentialGroup()
                    .addComponent(addButton)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(insertButton)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(setButton)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(insertToTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(controlPanelLayout.createSequentialGroup()
                    .addComponent(removeByIdxButton)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(removeIdxTextField))
                .addGroup(controlPanelLayout.createSequentialGroup()
                    .addComponent(removeAllButton)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(removeAllTextField))
                .addComponent(jSeparator1)
                .addGroup(controlPanelLayout.createSequentialGroup()
                    .addComponent(filterTextField)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(setFilterButton))
                .addGroup(controlPanelLayout.createSequentialGroup()
                    .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(controlPanelLayout.createSequentialGroup()
                            .addComponent(dumpButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(repaintButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(revalidateButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(fireAllChangedButton))
                        .addGroup(controlPanelLayout.createSequentialGroup()
                            .addComponent(logFilterCheckBox)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(logListCB)))
                    .addGap(0, 0, Short.MAX_VALUE))
        );
        controlPanelLayout.setVerticalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(controlPanelLayout.createSequentialGroup()
                    .addComponent(addTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(insertButton)
                        .addComponent(addButton)
                        .addComponent(insertToTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(setButton))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(removeByIdxButton)
                        .addComponent(removeIdxTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(removeAllButton)
                        .addComponent(removeAllTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(dumpButton)
                        .addComponent(repaintButton)
                        .addComponent(revalidateButton)
                        .addComponent(fireAllChangedButton))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(filterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(setFilterButton))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(logFilterCheckBox)
                        .addComponent(logListCB))
                    .addGap(0, 0, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        mainPanel.add(controlPanel, gridBagConstraints);

        tablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("table"));
        tablePanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        mainPanel.add(tablePanel, gridBagConstraints);

        srcTableScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("source table"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        mainPanel.add(srcTableScrollPane, gridBagConstraints);

        jSplitPane1.setLeftComponent(mainPanel);

        logTextArea.setColumns(20);
        logTextArea.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        logTextArea.setRows(5);
        logScrollPane.setViewportView(logTextArea);

        jSplitPane1.setRightComponent(logScrollPane);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>

    private void setFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setFilter();
    }

    private void dumpButtonActionPerformed(java.awt.event.ActionEvent evt) {
        dump();
    }

    private void setButtonActionPerformed(java.awt.event.ActionEvent evt) {
        onSet();
    }

    private void removeAllButtonActionPerformed(java.awt.event.ActionEvent evt) {
        removeAll1();
    }

    private void removeByIdxButtonActionPerformed(java.awt.event.ActionEvent evt) {
        removeByIdx();
    }

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
        onAdd();
    }

    private void insertButtonActionPerformed(java.awt.event.ActionEvent evt) {
        onInsert();
    }

    private void repaintButtonActionPerformed(java.awt.event.ActionEvent evt) {
        onRepaint();
    }

    private void revalidateButtonActionPerformed(java.awt.event.ActionEvent evt) {
        onRevalidate();
    }

    private void fireAllChangedButtonActionPerformed(java.awt.event.ActionEvent evt) {
        onFireAllChanged();
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
            java.util.logging.Logger.getLogger(FilterTMTest.class.getName()).log(java.util.logging.Level.SEVERE, null,
                ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FilterTMTest.class.getName()).log(java.util.logging.Level.SEVERE, null,
                ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FilterTMTest.class.getName()).log(java.util.logging.Level.SEVERE, null,
                ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FilterTMTest.class.getName()).log(java.util.logging.Level.SEVERE, null,
                ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FilterTMTest().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JButton addButton;
    private javax.swing.JTextField addTextField;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton dumpButton;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JButton fireAllChangedButton;
    private javax.swing.JButton insertButton;
    private javax.swing.JTextField insertToTextField;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JCheckBox logFilterCheckBox;
    private javax.swing.JCheckBox logListCB;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton removeAllButton;
    private javax.swing.JTextField removeAllTextField;
    private javax.swing.JButton removeByIdxButton;
    private javax.swing.JTextField removeIdxTextField;
    private javax.swing.JButton repaintButton;
    private javax.swing.JButton revalidateButton;
    private javax.swing.JButton setButton;
    private javax.swing.JButton setFilterButton;
    private javax.swing.JScrollPane srcTableScrollPane;
    private javax.swing.JPanel tablePanel;
    // End of variables declaration
}
