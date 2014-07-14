package com.pj.magic.gui.dialog;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pj.magic.model.Product;
import com.pj.magic.service.ProductService;

@Component
@Scope("prototype")
public class ProductsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -4623255772951504093L;
	private static final int PRODUCT_CODE_COLUMN_INDEX = 0;
	private static final int PRODUCT_DESCRIPTION_COLUMN_INDEX = 1;
	
	private String[] columnNames = {"Code", "Description"};
	private List<Product> products;
	
	@Autowired
	public ProductsTableModel(ProductService productService) {
		products = new ArrayList<>(productService.getAllProducts());
	}
	
	@Override
	public int getRowCount() {
		return products.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Product product = products.get(rowIndex);
		switch (columnIndex) {
		case PRODUCT_CODE_COLUMN_INDEX:
			return product.getCode();
		case PRODUCT_DESCRIPTION_COLUMN_INDEX:
			return product.getDescription();
		default:
			throw new RuntimeException("Fetching invalid column index: " + columnIndex);
		}
	}

}
