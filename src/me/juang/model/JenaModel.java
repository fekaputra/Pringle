package me.juang.model;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;

public class JenaModel {
	OntModel ontoModel = ModelFactory.createOntologyModel();
	DefaultMutableTreeNode ontoRoot;
	TreeModel ontoTreeModel;
	
	public JenaModel() {
		// Default file: PersonProject.owl
		// JenaModel.loadImports(); // consider this to owl import
		ontoModel.read("file:PersonProject.owl", "RDF/XML");
		setTreeModel();
	}
	
	public TreeModel getTreeModel() {
		return ontoTreeModel;
	}
	
	/* set class tree in the ontoTreeModel */
	private void setTreeModel() {
		ontoRoot = new DefaultMutableTreeNode(new JenaTreeNode(OWL.Thing));
		ontoTreeModel = new DefaultTreeModel(ontoRoot);
		
		Iterator<OntClass> ontClassIter = ontoModel.listHierarchyRootClasses();
		while (ontClassIter.hasNext()) {
			setRecursiveModel(ontoRoot, ontClassIter.next());
		}
	}
	
	/* recursively read tree model */
	private void setRecursiveModel(DefaultMutableTreeNode node, OntClass oClass) {
		if (oClass==null) return;
		
		DefaultMutableTreeNode temp = new DefaultMutableTreeNode(new JenaTreeNode(oClass));
		node.add(temp);
		
		Iterator<OntClass> ontClassIter = oClass.listSubClasses();
		while (ontClassIter.hasNext()) {
			setRecursiveModel(temp, ontClassIter.next());
		}
	}
	
	public Vector<JenaTreeNode> getInstances(String URI) {
		Vector<JenaTreeNode> v = new Vector<JenaTreeNode>();
		if(URI.equalsIgnoreCase(OWL.Thing.getURI())) {
			for(Iterator<OntClass> i = ontoModel.listHierarchyRootClasses(); i.hasNext(); ) {
				getClassInstances(i.next(), v);
			}
		} else {
			getClassInstances(ontoModel.getOntClass(URI), v);
		}
		return v;
	}
	
	private void getClassInstances(OntClass o, Vector<JenaTreeNode> v) {
		for(ExtendedIterator<? extends OntResource> i = o.listInstances(); i.hasNext(); ) {
			OntResource temp = (OntResource)i.next();
			v.add(new JenaTreeNode(temp));
		}
	}
	
//	public static void main(String[] args) {
//		String NS = "http://www.owl-ontologies.com/Ontology1356854760.owl#";
		
//		OntModel m = ModelFactory.createOntologyModel();
//		m.read("file:PersonProject.owl", "RDF/XML");
//		Individual i = m.getIndividual(NS+"eng_one");
//		for (Iterator it = i.listOntClasses(true); it.hasNext(); ) {
//		    Resource cls = (Resource) it.next();
//		    System.out.println( "deputy_dawg has rdf:type " + cls );
//		}
		
//		m.read("file:PersonProject.owl", "RDF/XML");
//		OntClass o = m.getOntClass(OWL.Thing.toString());
//		System.out.println(o.toString());
//		Iterator<OntClass> ontClassIter = o.listSubClasses();
//		while (ontClassIter.hasNext()) System.out.println(ontClassIter.next().getLocalName());
//	}
}
