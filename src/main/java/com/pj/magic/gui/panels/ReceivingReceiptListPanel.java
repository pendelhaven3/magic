package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.ReceivingReceiptSearchCriteriaDialog;
import com.pj.magic.gui.tables.ReceivingReceiptsTable;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.search.ReceivingReceiptSearchCriteria;
import com.pj.magic.service.ReceivingReceiptService;
import com.pj.magic.util.ComponentUtil;

@Component
public class ReceivingReceiptListPanel extends StandardMagicPanel {
	
	@Autowired private ReceivingReceiptsTable table;
	@Autowired private ReceivingReceiptService receivingReceiptService;
	@Autowired private ReceivingReceiptSearchCriteriaDialog receivingReceiptSearchCriteriaDialog;
	
	@Override
	public void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		List<ReceivingReceipt> receivingReceipts = receivingReceiptService.getAllNonPostedReceivingReceipts();
		table.setReceivingReceipts(receivingReceipts);
		receivingReceiptSearchCriteriaDialog.updateDisplay();
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;
		GridBagConstraints c = new GridBagConstraints();
		
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
		
		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, c);
	}
	
	@Override
	protected void registerKeyBindings() {
		
	}
	
	protected void switchToNewReceivingReceiptPanel() {
		getMagicFrame().switchToReceivingReceiptPanel(new ReceivingReceipt());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPurchasesMenuPanel();
	}
	
	public void displayReceivingReceiptDetails(ReceivingReceipt receivingReceipt) {
		getMagicFrame().switchToReceivingReceiptPanel(receivingReceipt);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchReceivingReceipts();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchReceivingReceipts() {
		receivingReceiptSearchCriteriaDialog.setVisible(true);
		
		ReceivingReceiptSearchCriteria criteria = receivingReceiptSearchCriteriaDialog.getSearchCriteria();
		if (criteria != null) {
			List<ReceivingReceipt> receivingReceipts = receivingReceiptService.search(criteria);
			table.setReceivingReceipts(receivingReceipts);
			if (!receivingReceipts.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

}
