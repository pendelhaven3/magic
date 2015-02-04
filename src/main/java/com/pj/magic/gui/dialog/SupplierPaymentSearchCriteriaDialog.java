package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicComboBox;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.SupplierPaymentSearchCriteria;
import com.pj.magic.service.SupplierService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.KeyUtil;

@Component
public class SupplierPaymentSearchCriteriaDialog extends MagicDialog {

	private static final int STATUS_NEW = 1;
	private static final int STATUS_POSTED = 2;
	private static final int STATUS_CANCELLED = 3;
	
	@Autowired private SupplierService supplierService;
	@Autowired private SelectSupplierDialog selectSupplierDialog;
	
	private MagicTextField supplierPaymentNumberField;
	private MagicTextField supplierCodeField;
	private JLabel supplierNameField;
	private MagicComboBox<String> statusComboBox;
	private UtilCalendarModel postDateModel;
	private JButton searchButton;
	private SupplierPaymentSearchCriteria searchCriteria;
	private JButton selectSupplierButton;
	
	public SupplierPaymentSearchCriteriaDialog() {
		setSize(600, 250);
		setLocationRelativeTo(null);
		setTitle("Search Supplier Payments");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		supplierPaymentNumberField = new MagicTextField();
		
		supplierCodeField = new MagicTextField();
		supplierCodeField.setMaximumLength(Constants.SUPPLIER_CODE_MAXIMUM_LENGTH);
		
		selectSupplierButton = new EllipsisButton("Select Supplier");
		selectSupplierButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openSelectSupplierDialog();
			}
		});
		
		statusComboBox = new MagicComboBox<>();
		statusComboBox.setModel(new DefaultComboBoxModel<>(new String[] {"All", "New", "Posted"}));
		
		postDateModel = new UtilCalendarModel();
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSearchCriteria();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(supplierPaymentNumberField);
	}

	private void openSelectSupplierDialog() {
		selectSupplierDialog.searchSuppliers(supplierCodeField.getText());
		selectSupplierDialog.setVisible(true);
		
		Supplier supplier = selectSupplierDialog.getSelectedSupplier();
		if (supplier != null) {
			supplierCodeField.setText(supplier.getCode());
			supplierNameField.setText(supplier.getName());
		}
	}

	private void saveSearchCriteria() {
		searchCriteria = new SupplierPaymentSearchCriteria();
		
		if (!StringUtils.isEmpty(supplierPaymentNumberField.getText())) {
			searchCriteria.setPaymentNumber(Long.valueOf(supplierPaymentNumberField.getText()));
		}
		
		Supplier supplier = supplierService.findSupplierByCode(supplierCodeField.getText());
		searchCriteria.setSupplier(supplier);
		if (supplier != null) {
			supplierNameField.setText(supplier.getName());
		} else {
			supplierNameField.setText(null);
		}
		
		if (statusComboBox.getSelectedIndex() != 0) {
			switch (statusComboBox.getSelectedIndex()) {
			case STATUS_NEW:
				searchCriteria.setPosted(false);
				searchCriteria.setCancelled(false);
				break;
			case STATUS_POSTED:
				searchCriteria.setPosted(true);
				break;
			case STATUS_CANCELLED:
				searchCriteria.setCancelled(true);
				break;
			}
		}

		if (postDateModel.getValue() != null) {
			searchCriteria.setPostDate(postDateModel.getValue().getTime());
		}
		
		setVisible(false);
	}

	private void registerKeyBindings() {
		supplierPaymentNumberField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				supplierCodeField.requestFocusInWindow();
			}
		});
		
		supplierCodeField.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		supplierCodeField.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchButton.requestFocusInWindow();
			}
		});
		
		searchButton.getInputMap().put(KeyUtil.getEnterKey(), "enter");
		searchButton.getActionMap().put("enter", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveSearchCriteria();
			}
		});
		
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		// nothing
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(180, "Supplier Payment No.:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		supplierPaymentNumberField.setPreferredSize(new Dimension(100, 25));
		add(supplierPaymentNumberField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Supplier:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(createCustomerPanel(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(120, "Status:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		statusComboBox.setPreferredSize(new Dimension(150, 25));
		add(statusComboBox, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(140, "Post Date:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(postDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		add(datePicker, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		searchButton.setPreferredSize(new Dimension(100, 25));
		add(searchButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0; // bottom space filler
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(), c);
	}
	
	public SupplierPaymentSearchCriteria getSearchCriteria() {
		SupplierPaymentSearchCriteria returnCriteria = searchCriteria;
		searchCriteria = null;
		return returnCriteria;
	}
	
	public void updateDisplay() {
		searchCriteria = null;
		supplierPaymentNumberField.setText(null);
		supplierCodeField.setText(null);
		supplierNameField.setText(null);
		statusComboBox.setSelectedIndex(0);
		postDateModel.setValue(null);
	}
	
	private JPanel createCustomerPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierCodeField.setPreferredSize(new Dimension(120, 25));
		panel.add(supplierCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectSupplierButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectSupplierButton, c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createFiller(10, 20), c);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		supplierNameField = ComponentUtil.createLabel(200);
		panel.add(supplierNameField, c);
		
		return panel;
	}
	
}