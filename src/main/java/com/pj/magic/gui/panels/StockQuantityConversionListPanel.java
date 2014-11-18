package com.pj.magic.gui.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.StockQuantityConversionsTable;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.service.StockQuantityConversionService;
import com.pj.magic.util.ComponentUtil;

@Component
public class StockQuantityConversionListPanel extends StandardMagicPanel {
	
	private static final String NEW_STOCK_QUANTITY_CONVERSION_ACTION_NAME = "newStockQuantityConversion";
	private static final String DELETE_STOCK_QUANTITY_CONVERSION_ACTION_NAME = "deleteStockQuantityConversion";
	
	@Autowired private StockQuantityConversionsTable table;
	@Autowired private StockQuantityConversionService stockQuantityConversionService;
	
	@Override
	public void initializeComponents() {
		focusOnComponentWhenThisPanelIsDisplayed(table);
	}

	public void updateDisplay() {
		table.updateDisplay();
	}

	public void displayStockQuantityConversionDetails(StockQuantityConversion stockQuantityConversion) {
		getMagicFrame().switchToStockQuantityConversionPanel(stockQuantityConversion);
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
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), NEW_STOCK_QUANTITY_CONVERSION_ACTION_NAME);
		getActionMap().put(NEW_STOCK_QUANTITY_CONVERSION_ACTION_NAME, new AbstractAction() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewStockQuantityConversionPanel();
			}
		});		
		
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), DELETE_STOCK_QUANTITY_CONVERSION_ACTION_NAME);
		getActionMap().put(DELETE_STOCK_QUANTITY_CONVERSION_ACTION_NAME, new AbstractAction() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteStockQuantityConversion();
			}
		});		
	}
	
	protected void switchToNewStockQuantityConversionPanel() {
		StockQuantityConversion stockQuantityConversion = new StockQuantityConversion();
		stockQuantityConversionService.save(stockQuantityConversion);
		
		getMagicFrame().switchToStockQuantityConversionPanel(stockQuantityConversion);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToMainMenuPanel();
	}
	
	private void deleteStockQuantityConversion() {
		if (table.getSelectedRow() != -1) {
			StockQuantityConversion selected = table.getCurrentlySelectedStockQuantityConversion();
			if (selected.isPosted()) {
				showErrorMessage("Cannot delete a stock quantity conversion that is already posted!");
				return;
			}
			if (confirm("Are you sure you want to delete this stock quantity conversion?")) {
				table.removeCurrentlySelectedRow();
			}
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		JButton addButton = new MagicToolBarButton("plus", "New");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchToNewStockQuantityConversionPanel();
			}
		});
		toolBar.add(addButton);
		
		JButton deleteButton = new MagicToolBarButton("minus", "Delete");
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteStockQuantityConversion();
			}
		});
		toolBar.add(deleteButton);
	}

}
