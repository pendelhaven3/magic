package com.pj.magic.gui.panels.promo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.CustomAction;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.panels.StandardMagicPanel;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRaffleTicketClaim;
import com.pj.magic.model.search.RaffleTicketSearchCriteria;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.service.impl.PromoServiceImpl;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class AlfonsoRaffleTicketClaimsListPanel extends StandardMagicPanel {

	private static final int CLAIM_ID_COLUMN_INDEX = 0;
	private static final int CUSTOMER_COLUMN_INDEX = 1;
	private static final int TRANSACTION_DATE_FROM_COLUMN_INDEX = 2;
	private static final int TRANSACTION_DATE_TO_COLUMN_INDEX = 3;
	private static final int NUMBER_OF_TICKETS_COLUMN_INDEX = 4;
	private static final int CLAIM_DATE_COLUMN_INDEX = 5;
	private static final int PROCESSED_BY_COLUMN_INDEX = 6;
	
	@Autowired private PromoService promoService;	
	@Autowired private CustomerService customerService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	
	private MagicTextField customerCodeField;
	private JLabel customerNameField;
	private MagicButton searchButton;
	private JButton selectCustomerButton;
	
	private MagicListTable table;
	private TicketClaimsTableModel tableModel = new TicketClaimsTableModel();
	
	public void updateDisplay() {
		customerCodeField.setText(null);
		customerNameField.setText(null);
		
		tableModel.setItems(promoService.getAllAlfonsoRaffleTicketClaims());
		if (tableModel.hasItems()) {
			table.selectFirstRow();
		}
	}

	@Override
	protected void initializeComponents() {
		customerCodeField = new MagicTextField();
		customerCodeField.setMaximumLength(Constants.CUSTOMER_CODE_MAXIMUM_LENGTH);
		
		selectCustomerButton = new EllipsisButton();
		selectCustomerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCustomer();
			}
		});
		
		searchButton = new MagicButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchClaims();
			}
		});
		
		table = new MagicListTable(tableModel);
		
		focusOnComponentWhenThisPanelIsDisplayed(customerCodeField);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets.top = 5;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Customer:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 4;
		mainPanel.add(createCustomerPanel(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 5;
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		searchButton.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(searchButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 15;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridwidth = 6;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
	}

	private JPanel createCustomerPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		customerCodeField.setPreferredSize(new Dimension(120, 25));
		panel.add(customerCodeField, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		selectCustomerButton.setPreferredSize(new Dimension(30, 24));
		panel.add(selectCustomerButton, c);
		
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
		customerNameField = ComponentUtil.createLabel(200);
		panel.add(customerNameField, c);
		
		return panel;
	}
	
	@Override
	protected void registerKeyBindings() {
		customerCodeField.onF5Key(() -> selectCustomer());
		searchButton.onEnterKey(() -> searchClaims());
		
		table.onEnterKeyAndDoubleClick(new CustomAction() {
			
			@Override
			public void doAction() {
				selectTicketClaim();
			}
		});
		
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
	}

	protected void selectTicketClaim() {
		PromoRaffleTicketClaim claim = tableModel.getItem(table.getSelectedRow());
		getMagicFrame().switchToAlfonsoRaffleTicketClaimPanel(claim);
	}

	private void switchToNewTicketClaimPanel() {
		getMagicFrame().switchToAlfonsoRaffleTicketClaimPanel(new PromoRaffleTicketClaim());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToAlfonsoRaffleMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New", e -> switchToNewTicketClaimPanel());
		toolBar.add(postButton);
	}

	private void selectCustomer() {
		selectCustomerDialog.searchCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			customerCodeField.setText(customer.getCode());
			customerNameField.setText(customer.getName());
			searchButton.requestFocusInWindow();
		}
	}
	
	private void searchClaims() {
		RaffleTicketSearchCriteria criteria = new RaffleTicketSearchCriteria();
		criteria.setPromo(new Promo(PromoServiceImpl.ALFONSO_RAFFLE_PROMO_ID));
		
		Customer customer = null;
		if (!customerCodeField.isEmpty()) {
			customer = customerService.findCustomerByCode(customerCodeField.getText());
		}
		
		if (customer != null) {
			tableModel.setItems(promoService.findAllAlfonsoRaffleTicketClaimsByCustomer(customer));
		} else {
			tableModel.setItems(promoService.getAllAlfonsoRaffleTicketClaims());
		}
		
		if (tableModel.hasItems()) {
			table.selectFirstRow();
		}
	}
	
	private class TicketClaimsTableModel extends ListBackedTableModel<PromoRaffleTicketClaim>{

		private final String[] columnNames = {"Claim ID", "Customer", "Transaction Date From", "Transaction Date To", "No. of Tickets", "Claim Date", "Processed By"};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PromoRaffleTicketClaim claim = getItem(rowIndex);
			switch (columnIndex) {
			case CLAIM_ID_COLUMN_INDEX:
				return String.valueOf(claim.getId());
			case CUSTOMER_COLUMN_INDEX:
				return claim.getCustomer().getName();
			case TRANSACTION_DATE_FROM_COLUMN_INDEX:
				return FormatterUtil.formatDate(claim.getTransactionDateFrom());
			case TRANSACTION_DATE_TO_COLUMN_INDEX:
				return FormatterUtil.formatDate(claim.getTransactionDateTo());
			case NUMBER_OF_TICKETS_COLUMN_INDEX:
				return claim.getNumberOfTickets();
			case CLAIM_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDateTime(claim.getClaimDate());
			case PROCESSED_BY_COLUMN_INDEX:
				return claim.getProcessedBy().getUsername();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}

		@Override
		protected String[] getColumnNames() {
			return columnNames;
		}

	}
	
}
