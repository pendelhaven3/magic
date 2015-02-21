package com.pj.magic.gui.panels.promo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DoubleClickMouseAdapter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.panels.StandardMagicPanel;
import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.service.PromoRedemptionService;
import com.pj.magic.util.ComponentUtil;

@Component
public class PromoRedemptionListPanel extends StandardMagicPanel {

	private static final int PROMO_REDEMPTION_NUMBER_COLUMN_INDEX = 0;
	private static final int CUSTOMER_COLUMN_INDEX = 1;
	private static final int STATUS_COLUMN_INDEX = 2;
	
	@Autowired private PromoRedemptionService promoRedemptionService;
	
	private MagicListTable table;
	private PromoRedemptionsTableModel tableModel;
	
	public void updateDisplay() {
		List<PromoRedemption> promoRedemptions = promoRedemptionService.getAllPromoRedemptions();
		tableModel.setPromoRedemptions(promoRedemptions);
		if (!promoRedemptions.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
	}

	@Override
	protected void initializeComponents() {
		tableModel = new PromoRedemptionsTableModel();
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
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Promo:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(200, "P&G - Jollibee GC Promo"), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Mechanics:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(new JLabel("For every 1,500 worth of P&G products, get a P50 Jollibee gift certificate"), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(Box.createVerticalStrut(20), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		mainPanel.add(new JScrollPane(table), c);
	}

	@Override
	protected void registerKeyBindings() {
		table.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectPromoRedemption();
			}
		});
		
		table.addMouseListener(new DoubleClickMouseAdapter() {
			
			@Override
			protected void onDoubleClick() {
				selectPromoRedemption();
			}
			
		});
	}

	protected void selectPromoRedemption() {
		PromoRedemption promoRedemption = tableModel.getPromoRedemption(table.getSelectedRow());
		getMagicFrame().switchToPromoRedemptionPanel(promoRedemption);
	}

	private void switchToNewPromoRedemptionPanel() {
		getMagicFrame().switchToPromoRedemptionPanel(new PromoRedemption());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton postButton = new MagicToolBarButton("plus", "New");
		postButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewPromoRedemptionPanel();
			}
		});
		toolBar.add(postButton);
	}
	
	private class PromoRedemptionsTableModel extends AbstractTableModel {

		private final String[] columnNames = {"Promo Redemption No.", "Customer", "Status"};

		private List<PromoRedemption> promoRedemptions = new ArrayList<>();
		
		public void setPromoRedemptions(List<PromoRedemption> promoRedemptions) {
			this.promoRedemptions = promoRedemptions;
			fireTableDataChanged();
		}
		
		public PromoRedemption getPromoRedemption(int rowIndex) {
			return promoRedemptions.get(rowIndex);
		}

		@Override
		public int getRowCount() {
			return promoRedemptions.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PromoRedemption promoRedemption = promoRedemptions.get(rowIndex);
			switch (columnIndex) {
			case PROMO_REDEMPTION_NUMBER_COLUMN_INDEX:
				return promoRedemption.getPromoRedemptionNumber().toString();
			case CUSTOMER_COLUMN_INDEX:
				return promoRedemption.getCustomer().getName();
			case STATUS_COLUMN_INDEX:
				return promoRedemption.getStatus();
			default:
				return null;
			}
		}
		
	}
	
}