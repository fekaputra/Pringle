package me.juang.controller;

import javax.swing.JTable;

import me.juang.model.JenaModel;
import me.juang.viewer.JenaTableModel;
import me.juang.viewer.JenaViewer;

public class JenaController {
	public static final JenaModel model = new JenaModel();
    public static final JenaTableModel tableModel = new JenaTableModel();
    public static final JTable table = new JTable(tableModel);
	public static final String NS = "http://www.owl-ontologies.com/Ontology1356854760.owl#";
	
	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JenaViewer.createAndShowGUI();
            }
        });
	}
}
