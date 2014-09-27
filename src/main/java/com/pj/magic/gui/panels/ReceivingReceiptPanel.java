package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.ReceivingReceiptItemsTable;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.service.PaymentTermService;
import com.pj.magic.service.PricingSchemeService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.ProductService;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class ReceivingReceiptPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(ReceivingReceiptPanel.class);
	
	@Autowired private ReceivingReceiptItemsTable itemsTable;
	@Autowired private ProductService productService;
	@Autowired private ReceivingReceiptService receivingReceiptService;
	@Autowired private PricingSchemeService pricingSchemeService;
	@Autowired private PaymentTermService paymentTermService;
	@Autowired private PrintService printService;
	
	private ReceivingReceipt receivingReceipt;
	private JLabel receivingReceiptNumberField;
	private JLabel relatedPurchaseOrderNumberField;
	private JLabel supplierField;
	private JLabel orderDateField;
	private JLabel paymentTermField;
	private UtilCalendarModel receivedDateModel;
	private JLabel referenceNumberField;
	private JLabel totalAmountField;
	private JLabel totalDiscountedAmountField;
	private JLabel totalNetAmountField;
	private MagicToolBarButton postButton;
	
	@Override
	protected void initializeComponents() {
		supplierField = new JLabel();
		paymentTermField = new JLabel();
		referenceNumberField = new JLabel();
		receivedDateModel = new UtilCalendarModel();
		receivedDateModel.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("value".equals(evt.getPropertyName()) && evt.getOldValue() != null 
						&& evt.getNewValue() != null) {
					receivingReceiptService.save(receivingReceipt);
				}
			}
		});
		
		focusOnItemsTableWhenThisPanelIsDisplayed();
		updateTotalAmountFieldWhenItemsTableChanges();
	}

	private void focusOnItemsTableWhenThisPanelIsDisplayed() {
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				itemsTable.highlight();
			}
		});
	}

	@Override
	protected void registerKeyBindings() {
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToReceivingReceiptListPanel();
	}
	
	private void updateTotalAmountFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalAmountField.setText(FormatterUtil.formatAmount(receivingReceipt.getTotalAmount()));
				totalDiscountedAmountField.setText(
						FormatterUtil.formatAmount(receivingReceipt.getTotalDiscountedAmount()));
				totalNetAmountField.setText(FormatterUtil.formatAmount(receivingReceipt.getTotalNetAmount()));
			}
		});
	}

	public void updateDisplay(ReceivingReceipt receivingReceipt) {
		this.receivingReceipt = receivingReceiptService.getReceivingReceipt(receivingReceipt.getId());
		receivingReceipt = this.receivingReceipt;
		
		receivingReceiptNumberField.setText(receivingReceipt.getReceivingReceiptNumber().toString());
		relatedPurchaseOrderNumberField.setText(receivingReceipt.getRelatedPurchaseOrderNumber().toString());
		supplierField.setText(receivingReceipt.getSupplier().getName());
		orderDateField.setText(FormatterUtil.formatDate(receivingReceipt.getOrderDate()));
		paymentTermField.setText(receivingReceipt.getPaymentTerm().getName());
		updateReceivedDateField();
		referenceNumberField.setText(receivingReceipt.getReferenceNumber());
		totalAmountField.setText(FormatterUtil.formatAmount(receivingReceipt.getTotalAmount()));
		totalDiscountedAmountField.setText(
				FormatterUtil.formatAmount(receivingReceipt.getTotalDiscountedAmount()));
		totalNetAmountField.setText(FormatterUtil.formatAmount(receivingReceipt.getTotalNetAmount()));
		itemsTable.setReceivingReceipt(receivingReceipt);
		
		postButton.setEnabled(!receivingReceipt.isPosted());
	}

	private void updateReceivedDateField() {
		receivedDateModel.setValue(null); // set to null first to prevent property change listener from triggering
		receivedDateModel.setValue(DateUtils.toCalendar(receivingReceipt.getReceivedDate()));
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 30), c);

		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "RR No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		receivingReceiptNumberField = ComponentUtil.createLabel(200, "");
		mainPanel.add(receivingReceiptNumberField, c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 1), c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Related PO No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		relatedPurchaseOrderNumberField = ComponentUtil.createLabel(100, "");
		mainPanel.add(relatedPurchaseOrderNumberField, c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 6;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(1, 1), c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Supplier:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(supplierField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Order Date:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		orderDateField = ComponentUtil.createLabel(150, "");
		mainPanel.add(orderDateField, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Payment Term:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(paymentTermField, c);
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Received Date:"), c);
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;

		JDatePanelImpl datePanel = new JDatePanelImpl(receivedDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Reference No.:"), c);
		
		c.weightx = c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		referenceNumberField = ComponentUtil.createLabel(150, "");
		mainPanel.add(referenceNumberField, c);

		currentRow++;
		
		c.weightx = c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createFiller(50, 10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 7;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}
	
	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createRightLabel(70, "");
		panel.add(totalAmountField, c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(ComponentUtil.createFiller(50, 1), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Disc. Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalDiscountedAmountField = ComponentUtil.createRightLabel(70, "");
		panel.add(totalDiscountedAmountField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Net Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalNetAmountField = ComponentUtil.createRightLabel(70, "");
		panel.add(totalNetAmountField, c);
		
		return panel;
	}

	private void postReceivingReceipt() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		
		if (confirm("Do you want to post this Receiving Receipt?")) {
			try {
				receivingReceiptService.post(receivingReceipt);
				showMessage("Post successful!");
				updateDisplay(receivingReceipt);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
				updateDisplay(receivingReceipt);
			}
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				postReceivingReceipt();
			}
		});
		toolBar.add(postButton);
		
		MagicToolBarButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printReceivingReceipt();
			}
		});
		toolBar.add(printButton);
	}

	protected void printReceivingReceipt() {
		int confirm = JOptionPane.showConfirmDialog(this, "Include discount details?", "Print Receiving Receipt", JOptionPane.YES_NO_CANCEL_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			printService.print(receivingReceipt, true);
		} else if (confirm == JOptionPane.NO_OPTION) {
			printService.print(receivingReceipt, false);
		}
	}

}
