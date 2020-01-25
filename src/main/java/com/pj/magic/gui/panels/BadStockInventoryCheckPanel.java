package com.pj.magic.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NoItemException;
import com.pj.magic.gui.MagicFrame;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.BadStockInventoryCheckItemsTable;
import com.pj.magic.model.BadStockInventoryCheck;
import com.pj.magic.service.BadStockInventoryCheckService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BadStockInventoryCheckPanel extends StandardMagicPanel {

	@Autowired
	private BadStockInventoryCheckService badStockInventoryCheckService;
	
	@Autowired
	private BadStockInventoryCheckItemsTable itemsTable;
	
	private BadStockInventoryCheck badStockInventoryCheck;
	
	private JLabel badStockInventoryCheckNumberLabel = new JLabel();
	private MagicTextField remarksField = new MagicTextField();
	private JLabel statusLabel = new JLabel();
	private JLabel postDateLabel = new JLabel();
	private JLabel postedByLabel = new JLabel();
	
	private JButton postButton;
	private MagicToolBarButton addItemButton;
	private MagicToolBarButton deleteItemButton;
	
	public BadStockInventoryCheckPanel() {
		setTitle("Bad Stock Inventory Check");
	}
	
	@Override
	protected void initializeComponents() {
		remarksField.setMaximumLength(100);
		remarksField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveRemarks();
			}
		});
		
		focusOnComponentWhenThisPanelIsDisplayed(remarksField);
	}

	private void saveRemarks() {
		if (!remarksField.getText().equals(badStockInventoryCheck.getRemarks())) {
			badStockInventoryCheck.setRemarks(remarksField.getText());
			badStockInventoryCheckService.save(badStockInventoryCheck);
		}
	}
	
	@Override
	protected void registerKeyBindings() {
		remarksField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveRemarks();
				updateDisplay(badStockInventoryCheck);
				itemsTable.highlight();
			}
		});
	}

	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
		getMagicFrame().back(MagicFrame.BAD_STOCK_INVENTORY_CHECK_LIST_PANEL);
	}
	
	public void updateDisplay(BadStockInventoryCheck badStockInventoryCheck) {
		if (badStockInventoryCheck.isNew()) {
			this.badStockInventoryCheck = badStockInventoryCheck;
			clearDisplay();
			return;
		}
		
		this.badStockInventoryCheck = badStockInventoryCheck = badStockInventoryCheckService.getBadStockInventoryCheck(badStockInventoryCheck.getId());
		
		badStockInventoryCheckNumberLabel.setText(badStockInventoryCheck.getBadStockInventoryCheckNumber().toString());
		remarksField.setText(badStockInventoryCheck.getRemarks());
		remarksField.setEnabled(!badStockInventoryCheck.isPosted());
		statusLabel.setText(badStockInventoryCheck.isPosted() ? "Yes" : "No");
		postButton.setEnabled(!badStockInventoryCheck.isPosted());
		addItemButton.setEnabled(!badStockInventoryCheck.isPosted());
		deleteItemButton.setEnabled(!badStockInventoryCheck.isPosted());
		
		if (badStockInventoryCheck.isPosted()) {
			postDateLabel.setText(FormatterUtil.formatDate(badStockInventoryCheck.getPostDate()));
			postedByLabel.setText(badStockInventoryCheck.getPostedBy().getUsername());
		}
		
		itemsTable.setBadStockInventoryCheck(badStockInventoryCheck);
	}

	private void clearDisplay() {
		badStockInventoryCheckNumberLabel.setText(null);
		statusLabel.setText(null);
		remarksField.setText(null);
		remarksField.setEnabled(true);
		postDateLabel.setText(null);
		postedByLabel.setText(null);
		
		itemsTable.setBadStockInventoryCheck(badStockInventoryCheck);
		postButton.setEnabled(false);
		addItemButton.setEnabled(false);
		deleteItemButton.setEnabled(false);
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(230, "Bad Stock Inventory Check No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		badStockInventoryCheckNumberLabel = ComponentUtil.createLabel(200, "");
		mainPanel.add(badStockInventoryCheckNumberLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Status:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(statusLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Remarks:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(remarksField, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Post Date:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(postDateLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createHorizontalStrut(50), c);
		
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Posted By:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(postedByLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(Box.createVerticalStrut(10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(createItemsTableToolBar(), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane itemsTableScrollPane = new JScrollPane(itemsTable);
		itemsTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(itemsTableScrollPane, c);
	}
	
	private JPanel createItemsTableToolBar() {
		JPanel panel = new JPanel();
		
		addItemButton = new MagicToolBarButton("plus_small", "Add Item", true);
		addItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.switchToAddMode();
			}
		});
		panel.add(addItemButton, BorderLayout.WEST);
		
		deleteItemButton = new MagicToolBarButton("minus_small", "Delete Item", true);
		deleteItemButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.removeCurrentlySelectedItem();
			}
		});
		panel.add(deleteItemButton, BorderLayout.WEST);
		
		return panel;
	}

	private void postBadStockInventoryCheck() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}
		
		if (confirm("Do you want to post this Bad Stock Inventory Check?")) { 
			if (!badStockInventoryCheck.hasItems()) {
				showErrorMessage("Cannot post with no items");
				itemsTable.requestFocusInWindow();
				return;
			}
			
			try {
				badStockInventoryCheckService.post(badStockInventoryCheck);
			} catch (NoItemException | AlreadyPostedException e) {
				showErrorMessage(e.getMessage());
				updateDisplay(badStockInventoryCheck);
				return;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				showErrorMessage("Unexpected error occurred during posting!");
				return;
			}
			
			showMessage("Post successful!");
			updateDisplay(badStockInventoryCheck);
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post", e -> postBadStockInventoryCheck());
		toolBar.add(postButton);
	}

}