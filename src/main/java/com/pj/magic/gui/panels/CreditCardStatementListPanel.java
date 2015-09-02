package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.service.CreditCardService;
import com.pj.magic.util.FormatterUtil;

@Component
public class CreditCardStatementListPanel extends StandardMagicPanel {

	private static final int STATEMENT_NUMBER_COLUMN_INDEX = 0;
	private static final int CREDIT_CARD_COLUMN_INDEX = 1;
	private static final int STATEMENT_DATE_COLUMN_INDEX = 2;
	
	@Autowired private CreditCardService creditCardService;
	
	private MagicListTable table;
	private CreditCardStatementTableModel tableModel;
	
	@Override
	protected void initializeComponents() {
		tableModel = new CreditCardStatementTableModel();
		table = new MagicListTable(tableModel);
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
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasePaymentsMenuPanel();
	}

	public void updateDisplay() {
		tableModel.setStatements(creditCardService.getAllCreditCardStatements());
	}
	
	private class CreditCardStatementTableModel extends AbstractTableModel {

		private final String[] COLUMN_NAMES = {"Statement No.", "Credit Card", "Statement Date"};
		
		private List<CreditCardStatement> statements = new ArrayList<>();
		
		public void setStatements(List<CreditCardStatement> statements) {
			this.statements = statements;
		}
		
		@Override
		public int getRowCount() {
			return statements.size();
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
			CreditCardStatement statement = statements.get(rowIndex);
			switch (columnIndex) {
			case STATEMENT_NUMBER_COLUMN_INDEX:
				return statement.getStatementNumber();
			case CREDIT_CARD_COLUMN_INDEX:
				return statement.getCreditCard();
			case STATEMENT_DATE_COLUMN_INDEX:
				return FormatterUtil.formatDate(statement.getStatementDate());
			default:
				throw new RuntimeException("Fetching invalid column index:" + columnIndex);
			}
		}
		
	}
	
}
