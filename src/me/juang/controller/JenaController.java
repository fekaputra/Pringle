package me.juang.controller;

import me.juang.model.JenaModel;
import me.juang.viewer.JenaViewer;

public class JenaController {
	public static final JenaModel model = new JenaModel();
	
	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JenaViewer.createAndShowGUI();
            }
        });
	}
}
