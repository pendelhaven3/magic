package com.pj.magic.gui;

import java.awt.CardLayout;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.panels.AdjustmentInListPanel;
import com.pj.magic.gui.panels.AdjustmentInPanel;
import com.pj.magic.gui.panels.AdjustmentOutListPanel;
import com.pj.magic.gui.panels.AdjustmentOutPanel;
import com.pj.magic.gui.panels.AreaInventoryReportListPanel;
import com.pj.magic.gui.panels.AreaInventoryReportPanel;
import com.pj.magic.gui.panels.CustomerListPanel;
import com.pj.magic.gui.panels.InventoryCheckListPanel;
import com.pj.magic.gui.panels.InventoryCheckPanel;
import com.pj.magic.gui.panels.LoginPanel;
import com.pj.magic.gui.panels.MainMenuPanel;
import com.pj.magic.gui.panels.MaintainCustomerPanel;
import com.pj.magic.gui.panels.MaintainManufacturerPanel;
import com.pj.magic.gui.panels.MaintainPaymentTermPanel;
import com.pj.magic.gui.panels.MaintainPricingSchemePanel;
import com.pj.magic.gui.panels.MaintainProductCategoryPanel;
import com.pj.magic.gui.panels.MaintainProductPanel;
import com.pj.magic.gui.panels.MaintainSupplierPanel;
import com.pj.magic.gui.panels.MaintainUserPanel;
import com.pj.magic.gui.panels.ManufacturerListPanel;
import com.pj.magic.gui.panels.MarkSalesInvoicePanel;
import com.pj.magic.gui.panels.PaymentTermListPanel;
import com.pj.magic.gui.panels.PricingSchemeListPanel;
import com.pj.magic.gui.panels.ProductCategoryListPanel;
import com.pj.magic.gui.panels.ProductListPanel;
import com.pj.magic.gui.panels.PurchaseOrderListPanel;
import com.pj.magic.gui.panels.PurchaseOrderPanel;
import com.pj.magic.gui.panels.ReceivingReceiptListPanel;
import com.pj.magic.gui.panels.ReceivingReceiptPanel;
import com.pj.magic.gui.panels.SalesInvoiceListPanel;
import com.pj.magic.gui.panels.SalesInvoicePanel;
import com.pj.magic.gui.panels.SalesRequisitionListPanel;
import com.pj.magic.gui.panels.SalesRequisitionPanel;
import com.pj.magic.gui.panels.StockQuantityConversionListPanel;
import com.pj.magic.gui.panels.StockQuantityConversionPanel;
import com.pj.magic.gui.panels.SupplierListPanel;
import com.pj.magic.gui.panels.UserListPanel;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.Customer;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.User;

/**
 * Main JFrame that holds all the panels.
 * Switching from one panel to another is done through this class.
 * 
 * @author PJ
 *
 */

@Component
public class MagicFrame extends JFrame {
	
	private static final String LOGIN_PANEL = "LOGIN_PANEL";
	private static final String MAIN_MENU_PANEL = "MAIN_MENU_PANEL";
	private static final String SALES_REQUISITIONS_LIST_PANEL = "SALES_REQUISITIONS_LIST_PANEL";
	private static final String SALES_REQUISITION_PANEL = "SALES_REQUISITION_PANEL";
	private static final String SALES_INVOICES_LIST_PANEL = "SALES_INVOICES_LIST_PANEL";
	private static final String SALES_INVOICE_PANEL = "SALES_INVOICE_PANEL";
	private static final String MARK_SALES_INVOICE_PANEL = "MARK_SALES_INVOICE_PANEL";
	private static final String PRODUCT_LIST_PANEL = "PRODUCT_LIST_PANEL";
	private static final String MAINTAIN_PRODUCT_PANEL = "MAINTAIN_PRODUCT_PANEL";
	private static final String MANUFACTURER_LIST_PANEL = "MANUFACTURER_LIST_PANEL";
	private static final String MAINTAIN_MANUFACTURER_PANEL = "MAINTAIN_MANUFACTURER_PANEL";
	private static final String SUPPLIER_LIST_PANEL = "SUPPLIER_LIST_PANEL";
	private static final String MAINTAIN_SUPPLIER_PANEL = "MAINTAIN_SUPPLIER_PANEL";
	private static final String PRODUCT_CATEGORY_LIST_PANEL = "PRODUCT_CATEGORY_LIST_PANEL";
	private static final String MAINTAIN_PRODUCT_CATEGORY_PANEL = "MAINTAIN_PRODUCT_CATEGORY_PANEL";
	private static final String CUSTOMER_LIST_PANEL = "CUSTOMER_LIST_PANEL";
	private static final String MAINTAIN_CUSTOMER_PANEL = "MAINTAIN_CUSTOMER_PANEL";
	private static final String PAYMENT_TERM_LIST_PANEL = "PAYMENT_TERM_LIST_PANEL";
	private static final String MAINTAIN_PAYMENT_TERM_PANEL = "MAINTAIN_PAYMENT_TERM_PANEL";
	private static final String PRICING_SCHEME_LIST_PANEL = "PRICING_SCHEME_LIST_PANEL";
	private static final String MAINTAIN_PRICING_SCHEME_PANEL = "MAINTAIN_PRICING_SCHEME_PANEL";
	private static final String STOCK_QUANTITY_CONVERSION_LIST_PANEL = "STOCK_QUANTITY_CONVERSION_LIST_PANEL";
	private static final String STOCK_QUANTITY_CONVERSION_PANEL = "STOCK_QUANTITY_CONVERSION_PANEL";
	private static final String PURCHASE_ORDER_LIST_PANEL = "PURCHASE_ORDER_LIST_PANEL";
	private static final String PURCHASE_ORDER_PANEL = "PURCHASE_ORDER_PANEL";
	private static final String RECEIVING_RECEIPT_LIST_PANEL = "RECEIVING_RECEIPT_LIST_PANEL";
	private static final String RECEIVING_RECEIPT_PANEL = "RECEIVING_RECEIPT_PANEL";
	private static final String ADJUSTMENT_OUT_LIST_PANEL = "ADJUSTMENT_OUT_LIST_PANEL";
	private static final String ADJUSTMENT_OUT_PANEL = "ADJUSTMENT_OUT_PANEL";
	private static final String ADJUSTMENT_IN_LIST_PANEL = "ADJUSTMENT_IN_LIST_PANEL";
	private static final String ADJUSTMENT_IN_PANEL = "ADJUSTMENT_IN_PANEL";
	private static final String INVENTORY_CHECK_LIST_PANEL = "INVENTORY_CHECK_LIST_PANEL";
	private static final String INVENTORY_CHECK_PANEL = "INVENTORY_CHECK_PANEL";
	private static final String AREA_INVENTORY_REPORT_LIST_PANEL = "AREA_INVENTORY_REPORT_LIST_PANEL";
	private static final String AREA_INVENTORY_REPORT_PANEL = "AREA_INVENTORY_REPORT_PANEL";
	private static final String USER_LIST_PANEL = "USER_LIST_PANEL";
	private static final String MAINTAIN_USER_PANEL = "MAINTAIN_USER_PANEL";
	
	@Autowired private LoginPanel loginPanel;
	@Autowired private MainMenuPanel mainMenuPanel;
	@Autowired private SalesRequisitionListPanel salesRequisitionsListPanel;
	@Autowired private SalesRequisitionPanel salesRequisitionPanel;
	@Autowired private SalesInvoiceListPanel salesInvoicesListPanel;
	@Autowired private SalesInvoicePanel salesInvoicePanel;
	@Autowired private MarkSalesInvoicePanel markSalesInvoicePanel;
	@Autowired private ProductListPanel productListPanel;
	@Autowired private MaintainProductPanel maintainProductPanel;
	@Autowired private ManufacturerListPanel manufacturerListPanel;
	@Autowired private MaintainManufacturerPanel maintainManufacturerPanel;
	@Autowired private SupplierListPanel supplierListPanel;
	@Autowired private MaintainSupplierPanel maintainSupplierPanel;
	@Autowired private ProductCategoryListPanel productCategoryListPanel;
	@Autowired private MaintainProductCategoryPanel maintainProductCategoryPanel;
	@Autowired private CustomerListPanel customerListPanel;
	@Autowired private MaintainCustomerPanel maintainCustomerPanel;
	@Autowired private PaymentTermListPanel paymentTermListPanel;
	@Autowired private MaintainPaymentTermPanel maintainPaymentTermPanel;
	@Autowired private PricingSchemeListPanel pricingSchemeListPanel;
	@Autowired private MaintainPricingSchemePanel maintainPricingSchemePanel;
	@Autowired private StockQuantityConversionListPanel stockQuantityConversionListPanel;
	@Autowired private StockQuantityConversionPanel stockQuantityConversionPanel;
	@Autowired private PurchaseOrderListPanel purchaseOrderListPanel;
	@Autowired private PurchaseOrderPanel purchaseOrderPanel;
	@Autowired private ReceivingReceiptListPanel receivingReceiptListPanel;
	@Autowired private ReceivingReceiptPanel receivingReceiptPanel;
	@Autowired private AdjustmentOutListPanel adjustmentOutListPanel;
	@Autowired private AdjustmentOutPanel adjustmentOutPanel;
	@Autowired private AdjustmentInListPanel adjustmentInListPanel;
	@Autowired private AdjustmentInPanel adjustmentInPanel;
	@Autowired private InventoryCheckListPanel inventoryCheckListPanel;
	@Autowired private InventoryCheckPanel inventoryCheckPanel;
	@Autowired private AreaInventoryReportListPanel areaInventoryReportListPanel;
	@Autowired private AreaInventoryReportPanel areaInventoryReportPanel;
	@Autowired private UserListPanel userListPanel;
	@Autowired private MaintainUserPanel maintainUserPanel;
	
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
		panelHolder.add(loginPanel, LOGIN_PANEL);
		panelHolder.add(mainMenuPanel, MAIN_MENU_PANEL);
		panelHolder.add(salesRequisitionsListPanel, SALES_REQUISITIONS_LIST_PANEL);
		panelHolder.add(salesRequisitionPanel, SALES_REQUISITION_PANEL);
		panelHolder.add(salesInvoicesListPanel, SALES_INVOICES_LIST_PANEL);
		panelHolder.add(salesInvoicePanel, SALES_INVOICE_PANEL);
		panelHolder.add(markSalesInvoicePanel, MARK_SALES_INVOICE_PANEL);
		panelHolder.add(productListPanel, PRODUCT_LIST_PANEL);
		panelHolder.add(maintainProductPanel, MAINTAIN_PRODUCT_PANEL);
		panelHolder.add(manufacturerListPanel, MANUFACTURER_LIST_PANEL);
		panelHolder.add(maintainManufacturerPanel, MAINTAIN_MANUFACTURER_PANEL);
		panelHolder.add(supplierListPanel, SUPPLIER_LIST_PANEL);
		panelHolder.add(maintainSupplierPanel, MAINTAIN_SUPPLIER_PANEL);
		panelHolder.add(productCategoryListPanel, PRODUCT_CATEGORY_LIST_PANEL);
		panelHolder.add(maintainProductCategoryPanel, MAINTAIN_PRODUCT_CATEGORY_PANEL);
		panelHolder.add(customerListPanel, CUSTOMER_LIST_PANEL);
		panelHolder.add(maintainCustomerPanel, MAINTAIN_CUSTOMER_PANEL);
		panelHolder.add(paymentTermListPanel, PAYMENT_TERM_LIST_PANEL);
		panelHolder.add(maintainPaymentTermPanel, MAINTAIN_PAYMENT_TERM_PANEL);
		panelHolder.add(pricingSchemeListPanel, PRICING_SCHEME_LIST_PANEL);
		panelHolder.add(maintainPricingSchemePanel, MAINTAIN_PRICING_SCHEME_PANEL);
		panelHolder.add(stockQuantityConversionListPanel, STOCK_QUANTITY_CONVERSION_LIST_PANEL);
		panelHolder.add(stockQuantityConversionPanel, STOCK_QUANTITY_CONVERSION_PANEL);
		panelHolder.add(purchaseOrderListPanel, PURCHASE_ORDER_LIST_PANEL);
		panelHolder.add(purchaseOrderPanel, PURCHASE_ORDER_PANEL);
		panelHolder.add(receivingReceiptListPanel, RECEIVING_RECEIPT_LIST_PANEL);
		panelHolder.add(receivingReceiptPanel, RECEIVING_RECEIPT_PANEL);
		panelHolder.add(adjustmentOutListPanel, ADJUSTMENT_OUT_LIST_PANEL);
		panelHolder.add(adjustmentOutPanel, ADJUSTMENT_OUT_PANEL);
		panelHolder.add(adjustmentInListPanel, ADJUSTMENT_IN_LIST_PANEL);
		panelHolder.add(adjustmentInPanel, ADJUSTMENT_IN_PANEL);
		panelHolder.add(inventoryCheckListPanel, INVENTORY_CHECK_LIST_PANEL);
		panelHolder.add(inventoryCheckPanel, INVENTORY_CHECK_PANEL);
		panelHolder.add(areaInventoryReportListPanel, AREA_INVENTORY_REPORT_LIST_PANEL);
		panelHolder.add(areaInventoryReportPanel, AREA_INVENTORY_REPORT_PANEL);
		panelHolder.add(userListPanel, USER_LIST_PANEL);
		panelHolder.add(maintainUserPanel, MAINTAIN_USER_PANEL);
        getContentPane().add(panelHolder);

        switchToLoginPanel();
	}
	
	public void switchToLoginPanel() {
		addPanelNameToTitle("Login");
		loginPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, LOGIN_PANEL);
	}
	
	public void switchToMainMenuPanel() {
		setTitle(constructTitle());
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAIN_MENU_PANEL);
	}
	
	public void switchToSalesRequisitionsListPanel() {
		addPanelNameToTitle("Sales Requisitions List");
		salesRequisitionsListPanel.updateDisplay();
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
		salesInvoicesListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, SALES_INVOICES_LIST_PANEL);
	}
	
	public void switchToSalesInvoicePanel(SalesInvoice salesInvoice) {
		addPanelNameToTitle("Sales Invoice");
		salesInvoicePanel.updateDisplay(salesInvoice);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, SALES_INVOICE_PANEL);
	}
	
	public void switchToProductListPanel() {
		switchToProductListPanel(true);
	}
	
	public void switchToProductListPanel(boolean refresh) {
		addPanelNameToTitle("Product List");
		if (refresh) {
			productListPanel.updateDisplay();
		}
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

	public void switchToProductCategoryListPanel() {
		addPanelNameToTitle("Product Category List");
		productCategoryListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PRODUCT_CATEGORY_LIST_PANEL);
	}
	
	public void switchToEditProductCategoryPanel(ProductCategory category) {
		addPanelNameToTitle("Edit Product Category");
		switchToMaintainProductCategoryPanel(category);
	}

	public void switchToAddNewProductCategoryPanel() {
		addPanelNameToTitle("Add New Product Category");
		switchToMaintainProductCategoryPanel(new ProductCategory());
	}

	private void switchToMaintainProductCategoryPanel(ProductCategory category) {
		maintainProductCategoryPanel.updateDisplay(category);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_PRODUCT_CATEGORY_PANEL);
	}

	public void switchToCustomerListPanel() {
		addPanelNameToTitle("Customer List");
		customerListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, CUSTOMER_LIST_PANEL);
	}

	public void switchToEditCustomerPanel(Customer customer) {
		addPanelNameToTitle("Edit Customer");
		switchToMaintainCustomerPanel(customer);
	}

	public void switchToAddNewCustomerPanel() {
		addPanelNameToTitle("Add New Customer");
		switchToMaintainCustomerPanel(new Customer());
	}

	private void switchToMaintainCustomerPanel(Customer customer) {
		maintainCustomerPanel.updateDisplay(customer);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_CUSTOMER_PANEL);
	}

	public void switchToPaymentTermListPanel() {
		addPanelNameToTitle("Payment Term List");
		paymentTermListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PAYMENT_TERM_LIST_PANEL);
	}
	
	public void switchToEditPaymentTermPanel(PaymentTerm paymentTerm) {
		addPanelNameToTitle("Edit Payment Term");
		switchToMaintainPaymentTermPanel(paymentTerm);
	}

	public void switchToAddNewPaymentTermPanel() {
		addPanelNameToTitle("Add New Payment Term");
		switchToMaintainPaymentTermPanel(new PaymentTerm());
	}

	private void switchToMaintainPaymentTermPanel(PaymentTerm paymentTerm) {
		maintainPaymentTermPanel.updateDisplay(paymentTerm);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_PAYMENT_TERM_PANEL);
	}

	public void switchToPricingSchemeListPanel() {
		addPanelNameToTitle("Pricing Schemes");
		pricingSchemeListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PRICING_SCHEME_LIST_PANEL);
	}
	
	public void switchToEditPricingSchemePanel(PricingScheme pricingScheme) {
		addPanelNameToTitle("Edit Pricing Scheme");
		switchToMaintainPricingSchemePanel(pricingScheme);
	}

	public void switchToAddNewPricingSchemePanel() {
		addPanelNameToTitle("Add New Pricing Scheme");
		switchToMaintainPricingSchemePanel(new PricingScheme());
	}

	private void switchToMaintainPricingSchemePanel(PricingScheme pricingScheme) {
		maintainPricingSchemePanel.updateDisplay(pricingScheme);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_PRICING_SCHEME_PANEL);
	}

	public void switchToStockQuantityConversionListPanel() {
		addPanelNameToTitle("Stock Quantity Conversions List");
		stockQuantityConversionListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, STOCK_QUANTITY_CONVERSION_LIST_PANEL);
	}

	public void switchToStockQuantityConversionPanel(StockQuantityConversion stockQuantityConversion) {
		addPanelNameToTitle("Stock Quantity Conversion");
		stockQuantityConversionPanel.updateDisplay(stockQuantityConversion);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, STOCK_QUANTITY_CONVERSION_PANEL);
	}

	public void switchToPurchaseOrderListPanel() {
		addPanelNameToTitle("Purchase Order List");
		purchaseOrderListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PURCHASE_ORDER_LIST_PANEL);
	}
	
	public void switchToPurchaseOrderPanel(PurchaseOrder purchaseOrder) {
		addPanelNameToTitle("Purchase Order");
		purchaseOrderPanel.updateDisplay(purchaseOrder);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PURCHASE_ORDER_PANEL);
	}

	public void switchToReceivingReceiptListPanel() {
		addPanelNameToTitle("Receiving Receipt List");
		receivingReceiptListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, RECEIVING_RECEIPT_LIST_PANEL);
	}
	
	public void switchToReceivingReceiptPanel(ReceivingReceipt receivingReceipt) {
		addPanelNameToTitle("Receiving Receipt");
		receivingReceiptPanel.updateDisplay(receivingReceipt);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, RECEIVING_RECEIPT_PANEL);
	}
	
	public void switchToAdjustmentOutListPanel() {
		addPanelNameToTitle("Adjustment Out List");
		adjustmentOutListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, ADJUSTMENT_OUT_LIST_PANEL);
	}
	
	public void switchToAdjustmentOutPanel(AdjustmentOut adjustmentOut) {
		addPanelNameToTitle("Adjustment Out");
		adjustmentOutPanel.updateDisplay(adjustmentOut);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, ADJUSTMENT_OUT_PANEL);
	}
	
	public void switchToAdjustmentInListPanel() {
		addPanelNameToTitle("Adjustment In List");
		adjustmentInListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, ADJUSTMENT_IN_LIST_PANEL);
	}
	
	public void switchToAdjustmentInPanel(AdjustmentIn adjustmentIn) {
		addPanelNameToTitle("Adjustment In");
		adjustmentInPanel.updateDisplay(adjustmentIn);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, ADJUSTMENT_IN_PANEL);
	}

	public void switchToInventoryCheckListPanel() {
		addPanelNameToTitle("Inventory Check List");
		inventoryCheckListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, INVENTORY_CHECK_LIST_PANEL);
	}

	public void switchToInventoryCheckPanel(InventoryCheck inventoryCheck) {
		addPanelNameToTitle("Inventory Check");
		inventoryCheckPanel.updateDisplay(inventoryCheck);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, INVENTORY_CHECK_PANEL);
	}

	public void switchToAreaInventoryReportListPanel() {
		addPanelNameToTitle("Area Inventory Report List");
		areaInventoryReportListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, AREA_INVENTORY_REPORT_LIST_PANEL);
	}

	public void switchToAreaInventoryReportPanel(AreaInventoryReport areaInventoryReport) {
		addPanelNameToTitle("Area Inventory Report");
		areaInventoryReportPanel.updateDisplay(areaInventoryReport);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, AREA_INVENTORY_REPORT_PANEL);
	}

	public void switchToMarkSalesInvoicesPanel() {
		addPanelNameToTitle("Mark Sales Invoice");
		markSalesInvoicePanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MARK_SALES_INVOICE_PANEL);
	}

	public void switchToUserListPanel() {
		addPanelNameToTitle("User List");
		userListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, USER_LIST_PANEL);
	}

	public void switchToEditUserPanel(User user) {
		addPanelNameToTitle("Edit User");
		switchToMaintainUserPanel(user);
	}
	
	public void switchToAddNewUserPanel() {
		addPanelNameToTitle("Add New User");
		switchToMaintainUserPanel(new User());
	}
	
	private void switchToMaintainUserPanel(User user) {
		maintainUserPanel.updateDisplay(user);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_USER_PANEL);
	}
	
}
