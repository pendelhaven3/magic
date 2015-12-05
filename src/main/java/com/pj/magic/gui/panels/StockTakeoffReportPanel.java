package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.report.StockTakeoffReportItem;
import com.pj.magic.service.ManufacturerService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.ListUtil;

import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class StockTakeoffReportPanel extends StandardMagicPanel {

	private static final int PRODUCT_COLUMN_INDEX = 0;
	private static final int UNIT_COLUMN_INDEX = 1;
	private static final int QUANTITY_DIFFERENCE_COLUMN_INDEX = 2;
	
	@Autowired private ManufacturerService manufacturerService;
	
	private MagicComboBox<Manufacturer> manufacturerComboBox;
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JButton generateButton;
	private MagicListTable table;
	private StockTakeoffTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		manufacturerComboBox = new MagicComboBox<>();
		fromDateModel = new UtilCalendarModel();
		toDateModel = new UtilCalendarModel();
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateReport();
			}
		});
		
		initializeTable();
	}

	private void initializeTable() {
		tableModel = new StockTakeoffTableModel();
		table = new MagicListTable(tableModel);
	}

	public void updateDisplay() {
		manufacturerComboBox.setModel(
				ListUtil.toDefaultComboBoxModel(manufacturerService.getAllManufacturers(), true));
		manufacturerComboBox.setSelectedIndex(0);
		fromDateModel.setValue(null);
		toDateModel.setValue(null);
	}
	
	private void generateReport() {
		if (!validateFields()) {
			return;
		}
	}

	private boolean validateFields() {
		if (isManufacturerNotSpecified()) {
			showErrorMessage("Manufacturer must be specified");
			manufacturerComboBox.requestFocus();
			return false;
		}
		
		if (isFromDateNotSpecified()) {
			showErrorMessage("From Date must be specified");
			return false;
		}
		
		if (isToDateNotSpecified()) {
			showErrorMessage("To Date must be specified");
			return false;
		}
		
		return true;
	}

	private boolean isManufacturerNotSpecified() {
		return manufacturerComboBox.getSelectedItem() == null;
	}

	private boolean isFromDateNotSpecified() {
		return fromDateModel.getValue() == null;
	}

	private boolean isToDateNotSpecified() {
		return toDateModel.getValue() == null;
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());

		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.weightx = 1.0;
		mainPanel.add(createControlsPanel(), c);
		
		currentRow++;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		mainPanel.add(ComponentUtil.createScrollPane(table), c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

	private JPanel createControlsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Manufacturer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(manufacturerComboBox, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "From Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createDatePicker(fromDateModel), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "To Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createDatePicker(toDateModel), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		panel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 2;
		panel.add(generateButton, c);

		return panel;
	}
	
	private class StockTakeoffTableModel extends ListBackedTableModel<StockTakeoffReportItem> {

		@Override
		protected String[] getColumnNames() {
			return new String[] {"Product", "Unit", "Qty Difference"};
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			StockTakeoffReportItem item = getItem(rowIndex);
			switch (columnIndex) {
			case PRODUCT_COLUMN_INDEX:
				return item.getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return item.getUnit();
			case QUANTITY_DIFFERENCE_COLUMN_INDEX:
				return String.valueOf(item.getQuantityDifference());
			default:
				throw new RuntimeException("Invalid column index: " + columnIndex);
			}
		}

	}
	
}