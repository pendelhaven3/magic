package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SalesInvoiceReportDialog;
import com.pj.magic.gui.dialog.SalesInvoiceSearchCriteriaDialog;
import com.pj.magic.gui.tables.SalesInvoicesTable;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceReport;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.SalesInvoiceService;

@Component
public class SalesInvoiceListPanel extends StandardMagicPanel {

	@Autowired private SalesInvoicesTable table;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private SalesInvoiceSearchCriteriaDialog salesInvoiceSearchCriteriaDialog;
	@Autowired private SalesInvoiceReportDialog salesInvoiceReportDialog;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private PrintService printService;
	
	@Override
	protected void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}
	
	public void updateDisplay() {
		table.setSalesInvoices(salesInvoiceService.getAllNewSalesInvoices());
		salesInvoiceSearchCriteriaDialog.updateDisplay();
	}

	public void displaySalesInvoiceDetails(SalesInvoice salesInvoice) {
		getMagicFrame().switchToSalesInvoicePanel(salesInvoice);
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
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchSalesInvoices();
			}
		});
		
		toolBar.add(searchButton);
		
		JButton printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview Sales Invoice Report");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printPreviewSalesInvoiceReport();
			}
		});
		toolBar.add(printPreviewButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print Sales Invoice Report");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printSalesInvoiceReport();
			}
		});
		toolBar.add(printButton);
	}

	private void printSalesInvoiceReport() {
		SalesInvoiceReport salesInvoiceReport = createSalesInvoiceReport();
		if (salesInvoiceReport != null) {
			printService.print(salesInvoiceReport);
		}
	}

	private SalesInvoiceReport createSalesInvoiceReport() {
		salesInvoiceReportDialog.setVisible(true);
		
		SalesInvoiceReport salesInvoiceReport = null;
		Date reportDate = salesInvoiceReportDialog.getReportDate();
		if (reportDate != null) {
			SalesInvoiceSearchCriteria criteria = new SalesInvoiceSearchCriteria();
			criteria.setMarked(true);
			criteria.setTransactionDate(reportDate);
			
			salesInvoiceReport = new SalesInvoiceReport();
			salesInvoiceReport.setReportDate(reportDate);
			salesInvoiceReport.setSalesInvoices(salesInvoiceService.search(criteria));
		}
		
		return salesInvoiceReport;
	}

	private void printPreviewSalesInvoiceReport() {
		SalesInvoiceReport salesInvoiceReport = createSalesInvoiceReport();
		if (salesInvoiceReport != null) {
			printPreviewDialog.updateDisplay(
					printService.generateReportAsString(salesInvoiceReport));
			printPreviewDialog.setVisible(true);
		}
	}

	private void searchSalesInvoices() {
		salesInvoiceSearchCriteriaDialog.setVisible(true);
		
		SalesInvoiceSearchCriteria criteria = salesInvoiceSearchCriteriaDialog.getSearchCriteria();
		if (criteria != null) {
			List<SalesInvoice> salesInvoices = salesInvoiceService.search(criteria);
			table.setSalesInvoices(salesInvoices);
			if (!salesInvoices.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

}
