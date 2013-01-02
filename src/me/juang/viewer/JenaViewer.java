package me.juang.viewer;

/**
 * Modification from tree example -- Java
 */
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import me.juang.controller.JenaController;
import me.juang.model.JenaTreeNode;
import me.juang.viewer.InstanceEditor.JenaInstanceEditor;
import me.juang.viewer.filter.JenaFilterComponent;

import com.hp.hpl.jena.vocabulary.OWL;

@SuppressWarnings("serial")
public class JenaViewer extends JPanel implements TreeSelectionListener {
	
//    private JEditorPane htmlPane;
    private JTree tree;
//    private JTable table;
    private JTabbedPane tabbedPanel;
    private JenaInstanceEditor instanceEditor;
//    private JenaRowEditor tableRowEditor;
    private JenaFilterComponent tableFilter;
    
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
//        htmlPane = new JEditorPane();
//        htmlPane.setEditable(false);
        
        //Create the Jtable viewing pane
//        table = new JTable(JenaController.tableModel);
        JenaController.table.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable)e.getSource();
					int row = target.getSelectedRow();
					tabbedPanel.setSelectedIndex(1);
					instanceEditor.initInstanceEditor(JenaController.tableModel.getIndividualURI(row));
				}
			}
		});
        
        
//        tableRowEditor = new JenaRowEditor(table);
//        
//        table.getModel().addTableModelListener(new TableModelListener() {
//			
//			@Override
//			public void tableChanged(TableModelEvent e) {
//				int col = e.getColumn();
//				int row = e.getFirstRow();
//				if(e.getType()==TableModelEvent.UPDATE && col>=0 && row>=0) {
//					System.out.println(e.getColumn()+"+"+e.getFirstRow());
//					JComboBox box = (JComboBox)tableModel.getValueAt(row, col);
//					tableRowEditor.setEditorAt(e.getFirstRow(), new DefaultCellEditor(box));
//					System.out.println(table.getColumnCount());
//					String tString = tableModel.getColumnName(col);
//					table.getColumn(tString).setCellEditor(tableRowEditor);
//				}
//			}
//		});
        
        instanceEditor = new JenaInstanceEditor();
        
        JScrollPane tableView = new JScrollPane(JenaController.table);
        tabbedPanel = new JTabbedPane();
        tabbedPanel.add("Instance Browser", tableView);
        tabbedPanel.add("Instance Editor", instanceEditor); // TODO: Change this panel into JenaTableModel

        Dimension minimumSize = new Dimension(200, 200);
        
        tableFilter = new JenaFilterComponent(OWL.Thing.toString());
        tableFilter.setMinimumSize(minimumSize);
        
        
        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(tabbedPanel);
        JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane, tableFilter);

        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(200); 
        splitPane.setPreferredSize(new Dimension(824, 600));
        splitPane2.setDividerLocation(820); 
        splitPane2.setPreferredSize(new Dimension(1024, 600));

        //Add the split pane to this panel.
        add(splitPane2);
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
    	tabbedPanel.setSelectedIndex(0);
    	tableFilter.changeConcept(node.getURI());
    	JenaController.tableModel.changeConcept(node.getURI());
    	
    	instanceList.clear();
    	instanceList.addAll(JenaController.model.getInstances(node.getURI()));
    	
//    	StringBuilder sb = new StringBuilder();
//    	
//    	for(Iterator<JenaTreeNode> i = instanceList.iterator(); i.hasNext(); ) {
//    		sb.append(i.next().toString()).append("\n");
//    	}
    	
//    	htmlPane.setText(sb.toString());
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
    
	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JenaViewer.createAndShowGUI();
            }
        });
	}
    
}
