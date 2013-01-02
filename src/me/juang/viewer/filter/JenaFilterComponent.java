/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package me.juang.viewer.filter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import me.juang.controller.JenaController;

import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.vocabulary.OWL;

/* ListDemo.java requires no other files. */
@SuppressWarnings("serial")
public class JenaFilterComponent extends JPanel
                      implements ListSelectionListener {
	
	private Vector<OntProperty> propertyList;
	
    private JList list;
    private DefaultListModel listModel;

    private static final String hireString = "+";
    private static final String fireString = "-";
    private JButton fireButton;
    private JComboBox propertyName;
    
    public Map<String, TableColumn> removedColumn = new HashMap<String, TableColumn>();

    public JenaFilterComponent(String ClassURI) {
        super(new BorderLayout());
        
        initProps(ClassURI);
        changeConcept(ClassURI);
    }
    
    public void changeConcept(String ClassURI) {
    	// ignore OWL:Thing
    	if(ClassURI.equalsIgnoreCase(OWL.Thing.getURI())) return;
    	
    	// clean up table
    	for(Iterator<TableColumn> i = removedColumn.values().iterator(); i.hasNext(); ) {
    		TableColumn t = i.next();
    		JenaController.table.addColumn(t);
    	}
    	
    	// clean up properties
    	list.removeAll();
    	propertyName.removeAllItems();
    	listModel.removeAllElements();
    	
    	// fill it again
    	propertyList = JenaController.model.getAllClassProps(ClassURI);

    	// combo box 
        for (Iterator<OntProperty> i = propertyList.iterator(); i.hasNext();) {
			propertyName.addItem(i.next().getLocalName());
		}

        // list element
        for(Iterator<OntProperty> i = propertyList.iterator(); i.hasNext(); ) {
        	listModel.addElement(i.next().getLocalName());
        }
    	
    }
    
    public void initProps(String ClassURI) {
    	setPreferredSize(new Dimension(200, 100));
        listModel = new DefaultListModel();
        list = new JList(listModel);


        //Create the list and put it in a scroll pane.
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);

        JButton hireButton = new JButton(hireString);
        HireListener hireListener = new HireListener(hireButton);
        hireButton.setActionCommand(hireString);
        hireButton.addActionListener(hireListener);
        hireButton.setEnabled(true);
        hireButton.setPreferredSize(new Dimension(30, 25));

        fireButton = new JButton(fireString);
        fireButton.setActionCommand(fireString);
        fireButton.addActionListener(new FireListener());
        fireButton.setPreferredSize(new Dimension(30, 25));
        
        propertyName = new JComboBox();
        propertyName.setPreferredSize(new Dimension(80, 25));

        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(fireButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
//        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(propertyName);
        buttonPane.add(hireButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
//        buttonPane.setPreferredSize(new Dimension(100, ))

        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    	
    }

    class FireListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            int index = list.getSelectedIndex();
            updateTable(list.getSelectedValue().toString());
            
            listModel.remove(index);

            int size = listModel.getSize();

            if (size == 0) { //Nobody's left, disable firing.
                fireButton.setEnabled(false);

            } else { //Select an index.
                if (index == listModel.getSize()) {
                    //removed item in last position
                    index--;
                }

                list.setSelectedIndex(index);
                list.ensureIndexIsVisible(index);
            }
        }
        
        private void updateTable(String oldValue) {

        	int indeks = JenaController.tableModel.findColumn(oldValue);
        	TableColumn temp = JenaController.table.getColumnModel().getColumn(indeks);
        	removedColumn.put(oldValue, temp);
        	JenaController.table.getColumnModel().removeColumn(temp);
        	
        }
    }

    //This listener is shared by the text field and the hire button.
    class HireListener implements ActionListener, DocumentListener {
        private boolean alreadyEnabled = false;
        private JButton button;

        public HireListener(JButton button) {
            this.button = button;
        }

        //Required by ActionListener.
        public void actionPerformed(ActionEvent e) {
        	String name = propertyName.getSelectedItem().toString();

            //User didn't type in a unique name...
            if (name.equals("") || alreadyInList(name)) {
                Toolkit.getDefaultToolkit().beep();
                propertyName.requestFocusInWindow();
                return;
            }

            int index = list.getSelectedIndex(); //get selected index
            if (index == -1) { //no selection, so insert at beginning
                index = 0;
            } else {           //add after the selected item
                index++;
            }

            listModel.insertElementAt(propertyName.getSelectedItem().toString(), index);
            //If we just wanted to add to the end, we'd do this:
            //listModel.addElement(employeeName.getText());
            
            // TODO: Create changes in the ontology

            //Reset the text field.
            propertyName.requestFocusInWindow();

            //Select the new item and make it visible.
            list.setSelectedIndex(index);
            list.ensureIndexIsVisible(index);
            
            //update ontology
            updateTable(name);
        }
        
        private void updateTable(String oldValue) {

        	TableColumn tc = removedColumn.remove(oldValue);
        	JenaController.table.getColumnModel().addColumn(tc);
        	
        }

        //This method tests for string equality. You could certainly
        //get more sophisticated about the algorithm.  For example,
        //you might want to ignore white space and capitalization.
        protected boolean alreadyInList(String name) {
            return listModel.contains(name);
        }

        //Required by DocumentListener.
        public void insertUpdate(DocumentEvent e) {
            enableButton();
        }

        //Required by DocumentListener.
        public void removeUpdate(DocumentEvent e) {
            handleEmptyTextField(e);
        }

        //Required by DocumentListener.
        public void changedUpdate(DocumentEvent e) {
            if (!handleEmptyTextField(e)) {
                enableButton();
            }
        }

        private void enableButton() {
            if (!alreadyEnabled) {
                button.setEnabled(true);
            }
        }

        private boolean handleEmptyTextField(DocumentEvent e) {
            if (e.getDocument().getLength() <= 0) {
                button.setEnabled(false);
                alreadyEnabled = false;
                return true;
            }
            return false;
        }
    }

    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (list.getSelectedIndex() == -1) {
            //No selection, disable fire button.
                fireButton.setEnabled(false);

            } else {
            //Selection, enable the fire button.
                fireButton.setEnabled(true);
            }
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("ListDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JenaFilterComponent newContentPane = new JenaFilterComponent(OWL.Thing.toString());
        newContentPane.changeConcept(JenaController.NS+"Engineer");
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
