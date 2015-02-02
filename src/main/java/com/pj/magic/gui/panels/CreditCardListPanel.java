package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.CreditCard;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.util.ComponentUtil;

@Component
public class CreditCardListPanel extends StandardMagicPanel {

	private static final int USER_COLUMN_INDEX = 0;
	private static final int BANK_COLUMN_INDEX = 1;
	private static final int CARD_NUMBER_COLUMN_INDEX = 2;
	
	@Autowired private CreditCardService creditCardService;
	
	private MagicListTable table;
	private CreditCardsTableModel tableModel = new CreditCardsTableModel();
	
	public void updateDisplay() {
		List<CreditCard> creditCards = creditCardService.getAllCreditCards();
		tableModel.setCreditCards(creditCards);
		if (!creditCards.isEmpty()) {
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
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectCreditCard();
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					selectCreditCard();
				}
			}
		});
	}

	protected void selectCreditCard() {
		CreditCard creditCard = tableModel.getCreditCard(table.getSelectedRow());
		getMagicFrame().switchToEditCreditCardPanel(creditCard);
	}

	private void switchToNewCreditCardPanel() {
		getMagicFrame().switchToAddNewCreditCardPanel();
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToRecordsMaintenanceMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewCreditCardPanel();
			}
		});
		toolBar.add(postButton);
	}

	private class CreditCardsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"User", "Bank", "Card Number"};
		
		private List<CreditCard> creditCards = new ArrayList<>();
		
		public void setCreditCards(List<CreditCard> creditCards) {
			this.creditCards = creditCards;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return creditCards.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		public CreditCard getCreditCard(int rowIndex) {
			return creditCards.get(rowIndex);
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			CreditCard creditCard = getCreditCard(rowIndex);
			switch (columnIndex) {
			case USER_COLUMN_INDEX:
				return creditCard.getUser();
			case BANK_COLUMN_INDEX:
				return creditCard.getBank();
			case CARD_NUMBER_COLUMN_INDEX:
				return creditCard.getCardNumber();
			default:
				throw new RuntimeException("Fetching invalid column index: " + columnIndex);
			}
		}
		
	}
	
}