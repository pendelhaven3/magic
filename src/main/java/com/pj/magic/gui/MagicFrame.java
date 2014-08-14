package com.pj.magic.gui;

import java.awt.CardLayout;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.panels.MainMenuPanel;
import com.pj.magic.gui.panels.MaintainManufacturerPanel;
import com.pj.magic.gui.panels.MaintainProductPanel;
import com.pj.magic.gui.panels.MaintainSupplierPanel;
import com.pj.magic.gui.panels.ManufacturerListPanel;
import com.pj.magic.gui.panels.ProductListPanel;
import com.pj.magic.gui.panels.SalesInvoiceListPanel;
import com.pj.magic.gui.panels.SalesInvoicePanel;
import com.pj.magic.gui.panels.SalesRequisitionListPanel;
import com.pj.magic.gui.panels.SalesRequisitionPanel;
import com.pj.magic.gui.panels.SupplierListPanel;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.Supplier;

@Component
public class MagicFrame extends JFrame {
	
	private static final String MAIN_MENU_PANEL = "MAIN_MENU_PANEL";
	private static final String SALES_REQUISITIONS_LIST_PANEL = "SALES_REQUISITIONS_LIST_PANEL";
	private static final String SALES_REQUISITION_PANEL = "SALES_REQUISITION_PANEL";
	private static final String SALES_INVOICES_LIST_PANEL = "SALES_INVOICES_LIST_PANEL";
	private static final String SALES_INVOICE_PANEL = "SALES_INVOICE_PANEL";
	private static final String PRODUCT_LIST_PANEL = "PRODUCT_LIST_PANEL";
	private static final String MAINTAIN_PRODUCT_PANEL = "MAINTAIN_PRODUCT_PANEL";
	private static final String MANUFACTURER_LIST_PANEL = "MANUFACTURER_LIST_PANEL";
	private static final String MAINTAIN_MANUFACTURER_PANEL = "MAINTAIN_MANUFACTURER_PANEL";
	private static final String SUPPLIER_LIST_PANEL = "SUPPLIER_LIST_PANEL";
	private static final String MAINTAIN_SUPPLIER_PANEL = "MAINTAIN_SUPPLIER_PANEL";
	
	@Autowired private MainMenuPanel mainMenuPanel;
	@Autowired private SalesRequisitionListPanel salesRequisitionsListPanel;
	@Autowired private SalesRequisitionPanel salesRequisitionPanel;
	@Autowired private SalesInvoiceListPanel salesInvoicesListPanel;
	@Autowired private SalesInvoicePanel salesInvoicePanel;
	@Autowired private ProductListPanel productListPanel;
	@Autowired private MaintainProductPanel maintainProductPanel;
	@Autowired private ManufacturerListPanel manufacturerListPanel;
	@Autowired private MaintainManufacturerPanel maintainManufacturerPanel;
	@Autowired private SupplierListPanel supplierListPanel;
	@Autowired private MaintainSupplierPanel maintainSupplierPanel;
	
	private JPanel panelHolder;
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("application");

	public MagicFrame() {
		this.setSize(1024, 600);
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
		panelHolder.add(productListPanel, PRODUCT_LIST_PANEL);
		panelHolder.add(maintainProductPanel, MAINTAIN_PRODUCT_PANEL);
		panelHolder.add(manufacturerListPanel, MANUFACTURER_LIST_PANEL);
		panelHolder.add(maintainManufacturerPanel, MAINTAIN_MANUFACTURER_PANEL);
		panelHolder.add(supplierListPanel, SUPPLIER_LIST_PANEL);
		panelHolder.add(maintainSupplierPanel, MAINTAIN_SUPPLIER_PANEL);
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
		salesRequisitionPanel.updateDisplay(salesRequisition);
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
	
	public void switchToProductListPanel() {
		addPanelNameToTitle("Product List");
		productListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PRODUCT_LIST_PANEL);
	}
	
	public void switchToEditProductPanel(Product product) {
		addPanelNameToTitle("Edit Product");
		switchToMaintainProductPanel(product);
	}

	private void switchToMaintainProductPanel(Product product) {
		maintainProductPanel.updateDisplay(product);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_PRODUCT_PANEL);
	}

	public void switchToAddNewProductListPanel() {
		addPanelNameToTitle("Add New Product");
		switchToMaintainProductPanel(new Product());
	}

	public void switchToManufacturerListPanel() {
		addPanelNameToTitle("Manufacturer List");
		manufacturerListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MANUFACTURER_LIST_PANEL);
	}

	public void switchToEditManufacturerPanel(Manufacturer manufacturer) {
		addPanelNameToTitle("Edit Manufacturer");
		switchToMaintainManufacturerPanel(manufacturer);
	}

	public void switchToAddNewManufacturerPanel() {
		addPanelNameToTitle("Add New Manufacturer");
		switchToMaintainManufacturerPanel(new Manufacturer());
	}

	private void switchToMaintainManufacturerPanel(Manufacturer manufacturer) {
		maintainManufacturerPanel.updateDisplay(manufacturer);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_MANUFACTURER_PANEL);
	}

	public void switchToSupplierListPanel() {
		addPanelNameToTitle("Supplier List");
		supplierListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, SUPPLIER_LIST_PANEL);
	}

	public void switchToEditSupplierPanel(Supplier supplier) {
		addPanelNameToTitle("Edit Supplier");
		switchToMaintainSupplierPanel(supplier);
	}

	public void switchToAddNewSupplierPanel() {
		addPanelNameToTitle("Add New Supplier");
		switchToMaintainSupplierPanel(new Supplier());
	}

	private void switchToMaintainSupplierPanel(Supplier supplier) {
		maintainSupplierPanel.updateDisplay(supplier);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_SUPPLIER_PANEL);
	}

}
