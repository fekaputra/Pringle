package me.juang.model;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Resource;

public class JenaTreeNode {
	private String name;
	private String URI;
	
	public JenaTreeNode(OntClass o) {
		name = o.getLocalName();
		URI = o.getURI();
	}
	
	public JenaTreeNode(Resource o) {
		name = o.getLocalName();
		URI = o.getURI();
	}
	
	public String getURI() {
		return URI;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
