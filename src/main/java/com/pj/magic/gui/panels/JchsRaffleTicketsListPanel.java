package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.component.EllipsisButton;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.dialog.SelectCustomerDialog;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRaffleTicket;
import com.pj.magic.model.search.RaffleTicketSearchCriteria;
import com.pj.magic.service.CustomerService;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.service.impl.PromoServiceImpl;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

@Component
public class JchsRaffleTicketsListPanel extends StandardMagicPanel {

	private static final int TICKET_NUMBER_COLUMN_INDEX = 0;
	private static final int CUSTOMER_COLUMN_INDEX = 1;
	private static final int CLAIM_DATE_COLUMN_INDEX = 2;
	
	@Autowired private PromoService promoService;
	@Autowired private CustomerService customerService;
	@Autowired private SelectCustomerDialog selectCustomerDialog;
	
	private MagicTextField ticketNumberField;
	private MagicTextField customerCodeField;
	private JLabel customerNameField;
	private JButton searchButton;
	private JButton selectCustomerButton;
	
	private MagicListTable table;
	private TicketsTableModel tableModel = new TicketsTableModel();
	
	public void updateDisplay() {
		customerCodeField.setText(null);
		customerNameField.setText(null);
		ticketNumberField.setText(null);
		
		List<PromoRaffleTicket> tickets = promoService.getAllJchsRaffleTickets();
		tableModel.setItems(tickets);
		if (!tickets.isEmpty()) {
			table.changeSelection(0, 0, false, false);
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
				openSelectCustomerDialog();
			}
		});
		
		ticketNumberField = new MagicTextField();
		ticketNumberField.setNumbersOnly(true);
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchTickets();
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
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "Ticket Number:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 4;
		ticketNumberField.setPreferredSize(new Dimension(120, 25));
		mainPanel.add(ticketNumberField, c);
		
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
		c.gridwidth = 3;
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
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToJchsRaffleMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}
	
	private void openSelectCustomerDialog() {
		selectCustomerDialog.searchCustomers(customerCodeField.getText());
		selectCustomerDialog.setVisible(true);
		
		Customer customer = selectCustomerDialog.getSelectedCustomer();
		if (customer != null) {
			customerCodeField.setText(customer.getCode());
			customerNameField.setText(customer.getName());
		}
	}
	
	private void searchTickets() {
		if (!ticketNumberField.isEmpty() && !NumberUtil.isAmount(ticketNumberField.getText())) {
			showErrorMessage("Ticket Number must be a valid number");
			ticketNumberField.requestFocusInWindow();
			return;
		}
		
		RaffleTicketSearchCriteria criteria = new RaffleTicketSearchCriteria();
		criteria.setPromo(new Promo(PromoServiceImpl.JCHS_RAFFLE_PROMO_ID));
		if (!customerCodeField.isEmpty()) {
			criteria.setCustomer(customerService.findCustomerByCode(customerCodeField.getText()));
		}
		if (!ticketNumberField.isEmpty()) {
			criteria.setTicketNumber(ticketNumberField.getTextAsInteger());
		}
		
		List<PromoRaffleTicket> tickets = promoService.searchJchsRaffleTickets(criteria);
		tableModel.setItems(tickets);
		if (!tickets.isEmpty()) {
			table.changeSelection(0, 0, false, false);
			table.requestFocusInWindow();
		} else {
			showMessage("No matching records");
		}
	}

	private class TicketsTableModel extends ListBackedTableModel<PromoRaffleTicket>{

		private final String[] columnNames = {"Ticket Number", "Customer", "Claim Date"};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PromoRaffleTicket ticket = getItem(rowIndex);
			switch (columnIndex) {
			case TICKET_NUMBER_COLUMN_INDEX:
				return ticket.getTicketNumberDisplay();
			case CUSTOMER_COLUMN_INDEX:
				return ticket.getCustomer().getName();
			case CLAIM_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDateTime(ticket.getClaimDate());
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
