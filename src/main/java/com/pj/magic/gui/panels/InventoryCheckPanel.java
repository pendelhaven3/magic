package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.DatePickerFormatter;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.service.InventoryCheckService;
import com.pj.magic.util.ComponentUtil;

@Component
public class InventoryCheckPanel extends StandardMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(InventoryCheckPanel.class);
	
	@Autowired private InventoryCheckService inventoryCheckService;
	
	private InventoryCheck inventoryCheck;
	private UtilCalendarModel inventoryDateModel;
	private JButton saveButton;
	
	@Override
	protected void initializeComponents() {
		inventoryDateModel = new UtilCalendarModel();
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveInventoryCheck();
			}
		});
	}

	protected void saveInventoryCheck() {
		if (inventoryDateModel.getValue() == null) {
			showErrorMessage("Inventory Date must be specified");
			return;
		}
		
		if (confirm("Save?")) {
			inventoryCheck.setInventoryDate(inventoryDateModel.getValue().getTime());
			
			try {
				inventoryCheckService.save(inventoryCheck);
				showMessage("Saved!");
				updateDisplay(inventoryCheck);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private void layoutPricesTable() {
//		TableColumnModel columnModel = pricesTable.getColumnModel();
//		columnModel.getColumn(ProductPricesTableModel.CODE_COLUMN_INDEX).setPreferredWidth(50);
//		columnModel.getColumn(ProductPricesTableModel.DESCRIPTION_COLUMN_INDEX).setPreferredWidth(250);
//		columnModel.getColumn(ProductPricesTableModel.CASE_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
//		columnModel.getColumn(ProductPricesTableModel.TIE_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
//		columnModel.getColumn(ProductPricesTableModel.CARTON_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
//		columnModel.getColumn(ProductPricesTableModel.DOZEN_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
//		columnModel.getColumn(ProductPricesTableModel.PIECES_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
//		
//		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
//		renderer.setHorizontalAlignment(JLabel.RIGHT);
//		
//		columnModel.getColumn(ProductPricesTableModel.CASE_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
//		columnModel.getColumn(ProductPricesTableModel.TIE_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
//		columnModel.getColumn(ProductPricesTableModel.CARTON_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
//		columnModel.getColumn(ProductPricesTableModel.DOZEN_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
//		columnModel.getColumn(ProductPricesTableModel.PIECES_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
	}

	@Override
	protected void registerKeyBindings() {
//		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
//				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_FIELD_ACTION_NAME);
//		getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				focusNextField();
//			}
//		});
//		
//		saveButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_ACTION_NAME);
//		saveButton.getActionMap().put(SAVE_ACTION_NAME, new AbstractAction() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				saveInventoryCheck();
//			}
//		});
//		
//		pricesTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
//				SELECT_PRODUCT_PRICE_ACTION_NAME);
//		pricesTable.getActionMap().put(SELECT_PRODUCT_PRICE_ACTION_NAME, new AbstractAction() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				selectProductPrice();
//			}
//		});
//		
//		pricesTable.addMouseListener(new DoubleClickMouseAdapter() {
//			
//			@Override
//			protected void onDoubleClick() {
//				selectProductPrice();
//			}
//		});
//		
	}

	public void updateDisplay(InventoryCheck inventoryCheck) {
		this.inventoryCheck = inventoryCheck;
		if (inventoryCheck.getId() == null) {
			clearDisplay();
			return;
		}

		updateInventoryDateField(inventoryCheck.getInventoryDate());
		
//		
//		inventoryCheck = pricingSchemeService.get(inventoryCheck.getId());
//		nameField.setText(inventoryCheck.getName());
//		pricesTableModel.setProducts(inventoryCheck.getProducts());
//		pricesTable.changeSelection(0, 0, false, false);
//		
//		SwingUtilities.invokeLater(new Runnable() {
//			
//			@Override
//			public void run() {
//				pricesTable.requestFocusInWindow();
//			}
//		});
//		
//		searchButton.setEnabled(true);
	}

	private void updateInventoryDateField(Date inventoryDate) {
		inventoryDateModel.setValue(null); // set to null first to prevent property change listener from triggering
		inventoryDateModel.setValue(DateUtils.toCalendar(inventoryDate));
	}

	private void clearDisplay() {
		updateInventoryDateField(new Date());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToInventoryCheckListPanel();
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(100, "Inventory Date: "), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		JDatePanelImpl datePanel = new JDatePanelImpl(inventoryDateModel);
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DatePickerFormatter());
		mainPanel.add(datePicker, c);
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridx = 2;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(1, 20), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		saveButton.setPreferredSize(new Dimension(100, 20));
		mainPanel.add(saveButton, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		mainPanel.add(ComponentUtil.createFiller(10, 10), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		layoutPricesTable();
//		JScrollPane pricesTableScrollPane = new JScrollPane(pricesTable);
//		pricesTableScrollPane.setPreferredSize(new Dimension(600, 100));
		mainPanel.add(ComponentUtil.createFiller(1, 1), c);
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
		// none
	}

}
