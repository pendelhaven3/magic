package com.pj.magic.gui.dialog;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.tables.models.SuppliersTableModel;
import com.pj.magic.model.Product;
import com.pj.magic.model.Supplier;
import com.pj.magic.service.ProductService;

@Component
public class SelectSupplierDialog extends MagicDialog {

	@Autowired private ProductService productService;
	
	private Supplier selectedSupplier;
	private JTable table;
	private SuppliersTableModel tableModel = new SuppliersTableModel();
	
	public SelectSupplierDialog() {
		setSize(500, 400);
		setLocationRelativeTo(null);
		setTitle("Select Supplier");
		addContents();
	}

	private void addContents() {
		table = new JTable(tableModel);
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (table.getSelectedRow() != -1) {
						selectedSupplier = tableModel.getSupplier(table.getSelectedRow());
						setVisible(false);
					}
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);	
	}

	public Supplier getSelectedSupplier() {
		return selectedSupplier;
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		selectedSupplier = null;
	}
	
	public void searchAvailableSuppliers(Product product) {
		List<Supplier> suppliers = productService.getAvailableSuppliers(product);
		tableModel.setSuppliers(suppliers);
		if (!suppliers.isEmpty()) {
			table.changeSelection(0, 0, false, false);
		}
	}
	
}
