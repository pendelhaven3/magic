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
import javax.swing.event.TableModelEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.AlreadyPostedException;
import com.pj.magic.exception.NoItemException;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.gui.MagicFrame;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.gui.tables.BadStockAdjustmentOutItemsTable;
import com.pj.magic.gui.tables.BadStockInfoTable;
import com.pj.magic.model.BadStock;
import com.pj.magic.model.BadStockAdjustmentOut;
import com.pj.magic.model.Product;
import com.pj.magic.service.BadStockAdjustmentOutService;
import com.pj.magic.service.BadStockService;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.FormatterUtil;

@Component
public class BadStockAdjustmentOutPanel extends StandardMagicPanel {

	private static final Logger LOGGER = LoggerFactory.getLogger(BadStockAdjustmentOutPanel.class);
	
	@Autowired
	private BadStockAdjustmentOutService badStockAdjustmentOutService;
	
	@Autowired
	private BadStockAdjustmentOutItemsTable itemsTable;
	
	@Autowired
	private BadStockService badStockService;
	
	private BadStockAdjustmentOut adjustmentOut;
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
	private BadStockInfoTable badStockInfoTable;
	
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
		updateTotalItemsFieldWhenItemsTableChanges();
		initializeUnitQuantitiesTable();
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
		if (adjustmentOut.getId() != null || !remarksField.getText().equals(adjustmentOut.getRemarks())) {
			adjustmentOut.setRemarks(remarksField.getText());
			try {
			    badStockAdjustmentOutService.save(adjustmentOut);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				showErrorMessage("Unexpected error on saving");
				return;
			}
			
			updateDisplay(adjustmentOut);
			itemsTable.highlight();
		}
	}

	private void savePilferage() {
		if (adjustmentOut.isPilferage() == pilferageCheckBox.isSelected()) {
			return;
		}
		
		adjustmentOut.setPilferage(pilferageCheckBox.isSelected());
		try {
			badStockAdjustmentOutService.save(adjustmentOut);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			showErrorMessage("Unexpected error on saving");
		}
	}
	
	@Override
	protected void doOnBack() {
		if (itemsTable.isEditing()) {
			itemsTable.getCellEditor().cancelCellEditing();
		}
        getMagicFrame().back(MagicFrame.BAD_STOCK_ADJUSTMENT_OUT_LIST_PANEL);
	}

	public void updateDisplay(BadStockAdjustmentOut adjustmentOut) {
		if (adjustmentOut.getId() == null) {
			this.adjustmentOut = adjustmentOut;
			clearDisplay();
			return;
		}
		
		this.adjustmentOut = adjustmentOut = badStockAdjustmentOutService.getBadStockAdjustmentOut(adjustmentOut.getId());
		
		adjustmentInNumberLabel.setText(adjustmentOut.getBadStockAdjustmentOutNumber().toString());
		statusLabel.setText(adjustmentOut.getStatus());
		if (adjustmentOut.getPostDate() != null) {
			postDateField.setText(FormatterUtil.formatDateTime(adjustmentOut.getPostDate()));
		} else {
			postDateField.setText(null);
		}
		if (adjustmentOut.getPostedBy() != null) {
			postedByField.setText(adjustmentOut.getPostedBy().getUsername());
		} else {
			postedByField.setText(null);
		}
		remarksField.setEnabled(!adjustmentOut.isPosted());
		remarksField.setText(adjustmentOut.getRemarks());
		pilferageCheckBox.setSelected(adjustmentOut.isPilferage());
		pilferageCheckBox.setEnabled(!adjustmentOut.isPosted());
		
		totalItemsField.setText(String.valueOf(adjustmentOut.getTotalItems()));
		postButton.setEnabled(!adjustmentOut.isPosted());
		addItemButton.setEnabled(!adjustmentOut.isPosted());
		deleteItemButton.setEnabled(!adjustmentOut.isPosted());
		
		itemsTable.setAdjustmentOut(adjustmentOut);
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
		itemsTable.setAdjustmentOut(adjustmentOut);
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
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = currentRow;
        c.gridwidth = 6;
        mainPanel.add(ComponentUtil.createScrollPane(badStockInfoTable, 500, 45), c);
        
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

	private void postAdjustmentOut() {
		if (itemsTable.isAdding()) {
			itemsTable.switchToEditMode();
		}

        if (!adjustmentOut.hasItems()) {
            showErrorMessage("Cannot post with no items");
            itemsTable.requestFocusInWindow();
            return;
        }
		
		if (confirm("Do you want to post this Bad Stock Adjustment Out?")) {
		    try {
	            badStockAdjustmentOutService.post(adjustmentOut);
	            showMessage("Post successful");
	            updateDisplay(adjustmentOut);
		    } catch (AlreadyPostedException e) {
		        showErrorMessage("Already posted");
            } catch (NoItemException e) {
                showErrorMessage("Cannot post with no items");
            } catch (NotEnoughStocksException e) {  
                showErrorMessage("Not enough available stocks!");
                updateDisplay(adjustmentOut);
                itemsTable.highlightColumn(e.getBadStockAdjustmentOutItem(), 
                        BadStockAdjustmentOutItemsTable.QUANTITY_COLUMN_INDEX);
		    } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                showMessageForUnexpectedError();
		    }
		}
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		postButton = new MagicToolBarButton("post", "Post");
		postButton.addActionListener(e -> postAdjustmentOut());
		toolBar.add(postButton);
	}

    private void updateTotalItemsFieldWhenItemsTableChanges() {
        itemsTable.getModel().addTableModelListener(e -> {
            totalItemsField.setText(String.valueOf(adjustmentOut.getTotalItems()));
        });
    }
	
    private void initializeUnitQuantitiesTable() {
        badStockInfoTable = new BadStockInfoTable();
        
        itemsTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == BadStockAdjustmentOutItemsTable.PRODUCT_CODE_COLUMN_INDEX ||
                    e.getColumn() == TableModelEvent.ALL_COLUMNS) {
                updateUnitQuantitiesTable();
            }
        });
            
        itemsTable.getSelectionModel().addListSelectionListener(e -> {
            updateUnitQuantitiesTable();
        });
    }
    
    private void updateUnitQuantitiesTable() {
        if (itemsTable.getSelectedRow() == -1) {
            badStockInfoTable.setBadStock(null);
            return;
        }
        
        Product product = itemsTable.getCurrentlySelectedRowItem().getProduct();
        if (product != null) {
            BadStock badStock = badStockService.getBadStock(product);
            if (badStock == null) {
                badStock = new BadStock(product);
            }
            badStockInfoTable.setBadStock(badStock);
        } else {
            badStockInfoTable.setBadStock(null);
        }
    }
    
}