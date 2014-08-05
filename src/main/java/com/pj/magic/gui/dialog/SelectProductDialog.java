package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.ProductsTableModel;
import com.pj.magic.gui.tables.UnitPricesAndQuantitiesTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.util.ComponentUtil;

@Component
public class SelectProductDialog extends MagicDialog {

	private static final String SELECT_PRODUCT_ACTION_NAME = "selectProduct";
	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;

	@Autowired private ProductsTableModel productsTableModel;

	private UnitPricesAndQuantitiesTableModel unitPricesAndQuantitiesTableModel = 
			new UnitPricesAndQuantitiesTableModel();
	private JTable productsTable;
	private String selectedProductCode;
	private JTable unitPricesAndQuantitiesTable;
	
	public SelectProductDialog() {
		setSize(500, 400);
		setLocationRelativeTo(null);
		setTitle("Select Product");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	@PostConstruct
	public void initialize() {
		productsTable = new JTable(productsTableModel);
		unitPricesAndQuantitiesTable = new JTable(unitPricesAndQuantitiesTableModel);
		
		layoutComponents();
		registerKeyBindings();
		
		productsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = productsTable.getSelectedRow();
				Product product = productsTableModel.getProduct(selectedRow);
				unitPricesAndQuantitiesTableModel.setProduct(product);
			}
		});
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowActivated(WindowEvent e) {
				productsTable.changeSelection(0, 0, false, false);
			}
		});
	}

	private void registerKeyBindings() {
		productsTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_PRODUCT_ACTION_NAME);
		productsTable.getActionMap().put(SELECT_PRODUCT_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedProductCode = (String)productsTable.getValueAt(productsTable.getSelectedRow(), PRODUCT_CODE_COLUMN_INDEX);
				setVisible(false);
			}
		});
	}

	public String getSelectedProductCode() {
		return selectedProductCode;
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedProductCode = null;
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;

		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		
		JScrollPane productsScrollPane = new JScrollPane(productsTable);
		productsScrollPane.setPreferredSize(new Dimension(400, 100));
		add(productsScrollPane, c);

		currentRow++;
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		
		add(ComponentUtil.createFiller(1, 5), c);
		
		currentRow++;
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;

		JScrollPane unitPricesAndQuantitiesScrollPane = new JScrollPane(unitPricesAndQuantitiesTable);
		unitPricesAndQuantitiesScrollPane.setPreferredSize(new Dimension(400, 87));
		add(unitPricesAndQuantitiesScrollPane, c);
	}
	
}
