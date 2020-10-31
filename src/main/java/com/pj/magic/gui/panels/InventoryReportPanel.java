package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.excel.InventoryReportExcelGenerator;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.report.InventoryReport;
import com.pj.magic.model.report.InventoryReportItem;
import com.pj.magic.model.search.InventoryReportCriteria;
import com.pj.magic.service.ManufacturerService;
import com.pj.magic.service.ReportService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.ExcelUtil;
import com.pj.magic.util.FileUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.ListUtil;

@Component
public class InventoryReportPanel extends StandardMagicPanel {

	private static final int CODE_COLUMN_INDEX = 0;
	private static final int DESCRIPTION_COLUMN_INDEX = 1;
	private static final int UNIT_COLUMN_INDEX = 2;
	private static final int QUANTITY_COLUMN_INDEX = 3;
	private static final int UNIT_COST_COLUMN_INDEX = 4;
	private static final int TOTAL_COST_COLUMN_INDEX = 5;
	
	@Autowired private ReportService reportService;
	@Autowired private ManufacturerService manufacturerService;
	
	private JLabel totalCostLabel;
	private JTable table;
	private InventoryReportItemsTableModel tableModel;
	private MagicComboBox<Manufacturer> manufacturerComboBox;
	private JButton generateButton;
	
	public void updateDisplay() {
		manufacturerComboBox.setSelectedIndex(0);
		generateAndDisplayReport();
	}

	@Override
	protected void initializeComponents() {
		manufacturerComboBox = new MagicComboBox<>();
		manufacturerComboBox.setModel(ListUtil.toDefaultComboBoxModel(manufacturerService.getAllManufacturers(), true));
		
		generateButton = new JButton("Generate");
		generateButton.addActionListener(e -> generateAndDisplayReport());
		
		totalCostLabel = new JLabel();
		
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void generateAndDisplayReport() {
		InventoryReport report = generateReport();
		displayReport(report);
	}

	private void displayReport(InventoryReport report) {
		tableModel.setItems(report.getItems());
		if (!report.getItems().isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
		totalCostLabel.setText(FormatterUtil.formatAmount(report.getTotalCost()));
	}

	private InventoryReport generateReport() {
		InventoryReportCriteria criteria = new InventoryReportCriteria();
		criteria.setManufacturer((Manufacturer)manufacturerComboBox.getSelectedItem());
		
		return reportService.getInventoryReport(criteria);
	}
	
	private void initializeTable() {
		tableModel = new InventoryReportItemsTableModel();
		table = new MagicListTable(tableModel);
		
		table.getColumnModel().getColumn(DESCRIPTION_COLUMN_INDEX).setPreferredWidth(250);
		table.getColumnModel().getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(50);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(150, "Manufacturer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		manufacturerComboBox.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(manufacturerComboBox, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.weightx = 1.0;
		mainPanel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		generateButton.setPreferredSize(new Dimension(120, 25));
		mainPanel.add(generateButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		mainPanel.add(new JScrollPane(table), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.EAST;
		totalCostLabel.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(ComponentUtil.createGenericPanel(
				ComponentUtil.createLabel(100, "Total Cost:"), totalCostLabel), c);
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToInventoryMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton toExcelButton = new MagicToolBarButton("excel", "Generate Excel spreadsheet",
				e -> generateExcel());
		toolBar.add(toExcelButton);
	}

	private void generateExcel() {
		MagicFileChooser excelFileChooser = FileUtil.createSaveFileChooser(generateDefaultSpreadsheetName());

		if (!excelFileChooser.selectSaveFile(this)) {
			return;
		}
		
		InventoryReport report = generateReport();
		
		try (
			Workbook workbook = new InventoryReportExcelGenerator().generate(report);
			FileOutputStream out = new FileOutputStream(excelFileChooser.getSelectedFile());
		) {
			workbook.write(out);
		} catch (IOException e) {
			showErrorMessage("Unexpected error during excel generation");
		}
		
		displayReport(report);
		
        if (confirm("Excel file generated.\nDo you wish to open the file?")) {
            openExcelFile(excelFileChooser.getSelectedFile());
        }
	}

	private String generateDefaultSpreadsheetName() {
		StringBuilder sb = new StringBuilder();
		sb.append("Inventory Report");
		
		if (manufacturerComboBox.getSelectedIndex() > -1) {
			Manufacturer manufacturer = (Manufacturer)manufacturerComboBox.getSelectedItem();
			sb.append(" - ").append(manufacturer.getName());
		}
		
		sb.append(" - ").append(new SimpleDateFormat("MMM-dd-yyyy").format(new Date())).append(".xlsx");
		return sb.toString();
	}
	
	private void openExcelFile(File file) {
		try {
			ExcelUtil.openExcelFile(file);
		} catch (IOException e) {
			showMessageForUnexpectedError();
		}
	}

	private class InventoryReportItemsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Code", "Description", "Unit", "Quantity", "Unit Cost",
				"Total Cost"};
		
		private List<InventoryReportItem> items = new ArrayList<>();
		
		public void setItems(List<InventoryReportItem> items) {
			this.items = items;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return items.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case UNIT_COST_COLUMN_INDEX:
			case TOTAL_COST_COLUMN_INDEX:
				return Number.class;
			default:
				return String.class;
			}
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			InventoryReportItem item = items.get(rowIndex);
			switch (columnIndex) {
			case CODE_COLUMN_INDEX:
				return item.getProduct().getCode();
			case DESCRIPTION_COLUMN_INDEX:
				return item.getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return item.getUnit();
			case QUANTITY_COLUMN_INDEX:
				return item.getQuantity();
			case UNIT_COST_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getCost());
			case TOTAL_COST_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getTotalCost());
			default:
				throw new RuntimeException("Error fetching column index: " + columnIndex);
			}
		}
		
	}
	
}