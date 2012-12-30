package me.juang.viewer;

/**
 * Modification from tree example -- Java
 */
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import me.juang.controller.JenaController;
import me.juang.model.JenaTreeNode;

@SuppressWarnings("serial")
public class JenaViewer extends JPanel implements TreeSelectionListener {
	
    private JEditorPane htmlPane;
    private JTree tree;
    private JTable table;
    private JenaTableModel tableModel;
    
    private static boolean DEBUG = false;
    private static boolean playWithLineStyle = false;
    private static String lineStyle = "Horizontal";

    private Vector<JenaTreeNode> instanceList;
    
    //Optionally set the look and feel.
    private static boolean useSystemLookAndFeel = false;

    public JenaViewer() {
        super(new GridLayout(1,0));

        instanceList = new Vector<JenaTreeNode>();

        //Create a tree that allows one selection at a time.
        tree = new JTree(JenaController.model.getTreeModel());
        tree.getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);

        if (playWithLineStyle) {
            System.out.println("line style = " + lineStyle);
            tree.putClientProperty("JTree.lineStyle", lineStyle);
        }

        //Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(tree);

        //Create the HTML viewing pane.
        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        JScrollPane htmlView = new JScrollPane(htmlPane);
        
        //Create the Jtable viewing pane
        tableModel = new JenaTableModel();
        table = new JTable(tableModel);
        JScrollPane tableView = new JScrollPane(table);

        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(tableView);

        Dimension minimumSize = new Dimension(200, 200);
        htmlView.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(200); 
        splitPane.setPreferredSize(new Dimension(640, 480));

        //Add the split pane to this panel.
        add(splitPane);
    }

    /** Required by TreeSelectionListener interface. */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();

        if (node == null) return;

        JenaTreeNode nodeInfo = (JenaTreeNode)node.getUserObject();
        displayInstances(nodeInfo);
        if (DEBUG) {
            System.out.println(nodeInfo.toString());
        }
    }
    
    public void displayInstances(JenaTreeNode node) {
    	tableModel.changeConcept(node.getURI());
    	
    	instanceList.clear();
    	instanceList.addAll(JenaController.model.getInstances(node.getURI()));
    	StringBuilder sb = new StringBuilder();
    	
    	for(Iterator<JenaTreeNode> i = instanceList.iterator(); i.hasNext(); ) {
    		sb.append(i.next().toString()).append("\n");
    	}
    	
    	htmlPane.setText(sb.toString());
    }
        
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    public static void createAndShowGUI() {
        if (useSystemLookAndFeel) {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Couldn't use system look and feel.");
            }
        }

        //Create and set up the window.
        JFrame frame = new JFrame("Hola");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new JenaViewer());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
