package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.ValidationException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.tables.ProductPricesTableModel;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.service.PricingSchemeService;
import com.pj.magic.util.ComponentUtil;

@Component
public class MaintainPricingSchemePanel extends AbstractMagicPanel {

	private static final Logger logger = LoggerFactory.getLogger(MaintainPricingSchemePanel.class);
	private static final String NEXT_FIELD_ACTION_NAME = "nextField";
	private static final String SAVE_ACTION_NAME = "save";
	
	@Autowired private PricingSchemeService pricingSchemeService;
	
	private PricingScheme pricingScheme;
	private MagicTextField nameField;
	private JButton saveButton;
	private JTable pricesTable;
	private ProductPricesTableModel pricesTableModel = new ProductPricesTableModel();
	
	@Override
	protected void initializeComponents() {
		nameField = new MagicTextField();
		nameField.setMaximumLength(50);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCustomer();
			}
		});
		
		pricesTable = new JTable(pricesTableModel);
		
		focusOnComponentWhenThisPanelIsDisplayed(pricesTable);
	}

	@Override
	protected void initializeFocusOrder(List<JComponent> focusOrder) {
		focusOrder.add(nameField);
		focusOrder.add(saveButton);
	}
	
	protected void saveCustomer() {
		if (!validatePricingScheme()) {
			return;
		}
		
		int confirm = showConfirmMessage("Save?");
		if (confirm == JOptionPane.OK_OPTION) {
			pricingScheme.setName(nameField.getText());
			
			try {
				pricingSchemeService.save(pricingScheme);
				showMessage("Saved!");
				nameField.requestFocusInWindow();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				showErrorMessage("Error occurred during saving!");
			}
		}
	}

	private boolean validatePricingScheme() {
		try {
			validateMandatoryField(nameField, "Name");
		} catch (ValidationException e) {
			return false;
		}
		return true;
	}

	@Override
	protected void layoutComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Name: "), c);
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		nameField.setPreferredSize(new Dimension(200, 20));
		add(nameField, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0; // right space filler
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(1, 1), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(1, 20), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.EAST;
		saveButton.setPreferredSize(new Dimension(100, 20));
		add(saveButton, c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(10, 10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.CENTER;
		add(new JSeparator(), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(ComponentUtil.createFiller(10, 10), c);
		
		currentRow++;
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		layoutPricesTable();
		add(new JScrollPane(pricesTable), c);
	}

	private void layoutPricesTable() {
		TableColumnModel columnModel = pricesTable.getColumnModel();
		columnModel.getColumn(ProductPricesTableModel.CODE_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(ProductPricesTableModel.DESCRIPTION_COLUMN_INDEX).setPreferredWidth(250);
		columnModel.getColumn(ProductPricesTableModel.CASE_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(ProductPricesTableModel.TIE_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(ProductPricesTableModel.CARTON_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(ProductPricesTableModel.DOZEN_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
		columnModel.getColumn(ProductPricesTableModel.PIECES_UNIT_PRICE_COLUMN_INDEX).setPreferredWidth(50);
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.RIGHT);
		
		columnModel.getColumn(ProductPricesTableModel.CASE_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(ProductPricesTableModel.TIE_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(ProductPricesTableModel.CARTON_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(ProductPricesTableModel.DOZEN_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
		columnModel.getColumn(ProductPricesTableModel.PIECES_UNIT_PRICE_COLUMN_INDEX).setCellRenderer(renderer);
	}

	@Override
	protected void registerKeyBindings() {
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NEXT_FIELD_ACTION_NAME);
		getActionMap().put(NEXT_FIELD_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				focusNextField();
			}
		});
		
		saveButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SAVE_ACTION_NAME);
		saveButton.getActionMap().put(SAVE_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCustomer();
			}
		});
	}

	public void updateDisplay(PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
		if (pricingScheme.getId() == null) {
			clearDisplay();
			return;
		}

		pricingScheme = pricingSchemeService.get(pricingScheme.getId());
		nameField.setText(pricingScheme.getName());
		pricesTableModel.setProducts(pricingScheme.getProducts());
		pricesTable.changeSelection(0, 0, false, false);
	}

	private void clearDisplay() {
		nameField.setText(null);
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchToPricingSchemeListPanel();
	}

}
