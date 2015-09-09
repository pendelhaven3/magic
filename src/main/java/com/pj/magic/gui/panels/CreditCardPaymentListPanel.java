package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.CustomAction;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.gui.tables.models.ListBackedTableModel;
import com.pj.magic.model.CreditCard;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.util.FormatterUtil;

@Component
public class CreditCardPaymentListPanel extends StandardMagicPanel {

	private static final int CREDIT_CARD_COLUMN_INDEX = 0;
	private static final int SURPLUS_PAYMENT_COLUMN_INDEX = 1;
	
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
		CreditCard creditCard = tableModel.getItem(table.getSelectedRow());
		getMagicFrame().switchToCreditCardPaymentPanel(creditCard);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasePaymentsMenuPanel();
	}

	public void updateDisplay() {
		tableModel.setItems(creditCardService.getAllCreditCards());
		table.selectFirstRow();
	}
	
	private class CreditCardTableModel extends ListBackedTableModel<CreditCard> {

		private final String[] COLUMN_NAMES = {"Credit Card", "Surplus Payment"};
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			CreditCard creditCard = getItem(rowIndex);
			switch (columnIndex) {
			case CREDIT_CARD_COLUMN_INDEX:
				return creditCard;
			case SURPLUS_PAYMENT_COLUMN_INDEX:
				return FormatterUtil.formatAmount(creditCardService.getSurplusPayment(creditCard));
			default:
				throw new RuntimeException("Fetching invalid column index:" + columnIndex);
			}
		}

		@Override
		protected String[] getColumnNames() {
			return COLUMN_NAMES;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == SURPLUS_PAYMENT_COLUMN_INDEX) {
				return Number.class;
			} else {
				return Object.class;
			}
		}
		
	}
	
}
