package me.juang.viewer;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import me.juang.controller.JenaController;

import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;

@SuppressWarnings("serial")
public class JenaTableModel extends AbstractTableModel {
	
	private String conceptURI;
	private Vector<OntProperty> columnsData; // property
	private Vector<OntResource> rowsData; // individual
	private RDFVisitor rv = new RDFVisitor() {
		
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
	
	public JenaTableModel() {
		conceptURI = "";
		columnsData = new Vector<OntProperty>();
		rowsData = new Vector<OntResource>();
	}
	
	public void changeConcept(String URI) {
		if(URI.equalsIgnoreCase(OWL.Thing.toString()) || URI.equalsIgnoreCase(conceptURI)) return;
		
		conceptURI = URI;
		columnsData = JenaController.model.getAllClassProps(conceptURI);
		rowsData = JenaController.model.getAllClassIndividual(conceptURI);
		
		fireTableStructureChanged();
	}

	@Override
	public int getColumnCount() {
		return columnsData.size()+1;
	}

	@Override
    public String getColumnName(int col) {
		if(col==0) return "Individu";
        return columnsData.get(col-1).getLocalName();
    }

	@Override
	public int getRowCount() {
		return rowsData.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		
		OntResource individu = rowsData.get(row);
		if(col==0) return individu.getLocalName();
		
		OntProperty property = columnsData.get(col-1);
		
		Vector<RDFNode> tempVec = JenaController.model.getIndividualProps(individu.getURI(), property);
		if(tempVec.size()==0) {
			return "-Empty-";
		} else if (tempVec.size()==1) {
			return tempVec.firstElement().visitWith(rv); 
		} else {
			return "-Multiple-";
//			JComboBox combo = new JComboBox();
//			combo.addItem("-Multiple-");
//			for(Iterator<RDFNode> i = tempVec.iterator(); i.hasNext(); ) {
//				combo.addItem(i.next().visitWith(rv));
//			}
//			return combo;
		}
	}

}
