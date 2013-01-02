package me.juang.viewer.InstanceEditor;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import me.juang.controller.JenaController;
import me.juang.viewer.JenaViewer;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.RDFNode;

@SuppressWarnings("serial")
public class PanelComboBox extends JPanel {
	Individual mIndividual;
	OntProperty mOntProperty;
	Vector<RDFNode> mProperties;
	Vector<Individual> mComboBoxFeed;
	
	public PanelComboBox() {
		super();
		
		mIndividual = null;
		mOntProperty = null;
		mProperties = null;
		mComboBoxFeed = null;
	}
	
	public PanelComboBox(Individual pIndi, OntProperty pOntProp) {
		super();
		
		setPanel(pIndi, pOntProp);
	}
	
	public void setPanel(Individual pIndi, OntProperty pOntProp) {
		setBorder(BorderFactory.createTitledBorder(pOntProp.getLocalName()));
		
		mIndividual = pIndi;
		mOntProperty = pOntProp;
		mProperties = JenaController.model.getIndividualProps(pIndi.getURI(), pOntProp);
		mComboBoxFeed = JenaController.model.getIndividualListFromPropertyRanges(mOntProperty.getURI());
	}
	
    public static void createAndShowGUI() {
        if (true) {
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
        
        String NS = "http://www.owl-ontologies.com/Ontology1356854760.owl#";
        Individual instance = JenaController.model.getIndiFromURI(NS+"eng_one");
        OntProperty prop = JenaController.model.getOntModel().getOntProperty(NS+"isParticipantOf");
		PanelComboBox p = new PanelComboBox(instance, prop);
		p.setPanel(instance, prop);

        //Add content to the window.
        frame.add(new JenaViewer());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
