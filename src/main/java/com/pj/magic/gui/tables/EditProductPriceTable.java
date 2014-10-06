package com.pj.magic.gui.tables;

import java.awt.Color;

import javax.annotation.PostConstruct;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.tables.models.EditProductPriceTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.util.NumberUtil;

@Component
public class EditProductPriceTable extends MagicTable {

	public static final int UNIT_COLUMN_INDEX = 0;
	public static final int GROSS_COST_COLUMN_INDEX = 1;
	public static final int FINAL_COST_COLUMN_INDEX = 2;
	public static final int SELLING_PRICE_COLUMN_INDEX = 3;
	public static final int PERCENT_PROFIT_COLUMN_INDEX = 4;
	public static final int FLAT_PROFIT_COLUMN_INDEX = 5;
	
	@Autowired private EditProductPriceTableModel tableModel;

	private Product product;
	
	@Autowired
	public EditProductPriceTable(EditProductPriceTableModel tableModel) {
		super(tableModel);
	}
	
	@PostConstruct
	public void initialize() {
		initializeColumns();
		initializeTableModelListener();
	}
	
	private void initializeTableModelListener() {
		tableModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				// TODO: Change other similar code
				final int column = e.getColumn();
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						switch (column) {
						case FINAL_COST_COLUMN_INDEX:
							validateAmount(FINAL_COST_COLUMN_INDEX, "Final Cost");
							break;
						case SELLING_PRICE_COLUMN_INDEX:
							validateAmount(SELLING_PRICE_COLUMN_INDEX, "Selling Price");
							break;
						case PERCENT_PROFIT_COLUMN_INDEX:
							validateAmount(PERCENT_PROFIT_COLUMN_INDEX, "Percent Profit");
							break;
						case FLAT_PROFIT_COLUMN_INDEX:
							validateAmount(FLAT_PROFIT_COLUMN_INDEX, "Flat Profit");
							break;
						}
					}
				});
			}
		});
	}

	private void validateAmount(int columnIndex, String fieldName) {
		String value = (String)getValueAt(0, columnIndex);
		if (StringUtils.isEmpty(value)) {
			showErrorMessage(fieldName + " must be specified");
			editCellAtCurrentRow(columnIndex);
		} else if (!NumberUtil.isAmount(value)) {
			showErrorMessage(fieldName + " must be a valid amount");
			editCellAtCurrentRow(columnIndex);
		}
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
				if (row == 0) {
					c.setBackground(Color.yellow);
				} else if (!isSelected) {
					c.setBackground(null);
				}
				return c;
			}
		};
		editableCellRenderer.setHorizontalAlignment(JLabel.RIGHT);
		
		DefaultTableCellRenderer nonEditableCellRenderer = new DefaultTableCellRenderer();
		nonEditableCellRenderer.setHorizontalAlignment(JLabel.RIGHT);
		
		columnModel.getColumn(FINAL_COST_COLUMN_INDEX)
			.setCellRenderer(editableCellRenderer);
		columnModel.getColumn(SELLING_PRICE_COLUMN_INDEX)
			.setCellRenderer(editableCellRenderer);
		columnModel.getColumn(PERCENT_PROFIT_COLUMN_INDEX)
			.setCellRenderer(editableCellRenderer);
		columnModel.getColumn(FLAT_PROFIT_COLUMN_INDEX)
			.setCellRenderer(editableCellRenderer);
		columnModel.getColumn(GROSS_COST_COLUMN_INDEX)
			.setCellRenderer(nonEditableCellRenderer);
		
		MagicTextField textField = new MagicTextField();
		textField.setMaximumLength(10);
		textField.setBackground(Color.yellow);
		
		DefaultCellEditor cellEditor = new DefaultCellEditor(textField);
		
		columnModel.getColumn(FINAL_COST_COLUMN_INDEX)
			.setCellEditor(cellEditor);
		columnModel.getColumn(SELLING_PRICE_COLUMN_INDEX)
			.setCellEditor(cellEditor);
		columnModel.getColumn(PERCENT_PROFIT_COLUMN_INDEX)
			.setCellEditor(cellEditor);
		columnModel.getColumn(FLAT_PROFIT_COLUMN_INDEX)
			.setCellEditor(cellEditor);
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
					changeSelection(0, FINAL_COST_COLUMN_INDEX, false, false);
					editCellAt(0, FINAL_COST_COLUMN_INDEX);
					getEditorComponent().requestFocusInWindow();
				}
			}
		});
	}
	
}
