package me.juang.model;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;
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
	
	/**
	 * Get all instance of concept with certain URI in JenaTreeNode format
	 * @param URI
	 * @return
	 */
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
	
	/**
	 * Jangan lupa ganti jadi private
	 * @param URI
	 */
//	public Vector<RDFNode> getIndividualProps(String URI, OntProperty o) {
//		Individual temp = ontoModel.getIndividual(URI);
//		Vector<RDFNode> tempVec = new Vector<RDFNode>();
//		
//		for(Iterator<RDFNode> i = temp.listPropertyValues(o); i.hasNext(); ) {
//			tempVec.add(i.next());
//		}
//		
//		return tempVec;
//	}
	
	public Vector<RDFNode> getIndividualProps(String URI, OntProperty o) {
		Individual temp = ontoModel.getIndividual(URI);
		Vector<RDFNode> tempVec = new Vector<RDFNode>();
		
		for(Iterator<RDFNode> i = temp.listPropertyValues(o); i.hasNext(); ) {
			tempVec.add(i.next());
		}
		
		return tempVec;
	}
	
	public Vector<OntResource> getAllClassIndividual(String URI) {
		OntClass temp = ontoModel.getOntClass(URI);
		Vector<OntResource> tempVec = new Vector<OntResource>();
		
		for(Iterator<? extends OntResource> i = temp.listInstances(); i.hasNext(); ) {
			tempVec.add(i.next());
		}
		
		return tempVec;
	}

	/**
	 * Jangan lupa ganti jadi private
	 * @param URI
	 */
	public Vector<OntProperty> getAllClassProps(String URI) {
		OntClass temp = ontoModel.getOntClass(URI);
		Vector<OntProperty> tempVec = new Vector<OntProperty>();
		
		for(ExtendedIterator<OntProperty> i = temp.listDeclaredProperties(); i.hasNext(); ) {
			tempVec.add(i.next());
		}
		
		return tempVec;
	}
	
	public static void main(String[] args) {
		String NS = "http://www.owl-ontologies.com/Ontology1356854760.owl#";
		JenaModel jm = new JenaModel();
		Vector<RDFNode> r = jm.getIndividualProps(NS+"eng_one", jm.ontoModel.getOntProperty(NS+"isParticipantOf"));
		RDFVisitor rv = new RDFVisitor() {
			
			@Override
			public Object visitURI(Resource r, String uri) {
				// TODO Auto-generated method stub
				return r.getLocalName();
			}
			
			@Override
			public Object visitLiteral(Literal l) {
				// TODO Auto-generated method stub
				return l.getValue();
			}
			
			@Override
			public Object visitBlank(Resource r, AnonId id) {
				// TODO Auto-generated method stub
				return r.getLocalName();
			}
		};
		
		System.out.println(r.get(0).visitWith(rv));
//		if(r.get(0).isResource()) {
//			System.out.println(r.get(0).asResource().getLocalName());
//		}
//		jm.showAllIndividualProps(NS+"eng_one");
		System.out.println("-----");
		Vector<OntProperty> v = jm.getAllClassProps(NS+"Engineer");
		System.out.println(v.toString());
	}
}
