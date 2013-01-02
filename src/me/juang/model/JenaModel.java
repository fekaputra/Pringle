package me.juang.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;

public class JenaModel {
	OntModel ontoModel;
	DefaultMutableTreeNode ontoRoot;
	TreeModel ontoTreeModel;
	Reasoner reasoner;
	Model instances;
	
	public JenaModel() {
		// Default file: PersonProject.owl
		// JenaModel.loadImports(); // consider this to owl import
		
//		ontoModel = ModelFactory.createOntologyModel();
		ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
		ontoModel.read("file:PersonProject.owl", "RDF/XML");
		setTreeModel();
	}
	
	public TreeModel getTreeModel() {
		return ontoTreeModel;
	}
	
	public OntModel getOntModel() {
		return ontoModel;
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
	
	public Vector<RDFNode> getIndividualProps(String URI, OntProperty o) {
		Individual temp = ontoModel.getIndividual(URI);
		Vector<RDFNode> tempVec = new Vector<RDFNode>();
		
		for(Iterator<RDFNode> i = temp.listPropertyValues(o); i.hasNext(); ) {
			tempVec.add(i.next());
		}
		
		return tempVec;
	}
	
	public HashMap<String, Individual> getIndividualMapProps(String URI, OntProperty o) {
		Individual temp = ontoModel.getIndividual(URI);
		HashMap<String, Individual> IndiProps = new HashMap<String, Individual>();
		
		for(Iterator<RDFNode> i = temp.listPropertyValues(o); i.hasNext(); ) {
			Individual propIndiv = i.next().as(Individual.class);
			IndiProps.put(propIndiv.getLocalName(), propIndiv);
		}
		
		return IndiProps;
	}
	
	public Vector<Statement> getIndividualProps(Individual pIndi) {
		Vector<Statement> tempVec = new Vector<Statement>();
		
		for(Iterator<Statement> i = pIndi.listProperties(); i.hasNext(); ) {
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

	public Vector<OntProperty> getAllClassProps(String URI) {
		OntClass temp = ontoModel.getOntClass(URI);
		return getAllClassProps(temp);
	}
	
	public Vector<OntProperty> getAllClassProps(OntClass pOntClass) {

		Vector<OntProperty> tempVec = new Vector<OntProperty>();
		
		for(ExtendedIterator<OntProperty> i = pOntClass.listDeclaredProperties(); i.hasNext(); ) {
			tempVec.add(i.next());
		}
		
		return tempVec;
	}
	
	public Individual getIndiFromURI(String URI) {
		return ontoModel.getIndividual(URI);
	}
	
	public OntClass getClassFromIndi(Individual pIndi) { 
		// TODO: should be changed into option
		return ((Resource) pIndi.listRDFTypes(true).next()).as(OntClass.class);
	}
	
	public Vector<Individual> getIndividualListFromPropertyRanges(String URI) {
		OntProperty ontProp = ontoModel.getOntProperty(URI);
		Vector<Individual> listIndividu = new Vector<Individual>();
		
		for(Iterator<? extends OntResource> i = ontProp.listRange(); i.hasNext(); ) {
			OntClass o = (OntClass)i.next();
			if(o.equals(OWL.Thing)) continue; // skip thing
			if(o.getURI().equalsIgnoreCase("http://www.w3.org/2000/01/rdf-schema#Resource")) continue; // skip rdf
			System.out.println(o.toString());
			for(Iterator<? extends OntResource> j = o.listInstances(); j.hasNext(); ) {
				Individual indi = (Individual)j.next();
				listIndividu.add(indi);
			}
		}
		
		return listIndividu;
	}
	
	public HashMap<String, RDFNode> getIndividualMapFromPropertyRanges(String URI) {
		OntProperty ontProp = ontoModel.getOntProperty(URI);
		HashMap<String, RDFNode> listIndividu = new HashMap<String, RDFNode>();
		
		for(Iterator<? extends OntResource> i = ontProp.listRange(); i.hasNext(); ) {
			OntClass o = (OntClass)i.next();
			if(o.equals(OWL.Thing)) continue; // skip thing
			if(o.getURI().equalsIgnoreCase("http://www.w3.org/2000/01/rdf-schema#Resource")) continue; // skip rdf
			
			System.out.println(o.toString());
			for(Iterator<? extends OntResource> j = o.listInstances(); j.hasNext(); ) {
				RDFNode indi = j.next();
				listIndividu.put(((Individual)indi).getLocalName(), indi);
			}
		}
		
		return listIndividu;
	}
	
	public void save() {
		try {
			ontoModel.write(new FileWriter("PersonProject.owl"), "RDF/XML");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String NS = "http://www.owl-ontologies.com/Ontology1356854760.owl#";
		JenaModel jm = new JenaModel();
		
//		Vector<Individual> v = jm.getIndividualListFromPropertyRanges(NS+"isResponsibleOf");
//		System.out.println(v.toString());
		 
//		for(Iterator<DatatypeProperty> i = jm.ontoModel.listDatatypeProperties(); i.hasNext(); ) {
//			DatatypeProperty prop = i.next();
//			System.out.println(prop.toString());
//			for(Iterator<? extends OntResource> j = prop.listRange(); j.hasNext(); ) {
//				System.out.println("--"+j.next().toString());
//			}
//		}		
//		System.out.println("====");
		
		OntClass o = jm.ontoModel.getOntClass(NS+"Client");
		for(Iterator<OntProperty> i = o.listDeclaredProperties(); i.hasNext(); ) {
			OntProperty prop = i.next();
			if(prop.isObjectProperty())
			System.out.println(prop.toString());
		}
		
//		for(Iterator<ObjectProperty> i = jm.ontoModel.listObjectProperties(); i.hasNext(); ) {
//			ObjectProperty prop = i.next();
//			System.out.println(prop.toString());
//			
//			
//			for(Iterator<? extends OntResource> j = prop.listRange(); j.hasNext(); ) {
//				System.out.println("--"+j.next().toString());
//			}
//		}
		
//		for( Iterator<OntProperty> i = v.iterator(); i.hasNext(); ) {
//			OntProperty tempP = i.next();
////			System.out.println(tempP.getLocalName());
////			System.out.println(tempP.listRange().toString());
//		}
//		Individual ind = jm.ontoModel.getIndividual(NS+"project_one");
//		OntProperty prop = jm.ontoModel.getOntProperty(NS+"startDate");
//		
//		Literal l = jm.ontoModel.createTypedLiteral("2011-12-05", XSD.date.toString());
//        
//		ind.addProperty(prop, l);
//		Vector<RDFNode> nodes = jm.getIndividualProps(ind.getURI(), prop);
//		
//		try {
//			jm.ontoModel.write(new FileWriter("PersonProject.owl"), "RDF/XML");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		for(Iterator<RDFNode> t = nodes.iterator(); t.hasNext(); ) {
//			System.out.println(t.next());
//		}
//		System.out.println("-----");
		

//		Vector<RDFNode> r = jm.getIndividualProps(NS+"eng_one", jm.ontoModel.getOntProperty(NS+"isParticipantOf"));		
//		RDFVisitor rv = new RDFVisitor() {
//			
//			@Override
//			public Object visitURI(Resource r, String uri) {
//				// TODO Auto-generated method stub
//				return r.getLocalName();
//			}
//			
//			@Override
//			public Object visitLiteral(Literal l) {
//				// TODO Auto-generated method stub
//				return l.getValue();
//			}
//			
//			@Override
//			public Object visitBlank(Resource r, AnonId id) {
//				// TODO Auto-generated method stub
//				return r.getLocalName();
//			}
//		};
//		
//		System.out.println(r.get(0).visitWith(rv));
	}
}
