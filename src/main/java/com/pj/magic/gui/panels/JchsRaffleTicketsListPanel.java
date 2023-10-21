package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.PromoRaffleTicket;
import com.pj.magic.service.impl.PromoService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class JchsRaffleTicketsListPanel extends StandardMagicPanel {

	private static final int TICKET_NUMBER_COLUMN_INDEX = 0;
	private static final int CUSTOMER_COLUMN_INDEX = 1;
	private static final int CLAIM_DATE_COLUMN_INDEX = 2;
	
	@Autowired private PromoService promoService;
	
	private JTable table;
	private TicketsTableModel tableModel = new TicketsTableModel();
	
	public void updateDisplay() {
		List<PromoRaffleTicket> tickets = promoService.getAllJchsRaffleTickets();
		tableModel.setItems(tickets);
		if (!tickets.isEmpty()) {
			table.changeSelection(0, 0, false, false);
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
		
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(new JScrollPane(table), c);
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
