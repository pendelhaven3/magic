package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SearchSalesInvoicesDialog;
import com.pj.magic.gui.tables.SalesInvoicesTable;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.service.SalesInvoiceService;

@Component
public class SalesInvoiceListPanel extends StandardMagicPanel {

	@Autowired private SalesInvoicesTable table;
	@Autowired private SalesInvoiceService salesInvoiceService;
	@Autowired private SearchSalesInvoicesDialog searchSalesInvoicesDialog;
	
	private SalesInvoiceSearchCriteria searchCriteria;
	
	public SalesInvoiceListPanel() {
		setTitle("Sales Invoices List");
	}
	
	@Override
	protected void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToSalesMenuPanel();
	}
	
	public void updateDisplay() {
		table.setSalesInvoices(salesInvoiceService.getAllNewSalesInvoices());
		searchSalesInvoicesDialog.updateDisplay();
		searchCriteria = null;
	}

	public void displaySalesInvoiceDetails(SalesInvoice salesInvoice) {
		getMagicFrame().switchToSalesInvoicePanel(salesInvoice);
	}
	
	@Override
	protected void registerKeyBindings() {
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
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
	}

	private void searchSalesInvoices() {
		searchSalesInvoicesDialog.setVisible(true);
		
		SalesInvoiceSearchCriteria criteria = searchSalesInvoicesDialog.getSearchCriteria();
		if (criteria != null) {
			searchCriteria = criteria;
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

	@Override
	public void updateDisplayOnBack() {
		if (searchCriteria != null) {
			List<SalesInvoice> salesInvoices = salesInvoiceService.search(searchCriteria);
			table.setSalesInvoices(salesInvoices);
		} else {
			updateDisplay();
		}
	}
	
}
