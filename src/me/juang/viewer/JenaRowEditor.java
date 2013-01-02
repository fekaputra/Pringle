package me.juang.viewer;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class JenaRowEditor implements TableCellEditor {
	protected Hashtable<Integer, TableCellEditor> editors;
	protected TableCellEditor editor, defaultEditor;
	JTable table;
	
	public JenaRowEditor(JTable table) {
		this.table = table;
		editors = new Hashtable<Integer, TableCellEditor>();
		defaultEditor = new DefaultCellEditor(new JTextField());
	}
	
	/**
	 * @param row
	 *            table row
	 * @param editor
	 *            table cell editor
	 */
	public void setEditorAt(int row, TableCellEditor editor) {
	    editors.put(new Integer(row), editor);
	}
	
	protected void selectEditor(MouseEvent e) {
	    int row;
	    if (e == null) {
	    	row = table.getSelectionModel().getAnchorSelectionIndex();
	    } else {
	    	row = table.rowAtPoint(e.getPoint());
	    }
	    editor = (TableCellEditor) editors.get(new Integer(row));
	    if (editor == null) {
	    	editor = defaultEditor;
	    }
	}

	@Override
	public void addCellEditorListener(CellEditorListener arg0) {
		editor.addCellEditorListener(arg0);
		
	}

	@Override
	public void cancelCellEditing() {
		editor.cancelCellEditing();
		
	}

	@Override
	public Object getCellEditorValue() {
		editor.getCellEditorValue();
		return null;
	}

	@Override
	public boolean isCellEditable(EventObject arg0) {
		selectEditor((MouseEvent)arg0);
		return editor.isCellEditable(arg0);
	}

	@Override
	public void removeCellEditorListener(CellEditorListener arg0) {
		editor.removeCellEditorListener(arg0);
		
	}

	@Override
	public boolean shouldSelectCell(EventObject arg0) {
		selectEditor((MouseEvent) arg0);
		return editor.shouldSelectCell(arg0);
	}

	@Override
	public boolean stopCellEditing() {
		return editor.stopCellEditing();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int col) {
		return editor.getTableCellEditorComponent(table, value, isSelected,
		        row, col);
	}

}
