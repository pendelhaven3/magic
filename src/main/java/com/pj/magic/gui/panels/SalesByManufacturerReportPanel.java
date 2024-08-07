package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.excel.SalesByManufacturerReportExcelGenerator;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.ExcelFileFilter;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Customer;
import com.pj.magic.model.report.SalesByManufacturerReport;
import com.pj.magic.model.report.SalesByManufacturerReportItem;
import com.pj.magic.model.search.SalesByManufacturerReportCriteria;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.ReportService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.ExcelUtil;
import com.pj.magic.util.FileUtil;
import com.pj.magic.util.FormatterUtil;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class SalesByManufacturerReportPanel extends StandardMagicPanel {

	private static final int MANUFACTURER_COLUMN_INDEX = 0;
	private static final int AMOUNT_COLUMN_INDEX = 1;
	
	@Autowired private CustomerService customerService;
	@Autowired private ReportService reportService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	
	private MagicTextField customerCodeField;
	private JLabel customerNameLabel;
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JButton generateButton;
	private JButton generateExcelButton;
	private EllipsisButton selectCustomerButton;
	private MagicListTable table;
	private SalesByManufacturerReportItemsTableModel tableModel;
	private Customer customer;
	
	@Override
	protected void initializeComponents() {
		customerCodeField = new MagicTextField();
		customerCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		customerNameLabel = new JLabel();
		
		selectCustomerButton = new EllipsisButton();
		selectCustomerButton.setToolTipText("Select Customer (F5)");
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
		
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
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
		
		initializeTable();
	}

	private void initializeTable() {
		tableModel = new SalesByManufacturerReportItemsTableModel();
		table = new MagicListTable(tableModel);
	}

	protected void openSelectCustomerDialog() {
		selectCustomerDialog.searchCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			customerCodeField.setText(customer.getCode());
			customerNameLabel.setText(customer.getName());
		}
	}

	private void generateReport() {
		if (!validateFields()) {
			return;
		}
		
		SalesByManufacturerReport report = doGenerateReport();
		tableModel.setItems(report.getItems());
		if (report.getItems().isEmpty()) {
			showErrorMessage("No records found");
		}
	}
	
	private boolean validateFields() {
		if (fromDateModel.getValue() == null) {
			showErrorMessage("Tran. Date From must be specified");
			return false;
		}
		
		if (toDateModel.getValue() == null) {
			showErrorMessage("Tran. Date To must be specified");
			return false;
		}
		
		return true;
	}

	private SalesByManufacturerReport doGenerateReport() {
		SalesByManufacturerReportCriteria criteria = new SalesByManufacturerReportCriteria();
		criteria.setFromDate(fromDateModel.getValue().getTime());
		criteria.setToDate(toDateModel.getValue().getTime());
		
		customer = customerService.findCustomerByCode(customerCodeField.getText());
		if (customer == null) {
			customerNameLabel.setText("-");
		} else {
			criteria.setCustomer(customer);
			customerCodeField.setText(customer.getCode());
			customerNameLabel.setText(customer.getName());
		}
		
		SalesByManufacturerReport report = reportService.getManufacturerSalesReport(criteria);
		report.setCriteria(criteria);
		return report;
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
		mainPanel.add(ComponentUtil.createLabel(120, "Customer: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 5;
		mainPanel.add(createCustomerPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(130, "Date From: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(fromDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Date To: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		datePanel = new JDatePanelImpl(toDateModel);
		datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 6;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.gridwidth = 4;
		generateButton.setPreferredSize(new Dimension(160, 25));
		generateExcelButton.setPreferredSize(new Dimension(160, 25));
		mainPanel.add(ComponentUtil.createGenericPanel(
				generateButton,
				Box.createHorizontalStrut(5),
				generateExcelButton), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 30), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(table);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);
	}

	private JPanel createCustomerPanel() {
		customerCodeField.setPreferredSize(new Dimension(150, 25));
		customerNameLabel.setPreferredSize(new Dimension(300, 20));
		
		JPanel panel = new JPanel();
		panel.add(customerCodeField);
		panel.add(selectCustomerButton);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(customerNameLabel);
		return panel;
	}

	@Override
	protected void registerKeyBindings() {
		customerCodeField.onF5Key(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectCustomerDialog();
			}
		});
	}

	public void updateDisplay() {
		customerCodeField.setText(null);
		customerNameLabel.setText(null);
		fromDateModel.setValue(Calendar.getInstance());
		toDateModel.setValue(Calendar.getInstance());
		tableModel.clear();
		customer = null;
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	private void generateExcelReport() {
		if (!validateFields()) {
			return;
		}
		
		MagicFileChooser saveFileChooser = createSaveFileChooser();
		if (!saveFileChooser.selectSaveFile(this)) {
			return;
		}
		
		SalesByManufacturerReport report = doGenerateReport();
		tableModel.setItems(report.getItems());
		
		File file = saveFileChooser.getSelectedFile();
		
		try (
			Workbook workbook = new SalesByManufacturerReportExcelGenerator().generate(report);
			FileOutputStream out = new FileOutputStream(file);
		) {
			workbook.write(out);
		} catch (IOException e) {
			showMessageForUnexpectedError();
			return;
		}
		
		if (confirm("Excel file generated.\nDo you wish to open the file?")) {
			openExcelFile(file);
		}
	}

	private void openExcelFile(File file) {
		try {
			ExcelUtil.openExcelFile(file);
		} catch (IOException e) {
			showMessageForUnexpectedError();
		}
	}

	private MagicFileChooser createSaveFileChooser() {
		MagicFileChooser fileChooser = new MagicFileChooser();
		fileChooser.setCurrentDirectory(FileUtil.getDesktopFolderPathAsFile());
		fileChooser.setFileFilter(ExcelFileFilter.getInstance());
		fileChooser.setSelectedFile(new File(constructDefaultExcelFileName()));
		return fileChooser;
	}

	private String constructDefaultExcelFileName() {
		Date date = toDateModel.getValue().getTime();
		
		return new StringBuilder()
				.append("SALES BY MANUFACTURER - ")
				.append(FormatterUtil.formatDateInFilename(date))
				.append(".xlsx")
				.toString();
	}
	
	private class SalesByManufacturerReportItemsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Manufacturer", "Amount"};
		
		private List<SalesByManufacturerReportItem> items = new ArrayList<>();
		
		public void setItems(List<SalesByManufacturerReportItem> items) {
			this.items = items;
			fireTableDataChanged();
		}
		
		public void clear() {
			items.clear();
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
		public Object getValueAt(int rowIndex, int columnIndex) {
			SalesByManufacturerReportItem item = items.get(rowIndex);
			switch (columnIndex) {
			case MANUFACTURER_COLUMN_INDEX:
				return item.getManufacturer().getName();
			case AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getAmount());
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == AMOUNT_COLUMN_INDEX) {
				return Number.class;
			} else {
				return Object.class;
			}
		}
		
	}
	
}