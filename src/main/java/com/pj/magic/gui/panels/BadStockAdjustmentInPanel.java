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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NoItemException;
import com.pj.magic.gui.MagicFrame;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.BadStockAdjustmentInItemsTable;
import com.pj.magic.model.BadStockAdjustmentIn;
import com.pj.magic.service.BadStockAdjustmentInService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class BadStockAdjustmentInPanel extends StandardMagicPanel {

	private static final Logger LOGGER = LoggerFactory.getLogger(BadStockAdjustmentInPanel.class);
	
	@Autowired
	private BadStockAdjustmentInService badStockAdjustmentInService;
	
	@Autowired
	private BadStockAdjustmentInItemsTable itemsTable;
	
	private BadStockAdjustmentIn adjustmentIn;
	private JLabel adjustmentInNumberLabel;
	private JLabel statusLabel;
	private MagicTextField remarksField;
	private JCheckBox pilferageCheckBox = new JCheckBox();
	private JLabel postDateField;
	private JLabel postedByField;
	private JLabel totalItemsField;
	private JButton postButton;
	private JButton addItemButton;
	private JButton deleteItemButton;
	
	@Override
	protected void initializeComponents() {
		postDateField = new JLabel();
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(100);
		remarksField.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e) {
				saveRemarks();
			}
		});
		
		pilferageCheckBox.addItemListener(e -> savePilferage());
		
		focusOnComponentWhenThisPanelIsDisplayed(remarksField);
	}

	@Override
	protected void registerKeyBindings() {
		remarksField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				itemsTable.highlight();
			}
		});
	}

	protected void saveRemarks() {
		if (adjustmentIn.getId() != null || !remarksField.getText().equals(adjustmentIn.getRemarks())) {
			adjustmentIn.setRemarks(remarksField.getText());
			try {
			    badStockAdjustmentInService.save(adjustmentIn);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				showErrorMessage("Unexpected error on saving");
				return;
			}
			
			updateDisplay(adjustmentIn);
			itemsTable.highlight();
		}
	}

	private void savePilferage() {
		if (adjustmentIn.isPilferage() == pilferageCheckBox.isSelected()) {
			return;
		}
		
		adjustmentIn.setPilferage(pilferageCheckBox.isSelected());
		try {
			badStockAdjustmentInService.save(adjustmentIn);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			showErrorMessage("Unexpected error on saving: " + e.getMessage());
		}
	}
	
	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
        getMagicFrame().back(MagicFrame.BAD_STOCK_ADJUSTMENT_IN_LIST_PANEL);
	}
	
	public void updateDisplay(BadStockAdjustmentIn adjustmentIn) {
		if (adjustmentIn.getId() == null) {
			this.adjustmentIn = adjustmentIn;
			clearDisplay();
			return;
		}
		
		this.adjustmentIn = adjustmentIn = badStockAdjustmentInService.getBadStockAdjustmentIn(adjustmentIn.getId());
		
		adjustmentInNumberLabel.setText(adjustmentIn.getBadStockAdjustmentInNumber().toString());
		statusLabel.setText(adjustmentIn.getStatus());
		if (adjustmentIn.getPostDate() != null) {
			postDateField.setText(FormatterUtil.formatDateTime(adjustmentIn.getPostDate()));
		} else {
			postDateField.setText(null);
		}
		if (adjustmentIn.getPostedBy() != null) {
			postedByField.setText(adjustmentIn.getPostedBy().getUsername());
		} else {
			postedByField.setText(null);
		}
		remarksField.setEnabled(!adjustmentIn.isPosted());
		remarksField.setText(adjustmentIn.getRemarks());
		pilferageCheckBox.setSelected(adjustmentIn.isPilferage());
		pilferageCheckBox.setEnabled(!adjustmentIn.isPosted());
		totalItemsField.setText(String.valueOf(adjustmentIn.getTotalItems()));
		postButton.setEnabled(!adjustmentIn.isPosted());
		addItemButton.setEnabled(!adjustmentIn.isPosted());
		deleteItemButton.setEnabled(!adjustmentIn.isPosted());
		
		itemsTable.setAdjustmentIn(adjustmentIn);
	}

	private void clearDisplay() {
		adjustmentInNumberLabel.setText(null);
		statusLabel.setText(null);
		postDateField.setText(null);
		postedByField.setText(null);
		remarksField.setEnabled(true);
		remarksField.setText(null);
		pilferageCheckBox.setSelected(true);
		pilferageCheckBox.setEnabled(true);
		totalItemsField.setText(null);
		itemsTable.setAdjustmentIn(adjustmentIn);
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
		mainPanel.add(Box.createHorizontalStrut(50));

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(120, "BS Adj. In No.:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		adjustmentInNumberLabel = ComponentUtil.createLabel(200, "");
		mainPanel.add(adjustmentInNumberLabel, c);
		
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = currentRow;
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
		statusLabel = ComponentUtil.createLabel(100, "");
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
		c.gridx = 4;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Post Date:"), c);
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 5;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(postDateField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Pilferage:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(pilferageCheckBox, c);
		
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
		postedByField = ComponentUtil.createLabel(100, "");
		mainPanel.add(postedByField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.insets.top = 10;
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
		mainPanel.add(ComponentUtil.createScrollPane(itemsTable, 600, 100), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 6;
		c.anchor = GridBagConstraints.EAST;
		mainPanel.add(createTotalsPanel(), c);
	}
	
	private JPanel createTotalsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		panel.add(ComponentUtil.createLabel(100, "Total Items:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.insets.right = 10;
		c.anchor = GridBagConstraints.WEST;
		totalItemsField = ComponentUtil.createLabel(60);
		panel.add(totalItemsField, c);
		
		return panel;
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

	private void postAdjustmentIn() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}

        if (!adjustmentIn.hasItems()) {
            showErrorMessage("Cannot post with no items");
            itemsTable.requestFocusInWindow();
            return;
        }
		
		if (confirm("Do you want to post this Bad Stock Adjustment In?")) {
		    try {
	            badStockAdjustmentInService.post(adjustmentIn);
	            showMessage("Post successful");
	            updateDisplay(adjustmentIn);
		    } catch (AlreadyPostedException e) {
		        showErrorMessage("Already posted");
            } catch (NoItemException e) {
                showErrorMessage("Cannot post with no items");
		    } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                showMessageForUnexpectedError();
		    }
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(e -> postAdjustmentIn());
		toolBar.add(postButton);
	}

}