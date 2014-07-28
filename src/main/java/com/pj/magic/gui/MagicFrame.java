package com.pj.magic.gui;

import java.awt.CardLayout;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.panels.MainMenuPanel;
import com.pj.magic.gui.panels.SalesInvoicePanel;
import com.pj.magic.gui.panels.SalesInvoicesListPanel;
import com.pj.magic.gui.panels.SalesRequisitionPanel;
import com.pj.magic.gui.panels.SalesRequisitionsListPanel;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;

@Component
public class MagicFrame extends JFrame {
	
	private static final String MAIN_MENU_PANEL = "MAIN_MENU_PANEL";
	private static final String SALES_REQUISITIONS_LIST_PANEL = "SALES_REQUISITIONS_LIST_PANEL";
	private static final String SALES_REQUISITION_PANEL = "SALES_REQUISITION_PANEL";
	private static final String SALES_INVOICES_LIST_PANEL = "SALES_INVOICES_LIST_PANEL";
	private static final String SALES_INVOICE_PANEL = "SALES_INVOICE_PANEL";
	
	@Autowired private MainMenuPanel mainMenuPanel;
	@Autowired private SalesRequisitionsListPanel salesRequisitionsListPanel;
	@Autowired private SalesRequisitionPanel salesRequisitionPanel;
	@Autowired private SalesInvoicesListPanel salesInvoicesListPanel;
	@Autowired private SalesInvoicePanel salesInvoicePanel;
	
	private JPanel panelHolder;
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("application");

	public MagicFrame() {
		this.setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	@PostConstruct
	private void addContents() {
		panelHolder = new JPanel(new CardLayout());
		panelHolder.add(mainMenuPanel, MAIN_MENU_PANEL);
		panelHolder.add(salesRequisitionsListPanel, SALES_REQUISITIONS_LIST_PANEL);
		panelHolder.add(salesRequisitionPanel, SALES_REQUISITION_PANEL);
		panelHolder.add(salesInvoicesListPanel, SALES_INVOICES_LIST_PANEL);
		panelHolder.add(salesInvoicePanel, SALES_INVOICE_PANEL);
        getContentPane().add(panelHolder);

        switchToMainMenuPanel();
	}
	
	public void switchToMainMenuPanel() {
		setTitle(constructTitle());
		mainMenuPanel.refreshDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAIN_MENU_PANEL);
	}
	
	public void switchToSalesRequisitionsListPanel() {
		addPanelNameToTitle("Sales Requisitions List");
		salesRequisitionsListPanel.refreshDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, SALES_REQUISITIONS_LIST_PANEL);
	}
	
	public void switchToSalesRequisitionPanel(SalesRequisition salesRequisition) {
		addPanelNameToTitle("Sales Requisition");
		salesRequisitionPanel.refreshDisplay(salesRequisition);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, SALES_REQUISITION_PANEL);
	}
	
	public String constructTitle() {
		return resourceBundle.getString("application.title");
	}
	
	public void addPanelNameToTitle(String panelName) {
		setTitle(constructTitle() + " - " + panelName);
	}

	public void switchToSalesInvoicesListPanel() {
		addPanelNameToTitle("Sales Invoices List");
		salesInvoicesListPanel.refreshDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, SALES_INVOICES_LIST_PANEL);
	}
	
	public void switchToSalesInvoicePanel(SalesInvoice salesInvoice) {
		addPanelNameToTitle("Sales Invoice");
		salesInvoicePanel.refreshDisplay(salesInvoice);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, SALES_INVOICE_PANEL);
	}
	
}
