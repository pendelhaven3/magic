package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.report.PostedSalesReport;
import com.pj.magic.model.report.PostedSalesReportItem;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.model.search.SalesReturnSearchCriteria;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.SalesInvoiceService;
import com.pj.magic.service.SalesReturnService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class PostedSalesReportPanel extends StandardMagicPanel {

	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 0;
	private static final int CUSTOMER_COLUMN_INDEX = 1;
	private static final int TOTAL_AMOUNT_COLUMN_INDEX = 2;
	private static final int DISCOUNTED_AMOUNT_COLUMN_INDEX = 3;
	private static final int NET_AMOUNT_COLUMN_INDEX = 4;
	
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private SalesReturnService salesReturnService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	
	private UtilCalendarModel transactionDateModel;
	private JButton generateButton;
	private MagicListTable table;
	private PostedSalesReportItemTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		transactionDateModel = new UtilCalendarModel();
		
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
		tableModel = new PostedSalesReportItemTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(SALES_INVOICE_NUMBER_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(CUSTOMER_COLUMN_INDEX).setPreferredWidth(200);
	}

	private void generateReport() {
		if (transactionDateModel.getValue() == null) {
			showErrorMessage("Transaction Date must be specified");
			return;
		}
		
		List<PostedSalesReportItem> items = retrieveReportItems();
		tableModel.setItems(items);
		if (items.isEmpty()) {
			showErrorMessage("No records found");
		}
	}

	private List<PostedSalesReportItem> retrieveReportItems() {
		List<PostedSalesReportItem> items = new ArrayList<>();
		
		SalesInvoiceSearchCriteria salesInvoiceCriteria = new SalesInvoiceSearchCriteria();
		salesInvoiceCriteria.setMarked(true);
		salesInvoiceCriteria.setOrderBy("SALES_INVOICE_NO");
		salesInvoiceCriteria.setTransactionDate(transactionDateModel.getValue().getTime());
		
		List<SalesInvoice> salesInvoices = salesInvoiceService.search(salesInvoiceCriteria);
		items.addAll(
			Collections2.transform(salesInvoices, new Function<SalesInvoice, PostedSalesReportItem>() {
	
				@Override
				public PostedSalesReportItem apply(SalesInvoice input) {
					return new PostedSalesReportItem(input);
				}
			})
		);
		
		SalesReturnSearchCriteria salesReturnCriteria = new SalesReturnSearchCriteria();
		salesReturnCriteria.setPosted(true);
		salesReturnCriteria.setPostDate(transactionDateModel.getValue().getTime());
		
		List<SalesReturn> salesReturns = salesReturnService.search(salesReturnCriteria);
		items.addAll(
			Collections2.transform(salesReturns, new Function<SalesReturn, PostedSalesReportItem>() {
	
				@Override
				public PostedSalesReportItem apply(SalesReturn input) {
					return new PostedSalesReportItem(input);
				}
			})
		);
		
		Collections.sort(items, new Comparator<PostedSalesReportItem>() {

			@Override
			public int compare(PostedSalesReportItem o1, PostedSalesReportItem o2) {
				return o1.getTransactionNumber().compareTo(o2.getTransactionNumber());
			}
		});
		
		return items;
	}
	
	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createHorizontalFiller(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(140, "Transaction Date: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(transactionDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0; // right space filler
		c.gridx = 3;
		c.gridy = currentRow;
		mainPanel.add(Box.createGlue(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createVerticalFiller(20), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		generateButton.setPreferredSize(new Dimension(160, 25));
		mainPanel.add(generateButton, c);
		
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

	@Override
	protected void registerKeyBindings() {
		// none
	}

	public void updateDisplay() {
		transactionDateModel.setValue(Calendar.getInstance());
		tableModel.clear();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreviewReport();
			}
		});
		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printReport();
			}
		});
		toolBar.add(printButton);
	}

	private void printPreviewReport() {
		PostedSalesReport report = createReport();
		printPreviewDialog.updateDisplay(printService.generateReportAsString(report));
		printPreviewDialog.setVisible(true);
	}

	private PostedSalesReport createReport() {
		PostedSalesReport report = new PostedSalesReport();
		report.setReportDate(transactionDateModel.getValue().getTime());
		report.setItems(tableModel.getItems());
		return report;
	}

	private void printReport() {
		printService.print(createReport());
	}

	private class PostedSalesReportItemTableModel extends AbstractTableModel {

		private final String[] columnNames =
			{"SI/SR No.", "Customer", "Total Amount", "Disc. Amount", "Net Amount"};
		
		private List<PostedSalesReportItem> items = new ArrayList<>();
		
		public void setItems(List<PostedSalesReportItem> items) {
			this.items = items;
			fireTableDataChanged();
		}
		
		public List<PostedSalesReportItem> getItems() {
			return items;
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
			PostedSalesReportItem item = items.get(rowIndex);
			switch (columnIndex) {
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return item.getTransactionNumber();
			case CUSTOMER_COLUMN_INDEX:
				return item.getCustomer().getName();
			case TOTAL_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getTotalAmount());
			case DISCOUNTED_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getTotalDiscounts());
			case NET_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(item.getTotalNetAmount());
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case TOTAL_AMOUNT_COLUMN_INDEX:
			case DISCOUNTED_AMOUNT_COLUMN_INDEX:
			case NET_AMOUNT_COLUMN_INDEX:
				return Number.class;
			default:
				return Object.class;
			}
		}
		
	}
	
}