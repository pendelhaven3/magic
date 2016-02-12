package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.ExcelFileFilter;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.report.StockUptakeReport;
import com.pj.magic.model.report.StockUptakeReportItem;
import com.pj.magic.model.search.StockUptakeReportCriteria;
import com.pj.magic.service.ExcelService;
import com.pj.magic.service.ManufacturerService;
import com.pj.magic.service.ReportService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FileUtil;
import com.pj.magic.util.ListUtil;

import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class StockUptakeReportPanel extends StandardMagicPanel {

	private static final int PRODUCT_COLUMN_INDEX = 0;
	private static final int UNIT_COLUMN_INDEX = 1;
	private static final int QUANTITY_COLUMN_INDEX = 2;
	
	@Autowired private ManufacturerService manufacturerService;
	@Autowired private ReportService reportService;
	@Autowired private ExcelService excelService;
	
	private MagicComboBox<Manufacturer> manufacturerComboBox;
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JButton generateButton;
	private JButton generateExcelButton;
	private MagicListTable table;
	private StockUptakeTableModel tableModel;
	
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
		
		generateExcelButton = new JButton("Generate Excel");
		generateExcelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateExcelReport();
			}
		});
		
		initializeTable();
	}

	private void initializeTable() {
		tableModel = new StockUptakeTableModel();
		table = new MagicListTable(tableModel);
		
		table.getColumnModel().getColumn(UNIT_COLUMN_INDEX).setPreferredWidth(100);
		table.getColumnModel().getColumn(QUANTITY_COLUMN_INDEX).setPreferredWidth(100);
		table.getColumnModel().getColumn(PRODUCT_COLUMN_INDEX).setPreferredWidth(400);
	}

	public void updateDisplay() {
		manufacturerComboBox.setModel(
				ListUtil.toDefaultComboBoxModel(manufacturerService.getAllManufacturers(), true));
		manufacturerComboBox.setSelectedIndex(0);
		fromDateModel.setValue(null);
		toDateModel.setValue(null);
		tableModel.clear();
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
		panel.add(ComponentUtil.createGenericPanel(
				generateButton,
				Box.createHorizontalStrut(5),
				generateExcelButton), c);

		return panel;
	}
	
	private class StockUptakeTableModel extends ListBackedTableModel<StockUptakeReportItem> {

		@Override
		protected String[] getColumnNames() {
			return new String[] {"Product", "Unit", "Quantity"};
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			StockUptakeReportItem item = getItem(rowIndex);
			switch (columnIndex) {
			case PRODUCT_COLUMN_INDEX:
				return item.getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return item.getUnit();
			case QUANTITY_COLUMN_INDEX:
				return String.valueOf(item.getQuantity());
			default:
				throw new RuntimeException("Invalid column index: " + columnIndex);
			}
		}

	}
	
	private void generateReport() {
		if (!validateFields()) {
			return;
		}
		
		StockUptakeReport report = doGenerateReport();
		tableModel.setItems(report.getItems());
		if (report.getItems().isEmpty()) {
			showErrorMessage("No records found");
		}
	} 

	private StockUptakeReport doGenerateReport() {
		StockUptakeReportCriteria criteria = new StockUptakeReportCriteria();
		criteria.setManufacturer((Manufacturer)manufacturerComboBox.getSelectedItem());
		criteria.setFromDate(fromDateModel.getValue().getTime());
		criteria.setToDate(toDateModel.getValue().getTime());
		
		return reportService.getStockUptakeReport(criteria);
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
	
	private void generateExcelReport() {
		if (!validateFields()) {
			return;
		}
		
		MagicFileChooser saveFileChooser = createSaveFileChooser();
		if (!saveFileChooser.selectSaveFile(this)) {
			return;
		}
		
		StockUptakeReport report = doGenerateReport();
		tableModel.setItems(report.getItems());
		
		try (
			Workbook workbook = excelService.generateSpreadsheet(report);
			FileOutputStream out = new FileOutputStream(saveFileChooser.getSelectedFile());
		) {
			workbook.write(out);
		} catch (IOException e) {
			showMessageForUnexpectedError();
			return;
		}
		
		showMessage("Excel spreadsheet generated");
	}

	private MagicFileChooser createSaveFileChooser() {
		MagicFileChooser fileChooser = new MagicFileChooser();
		fileChooser.setCurrentDirectory(FileUtil.getDesktopFolderPathAsFile());
		fileChooser.setFileFilter(ExcelFileFilter.getInstance());
		fileChooser.setSelectedFile(new File("stockUptakeReport.xlsx"));
		return fileChooser;
	}
	
}