package com.pj.magic.gui;

import java.awt.CardLayout;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import com.pj.magic.OnStartUp;
import com.pj.magic.gui.component.CardLayoutPanel;
import com.pj.magic.gui.panels.*;
import com.pj.magic.gui.panels.menu.AdminMenuPanel;
import com.pj.magic.gui.panels.menu.AlfonsoRaffleMenuPanel;
import com.pj.magic.gui.panels.menu.BadStockMenuPanel;
import com.pj.magic.gui.panels.menu.InventoryCheckMenuPanel;
import com.pj.magic.gui.panels.menu.InventoryMenuPanel;
import com.pj.magic.gui.panels.menu.JchsCellphoneRaffleMenuPanel;
import com.pj.magic.gui.panels.menu.JchsRaffleMenuPanel;
import com.pj.magic.gui.panels.menu.PromoMenuPanel;
import com.pj.magic.gui.panels.menu.PurchasePaymentsMenuPanel;
import com.pj.magic.gui.panels.menu.PurchasesMenuPanel;
import com.pj.magic.gui.panels.menu.RecordsMaintenanceMenuPanel;
import com.pj.magic.gui.panels.menu.ReportsMenuPanel;
import com.pj.magic.gui.panels.menu.SalesMenuPanel;
import com.pj.magic.gui.panels.menu.SalesPaymentsMenuPanel;
import com.pj.magic.gui.panels.menu.StockMovementMenuPanel;
import com.pj.magic.gui.panels.promo.AlfonsoRaffleParticipatingItemsPanel;
import com.pj.magic.gui.panels.promo.AlfonsoRaffleTicketClaimPanel;
import com.pj.magic.gui.panels.promo.AlfonsoRaffleTicketClaimsListPanel;
import com.pj.magic.gui.panels.promo.AlfonsoRaffleTicketsListPanel;
import com.pj.magic.gui.panels.promo.PromoListPanel;
import com.pj.magic.gui.panels.promo.PromoPanel;
import com.pj.magic.gui.panels.promo.PromoRedemptionListPanel;
import com.pj.magic.gui.panels.promo.PromoRedemptionPanel;
import com.pj.magic.gui.panels.promo.PromoRedemptionPromoListPanel;
import com.pj.magic.gui.panels.promo.PromoRedemptionRebatesPanel;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.model.Area;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.BadStockAdjustmentIn;
import com.pj.magic.model.BadStockAdjustmentOut;
import com.pj.magic.model.BadStockInventoryCheck;
import com.pj.magic.model.BadStockReport;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.BirForm2307Report;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.Customer;
import com.pj.magic.model.EcashReceiver;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCorrection;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRaffleTicketClaim;
import com.pj.magic.model.PromoRedemption;
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
import com.pj.magic.model.report.ProductQuantityDiscrepancyReport;
import com.pj.magic.service.SystemService;
import com.pj.magic.util.ApplicationUtil;

/**
 * Main JFrame that holds all the panels.
 * Switching from one panel to another is done through this class.
 * 
 * @author PJ
 *
 */

@Component
public class MagicFrame extends JFrame implements ApplicationContextAware {
	
    private static final long serialVersionUID = 8652209770458750280L;

    private static final Logger logger = LoggerFactory.getLogger(MagicFrame.class);
	
	public static final String LOGIN_PANEL = "LOGIN_PANEL";
	public static final String MAIN_MENU_PANEL = "MAIN_MENU_PANEL";
	public static final String SALES_REQUISITIONS_LIST_PANEL = "SALES_REQUISITIONS_LIST_PANEL";
	public static final String SALES_REQUISITION_PANEL = "SALES_REQUISITION_PANEL";
	public static final String SALES_INVOICES_LIST_PANEL = "SALES_INVOICES_LIST_PANEL";
	public static final String SALES_INVOICE_PANEL = "SALES_INVOICE_PANEL";
	public static final String MARK_SALES_INVOICE_PANEL = "MARK_SALES_INVOICE_PANEL";
	public static final String PRODUCT_LIST_PANEL = "PRODUCT_LIST_PANEL";
	public static final String MAINTAIN_PRODUCT_PANEL = "MAINTAIN_PRODUCT_PANEL";
	public static final String MANUFACTURER_LIST_PANEL = "MANUFACTURER_LIST_PANEL";
	public static final String MAINTAIN_MANUFACTURER_PANEL = "MAINTAIN_MANUFACTURER_PANEL";
	public static final String SUPPLIER_LIST_PANEL = "SUPPLIER_LIST_PANEL";
	public static final String MAINTAIN_SUPPLIER_PANEL = "MAINTAIN_SUPPLIER_PANEL";
	public static final String PRODUCT_CATEGORY_LIST_PANEL = "PRODUCT_CATEGORY_LIST_PANEL";
	public static final String MAINTAIN_PRODUCT_CATEGORY_PANEL = "MAINTAIN_PRODUCT_CATEGORY_PANEL";
	public static final String CUSTOMER_LIST_PANEL = "CUSTOMER_LIST_PANEL";
	public static final String MAINTAIN_CUSTOMER_PANEL = "MAINTAIN_CUSTOMER_PANEL";
	public static final String PAYMENT_TERM_LIST_PANEL = "PAYMENT_TERM_LIST_PANEL";
	public static final String MAINTAIN_PAYMENT_TERM_PANEL = "MAINTAIN_PAYMENT_TERM_PANEL";
	public static final String PRICING_SCHEME_LIST_PANEL = "PRICING_SCHEME_LIST_PANEL";
	public static final String MAINTAIN_PRICING_SCHEME_PANEL = "MAINTAIN_PRICING_SCHEME_PANEL";
	public static final String STOCK_QUANTITY_CONVERSION_LIST_PANEL = "STOCK_QUANTITY_CONVERSION_LIST_PANEL";
	public static final String STOCK_QUANTITY_CONVERSION_PANEL = "STOCK_QUANTITY_CONVERSION_PANEL";
	public static final String PURCHASE_ORDER_LIST_PANEL = "PURCHASE_ORDER_LIST_PANEL";
	public static final String PURCHASE_ORDER_PANEL = "PURCHASE_ORDER_PANEL";
	public static final String RECEIVING_RECEIPT_LIST_PANEL = "RECEIVING_RECEIPT_LIST_PANEL";
	public static final String RECEIVING_RECEIPT_PANEL = "RECEIVING_RECEIPT_PANEL";
	public static final String ADJUSTMENT_OUT_LIST_PANEL = "ADJUSTMENT_OUT_LIST_PANEL";
	public static final String ADJUSTMENT_OUT_PANEL = "ADJUSTMENT_OUT_PANEL";
	public static final String ADJUSTMENT_IN_LIST_PANEL = "ADJUSTMENT_IN_LIST_PANEL";
	public static final String ADJUSTMENT_IN_PANEL = "ADJUSTMENT_IN_PANEL";
	public static final String INVENTORY_CHECK_LIST_PANEL = "INVENTORY_CHECK_LIST_PANEL";
	public static final String INVENTORY_CHECK_PANEL = "INVENTORY_CHECK_PANEL";
	public static final String AREA_INVENTORY_REPORT_LIST_PANEL = "AREA_INVENTORY_REPORT_LIST_PANEL";
	public static final String AREA_INVENTORY_REPORT_PANEL = "AREA_INVENTORY_REPORT_PANEL";
	public static final String USER_LIST_PANEL = "USER_LIST_PANEL";
	public static final String MAINTAIN_USER_PANEL = "MAINTAIN_USER_PANEL";
	public static final String CHANGE_PASSWORD_PANEL = "CHANGE_PASSWORD_PANEL";
	public static final String RESET_PASSWORD_PANEL = "RESET_PASSWORD_PANEL";
	public static final String PRODUCT_CANVASS_PANEL = "PRODUCT_CANVASS_PANEL";
	public static final String STOCK_CARD_INVENTORY_REPORT_PANEL = "STOCK_CARD_INVENTORY_REPORT_PANEL";
	public static final String AREA_LIST_PANEL = "AREA_LIST_PANEL";
	public static final String MAINTAIN_AREA_PANEL = "MAINTAIN_AREA_PANEL";
	public static final String PAYMENT_PANEL = "PAYMENT_PANEL";
	public static final String PAYMENT_LIST_PANEL = "PAYMENT_LIST_PANEL";
	public static final String PAYMENT_TERMINAL_ASSIGNMENT_LIST_PANEL = 
			"PAYMENT_TERMINAL_ASSIGNMENT_LIST_PANEL";
	public static final String MAINTAIN_PAYMENT_TERMINAL_ASSIGNMENT_PANEL =
			"MAINTAIN_PAYMENT_TERMINAL_ASSIGNMENT_PANEL";
	public static final String SALES_RETURN_LIST_PANEL = "SALES_RETURN_LIST_PANEL";
	public static final String SALES_RETURN_PANEL = "SALES_RETURN_PANEL";
	public static final String REPORTS_PANEL = "REPORTS_PANEL";
	public static final String UNPAID_SALES_INVOICES_LIST_PANEL = "UNPAID_SALES_INVOICES_LIST_PANEL";
	public static final String POSTED_SALES_AND_PROFIT_REPORT_PANEL = "POSTED_SALES_AND_PROFIT_REPORT_PANEL";
	public static final String BACKUP_DATA_PANEL = "BACKUP_DATA_PANEL";
	public static final String SALES_MENU_PANEL = "SALES_MENU_PANEL";
	public static final String PURCHASES_MENU_PANEL = "PURCHASES_MENU_PANEL";
	public static final String INVENTORY_MENU_PANEL = "INVENTORY_MENU_PANEL";
	public static final String SALES_PAYMENTS_MENU_PANEL = "SALES_PAYMENTS_MENU_PANEL";
	public static final String STOCK_MOVEMENT_MENU_PANEL = "STOCK_MOVEMENT_MENU_PANEL";
	public static final String INVENTORY_CHECK_MENU_PANEL = "INVENTORY_CHECK_MENU_PANEL";
	public static final String RECORDS_MAINTENANCE_MENU_PANEL = "RECORDS_MAINTENANCE_MENU_PANEL";
	public static final String ADMIN_MENU_PANEL = "ADMIN_MENU_PANEL";
	public static final String BAD_STOCK_RETURN_LIST_PANEL = "BAD_STOCK_RETURN_LIST_PANEL";
	public static final String BAD_STOCK_RETURN_PANEL = "BAD_STOCK_RETURN_PANEL";
	public static final String CASH_FLOW_REPORT_PANEL = "CASH_FLOW_REPORT_PANEL";
	public static final String ADJUSTMENT_TYPE_LIST_PANEL = "ADJUSTMENT_TYPE_LIST_PANEL";
	public static final String MAINTAIN_ADJUSTMENT_TYPE_PANEL = "MAINTAIN_ADJUSTMENT_TYPE_PANEL";
	public static final String REMITTANCE_REPORT_PANEL = "REMITTANCE_REPORT_PANEL";
	public static final String PRICE_CHANGES_REPORT_PANEL = "PRICE_CHANGES_REPORT_PANEL";
	public static final String NO_MORE_STOCK_ADJUSTMENT_LIST_PANEL = "NO_MORE_STOCK_ADJUSTMENT_LIST_PANEL";
	public static final String NO_MORE_STOCK_ADJUSTMENT_PANEL = "NO_MORE_STOCK_ADJUSTMENT_PANEL";
	public static final String INVENTORY_REPORT_PANEL = "INVENTORY_REPORT_PANEL";
	public static final String CUSTOMER_SALES_SUMMARY_REPORT_PANEL = "CUSTOMER_SALES_SUMMARY_REPORT_PANEL";
	public static final String PAYMENT_ADJUSTMENT_LIST_PANEL = "PAYMENT_ADJUSTMENT_LIST_PANEL";
	public static final String MAINTAIN_PAYMENT_ADJUSTMENT_PANEL = "MAINTAIN_PAYMENT_ADJUSTMENT_PANEL";
	public static final String PURCHASE_PAYMENT_LIST_PANEL = "PURCHASE_PAYMENT_LIST_PANEL";
	public static final String PURCHASE_PAYMENT_PANEL = "PURCHASE_PAYMENT_PANEL";
	public static final String PURCHASE_PAYMENT_ADJUSTMENT_LIST_PANEL = 
			"PURCHASE_PAYMENT_ADJUSTMENT_LIST_PANEL";
	public static final String MAINTAIN_PURCHASE_PAYMENT_ADJUSTMENT_PANEL = 
			"MAINTAIN_PURCHASE_PAYMENT_ADJUSTMENT_PANEL";
	public static final String CREDIT_CARD_LIST_PANEL = "CREDIT_CARD_LIST_PANEL";
	public static final String MAINTAIN_CREDIT_CARD_PANEL = "MAINTAIN_CREDIT_CARD_PANEL";
	public static final String PURCHASE_RETURN_LIST_PANEL = "PURCHASE_RETURN_LIST_PANEL";
	public static final String PURCHASE_RETURN_PANEL = "PURCHASE_RETURN_PANEL";
	public static final String PURCHASE_PAYMENT_ADJUSTMENT_TYPE_LIST_PANEL = 
			"PURCHASE_PAYMENT_ADJUSTMENT_TYPE_LIST_PANEL";
	public static final String MAINTAIN_PURCHASE_PAYMENT_ADJUSTMENT_TYPE_PANEL = 
			"MAINTAIN_PURCHASE_PAYMENT_ADJUSTMENT_TYPE_PANEL";
	public static final String PURCHASE_RETURN_BAD_STOCK_LIST_PANEL = "BAD_PURCHASE_RETURN_LIST_PANEL";
	public static final String PURCHASE_RETURN_BAD_STOCK_PANEL = "BAD_PURCHASE_RETURN_PANEL";
	public static final String PURCHASE_PAYMENTS_MENU_PANEL = "PURCHASE_PAYMENTS_MENU_PANEL";
	public static final String UNPAID_RECEIVING_RECEIPTS_LIST_PANEL = "UNPAID_RECEIVING_RECEIPTS_LIST_PANEL";
	public static final String PURCHASE_PAYMENT_BANK_TRANSFERS_REPORT_PANEL = 
			"PURCHASE_PAYMENT_BANK_TRANSFERS_REPORT_PANEL";
	public static final String PURCHASE_PAYMENT_CHECK_PAYMENTS_REPORT_PANEL = 
			"PURCHASE_PAYMENT_CHECK_PAYMENTS_REPORT_PANEL";
	public static final String PURCHASE_PAYMENT_CREDIT_CARD_PAYMENTS_REPORT_PANEL = 
			"PURCHASE_PAYMENT_CREDIT_CARD_PAYMENTS_REPORT_PANEL";
	public static final String SALES_BY_MANUFACTURER_REPORT_PANEL = "SALES_BY_MANUFACTURER_REPORT_PANEL";
	public static final String CUSTOMER_CHECK_PAYMENTS_REPORT_PANEL = "CUSTOMER_CHECK_PAYMENTS_REPORT_PANEL";
	public static final String DISBURSEMENT_REPORT_PANEL = "DISBURSEMENT_REPORT_PANEL";
	public static final String PROMO_REDEMPTION_PANEL = "PROMO_REDEMPTION_PANEL";
	public static final String PROMO_REDEMPTION_LIST_PANEL = "PROMO_REDEMPTION_LIST_PANEL";
	public static final String PROMO_REDEMPTION_PROMO_LIST_PANEL = "PROMO_REDEMPTION_PROMO_LIST_PANEL";
	public static final String PROMO_LIST_PANEL = "PROMO_LIST_PANEL";
	public static final String PROMO_PANEL = "PROMO_PANEL";
	public static final String PROMO_POINTS_PANEL = "PROMO_POINTS_PANEL";
	public static final String UNPAID_CREDIT_CARD_PAYMENTS_LIST_PANEL = "UNPAID_CREDIT_CARD_PAYMENTS_LIST_PANEL";
	public static final String CREDIT_CARD_STATEMENT_LIST_PANEL = "CREDIT_CARD_STATEMENT_LIST_PANEL";
	public static final String CREDIT_CARD_STATEMENT_PANEL = "CREDIT_CARD_STATEMENT_PANEL";
	public static final String STOCK_OFFTAKE_REPORT_PANEL = "STOCK_OFFTAKE_REPORT_PANEL";
	public static final String SALES_REQUISITION_SEPARATE_ITEMS_PANEL = "SALES_REQUISITION_SEPARATE_ITEMS_PANEL";
	public static final String PROMO_REDEMPTION_REBATES_PANEL = "PROMO_REDEMPTION_REBATES_PANEL";
	public static final String UPLOAD_MAXIMUM_STOCK_LEVEL_CHANGES_PANEL = 
			"UPLOAD_MAXIMUM_STOCK_LEVEL_CHANGES_PANEL";
	public static final String INVENTORY_CORRECTION_LIST_PANEL = "INVENTORY_CORRECTION_LIST_PANEL";
	public static final String INVENTORY_CORRECTION_PANEL = "INVENTORY_CORRECTION_PANEL";
	public static final String DAILY_PRODUCT_QUANTITY_DISCREPANCY_REPORT_LIST_PANEL = 
			"DAILY_PRODUCT_QUANTITY_DISCREPANCY_REPORT_LIST_PANEL";
	public static final String PRODUCT_QUANTITY_DISCREPANCY_REPORT_PANEL = 
			"DAILY_PRODUCT_QUANTITY_DISCREPANCY_REPORT_PANEL";
    public static final String PILFERAGE_REPORT_PANEL = "PILFERAGE_REPORT_PANEL";
	public static final String SCHEDULED_PRICE_CHANGES_LIST_PANEL = "SCHEDULED_PRICE_CHANGES_LIST_PANEL";
    public static final String EDIT_PRODUCT_PRICES_LIST_PANEL = "EDIT_PRODUCT_PRICES_LIST_PANEL";
    public static final String EWT_REPORT_PANEL = "EWT_REPORT_PANEL";
    public static final String BIR_FORM_2307_REPORT_LIST_PANEL = "BIR_FORM_2307_REPORT_LIST_PANEL";
    public static final String BIR_FORM_2307_REPORT_PANEL = "BIR_FORM_2307_REPORT_PANEL";
    public static final String BAD_STOCK_MENU_PANEL = "BAD_STOCK_MENU_PANEL";
    public static final String BAD_STOCK_INVENTORY_LIST_PANEL = "BAD_STOCK_INVENTORY_LIST_PANEL";
    public static final String BAD_STOCK_ADJUSTMENT_IN_LIST_PANEL = "BAD_STOCK_ADJUSTMENT_IN_LIST_PANEL";
    public static final String BAD_STOCK_ADJUSTMENT_IN_PANEL = "BAD_STOCK_ADJUSTMENT_IN_PANEL";
    public static final String BAD_STOCK_ADJUSTMENT_OUT_LIST_PANEL = "BAD_STOCK_ADJUSTMENT_OUT_LIST_PANEL";
    public static final String BAD_STOCK_ADJUSTMENT_OUT_PANEL = "BAD_STOCK_ADJUSTMENT_OUT_PANEL";
    public static final String BAD_STOCK_REPORT_LIST_PANEL = "BAD_STOCK_REPORT_LIST_PANEL";
    public static final String BAD_STOCK_REPORT_PANEL = "BAD_STOCK_REPORT_PANEL";
    public static final String BAD_STOCK_CARD_INVENTORY_REPORT_PANEL = "BAD_STOCK_CARD_INVENTORY_REPORT_PANEL";
    public static final String BAD_STOCK_INVENTORY_CHECK_LIST_PANEL = "BAD_STOCK_INVENTORY_CHECK_LIST_PANEL";
    public static final String BAD_STOCK_INVENTORY_CHECK_PANEL = "BAD_STOCK_INVENTORY_CHECK_PANEL";
	public static final String ECASH_RECEIVER_LIST_PANEL = "ECASH_RECEIVER_LIST_PANEL";
	public static final String MAINTAIN_ECASH_RECEIVER_PANEL = "MAINTAIN_ECASH_RECEIVER_PANEL";
    public static final String ECASH_PAYMENTS_REPORT_PANEL = "ECASH_PAYMENTS_REPORT_PANEL";
    public static final String ECASH_PURCHASE_PAYMENTS_REPORT_PANEL = "ECASH_PURCHASE_PAYMENTS_REPORT_PANEL";
	public static final String PROMO_MENU_PANEL = "PROMO_MENU_PANEL";
	public static final String JCHS_RAFFLE_MENU_PANEL = "JCHS_RAFFLE_MENU_PANEL";
	public static final String JCHS_RAFFLE_TICKETS_LIST_PANEL = "JCHS_RAFFLE_TICKETS_LIST_PANEL";
	public static final String JCHS_RAFFLE_TICKET_CLAIMS_LIST_PANEL = "JCHS_RAFFLE_TICKET_CLAIMS_LIST_PANEL";
	public static final String JCHS_RAFFLE_TICKET_CLAIM_PANEL = "JCHS_RAFFLE_TICKET_CLAIM_PANEL";
	public static final String JCHS_CELLPHONE_RAFFLE_MENU_PANEL = "JCHS_CELLPHONE_RAFFLE_MENU_PANEL";
	public static final String JCHS_CELLPHONE_RAFFLE_TICKETS_LIST_PANEL = "JCHS_CELLPHONE_RAFFLE_TICKETS_LIST_PANEL";
	public static final String JCHS_CELLPHONE_RAFFLE_TICKET_CLAIMS_LIST_PANEL = "JCHS_CELLPHONE_RAFFLE_TICKET_CLAIMS_LIST_PANEL";
	public static final String JCHS_CELLPHONE_RAFFLE_TICKET_CLAIM_PANEL = "JCHS_CELLPHONE_RAFFLE_TICKET_CLAIM_PANEL";
	public static final String ALFONSO_RAFFLE_MENU_PANEL = "ALFONSO_RAFFLE_MENU_PANEL";
	public static final String ALFONSO_RAFFLE_TICKETS_LIST_PANEL = "ALFONSO_RAFFLE_TICKETS_LIST_PANEL";
	public static final String ALFONSO_RAFFLE_TICKET_CLAIMS_LIST_PANEL = "ALFONSO_RAFFLE_TICKET_CLAIMS_LIST_PANEL";
	public static final String ALFONSO_RAFFLE_TICKET_CLAIM_PANEL = "ALFONSO_RAFFLE_TICKET_CLAIM_PANEL";
	public static final String ALFONSO_RAFFLE_PARTICIPATING_ITEMS_PANEL = "ALFONSO_RAFFLE_PARTICIPATING_ITEMS_PANEL";
	public static final String TOP_SALES_BY_ITEM_REPORT_PANEL = "TOP_SALES_BY_ITEM_REPORT_PANEL";
	
    private static final Map<String, Class<? extends StandardMagicPanel>> panelClasses = new HashMap<>();
    
    static {
    	panelClasses.put(BAD_STOCK_INVENTORY_LIST_PANEL, BadStockInventoryListPanel.class);
    	panelClasses.put(BAD_STOCK_ADJUSTMENT_IN_LIST_PANEL, BadStockAdjustmentInListPanel.class);
    	panelClasses.put(BAD_STOCK_ADJUSTMENT_OUT_LIST_PANEL, BadStockAdjustmentOutListPanel.class);
    	panelClasses.put(BAD_STOCK_REPORT_LIST_PANEL, BadStockReportListPanel.class);
    	panelClasses.put(BAD_STOCK_CARD_INVENTORY_REPORT_PANEL, BadStockCardInventoryReportPanel.class);
    	panelClasses.put(BAD_STOCK_INVENTORY_CHECK_LIST_PANEL, BadStockInventoryCheckListPanel.class);
    }
    
    private AutowireCapableBeanFactory beanFactory;
    
	@Value("${application.title}")
	private String baseTitle;
	
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
	@Autowired private ReportsMenuPanel reportsPanel;
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
	@Autowired private PurchasePaymentCreditCardPaymentsReportPanel purchasePaymentCreditCardPaymentsReportPanel;
	@Autowired private SalesByManufacturerReportPanel salesByManufacturerReportPanel;
	@Autowired private CustomerCheckPaymentsReportPanel customerCheckPaymentsReportPanel;
	@Autowired private DisbursementReportPanel disbursementReportPanel;
	@Autowired private PromoRedemptionPanel promoRedemptionPanel;
	@Autowired private PromoRedemptionListPanel promoRedemptionListPanel;
	@Autowired private PromoRedemptionPromoListPanel promoRedemptionPromoListPanel;
	@Autowired private PromoListPanel promoListPanel;
	@Autowired private PromoPanel promoPanel;
	@Autowired private PromoPointsPanel promoPointsPanel;
	@Autowired private UnpaidCreditCardPaymentsListPanel unpaidCreditCardPaymentsListPanel;
	@Autowired private CreditCardStatementListPanel creditCardStatementListPanel;
	@Autowired private CreditCardStatementPanel creditCardStatementPanel;
	@Autowired private StockOfftakeReportPanel stockOfftakeReportPanel;
	@Autowired private SalesRequisitionSeparateItemsPanel salesRequisitionSeparateItemsPanel;
	@Autowired private PromoRedemptionRebatesPanel promoRedemptionRebatesPanel;
	@Autowired private UploadMaximumStockLevelChangesPanel uploadMaximumStockLevelChangesPanel;
	@Autowired private InventoryCorrectionListPanel inventoryCorrectionListPanel;
	@Autowired private InventoryCorrectionPanel inventoryCorrectionPanel;
	@Autowired private DailyProductQuantityDiscrepancyReportListPanel dailyProductQuantityDiscrepancyReportListPanel;
	@Autowired private ProductQuantityDiscrepancyReportPanel productQuantityDiscrepancyReportPanel;
	@Autowired private PilferageReportPanel pilferageReportPanel;
    @Autowired private ScheduledPriceChangesListPanel scheduledPriceChangesListPanel;
    @Autowired private EditProductPricesListPanel editProductPricesListPanel;
    @Autowired private EwtReportPanel ewtReportPanel;
    @Autowired private BirForm2307ReportListPanel birForm2307ReportListPanel;
    @Autowired private BirForm2307ReportPanel birForm2307ReportPanel;
    @Autowired private BadStockMenuPanel badStockMenuPanel;
    @Autowired private BadStockAdjustmentInPanel badStockAdjustmentInPanel;
    @Autowired private BadStockAdjustmentOutPanel badStockAdjustmentOutPanel;
    @Autowired private BadStockReportPanel badStockReportPanel;
    @Autowired private BadStockInventoryCheckPanel badStockInventoryCheckPanel;
	@Autowired private EcashReceiverListPanel ecashReceiverListPanel;
	@Autowired private MaintainEcashReceiverPanel maintainEcashReceiverPanel;
	@Autowired private EcashPaymentsReportPanel ecashPaymentsReportPanel;
	@Autowired private EcashPurchasePaymentsReportPanel ecashPurchasePaymentsReportPanel;
	@Autowired private PromoMenuPanel promoMenuPanel;
	@Autowired private JchsCellphoneRaffleMenuPanel jchsCellphoneRaffleMenuPanel;
	@Autowired private JchsCellphoneRaffleTicketsListPanel jchsCellphoneRaffleTicketsListPanel;
	@Autowired private JchsCellphoneRaffleTicketClaimsListPanel jchsCellphoneRaffleTicketClaimsListPanel;
	@Autowired private JchsCellphoneRaffleTicketClaimPanel jchsCellphoneRaffleTicketClaimPanel;
	@Autowired private JchsRaffleMenuPanel jchsRaffleMenuPanel;
	@Autowired private JchsRaffleTicketsListPanel jchsRaffleTicketsListPanel;
	@Autowired private JchsRaffleTicketClaimsListPanel jchsRaffleTicketClaimsListPanel;
	@Autowired private JchsRaffleTicketClaimPanel jchsRaffleTicketClaimPanel;
	@Autowired private AlfonsoRaffleMenuPanel alfonsoRaffleMenuPanel;
	@Autowired private AlfonsoRaffleTicketsListPanel alfonsoRaffleTicketsListPanel;
	@Autowired private AlfonsoRaffleTicketClaimsListPanel alfonsoRaffleTicketClaimsListPanel;
	@Autowired private AlfonsoRaffleTicketClaimPanel alfonsoRaffleTicketClaimPanel;
	@Autowired private AlfonsoRaffleParticipatingItemsPanel alfonsoRaffleParticipatingItemsPanel;
	@Autowired private TopSalesByItemReportPanel topSalesByItemReportPanel;
	
	@Autowired private SystemService systemParameterService;
	@Autowired private DataSource dataSource;
	@Autowired private OnStartUp onStartUp;
	
	private CardLayoutPanel panelHolder;
	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("application");
	
	public MagicFrame() {
		this.setSize(1024, 640);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
    @PostConstruct
	public void initialize() {
		if (!isDatabaseUp()) {
			JOptionPane.showMessageDialog(this, "Cannot connect to database", 
					"Error Message", JOptionPane.ERROR_MESSAGE);
			closeProgram();
		} else if (!isDatabaseVersionCorrect()) {
			logger.error("Program not up-to-date. Expected: " + getDatabaseVersionRequired());
			JOptionPane.showMessageDialog(this, "Program not up-to-date", 
					"Error Message", JOptionPane.ERROR_MESSAGE);
			closeProgram();
		} else {
            initializeBaseTitle();
			addPanels();
			if (ApplicationUtil.isServer()) {
	            onStartUp.fire();
			}
		}
	}
	
	private void initializeBaseTitle() {
	    if (ApplicationUtil.isServer()) {
	        baseTitle = baseTitle.replace("Magic", "Magic SERVER");
	    }
    }

    private String getDatabaseVersionRequired() {
		return resourceBundle.getString("db.version");
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

	private boolean isDatabaseVersionCorrect() {
		return getDatabaseVersionRequired().equals(systemParameterService.getDatabaseVersion());
	}

	private void addPanels() {
		panelHolder = new CardLayoutPanel();
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
		panelHolder.add(purchasePaymentCreditCardPaymentsReportPanel, 
				PURCHASE_PAYMENT_CREDIT_CARD_PAYMENTS_REPORT_PANEL);
		panelHolder.add(salesByManufacturerReportPanel, SALES_BY_MANUFACTURER_REPORT_PANEL);
		panelHolder.add(customerCheckPaymentsReportPanel, CUSTOMER_CHECK_PAYMENTS_REPORT_PANEL);
		panelHolder.add(disbursementReportPanel, DISBURSEMENT_REPORT_PANEL);
		panelHolder.add(promoRedemptionPanel, PROMO_REDEMPTION_PANEL);
		panelHolder.add(promoRedemptionListPanel, PROMO_REDEMPTION_LIST_PANEL);
		panelHolder.add(promoRedemptionPromoListPanel, PROMO_REDEMPTION_PROMO_LIST_PANEL);
		panelHolder.add(promoListPanel, PROMO_LIST_PANEL);
		panelHolder.add(promoPanel, PROMO_PANEL);
		panelHolder.add(promoPointsPanel, PROMO_POINTS_PANEL);
		panelHolder.add(unpaidCreditCardPaymentsListPanel, UNPAID_CREDIT_CARD_PAYMENTS_LIST_PANEL);
		panelHolder.add(creditCardStatementListPanel, CREDIT_CARD_STATEMENT_LIST_PANEL);
		panelHolder.add(creditCardStatementPanel, CREDIT_CARD_STATEMENT_PANEL);
		panelHolder.add(stockOfftakeReportPanel, STOCK_OFFTAKE_REPORT_PANEL);
		panelHolder.add(salesRequisitionSeparateItemsPanel, SALES_REQUISITION_SEPARATE_ITEMS_PANEL);
		panelHolder.add(promoRedemptionRebatesPanel, PROMO_REDEMPTION_REBATES_PANEL);
		panelHolder.add(uploadMaximumStockLevelChangesPanel, UPLOAD_MAXIMUM_STOCK_LEVEL_CHANGES_PANEL);
		panelHolder.add(inventoryCorrectionListPanel, INVENTORY_CORRECTION_LIST_PANEL);
		panelHolder.add(inventoryCorrectionPanel, INVENTORY_CORRECTION_PANEL);
		panelHolder.add(dailyProductQuantityDiscrepancyReportListPanel, 
				DAILY_PRODUCT_QUANTITY_DISCREPANCY_REPORT_LIST_PANEL);
		panelHolder.add(productQuantityDiscrepancyReportPanel, PRODUCT_QUANTITY_DISCREPANCY_REPORT_PANEL);
        panelHolder.add(pilferageReportPanel, PILFERAGE_REPORT_PANEL);
		panelHolder.add(scheduledPriceChangesListPanel, SCHEDULED_PRICE_CHANGES_LIST_PANEL);
        panelHolder.add(editProductPricesListPanel, EDIT_PRODUCT_PRICES_LIST_PANEL);
        panelHolder.add(ewtReportPanel, EWT_REPORT_PANEL);
        panelHolder.add(birForm2307ReportListPanel, BIR_FORM_2307_REPORT_LIST_PANEL);
        panelHolder.add(birForm2307ReportPanel, BIR_FORM_2307_REPORT_PANEL);
        panelHolder.add(badStockMenuPanel, BAD_STOCK_MENU_PANEL);
        panelHolder.add(badStockAdjustmentInPanel, BAD_STOCK_ADJUSTMENT_IN_PANEL);
        panelHolder.add(badStockAdjustmentOutPanel, BAD_STOCK_ADJUSTMENT_OUT_PANEL);
        panelHolder.add(badStockReportPanel, BAD_STOCK_REPORT_PANEL);
        panelHolder.add(badStockInventoryCheckPanel, BAD_STOCK_INVENTORY_CHECK_PANEL);
		panelHolder.add(ecashReceiverListPanel, ECASH_RECEIVER_LIST_PANEL);
		panelHolder.add(maintainEcashReceiverPanel, MAINTAIN_ECASH_RECEIVER_PANEL);
		panelHolder.add(ecashPaymentsReportPanel, ECASH_PAYMENTS_REPORT_PANEL);
		panelHolder.add(ecashPurchasePaymentsReportPanel, ECASH_PURCHASE_PAYMENTS_REPORT_PANEL);
		panelHolder.add(promoMenuPanel, PROMO_MENU_PANEL);
		panelHolder.add(jchsCellphoneRaffleMenuPanel, JCHS_CELLPHONE_RAFFLE_MENU_PANEL);
		panelHolder.add(jchsCellphoneRaffleTicketsListPanel, JCHS_CELLPHONE_RAFFLE_TICKETS_LIST_PANEL);
		panelHolder.add(jchsCellphoneRaffleTicketClaimsListPanel, JCHS_CELLPHONE_RAFFLE_TICKET_CLAIMS_LIST_PANEL);
		panelHolder.add(jchsCellphoneRaffleTicketClaimPanel, JCHS_CELLPHONE_RAFFLE_TICKET_CLAIM_PANEL);
		panelHolder.add(jchsRaffleMenuPanel, JCHS_RAFFLE_MENU_PANEL);
		panelHolder.add(jchsRaffleTicketsListPanel, JCHS_RAFFLE_TICKETS_LIST_PANEL);
		panelHolder.add(jchsRaffleTicketClaimsListPanel, JCHS_RAFFLE_TICKET_CLAIMS_LIST_PANEL);
		panelHolder.add(jchsRaffleTicketClaimPanel, JCHS_RAFFLE_TICKET_CLAIM_PANEL);
		panelHolder.add(alfonsoRaffleMenuPanel, ALFONSO_RAFFLE_MENU_PANEL);
		panelHolder.add(alfonsoRaffleTicketsListPanel, ALFONSO_RAFFLE_TICKETS_LIST_PANEL);
		panelHolder.add(alfonsoRaffleTicketClaimsListPanel, ALFONSO_RAFFLE_TICKET_CLAIMS_LIST_PANEL);
		panelHolder.add(alfonsoRaffleTicketClaimPanel, ALFONSO_RAFFLE_TICKET_CLAIM_PANEL);
		panelHolder.add(alfonsoRaffleParticipatingItemsPanel, ALFONSO_RAFFLE_PARTICIPATING_ITEMS_PANEL);
		panelHolder.add(topSalesByItemReportPanel, TOP_SALES_BY_ITEM_REPORT_PANEL);
        getContentPane().add(panelHolder);

        switchToLoginPanel();
	}
	
	public void switchToLoginPanel() {
		addPanelNameToTitle("Login");
		loginPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, LOGIN_PANEL);
	}
	
	public void switchToMainMenuPanel() {
		setTitle(baseTitle);
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
	
	public void addPanelNameToTitle(String panelName) {
		setTitle(baseTitle + " - " + panelName);
	}

	public void back(String panelName) {
		StandardMagicPanel panel = panelHolder.getCardPanel(panelName);
		panel.updateDisplayOnBack();
		addPanelNameToTitle(panel.getTitle());
		((CardLayout)panelHolder.getLayout()).show(panelHolder, panelName);
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

	public void switchToPurchasePaymentCreditCardPaymentsReportPanel() {
		addPanelNameToTitle("Purchase Payment Credit Card Payments Report");
		purchasePaymentCreditCardPaymentsReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, 
				PURCHASE_PAYMENT_CREDIT_CARD_PAYMENTS_REPORT_PANEL);
	}

	public void switchToSalesByManufacturerReportPanel() {
		addPanelNameToTitle("Sales By Manufacturer Report");
		salesByManufacturerReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, SALES_BY_MANUFACTURER_REPORT_PANEL);
	}
	
	public void switchToCustomerCheckPaymentsReportPanel() {
		addPanelNameToTitle("Customer Check Payments Report");
		customerCheckPaymentsReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, CUSTOMER_CHECK_PAYMENTS_REPORT_PANEL);
	}

	public void switchToDisbursementReportPanel() {
		addPanelNameToTitle("Disbursement Report");
		disbursementReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, DISBURSEMENT_REPORT_PANEL);
	}

	public void switchToPromoRedemptionPanel(PromoRedemption promoRedemption) {
		addPanelNameToTitle("Promo Redemption");
		promoRedemptionPanel.updateDisplay(promoRedemption);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PROMO_REDEMPTION_PANEL);
	}
	
	public void switchToPromoRedemptionListPanel(Promo promo) {
		addPanelNameToTitle("Promo Redemption List");
		promoRedemptionListPanel.updateDisplay(promo);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PROMO_REDEMPTION_LIST_PANEL);
	}
	
	public void switchToPromoRedemptionPromoListPanel() {
		addPanelNameToTitle("Promo Redemption - Select Promo");
		promoRedemptionPromoListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PROMO_REDEMPTION_PROMO_LIST_PANEL);
	}

	public void switchToPromoListPanel() {
		addPanelNameToTitle("Promo List");
		promoListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PROMO_LIST_PANEL);
	}

	public void switchToPromoPanel(Promo promo) {
		addPanelNameToTitle("Promo");
		promoPanel.updateDisplay(promo);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PROMO_PANEL);
	}
	
	public void switchToPromoPointsPanel(Promo promo) {
		addPanelNameToTitle("Promo Points");
		promoPointsPanel.updateDisplay(promo);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PROMO_POINTS_PANEL);
	}

	public void switchToUnpaidCreditCardPaymentsListPanel() {
		addPanelNameToTitle("Unpaid Credit Card Payments List");
		unpaidCreditCardPaymentsListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, UNPAID_CREDIT_CARD_PAYMENTS_LIST_PANEL);
	}

	public void switchToCreditCardStatementPanel(CreditCardStatement statement) {
		addPanelNameToTitle("Credit Card Statement");
		creditCardStatementPanel.updateDisplay(statement);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, CREDIT_CARD_STATEMENT_PANEL);
	}
	
	public void switchToCreditCardStatementListPanel() {
		addPanelNameToTitle("Credit Card Statement List");
		creditCardStatementListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, CREDIT_CARD_STATEMENT_LIST_PANEL);
	}

	public void switchToStockOfftakeReportPanel() {
		addPanelNameToTitle("Stock Offtake Report");
		stockOfftakeReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, STOCK_OFFTAKE_REPORT_PANEL);
	}

	public void switchToSalesRequisitionSeparateItemsPanel() {
		addPanelNameToTitle("Sales Requisition Separate Items");
		salesRequisitionSeparateItemsPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, SALES_REQUISITION_SEPARATE_ITEMS_PANEL);
	}

	public void switchToPromoRedemptionRebatesPanel(PromoRedemption promoRedemption) {
		addPanelNameToTitle("Promo Redemption Rebates");
		promoRedemptionRebatesPanel.updateDisplay(promoRedemption);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PROMO_REDEMPTION_REBATES_PANEL);
	}

	public void switchToUploadMaximumStockLevelChangesPanel() {
		addPanelNameToTitle("Upload Maximum Stock Level Changes");
		uploadMaximumStockLevelChangesPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, UPLOAD_MAXIMUM_STOCK_LEVEL_CHANGES_PANEL);
	}

	public void switchToInventoryCorrectionListPanel() {
		addPanelNameToTitle("Inventory Correction List");
		inventoryCorrectionListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, INVENTORY_CORRECTION_LIST_PANEL);
	}

	public void switchToInventoryCorrectionPanel(InventoryCorrection inventoryCorrection) {
		addPanelNameToTitle("Inventory Correction");
		inventoryCorrectionPanel.updateDisplay(inventoryCorrection);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, INVENTORY_CORRECTION_PANEL);
	}

	public void switchToDailyProductQuantityDiscrepancyReportListPanel() {
		addPanelNameToTitle("Daily Product Quantity Discrepancy Report List");
		dailyProductQuantityDiscrepancyReportListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, DAILY_PRODUCT_QUANTITY_DISCREPANCY_REPORT_LIST_PANEL);
	}

	public void switchToProductQuantityDiscrepancyReportPanel(ProductQuantityDiscrepancyReport report) {
		addPanelNameToTitle("Product Quantity Discrepancy Report");
		productQuantityDiscrepancyReportPanel.updateDisplay(report);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PRODUCT_QUANTITY_DISCREPANCY_REPORT_PANEL);
	}

	public void switchToPilferageReportPanel() {
		addPanelNameToTitle("Pilferage Report");
		pilferageReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PILFERAGE_REPORT_PANEL);
	}

    public void switchToScheduledPriceChangesListPanel() {
        addPanelNameToTitle("Scheduled Price Changes List");
        scheduledPriceChangesListPanel.updateDisplay();
        ((CardLayout)panelHolder.getLayout()).show(panelHolder, SCHEDULED_PRICE_CHANGES_LIST_PANEL);
    }

    public void switchToEditProductPricesListPanel() {
        addPanelNameToTitle("Edit Product Prices List");
        editProductPricesListPanel.updateDisplay();
        ((CardLayout)panelHolder.getLayout()).show(panelHolder, EDIT_PRODUCT_PRICES_LIST_PANEL);
    }

    public void switchToBadStockMenuPanel() {
        addPanelNameToTitle("Bad Stock Menu");
        badStockMenuPanel.updateDisplay();
        ((CardLayout)panelHolder.getLayout()).show(panelHolder, BAD_STOCK_MENU_PANEL);
    }

    public void switchToNewBadStockAdjustmentInPanel() {
        switchToBadStockAdjustmentInPanel(null);
    }
	
    public void switchToBadStockAdjustmentInPanel(BadStockAdjustmentIn adjustmentIn) {
        if (adjustmentIn == null) {
            adjustmentIn = new BadStockAdjustmentIn();
        }
        
        addPanelNameToTitle("Bad Stock Adjustment In");
        badStockAdjustmentInPanel.updateDisplay(adjustmentIn);
        ((CardLayout)panelHolder.getLayout()).show(panelHolder, BAD_STOCK_ADJUSTMENT_IN_PANEL);
    }

    public void switchPanel(String panelName) {
        StandardMagicPanel panel = panelHolder.getCardPanel(panelName);
        if (panel == null) {
        	panel = loadPanel(panelName);
        }
        addPanelNameToTitle(panel.getTitle());
        panel.updateDisplay();
        ((CardLayout)panelHolder.getLayout()).show(panelHolder, panelName);
    }

    public void switchToNewBadStockAdjustmentOutPanel() {
        switchToBadStockAdjustmentOutPanel(null);
    }
    
    public void switchToBadStockAdjustmentOutPanel(BadStockAdjustmentOut adjustmentOut) {
        if (adjustmentOut == null) {
            adjustmentOut = new BadStockAdjustmentOut();
        }
        
        addPanelNameToTitle("Bad Stock Adjustment Out");
        badStockAdjustmentOutPanel.updateDisplay(adjustmentOut);
        ((CardLayout)panelHolder.getLayout()).show(panelHolder, BAD_STOCK_ADJUSTMENT_OUT_PANEL);
    }
    
    public void switchToBirForm2307ReportPanel(BirForm2307Report report) {
        addPanelNameToTitle(birForm2307ReportPanel.getTitle());
        birForm2307ReportPanel.updateDisplay(report);
        ((CardLayout)panelHolder.getLayout()).show(panelHolder, BIR_FORM_2307_REPORT_PANEL);
    }

	public void switchToBadStockReportPanel(BadStockReport report) {
        addPanelNameToTitle(badStockReportPanel.getTitle());
        badStockReportPanel.updateDisplay(report);
        ((CardLayout)panelHolder.getLayout()).show(panelHolder, BAD_STOCK_REPORT_PANEL);
	}

	public void switchToBadStockInventoryCheckPanel(BadStockInventoryCheck badStockInventoryCheck) {
        addPanelNameToTitle(badStockInventoryCheckPanel.getTitle());
        badStockInventoryCheckPanel.updateDisplay(badStockInventoryCheck);
        ((CardLayout)panelHolder.getLayout()).show(panelHolder, BAD_STOCK_INVENTORY_CHECK_PANEL);
	}
	
	public void switchToEcashReceiverListPanel() {
		addPanelNameToTitle("E-Cash Receiver List");
		ecashReceiverListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, ECASH_RECEIVER_LIST_PANEL);
	}

	public void switchToAddEcashReceiverPanel() {
		addPanelNameToTitle("Add New E-Cash Receiver");
		switchToMaintainEcashReceiverPanel(new EcashReceiver());
	}
	
	public void switchToEditEcashReceiverPanel(EcashReceiver ecashReceiver) {
		addPanelNameToTitle("Edit E-Cash Receiver");
		switchToMaintainEcashReceiverPanel(ecashReceiver);
	}
	
	public void switchToMaintainEcashReceiverPanel(EcashReceiver ecashReceiver) {
		maintainEcashReceiverPanel.updateDisplay(ecashReceiver);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, MAINTAIN_ECASH_RECEIVER_PANEL);
	}
	
    private StandardMagicPanel loadPanel(String panelName) {
    	StandardMagicPanel panel = 
    			(StandardMagicPanel)beanFactory.autowire(panelClasses.get(panelName), AutowireCapableBeanFactory.AUTOWIRE_NO, false);
    	panel.afterPropertiesSet(); // TODO: Convert to InitializingBean 
    	panelHolder.add(panel, panelName);
    	logger.info("Initialized {}", panelName);
    	return panel;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		beanFactory = applicationContext.getAutowireCapableBeanFactory();
	}

	public void switchToPromoMenuPanel() {
		addPanelNameToTitle("Promo Menu");
		promoMenuPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, PROMO_MENU_PANEL);
	}

	public void switchToJchsRaffleMenuPanel() {
		addPanelNameToTitle("JCHS Raffle Menu");
		jchsRaffleMenuPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, JCHS_RAFFLE_MENU_PANEL);
	}

	public void switchToJchsRaffleTicketsListPanel() {
		addPanelNameToTitle("JCHS Raffle Tickets List");
		jchsRaffleTicketsListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, JCHS_RAFFLE_TICKETS_LIST_PANEL);
	}

	public void switchToJchsRaffleTicketClaimsListPanel() {
		addPanelNameToTitle("JCHS Raffle Ticket Claims List");
		jchsRaffleTicketClaimsListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, JCHS_RAFFLE_TICKET_CLAIMS_LIST_PANEL);
	}

	public void switchToJchsRaffleTicketClaimPanel(PromoRaffleTicketClaim claim) {
		addPanelNameToTitle("JCHS Raffle Ticket Claim");
		jchsRaffleTicketClaimPanel.updateDisplay(claim);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, JCHS_RAFFLE_TICKET_CLAIM_PANEL);
	}

	public void switchToAlfonsoRaffleMenuPanel() {
		addPanelNameToTitle("Alfonso Raffle Menu");
		alfonsoRaffleMenuPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, ALFONSO_RAFFLE_MENU_PANEL);
	}

	public void switchToAlfonsoRaffleTicketsListPanel() {
		addPanelNameToTitle("Alfonso Raffle Tickets List");
		alfonsoRaffleTicketsListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, ALFONSO_RAFFLE_TICKETS_LIST_PANEL);
	}

	public void switchToAlfonsoRaffleTicketClaimsListPanel() {
		addPanelNameToTitle("Alfonso Raffle Ticket Claims List");
		alfonsoRaffleTicketClaimsListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, ALFONSO_RAFFLE_TICKET_CLAIMS_LIST_PANEL);
	}

	public void switchToAlfonsoRaffleTicketClaimPanel(PromoRaffleTicketClaim claim) {
		addPanelNameToTitle("Alfonso Raffle Ticket Claim");
		alfonsoRaffleTicketClaimPanel.updateDisplay(claim);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, ALFONSO_RAFFLE_TICKET_CLAIM_PANEL);
	}

	public void switchToAlfonsoRaffleParticipatingItemsPanel() {
		addPanelNameToTitle("Alfonso Raffle Participating Items");
		alfonsoRaffleParticipatingItemsPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, ALFONSO_RAFFLE_PARTICIPATING_ITEMS_PANEL);
	}

	public void switchToTopSalesByItemReportPanel() {
		addPanelNameToTitle("Top Sales By Item Report");
		topSalesByItemReportPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, TOP_SALES_BY_ITEM_REPORT_PANEL);
	}

	public void switchToJchsCellphoneRaffleMenuPanel() {
		addPanelNameToTitle("JCHS Cellphone Raffle Menu");
		jchsCellphoneRaffleMenuPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, JCHS_CELLPHONE_RAFFLE_MENU_PANEL);
	}

	public void switchToJchsCellphoneRaffleTicketsListPanel() {
		addPanelNameToTitle("JCHS Cellphone Raffle Tickets List");
		jchsCellphoneRaffleTicketsListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, JCHS_CELLPHONE_RAFFLE_TICKETS_LIST_PANEL);
	}

	public void switchToJchsCellphoneRaffleTicketClaimsListPanel() {
		addPanelNameToTitle("JCHS Cellphone Raffle Ticket Claims List");
		jchsCellphoneRaffleTicketClaimsListPanel.updateDisplay();
		((CardLayout)panelHolder.getLayout()).show(panelHolder, JCHS_CELLPHONE_RAFFLE_TICKET_CLAIMS_LIST_PANEL);
	}

	public void switchToJchsCellphoneRaffleTicketClaimPanel(PromoRaffleTicketClaim claim) {
		addPanelNameToTitle("JCHS Cellphone Raffle Ticket Claim");
		jchsCellphoneRaffleTicketClaimPanel.updateDisplay(claim);
		((CardLayout)panelHolder.getLayout()).show(panelHolder, JCHS_CELLPHONE_RAFFLE_TICKET_CLAIM_PANEL);
	}

}