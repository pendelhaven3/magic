package com.pj.magic.gui;

import java.awt.CardLayout;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pj.magic.gui.panels.AdjustmentInListPanel;
import com.pj.magic.gui.panels.AdjustmentInPanel;
import com.pj.magic.gui.panels.AdjustmentOutListPanel;
import com.pj.magic.gui.panels.AdjustmentOutPanel;
import com.pj.magic.gui.panels.AdjustmentTypeListPanel;
import com.pj.magic.gui.panels.AreaInventoryReportListPanel;
import com.pj.magic.gui.panels.AreaInventoryReportPanel;
import com.pj.magic.gui.panels.AreaListPanel;
import com.pj.magic.gui.panels.BackupDataPanel;
import com.pj.magic.gui.panels.BadStockReturnListPanel;
import com.pj.magic.gui.panels.BadStockReturnPanel;
import com.pj.magic.gui.panels.CashFlowReportPanel;
import com.pj.magic.gui.panels.ChangePasswordPanel;
import com.pj.magic.gui.panels.CreditCardListPanel;
import com.pj.magic.gui.panels.CustomerListPanel;
import com.pj.magic.gui.panels.CustomerSalesSummaryReportPanel;
import com.pj.magic.gui.panels.InventoryCheckListPanel;
import com.pj.magic.gui.panels.InventoryCheckPanel;
import com.pj.magic.gui.panels.InventoryReportPanel;
import com.pj.magic.gui.panels.LoginPanel;
import com.pj.magic.gui.panels.MainMenuPanel;
import com.pj.magic.gui.panels.MaintainAdjustmentTypePanel;
import com.pj.magic.gui.panels.MaintainAreaPanel;
import com.pj.magic.gui.panels.MaintainCreditCardPanel;
import com.pj.magic.gui.panels.MaintainCustomerPanel;
import com.pj.magic.gui.panels.MaintainManufacturerPanel;
import com.pj.magic.gui.panels.MaintainPaymentAdjustmentPanel;
import com.pj.magic.gui.panels.MaintainPaymentTermPanel;
import com.pj.magic.gui.panels.MaintainPaymentTerminalAssignmentPanel;
import com.pj.magic.gui.panels.MaintainPricingSchemePanel;
import com.pj.magic.gui.panels.MaintainProductCategoryPanel;
import com.pj.magic.gui.panels.MaintainProductPanel;
import com.pj.magic.gui.panels.MaintainPurchasePaymentAdjustmentPanel;
import com.pj.magic.gui.panels.MaintainPurchasePaymentAdjustmentTypePanel;
import com.pj.magic.gui.panels.MaintainSupplierPanel;
import com.pj.magic.gui.panels.MaintainUserPanel;
import com.pj.magic.gui.panels.ManufacturerListPanel;
import com.pj.magic.gui.panels.MarkSalesInvoicePanel;
import com.pj.magic.gui.panels.NoMoreStockAdjustmentListPanel;
import com.pj.magic.gui.panels.NoMoreStockAdjustmentPanel;
import com.pj.magic.gui.panels.PaymentAdjustmentListPanel;
import com.pj.magic.gui.panels.PaymentListPanel;
import com.pj.magic.gui.panels.PaymentPanel;
import com.pj.magic.gui.panels.PaymentTermListPanel;
import com.pj.magic.gui.panels.PaymentTerminalAssignmentListPanel;
import com.pj.magic.gui.panels.PostedSalesAndProfitReportPanel;
import com.pj.magic.gui.panels.PriceChangesReportPanel;
import com.pj.magic.gui.panels.PricingSchemeListPanel;
import com.pj.magic.gui.panels.ProductCanvassPanel;
import com.pj.magic.gui.panels.ProductCategoryListPanel;
import com.pj.magic.gui.panels.ProductListPanel;
import com.pj.magic.gui.panels.PurchaseOrderListPanel;
import com.pj.magic.gui.panels.PurchaseOrderPanel;
import com.pj.magic.gui.panels.PurchasePaymentAdjustmentListPanel;
import com.pj.magic.gui.panels.PurchasePaymentAdjustmentTypeListPanel;
import com.pj.magic.gui.panels.PurchasePaymentBankTransfersReportPanel;
import com.pj.magic.gui.panels.PurchasePaymentCheckPaymentsReportPanel;
import com.pj.magic.gui.panels.PurchasePaymentListPanel;
import com.pj.magic.gui.panels.PurchasePaymentPanel;
import com.pj.magic.gui.panels.PurchaseReturnBadStockListPanel;
import com.pj.magic.gui.panels.PurchaseReturnBadStockPanel;
import com.pj.magic.gui.panels.PurchaseReturnListPanel;
import com.pj.magic.gui.panels.PurchaseReturnPanel;
import com.pj.magic.gui.panels.ReceivingReceiptListPanel;
import com.pj.magic.gui.panels.ReceivingReceiptPanel;
import com.pj.magic.gui.panels.RemittanceReportPanel;
import com.pj.magic.gui.panels.ResetPasswordPanel;
import com.pj.magic.gui.panels.SalesInvoiceListPanel;
import com.pj.magic.gui.panels.SalesInvoicePanel;
import com.pj.magic.gui.panels.SalesRequisitionListPanel;
import com.pj.magic.gui.panels.SalesRequisitionPanel;
import com.pj.magic.gui.panels.SalesReturnListPanel;
import com.pj.magic.gui.panels.SalesReturnPanel;
import com.pj.magic.gui.panels.StockCardInventoryReportPanel;
import com.pj.magic.gui.panels.StockQuantityConversionListPanel;
import com.pj.magic.gui.panels.StockQuantityConversionPanel;
import com.pj.magic.gui.panels.SupplierListPanel;
import com.pj.magic.gui.panels.UnpaidReceivingReceiptsListPanel;
import com.pj.magic.gui.panels.UnpaidSalesInvoicesListPanel;
import com.pj.magic.gui.panels.UserListPanel;
import com.pj.magic.gui.panels.menu.AdminMenuPanel;
import com.pj.magic.gui.panels.menu.InventoryCheckMenuPanel;
import com.pj.magic.gui.panels.menu.InventoryMenuPanel;
import com.pj.magic.gui.panels.menu.PurchasePaymentsMenuPanel;
import com.pj.magic.gui.panels.menu.PurchasesMenuPanel;
import com.pj.magic.gui.panels.menu.RecordsMaintenanceMenuPanel;
import com.pj.magic.gui.panels.menu.ReportsPanel;
import com.pj.magic.gui.panels.menu.SalesMenuPanel;
import com.pj.magic.gui.panels.menu.SalesPaymentsMenuPanel;
import com.pj.magic.gui.panels.menu.StockMovementMenuPanel;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.model.Area;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.Customer;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentAdjustment;
import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.model.PurchaseReturn;
import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.User;
import com.pj.magic.service.SystemService;

/**
 * Main JFrame that holds all the panels.
 * Switching from one panel to another is done through this class.
 * 
 * @author PJ
 *
 */

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
	private static final String CHANGE_PASSWORD_PANEL = "CHANGE_PASSWORD_PANEL";
	private static final String RESET_PASSWORD_PANEL = "RESET_PASSWORD_PANEL";
	private static final String PRODUCT_CANVASS_PANEL = "PRODUCT_CANVASS_PANEL";
	private static final String STOCK_CARD_INVENTORY_REPORT_PANEL = "STOCK_CARD_INVENTORY_REPORT_PANEL";
	private static final String AREA_LIST_PANEL = "AREA_LIST_PANEL";
	private static final String MAINTAIN_AREA_PANEL = "MAINTAIN_AREA_PANEL";
	private static final String PAYMENT_PANEL = "PAYMENT_PANEL";
	private static final String PAYMENT_LIST_PANEL = "PAYMENT_LIST_PANEL";
	private static final String PAYMENT_TERMINAL_ASSIGNMENT_LIST_PANEL = 
			"PAYMENT_TERMINAL_ASSIGNMENT_LIST_PANEL";
	private static final String MAINTAIN_PAYMENT_TERMINAL_ASSIGNMENT_PANEL =
			"MAINTAIN_PAYMENT_TERMINAL_ASSIGNMENT_PANEL";
	private static final String SALES_RETURN_LIST_PANEL = "SALES_RETURN_LIST_PANEL";
	private static final String SALES_RETURN_PANEL = "SALES_RETURN_PANEL";
	private static final String REPORTS_PANEL = "REPORTS_PANEL";
	private static final String UNPAID_SALES_INVOICES_LIST_PANEL = "UNPAID_SALES_INVOICES_LIST_PANEL";
	private static final String POSTED_SALES_AND_PROFIT_REPORT_PANEL = "POSTED_SALES_AND_PROFIT_REPORT_PANEL";
	private static final String BACKUP_DATA_PANEL = "BACKUP_DATA_PANEL";
	private static final String SALES_MENU_PANEL = "SALES_MENU_PANEL";
	private static final String PURCHASES_MENU_PANEL = "PURCHASES_MENU_PANEL";
	private static final String INVENTORY_MENU_PANEL = "INVENTORY_MENU_PANEL";
	private static final String SALES_PAYMENTS_MENU_PANEL = "SALES_PAYMENTS_MENU_PANEL";
	private static final String STOCK_MOVEMENT_MENU_PANEL = "STOCK_MOVEMENT_MENU_PANEL";
	private static final String INVENTORY_CHECK_MENU_PANEL = "INVENTORY_CHECK_MENU_PANEL";
	private static final String RECORDS_MAINTENANCE_MENU_PANEL = "RECORDS_MAINTENANCE_MENU_PANEL";
	private static final String ADMIN_MENU_PANEL = "ADMIN_MENU_PANEL";
	private static final String BAD_STOCK_RETURN_LIST_PANEL = "BAD_STOCK_RETURN_LIST_PANEL";
	private static final String BAD_STOCK_RETURN_PANEL = "BAD_STOCK_RETURN_PANEL";
	private static final String CASH_FLOW_REPORT_PANEL = "CASH_FLOW_REPORT_PANEL";
	private static final String ADJUSTMENT_TYPE_LIST_PANEL = "ADJUSTMENT_TYPE_LIST_PANEL";
	private static final String MAINTAIN_ADJUSTMENT_TYPE_PANEL = "MAINTAIN_ADJUSTMENT_TYPE_PANEL";
	private static final String REMITTANCE_REPORT_PANEL = "REMITTANCE_REPORT_PANEL";
	private static final String PRICE_CHANGES_REPORT_PANEL = "PRICE_CHANGES_REPORT_PANEL";
	private static final String NO_MORE_STOCK_ADJUSTMENT_LIST_PANEL = "NO_MORE_STOCK_ADJUSTMENT_LIST_PANEL";
	private static final String NO_MORE_STOCK_ADJUSTMENT_PANEL = "NO_MORE_STOCK_ADJUSTMENT_PANEL";
	private static final String INVENTORY_REPORT_PANEL = "INVENTORY_REPORT_PANEL";
	private static final String CUSTOMER_SALES_SUMMARY_REPORT_PANEL = "CUSTOMER_SALES_SUMMARY_REPORT_PANEL";
	private static final String PAYMENT_ADJUSTMENT_LIST_PANEL = "PAYMENT_ADJUSTMENT_LIST_PANEL";
	private static final String MAINTAIN_PAYMENT_ADJUSTMENT_PANEL = "MAINTAIN_PAYMENT_ADJUSTMENT_PANEL";
	private static final String PURCHASE_PAYMENT_LIST_PANEL = "SUPPLIER_PAYMENT_LIST_PANEL";
	private static final String PURCHASE_PAYMENT_PANEL = "SUPPLIER_PAYMENT_PANEL";
	private static final String PURCHASE_PAYMENT_ADJUSTMENT_LIST_PANEL = 
			"SUPPLIER_PAYMENT_ADJUSTMENT_LIST_PANEL";
	private static final String MAINTAIN_PURCHASE_PAYMENT_ADJUSTMENT_PANEL = 
			"MAINTAIN_SUPPLIER_PAYMENT_ADJUSTMENT_PANEL";
	private static final String CREDIT_CARD_LIST_PANEL = "CREDIT_CARD_LIST_PANEL";
	private static final String MAINTAIN_CREDIT_CARD_PANEL = "MAINTAIN_CREDIT_CARD_PANEL";
	private static final String PURCHASE_RETURN_LIST_PANEL = "PURCHASE_RETURN_LIST_PANEL";
	private static final String PURCHASE_RETURN_PANEL = "PURCHASE_RETURN_PANEL";
	private static final String PURCHASE_PAYMENT_ADJUSTMENT_TYPE_LIST_PANEL = 
			"PURCHASE_PAYMENT_ADJUSTMENT_TYPE_LIST_PANEL";
	private static final String MAINTAIN_PURCHASE_PAYMENT_ADJUSTMENT_TYPE_PANEL = 
			"MAINTAIN_PURCHASE_PAYMENT_ADJUSTMENT_TYPE_PANEL";
	private static final String PURCHASE_RETURN_BAD_STOCK_LIST_PANEL = "BAD_PURCHASE_RETURN_LIST_PANEL";
	private static final String PURCHASE_RETURN_BAD_STOCK_PANEL = "BAD_PURCHASE_RETURN_PANEL";
	private static final String PURCHASE_PAYMENTS_MENU_PANEL = "PURCHASE_PAYMENTS_MENU_PANEL";
	private static final String UNPAID_RECEIVING_RECEIPTS_LIST_PANEL = "UNPAID_RECEIVING_RECEIPTS_LIST_PANEL";
	private static final String PURCHASE_PAYMENT_BANK_TRANSFERS_REPORT_PANEL = 
			"PURCHASE_PAYMENT_BANK_TRANSFERS_REPORT_PANEL";
	private static final String PURCHASE_PAYMENT_CHECK_PAYMENTS_REPORT_PANEL = 
			"PURCHASE_PAYMENT_CHECK_PAYMENTS_REPORT_PANEL";
	
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
	@Autowired private ChangePasswordPanel changePasswordPanel;
	@Autowired private ResetPasswordPanel resetPasswordPanel;
	@Autowired private ProductCanvassPanel productCanvassPanel;
	@Autowired private StockCardInventoryReportPanel stockCardInventoryReportPanel;
	@Autowired private AreaListPanel areaListPanel;
	@Autowired private MaintainAreaPanel maintainAreaPanel;
	@Autowired private PaymentPanel paymentPanel;
	@Autowired private PaymentListPanel paymentListPanel;
	@Autowired private PaymentTerminalAssignmentListPanel paymentTerminalAssignmentListPanel;
	@Autowired private MaintainPaymentTerminalAssignmentPanel maintainPaymentTerminalAssignmentPanel;
	@Autowired private SalesReturnListPanel salesReturnListPanel;
	@Autowired private SalesReturnPanel salesReturnPanel;
	@Autowired private ReportsPanel reportsPanel;
	@Autowired private UnpaidSalesInvoicesListPanel unpaidSalesInvoicesListPanel;
	@Autowired private PostedSalesAndProfitReportPanel postedSalesAndProfitReportPanel;
	@Autowired private BackupDataPanel backupDataPanel;
	@Autowired private SalesMenuPanel salesMenuPanel;
	@Autowired private PurchasesMenuPanel purchasesMenuPanel;
	@Autowired private InventoryMenuPanel inventoryMenuPanel;
	@Autowired private SalesPaymentsMenuPanel salesPaymentsMenuPanel;
	@Autowired private PurchasePaymentsMenuPanel purchasePaymentsMenuPanel;
	@Autowired private StockMovementMenuPanel stockMovementMenuPanel;
	@Autowired private InventoryCheckMenuPanel inventoryCheckMenuPanel;
	@Autowired private RecordsMaintenanceMenuPanel recordsMaintenanceMenuPanel;
	@Autowired private AdminMenuPanel adminMenuPanel;
	@Autowired private BadStockReturnListPanel badStockReturnListPanel;
	@Autowired private BadStockReturnPanel badStockReturnPanel;
	@Autowired private CashFlowReportPanel cashFlowReportPanel;
	@Autowired private AdjustmentTypeListPanel adjustmentTypeListPanel;
	@Autowired private MaintainAdjustmentTypePanel maintainAdjustmentTypePanel;
	@Autowired private RemittanceReportPanel remittanceReportPanel;
	@Autowired private PriceChangesReportPanel priceChangesReportPanel;
	@Autowired private NoMoreStockAdjustmentListPanel noMoreStockAdjustmentListPanel;
	@Autowired private NoMoreStockAdjustmentPanel noMoreStockAdjustmentPanel;
	@Autowired private InventoryReportPanel inventoryReportPanel;
	@Autowired private CustomerSalesSummaryReportPanel customerSalesSummaryReportPanel;
	@Autowired private PaymentAdjustmentListPanel paymentAdjustmentListPanel;
	@Autowired private MaintainPaymentAdjustmentPanel maintainPaymentAdjustmentPanel;
	@Autowired private PurchasePaymentListPanel purchasePaymentListPanel;
	@Autowired private PurchasePaymentPanel purchasePaymentPanel;
	@Autowired private PurchasePaymentAdjustmentListPanel purchasePaymentAdjustmentListPanel;
	@Autowired private MaintainPurchasePaymentAdjustmentPanel maintainPurchasePaymentAdjustmentPanel;
	@Autowired private CreditCardListPanel creditCardListPanel;
	@Autowired private MaintainCreditCardPanel maintainCreditCardPanel;
	@Autowired private PurchaseReturnListPanel purchaseReturnListPanel;
	@Autowired private PurchaseReturnPanel purchaseReturnPanel;
	@Autowired private PurchasePaymentAdjustmentTypeListPanel purchasePaymentAdjustmentTypeListPanel;
	@Autowired private MaintainPurchasePaymentAdjustmentTypePanel maintainPurchasePaymentAdjustmentTypePanel;
	@Autowired private PurchaseReturnBadStockListPanel purchaseReturnBadStockListPanel;
	@Autowired private PurchaseReturnBadStockPanel purchaseReturnBadStockPanel;
	@Autowired private UnpaidReceivingReceiptsListPanel unpaidReceivingReceiptsListPanel;
	@Autowired private PurchasePaymentBankTransfersReportPanel purchasePaymentBankTransfersReportPanel;
	@Autowired private PurchasePaymentCheckPaymentsReportPanel purchasePaymentCheckPaymentsReportPanel;
	
	@Autowired private SystemService systemParameterService;
	@Autowired private DataSource dataSource;
	
	private JPanel panelHolder;
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("application");

	public MagicFrame() {
		this.setSize(1024, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	@PostConstruct
	public void initialize() {
		if (!isDatabaseUp()) {
			JOptionPane.showMessageDialog(this, "Cannot connect to database", 
					"Error Message", JOptionPane.ERROR_MESSAGE);
			closeProgram();
		} else if (!isProgramVersionValid()) {
			JOptionPane.showMessageDialog(this, "Program not up-to-date", 
					"Error Message", JOptionPane.ERROR_MESSAGE);
			closeProgram();
		} else {
			addPanels();
		}
	}
	
	private boolean isDatabaseUp() {
		try {
			DataSourceUtils.releaseConnection(dataSource.getConnection(), dataSource);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	private void closeProgram() {
		processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private boolean isProgramVersionValid() {
		return resourceBundle.getString("application.version")
				.equals(systemParameterService.getProgramVersion());
	}

	private void addPanels() {
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
		panelHolder.add(changePasswordPanel, CHANGE_PASSWORD_PANEL);
		panelHolder.add(resetPasswordPanel, RESET_PASSWORD_PANEL);
		panelHolder.add(productCanvassPanel, PRODUCT_CANVASS_PANEL);
		panelHolder.add(stockCardInventoryReportPanel, STOCK_CARD_INVENTORY_REPORT_PANEL);
		panelHolder.add(areaListPanel, AREA_LIST_PANEL);
		panelHolder.add(maintainAreaPanel, MAINTAIN_AREA_PANEL);
		panelHolder.add(paymentPanel, PAYMENT_PANEL);
		panelHolder.add(paymentListPanel, PAYMENT_LIST_PANEL);
		panelHolder.add(paymentTerminalAssignmentListPanel, PAYMENT_TERMINAL_ASSIGNMENT_LIST_PANEL);
		panelHolder.add(maintainPaymentTerminalAssignmentPanel, MAINTAIN_PAYMENT_TERMINAL_ASSIGNMENT_PANEL);
		panelHolder.add(salesReturnListPanel, SALES_RETURN_LIST_PANEL);
		panelHolder.add(salesReturnPanel, SALES_RETURN_PANEL);
		panelHolder.add(reportsPanel, REPORTS_PANEL);
		panelHolder.add(unpaidSalesInvoicesListPanel, UNPAID_SALES_INVOICES_LIST_PANEL);
		panelHolder.add(postedSalesAndProfitReportPanel, POSTED_SALES_AND_PROFIT_REPORT_PANEL);
		panelHolder.add(backupDataPanel, BACKUP_DATA_PANEL);
		panelHolder.add(salesMenuPanel, SALES_MENU_PANEL);
		panelHolder.add(purchasesMenuPanel, PURCHASES_MENU_PANEL);
		panelHolder.add(inventoryMenuPanel, INVENTORY_MENU_PANEL);
		panelHolder.add(salesPaymentsMenuPanel, SALES_PAYMENTS_MENU_PANEL);
		panelHolder.add(purchasePaymentsMenuPanel, PURCHASE_PAYMENTS_MENU_PANEL);
		panelHolder.add(stockMovementMenuPanel, STOCK_MOVEMENT_MENU_PANEL);
		panelHolder.add(inventoryCheckMenuPanel, INVENTORY_CHECK_MENU_PANEL);
		panelHolder.add(recordsMaintenanceMenuPanel, RECORDS_MAINTENANCE_MENU_PANEL);
		panelHolder.add(adminMenuPanel, ADMIN_MENU_PANEL);
		panelHolder.add(badStockReturnListPanel, BAD_STOCK_RETURN_LIST_PANEL);
		panelHolder.add(badStockReturnPanel, BAD_STOCK_RETURN_PANEL);
		panelHolder.add(cashFlowReportPanel, CASH_FLOW_REPORT_PANEL);
		panelHolder.add(adjustmentTypeListPanel, ADJUSTMENT_TYPE_LIST_PANEL);
		panelHolder.add(maintainAdjustmentTypePanel, MAINTAIN_ADJUSTMENT_TYPE_PANEL);
		panelHolder.add(remittanceReportPanel, REMITTANCE_REPORT_PANEL);
		panelHolder.add(priceChangesReportPanel, PRICE_CHANGES_REPORT_PANEL);
		panelHolder.add(noMoreStockAdjustmentListPanel, NO_MORE_STOCK_ADJUSTMENT_LIST_PANEL);
		panelHolder.add(noMoreStockAdjustmentPanel, NO_MORE_STOCK_ADJUSTMENT_PANEL);
		panelHolder.add(inventoryReportPanel, INVENTORY_REPORT_PANEL);
		panelHolder.add(customerSalesSummaryReportPanel, CUSTOMER_SALES_SUMMARY_REPORT_PANEL);
		panelHolder.add(paymentAdjustmentListPanel, PAYMENT_ADJUSTMENT_LIST_PANEL);
		panelHolder.add(maintainPaymentAdjustmentPanel, MAINTAIN_PAYMENT_ADJUSTMENT_PANEL);
		panelHolder.add(purchasePaymentListPanel, PURCHASE_PAYMENT_LIST_PANEL);
		panelHolder.add(purchasePaymentPanel, PURCHASE_PAYMENT_PANEL);
		panelHolder.add(purchasePaymentAdjustmentListPanel, PURCHASE_PAYMENT_ADJUSTMENT_LIST_PANEL);
		panelHolder.add(maintainPurchasePaymentAdjustmentPanel, MAINTAIN_PURCHASE_PAYMENT_ADJUSTMENT_PANEL);
		panelHolder.add(creditCardListPanel, CREDIT_CARD_LIST_PANEL);
		panelHolder.add(maintainCreditCardPanel, MAINTAIN_CREDIT_CARD_PANEL);
		panelHolder.add(purchaseReturnListPanel, PURCHASE_RETURN_LIST_PANEL);
		panelHolder.add(purchaseReturnPanel, PURCHASE_RETURN_PANEL);
		panelHolder.add(purchasePaymentAdjustmentTypeListPanel, PURCHASE_PAYMENT_ADJUSTMENT_TYPE_LIST_PANEL);
		panelHolder.add(maintainPurchasePaymentAdjustmentTypePanel, 
				MAINTAIN_PURCHASE_PAYMENT_ADJUSTMENT_TYPE_PANEL);
		panelHolder.add(purchaseReturnBadStockListPanel, PURCHASE_RETURN_BAD_STOCK_LIST_PANEL);
		panelHolder.add(purchaseReturnBadStockPanel, PURCHASE_RETURN_BAD_STOCK_PANEL);
		panelHolder.add(unpaidReceivingReceiptsListPanel, UNPAID_RECEIVING_RECEIPTS_LIST_PANEL);
		panelHolder.add(purchasePaymentBankTransfersReportPanel, PURCHASE_PAYMENT_BANK_TRANSFERS_REPORT_PANEL);
		panelHolder.add(purchasePaymentCheckPaymentsReportPanel, PURCHASE_PAYMENT_CHECK_PAYMENTS_REPORT_PANEL);
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
		mainMenuPanel.updateDisplay();
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

	public void switchToAddNewProductPanel() {
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
	
	public void switchToAddBadStockReturnPanel(AdjustmentIn adjustmentIn) {
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

	public void switchToChangePasswordPanel() {
		addPanelNameToTitle("Change Password");
		changePasswordPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, CHANGE_PASSWORD_PANEL);
	}

	public void switchToResetPasswordPanel() {
		addPanelNameToTitle("Reset Password");
		resetPasswordPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, RESET_PASSWORD_PANEL);
	}

	public void switchToProductCanvassPanel() {
		addPanelNameToTitle("Product Canvass");
		productCanvassPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PRODUCT_CANVASS_PANEL);
	}

	public void switchToStockCardInventoryReportPanel() {
		addPanelNameToTitle("Stock Card Inventory Report");
		stockCardInventoryReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, STOCK_CARD_INVENTORY_REPORT_PANEL);
	}

	public void switchToAreaListPanel() {
		addPanelNameToTitle("Area List");
		areaListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, AREA_LIST_PANEL);
	}

	public void switchToEditAreaPanel(Area area) {
		addPanelNameToTitle("Edit Area");
		switchToMaintainAreaPanel(area);
	}
	
	public void switchToAddNewAreaPanel() {
		addPanelNameToTitle("Add New Area");
		switchToMaintainAreaPanel(new Area());
	}
	
	private void switchToMaintainAreaPanel(Area area) {
		maintainAreaPanel.updateDisplay(area);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_AREA_PANEL);
	}

	public void switchToPaymentListPanel() {
		addPanelNameToTitle("Payment");
		paymentListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PAYMENT_LIST_PANEL);
	}

	public void switchToPaymentTerminalAssignmentListPanel() {
		addPanelNameToTitle("Payment Terminal Assignment List");
		paymentTerminalAssignmentListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PAYMENT_TERMINAL_ASSIGNMENT_LIST_PANEL);
	}

	public void switchToAddNewPaymentTerminalAssignmentPanel() {
		addPanelNameToTitle("Add New Payment Terminal Assignment");
		switchToMaintainPaymentTerminalAssignmentPanel(new PaymentTerminalAssignment());
	}
	
	public void switchToEditPaymentTerminalAssignmentPanel(PaymentTerminalAssignment assignment) {
		addPanelNameToTitle("Edit Payment Terminal Assignment");
		switchToMaintainPaymentTerminalAssignmentPanel(assignment);
	}

	private void switchToMaintainPaymentTerminalAssignmentPanel(PaymentTerminalAssignment assignment) {
		maintainPaymentTerminalAssignmentPanel.updateDisplay(assignment);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_PAYMENT_TERMINAL_ASSIGNMENT_PANEL);
	}

	public void switchToSalesReturnListPanel() {
		addPanelNameToTitle("Sales Return List");
		salesReturnListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, SALES_RETURN_LIST_PANEL);
	}

	public void switchToSalesReturnPanel(SalesReturn salesReturn) {
		addPanelNameToTitle("Sales Return");
		salesReturnPanel.updateDisplay(salesReturn);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, SALES_RETURN_PANEL);
	}

	public void switchToPaymentPanel(Payment payment) {
		addPanelNameToTitle("Payment Slip");
		paymentPanel.updateDisplay(payment);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PAYMENT_PANEL);
	}

	public void switchToReportsMenuPanel() {
		addPanelNameToTitle("Reports Menu");
		reportsPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, REPORTS_PANEL);
	}
	
	public void switchToUnpaidSalesInvoicesListPanel() {
		addPanelNameToTitle("Unpaid Sales Invoices List");
		unpaidSalesInvoicesListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, UNPAID_SALES_INVOICES_LIST_PANEL);
	}

	public void switchToPostedSalesAndProfitReportPanel() {
		addPanelNameToTitle("Posted Sales And Profit Report");
		postedSalesAndProfitReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, POSTED_SALES_AND_PROFIT_REPORT_PANEL);
	}

	public void switchToBackupDataPanel() {
		addPanelNameToTitle("Backup Data");
		backupDataPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, BACKUP_DATA_PANEL);
	}

	public void switchToSalesMenuPanel() {
		addPanelNameToTitle("Sales Menu");
		salesMenuPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, SALES_MENU_PANEL);
	}

	public void switchToPurchasesMenuPanel() {
		addPanelNameToTitle("Purchases Menu");
		purchasesMenuPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PURCHASES_MENU_PANEL);
	}

	public void switchToInventoryMenuPanel() {
		addPanelNameToTitle("Product Maintenance and Pricing Schemes Menu");
		inventoryMenuPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, INVENTORY_MENU_PANEL);
	}
	
	public void switchToSalesPaymentsMenuPanel() {
		addPanelNameToTitle("Sales Payments Menu");
		salesPaymentsMenuPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, SALES_PAYMENTS_MENU_PANEL);
	}

	public void switchToStockMovementMenuPanel() {
		addPanelNameToTitle("Stock Movement Menu");
		stockMovementMenuPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, STOCK_MOVEMENT_MENU_PANEL);
	}
	
	public void switchToInventoryCheckMenuPanel() {
		addPanelNameToTitle("Inventory Check Menu");
		inventoryCheckMenuPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, INVENTORY_CHECK_MENU_PANEL);
	}

	public void switchToRecordsMaintenanceMenuPanel() {
		addPanelNameToTitle("Records Maintenance Menu");
		recordsMaintenanceMenuPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, RECORDS_MAINTENANCE_MENU_PANEL);
	}
	
	public void switchToAdminMenuPanel() {
		addPanelNameToTitle("Admin Menu");
		adminMenuPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, ADMIN_MENU_PANEL);
	}

	public void switchToBadStockReturnListPanel() {
		addPanelNameToTitle("Bad Stock Return List");
		badStockReturnListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, BAD_STOCK_RETURN_LIST_PANEL);
	}

	public void switchToBadStockReturnPanel(BadStockReturn badStockReturn) {
		addPanelNameToTitle("Bad Stock Return");
		badStockReturnPanel.updateDisplay(badStockReturn);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, BAD_STOCK_RETURN_PANEL);
	}

	public void switchToCashFlowReportPanel() {
		addPanelNameToTitle("Cash Flow Report");
		cashFlowReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, CASH_FLOW_REPORT_PANEL);
	}

	public void switchToAdjustmentTypeListPanel() {
		addPanelNameToTitle("Adjustment Type List");
		adjustmentTypeListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, ADJUSTMENT_TYPE_LIST_PANEL);
	}

	public void switchToEditAdjustmentTypePanel(AdjustmentType type) {
		addPanelNameToTitle("Edit Adjustment Type");
		switchToMaintainAdjustmentTypePanel(type);
	}
	
	public void switchToAddNewAdjustmentTypePanel() {
		addPanelNameToTitle("Add New Adjustment Type");
		switchToMaintainAdjustmentTypePanel(new AdjustmentType());
	}
	
	private void switchToMaintainAdjustmentTypePanel(AdjustmentType type) {
		maintainAdjustmentTypePanel.updateDisplay(type);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_ADJUSTMENT_TYPE_PANEL);
	}

	public void switchToRemittanceReportPanel() {
		addPanelNameToTitle("Remittance Report");
		remittanceReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, REMITTANCE_REPORT_PANEL);
	}

	public void switchToPriceChangesReportPanel() {
		addPanelNameToTitle("Price Changes Report");
		priceChangesReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PRICE_CHANGES_REPORT_PANEL);
	}

	public void switchToNoMoreStockAdjustmentListPanel() {
		addPanelNameToTitle("No More Stock Adjustment List");
		noMoreStockAdjustmentListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, NO_MORE_STOCK_ADJUSTMENT_LIST_PANEL);
	}

	public void switchToNoMoreStockAdjustmentPanel(NoMoreStockAdjustment noMoreStockAdjustment) {
		addPanelNameToTitle("No More Stock Adjustment");
		noMoreStockAdjustmentPanel.updateDisplay(noMoreStockAdjustment);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, NO_MORE_STOCK_ADJUSTMENT_PANEL);
	}

	public void switchToInventoryReportPanel() {
		addPanelNameToTitle("Inventory Report");
		inventoryReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, INVENTORY_REPORT_PANEL);
	}

	public void switchToCustomerSalesSummaryReportPanel() {
		addPanelNameToTitle("Customer Sales Summary Report");
		customerSalesSummaryReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, CUSTOMER_SALES_SUMMARY_REPORT_PANEL);
	}

	public void switchToPaymentAdjustmentListPanel() {
		addPanelNameToTitle("Payment Adjustment List");
		paymentAdjustmentListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PAYMENT_ADJUSTMENT_LIST_PANEL);
	}

	public void switchToAddNewPaymentAdjustmentPanel() {
		addPanelNameToTitle("Add New Payment Adjustment");
		switchToMaintainPaymentAdjustmentPanel(new PaymentAdjustment());
	}
	
	public void switchToEditPaymentAdjustmentPanel(PaymentAdjustment paymentAdjustment) {
		addPanelNameToTitle("Edit Payment Adjustment");
		switchToMaintainPaymentAdjustmentPanel(paymentAdjustment);
	}
	
	public void switchToMaintainPaymentAdjustmentPanel(PaymentAdjustment paymentAdjustment) {
		maintainPaymentAdjustmentPanel.updateDisplay(paymentAdjustment);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_PAYMENT_ADJUSTMENT_PANEL);
	}

	public void switchToPurchasePaymentListPanel() {
		addPanelNameToTitle("Purchase Payment List");
		purchasePaymentListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PURCHASE_PAYMENT_LIST_PANEL);
	}
	
	public void switchToPurchasePaymentPanel(PurchasePayment purchasePayment) {
		addPanelNameToTitle("Purchase Payment");
		purchasePaymentPanel.updateDisplay(purchasePayment);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PURCHASE_PAYMENT_PANEL);
	}

	public void switchToPurchasePaymentAdjustmentListPanel() {
		addPanelNameToTitle("Purchase Payment Adjustment List");
		purchasePaymentAdjustmentListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PURCHASE_PAYMENT_ADJUSTMENT_LIST_PANEL);
	}

	public void switchToAddNewPurchasePaymentAdjustmentPanel() {
		addPanelNameToTitle("Add New Purchase Payment Adjustment");
		switchToMaintainPurchasePaymentAdjustmentPanel(new PurchasePaymentAdjustment());
	}
	
	public void switchToEditPurchasePaymentAdjustmentPanel(PurchasePaymentAdjustment paymentAdjustment) {
		addPanelNameToTitle("Edit Purchase Payment Adjustment");
		switchToMaintainPurchasePaymentAdjustmentPanel(paymentAdjustment);
	}
	
	public void switchToMaintainPurchasePaymentAdjustmentPanel(PurchasePaymentAdjustment paymentAdjustment) {
		maintainPurchasePaymentAdjustmentPanel.updateDisplay(paymentAdjustment);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_PURCHASE_PAYMENT_ADJUSTMENT_PANEL);
	}
	
	public void switchToCreditCardListPanel() {
		addPanelNameToTitle("Credit Card List");
		creditCardListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, CREDIT_CARD_LIST_PANEL);
	}

	public void switchToAddNewCreditCardPanel() {
		addPanelNameToTitle("Add New Credit Card");
		switchToMaintainCreditCardPanel(new CreditCard());
	}
	
	public void switchToEditCreditCardPanel(CreditCard creditCard) {
		addPanelNameToTitle("Edit Credit Card");
		switchToMaintainCreditCardPanel(creditCard);
	}
	
	public void switchToMaintainCreditCardPanel(CreditCard creditCard) {
		maintainCreditCardPanel.updateDisplay(creditCard);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_CREDIT_CARD_PANEL);
	}
	
	public void switchToPurchaseReturnListPanel() {
		addPanelNameToTitle("Purchase Return (Good Stock) List");
		purchaseReturnListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PURCHASE_RETURN_LIST_PANEL);
	}

	public void switchToPurchaseReturnPanel(PurchaseReturn purchaseReturn) {
		addPanelNameToTitle("Purchase Return (Good Stock)");
		purchaseReturnPanel.updateDisplay(purchaseReturn);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PURCHASE_RETURN_PANEL);
	}
	
	public void switchToPurchasePaymentAdjustmentTypeListPanel() {
		addPanelNameToTitle("Purchase Payment Adjustment Type List");
		purchasePaymentAdjustmentTypeListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PURCHASE_PAYMENT_ADJUSTMENT_TYPE_LIST_PANEL);
	}

	public void switchToEditPurchasePaymentAdjustmentTypePanel(PurchasePaymentAdjustmentType type) {
		addPanelNameToTitle("Edit Purchase Payment Adjustment Type");
		switchToMaintainPurchasePaymentAdjustmentTypePanel(type);
	}
	
	public void switchToAddNewPurchasePaymentAdjustmentTypePanel() {
		addPanelNameToTitle("Add New Purchase Payment Adjustment Type");
		switchToMaintainPurchasePaymentAdjustmentTypePanel(new PurchasePaymentAdjustmentType());
	}
	
	private void switchToMaintainPurchasePaymentAdjustmentTypePanel(PurchasePaymentAdjustmentType type) {
		maintainPurchasePaymentAdjustmentTypePanel.updateDisplay(type);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_PURCHASE_PAYMENT_ADJUSTMENT_TYPE_PANEL);
	}
	
	public void switchToPurchaseReturnBadStockListPanel() {
		addPanelNameToTitle("Purchase Return (Bad Stock) List");
		purchaseReturnBadStockListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PURCHASE_RETURN_BAD_STOCK_LIST_PANEL);
	}

	public void switchToPurchaseReturnBadStockPanel(PurchaseReturnBadStock purchaseReturnBadStock) {
		addPanelNameToTitle("Purchase Return (Bad Stock)");
		purchaseReturnBadStockPanel.updateDisplay(purchaseReturnBadStock);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PURCHASE_RETURN_BAD_STOCK_PANEL);
	}
	
	public void switchToPurchasePaymentsMenuPanel() {
		addPanelNameToTitle("Purchase Payments Menu");
		purchasePaymentsMenuPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PURCHASE_PAYMENTS_MENU_PANEL);
	}

	public void switchToUnpaidReceivingReceiptsListPanel() {
		addPanelNameToTitle("Unpaid Receiving Receipts List");
		unpaidReceivingReceiptsListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, UNPAID_RECEIVING_RECEIPTS_LIST_PANEL);
	}

	public void switchToPurchasePaymentBankTransfersReportPanel() {
		addPanelNameToTitle("Purchase Payment Bank Transfers Report");
		purchasePaymentBankTransfersReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PURCHASE_PAYMENT_BANK_TRANSFERS_REPORT_PANEL);
	}

	public void switchToPurchasePaymentCheckPaymentsReportPanel() {
		addPanelNameToTitle("Purchase Payment Check Payments Report");
		purchasePaymentCheckPaymentsReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PURCHASE_PAYMENT_CHECK_PAYMENTS_REPORT_PANEL);
	}
	
}