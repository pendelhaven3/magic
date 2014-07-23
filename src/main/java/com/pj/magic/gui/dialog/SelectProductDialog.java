package com.pj.magic.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pj.magic.service.ProductService;

@Component
@Scope("prototype")
public class SelectProductDialog extends MagicDialog {

	private static final long serialVersionUID = -1155384453472953071L;
	private static final String SELECT_PRODUCT_ACTION_NAME = "selectProduct";
	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;

	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductsTableModel tableModel;
	
	private String selectedProductCode;
	
	public SelectProductDialog() {
		setSize(500, 200);
		setLocationRelativeTo(null);
		setTitle("Select Product");
	}

	@PostConstruct
	public void initialize() {
		final JTable table = new JTable(tableModel);
		table.setRowSelectionInterval(0, 0);
		
		table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), SELECT_PRODUCT_ACTION_NAME);
		table.getActionMap().put(SELECT_PRODUCT_ACTION_NAME, new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedProductCode = (String)table.getValueAt(table.getSelectedRow(), PRODUCT_CODE_COLUMN_INDEX);
				setVisible(false);
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);	
	}

	public String getSelectedProductCode() {
		return selectedProductCode;
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedProductCode = null;
	}
	
}
