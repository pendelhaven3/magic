package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.CustomAction;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.CreditCard;
import com.pj.magic.service.CreditCardService;

@Component
public class CreditCardPaymentListPanel extends StandardMagicPanel {

	private static final int CREDIT_CARD_COLUMN_INDEX = 0;
	
	@Autowired private CreditCardService creditCardService;
	
	private MagicListTable table;
	private CreditCardTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		tableModel = new CreditCardTableModel();
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
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	@Override
	protected void registerKeyBindings() {
		onEscapeKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doOnBack();
			}
		});
		
		table.onEnterKeyAndDoubleClick(new CustomAction() {
			
			@Override
			public void doAction() {
				selectCreditCard();
			}
		});
	}

	private void selectCreditCard() {
		CreditCard creditCard = tableModel.getCreditCard(table.getSelectedRow());
		getMagicFrame().switchToCreditCardPaymentPanel(creditCard);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasePaymentsMenuPanel();
	}

	public void updateDisplay() {
		tableModel.setCreditCards(creditCardService.getAllCreditCards());
		table.selectFirstRow();
	}
	
	private class CreditCardTableModel extends AbstractTableModel {

		private final String[] COLUMN_NAMES = {"Credit Card"};
		
		private List<CreditCard> creditCards = new ArrayList<>();
		
		public void setCreditCards(List<CreditCard> creditCards) {
			this.creditCards = creditCards;
			fireTableDataChanged();
		}
		
		public CreditCard getCreditCard(int rowIndex) {
			return creditCards.get(rowIndex);
		}

		@Override
		public int getRowCount() {
			return creditCards.size();
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			CreditCard creditCard = creditCards.get(rowIndex);
			switch (columnIndex) {
			case CREDIT_CARD_COLUMN_INDEX:
				return creditCard;
			default:
				throw new RuntimeException("Fetching invalid column index:" + columnIndex);
			}
		}
		
	}
	
}
