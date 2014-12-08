package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.dialog.AdjustmentInSearchCriteriaDialog;
import com.pj.magic.gui.tables.AdjustmentInsTable;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.search.AdjustmentInSearchCriteria;
import com.pj.magic.service.AdjustmentInService;
import com.pj.magic.util.ComponentUtil;

@Component
public class AdjustmentInListPanel extends StandardMagicPanel {
	
	private static final String NEW_ADJUSTMENT_IN_ACTION_NAME = "newAdjustmentIn";
	private static final String NEW_ADJUSTMENT_IN_ACTION_COMMAND_NAME = "newAdjustmentIn";
	
	@Autowired private AdjustmentInsTable table;
	@Autowired private AdjustmentInService adjustmentInService;
	@Autowired private AdjustmentInSearchCriteriaDialog adjustmentInSearchCriteriaDialog;
	
	@Override
	public void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		table.setAdjustmentIns(adjustmentInService.getAllNonPostedAdjustmentIns());
		adjustmentInSearchCriteriaDialog.updateDisplay();
	}

	public void displayAdjustmentInDetails(AdjustmentIn AdjustmentIn) {
		getMagicFrame().switchToAdjustmentInPanel(AdjustmentIn);
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
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), NEW_ADJUSTMENT_IN_ACTION_NAME);
		getActionMap().put(NEW_ADJUSTMENT_IN_ACTION_NAME, new AbstractAction() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewAdjustmentInPanel();
			}
		});		
	}
	
	protected void switchToNewAdjustmentInPanel() {
		getMagicFrame().switchToAdjustmentInPanel(new AdjustmentIn());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToStockMovementMenuPanel();
	}
	
	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.setActionCommand(NEW_ADJUSTMENT_IN_ACTION_COMMAND_NAME);
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewAdjustmentInPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton searchButton = new MagicToolBarButton("search", "Search");
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchAdjustmentIns();
			}
		});
		
		toolBar.add(searchButton);
	}

	private void searchAdjustmentIns() {
		adjustmentInSearchCriteriaDialog.setVisible(true);
		
		AdjustmentInSearchCriteria criteria = adjustmentInSearchCriteriaDialog.getSearchCriteria();
		if (criteria != null) {
			List<AdjustmentIn> adjustmentIns = adjustmentInService.search(criteria);
			table.setAdjustmentIns(adjustmentIns);
			if (!adjustmentIns.isEmpty()) {
				table.changeSelection(0, 0, false, false);
				table.requestFocusInWindow();
			} else {
				showMessage("No matching records");
			}
		}
	}

}
