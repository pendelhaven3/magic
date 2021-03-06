package com.pj.magic.gui.tables;

import java.awt.Color;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicCellEditor;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.tables.models.EditProductPriceTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.util.NumberUtil;



@Component
public class EditProductPriceTable extends MagicTable {

    private static final long serialVersionUID = 3315995393924397381L;
    
    public static final int UNIT_COLUMN_INDEX = 0;
	public static final int SELLING_PRICE_COLUMN_INDEX = 1;
	public static final int FINAL_COST_COLUMN_INDEX = 2;
	public static final int PERCENT_PROFIT_COLUMN_INDEX = 3;
	public static final int FLAT_PROFIT_COLUMN_INDEX = 4;
	
	@Autowired private EditProductPriceTableModel tableModel;

	private Product product;
	
	@Autowired
	public EditProductPriceTable(EditProductPriceTableModel tableModel) {
		super(tableModel);
        initializeColumns();
	}
	
	private boolean validateAmount(String amount, String fieldName) {
		boolean valid = false;
		if (StringUtils.isEmpty(amount)) {
			showErrorMessage(fieldName + " must be specified");
		} else if (!NumberUtil.isAmount(amount)) {
			showErrorMessage(fieldName + " must be a valid amount");
		} else {
			valid = true;
		}
		return valid;
	}

	private boolean validateSellingPrice(int row, String amount) {
		boolean valid = validateAmount(amount, "Selling Price");
		if (valid) {
		    List<String> units = Unit.sortDescending(product.getUnits());
			String unit = units.get(row);
			if (product.getFinalCost(unit).compareTo(NumberUtil.toBigDecimal(amount)) > 0) {
				showErrorMessage("Selling price less than cost");
				valid = false;
			} else if (row > 0 && !confirm("Do you really want to update selling price of smaller unit?")) {
                valid = false;
			}
		}
		return valid;
	}

	public void initializeColumns() {
		TableColumnModel columnModel = getColumnModel();
		
		DefaultTableCellRenderer editableCellRenderer = new DefaultTableCellRenderer() {
			
			@Override
			public java.awt.Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (tableModel.isCellEditable(row, column)) {
					c.setBackground(Color.yellow);
				} else if (!isSelected) {
					c.setBackground(null);
				}
				return c;
			}
		};
		editableCellRenderer.setHorizontalAlignment(JLabel.RIGHT);
		
		columnModel.getColumn(FINAL_COST_COLUMN_INDEX)
			.setCellRenderer(editableCellRenderer);
		columnModel.getColumn(SELLING_PRICE_COLUMN_INDEX)
			.setCellRenderer(editableCellRenderer);
		columnModel.getColumn(PERCENT_PROFIT_COLUMN_INDEX)
			.setCellRenderer(editableCellRenderer);
		columnModel.getColumn(FLAT_PROFIT_COLUMN_INDEX)
			.setCellRenderer(editableCellRenderer);
		
		
		MagicTextField textField = new MagicTextField();
		textField.setMaximumLength(10);
		textField.setBackground(Color.yellow);
		
		columnModel.getColumn(FINAL_COST_COLUMN_INDEX)
			.setCellEditor(new AmountCellEditor("Final Cost"));
		columnModel.getColumn(SELLING_PRICE_COLUMN_INDEX)
			.setCellEditor(new SellingPriceCellEditor());
		columnModel.getColumn(PERCENT_PROFIT_COLUMN_INDEX)
			.setCellEditor(new AmountCellEditor("Percent Profit"));
		columnModel.getColumn(FLAT_PROFIT_COLUMN_INDEX)
			.setCellEditor(new AmountCellEditor("Flat Profit"));
	}

	public void setProduct(Product product) {
		this.product = product;
		tableModel.setProduct(product);
	}

	public void highlight() {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if (!product.getUnits().isEmpty()) {
					changeSelection(0, SELLING_PRICE_COLUMN_INDEX, false, false);
				}
			}
		});
	}
	
	private class AmountCellEditor extends MagicCellEditor {

		private String fieldName;
		
		public AmountCellEditor(String fieldName) {
			super(new MagicTextField());
			this.fieldName = fieldName;
			
			MagicTextField textField = (MagicTextField)getComponent();
			textField.setMaximumLength(10);
			textField.setBackground(Color.yellow);
		}
		
		@Override
		public boolean stopCellEditing() {
			String amount = ((JTextField)getComponent()).getText();
			return (validateAmount(amount, fieldName)) ? super.stopCellEditing() : false;
		}
		
	}
	
	private class SellingPriceCellEditor extends MagicCellEditor {
	    
	    private int row;

		public SellingPriceCellEditor() {
			super(new MagicTextField());
			
			MagicTextField textField = (MagicTextField)getComponent();
			textField.setMaximumLength(10);
			textField.setBackground(Color.yellow);
		}
		
		@Override
		public boolean stopCellEditing() {
			String amount = ((JTextField)getComponent()).getText();
			return (validateSellingPrice(row, amount)) ? super.stopCellEditing() : false;
		}
		
		@Override
		public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
		        int column) {
		    this.row = row;
		    return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}
		
	}

	public void setTableModel(EditProductPriceTableModel tableModel) {
        this.tableModel = tableModel;
    }
	
}
