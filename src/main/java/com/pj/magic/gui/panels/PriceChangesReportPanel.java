package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.ProductPriceHistory;
import com.pj.magic.model.Unit;
import com.pj.magic.model.report.PriceChangesReport;
import com.pj.magic.service.PaymentService;
import com.pj.magic.service.PaymentTerminalService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.PrintServiceImpl;
import com.pj.magic.service.ProductPriceService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PriceChangesReportPanel extends StandardMagicPanel {

	private static final int PRODUCT_COLUMN_INDEX = 0;
	private static final int UPDATE_DATE_COLUMN_INDEX = 1;
	private static final int UNIT_PRICE_CASE_COLUMN_INDEX = 2;
	private static final int UNIT_PRICE_TIE_COLUMN_INDEX = 3;
	private static final int UNIT_PRICE_CARTON_COLUMN_INDEX = 4;
	private static final int UNIT_PRICE_DOZEN_COLUMN_INDEX = 5;
	private static final int UNIT_PRICE_PIECES_COLUMN_INDEX = 6;
	
	@Autowired private PaymentService paymentService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	@Autowired private PaymentTerminalService paymentTerminalService;
	@Autowired private ProductPriceService productPriceService;
	
	private MagicListTable table;
	private PriceChangesTableModel tableModel;
	private UtilCalendarModel reportDateModel;
	private JButton generateButton;
	
	@Override
	protected void initializeComponents() {
		reportDateModel = new UtilCalendarModel();
		
		generateButton = new JButton("Generate Report");
		generateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				generateReport();
			}
		});
		
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void generateReport() {
		if (reportDateModel.getValue() == null) {
			showErrorMessage("Date must be specified");
			return;
		}
		
		List<ProductPriceHistory> items = productPriceService
				.getAllProductPriceHistoriesByDate(reportDateModel.getValue().getTime());
		tableModel.setItems(items);
		if (!items.isEmpty()) {
			table.changeSelection(0, 0);
		} else {
			showErrorMessage("No matching records");
		}
	}

	private PriceChangesReport createPriceChangesReport() {
		Date reportDate = reportDateModel.getValue().getTime();
		
		PriceChangesReport report = new PriceChangesReport();
		report.setReportDate(reportDate);
		report.setItems(productPriceService.getAllProductPriceHistoriesByDate(reportDate));
		return report;
	}

	private void initializeTable() {
		tableModel = new PriceChangesTableModel();
		table = new MagicListTable(tableModel);
		
		table.getColumnModel().getColumn(PRODUCT_COLUMN_INDEX).setPreferredWidth(300);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}
	
	public void updateDisplay() {
		reportDateModel.setValue(Calendar.getInstance());
		tableModel.clear();
	}

	@Override
	protected void registerKeyBindings() {
		// none
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
		mainPanel.add(ComponentUtil.createLabel(120, "Date:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(reportDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createHorizontalFiller(30), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(generateButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 5;
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(600, 200));
		mainPanel.add(scrollPane, c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreview();
			}
		});
		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print();
			}
		});
		toolBar.add(printButton);
	}

	private void print() {
		if (reportDateModel.getValue() == null) {
			showErrorMessage("Date must be specified");
			return;
		}
		
		printService.print(createPriceChangesReport());
	}

	private void printPreview() {
		if (reportDateModel.getValue() == null) {
			showErrorMessage("Date must be specified");
			return;
		}
		
		PriceChangesReport report = createPriceChangesReport();
		printPreviewDialog.updateDisplay(printService.generateReportAsString(report));
		printPreviewDialog.setColumnsPerLine(PrintServiceImpl.PRICE_CHANGES_REPORT_CHARACTERS_PER_LINE);
		printPreviewDialog.setUseCondensedFontForPrinting(true);
		printPreviewDialog.setVisible(true);
	}

	private class PriceChangesTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Product", "Time", "CSE", "TIE", "CTN", "DOZ", "PCS"};
		
		private List<ProductPriceHistory> items = new ArrayList<>();
		
		public void setItems(List<ProductPriceHistory> items) {
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
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case UNIT_PRICE_CASE_COLUMN_INDEX:
			case UNIT_PRICE_TIE_COLUMN_INDEX:
			case UNIT_PRICE_CARTON_COLUMN_INDEX:
			case UNIT_PRICE_DOZEN_COLUMN_INDEX:
			case UNIT_PRICE_PIECES_COLUMN_INDEX:
				return Number.class;
			default:
				return String.class;
			}
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ProductPriceHistory item = items.get(rowIndex);
			switch (columnIndex) {
			case PRODUCT_COLUMN_INDEX:
				return item.getProduct().getDescription();
			case UPDATE_DATE_COLUMN_INDEX:
				return FormatterUtil.formatTime(item.getUpdateDate());
			case UNIT_PRICE_CASE_COLUMN_INDEX:
				BigDecimal unitPriceCase = item.getUnitPrice(Unit.CASE);
				return (unitPriceCase != null) ? FormatterUtil.formatAmount(unitPriceCase) : null;
			case UNIT_PRICE_TIE_COLUMN_INDEX:
				BigDecimal unitPriceTie = item.getUnitPrice(Unit.TIE);
				return (unitPriceTie != null) ? FormatterUtil.formatAmount(unitPriceTie) : null;
			case UNIT_PRICE_CARTON_COLUMN_INDEX:
				BigDecimal unitPriceCarton = item.getUnitPrice(Unit.CARTON);
				return (unitPriceCarton != null) ? FormatterUtil.formatAmount(unitPriceCarton) : null;
			case UNIT_PRICE_DOZEN_COLUMN_INDEX:
				BigDecimal unitPriceDozen = item.getUnitPrice(Unit.DOZEN);
				return (unitPriceDozen != null) ? FormatterUtil.formatAmount(unitPriceDozen) : null;
			case UNIT_PRICE_PIECES_COLUMN_INDEX:
				BigDecimal unitPricePieces = item.getUnitPrice(Unit.PIECES);
				return (unitPricePieces != null) ? FormatterUtil.formatAmount(unitPricePieces) : null;
			default:
				throw new RuntimeException("Fetcing invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
	}
	
}