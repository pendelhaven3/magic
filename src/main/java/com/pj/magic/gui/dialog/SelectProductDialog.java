package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.ProductUnitPricesTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.service.ProductService;

@Component
public class SelectProductDialog extends MagicDialog {

	private static final long serialVersionUID = -1155384453472953071L;
	private static final String SELECT_PRODUCT_ACTION_NAME = "selectProduct";
	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;

	@Autowired private ProductService productService;
	@Autowired private ProductsTableModel tableModel;
	@Autowired private ProductUnitPricesTableModel unitPricesTableModel;
	
	private JTable productsTable;
	private String selectedProductCode;
	
	public SelectProductDialog() {
		setSize(500, 400);
		setLocationRelativeTo(null);
		setTitle("Select Product");
	}

	@PostConstruct
	public void initialize() {
		productsTable = new JTable(tableModel);
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		add(new JScrollPane(productsTable));
		add(Box.createRigidArea(new Dimension(0, 5)));
		
		JScrollPane unitPricesScrollPane = new JScrollPane(new JTable(unitPricesTableModel));
		unitPricesScrollPane.setPreferredSize(new Dimension(500, 120));
		add(unitPricesScrollPane);
		
		productsTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_PRODUCT_ACTION_NAME);
		productsTable.getActionMap().put(SELECT_PRODUCT_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedProductCode = (String)productsTable.getValueAt(productsTable.getSelectedRow(), PRODUCT_CODE_COLUMN_INDEX);
				setVisible(false);
			}
		});
		
		productsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = productsTable.getSelectedRow();
				Product product = tableModel.getProduct(selectedRow);
				unitPricesTableModel.setProduct(product);
			}
		});
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowActivated(WindowEvent e) {
				productsTable.changeSelection(0, 0, false, false);
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
	
}
