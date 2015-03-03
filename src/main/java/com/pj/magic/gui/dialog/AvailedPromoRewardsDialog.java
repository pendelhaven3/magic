package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.MagicListTable;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRedemptionReward;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.service.impl.PromoService;

@Component
public class AvailedPromoRewardsDialog extends MagicDialog {

	private static final int PROMO_COLUMN_INDEX = 0;
	private static final int PRODUCT_CODE_COLUMN_INDEX = 1;
	private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 2;
	private static final int UNIT_COLUMN_INDEX = 3;
	private static final int QUANTITY_COLUMN_INDEX = 4;
	
	@Autowired private PromoService promoService;
	
	private MagicListTable table;
	private AvailedPromoRewardsTableModel tableModel;
	
	public AvailedPromoRewardsDialog() {
		setSize(600, 300);
		setLocationRelativeTo(null);
		setTitle("Availed Promo Rewards");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}

	private void initializeComponents() {
		tableModel = new AvailedPromoRewardsTableModel();
		table = new MagicListTable(tableModel);
	}

	private void registerKeyBindings() {
		// none
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(400, 100));
		add(scrollPane, c);
	}
	
	public void updateDisplay(SalesRequisition salesRequisition) {
		List<PromoRedemptionReward> rewards = getAvailedPromoRewards(salesRequisition);
		tableModel.setRewards(rewards);
		if (!rewards.isEmpty()) {
			table.selectFirstRow();
		}
	}

	private List<PromoRedemptionReward> getAvailedPromoRewards(SalesRequisition salesRequisition) {
		List<PromoRedemptionReward> rewards = new ArrayList<>();
		for (Promo promo : promoService.getAllActivePromos()) {
			if (promo.getPromoType().isType2()) {
				rewards.addAll(promo.evaluate(salesRequisition));
			}
		}
		return rewards;
	}
	
	public void updateDisplay(SalesInvoice salesInvoice) {
		List<PromoRedemptionReward> rewards = getAvailedPromoRewards(salesInvoice);
		tableModel.setRewards(rewards);
		if (!rewards.isEmpty()) {
			table.selectFirstRow();
		}
	}
	
	private List<PromoRedemptionReward> getAvailedPromoRewards(SalesInvoice salesInvoice) {
		List<PromoRedemptionReward> rewards = new ArrayList<>();
		for (Promo promo : promoService.getAllActivePromos()) {
			if (promo.getPromoType().isType2()) {
				rewards.addAll(promo.evaluate(salesInvoice));
			}
		}
		return rewards;
	}

	private class AvailedPromoRewardsTableModel extends AbstractTableModel {
		
		private final String[] columnNames = {"Promo", "Product Code", "Description", "Unit", "Quantity"};
		
		private List<PromoRedemptionReward> rewards = new ArrayList<>();
		
		public void setRewards(List<PromoRedemptionReward> rewards) {
			this.rewards = rewards;
			fireTableDataChanged();
		}
		
		@Override
		public int getRowCount() {
			return rewards.size();
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
			PromoRedemptionReward reward = rewards.get(rowIndex);
			switch (columnIndex) {
			case PROMO_COLUMN_INDEX:
				return reward.getParent().getPromo().getName();
			case PRODUCT_CODE_COLUMN_INDEX:	
				return reward.getProduct().getCode();
			case PRODUCT_DESCRIPTION_COLUMN_INDEX:
				return reward.getProduct().getDescription();
			case UNIT_COLUMN_INDEX:
				return reward.getUnit();
			case QUANTITY_COLUMN_INDEX:
				return reward.getQuantity();
			default:
				throw new RuntimeException("Error fetching invalid column index: " + columnIndex);
			}
		}
		
	}

}