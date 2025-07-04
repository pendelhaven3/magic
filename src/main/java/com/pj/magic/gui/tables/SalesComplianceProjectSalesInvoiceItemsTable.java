package com.pj.magic.gui.tables;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.MagicCellEditor;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.tables.models.SalesComplianceProjectSalesInvoiceItemsTableModel;
import com.pj.magic.gui.tables.rowitems.SalesComplianceProjectSalesInvoiceItemRowItem;
import com.pj.magic.model.SalesComplianceProjectSalesInvoice;

@Component
public class SalesComplianceProjectSalesInvoiceItemsTable extends MagicTable {
	
	public static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	public static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	public static final int UNIT_COLUMN_INDEX = 2;
	public static final int ORIGINAL_QUANTITY_COLUMN_INDEX = 3;
	public static final int QUANTITY_COLUMN_INDEX = 4;
	public static final int COST_COLUMN_INDEX = 5;
	public static final int AMOUNT_COLUMN_INDEX = 6;
	public static final int DISCOUNT_1_COLUMN_INDEX = 7;
	public static final int DISCOUNT_2_COLUMN_INDEX = 8;
	public static final int DISCOUNT_3_COLUMN_INDEX = 9;
	public static final int FLAT_RATE_COLUMN_INDEX = 10;
	public static final int DISCOUNTED_AMOUNT_COLUMN_INDEX = 11;
	public static final int NET_AMOUNT_COLUMN_INDEX = 12;
	
	@Autowired private SalesComplianceProjectSalesInvoiceItemsTableModel tableModel;
	
	private SalesComplianceProjectSalesInvoice salesInvoice;
	
	@Autowired
	public SalesComplianceProjectSalesInvoiceItemsTable(SalesComplianceProjectSalesInvoiceItemsTableModel tableModel) {
		super(tableModel);
		setSurrendersFocusOnKeystroke(true);
		initializeColumns();
		initializeRowItemValidationBehavior();
		initializeModelListener();
		registerKeyBindings();
	}
	
	private void initializeColumns() {
		TableColumnModel columnModel = getColumnModel();
		columnModel.getColumn(PRODUCT_CODE_COLUMN_INDEX).setPreferredWidth(80);
		columnModel.getColumn(PRODUCT_DESCRIPTION_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(60);
		columnModel.getColumn(ORIGINAL_QUANTITY_COLUMN_INDEX).setPreferredWidth(60);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(60);
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		getColumnModel().getColumn(COST_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(AMOUNT_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(DISCOUNT_1_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(DISCOUNT_2_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(DISCOUNT_3_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(FLAT_RATE_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(DISCOUNTED_AMOUNT_COLUMN_INDEX).setCellRenderer(rightRenderer);
		getColumnModel().getColumn(NET_AMOUNT_COLUMN_INDEX).setCellRenderer(rightRenderer);
		
		MagicTextField quantityTextField = new MagicTextField();
		quantityTextField.setMaximumLength(Constants.QUANTITY_MAXIMUM_LENGTH);
		quantityTextField.setNumbersOnly(true);
		columnModel.getColumn(QUANTITY_COLUMN_INDEX).setCellEditor(new QuantityCellEditor(quantityTextField));
	}
	
	public boolean isQuantityFieldSelected() {
		return getSelectedColumn() == QUANTITY_COLUMN_INDEX;
	}
	
	public boolean isProductCodeFieldSelected() {
		return getSelectedColumn() == PRODUCT_CODE_COLUMN_INDEX;
	}

	public boolean isUnitFieldSelected() {
		return getSelectedColumn() == UNIT_COLUMN_INDEX;
	}
	
	public boolean isLastRowSelected() {
		return getSelectedRow() + 1 == tableModel.getRowCount();
	}

	public SalesComplianceProjectSalesInvoiceItemRowItem getCurrentlySelectedRowItem() {
		return tableModel.getRowItem(getSelectedRow());
	}
	
	public void setSalesInvoice(SalesComplianceProjectSalesInvoice salesInvoice) {
		clearSelection();
		this.salesInvoice = salesInvoice;
		tableModel.setSalesInvoice(salesInvoice);
	}
	
	protected void registerKeyBindings() { }
	
	public int getTotalNumberOfItems() {
		return salesInvoice.getItems().size();
	}

	public void highlight() {
		changeSelection(0, QUANTITY_COLUMN_INDEX, false, false);
		editCellAt(0, QUANTITY_COLUMN_INDEX);
		getEditorComponent().requestFocusInWindow();
	}
	
	private void initializeRowItemValidationBehavior() {
		getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				final int column = e.getColumn();
				
				if (column == QUANTITY_COLUMN_INDEX) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							validateQuantity(getCurrentlySelectedRowItem());
						}
					});
				}
			}
		});
	}
	
	private void validateQuantity(SalesComplianceProjectSalesInvoiceItemRowItem rowItem) {
		if (rowItem.getItem().getOriginalQuantity() < Integer.valueOf(rowItem.getQuantity())) {
			showErrorMessage("Quantity must not be higher than original quantity");
			editCellAtCurrentRow(QUANTITY_COLUMN_INDEX);
		}
	}
	
	private void initializeModelListener() {
		getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				final int row = e.getFirstRow();
				final int column = e.getColumn();
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						switch (column) {
						case QUANTITY_COLUMN_INDEX:
							if (row < getTotalNumberOfItems() - 1) {
								selectAndEditCellAt(row + 1, QUANTITY_COLUMN_INDEX);
							}
							break;
						}
					}
				});
			}
		});
	}
	
	private class QuantityCellEditor extends MagicCellEditor {
		
		public QuantityCellEditor(JTextField textField) {
			super(textField);
		}
		
		@Override
		public boolean stopCellEditing() {
			String quantity = ((JTextField)getComponent()).getText();
			boolean valid = false;
			if (StringUtils.isEmpty(quantity)) {
				showErrorMessage("Quantity must be specified");
			} else if (Integer.parseInt(quantity) == 0) {
				showErrorMessage("Quantity must be greater than 0");
			} else {
				valid = true;
			}
			return (valid) ? super.stopCellEditing() : false;
		}
		
	}
	
}