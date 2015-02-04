package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.PrintPreviewDialog;
import com.pj.magic.gui.dialog.SelectSupplierDialog;
import com.pj.magic.gui.tables.BadPurchaseReturnItemsTable;
import com.pj.magic.model.BadPurchaseReturn;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.BadPurchaseReturnService;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PrintService;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class BadPurchaseReturnPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(BadPurchaseReturnPanel.class);
	
	private static final String SAVE_SUPPLIER_ACTION_NAME = "SAVE_CUSTOMER_ACTION_NAME";
	private static final String OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME = 
			"OPEN_SELECT_CUSTOMER_DIALOG_ACTION_NAME";
	
	@Autowired private BadPurchaseReturnItemsTable itemsTable;
	@Autowired private BadPurchaseReturnService badPurchaseReturnService;
	@Autowired private SupplierService supplierService;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	@Autowired private PrintService printService;
	@Autowired private PrintPreviewDialog printPreviewDialog;
	@Autowired private LoginService loginService;
	
	private BadPurchaseReturn badPurchaseReturn;
	private JLabel badPurchaseReturnNumberField;
	private JTextField supplierCodeField;
	private JLabel supplierNameField;
	private JButton selectSupplierButton;
	private JLabel statusField;
	private JLabel postDateField;
	private JLabel postedByField;
	private MagicTextField remarksField;
	private JLabel totalItemsField;
	private JLabel totalAmountField;
	private JButton addItemButton;
	private JButton deleteItemButton;
	private JButton printPreviewButton;
	private JButton printButton;
	
	@Override
	protected void initializeComponents() {
		supplierCodeField = new MagicTextField();
		supplierNameField = new JLabel();
		
		selectSupplierButton = new EllipsisButton();
		selectSupplierButton.setToolTipText("Select Supplier (F5)");
		selectSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSupplier();
			}
		});;
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		remarksField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveRemarks();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(supplierCodeField);
		updateTotalAmountFieldWhenItemsTableChanges();
	}

	private void saveRemarks() {
		if (!remarksField.getText().equals(badPurchaseReturn.getRemarks())) {
			badPurchaseReturn.setRemarks(remarksField.getText());
			badPurchaseReturnService.save(badPurchaseReturn);
		}
	}
	
	private void selectSupplier() {
		selectSupplierDialog.searchSuppliers(supplierCodeField.getText());
		selectSupplierDialog.setVisible(true);
		
		Supplier supplier = selectSupplierDialog.getSelectedSupplier();
		if (supplier != null) {
			if (badPurchaseReturn.getSupplier() != null && badPurchaseReturn.getSupplier().equals(supplier)) {
				// skip saving since there is no change
				remarksField.requestFocusInWindow();
				return;
			} else {
				badPurchaseReturn.setSupplier(supplier);
				try {
					badPurchaseReturnService.save(badPurchaseReturn);
					updateDisplay(badPurchaseReturn);
					remarksField.requestFocusInWindow();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					showMessageForUnexpectedError();
				}
			}
		}
	}

	@Override
	protected void registerKeyBindings() {
		supplierCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_SUPPLIER_ACTION_NAME);
		supplierCodeField.getActionMap().put(SAVE_SUPPLIER_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSupplier();
			}
		});
		
		supplierCodeField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME);
		supplierCodeField.getActionMap().put(OPEN_SELECT_SUPPLIER_DIALOG_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSupplier();
			}
		});
		
		remarksField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.highlight();
			}
		});
	}

	private void saveSupplier() {
		if (badPurchaseReturn.getSupplier() != null) {
			if (badPurchaseReturn.getSupplier().getCode().equals(supplierCodeField.getText())) {
				// skip saving since there is no change in supplier
				remarksField.requestFocusInWindow();
				return;
			}
		}
		
		if (StringUtils.isEmpty(supplierCodeField.getText())) {
			showErrorMessage("Supplier must be specified");
			return;
		}
		
		Supplier supplier = supplierService.findSupplierByCode(supplierCodeField.getText());
		if (supplier == null) {
			showErrorMessage("No supplier matching code specified");
			return;
		} else {
			badPurchaseReturn.setSupplier(supplier);
			try {
				badPurchaseReturnService.save(badPurchaseReturn);
				updateDisplay(badPurchaseReturn);
				remarksField.requestFocusInWindow();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showMessageForUnexpectedError();
			}
		}
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().switchToBadPurchaseReturnListPanel();
	}
	
	private void updateTotalAmountFieldWhenItemsTableChanges() {
		itemsTable.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				totalItemsField.setText(String.valueOf(badPurchaseReturn.getTotalItems()));
				totalAmountField.setText(FormatterUtil.formatAmount(badPurchaseReturn.getTotalAmount()));
			}
		});
	}

	public void updateDisplay(BadPurchaseReturn badPurchaseReturn) {
		if (badPurchaseReturn.getId() == null) {
			this.badPurchaseReturn = badPurchaseReturn;
			clearDisplay();
			return;
		}
		
		this.badPurchaseReturn = badPurchaseReturn = badPurchaseReturnService.getBadPurchaseReturn(badPurchaseReturn.getId());
		
		badPurchaseReturnNumberField.setText(badPurchaseReturn.getBadPurchaseReturnNumber().toString());
		supplierCodeField.setText(badPurchaseReturn.getSupplier().getCode());
		supplierCodeField.setEnabled(!badPurchaseReturn.isPosted());
		supplierNameField.setText(badPurchaseReturn.getSupplier().getName());
		selectSupplierButton.setEnabled(!badPurchaseReturn.isPosted());
		statusField.setText(badPurchaseReturn.getStatus());
		if (badPurchaseReturn.getPostDate() != null) {
			postDateField.setText(FormatterUtil.formatDate(badPurchaseReturn.getPostDate()));
		} else {
			postDateField.setText(null);
		}
		if (badPurchaseReturn.getPostedBy() != null) {
			postedByField.setText(badPurchaseReturn.getPostedBy().getUsername());
		} else {
			postedByField.setText(null);
		}
		remarksField.setText(badPurchaseReturn.getRemarks());
		remarksField.setEnabled(!badPurchaseReturn.isPosted());
		totalItemsField.setText(String.valueOf(badPurchaseReturn.getTotalItems()));
		totalAmountField.setText(badPurchaseReturn.getTotalAmount().toString());
		addItemButton.setEnabled(!badPurchaseReturn.isPosted());
		deleteItemButton.setEnabled(!badPurchaseReturn.isPosted());
		printPreviewButton.setEnabled(true);
		printButton.setEnabled(true);
		
		itemsTable.setBadPurchaseReturn(badPurchaseReturn);
	}

	private void clearDisplay() {
		badPurchaseReturnNumberField.setText(null);
		supplierCodeField.setText(null);
		supplierCodeField.setEnabled(true);
		supplierNameField.setText(null);
		selectSupplierButton.setEnabled(true);
		statusField.setText(null);
		postDateField.setText(null);
		postedByField.setText(null);
		remarksField.setText(null);
		remarksField.setEnabled(false);
		totalItemsField.setText(null);
		totalAmountField.setText(null);
		itemsTable.setBadPurchaseReturn(badPurchaseReturn);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
		printPreviewButton.setEnabled(false);
		printButton.setEnabled(false);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "PRBS No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		badPurchaseReturnNumberField = ComponentUtil.createLabel(200, "");
		mainPanel.add(badPurchaseReturnNumberField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusField = ComponentUtil.createLabel(150);
		mainPanel.add(statusField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Supplier:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createSupplierPanel(), c);

		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Post Date:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postDateField = ComponentUtil.createLabel(100, "");
		mainPanel.add(postDateField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Posted By:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		postedByField = ComponentUtil.createLabel(100, "");
		mainPanel.add(postedByField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Remarks:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(300, 25));
		mainPanel.add(remarksField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}
	
	private JPanel createSupplierPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierCodeField.setPreferredSize(new Dimension(100, 25));
		panel.add(supplierCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectSupplierButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectSupplierButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(Box.createHorizontalStrut(10), c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierNameField.setPreferredSize(new Dimension(200, 20));
		panel.add(supplierNameField, c);
		
		return panel;
	}
	
	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(60);
		panel.add(totalItemsField, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		panel.add(Box.createHorizontalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(120, "Total Amount:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		totalAmountField = ComponentUtil.createLabel(100);
		panel.add(totalAmountField, c);
		
		return panel;
	}
	
	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		addItemButton = new MagicToolBarButton("plus_small", "Add Item", true);
		addItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.switchToAddMode();
			}
		});
		panel.add(addItemButton, BorderLayout.WEST);
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item", true);
		deleteItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		printPreviewButton = new MagicToolBarButton("print_preview", "Print Preview");
		printPreviewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				printPreviewDialog.updateDisplay(printService.generateReportAsString(badPurchaseReturn));
//				printPreviewDialog.setVisible(true);
			}
		});
//		toolBar.add(printPreviewButton);
		
		printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				printService.print(badPurchaseReturn);
			}
		});
//		toolBar.add(printButton);
	}

}