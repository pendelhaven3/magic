package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.excel.TopSalesByItemReportExcelGenerator;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.ExcelFileFilter;
import com.pj.magic.gui.component.MagicFileChooser;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.report.TopSalesByItemReport;
import com.pj.magic.model.report.TopSalesByItemReportItem;
import com.pj.magic.service.ReportService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.ExcelUtil;
import com.pj.magic.util.FileUtil;
import com.pj.magic.util.FormatterUtil;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

@Component
public class TopSalesByItemReportPanel extends StandardMagicPanel {

	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	private static final int UNIT_COLUMN_INDEX = 2;
	private static final int AMOUNT_COLUMN_INDEX = 3;
	
	@Autowired private ReportService reportService;
	
	private UtilCalendarModel fromDateModel;
	private UtilCalendarModel toDateModel;
	private JButton generateButton;
	private JButton generateExcelButton;
	private MagicListTable table;
	private TopSalesByItemReportItemsTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
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
		tableModel = new TopSalesByItemReportItemsTableModel();
		table = new MagicListTable(tableModel);
	}

	private void generateReport() {
		if (!validateFields()) {
			return;
		}
		
		TopSalesByItemReport report = doGenerateReport();
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

	private TopSalesByItemReport doGenerateReport() {
		Date fromDate = fromDateModel.getValue().getTime();
		Date toDate = toDateModel.getValue().getTime();
		
		return reportService.getTopSalesByItemReport(fromDate, toDate);
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

	public void updateDisplay() {
		fromDateModel.setValue(Calendar.getInstance());
		toDateModel.setValue(Calendar.getInstance());
		tableModel.clear();
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
		
		TopSalesByItemReport report = doGenerateReport();
		tableModel.setItems(report.getItems());
		
		File file = saveFileChooser.getSelectedFile();
		
		try (
			Workbook workbook = new TopSalesByItemReportExcelGenerator().generate(report);
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
				.append("TOP SALES BY ITEM - ")
				.append(FormatterUtil.formatDateInFilename(date))
				.append(".xlsx")
				.toString();
	}
	
	private class TopSalesByItemReportItemsTableModel extends ListBackedTableModel<TopSalesByItemReportItem> {

		private final String[] columnNames = {"Product Code", "Description", "Unit", "Amount"};
		
		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			TopSalesByItemReportItem item = getItem(rowIndex);
			switch (columnIndex) {
			case PRODUCT_CODE_COLUMN_INDEX:
				return item.getProduct().getCode();
			case PRODUCT_DESCRIPTION_COLUMN_INDEX:
				return item.getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return item.getUnit();
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

	@Override
	protected void registerKeyBindings() {
		// TODO Auto-generated method stub
		
	}
	
}