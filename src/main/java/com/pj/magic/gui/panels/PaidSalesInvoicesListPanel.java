package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.report.UnpaidSalesInvoicesReport;
import com.pj.magic.service.PaymentService;
import com.pj.magic.service.PrintService;
import com.pj.magic.util.FormatterUtil;

@Component
public class PaidSalesInvoicesListPanel extends StandardMagicPanel {

	private static final int PAYMENT_DATE_COLUMN_INDEX = 0;
	private static final int SALES_INVOICE_NUMBER_COLUMN_INDEX = 1;
	private static final int CUSTOMER_COLUMN_INDEX = 2;
	private static final int NET_AMOUNT_COLUMN_INDEX = 3;
	private static final int PAYMENT_NUMBER_COLUMN_INDEX = 4;
	
	@Autowired private PaymentService paymentService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	
	private MagicListTable table;
	private PaymentSalesInvoicesTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		initializeTable();
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	private void initializeTable() {
		tableModel = new PaymentSalesInvoicesTableModel();
		table = new MagicListTable(tableModel);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(PAYMENT_DATE_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(SALES_INVOICE_NUMBER_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(CUSTOMER_COLUMN_INDEX).setPreferredWidth(300);
		columnModel.getColumn(NET_AMOUNT_COLUMN_INDEX).setPreferredWidth(100);
		columnModel.getColumn(PAYMENT_NUMBER_COLUMN_INDEX).setPreferredWidth(100);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToReportsPanel();
	}
	
	public void updateDisplay() {
		tableModel.setPaymentSalesInvoices(paymentService.findAllPaidSalesInvoices());
	}

	@Override
	protected void registerKeyBindings() {
		// none
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		
		JScrollPane scrollPane = new JScrollPane(table);
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
//		UnpaidSalesInvoicesReport report = createUnpaidSalesInvoicesReport();
//		printService.print(report);
	}

	private void printPreview() {
//		UnpaidSalesInvoicesReport report = createUnpaidSalesInvoicesReport();
//		printPreviewDialog.updateDisplay(printService.generateReportAsString(report));
//		printPreviewDialog.setVisible(true);
	}

	private UnpaidSalesInvoicesReport createPaidSalesInvoicesReport() {
		UnpaidSalesInvoicesReport report = new UnpaidSalesInvoicesReport();
//		report.setSalesInvoices(salesInvoiceService.findAllUnpaidSalesInvoices());
		return report;
	}

	private class PaymentSalesInvoicesTableModel extends AbstractTableModel {

		private final String[] columnNames = 
			{"Payment Date", "SI No.", "Customer", "Net Amount", "Payment No."};
		
		private List<PaymentSalesInvoice> paymentSalesInvoices = new ArrayList<>();
		
		public void setPaymentSalesInvoices(List<PaymentSalesInvoice> paymentSalesInvoices) {
			this.paymentSalesInvoices = paymentSalesInvoices;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return paymentSalesInvoices.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PaymentSalesInvoice paymentSalesInvoice = paymentSalesInvoices.get(rowIndex);
			switch (columnIndex) {
			case PAYMENT_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(paymentSalesInvoice.getParent().getPostDate());
			case SALES_INVOICE_NUMBER_COLUMN_INDEX:
				return paymentSalesInvoice.getSalesInvoice().getSalesInvoiceNumber();
			case CUSTOMER_COLUMN_INDEX:
				return paymentSalesInvoice.getSalesInvoice().getCustomer().getName();
			case NET_AMOUNT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(paymentSalesInvoice.getSalesInvoice().getTotalNetAmount());
			case PAYMENT_NUMBER_COLUMN_INDEX:
				return paymentSalesInvoice.getParent().getPaymentNumber();
			default:
				throw new RuntimeException("Fetch invalid column index: " + columnIndex);
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == NET_AMOUNT_COLUMN_INDEX) {
				return Number.class;
			} else {
				return Object.class;
			}
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
	}
	
}