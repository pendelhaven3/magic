package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.CustomAction;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.PromoRaffleTicketClaim;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.util.FormatterUtil;

@Component
public class JchsRaffleTicketClaimsListPanel extends StandardMagicPanel {

	private static final int CLAIM_ID_COLUMN_INDEX = 0;
	private static final int CUSTOMER_COLUMN_INDEX = 1;
	private static final int TRANSACTION_DATE_COLUMN_INDEX = 2;
	private static final int NUMBER_OF_TICKETS_COLUMN_INDEX = 3;
	private static final int CLAIM_DATE_COLUMN_INDEX = 4;
	private static final int PROCESSED_BY_COLUMN_INDEX = 5;
	
	@Autowired
	private PromoService promoService;
	
	private MagicListTable table;
	private TicketClaimsTableModel tableModel = new TicketClaimsTableModel();
	
	public void updateDisplay() {
		tableModel.setItems(promoService.getAllJchsRaffleTicketClaims());
		if (tableModel.hasItems()) {
			table.selectFirstRow();
		}
	}

	@Override
	protected void initializeComponents() {
		table = new MagicListTable(tableModel);
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
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
		getMagicFrame().switchToJchsRaffleTicketClaimPanel(claim);
	}

	private void switchToNewTicketClaimPanel() {
		getMagicFrame().switchToJchsRaffleTicketClaimPanel(new PromoRaffleTicketClaim());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToJchsRaffleMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New", e -> switchToNewTicketClaimPanel());
		toolBar.add(postButton);
	}

	private class TicketClaimsTableModel extends ListBackedTableModel<PromoRaffleTicketClaim>{

		private final String[] columnNames = {"Claim ID", "Customer", "Transaction Date", "No. of Tickets", "Claim Date", "Processed By"};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PromoRaffleTicketClaim claim = getItem(rowIndex);
			switch (columnIndex) {
			case CLAIM_ID_COLUMN_INDEX:
				return String.valueOf(claim.getId());
			case CUSTOMER_COLUMN_INDEX:
				return claim.getCustomer().getName();
			case TRANSACTION_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(claim.getTransactionDate());
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
