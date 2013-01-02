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

	int rowSize;
	int columnSize;
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
	Object[][] matriks;
	
	public JenaTableModel() {
		conceptURI = "";
		columnsData = new Vector<OntProperty>();
		rowsData = new Vector<OntResource>();
	}
	
	public String getIndividualURI(int index) {
		return rowsData.get(index).getURI();
	}
	
	public void changeConcept(String URI) {
		if(URI.equalsIgnoreCase(OWL.Thing.toString()) || URI.equalsIgnoreCase(conceptURI)) return;
		
		conceptURI = URI;
		initMatriks();
		
		fireTableStructureChanged();
	}
	
	private void initMatriks() {
		
		columnsData = JenaController.model.getAllClassProps(conceptURI);
		rowsData = JenaController.model.getAllClassIndividual(conceptURI);
		rowSize = rowsData.size();
		columnSize = columnsData.size()+1;
		matriks = new Object[rowSize][columnSize];
		
		// regular properties
		for(int col=1;col<columnSize;col++) {
			for(int row=0;row<rowSize;row++) {
				OntResource individu = rowsData.get(row);
				OntProperty property = columnsData.get(col-1); // first column belong to name
				
				Vector<RDFNode> tempVec = JenaController.model.getIndividualProps(individu.getURI(), property);
				if(tempVec.size()==0) {
					matriks[row][col] = "-Empty-";
					
				} else if (tempVec.size()==1) {
					matriks[row][col] = tempVec.firstElement().visitWith(rv);
					
				} else {
					matriks[row][col] = "-Multiple-";
					
//					JComboBox combo = new JComboBox();
//					combo.addItem("-Multiple-");
//					for(Iterator<RDFNode> i = tempVec.iterator(); i.hasNext(); ) {
//						combo.addItem(i.next().visitWith(rv));
//					}
//					combo.addComponentListener(new ComponentAdapter() {
//
//			            @Override
//			            public void componentShown(ComponentEvent e) {
//			                final JComponent c = (JComponent) e.getSource();
//			                SwingUtilities.invokeLater(new Runnable() {
//
//			                    @Override
//			                    public void run() {
//			                        c.requestFocus();
//			                        System.out.println(c);
//			                        if (c instanceof JComboBox) {
//			                            System.out.println("a");
//			                        }
//			                    }
//			                });
//			            }
//			        });
//					matriks[row][col] = combo;
//					fireTableCellUpdated(row, col);
				}
			}
		}
		
		// name properties
		for(int row=0;row<rowSize;row++) {
			matriks[row][0] = rowsData.get(row).getLocalName();
		}
	}

	@Override
	public int getColumnCount() {
		return columnSize;
	}

	@Override
    public String getColumnName(int col) {
		if(col==0) return "Individu";
        return columnsData.get(col-1).getLocalName();
    }

	@Override
	public int getRowCount() {
		return rowSize;
	}

	@Override
	public Object getValueAt(int row, int col) {
		return matriks[row][col];
	}

}
