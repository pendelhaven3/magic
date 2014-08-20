package com.pj.magic.gui.tables;

import javax.swing.JTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.model.Product;
import com.pj.magic.service.ProductService;

@Component
public class ProductSuppliersTable extends JTable {

	@Autowired private ProductService productService;
	@Autowired private ProductSuppliersTableModel tableModel;
	
	@Autowired
	public ProductSuppliersTable(ProductSuppliersTableModel tableModel) {
		super(tableModel);
		setTableHeader(null);
	}

	public void updateDisplay(Product product) {
		tableModel.setSuppliers(productService.getProductSuppliers(product));
	}

	
	
}
