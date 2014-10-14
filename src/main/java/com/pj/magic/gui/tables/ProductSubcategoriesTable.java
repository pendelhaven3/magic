package com.pj.magic.gui.tables;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.ProductSubcategory;
import com.pj.magic.service.ProductCategoryService;

@Component
public class ProductSubcategoriesTable extends JTable {

	public static final int BUTTON_COLUMN_INDEX = 1;
	
	@Autowired private ProductCategoryService productCategoryService;
	
	private ProductSubcategoriesTableModel tableModel;
	private DeleteSubcategoryButton tableButton = new DeleteSubcategoryButton();
	
	public ProductSubcategoriesTable() {
		setModel(new ProductSubcategoriesTableModel());
		tableModel = (ProductSubcategoriesTableModel)getModel();
		setTableHeader(null);
	}
	
	@PostConstruct
	public void initialize() {
		initializeColumns();
		registerKeyBindings();
	}

	private void initializeColumns() {
		getColumnModel().getColumn(0).setPreferredWidth(270);

		TableColumn buttonColumn = getColumnModel().getColumn(1);
		buttonColumn.setCellEditor(tableButton);
		buttonColumn.setCellRenderer(tableButton);
		buttonColumn.setPreferredWidth(80);
	}

	private void registerKeyBindings() {
		// none
	}

	public void updateDisplay(ProductCategory productCategory) {
		tableModel.setSubcategories(productCategory.getSubcategories());
	}
	
	public void clearDisplay() {
		tableModel.setSubcategories(new ArrayList<ProductSubcategory>());
	}
	
	public ProductSubcategory getSubcategory(int rowIndex) {
		return tableModel.getSubcategory(rowIndex);
	}

	private class ProductSubcategoriesTableModel extends AbstractTableModel {

		private List<ProductSubcategory> subcategories = new ArrayList<>();
		
		public void setSubcategories(List<ProductSubcategory> subcategories) {
			this.subcategories = subcategories;
			fireTableDataChanged();
		}
		
		@Override
		public Object getValueAt(int row, int column) {
			return subcategories.get(row).getName();
		}
		
		@Override
		public int getRowCount() {
			return subcategories.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		public ProductSubcategory getSubcategory(int rowIndex) {
			return subcategories.get(rowIndex);
		}
		
	}
	
	private class DeleteSubcategoryButton extends JButton implements TableCellEditor, TableCellRenderer {

		public DeleteSubcategoryButton() {
			super("Delete");
		}
		
		@Override
		public Object getCellEditorValue() {
			return "";
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			return false;
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			return true;
		}

		@Override
		public boolean stopCellEditing() {
			return true;
		}

		@Override
		public void cancelCellEditing() {
		}

		@Override
		public void addCellEditorListener(CellEditorListener l) {
		}

		@Override
		public void removeCellEditorListener(CellEditorListener l) {
		}

		@Override
		public java.awt.Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			return this;
		}

		@Override
		public java.awt.Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			return this;
		}
		
	}

	public ProductSubcategory getCurrentlySelectedSubcategory() {
		return tableModel.getSubcategory(getSelectedRow());
	}
	
}
