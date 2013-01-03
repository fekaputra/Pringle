package me.juang.viewer.InstanceEditor;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import me.juang.controller.JenaController;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;

@SuppressWarnings("serial")
public class JenaInstanceEditor extends JPanel {
    private List<JPanel> objectList;
//    private List<JPanel> datatypeList;
    
    private JPanel panelObject;
    private JPanel panelDatatype;
    
	private OntClass concept;
	private Individual instance;
	private Vector<OntProperty> properties;

	public JenaInstanceEditor() {
		super();
		initGUI();
	}
	
	public JenaInstanceEditor(String InstanceURI) {
		super();
		initGUI();
		
		initInstanceEditor(InstanceURI);
		initGUIObjects();
	}
	
	public void initInstanceEditor(String InstanceURI) {
		objectList = new ArrayList<JPanel>();
		
		instance = JenaController.model.getIndiFromURI(InstanceURI);
		concept = JenaController.model.getClassFromIndi(instance);
		properties = JenaController.model.getAllClassProps(concept);
		
		setBorder(BorderFactory.createTitledBorder("Instance: "+instance.getLocalName()));
		
		initGUIObjects();
	}
	
	public void initGUI() {
		panelObject = new JPanel();
		panelObject.setPreferredSize(new Dimension(275,500));
		panelObject.setMaximumSize(new Dimension(275,500));
		panelObject.setMinimumSize(new Dimension(275,500));
		panelObject.setBorder(BorderFactory.createTitledBorder("Object Properties"));
		
		panelDatatype = new JPanel();
		panelDatatype.setPreferredSize(new Dimension(275,525));
		panelDatatype.setBorder(BorderFactory.createTitledBorder("Datatype Properties"));
		
		JSplitPane jsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelObject, panelDatatype);
		add(jsplit);
	}
	
	private void initGUIObjects() {
		panelObject.removeAll();
		
		if(properties.isEmpty()) return;
		
		for(Iterator<OntProperty> i = properties.iterator(); i.hasNext(); ) {
			OntProperty tempProp = i.next();
			if(tempProp.isObjectProperty()) {
				if(!tempProp.isFunctionalProperty() && !tempProp.isInverseFunctionalProperty()) {
					JenaMultiplePropertyEditor cBox = new JenaMultiplePropertyEditor(instance, tempProp);
					cBox.setBorder(BorderFactory.createTitledBorder(tempProp.getLocalName()));
					objectList.add(cBox);
					panelObject.add(cBox);
				} else {
					// TODO
				}
			} else if(tempProp.isDatatypeProperty()) {
				// TODO
			}
		}
	}
}
